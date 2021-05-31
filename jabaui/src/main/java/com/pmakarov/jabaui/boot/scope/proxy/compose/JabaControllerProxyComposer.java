package com.pmakarov.jabaui.boot.scope.proxy.compose;

import com.pmakarov.jabaui.boot.CDIService;
import com.pmakarov.jabaui.boot.JabaUI;
import com.pmakarov.jabaui.boot.metadata.AfterInvokeOpen;
import com.pmakarov.jabaui.boot.metadata.BeforeInvokeOpen;
import com.pmakarov.jabaui.boot.metadata.Builder;
import com.pmakarov.jabaui.boot.metadata.Primary;
import com.pmakarov.jabaui.boot.scope.proxy.exception.JabaProxyCreationException;
import javassist.*;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for create proxy for <code>Swing</code> components
 */
public class JabaControllerProxyComposer extends JabaProxyComposer {

    private static final String DEFAULT_CONSTRUCTOR_DESCRIPTOR = "()V";

    private Class<?> origClass;

    @Override
    public Class<?> createProxy(Class<?> original) throws JabaProxyCreationException {
        try {
            origClass = original;
            return createProxyClass(origClass);
        } catch (JabaProxyCreationException | CannotCompileException | NotFoundException | ClassNotFoundException | IOException e) {
            throw new JabaProxyCreationException("Error while create proxies", e);
        }
    }

    private Class<?> createProxyClass(Class<?> orig) throws NotFoundException, CannotCompileException, ClassNotFoundException, JabaProxyCreationException, IOException {
        ClassPool pool = ClassPool.getDefault();
        CtClass extend = pool.get(orig.getName());
        CtClass proxy = pool.getAndRename(orig.getName(), orig.getName() + DEFAULT_PROXY_SUFFIX_NAME);
        proxy.setSuperclass(extend);
        proccessSuperConstructorsCall(proxy);
        processPrimaryAnnotation(proxy);
        processMethodsAndConstructorAnnotations(proxy);
        return proxy.toClass();
    }

    /**
     * Writes <code><b>super(args);<b/></code> code into body of constructors and builders
     * because we need it to prevent from double code invocation:<br><code>Javaassist</code> subclassing proxy and do not use <code><b>super(args);<b/></code>
     * keyword, instead it just copy bytecode of super class to its subclassed proxy. It may lead bytecode to double invocation
     * (e.g in default no arg constructor first bytecode of super constructor will be invoked, then same bytecode(copied)
     * will be invoked in subclass constructor)
     *
     * @param proxy current proxy
     * @throws CannotCompileException error while compile bytecode
     */
    private void proccessSuperConstructorsCall(CtClass proxy) throws CannotCompileException {
        for (CtConstructor ctConstructor : proxy.getDeclaredConstructors()) {
            ctConstructor.setBody("super($$);");
        }
        List<CtMethod> builders = Arrays.stream(proxy.getDeclaredMethods()).filter(m -> m.hasAnnotation(Builder.class)).collect(Collectors.toList());
        if (builders.size() > 0) {
            for (CtMethod builder : builders) {
                builder.setBody("super." + builder.getName() + "($$);");
            }
        }
    }

    /**
     * Process controller (add logic) if class has {@link Primary} annotation
     *
     * @param proxy current proxy
     * @throws CannotCompileException     error on compile
     * @throws JabaProxyCreationException error if current class isn't (sub)type of {@link Window}
     */
    private void processPrimaryAnnotation(CtClass proxy) throws CannotCompileException, JabaProxyCreationException {
        if (proxy.hasAnnotation(Primary.class)) {
            if (Window.class.isAssignableFrom(origClass)) {
                List<CtMethod> builders = Arrays.stream(proxy.getDeclaredMethods()).filter(m -> m.hasAnnotation(Builder.class)).collect(Collectors.toList());
                if (builders.size() > 0) {
                    for (CtMethod builder : builders) {
                        builder.insertAfter(new WindowOpenLogic().generate(origClass.getName()));
                    }
                } else {
                    for (CtConstructor constructor : proxy.getConstructors()) {
                        constructor.insertAfter(new WindowOpenLogic().generate(origClass.getName()));
                    }
                }
            } else {
                throw new JabaProxyCreationException("Primary is available only for controllers with (sub)type of " + Window.class.getName());
            }
        }
    }

    private void processMethodsAndConstructorAnnotations(CtClass proxy) throws ClassNotFoundException, CannotCompileException, JabaProxyCreationException {
        for (CtMethod method : proxy.getDeclaredMethods()) {
            processOpenAnnotations(method);
        }
        for (CtConstructor constructor : proxy.getConstructors()) {
            processOpenAnnotations(constructor);
        }
    }

    private void processOpenAnnotations(CtBehavior behavior) throws CannotCompileException, ClassNotFoundException, JabaProxyCreationException {
        try {
            WindowOpenLogic windowOpenLogic = new WindowOpenLogic();
            if (behavior.hasAnnotation(BeforeInvokeOpen.class)) {
                BeforeInvokeOpen beforeInvokeOpen = (BeforeInvokeOpen) behavior.getAnnotation(BeforeInvokeOpen.class);
                Class<? extends Window> windowToOpenBefore = beforeInvokeOpen.value();
                behavior.insertBefore(windowOpenLogic.generate(windowToOpenBefore));
            }
            if (behavior.hasAnnotation(AfterInvokeOpen.class)) {
                AfterInvokeOpen afterInvileOpen = (AfterInvokeOpen) behavior.getAnnotation(AfterInvokeOpen.class);
                Class<? extends Window> windowToOpenAfter = afterInvileOpen.value();
                behavior.insertAfter(windowOpenLogic.generate(windowToOpenAfter));
            }
        } catch (UndeclaredThrowableException e) {
            throw new JabaProxyCreationException("Failed to get class to open from AfterInvokeOpen or BeforeInvokeOpen annotations. " +
                    "Probably you described same class as controller. If so, you should declare Primary annotation on controller instead", e.getCause());
        }
    }

    /**
     * Class that generate code "JabaUI#open"
     */
    private class WindowOpenLogic {

        private String generate(Class<? extends Window> windowToOpen) {
            return generate(windowToOpen.getName());
        }

        private String generate(String windowToOpenClassName) {
            String defaultInstance = windowToOpenClassName.equals(origClass.getName()) ? "this" : "new " + windowToOpenClassName + "()";
            return "if (null == " + CDIService.class.getName() + ".getBean(" + windowToOpenClassName + ".class)) {"
                    + JabaUI.class.getName() + ".open(" + defaultInstance + "); } "
                    + "else { " + JabaUI.class.getName() + ".open( (" + Window.class.getName() + ") " + CDIService.class.getName() + ".getBean(" + windowToOpenClassName + ".class)); }";
        }
    }
}
