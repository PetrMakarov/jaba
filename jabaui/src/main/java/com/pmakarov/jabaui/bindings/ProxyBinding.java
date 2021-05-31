package com.pmakarov.jabaui.bindings;

/**
 * @author pmakarov
 * Proxy class of object to make 2-way binding possible
 */
public interface ProxyBinding<T> {

    /**
     * Just simple getter
     *
     * @return instance of ValuePropagator
     */
    ValuePropagator getValuePropagator();

    /**
     * Get original object that was proxied
     *
     * @return original object
     */
    T getOriginal();
}
