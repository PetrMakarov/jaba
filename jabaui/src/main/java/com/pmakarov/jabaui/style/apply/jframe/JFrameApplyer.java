package com.pmakarov.jabaui.style.apply.jframe;

import com.pmakarov.jabaui.style.apply.PropertyApplyer;
import com.pmakarov.jabaui.style.apply.StyleApplyer;
import com.pmakarov.jabaui.style.objects.JCSSStyle;

import javax.swing.*;
import java.util.Map;
import java.util.function.BiPredicate;

public class JFrameApplyer extends StyleApplyer<JFrame> {
    @Override
    protected BiPredicate<JCSSStyle, JFrame> predicate() {
        return componentStylePredicate(JFrame.class);
    }

}
