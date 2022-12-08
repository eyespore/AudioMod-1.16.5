package com.github.audio.master;

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
