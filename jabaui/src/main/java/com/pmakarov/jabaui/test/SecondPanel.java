package com.pmakarov.jabaui.test;

import com.pmakarov.jabaui.boot.metadata.JabaController;

import javax.swing.*;
import java.awt.*;

@JabaController
public class SecondPanel extends JPanel {

    public SecondPanel(){
        System.out.println("Create SecondPanel");
//        setBorder(BorderFactory.createLineBorder(Color.RED));
        CreateRoundButton button = new CreateRoundButton("Second Button");
        add(button);
    }
}
