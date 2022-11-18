package com.github.audio.client.clientevent;

import com.github.audio.api.AudioAnnotation;
import com.github.audio.api.ISoundHandlerJudgement;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.Objects;
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
    /* detect when should stop or resume the sound. */
    protected static boolean shouldPauseOrResume = false;
    /* detect when should play a sound to the player. */
    protected static boolean shouldSwitchToNext = false;
    protected static boolean shouldSwitchToLast = false;
    protected static boolean hasAutoSwitch = false;
    /* define if the song stop because player turn it off by himself. */
    protected static boolean isForceStop = false;
    protected static boolean isSwitching = false;

    protected static int soundIndex = 0;
    protected static Enum<HandleMethodType> toBeSolved = HandleMethodType.NULL;
    static boolean hasRecord = false;
    static long firstRecord;

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
        shouldSwitchToNext = false;
        shouldSwitchToLast = false;
        shouldPauseOrResume = false;

        hasPlayInit = false;
        hasRecord = false;
        gonnaPlay = false;

        ClientEventHandler.isHoldingMp3 = false;
        SoundHandler.hasInitRFB = false;
        SoundHandler.currentSourceHasChanged = false;
    }

    public static class SwitchToNext implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (shouldSwitchToNext) {
                if (isPaused || !isPlaySong) {
                    soundIndex = soundIndex + 1 > SoundHandler.getChannelSize() - 1 ? 0 : soundIndex + 1;
                    SoundHandler.currentSourceHasChanged = true;
                } else {
                    SoundHandler.stopSound(clientPlayer.getUniqueID());
                    SoundHandler.playTickableSound(context, SoundHandler::switchToNext, true);
                    SoundHandler.audioToastDraw();
                    SoundHandler.currentSourceHasChanged = true;
                    isPlaySong = true;
//                    SoundHandler.resetThis = true;
                }
                shouldSwitchToNext = false;
                toBeSolved = HandleMethodType.NULL;
                isPaused = false;
            }
        }
    }

    @AudioAnnotation.ClientOnly
    public static class SwitchToLast implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (shouldSwitchToLast) {
                if (isPaused || !isPlaySong) {
                    soundIndex = soundIndex - 1 < 0 ? SoundHandler.CURRENT_SOUND_CHANNEL.getChannelSoundList().size() - 1 : soundIndex - 1;
                } else {
                    SoundHandler.stopSound(clientPlayer.getUniqueID());
                    SoundHandler.playTickableSound(context, SoundHandler::switchToLast, true);
                    isPlaySong = true;
//                    SoundHandler.resetThis = true;
                }
                isPaused = false;
                shouldSwitchToLast = false;
                SoundHandler.currentSourceHasChanged = true;
                toBeSolved = HandleMethodType.NULL;
            }
        }
    }

    @AudioAnnotation.ClientOnly
    public static class PauseOrResume implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (shouldPauseOrResume) {
                if (!isPlaySong && !isPaused) {
                    if (!hasPlayInit) {
                        recordNow();
                        SoundHandler.playTickableSound(context, () -> SoundEventRegistryHandler.katanaZeroInit, true);
                        hasPlayInit = true;
                        gonnaPlay = true;
                        toBeSolved = HandleMethodType.GONNA_PLAY;
                    }
                } else if (isPlaySong && !isPaused) {
                    if (SoundHandler.currentSource == null) return;
                    /* If the sound has started to player, first press button turn into pause. */
                    SoundHandler.currentSource.pause();
                    isPaused = true;
                    isPlaySong = false;
                    toBeSolved = HandleMethodType.NULL;
                } else if (!isPlaySong) {
                    /* The second time when player press the button it turns into resume the sound. */
                    SoundHandler.currentSource.resume();
                    isPaused = false;
                    isPlaySong = true;
                    toBeSolved = HandleMethodType.NULL;
                }
                shouldPauseOrResume = false;
            }
        }
    }

    @AudioAnnotation.ClientOnly
    public static class GonnaPlay implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (gonnaPlay) {
                ClientWorld clientWorld = Minecraft.getInstance().world;
                if (clientWorld == null) return;
                if (clientWorld.getGameTime() - firstRecord > SoundEventRegistryHandler.katanaZeroInit.getDuration() - 10) {
                    /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
                    SoundHandler.playTickableSound(context, SoundHandler::playCurrent, false);
                    SoundHandler.audioToastDraw();
                    isPaused = false;
                    isPlaySong = true;
                    gonnaPlay = false;
                    toBeSolved = HandleMethodType.NULL;
                }
            }
        }
    }

    public static class AutoSwitch implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            //TODO
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
}
