package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.api.NameGenerator;
import com.github.audio.util.IAudioTool;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

public class IOHelper implements IAudioTool {

    /**
     * @Description: The path to the .zip or .jar for resource folder which is mainly for store
     * the sound resource, at the very first of time this might be designed for .jar but later
     * this is probably for .zip's visit.
     */
    private final String resourcePackPath = ClientFileOperator.DEF_RESOURCE_PACK_PATH;
    /**
     * @Description: The zip or jar for visit while doing operation to the file, usually for
     * input resource from custom resource which is from client side.
     */
    private JarFile resourcePackZip;
    /**
     * @Description: This is the folder for player to store the origin music file which is probably
     * be translated into the type of .ogg, in the audio sound generate cycle the .ogg music in this
     * file will be all put into the resource path, into the given zip.
     */
    private final File musicFolder = new File(ClientFileOperator.DEF_MUSIC_FOLDER_PATH);
    private static final IOHelper IO_HELPER = new IOHelper();

    private static final NameGenerator gen = new NameGenerator();

    public static IOHelper getInstance() {
        return IO_HELPER;
    }

    private IOHelper() {
        try {
            this.resourcePackZip = new JarFile(ClientFileOperator.DEF_RESOURCE_PACK_PATH);
        } catch (IOException e) {
            Audio.warn("The given jar path could not be found, this is probably caused by the resource folder named \" audioresource.zip \"" +
                    " is not placed in the right place, plz check if you have download this resource pack and have it loaded on your minecraft.");
            e.printStackTrace();
        }
    }

    public File[] getMusicListFile() {
        return musicFolder.listFiles();
    }

    public static Optional<Long> getOggDuration(File file) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            OggInfoReader oggInfoReader = new OggInfoReader();
            GenericAudioHeader read = oggInfoReader.read(randomAccessFile);
            return Optional.of(toTicks(read.getPreciseTrackLength()));
        } catch (IOException | CannotReadException e) {
            e.printStackTrace();
        }
        Audio.warn("cannot correctly read ogg file.");
        return Optional.of(-1L);
    }

    private static <T extends Number> long toTicks(T second) {
        return Math.round(second.doubleValue() * 20);
    }

    public void generateResourcePack() {

    }

    public void generateMusicFolder() throws CannotReadException, IOException {
        if (!musicFolder.exists()) Audio.info(musicFolder.mkdir() ?
                "Music Folder is created at " + ClientFileOperator.DEF_MUSIC_FOLDER_PATH : "Try create music folder but fail.");
//        else
//            for (File file : Objects.requireNonNull(musicFolder.listFiles()))
//                AudioRegistryHandler.CUSTOM_FILE_MAP.put(file.getName().split(".ogg")[0],
//                        getOggDuration(file).orElse(-1L));
        //Debug
//        AudioRegistryHandler.CUSTOM_FILE_MAP.forEach((key, val) -> Audio.info("from DURATION :" + key + " : " + val));
    }

    public int countFile() {
        if (!musicFolder.exists()) return -1;
        return Objects.requireNonNull(musicFolder.listFiles()).length;
    }

    public int countJar() {
        return getEntryNames().size();
    }

    /**
     * @return an Array of String that contains the filename in the jar , also called 'entry'
     * @clclfl: return the file names that contained in the resource pack.
     */
    public List<String> getEntryNames() {
        return resourcePackZip.stream().map(JarEntry::getName).collect(Collectors.toList());
    }

    /**
     * @param entryName Which provided to locate the file or folder in jar.But remember,
     *                  if you want to extract a folder,please add a '/' behind or something
     *                  unexpected might happen.
     * @param filePath  Which provided to locate where you want to insert your file to.And notice
     *                  that it's path but not file name.
     */
    public void extractEntry(String entryName, String filePath) throws IOException {
        HashMap<String, byte[]> entryMap = new HashMap<>();
        Enumeration<JarEntry> iterator = new JarFile(resourcePackPath).entries();
        String signedName = processEntryName(entryName);
        while (iterator.hasMoreElements()) {
            JarEntry jarEntry = iterator.nextElement();
            if (jarEntry.getName().startsWith(entryName)) { //Sub or not
                if (jarEntry.isDirectory())
                    new File(signedName + jarEntry.getName().substring(entryName.length())).mkdir();
                else
                    entryMap.put(signedName + jarEntry.getName().substring(entryName.length()), toBytes(new JarFile(resourcePackPath), jarEntry));
            }
        }
        for (String externFileName : entryMap.keySet())
            new FileOutputStream(externFileName).write(entryMap.get(externFileName)); //We still need to judge here
    }

    public void deleteEntry(String entryName) throws IOException {
        HashMap<String, byte[]> entryMap = readZip(new JarFile(resourcePackPath));
        Set<String> collect = entryMap.keySet().stream().filter(k -> k.startsWith(entryName)).collect(Collectors.toSet());
        for (String key : collect) {
            entryMap.remove(key);
        }
        writeZip(entryMap);
        Audio.info("Entry deleted.");
    }

    /**
     * @param insertingFilePath the insertingFilePath you want to insert into a jar or zip
     * @param destinationPath         Which provided to locate where you want to insert your insertingFilePath to.And notice
     *                          that it's path but not insertingFilePath name.
     */
    public void insertFile(String insertingFilePath, String destinationPath) throws IOException {
        File insertingFile = new File(insertingFilePath);
        if (insertingFile.exists() && !insertingFile.isDirectory()) {
            HashMap<String, byte[]> originalZip = readZip();
            putEntry(originalZip, destinationPath + insertingFile.getName(), toBytes(insertingFile));
            writeZip(originalZip);
        } else
            Audio.warn("File not found or you just input a folder!");
    }
