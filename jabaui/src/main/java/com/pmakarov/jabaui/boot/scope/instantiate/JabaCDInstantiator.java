package com.pmakarov.jabaui.boot.scope.instantiate;

import com.pmakarov.jabaui.boot.scope.JabaApplicationScope;
import lombok.AllArgsConstructor;

/**
 * Base entity that represents factory for application instances creation
 */
@AllArgsConstructor
abstract class JabaCDInstantiator {

    /**
     * Abstract filter that help determine if current instantiator is suitable for passed class instance creation
     *
     * @param locatedClass class
     * @return true or false
     */
    abstract boolean isSuitableFor(final Class<?> locatedClass);

    /**
     * Abstract instantiate method that should create instance of current class (actually proxy)
     *
     * @param clazz current class
     * @param proxy current class proxy
     * @return class (proxy) instance
     */
    abstract Object instantiate(final Class<?> clazz, final Class<?> proxy);

    /**
     * Common logic for instance creation
     *
     * @param locatedClass current class
     * @param scope        application scope
     * @return class instance
     */
    public final Object createInstances(final Class<?> locatedClass, final JabaApplicationScope scope) {
        Object obj = instantiate(locatedClass, scope.proxyScope().getProxy(locatedClass));
        scope.put(locatedClass, obj);
        return obj;
    }
}
