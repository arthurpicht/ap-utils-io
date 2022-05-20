package de.arthurpicht.utils.io.assertions;

import java.nio.file.Path;

public class PathAssertionException extends RuntimeException{

    private final Path path;

    public PathAssertionException(Path path) {
        this.path = path;
    }

    public PathAssertionException(Path path, String message) {
        super(message);
        this.path = path;
    }

    public PathAssertionException(Path path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    public PathAssertionException(Path path, Throwable cause) {
        super(cause);
        this.path = path;
    }

    public PathAssertionException(Path path, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

}
