package com.pmakarov.jabaui.style.apply.jframe.main;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.apply.common.ColorFactory;
import com.pmakarov.jabaui.style.objects.JCSSProperty;

import javax.swing.*;

public class JFrameMainApplyer implements PropertyApplyer<JFrame> {
    @Override
    public void apply(JFrame component, JCSSProperty property) {
        if ("background-color".equals(property.getName())) {
            component.setBackground(ColorFactory.get((String) property.getValue()));
        }
        if ("opacity".equals(property.getName())) {
            component.setBackground(ColorFactory.withOpacity(component.getBackground(), Integer.parseInt((String)property.getValue())));
        }
    }
}
