package com.pmakarov.jabaui.inputlist;

import com.pmakarov.jabaui.dropdown.EditableDropDown;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

@NoArgsConstructor
public class DropDownSection<T> extends InputSection {

    private Class<?> panelClass;
    private Class<T> dataClass;
    private List<T> data;
    private Event onUserChoose;

    @Builder(toBuilder = true)
    public DropDownSection(String label, List<T> data, Class<T> dataClass, Class<?> panelClass, Event onUserChoose) {
        super(label, null, null);
        this.data = data;
        this.panelClass = panelClass;
        this.dataClass = dataClass;
        this.onUserChoose = onUserChoose;
    }

    @Override
    public Component createComponent() {
        return new EditableDropDown<>(panelClass, dataClass, data, onUserChoose);
    }
}
