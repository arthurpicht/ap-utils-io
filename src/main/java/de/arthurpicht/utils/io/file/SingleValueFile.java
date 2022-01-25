package de.arthurpicht.utils.io.file;

import de.arthurpicht.utils.core.assertion.AssertMethodPrecondition;
import de.arthurpicht.utils.core.strings.Strings;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Simple functionality for reading and writing a string value to/from a file.
 * Implementation is thread save. Existence of parent directory is assumed.
 */
public class SingleValueFile {

    private final Path path;
    private final Charset charset;

    public SingleValueFile(Path path) {
        this.path = path;
        this.charset = StandardCharsets.UTF_8;
    }

    public SingleValueFile(Path path, Charset charset) {
        this.path = path;
        this.charset = charset;
    }

    public Path getPath() {
        return this.path;
    }

    public Charset getCharset() {
        return this.charset;
    }

    /**
     * Writes specified string to file. If specified string contains more than one line, then the first line is written
     * only.
     *
     * @param string
     * @throws IOException
     */
    public synchronized void write(String string) throws IOException {
        AssertMethodPrecondition.parameterNotNull("string", string);

        String value = Strings.getFirstLine(string);
        Files.writeString(this.path, value, this.charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Returns content from file as string. Existence of file is a precondition. If file contains more than one line,
     * only the first line is returned.
     * If checking for file existence and reading can not be performed as a thread save (atomic) operation,
     * then catch IllegalStateException.
     *
     * @return content from file as String.
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public synchronized String read() throws IOException {
        if (!exists()) throw new IllegalStateException("No such file to read from: " + this.path.toString());
        String content = Files.readString(this.path, this.charset);
        return Strings.getFirstLine(content);
    }

    /**
     * Deletes file. Existence of file is a precondition.
     *
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public synchronized void delete() throws IOException {
        if (!exists()) throw new IllegalStateException("No such file to delete: " + this.path.toString());
        Files.delete(this.path);
    }

    /**
     * Deletes file if existing.
     *
     * @throws IOException
     */
    public synchronized void deleteIfExists() throws IOException {
        Files.deleteIfExists(this.path);
    }

    /**
     * Checks is file exists.
     *
     * @return
     */
    public synchronized boolean exists() {
        return Files.exists(this.path);
    }

    /**
     * Checks if file has content.
     *
     * @return
     * @throws IOException
     */
    public synchronized boolean hasContent() throws IOException {
        if (!exists()) throw new IllegalStateException("No such file: " + this.path.toString());
        return Files.size(this.path) > 0;
    }

}
