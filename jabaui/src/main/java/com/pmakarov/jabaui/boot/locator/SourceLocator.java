package com.pmakarov.jabaui.boot.locator;

import java.util.Set;

/**
 * Source(bytecode, binary, jar, <code>*.class</code>) locator for application
 * Main target of that object is to return set of {@link Class} instances
 */
public interface SourceLocator {

    /**
     * Java binary files extension
     */
    String JAVA_BINARY_EXTENSION = ".class";

    /**
     * Locate sources by directory and return set of classes
     *
     * @param directory passed directory
     * @return class set
     */
    Set<Class<?>> locate(String directory);
}
