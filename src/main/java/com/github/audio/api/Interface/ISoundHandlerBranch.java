package com.github.audio.api.Interface;

import com.github.audio.client.clienthandler.mp3.Mp3Context;
import net.minecraft.client.entity.player.ClientPlayerEntity;

@FunctionalInterface
public interface ISoundHandlerBranch {

    void withBranch(ClientPlayerEntity clientPlayer , Mp3Context.Mp3SoundContext context);

}
