package com.github.audio.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AudioSoundPack {

    private final AudioSoundPackBranchFactory.JudgementType judgementType;

    public AudioSoundPack(PacketBuffer buffer) {
        judgementType = buffer.readEnumValue(AudioSoundPackBranchFactory.JudgementType.class);
    }

    public AudioSoundPack(AudioSoundPackBranchFactory.JudgementType judgementType) {
        this.judgementType = judgementType;
    }

    public void toByte(PacketBuffer buf) {
        buf.writeEnumValue(this.judgementType);
    }

    /* handle the situation according to the given judgement, judgement packet usually comes from server side. */
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) {
                ctx.get().setPacketHandled(true);
                return;
            }
            AudioSoundPackBranchFactory.JUDGEMENT_MAP.get(this.judgementType).branch(clientPlayer);
        });
        ctx.get().setPacketHandled(true);
    }
}
