package com.pmakarov.jabaui.style.apply;

import com.pmakarov.jabaui.style.objects.JCSSProperty;

import java.awt.*;

/**
 * @author pmakarov
 * Basic api for classes that implement property apply logic
 */
public interface PropertyApplyer<T extends Component> {

    /**
     * Apply given property to the component
     *
     * @param component swing component
     * @param property  property object
     */
    void apply(T component, JCSSProperty property);
}
