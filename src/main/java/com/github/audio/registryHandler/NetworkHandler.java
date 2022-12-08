package com.github.audio.registryHandler;

import com.github.audio.master.net.BackPackPacket;
import com.github.audio.master.net.Mp3Packet;
import com.github.audio.master.net.SendPack;
import com.github.audio.util.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    public static SimpleChannel INSTANCE_1 = asSimpleChannel("test_networking");;
    public static SimpleChannel BACKPACK_SOUND_CHANNEL = asSimpleChannel("backpack_sound_networking");
    public static SimpleChannel MP3_CHANNEL = asSimpleChannel("mp3_networking");
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID(){
        return ID++;
    }

    private static SimpleChannel asSimpleChannel(String pathIn) {
        return NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Utils.MOD_ID, pathIn),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );
    }

    public static void registerMessage(){
        /* Instance Channel */
        INSTANCE_1.messageBuilder(SendPack.class , nextID()).encoder(SendPack::toByte).decoder(SendPack::new).consumer(SendPack::handle).add();

        BACKPACK_SOUND_CHANNEL.registerMessage(nextID() , BackPackPacket.class , BackPackPacket::toByte , BackPackPacket::new , BackPackPacket::handle);
        MP3_CHANNEL.registerMessage(nextID() , Mp3Packet.class , Mp3Packet::toByte , Mp3Packet::new , Mp3Packet::handle);

        /*
           public <MSG> IndexedMessageCodec.MessageHandler<MSG>
                registerMessage(int index,
                        Class<MSG> messageType,
                        BiConsumer<MSG, PacketBuffer> encoder,
                        Function<PacketBuffer, MSG> decoder,
                        BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {

           return registerMessage(index, messageType, encoder, decoder, messageConsumer, Optional.empty());
    }
        */
    }
}
