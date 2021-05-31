package com.pmakarov.jabaui.boot.scope.configuration;

import com.pmakarov.jabahelper.JabaAnnotations;
import com.pmakarov.jabahelper.PlainJabaHelper;
import com.pmakarov.jabahelper.JabaReflection;
import com.pmakarov.jabaui.boot.JabaBootException;
import com.pmakarov.jabaui.boot.RuntimeFactory;
import com.pmakarov.jabaui.boot.metadata.*;
import com.pmakarov.jabaui.boot.scope.JabaApplicationScope;
import com.pmakarov.jabaui.boot.scope.exception.JabaDependencyInjectException;
import com.pmakarov.jabaui.boot.scope.instantiate.JabaCDInstantiateFactory;
import com.pmakarov.jabaui.boot.scope.proxy.factory.JabaProxyFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that help to describe scope initializing and dependencies injecting process
 */
public class JabaConfigurationOrganizer {

    /**
     * App classes
     */
    private Set<Class<?>> locatedClasses;

    /**
     * Newly created scope
     */
    private JabaApplicationScope jabaApplicationScope;

    /**
     * Ready for inject state
     */
    private ReadyForInject readyForInject;

    /**
     * Make proxies state
     */
    private ProxyMaking proxyMaking;

    public JabaConfigurationOrganizer(Set<Class<?>> locatedClasses, JabaApplicationScope jabaApplicationScope) {
        this.locatedClasses = locatedClasses;
        this.jabaApplicationScope = jabaApplicationScope;
        this.readyForInject = new ReadyForInject();
        this.proxyMaking = new ProxyMaking();
    }

    /**
     * Start app config with proxies making process
     *
     * @return ProxyMaking as current state
     */
    public ProxyMaking makeProxies() {
        return proxyMaking;
    }

    /**
     * State of current CDI process that indicates that classes are ready for proxy creation process
     */
    public class ProxyMaking {

        /**
         * Proxy factories
         */
        private Set<Class<? extends JabaProxyFactory>> proxyFactories;

        private ProxyMaking() {
            this.proxyFactories = new LinkedHashSet<>();
        }

        /**
         * Register proxy factory for later invoking
         *
         * @param proxyFactory class of initializer
         * @return ProxyMaking current state
         */
        public ProxyMaking withFactory(Class<? extends JabaProxyFactory> proxyFactory) {
            proxyFactories.add(proxyFactory);
            return ProxyMaking.this;
        }

        /**
         * Invoke proxy factories creation process and return next configuration state
         *
         * @return ContextAndDependenciesInitializing as current state
         */
        public ReadyForInject thenInitAndInject() {
            for (Class<? extends JabaProxyFactory> factory : proxyFactories) {
                RuntimeFactory.wrap(factory).createProxies(locatedClasses, jabaApplicationScope.proxyScope());
            }
            return readyForInject;
        }
    }

    /**
     * State of current CDI process that indicates that all initialized beans are ready to be injected
     */
    public class ReadyForInject {

        /**
         * Inject all initialized beans
         *
         * @throws JabaDependencyInjectException error while inject dependencies
         */
        public void perform() throws JabaDependencyInjectException {
            try {
                for (Class<?> locatedClass : sortForInjection(locatedClasses)) {
                    inject0(locatedClass);
                }
            } catch (Exception e) {
                throw new JabaDependencyInjectException("Error while inject dependencies", e);
            }
        }

        /**
         * Sort application classes so {@link Primary} annotated controllers will be processed last
         *
         * @param locatedClasses application located classes
         * @return sorted class list
         */
        private List<Class<?>> sortForInjection(Set<Class<?>> locatedClasses) {
            List<Class<?>> sortedList = new ArrayList<>(locatedClasses);
            sortedList.sort((o1, o2) -> Boolean.compare(o1.isAnnotationPresent(Primary.class), o2.isAnnotationPresent(Primary.class)));
            return sortedList;
        }

        /**
         * Determine if class is not processable for inject
         *
         * @param locatedClass current class
         * @return boolean
         */
        private boolean isNotProcessable(final Class<?> locatedClass) {
            return null != jabaApplicationScope.get(locatedClass)
                    || !JabaAnnotations.isAnnotationPresent(locatedClass, JabaComponent.class)
                    || Annotation.class.isAssignableFrom(locatedClass);
        }

        /**
         * Inject function with recursion
         *
         * @param locatedClass current injection process class
         */
        private void inject0(final Class<?> locatedClass) {
            //if object already present in the current scope (or by some other reasons object is cannot be a part of injection process), than do no action
            if (isNotProcessable(locatedClass)) {
                return;
            }
            //check and determine injection strategy for current class and perform inject function
            getInjectionStrategy(locatedClass)
                    .getFunction()
                    .invoke(locatedClass, jabaApplicationScope, this::inject0);
        }

