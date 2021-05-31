package com.pmakarov.jabaui.boot.scope.proxy.factory;

import com.pmakarov.jabahelper.JabaAnnotations;
import com.pmakarov.jabaui.boot.metadata.JabaModel;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaModelProxyComposer;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaProxyComposer;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * Factory for create proxies for model entities (annotated with {@link JabaModel})
 */
public class JabaModelProxyFactory extends JabaProxyFactory {

    @Override
    Predicate<? super Class<?>> iterationFilter() {
        return l -> JabaAnnotations.isAnnotationPresent(l, JabaModel.class)
                && !Annotation.class.isAssignableFrom(l);
    }

    @Override
    JabaProxyComposer proxyComposer() {
        return new JabaModelProxyComposer();
    }
}
