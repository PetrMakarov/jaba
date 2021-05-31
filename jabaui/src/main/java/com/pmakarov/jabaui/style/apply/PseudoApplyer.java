package com.pmakarov.jabaui.style.apply;

import com.pmakarov.jabaui.style.objects.JCSSProperty;
import com.pmakarov.jabaui.style.objects.JCSSStyle;

import java.awt.*;
import java.util.Map;
import java.util.function.BiPredicate;

public abstract class PseudoApplyer<T extends Component> extends StyleApplyer<T> {

    @Override
    protected BiPredicate<JCSSStyle, T> predicate() {
        throw new IllegalAccessError("Pseudo classes cannot have predicate");
    }

    @Override
    public Map<String, PseudoApplyer<T>> getAvailablePseudos() {
        throw new IllegalAccessError("Pseudo classes cannot have sub pseudo classes");
    }

    @Override
    public void apply(JCSSStyle pseudoStyle, T component) {
        for (JCSSProperty styleProperty : pseudoStyle.getStyleBody().getProperties()) {
            PropertyApplyer<T> propertyApplyer = getAvailableProperties().get(styleProperty.getName());
            if (null != propertyApplyer) {
                propertyApplyer.apply(component, styleProperty);
            }
        }
    }
}
