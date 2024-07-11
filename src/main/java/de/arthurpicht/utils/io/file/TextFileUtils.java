package de.arthurpicht.utils.io.file;

import de.arthurpicht.utils.core.strings.Strings;
import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class TextFileUtils {

    /**
     * Reads the content of a file to a list of strings representing the lines.
     *
     * @param path file to be read
     * @return list of strings as lines
     * @throws IOException on error when reading file
     */
    public static List<String> readLinesAsStrings(Path path) throws IOException {
        assertArgumentNotNull("path", path);
        if (!FileUtils.isExistingRegularFile(path))
            throw new IllegalArgumentException("File not found: [" + path.toAbsolutePath() + "].");

        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(path.toFile()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Reads the content of a file to a list of strings representing the lines. Each line will be trimmed.
     * Empty lines and lines only consisting white space will be removed.
     *
     * @param path file to be read
     * @return list of strings as lines
     * @throws IOException on error when reading file
     */
    public static List<String> readTrimmedContentLines(Path path) throws IOException {
        assertArgumentNotNull("path", path);
        List<String> lines = readLinesAsStrings(path);
        List<String> list = new ArrayList<>();
        for (String s1 : lines) {
            s1 = s1.trim();
            if (!s1.isEmpty()) {
                list.add(s1);
            }
        }
        return list;
    }

    /**
     * Checks if specified text file contains specified line. The specified line string is checked as trimmed against
     * all trimmed lines in the text file.
     *
     * @param path file to be checked
     * @param line line string to be checked
     * @return if file contains line (all trimmed)
     * @throws IOException on error when reading file
     */
    public static boolean containsLineTrimmed(Path path, String line) throws IOException {
        assertArgumentNotNull("path", path);
        assertArgumentNotNull("line", line);
        if (!FileUtils.isExistingRegularFile(path))
            throw new IllegalArgumentException("File not found: [" + path.toAbsolutePath() + "].");
        List<String> lines = readLinesAsStrings(path);
        String lineTrimmed = line.trim();
        return lines.stream().anyMatch(l -> l.trim().equals(lineTrimmed));
    }

    /**
     * Appends specified content to specified file. File must be preexisting. Content will be added as is without
     * any line-break.
     *
     * @param path file that is added to
     * @param content content to be added
     * @throws IOException ...
     * @throws IllegalArgumentException If file does not exist.
     */
    public static void append(Path path, String content) throws IOException {
        if (!FileUtils.isExistingRegularFile(path))
            throw new IllegalArgumentException("File not found: [" + path.toAbsolutePath() + "].");
        Files.write(
                path,
                content.getBytes(),
                StandardOpenOption.APPEND);
    }

    /**
     * Appends specified content string as a new line to specified file. If file does not contain a new line
     * character at the end it will be added. Also, a new line is added after appending the content string.
     *
     * @param path file that is added to
     * @param line content to be added as a new line
     * @throws IOException ...
     * @throws IllegalArgumentException If file does not exist.
     */
    public static void appendLine(Path path, String line) throws IOException {
        if (!FileUtils.isExistingRegularFile(path))
            throw new IllegalArgumentException("File not found: [" + path.toAbsolutePath() + "].");
        if (!endsWithNewLineOrIsEmpty(path))
            line = "\n" + line;
        line += "\n";
        Files.write(
                path,
                line.getBytes(),
                StandardOpenOption.APPEND);
    }

    /**
     * Appends specified strings as lines to specified file. File must be preexisting. If file does not contain a
     * new line character at the end it will be added. Also, a new line is added after appending the strings.
     *
     * @param path file that is added to
     * @param lines lines to be added
     * @throws IOException ...
     * @throws IllegalArgumentException If file does not exist.
     */
    public static void appendLines(Path path, List<String> lines) throws IOException {
        if (!FileUtils.isExistingRegularFile(path))
            throw new IllegalArgumentException("File not found: [" + path.toAbsolutePath() + "].");
        if (lines.isEmpty()) return;
        String formattedLine = Strings.listing(lines, "\n");
        if (!endsWithNewLineOrIsEmpty(path)) {
            formattedLine = "\n" + formattedLine;
        }
        formattedLine = formattedLine + "\n";
        Files.write(
                path,
                formattedLine.getBytes(),
                StandardOpenOption.APPEND);
    }

    /**
     * Checks if specified file ends with a new line character or is empty. File must be existing.
     *
     * @param path file to be checked
     * @return ...
     * @throws IOException ...
     */
    public static boolean endsWithNewLineOrIsEmpty(Path path) throws IOException {
        if (!FileUtils.isExistingRegularFile(path))
            throw new IllegalArgumentException("File not found: [" + path.toAbsolutePath() + "].");
        try (RandomAccessFile fileHandler = new RandomAccessFile(path.toFile(), "r")) {
            long fileLength = fileHandler.length() - 1;
            if (fileLength < 0) {
                return true;
            }
            fileHandler.seek(fileLength);
            byte readByte = fileHandler.readByte();
            return readByte == 0xA || readByte == 0xD;
        }
    }
}
