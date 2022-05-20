package de.arthurpicht.utils.io.nio2;

import de.arthurpicht.utils.io.assertions.PathAssertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;
import static de.arthurpicht.utils.io.assertions.PathAssertions.assertIsExistingDirectory;

public class FileUtils {

    /**
     * Removes directory recursively and silently on JVM shutdown.
     * @param path directory to be deleted
     */
    public static void forceDeleteOnShutdown(Path path) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> forceDeleteSilently(path)));
    }

    /**
     * Performs a force delete if specified path is classified as file or directory. No exceptions are thrown.
     * Nothing is performed if path is not existing.
     *
     * @param path File or directory to be deleted.
     */
    public static void forceDeleteSilently(Path path) {
        if (!Files.exists(path)) return;
        try {
            forceDelete(path);
        } catch (IOException e) {
            // din
        }
    }

    /**
     * Deletes file if specified path is a file. Deletes directory recursively if specified path is a
     * directory.
     *
     * @param path File or directory to be deleted.
     * @throws IOException
     */
    public static void forceDelete(Path path) throws IOException {
        Objects.requireNonNull(path);
        if (!Files.exists(path) || (!isFileOrDirectory(path)))
            throw new IllegalArgumentException("No such file or directory: " + path.toAbsolutePath());
        if (Files.isRegularFile(path)) {
            Files.delete(path);
        } else if (Files.isDirectory(path)) {
            rmDir(path);
        } else {
            throw new IllegalArgumentException("No such file or directory: " + path.toAbsolutePath());
        }
    }

    public static boolean isFileOrDirectory(Path path) {
        Objects.requireNonNull(path);
        return Files.isRegularFile(path) || Files.isDirectory(path);
    }

    public static boolean isExistingDirectory(Path path) {
        assertArgumentNotNull("path", path);
        return Files.exists(path) && Files.isDirectory(path);
    }

    public static boolean isExistingRegularFile(Path path) {
        assertArgumentNotNull("path", path);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Deletes specified directory recursively if existing. No operation is performed if directory does not exist.
     * IOException is suppressed.
     *
     * @param dir directory to be deleted
     */
    public static void rmDirSilently(Path dir) {
        if (!isExistingDirectory(dir)) return;
        try {
            rmDir(dir);
        } catch (IOException e) {
            // din
        }
    }

    /**
     * Deletes specified directory recursively.
     *
     * @param dir Directory to be deleted.
     * @throws IOException ioException
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void rmDir(Path dir) throws IOException {
        Objects.requireNonNull(dir);
        if (!Files.exists(dir) || !Files.isDirectory(dir))
            throw new IllegalArgumentException("No such directory: " + dir.toAbsolutePath());

        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Finds the deepest directory in the directory tree. If there are more than one paths with equal length, some of
     * those paths will be returned.
     *
     * @param dirPath starting point to search from
     * @return deepest path under specified dirPath
     * @throws IOException ioException
     * @throws IllegalArgumentException if specified path is no existing directory
     */
    public static Path findDeepest(Path dirPath) throws IOException {
        Objects.requireNonNull(dirPath);
        if (!Files.isDirectory(dirPath) || !Files.exists(dirPath))
            throw new IllegalArgumentException("No such directory: " + dirPath.toAbsolutePath().toString());

        try (Stream<Path> s = Files.walk(dirPath)) {
            Optional<Path> deepestOptional = s.filter(Files::isDirectory).max(Comparator.comparing(Path::getNameCount));
            if (deepestOptional.isEmpty()) throw new IllegalStateException();
            return deepestOptional.get();
        }
    }

    /**
     * Determines the number of directory levels under specified directory.
     *
     * @param path starting point to count from
     * @return number of directory levels
     * @throws IOException ioException
     */
    public static int getDepth(Path path) throws IOException {
        Objects.requireNonNull(path);
        if (!Files.isDirectory(path) || !Files.exists(path))
            throw new IllegalArgumentException("No such directory: " + path.toAbsolutePath().toString());

        Path deepestPath = findDeepest(path);
        return deepestPath.getNameCount() - path.getNameCount();
    }

    /**
     * Returns the canonical form of the specified path. A canonical form is absolute and without any './' or '../'.
     *
     * @param path path to be expressed in canonical form
     * @return canonical form of specified path
     */
    public static Path toCanonicalPath(Path path) {
        return path.normalize().toAbsolutePath();
    }

    /**
     * Returns the current working directory in a normalized and absolute form (canonical form).
     *
     * @return current working directory
     */
    public static Path getWorkingDir() {
        return Paths.get(".").normalize().toAbsolutePath();
    }

    /**
     * Determines whether specified path element is a child element of specified reference.
     *
     * @param reference
     * @param element
     * @return
     */
    public static boolean isChild(Path reference, Path element) {
        Path referenceCanonical = toCanonicalPath(reference);
        Path elementCanonical = toCanonicalPath(element);
        if (referenceCanonical.equals(elementCanonical)) return false;
        return elementCanonical.startsWith(referenceCanonical);
    }

    /**
     * Checks if the farthest element of specified path equals to specified fileName. Parameter fileName can denote
     * a file or directory.
     *
     * @param path
     * @param fileName
     * @return
     */
    public static boolean isPathEndingWithFileName(Path path, String fileName) {
        return (path.getFileName().toString().equals(fileName));
    }

    /**
     * Recursively copies the specified source directory to specified destination directory. If the destination
     * directory is not preexisting then it will be created.<br>
     * Examples:<br>
     * After copying a/b/c to x/y/c you will find a copy of c as a subdirectory of x/y/.<br>
     * After copying a/b/c to x/y/z you will find a copy of c as z.
     *
     * @param source
     * @param destination
     * @param options
     * @throws IOException
     */
    public static void copyDirectory(Path source, Path destination, CopyOption... options) throws IOException {
        assertArgumentNotNull("source", source);
        assertArgumentNotNull("destination", destination);
        if (!FileUtils.isExistingDirectory(source))
            throw new IllegalArgumentException("Source directory not found: [" + source.toAbsolutePath() + "].");

        Files.walkFileTree(source, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                Files.createDirectories(destination.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.copy(file, destination.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Returns a list of paths, denoting all regular files found recursively in specified directory.
     *
     * @param directory directory to find files in
     * @return list of regular files
     * @throws IOException ...
     */
    public static List<Path> getContainingFiles(Path directory) throws IOException {
        assertArgumentNotNull("directory", directory);
        if (!FileUtils.isExistingDirectory(directory))
            throw new IllegalArgumentException("Directory not found: [" + directory.toAbsolutePath() + "].");

        List<Path> pathList = new ArrayList<>();

        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                pathList.add(file);
                return FileVisitResult.CONTINUE;
            }
        });

        return pathList;
    }

    /**
     * Returns a list of all regular files in specified directory (non-recursive).
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static List<Path> getRegularFilesInDirectory(Path dir) throws IOException {
        PathAssertions.assertIsExistingDirectory(dir);
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }

    /**
     * Returns al list af all non-hidden regular files in specified directory (non-recursive).
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static List<Path> getRegularNonHiddenFilesInDirectory(Path dir) throws IOException {
        PathAssertions.assertIsExistingDirectory(dir);
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(f -> !f.getFileName().toString().startsWith("."))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Checks if specified directory contains at least one subdirectory.
     *
     * @param dir Existing directory
     * @return
     * @throws IOException
     */
    public static boolean hasSubdirectories(Path dir) throws IOException {
        assertArgumentNotNull("dir", dir);
        assertIsExistingDirectory(dir);

        return Files.list(dir).anyMatch(Files::isDirectory);
    }

    /**
     * Obtains all subdirectories for specified path. Specified path must be an existing directory.
     *
     * @param dir directory
     * @return a list of {@link Path} instances representing subdirectories
     * @throws IOException
     * @throws de.arthurpicht.utils.io.assertions.PathAssertionException if specified directory does not exist
     */
    public static List<Path> getSubdirectories(Path dir) throws IOException {
        assertArgumentNotNull("dir", dir);
        assertIsExistingDirectory(dir);
        List<Path> subdirectories = Files.walk(dir, 1).filter(Files::isDirectory).collect(Collectors.toList());
        // remove specified dir
        subdirectories.remove(0);
        return subdirectories;
    }

    /**
     * Selects all subdirectories not ending with tilde.
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<Path> getSubdirectoriesNotEndingWithTilde(Path path) throws IOException {
        List<Path> subdirectories = getSubdirectories(path);
        return subdirectories.stream()
                .filter(p -> !p.getFileName().toString().endsWith("~"))
                .collect(Collectors.toList());
    }

    /**
     * Checks if specified reference directory contains specified subdirectory.
     * Returns false if reference directory or subdirectory do not exist.
     *
     * @param referenceDir
     * @param subDir
     * @return
     */
    public static boolean isDirectSubdirectory(Path referenceDir, Path subDir) {
        Path dirWork = referenceDir.normalize().toAbsolutePath();
        if (!FileUtils.isExistingDirectory(referenceDir)) return false;
        int nameCountDirWork = dirWork.getNameCount();

        Path subdirWork = subDir.normalize().toAbsolutePath();
        if (!FileUtils.isExistingDirectory(subdirWork)) return false;
        int nameCountSubdirWork = subdirWork.getNameCount();

        boolean oneLonger = (nameCountSubdirWork - nameCountDirWork == 1);

        return (!dirWork.equals(subdirWork) && subdirWork.startsWith(dirWork) && oneLonger);
    }

}
