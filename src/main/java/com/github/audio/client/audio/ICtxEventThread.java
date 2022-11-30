package com.github.audio.client.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ICtxEventThread {

    void tick(TickEvent.ClientTickEvent event);

}
