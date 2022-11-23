package com.github.audio.api.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Experimental {
    String explanation() default "void";
    String author() default "dev";
}