        /**
         * Check class for injection possibility
         *
         * @param locatedClass class
         * @return inject strategy
         */
        private InjectStrategy getInjectionStrategy(Class<?> locatedClass) {
            long cdiConstructorsCount =
                    Arrays.stream(locatedClass.getDeclaredConstructors()).filter(c -> c.isAnnotationPresent(JabaCDI.class)).count();
            long cdiFieldsCount =
                    Arrays.stream(locatedClass.getDeclaredFields()).filter(f -> f.isAnnotationPresent(JabaCDI.class)).count();
            if (cdiConstructorsCount > 1) {
                throw new JabaBootException("There are more than one JabaCDI constructors found for " + locatedClass.getName());
            }
            if (cdiConstructorsCount > 0 && cdiFieldsCount > 0) {
                return InjectStrategy.MIXED;
            }
            if (cdiConstructorsCount > 0) {
                return InjectStrategy.CONSTRUCTOR;
            }
            if (cdiFieldsCount > 0) {
                return InjectStrategy.FIELDS;
            }
            return InjectStrategy.NO_INJECTION;
        }

    }

    /**
     * Constant values of currently existing injection strategies
     */
    private enum InjectStrategy {

        /**
         * Injection via constructor
         */
        CONSTRUCTOR(((locatedClass, jabaApplicationScope, inject0) -> {
            Class<?> currentInstanceClass = jabaApplicationScope.proxyScope().getProxy(locatedClass);
            //find suitable constructor
            Constructor<?> cdiConstructor = Arrays.stream(currentInstanceClass.getDeclaredConstructors())
                    .filter(c -> c.isAnnotationPresent(JabaCDI.class))
                    .findFirst()
                    .orElseThrow(() -> new JabaBootException("No suitable constructor found for cdi process (at least one should be declared with JabaCDI annotation)"));
            //then inject all params before
            Arrays.stream(cdiConstructor.getParameterTypes())
                    .filter(parameterType -> !PlainJabaHelper.isPrimitive(parameterType))
                    .forEach(inject0::invoke);
            //then get params for constructor
            LinkedList<Object> constructorParams =
                    Arrays.stream(cdiConstructor.getParameterTypes())
                            .map(jabaApplicationScope::get)
                            .collect(Collectors.toCollection(LinkedList::new));
            try {
                //then create instance and put in scope
                Object currentInstance = cdiConstructor.newInstance(constructorParams.toArray());
                jabaApplicationScope.put(locatedClass, currentInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new JabaBootException("Error while invoke constructor " + cdiConstructor.getName() + " of class " + locatedClass.getName(), e);
            }
        })),

        /**
         * Injection via fields
         */
        FIELDS((locatedClass, jabaApplicationScope, inject0) -> {
            //try to perform inject to field types
            Arrays.stream(locatedClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(JabaCDI.class))
                    .map(Field::getType)
                    .forEach(inject0::invoke);
            //then create instance of current class and put in scope
            Object currentInstance = JabaCDInstantiateFactory.create(locatedClass, jabaApplicationScope);
            Class<?> currentInstanceClass = currentInstance.getClass();
            // then inject fields
            // Iterate over locatedClass because we set "super" fields
            // If we iterate over currentInstanceClass(proxy class) then it would set to current instance field (not super)
            Arrays.stream(locatedClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(JabaCDI.class))
                    .forEach(field -> {
                        try {
                            // check if value is null, otherwise we do not set value
                            if (null == JabaReflection.getValueByField(currentInstance, field)) {
                                JabaReflection.setFieldValue(currentInstance, field, jabaApplicationScope.get(field.getType()));
                            }
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new JabaBootException("Error while process field " + field.getName() + " of class " + locatedClass.getName());
                        }
                    });

            long buildersCount = Arrays.stream(currentInstanceClass.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Builder.class)).count();
            Method builder = Arrays.stream(currentInstanceClass.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Builder.class)).findFirst().orElse(null);
            if (buildersCount > 1) {
                if (!currentInstanceClass.isAnnotationPresent(JabaController.class)) {
                    throw new JabaBootException("You can use @Builder annotated method only inside @JabaController class, error for " + locatedClass.getName());
                }
                throw new JabaBootException("More than 1 @Builder method found for " + locatedClass.getName());
            }
            if (null != builder) {
                try {
                    int builderParamsCount = builder.getParameterTypes().length;
                    if (builderParamsCount > 0) {
                        throw new JabaBootException("@Builder method should have no parameters. Error on " + locatedClass.getName());
                    }
                    builder.invoke(currentInstance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new JabaBootException("Error while execute @Builder method for " + locatedClass.getName(), e);
                }
            }
        }),

