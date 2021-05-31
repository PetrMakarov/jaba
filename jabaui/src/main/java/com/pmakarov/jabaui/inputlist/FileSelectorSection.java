package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.bindings.OneWayBinding;
import com.pmakarov.jabaui.bindings.ProxyBinding;
import com.pmakarov.jabaui.bindings.TwoWayBinding;
import lombok.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.util.Optional;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FileSelectorSection extends InputSection {

    private String title = "Choose file";
    private ExtensionFilter filter = ExtensionFilter.NONE;
    private String buttonText = "Browse";
    private Boolean enableInput = true;
    private Event beforeChoose;
    private Event afterChoose;

    private JTextField input;

    @Builder(toBuilder = true)
    public FileSelectorSection(String label, Object data, String fieldName, String title, String buttonText, Boolean enableInput, ExtensionFilter filter, Event beforeChoose, Event afterChoose) {
        super(label, data, fieldName);
        this.title = title;
        this.buttonText = buttonText;
        this.enableInput = enableInput;
        this.filter = filter;
        this.beforeChoose = beforeChoose;
        this.afterChoose = afterChoose;
    }

    @Override
    public Component createComponent() {
        JPanel component = new JPanel();
        component.setLayout(new GridBagLayout());
        JFileChooser jfc = createFileChooser();
        JTextField input = createInputField();
        this.input = input;
        input.setEnabled(this.enableInput);
        JButton browse = createButton(jfc, input);
        component.add(input, getLeftGBC());
        component.add(browse, getRightGBC());
        return component;
    }

    @Override
    protected OneWayBinding createOneWayBinding() {
        return new OneWayBinding() {
            @Override
            public void init(Object value) {
                input.setText(value.toString());
            }

            @Override
            public void registerOnChange() {
                CommonViewPropagators.textComponent(input,
                        FileSelectorSection.this,
                        () -> input.getText());
            }
        };
    }

    @Override
    protected TwoWayBinding createTwoWayBinding(Object data) {
        return new TwoWayBinding() {
            @Override
            public void propagateFromView() {
                CommonViewPropagators.textComponent(input,
                        FileSelectorSection.this,
                        () -> input.getText());
            }

            @Override
            public void propagateFromObject(Object initValue) {
                input.setText(initValue.toString());
                ((ProxyBinding) data).getValuePropagator().registerPropagation(fieldName, value -> input.setText(value.toString()));
            }
        };
    }

    /**
     * Create input field
     *
     * @return input field
     */
    private JTextField createInputField() {
        return new JTextField();
    }

    /**
     * Create button for choosing files
     *
     * @param jfc file chooser
     * @return button
     */
    private JButton createButton(JFileChooser jfc, JTextField input) {
        JButton button = new JButton(this.getButtonText());
        button.addActionListener(e -> {
            Optional.ofNullable(beforeChoose).ifPresent(event -> beforeChoose.emmit(null));
            int returnValue = jfc.showDialog(null, "Select");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                input.setText(jfc.getSelectedFile().getAbsolutePath());
            }
            Optional.ofNullable(afterChoose).ifPresent(event -> afterChoose.emmit(null));
        });
        return button;
    }

    /**
     * Create file chooser instance
     *
     * @return file chooser
     */
    private JFileChooser createFileChooser() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        jfc.setDialogTitle(this.getTitle());
        if (!this.getFilter().equals(ExtensionFilter.NONE)) {
            if (this.getFilter().equals(ExtensionFilter.DIRECTORY)) {
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            } else {
                jfc.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter =
                        new FileNameExtensionFilter(this.getFilter().getDescription(),
                                this.getFilter().getExtensions());
                jfc.addChoosableFileFilter(filter);
            }
        }
        return jfc;
    }

    /**
     * Style for input
     *
     * @return style
     */
    private GridBagConstraints getLeftGBC() {
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 2.0;
        return left;
    }

    /**
     * Style for choose
     *
     * @return style
     */
    private GridBagConstraints getRightGBC() {
        GridBagConstraints right = new GridBagConstraints();
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
        return right;
    }

    /**
     * Filter chooser extensions
     */
    @Getter
    public enum ExtensionFilter {
        XML("XML", "xml"),
        DIRECTORY("Directories only", (String) null),
        NONE(null, (String) null);

        private String description;
        private String[] extensions;

        ExtensionFilter(String description, String... extensions) {
            this.description = description;
            this.extensions = extensions;
        }

        public String getDescription() {
            return description;
        }

        public String[] getExtensions() {
            return extensions;
        }
    }
}
