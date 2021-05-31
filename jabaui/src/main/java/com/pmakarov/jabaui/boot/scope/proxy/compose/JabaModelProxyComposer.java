package com.pmakarov.jabaui.boot.scope.proxy.compose;

import com.pmakarov.jabahelper.JabaReflection;
import com.pmakarov.jabahelper.PlainJabaHelper;
import com.pmakarov.jabaui.bindings.ProxyBinding;
import com.pmakarov.jabaui.bindings.ValuePropagator;
import com.pmakarov.jabaui.boot.scope.proxy.exception.JabaProxyCreationException;
import javassist.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for create proxy for 2-way binding on UI.
 * It adds {@link ValuePropagator} instance and getter for it.
 * <pre>
 *     ...
 *     private ValuePropagator valuePropagator = new ValuePropagator();
 *     ...
 *     public ValuePropagator getValuePropagator() {
 *         return valuePropagator;
 *     }
 * </pre>
 * To do so, this builder create proxy that extends current class and implements {@link ProxyBinding}
 * Also it adds logic to execute propagation in every primitive setter method of class
 * i.e
 * <pre>
 *     ...
 *     public void setValue(java.lang.String value) {
 *         this.value = value;
 *         <font color="green">valuePropagator.execute("value",value);</font> // added part
 *     }
 *     ...
 * </pre>
 */
public class JabaModelProxyComposer extends JabaProxyComposer {

    private final static String DEFAULT_PROPAGATOR_FIELD_NAME = "valuePropagator";
    private final static String DEFAULT_PROPAGATOR_GETTER_NAME = "getValuePropagator";
    private final static String DEFAULT_ORIGINAL_GETTER_NAME = "getOriginal";

    private Collection<Class<?>> classNestingCollection;

    /**
     * Create proxy object instance for passed object accordingly to comment section on {@link JabaModelProxyComposer}
     *
     * @param original original class
     * @return proxy class
     * @throws JabaProxyCreationException errors while creating proxy classes and instances
     */
    @Override
    public Class<?> createProxy(Class<?> original) throws JabaProxyCreationException {
        try {
            return createProxyRecursively(original);
        } catch (NotFoundException | CannotCompileException e) {
            throw new JabaProxyCreationException("Error while create proxies", e);
        }
    }

    /**
     * Create proxies recursively(except primitive field types) for current object
     *
     * @param original original class
     * @return root proxy class
     * @throws NotFoundException      errors on proxy members
     * @throws CannotCompileException errors on proxy access members
     */
    private Class<?> createProxyRecursively(Class<?> original) throws NotFoundException, CannotCompileException {
        List<Class<?>> proxyClassList = new ArrayList<>();
        classNestingCollection = new LinkedList<>(modelNestingToSet(original));
        Collections.reverse((LinkedList<?>) classNestingCollection);
        for (Class<?> current : classNestingCollection) {
            proxyClassList.add(createProxyClass(current));
        }
        // root is always first in this list
        return proxyClassList.stream()
                .filter(original::isAssignableFrom)
                .findFirst()
                .orElse(null);
    }

    /**
     * Method invokes "stretch" of class nesting of current object and provide collection
     * to store all these elements
     *
     * @param original root class of nesting
     * @return list of all classes in nesting (except primitive types)
     */
    private Set<Class<?>> modelNestingToSet(Class<?> original) {
        Set<Class<?>> classList = new LinkedHashSet<>();
        modelsRecursiveSearch(original, classList);
        return classList;
    }

    /**
     * Recursive search to add all classes to be proxied to list (stretch model to list)
     *
     * @param current         current class in recursion
     * @param classCollection collection where all nesting is stored
     */
    private void modelsRecursiveSearch(Class<?> current, Set<Class<?>> classCollection) {
        if (!PlainJabaHelper.isPrimitive(current)) {
            classCollection.add(current);
            for (Field field : current.getDeclaredFields()) {
                modelsRecursiveSearch(determineFieldTypeForProxy(field), classCollection);
            }
        }
    }

    /**
     * Determines type(Class) of field
     *
     * @param field passed field
     * @return field type
     */
    private Class<?> determineFieldTypeForProxy(Field field) {
        Class<?> type = field.getType();
        if (isCollection(type)) {
            type = JabaReflection.getFieldGenericSingleClass(field);
        }
        return type;
    }

