package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.bindings.OneWayBinding;
import com.pmakarov.jabaui.bindings.ProxyBinding;
import com.pmakarov.jabaui.bindings.TwoWayBinding;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpinnerNumberInputSection extends InputSection {

    private Number initial;
    private int min;
    private int max;
    private Number step;

    @Builder(toBuilder = true)
    public SpinnerNumberInputSection(String label, Object data, String fieldName, Number initial, int min, int max, Number step) {
        super(label, data, fieldName);
        this.initial = initial;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public Component createComponent() {
        return new JSpinner(new SpinnerNumberModel(this.getInitial(),
                this.getMin(),
                this.getMax(),
                this.getStep()
        ));
    }

    @Override
    protected OneWayBinding createOneWayBinding() {
        return new OneWayBinding() {
            @Override
            public void init(Object value) {
                ((JSpinner) component).getModel().setValue(value);
            }

            @Override
            public void registerOnChange() {
                CommonViewPropagators.textComponent(((JSpinner.DefaultEditor) ((JSpinner) component).getEditor()).getTextField(),
                        SpinnerNumberInputSection.this,
                        () -> ((JSpinner) component).getValue());
            }
        };
    }

    @Override
    protected TwoWayBinding createTwoWayBinding(Object data) {
        return new TwoWayBinding() {
            @Override
            public void propagateFromView() {
                CommonViewPropagators.textComponent(((JSpinner.DefaultEditor) ((JSpinner) component).getEditor()).getTextField(),
                        SpinnerNumberInputSection.this,
                        () -> ((JSpinner) component).getValue());
            }

            @Override
            public void propagateFromObject(Object initValue) {
                ((JSpinner) component).getModel().setValue(initValue);
                ((ProxyBinding) data).getValuePropagator().registerPropagation(fieldName, value -> ((JSpinner) component).getModel().setValue(value));
            }
        };
    }
}
