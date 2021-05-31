package com.pmakarov.jabahelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author pmakarov
 * Class for do easy plain java operations
 */
public final class PlainJabaHelper {

    public static final List<String> JAVA_LANG_AND_PRIMITIVES = Collections.unmodifiableList(new ArrayList<String>() {{
        add("java.lang.Character");
        add("java.lang.Boolean");
        add("java.lang.Integer");
        add("java.lang.Double");
        add("java.lang.Float");
        add("java.lang.Number");
        add("java.lang.Short");
        add("java.lang.Byte");
        add("java.lang.Object");
        add("java.lang.String");
        add("java.lang.Long");
        add("java.util.Date");
        add("int");
        add("float");
        add("double");
        add("byte");
        add("short");
        add("long");
        add("boolean");
        add("char");
    }});

    public static final List<String> STANDARD_METHODS = Collections.unmodifiableList(new ArrayList<String>() {{
        add("toString");
        add("hashCode");
        add("equals");
    }});

    private PlainJabaHelper() {

    }

    public static boolean isPrimitive(String type) {
        return JAVA_LANG_AND_PRIMITIVES.contains(type);
    }

    public static boolean isPrimitive(Class<?> type) {
        return isPrimitive(type.getName());
    }

    /**
     * Upper case first char
     *
     * @param input string
     * @return string with capitalized first char
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Lower case first char
     *
     * @param input string
     * @return string with uncapitalized first char
     */
    public static String uncapitalize(String input) {
        if (null == input || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    /**
     * Check if string is numeric
     *
     * @param strNum string to check
     * @return is string numeric or not
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * If map contains list as value, this method help to add element to this very list by key and value
     *
     * @param map source map that contains list
     * @param k   key
     * @param v   value
     * @param <K> type of key
     * @param <V> type of value
     */
    public static <K, V> void addToListInMap(Map<K, List<V>> map, K k, V v) {
        if (null == map.get(k)) {
            map.put(k, new ArrayList<V>() {{
                add(v);
            }});
        } else {
            map.get(k).add(v);
        }
    }
}
