package com.pmakarov.jabaui.boot.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a (pojo) class that it has xml datasource and provide path for this xml
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JabaModel
public @interface JabaModelXml {
    String resource() default "";

    String validate() default "";

    Class<?> jaxbFactory() default void.class;
}
