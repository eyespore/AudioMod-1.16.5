package com.github.audio.api.Interface;

import com.github.audio.master.Executor;
import net.minecraftforge.event.TickEvent;

import java.util.function.Consumer;

public interface IEchoConsumer<T> {

    Consumer<T> process();

    void loop(TickEvent event);

}
