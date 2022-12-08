package com.github.audio.master.net;

import com.github.audio.master.client.NetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SendPack extends NetHandler {
    private final String message;

    public SendPack(PacketBuffer buffer) {
        message = buffer.readString(Short.MAX_VALUE);
    }

    public SendPack(String message){
        this.message = message;
    }

    @Override
    public void toByte(PacketBuffer buf){
        buf.writeString(this.message);
    }

    /**
     * method "handler" define how a packet is going to be handled after being sent to its destination,
     *     notice that handling packet's process should be in "ctx.get().enqueueWork(() -> {...})" to keep
     *     the whole process is in safe.
     * @param ctx packet that gonna to be handled.
     */
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> System.out.println(message));
        //hook that marks this packet has been handled.
        ctx.get().setPacketHandled(true);
    }

}
