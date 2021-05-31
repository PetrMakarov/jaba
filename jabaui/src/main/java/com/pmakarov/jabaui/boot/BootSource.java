package com.pmakarov.jabaui.boot;

import lombok.Getter;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Pojo that determines source path and type of boot class
 */
@Getter
public class BootSource {

    /**
     * Boot class
     */
    private Class<?> bootClass;

    /**
     * Boot source path
     */
    private String source;

    /**
     * Boot class type
     */
    private Type type;

    /**
     * Source type
     */
    public enum Type {
        DIRECTORY, JAR;
    }

    public BootSource(Class<?> bootClass) {
        try {
            this.bootClass = bootClass;
            source = Paths.get(bootClass.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
            type = !new File(source).isDirectory() && source.endsWith(".jar") ? Type.JAR : Type.DIRECTORY;
        } catch (URISyntaxException e) {
            throw new JabaBootException("Failed to parse boot source", e);
        }
    }
}
