package com.github.audio.util.gen;

import com.github.audio.util.IAudioTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Supplier;


/**
 * @author clclFL , blu3
 * @Description: The builder for generate essential custom ogg json file which is in the resource pack.
 */
@OnlyIn(Dist.CLIENT)
public class JsonBuilder implements IAudioTool {

    private JsonBuilder() {}

    private final LinkedHashMap<String, soundEvent> JSON_MAP = new LinkedHashMap<>();
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public JsonBuilder clear() {
        JSON_MAP.clear();
        return this;
    }

    public void toJson(File file) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(file));
        br.write(GSON.toJson(JSON_MAP));
        br.close();
    }

    public void toJson(String path) throws IOException {
        toJson(new File(path));
    }

    /**
     * @Description: add new json object that point the ogg file into the json file.
     */
    public JsonBuilder add(String registryName, String oggName) {
        ArrayList<ogg> oggs = new ArrayList<>();
        oggs.add(new ogg("audio:" + oggName, true));
        JSON_MAP.put(registryName, new soundEvent(oggs));
        return this;
    }

    public JsonBuilder add(String registryName, String oggFolderPath, String oggName) {
        ArrayList<ogg> oggs = new ArrayList<>();
        oggs.add(new ogg("audio:" + oggFolderPath + "/" + oggName, true));
        JSON_MAP.put(registryName, new soundEvent(oggs));
        return this;
    }

    public JsonBuilder add(String registryName, String oggFolderPath, Supplier<String> oggNameGenerator, int amount) {
        ArrayList<ogg> oggs = new ArrayList<>();
        for (int i = 0; i < amount; i++)
            oggs.add(new ogg("audio:" + oggFolderPath + "/" + oggNameGenerator.get(), true));
        JSON_MAP.put(registryName, new soundEvent(oggs));
        return this;
    }

    /* For custom ogg registry */
    public JsonBuilder addMultiple(Supplier<String> registryNameGenerator, int amount) {
        for (int i = 0; i < amount; i++) {
            ArrayList<ogg> oggs = new ArrayList<>();
            String registryName = registryNameGenerator.get();
            oggs.add(new ogg("audio:" + registryName, true));
            JSON_MAP.put(registryName, new soundEvent(oggs));
        }
        return this;
    }

    /* For custom ogg registry */
    public JsonBuilder addMultiple(Supplier<String> registryNameGenerator, String oggFolderPath, int amount) {
        for (int i = 0; i < amount; i++) {
            ArrayList<ogg> oggs = new ArrayList<>();
            String registryName = registryNameGenerator.get();
            oggs.add(new ogg("audio:" + oggFolderPath + "/" + registryName, true));
            JSON_MAP.put(registryName, new soundEvent(oggs));
        }
        return this;
    }

    public static JsonBuilder getJsonBuilder() {
        return new JsonBuilder();
    }

    private static class ogg {

        private final String name;
        private final boolean stream;

        public ogg(String path, boolean isStream) {
            this.name = path;
            this.stream = isStream;
        }
    }

    private static class soundEvent {

        private final ArrayList<ogg> sounds;

        public soundEvent(ArrayList<ogg> oggs) {
            this.sounds = oggs;
        }
    }

    @Override
    public String toString() {
        return GSON.toJson(JSON_MAP);
    }
}
