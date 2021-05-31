package com.pmakarov.jabaui.boot.scope.proxy.factory;

import com.pmakarov.jabaui.boot.JabaBootException;
import com.pmakarov.jabaui.boot.scope.JabaProxyScope;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaProxyComposer;
import com.pmakarov.jabaui.boot.scope.proxy.exception.JabaProxyCreationException;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Base entity factory for create proxy classes and put it in current scope
 */
public abstract class JabaProxyFactory {

    /**
     * Creates proxies instances
     *
     * @param locatedClasses boot located classes
     * @param proxyScope     current proxy scope
     */
    public final void createProxies(Set<Class<?>> locatedClasses, JabaProxyScope proxyScope) {
        locatedClasses.stream()
                .filter(iterationFilter())
                .forEach(clazz -> {
                    try {
                        Class<?> proxy = proxyComposer().createProxy(clazz);
                        proxyScope.addProxy(clazz, proxy);
                    } catch (JabaProxyCreationException e) {
                        throw new JabaBootException("Error process controller " + clazz.getName(), e);
                    }
                });
    }

    /**
     * Abstract filter getter to iterate over located classes
     *
     * @return filter
     */
    abstract Predicate<? super Class<?>> iterationFilter();

    /**
     * Abstract {@link JabaProxyComposer proxy composer} getter to create proxy class
     *
     * @return {@link JabaProxyComposer proxy composer} instance
     */
    abstract JabaProxyComposer proxyComposer();
}
