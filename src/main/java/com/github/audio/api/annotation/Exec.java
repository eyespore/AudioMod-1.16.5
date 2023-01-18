package com.github.audio.api.annotation;

import net.minecraftforge.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Exec {

    Dist value() default Dist.DEDICATED_SERVER;

    Class<?> _interface() default Object.class;

}
