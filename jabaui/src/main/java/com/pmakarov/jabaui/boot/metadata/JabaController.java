package com.pmakarov.jabaui.boot.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates class as a controller one for the app
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JabaComponent
public @interface JabaController {
}
