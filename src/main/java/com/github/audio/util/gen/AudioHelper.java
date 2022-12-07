package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.client.audio.AudioSelector;
import com.github.audio.sound.AudioSoundRegistryHandler;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

import static com.github.audio.sound.AudioSoundRegistryHandler.CUSTOM_FILE_MAP;

public class AudioHelper {

    public static final AudioHelper AUDIO_HELPER = new AudioHelper();

    private AudioHelper() {
    }

    public static final JarHelper JAR_HELPER = new JarHelper();

    public static void initBeforeRegistry() throws IOException, CannotReadException {
        AudioHelper.AUDIO_HELPER.initMusicFolderMap(CUSTOM_FILE_MAP);
        JsonBuilder.getJsonBuilder().addCus(JAR_HELPER::countFile).toJson("./sounds.json");
        JAR_HELPER.fileInsert(new File("./sounds.json"), "assets/audio/");
        JAR_HELPER.simpleInsert(new File("./music"), "assets/audio/sounds/", false);
    }

    /**
     * @param registryName registry name of some, divided into part with underscore and lowercase
     *                     such as "breath_of_a_serpent".
     * @return the length of song, the file for the song must be .ogg format.
     */
    public Optional<Long> getSongDuration(String registryName) throws IOException, CannotReadException {
        File file = new File("src\\main\\resources\\assets\\audio\\sounds\\" + registryName + ".ogg");
        return getSongDuration(file);
    }

    public Optional<Long> getSongDuration(File file) throws IOException, CannotReadException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        OggInfoReader oggInfoReader = new OggInfoReader();
        GenericAudioHeader read = oggInfoReader.read(randomAccessFile);
        return Optional.of(toTicks(read.getPreciseTrackLength()));
    }

    /**
     * @param DURATION A TreeMap that containing the song names and their durations.
     */
    public void initMusicFolderMap(final LinkedHashMap<String, Long> DURATION) throws CannotReadException, IOException {
        File musicFolder = new File("./music");
        if (!musicFolder.exists())
            Audio.getLOGGER().info(musicFolder.mkdir() ? "Folder created!" : "Folder create fail.");
        for (File file : Objects.requireNonNull(musicFolder.listFiles()))
            DURATION.put(file.getName().split(".ogg")[0], getSongDuration(file).get());
        //Debug
        for (Object key : DURATION.keySet()) {
            Object val = DURATION.get(key);
            Audio.getLOGGER().info("duration now " + key + " : " + val);
        }
    }

    private static <T extends Number> long toTicks(T second) {
        return Math.round(second.doubleValue() * 20);
    }
}
