package com.github.audio.master.client.api;

import com.github.audio.api.exception.InitBeforeWorldGenerationException;
import com.github.audio.master.client.AudioContext;
import com.github.audio.master.client.Selector;
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
public interface IDeviceExecutor<K extends AudioContext, T extends Selector> {

    K getCtx();

    T getSel();

    void init() throws InitBeforeWorldGenerationException;
}
