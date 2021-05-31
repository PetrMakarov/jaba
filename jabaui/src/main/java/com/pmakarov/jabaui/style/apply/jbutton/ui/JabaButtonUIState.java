package com.pmakarov.jabaui.style.apply.jbutton.ui;

import com.pmakarov.jabaui.style.apply.common.ColorFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class JabaButtonUIState {
    private Color borderColor;
    private Color textColor;
    private Color backgroundColor;

    public JabaButtonUIState(String borderHex, String textHex, String backgroundHex) {
        this(ColorFactory.get(borderHex), ColorFactory.get(textHex), ColorFactory.get(backgroundHex));
    }

    public void setBorderColor(String hex) {
        borderColor = ColorFactory.get(hex);
    }

    public void setTextColor(String hex) {
        textColor = ColorFactory.get(hex);
    }

    public void setBackgroundColor(String hex) {
        backgroundColor = ColorFactory.get(hex);
    }
}
