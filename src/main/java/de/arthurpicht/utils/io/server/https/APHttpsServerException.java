package de.arthurpicht.utils.io.server.https;

public class APHttpsServerException extends Exception {

    public APHttpsServerException() {
    }

    public APHttpsServerException(String message) {
        super(message);
    }

    public APHttpsServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public APHttpsServerException(Throwable cause) {
        super(cause);
    }

    public APHttpsServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
