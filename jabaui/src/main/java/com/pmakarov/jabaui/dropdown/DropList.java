package com.pmakarov.jabaui.dropdown;

import javax.swing.*;
import java.util.List;

/**
 * List for drop down edit frame
 *
 * @param <T>
 */
class DropList<T> extends JList<T> {

    /*
    Model
     */
    private DefaultListModel<T> model;

    DropList(final List<T> list) {
        model = new DefaultListModel<>();
        setModel(model);
        // create custom renderer
        setCellRenderer(new DropListRenderer());
        for (T item : list) {
            model.addElement(item);
        }
    }
}
