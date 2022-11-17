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
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Specific method and function about how the soundEvent from audio correctly runs.
 */
@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public class SoundHandler {

    /**
     * The sound channel using for playing now.
     */
    public static final SoundEventRegistryHandler.SoundChannel CURRENT_SOUND_CHANNEL =
            SoundEventRegistryHandler.SoundChannel.KATANA_ZERO_CHANNEL;

//    public static final ArrayList<String> SOURCE_PATH = new ArrayList<>();

    public static SoundSource currentSource;
    private static int soundIndex = 0;
    public static boolean isPlaySong;
    public static boolean isPaused;
    public static String currentSongNameRollingBar;

    public static boolean hasPlayInit;

    private static boolean hasRecord = false;
    private static long firstRecord;
    private static boolean gonnaPlay = false;

    /* For font time ticker */
    private static int timeTicker = 0;
    private static boolean hasInitRFB = false;
    private static Utils.RollingFontBar rfb = new Utils.RollingFontBar("");
    protected static boolean currentSourceHasChanged = false;

    public static boolean shouldPlayEndSound = false;
    /* detect when should stop or resume the sound. */
    protected static boolean shouldPauseOrResume = false;
    /* detect when should play a sound to the player. */
    protected static boolean shouldSwitchToNext = false;
    protected static boolean shouldSwitchToLast = false;
    /* define if the song stop because player turn it off by himself. */
    protected static boolean isForceStop = false;


    protected static boolean isSwitching;
    private static final int SOUND_STOP_CHECK_INTERVAL = 10;
    /**
     * True : means this source will be reset if player press stop button.
     * False : means this source should be switch to the last song when next time player press stop button.
     */
    private static boolean resetThis = true;

    private static final Map<UUID, ISound> PLAYER_UUID_LIST = new ConcurrentHashMap<>();
    private static long lastPlaybackChecked = 0;

    private SoundHandler() {
    }

    public static AudioSound getCurrentAudioSound() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().get(soundIndex);
    }

    private static void recordNow() {
        if (!hasRecord) {
            firstRecord = Objects.requireNonNull(Minecraft.getInstance().world).getGameTime();
            hasRecord = true;
        }
    }

    /**
     * gathering the information for play itickable sound to player or entity.
     */
    public static class AudioPlayerContext {
        public SoundEventRegistryHandler.SoundChannel currentChannel;
        public UUID clientPlayerUUID;
        public int entityID;

        public AudioPlayerContext(SoundEventRegistryHandler.SoundChannel currentChannel, UUID clientPlayerUUID, int entityID) {
            this.currentChannel = currentChannel;
            this.clientPlayerUUID = clientPlayerUUID;
            this.entityID = entityID;
        }
    }

    public static void flushCurrentRollingBar() {
        rfb = Utils.getRollingBar(getCurrentAudioSound().getDisplayName()).get();
        currentSongNameRollingBar = rfb.nextRollingFormat();
        timeTicker = 0;
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientPlayer == null || clientWorld == null) return;
        AudioPlayerContext context = new AudioPlayerContext(CURRENT_SOUND_CHANNEL , clientPlayer.getUniqueID() , clientPlayer.getEntityId());

        if (currentSourceHasChanged || !hasInitRFB) {
            flushCurrentRollingBar();
            currentSourceHasChanged = false;
            hasInitRFB = true;
        }

        timeTicker++;
        if (timeTicker >= 50) {
            currentSongNameRollingBar = rfb.nextRollingFormat();
            timeTicker = 0;

            //TODO :
            if (currentSource != null) System.out.println("is stopped : " + currentSource.isStopped());

        }

        if (gonnaPlay) {

            if (clientWorld.getGameTime() - firstRecord > SoundEventRegistryHandler.katanaZeroInit.getDuration() - 10) {
                /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
                playTickableSound(context, SoundHandler::playCurrent , false);
                audioToastDraw();
                isPaused = false;
                isPlaySong = true;
                gonnaPlay = false;
            }
        }
    }

    private static void audioToastDraw() {
        new AudioToastMessage().show("Now Playing:", getCurrentAudioSound().getDisplayName().length() > 20 ?
                getCurrentAudioSound().getDisplayName().substring(0 , 20) + "..." : getCurrentAudioSound().getDisplayName());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().world == null || Minecraft.getInstance().player == null) return;
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        AudioPlayerContext context = new AudioPlayerContext(CURRENT_SOUND_CHANNEL,
                clientPlayer.getUniqueID(), clientPlayer.getEntityId());

        //switch to next sound
        if (shouldSwitchToNext) {
            if (isPaused || !isPlaySong) {
                soundIndex = soundIndex + 1 > getChannelSize() - 1 ? 0 : soundIndex + 1;
                currentSourceHasChanged = true;
            } else {
                stopSound(clientPlayer.getUniqueID());
                playTickableSound(context, SoundHandler::switchToNext , true);
                audioToastDraw();
                currentSourceHasChanged = true;
                isPlaySong = true;
                resetThis = true;
            }
            shouldSwitchToNext = false;
            isPaused = false;
            return;
        }

        //switch to last sound
        if (shouldSwitchToLast) {
            if (isPaused || !isPlaySong) {
                soundIndex = soundIndex - 1 < 0 ? CURRENT_SOUND_CHANNEL.getChannelSoundList().size() - 1 : soundIndex - 1;
            } else {
                stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, SoundHandler::switchToLast , true);
                isPlaySong = true;
                resetThis = true;
            }
            isPaused = false;
            shouldSwitchToLast = false;
            currentSourceHasChanged = true;
            return;
        }

        //TODO : what if a player pause a playing car and then he press next song button.

        //pause or resume sound
        if (shouldPauseOrResume) {
            if (!isPlaySong && !isPaused) {

                if (!hasPlayInit) {
                    recordNow();
                    playTickableSound(context , () -> SoundEventRegistryHandler.katanaZeroInit , true);
                    hasPlayInit = true;
                    gonnaPlay = true;
                }

            } else if (isPlaySong && !isPaused) {
                /* If the sound has started to player, first press button turn into pause. */
                currentSource.pause();
                isPaused = true;
                isPlaySong = false;
            } else if (!isPlaySong) {
                /* The second time when player press the button it turns into resume the sound. */
                currentSource.resume();
                isPaused = false;
                isPlaySong = true;
            }
            shouldPauseOrResume = false;
        }
    }

    private static boolean resetThis() {
        return !(resetThis = !resetThis);
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
    public static void playTickableSound(AudioPlayerContext context, Supplier<AudioSound> sup , boolean renderLast) {
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

            shouldPlayEndSound = true;
            //Server Thread
//            PacketHandler.sendToServer(new SoundStopNotificationMessage(playerUUID));
        }
    }

    /**
     * ----------------- Sound Switch Operation ---------------------------
     */
    protected static AudioSound switchToNext() {
        soundIndex++;
        if (isChannelEmpty()) return null;
        if (soundIndex > getChannelSize() - 1) soundIndex = 0;
        return getChannelSoundList().get(soundIndex);
    }

    protected static AudioSound switchToLast() {
        soundIndex--;
        if (isChannelEmpty()) return null;
        if (soundIndex < 0) soundIndex = getChannelSize() - 1;
        return getChannelSoundList().get(soundIndex);
    }

    protected static AudioSound resetCurrent() {
        if (isChannelEmpty()) return null;
        return getChannelSoundList().get(soundIndex);
    }

    protected static AudioSound playCurrent() {
        if (isChannelEmpty()) return null;
        return getChannelSoundList().get(soundIndex);
    }

    /*------------------------- GETTER -------------------------------------*/
    private static int getChannelSize() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().size();
    }

    private static boolean isChannelEmpty() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList().isEmpty();
    }

    private static ArrayList<AudioSound> getChannelSoundList() {
        return CURRENT_SOUND_CHANNEL.getChannelSoundList();
    }

    public static int getSoundIndex() {
        return soundIndex;
    }

    /* Call this method only in client side, reset all mark defined in the class. */
    public static void resetAllParameter() {
        isPaused = false;
        isPlaySong = false;
        isSwitching = false;
        isForceStop = false;
        shouldSwitchToNext = false;
        shouldSwitchToLast = false;
        shouldPauseOrResume = false;

        hasPlayInit = false;
        hasRecord = false;
        hasInitRFB = false;
        currentSourceHasChanged = false;
        gonnaPlay = false;
        ClientEventHandler.isHoldingMp3 = false;
    }

    @SuppressWarnings({"unused", "java:S1172"})
    // needs to be here for addListener to recognize which event this method should be subscribed to
    public static void onWorldUnload(WorldEvent.Unload evt) {
        PLAYER_UUID_LIST.clear();
        lastPlaybackChecked = 0;
    }
}
