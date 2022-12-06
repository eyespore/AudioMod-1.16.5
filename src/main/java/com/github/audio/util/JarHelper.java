package com.github.audio.util;

import com.github.audio.Audio;
import com.github.audio.api.exception.MultipleSingletonException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarHelper {

    public static final JarHelper JAR_HELPER = new JarHelper();
    public static final AudioHelper AUDIO_HELPER = JAR_HELPER.new AudioHelper();

    private JarHelper() {
        if (JAR_HELPER != null) {
            throw new MultipleSingletonException("JAR_HELPER");
        }
    }

    /**
     * @param jarPath Which provided to locate the mod jar , commonly in mods/xxx.jar
     * @return an Array of String that contains the filename in the jar , also called 'entry'
     */
    public String[] getEntrys(String jarPath) throws IOException {
        JarFile modJar = new JarFile(jarPath);
        ArrayList<String> fileList = new ArrayList<>();
        Enumeration entrys = modJar.entries();
        while (entrys.hasMoreElements()) {
            fileList.add(((JarEntry) entrys.nextElement()).getName());
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    /**
     * @param jarPath   Which provided to locate the mod jar , commonly in mods/xxx.jar
     * @param entryName Which provided to locate the file or folder in jar.But remember,
     *                  if you want to extract a folder,please add a '/' behind or something
     *                  unexpected might happen.
     * @param filePath  Which provided to locate where you want to insert your file to.And notice
     *                  that it's path but not file name.
     */
    public void extractEntry(String jarPath, String entryName, String filePath) throws IOException {
        TreeMap<String, byte[]> entryMap = new TreeMap<>();
        Enumeration<JarEntry> iterator = new JarFile(jarPath).entries();
        String signedName = processEntryName(entryName);
        while (iterator.hasMoreElements()) {
            JarEntry jarEntry = iterator.nextElement();
            if (jarEntry.getName().startsWith(entryName)) { //Sub or not
                if (jarEntry.isDirectory())
                    new File(signedName + jarEntry.getName().substring(entryName.length())).mkdir();
                else
                    entryMap.put(signedName + jarEntry.getName().substring(entryName.length()), readEntry(new JarFile(jarPath), jarEntry));
            }
        }
        for (String externFileName : entryMap.keySet())
            new FileOutputStream(externFileName).write(entryMap.get(externFileName)); //We still need to judge here
    }

    /**
     * @param jarPath   Which provided to locate the mod jar , commonly in mods/xxx.jar
     * @param file      the file you want to insert into a jar or zip
     * @param entryPath Which provided to locate where you want to insert your file to.And notice
     *                  that it's path but not file name.
     */
    public void fileInsert(String jarPath, File file, String entryPath) throws IOException {
        if (file.exists() && !file.isDirectory()) {
            Audio.getLOGGER().info("Start inserting file");
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath));
            reWriteMap(entryMap, entryPath + file.getName(), readFile(file));
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
            Audio.getLOGGER().info("Done insertion");
        } else
            Audio.getLOGGER().warn("File not found or you just input a folder!");
    }

    /**
     * @param jarPath    Which provided to locate the mod jar , commonly in mods/xxx.jar
     * @param folder     the folder you want to insert into a jar or zip
     * @param entryPath  Which provided to locate where you want to insert your file to.And notice
     *                   that it's path but not file name.
     * @param keepParent To make sure if you want to keep the parent folder or not.For example,if you
     *                   want to insert whole "sounds/" into "assets/audio/",then true would make it
     *                   "assets/audio/sounds".oppositely false would make it "assets/audio/xxx.ogg(files in folder)"
     */
    public void folderInsert(String jarPath, File folder, String entryPath, boolean keepParent) throws IOException {
        if (folder.exists() && folder.isDirectory()) {
            Audio.getLOGGER().info("Start inserting folder");
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath));
            TreeMap<String, byte[]> folderMap = readFolder(folder);
            folderMap.forEach((fileName, fileContent) -> {
                boolean reWrite = keepParent ?
                        reWriteMap(entryMap, entryPath + folder.getName() + '/' + fileName, fileContent) :
                        reWriteMap(entryMap, entryPath + fileName, fileContent);

            });
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
            Audio.getLOGGER().info("Done insertion");
        } else
            Audio.getLOGGER().warn("Folder not found or you just input a file!");
    }

    private byte[] readEntry(JarFile modJar, JarEntry jarEntry) throws IOException {
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

    private byte[] readFile(File file) throws IOException {
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

    private TreeMap<String, byte[]> readFolder(File folder) throws IOException {
        File[] fileList = folder.listFiles();
        TreeMap<String, byte[]> files = new TreeMap<>();
        for (File file : fileList) {
            files.put(file.getName(), readFile(file));
        }
        return files;
    }

    private void writeJar(TreeMap<String, byte[]> entryMap, JarOutputStream outputStream) throws IOException {
        for (Map.Entry<String, byte[]> entrySet : entryMap.entrySet()) {
            JarEntry entry = new JarEntry(entrySet.getKey());
            outputStream.putNextEntry(entry);
            outputStream.write(entrySet.getValue(), 0, entrySet.getValue().length);
        }
        outputStream.close();
    }

    private TreeMap<String, byte[]> readJar(JarFile modJar) throws IOException {
        TreeMap<String, byte[]> entryMap = new TreeMap<>();
        Enumeration<JarEntry> entrys = modJar.entries();
        while (entrys.hasMoreElements()) {
            JarEntry entry = entrys.nextElement();
            entryMap.put(entry.getName(), readEntry(modJar, entry));
        }
        return entryMap;
    }

    private boolean reWriteMap(TreeMap<String, byte[]> entryMap, String key, byte[] value) {
        try {
            entryMap.remove(key);
            entryMap.put(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String processEntryName(String entryName) {
        String[] entryPiece = entryName.split("/");
        entryName = entryName.endsWith("/") ?
                entryName.equals(entryPiece[entryPiece.length - 1] + '/') ?
                        entryName : entryPiece[entryPiece.length - 1] + '/'
                : entryName.equals(entryPiece[entryPiece.length - 1]) ?
                entryName : entryPiece[entryPiece.length - 1];
        return entryName;
    }

    public class AudioHelper {

        private AudioHelper() {
            if (AUDIO_HELPER != null) {
                throw new MultipleSingletonException(AUDIO_HELPER);
            }
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
        public void initMusicFolderMap(final HashMap<String, Long> DURATION) throws CannotReadException, IOException {
            File musicFolder = new File("./music");
            if (!musicFolder.exists())
                Audio.getLOGGER().info(musicFolder.mkdir() ? "Folder created!" : "Folder create fail.");
            if (musicFolder.list() != null) {
                String[] fileList = musicFolder.list();
                for (String fileName : fileList) {
                    File musicFile = new File(musicFolder.getName() + File.separator + fileName);
                    DURATION.put(fileName.split(".ogg")[0], getSongDuration(musicFile).get());
                }
                //Debug
                for (Object key : DURATION.keySet()) {
                    Object val = DURATION.get(key);
                    Audio.getLOGGER().info("duration now " + key + " : " + val);
                }
            }
        }
    }

    private static <T extends Number> long toTicks(T second) {
        return Math.round(second.doubleValue() * 20);
    }
}
