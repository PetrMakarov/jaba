package com.pmakarov.jabaui.style.apply.jbutton;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.apply.PseudoApplyer;
import com.pmakarov.jabaui.style.apply.StyleApplyer;
import com.pmakarov.jabaui.style.apply.jbutton.ui.JabaButtonUI;
import com.pmakarov.jabaui.style.objects.JCSSStyle;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author pmakarov
 */
public class JButtonApplyer extends StyleApplyer<JButton> {

    @Override
    protected BiPredicate<JCSSStyle, JButton> predicate() {
        return componentStylePredicate(JButton.class);
    }

    @Override
    public void apply(JCSSStyle pseudoStyle, JButton component){
        component.setUI(new JabaButtonUI());
        super.apply(pseudoStyle, component);
    }
}
