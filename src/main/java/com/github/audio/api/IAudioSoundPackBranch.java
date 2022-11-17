package com.github.audio.api;

import net.minecraft.client.entity.player.ClientPlayerEntity;

@FunctionalInterface
public interface IAudioSoundPackBranch {

    void branch(ClientPlayerEntity clientPlayer);

}
