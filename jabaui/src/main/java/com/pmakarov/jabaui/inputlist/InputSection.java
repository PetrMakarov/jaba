package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabahelper.JabaReflection;
import com.pmakarov.jabaui.bindings.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Optional;

/**
 * Input section class for components layout on panel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class InputSection {

    final static Logger logger = Logger.getLogger(InputSection.class);

    protected String label;
    protected Object data;
    protected String fieldName;
    protected Component component;
    private Binding binding;

    public InputSection(String label, Object data, String fieldName) {
        this(label, data, fieldName, null, null);
    }

    /**
     * Creates component and one way binding (via gui)
     *
     * @return componen instance
     */
    public Component create() {
        if (null == this.component) {
            this.component = createComponent();
            try {
                if (data instanceof ProxyBinding) {
                    binding = createTwoWayBinding(data);
                } else {
                    binding = createOneWayBinding();
                }
                if (null != binding) {
                    Object modelValue = Optional.ofNullable(this.getModelValue()).orElse("");
                    if (binding instanceof OneWayBinding) {
                        ((OneWayBinding) binding).init(modelValue);
                        ((OneWayBinding) binding).registerOnChange();
                    }
                    if (binding instanceof TwoWayBinding) {
                        ((TwoWayBinding) binding).propagateFromObject(modelValue);
                        ((TwoWayBinding) binding).propagateFromView();
                    }
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Cant getUserObject model value. Class" + data.getClass().getName() + ", Field: " + fieldName, e);
            }
        }
        return this.component;
    }

    /**
     * Getter of model attribute value
     *
     * @return value
     * @throws NoSuchFieldException   If no field exist in the model
     * @throws IllegalAccessException No access to model class
     */
    private Object getModelValue() throws NoSuchFieldException, IllegalAccessException {
        return JabaReflection.getValueByFieldName(data, fieldName);
    }

    /**
     * Create a swing component
     *
     * @return component
     */
    public abstract Component createComponent();

    /**
     * Crate a one way binding for component
     *
     * @return binding instance
     */
    OneWayBinding createOneWayBinding() {
        return null;
    }

    /**
     * Crate a two way binding for component
     *
     * @return binding instance
     */
    TwoWayBinding createTwoWayBinding(Object data) {
        return null;
    }

    /**
     * Class for hold default binding such as text field
     */
    public static class CommonViewPropagators {

        /**
         * Binding for text component
         *
         * @param textComponent component
         * @param section       section for bind ('this' exactly, in most cases)
         * @param getter        interface to getUserObject value of component
         */
        public static void textComponent(JTextComponent textComponent, InputSection section, ComponentValueGetter getter) {
            if (null != textComponent) {
                textComponent.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        try {
                            JabaReflection.setValueByFieldName(section.getData(), section.getFieldName(), getter.call());
                        } catch (NoSuchFieldException | IllegalAccessException ex) {
                            logger.error("Error create propagator binding on text component. Class: " + section.getData().getClass().getName() + " Field: " + section.getFieldName(), ex);
                        }
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        changedUpdate(e);
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        changedUpdate(e);
                    }
                });
            }
        }


        public static class CheckBox {

            public static void initView(JCheckBox checkBox, Object value) {
                if (!value.toString().isEmpty()) {
                    checkBox.setSelected(Boolean.parseBoolean(value.toString()));
                }
            }

            public static void propagateFromView(JCheckBox checkBox, InputSection section, ComponentValueGetter valueGetter) {
                if (null != checkBox) {
                    checkBox.addItemListener(e -> {
                        try {
                            JabaReflection.setValueByFieldName(valueGetter.call(), section.getFieldName(), checkBox.isSelected());
                        } catch (NoSuchFieldException | IllegalAccessException ex) {
                            logger.error("Error create propagator binding on checkbox. Class: " + section.getData().getClass().getName() + " Field: " + section.getFieldName(), ex);
                        }
                    });
                }
            }
        }
    }

}
