package com.github.audio;

import com.github.audio.sound.SoundEventHelper;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.util.text.*;
import org.jaudiotagger.audio.exceptions.CannotReadException;

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
        public static void extractMusic() {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            String folderPath = "./music";
            File file = new File(folderPath);
            if (!file.exists()) {
                if (file.mkdir()) {
                    Audio.getLOGGER().info("folder created!");
                }
            }
            if (Objects.requireNonNull(file.list()).length == 0) {
                try {
                    JarFile modJar = new JarFile("./mods/qqa's Mp3-1.1.1.jar");
                    for (Enumeration<JarEntry> entry = modJar.entries(); entry.hasMoreElements(); ) {
                        JarEntry jarEntry = entry.nextElement();
                        if (jarEntry.getName().contains("ogg")) {
                            inputStream = modJar.getInputStream(jarEntry);
                            String[] split = jarEntry.getName().split("/");
                            String fileName = split[split.length - 1];
                            File targetFile = new File(folderPath + File.separator + fileName);
                            outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                            byte[] bytes = new byte[2048];
                            int len;
                            while ((len = inputStream.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, len);
                            }
                            outputStream.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void getDurationFromFile() {
            String folderPath = "./music";
            File file = new File(folderPath);
            if (!file.exists()) {
                if (file.mkdir()) {
                    Audio.getLOGGER().info("folder created!");
                }
            }
            if (file.list() != null) {
                String[] fileList = file.list();
                for(String fileName : fileList) {
                    file = new File(folderPath + File.separator + fileName);

                        if (!fileName.contains("backpack")) {
                            String registryName = fileName.split(".ogg")[0];
                            try {
                                SoundEventRegistryHandler.duration.put(registryName, SoundEventHelper.getSongDuration(file).get());
                            } catch (IOException | CannotReadException e) {
                                e.printStackTrace();
                                }
                        }
                }
                //Debug
                Set keyset = SoundEventRegistryHandler.duration.keySet();
                for (Object key : keyset) {
                    Object val = SoundEventRegistryHandler.duration.get(key);
                    Audio.getLOGGER().info("duration now " + key + " : " + val);
                }
            }

        }
    }

}
