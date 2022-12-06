package com.github.audio.client;

import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.util.Utils;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD , value = Dist.CLIENT)
public class PropertyRegistry {

//    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemModelsProperties.registerProperty(ItemRegisterHandler.Mp3.get() ,
                new ResourceLocation(Utils.MOD_ID , "time") ,
                (itemStack , clientWorld , livingEntity) -> {
                    return Objects.requireNonNull(clientWorld).getGameTime();
                }));
    }
}
