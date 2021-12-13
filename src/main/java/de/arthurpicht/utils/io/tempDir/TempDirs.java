package de.arthurpicht.utils.io.tempDir;

import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class TempDirs {

    /**
     * Creates a {@link TempDir} with as random UUID as name in specified directory. Specified parent directory must exist.
     *
     * @param parentDir directory in which temporary directory will be created.
     * @return newly created instance of {@link TempDir}
     * @throws IOException
     */
    public static TempDir createUniqueTempDir(Path parentDir) throws IOException {
        assertExistence(parentDir);
        Path tempDirPath = createTemDir(parentDir);
        return new TempDir(tempDirPath, false);
    }

    /**
     * Creates a {@link TempDir} with a random UUID as name in specified directory. Specified directory must exist.
     * Created TempDir will be deleted automatically on shutdown.
     *
     * @param parentDir directory in which temporary directory will be created.
     * @return newly created instance of {@link TempDir}
     * @throws IOException
     */
    public static TempDir createUniqueTempDirAutoClean(Path parentDir) throws IOException {
        assertExistence(parentDir);
        Path tempDirPath = createTemDir(parentDir);
        FileUtils.forceDeleteOnShutdown(tempDirPath);
        return new TempDir(tempDirPath, true);
    }

    private static void assertExistence(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path))
            throw new IllegalArgumentException(
                    "Specified parent path for temp dir not existing: [" + path.toAbsolutePath() + "].");
    }

    private static Path createTemDir(Path parentDir) throws IOException {
        Path tempDirPath = parentDir.resolve(UUID.randomUUID().toString());
        Files.createDirectory(tempDirPath);
        return tempDirPath;
    }

}
