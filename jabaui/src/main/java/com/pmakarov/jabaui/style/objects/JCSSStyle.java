package com.pmakarov.jabaui.style.objects;

import lombok.Data;

/**
 * @author pmakarov
 */
@Data
public class JCSSStyle {

    public static final String PSEUDO_PREFIX = "_@";

    private String selector;
    private JCSSStyleAttribute styleAttribute;
    private JCSSStyleBody styleBody;
    private JCSSStyle parentStyle;
    private int depth;
}
