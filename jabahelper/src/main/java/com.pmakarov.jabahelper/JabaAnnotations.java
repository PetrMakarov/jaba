package com.pmakarov.jabahelper;

import java.lang.annotation.*;
import java.util.HashSet;
import java.util.Set;

public final class JabaAnnotations {

    private static final Set<Class<? extends Annotation>> JAVA_LANG_ANNOTATIONS = new HashSet<Class<? extends Annotation>>() {{
        add(Documented.class);
        add(Inherited.class);
        add(Native.class);
        add(Repeatable.class);
        add(Retention.class);
        add(Target.class);
    }};

    private JabaAnnotations() {
    }

    public static <A extends Annotation> boolean isAnnotationPresent(Class<?> clazz, Class<A> annotationType) {
        return null != findAnnotation(clazz, annotationType);
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        return findAnnotation(clazz, annotationType, new HashSet<Annotation>());
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, Set<Annotation> visited) {
        try {
            Annotation[] anns = clazz.getDeclaredAnnotations();
            for (Annotation ann : anns) {
                if (ann.annotationType() == annotationType) {
                    return (A) ann;
                }
            }
            for (Annotation ann : anns) {
                if (!isInJavaLangAnnotationPackage(ann) && visited.add(ann)) {
                    A annotation = findAnnotation(ann.annotationType(), annotationType, visited);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        } catch (Exception ex) {
            return null;
        }

        for (Class<?> ifc : clazz.getInterfaces()) {
            A annotation = findAnnotation(ifc, annotationType, visited);
            if (annotation != null) {
                return annotation;
            }
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null || Object.class == superclass) {
            return null;
        }
        return findAnnotation(superclass, annotationType, visited);
    }

    private static boolean isInJavaLangAnnotationPackage(Annotation annotation) {
        return JAVA_LANG_ANNOTATIONS.contains(annotation.getClass());
    }
}
