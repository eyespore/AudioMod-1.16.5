package com.github.audio;

import net.minecraft.util.text.*;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

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

        public static void initMusicFolderMap(final HashMap<String, Long> DURATION) {
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
                    File musicFile = new File(folderPath + File.separator + fileName);

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

    public static class JarHelper {
        /**
         * @param jarPath Which provided to locate the mod jar , commonly in mods/xxx.jar
         * @return an Array of String that contains the filename in the jar , also called 'entry'
         */
        public static String[] getEntrys(String jarPath) throws IOException {
            JarFile modJar = new JarFile(jarPath);
            ArrayList<String> fileList = new ArrayList<>();
            Enumeration entrys = modJar.entries();
            while (entrys.hasMoreElements()) {
                fileList.add(((JarEntry) entrys.nextElement()).getName());
            }
            return fileList.toArray(new String[fileList.size()]);
        }

        //TODO : complete the folder process part
        public static void extractEntry(String jarPath, String entryName, String filePath) throws IOException {
            JarFile modJar = new JarFile(jarPath);
            JarEntry jarEntry = modJar.getJarEntry(entryName);
            HashMap<String, HashMap<String, HashMap>> fileStructure = new HashMap<>();
            OutputStream outputStream = null;
            TreeMap<String, byte[]> entryMap = new TreeMap<>();
            if (jarEntry.isDirectory()) {
                Enumeration iterator = modJar.entries();
                while (iterator.hasMoreElements()) {
                    jarEntry = (JarEntry) iterator.nextElement();
                    String queryName = jarEntry.getName();
                    if (queryName.contains(entryName + "/") && queryName.split(entryName + "/").length > 1) {

                    }
                }
                File file = new File(filePath);
                file.mkdir();
                //for ()
            }
            byte[] buffer = readEntry(modJar, jarEntry);
            outputStream.write(buffer);
            outputStream.close();
            modJar.close();
        }
        public static void insertJar(String jarPath, File file, String entryPath) throws IOException {
            Audio.getLOGGER().info("Now insert phase!");
            JarFile modJar = new JarFile(jarPath);
            JarOutputStream outputStream = null;    //Pay attention that this stream would clear the jar when you just give it a value
            TreeMap<String, byte[]> folder = null;
            byte[] externBuffer = null;
            TreeMap<String, byte[]> entryMap = new TreeMap<>();
            Enumeration<JarEntry> entrys = modJar.entries();
            while (entrys.hasMoreElements()) {
                JarEntry entry = entrys.nextElement();
                byte[] innerBuffer = readEntry(modJar, entry);
                entryMap.put(entry.getName(), innerBuffer);
            }

            if (file.isDirectory() && file.exists()) {
                Audio.getLOGGER().info("You are now trying to insert a folder.");
                folder = readFolder(file);
                for (Map.Entry<String, byte[]> iterator : folder.entrySet()) {
                    entryMap.remove(entryPath + file.getName() + '/' + iterator.getKey());
                    entryMap.put(entryPath + file.getName() + '/' + iterator.getKey(), iterator.getValue());
                    Audio.getLOGGER().info(entryPath + file.getName() + '/' + iterator.getKey());
                }
            } else if (file.exists()){
                Audio.getLOGGER().info("You are trying to insert a file");
                externBuffer = readFile(file);
                entryMap.remove(entryPath + file.getName());
                entryMap.put(entryPath + file.getName(), externBuffer);
            }

            outputStream = new JarOutputStream(new FileOutputStream(jarPath));
            for (Map.Entry<String, byte[]> stringEntry : entryMap.entrySet()) {
                JarEntry entry = new JarEntry(stringEntry.getKey());
                outputStream.putNextEntry(entry);
                outputStream.write(entryMap.get(entry.getName()), 0, entryMap.get(entry.getName()).length);
            }
            outputStream.close();
            modJar.close();
            Audio.getLOGGER().info("done insertion");
        }


        private static byte[] readEntry(JarFile modJar, JarEntry jarEntry) throws IOException {
            InputStream inputStream = modJar.getInputStream(jarEntry);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        }

        private static byte[] readFile(File file) throws IOException {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        }

        private static TreeMap<String, byte[]> readFolder(File folder) throws IOException {
            File[] fileList = folder.listFiles();
            TreeMap<String, byte[]> files = new TreeMap<>();
            for (File file : fileList) {
                files.put(file.getName(), readFile(file));
            }
            return files;
        }
    }
}
