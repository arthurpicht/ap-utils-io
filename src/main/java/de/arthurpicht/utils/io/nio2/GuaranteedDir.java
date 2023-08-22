package de.arthurpicht.utils.io.nio2;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A functionality that guarantees, that specified directories are existing and writable. If specified directory
 * is not existing, it will be created.
 */
public class GuaranteedDir {

    public static class GuaranteedDirException extends RuntimeException {
        public GuaranteedDirException(String message, Throwable cause) {
            super(message, cause);
        }

        public GuaranteedDirException(String message) {
            super(message);
        }
    }

    /**
     * Guarantees that specified directory exists and is writeable. Directory and all parent directories will
     * be created if necessary.
     *
     * @param first the path string of directory or initial part of the path string of directory
     * @param more additional strings to be joined to form the path string
     * @return the resulting path of guaranteed directory
     */
    public static Path get(String first, String... more) {
        Path dir = Paths.get(first, more);
        guarantee(dir);
        return dir;
    }

    /**
     * Guarantees that specified directory exists and is writeable. Directory and all parent directories will
     * be created if necessary.
     *
     * @param uri directory
     * @return the resulting path of guaranteed directory
     */
    public static Path get(URI uri) {
        Path dir = Paths.get(uri);
        guarantee(dir);
        return dir;
    }

    /**
     * Guarantees that specified directory exists and is writeable. The first part of the path is expected as existent.
     * Directory denoted by second part is created if not preexisting including all parent levels.
     *
     * @param assertedFirst a parent directory of specified directory that must exist and must be writable
     * @param second a subdirectory relative to specified parent directory that will be created if not preexisting.
     * @return the resulting path of guaranteed directory
     */
    public static Path getAsSubdir(Path assertedFirst, Path second) {
        if (!FileUtils.isExistingDirectory(assertedFirst))
            throw new GuaranteedDirException("Directory not found: [" + assertedFirst.toAbsolutePath() + "].");
        if (!Files.isWritable(assertedFirst))
            throw new GuaranteedDirException("Write rights missing for directory: " +
                    "[" + assertedFirst.toAbsolutePath() + "].");
        Path dir = assertedFirst.resolve(second);
        guarantee(dir);
        return dir;
    }

    /**
     * Guarantees that specified directory exists and is writeable. Directory and all parent directories will
     * be created if necessary.
     *
     * @param dir directory
     */
    public static void guarantee(Path dir) {
        if (FileUtils.isExistingDirectory(dir) && Files.isWritable(dir)) return;
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new GuaranteedDirException("Could not create directory [" + dir.toAbsolutePath() + "]. " +
                    "Cause: " + e.getMessage(), e);
        }
    }

}
