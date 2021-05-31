package com.pmakarov.jabahelper;

import org.apache.log4j.Logger;

import java.lang.reflect.*;

/**
 * Helper class for common reflection logic of application
 */
public final class JabaReflection {

    private final static Logger logger = Logger.getLogger(JabaReflection.class);

    private JabaReflection() {
    }

    /**
     * Create new instance of resource
     *
     * @param type class
     * @param <T>  generic type
     * @return new instance
     */
    public static <T> T createNewInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Error create new instance of " + type.getName(), e);
        }
        return null;
    }

    /**
     * Set value to object by field name and perform type cast
     *
     * @param obj       object
     * @param fieldName field name
     * @param value     value to set
     * @throws NoSuchFieldException   if no such field exists
     * @throws IllegalAccessException access denied
     */
    public static void setValueByFieldName(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        setFieldValueTypeCast(obj, field, value);
    }

    /**
     * Set value to object by field object and perform type cast
     *
     * @param obj   object
     * @param field field object
     * @param value value
     * @throws IllegalAccessException access denied
     */
    public static void setFieldValueTypeCast(Object obj, Field field, Object value) throws IllegalAccessException {
        setFieldValue(obj, field, value, true);
    }

    /**
     * Set value to object by field object without type cast
     *
     * @param obj   object
     * @param field field object
     * @param value value
     * @throws IllegalAccessException access denied
     */
    public static void setFieldValue(Object obj, Field field, Object value) throws IllegalAccessException {
        setFieldValue(obj, field, value, false);
    }

    /**
     * Set calue to object by field object
     *
     * @param obj      object
     * @param field    field object
     * @param value    value
     * @param typeCast either perform type cast or not
     * @throws IllegalAccessException access denied
     */
    public static void setFieldValue(Object obj, Field field, Object value, Boolean typeCast) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        if (typeCast) {
            Class<?> fieldType = field.getType();
            if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType))
                field.set(obj, Integer.parseInt(String.valueOf(value)));
            if (String.class.isAssignableFrom(fieldType))
                field.set(obj, String.valueOf(value));
            if (Long.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType))
                field.set(obj, Long.parseLong(String.valueOf(value)));
            if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))
                field.set(obj, Boolean.parseBoolean(String.valueOf(value)));
        } else {
            field.set(obj, value);
        }
        field.setAccessible(isAccessible);
    }

    /**
     * Get value from object by field name
     *
     * @param data      object
     * @param fieldName field name
     * @return value
     * @throws NoSuchFieldException   if no such field exists
     * @throws IllegalAccessException access denied
     */
    private static Object getValueByFieldNameNoGetter(Object data, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (data == null) {
            return null;
        }
        Field field = data.getClass().getDeclaredField(fieldName);
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(data);
        field.setAccessible(accessible);
        return value;
    }

    /**
     * Get value of object by field name
     *
     * @param data      object
     * @param fieldName field name
     * @return value of field
     * @throws NoSuchFieldException   error on field search
     * @throws IllegalAccessException error on private or protected access
     */
    public static Object getValueByFieldName(Object data, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (data == null) {
            return null;
        }
        Object value;
        try {
            Method getter = data.getClass().getMethod(getterName(fieldName));
            value = getter.invoke(data);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            value = getValueByFieldNameNoGetter(data, fieldName);
        }
        return value;
    }

    /**
     * Get value of object by its field
     *
     * @param data  object
     * @param field field
     * @return value of field
     * @throws NoSuchFieldException   error on field search
     * @throws IllegalAccessException error on private or protected access
     */
    public static Object getValueByField(Object data, Field field) throws NoSuchFieldException, IllegalAccessException {
        return getValueByFieldName(data, field.getName());
    }

    /**
     * Get getter name by field object
     *
     * @param field current field
     * @return getter name for field
     */
    public static String getterName(Field field) {
        return getterName(field.getName());
    }

    /**
     * Get getter name by field name
     *
     * @param fieldName field name
     * @return getter name for field
     */
    public static String getterName(String fieldName) {
        if (null == fieldName || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name is empty or null");
        }
        return "get" + PlainJabaHelper.capitalize(fieldName);
    }

    /**
     * Get single generic parameter type of field
     *
     * @param field passed field
     * @return generic class type
     */
    public static Class<?> getFieldGenericSingleClass(Field field) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    /**
     * Get single generic parameter type of field by instance and field name
     *
     * @param obj       object
     * @param fieldName field name
     * @return generic class type
     */
    public static Class<?> getFieldGenericSingleClass(Object obj, String fieldName) {
        return getFieldGenericSingleClass(obj.getClass(), fieldName);
    }

    /**
     * Get single generic parameter type of field by class and field name
     *
     * @param clazz     class
     * @param fieldName field name
     * @return generic class type
     */
    public static Class<?> getFieldGenericSingleClass(Class<?> clazz, String fieldName) {
        try {
            return getFieldGenericSingleClass(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            logger.error("Error get generic type of: " + clazz + "$" + fieldName);
        }
        return null;
    }

}
