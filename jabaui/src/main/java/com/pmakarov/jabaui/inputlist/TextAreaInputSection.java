package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.bindings.OneWayBinding;
import com.pmakarov.jabaui.bindings.ProxyBinding;
import com.pmakarov.jabaui.bindings.TwoWayBinding;
import lombok.NoArgsConstructor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.*;

@NoArgsConstructor
public class TextAreaInputSection extends InputSection {

    private String syntax = null;

    public TextAreaInputSection(String label, Object data, String fieldName) {
        this(label, data, fieldName, null);
    }

    public TextAreaInputSection(String label, Object data, String fieldName, String syntax) {
        super(label, data, fieldName);
        this.syntax = syntax;
    }

    @Override
    public Component createComponent() {
        RSyntaxTextArea textArea = new RSyntaxTextArea(7, 60);
//        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
//        textArea.setCodeFoldingEnabled(true);
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setBackground(Color.YELLOW);
        return textArea;
    }

    @Override
    protected OneWayBinding createOneWayBinding() {
        return new OneWayBinding() {
            @Override
            public void init(Object value) {
                ((RSyntaxTextArea) component).setText(value.toString());
            }

            @Override
            public void registerOnChange() {
                CommonViewPropagators.textComponent((RSyntaxTextArea) component,
                        TextAreaInputSection.this,
                        () -> ((RSyntaxTextArea) component).getText());
            }
        };
    }

    @Override
    protected TwoWayBinding createTwoWayBinding(Object data) {
        return new TwoWayBinding() {
            @Override
            public void propagateFromView() {
                CommonViewPropagators.textComponent((RSyntaxTextArea) component,
                        TextAreaInputSection.this,
                        () -> ((RSyntaxTextArea) component).getText());
            }

            @Override
            public void propagateFromObject(Object initValue) {
                ((RSyntaxTextArea) component).setText(initValue.toString());
                ((ProxyBinding) data).getValuePropagator().registerPropagation(fieldName, value -> ((RSyntaxTextArea) component).setText(value.toString()));
            }
        };
    }
}
