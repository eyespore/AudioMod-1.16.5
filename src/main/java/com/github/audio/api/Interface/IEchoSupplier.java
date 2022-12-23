package com.github.audio.api.Interface;

import net.minecraftforge.event.TickEvent;

import java.util.function.Supplier;

public interface IEchoSupplier<T> {

    Supplier<T> process();

    T loop(TickEvent e);

}
