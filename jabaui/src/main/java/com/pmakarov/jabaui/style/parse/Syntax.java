package com.pmakarov.jabaui.style.parse;

import com.pmakarov.jabaui.style.parse.exception.JabaStyleSyntaxException;

import java.util.NoSuchElementException;

import static com.pmakarov.jabaui.style.objects.JCSSStyle.PSEUDO_PREFIX;

/**
 * @author pmakarov
 */
public class Syntax {

    public static void analyze(Tokenizer tokenizer) {
        new Analyzer(tokenizer).run();
    }

    public static class Analyzer {

        protected Tokenizer tokenizer;
        protected Token current;

        public Analyzer(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }

        /**
         * Run syntax analyze for current styles
         */
        private void run() {
            nextToken();
            while (null != current) {
                style();
            }
        }

        /**
         * Analyze style entry
         */
        private void style() {
            //TODO
            // use special token instead of TEXT for PSEUDO_PREFIX token
            if (currentTypeIn(TokenType.ONLY_LETTERS, TokenType.DOTTED_LETTERS, TokenType.TEXT)) {
                nextToken();
                switch (current.getType()) {
                    case OPEN_BRACE: {
                        nextToken();
                        styleBody();
                        if (null != current && current.getType() == TokenType.CLOSE_BRACE) {
                            nextToken();
                        } else {
                            createError("\"}\" expected");
                        }
                        break;
                    }
                    case OPEN_SQUARE_BRACKET: {
                        nextToken();
                        styleAttributes();
                        if (current.getType() == TokenType.OPEN_BRACE) {
                            nextToken();
                        } else {
                            createError("\"{\" expected");
                        }
                        styleBody();
                        if (null != current && current.getType() == TokenType.CLOSE_BRACE) {
                            nextToken();
                        } else {
                            createError("\"}\" expected");
                        }
                        break;
                    }
                    default:
                        createError("{ or [ expected");
                }
            } else {
                createError("Selector expected (should contain only letters, dotted separated words, or start with " + PSEUDO_PREFIX + ")");
            }
        }

        /**
         * Analyze style body
         */
        private void styleBody() {
            if (!currentTypeIn(TokenType.ONLY_LETTERS, TokenType.DASHED_WORD)) {
                createError("Property or selector expected");
            }
            //TODO
            // use special token instead of TEXT for PSEUDO_PREFIX token
            // probably need to add DOTTED_WORD
            while (currentTypeIn(TokenType.ONLY_LETTERS, TokenType.DASHED_WORD, TokenType.TEXT)) {
                nextToken();
                if (currentTypeIn(TokenType.COLON, TokenType.OPEN_BRACE, TokenType.OPEN_SQUARE_BRACKET)) {
                    if (current.getType() == TokenType.COLON) {
                        nextToken();
                        property();
                    } else {
                        previousToken();
                        style();
                    }
                } else {
                    createError("\"[\" or \"{\" or \":\" expected");
                }
            }
        }

        /**
         * Analyze property
         */
        private void property() {
            if (!currentTypeIn(TokenType.TEXT, TokenType.NUMBER, TokenType.ONLY_LETTERS)) {
                createError("Property value expected");
            }
            while (currentTypeIn(TokenType.TEXT, TokenType.NUMBER, TokenType.ONLY_LETTERS)) {
                nextToken();
            }
            if (current.getType() != TokenType.SEMICOLON) {
                createError("; expected");
            }
            nextToken();
        }

        /**
         * Analyze style attributes
         */
        private void styleAttributes() {
            if (currentTypeIn(TokenType.IDENTIFIER, TokenType.ONLY_LETTERS)) {
                nextToken();
                if (current.getType() == TokenType.EQUALS) {
                    nextToken();
                    if (currentTypeIn(TokenType.TEXT, TokenType.IDENTIFIER, TokenType.ONLY_LETTERS)) {
                        nextToken();
                        if (current.getType() == TokenType.CLOSE_SQUARE_BRACKET) {
                            nextToken();
                        } else {
                            createError("\"}\" expected");
                        }
                    } else {
                        createError("Attribute value expected");
                    }
                } else {
                    createError("\"=\" expected");
                }
            } else {
                createError("Attribute name expected");
            }
        }

        /**
         * Set current to next token
         */
        protected Token nextToken() {
            try {
                current = tokenizer.next();
            } catch (NoSuchElementException e) {
                current = null;
            }
            return current;
        }

        /**
         * Set current to previous token
         */
        protected Token previousToken() {
            try {
                current = tokenizer.previous();
            } catch (NoSuchElementException e) {
                current = null;
            }
            return current;
        }

        /**
         * Check through "or" type of current token
         *
         * @param tokenType array of probably types
         * @return boolean
         */
        protected boolean currentTypeIn(TokenType... tokenType) {
            boolean result = false;
            for (TokenType type : tokenType) {
                result |= current.getType() == type;
            }
            return result;
        }

        /**
         * Throws exception with passed error
         *
         * @param errorText error text
         */
        private void createError(String errorText) {
            throw new JabaStyleSyntaxException("Syntax analysis error at [" + current.getPosition().toString() + "] Error description: " + errorText);
        }
    }
}
