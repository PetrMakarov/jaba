package com.pmakarov.jabaui.boot;

import com.pmakarov.jabaui.boot.scope.*;
import com.pmakarov.jabaui.boot.scope.configuration.JabaConfigurationOrganizer;
import com.pmakarov.jabaui.boot.scope.exception.JabaDependencyInjectException;
import com.pmakarov.jabaui.boot.scope.proxy.factory.JabaControllerProxyFactory;
import com.pmakarov.jabaui.boot.scope.proxy.factory.JabaModelProxyFactory;

import java.util.Set;

/**
 * Provides public api for getting proxy and beans of application
 */
public class CDIService {

    /**
     * Application scope (container)
     */
    private static JabaApplicationScope applicationScope;

    /**
     * Application located classes
     */
    private Set<Class<?>> locatedClasses;

    /**
     * Package private constructor: we don't need to provide public creation methods
     * because its contains non static package private dependency injection method
     *
     * @param locatedClasses set of located classes
     */
    CDIService(Set<Class<?>> locatedClasses) {
        this.locatedClasses = locatedClasses;
        applicationScope = new JabaApplicationScope();
    }

    /**
     * Dependency process describing and performing
     *
     * @throws JabaDependencyInjectException error while configure application
     */
    void initAndInjectDependencies() throws JabaDependencyInjectException {
        new JabaConfigurationOrganizer(locatedClasses, applicationScope)
                .makeProxies()
                .withFactory(JabaModelProxyFactory.class)
                .withFactory(JabaControllerProxyFactory.class)
                .thenInitAndInject()
                .perform();
    }

    /**
     * Get bean instance by class
     *
     * @param beanType class
     * @return bean
     */
    public static Object getBean(Class<?> beanType) {
        return null == applicationScope ? null : applicationScope.get(beanType);
    }

    /**
     * Get proxy class by class name
     *
     * @param className class name
     * @return proxy class
     */
    public static Class<?> getProxy(String className) {
        return null == applicationScope ? null : applicationScope.proxyScope().getProxy(className);
    }
}
