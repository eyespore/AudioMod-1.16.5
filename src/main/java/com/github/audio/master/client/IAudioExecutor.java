package com.github.audio.master.client;

import com.github.audio.api.exception.InitBeforeWorldGenerationException;
import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAudioExecutor<K extends AudioContext, T extends AudioSelector> {

    K getCtx();

    T getSel();

    void init() throws InitBeforeWorldGenerationException;

    static void playAudio(AudioSound sound , ClientPlayerEntity clientPlayer) {
        Minecraft.getInstance().getSoundHandler().play(new EntityTickableSound(sound.getSoundEvent() ,
                SoundCategory.RECORDS , 3 , 1 , clientPlayer));
    }

    static void playAudio(AudioSound sound , BlockPos pos) {
        Minecraft.getInstance().getSoundHandler().play(new SimpleSound(sound.getSoundEvent(), SoundCategory.RECORDS ,
                3 , 1 , pos));
    }
}
