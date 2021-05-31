package com.pmakarov.jabaui.console;

import lombok.Getter;

import javax.swing.*;

public class PConsoleWrapper extends JFrame {

    @Getter
    private PConsole console;

    public PConsoleWrapper(int width, int height) {
        console = new PConsole();
        add(console);
        setLocationRelativeTo(null);
        setSize(width, height);
    }
}
