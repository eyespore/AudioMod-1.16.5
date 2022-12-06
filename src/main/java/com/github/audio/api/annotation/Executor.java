package com.github.audio.api.annotation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@OnlyIn(Dist.CLIENT)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Executor {
}
