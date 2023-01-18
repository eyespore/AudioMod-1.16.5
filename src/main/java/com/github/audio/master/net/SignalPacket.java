package com.github.audio.master.net;

import com.github.audio.Audio;
import com.github.audio.Env;
import com.github.audio.master.Executor;
import com.github.audio.master.client.ClientExecutor;
import com.github.audio.master.client.NetHandler;
import com.github.audio.master.client.exec.DataExecutor;
import com.github.audio.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class SignalPacket extends NetHandler {

    private final Signal signal;
    public enum Signal {
        CALL_CLIENT_TO_SEND_MUSIC, PLAY_NEW_SOUND_EVENT;
    }

    public SignalPacket(PacketBuffer buffer) {
        signal = buffer.readEnumValue(Signal.class);
    }

    public SignalPacket(Signal signal){
        this.signal = signal;
    }

    @Override
    public void toByte(PacketBuffer buf){
        buf.writeEnumValue(signal);
    }

    /**
     * method "handler" define how a packet is going to be handled after being sent to its destination,
     *     notice that handling packet's process should be in "ctx.get().enqueueWork(() -> {...})" to keep
     *     the whole process is in safe.
     * @param ctx packet that gonna to be handled.
     */
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            switch (signal) {
                case CALL_CLIENT_TO_SEND_MUSIC: {
                    if (!Env.isServer()) {
                        File[] folder = new File("./music").listFiles();
                        for (File file : folder) {
                            try {
                                DataExecutor.getDataExecutor().transData(file);
                                Audio.getLOGGER().info("sending : " + file.getName());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    break;
                }

                case PLAY_NEW_SOUND_EVENT: {
                    assert Minecraft.getInstance().player != null;
                    Minecraft.getInstance().player.playSound(
                            new SoundEvent(new ResourceLocation(Utils.MOD_ID , "new_sound_event"))
                            , SoundCategory.AMBIENT , 1.0f , 1.0f);
                    break;
                }
                default: break;
            }
            //hook that marks this packet has been handled.
            ctx.get().setPacketHandled(true);
        });
    }

}
