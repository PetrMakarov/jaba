package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.PanelView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Input list base class
 * Generate panel that contains fields described by InputSection's
 */
public class InputList extends PanelView<InputSection> {

    public InputList() {
        super();
    }

    /**
     * Create fields from list of InputSection's
     *
     * @param sections list of section to be placed on panel
     */
    @Override
    public void sections(List<InputSection> sections) {
        for (InputSection section : sections) {
            createSection(section);
        }
    }

    /**
     * Create single section
     *
     * @param section InputSection instance
     */
    private void createSection(InputSection section) {
        // label
        JLabel nameLabel = new JLabel(section.getLabel());
        // input component
        Component component = section.create();
        this.add(nameLabel, createGbc(0, count));
        this.add(component, createGbc(1, count));
        count++;
    }

    /**
     * Create positioning constants for created component (grid-like)
     *
     * @param x column number
     * @param y row number
     * @return style constant
     */
    protected GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (x == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;

        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }
}
