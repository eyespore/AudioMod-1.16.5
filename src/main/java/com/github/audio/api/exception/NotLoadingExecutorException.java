package com.github.audio.api.exception;

/**
 * @Description: This exception is probably caused by unsuccessfully invoking the method "getExecutor" in the
 * executor class, besides, while this exception occur the reason could also be the wrong usage of annotation
 * of {@link com.github.audio.api.annotation.Executor}, this annotation is present only in the executor class
 * while is mainly used for non-static listener registry.
 */
public class NotLoadingExecutorException extends RuntimeException {

    private static final String MSG_1 = "Fail in loading executor ";
    private static final String MSG_2 = " .Check for the annotation or if the method *getExecutor* is written correctly.";
    public final Class<?> exec;

    public NotLoadingExecutorException(Class<?> exec) {
        super(MSG_1 + exec + MSG_2);
        this.exec = exec;
    }
}
