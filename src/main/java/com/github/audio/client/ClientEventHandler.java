package com.github.audio.client;

import com.github.audio.client.gui.ConfigScreen;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.client.commands.ReloadResourceCommand;
import com.github.audio.client.config.Config;
import com.github.audio.networking.NetworkingHandler;
import com.github.audio.networking.BackPackSoundPack;
import com.github.audio.sound.AudioSoundRegistryHandler;
import com.github.audio.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.Objects;

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
                    playerClient.playSound(AudioSoundRegistryHandler.BACKPACK_UNFOLD_SOUND.getSoundEvent(), SoundCategory.PLAYERS, 3f, 1f);
                }
                NetworkingHandler.BACKPACK_SOUND_CHANNEL.sendToServer(
                        new BackPackSoundPack(playerClient.getUniqueID(),
                                true, true, playerClient.getPosition()));
            }
        }
    }

    /**
     * Handle the sound play event when backpack is folded, include player and the other players in
     * the server if player playing game in the multiple world.
     *
     * @param soundPlayPos the position where the sound will play in the world.
     */
    public static void onPlayerFoldBackpack(BlockPos soundPlayPos) {
        ClientPlayerEntity playerClient = Minecraft.getInstance().player;
        if (playerClient != null) {
            Objects.requireNonNull(Minecraft.getInstance().world)
                    .playSound(playerClient, soundPlayPos, AudioSoundRegistryHandler.BACKPACK_FOLD_SOUND.getSoundEvent(),
                            SoundCategory.PLAYERS, 3f, 1f);
        }
    }

    //TODO : Make function to judge when player delete item mp3 in creative inventory.
//    @SubscribeEvent
    public static void onMp3Deleted(GuiScreenEvent.MouseClickedEvent event) {
        if (!event.isCanceled() && event.getGui().getClass().getName()
                .equals("net.minecraft.client.gui.screen.inventory.CreativeScreen")) {
            System.out.println(event.getResult());
        }
    }

    public static void onPlayerUnFoldBackpack(BlockPos soundPlayPos) {
        ClientPlayerEntity playerClient = Minecraft.getInstance().player;
        if (playerClient != null && AudioSoundRegistryHandler.BACKPACK_UNFOLD_SOUND != null) {
            Objects.requireNonNull(Minecraft.getInstance().world).playSound(playerClient, soundPlayPos,
                    AudioSoundRegistryHandler.BACKPACK_UNFOLD_SOUND.getSoundEvent(), SoundCategory.PLAYERS, 3f, 1f);
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
//        if (!AudioExecutor.PLAYER_UUID_LIST.isEmpty() && Mp3HandleMethod.lastPlaybackChecked < event.world.getGameTime()) {
//            AudioExecutor.PLAYER_UUID_LIST.entrySet().removeIf(entry -> {
//                if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
//                    return true;
//                }
//                return false;
//            });
//        }
//    }
}

