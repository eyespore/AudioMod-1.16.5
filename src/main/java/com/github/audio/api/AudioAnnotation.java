package com.github.audio.api;

import java.lang.annotation.*;

public class AudioAnnotation {

    /**
     * The type, field or method with this annotation should be used in client side only.
     */
    @Documented
    @Target({ElementType.TYPE , ElementType.FIELD , ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ClientOnly {

    }

}
