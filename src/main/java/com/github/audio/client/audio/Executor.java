package com.github.audio.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.function.Supplier;

public abstract class Executor {

    public final Supplier<ClientPlayerEntity> getPlayer() {
        return () -> Minecraft.getInstance().player;
    }

    public final Supplier<ClientWorld> getWorld() {
        return () -> Minecraft.getInstance().world;
    }

    public final Supplier<Long> getTime() {
        if (Minecraft.getInstance().world == null) return () -> -1L;
        return () -> Minecraft.getInstance().world.getGameTime();
    }

    public boolean isNullEnv() {
        return Minecraft.getInstance().world == null || Minecraft.getInstance().player == null;
    }

    @FunctionalInterface
    public interface Judge {
        boolean judge();
    }

    @FunctionalInterface
    public interface Exec {
        void exec();
    }
}
