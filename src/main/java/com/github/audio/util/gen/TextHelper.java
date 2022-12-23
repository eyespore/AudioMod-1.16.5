package com.github.audio.util.gen;

import com.github.audio.api.EchoConsumer;
import com.github.audio.util.IAudioTool;
import net.minecraft.util.text.*;
import net.minecraftforge.event.TickEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextHelper implements IAudioTool {


    public static class Scroller implements IAudioTool {

        private String rec;
        private String str;
        private String cur;
        private final EchoConsumer<String> out;
        private final EchoConsumer<String> in;

        private Scroller(Supplier<String> sup , long delay , int length) {
            cur = sup.get();
            rec = sup.get();

            this.out = new EchoConsumer<String>(sup , 1) {
                @Override
                public Consumer<String> process() {
                    return s -> {
                        if (!rec.equals(s)) {
                            rec = s;
                            if (length > rec.length()) cur = rec;
                            else cur = rec + "   ";
                        }
                        str = cur.substring(0 , length);
                    };
                }
            };

            this.in = new EchoConsumer<String>(() -> this.cur , delay) {
                @Override
                public Consumer<String> process() {
                    return s -> cur = length <= rec.length() ?  cur.substring(1) + cur.split("")[0] : rec;
                }
            };
        }

        public Scroller loop(TickEvent.ClientTickEvent event) {
            out.loop(event);
            in.loop(event);
            return this;
        }

        public void reset() {
            out.reset();
            in.reset();
        }

        public String toStr() {
            return str;
        }

        public static Scroller newInstance(Supplier<String> sup , long delay , int length) {
            return new Scroller(sup , delay , length);
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