    /**
     * Create proxy of model object (should be pojo)
     *
     * @param orig class of pojo model
     * @return proxy class
     * @throws NotFoundException      errors on proxy members
     * @throws CannotCompileException errors on proxy access members
     */
    private Class<?> createProxyClass(Class<?> orig) throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        CtClass extend = pool.get(orig.getName());
        CtClass[] interfaces = new CtClass[]{pool.get(ProxyBinding.class.getName())};
        CtClass proxy = pool.getAndRename(orig.getName(), orig.getName() + DEFAULT_PROXY_SUFFIX_NAME);
        proxy.setSuperclass(extend);
        proxy.setInterfaces(interfaces);
        insertOriginalFieldAndConstructor(proxy, orig);
        insertPropagator(proxy);
        insertProxyMethods(proxy, orig);
        return proxy.toClass();
    }

    /**
     * Add propagator field (with init logic) and its getter method
     *
     * @param proxy newly created proxy class
     * @throws NotFoundException      errors on get propagator class from pull
     * @throws CannotCompileException errors on access proxy members
     */
    private void insertPropagator(CtClass proxy) throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        CtClass propagator = pool.get(ValuePropagator.class.getName());
        CtField propagatorField = new CtField(propagator, DEFAULT_PROPAGATOR_FIELD_NAME, proxy);
        propagatorField.setModifiers(Modifier.PRIVATE);
        proxy.addField(propagatorField, CtField.Initializer.byExpr("new " + ValuePropagator.class.getName() + "();"));
        String getterName = Arrays.stream(ProxyBinding.class.getDeclaredMethods())
                .filter(m -> ValuePropagator.class.equals(m.getReturnType()))
                .map(Method::getName)
                .findFirst()
                .orElse(DEFAULT_PROPAGATOR_GETTER_NAME);
        CtMethod propagatorGetter = new CtMethod(propagator, getterName, null, proxy);
        propagatorGetter.setBody("return this." + DEFAULT_PROPAGATOR_FIELD_NAME + ";");
        proxy.addMethod(propagatorGetter);
    }

    /**
     * Insert original object bytecod and constructor (and getter) for it
     *
     * @param proxy    current proxy class
     * @param original original class
     * @throws CannotCompileException class compile error
     * @throws NotFoundException      if member of class not found
     */
    private void insertOriginalFieldAndConstructor(CtClass proxy, Class<?> original) throws CannotCompileException, NotFoundException {
        CtClass ctOriginal = ClassPool.getDefault().get(original.getName());
        // origin object field (lower case of class name)
        String originalFieldName = getOriginalFieldName(original);
        CtField originalField = new CtField(ctOriginal, originalFieldName, proxy);
        originalField.setModifiers(Modifier.PRIVATE);
        proxy.addField(originalField);

        // origin object getter
        CtMethod originalGetter = new CtMethod(ctOriginal, DEFAULT_ORIGINAL_GETTER_NAME, null, proxy);
        originalGetter.setBody("return this." + originalFieldName + ";");
        proxy.addMethod(originalGetter);

        // constructor
        CtConstructor constructor = new CtConstructor(new CtClass[]{ctOriginal}, proxy);
        StringBuilder body = new StringBuilder();
        body.append("{this.").append(originalFieldName).append(" = $1;").append(System.lineSeparator());
        for (Field field : original.getDeclaredFields()) {
            Class<?> fieldType = determineFieldTypeForProxy(field);
            if (classNestingCollection.contains(fieldType)) {
                if (isCollection(fieldType)) {
                    String lambdaParamName = field.getName() + "$";
                    body.append("this.")
                            .append(field.getName())
                            .append(" = ")
                            .append(originalFieldName)
                            .append(".")
                            .append(JabaReflection.getterName(field))//TODO fix JabaReflection getterName
                            .append(".stream().map(")
                            .append(lambdaParamName)
                            .append(" -> { return new ")
                            .append(JabaReflection.getFieldGenericSingleClass(field))
                            .append(DEFAULT_PROXY_SUFFIX_NAME)
                            .append("(")
                            .append(lambdaParamName)
                            .append(");}).collect(")
                            .append(Collectors.class.getName())
                            .append(".toCollection(")
                            .append(field.getClass())
                            .append("::new))");
                } else {
                    body.append("this.")
                            .append(field.getName())
                            .append(" = new ")
                            .append(fieldType.getName())
                            .append(DEFAULT_PROXY_SUFFIX_NAME)
                            .append("(")
                            .append(originalFieldName)
                            .append(".get")
                            .append(PlainJabaHelper.capitalize(field.getName()))
                            .append("());")
                            .append(System.lineSeparator());
                }
            }
        }
        body.append("}");
        constructor.setBody(body.toString());
        proxy.addConstructor(constructor);
    }

    private boolean isCollection(Class<?> fieldType) {
        return Collection.class.isAssignableFrom(fieldType);
    }

    /**
     * Create field name for original object
     *
     * @param original original object
     * @return field name
     */
    private String getOriginalFieldName(Class<?> original) {
        return PlainJabaHelper.uncapitalize(original.getSimpleName());
    }

    /**
     * Add block of logic in setter methods of newly created proxy
     *
     * @param proxy proxy object
     * @throws NotFoundException      errors in {@link #isPrimitiveSetter}
     * @throws CannotCompileException errors on adding logic to methods
     */
    private void insertProxyMethods(CtClass proxy, Class<?> original) throws NotFoundException, CannotCompileException {
        for (CtMethod method : proxy.getDeclaredMethods()) {
            tryInsertSetter(method);
            tryInsertGetter(method, original);
            tryInsertStandardMethods(method, original);
        }
    }

    /**
     * Insert override primitive setter logic for method
     *
     * @param method current method
     * @throws NotFoundException      method access errors
     * @throws CannotCompileException method compile errors
     */
    private void tryInsertSetter(CtMethod method) throws NotFoundException, CannotCompileException {
        if (isPrimitiveSetter(method)) {
            method.insertAfter(DEFAULT_PROPAGATOR_FIELD_NAME + ".execute(\"" + getSetterProperty(method) + "\", this." + getSetterProperty(method) + ");");
        }
    }

    /**
     * Insert override getter logic in method
     *
     * @param method   current method
     * @param original original class
     * @throws NotFoundException      method access errors
     * @throws CannotCompileException method compile errors
     */
    private void tryInsertGetter(CtMethod method, Class<?> original) throws NotFoundException, CannotCompileException {
        if (isGetter(method)) {
            method.setBody("return this." + getOriginalFieldName(original) + "." + method.getName() + "();");
        }
    }

    /**
     * Insert override logic for standard methods
     *
     * @param method   current method
     * @param original original class
     * @throws CannotCompileException method compile errors
     */
    private void tryInsertStandardMethods(CtMethod method, Class<?> original) throws CannotCompileException {
        if (PlainJabaHelper.STANDARD_METHODS.contains(method.getName())) {
            method.setBody("return this." + getOriginalFieldName(original) + "." + method.getName() + "($$);");
        }
    }

    /**
     * Determines whether or not method is getter
     *
     * @param method javaassist method
     * @return true if method is getter
     * @throws NotFoundException method access errors
     */
    private boolean isGetter(CtMethod method) throws NotFoundException {
        return !DEFAULT_PROPAGATOR_GETTER_NAME.equals(method.getName())
                && !DEFAULT_ORIGINAL_GETTER_NAME.equals(method.getName())
                && method.getName().startsWith("get")
                && method.getParameterTypes().length == 0;
    }

    /**
     * Determine whether or not method is setter of primitive type
     *
     * @param method javassist method
     * @return true if method is setter of primitive type
     * @throws NotFoundException method access errors
     */
    private boolean isPrimitiveSetter(CtMethod method) throws NotFoundException {
        return method.getName().startsWith("set")
                && method.getParameterTypes().length == 1 // 1 param
                && PlainJabaHelper.isPrimitive(method.getParameterTypes()[0].getName()) // param is primitive TODO: make universal
                && "void".equals(method.getReturnType().getName()); // return type is void
    }

    /**
     * Get name of property in setter
     *
     * @param method setter method
     * @return property name
     */
    private String getSetterProperty(CtMethod method) {
        return PlainJabaHelper.uncapitalize(method.getName().substring(3));
    }

}
