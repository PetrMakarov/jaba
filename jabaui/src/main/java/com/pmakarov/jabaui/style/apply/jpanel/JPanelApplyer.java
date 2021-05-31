package com.pmakarov.jabaui.style.apply.jpanel;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.apply.StyleApplyer;
import com.pmakarov.jabaui.style.objects.JCSSStyle;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author pmakarov
 */
public class JPanelApplyer extends StyleApplyer<JPanel> {

    @Override
    public BiPredicate<JCSSStyle, JPanel> predicate() {
        return componentStylePredicate(JPanel.class);
    }

    @Override
    public Map<String, PropertyApplyer<JPanel>> getAvailableProperties() {
        return availableProperties;
    }

}
