package com.pmakarov.jabaui.bindings;

/**
 * @author pmakarov
 * Represents getter for some component
 */
public interface ComponentValueGetter {
    /**
     * Invoker for getting result
     *
     * @return component value
     */
    Object call();
}
