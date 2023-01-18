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
public class JarOperator {

    private final File fileOfJar;
    private final HashMap<String, byte[]> byteMap = new HashMap<>();
    private final ByteTransformer byteTransformer = ByteTransformer.getByteTransformer();
    private boolean isFlushed = false;

    private static final byte[] DIRECTORY_BYTE_ARRAY = new byte[2048];

    public boolean isFlushed() {
        return isFlushed;
    }

    public JarOperator(String path) throws IOException {
        if (!(path.endsWith(".zip") || path.endsWith(".jar")))
            throw new IOException("The given path \"" + path + "\" cannot be recognized as a jar or zip path.");
        this.fileOfJar = new File(path);
        JarFile jarFile = getJarFile();
        if (exists() && jarFile != null) {
            cover(byteTransformer.toByteMap(jarFile));
            jarFile.close();
            isFlushed = true;
        }
    }

    public void cover(Map<String, byte[]> newByteMap) {
        byteMap.clear();
        byteMap.putAll(newByteMap);
        isFlushed = false;
    }

    public void cover(JarOperator jarOperator) {
        cover(jarOperator.byteMap);
    }

    /**
     * @Description: Get the amount of entries in this jar package.
     */
    public int getCurrentEntryAmount() throws IOException {
        if (!fileOfJar.exists()) return -1;
        return entries().size();
    }

    /**
     * @Description: Get the amount of entries existed in the {@link JarOperator#byteMap}.
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
        JarFile jarFile = getJarFile();
        List<JarEntry> toReturn = jarFile.stream().collect(Collectors.toList());
        jarFile.close();
        return toReturn;
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
     * @Description: The default method to add file into the {@link JarOperator#byteMap}, this method will change the statue of the
     * parameter {@link JarOperator#isFlushed}, which is the signal that show if {@link JarOperator}'s instance has been flushed, with the
     * use of recursion, this method allow to add a directory into the byte map.
     * @see JarOperator#add(File)
     * @see JarOperator#add(String, File)
     */
    private void addFile(String pre, File file, String fileAbsolutePath, String fileName) {
        isFlushed = false;
        String fileNameInJar = pre + fileName + new StringBuilder(file.getAbsolutePath())
                .delete(0, fileAbsolutePath.length()).toString().replace("\\", "/");

        if (!file.isDirectory()) {
            byteMap.put(fileNameInJar, byteTransformer.toByteArray(file));
            return;
        }

        byteMap.put(fileNameInJar + "/" , DIRECTORY_BYTE_ARRAY);
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

    public static void main(String[] args) throws IOException {
        JarOperator jarOperator = new JarOperator("testJar.zip");
        if (!jarOperator.exists()) jarOperator.createNewJar();

        jarOperator.removeAll();

        jarOperator.add(new File("D:\\BaiduNetdiskDownload\\AudioMod-1.16.5\\src\\main\\java\\com\\github\\audio\\util"));
        jarOperator.flush();

        System.out.println(jarOperator);
    }

    @Deprecated
    public List<String> getEntryNames() throws IOException {
        return entries().stream().map(JarEntry::getName).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        byteMap.keySet().forEach(k -> toReturn.append(k).append('\n'));
        return toReturn.toString();
    }

    public void extractEntry(String entryName) throws IOException {
        String signedName = entryName.substring(entryName.lastIndexOf("/",entryName.length() - 2) + 1);//This is the replacement of the processEntryName()
        entries().forEach((entry) -> {
            if (entry.getName().startsWith(entryName)) {
                if (entry.isDirectory())
                    new File(signedName + entry.getName().substring(entryName.length())).mkdir();
                //TODO: finish the extract function.
            }
        });
    }

//    public void extractEntry(String jarPath, String entryName, String filePath) throws IOException {
//        JarFile modJar = new JarFile(jarPath);
//        TreeMap<String, byte[]> entryMap = new TreeMap<>();
//        Enumeration<JarEntry> iterator = getJarFile().entries();
//        String signedName = processEntryName(entryName);
//        while (iterator.hasMoreElements()) {
//            JarEntry jarEntry = iterator.nextElement();
//            if (jarEntry.getName().startsWith(entryName)) { //Sub or not
//                if (jarEntry.isDirectory()) //What if it's "assets/" and contains "assets/assets/assets/"
//                    new File(signedName + jarEntry.getName().substring(entryName.length())).mkdir();
//                else
//                    entryMap.put(signedName + jarEntry.getName().substring(entryName.length()), readEntry(modJar, jarEntry));
//            }
//        }
//        for (String externFileName : entryMap.keySet()) {
//            new FileOutputStream(externFileName).write(entryMap.get(externFileName));
//        }
//
//        modJar.close();
//    }
//
//    private static String processEntryName(String entryName) {
//        String[] entryPiece = entryName.split("/");
//        entryName = entryName.endsWith("/") ?
//                entryName.equals(entryPiece[entryPiece.length - 1] + '/') ?
//                        entryName : entryPiece[entryPiece.length - 1] + '/'
//                : entryName.equals(entryPiece[entryPiece.length - 1]) ?
//                entryName : entryPiece[entryPiece.length - 1];
//        return entryName;
//    }


}

