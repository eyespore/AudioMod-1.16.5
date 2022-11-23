package com.github.audio.creativetab;

import com.github.audio.item.ItemRegisterHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModCreativeTab {

    public final static TabAudio TAB_AUDIO = new TabAudio();

    public static class TabAudio extends ItemGroup {

        public TabAudio() {
            super("tab.audio.tabAudio");
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemRegisterHandler.Mp3.get());
        }
    }
}
