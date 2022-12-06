package com.github.audio.api.Interface;

import com.github.audio.client.audio.Executor;
import net.minecraftforge.event.TickEvent;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description: The looper of audio mod, implement with interface {@link ILooper}, this class should be the
 * super class of all looper in the mod, while calling a looper to work it should probably be placed into a listener
 * which is usually listen for the tick event such as {@link net.minecraftforge.event.TickEvent.ClientTickEvent} or
 * {@link TickEvent} and etc, if the looper is under some specific condition then its subclass should override the
 * method *condition* to correct judge the environment and choose the right tick to set up.
 * @param <T> The parameter for being operated in the loop, this is a generic so if there's no parameter introduced
 *           into this place then it should be defined as *Void*.
 * @param <R> The result that gonna to be return from the loop, this value will be refreshed every time the looper
 *           go for a time, as the same as the first parameter, this parameter can also be Void if there's no parameter
 *           for this.
 * @author clclFL
 */
public abstract class Looper<T , R> extends Executor implements ILooper<T , R>{

    public Supplier<T> src;
    private long ticker;
    private long interval;
    private R toReturn;

    public Looper(Supplier<T> src , long interval) {
        this.interval = interval;
        this.src = src;
        toReturn = process().apply(src.get());
    }

    @Override
    public final R loop(TickEvent.ClientTickEvent event) {
        if (isNullEnv()) return null;
        if (!getCondition().judge()) return null;
        ticker ++;
        if (ticker > interval) {
            toReturn = process().apply(src.get());
            ticker = 0;
        }
        return toReturn;
    }

    @Override
    public abstract Function<T, R> process();

    @Override
    public Judge getCondition() {
        return () -> true;
    }

    public final void setInterval(long interval) {
        this.interval = interval;
    }

    public void setSrc(Supplier<T> src) {
        this.src = src;
    }

    public final void resetTicker() {
        this.ticker = 0L;
    }
}
