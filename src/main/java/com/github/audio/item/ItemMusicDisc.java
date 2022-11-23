package com.github.audio.item;

import com.github.audio.Utils;
import com.github.audio.creativetab.ModCreativeTab;
import com.github.audio.sound.AudioSoundRegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMusicDisc {

    private static final Item.Properties PROP = new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO);

    public static class EndOfTheRoadMusicDisc extends MusicDiscItem {

        public EndOfTheRoadMusicDisc() {
            super(1, AudioSoundRegistryHandler.END_OF_THE_ROAD::getSoundEvent, PROP);
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new TranslationTextComponent("tooltip.audio.endOfTheRoadMusicDisc.line1"));
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    public static class NocturneMusicDisc extends MusicDiscItem {

        public NocturneMusicDisc() {
            super(1, AudioSoundRegistryHandler.NOCTURNE::getSoundEvent, PROP);
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(Utils.TipHelper.getTip("tooltip.audio.nocturneMusicDisc.line1"));
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    public static class SilhouetteMusicDisc extends MusicDiscItem {

        public SilhouetteMusicDisc() {
            super(1, AudioSoundRegistryHandler.SILHOUETTE::getSoundEvent , PROP);
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(Utils.TipHelper.getTip("tooltip.audio.silhouette.line1"));
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    public static class SnowMusicDisc extends MusicDiscItem {

        public SnowMusicDisc() {
            super(1 , AudioSoundRegistryHandler.SNOW::getSoundEvent , PROP);
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(Utils.TipHelper.getTip("tooltip.audio.snowMusicDisc.line1"));
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
}
