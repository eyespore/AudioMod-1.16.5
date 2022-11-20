package com.github.audio.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AudioSoundPack {

    private final ASPMethodFactory.ASPJudgementType ASPJudgementType;

    public AudioSoundPack(PacketBuffer buffer) {
        ASPJudgementType = buffer.readEnumValue(ASPMethodFactory.ASPJudgementType.class);
    }

    public AudioSoundPack(ASPMethodFactory.ASPJudgementType ASPJudgementType) {
        this.ASPJudgementType = ASPJudgementType;
    }

    public void toByte(PacketBuffer buf) {
        buf.writeEnumValue(this.ASPJudgementType);
    }

    /* handle the situation according to the given judgement, judgement packet usually comes from server side. */
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) {
                ctx.get().setPacketHandled(true);
                return;
            }
            ASPMethodFactory.BRANCH_MAP.get(this.ASPJudgementType).withBranch(clientPlayer);
        });
        ctx.get().setPacketHandled(true);
    }
}
