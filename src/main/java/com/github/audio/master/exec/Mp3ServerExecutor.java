package com.github.audio.master.exec;

import com.github.audio.Audio;
import com.github.audio.api.EchoConsumer;
import com.github.audio.api.Interface.IEchoConsumer;
import com.github.audio.api.annotation.Exec;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.master.ServerExecutor;
import com.github.audio.master.net.Mp3Packet;
import com.github.audio.registryHandler.NetworkHandler;
import com.github.audio.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Exec(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(modid = Utils.MOD_ID)
public class Mp3ServerExecutor extends ServerExecutor {

    private static final Mp3ServerExecutor MP_3_SERVER_EXECUTOR = new Mp3ServerExecutor();

    /* Set echo loop to check if mp3 is in player's inventory. */
    private static final IEchoConsumer<List<ServerPlayerEntity>> MP3_CHECKER = new EchoConsumer<List<ServerPlayerEntity>>(
            () -> ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
        @Override
        public Consumer<List<ServerPlayerEntity>> process() {
            return (s) -> s.stream().filter(Objects::nonNull)
                    .collect(Collectors.toMap(p -> p , p -> p.inventory.mainInventory
                            .stream().anyMatch(i -> i.isItemEqual(new ItemStack(ItemRegisterHandler.Mp3.get())))))
                    .forEach((p , b) -> NetworkHandler.MP3_CHANNEL.send(PacketDistributor.PLAYER.with(() -> p),
                            new Mp3Packet(b ? Mp3Packet.Type.HAS_MP3 : Mp3Packet.Type.NOT_HAS_MP3)));
        }
    };

    private Mp3ServerExecutor() {
    }

    public static Mp3ServerExecutor getExecutor() {
        return MP_3_SERVER_EXECUTOR;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MP3_CHECKER.loop(event , 30);
    }

    @SubscribeEvent
    public void onPlayerTossMp3(ItemTossEvent event) {
        if (event.getPlayer() == null) return;
        NetworkHandler.MP3_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
                new Mp3Packet(Mp3Packet.Type.TOSS)
        );
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        if (serverPlayer != null) {
            NetworkHandler.MP3_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new Mp3Packet(Mp3Packet.Type.CHANGE_DIMENSION));
        }
    }
}
