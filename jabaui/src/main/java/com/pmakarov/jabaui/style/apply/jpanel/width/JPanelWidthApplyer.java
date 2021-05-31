package com.pmakarov.jabaui.style.apply.jpanel.width;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.objects.JCSSProperty;

import javax.swing.*;
import java.awt.*;

public class JPanelWidthApplyer implements PropertyApplyer<JPanel> {

    @Override
    public void apply(JPanel component, JCSSProperty property) {
        Dimension dimension = component.getSize();
        int width = Integer.parseInt(property.getValue().toString());
        component.setSize(new Dimension(width, dimension.height));
    }
}
