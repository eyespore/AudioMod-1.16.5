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
    private boolean isCanceled = false;

    public EchoConsumer(@Nullable Supplier<T> src) {
        this.src = src;
    }

    /**
     * @Description: This is the method for those looper that needs the same interval between each loop.
     * @param event the tick event for execute the logic in the method body.
     */
    @Override
    public final void loop(TickEvent event , long delay) {
        if (isNullEnv(event)) return;
        ticker ++;
        if (ticker > delay) {
            loop(event);
            ticker = 0;
        }
    }

    /**
     * @Description: This is the default method for the looper that only need one tick looping.
     * @param event the tick event for execute the logic in the method body.
     */
    public final void loop(TickEvent event) {
        if (isNullEnv(event)) return;
        process().accept(src.get());
    }

    private boolean isNullEnv(TickEvent event) {
        return isCanceled || event.phase != TickEvent.Phase.END || src == null || src.get() == null;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @Override
    public abstract Consumer<T> process();

    public void setSrc(Supplier<T> src) {
        this.src = src;
    }

    public final void reset() {
        this.ticker = 0L;
    }
}
