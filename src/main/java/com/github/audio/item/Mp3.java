package com.github.audio.item;

import com.github.audio.client.clientevent.ClientEventHandler;
import com.github.audio.client.clientevent.HandleMethod;
import com.github.audio.client.clientevent.SoundHandler;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class Mp3 extends Item {

    public Mp3(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        //TODO : implement function for mp3's right click
        if (worldIn.isRemote) {
            Minecraft client = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = client.player;
            if (clientPlayer != null) {
                if (Screen.hasShiftDown()) {
                    SoundHandler.stopSound(Objects.requireNonNull(client.player).getUniqueID());
                    SoundHandler.resetAllParameter();
                    if (HandleMethod.shouldPlayEndSound) {
                        playMp3EndSound(clientPlayer);
                        HandleMethod.shouldPlayEndSound = false;
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public static void playMp3EndSound(ClientPlayerEntity clientPlayer) {
        SoundHandler.playTickableSound(new HandleMethod.AudioPlayerContext(SoundHandler.CURRENT_SOUND_CHANNEL,
                        clientPlayer.getUniqueID(), clientPlayer.getEntityId()),
                () -> SoundEventRegistryHandler.katanaZeroEnd, false);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World
            worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(getTooltip());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static ITextComponent getCurrentSoundITextComponent(String translationKey) {
        return new TranslationTextComponent(translationKey, SoundHandler.currentSongNameRollingBar);
    }

    private static ITextComponent getTooltip() {
        return HandleMethod.isPlaySong ?
                new TranslationTextComponent("item.audio.audio.hasSong",
                        getCurrentSoundITextComponent("item.audio.audio.nowPlaySong"))
                : HandleMethod.isPaused ?
                new TranslationTextComponent("item.audio.audio.hasSong",
                        getCurrentSoundITextComponent("item.audio.audio.isPauseNow"))
                : new TranslationTextComponent("item.audio.audio.hasSong",
                getCurrentSoundITextComponent("item.audio.audio.waitToPlay"));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack p_200295_1_) {
        return getAudioDisplayName();
    }

    private static ITextComponent getAudioDisplayName() {
        return HandleMethod.isPlaySong ? new TranslationTextComponent("displayName.audio.audio.playingNow")
                : HandleMethod.isPaused ? new TranslationTextComponent("displayName.audio.audio.pausingNow")
                : new TranslationTextComponent("displayName.audio.audio.waitToPlay");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void inventoryTick(ItemStack stackIn, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof PlayerEntity &&
                ((((PlayerEntity)entityIn).getHeldItemMainhand().isItemEqual(stackIn))
                || ((PlayerEntity)entityIn).getHeldItemOffhand().isItemEqual(stackIn))) {
            if (worldIn.isRemote) {
                ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
                if (clientPlayer == null) return;
                clientPlayer.sendStatusMessage(getTooltip() , true);
                ClientEventHandler.isHoldingMp3 = true;
            }
        } else {
            if (worldIn.isRemote) {
                ClientEventHandler.isHoldingMp3 = false;
            }
        }
    }

    //TODO : add other information displaying in the tooltip of mp3
    private static ITextComponent belowText() {
        return null;
    }

    private static ITextComponent getAfter() {
        return null;
    }
}


