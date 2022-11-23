package com.github.audio.sound;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class SoundEventHelper {

    /**
     * @param registryName registry name of some, divided into part with underscore and lowercase
     *                     such as "breath_of_a_serpent".
     * @return the length of song, the file for the song must be .ogg format.
     */
    public static Optional<Long> getSongDuration(String registryName) throws IOException, CannotReadException {
        File file = new File("src\\main\\resources\\assets\\audio\\sounds\\" + registryName + ".ogg");
        return getSongDuration(file);
    }

    public static Optional<Long> getSongDuration(File file) throws IOException, CannotReadException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        OggInfoReader oggInfoReader = new OggInfoReader();
        GenericAudioHeader read = oggInfoReader.read(randomAccessFile);
        return Optional.of(toTicks(read.getPreciseTrackLength()));
    }

    /**
     * the song registry name should be the lower case and make sure they have been divided into part
     * by underscore such as "breath_of_a_serpent".
     */
    protected static String getSongName(String registryName) {
        String[] strings = registryName.split("_");
        StringBuilder toReturn = new StringBuilder();
        int i = 0;
        for (String str : strings) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
            if (i < strings.length - 1) toReturn.append(str).append(" ");
            else toReturn.append(str);
            i++;
        }
        return toReturn.toString();
    }

    public static <T extends Number> long toTicks(T second){
        return Math.round(second.doubleValue() * 20);
    }

    //Print out the sound duration in the dir.
    public static void main(String[] args) throws CannotReadException, IOException {
        File audioFile = new File("src\\main\\resources\\assets\\audio\\sounds");
        Arrays.stream(Objects.requireNonNull(audioFile.listFiles())).forEach(file -> {
            String registryName = file.getName().substring(0 , file.getName().lastIndexOf("."));
            try {
                System.out.println(registryName + " : " + getSongDuration(registryName).orElse(AudioSound.DEF_DURATION));;
            } catch (IOException | CannotReadException e) {
                e.printStackTrace();
            }
        });
    }

}
