package com.pmakarov.jabaui.inputlist;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ButtonInputSection extends InputSection {

    private String buttonText;
    private Action action;
    private KeyStroke hotkey;

    @Builder(toBuilder = true)
    public ButtonInputSection(String buttonText, Action action, KeyStroke hotkey) {
        super("", null, null);
        this.buttonText = buttonText;
        this.action = action;
        this.hotkey = hotkey;
    }

    @Override
    public Component createComponent() {
        this.action.putValue(Action.NAME, this.buttonText);
        JButton button = new JButton(this.action);
        button.getActionMap().put("action", this.action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(this.hotkey, "action");
        return button;
    }
}
