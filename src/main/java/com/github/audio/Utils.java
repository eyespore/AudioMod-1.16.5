package com.github.audio;

import net.minecraft.util.text.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Utils {

    public static final String MOD_ID = "audio";

    //For font
    /**
     *  Normal item tool tip.
     *  The key's type for translation in the lang.json is "tooltip.clclfl.key"
     */
    public static class TipHelper{

        /**
         * return gray font to describe tool's tip and return DARK_GRAY FONT by default,
         * complete key name required.
         */
        public static ITextComponent getTip(String key) {
            return new TranslationTextComponent(key)
                    .setStyle(Style.EMPTY.applyFormatting(TextFormatting.DARK_GRAY));
        }

        /**
         * allow custom font format and return DARK_GRAY FONT by default,
         * complete key name required.
         */
        public static ITextComponent getTip(String key , TextFormatting... textFormattings){
            IFormattableTextComponent ftc = new TranslationTextComponent(key);
            return ftc.setStyle(Style.EMPTY.mergeWithFormatting(textFormattings)
                    .applyFormatting(TextFormatting.DARK_GRAY));
        }
    }

    /* Rolling Format */
    public static Supplier<RollFontHelper> getRollingBar(String rollingFontBar) {
        return () -> new RollFontHelper(rollingFontBar);
    }

    public static Supplier<RollFontHelper> getRollingBar(String rollingFontBar , int disPlayLength) {
        return () -> new RollFontHelper(rollingFontBar , disPlayLength);
    }

    public static class RollFontHelper {
        private final int displayLength;
        private String rollingFont;

        /* Constructor */
        public RollFontHelper(String rollingFont , int displayLength) {
            this.rollingFont = rollingFont.length() <= displayLength ? rollingFont : rollingFont + "  ";
            this.displayLength = displayLength;
        }

        /* Constructor */
        public RollFontHelper(String rollingFont) {
            displayLength = 15;
            this.rollingFont = rollingFont.length() <= displayLength ? rollingFont : rollingFont + "  ";
        }

        public String nextRollingFormat() {
            if (rollingFont.length() <= displayLength) {
                return rollingFont;
            }
            String toReturn = rollingFont.substring(0, displayLength);
            rollingFont = rollingFont.substring(1) + rollingFont.split("")[0];
            return toReturn;
        }
    }

    public static class CollectionHelper {

        @SafeVarargs
        public static <T> void add(List<T> list, T... t) {
            list.addAll(Arrays.asList(t));
        }
    }
}
