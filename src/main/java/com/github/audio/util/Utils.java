package com.github.audio.util;

import com.github.audio.util.gen.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.function.Supplier;

public class Utils {

    /**
     * @Description: The unique identifier for the mod.
     */
    public static final String MOD_ID = "audio";

    private Utils() {
    }

    public static IOHelper getIOHelper() {
        return IOHelper.getInstance();
    }

    public static TextHelper.Scroller getScroller(Supplier<String> sup, int length) {
        return TextHelper.Scroller.newInstance(sup ,length);
    }

    public static TextHelper.TipHelper getTipHelper() {
        return TextHelper.TipHelper.getInstance();
    }

    public static ServerFileOperator getServerFileOperator() {
        return ServerFileOperator.getServerFileOperator();
    }

    @OnlyIn(Dist.CLIENT)
    public static ClientFileOperator getClientFileOperator() {
        return ClientFileOperator.getClientFileOperator();
    }

    @OnlyIn(Dist.CLIENT)
    public static JsonBuilder getJsonBuilder() {
        return JsonBuilder.getJsonBuilder();
    }

    public static Supplier<String> getUniqueSignal() {
        return () -> System.currentTimeMillis() + "-" + UUID.randomUUID();
    }

}
