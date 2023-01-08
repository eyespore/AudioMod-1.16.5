package com.github.audio.master.exec;

import com.github.audio.api.annotation.Exec;
import com.github.audio.commands.ReloadResourceCommand;
import com.github.audio.commands.SendMusicCommand;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.master.ServerExecutor;
import com.github.audio.master.net.Mp3Packet;
import com.github.audio.registryHandler.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.server.command.ConfigCommand;

@Exec(Dist.DEDICATED_SERVER)
public class SimpleExecutor extends ServerExecutor {

    private static final SimpleExecutor SIMPLE_EXECUTOR = new SimpleExecutor();

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
    public void onCommandRegister(RegisterCommandsEvent event) {
        new ReloadResourceCommand(event.getDispatcher());
        new SendMusicCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
