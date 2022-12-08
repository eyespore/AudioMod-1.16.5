package com.github.audio.master.exec;

import com.github.audio.api.annotation.Exec;
import com.github.audio.master.ServerExecutor;
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

    private static final BackpackServerExecutor BACKPACK_SERVER_EXECUTOR = new BackpackServerExecutor();

    public static BackpackServerExecutor getExecutor() {
        return BACKPACK_SERVER_EXECUTOR;
    }

    /**
     * if the packet is from client, this method will simply send it back, change the argument "formClient" from
     * "true" to "false" to make sure the packet will be handled by the player client thread.
     */
    public void sendClientPackBack(BackPackPacket clientPack) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
//        for (UUID uuid : PlayerManager.getUUIDSet()){
        for (ServerPlayerEntity player : playerList.getPlayers()) {
            NetworkHandler.BACKPACK_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> player),
                    new BackPackPacket(clientPack.getUUID(), false, clientPack.isUnfold())
            );
        }
    }

    @SubscribeEvent
    public void onPlayerGuiClose(PlayerContainerEvent.Close event) {
        if (event.getContainer().getClass().getName()
                .equals("net.minecraft.inventory.container.PlayerContainer")) {
            PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
            for (ServerPlayerEntity player : playerList.getPlayers()) {
                NetworkHandler.BACKPACK_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> player),
                        new BackPackPacket(player.getUniqueID(), false, false));
            }
        }
//        NetworkHandler.AUDIO_SOUND_CHANNEL.send(
//                PacketDistributor.PLAYER.with(
//                        () -> (ServerPlayerEntity) event.getPlayer()) ,
//                    new Mp3Packet(ASPMethodFactory.Type.CLOSE_GUI));
    }
}
