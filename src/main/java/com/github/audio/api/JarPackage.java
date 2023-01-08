package com.github.audio.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

/**
 * @author clclFL
 * @Description: The class for operate the jar or zip file, this class supply the essential method for operating a jar
 * or zip pack.
 */
public class JarPackage {

    private final File fileOfJar;
    private final HashMap<String, byte[]> byteMap = new HashMap<>();
    private final ByteTransformer byteTransformer = ByteTransformer.getByteTransformer();
    private boolean isFlushed = false;

    public boolean isFlushed() {
        return isFlushed;
    }

    public JarPackage(String path) throws IOException {
        if (!(path.endsWith(".zip") || path.endsWith(".jar")))
            throw new IOException("The given path \"" + path + "\" cannot be recognized as a jar or zip path.");
        this.fileOfJar = new File(path);
        if (exists() && getJarFile() != null) {
            cover(byteTransformer.toByteMap(getJarFile()));
            isFlushed = true;
        }
    }

    public void cover(Map<String, byte[]> newByteMap) {
        byteMap.clear();
        byteMap.putAll(newByteMap);
        isFlushed = false;
    }

    public void cover(JarPackage jarPackage) {
        cover(jarPackage.byteMap);
    }

    /**
     * @Description: Get the amount of entries in this jar package.
     */
    public int getCurrentEntryAmount() throws IOException {
        if (!fileOfJar.exists()) return -1;
        return entries().size();
    }

    /**
     * @Description: Get the amount of entries existed in the {@link JarPackage#byteMap}.
     */
    public int getMapEntryAmount() {
        if (!fileOfJar.exists()) return -1;
        return byteMap.size();
    }

    public String getName() {
        return fileOfJar.getName();
    }

    public String getPath() {
        return fileOfJar.getPath();
    }

    public String getAbsolutePath() {
        return fileOfJar.getAbsolutePath();
    }

    public boolean exists() {
        return fileOfJar.exists();
    }

    public boolean createNewJar() throws IOException {
        return fileOfJar.createNewFile();
    }

    public JarFile getJarFile() throws IOException {
        return new JarFile(fileOfJar.getAbsoluteFile());
    }

    public void remove(String path) {
        String p = path.replace("\\" , "/");
        List<String> toBeDeleted = byteMap.keySet().stream().filter(s -> s.length() >= p.length() && s.startsWith(p)).collect(Collectors.toList());
        toBeDeleted.forEach(byteMap::remove);
    }

    public void removeAll() {
        byteMap.clear();
    }

    public void renameEntry(String entryPath, String rename) {
        byteMap.put(new StringBuilder(entryPath).substring(0 , entryPath.lastIndexOf('/')) + rename , byteMap.remove(entryPath));
    }

    public List<JarEntry> entries() throws IOException {
        return getJarFile().stream().collect(Collectors.toList());
    }

    public void copy(File sourceFile) {
        copy("" , sourceFile , sourceFile::getName , 1);
    }

    public void copy(File sourceFile, String rename) {
        copy("" , sourceFile , () -> rename , 1);
    }

    public void copy(String destinationPath, File sourceFile, Supplier<String> rename) {
        copy(destinationPath , sourceFile , rename , 1);
    }

    public void copy(String destinationPath, File sourceFile, Supplier<String> rename, int amount) {
        for (int i = 0; i < amount; i++) {
            copy(destinationPath , sourceFile , rename.get());
        }
    }

    private void copy(String destinationPath, String sourceFilePath, String nameOfCopy) {
        copy(destinationPath , new File(sourceFilePath) , nameOfCopy);
    }

    private void copy(String destinationPath, File sourceFile, String nameOfCopy) {
        String absolutePath = sourceFile.getAbsolutePath().replace("\\" , "/");
        addFile(destinationPath.length() == 0 ? "" : destinationPath.replace("\\" , "/") + "/", sourceFile ,
                sourceFile.getAbsolutePath(), nameOfCopy);
    }

    private String getFileName(String absolutePathOfFile) {
        String[] result = absolutePathOfFile.replace("\\" , "/").split("/");
        return result[result.length - 1];
    }

    /**
     * @Description: Allow to add single or multiple file, or directory into a given path as destination in a jar or zip
     * package's path-byte map.
     */
    public void add(String destinationPath, File file) {
        addFile(destinationPath.length() == 0 ?
                "" : destinationPath.replace("\\" , "/") + "/", file, file.getAbsolutePath(), file.getName());
    }

    /**
     * @Description: While the given path of destination is in lack, the file will be automatically into the top directory
     * in the jar package.
     */
    public void add(File file) {
        addFile("", file, file.getAbsolutePath(), file.getName());
    }

    /**
     * @param pre  The destination for the file to be added.
     * @param file             The file which is going to be added into the jar.
     * @param fileAbsolutePath The absolute path of the given file, this parameter will mostly be given by the method that
     *                         in inside of the {@link ByteTransformer}
     * @param fileName         The name of the given file.
     * @Description: The default method to add file into the {@link JarPackage#byteMap}, this method will change the statue of the
     * parameter {@link JarPackage#isFlushed}, which is the signal that show if {@link JarPackage}'s instance has been flushed, with the
     * use of recursion, this method allow to add a directory into the byte map.
     * @see JarPackage#add(File)
     * @see JarPackage#add(String, File)
     */
    private void addFile(String pre, File file, String fileAbsolutePath, String fileName) {
        isFlushed = false;
        if (!file.isDirectory()) {
            byteMap.put(pre + fileName + new StringBuilder(file.getAbsolutePath())
                    .delete(0, fileAbsolutePath.length()).toString().replace("\\", "/"), byteTransformer.toByteArray(file));
            return;
        }
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> addFile(pre, f, fileAbsolutePath, fileName));
    }

    /**
     * @Description: Main method for flushing the file in the jar pack, after adding file or directory into the jar map
     * then this method should be called to flush the containing stuff in the jar or zip file.
     */
    public void flush() throws IOException {
        if (!exists()) return;
        JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(Paths.get(fileOfJar.getAbsolutePath())));
        for (Map.Entry<String, byte[]> byteEntry : byteMap.entrySet()) {
            outputStream.putNextEntry(new JarEntry(byteEntry.getKey()));
            outputStream.write(byteEntry.getValue(), 0, byteEntry.getValue().length);
        }
        outputStream.close();
        isFlushed = true;
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        byteMap.keySet().forEach(k -> toReturn.append(k).append('\n'));
        return toReturn.toString();
    }
}
