package com.github.audio.master.client;

import com.github.audio.sound.SoundChannel;
import net.minecraft.client.audio.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class AudioSelector implements IAudioSelector {

    protected int pointer;
    protected LinkedList<Integer> currentList = new LinkedList<>();

    public SoundSource source;
    public SoundChannel channel;
    public boolean sourceChange = false;

    public AudioSelector(SoundChannel channel) {
        this.channel = channel;
    }

    public final void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public final int getPointer() {
        return pointer;
    }

    public boolean isNull() {
        return channel == null || channel.isEmpty();
    }
}
