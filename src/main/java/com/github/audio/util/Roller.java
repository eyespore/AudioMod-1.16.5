package com.github.audio.util;

import net.minecraftforge.event.TickEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public class Roller {
    private int len;
    private Supplier<String> srs;
    private long ticker;
    private long interval;
    private String current;
    private String recordCur;

    private static final int DEF_LENGTH = 15;
    private static final long DEF_INTERVAL = 40;

    public Roller(Supplier<String> srs) {
        this(srs, DEF_LENGTH, DEF_INTERVAL);
    }

    public Roller(Supplier<String> srs, int len) {
        this(srs, len, DEF_INTERVAL);
    }

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
            if (s.length() <= DEF_LENGTH) return s;
            String cur = s;
            cur = cur.substring(1) + cur.split("")[0];
            return cur;
        });
    }

}
