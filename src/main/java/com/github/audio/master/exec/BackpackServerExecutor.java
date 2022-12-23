package com.github.audio.master.exec;

import com.github.audio.api.annotation.Exec;
import com.github.audio.master.ServerExecutor;
import com.github.audio.master.client.NetHandler;
import com.github.audio.master.net.BackPackPacket;
import com.github.audio.registryHandler.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Exec(Dist.DEDICATED_SERVER)
public class BackpackServerExecutor extends ServerExecutor {

    private static final String INVENTORY_GUI = "net.minecraft.inventory.container.PlayerContainer";
    private static final BackpackServerExecutor BACKPACK_SERVER_EXECUTOR = new BackpackServerExecutor();

    public static BackpackServerExecutor getExecutor() {
        return BACKPACK_SERVER_EXECUTOR;
    }

    /**
     * if the packet is from client, this method will simply send it back, change the argument "formClient" from
     * "true" to "false" to make sure the packet will be handled by the player client thread.
     */
    public void sendClientPackBack(BackPackPacket packet) {
        if (isNullEnv()) return;
        getPlayers().get().forEach(p -> NetworkHandler.BACKPACK_SOUND_CHANNEL
                .send(PacketDistributor.PLAYER.with(() -> p),
                        new BackPackPacket(packet.getUUID(), false, packet.isUnfold())));
    }

    @SubscribeEvent
    public void onPlayerGuiClose(PlayerContainerEvent.Close event) {
        if (event.getContainer().getClass().getName().equals(INVENTORY_GUI)) {
            getPlayers().get().forEach(p -> NetworkHandler.BACKPACK_SOUND_CHANNEL
                    .send(PacketDistributor.PLAYER.with(() -> p),
                            new BackPackPacket(p.getUniqueID(), false, false)));
        }
    }
}
