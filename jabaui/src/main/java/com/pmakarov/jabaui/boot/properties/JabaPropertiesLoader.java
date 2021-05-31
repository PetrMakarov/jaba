package com.pmakarov.jabaui.boot.properties;

import java.util.Properties;

/**
 * Loader for properties
 */
public interface JabaPropertiesLoader {

    /**
     * Loader override priority
     *
     * @return priority (increase from 0 to Long.MAX_VALUE)
     */
    Long overridePriority();

    /**
     * Load values to passed {@link Properties} object
     *
     * @param properties object
     */
    void load(Properties properties);
}
