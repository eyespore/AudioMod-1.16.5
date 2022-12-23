package com.github.audio.master.client.exec;

import com.github.audio.api.annotation.Exec;
import com.github.audio.client.config.Config;
import com.github.audio.master.client.AudioExecutor;
import com.github.audio.master.net.BackPackPacket;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.registryHandler.NetworkHandler;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Exec(Dist.CLIENT)
public class BackpackExecutor extends AudioExecutor {

    private static final BackpackExecutor BACKPACK_EXECUTOR = new BackpackExecutor();

    public static BackpackExecutor getExecutor() {
        return BACKPACK_EXECUTOR;
    }

    public void onFoldBackpack() {
        playAudio(AudioRegistryHandler.BACKPACK_FOLD_SOUND);
    }

    public void onUnFoldBackpack() {
        playAudio(AudioRegistryHandler.BACKPACK_UNFOLD_SOUND);
    }

    @SubscribeEvent
    public void playerUnfoldBPListener(GuiOpenEvent event) {
        if (isNullEnv() || !(event.getGui() instanceof InventoryScreen)) return;
        //Multiple or Single
        if (Config.BACK_PACK_SOUND_STATUE.get() == 0 || Config.BACK_PACK_SOUND_STATUE.get() == 1) {
            playAudio(AudioRegistryHandler.BACKPACK_UNFOLD_SOUND);
            NetworkHandler.BACKPACK_SOUND_CHANNEL.sendToServer(
                    new BackPackPacket(getUUID().get(), true, true));
        }
    }
}

