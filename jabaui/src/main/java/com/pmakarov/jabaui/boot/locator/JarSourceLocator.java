package com.pmakarov.jabaui.boot.locator;

import com.pmakarov.jabaui.boot.JabaBootException;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Locator for sources in jar file
 */
public class JarSourceLocator implements SourceLocator {

    @Override
    public Set<Class<?>> locate(String directory) {
        final Set<Class<?>> locatedClasses = new HashSet<>();
        try {
            JarFile jarFile = new JarFile(new File(directory));
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.getName().endsWith(JAVA_BINARY_EXTENSION)) {
                    continue;
                }
                final String className = jarEntry.getName().replace(JAVA_BINARY_EXTENSION, "")
                        .replaceAll("\\\\", ".")
                        .replaceAll("/", ".");
                locatedClasses.add(Class.forName(className, true, Thread.currentThread().getContextClassLoader()));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new JabaBootException(e.getMessage(), e);
        }
        return locatedClasses;
    }
}
