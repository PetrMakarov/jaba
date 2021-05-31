package com.pmakarov.jabaui.bindings;

/**
 * One way binding for swing components
 */
public interface OneWayBinding extends Binding {
    /**
     * Init the value of component
     *
     * @param value model value
     */
    void init(Object value);

    /**
     * Register on change event for component and model
     */
    void registerOnChange();
}
