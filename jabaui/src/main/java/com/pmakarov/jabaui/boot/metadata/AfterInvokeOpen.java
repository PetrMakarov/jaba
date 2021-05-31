package com.pmakarov.jabaui.boot.metadata;

import java.awt.Window;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface AfterInvokeOpen {
    Class<? extends Window> value();
}
