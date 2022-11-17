package com.github.audio.client.clientevent;

import com.github.audio.Utils;
import com.github.audio.client.gui.AudioToastMessage;
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

    public static SoundSource currentSource;
    public static String currentSongNameRollingBar;

    /* For font time ticker */
    private static int timeTicker = 0;
    private static boolean hasInitRFB = false;
    private static Utils.RollingFontBar rfb = new Utils.RollingFontBar("");
    protected static boolean currentSourceHasChanged = false;

    private static final int SOUND_STOP_CHECK_INTERVAL = 10;

    private static final Map<UUID, ISound> PLAYER_UUID_LIST = new ConcurrentHashMap<>();
    private static long lastPlaybackChecked = 0;

    private SoundHandler() {
    }

    public static AudioSound getCurrentAudioSound() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().get(HandleMethod.soundIndex);
    }

    public static void flushCurrentRollingBar() {
        rfb = Utils.getRollingBar(getCurrentAudioSound().getDisplayName()).get();
        currentSongNameRollingBar = rfb.nextRollingFormat();
        timeTicker = 0;
    }

    /**
     * draw the statues bar while player holding mod item such as Mp3.
     * @param event
     */
    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientPlayer == null || clientWorld == null) return;
        HandleMethod.AudioPlayerContext context = new HandleMethod.AudioPlayerContext(CURRENT_SOUND_CHANNEL , clientPlayer.getUniqueID() , clientPlayer.getEntityId());

        if (currentSourceHasChanged || !hasInitRFB) {
            flushCurrentRollingBar();
            currentSourceHasChanged = false;
            hasInitRFB = true;
        }

        timeTicker++;
        if (timeTicker >= 50) {
            currentSongNameRollingBar = rfb.nextRollingFormat();
            timeTicker = 0;

            //todo :
//            if (currentSource != null) System.out.println("is stopped : " + currentSource.isStopped());
        }
    }

    static void audioToastDraw() {
        new AudioToastMessage().show("Now Playing:", getCurrentAudioSound().getDisplayName().length() > 20 ?
                getCurrentAudioSound().getDisplayName().substring(0 , 20) + "..." : getCurrentAudioSound().getDisplayName());
    }
    //TODO : what if a player pause a playing car and then he press next song button.
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().world == null || Minecraft.getInstance().player == null) return;
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        HandleMethod.AudioPlayerContext context = new HandleMethod.AudioPlayerContext(CURRENT_SOUND_CHANNEL,
                clientPlayer.getUniqueID(), clientPlayer.getEntityId());

        if (HandleMethod.toBeSolved != HandleMethodType.NULL) {
            HandleMethodFactory.SOUND_HANDLER_JUDGEMENT_MAP.get(HandleMethod.toBeSolved).estimate(clientPlayer ,context);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote) return;
        if (!PLAYER_UUID_LIST.isEmpty() && lastPlaybackChecked < event.world.getGameTime() - SOUND_STOP_CHECK_INTERVAL) {
            lastPlaybackChecked = event.world.getGameTime();
            PLAYER_UUID_LIST.entrySet().removeIf(entry -> {
                if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
//                    PacketHandler.sendToServer(new SoundStopNotificationMessage(entry.getKey()));
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
    public static void playTickableSound(HandleMethod.AudioPlayerContext context, Supplier<AudioSound> sup , boolean renderLast) {
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

            HandleMethod.shouldPlayEndSound = true;
            //Server Thread
//            PacketHandler.sendToServer(new SoundStopNotificationMessage(playerUUID));
        }
    }

    /**
     * ----------------- Sound Switch Operation ---------------------------
     */
    protected static AudioSound switchToNext() {
        HandleMethod.soundIndex++;
        if (isChannelEmpty()) return null;
        if (HandleMethod.soundIndex > getChannelSize() - 1) HandleMethod.soundIndex = 0;
        return getChannelSoundList().get(HandleMethod.soundIndex);
    }

    protected static AudioSound switchToLast() {
        HandleMethod.soundIndex--;
        if (isChannelEmpty()) return null;
        if (HandleMethod.soundIndex < 0) HandleMethod.soundIndex = getChannelSize() - 1;
        return getChannelSoundList().get(HandleMethod.soundIndex);
    }

    protected static AudioSound resetCurrent() {
        if (isChannelEmpty()) return null;
        return getChannelSoundList().get(HandleMethod.soundIndex);
    }

    protected static AudioSound playCurrent() {
        if (isChannelEmpty()) return null;
        return getChannelSoundList().get(HandleMethod.soundIndex);
    }

    /*------------------------- GETTER -------------------------------------*/
    static int getChannelSize() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().size();
    }

    private static boolean isChannelEmpty() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().isEmpty();
    }

    private static ArrayList<AudioSound> getChannelSoundList() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList();
    }

    private static int getSoundIndex() {
        return HandleMethod.soundIndex;
    }

    /* Call this method only in client side, reset all mark defined in the class. */
    public static void resetAllParameter() {
        HandleMethod.isPaused = false;
        HandleMethod.isPlaySong = false;
        HandleMethod.isSwitching = false;
        HandleMethod.isForceStop = false;
        HandleMethod.shouldSwitchToNext = false;
        HandleMethod.shouldSwitchToLast = false;
        HandleMethod.shouldPauseOrResume = false;

        HandleMethod.hasPlayInit = false;
        HandleMethod.hasRecord = false;
        hasInitRFB = false;
        currentSourceHasChanged = false;
        HandleMethod.gonnaPlay = false;
        ClientEventHandler.isHoldingMp3 = false;
    }

    @SuppressWarnings({"unused", "java:S1172"})
    // needs to be here for addListener to recognize which event this method should be subscribed to
    public static void onWorldUnload(WorldEvent.Unload evt) {
        PLAYER_UUID_LIST.clear();
        lastPlaybackChecked = 0;
    }
}
