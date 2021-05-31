package com.pmakarov.jabaui.boot.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents proxy scope (container) of application
 */
public class JabaProxyScope {

    /**
     * Contains links of original and proxy classes
     */
    private Map<Class<?>, Class<?>> PROXIES = new ConcurrentHashMap<>();

    /**
     * Containes links of original class names and proxy classes
     */
    private Map<String, Class<?>> PROXIES_NAMES = new ConcurrentHashMap<>();

    /**
     * Put original->proxy link to scope
     *
     * @param original original class
     * @param proxy    proxy class
     */
    public void addProxy(Class<?> original, Class<?> proxy) {
        PROXIES.putIfAbsent(original, proxy);
        PROXIES_NAMES.putIfAbsent(original.getName(), proxy);
        PROXIES_NAMES.putIfAbsent(original.getSimpleName(), proxy);
    }

    /**
     * Get proxy class by original class
     *
     * @param original original class
     * @return proxy class
     */
    public Class<?> getProxy(Class<?> original) {
        if (PROXIES.containsKey(original)) {
            return PROXIES.get(original);
        }
        return original;
    }

    /**
     * Get proxy class by original class name
     *
     * @param originalName original class name
     * @return proxy class
     */
    public Class<?> getProxy(String originalName) {
        if (PROXIES_NAMES.containsKey(originalName)) {
            return PROXIES_NAMES.get(originalName);
        }
        return null;
    }
}
