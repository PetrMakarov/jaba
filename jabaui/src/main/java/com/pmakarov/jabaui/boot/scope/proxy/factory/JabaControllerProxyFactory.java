package com.pmakarov.jabaui.boot.scope.proxy.factory;

import com.pmakarov.jabahelper.JabaAnnotations;
import com.pmakarov.jabaui.boot.metadata.JabaController;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaControllerProxyComposer;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaProxyComposer;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * Factory for create proxies for controllers entities (annotated with {@link JabaController})
 */
public class JabaControllerProxyFactory extends JabaProxyFactory {

    @Override
    Predicate<? super Class<?>> iterationFilter() {
        return l -> JabaAnnotations.isAnnotationPresent(l, JabaController.class)
                && !Annotation.class.isAssignableFrom(l);
    }

    @Override
    JabaProxyComposer proxyComposer() {
        return new JabaControllerProxyComposer();
    }
}
