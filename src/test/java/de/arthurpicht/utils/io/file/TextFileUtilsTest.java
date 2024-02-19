package de.arthurpicht.utils.io.file;

import de.arthurpicht.utils.io.nio2.FileUtils;
import de.arthurpicht.utils.io.tempDir.TempDir;
import de.arthurpicht.utils.io.tempDir.TempDirs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFileUtilsTest {

    @Test
    void append() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.append(textFile, "test");
        TextFileUtils.append(textFile, "test2");

        assertTrue(FileUtils.isExistingRegularFile(textFile));
        List<String> lines = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(1, lines.size());
        assertEquals("testtest2", lines.get(0));
    }

    @Test
    void appendLine_empty() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test");
        TextFileUtils.appendLine(textFile, "test2");

        assertTrue(FileUtils.isExistingRegularFile(textFile));
        List<String> lines = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(2, lines.size());
        assertEquals("test", lines.get(0));
        assertEquals("test2", lines.get(1));
    }

    @Test
    void appendLine_nonemptyNoNewLine() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.append(textFile, "pre");
        TextFileUtils.appendLine(textFile, "test");
        TextFileUtils.appendLine(textFile, "test2");

        assertTrue(FileUtils.isExistingRegularFile(textFile));
        List<String> lines = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(3, lines.size());
        assertEquals("pre", lines.get(0));
        assertEquals("test", lines.get(1));
        assertEquals("test2", lines.get(2));
    }

    @Test
    void appendLines_empty() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        List<String> lines = Arrays.asList("line1", "line2");
        TextFileUtils.appendLines(textFile, lines);

        List<String> linesRead = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(2, linesRead.size());
        assertEquals("line1", linesRead.get(0));
        assertEquals("line2", linesRead.get(1));
    }

    @Test
    void appendLines_nonemptyNoNewLine() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.append(textFile, "pre");
        List<String> lines = Arrays.asList("line1", "line2");
        TextFileUtils.appendLines(textFile, lines);

        List<String> linesRead = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(3, linesRead.size());
        assertEquals("pre", linesRead.get(0));
        assertEquals("line1", linesRead.get(1));
        assertEquals("line2", linesRead.get(2));
    }

    @Test
    void appendLines_nonemptyWithNewLine() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "pre");
        List<String> lines = Arrays.asList("line1", "line2");
        TextFileUtils.appendLines(textFile, lines);

        List<String> linesRead = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(3, linesRead.size());
        assertEquals("pre", linesRead.get(0));
        assertEquals("line1", linesRead.get(1));
        assertEquals("line2", linesRead.get(2));
    }

    @Test
    void endsWithNewLineOrIsEmpty_empty() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);

        assertTrue(TextFileUtils.endsWithNewLineOrIsEmpty(textFile));
    }

    @Test
    void endsWithNewLineOrIsEmpty_oneLineNoNewLine() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);

        TextFileUtils.append(textFile, "abc");

        assertFalse(TextFileUtils.endsWithNewLineOrIsEmpty(textFile));
    }

    @Test
    void endsWithNewLineOrIsEmpty_oneLineWithNewLine() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove("testTemp");
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);

        TextFileUtils.append(textFile, "abc\n");

        assertTrue(TextFileUtils.endsWithNewLineOrIsEmpty(textFile));
    }

}