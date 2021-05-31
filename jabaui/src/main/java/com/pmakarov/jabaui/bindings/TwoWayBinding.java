package com.pmakarov.jabaui.bindings;

/**
 * @author pmakarov
 */
public interface TwoWayBinding extends Binding {
    void propagateFromView();
    void propagateFromObject(Object initValue);
}
