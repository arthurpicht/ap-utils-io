package de.arthurpicht.utils.io.basics;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {

    /**
     * Deletes specified directory recursively if directory tree is not deeper than specified maxDepth.
     * Otherwise a java.nio.{@link java.nio.file.DirectoryNotEmptyException} is thrown.
     *
     * @param dir directory to be deleted recursively
     * @param maxDepth max depth of directory tree that will be deleted
     * @throws IOException ioException
     * @throws java.nio.file.DirectoryNotEmptyException if directory tree is deeper than specified maxDepth
     */
    public static void rmDirR(Path dir, int maxDepth) throws IOException {
        Objects.requireNonNull(dir);
        if (!Files.exists(dir) || !Files.isDirectory(dir))
            throw new IllegalArgumentException("Directory not found: " + dir.toAbsolutePath().toString());
        if (maxDepth < 1)
            throw new IllegalArgumentException("Method parameter maxDepth must be >= 1. Actually is " + maxDepth);

        Files.walkFileTree(dir, new HashSet<>(), maxDepth, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc;
                }
            }
        });

    }

    public static void rmDirR(File dir, int maxDepth) throws IOException {
        Objects.requireNonNull(dir);
        rmDirR(dir.toPath(), maxDepth);
    }

    /**
     * Deletes specified directory recursively.
     *
     * @param dir Directory to be deleted.
     * @throws IOException ioException
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void rmDirR(Path dir) throws IOException {
        Objects.requireNonNull(dir);
        if (!Files.exists(dir) || !Files.isDirectory(dir))
            throw new IllegalArgumentException("No such directory: " + dir.toAbsolutePath().toString());

        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public static void rmDirR(File dir) throws IOException {
        Objects.requireNonNull(dir);
        rmDirR(dir.toPath());
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

}
