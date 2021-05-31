package com.pmakarov.jabaui.bindings;

import com.pmakarov.jabahelper.PlainJabaHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pmakarov
 * This object is link between model and swing component
 * Used in model proxy classes to achive 2-way binding
 */
public class ValuePropagator {
    private final Map<String, List<Propagation>> propagators = new HashMap<>();

    /**
     * Add function to be executed on setter call
     *
     * @param fieldName   field(key) to link
     * @param propagation function
     */
    public void registerPropagation(String fieldName, Propagation propagation) {
        PlainJabaHelper.addToListInMap(propagators, fieldName, propagation);
    }

    /**
     * Executes function by field(key) with passed value
     *
     * @param fieldName field(key) in setter call
     * @param value     value in setter call
     */
    public void execute(String fieldName, Object value) {
        List<Propagation> propagations = propagators.get(fieldName);
        if (null != propagations) {
            for (Propagation propagation : propagations) {
                if (null != propagation) {
                    propagation.execute(value);
                }
            }
        }
    }
}
