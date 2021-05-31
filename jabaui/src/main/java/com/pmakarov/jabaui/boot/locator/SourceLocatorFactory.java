package com.pmakarov.jabaui.boot.locator;

import com.pmakarov.jabaui.boot.BootSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for create(get) matching {@link SourceLocator}
 */
public class SourceLocatorFactory {

    /**
     * Locator for sources in directory
     */
    private static final DirectorySourceLocator DIRECTORY_LOCATOR = new DirectorySourceLocator();

    /**
     * Locator for sources in jar file
     */
    private static final JarSourceLocator JAR_SOURCE_LOCATOR = new JarSourceLocator();

    /**
     * Contains dependency of {@link BootSource#getType()} and source locator
     */
    private static final Map<BootSource.Type, SourceLocator> SOURCE_LOCATOR_TYPE_MAP =
            new HashMap<BootSource.Type, SourceLocator>() {{
                put(BootSource.Type.DIRECTORY, DIRECTORY_LOCATOR);
                put(BootSource.Type.JAR, JAR_SOURCE_LOCATOR);
            }};

    /**
     * Factory method for create(get) source locator by type
     *
     * @param type source type
     * @return source locator
     */
    public static SourceLocator create(BootSource.Type type) {
        return SOURCE_LOCATOR_TYPE_MAP.get(type);
    }

}
