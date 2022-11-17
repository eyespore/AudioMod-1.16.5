package com.github.audio.api;

import com.github.audio.client.clientevent.HandleMethod;
import net.minecraft.client.entity.player.ClientPlayerEntity;

@FunctionalInterface
public interface ISoundHandlerJudgement {

    void estimate(ClientPlayerEntity clientPlayer , HandleMethod.AudioPlayerContext context);

}
