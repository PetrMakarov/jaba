package com.pmakarov.jabaui.style.parse.exception;

/**
 * @author pmakarov
 */
public class JabaStyleSyntaxException extends JabaStyleException {
    public JabaStyleSyntaxException() {
    }

    public JabaStyleSyntaxException(String message) {
        super(message);
    }

    public JabaStyleSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaStyleSyntaxException(Throwable cause) {
        super(cause);
    }

    public JabaStyleSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
