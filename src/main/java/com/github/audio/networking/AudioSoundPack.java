package com.github.audio.networking;

import com.github.audio.Audio;
import com.github.audio.client.clientevent.SoundHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AudioSoundPack {

    private final String message;

    public AudioSoundPack(PacketBuffer buffer) {
        message = buffer.readString(Short.MAX_VALUE);
    }

    public AudioSoundPack(String message) {
        this.message = message;
    }

    public void toByte(PacketBuffer buf) {
        buf.writeString(this.message);
    }

    /* When player died, reset player's sound parameter. */
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Audio.getLOGGER().info(message);
            SoundHandler.resetAllParameter();
        });
        ctx.get().setPacketHandled(true);
    }
}
