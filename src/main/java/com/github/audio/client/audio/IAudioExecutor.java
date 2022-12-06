package com.github.audio.client.audio;

import com.github.audio.api.exception.InitBeforeWorldGenerationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAudioExecutor<K extends AudioContext, T extends AudioSelector> {

    K getCtx();

    T getSel();

    void init() throws InitBeforeWorldGenerationException;
}
