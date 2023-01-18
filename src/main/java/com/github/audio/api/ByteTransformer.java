package com.github.audio.api;

import com.github.audio.util.IAudioTool;
import com.github.audio.util.gen.IOHelper;
import com.github.audio.util.gen.ServerFileOperator;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author clclFL
 * @Description: The super class of File Operator, used for operate the io, while avoiding problems caused by various kinds
 * of exceptions, basic method and logic, is from {@link IOHelper}, This is the main class for operate the file, which is mainly
 * for translate file into byte. For the method about translate file into bytes, for jar or zip entry you could see the Method
 * {@link ByteTransformer#toByteArray(JarFile, JarEntry)} and for file see the method {@link ByteTransformer#toByteArray(File)}
 * server side, see {@link ServerFileOperator}
 * @see IOHelper
 */
public class ByteTransformer implements IAudioTool {

    private static final ByteTransformer BYTE_TRANSFORMER = new ByteTransformer();
    private static final byte[] DIRECTORY_BYTE_ARRAY = new byte[2048];

    private ByteTransformer() {
    }

    public static ByteTransformer getByteTransformer() {
        return BYTE_TRANSFORMER;
    }

    @Deprecated
    public Map<String, byte[]> toByteMap(File folder) {
        return Arrays.stream(Objects.requireNonNull(folder.listFiles())).collect(Collectors.toMap(File::getName, this::toByteArray));
    }

    @Deprecated
    public Map<String, byte[]> toByteMap(JarFile jar) {
        return jar.stream().collect(Collectors.toMap(JarEntry::getName, e -> toByteArray(jar, e)));
    }

    public void ofNameCollection(File file, Collection<String> collection) {
        ofNameCollection(file , collection , File::getAbsolutePath);
    }

    public void ofNameCollection(File file, Collection<String> collection, Function<File , String> toString) {
        collection.add(toString.apply(file));
        if (!file.isDirectory()) return;
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> ofNameCollection(f , collection , toString));
    }

    public void ofByteMap(JarFile jar, Map<String, byte[]> byteMap, ByteArrayOutputStream bos) {
        ofByteMap(jar, byteMap, bos, JarEntry::getName);
    }

    public void ofByteMap(JarFile jar, Map<String, byte[]> byteMap, ByteArrayOutputStream bos, Function<JarEntry , String> toString) {
        jar.stream().forEach(e -> byteMap.put(toString.apply(e), toByteArray(jar, e, bos)));
    }

    public void ofByteMap(File file, Map<String, byte[]> byteMap, ByteArrayOutputStream bos) {
        ofByteMap(file, byteMap, bos, File::getAbsolutePath);
    }

    public void ofByteMap(File file, Map<String, byte[]> byteMap, ByteArrayOutputStream bos, Function<File, String> toString) {
        if (!file.isDirectory()) {
            byteMap.put(toString.apply(file), toByteArray(file, bos));
            return;
        }

        byteMap.put(toString.apply(file), DIRECTORY_BYTE_ARRAY);
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> ofByteMap(f, byteMap, bos, toString));
    }

    /**
     * @param jar      The jar that for being operated
     * @param jarEntry The entry that in the jar which is going to be read into bytes.
     * @return The bytes that translated from the given jar entry.
     * @Description: This method is used for translate given jar entry into bytes, this method might cause problem if the
     * given entry cannot be correctly read.
     */
    public byte[] toByteArray(JarFile jar, JarEntry jarEntry) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] toReturn = processJarToByteArray(jar, jarEntry, outputStream);
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public byte[] toByteArray(JarFile jar, JarEntry jarEntry, ByteArrayOutputStream bos) {
        bos.reset();
        return processJarToByteArray(jar, jarEntry, bos);
    }

    /**
     * @Description: Mainly use for a-single-file translate, for multiple translation may better use the another
     * method {@link ByteTransformer#toByteArray(File, ByteArrayOutputStream)}
     */
    public byte[] toByteArray(File file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] toReturn = processFileToByteArray(file, outputStream);
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * @Description: Used for multiple-files translation work, this method need to close the bos manually. For single-file
     * translation work may better use the method {@link ByteTransformer#toByteArray(File)}
     */
    public byte[] toByteArray(File file, ByteArrayOutputStream bos) {
        bos.reset();
        return processFileToByteArray(file, bos);
    }

    private byte[] processJarToByteArray(JarFile jar, JarEntry jarEntry, ByteArrayOutputStream bos) {
        try {
            BufferedInputStream bis = new BufferedInputStream(jar.getInputStream(jarEntry));
            return processToByteArray(bos, bis);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[2048];
        }
    }

    private byte[] processFileToByteArray(File file, ByteArrayOutputStream bos) {
        try {
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            byte[] toReturn = processToByteArray(bos, bis);
            bis.close();
            return toReturn;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[2048];
        }
    }

    private byte[] processToByteArray(ByteArrayOutputStream bos, InputStream is) throws IOException {
        byte[] bytes = new byte[2048];
        int len;
        while ((len = is.read(bytes)) != -1) bos.write(bytes, 0, len);
        return bos.toByteArray();
    }

}
