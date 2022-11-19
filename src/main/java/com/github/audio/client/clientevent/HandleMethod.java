package com.github.audio.client.clientevent;

import com.github.audio.Utils;
import com.github.audio.api.AudioAnnotation;
import com.github.audio.api.ISoundHandlerJudgement;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.*;

/**
 * Specific method and function about how the soundEvent from audio correctly runs, the logic of how those method
 * running is in the class {@link SoundHandler}.
 */
public class HandleMethod {
    public static boolean isPaused;
    public static boolean isPlaySong;
    public static boolean hasPlayInit;
    protected static boolean gonnaPlay = false;

    protected static Enum<HandleMethodFactory.HandleMethodType> toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
    protected static final ArrayList<Integer> RANDOM_MODE_SOUND_INDEX_LIST = new ArrayList<>();
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
        hasPlayInit = false;
        hasRecord = false;
        gonnaPlay = false;

        ClientEventHandler.isHoldingMp3 = false;
        SoundHandler.hasInitRFB = false;
        SoundHandler.preventAutoSwitch = false;
        SoundHandler.currentSourceHasChanged = false;

        toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
    }

    /**
     * ----------------- Sound Switch Operation ---------------------------
     */

    protected static AudioSound toNext() {
        if (SoundHandler.isChannelEmpty()) return null;
        int currentIndex = SoundHandler.getChannelSoundList().indexOf(SoundHandler.currentAudioSound);
        currentIndex = currentIndex + 1 > SoundHandler.getChannelSize() - 1 ? 0 : currentIndex + 1;
        return SoundHandler.currentAudioSound = SoundHandler.getChannelSoundList().get(currentIndex);
    }

    protected static AudioSound toLast() {
        if (SoundHandler.isChannelEmpty()) return null;
        int currentIndex = SoundHandler.getChannelSoundList().indexOf(SoundHandler.currentAudioSound);
        currentIndex = currentIndex - 1 < 0 ? SoundHandler.getChannelSize() - 1 : currentIndex - 1;
        return SoundHandler.currentAudioSound = SoundHandler.getChannelSoundList().get(currentIndex);
    }

    protected static AudioSound onCurrent() {
        if (SoundHandler.isChannelEmpty()) return null;
        return SoundHandler.currentAudioSound;
    }

    protected static AudioSound randomNext() {
        if (SoundHandler.isChannelEmpty()) return null;
//        return SoundHandler.getChannelSoundList().get(new Random().nextInt(soundIndex));
        //TODO
        return null;
    }

    public static final class ToNext implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (isPaused || !isPlaySong) {
                int currentIndex = SoundHandler.getChannelSoundList().indexOf(SoundHandler.currentAudioSound);
                currentIndex = currentIndex + 1 > SoundHandler.getChannelSoundList().size() - 1 ? 0 : currentIndex + 1;
                SoundHandler.currentAudioSound = SoundHandler.getChannelSoundList().get(currentIndex);
            } else {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, HandleMethod::toNext, true);
                SoundHandler.audioToastDraw();
                isPlaySong = true;
            }
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
            SoundHandler.currentSourceHasChanged = true;
            isPaused = false;
        }
    }

    @AudioAnnotation.ClientOnly
    public static final class ToLast implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (isPaused || !isPlaySong) {
                int currentIndex = SoundHandler.getChannelSoundList().indexOf(SoundHandler.currentAudioSound);
                currentIndex = currentIndex - 1 < 0 ? SoundHandler.getChannelSize() - 1 : currentIndex - 1;
                SoundHandler.currentAudioSound =  SoundHandler.getChannelSoundList().get(currentIndex);

            } else {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, HandleMethod::toLast, true);
                isPlaySong = true;
            }
            isPaused = false;
            SoundHandler.currentSourceHasChanged = true;
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
        }
    }

    @AudioAnnotation.ClientOnly
    public static class PauseOrResume implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (!isPlaySong && !isPaused) {
                if (!hasPlayInit) {
                    recordNow();
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
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
        }
    }

    @AudioAnnotation.ClientOnly
    public static class GonnaPlay implements ISoundHandlerJudgement {
        @Override
        public void estimate(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            ClientWorld clientWorld = Minecraft.getInstance().world;
            if (clientWorld == null) return;
            if (clientWorld.getGameTime() - firstRecord > SoundEventRegistryHandler.katanaZeroInit.getDuration() - 10) {
                /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
                SoundHandler.playTickableSound(context, HandleMethod::onCurrent, false);
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
