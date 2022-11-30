package com.github.audio.client.audio.mp3;

import com.github.audio.client.audio.AudioContext;
import com.github.audio.api.IPausable;
import com.github.audio.api.IDefaultAudio;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.AudioSoundRegistryHandler;
import com.github.audio.sound.SoundChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

import java.util.function.Consumer;

public class Mp3Context extends AudioContext implements IDefaultAudio, IPausable {

    public boolean isPaused;
    public long firstRecord;
    boolean hasCheckedLastTickMode = false;
    public boolean currentSourceHasChanged = false;
    public long lastAutoSwitchChecked = 0;
    protected boolean gonnaPlay = false;
    public Enum<Mp3.RelayMode> recordMode;
    public String currentSongNameRollingBar;

    private Mp3Context(SoundChannel currentChannel) {
        super(currentChannel);

        if (Mp3ContextHolder.MP3_CTX != null) {
            throw new RuntimeException("Mp3Ctx should not be created more than once.");
        }
    }

    public static Mp3Context getCtx() {
        return Mp3ContextHolder.MP3_CTX;
    }

    public void playInitMusic() {
        playTickableSound(() -> AudioSoundRegistryHandler.KATANA_ZERO_INIT, true);
    }

    public void playMp3EndSound() {
        playTickableSound(() -> AudioSoundRegistryHandler.KATANA_ZERO_END, false);
    }

    @Override
    public void toNext() {
        boolean flag1 = Mp3.currentMode == Mp3.RelayMode.DEFAULT || Mp3.getCurrentMode() == Mp3.RelayMode.SINGLE;
        boolean flag2 = Mp3.currentMode == Mp3.RelayMode.RANDOM;

        if (isPaused || !isPlaySong) {
            if (flag1) currentAudioSound = getNext();
            if (flag2) currentAudioSound = getRandomNext();
        } else {
            stopSound();
            playTickableSound(currentAudioSound);
            drawAudioToast();
            isPlaySong = true;
        }
        isPaused = false;
        currentSourceHasChanged = true;
    }

    @Override
    public void toLast() {
        boolean flag1 = Mp3.currentMode == Mp3.RelayMode.DEFAULT || Mp3.getCurrentMode() == Mp3.RelayMode.SINGLE;
        boolean flag2 = Mp3.currentMode == Mp3.RelayMode.RANDOM;

        if (isPaused || !isPlaySong) {
            if (flag1) currentAudioSound = getLast();
            if (flag2) currentAudioSound = getRandomLast();
        } else {
            stopSound();
            playTickableSound(currentAudioSound);
            Mp3Context.getCtx().isPlaySong = true;
        }
        isPaused = false;
        currentSourceHasChanged = true;
    }

    @Override
    public void toStop() {
        stopSound();
        playMp3EndSound();
        initSoundIndexList();
    }

    @Override
    public void toPause() {
        if (!isPlaySong && !isPaused) {
            if (Minecraft.getInstance().world == null) return;
            firstRecord = Minecraft.getInstance().world.getGameTime();
            playInitMusic();
            gonnaPlay = true;
            return;

        } else if (isPlaySong && !isPaused) {
            currentSource.pause();

        } else if (!isPlaySong) {

            currentSource.resume();
        }
        isPaused = !isPaused;
        isPlaySong = !isPlaySong;
    }

    public void toDelayPlay() {
        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientWorld == null) return;
        if (clientWorld.getGameTime() - firstRecord > AudioSoundRegistryHandler.KATANA_ZERO_INIT.getDuration() - 10) {
            /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
            playTickableSound(() -> currentAudioSound , false);
            drawAudioToast();
            isPaused = false;
            isPlaySong = true;
            gonnaPlay = false;
        }
    }

    private static class Mp3ContextHolder {
        private static final Mp3Context MP3_CTX = new Mp3Context(SoundChannel.KATANA_ZERO_CHANNEL);
    }

    @Override
    public Consumer<AudioContext> getInitializer() {
        return context -> {
            isPaused = false;
            gonnaPlay = false;
            isPlaySong = false;

            preventAutoSwitch = false;
            hasCheckedLastTickMode = false;
            currentSourceHasChanged = false;

            Mp3.isHoldingMp3 = false;
            Mp3.isMp3InInventory = false;
            Mp3HandleMethod.hasInitRFB = false;
            shouldInitRandomList = (Mp3.currentMode == Mp3.RelayMode.RANDOM);
        };
    }

}
