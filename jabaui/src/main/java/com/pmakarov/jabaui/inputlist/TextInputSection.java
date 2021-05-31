package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.bindings.OneWayBinding;
import com.pmakarov.jabaui.bindings.ProxyBinding;
import com.pmakarov.jabaui.bindings.TwoWayBinding;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;

@NoArgsConstructor
public class TextInputSection extends InputSection {

    public TextInputSection(String label, Object data, String fieldName) {
        super(label, data, fieldName);
    }

    @Override
    public Component createComponent() {
        return new JTextField();
    }

    @Override
    protected OneWayBinding createOneWayBinding() {
        return new OneWayBinding() {
            @Override
            public void init(Object value) {
                ((JTextField) component).setText(value.toString());
            }

            @Override
            public void registerOnChange() {
                CommonViewPropagators.textComponent((JTextField) component,
                        TextInputSection.this,
                        () -> ((JTextField) component).getText());
            }
        };
    }

    @Override
    protected TwoWayBinding createTwoWayBinding(Object data) {
        return new TwoWayBinding() {
            @Override
            public void propagateFromView() {
                CommonViewPropagators.textComponent((JTextField) component,
                        TextInputSection.this,
                        () -> ((JTextField) component).getText());
            }

            @Override
            public void propagateFromObject(Object initValue) {
                ((JTextField) component).setText(initValue.toString());
                ((ProxyBinding<?>) data).getValuePropagator().registerPropagation(fieldName, value -> ((JTextField) component).setText(value.toString()));
            }
        };
    }
}
