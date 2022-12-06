package com.github.audio.util;

import net.minecraft.util.text.*;

public class Tipper {

    public static ITextComponent getTip(String key) {
        return new TranslationTextComponent(key)
                .setStyle(Style.EMPTY.applyFormatting(TextFormatting.DARK_GRAY));
    }

    public static ITextComponent getTip(String key, TextFormatting... textFormattings) {
        IFormattableTextComponent ftc = new TranslationTextComponent(key);
        return ftc.setStyle(Style.EMPTY.mergeWithFormatting(textFormattings)
                .applyFormatting(TextFormatting.DARK_GRAY));
    }
}
