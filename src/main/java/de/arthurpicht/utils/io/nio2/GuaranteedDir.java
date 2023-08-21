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
        return guarantee(dir);
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
        return guarantee(dir);
    }

    /**
     * Guarantees that specified directory exists and is writeable. Directory and all parent directories will
     * be created if necessary.
     *
     * @param path directory
     */
    public static void check(Path path) {
        guarantee(path);
    }

    /**
     * Guarantees that specified directory exists and is writeable. The specification of directory is divided into
     * a parent directory and a subdirectory relative to parent. The specified parent directory  does not necessarily
     * have to be a direct parent. Specified subdirectories will be created is not existent.
     *
     * @param assertedParentDir a parent directory of specified directory that must exist and must be writable
     * @param subDir a subdirectory relative to specified parent directory that will be created if not preexisting.
     * @return the resulting path of guaranteed directory
     */
    public static Path assertedGet(Path assertedParentDir, Path subDir) {
        if (!FileUtils.isExistingDirectory(assertedParentDir))
            throw new GuaranteedDirException("Directory not found: [" + assertedParentDir.toAbsolutePath() + "].");
        if (!Files.isWritable(assertedParentDir))
            throw new GuaranteedDirException("Write rights missing for directory: " +
                    "[" + assertedParentDir.toAbsolutePath() + "].");
        Path dir = assertedParentDir.resolve(subDir);
        return guarantee(dir);
    }

    private static Path guarantee(Path dir) {
        if (FileUtils.isExistingDirectory(dir) && Files.isWritable(dir)) return dir;
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new GuaranteedDirException("Could not create directory [" + dir.toAbsolutePath() + "]. " +
                    "Cause: " + e.getMessage(), e);
        }
        return dir;
    }

}
