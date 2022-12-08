package com.github.audio.master.client.api;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface INetHandler {

    void toByte(PacketBuffer buf);

    void handle(Supplier<NetworkEvent.Context> ctx);

}
