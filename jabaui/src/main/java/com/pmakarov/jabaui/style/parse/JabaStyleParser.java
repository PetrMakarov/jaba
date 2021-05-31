package com.pmakarov.jabaui.style.parse;

import com.pmakarov.jabaresource.JabaResource;
import com.pmakarov.jabaui.style.parse.exception.JabaStyleException;

import java.io.IOException;
import java.util.Objects;

/**
 * @author pmakarov
 */
public final class JabaStyleParser {

    private JabaStyleParser() {
    }

    public static Tokenizer parse() throws IOException {
        try {
            String content = JabaResource.getReader().fileContent("style.jcss");
            checkForBordersMatching(content);
            Tokenizer jcssTokenizer = new JCSSTokenizer(content);
            Syntax.analyze(jcssTokenizer);
            return jcssTokenizer;
        } catch (NullPointerException e) {
            throw new JabaStyleException("Error load styles", e);
        }
    }

    /**
     * Check content for missing "borders" (braces, bracket, quotes)
     *
     * @param content styles content
     * @throws JabaStyleException exception description for missing element
     */
    private static void checkForBordersMatching(String content) {
        long quotesCount = findCharCount(content, '"');
        if (quotesCount % 2 != 0) {
            throw new JabaStyleException("Missing \" character");
        }
        long openBraceCount = findCharCount(content, '{');
        long closeBraceCount = findCharCount(content, '}');
        if (openBraceCount != closeBraceCount) {
            throw new JabaStyleException("Miscount for {} characters");
        }
        long openSquareBracketCount = findCharCount(content, '[');
        long closeSquareBracketCount = findCharCount(content, ']');
        if (openSquareBracketCount != closeSquareBracketCount) {
            throw new JabaStyleException("Miscount for [] characters");
        }
    }

    /**
     * Just for find any character count in string
     *
     * @param content   input string
     * @param character character for count
     * @return character count
     */
    private static long findCharCount(String content, int character) {
        return content.chars().filter(value -> Objects.equals(value, character)).count();
    }
}
