package com.github.audio.master;

import com.github.audio.api.annotation.Exec;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.function.Supplier;

@Exec(Dist.DEDICATED_SERVER)
public class ServerExecutor extends Executor{

    public Supplier<ServerWorld> getWorld(RegistryKey<World> dimension) {
        return () -> ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
    }

    public Supplier<Long> getServerTime() {
        if (ServerLifecycleHooks.getCurrentServer() == null) return () -> -1L;
        return () -> ServerLifecycleHooks.getCurrentServer().getServerTime();
    }

    public Supplier<List<ServerPlayerEntity>> getPlayers() {
        return () -> ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }
}
