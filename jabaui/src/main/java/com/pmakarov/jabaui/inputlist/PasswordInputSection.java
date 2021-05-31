package com.pmakarov.jabaui.inputlist;

import javax.swing.*;
import java.awt.*;

public class PasswordInputSection extends TextInputSection {

    public PasswordInputSection(String label, Object data, String fieldName){
        super(label, data, fieldName);
    }

    @Override
    public Component createComponent(){
        return new JPasswordField();
    }
}
