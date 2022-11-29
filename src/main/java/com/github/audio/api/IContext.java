package com.github.audio.api;

import com.github.audio.api.annotation.Experimental;

/**
 * @author clclFL
 * The super class of all audio context class, audio context is a kind of class that include
 * various kinds of parameters which are for called in the other class to use.
 */
@Experimental
public abstract class IContext {

    /**
     * The class which extended this abstract class can override this method, this method should
     * be defined as an initialization method that for initializing *EVERY* parameter in its instance.
     */
    public void init(){}

    /**
     * This method is used for initializing all parameters in class's instance, implemented by introducing
     * the parameter into method and this method should have the logic to reload *EVERY* parameter from the
     * parameters which the caller instance original have to the new parameter that introduced.
     * @param context the parameter that is used for initialize the caller instance.
     */
    public void reset(IContext context) {}

    /**
     * The sign return is the class extends {@link IContext} class as super class.
     * @return if the sign is the subclass from {@link IContext}
     */
    public final boolean isCtx() {
        return true;
    }

}
