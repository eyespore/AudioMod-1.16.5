package com.github.audio.api.Interface;

import com.github.audio.client.audio.Executor;
import net.minecraftforge.event.TickEvent;

import java.util.function.Function;

/**
 * @author clclFL
 * @Description: This is the main interface implemented by the class Looper, which implements a looper
 * function for the audio mod, if a class implement this interface, this means this class should be
 * defined as a looper too, while I have not any better idea for this, implement a looper might probably
 * better to extend the class {@link Looper} rather than implement this interface, which will be much
 * more complex and hard to get them into a union.
 * @param <T> The type of parameter for being introduced into the function and method, mainly used for operated.
 * @param <R> The type of result that returns back from the function loop, notice that every time the loop goes, this value
 *           will be refreshed and have a new value to return.
 */
public interface ILooper<T , R> {

    /**
     * @Description: The step while the loop was being executed, the frequency with this method is depended
     * on the interval which is defined while a looper is created, it's one of the para in that class.
     * @return The result return from the loop body, usually through a function, and the key whose type is
     * T will then being translated into the value whose type is R.
     */
    Function<T, R> process();

    /**
     * @Description: The condition for judging when this loop should occur or not, this method will be called
     * in the loop body as the second step, after the step that judge if the environment is null, the environment
     * will be a null if the world and the player have not been initialized yet, and then this will be the next
     * judging step of it.
     * @return Return the condition that if this loop can go on.
     */
    Executor.Judge getCondition();

    /**
     * @Description: Method to execute the loop, while this method should not be rewritten if the looper super class
     * has its own implement for this method, subclass of this sup class can merely rewrite the method loop to build
     * the body for the loop and this method will help sub class to execute the method.
     * @param event The tick event for executing the loop, usually the tick event such as
     * {@link net.minecraftforge.event.TickEvent.ClientTickEvent} in the client side or {@link TickEvent} in the
     * server side.
     * @return This method return the condition for judging if the loop could go on, the sup class should usually have a
     * default return value for this which will simply return a *TRUE*, if a sub class want some other condition of it
     * it will have to rewrite this method.
     */
    R loop(TickEvent.ClientTickEvent event);
}
