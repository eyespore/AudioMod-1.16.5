package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.Env;
import com.github.audio.api.ByteTransformer;
import com.github.audio.api.NameGenerator;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.util.IAudioTool;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

/**
 * @author clclFL
 * @Description: The file operator for the client side, which holds the responsibility about generating essential file
 * for the whole-mod running in correct order, could see the method, also take
 * the responsibility about the other stuff such as translate the ogg file into bytes or move ogg into the resource pack
 * to make sure while the ogg is call the song can be played correctly, could see the
 * method {@link com.github.audio.util.gen.ClientFileOperator#putOggIntoResourcePackMap()}, also contain some other responsibilities.
 * @see ByteTransformer
 * @see com.github.audio.util.IAudioTool
 */
@OnlyIn(Dist.CLIENT)
public final class ClientFileOperator implements IAudioTool {

    public static void main(String[] args) throws IOException {
        JSON_BUILDER.addMultiple(getClientFileOperator().generator3, 50).toJson(DEF_JSON_GENERATION_PATH);
    }

    /**
     * @Main: The path to the zip supporting the resources for players' additional custom music,
     * and the main path to executor operation to the resource jar.
     * @Description: this zip usually used for being the container that store the .ogg music file
     * comes from the music folder, which is the resource folder for player to place custom music.
     */
    public static final String DEF_RESOURCE_PACK_PATH = "./resourcepacks/audioresource.zip";
    /**
     * @Description: This is the path for client player to store the music, the music which is
     * stored in this folder should probably be translated into .ogg rather than .map3 or some
     * other song file type, in the process of generating audio sound the music in this file will
     * be put into the .zip and becomes the resource of *SoundEvent*.
     */
    public static final String DEF_MUSIC_FOLDER_PATH = "./music";
    /**
     * @Description: The path for generate the json file for custom music, which has the unique name
     * such as "custom_1", this path decide where the json file will be summoned, and later this
     * json file will be translated into the resource zip whose path is {@value DEF_RESOURCE_PACK_PATH}
     * as the place to store this file.
     */
    public static final String DEF_JSON_GENERATION_PATH = "./sounds.json";
    public static final String DEF_OGG_DESTINATION_PATH = "assets/audio/sounds";
    public static final String DEF_MODEL_RESOURCE_PACK_PATH = "assets/audio/resourcepacks/audioresource.zip";
    public static final String DEF_JSON_DESTINATION_PATH = "assets/audio";
    public static final String DEF_MODEL_OGG_PATH = "assets/audio/sounds/model_ogg.ogg";

    private static final JsonBuilder JSON_BUILDER = JsonBuilder.getJsonBuilder();
    private static final ByteTransformer BYTE_TRANSFORMER = ByteTransformer.getByteTransformer();

    /**
     * @Description: This is for matching the name with registry name.
     */
    public final HashMap<String, AudioSoundContext> contextMap = new HashMap<>();

    /**
     * @Description: The container that holds the data in the music folder.
     */
    private HashMap<String, byte[]> musicFolderBytesMap = new HashMap<>();
    /**
     * @Description: The container that holds the data in the resource zip.
     */
    private HashMap<String, byte[]> resourcePackBytesMap = new HashMap<>();
    private HashMap<String, byte[]> modelResourcePackBytesMap = new HashMap<>();
    /**
     * @Description: The path that points to the player's music folder.
     */
    private final File musicFolder;
    private final File resourcePack;
    /**
     * @Description: The path of model ogg file in the mod, this model ogg is mainly used for being cloned and then translate
     * into custom void ogg.
     */
    public final String modelOggPath;
    /**
     * @Description: The path that points to the resource pack.
     */
    private final String resourcePackPath;
    /**
     * @Description: The model resource's path, which should be in the inner side of mod.
     */
    private final String modelResourcePackPath;

