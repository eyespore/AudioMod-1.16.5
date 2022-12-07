package com.github.audio.master;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class Executor {
    public Executor() {}

    @FunctionalInterface
    public interface Judge {
        boolean judge();
    }

    @FunctionalInterface
    public interface Exec {
        void exec();
    }
}
