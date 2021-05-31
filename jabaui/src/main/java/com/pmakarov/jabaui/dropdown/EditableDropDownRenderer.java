package com.pmakarov.jabaui.dropdown;

import javax.swing.*;
import java.awt.*;

/**
 * Drop down renderer
 */
class EditableDropDownRenderer<T> extends JPanel implements ListCellRenderer<T> {
    private JLabel labelItem = new JLabel();

    EditableDropDownRenderer() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(2, 2, 2, 2);

//        labelItem.setOpaque(true);
        labelItem.setHorizontalAlignment(JLabel.LEFT);

        add(labelItem, constraints);
//        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        labelItem.setText(value.toString());
        if (isSelected) {
            labelItem.setBackground(list.getBackground());
            labelItem.setForeground(list.getForeground());
        } else {
            labelItem.setForeground(Color.WHITE);
            labelItem.setBackground(Color.WHITE);
        }
        return this;
    }

}