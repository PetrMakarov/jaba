package com.pmakarov.jabaui.console;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;

public class PConsole extends JPanel {

    @Getter
    private OutputStream outputStream;

    public PConsole() {
        setLayout(new BorderLayout());
        JTextArea jTextArea = new JTextArea(10, 20);
        outputStream = new TextAreaOutputStream(jTextArea);
        JScrollPane scroll = new JScrollPane(jTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroll);
    }

    public void clear() {
        ((TextAreaOutputStream) outputStream).clear();
    }
}
