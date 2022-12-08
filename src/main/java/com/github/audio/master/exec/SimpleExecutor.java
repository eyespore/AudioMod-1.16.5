package com.github.audio.master.exec;

import com.github.audio.api.annotation.Exec;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.master.ServerExecutor;
import com.github.audio.master.net.Mp3Packet;
import com.github.audio.registryHandler.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;

@Exec(Dist.DEDICATED_SERVER)
public class SimpleExecutor extends ServerExecutor {

    private static final SimpleExecutor SIMPLE_EXECUTOR = new SimpleExecutor();
    private static final long CHECK_PLAYER_INVENTORY_DELAY = 80L;
    private static long lastPlayerInventoryChecked = 0L;

    public static SimpleExecutor getExecutor() {
        return SIMPLE_EXECUTOR;
    }

    /**
     * This event will be fired once player get into the server or put into command "/reload",
     * in this method developer can add some custom packet in it to make Sync.
     */
    public void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {

        }
    }

    @SubscribeEvent
    public void onPlayerJoinIn(EntityJoinWorldEvent event) {
        if (event.isCanceled() || event.getEntity() == null || !(event.getEntity() instanceof PlayerEntity)) return;
        if (event.getEntity().getEntityWorld().isRemote) {
            Mp3.hasInitStatue = false;
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        if (serverPlayer != null) {
            NetworkHandler.MP3_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> serverPlayer),
                    new Mp3Packet(Mp3Packet.Type.CHANGE_DIMENSION));
        }
    }
}
