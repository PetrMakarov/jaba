package com.pmakarov.jabaui.boot.scope.proxy.exception;

public class JabaProxyCreationException extends Exception {
    public JabaProxyCreationException() {
    }

    public JabaProxyCreationException(String message) {
        super(message);
    }

    public JabaProxyCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaProxyCreationException(Throwable cause) {
        super(cause);
    }

    public JabaProxyCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
