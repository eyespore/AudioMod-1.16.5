package com.github.audio.master.net;

import com.github.audio.master.client.NetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Supplier;

public class DataPacket extends NetHandler {
    //For example: "rain_on_bricks.ogg:123"
    private final String id;
    private final byte[] data;

    public DataPacket(PacketBuffer buffer) {
        id = buffer.readString();
        data = buffer.readByteArray();
    }

    public DataPacket(String id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public void toByte(PacketBuffer buf) {
        buf.writeString(this.id);
        buf.writeByteArray(this.data);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                RandomAccessFile raf = new RandomAccessFile("./server/music/" + id.split(":")[0],"rw");
                raf.seek(Long.valueOf(id.split(":")[1]) * 1024 * 31);
                raf.write(data);
                raf.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
