package com.github.audio.master.client;

import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public abstract class AudioExecutor extends ClientExecutor {

    public void playAudio(AudioSound audioSound) {
        if (isNullEnv()) return;
        playSound(new EntityTickableSound(audioSound.getSoundEvent(),
                SoundCategory.RECORDS, 2, 1, getPlayer().get()));
    }

    /**
     * @param audioSound the audio sound that plays to the player.
     * @Description: This method will play an audio sound that introduce to this method, call getSoundEvent()
     * method from the audio sound and play the sound event to the player.
     * @Notice : this method will NOT render over the audio sound that last play to the player.
     */
    public void playSound(AudioSound audioSound) {
        if (isNullEnv()) return;
        Minecraft.getInstance().getSoundHandler().play(new EntityTickableSound(audioSound.getSoundEvent(),
                SoundCategory.RECORDS, 2, 1, getPlayer().get()));
    }

    public void playAudio(AudioSound audioSound, BlockPos pos) {
        playSound(SimpleSound.ambientWithAttenuation(
                audioSound.getSoundEvent(), pos.getX(), pos.getY(), pos.getZ()));
    }

    protected void playSound(ISound sound) {
        Minecraft.getInstance().getSoundHandler().play(sound);
    }


    @Override
    public boolean isNullEnv() {
        return super.isNullEnv();
    }
}
