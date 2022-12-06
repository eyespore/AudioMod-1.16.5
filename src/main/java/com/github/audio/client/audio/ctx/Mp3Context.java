package com.github.audio.client.audio.ctx;

import com.github.audio.client.audio.AudioContext;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Mp3Context extends AudioContext {

    public boolean isPaused = false;
    public boolean isPlaySong = false;
    public boolean gonnaPlay = false;

    public long lastChecked = 0L;
    public boolean preventChecked = false;

    public Mp3Context(ClientPlayerEntity player, ClientWorld world) {
        super(player , world);
    }

    @Override
    public void reload() {
        Mp3.isHoldingMp3 = false;
        Mp3.isMp3InInventory = false;
    }
}
