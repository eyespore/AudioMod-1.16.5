package com.github.audio.master.client;

import com.github.audio.sound.AudioSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAudioSelector {

    AudioSound next();

    AudioSound last();

    AudioSound getNext();

    AudioSound getLast();

    AudioSound getCurrent();

    void reload();

}
