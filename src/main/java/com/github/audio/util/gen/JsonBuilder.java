package com.github.audio.util.gen;

import com.github.audio.api.exception.MultipleSingletonException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * @author Blu3 , clclfl
 * @Description: The tool class for operate Json file.
 */
public class JsonBuilder  {

    private static final LinkedHashMap<String, CustomSound> jsonMap = new LinkedHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static int num = 0;
    private static final Supplier<String> SUP = () -> "custom_" + (++ num);

    public JsonBuilder add(String... registryNames) {
        for (String s : registryNames) add(s);
        return this;
    }

    public JsonBuilder addCus(Supplier<Integer> sup) {
        for (int i = 0; i < sup.get(); i++) {
            add(SUP.get());
        }
        return this;
    }

    public JsonBuilder clear() {
        jsonMap.clear();
        return this;
    }

    public void toJson(File file) throws IOException{
        BufferedWriter br = new BufferedWriter(new FileWriter(file));
        br.write(GSON.toJson(jsonMap));
        br.close();
    }

    public void toJson(String path) throws IOException{
        toJson(new File(path));
    }

    public static JsonBuilder getJsonBuilder()  {
        jsonMap.clear();
        return JsonBuilderHolder.JSON_BUILDER;
    }

    public JsonBuilder add(String registryName) {
        ArrayList<SoundObject> tempList = new ArrayList<>();
        tempList.add(new SoundObject("audio:" + registryName, true));
        jsonMap.put(registryName, new CustomSound(tempList));
        return this;
    }

    private static class JsonBuilderHolder{
        private static final JsonBuilder JSON_BUILDER = new JsonBuilder();
    }

    private static class SoundObject {
        public SoundObject(String name, boolean stream) {
            this.name = name;
            this.stream = stream;
        }

        String name;
        boolean stream;
    }

    private static class CustomSound {
        public CustomSound(ArrayList<SoundObject> sounds) {
            this.sounds = sounds;
        }
        final ArrayList<SoundObject> sounds;
    }

    private JsonBuilder() {
        if (JsonBuilderHolder.JSON_BUILDER != null) {
            throw new MultipleSingletonException(JsonBuilderHolder.JSON_BUILDER);
        }
    }

    @Override
    public String toString() {
        return GSON.toJson(getJsonBuilder().jsonMap);
    }
}
