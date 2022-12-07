package com.github.audio.client;

import com.github.audio.master.client.IAudioExecutor;
import com.github.audio.client.gui.ConfigScreen;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.client.commands.ReloadResourceCommand;
import com.github.audio.client.config.Config;
import com.github.audio.networking.NetworkingHandler;
import com.github.audio.networking.BackPackSoundPack;
import com.github.audio.sound.AudioRegistryHandler;
import com.github.audio.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    public static boolean isPlayerExist = false;

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new ReloadResourceCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
            isPlayerExist = false;
            return;
        }
        isPlayerExist = true;
    }

    @SubscribeEvent
    public static void playerUnfoldBPListener(GuiOpenEvent event) {
        if (!event.isCanceled() && event.getGui() instanceof InventoryScreen) {
            //Multiple or Single
            ClientPlayerEntity playerClient = Minecraft.getInstance().player;
            if (playerClient != null) {
                if (Config.BACK_PACK_SOUND_STATUE.get() == 0 || Config.BACK_PACK_SOUND_STATUE.get() == 1) {
                    playerClient.playSound(AudioRegistryHandler.BACKPACK_UNFOLD_SOUND.getSoundEvent(), SoundCategory.PLAYERS, 3f, 1f);
                }
                NetworkingHandler.BACKPACK_SOUND_CHANNEL.sendToServer(
                        new BackPackSoundPack(playerClient.getUniqueID(),
                                true, true, playerClient.getPosition()));
            }
        }
    }

    public static void onFoldBackpack() {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null) {
            IAudioExecutor.playAudio(AudioRegistryHandler.BACKPACK_FOLD_SOUND , clientPlayer);
        }
    }

//    @SubscribeEvent
    public static void onMp3Deleted(GuiScreenEvent.MouseClickedEvent event) {
        if (!event.isCanceled() && event.getGui().getClass().getName()
                .equals("net.minecraft.client.gui.screen.inventory.CreativeScreen")) {
            System.out.println(event.getResult());
        }
    }

    public static void onUnFoldBackpack() {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null && AudioRegistryHandler.BACKPACK_UNFOLD_SOUND != null) {
            IAudioExecutor.playAudio(AudioRegistryHandler.BACKPACK_UNFOLD_SOUND , clientPlayer);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) return;
        Minecraft client = Minecraft.getInstance();
        if (KeyBinds.settingMenu.isPressed()) client.displayGuiScreen(new ConfigScreen());
    }

//    @SubscribeEvent
//    public static void onWorldTick(TickEvent.WorldTickEvent event) {
//        if (!event.world.isRemote) return;
//        if (!Exec.PLAYER_UUID_LIST.isEmpty() && Mp3HandleMethod.lastPlaybackChecked < event.world.getGameTime()) {
//            Exec.PLAYER_UUID_LIST.entrySet().removeIf(entry -> {
//                if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
//                    return true;
//                }
//                return false;
//            });
//        }
//    }
}