    /**
     * @Description: The first generator is for multiple clone or generate the ogg file in the model resource pack.
     */
    private final NameGenerator generator1 = new NameGenerator(AudioRegistryHandler.DEF_REGISTRY_NAME);
    /**
     * @Description: The second generator is for the ogg registry, while move the ogg file from
     */
    private final NameGenerator generator2 = new NameGenerator(AudioRegistryHandler.DEF_REGISTRY_NAME);
    /**
     * @Description: The third generator is for the ogg json generator to use. this generator will make sure every object
     * that added into the json file has its own registry name for example "custom_num".
     */
    private final NameGenerator generator3 = new NameGenerator(AudioRegistryHandler.DEF_REGISTRY_NAME);

    /**
     * @Description: Put the entries bytes that bytesMap contains into the zip, mostly being used while there's a given map
     * that holds all the bytes in it and if the operator or the player want to flush the information it holds then this method
     * should be called.
     */
    public static void flushZip(String zipPath, Map<String, byte[]> bytesMap) throws IOException {
        JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(Paths.get(zipPath)));
        for (Map.Entry<String, byte[]> byteEntry : bytesMap.entrySet()) {
            outputStream.putNextEntry(new JarEntry(byteEntry.getKey()));
            outputStream.write(byteEntry.getValue() , 0 , byteEntry.getValue().length);
        }
        outputStream.close();
    }

    /**
     * @throws IOException This exception might be caused because the path is not in correctly writing or some unknown folder
     *                     or jar file cannot be open.
     * @Description: The main method for reconstruct the ogg files, while player reconstruct the music folder for example
     * adding new ogg file into it, then the player should be required to call this method to make sure the ogg construction
     * in the game is in the right order with the music folder, also this method should be the main method to generate all those
     * essential files that the whole mod needs, such as the ogg files, some directory, etc.
     * @see ClientFileOperator#readBytesIntoMap()
     * @see ClientFileOperator#putOggIntoResourcePackMap()
     * @see ClientFileOperator#flushResourcePack()
     * @see ClientFileOperator#generateResourcePack()
     * @see ClientFileOperator#generateMusicFolder()
     */
    public void clientFileGenerateCycle() throws IOException {
        generateResourcePack();
        generateMusicFolder();
        readBytesIntoMap();
        putOggIntoResourcePackMap();
        flushResourcePack();
    }

    public void flushOgg() throws IOException {
        if (isNullEnv()) return;
        putOggIntoResourcePackMap();
        flushResourcePack();
    }

    /**
     * @Description: This method will check if the environment is suitable for continuing running the code.
     * @return If lacking essential file or jar pack this method will return false, or this method will return true.
     */
    public boolean isNullEnv() {
        return !musicFolder.exists() || !resourcePack.exists();
    }

    public static ClientFileOperator getClientFileOperator() {
        return ClientFileOperatorHolder.CLIENT_FILE_OPERATOR;
    }

    private ClientFileOperator(RunningEnv env) {
        if (env.equals(RunningEnv.MAIN)) {
            musicFolder = new File("./run/music");
            modelOggPath = "./src/main/resources/assets/audio/sounds/model_ogg.ogg";
            resourcePackPath = "./run/resourcepacks/audioresource.zip";
            modelResourcePackPath = "./src/main/resources/assets/audio/resourcepacks/audioresource.zip";
        } else {
            musicFolder = new File(DEF_MUSIC_FOLDER_PATH);
            modelOggPath = Env.isOnTest() ? "../src/main/resources/assets/audio/sounds/model_ogg.ogg" : DEF_MODEL_OGG_PATH;
            resourcePackPath = Env.isOnTest() ? "../run/resourcepacks/audioresource.zip" : DEF_RESOURCE_PACK_PATH;
            modelResourcePackPath = Env.isOnTest() ? "../src/main/resources/assets/audio/resourcepacks/audioresource.zip" : DEF_MODEL_RESOURCE_PACK_PATH;
        }
        resourcePack = new File(resourcePackPath);
    }

    private void generateResourcePack() throws IOException {
        if (resourcePack.exists()) return;
        Audio.info(resourcePack.createNewFile() ? "Resource Pack is created." : "Try create resource pack but fail");
        resourcePackBytesMap = readModelResourcePack();
        flushResourcePack();
    }

    private void generateMusicFolder() {
        if (!musicFolder.exists())
            Audio.info(musicFolder.mkdir() ? "Music Folder is created." : "Try create music folder but fail.");
    }

    /**
     * @throws IOException Usually caused by the file cannot be correct translate into bytes.
     * @Description: Get current file bytes into the map, map will simply include the file name(which also contains the
     * construct in it), and the content of the file.
     */
    private void readBytesIntoMap() throws IOException {
        musicFolderBytesMap = readMusicFolder();
        resourcePackBytesMap = readResourcePack();
        modelResourcePackBytesMap = readModelResourcePack();
    }

    /**
     * @throws IOException Some exception might be aroused while calling this method, probably because the not correctly
     *                     using the method while the zip is still on opening and the io stream has no access to contact with
     *                     it.
     * @Description: Return the JarFile that points to the resource pack, used for operate the resource zip.
     */
    private JarFile getResourcePack() throws IOException {
        return new JarFile(resourcePackPath);
    }

    /**
     * @throws IOException This exception will be thrown while the resource cannot be correctly open, this usually
     *                     occur due the lack of default resource pack is not on the right position.
     * @Description: The model resource pack, which contains the original resource such as assets and sounds folder,
     * this resource pack should be used while the player doesn't have to default resource pack for containing the sound.
     * in that case the method {@link com.github.audio.util.gen.ClientFileOperator#generateResourcePack()} will simply generate one default resource pack
     * in the client side for containing the resource.
     */
    private JarFile getModelResourcePack() throws IOException {
        return new JarFile(modelResourcePackPath);
    }

    /**
     * @Description: Load current resource bytes map into resource pack, this method make sure the construct in the resource
     * pack be synchronized with {@link this#resourcePackBytesMap}
     */
    public void flushResourcePack() throws IOException {
        flushZip(resourcePackPath, resourcePackBytesMap);
    }

    /**
     * @Description: Load current resource bytes map into resource pack, this method make sure the construct in the model resource
     * pack be synchronized with {@link this#modelResourcePackBytesMap}
     */
    private void flushModelResourcePack() throws IOException {
        flushZip(modelResourcePackPath, modelResourcePackBytesMap);
    }

    @Deprecated
    public void putJsonIntoResourcePackMap() throws IOException {
        JSON_BUILDER.addMultiple(generator3, AudioRegistryHandler.REGISTRY_SOUND_EVENT_AMOUNT).toJson(DEF_JSON_GENERATION_PATH);
        putFileIntoResourcePackMap(DEF_JSON_DESTINATION_PATH, new File(DEF_JSON_GENERATION_PATH));
        Audio.info(new File(DEF_JSON_GENERATION_PATH).delete() ? "successfully delete original json file." : "fail in deleting json file.");
    }

    public void putOggIntoResourcePackMap() {
        generator2.reset();
        readMusicFolder().forEach((s, b) -> {
            String registryName = generator2.get();
            String displayName = s.split(".ogg")[0];
            contextMap.put(registryName, new AudioSoundContext(registryName, displayName, -1));
            resourcePackBytesMap.put(DEF_OGG_DESTINATION_PATH + "/custom/" + registryName + ".ogg", b);
        });
    }

    public void putFileIntoResourcePackMap(String destinationPath, File... resourceFiles) {
        Arrays.stream(resourceFiles).forEach(f -> resourcePackBytesMap.put(destinationPath + "/" + f.getName(), BYTE_TRANSFORMER.toByteArray(f)));
    }

    public int getMusicAmount() {
        if (!musicFolder.exists()) return -1;
        return Objects.requireNonNull(musicFolder.listFiles()).length;
    }

    /**
     * @Description: This method may cause problem while trying clone the model ogg file if the given Supplier for the name
     * is not in correctly writing, to make sure this method make successful cloning the name supplier give to the method
     * should have its own logic to generate the name, for example the class {@link NameGenerator}
     * @see NameGenerator
     */
    private void cloneModelOgg(Supplier<String> name, int amount) throws IOException {
        modelResourcePackBytesMap = readModelResourcePack();
        List<String> removeKeyList = modelResourcePackBytesMap.keySet().stream().filter(k -> k.startsWith(DEF_OGG_DESTINATION_PATH) && k.endsWith(".ogg")).collect(Collectors.toList());
        removeKeyList.forEach(modelResourcePackBytesMap::remove);
        byte[] modelOggBytes = modelResourcePackBytesMap.get(modelOggPath);
        for (int i = 0; i < amount; i++)
            modelResourcePackBytesMap.put(DEF_OGG_DESTINATION_PATH + "/" + name.get() + ".ogg", modelOggBytes);
        flushModelResourcePack();
    }

    /**
     * @Description: This method will cause problems while trying rename thd ogg files in the model resource pack if you
     * do not give the right name supplier, the name supplier is require to have its own logic to generate the name, such
     * as {@link NameGenerator}
     * @see NameGenerator
     */
    private void renameModelResourcePackOgg(Supplier<String> name) throws IOException {
        modelResourcePackBytesMap = readModelResourcePack();
        HashMap<String, byte[]> temBytesMap = new HashMap<>();
        modelResourcePackBytesMap.entrySet().stream().filter(e -> e.getKey().startsWith(DEF_OGG_DESTINATION_PATH) && e.getKey().endsWith(".ogg")).forEach(e -> temBytesMap.put("assets/audio/sounds/" + name.get() + ".ogg", e.getValue()));
        List<String> removeKeyList = modelResourcePackBytesMap.keySet().stream().filter(k -> k.startsWith(DEF_OGG_DESTINATION_PATH) && k.endsWith(".ogg")).collect(Collectors.toList());
        removeKeyList.forEach(modelResourcePackBytesMap::remove);
        modelResourcePackBytesMap.putAll(temBytesMap);
        flushModelResourcePack();
    }

    @Deprecated
    private void resetBytesMap(Map<String, byte[]> bytesMap, Map<String, byte[]> newBytesMap) {
        bytesMap.clear();
        bytesMap.putAll(newBytesMap);
    }

    /**
     * @Description: Read bytes from the folder music.
     */
    private HashMap<String, byte[]> readMusicFolder() {
        return (HashMap<String, byte[]>) BYTE_TRANSFORMER.toByteMap(musicFolder);
    }

    private HashMap<String, byte[]> readResourcePack() throws IOException {
        return (HashMap<String, byte[]>) BYTE_TRANSFORMER.toByteMap(getResourcePack());
    }

    private HashMap<String, byte[]> readModelResourcePack() throws IOException {
        return (HashMap<String, byte[]>) BYTE_TRANSFORMER.toByteMap(getModelResourcePack());
    }

    /**
     * @Description: This context is for audio sound registry, while trying to translate the information about the ogg into
     * the existed channel. there will be a map used for this.
     */
    @OnlyIn(Dist.CLIENT)
    public static final class AudioSoundContext {
        private final String registryName;
        private final String displayName;
        private final long duration;

        public String getDisplayName() {
            return displayName;
        }

        public long getDuration() {
            return duration;
        }

        public String getRegistryName() {
            return registryName;
        }

        public AudioSoundContext(String registryName, String displayName, long duration) {
            this.displayName = displayName;
            this.registryName = registryName;
            this.duration = duration;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientFileOperatorHolder {
        private static final ClientFileOperator CLIENT_FILE_OPERATOR = new ClientFileOperator(RunningEnv.RUN);
    }

    @OnlyIn(Dist.CLIENT)
    private enum RunningEnv {
        MAIN, RUN;
    }

}
