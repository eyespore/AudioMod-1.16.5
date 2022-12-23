package com.github.audio.api;

import com.github.audio.api.Interface.IEchoConsumer;
import com.github.audio.master.Executor;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Description: The looper of audio mod, implement with interface {@link IEchoConsumer}, this class should be the
 * super class of all looper in the mod, while calling a looper to work it should probably be placed into a listener
 * which is usually listen for the tick event such as {@link net.minecraftforge.event.TickEvent.ClientTickEvent} or
 * {@link TickEvent} and etc, if the looper is under some specific condition then its subclass should override the
 * method *condition* to correct judge the environment and choose the right tick to set up.
 * @param <T> The parameter for being operated in the loop, this is a generic so if there's no parameter introduced
 *           into this place then it should be defined as *Void*.
 * @author clclFL
 */
public abstract class EchoConsumer<T> extends Executor implements IEchoConsumer<T> {

    public Supplier<T> src;
    private long ticker;
    private long delay;
    private boolean isCanceled = false;

    public EchoConsumer(@Nullable Supplier<T> src , long delay) {
        this.delay = delay;
        this.src = src;
    }

    @Override
    public final void loop(TickEvent event) {
        if (isCanceled) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (src == null || src.get() == null) return;
        ticker ++;
        if (ticker > delay) {
            process().accept(src.get());
            ticker = 0;
        }
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @Override
    public abstract Consumer<T> process();

    public final void setDelay(long delay) {
        this.delay = delay;
    }

    public void setSrc(Supplier<T> src) {
        this.src = src;
    }

    public final void reset() {
        this.ticker = 0L;
    }
}
