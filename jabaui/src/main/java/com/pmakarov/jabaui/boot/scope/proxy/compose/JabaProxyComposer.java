package com.pmakarov.jabaui.boot.scope.proxy.compose;

import com.pmakarov.jabaui.boot.scope.proxy.exception.JabaProxyCreationException;

/**
 * Base entity that represent proxy composer (factory)
 */
public abstract class JabaProxyComposer {

    /**
     * Proxy class name suffix
     */
    public final static String DEFAULT_PROXY_SUFFIX_NAME = "Proxy";

    /**
     * Compose(create) proxy class
     *
     * @param original original class
     * @return proxy class
     * @throws JabaProxyCreationException error while compile proxy
     */
    public abstract Class<?> createProxy(Class<?> original) throws JabaProxyCreationException;
}
