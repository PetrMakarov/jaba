package com.pmakarov.jabaui.boot.scope.exception;

public class JabaDependencyInjectException extends Exception {
    public JabaDependencyInjectException() {
    }

    public JabaDependencyInjectException(String message) {
        super(message);
    }

    public JabaDependencyInjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaDependencyInjectException(Throwable cause) {
        super(cause);
    }

    public JabaDependencyInjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
