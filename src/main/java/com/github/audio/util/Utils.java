package com.github.audio.util;

import com.github.audio.Audio;
import net.minecraft.util.text.*;
import net.minecraftforge.event.TickEvent;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.io.File.separator;

public class Utils {

    public static final String MOD_ID = "audio";

    public static class CollectionHelper {
        @SafeVarargs
        public static <T> void add(List<T> list, T... t) {
            list.addAll(Arrays.asList(t));
        }
    }

    public static class AudioHelper {
        public static Optional<Long> getSongDuration(File file) throws IOException, CannotReadException {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            OggInfoReader oggInfoReader = new OggInfoReader();
            GenericAudioHeader read = oggInfoReader.read(randomAccessFile);
            return Optional.of(toTicks(read.getPreciseTrackLength()));
        }

        public static void initMusicFolderMap(final HashMap<String , Long> DURATION) {
            String folderPath = "./music";
            File musicFolder = new File(folderPath);
            if (!musicFolder.exists()) {
//                if (musicFolder.mkdir()) {
//                    Audio.getLOGGER().info("folder created!");
//                }
                Audio.getLOGGER().info(musicFolder.mkdir() ? "Folder created!" : "Folder create fail.");

            }
            if (musicFolder.list() != null) {
                String[] fileList = musicFolder.list();
                for (String fileName : fileList) {
                    File musicFile = new File(folderPath + separator + fileName);
                    String signedName = fileName.split(".ogg")[0];
                    try {
                            DURATION.put(signedName, getSongDuration(musicFile).get());
                    } catch (IOException | CannotReadException e) {
                        e.printStackTrace();
                    }
                }
                //Debug
                for (Object key : DURATION.keySet()) {
                    Object val = DURATION.get(key);
                    Audio.getLOGGER().info("duration now " + key + " : " + val);
                }
            }
        }

        public static <T extends Number> long toTicks(T second) {
            return Math.round(second.doubleValue() * 20);
        }
    }
}
