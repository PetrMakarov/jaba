package com.pmakarov.jabaui.style.parse.exception;

/**
 * @author pmakarov
 */
public class JabaStyleLexicalException extends JabaStyleException {
    public JabaStyleLexicalException() {
    }

    public JabaStyleLexicalException(String message) {
        super(message);
    }

    public JabaStyleLexicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaStyleLexicalException(Throwable cause) {
        super(cause);
    }

    public JabaStyleLexicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
