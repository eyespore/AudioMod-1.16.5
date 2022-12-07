package com.github.audio.util.gen;

import com.github.audio.Audio;
import com.github.audio.sound.AudioSound;
import com.github.audio.util.IAudioTool;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

public class JarHelper implements IAudioTool {

    private String jarPath;
    private static final JarHelper JAR_HELPER = new JarHelper("./resourcepacks/audioresource.zip");
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
    public static final String RESOURCE_ZIP_PATH = "./resourcepacks/audioresource.zip";

    public int countFile() {
        if (!MUSIC_FOLDER_FILE.exists()) return -1;
        return Objects.requireNonNull(MUSIC_FOLDER_FILE.listFiles()).length;
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
        TreeMap<String,byte[]> entryMap = readJar(new JarFile(jarPath));
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
            reWriteMap(entryMap, entryPath + file.getName(), readFile(file));
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
            Audio.info("Start inserting folder");
            TreeMap<String, byte[]> entryMap = readJar(new JarFile(jarPath)); //resource
            TreeMap<String, byte[]> folderMap = readFolder(folder); //music
            folderMap.forEach((fileName, fileContent) -> {
                        if (keepParent)
                            reWriteMap(entryMap, entryPath + folder.getName() + '/' + gen.get() + ".ogg", fileContent);
                        else
                            reWriteMap(entryMap, entryPath + gen.get() + ".ogg", fileContent);
                    }
            );
            writeJar(entryMap, new JarOutputStream(new FileOutputStream(jarPath)));
        }
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
        TreeMap<String , String> map = new TreeMap<>(new Comparator<String>() {
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
