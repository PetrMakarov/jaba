package com.pmakarov.jabaui.boot.scope;

import com.pmakarov.jabahelper.PlainJabaHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents application scope (ioc container)
 */
public class JabaApplicationScope {

    /**
     * Contains located dependencies in application (e.g interfaceOrClass -> {impl1, impl2})
     */
    private final Map<Class<?>, List<Class<?>>> DEPENDENCIES = new ConcurrentHashMap<>();

    /**
     * Contains current context of application (e.g interfaceOrClass -> instance)
     */
    private final Map<Class<?>, Object> CONTEXT = new ConcurrentHashMap<>();

    /**
     * Contains proxy scope (container)
     */
    private final JabaProxyScope PROXY_SCOPE = new JabaProxyScope();

    /**
     * Add dependencies for current class
     *
     * @param implClass passed class
     */
    private void addDependency(Class<?> implClass) {
        if (implClass.getInterfaces().length > 0) {
            for (Class<?> interFaceClass : implClass.getInterfaces()) {
                PlainJabaHelper.addToListInMap(DEPENDENCIES, interFaceClass, implClass);
            }
        } else {
            PlainJabaHelper.addToListInMap(DEPENDENCIES, implClass, implClass);
        }
    }

    /**
     * Create context record
     *
     * @param implClass passed class
     * @param instance  passed instance
     */
    private void addContext(Class<?> implClass, Object instance) {
        CONTEXT.putIfAbsent(implClass, instance);
    }

    /**
     * Add dependencies for class and create context with instance
     *
     * @param implClass passed class
     * @param instance  passed instance
     */
    public void put(Class<?> implClass, Object instance) {
        addDependency(implClass);
        addContext(implClass, instance);
    }

    /**
     * Get instance from application scope (container) by class
     *
     * @param interfaceClass passed class
     * @return context instance
     */
    public Object get(Class<?> interfaceClass) {
        List<Class<?>> implList = DEPENDENCIES.get(interfaceClass);
        if (null != implList && implList.size() > 0) {
            Class<?> implClass = implList.get(0);
            if (CONTEXT.containsKey(implClass)) {
                return CONTEXT.get(implClass);
            }
        }
        return null;
    }

    /**
     * Access proxy scope of application
     *
     * @return proxy scope
     */
    public JabaProxyScope proxyScope() {
        return PROXY_SCOPE;
    }
}
