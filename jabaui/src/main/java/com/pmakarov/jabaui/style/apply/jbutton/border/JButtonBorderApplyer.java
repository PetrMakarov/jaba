package com.pmakarov.jabaui.style.apply.jbutton.border;

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
public class JButtonBorderApplyer implements PropertyApplyer<JButton> {

    @Override
    public void apply(JButton component, JCSSProperty property) {
        JabaButtonUI ui = (JabaButtonUI) component.getUI();
        if ("border-radius".equals(property.getName())) {
            double heightRadius = Double.parseDouble(((String) property.getValue()).split(" ")[0]);
            double widthRadius = Double.parseDouble(((String) property.getValue()).split(" ")[1]);
            ui.setHeightRadius(heightRadius);
            ui.setWidthRadius(widthRadius);
        }
    }

}