        /**
         * No injection
         */
        NO_INJECTION(((locatedClass, jabaApplicationScope, inject0) -> {
            //than create instance of current class
            JabaCDInstantiateFactory.create(locatedClass, jabaApplicationScope);
        })),

        /**
         * Mixed injection (field + constructor)
         */
        MIXED((locatedClass, jabaApplicationScope, inject0) -> {
            //try to perform inject to field types
            Arrays.stream(locatedClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(JabaCDI.class))
                    .map(Field::getType)
                    .forEach(inject0::invoke);

            Class<?> currentInstanceClass = jabaApplicationScope.proxyScope().getProxy(locatedClass);
            //find suitable constructor
            Constructor<?> cdiConstructor = Arrays.stream(currentInstanceClass.getDeclaredConstructors())
                    .filter(c -> c.isAnnotationPresent(JabaCDI.class))
                    .findFirst()
                    .orElseThrow(() -> new JabaBootException("No suitable constructor found for cdi process (at least one should be declared with JabaCDI annotation)"));
            //then inject all params before
            Arrays.stream(cdiConstructor.getParameterTypes())
                    .filter(parameterType -> !PlainJabaHelper.isPrimitive(parameterType))
                    .forEach(inject0::invoke);
            //then get params for constructor
            LinkedList<Object> constructorParams =
                    Arrays.stream(cdiConstructor.getParameterTypes())
                            .map(jabaApplicationScope::get)
                            .collect(Collectors.toCollection(LinkedList::new));
            try {
                //then create instance
                Object currentInstance = cdiConstructor.newInstance(constructorParams.toArray());

                // then inject fields
                // Iterate over locatedClass because we set "super" fields
                // If we iterate over currentInstanceClass(proxy class) then it would set to current instance field (not super)
                Arrays.stream(locatedClass.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(JabaCDI.class))
                        .forEach(field -> {
                            try {
                                // check if value is null, otherwise we do not set value
                                if (null == JabaReflection.getValueByField(currentInstance, field)) {
                                    JabaReflection.setFieldValue(currentInstance, field, jabaApplicationScope.get(field.getType()));
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                throw new JabaBootException("Error while process field " + field.getName() + " of class " + locatedClass.getName());
                            }
                        });

                //invoke builder method
                long buildersCount = Arrays.stream(currentInstanceClass.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Builder.class)).count();
                Method builder = Arrays.stream(currentInstanceClass.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Builder.class)).findFirst().orElse(null);
                if (buildersCount > 1) {
                    if (!currentInstanceClass.isAnnotationPresent(JabaController.class)) {
                        throw new JabaBootException("You can use @Builder annotated method only inside @JabaController class, error for " + locatedClass.getName());
                    }
                    throw new JabaBootException("More than 1 @Builder method found for " + locatedClass.getName());
                }
                if (null != builder) {
                    try {
                        int builderParamsCount = builder.getParameterTypes().length;
                        if (builderParamsCount > 0) {
                            throw new JabaBootException("@Builder method should have no parameters. Error on " + locatedClass.getName());
                        }
                        builder.invoke(currentInstance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new JabaBootException("Error while execute @Builder method for " + locatedClass.getName(), e);
                    }
                }

                jabaApplicationScope.put(locatedClass, currentInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new JabaBootException("Error while invoke constructor " + cdiConstructor.getName() + " of class " + locatedClass.getName(), e);
            }
        });

        /**
         * Injection strategy function
         */
        private InjectionStrategyFunction injectionStrategyFunction;

        /**
         * Function getter
         *
         * @return InjectionStrategyFunction
         */
        InjectionStrategyFunction getFunction() {
            return injectionStrategyFunction;
        }

        /**
         * Constructor
         *
         * @param injectionStrategyFunction InjectionStrategyFunction
         */
        InjectStrategy(InjectionStrategyFunction injectionStrategyFunction) {
            this.injectionStrategyFunction = injectionStrategyFunction;
        }

    }

    /**
     * Function of injection strategy
     */
    private interface InjectionStrategyFunction {
        void invoke(Class<?> locatedClass, JabaApplicationScope scope, RecursionInject0 inject0);
    }

    /**
     * This interface need for invoke {@link ReadyForInject#inject0 inject0} method inside injection strategy function
     */
    private interface RecursionInject0 {
        void invoke(Class<?> locatedClass);
    }

}
