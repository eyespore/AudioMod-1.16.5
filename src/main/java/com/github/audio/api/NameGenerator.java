package com.github.audio.api;

import java.util.function.Supplier;

/**
 * @author clclFL
 * @Description: Name generator is for those which needs a serializable name with id number behind it, while using the
 * method {@link NameGenerator#get()} the instance's field {@link NameGenerator#id} will plus itself with one, finally
 * make into the name such as "_num", while any name generators are required to be used more than once then the method
 * {@link NameGenerator#reset()} can be called to reset the id of each name generator.
 */
public class NameGenerator implements Supplier<String> {

    private int id = 0;
    private final String pre;

    public NameGenerator(String pre) {
        this.pre = pre;
    }

    public NameGenerator() {
        this("_");
    }

    @Override
    public String get() {
        return pre + (id++);
    }

    public void reset() {
        this.id = 0;
    }
}
