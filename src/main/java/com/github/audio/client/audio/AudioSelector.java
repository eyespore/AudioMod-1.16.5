package com.github.audio.client.audio;

import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.SoundChannel;
import com.github.audio.util.Utils;
import net.minecraft.client.audio.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class AudioSelector implements IAudioSelector {

    public static final ArrayList<String> SOUND_SOURCE_PATH = new ArrayList<String>();
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
}
