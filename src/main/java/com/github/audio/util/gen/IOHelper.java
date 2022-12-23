package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.sound.AudioSound;
import com.github.audio.util.IAudioTool;
import com.github.audio.util.Utils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class IOHelper implements IAudioTool {

    /**
     * @Description: The path to the .zip or .jar for resource folder which is mainly for store
     * the sound resource, at the very first of time this might be designed for .jar but later
     * this is probably for .zip's visit.
     */
    private final String jarPath;
    /**
     * @Description: The zip or jar for visit while doing operation to the file, usually for
     * input resource from custom resource which is from client side.
     */
    private final JarFile zip;
    /**
     * @Description: This is the folder for player to store the origin music file which is probably
     * be translated into the type of .ogg, in the audio sound generate cycle the .ogg music in this
     * file will be all put into the resource path, into the given zip.
     */
    private final File music;

    private static final IOHelper IO_HELPER = new IOHelper(Utils.RESOURCE_ZIP_PATH);
    private static final AudioSound.NameGenerator gen = new AudioSound.NameGenerator();

    public static IOHelper getInstance() {
        return IO_HELPER;
    }

    private IOHelper(String jarPath) {
        JarFile temZip;
        this.jarPath = jarPath;
        this.music = new File(Utils.MUSIC_FOLDER_PATH);
        try {
            temZip = new JarFile(jarPath);
        } catch (IOException e) {
            Audio.warn("The given jar path could not be found, this is probably caused by the resource folder named \" audioresource.zip \"" +
                    " is not placed in the right place, plz check if you have download this resource pack and have it loaded on your minecraft.");
            temZip = null;
        }
        this.zip = temZip;
    }

    public static Optional<Long> getOggDuration(File file) throws IOException, CannotReadException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        OggInfoReader oggInfoReader = new OggInfoReader();
        GenericAudioHeader read = oggInfoReader.read(randomAccessFile);
        return Optional.of(toTicks(read.getPreciseTrackLength()));
    }

    @Deprecated
    private static Optional<Long> getOggDuration(String registryName) throws IOException, CannotReadException {
        File file = new File("src\\main\\resources\\assets\\audio\\sounds\\" + registryName + ".ogg");
        return getOggDuration(file);
    }

    private static <T extends Number> long toTicks(T second) {
        return Math.round(second.doubleValue() * 20);
    }

    public void generateMusicFolder() throws CannotReadException, IOException {
        if (!music.exists()) {
            Audio.info(music.mkdir() ? "Music Folder is created at " + Utils.MUSIC_FOLDER_PATH
                    : "Try create music folder but fail.");
        } else for (File file : Objects.requireNonNull(music.listFiles()))
            AudioRegistryHandler.CUSTOM_FILE_MAP.put(file.getName().split(".ogg")[0],
                    getOggDuration(file).orElse(-1L));
        //Debug
//        AudioRegistryHandler.CUSTOM_FILE_MAP.forEach((key, val) -> Audio.info("from DURATION :" + key + " : " + val));
    }

    public int countFile() {
        if (!music.exists()) return -1;
        return Objects.requireNonNull(music.listFiles()).length;
    }

    public int countJar() throws IOException {
        return getEntrys().length;
    }

    /**
     * @return an Array of String that contains the filename in the jar , also called 'entry'
     */
    public String[] getEntrys() throws IOException {
        JarFile modJar = new JarFile(jarPath);
        ArrayList<String> fileList = new ArrayList<>();
        Enumeration entrys = modJar.entries();
        while (entrys.hasMoreElements()) {
            fileList.add(((JarEntry) entrys.nextElement()).getName());
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    /**
     * @param entryName Which provided to locate the file or folder in jar.But remember,
     *                  if you want to extract a folder,please add a '/' behind or something
     *                  unexpected might happen.
     * @param filePath  Which provided to locate where you want to insert your file to.And notice
     *                  that it's path but not file name.
     */
    public void extractEntry(String entryName, String filePath) throws IOException {
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

    public void deleteEntry(String entryName) throws IOException {
        TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath));
        Set<String> collect = entryMap.keySet().stream().filter(k -> k.startsWith(entryName)).collect(Collectors.toSet());
        for (String key : collect) {
            entryMap.remove(key);
        }
        writeJar(entryMap, new JarOutputStream(Files.newOutputStream(Paths.get(jarPath))));
        Audio.info("Entry deleted.");
    }

    /**
     * @param file      the file you want to insert into a jar or zip
     * @param entryPath Which provided to locate where you want to insert your file to.And notice
     *                  that it's path but not file name.
     */
    public void fileInsert(File file, String entryPath) throws IOException {
        if (file.exists() && !file.isDirectory()) {
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath));
            reWriteMap(entryMap, entryPath + file.getName(), readFile(file));
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
        } else
            Audio.warn("File not found or you just input a folder!");
    }

    /**
     * @param folder     the folder you want to insert into a jar or zip
     * @param entryPath  Which provided to locate where you want to insert your file to.And notice
     *                   that it's path but not file name.
     * @param keepParent To make sure if you want to keep the parent folder or not.For example,if you
     *                   want to insert whole "sounds/" into "assets/audio/",then true would make it
     *                   "assets/audio/sounds".oppositely false would make it "assets/audio/xxx.ogg(files in folder)"
     */
    public void folderInsert(File folder, String entryPath, boolean keepParent) throws IOException {
        if (folder.exists() && folder.isDirectory()) {
            Audio.info("Start inserting folder");
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath));
            TreeMap<String, byte[]> folderMap = readFolder(folder);
            folderMap.forEach((fileName, fileContent) -> {
                boolean reWrite = keepParent ?
                        reWriteMap(entryMap, entryPath + folder.getName() + '/' + fileName, fileContent) :
                        reWriteMap(entryMap, entryPath + fileName, fileContent);

            });
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
            Audio.info("Done insertion");
        } else
            Audio.warn("Folder not found or you just input a file!");
    }

    public void simpleInsert(File folder, String entryPath, boolean keepParent) throws IOException {
        if (folder.exists() && folder.isDirectory()) {
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath)); //resource
            TreeMap<String, byte[]> folderMap = readFolder(folder); //music
            folderMap.forEach((fileName, fileContent) -> {
                        if (keepParent)
                            reWriteMap(entryMap, entryPath + folder.getName() + '/' + sup.get() + ".ogg", fileContent);
                        else reWriteMap(entryMap, entryPath + sup.get() + ".ogg", fileContent);
                    }
            );
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
        }
    }

    private static int num = 0;
    private static final Supplier<String> sup = () -> "custom_" + (++num);

    private byte[] readEntry(JarFile modJar, JarEntry jarEntry) throws IOException {
        InputStream inputStream = modJar.getInputStream(jarEntry);
        return getBytes(inputStream);
    }

    private byte[] readFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return getBytes(inputStream);
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
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
        TreeMap<String, String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().charAt(0) - o2.toLowerCase().charAt(0);
            }
        });
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
        entryMap.remove(key);
        entryMap.put(key, value);
        return true;
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
