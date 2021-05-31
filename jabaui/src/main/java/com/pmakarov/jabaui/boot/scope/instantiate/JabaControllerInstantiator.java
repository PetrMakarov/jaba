package com.pmakarov.jabaui.boot.scope.instantiate;

import com.pmakarov.jabaui.boot.JabaBootException;
import com.pmakarov.jabaui.boot.metadata.JabaController;

import java.lang.reflect.InvocationTargetException;

/**
 * Class represents factory for application controllers instances creation
 */
public class JabaControllerInstantiator extends JabaCDInstantiator {

    @Override
    boolean isSuitableFor(Class<?> locatedClass) {
        return locatedClass.isAnnotationPresent(JabaController.class);
    }

    @Override
    Object instantiate(Class<?> controllerClass, Class<?> proxy) {
        try {
            return proxy.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new JabaBootException("Error create controller " + controllerClass.getName(), e);
        }
    }
}
