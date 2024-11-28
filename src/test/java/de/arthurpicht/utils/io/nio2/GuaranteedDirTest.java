package de.arthurpicht.utils.io.nio2;

import de.arthurpicht.utils.io.nio2.GuaranteedDir.GuaranteedDirException;
import de.arthurpicht.utils.io.tempDir.TempDir;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GuaranteedDirTest {

    private static final String PROJECT_TEMP_DIR = "testTemp";

    @Test
    void specifiedByStrings() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
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
    void specifiedByUri() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
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
    void specifiedByPath() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
        String tempDirName = tempDir.asPath().getFileName().toString();

        Path dir = Paths.get("testTemp", tempDirName, "test");
        assertFalse(FileUtils.isExistingDirectory(dir));

        GuaranteedDir.guarantee(dir);
        assertTrue(FileUtils.isExistingDirectory(dir));

        GuaranteedDir.guarantee(dir);
        assertTrue(FileUtils.isExistingDirectory(dir));
    }

    @Test
    void getAsSubdirectory() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();

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
    void getAsSubdirectory_neg() {
        Path path = Paths.get("testTemp/notExisting");
        GuaranteedDirException e =
                assertThrows(GuaranteedDirException.class, () -> GuaranteedDir.getAsSubdir(path, Paths.get("ABC")));
        assertTrue(e.getMessage().startsWith("Directory not found: ["));
        assertTrue(e.getMessage().endsWith("/testTemp/notExisting]."));
    }

}