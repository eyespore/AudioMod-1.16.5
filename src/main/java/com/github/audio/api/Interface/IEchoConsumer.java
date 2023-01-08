package com.github.audio.api.Interface;

import com.github.audio.master.Executor;
import net.minecraftforge.event.TickEvent;

import java.util.Map;
import java.util.function.Consumer;

public interface IEchoConsumer<T> {

    Consumer<T> process();

    default void loop(TickEvent e , long delay) {
        return;
    }
}
