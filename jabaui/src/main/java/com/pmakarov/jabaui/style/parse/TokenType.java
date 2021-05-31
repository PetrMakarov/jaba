/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmakarov.jabaui.style.parse;

/**
 * @author Petr
 */
public enum TokenType {
//    // selectors
//    SELECTOR, ATTRIBUTE_NAME, ATTRIBUTE_VALUE,
//    // properties content
//    PROPERTY, VALUE,
    ONLY_LETTERS, IDENTIFIER, DASHED_WORD, TEXT, NUMBER, DOTTED_LETTERS,
    // punctuation
    SEMICOLON, COLON,
    // borders
    OPEN_BRACE, CLOSE_BRACE, OPEN_SQUARE_BRACKET, CLOSE_SQUARE_BRACKET, QUOTE,
    // operators
    EQUALS,
    // comment
    COMMENT
}
