package com.github.audio.networking;

import com.github.audio.Utils;
import com.mojang.serialization.Decoder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkingHandler {

    public static SimpleChannel INSTANCE_1;
    public static SimpleChannel BACKPACK_SOUND_CHANNEL;
    public static SimpleChannel AUDIO_SOUND_CHANNEL;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID(){
        return ID++;
    }

    public static void registerMessage(){
        /* Instance Channel */
        INSTANCE_1 = NetworkRegistry.newSimpleChannel(

                new ResourceLocation(Utils.MOD_ID , "test_networking"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );

        INSTANCE_1.messageBuilder(SendPack.class , nextID())
                .encoder(SendPack::toByte).decoder(SendPack::new).consumer(SendPack::handler).add();

        /* Backpack Sound Channel */
        BACKPACK_SOUND_CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Utils.MOD_ID , "backpack_sound_networking"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );

        BACKPACK_SOUND_CHANNEL.registerMessage(nextID() , BackPackSoundEventPack.class ,
                BackPackSoundEventPack::toByte , BackPackSoundEventPack::new , BackPackSoundEventPack::handle);

        /* Mp3 Sound Channel */
        AUDIO_SOUND_CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Utils.MOD_ID , "audio_sound_networking"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );

        AUDIO_SOUND_CHANNEL.registerMessage(nextID() , AudioSoundPack.class ,
                AudioSoundPack::toByte , AudioSoundPack::new , AudioSoundPack::handler);


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
