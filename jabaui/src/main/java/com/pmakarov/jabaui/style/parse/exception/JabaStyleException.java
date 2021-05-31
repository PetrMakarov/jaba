package com.pmakarov.jabaui.style.parse.exception;

/**
 * @author pmakarov
 */
public class JabaStyleException extends RuntimeException {
    public JabaStyleException() {
    }

    public JabaStyleException(String message) {
        super(message);
    }

    public JabaStyleException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaStyleException(Throwable cause) {
        super(cause);
    }

    public JabaStyleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
