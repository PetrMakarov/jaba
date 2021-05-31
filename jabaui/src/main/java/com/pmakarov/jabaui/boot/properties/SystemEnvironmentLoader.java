package com.pmakarov.jabaui.boot.properties;

import java.util.Map;
import java.util.Properties;

/**
 * Loader for java environment properties
 */
public class SystemEnvironmentLoader implements JabaPropertiesLoader {
    @Override
    public Long overridePriority() {
        return 1L;
    }

    @Override
    public void load(Properties properties) {
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
