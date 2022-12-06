package com.github.audio.api.exception;

/**
 * @Description: This exception caused usually because there's singleton design mode and some unknown
 * places broke this, if this exception occurs you can follow the stackTrace to seek for the singleton with
 * problem and search for the places where the problem might be caused.
 */
public class MultipleSingletonException extends RuntimeException{

    private static final String MSG = "Fail with trying creating more than one singleton, contradiction with existing singleton : ";

    public final Object existSingleton;

    public MultipleSingletonException(Object existSingleton) {
        super(MSG + existSingleton);
        this.existSingleton = existSingleton;
    }
}
