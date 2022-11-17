package com.github.audio.networking;

import com.github.audio.client.clientevent.SoundHandler;
import com.github.audio.item.Mp3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AudioSoundPack {

//    private final String judgement;
    private final Judgement judgement;

    public AudioSoundPack(PacketBuffer buffer) {
//        judgement = buffer.readString(Short.MAX_VALUE);
        judgement = buffer.readEnumValue(Judgement.class);
    }

    public AudioSoundPack(Judgement judgement) {
        this.judgement = judgement;
    }

    public void toByte(PacketBuffer buf) {
        buf.writeEnumValue(this.judgement);
    }

    /* When player died, reset player's sound parameter. */
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) {
                ctx.get().setPacketHandled(true);
                return;
            }
            String type = this.judgement.type;
            if (type.equals(Judgement.REBORN.type)) {
                SoundHandler.resetAllParameter();
            } else if (type.equals(Judgement.TOSS.type) || type.equals(Judgement.CHANGE_DIMENSION.type)) {
                SoundHandler.stopSound(clientPlayer.getUniqueID());
                SoundHandler.resetAllParameter();
                Mp3.playMp3EndSound(Minecraft.getInstance().player);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Judgement {
        REBORN("reborn"),
        TOSS("toss"),
        CHANGE_DIMENSION("change_dimension");

        final String type;
        Judgement(String type) {
            this.type = type;
        };
    }
}
