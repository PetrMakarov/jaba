package com.pmakarov.jabaui.boot;

/**
 * Provide exception wrapper for <code>Jaba application</code> class instance creation
 * to throw {@link JabaBootException} on error
 */
public class RuntimeFactory {
    public static <T> T wrap(Class<T> factoryClass) {
        try {
            return factoryClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JabaBootException("Error create factory instance " + factoryClass.getName(), e);
        }
    }
}
