package com.pmakarov.jabaui.test;

import com.pmakarov.jabaui.boot.metadata.Builder;
import com.pmakarov.jabaui.boot.metadata.JabaCDI;
import com.pmakarov.jabaui.boot.metadata.JabaController;

import javax.swing.*;
import java.awt.*;

@JabaController
public class MainPanel extends JPanel {

    private Book book;

    @JabaCDI
    public MainPanel(Book book) {
        System.out.println("Create panel");
        this.book = book;
//        setBorder(BorderFactory.createLineBorder(Color.BLUE));
        JButton button = new JButton("TEST1");
        button.addActionListener(e -> {
            System.out.println(book.toString());
            System.out.println(book.getName());
        });
        add(button);
    }

}
