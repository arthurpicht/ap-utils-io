package de.arthurpicht.utils.io.assertions;

import de.arthurpicht.utils.io.tempDir.TempDir;
import de.arthurpicht.utils.io.tempDir.TempDirs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathAssertionsTest {

    private static final String PROJECT_TEMP_DIR = "testTemp";

    @Test
    void assertFileName_filePos() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Files.createFile(tempDir.asPath().resolve("a"));
        Path path = tempDir.asPath().resolve("a");

        assertDoesNotThrow(() -> PathAssertions.assertFileName(path, "a"));
    }

    @Test
    void assertFileName_fileNeg() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Files.createFile(tempDir.asPath().resolve("a"));
        Path path = tempDir.asPath().resolve("a");

        assertThrows(PathAssertionException.class, () -> PathAssertions.assertFileName(path, "b"));
    }

    @Test
    void assertFileName_dirPos() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);

        assertDoesNotThrow(() ->
                PathAssertions.assertFileName(tempDir.asPath(), tempDir.asPath().getFileName().toString())
        );
    }

    @Test
    void assertFileName_dirNeg() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);

        assertThrows(PathAssertionException.class, () -> PathAssertions.assertFileName(tempDir.asPath(), "b"));
    }

    @Test
    void assertFileName_dirPosNotExisting() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path dir = tempDir.asPath().resolve(UUID.randomUUID().toString());

        assertDoesNotThrow(() ->
                PathAssertions.assertFileName(dir, dir.getFileName().toString())
        );
    }

    @Test
    void assertPathNotExisting() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertPathNotExisting(tempDir.asPath())
        );
    }

    @Test
    void assertPathNotExisting_neg() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);

        assertDoesNotThrow(() ->
                PathAssertions.assertPathNotExisting(tempDir.asPath().resolve(UUID.randomUUID().toString()))
        );
    }

    @Test
    void assertIsDirectSubdirectory() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = Files.createDirectory(tempDir.asPath().resolve("sub"));

        assertDoesNotThrow(() ->
                PathAssertions.assertIsDirectSubdirectory(tempDir.asPath(), subDir)
        );
    }

    @Test
    void assertIsDirectSubdirectory_negNoSubDir() throws IOException {
        TempDir tempDir1 = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        TempDir tempDir2 = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertIsDirectSubdirectory(tempDir1.asPath(), tempDir2.asPath())
        );
    }

    @Test
    void assertIsDirectSubdirectory_negDirectSubDir() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subSubDir = Files.createDirectories(tempDir.asPath().resolve("sub").resolve("subSub"));

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertIsDirectSubdirectory(tempDir.asPath(), subSubDir)
        );
    }

    @Test
    void assertIsDirectSubdirectory_negNonExistent() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subSubDir = tempDir.asPath().resolve(UUID.randomUUID().toString());

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertIsDirectSubdirectory(tempDir.asPath(), subSubDir)
        );
    }

}