package com.github.audio.api.exception;

public class ExperimentalException extends Exception{

    public ExperimentalException() {
    }

    public ExperimentalException(String message) {
        super("has experimental exception in it");
    }
}
