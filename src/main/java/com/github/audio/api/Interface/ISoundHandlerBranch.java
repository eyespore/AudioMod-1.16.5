package com.github.audio.api.Interface;

import com.github.audio.client.audio.AudioContext;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public interface ISoundHandlerBranch {

    void withBranch(ClientPlayerEntity clientPlayer , AudioContext context);

}
