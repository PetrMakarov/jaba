package com.pmakarov.jabaui.boot.properties;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Loader for java system properties
 */
public class SystemPropertiesLoader implements JabaPropertiesLoader {
    @Override
    public Long overridePriority() {
        return 0L;
    }

    @Override
    public void load(Properties properties) {
        for (Enumeration<?> enumeration = System.getProperties().propertyNames(); enumeration.hasMoreElements(); ) {
            String key = (String) enumeration.nextElement();
            properties.setProperty(key, System.getProperty(key));
        }
    }
}
