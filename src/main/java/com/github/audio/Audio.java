package com.github.audio;

import com.github.audio.registryHandler.NetworkHandler;
import com.github.audio.registryHandler.ExecRegistryHandler;
import com.github.audio.client.config.Config;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.AudioGenerateCycle;
import com.github.audio.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Utils.MOD_ID)
public class Audio
{
    private static final Logger LOGGER = LogManager.getLogger();

    public Audio() {
        IEventBus ModEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus ForgeEventBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext context = ModLoadingContext.get();

        ItemRegisterHandler.register(ModEventBus);
        AudioGenerateCycle.cycle(ModEventBus);


        ModEventBus.addListener(this::setup);
        ModEventBus.addListener(this::enqueueIMC);
        ModEventBus.addListener(this::processIMC);
        ModEventBus.addListener(this::doClientStuff);

        context.registerConfig(ModConfig.Type.COMMON, Config.AUDIO_CONFIG);
        context.registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true));

        // Register ourselves for server and other game events we are interested in
        ForgeEventBus.register(this);
        /* For executor registry */
        ExecRegistryHandler.registryExecutor(ForgeEventBus);
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::registerMessage);
    }

    public void doClientStuff(final FMLClientSetupEvent event) {
        KeyBinds.register();
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    public void onServerStarting(FMLServerStartingEvent event) {
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

}
