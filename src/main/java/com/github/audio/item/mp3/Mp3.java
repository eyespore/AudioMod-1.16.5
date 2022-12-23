package com.github.audio.item.mp3;

import com.github.audio.master.client.exec.Mp3Executor;
import com.github.audio.creativetab.ModCreativeTab;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class Mp3 extends Item {

    public static boolean isHoldingMp3 = false;
    public static boolean isMp3InInventory = false;

    public static Enum<RelayMode> currentMode = RelayMode.DEFAULT;
    public static final ArrayList<RelayMode> MODE_LIST = new ArrayList<RelayMode>();

    public static Enum<RelayMode> getCurrentMode() {
        return currentMode;
    }

    public Mp3() {
        super(new Item.Properties().group(ModCreativeTab.TAB_AUDIO).maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        if (worldIn.isRemote) {
            Minecraft client = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = client.player;
            if (clientPlayer != null) {
                if (Screen.hasShiftDown()) {
                    Mp3Executor.getExecutor().stopDevice();
                } else {
                    currentMode = Mp3.MODE_LIST.get(Mp3.MODE_LIST.indexOf(currentMode) + 1 > Mp3.MODE_LIST.size() - 1 ?
                            0 : Mp3.MODE_LIST.indexOf(currentMode) + 1);
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World
            worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(getTooltip());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static ITextComponent getCurrentSoundITextComponent(String translationKey) {
        return new TranslationTextComponent(translationKey, Mp3Executor.getExecutor().displayStr);
    }

    private static ITextComponent getTooltip() {
        if (Minecraft.getInstance().player == null || Mp3Executor.getExecutor().getCtx() == null) return new StringTextComponent("Mp3");
        return Mp3Executor.getExecutor().getCtx().isPlaySong ?
                new TranslationTextComponent("item.audio.audio.hasSong",
                        getCurrentSoundITextComponent("item.audio.audio.nowPlaySong"))
                : Mp3Executor.getExecutor().getCtx().isPaused ?
                new TranslationTextComponent("item.audio.audio.hasSong",
                        getCurrentSoundITextComponent("item.audio.audio.isPauseNow"))
                : new TranslationTextComponent("item.audio.audio.hasSong",
                getCurrentSoundITextComponent("item.audio.audio.waitToPlay"));
    }

    private String getModeName() {
        return (currentMode.name().substring(0, 1) + currentMode.name().substring(1, currentMode.name().length()).toLowerCase());
    }

    @Override
    public ITextComponent getDisplayName(ItemStack p_200295_1_) {
        if (Minecraft.getInstance().player == null || Mp3Executor.getExecutor().getCtx() == null) return new StringTextComponent("Mp3");
        return Mp3Executor.getExecutor().getCtx().isPlaySong ? new TranslationTextComponent("displayName.audio.audio.playingNow", getModeName())
                : Mp3Executor.getExecutor().getCtx().isPaused ? new TranslationTextComponent("displayName.audio.audio.pausingNow", getModeName())
                : new TranslationTextComponent("displayName.audio.audio.waitToPlay", getModeName());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void inventoryTick(ItemStack stackIn, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) {
            isMp3InInventory = true;
        }

        if (entityIn instanceof PlayerEntity && isSelected && worldIn.isRemote) {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) return;
            clientPlayer.sendStatusMessage(getTooltip(), true);
            isHoldingMp3 = true;
            showDurabilityBar(stackIn);
        } else if (worldIn.isRemote) {
            isHoldingMp3 = false;
        }
    }

    //TODO : add other information displaying in the tooltip of mp3
    private static ITextComponent belowText() {
        return null;
    }

    private static ITextComponent getAfter() {
        return null;
    }

    public static enum RelayMode {
        DEFAULT, SINGLE, RANDOM;
    }

    public static void stopMp3() {
        Mp3Executor.getExecutor().stopDevice();
    }
}


