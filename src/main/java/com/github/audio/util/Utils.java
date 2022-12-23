package com.github.audio.util;

import com.github.audio.util.gen.IOHelper;
import com.github.audio.util.gen.JsonBuilder;
import com.github.audio.util.gen.TextHelper;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Utils {

    /**
     * @Description: The unique identifier for the mod.
     */
    public static final String MOD_ID = "audio";

    /**
     * @Main: The path to the zip supporting the resources for players' additional custom music,
     * and the main path to executor operation to the resource jar.
     * @Description: this zip usually used for being the container that store the .ogg music file
     * comes from the music folder, which is the resource folder for player to place custom music.
     */
    public static final String RESOURCE_ZIP_PATH = "./resourcepacks/audioresource.zip";
    /**
     * @Description: This is the path for client player to store the music, the music which is
     * stored in this folder should probably be translated into .ogg rather than .map3 or some
     * other song file type, in the process of generating audio sound the music in this file will
     * be put into the .zip and becomes the resource of *SoundEvent*.
     */
    public static final String MUSIC_FOLDER_PATH = "./music";
    /**
     * @Description: The path for generate the json file for custom music, which has the unique name
     * such as "custom_1", this path decide where the json file will be summoned, and later this
     * json file will be translated into the resource zip whose path is {@value RESOURCE_ZIP_PATH}
     * as the place to store this file.
     */
    public static final String JSON_GEN_PATH = "./sounds.json";
    /**
     * @Description: The list that hold all the custom sounds name in it, which is used for judge if
     * a sound source belongs to the player's custom song, the condition for the judgement is if the
     * playing song name has a pattern such as "custom_num".
     */
    public static final ArrayList<String> SOUND_SOURCE_PATH = new ArrayList<>();

    private Utils() {
    }

    public static IOHelper getIOHelper() {
        return IOHelper.getInstance();
    }

    public static JsonBuilder getJsonBuilder() {
        return JsonBuilder.getInstance();
    }

    public static TextHelper.Scroller getScroller(Supplier<String> sup , long delay , int length) {
        return TextHelper.Scroller.newInstance(sup , delay , length);
    }

    public static TextHelper.TipHelper getTipHelper() {
        return TextHelper.TipHelper.getInstance();
    }
}
