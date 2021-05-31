package com.pmakarov.jabaui.boot.properties;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class stores system, env and application properties
 * Client may access property by {$property.name} pattern
 */
public class JabaProperties {

    private static final Logger LOGGER = Logger.getLogger(JabaProperties.class);

    private static final Properties PROPERTIES = new Properties();
    private static final Pattern PARAMETR_PATTERN = Pattern.compile("\\$\\{([^}]*)\\}");

    private static final List<JabaPropertiesLoader> LOADERS = new ArrayList<JabaPropertiesLoader>() {{
        add(new ApplicationPropertiesLoader());
        add(new SystemEnvironmentLoader());
        add(new SystemPropertiesLoader());
    }};

    static {
        LOADERS.stream()
                .sorted(Comparator.comparing(JabaPropertiesLoader::overridePriority))
                .forEach(loader -> loader.load(PROPERTIES));
    }

    public static String get(final String propertyName) {
        return PROPERTIES.getProperty(propertyName);
    }

    /**
     * Get property value
     *
     * @param stringWithProperties string contains property name substitution
     * @return string with property value
     */
    public static String injectTo(final String stringWithProperties) {
        String propertyValue = stringWithProperties;
        Matcher parametersMatcher = PARAMETR_PATTERN.matcher(stringWithProperties);
        while (parametersMatcher.find()) {
            final String parameterPropertyName = parametersMatcher.group(1);
            if (!parameterPropertyName.isEmpty()) {
                String parameterPropertyValue = PROPERTIES.containsKey(parameterPropertyName) ? PROPERTIES.getProperty(parameterPropertyName) : "";
                propertyValue = propertyValue.replace(parametersMatcher.group(), parameterPropertyValue);
            }
        }
        return propertyValue;
    }
}
