package de.arthurpicht.utils.io.file;

import de.arthurpicht.utils.io.nio2.FileUtils;
import de.arthurpicht.utils.io.tempDir.TempDir;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFileUtilsTest {

    @Test
    void readLinesAsStrings() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, " test2 ");
        TextFileUtils.appendLine(textFile, "");

        List<String> contentLines = TextFileUtils.readLinesAsStrings(textFile);
        assertEquals(3, contentLines.size());
        assertEquals("test1", contentLines.get(0));
        assertEquals(" test2 ", contentLines.get(1));
        assertEquals("", contentLines.get(2));
    }

    @Test
    void readUncommentedLinesAsStringsSimple() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, "#test2");
        TextFileUtils.appendLine(textFile, " # test3");
        TextFileUtils.appendLine(textFile, " test # 3");
        TextFileUtils.appendLine(textFile, "");

        List<String> contentLines = TextFileUtils.readNonCommentedLinesAsStrings(textFile, "#");
        assertEquals(3, contentLines.size());
        assertEquals("test1", contentLines.get(0));
        assertEquals(" test # 3", contentLines.get(1));
        assertEquals("", contentLines.get(2));
    }

    @Test
    void readUncommentedLinesAsStringsDoubleSlash() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, "//test2");
        TextFileUtils.appendLine(textFile, " // test3");
        TextFileUtils.appendLine(textFile, " test // 3");
        TextFileUtils.appendLine(textFile, "");

        List<String> contentLines = TextFileUtils.readNonCommentedLinesAsStrings(textFile, "//");
        assertEquals(3, contentLines.size());
        assertEquals("test1", contentLines.get(0));
        assertEquals(" test // 3", contentLines.get(1));
        assertEquals("", contentLines.get(2));
    }

    @Test
    void readUncommentedLinesAsStringsEmptyPrefix() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, "//test2");
        TextFileUtils.appendLine(textFile, " # test3");
        TextFileUtils.appendLine(textFile, " test // 3 ");
        TextFileUtils.appendLine(textFile, "");

        List<String> contentLines = TextFileUtils.readNonCommentedLinesAsStrings(textFile, "");
        assertEquals(5, contentLines.size());
        assertEquals("test1", contentLines.get(0));
        assertEquals("//test2", contentLines.get(1));
        assertEquals(" # test3", contentLines.get(2));
        assertEquals(" test // 3 ", contentLines.get(3));
        assertEquals("", contentLines.get(4));
    }

    @Test
    void readTrimmedContentLines() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, " test2 ");
        TextFileUtils.appendLine(textFile, "");

        List<String> contentLines = TextFileUtils.readTrimmedContentLines(textFile);
        assertEquals(2, contentLines.size());
        assertEquals("test1", contentLines.get(0));
        assertEquals("test2", contentLines.get(1));
    }

    @Test
    void containsLineTrimmedSimple() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, "test2");

        assertTrue(TextFileUtils.containsLineTrimmed(textFile, "test2"));
    }

    @Test
    void containsLineTrimmedWhiteSpaceInSearchString() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, "test2");

        assertTrue(TextFileUtils.containsLineTrimmed(textFile, "   test2"));
    }

    @Test
    void containsLineTrimmedWhiteSpaceInLine() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);
        TextFileUtils.appendLine(textFile, "test1");
        TextFileUtils.appendLine(textFile, "   test2   ");

        assertTrue(TextFileUtils.containsLineTrimmed(textFile, "test2"));
    }

    @Test
    void append() throws IOException {
        TempDir tempDir = TempDir.create();
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
        TempDir tempDir = TempDir.create();
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
        TempDir tempDir = TempDir.create();
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
        TempDir tempDir = TempDir.create();
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
        TempDir tempDir = TempDir.create();
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
        TempDir tempDir = TempDir.create();
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
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);

        assertTrue(TextFileUtils.endsWithNewLineOrIsEmpty(textFile));
    }

    @Test
    void endsWithNewLineOrIsEmpty_oneLineNoNewLine() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);

        TextFileUtils.append(textFile, "abc");

        assertFalse(TextFileUtils.endsWithNewLineOrIsEmpty(textFile));
    }

    @Test
    void endsWithNewLineOrIsEmpty_oneLineWithNewLine() throws IOException {
        TempDir tempDir = TempDir.create();
        Path textFile = tempDir.asPath().resolve("textFile.txt");
        Files.createFile(textFile);

        TextFileUtils.append(textFile, "abc\n");

        assertTrue(TextFileUtils.endsWithNewLineOrIsEmpty(textFile));
    }

}