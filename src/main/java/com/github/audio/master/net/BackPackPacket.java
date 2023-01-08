package com.github.audio.master.net;

import com.github.audio.client.config.Config;
import com.github.audio.master.client.NetHandler;
import com.github.audio.master.client.exec.BackpackExecutor;
import com.github.audio.master.exec.BackpackServerExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class BackPackPacket extends NetHandler {

    private final UUID uuid;
    private final boolean fromClient;
    private final boolean isUnfold;

    public BackPackPacket(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        this.fromClient = buffer.readBoolean();
        this.isUnfold = buffer.readBoolean();
    }

    public BackPackPacket(UUID uuid, boolean fromClient, boolean isUnfold) {
        this.uuid = uuid;
        this.isUnfold = isUnfold;
        this.fromClient = fromClient;
    }

    public void toByte(PacketBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
        buffer.writeBoolean(this.fromClient);
        buffer.writeBoolean(this.isUnfold);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (this.fromClient) {
                //Server Thread, sending client packet back to client.
                BackpackServerExecutor.getExecutor().sendClientPackBack(this);

            } else {
                //Client Thread, handling the packet sent from the server.
                int parameter = Config.BACK_PACK_SOUND_STATUE.get();
                //Handle unfolding sound
                if (this.isUnfold) {
                    //Multiple
                    if (parameter == 0 && !this.uuid.equals(
                            Objects.requireNonNull(Minecraft.getInstance().player).getUniqueID())) {
                        BackpackExecutor.getExecutor().playUnfoldBackPackSound();
                    }
                } else {
                    //handle folding sound
                    if (parameter == 0){
                        //Multiple
                        BackpackExecutor.getExecutor().playFoldBackPackSound();
                    } else if (parameter == 1 && this.uuid.equals(
                            Objects.requireNonNull(Minecraft.getInstance().player).getUniqueID())) {
                        //Single
                        BackpackExecutor.getExecutor().playFoldBackPackSound();
                    }
                }
            }
            ctx.get().setPacketHandled(true);
        });
    }

    //GETTER
    public UUID getUUID() {
        return uuid;
    }

    public boolean isFromClient() {
        return fromClient;
    }

    public boolean isUnfold() {
        return isUnfold;
    }
}
