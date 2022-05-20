package de.arthurpicht.utils.io.assertions;

import de.arthurpicht.utils.io.nio2.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class PathAssertions {

    /**
     * Asserts that the file denoted by a specified path instance equals to the specified file name.
     *
     * @param file
     * @param fileName
     */
    public static void assertFileName(Path file, String fileName) {
        if (!FileUtils.isPathEndingWithFileName(file, fileName))
            throw new PathAssertionException(file, "Assertion failed. File name [" + fileName + "] expected " +
                    "but is [" + file.getFileName() + "].");
    }

    public static void assertIsExistingDirectory(Path dir) {
        if (!FileUtils.isExistingDirectory(dir))
            throw new PathAssertionException(dir, "Assertion failed. Existing directory expected: ["
                    + dir.toAbsolutePath() + "].");
    }

    public static void assertPathNotExisting(Path path) {
        if (Files.exists(path))
            throw new PathAssertionException(path, "Assertion failed. Path as not existing expected : ["
                    + path.toAbsolutePath() + "].");
    }

    public static void assertIsDirectSubdirectory(Path referenceDir, Path subDir) {
        if (!FileUtils.isDirectSubdirectory(referenceDir, subDir))
            throw new PathAssertionException(subDir, "Assertion failed. Directory [" + referenceDir.toAbsolutePath() + "] " +
                    "does not contain [" + subDir.toAbsolutePath() + "].");
    }

}
