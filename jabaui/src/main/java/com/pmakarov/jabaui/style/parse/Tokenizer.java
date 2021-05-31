package com.pmakarov.jabaui.style.parse;

/**
 * @author pmakarov
 */
public interface Tokenizer {
    /**
     * Iterator is not in the end
     *
     * @return boolean
     */
    boolean hasNext();

    /**
     * Iterator is not in the beginning
     *
     * @return boolean
     */
    boolean hasPrevious();

    /**
     * Return next item
     *
     * @return Token
     */
    Token next();

    /**
     * Return previous item
     *
     * @return Token
     */
    Token previous();

    /**
     * Count of items
     *
     * @return long
     */
    long count();

    /**
     * Reset increment index
     */
    void reset();

}
