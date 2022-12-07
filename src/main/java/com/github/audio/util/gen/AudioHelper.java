package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.util.IAudioTool;
import com.github.audio.util.Utils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

import static com.github.audio.sound.AudioRegistryHandler.CUSTOM_FILE_MAP;

public class AudioHelper implements IAudioTool {

    public static AudioHelper getInstance() {
        return AUDIO_HELPER;
    }

    private static final AudioHelper AUDIO_HELPER = new AudioHelper();

    private AudioHelper() {
    }

    public static final JarHelper JAR_HELPER = Utils.getJarHelper();

    public static void initBeforeRegistry() throws IOException, CannotReadException {
        AudioHelper.AUDIO_HELPER.initMusicFolderMap(CUSTOM_FILE_MAP);
        JsonBuilder.getInstance().addCus(JAR_HELPER::countFile).toJson("./sounds.json");
        JAR_HELPER.fileInsert(new File("./sounds.json"), "assets/audio/");
        JAR_HELPER.simpleInsert(new File("./music"), "assets/audio/sounds/", false);
    }

    /**
     * @param registryName registry name of some, divided into part with underscore and lowercase
     *                     such as "breath_of_a_serpent".
     * @return the length of song, the file for the song must be .ogg format.
     */
    @Deprecated
    private Optional<Long> getOggDuration(String registryName) throws IOException, CannotReadException {
        File file = new File("src\\main\\resources\\assets\\audio\\sounds\\" + registryName + ".ogg");
        return getOggDuration(file);
    }

    private Optional<Long> getOggDuration(File file) throws IOException, CannotReadException {
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
        if (!musicFolder.exists()) {
            Audio.info(musicFolder.mkdir() ? "Folder created!" : "Folder create fail.");
        } else for (File file : Objects.requireNonNull(musicFolder.listFiles()))
                DURATION.put(file.getName().split(".ogg")[0], getOggDuration(file).orElse(-1L));

        //Debug
        DURATION.forEach((key, val) -> Audio.info("from DURATION :" + key + " : " + val));
    }

    private static <T extends Number> long toTicks(T second) {
        return Math.round(second.doubleValue() * 20);
    }
}
