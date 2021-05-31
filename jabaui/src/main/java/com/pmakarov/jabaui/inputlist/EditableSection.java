package com.pmakarov.jabaui.inputlist;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class EditableSection extends InputSection {

    private EditFrame editFrame;
    private JPanel panel;
    private Event onSave;
    private Event onCancel;
    private Renderer renderer;
    private JLabel rendererLabel;

    @Builder(toBuilder = true)
    public EditableSection(String label, JPanel panel, Event onSave, Event onCancel, Renderer renderer) {
        super(label, null, null);
//        this.panel = panel;
//        this.onSave = onSave;
//        this.onCancel = onCancel;
        this.renderer = renderer;
        this.rendererLabel = createLabel();
        this.editFrame = new EditFrame(panel, onSave, onCancel);
        this.editFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rendererLabel.setText(renderer.invoke());
            }
        });
    }

    public EditFrame getEditFrame() {
        return editFrame;
    }

    @Override
    public Component createComponent() {
        JPanel component = new JPanel();
        component.setLayout(new GridBagLayout());
        this.rendererLabel.setText(this.renderer.invoke());
        JButton editButton = createEditButton();
        component.add(this.rendererLabel, getLeftGBC());
        component.add(editButton, getRightGBC());
        return component;
    }

    /**
     * Create label component
     *
     * @return label
     */
    private JLabel createLabel() {
        return new JLabel();
    }

    /**
     * Create edit button
     *
     * @return button
     */
    private JButton createEditButton() {
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
//            this.editFrame = new EditFrame(panel, onSave, onCancel);
            this.getEditFrame().pack();
            this.getEditFrame().setVisible(true);
        });
        return editButton;
    }

    /**
     * Style for input
     *
     * @return style
     */
    private GridBagConstraints getLeftGBC() {
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 2.0;
        return left;
    }

    /**
     * Style for choose
     *
     * @return style
     */
    private GridBagConstraints getRightGBC() {
        GridBagConstraints right = new GridBagConstraints();
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
        return right;
    }

    public interface Renderer {
        String invoke();
    }
}
