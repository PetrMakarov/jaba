package com.pmakarov.jabaui.dropdown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * How to create new name in drop list
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DropListNaming {

    /*
    Field that will be used for name input
     */
    String field();

    /*
    Initial value
     */
    String initValue() default "Unnamed";
}
