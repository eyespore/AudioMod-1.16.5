package com.github.audio.networking;

import com.github.audio.client.ClientEventHandler;
import com.github.audio.client.config.Config;
import com.github.audio.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class BackPackSoundPack {

    private final UUID uuid;
    private final boolean fromClient;
    private final boolean isUnfold;
    private final BlockPos pos;

    public BackPackSoundPack(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        this.fromClient = buffer.readBoolean();
        this.isUnfold = buffer.readBoolean();
        this.pos = buffer.readBlockPos();
    }

    public BackPackSoundPack(UUID uuid, boolean fromClient, boolean isUnfold, BlockPos pos) {
        this.uuid = uuid;
        this.fromClient = fromClient;
        this.isUnfold = isUnfold;
        this.pos = pos;
    }

    public void toByte(PacketBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
        buffer.writeBoolean(this.fromClient);
        buffer.writeBoolean(this.isUnfold);
        buffer.writeBlockPos(this.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (this.fromClient) {
                //Server Thread, sending client packet back to client.
                EventHandler.sendClientPackBack(this);

            } else {
                //Client Thread, handling the packet sent from the server.
                int parameter = Config.BACK_PACK_SOUND_STATUE.get();
                //Handle unfolding sound
                if (this.isUnfold) {
                    //Multiple
                    if (parameter == 0 && !this.uuid.equals(
                            Objects.requireNonNull(Minecraft.getInstance().player).getUniqueID())) {
                        ClientEventHandler.onPlayerUnFoldBackpack(this.getPos());
                    }
                } else {
                    //handle folding sound
                    if (parameter == 0){
                        //Multiple
                        ClientEventHandler.onPlayerFoldBackpack(this.getPos());
                    } else if (parameter == 1 && this.uuid.equals(
                            Objects.requireNonNull(Minecraft.getInstance().player).getUniqueID())) {
                        //Single
                        ClientEventHandler.onPlayerFoldBackpack(this.getPos());
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

    public BlockPos getPos() {
        return pos;
    }

    public boolean isUnfold() {
        return isUnfold;
    }
}
