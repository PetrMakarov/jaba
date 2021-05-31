package com.pmakarov.jabaui.boot;

/**
 * @author pmakarov
 */
public class JabaBootException extends RuntimeException {
    public JabaBootException() {
    }

    public JabaBootException(String message) {
        super(message);
    }

    public JabaBootException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaBootException(Throwable cause) {
        super(cause);
    }

    public JabaBootException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
