package de.arthurpicht.utils.io.assertions;

import de.arthurpicht.utils.io.nio2.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class PathAssertions {

    /**
     * Asserts that the file denoted by a specified path instance equals to the specified file name.
     * Works for path objects in general including regular files and directories. Specified path object is not
     * expected to be existing.
     *
     * @param path path representation of file
     * @param fileName file name
     * @throws PathAssertionException if specified fileName does not match specified path
     * @throws IllegalArgumentException if one of the specified arguments is null
     *
     */
    public static void assertFileName(Path path, String fileName) {
        assertArgumentNotNull("path", path);
        assertArgumentNotNull("fileName", fileName);
        if (!FileUtils.isPathEndingWithFileName(path, fileName))
            throw new PathAssertionException(path, "File name [" + fileName + "] expected " +
                    "but is [" + path.getFileName() + "].");
    }

    /**
     * Asserts that the specified directory is existing in file system.
     *
     * @param dir directory to be checked
     * @throws PathAssertionException if specified directory is not existing in file system
     * @throws IllegalArgumentException if specified directory is null
     */
    public static void assertIsExistingDirectory(Path dir) {
        assertArgumentNotNull("dir", dir);
        if (!FileUtils.isExistingDirectory(dir))
            throw new PathAssertionException(dir, "No such directory: ["
                    + dir.toAbsolutePath() + "].");
    }

    /**
     * Asserts that the file denoted by specified path object is NOT existing in file system.
     *
     * @param path file to be checked
     * @throws PathAssertionException if specified path is existing in file system
     * @throws IllegalArgumentException if specified path is null
     */
    public static void assertPathNotExisting(Path path) {
        assertArgumentNotNull("path", path);
        if (Files.exists(path))
            throw new PathAssertionException(path, "Path is existing but expected as not existing: ["
                    + path.toAbsolutePath() + "].");
    }

    /**
     * Asserts that specified subDir is a direct subdirectory of specified referenceDir. Both are expected as
     * existing.
     *
     * @param referenceDir reference directory
     * @param subDir subdirectory
     * @throws PathAssertionException if specified subDir is not direct subdir of specified reference directory
     * @throws IllegalArgumentException if one of the specified arguments is null
     */
    public static void assertIsDirectSubdirectory(Path referenceDir, Path subDir) {
        assertArgumentNotNull("referenceDir", referenceDir);
        assertIsExistingDirectory(referenceDir);
        assertArgumentNotNull("subDir", subDir);
        assertIsExistingDirectory(subDir);

        if (!FileUtils.isDirectSubdirectory(referenceDir, subDir))
            throw new PathAssertionException(subDir, "Directory [" + referenceDir.toAbsolutePath() + "] " +
                    "does not contain [" + subDir.toAbsolutePath() + "].");
    }

}
