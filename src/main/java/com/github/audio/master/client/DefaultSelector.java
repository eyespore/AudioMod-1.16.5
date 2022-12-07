package com.github.audio.master.client;

import com.github.audio.master.client.AudioSelector;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultSelector extends AudioSelector {

    public DefaultSelector(SoundChannel channel) {
        super(channel);
    }

    @Override
    public AudioSound next() {
        pointer ++;
        if (pointer > channel.getSize() -1) pointer = 0;
        return channel.getList().get(currentIndex());
    }

    @Override
    public AudioSound last() {
        pointer --;
        if (pointer < 0) pointer = channel.getSize() - 1;
        return channel.getList().get(currentIndex());
    }

    @Override
    public AudioSound getCurrent() {
        return channel.getList().get(currentIndex());
    }

    @Override
    public void reload() {
        for (int i = 0; i < channel.getSize(); i++) currentList.add(i, i);
        pointer = 0;
    }

    @Override
    public AudioSound getNext() {
        return channel.getList().get(currentList.get(pointer + 1 >
                channel.getSize() - 1 ? 0 : pointer + 1));
    }

    @Override
    public AudioSound getLast() {
        return channel.getList().get(currentList.get(pointer - 1 < 0 ?
                channel.getSize() - 1 : pointer - 1));
    }

    public int currentIndex() {
        return currentList.get(pointer);
    }

    public int soundIndex() {
        return channel.getList().indexOf(getCurrent());
    }
}
