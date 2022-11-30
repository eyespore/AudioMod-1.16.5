package com.github.audio.client.audio;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.Serializable;

public class CtxEventHandler implements Serializable {

    public Exec exec1;

    public CtxEventHandler(Exec exec1) {
        this.exec1 = exec1;
    }

    @SubscribeEvent
    public void tick1(TickEvent.ClientTickEvent event) {
        return;
    }

    @FunctionalInterface
    public interface Exec {
        void execute();
    }

    private Object readResolve() {
        return this;
    }
}
