package com.github.audio.util;

import com.github.audio.util.gen.AudioHelper;
import com.github.audio.util.gen.JarHelper;
import com.github.audio.util.gen.JsonBuilder;
import com.github.audio.util.gen.TextHelper;

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

    private Utils() {
    }

    public static JarHelper getJarHelper() {
        return JarHelper.getInstance();
    }

    public static AudioHelper getAudioHelper() {
        return AudioHelper.getInstance();
    }

    public static JsonBuilder getJsonBuilder() {
        return JsonBuilder.getInstance();
    }

    public static TextHelper.RollerBuilder getRollerBuilder() {
        return TextHelper.RollerBuilder.getInstance();
    }

    public static TextHelper.TipHelper getTipHelper() {
        return TextHelper.TipHelper.getInstance();
    }
}
