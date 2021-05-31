package com.pmakarov.jabaui.dropdown;

import javax.swing.*;
import java.awt.*;

/**
 * Drop down frame list renderer
 * @param <T>
 */
class DropListRenderer<T> extends JLabel implements ListCellRenderer<T> {

    DropListRenderer(){
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
