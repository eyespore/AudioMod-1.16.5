package com.github.audio;

import com.github.audio.client.config.Config;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Utils.MOD_ID)
public class Audio
{
    private static final Logger LOGGER = LogManager.getLogger();

    public Audio() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();

        ItemRegisterHandler.register(eventBus);
        SoundEventRegistryHandler.register(eventBus);

        eventBus.addListener(this::onRegistry);
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

    public void setup(final FMLCommonSetupEvent event) {

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

    public void onRegistry(final FMLModIdMappingEvent event) {
        getLOGGER().info("------------------------- marked event detected -------------------------");
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

}
