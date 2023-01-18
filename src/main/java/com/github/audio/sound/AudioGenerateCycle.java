package com.github.audio.sound;

import com.github.audio.Audio;
import com.github.audio.Env;
import com.github.audio.api.NameGenerator;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.util.Utils;
import com.github.audio.util.gen.ClientFileOperator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: The generation process of PlayableAudio.
 */
public class AudioGenerateCycle {

    private static final List<SoundEvent> soundEvents = new ArrayList<>();

    public static void cycle(IEventBus eventBus) {
        /* Client IO stuff */
        if (!Env.isServer()) {
            try {
                ClientFileOperator.getClientFileOperator().clientFileGenerateCycle();
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

        soundEvents.add(new SoundEvent(new ResourceLocation(Utils.MOD_ID , "test")));

//        for (int i = 0; i < 65535; i++) {
//
//            String registryName = gen.get();
//            soundEvents.add(register.register(registryName, () -> new SoundEvent(new ResourceLocation(Utils.MOD_ID , registryName))));
//        }

        if (Env.isServer()) return;
        /* Client stuff */
        AudioRegistryHandler.AudioSoundRegister.convertClientOgg();

    }

}
