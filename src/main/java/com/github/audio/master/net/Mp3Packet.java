package com.github.audio.master.net;

import com.github.audio.master.client.NetHandler;
import com.github.audio.master.client.exec.Mp3Executor;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static com.github.audio.master.net.Mp3Packet.Type.HAS_MP3;
import static com.github.audio.master.net.Mp3Packet.Type.NOT_HAS_MP3;

public class Mp3Packet extends NetHandler {

    private final Type Type;

    public Mp3Packet(PacketBuffer buffer) {
        Type = buffer.readEnumValue(Type.class);
    }

    public Mp3Packet(Type Type) {
        this.Type = Type;
    }

    @Override
    public void toByte(PacketBuffer buf) {
        buf.writeEnumValue(this.Type);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Mp3Executor executor = Mp3Executor.getExecutor();
            switch (this.Type) {
                case TOSS:
                    executor.onTossMp3();
                    break;
                case CHANGE_DIMENSION:
                    executor.onChangeDimen();
                    break;
                case HAS_MP3:
                    executor.refresh(HAS_MP3);
                    break;
                case NOT_HAS_MP3:
                    executor.refresh(NOT_HAS_MP3);
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Type {
        TOSS, CHANGE_DIMENSION, HAS_MP3, NOT_HAS_MP3;
    }
}
