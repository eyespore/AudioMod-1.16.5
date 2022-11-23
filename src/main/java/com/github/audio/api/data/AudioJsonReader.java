package com.github.audio.api.data;

import com.github.audio.Utils;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import jdk.nashorn.internal.runtime.options.Option;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AudioJsonReader {

    private static final HashMap<String , Integer[]> DURATION_MAP = new HashMap<>();
    private static final Gson GSON = new Gson();

    static {
        DURATION_MAP.put("hit_the_floor" , new Integer[]{1 , 2 , 3});
        DURATION_MAP.put("hello_world" , new Integer[]{2 , 5 , 7});
        DURATION_MAP.put("prison_2" , new Integer[]{12 , 5 , 16});
        DURATION_MAP.put("come_and_see" , new Integer[]{18 , 19});
    }

    public static void main(String[] args) throws IOException {
        File file = new File("audio_data");
        if (!file.exists()) {
            System.out.println(file.mkdir() ? "Missing audio_data dir, now creating..." : "fail to creating def audio_data dir.");
        }
        System.out.println(GSON.toJson(DURATION_MAP));
    }

    public static Gson getGSON() {
        return GSON;
    }
}
