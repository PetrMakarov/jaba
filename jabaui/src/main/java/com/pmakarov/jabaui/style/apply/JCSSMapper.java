package com.pmakarov.jabaui.style.apply;

import com.pmakarov.jabahelper.PlainJabaHelper;
import com.pmakarov.jabahelper.JabaComponents;
import com.pmakarov.jabaui.boot.JabaBootException;
import com.pmakarov.jabaui.style.objects.JCSSStyle;
import com.pmakarov.jabaui.style.objects.ObjectMapper;
import com.pmakarov.jabaui.style.parse.JabaStyleParser;
import com.pmakarov.jabaui.style.parse.Tokenizer;
import com.pmakarov.jabaui.style.parse.exception.JabaStyleException;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author pmakarov
 * This class used for apply parsed styles to components
 */
public class JCSSMapper {

    private static final Logger LOGGER = Logger.getLogger(JCSSMapper.class);

    private static final ApplyersAccessor APPLYERS_ACCESSOR = ApplyersAccessor.getSingleton();
    /**
     * Tokenizer object
     */
    private Tokenizer tokenizer;
    /**
     * Parsed styles
     */
    private List<JCSSStyle> styles;
    /**
     * Object for store styles with different depth
     * For example
     * style1 {
     * style2{...}
     * ...
     * }
     * Then  {0 -> style1Obj, 1 -> style2Obj}
     */
    private Map<Integer, List<JCSSStyle>> styleDepthMap = new HashMap<>();


    public JCSSMapper() {
        try {
            tokenizer = JabaStyleParser.parse();
            styles = new ObjectMapper(tokenizer).extractStyles();
            assignDepth(styles, 0, 0);
        } catch (JabaStyleException e) {
            throw new JabaBootException("Style parsing error", e);
        } catch (IOException e) {
            LOGGER.info("No style are provided in the app");
            styles = null;
        }
    }

    /**
     * Apply styles to given component cascade
     *
     * @param component component to apply style to
     */
    public void applyStyles(Component component) {
        if (null != styles) {
            int index = 0;
            // object stores component that was styled one step ago (depth - 1)
            Map<String, List<Component>> applyedStylesComponents = new HashMap<>();
            while (null != styleDepthMap.get(index)) {
                for (JCSSStyle style : styleDepthMap.get(index)) {
                    // for depth > 0
                    if (null != style.getParentStyle()) {
                        applyedStylesComponents.get(getStyleSelectorPath(style.getParentStyle()))
                                .forEach(applyedComponent -> {
                                    applyForComponent(applyedComponent, style, applyedStylesComponents);
                                });
                    } else {
                        // for depth == 0
                        applyForComponent(component, style, applyedStylesComponents);
                    }
                }
                index++;
            }
        }
    }

    /**
     * Apply given style to current component cascade
     *
     * @param component               given component
     * @param style                   given style
     * @param applyedStylesComponents object stores component that was styled one step ago (depth - 1)
     */
    private void applyForComponent(Component component, JCSSStyle style, Map<String, List<Component>> applyedStylesComponents) {
        JabaComponents.performDeep(component, nested -> {
            applyAction(nested, style, applyedStylesComponents);
        });
    }

    /**
     * Test if current style can be applyed to current component and if so apply all properties
     *
     * @param component              given component
     * @param style                  given style
     * @param applyedStylesComponent object stores component that was styled one step ago (depth - 1)
     */
    private void applyAction(Component component, JCSSStyle style, Map<String, List<Component>> applyedStylesComponent) {
        for (StyleApplyer<Component> styleApplyer : APPLYERS_ACCESSOR.getList()) {
            if (styleApplyer.match(style, component)) {
                styleApplyer.apply(style, component);
                PlainJabaHelper.addToListInMap(applyedStylesComponent, getStyleSelectorPath(style), component);
            }
        }
    }

    /**
     * Create string contains style path (e.g parentParentStyleSelector.parentStyleSelector.currentStyleSelector)
     *
     * @param style given style
     * @return style path
     */
    private String getStyleSelectorPath(JCSSStyle style) {
        List<String> result = new ArrayList<String>();
        result.add(style.getSelector());
        while (null != style.getParentStyle()) {
            result.add(style.getParentStyle().getSelector());
            style = style.getParentStyle();
        }
        Collections.reverse(result);
        return String.join(".", result);
    }

    /**
     * Spread style to separated depth levels
     *
     * @param styles style to process
     * @param depth  initial depth
     * @param index  start list index
     */
    private void assignDepth(List<JCSSStyle> styles, Integer depth, Integer index) {
        if (index < styles.size()) {
            styles.get(index).setDepth(depth);
            PlainJabaHelper.addToListInMap(styleDepthMap, depth, styles.get(index));
            if (null != styles.get(index).getStyleBody().getNestedStyles()
                    && styles.get(index).getStyleBody().getNestedStyles().size() > 0) {
                assignDepth(styles.get(index).getStyleBody().getNestedStyles(), depth + 1, 0);
            }
            assignDepth(styles, depth, index + 1);
        }
    }

}
