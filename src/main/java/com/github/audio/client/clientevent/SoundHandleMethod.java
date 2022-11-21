package com.github.audio.client.clientevent;

import com.github.audio.api.AudioAnnotation;
import com.github.audio.api.ISoundHandlerBranch;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.*;

import static com.github.audio.client.clientevent.SoundHandler.currentAudioSound;

/**
 * Specific method and function about how the soundEvent from audio correctly runs, the logic of how those method
 * running is in the class {@link SoundHandler}.
 */
public class SoundHandleMethod {
    public static boolean isPaused;
    public static boolean isPlaySong;
    public static boolean hasPlayInit;
    protected static boolean gonnaPlay = false;

    protected static Enum<HandleMethodFactory.HandleMethodType> toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
    protected static final ArrayList<Integer> SOUND_INDEX_LIST = new ArrayList<>();
    protected static final LinkedList<Integer> RANDOM_INDEX_LIST = new LinkedList<>();
    protected static boolean shouldInitRandomList = false;
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
        shouldInitRandomList = false;

        Mp3.isHoldingMp3 = false;
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
        if (Mp3.currentMode == Mp3.RelayMode.DEFAULT || Mp3.getCurrentMode() == Mp3.RelayMode.SINGLE) {
            int currentIndex = SoundHandler.getChannelSoundList().indexOf(currentAudioSound);
            currentIndex = currentIndex + 1 > SoundHandler.getChannelSize() - 1 ? 0 : currentIndex + 1;
            return currentAudioSound = SoundHandler.getChannelSoundList().get(currentIndex);

        } else if (Mp3.currentMode == Mp3.RelayMode.RANDOM) {
            if (shouldInitRandomList) initRandomSoundList(true);
            int nextIndex = RANDOM_INDEX_LIST.indexOf(SoundHandler.getChannelSoundList().indexOf(currentAudioSound)) + 1;
            return currentAudioSound = SoundHandler.getChannelSoundList().get(RANDOM_INDEX_LIST
                    .get(nextIndex > RANDOM_INDEX_LIST.size() - 1 ? initRandomSoundList(true) + 1 : nextIndex));
        }
        return null;
    }

    protected static AudioSound toLast() {
        if (SoundHandler.isChannelEmpty()) return null;
        if (Mp3.currentMode == Mp3.RelayMode.DEFAULT || Mp3.getCurrentMode() == Mp3.RelayMode.SINGLE) {
            int currentIndex = SoundHandler.getChannelSoundList().indexOf(currentAudioSound);
            currentIndex = currentIndex - 1 < 0 ? SoundHandler.getChannelSize() - 1 : currentIndex - 1;
            return currentAudioSound = SoundHandler.getChannelSoundList().get(currentIndex);

        } else if (Mp3.currentMode == Mp3.RelayMode.RANDOM) {
            int lastIndex = RANDOM_INDEX_LIST.indexOf(SoundHandler.getChannelSoundList().indexOf(currentAudioSound)) - 1;
            return currentAudioSound = SoundHandler.getChannelSoundList().get(RANDOM_INDEX_LIST
                    .get(lastIndex < 0 ? initRandomSoundList(false) - 1 : lastIndex));
        }
        return null;
    }

    protected static AudioSound onCurrent() {
        if (SoundHandler.isChannelEmpty()) return null;
        return currentAudioSound;
    }

    /**
     * Method that used for initialize the sound index array while player change the mp3 mode into random, immediately mp3's mode
     * is turn into random, this method will be called to construct a new array.
     *
     * @param startFromBeginning to point which index will this method return.
     * @return if this parameter is true, this method will return the very first index which is 0, or this method will
     * return the last index of the new constructed array which is the final index of that array.
     */
    protected static int initRandomSoundList(boolean startFromBeginning) {
        int toReturn;
        RANDOM_INDEX_LIST.addAll(SOUND_INDEX_LIST);
        Collections.shuffle(RANDOM_INDEX_LIST);
        RANDOM_INDEX_LIST.removeIf(integer -> integer == SoundHandler.getChannelSoundList().indexOf(currentAudioSound));
        if (startFromBeginning) {
            RANDOM_INDEX_LIST.addFirst(SoundHandler.getChannelSoundList().indexOf(currentAudioSound));
            toReturn = 0;
        } else {
            RANDOM_INDEX_LIST.addLast(SoundHandler.getChannelSoundList().indexOf(currentAudioSound));
            toReturn = RANDOM_INDEX_LIST.size() - 1;
        }
        shouldInitRandomList = false;
        return toReturn;
    }

    public static final class ToNext implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (isPaused || !isPlaySong) {
                currentAudioSound = toNext();
            } else {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, SoundHandleMethod::toNext, true);
                SoundHandler.audioToastDraw();
                isPlaySong = true;
            }
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
            SoundHandler.currentSourceHasChanged = true;
            isPaused = false;
        }
    }

    @AudioAnnotation.ClientOnly
    public static final class ToLast implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            if (isPaused || !isPlaySong) {
                currentAudioSound = toLast();
            } else {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.playTickableSound(context, SoundHandleMethod::toLast, true);
                isPlaySong = true;
            }
            isPaused = false;
            SoundHandler.currentSourceHasChanged = true;
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
        }
    }

    @AudioAnnotation.ClientOnly
    public static class PauseOrResume implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
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
    public static class GonnaPlay implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            ClientWorld clientWorld = Minecraft.getInstance().world;
            if (clientWorld == null) return;
            if (clientWorld.getGameTime() - firstRecord > SoundEventRegistryHandler.katanaZeroInit.getDuration() - 10) {
                /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
                SoundHandler.playTickableSound(context, SoundHandleMethod::onCurrent, false);
                SoundHandler.audioToastDraw();
                isPaused = false;
                isPlaySong = true;
                gonnaPlay = false;
            }
        }
    }

    public static class AutoSwitch implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, AudioPlayerContext context) {
            ClientEventHandler.trySwitchToNext();
        }
    }

    public static void playInitMusic(AudioPlayerContext context) {
        SoundHandler.playTickableSound(context, () -> SoundEventRegistryHandler.katanaZeroInit, true);
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
