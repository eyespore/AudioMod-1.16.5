package com.github.audio.util;

import com.github.audio.util.gen.AudioHelper;
import com.github.audio.util.gen.JarHelper;
import com.github.audio.util.gen.JsonBuilder;
import com.github.audio.util.gen.TextHelper;

public class Utils {

    public static final String MOD_ID = "audio";

    private Utils() {}

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
