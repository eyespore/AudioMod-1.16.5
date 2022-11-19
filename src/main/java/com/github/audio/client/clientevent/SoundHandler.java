package com.github.audio.client.clientevent;

import com.github.audio.Utils;
import com.github.audio.client.gui.AudioToastMessage;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public class SoundHandler {

    /**
     * The sound channel using for playing now.
     */
    public static final SoundEventRegistryHandler.SoundChannel CURRENT_SOUND_CHANNEL =
            SoundEventRegistryHandler.SoundChannel.KATANA_ZERO_CHANNEL;
    /* If you change the value "CURRENT_SOUND_CHANNEL", at the same time you should change this field as well. */
    static final ArrayList<String> soundSourcePath = new ArrayList<String>();

    public static SoundSource currentSource;
    public static AudioSound currentAudioSound;
    public static String currentSongNameRollingBar;

    /* For font time ticker */
    private static int timeTicker = 0;
    protected static boolean hasInitRFB = false;
    private static Utils.RollFontHelper rfb = new Utils.RollFontHelper("");
    protected static boolean currentSourceHasChanged = false;

    private static final Map<UUID, ISound> PLAYER_UUID_LIST = new ConcurrentHashMap<>();

    private static long lastPlaybackChecked = 0;
    private static final int SOUND_STOP_CHECK_INTERVAL = 10;
    protected static boolean preventAutoSwitch = false;

    private static long lastPreventAutoSwitchChecked = 0;
    private static long lastAutoSwitchChecked = 0;
    private static final int SOUND_AUTO_SWITCH_CHECK_INTERVAL = 60;

    private SoundHandler() {
    }

    public static AudioSound getCurrentAudioSound() {
//        return CURRENT_SOUND_CHANNEL.getChannelSoundList().get(HandleMethod.soundIndex);
        return currentAudioSound;
    }

    public static void flushCurrentRollingBar() {
        rfb = Utils.getRollingBar(getCurrentAudioSound().getDisplayName()).get();
        currentSongNameRollingBar = rfb.nextRollingFormat();
        timeTicker = 0;
    }

    /**
     * draw the statues bar while player holding mod item such as Mp3.
     *
     * @param event
     */
    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientPlayer == null || clientWorld == null) return;
        HandleMethod.AudioPlayerContext context = new HandleMethod.AudioPlayerContext(CURRENT_SOUND_CHANNEL, clientPlayer.getUniqueID(), clientPlayer.getEntityId());

        if (currentSourceHasChanged || !hasInitRFB) {
            flushCurrentRollingBar();
            currentSourceHasChanged = false;
            hasInitRFB = true;
        }

        timeTicker++;
        if (timeTicker >= 50) {
            currentSongNameRollingBar = rfb.nextRollingFormat();
            timeTicker = 0;
        }

        if (HandleMethod.gonnaPlay) {
            preventAutoSwitch();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethodFactory.HandleMethodType.GONNA_PLAY).estimate(clientPlayer, context);
        }
    }

    protected static void audioToastDraw() {
        new AudioToastMessage().show("Now Playing:", getCurrentAudioSound().getDisplayName().length() > 20 ?
                getCurrentAudioSound().getDisplayName().substring(0, 20) + "..." : getCurrentAudioSound().getDisplayName());
    }

    protected static void preventAutoSwitch() {
        if (Minecraft.getInstance().world != null) {
            lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }
        preventAutoSwitch = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().world == null || Minecraft.getInstance().player == null) return;
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        HandleMethod.AudioPlayerContext context = new HandleMethod.AudioPlayerContext(CURRENT_SOUND_CHANNEL,
                clientPlayer.getUniqueID(), clientPlayer.getEntityId());

        if (HandleMethod.toBeSolved != HandleMethodFactory.HandleMethodType.NULL && HandleMethod.toBeSolved != HandleMethodFactory.HandleMethodType.GONNA_PLAY) {
            preventAutoSwitch();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethod.toBeSolved).estimate(clientPlayer, context);
        }

        boolean flag1 = currentSource != null && currentSource.isStopped() && HandleMethod.isPlaySong && !HandleMethod.gonnaPlay;
        boolean flag2 = Minecraft.getInstance().world.getGameTime() > lastAutoSwitchChecked + SOUND_AUTO_SWITCH_CHECK_INTERVAL;
        boolean flag3 = Minecraft.getInstance().world.getGameTime() > lastPreventAutoSwitchChecked + SOUND_AUTO_SWITCH_CHECK_INTERVAL;
        boolean flag4 = getHandler() == HandleMethodFactory.HandleMethodType.NULL && !HandleMethod.gonnaPlay && Mp3.getCurrentMode() != Mp3.RelayMode.SINGLE;

        if (flag3) {
            preventAutoSwitch = false;
            lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }

        if (flag1 && flag2 && flag4 && !preventAutoSwitch) {
            lastAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethodFactory.HandleMethodType.AUTO_SWITCH_NEXT).estimate(clientPlayer, context);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote) return;
        if (!PLAYER_UUID_LIST.isEmpty() && lastPlaybackChecked < event.world.getGameTime() - SOUND_STOP_CHECK_INTERVAL) {
            lastPlaybackChecked = event.world.getGameTime();
            PLAYER_UUID_LIST.entrySet().removeIf(entry -> {
                if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
                    return true;
                }
                return false;
            });
        }
    }

    /*---------------- Sound Play&Stop Operation --------------------------*/
    public static void playSound(UUID playerUUID, ISound sound) {
        if (PLAYER_UUID_LIST.containsKey(playerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(playerUUID));
        }
        Minecraft.getInstance().getSoundHandler().play(sound);
        PLAYER_UUID_LIST.put(playerUUID, sound);
    }

    /**
     * play a sound to player while this method will not render the last song.
     */
    public static void playSoundWithoutOverRender(UUID playerUUID, ISound sound) {
        PLAYER_UUID_LIST.remove(playerUUID);
        Minecraft.getInstance().getSoundHandler().play(sound);
        PLAYER_UUID_LIST.put(playerUUID, sound);
    }

    //ISound
    public static void playSimpleSound(SoundEvent soundEvent, UUID backpackUuid, BlockPos pos) {
        playSound(backpackUuid, SimpleSound.ambientWithAttenuation(soundEvent, pos.getX(), pos.getY(), pos.getZ()));
    }

    public static void playSimpleSound(AudioSound audioSound, UUID playerUUID, BlockPos pos) {
        playSound(playerUUID, SimpleSound.ambientWithAttenuation(
                audioSound.getSoundEvent(), pos.getX(), pos.getY(), pos.getZ()));
    }

    private static void playTickableSound(SoundEvent soundEvent, UUID playerUUID, int entityId) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(entityId);
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        playSound(playerUUID, new EntityTickableSound(soundEvent, SoundCategory.RECORDS, 2, 1, entity));
    }

    public static void playTickableSound(AudioSound audioSound, UUID playerUUID, int entityId) {
        playTickableSound(audioSound.getSoundEvent(), playerUUID, entityId);
    }

    /**
     * Main method to play itickable sound to player
     */
    public static void playTickableSound(HandleMethod.AudioPlayerContext context, Supplier<AudioSound> sup, boolean renderLast) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(context.entityID);
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        SoundEvent sound = (Objects.requireNonNull(sup.get()).getSoundEvent());
        if (renderLast) {
            playSound(context.clientPlayerUUID, new EntityTickableSound(
                    sound, SoundCategory.RECORDS, 2, 1, entity));
        } else {
            playSoundWithoutOverRender(context.clientPlayerUUID, new EntityTickableSound(
                    sound, SoundCategory.RECORDS, 2, 1, entity));
        }

        if (entity instanceof PlayerEntity && entity.getEntityWorld().isRemote) {
            ClientPlayerEntity playerClient = (ClientPlayerEntity) entity;
        }
    }

    public static void stopSound(UUID playerUUID) {
        if (PLAYER_UUID_LIST.containsKey(playerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(playerUUID));
        }
    }

    /*------------------------- GETTER -------------------------------------*/
    static int getChannelSize() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().size();
    }

    protected static boolean isChannelEmpty() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().isEmpty();
    }

    protected static ArrayList<AudioSound> getChannelSoundList() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList();
    }

    @SuppressWarnings({"unused", "java:S1172"})
    // needs to be here for addListener to recognize which event this method should be subscribed to
    public static void onWorldUnload(WorldEvent.Unload evt) {
        PLAYER_UUID_LIST.clear();
        lastPlaybackChecked = 0;
    }

    protected static Enum<HandleMethodFactory.HandleMethodType> getHandler() {
        return HandleMethod.toBeSolved;
    }

    /* To judge when exactly the custom sound source has changed */
    public static void initSoundList() {
        Utils.CollectionHelper.add(soundSourcePath, "a_fine_red_mist", "blue_room", "breath_of_a_serpent", "chemical_brew", "china_town",
                "come_and_see", "driving_force", "end_of_the_road", "full_confession", "hit_the_floor", "katana_zero",
                "meat_grinder", "nocturne", "overdose", "prison_2", "rain_on_bricks", "silhouette", "sneaky_driver",
                "snow", "worst_neighbor_ever", "third_district", "you_will_never_know", "start_up");
        Utils.CollectionHelper.add(Mp3.MODE_LIST, Mp3.RelayMode.DEFAULT, Mp3.RelayMode.SINGLE, Mp3.RelayMode.RANDOM);

        for (int i = 0; i < SoundHandler.getChannelSize(); i++) {
            HandleMethod.RANDOM_MODE_SOUND_INDEX_LIST.add(i, i);
        }

        currentAudioSound = CURRENT_SOUND_CHANNEL.getChannelSoundList().get(0);
    }
}
