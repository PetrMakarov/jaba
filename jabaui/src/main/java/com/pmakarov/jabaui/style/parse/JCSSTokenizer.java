package com.pmakarov.jabaui.style.parse;

import com.pmakarov.jabaui.style.parse.exception.JabaStyleLexicalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pmakarov
 * Lexical analyzer for jcss styles
 */
public class JCSSTokenizer implements Tokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JCSSTokenizer.class);

    /**
     * Context used in token classifying
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private static class ClassifyContext {
        private String previous;
        private String current;
        private String next;

        private ClassifyContext(String[] tokens, int index) {
            this.current = tokens[index];
            if (index > 0) {
                this.previous = tokens[index - 1];
            }
            if (index < tokens.length - 1) {
                this.next = tokens[index + 1];
            }
        }
    }

    /**
     * Direction of incrementing tokens
     */
    private enum IncrementDirection {

        FORWARD(2, 1),
        BACKWARD(-2, -1);

        /**
         * Increment by this value while skipping token if its type is in exclude list
         */
        int onSkipExclude;

        /**
         * Increment index by this value if direction of increment has changed
         */
        int onChangeDirection;

        IncrementDirection(int onChangeDirection, int onSkipExclude) {
            this.onChangeDirection = onChangeDirection;
            this.onSkipExclude = onSkipExclude;
        }
    }

    private IncrementDirection currentDirection = IncrementDirection.FORWARD;

    /**
     * Use this sequence of symbols to help split by whitespace
     */
    private static final String WHITESPACE_REPLACEMENT = "@#%";

    /**
     * Regexp
     */
    private static final String TEXT_IN_QUOTES_REGEX = "'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"";
    private static final String IDENTIFIER_REGEX = "^[a-zA-Z_][a-zA-Z_0-9]*";
    private static final String ONLY_LETTERS_REGEX = "[a-zA-Z]+";
    /**
     * Patterns
     */
    private static final Pattern TEXT_IN_QUOTES_PATTERN = Pattern.compile(TEXT_IN_QUOTES_REGEX);
    private static final Pattern COMMENTS_PATTERN = Pattern.compile("(?m)//.*$");

    /**
     * Map contains predicate to test against for determine token type
     */
    private static final Map<TokenType, Predicate<ClassifyContext>> TOKEN_TYPE_PREDICATE_MAP = new LinkedHashMap<TokenType, Predicate<ClassifyContext>>() {{

        put(TokenType.ONLY_LETTERS, classifyContext -> classifyContext.getCurrent().matches("[a-zA-Z]+"));

        put(TokenType.DOTTED_LETTERS, classifyContext -> classifyContext.getCurrent().matches("[a-zA-Z]+(\\.{1}[a-zA-Z]+)+"));

        put(TokenType.IDENTIFIER, classifyContext -> classifyContext.getCurrent().matches("^[a-zA-Z_][a-zA-Z_0-9]*"));

        put(TokenType.DASHED_WORD, classifyContext -> classifyContext.getCurrent().matches("^[a-z][a-z-]*"));

        put(TokenType.NUMBER, classifyContext -> classifyContext.getCurrent().matches("[0-9]+"));

        put(TokenType.SEMICOLON, classifyContext -> ";".equals(classifyContext.getCurrent()));

        put(TokenType.COLON, classifyContext -> ":".equals(classifyContext.getCurrent()));

        put(TokenType.OPEN_BRACE, classifyContext -> "{".equals(classifyContext.getCurrent()));

        put(TokenType.CLOSE_BRACE, classifyContext -> "}".equals(classifyContext.getCurrent()));

        put(TokenType.OPEN_SQUARE_BRACKET, classifyContext -> "[".equals(classifyContext.getCurrent()));

        put(TokenType.CLOSE_SQUARE_BRACKET, classifyContext -> "]".equals(classifyContext.getCurrent()));

        put(TokenType.QUOTE, classifyContext -> "\"".equals(classifyContext.getCurrent()));

        put(TokenType.EQUALS, classifyContext -> "=".equals(classifyContext.getCurrent()));

        put(TokenType.COMMENT, classifyContext -> classifyContext.getCurrent().matches("^//.*$"));

        put(TokenType.TEXT, classifyContext -> true);
    }};

    /**
     * Filter on iterating
     */
    private static final Set<TokenType> exclude = new HashSet<TokenType>() {{
        add(TokenType.COMMENT);
    }};

    private String content;
    private String preparedContent;
    private String[] stringTokens;
    private List<Token> tokens;

    private int index = 0;

    public JCSSTokenizer(String content) {
        this.content = content;
        preparedContent = prepareContent(content);
        stringTokens = processStringTokens(preparedContent);
        tokens = classifyTokens(stringTokens);
        if (stringTokens.length != tokens.size()) {
            throw new JabaStyleLexicalException("Lexical analysis error: cannot parse all tokens");
        }
        determinePositions(tokens);
        LOGGER.debug("Styles tokens: {}", tokens);
    }

    @Override
    public boolean hasNext() {
        return index < tokens.size();
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public Token next() {
        changeDirectionOnNeeded(IncrementDirection.FORWARD);
        if (!hasNext()) {
            throw new NoSuchElementException("No element at index " + index + ".");
        }
        return tokens.get(index++);
    }

    @Override
    public Token previous() {
        changeDirectionOnNeeded(IncrementDirection.BACKWARD);
        if (!hasPrevious()) {
            throw new NoSuchElementException("Already at start of list.");
        }
        return tokens.get(index--);
    }

    @Override
    public long count() {
        return tokens.size();
    }

    @Override
    public void reset() {
        index = 0;
    }

    /**
     * Change iteration direction
     *
     * @param incrementDirection new direction
     */
    private void changeDirectionOnNeeded(IncrementDirection incrementDirection) {
        if (currentDirection != incrementDirection) {
            currentDirection = incrementDirection;
            index += currentDirection.onChangeDirection;
        }
        skipExcluded();
    }

    /**
     * Skip token if its type is in exclude list
     */
    private void skipExcluded() {
        if (index > 0 && index < tokens.size() && exclude.contains(tokens.get(index).getType())) {
            index += currentDirection.onSkipExclude;
        }
    }

    /**
     * Prepare content to tokenize process
     *
     * @param content just content string
     * @return prepared content string
     */
    private String prepareContent(String content) {
        content = replaceInsideCommentOrQuotes(content, " ", WHITESPACE_REPLACEMENT);
        return content.replaceAll("\\[|]|\\{|}|:|;|=", " $0 ");
    }

    /**
     * Return string tokens by prepared content string
     *
     * @param preparedContent input
     * @return tokens
     */
    private String[] processStringTokens(String preparedContent) {
        String[] stringTokens = preparedContent.trim().split("\\s+");
        for (int i = 0; i < stringTokens.length; i++) {
            if (stringTokens[i].contains(WHITESPACE_REPLACEMENT)) {
                stringTokens[i] = stringTokens[i].replaceAll(WHITESPACE_REPLACEMENT, " ");
            }
        }
        return stringTokens;
    }

    /**
     * Replace string inside comment block or between quotes
     *
     * @param input      input string
     * @param replaceble string to replace
     * @param alternate  replacement string
     * @return edited string
     */
    private String replaceInsideCommentOrQuotes(String input, String replaceble, String alternate) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = TEXT_IN_QUOTES_PATTERN.matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().replaceAll(replaceble, alternate));
        }
        Integer lastQouteIndex = input.lastIndexOf("\"");
        String str = new StringBuilder(input).substring(lastQouteIndex + 1);
        String boundaryQuotes = sb.append(str).toString();
        sb = new StringBuffer();
        matcher = COMMENTS_PATTERN.matcher(boundaryQuotes);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().replaceAll(replaceble, alternate));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Classify string tokens to types
     *
     * @param stringTokens just string tokens
     * @return classified tokens
     */
    private List<Token> classifyTokens(String[] stringTokens) {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < stringTokens.length; i++) {
            String currentToken = stringTokens[i];
            for (Map.Entry<TokenType, Predicate<ClassifyContext>> entry : TOKEN_TYPE_PREDICATE_MAP.entrySet()) {
                TokenType type = entry.getKey();
                Predicate<ClassifyContext> predicate = entry.getValue();
                if (predicate.test(new ClassifyContext(stringTokens, i))) {
                    tokens.add(new Token(type, currentToken));
                    break;
                }
            }
        }
        return tokens;
    }

    /**
     * Find and set position of tokens in text
     *
     * @param tokens list of tokens
     */
    private void determinePositions(List<Token> tokens) {
        String[] lines = this.content.split("\n");
        Map<String, Token.Position> helperMap = createHelperMap(tokens);
        // определяем позицию каждого токена
        tokeniterate:
        for (Token token : tokens) {
            String value = token.getValue();
            // вспомогательное значение позиции для токена
            Token.Position oldPosition = helperMap.get(value);
            // поиск в каждой строке текста
            for (int i = 0; i < lines.length; i++) {
                String currentLine = lines[i];
                // отсекаем предыдущие строки, где уже был подобный токен
                if (i >= oldPosition.getLine()) {
                    // поиск по строке
                    for (int index = currentLine.indexOf(value); index >= 0; index = currentLine.indexOf(value, index + 1)) {
                        // если елемент найден
                        if (index > -1) {
                            // и в этой строке уже есть такой же токен
                            if (i == oldPosition.getLine()) {
                                // то проверяем, что это не тот же самый токен, а следующий
                                if (index > oldPosition.getColumn()) {
                                    // если нашли следующий токен, то записываем значения позиций
                                    token.setPosition(new Token.Position(i + 1, index + 1));
                                    helperMap.put(value, new Token.Position(i, index));
                                    continue tokeniterate;
                                }
                                // если нашли такой же токен не в текущей, а в последующих строках, то просто записываем значение позиции
                            } else {
                                token.setPosition(new Token.Position(i + 1, index + 1));
                                helperMap.put(value, new Token.Position(i, index));
                                continue tokeniterate;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper map to find tokens positions
     *
     * @return helper map
     */
    private Map<String, Token.Position> createHelperMap(List<Token> tokens) {
        Map<String, Token.Position> helperMap = new HashMap();
        for (Token token : tokens) {
            helperMap.put(token.getValue(), new Token.Position());
        }
        return helperMap;
    }
}
