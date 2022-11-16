package com.github.audio.client.clientevent;

import com.github.audio.Audio;
import com.github.audio.Utils;
import com.github.audio.client.config.Config;
import com.github.audio.client.gui.ConfigScreen;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.networking.NetworkingHandler;
import com.github.audio.networking.BackPackSoundEventPack;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    public static boolean isHoldingMp3 = false;
    public static boolean hasInitSoundSourcePath = false;
    private static final ArrayList<String> soundSourcePath = new ArrayList<String>();

    /* To judge when exactly the custom sound source has changed */
    public static void initSoundSourcePath() {
        soundSourcePath.add("a_fine_red_mist");
        soundSourcePath.add("blue_room");
        soundSourcePath.add("breath_of_a_serpent");
        soundSourcePath.add("chemical_brew");
        soundSourcePath.add("china_town");
        soundSourcePath.add("come_and_see");
        soundSourcePath.add("driving_force");
        soundSourcePath.add("end_of_the_road");
        soundSourcePath.add("full_confession");
        soundSourcePath.add("hit_the_floor");
        soundSourcePath.add("katana_zero");
        soundSourcePath.add("meat_grinder");
        soundSourcePath.add("nocturne");
        soundSourcePath.add("overdose");
        soundSourcePath.add("prison_2");
        soundSourcePath.add("rain_on_bricks");
        soundSourcePath.add("silhouette");
        soundSourcePath.add("sneaky_driver");
        soundSourcePath.add("snow");
        soundSourcePath.add("third_district");
        soundSourcePath.add("you_will_never_know");
    }

    @SubscribeEvent
    public static void onSoundSource(SoundEvent.SoundSourceEvent event) {

        if (!hasInitSoundSourcePath) {
            initSoundSourcePath();
            hasInitSoundSourcePath = true;
        }

        if (soundSourcePath.contains(event.getName())) {
            SoundHandler.currentSource = event.getSource();
            Audio.getLOGGER().info("new sound source detected");
            SoundHandler.currentSourceHasChanged = true;
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof PlayerEntity) {
            ClientPlayerEntity playerClient = Minecraft.getInstance().player;
            if (playerClient != null) {
                if (event.getEntity().getUniqueID() == playerClient.getUniqueID()) {
//                Clclfl.getLogger().info("LeaveWorldEvent Result : " + event.getResult());
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
                        new BackPackSoundEventPack(playerClient.getUniqueID(),
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
        SoundHandler.shouldSwitchToLast = true;
    }

    public static void trySwitchToNext() {
        SoundHandler.shouldSwitchToNext = true;
    }

    public static void tryPauseOrResume() {
        SoundHandler.shouldPauseOrResume = true;
    }

    public static void playInitMusic(ClientPlayerEntity clientPlayer) {
//        SoundHandler.playSound(SoundEventRegistryHandler.SoundChannel.KATANA_ZERO_INIT ,
//                clientPlayer.getUniqueID() , clientPlayer.getEntityId());
    }
}

