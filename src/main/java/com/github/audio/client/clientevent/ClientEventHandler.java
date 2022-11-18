package com.github.audio.client.clientevent;

import com.github.audio.Utils;
import com.github.audio.client.config.Config;
import com.github.audio.client.gui.ConfigScreen;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.networking.NetworkingHandler;
import com.github.audio.networking.BackPackSoundPack;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    public static boolean isHoldingMp3 = false;
    public static boolean hasInitSoundSourcePath = false;

    @SubscribeEvent
    public static void onSoundSourceChange(SoundEvent.SoundSourceEvent event) {

        if (!hasInitSoundSourcePath) {
            SoundHandler.initSoundSourcePath();
            hasInitSoundSourcePath = true;
        }

        if (SoundHandler.soundSourcePath.contains(event.getName())) {
            SoundHandler.currentSource = event.getSource();
            SoundHandler.currentSourceHasChanged = true;
//            HandleMethod.hasAutoSwitch = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof PlayerEntity) {
            ClientPlayerEntity playerClient = Minecraft.getInstance().player;
            if (playerClient != null) {
                if (event.getEntity().getUniqueID() == playerClient.getUniqueID()) {
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerUnfoldBPListener(GuiOpenEvent event) {
        if (!event.isCanceled() && event.getGui() instanceof InventoryScreen) {
            //Multiple or Single
            ClientPlayerEntity playerClient = Minecraft.getInstance().player;
            if (playerClient != null) {
                if (Config.BACK_PACK_SOUND_STATUE.get() == 0 || Config.BACK_PACK_SOUND_STATUE.get() == 1) {
                    playerClient.playSound(SoundEventRegistryHandler.BACKPACK_UNFOLD_SOUND.get(), SoundCategory.PLAYERS, 3f, 1f);
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
                    .playSound(playerClient, soundPlayPos, SoundEventRegistryHandler.BACKPACK_FOLD_SOUND.get(),
                            SoundCategory.PLAYERS, 3f, 1f);
        }
    }

    //TODO : Make function to judge when player delete item mp3 in creative inventory.
    @SubscribeEvent
    public static void onMp3Deleted(GuiScreenEvent.MouseClickedEvent event) {
        if (!event.isCanceled() && event.getGui().getClass().getName()
                .equals("net.minecraft.client.gui.screen.inventory.CreativeScreen")) {
            System.out.println(event.getResult());
        }
    }

    public static void onPlayerUnFoldBackpack(BlockPos soundPlayPos) {
        ClientPlayerEntity playerClient = Minecraft.getInstance().player;
        if (playerClient != null) {
            Objects.requireNonNull(Minecraft.getInstance().world).playSound(playerClient, soundPlayPos,
                    SoundEventRegistryHandler.BACKPACK_UNFOLD_SOUND.get(), SoundCategory.PLAYERS, 3f, 1f);
        }
    }

    /**
     * Key input event
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft client = Minecraft.getInstance();
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null) {

            if (KeyBinds.soundSetting.isPressed()) client.displayGuiScreen(new ConfigScreen());

            if (isHoldingMp3) {
                //switch to last disc
                if (KeyBinds.lastDisc.isPressed()) trySwitchToLast();
                //switch to next disc
                if (KeyBinds.nextDisc.isPressed()) trySwitchToNext();
                //try pause or resume the current disc
                if (KeyBinds.pauseAndResume.isPressed()) tryPauseOrResume();
            } else if (KeyBinds.lastDisc.isPressed() || KeyBinds.nextDisc.isPressed()
                    || KeyBinds.pauseAndResume.isPressed()) {
                return;
            }
        }
    }

    public static void trySwitchToLast() {
        HandleMethod.shouldSwitchToLast = true;
        HandleMethod.toBeSolved = HandleMethodType.SWITCH_TO_LAST;
    }

    public static void trySwitchToNext() {
        HandleMethod.shouldSwitchToNext = true;
        HandleMethod.toBeSolved = HandleMethodType.SWITCH_TO_NEXT;
    }

    public static void tryPauseOrResume() {
        HandleMethod.shouldPauseOrResume = true;
        HandleMethod.toBeSolved = HandleMethodType.PAUSE_OR_RESUME;
    }

    public static void playInitMusic(ClientPlayerEntity clientPlayer) {
//        SoundHandler.playSound(SoundEventRegistryHandler.SoundChannel.KATANA_ZERO_INIT ,
//                clientPlayer.getUniqueID() , clientPlayer.getEntityId());
    }
}

