package com.pmakarov.jabaui.test;

import com.pmakarov.jabaui.boot.metadata.*;

import javax.swing.*;
import java.awt.*;

@Primary
@JabaController
public class MainWindow extends JFrame {

    private MainPanel mainPanel;

    @JabaCDI
    private SecondPanel secondPanel;

    @JabaCDI
    public MainWindow(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Builder
    public void builder(){
        System.out.println("Create window");
        setLayout(new GridBagLayout());
        add(mainPanel, getRepositoryInfoGBC());
        add(secondPanel, getWildflyGBC());
    }

    private GridBagConstraints getRepositoryInfoGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
//        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = 1;
        gbc.weighty = 1;
        return gbc;
    }

    private GridBagConstraints getWildflyGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
//        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = 1;
        gbc.weighty = 1;
        return gbc;
    }

    private GridBagConstraints getProjectsGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
//        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = 1;
        gbc.weighty = 1;
        return gbc;
    }
}

