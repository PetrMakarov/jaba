package com.pmakarov.jabaui.style.apply.jbutton.pseudo.hover.border;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.apply.common.ColorFactory;
import com.pmakarov.jabaui.style.apply.jbutton.ui.JabaButtonUI;
import com.pmakarov.jabaui.style.objects.JCSSProperty;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author pmakarov
 */
public class JButtonHoverBorderApplyer implements PropertyApplyer<JButton> {

    @Override
    public void apply(JButton component, JCSSProperty property) {
        JabaButtonUI ui = (JabaButtonUI) component.getUI();
        if ("border-color".equals(property.getName())) {
            ui.getHover().setBorderColor((String) property.getValue());
        }
        if ("text-color".equals(property.getName())) {
            ui.getHover().setTextColor((String) property.getValue());
        }
        if ("background-color".equals(property.getName())) {
            ui.getHover().setBackgroundColor((String) property.getValue());
        }
    }

}
