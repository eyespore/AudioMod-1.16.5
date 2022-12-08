package com.github.audio.master.client;

import com.github.audio.master.Executor;
import com.github.audio.master.client.api.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class NetHandler extends Executor implements INetHandler {

    public abstract void toByte(PacketBuffer buf);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);

}
