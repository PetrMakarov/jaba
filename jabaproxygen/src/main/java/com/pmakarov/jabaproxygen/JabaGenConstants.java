package com.pmakarov.jabaproxygen;

import java.nio.file.Paths;

/**
 * @author pmakarov
 */
public class JabaGenConstants {
    private static final String USER_DIR = System.getProperty("user.dir");
    public static final String SOURCE_DIR = Paths.get(USER_DIR, "src", "main", "java").toString();
    public static final String GENERATED_DIR = Paths.get(USER_DIR, "target", "generated-sources", "jabaproxy").toString();
}
