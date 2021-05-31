package com.pmakarov.jabaui.boot.scope.exception;

public class JabaScopeInitException extends Exception {

    public JabaScopeInitException() {
    }

    public JabaScopeInitException(String message) {
        super(message);
    }

    public JabaScopeInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabaScopeInitException(Throwable cause) {
        super(cause);
    }

    public JabaScopeInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
