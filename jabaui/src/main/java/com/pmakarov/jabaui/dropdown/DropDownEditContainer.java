package com.pmakarov.jabaui.dropdown;

import com.pmakarov.jabahelper.JabaReflection;
import com.pmakarov.jabaui.inputlist.Event;
import lombok.Builder;
import lombok.NonNull;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Drop down edit frame container
 *
 * @param <T>
 */
class DropDownEditContainer<T> extends JFrame {

    final static Logger logger = Logger.getLogger(DropDownEditContainer.class);

    /*
    Class of panel that contains fields to edit dropdown item
     */
    private Class<?> panelClass;

    /*
    Store panel after creating to getUserObject opportunity to remove this on another item select
     */
    private JPanel panel;

    /*
    Custom JList
     */
    private JList dropList;

    /*
    Class of data objects in list
     */
    private Class<T> dataClass;

    /*
    Data list
     */
    private List<T> list;

    /*
    Name generator for JList
     */
    private NameGenerator<T> nameGenerator;

    /*
    On list change event
     */
    private Event onChange;

    @Builder
    DropDownEditContainer(@NonNull Class<?> panelClass, @NonNull Class<T> dataClass, List<T> list, Event onChange) {
        this.panelClass = panelClass;
        this.dataClass = dataClass;
        this.list = list;
        this.onChange = onChange;
        this.nameGenerator = new NameGenerator<>(this.dataClass, list);
        Container contentPane = this.getContentPane();
        // list and buttons container
        JPanel leftSide = new JPanel(new BorderLayout());
        dropList = new DropList<>(list);
        JPanel buttonContainer = createButtons();
        leftSide.add(buttonContainer, BorderLayout.NORTH);
        leftSide.add(dropList, BorderLayout.CENTER);
        dropList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(leftSide);
        contentPane.add(scrollPane, BorderLayout.WEST);
//        contentPane.add(panel, BorderLayout.CENTER);
        dropList.addListSelectionListener(listSelectionListener);
    }

    /**
     * Selection listener
     */
    private ListSelectionListener listSelectionListener = listSelectionEvent -> {
        if (!listSelectionEvent.getValueIsAdjusting()) {
            JList list = (JList) listSelectionEvent.getSource();
            List<T> data = list.getSelectedValuesList();
            if (data.size() > 0) {
                initializeEditPanel(data.get(0));
                System.out.println(data.get(0).toString());
            }
        }
    };

    /**
     * Create container and buttons for list control
     *
     * @return buttons container
     */
    private JPanel createButtons() {
        JButton addButton = new JButton("add");
        JButton cancelButton = new JButton("del");
        addButton.addActionListener(e -> {
            T item = createGenericInstance();
            if (null != item) {
                ((DefaultListModel<T>) dropList.getModel()).addElement(item);
                this.list.add(item);
                onChange.emmit(this.list);
            }
        });
        cancelButton.addActionListener(e -> {
            ListSelectionModel model = dropList.getSelectionModel();
            int index = model.getMinSelectionIndex();
            if (index >= 0) {
                ((DefaultListModel) dropList.getModel()).remove(index);
                this.list.remove(index);
                onChange.emmit(this.list);
            }
        });
        JPanel buttonContainer = new JPanel();
        buttonContainer.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        buttonContainer.add(addButton);
        buttonContainer.add(cancelButton);
        return buttonContainer;
    }

    /**
     * Create instance of generic object
     *
     * @return instance
     */
    private T createGenericInstance() {
        try {
            String newName = nameGenerator.next();
            T newObject = this.dataClass.newInstance();
            JabaReflection.setValueByFieldName(newObject, nameGenerator.getNamingAnnotation().field(), newName);
            return newObject;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            logger.error("Error create data class in dropdown container with name [" + this.dataClass.getName() + "]", e);
        }
        return null;
    }

    /**
     * Initialize panel
     *
     * @param data data
     */
    private void initializeEditPanel(T data) {
        try {
            // remove if exists
            if (null != this.panel) {
                this.getContentPane().remove(this.panel);
            }
            // TODO FIX?
//            Constructor<?> constructor = this.panelClass.getConstructors()[0];
//            constructor.newInstance(data);
            JPanel panel = (JPanel) this.panelClass.getConstructors()[0].newInstance(data);
            this.panel = panel;
            this.getContentPane().add(panel, BorderLayout.CENTER);
            this.setVisible(true);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error create panel in dropdown container with name [" + this.panelClass.getName() + "]", e);
        }
    }

}
