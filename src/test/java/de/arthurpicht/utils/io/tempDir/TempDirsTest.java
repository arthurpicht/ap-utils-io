package de.arthurpicht.utils.io.tempDir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempDirsTest {

    @Test
    public void parentNotExisting() {
        Path path = Paths.get("thisIsNotExisting");
        Assertions.assertThrows(IllegalArgumentException.class, () -> TempDirs.createUniqueTempDir(path));
    }

    @Test
    public void uniqueTempDir() throws IOException {
        Path path = Paths.get("testTemp");

        TempDir tempDir = TempDirs.createUniqueTempDir(path);
        assertTrue(tempDir.exists());
        assertFalse(tempDir.isAutoRemove());
        assertTrue(Files.exists(tempDir.asPath()));
        assertTrue(Files.isDirectory(tempDir.asPath()));
        assertTrue(tempDir.asFile().exists());
        assertTrue(tempDir.asFile().isDirectory());

        Path tempFile = tempDir.asPath().resolve("tempFile.txt");
        Files.writeString(tempFile, "Test");
        assertTrue(Files.exists(tempFile));
        assertTrue(Files.isRegularFile(tempFile));

        tempDir.remove();
        assertFalse(tempDir.exists());
    }

}