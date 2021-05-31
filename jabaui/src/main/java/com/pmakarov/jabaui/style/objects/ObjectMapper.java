package com.pmakarov.jabaui.style.objects;

import com.pmakarov.jabaui.style.parse.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pmakarov.jabaui.style.objects.JCSSStyle.PSEUDO_PREFIX;

/**
 * @author pmakarov
 */
public class ObjectMapper extends Syntax.Analyzer {

    public ObjectMapper(Tokenizer tokenizer) {
        super(tokenizer);
    }

    public List<JCSSStyle> extractStyles() {
        if (null != tokenizer) {
            List<JCSSStyle> styles = new ArrayList<>();
            tokenizer.reset();
            while (tokenizer.hasNext()) {
                styles.add(createStyle());
            }
            return styles;
        }
        return null;
    }

    private JCSSStyle createStyle() {
        nextToken();// selector
        JCSSStyle style = new JCSSStyle();
        style.setSelector(current.getValue());
        nextToken(); // { or [
        if (currentTypeIn(TokenType.OPEN_SQUARE_BRACKET)) {
            style.setStyleAttribute(createStyleAttribute());
            nextToken(); // {
        }
        style.setStyleBody(createStyleBody(style));
        return style;
    }

    private JCSSStyleAttribute createStyleAttribute() {
        nextToken(); // attribute name
        JCSSStyleAttribute styleAttribute = new JCSSStyleAttribute();
        styleAttribute.setName(current.getValue());
        nextToken(); // =(equals)
        nextToken(); // attribute vale
        styleAttribute.setValue(current.getValue());
        nextToken(); // ]
        return styleAttribute;
    }

    private JCSSStyleBody createStyleBody(JCSSStyle parentStyle) {
        JCSSStyleBody styleBody = new JCSSStyleBody();
        nextToken(); // selector or property
        while (!currentTypeIn(TokenType.CLOSE_BRACE)) {
            String selectorOrProperty = current.getValue();
            nextToken(); // : or {
            if (currentTypeIn(TokenType.COLON)) {
                styleBody.getProperties().add(createProperty(selectorOrProperty));
            } else {
                previousToken();
                previousToken();
                JCSSStyle childStyle = createStyle();
                if (childStyle.getSelector().startsWith(PSEUDO_PREFIX)) {
                    styleBody.getPseudoClasses().add(childStyle);
                } else {
                    styleBody.getNestedStyles().add(childStyle);
                    childStyle.setParentStyle(parentStyle);
                }
            }
            nextToken();
        }
        return styleBody;
    }

    private JCSSProperty createProperty(String name) {
        JCSSProperty property = new JCSSProperty();
        property.setName(name);
        List<Token> values = new ArrayList<>();
        nextToken();
        while (!currentTypeIn(TokenType.SEMICOLON)) {
            values.add(current); // property value
            nextToken();
        }
        property.setValue(values.stream().map(Token::getValue).collect(Collectors.joining(" ")));
//        nextToken(); // ;
        return property;
    }
}
