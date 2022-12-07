package com.github.audio.util;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static final String MOD_ID = "audio";

    public static class CollectionHelper {
        @SafeVarargs
        public static <T> void add(List<T> list, T... t) {
            list.addAll(Arrays.asList(t));
        }
    }


}
