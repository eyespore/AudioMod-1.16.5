package com.github.audio;

import com.github.audio.sound.AudioSoundRegistryHandler;
import net.minecraft.util.text.*;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {

    public static final String MOD_ID = "audio";

    //For font

    /**
     * Normal item tool tip.
     * The key's type for translation in the lang.json is "tooltip.clclfl.key"
     */
    public static class TipHelper {

        /**
         * return gray font to describe tool's tip and return DARK_GRAY FONT by default,
         * complete key name required.
         */
        public static ITextComponent getTip(String key) {
            return new TranslationTextComponent(key)
                    .setStyle(Style.EMPTY.applyFormatting(TextFormatting.DARK_GRAY));
        }

        /**
         * allow custom font format and return DARK_GRAY FONT by default,
         * complete key name required.
         */
        public static ITextComponent getTip(String key, TextFormatting... textFormattings) {
            IFormattableTextComponent ftc = new TranslationTextComponent(key);
            return ftc.setStyle(Style.EMPTY.mergeWithFormatting(textFormattings)
                    .applyFormatting(TextFormatting.DARK_GRAY));
        }
    }

    /* Rolling Format */
    public static Supplier<RollFontHelper> getRollingBar(String rollingFontBar) {
        return () -> new RollFontHelper(rollingFontBar);
    }

    public static Supplier<RollFontHelper> getRollingBar(String rollingFontBar, int disPlayLength) {
        return () -> new RollFontHelper(rollingFontBar, disPlayLength);
    }

    public static class RollFontHelper {
        private final int displayLength;
        private String rollingFont;

        /* Constructor */
        public RollFontHelper(String rollingFont, int displayLength) {
            this.rollingFont = rollingFont.length() <= displayLength ? rollingFont : rollingFont + "  ";
            this.displayLength = displayLength;
        }

        /* Constructor */
        public RollFontHelper(String rollingFont) {
            displayLength = 15;
            this.rollingFont = rollingFont.length() <= displayLength ? rollingFont : rollingFont + "  ";
        }

        public String nextRollingFormat() {
            if (rollingFont.length() <= displayLength) {
                return rollingFont;
            }
            String toReturn = rollingFont.substring(0, displayLength);
            rollingFont = rollingFont.substring(1) + rollingFont.split("")[0];
            return toReturn;
        }
    }

    public static class CollectionHelper {

        @SafeVarargs
        public static <T> void add(List<T> list, T... t) {
            list.addAll(Arrays.asList(t));
        }
    }

    public static class AudioHelper {
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

        public static void initMusicFolderMap(final HashMap<String , Long> DURATION) {
            String folderPath = "./music";
            File musicFolder = new File(folderPath);
            if (!musicFolder.exists()) {
                if (musicFolder.mkdir()) {
                    Audio.getLOGGER().info("folder created!");
                }
            }
            if (musicFolder.list() != null) {
                String[] fileList = musicFolder.list();
                for (String fileName : fileList) {
                    File musicFile = new File(folderPath + File.separator + fileName);

                    String signedName = fileName.split(".ogg")[0];
                    try {
                            DURATION.put(signedName, getSongDuration(musicFile).get());
                    } catch (IOException | CannotReadException e) {
                        e.printStackTrace();
                    }
                }
                //Debug
                Set keyset = DURATION.keySet();
                for (Object key : keyset) {
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
