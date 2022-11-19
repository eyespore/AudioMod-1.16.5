package com.github.audio.client.clientevent;

import com.github.audio.api.AudioAnnotation;
import com.github.audio.api.ISoundHandlerJudgement;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Specific method and function about how the soundEvent from audio correctly runs, the logic of how those method
 * running is in the class {@link SoundHandler}.
 */
public class HandleMethod {
    public static boolean shouldPlayEndSound = false;
    public static boolean isPaused;

    public static boolean isPlaySong;

    public static boolean hasPlayInit;
    protected static boolean gonnaPlay = false;
    /* define if the song stop because player turn it off by himself. */
    protected static boolean isForceStop = false;
    protected static boolean isSwitching = false;

    protected static int soundIndex = 0;
    protected static Enum<HandleMethodType> toBeSolved = HandleMethodType.NULL;
    protected static final HashMap<String, Boolean> SOUND_PARAMETER_STATUES = new HashMap<>();
    static boolean hasRecord = false;
    static long firstRecord;

    static {
//        SOUND_PARAMETER_STATUES.put("sound index", soundIndex);
    }

    static void recordNow() {
        if (!hasRecord) {
            firstRecord = Objects.requireNonNull(Minecraft.getInstance().world).getGameTime();
            hasRecord = true;
        }
    }

    /* Call this method only in client side, reset all mark defined in the class. */
    public static void resetAllParameter() {
        isPaused = false;
        isPlaySong = false;
        isSwitching = false;
        isForceStop = false;
        toBeSolved = HandleMethodType.NULL;

        hasPlayInit = false;
        hasRecord = false;
        gonnaPlay = false;

        ClientEventHandler.isHoldingMp3 = false;
        SoundHandler.hasInitRFB = false;
        SoundHandler.currentSourceHasChanged = false;
    }

    /**
     * ----------------- Sound Switch Operation ---------------------------
     */

    protected static AudioSound next() {
        if (SoundHandler.isChannelEmpty()) return null;
        soundIndex = getSoundIndex() + 1 > SoundHandler.getChannelSize() - 1 ? 0 : getSoundIndex() + 1;
        return SoundHandler.getChannelSoundList().get(soundIndex);
    }

    protected static AudioSound last() {
        if (SoundHandler.isChannelEmpty()) return null;
        soundIndex = getSoundIndex() - 1 < 0 ? SoundHandler.getChannelSize() - 1 : getSoundIndex() - 1;
        return SoundHandler.getChannelSoundList().get(soundIndex);
    }

    protected static AudioSound current() {
        if (SoundHandler.isChannelEmpty()) return null;
        return SoundHandler.getChannelSoundList().get(soundIndex);
    }

    protected static AudioSound random() {
        if (SoundHandler.isChannelEmpty()) return null;
        return SoundHandler.getChannelSoundList().get(new Random().nextInt(soundIndex));
    }

    private static int getSoundIndex() {
        return soundIndex;
    }

    public static class SwitchToNext implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            SoundHandler.preventAutoSwitch();
            if (isPaused || !isPlaySong) {
                soundIndex = soundIndex + 1 > SoundHandler.getChannelSize() - 1 ? 0 : soundIndex + 1;
                SoundHandler.currentSourceHasChanged = true;
            } else {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, HandleMethod::next, true);
                SoundHandler.audioToastDraw();
                SoundHandler.currentSourceHasChanged = true;
                isPlaySong = true;
            }
            toBeSolved = HandleMethodType.NULL;
            isPaused = false;
        }
    }

    @AudioAnnotation.ClientOnly
    public static class SwitchToLast implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            SoundHandler.preventAutoSwitch();
            if (isPaused || !isPlaySong) {
                soundIndex = soundIndex - 1 < 0 ? SoundHandler.CURRENT_SOUND_CHANNEL.getChannelSoundList().size() - 1 : soundIndex - 1;
            } else {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, HandleMethod::last, true);
                isPlaySong = true;
            }
            isPaused = false;
            SoundHandler.currentSourceHasChanged = true;
            toBeSolved = HandleMethodType.NULL;
        }
    }

    @AudioAnnotation.ClientOnly
    public static class PauseOrResume implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            SoundHandler.preventAutoSwitch();
            if (!isPlaySong && !isPaused) {
                if (!hasPlayInit) {
                    recordNow();
//                    SoundHandler.playTickableSound(context, () -> SoundEventRegistryHandler.katanaZeroInit, true);
                    playInitMusic(context);
                    hasPlayInit = true;
                    gonnaPlay = true;
                }
            } else if (isPlaySong && !isPaused) {
                if (SoundHandler.currentSource == null) return;
                /* If the sound has started to player, first press button turn into pause. */
                SoundHandler.currentSource.pause();
                isPaused = true;
                isPlaySong = false;
            } else if (!isPlaySong) {
                /* The second time when player press the button it turns into resume the sound. */
                SoundHandler.currentSource.resume();
                isPaused = false;
                isPlaySong = true;
            }
            toBeSolved = HandleMethodType.NULL;
        }
    }

    @AudioAnnotation.ClientOnly
    public static class GonnaPlay implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            ClientWorld clientWorld = Minecraft.getInstance().world;
            if (clientWorld == null) return;
            if (clientWorld.getGameTime() - firstRecord > SoundEventRegistryHandler.katanaZeroInit.getDuration() - 10) {
                SoundHandler.preventAutoSwitch();
                /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
                SoundHandler.playTickableSound(context, HandleMethod::current, false);
                SoundHandler.audioToastDraw();
                isPaused = false;
                isPlaySong = true;
                gonnaPlay = false;
            }
        }
    }

    public static class AutoSwitch implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            ClientEventHandler.trySwitchToNext();
        }
    }

    public static void playInitMusic(AudioPlayerContext context) {
        SoundHandler.playTickableSound(context , () -> SoundEventRegistryHandler.katanaZeroInit , true);
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
}
