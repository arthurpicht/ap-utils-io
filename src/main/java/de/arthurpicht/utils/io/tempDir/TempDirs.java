package de.arthurpicht.utils.io.tempDir;

import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Deprecated
public class TempDirs {


    /**
     * Deprecated: Use TempDir.Builder instead.
     *
     * Creates a {@link TempDir} with random UUID as name in specified directory. Specified parent directory must exist.
     *
     * @param parentDir directory in which temporary directory will be created.
     * @return newly created instance of {@link TempDir}
     * @throws IOException
     */
    @Deprecated
    public static TempDir createUniqueTempDir(Path parentDir) throws IOException {
        assertExistence(parentDir);
        Path tempDirPath = createTemDir(parentDir);
        return new TempDir(tempDirPath, false);
    }


    /**
     * Deprecated: Use TempDir.Builder instead.
     */
    @Deprecated
    public static TempDir createUniqueTempDir(String first, String... more) throws IOException {
        Path parentDir = Paths.get(first, more);
        return createUniqueTempDir(parentDir);
    }

    /**
     * Deprecated: Use TempDir.Builder instead.
     * Creates a {@link TempDir} with random UUID as name in specified directory. Specified directory must exist.
     * Created TempDir will be deleted automatically on shutdown.
     *
     * @param parentDir directory in which temporary directory will be created.
     * @return newly created instance of {@link TempDir}
     * @throws IOException
     */
    @Deprecated
    public static TempDir createUniqueTempDirAutoRemove(Path parentDir) throws IOException {
        assertExistence(parentDir);
        Path tempDirPath = createTemDir(parentDir);
        FileUtils.forceDeleteOnShutdown(tempDirPath);
        return new TempDir(tempDirPath, true);
    }

    /**
     * Deprecated: Use TempDir.Builder instead.
     * @param first
     * @param more
     * @return
     * @throws IOException
     */
    @Deprecated
    public static TempDir createUniqueTempDirAutoRemove(String first, String... more) throws IOException {
        Path parentDir = Paths.get(first, more);
        return createUniqueTempDirAutoRemove(parentDir);
    }

    private static void assertExistence(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path))
            throw new IllegalArgumentException(
                    "Could not create temp dir. Specified parent path not existing: [" + path.toAbsolutePath() + "].");
    }

    private static Path createTemDir(Path parentDir) throws IOException {
        Path tempDirPath = parentDir.resolve(UUID.randomUUID().toString());
        Files.createDirectory(tempDirPath);
        return tempDirPath;
    }

}
