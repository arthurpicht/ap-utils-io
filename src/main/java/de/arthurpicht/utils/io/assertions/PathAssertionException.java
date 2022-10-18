package de.arthurpicht.utils.io.assertions;

import java.nio.file.Path;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class PathAssertionException extends RuntimeException {

    private final Path path;

    public PathAssertionException(Path path, String message) {
        super(message);
        assertArgumentNotNull("path", path);
        assertArgumentNotNull("message", message);
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public String getMessage() {
        return "Assertion failed. " + super.getMessage();
    }

    public String getShortMessage() {
        return super.getMessage();
    }

}
