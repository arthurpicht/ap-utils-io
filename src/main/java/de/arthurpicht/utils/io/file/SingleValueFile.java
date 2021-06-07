package de.arthurpicht.utils.io.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Simple functionality for reading and writing a string to/from a file.
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

    public synchronized void write(String string) throws IOException {
        Files.writeString(this.path, string, this.charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Returns content from file as string. Existence of file is a precondition.
     * If checking for file existence and reading can not be performed as an thread save (atomic) operation,
     * catch IllegalStateException.
     *
     * @return content from file as String.
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public synchronized String read() throws IOException {
        if (!exists()) throw new IllegalStateException("No such file to read from: " + this.path.toString());
        return Files.readString(this.path, this.charset);
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

    public synchronized void deleteIfExists() throws IOException {
        Files.deleteIfExists(this.path);
    }

    public synchronized boolean exists() {
        return Files.exists(this.path);
    }

}
