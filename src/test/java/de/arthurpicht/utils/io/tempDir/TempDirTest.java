package de.arthurpicht.utils.io.tempDir;

import de.arthurpicht.utils.io.nio2.FileUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TempDirTest {

    @Test
    void simpleDefault() {
        TempDir tempDir = new TempDir.Creator().create();

        assertTrue(FileUtils.isExistingDirectory(tempDir.asPath()));
        assertTrue(tempDir.exists());
        assertTrue(tempDir.isAutoRemove());
    }

    @Test
    void parentDir() {
        TempDir tempDir = new TempDir.Creator().withParentDir("testTemp").create();

        assertTrue(FileUtils.isExistingDirectory(tempDir.asPath()));
        assertTrue(tempDir.exists());
        assertTrue(tempDir.isAutoRemove());

        String tempDirParentName = tempDir.asPath().getParent().getFileName().toString();
        assertEquals("testTemp", tempDirParentName);
    }

    @Test
    void prefixSuffix() {
        TempDir tempDir = new TempDir.Creator()
                .withTempDirPrefix("pre-")
                .withTempDirSuffix("-post")
                .create();

        String tempDirName = tempDir.asPath().getFileName().toString();
        assertTrue(tempDirName.startsWith("pre-"));
        assertTrue(tempDirName.endsWith("-post"));
    }

    @Test
    void noAutoRemove() {
        TempDir tempDir = new TempDir.Creator()
                .withParentDir("testTemp")
                .withTempDirPrefix("test-")
                .withNoAutoRemove()
                .create();

        assertTrue(FileUtils.isExistingDirectory(tempDir.asPath()));
        assertTrue(tempDir.exists());
        assertFalse(tempDir.isAutoRemove());
    }

}