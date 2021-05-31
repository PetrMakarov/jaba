package com.pmakarov.jabaui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Custom panel view for implement panel sections generator
 *
 * @param <S>
 */
public abstract class PanelView<S> extends JPanel {

    protected static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    protected static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    /*
    Sections count
     */
    protected int count = 0;

    protected PanelView() {
        // grid layout
        this.setLayout(new GridBagLayout());
    }

    /**
     * Create sections by list
     *
     * @param sections sections list
     */
    protected abstract void sections(List<S> sections);

    /**
     * Create title with borders
     *
     * @param title title
     */
    protected void title(String title) {
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
}
