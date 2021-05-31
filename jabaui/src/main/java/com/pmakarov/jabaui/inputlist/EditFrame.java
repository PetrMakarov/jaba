package com.pmakarov.jabaui.inputlist;

import lombok.Builder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Optional;

@Builder
public class EditFrame extends JFrame {

    private JPanel panel;
    private Event onSave;
    private Event onCancel;

    public EditFrame(JPanel panel, Event onSave, Event onCancel) {
        this.onSave = onSave;
        this.onCancel = onCancel;
        this.getContentPane().setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        JPanel buttonContainer = createButtonsContainer();
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.getContentPane().add(buttonContainer, BorderLayout.SOUTH);
    }

    private JPanel createButtonsContainer() {
        JButton saveButton = createSaveButton();
        JButton cancelButton = createCancelButton();
        JPanel buttonContainer = new JPanel();
        buttonContainer.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        buttonContainer.add(saveButton);
        buttonContainer.add(cancelButton);
        return buttonContainer;
    }

    private JButton createSaveButton() {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            Optional.ofNullable(onSave).ifPresent(event -> onSave.emmit(null));
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            this.setVisible(false);
            this.dispose();
        });
        return saveButton;
    }

    private JButton createCancelButton() {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            Optional.ofNullable(onCancel).ifPresent(event -> onCancel.emmit(null));
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            this.setVisible(false);
            this.dispose();
        });
        return cancelButton;
    }
}
