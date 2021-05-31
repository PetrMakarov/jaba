package com.pmakarov.jabaui.boot.scope.instantiate;

import com.pmakarov.jabaui.boot.JabaBootException;
import com.pmakarov.jabaui.boot.scope.JabaApplicationScope;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Factory for access @{link JabaCDInstantiator instantiators}
 */
public class JabaCDInstantiateFactory {

    /**
     * Available instantiators
     */
    private final static Set<JabaCDInstantiator> instantiators = new LinkedHashSet<JabaCDInstantiator>() {{
        add(new JabaModelXMLInstantiator());
        add(new JabaControllerInstantiator());
    }};

    /**
     * Create (get) instantiator for current class
     *
     * @param locatedClass passed class
     * @param scope        application scope
     * @return class instance
     */
    public static Object create(Class<?> locatedClass, JabaApplicationScope scope) {
        return instantiators.stream()
                .filter(i -> i.isSuitableFor(locatedClass))
                .findFirst()
                .orElseThrow(() -> new JabaBootException("Cannot find suitable instantiator for bean type " + locatedClass.getName()))
                .createInstances(locatedClass, scope);
    }
}
