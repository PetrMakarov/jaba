package com.pmakarov.jabaui.style.apply;

import com.pmakarov.jabaui.boot.CDIService;
import com.pmakarov.jabaui.style.objects.JCSSProperty;
import com.pmakarov.jabaui.style.objects.JCSSStyle;
import com.pmakarov.jabaui.style.objects.JCSSStyleAttribute;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Base class for apply
 *
 * @param <T>
 */
public abstract class StyleApplyer<T extends Component> {

    @Getter
    protected Map<String, PropertyApplyer<T>> availableProperties = new HashMap<>();

    @Getter
    protected Map<String, PseudoApplyer<T>> availablePseudos = new HashMap<>();

    /**
     * TODO выпилить и оставить деволтный геттер lombok
     * Abstract getter for access custom predicate for component->style matching
     *
     * @return predicate instance
     */
    protected abstract BiPredicate<JCSSStyle, T> predicate();

    public void apply(JCSSStyle style, T component) {
        // style properties
        for (JCSSProperty styleProperty : style.getStyleBody().getProperties()) {
            PropertyApplyer<T> propertyApplyer = getAvailableProperties().get(styleProperty.getName());
            if (null != propertyApplyer) {
                propertyApplyer.apply(component, styleProperty);
            }
        }
        // style pseudo classes
        for (JCSSStyle pseudoStyle : style.getStyleBody().getPseudoClasses()) {
            PseudoApplyer<T> pseudoApplyer = getAvailablePseudos().get(pseudoStyle.getSelector());
            if (null != pseudoApplyer) {
                pseudoApplyer.apply(pseudoStyle, component);
            }
        }
    }

    public Boolean match(JCSSStyle style, T component) {
        return predicate().test(style, component);
    }

    /**
     * Predicate for component class and style selector equality
     *
     * @param clazz explicitly defined class
     * @return predicate instance
     */
    protected BiPredicate<JCSSStyle, T> componentIsInstanceOfPredicate(Class<T> clazz) {
        return (style, component) -> clazz.isAssignableFrom(component.getClass());
    }

    /**
     * Predicate for component class name and style selector equality
     *
     * @return predicate instance
     */
    protected BiPredicate<JCSSStyle, T> componentClassNamePredicate() {
        return (style, component) -> {
            String proxyName = style.getSelector();
            Class<?> proxy = CDIService.getProxy(style.getSelector());
            if (null != proxy) {
                proxyName = proxy.getName();
            }
            //TODO getSimpleName or from mapping
            return component.getClass().getName().equals(proxyName) || component.getClass().getSimpleName().equals(proxyName);
        };
    }

    /**
     * Predicate for component name and style attribute "name" equality
     *
     * @return predicate instance
     */
    protected BiPredicate<JCSSStyle, T> componentNameAttributePredicate() {
        return (style, component) -> {
            boolean attributeNameMatching = true;
            JCSSStyleAttribute attribute = style.getStyleAttribute();
            if (null != attribute) {
                String attributeName = attribute.getName();
                if ("name".equals(attributeName)) {
                    String attributeValue = (String) attribute.getValue();
                    String componentName = component.getName();
                    if (!attributeValue.equals(componentName)) {
                        attributeNameMatching = false;
                    }
                } else {
                    attributeNameMatching = false;
                }
            }
            return attributeNameMatching;
        };
    }

    /**
     * Predicate for component(JComponent) client property and style attribute "not name" equality
     *
     * @return predicate instance
     */
    protected BiPredicate<JCSSStyle, T> componentCustomAttributePredicate() {
        return (style, component) -> {
            boolean attributePropertyMatching = true;
            JCSSStyleAttribute attribute = style.getStyleAttribute();
            if (null != attribute) {
                if (component instanceof JComponent) {
                    String attributeName = attribute.getName();
                    if (!"name".equals(attributeName)) {
                        String attributeValue = (String) attribute.getValue();
                        JComponent jComponent = (JComponent) component;
                        if (null != jComponent.getClientProperty(attributeName)) {
                            String componentProperty = jComponent.getClientProperty(attributeName).toString();
                            if (!attributeValue.equals(componentProperty)) {
                                attributePropertyMatching = false;
                            }
                        } else {
                            attributePropertyMatching = false;
                        }
                    } else {
                        attributePropertyMatching = false;
                    }
                } else {
                    attributePropertyMatching = false;
                }
            }
            return attributePropertyMatching;
        };
    }

    /**
     * Predicate for matching component and style
     *
     * @param clazz explicitly defined class
     * @return predicate instance
     */
    protected BiPredicate<JCSSStyle, T> componentStylePredicate(Class<T> clazz) {
        return componentIsInstanceOfPredicate(clazz).and(componentClassNamePredicate()).and(componentNameAttributePredicate().or(componentCustomAttributePredicate()));
    }

}
