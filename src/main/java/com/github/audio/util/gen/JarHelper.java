package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.sound.AudioSound;
import com.github.audio.util.IAudioTool;
import com.github.audio.util.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class JarHelper implements IAudioTool {

    private String jarPath;

    public static final JarHelper JAR_HELPER = new JarHelper(Utils.RESOURCE_ZIP_PATH);

    private static final AudioSound.NameGenerator gen = new AudioSound.NameGenerator();

    public static JarHelper getInstance() {
        return JAR_HELPER;
    }

    private JarHelper(String jarPath) {
        this.jarPath = jarPath;
    }

    public void setJarPath(String jarPath) {
        JAR_HELPER.jarPath = jarPath;
    }

    public static final File MUSIC_FOLDER_FILE = new File("./music");

    public int countFile() {
        if (!MUSIC_FOLDER_FILE.exists()) return -1;
        return Objects.requireNonNull(MUSIC_FOLDER_FILE.listFiles()).length;
    }

    public int countJar() throws IOException {
        return getZipFileNames().size();
    }

    /**
     * @return an Array of String that contains the filename in the jar , also called 'entry'
     * @clclfl: May better use method : getZipFileNames()
     */
    @SuppressWarnings("resource")
    @Deprecated
    public String[] getEntries() throws IOException {
        JarFile modJar = new JarFile(jarPath);
        ArrayList<String> fileList = new ArrayList<>();
        Enumeration<JarEntry> entries = modJar.entries();
        while (entries.hasMoreElements()) {
            fileList.add((entries.nextElement()).getName());
        }
        return fileList.toArray(new String[0]);
    }

    @SuppressWarnings("resource")
    public List<String> getZipFileNames() throws IOException {
        return new JarFile(Utils.RESOURCE_ZIP_PATH).stream().map(ZipEntry::getName).collect(Collectors.toList());
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
                    entryMap.put(signedName + jarEntry.getName().substring(entryName.length()), readJar(new JarFile(jarPath), jarEntry));
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
            Audio.info("Start inserting file");
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath));
            rewriteMap(entryMap, entryPath + file.getName(), readFile(file));
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
            Audio.info("Done insertion");
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
                        rewriteMap(entryMap, entryPath + folder.getName() + '/' + fileName, fileContent) :
                        rewriteMap(entryMap, entryPath + fileName, fileContent);

            });
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
            Audio.info("Done insertion");
        } else
            Audio.warn("Folder not found or you just input a file!");
    }

    public void simpleInsert(File file, String entryPath, boolean keepParent) throws IOException {
        if (!file.exists() && !file.isDirectory()) return;
        Audio.info("Start inserting folder");
        TreeMap<String, byte[]> destination = readJar(new JarFile(jarPath)); //resource
        TreeMap<String, byte[]> resources = readFolder(file); //music
        resources.forEach((fileName, fileContent) -> {
                    if (keepParent)
                        rewriteMap(destination, entryPath + file.getName() + '/' + gen.get() + ".ogg", fileContent);
                    else
                        rewriteMap(destination, entryPath + gen.get() + ".ogg", fileContent);
                }
        );
        writeJar(destination, new JarOutputStream(new FileOutputStream(jarPath)));
    }

    private byte[] readIntoByte(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        while ((len = stream.read(buffer)) != -1) outputStream.write(buffer, 0, len);
        stream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }


    private byte[] readJar(JarFile modJar, JarEntry jarEntry) throws IOException {
        InputStream inputStream = modJar.getInputStream(jarEntry);
        return readIntoByte(inputStream);
    }

    private byte[] readFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return readIntoByte(inputStream);
    }

    private TreeMap<String, byte[]> readFolder(File folder) throws IOException {
        TreeMap<String, byte[]> fileInfo = new TreeMap<>();
        for (File file : Objects.requireNonNull(folder.listFiles())) fileInfo.put(file.getName(), readFile(file));
        TreeMap<String, String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().charAt(0) - o2.toLowerCase().charAt(0);
            }
        });
        return fileInfo;
    }

    private void writeJar(TreeMap<String, byte[]> entryMap, JarOutputStream outputStream) throws IOException {
        for (Map.Entry<String, byte[]> entry : entryMap.entrySet()) {
            outputStream.putNextEntry(new JarEntry(entry.getKey()));
            outputStream.write(entry.getValue(), 0, entry.getValue().length);
        }
        outputStream.close();
    }

    private TreeMap<String, byte[]> readJar(JarFile jar) {
        TreeMap<String, byte[]> jarInfo = new TreeMap<>();
        jar.stream().forEach(e -> {
            try {
                jarInfo.put(e.getName() , readJar(jar, e));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return jarInfo;
    }

    private boolean rewriteMap(TreeMap<String, byte[]> entryMap, String key, byte[] value) {
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
