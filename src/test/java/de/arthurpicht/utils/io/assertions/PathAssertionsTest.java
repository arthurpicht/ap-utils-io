package de.arthurpicht.utils.io.assertions;

import de.arthurpicht.utils.io.tempDir.TempDir;
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
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
        Files.createFile(tempDir.asPath().resolve("a"));
        Path path = tempDir.asPath().resolve("a");

        assertDoesNotThrow(() -> PathAssertions.assertFileName(path, "a"));
    }

    @Test
    void assertFileName_fileNeg() throws IOException {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
        Files.createFile(tempDir.asPath().resolve("a"));
        Path path = tempDir.asPath().resolve("a");

        assertThrows(PathAssertionException.class, () -> PathAssertions.assertFileName(path, "b"));
    }

    @Test
    void assertFileName_dirPos() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        assertDoesNotThrow(() ->
                PathAssertions.assertFileName(tempDir.asPath(), tempDir.asPath().getFileName().toString())
        );
    }

    @Test
    void assertFileName_dirNeg() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        assertThrows(PathAssertionException.class, () -> PathAssertions.assertFileName(tempDir.asPath(), "b"));
    }

    @Test
    void assertFileName_dirPosNotExisting() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        Path dir = tempDir.asPath().resolve(UUID.randomUUID().toString());

        assertDoesNotThrow(() ->
                PathAssertions.assertFileName(dir, dir.getFileName().toString())
        );
    }

    @Test
    void assertPathNotExisting() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertPathNotExisting(tempDir.asPath())
        );
    }

    @Test
    void assertPathNotExisting_neg() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        assertDoesNotThrow(() ->
                PathAssertions.assertPathNotExisting(tempDir.asPath().resolve(UUID.randomUUID().toString()))
        );
    }

    @Test
    void assertIsDirectSubdirectory() throws IOException {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
        Path subDir = Files.createDirectory(tempDir.asPath().resolve("sub"));

        assertDoesNotThrow(() ->
                PathAssertions.assertIsDirectSubdirectory(tempDir.asPath(), subDir)
        );
    }

    @Test
    void assertIsDirectSubdirectory_negNoSubDir() {
        TempDir tempDir1 = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        TempDir tempDir2 = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertIsDirectSubdirectory(tempDir1.asPath(), tempDir2.asPath())
        );
    }

    @Test
    void assertIsDirectSubdirectory_negDirectSubDir() throws IOException {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
        Path subSubDir = Files.createDirectories(tempDir.asPath().resolve("sub").resolve("subSub"));

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertIsDirectSubdirectory(tempDir.asPath(), subSubDir)
        );
    }

    @Test
    void assertIsDirectSubdirectory_negNonExistent() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
        Path subSubDir = tempDir.asPath().resolve(UUID.randomUUID().toString());

        assertThrows(PathAssertionException.class, () ->
                PathAssertions.assertIsDirectSubdirectory(tempDir.asPath(), subSubDir)
        );
    }

}