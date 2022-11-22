package com.github.audio.api;

import com.github.audio.client.clienthandler.mp3.Mp3Statues;
import net.minecraft.client.entity.player.ClientPlayerEntity;

@FunctionalInterface
public interface ISoundHandlerBranch {

    void withBranch(ClientPlayerEntity clientPlayer , Mp3Statues.Mp3SoundContext context);

}