//    public void insertFile(File file, String entryPath) throws IOException {
//        if (file.exists() && !file.isDirectory()) {
//            HashMap<String, byte[]> entryMap = readZip();
//            rewriteEntry(entryMap, entryPath + file.getName(), toBytes(file));
//            writeZip(entryMap);
//        } else
//            Audio.warn("File not found or you just input a folder!");
//    }

    /**
     * @param keepParent To make sure if you want to keep the parent folder or not.For example,if you
     *                   want to insert whole "sounds/" into "assets/audio/",then true would make it
     *                   "assets/audio/sounds".oppositely false would make it "assets/audio/xxx.ogg(files in folder)"
     */
    public void folderInsert(boolean keepParent) throws IOException {
        if (musicFolder.exists() && musicFolder.isDirectory()) {
            Audio.info("Start inserting folder");
            HashMap<String, byte[]> entryMap = readZip(resourcePackZip);
            HashMap<String, byte[]> folderMap = readFolder(musicFolder);
            folderMap.forEach((fileName, fileContent) -> {
                if (keepParent)
                    putEntry(entryMap, ClientFileOperator.DEF_OGG_DESTINATION_PATH + musicFolder.getName() + '/' + fileName, fileContent);
                else
                    putEntry(entryMap, ClientFileOperator.DEF_OGG_DESTINATION_PATH + fileName, fileContent);
            });
            writeZip(entryMap);
            Audio.info("Done insertion");
        } else
            Audio.warn("Folder not found or you just input a file!");
    }

    /**
     * @param keepParent if this parameter is true, then after copying all ogg files into resource pack, the music file will
     *                   still keep its own construct, this parameter is still on bata.
     * @Description: This method is for translate ogg file from folder "music" into resource pack.
     */
    public void moveOgg(boolean keepParent) throws IOException {
        if (musicFolder.exists() && musicFolder.isDirectory()) {
            HashMap<String, byte[]> resourcePackBytes = readZip();
            readFolder().forEach((fileName, fileContent) -> {
                String registryName = gen.get();
                String displayName = fileName.split("ogg")[0];
                String zipFileName = ClientFileOperator.DEF_OGG_DESTINATION_PATH + (keepParent ? musicFolder.getName() + "/" : "") + registryName + ".ogg";
//                ByteTransformer.getClientFileOperator().tagMap.put(registryName, displayName);
                putEntry(resourcePackBytes, zipFileName, fileContent);
            });
            writeZip(resourcePackBytes);
        }
    }

    private static byte[] toBytes(JarFile zip, JarEntry zipEntry) {
        byte[] bytes = new byte[2048];
        try {
            InputStream inputStream = zip.getInputStream(zipEntry);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(bytes)) != -1) outputStream.write(bytes, 0, len);
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Audio.warn("sound unknown exception occur while trying translate zip file into bytes.");
        return bytes;
    }

//    private static byte[] toBytes(JarFile modJar, JarEntry jarEntry) throws IOException {
//        InputStream inputStream = modJar.getInputStream(jarEntry);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[2048];
//        int len = 0;
//        while ((len = inputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, len);
//        }
//        inputStream.close();
//        outputStream.close();
//        return outputStream.toByteArray();
//    }

    static byte[] toBytes(File file) {
        byte[] bytes = new byte[2048];
        try {
            InputStream inputStream = Files.newInputStream(file.toPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(bytes)) != -1) outputStream.write(bytes, 0, len);
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Audio.warn("sound unknown exception occur while trying translate file into bytes.");
        return bytes;
    }

    /**
     * @return
     * @Description: Read bytes from the folder music.
     */
    private HashMap<String, byte[]> readFolder() {
        return readFolder(musicFolder);
    }

    private HashMap<String, byte[]> readZip() {
        return readZip(resourcePackZip);
    }

    public static HashMap<String, byte[]> readFolder(File folder) {
        return (HashMap<String, byte[]>) Arrays.stream(Objects.requireNonNull(folder.listFiles())).collect(Collectors.toMap(File::getName, IOHelper::toBytes));
    }

    public static HashMap<String, byte[]> readZip(JarFile resourcePack) {
        return (HashMap<String, byte[]>) resourcePack.stream().collect(Collectors.toMap(JarEntry::getName, e -> toBytes(resourcePack, e)));
    }

    /**
     * @param byteMap The byte read from the file source.
     * @Description: The method that used for write entry into the zip, which is the resource pack in the game.
     */
    private void writeZip(HashMap<String, byte[]> byteMap) throws IOException {
        JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(Paths.get(ClientFileOperator.DEF_RESOURCE_PACK_PATH)));
        byteMap.forEach((k, v) -> {
            try {
                outputStream.putNextEntry(new JarEntry(k));
                outputStream.write(v, 0, v.length);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        outputStream.close();
    }

    private void putEntry(HashMap<String, byte[]> entryMap, String key, byte[] value) {
        entryMap.put(key, value);
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
}
