package de.arthurpicht.utils.io.nio2;

import de.arthurpicht.utils.io.nio2.GuaranteedDir.GuaranteedDirException;
import de.arthurpicht.utils.io.tempDir.TempDir;
import de.arthurpicht.utils.io.tempDir.TempDirs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GuaranteedDirTest {

    @Test
    void specifiedByStrings() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        String tempDirName = tempDir.asPath().getFileName().toString();

        Path dir = Paths.get("testTemp", tempDirName, "test");
        assertFalse(FileUtils.isExistingDirectory(dir));

        Path guaranteedDir = GuaranteedDir.get("testTemp", tempDirName, "test");
        assertEquals(dir, guaranteedDir);
        assertTrue(FileUtils.isExistingDirectory(dir));

        Path guaranteedDirSecondCall = GuaranteedDir.get("testTemp", tempDirName, "test");
        assertEquals(dir, guaranteedDirSecondCall);
        assertTrue(FileUtils.isExistingDirectory(dir));
    }

    @Test
    void specifiedByUri() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path dir = tempDir.asPath().resolve("test");
        URI uri = dir.toUri();

        assertFalse(FileUtils.isExistingDirectory(dir));

        Path guaranteedDir = GuaranteedDir.get(uri);
        assertEquals(dir.toAbsolutePath(), guaranteedDir.toAbsolutePath());
        assertTrue(FileUtils.isExistingDirectory(dir));

        Path guaranteedDirSecondCall = GuaranteedDir.get(uri);
        assertEquals(dir.toAbsolutePath(), guaranteedDirSecondCall.toAbsolutePath());
        assertTrue(FileUtils.isExistingDirectory(dir));
    }

    @Test
    void specifiedByPath() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        String tempDirName = tempDir.asPath().getFileName().toString();

        Path dir = Paths.get("testTemp", tempDirName, "test");
        assertFalse(FileUtils.isExistingDirectory(dir));

        GuaranteedDir.guarantee(dir);
        assertTrue(FileUtils.isExistingDirectory(dir));

        GuaranteedDir.guarantee(dir);
        assertTrue(FileUtils.isExistingDirectory(dir));
    }

    @Test
    void getAsSubdir() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");

        Path dir = tempDir.asPath().resolve("test");
        assertFalse(FileUtils.isExistingDirectory(dir));

        Path second = Paths.get("test");
        Path guaranteedDir = GuaranteedDir.getAsSubdir(tempDir.asPath(), second);
        assertEquals(dir.toAbsolutePath(), guaranteedDir.toAbsolutePath());
        assertTrue(FileUtils.isExistingDirectory(dir));

        Path guaranteedDirSecondCall = GuaranteedDir.getAsSubdir(tempDir.asPath(), second);
        assertEquals(dir.toAbsolutePath(), guaranteedDirSecondCall.toAbsolutePath());
        assertTrue(FileUtils.isExistingDirectory(dir));
    }

    @Test
    void getAsSubdir_neg() {
        Path path = Paths.get("testTemp/notExisting");
        GuaranteedDirException e =
                assertThrows(GuaranteedDirException.class, () -> GuaranteedDir.getAsSubdir(path, Paths.get("ABC")));
        assertTrue(e.getMessage().startsWith("Directory not found: ["));
        assertTrue(e.getMessage().endsWith("/testTemp/notExisting]."));
    }

}