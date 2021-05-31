package com.pmakarov.jabaui.style.objects;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pmakarov
 */
@Data
public class JCSSStyleBody {
    private List<JCSSProperty> properties = new ArrayList<>();
    @ToString.Exclude
    private List<JCSSStyle> nestedStyles = new ArrayList<>();
    private List<JCSSStyle> pseudoClasses = new ArrayList<>();
}
