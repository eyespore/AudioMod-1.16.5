package com.github.audio.sound;

import com.github.audio.Audio;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.util.Utils;
import com.github.audio.util.gen.IOHelper;
import com.github.audio.util.gen.JsonBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jaudiotagger.audio.exceptions.CannotReadException;

import java.io.File;
import java.io.IOException;

/**
 * @Description: The generation process of PlayableAudio.
 */
public class AudioGenerateCycle {

    public static final IOHelper IO_HELPER = IOHelper.getInstance();
    public static final JsonBuilder JSON_BUILDER = JsonBuilder.getInstance();

    public static void cycle(IEventBus eventBus) {
        try {
            /* try to generate music folder in player client side if there's not */
            IO_HELPER.generateMusicFolder();
        } catch (CannotReadException | IOException e) {
            Audio.warn("Some thing goes wrong while trying load the song in the music folder, if this occur you should" +
                    "probably check if the song in the music folder have been all in .ogg type or this .ogg music has more than one channel.");
        }

        try {
            /* try to generate json file for registry sound event. */
            JSON_BUILDER.addCus(IO_HELPER::countFile).toJson(Utils.JSON_GEN_PATH);
        } catch (IOException e) {
            Audio.warn("Json file for the registry sound event could not be created correctly");
        }

        try {
            /* try to translate the music in the music folder and the json file into the resource zip. */
            Audio.info("Start inserting Json file and music song into resource zip...");
            IO_HELPER.fileInsert(new File(Utils.JSON_GEN_PATH), "assets/audio/");

            IO_HELPER.simpleInsert(new File(Utils.MUSIC_FOLDER_PATH), "assets/audio/sounds/", false);
            Audio.info("Done inserting!");
        } catch (IOException e) {
            Audio.warn("Fail to load music into the game , The given zip according to the path could not be found, this is probably caused " +
                    "by the resource folder named \" audioresource.zip \" is not placed in the right place, plz check if you have download this " +
                    "resource pack and have it loaded on your minecraft.");
            e.printStackTrace();
        }

        /* Add sound register into eventbus so the sound event could be correctly registry */
        com.github.audio.sound.AudioSound.SOUND_REGISTER.register(eventBus);

        /* Initialize mod sound list, map, and registry custom sound event into eventbus. */
        registryAudioSound();
    }

    public static void registryAudioSound() {
        AudioRegistryHandler.initVanillaSound();
        AudioRegistryHandler.REGISTER.autoConstructor();
    }

}
