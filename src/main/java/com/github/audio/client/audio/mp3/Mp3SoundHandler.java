package com.github.audio.client.audio.mp3;

import com.github.audio.Utils;
import com.github.audio.client.audio.AudioContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public final class Mp3SoundHandler {

    private Mp3SoundHandler() {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote) return;
        if (!AudioContext.PLAYER_UUID_LIST.isEmpty() && Mp3HandleMethod.lastPlaybackChecked < event.world.getGameTime()) {
            Mp3HandleMethod.lastPlaybackChecked = event.world.getGameTime();
            AudioContext.PLAYER_UUID_LIST.entrySet().removeIf(entry -> {
                if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
                    return true;
                }
                return false;
            });
        }
    }

    @SuppressWarnings({"unused", "java:S1172"})
    // needs to be here for addListener to recognize which event this method should be subscribed to
    public static void onWorldUnload(WorldEvent.Unload evt) {
        AudioContext.PLAYER_UUID_LIST.clear();
        Mp3HandleMethod.lastPlaybackChecked = 0;
    }

}
