package com.github.audio.client.audio.exec;

import com.github.audio.Audio;
import com.github.audio.Env;
import com.github.audio.api.annotation.Executor;
import com.github.audio.api.exception.NotLoadingExecutorException;
import com.github.audio.client.audio.AudioExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

import java.io.File;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

@OnlyIn(Dist.CLIENT)
public class ExecRegistryHandler {

    /* this path is for run client test. */
    private static final File FILE_FILE = new File("..\\src\\main\\java\\com\\github\\audio\\client\\audio\\exec");
    /* this path work properly ISwitchable think, probably the mod name */
    //TODO : you guys should figure out what if a player change the mod display name, in that case this path will not exist.
    private static final String JAR_PATH = "./mods/AudioMod-1.16.5-1.0.0.jar";

    /* the default folder for store the audio executor, if the executor class is defined out of this folder,
     * then the method will not detect its existence, then it will not be added in to the event but. */
    private static final String PRE = "com.github.audio.client.audio.exec";
    /* this path work in game environment. */
    private static final String JAR_PRE = "com/github/audio/client/audio/exec/";

    private static final ArrayList<AudioExecutor> TARGETS = new ArrayList<>();

    public static void registry(IEventBus eventBus) {
        try {
            Audio.getLOGGER().info(
                    TARGETS.addAll(getTarget(Env.getEnv().equals(Env.RUN_CLIENT) ? getFileClasses() : getJarClasses())) ?
                    "successfully adding executor into event bus." : "fail in adding executor into event bus.");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        for (Object t : TARGETS) {
            eventBus.register(t);
        }
    }

    private static List<AudioExecutor> getTarget(Set<Class<?>> classes) {
        List<Class<?>> collect = classes.stream().filter(c -> c.isAnnotationPresent(Executor.class)).collect(Collectors.toList());
        return collect.stream().map(c -> {
            try {
                Method getExecutor = c.getDeclaredMethod("getExecutor");
                return (AudioExecutor) getExecutor.invoke(null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            throw new NotLoadingExecutorException(c);
        }).collect(Collectors.toList());
    }

    private static Set<Class<?>> getFileClasses() throws ClassNotFoundException {
        return Arrays.stream(Objects.requireNonNull(FILE_FILE.listFiles()))
                .filter(file -> !file.isDirectory())
                .map(file -> PRE + "." + file.getName().substring(0, file.getName().length() - 5))
                .map(s -> {
                    try {
                        Class<?> registryExecutor = Class.forName(s);
                        Audio.getLOGGER().info("successfully registry executor " + registryExecutor.getName());
                        return registryExecutor;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    throw new RuntimeException("fail in loading executor");
                }).collect(Collectors.toSet());
    }

    private static Set<Class<?>> getJarClasses() throws IOException, ClassNotFoundException {
        Enumeration<JarEntry> entries = new JarFile(JAR_PATH).entries();
        ArrayList<JarEntry> list = new ArrayList<>();
        while (entries.hasMoreElements()) list.add(entries.nextElement());

        return list.stream().map(ZipEntry::getName)
                .filter(name -> name.startsWith(JAR_PRE) && name.endsWith(".class") && !name.contains("$"))
                .map(s -> s.replace('/', '.').substring(0, s.length() - 6))
                .map(s -> {
                    try {
                        Class<?> registryExecutor = Class.forName(s);
                        Audio.getLOGGER().info("successfully registry executor " + registryExecutor.getName());
                        return registryExecutor;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    throw new RuntimeException("fail in loading executor");
                }).collect(Collectors.toSet());
    }
}
