package com.github.audio.sound;

import com.github.audio.Audio;
import com.github.audio.Env;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.util.gen.ClientFileOperator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

import java.io.IOException;

/**
 * @Description: The generation process of PlayableAudio.
 */
public class AudioGenerateCycle {

    //    public static final IOHelper IO_HELPER = IOHelper.getInstance();
    @OnlyIn(Dist.CLIENT)
    public static final ClientFileOperator CLIENT_FILE_OPERATOR = ClientFileOperator.getClientFileOperator();

    public static void cycle(IEventBus eventBus) {
        /* Client IO stuff */
        if (!Env.isServer()) {
            try {
                CLIENT_FILE_OPERATOR.clientFileGenerateCycle();
            } catch (IOException e) {
                e.printStackTrace();
                Audio.info("Caused exception while trying running the io operator.");
            }
        }

        /* Add sound register into eventbus so the sound event could be correctly registry */
        AudioSound.SOUND_REGISTER.register(eventBus);

        /* Initialize mod sound list, map, and registry custom sound event into eventbus. */
        registryAudioSound();
    }

    public static void registryAudioSound() {
        AudioRegistryHandler.initVanillaSound();
        AudioRegistryHandler.REGISTER.autoConstructor();
        if (Env.isServer()) return;
        /* Client stuff */
        AudioRegistryHandler.AudioSoundRegister.convertClientOgg();

    }

}
