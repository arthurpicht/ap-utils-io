package de.arthurpicht.utils.io.file;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SingleValueFileTest {

    // If a test fails, delete TEMP_TEST_DIR and all its content before rerunning!

    private static final String TEMP_TEST_DIR = "tempTest";

    @BeforeAll
    static void createTempDir() throws IOException {
        if (!Files.exists(Paths.get(TEMP_TEST_DIR))) {
            Files.createDirectory(Paths.get(TEMP_TEST_DIR));
        }
    }

    @AfterAll
    static void deleteTempDir() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_TEST_DIR));
    }

    @Test
    void simple() throws IOException {

        String test = "test";
        Path path = Paths.get(TEMP_TEST_DIR, "test.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write(test);

        String retest = singleValueFile.read();
        assertEquals(test, retest);

        singleValueFile.delete();
        assertFalse(Files.exists(path));
    }

    @Test
    void rewrite() throws IOException {

        String test = "test";
        Path path = Paths.get(TEMP_TEST_DIR, "test2.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write("0000000000000");
        singleValueFile.write(test);

        String retest = singleValueFile.read();
        assertEquals(test, retest);

        singleValueFile.delete();
        assertFalse(Files.exists(path));
    }

    @Test
    void exists() throws IOException {
        Path path = Paths.get(TEMP_TEST_DIR, "exist.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        assertFalse(singleValueFile.exists());
        singleValueFile.write("something");
        assertTrue(singleValueFile.exists());
        singleValueFile.delete();
        assertFalse(singleValueFile.exists());
    }

    @Test
    void readFromNonExistingFile() throws IOException {
        Path path = Paths.get(TEMP_TEST_DIR, "not_existing.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        try {
            singleValueFile.read();
            fail("IllegalStateException expected.");
        } catch (IllegalStateException e) {
            // intentionally do nothing
        }
    }

    @Test
    void simpleISO() throws IOException {
        //noinspection SpellCheckingInspection
        String test = "testöäü";
        Path path = Paths.get(TEMP_TEST_DIR, "test.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path, StandardCharsets.ISO_8859_1);
        singleValueFile.write(test);

        String retest = singleValueFile.read();
        assertEquals(test, retest);

        singleValueFile.delete();
        assertFalse(Files.exists(path));
    }

    @Test
    void deleteIfExists() throws IOException {
        String test = "test";
        Path path = Paths.get(TEMP_TEST_DIR, "test.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path, StandardCharsets.ISO_8859_1);
        singleValueFile.write(test);

        singleValueFile.deleteIfExists();

        assertFalse(Files.exists(path));
    }

    @Test
    void getPath() {
        Path path = Paths.get(TEMP_TEST_DIR, "test.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        assertEquals(path.toString(), singleValueFile.getPath().toString());
    }

    @Test
    void getDefaultCharset() {
        Path path = Paths.get(TEMP_TEST_DIR, "test.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        assertEquals(StandardCharsets.UTF_8, singleValueFile.getCharset());
    }

    @Test
    void getSpecifiedCharset() {
        Path path = Paths.get(TEMP_TEST_DIR, "test.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path, StandardCharsets.ISO_8859_1);
        assertEquals(StandardCharsets.ISO_8859_1, singleValueFile.getCharset());
    }

}