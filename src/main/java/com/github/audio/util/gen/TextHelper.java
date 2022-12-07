package com.github.audio.util.gen;

import com.github.audio.util.IAudioTool;
import net.minecraft.util.text.*;
import net.minecraftforge.event.TickEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public class TextHelper implements IAudioTool {

    public static class Roller implements IAudioTool{
        private int len;
        private Supplier<String> srs;
        private long interval;
        private long ticker;
        private String current;
        private String recordCur;

        public Roller(Supplier<String> srs, int len, long interval) {
            this.srs = srs;
            this.len = len;
            this.interval = interval;
            setCurrent(srs.get());
        }

        public String loop(final TickEvent.ClientTickEvent event) {
            ticker++;
            if (!recordCur.equals(srs.get())) {
                setCurrent(srs.get());
                ticker = 0;
            }

            if (ticker > interval) {
                ticker = 0;
                current = process().apply(current);
            }

            return current.substring(0, Math.min(len, current.length()));
        }

        public void setLen(int len) {
            this.len = len;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public void setSrs(Supplier<String> srs) {
            this.srs = srs;
            setCurrent(srs.get());
        }

        private void setCurrent(String str) {
            current = str.length() < len ? str : str + "   ";
            recordCur = str;
        }

        public Function<String, String> process() {
            return (s -> {
                if (s.length() <= 40) return s;
                String cur = s;
                cur = cur.substring(1) + cur.split("")[0];
                return cur;
            });
        }
    }

    public static class RollerBuilder implements IAudioTool {

        private static final int DEF_LENGTH = 15;
        private static final long DEF_INTERVAL = 40;

        private int len = -1;
        private Supplier<String> src = null;
        private long interval = -1;

        private RollerBuilder() {}

        public static RollerBuilder getInstance() {
            return new RollerBuilder();
        }

        public RollerBuilder len(int len) {
            this.len = len;
            return this;
        }

        public RollerBuilder src (Supplier<String> src) {
            this.src = src;
            return this;
        }

        public RollerBuilder interval(long interval) {
            this.interval = interval;
            return this;
        }

        public Roller build() {
            this.len = (len == -1 ? DEF_LENGTH : len);
            this.src = (src == null ? () -> "null information" : src);
            this.interval = (interval == -1L ? DEF_INTERVAL : interval);
            return new Roller(src , len , interval);
        }
    }


    public static class TipHelper implements IAudioTool{

        private static final TipHelper TIP_HELPER = new TipHelper();

        public static TipHelper getInstance() {
            return TIP_HELPER;
        }

        public ITextComponent getTip(String key) {
            return new TranslationTextComponent(key)
                    .setStyle(Style.EMPTY.applyFormatting(TextFormatting.DARK_GRAY));
        }

        public ITextComponent getTip(String key, TextFormatting... textFormattings) {
            IFormattableTextComponent ftc = new TranslationTextComponent(key);
            return ftc.setStyle(Style.EMPTY.mergeWithFormatting(textFormattings)
                    .applyFormatting(TextFormatting.DARK_GRAY));
        }
    }
}
