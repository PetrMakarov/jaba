package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.bindings.OneWayBinding;
import com.pmakarov.jabaui.bindings.ProxyBinding;
import com.pmakarov.jabaui.bindings.TwoWayBinding;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

@NoArgsConstructor
public class CheckBoxSection extends InputSection {

    final static Logger logger = Logger.getLogger(CheckBoxSection.class);

    public CheckBoxSection(String label, Object data, String fieldName) {
        super(label, data, fieldName);
    }

    @Override
    public Component createComponent() {
        return new JCheckBox();
    }

    @Override
    protected OneWayBinding createOneWayBinding() {
        return new OneWayBinding() {
            @Override
            public void init(Object value) {
                CommonViewPropagators.CheckBox.initView((JCheckBox) component, value);
            }

            @Override
            public void registerOnChange() {
                CommonViewPropagators.CheckBox.propagateFromView((JCheckBox) component,
                        CheckBoxSection.this,
                        () -> ((JCheckBox) component).isSelected());
            }
        };
    }

    @Override
    protected TwoWayBinding createTwoWayBinding(Object data) {
        return new TwoWayBinding() {
            @Override
            public void propagateFromView() {
                CommonViewPropagators.CheckBox.propagateFromView((JCheckBox) component,
                        CheckBoxSection.this,
                        () -> ((JCheckBox) component).isSelected());
            }

            @Override
            public void propagateFromObject(Object initValue) {
                CommonViewPropagators.CheckBox.initView((JCheckBox) component, initValue);
                ((ProxyBinding) data).getValuePropagator().registerPropagation(fieldName, value -> ((JTextField) component).setText(value.toString()));
            }
        };
    }
}
