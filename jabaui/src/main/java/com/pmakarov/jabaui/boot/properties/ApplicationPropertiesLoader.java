package com.pmakarov.jabaui.boot.properties;

import com.pmakarov.jabaresource.JabaResource;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Loader for application (custom, user-defined) properties
 */
public class ApplicationPropertiesLoader implements JabaPropertiesLoader {

    private static final Logger LOGGER = Logger.getLogger(ApplicationPropertiesLoader.class);

    private static final String PROPERTIES_DEFAULT_FILE_NAME = "application.properties";

    @Override
    public Long overridePriority() {
        return 2L;
    }

    @Override
    public void load(Properties properties) {
        try {
            properties.load(JabaResource.getReader().stream(PROPERTIES_DEFAULT_FILE_NAME));
        } catch (IOException e) {
            LOGGER.info("No properties except system ones are provided in the app");
        }
    }
}
