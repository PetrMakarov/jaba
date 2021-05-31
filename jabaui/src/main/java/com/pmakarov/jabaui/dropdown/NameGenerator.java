package com.pmakarov.jabaui.dropdown;

import com.pmakarov.jabahelper.JabaReflection;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Name generator for new instance of dropdown
 * @param <T>
 */
class NameGenerator<T> {

    final static Logger logger = Logger.getLogger(NameGenerator.class);

    /*
    Generator description
     */
    @Getter
    private DropListNaming namingAnnotation;

    /*
    List of objects
     */
    private List<T> list;

    /*
    Last generated
     */
    private String last;

    /*
    Counter for name generator
     */
    private int counter = 1;

    NameGenerator(Class<T> dataClass, List<T> list) {
        this.namingAnnotation = dataClass.getAnnotation(DropListNaming.class);
        this.list = list;
    }

    String next() {
        if (null == last) {
            last = namingAnnotation.initValue();
        }
        String field = namingAnnotation.field();
        list.stream().filter(e -> {
            try {
                return last.equals(JabaReflection.getValueByFieldName(e, field));
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                logger.error("Error on generating name", ex);
            }
            return false;
        })
                .findFirst()
                .ifPresent(found -> {
                    last = last.replaceAll("\\(.*\\)", "") + "(" + counter++ + ")";
                });
        return last;
    }
}
