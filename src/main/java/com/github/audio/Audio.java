package com.github.audio;

import com.github.audio.client.config.Config;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.SoundEventHelper;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mod(Utils.MOD_ID)
public class Audio
{
    private static final Logger LOGGER = LogManager.getLogger();


    public Audio() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();

        ItemRegisterHandler.ITEM_REGISTER.register(eventBus);
        SoundEventRegistryHandler.SOUND_REGISTER.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
            eventBus.addListener(this::doClientStuff);

        context.registerConfig(ModConfig.Type.COMMON, Config.AUDIO_CONFIG);
        context.registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
    /*
    @SubscribeEvent
    public void registryPhase(final RegistryEvent event) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String folderPath = "./music";
        File file = new File(folderPath);
        if (!file.exists()) {
            if (file.mkdir()) {
                Audio.getLOGGER().info("Folder created!");
            }
        } else {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; i++) {
                file = new File(folderPath + File.separator + fileList[i]);
                try {
                    //Audio.getLOGGER().info(fileList[i] + " : " + SoundEventHelper.getSongDuration(file).get());
                    if (!fileList[i].contains("backpack")) {
                        String registryName = fileList[i].split(".ogg")[0];
                        //getLOGGER().info("We have " + registryName);
                        duration.put(registryName, SoundEventHelper.getSongDuration(file).get());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Object key : duration.keySet()) {
                Object val = duration.get(key);
                getLOGGER().info("duration now " + key + " : " + val);
            }

        }
        if (file.isDirectory() && Objects.requireNonNull(file.list()).length == 0) {
            try {
                JarFile modJar = new JarFile("./mods/qqa's Mp3-1.1.1.jar");
                for (Enumeration<JarEntry> e = modJar.entries(); e.hasMoreElements(); ) {
                    JarEntry jarEntry = e.nextElement();
                    if (jarEntry.getName().contains("ogg")) {
                        inputStream = modJar.getInputStream(jarEntry);
                        String[] split = jarEntry.getName().split("/");
                        String fileName = split[split.length - 1];
                        File targetFile = new File(folderPath + File.separator + fileName);
                        outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                        byte[] bytes = new byte[2048];
                        int len;
                        while ((len = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, len);
                        }
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */
    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
    }
    @SubscribeEvent
    public void doClientStuff(final FMLClientSetupEvent event) {
        KeyBinds.register();
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
