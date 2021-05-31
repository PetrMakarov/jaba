package com.pmakarov.jabaui.boot.locator;

import com.pmakarov.jabaui.boot.JabaBootException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Locator for sources in directory
 */
public class DirectorySourceLocator implements SourceLocator {

    @Override
    public Set<Class<?>> locate(String directory) {

        Path directoryPath = Paths.get(directory);

        if (!directoryPath.toFile().isDirectory()) {
            throw new JabaBootException(String.format("Invalid directory '%s'.", directory));
        }
        try {
            return Files.walk(directoryPath)
                    .filter(p -> p.toString().endsWith(JAVA_BINARY_EXTENSION))
                    .map(path -> {
                        String classFullName = directoryPath.relativize(path)
                                .toString()
                                .replace("\\", ".")
                                .replace(JAVA_BINARY_EXTENSION, "");
                        try {
                            return Class.forName(classFullName, true, Thread.currentThread().getContextClassLoader());
                        } catch (ClassNotFoundException e) {
                            throw new JabaBootException(e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new JabaBootException(e.getMessage(), e);
        }
    }

}
