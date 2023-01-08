package com.github.audio.api;

import com.github.audio.Audio;
import com.github.audio.util.IAudioTool;
import com.github.audio.util.gen.IOHelper;
import com.github.audio.util.gen.ServerFileOperator;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
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

    private ByteTransformer() {}

    public static ByteTransformer getByteTransformer() {
        return BYTE_TRANSFORMER;
    }

    public Map<String, byte[]> toByteMap(File folder) {
        return Arrays.stream(Objects.requireNonNull(folder.listFiles())).collect(Collectors.toMap(File::getName, this::toByteArray));
    }

    public Map<String, byte[]> toByteMap(JarFile jar) {
        return jar.stream().collect(Collectors.toMap(JarEntry::getName, e -> toByteArray(jar, e)));
    }

    /**
     * @param jar      The jar that for being operated
     * @param jarEntry The entry that in the jar which is going to be read into bytes.
     * @return The bytes that translated from the given jar entry.
     * @Description: This method is used for translate given jar entry into bytes, this method might cause problem if the
     * given entry cannot be correctly read.
     */
    public byte[] toByteArray(JarFile jar, JarEntry jarEntry) {
        try {
            byte[] bytes = new byte[2048];
            InputStream inputStream = jar.getInputStream(jarEntry);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(bytes)) != -1) outputStream.write(bytes, 0, len);
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Audio.warn("sound unknown exception occur while trying translate jar file into bytes.");
            return new byte[2048];
        }
    }

    public byte[] toByteArray(File file) {
        try {
            byte[] bytes = new byte[2048];
            InputStream inputStream = Files.newInputStream(file.toPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(bytes)) != -1) outputStream.write(bytes, 0, len);
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Audio.warn("sound unknown exception occur while trying translate file into bytes.");
            return new byte[2048];
        }
    }
}
