package com.pmakarov.jabaui.style.apply.jpanel.main;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.apply.common.ColorFactory;
import com.pmakarov.jabaui.style.objects.JCSSProperty;

import javax.swing.*;
import java.awt.*;

public class JPanelMainApplyer implements PropertyApplyer<JPanel> {

    @Override
    public void apply(JPanel component, JCSSProperty property) {
        if ("background-color".equals(property.getName())) {
            component.setBackground(ColorFactory.withOpacity((String) property.getValue(), component.getBackground().getAlpha()));
        }
        if ("opacity".equals(property.getName())) {
            component.setBackground(ColorFactory.withOpacity(component.getBackground(), Integer.parseInt((String) property.getValue())));
        }
    }
}
