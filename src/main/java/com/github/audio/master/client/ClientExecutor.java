package com.github.audio.master.client;

import com.github.audio.api.annotation.Exec;
import com.github.audio.master.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.function.Supplier;

@Exec(Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientExecutor extends Executor {

    public final Supplier<ClientPlayerEntity> getPlayer() {
        return () -> Minecraft.getInstance().player;
    }

    public final Supplier<UUID> getUUID () {
        return () -> getPlayer().get().getUniqueID();
    }

    public final Supplier<ClientWorld> getWorld() {
        return () -> Minecraft.getInstance().world;
    }

    public final Supplier<Long> getGameTime() {
        if (Minecraft.getInstance().world == null) return () -> -1L;
        return () -> Minecraft.getInstance().world.getGameTime();
    }

    public boolean isNullEnv() {
        return Minecraft.getInstance().world == null || Minecraft.getInstance().player == null;
    }
}
