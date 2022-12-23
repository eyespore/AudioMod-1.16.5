package com.github.audio.master.client;

import com.github.audio.master.client.sound.PlayableAudio;
import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;


public abstract class AudioExecutor extends ClientExecutor {

    public void playAudio(AudioSound audioSound) {
        if (isNullEnv()) return;
        playSound(PlayableAudio.global(audioSound));
    }

    public void playAudio(AudioSound audioSound, BlockPos pos) {
        playSound(PlayableAudio.located(audioSound , pos));
    }

    public void playAudio(AudioSound audioSound , Entity entity) {
        playSound(PlayableAudio.tickable(audioSound , entity));
    }

    protected void playSound(PlayableAudio sound) {
        Minecraft.getInstance().getSoundHandler().play(sound);
    }


    @Override
    public boolean isNullEnv() {
        return super.isNullEnv();
    }
}
