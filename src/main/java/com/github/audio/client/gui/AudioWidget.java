package com.github.audio.client.gui;

import com.github.audio.sound.AudioRegistryHandler;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.text.ITextComponent;

public class AudioWidget extends net.minecraft.client.gui.widget.Widget {

    public AudioWidget(int x, int y, int width, int height, ITextComponent title) {
        super(x, y, width, height, title);
    }

    @Override
    public void playDownSound(SoundHandler handler) {
        handler.play(SimpleSound.master(
                AudioRegistryHandler.BACKPACK_FOLD_SOUND.getSoundEvent(), 1.0F));
    }
}
