package com.pmakarov.jabaui.dropdown;

import com.pmakarov.jabaui.inputlist.Event;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

/**
 * Editable drop down
 *
 * @param <T>
 */
public class EditableDropDown<T> extends JComboBox<T> {
    private DefaultComboBoxModel model;
    private DropDownEditContainer<T> dropDownEditContainer;
    private Event onUserChoose;

    public EditableDropDown(Class<?> panelClass, Class<T> dataClass, List<T> items, Event onUserChoose) {
        model = new DefaultComboBoxModel<>();
        setModel(model);
        setRenderer(new EditableDropDownRenderer<T>());
        setEditor(new EditableDropDownEditor());
        addItemsToModel(items);
        this.onUserChoose = onUserChoose;
        dropDownEditContainer = DropDownEditContainer.<T>builder()
                .dataClass(dataClass)
                .panelClass(panelClass)
                .list(items)
                .onChange(target -> {
                    this.updateModel((List<T>) target);
                })
                .build();
        //new DropDownEditContainer<>(panelClass, dataClass, items);
        dropDownEditContainer.setSize(450, 600);
        addActionListener(new ClickItemListener());
    }

    /**
     * Add list to drop down
     *
     * @param items data
     */
    private void addItemsToModel(List<T> items) {
        Object first = "Edit";
        model.addElement(first);
        for (T item : items) {
            model.addElement(item);
        }
    }

    /**
     * Update model of dropdown
     *
     * @param list updated list
     */
    private void updateModel(List<T> list) {
        model.removeAllElements();
        Object first = "Edit";
        model.addElement(first);
        for (T item : list) {
            model.addElement(item);
        }
        if (list.size() >= 1) {
            this.setSelectedIndex(1);
        }
    }

    /**
     * Click listener
     */
    class ClickItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            EditableDropDown<T> selected = (EditableDropDown<T>) event.getSource();
            Object selectedItem = selected.getSelectedItem();
            if (null != selectedItem) {
                if ("Edit".equals(selectedItem.toString())) {
                    dropDownEditContainer.setVisible(true);
                } else {
                    Optional.ofNullable(onUserChoose).ifPresent(event1 -> onUserChoose.emmit(selectedItem));
                }
            }
        }
    }
}