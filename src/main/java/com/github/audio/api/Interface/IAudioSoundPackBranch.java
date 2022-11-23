package com.github.audio.api.Interface;

import net.minecraft.client.entity.player.ClientPlayerEntity;

@FunctionalInterface
public interface IAudioSoundPackBranch {

    void withBranch(ClientPlayerEntity clientPlayer);

}
