package com.github.audio.api;

import com.github.audio.client.clientevent.SoundHandleMethod;
import net.minecraft.client.entity.player.ClientPlayerEntity;

@FunctionalInterface
public interface ISoundHandlerBranch {

    void withBranch(ClientPlayerEntity clientPlayer , SoundHandleMethod.AudioPlayerContext context);

}
