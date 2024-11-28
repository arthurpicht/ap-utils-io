package de.arthurpicht.utils.io.file;

import de.arthurpicht.utils.io.tempDir.TempDir;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SingleValueFileTest {

    private static final String PROJECT_TEMP_DIR = "testTemp";
    private static TempDir tempDir;

    @BeforeAll
    static void createTempDir() {
        tempDir = new TempDir.Creator()
                .withParentDir(PROJECT_TEMP_DIR)
                .create();
    }

    @AfterAll
    static void deleteTempDir() {
        tempDir.remove();
    }

    @Test
    void simple() throws IOException {

        String test = "test";
        Path path = tempDir.asPath().resolve("simple.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write(test);

        String retest = singleValueFile.read();
        assertEquals(test, retest);

        singleValueFile.delete();
        assertFalse(Files.exists(path));
    }

    @Test
    void twoLines() throws IOException {
        String content = "test\nsecondLine";
        Path path = tempDir.asPath().resolve("testTwoLines.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write(content);

        String value = singleValueFile.read();
        assertEquals("test", value);

        singleValueFile.delete();
        assertFalse(Files.exists(path));
    }

    @Test
    void trailingNewline() throws IOException {
        String content = "test\n";
        Path path = tempDir.asPath().resolve("testTwoLines.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write(content);

        String value = singleValueFile.read();
        assertEquals("test", value);

        singleValueFile.delete();
        assertFalse(Files.exists(path));
    }


    @Test
    void rewrite() throws IOException {

        String test = "test";
        Path path = tempDir.asPath().resolve("rewrite.txt");

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
        Path path = tempDir.asPath().resolve("exists.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        assertFalse(singleValueFile.exists());
        singleValueFile.write("something");
        assertTrue(singleValueFile.exists());
        singleValueFile.delete();
        assertFalse(singleValueFile.exists());
    }

    @Test
    void readFromNonExistingFile() throws IOException {
        Path path = tempDir.asPath().resolve("not_existing.txt");
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
        Path path = tempDir.asPath().resolve("simpleISO.txt");

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
        Path path = tempDir.asPath().resolve("deleteIfExists.txt");

        SingleValueFile singleValueFile = new SingleValueFile(path, StandardCharsets.ISO_8859_1);
        singleValueFile.write(test);

        singleValueFile.deleteIfExists();

        assertFalse(Files.exists(path));
    }

    @Test
    void getPath() {
        Path path = tempDir.asPath().resolve("testGetPath.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        assertEquals(path.toString(), singleValueFile.getPath().toString());
    }

    @Test
    void getDefaultCharset() {
        Path path = tempDir.asPath().resolve("testGetDefaultCharset.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        assertEquals(StandardCharsets.UTF_8, singleValueFile.getCharset());
    }

    @Test
    void getSpecifiedCharset() {
        Path path = tempDir.asPath().resolve("testGetSpecifiedCharset.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path, StandardCharsets.ISO_8859_1);
        assertEquals(StandardCharsets.ISO_8859_1, singleValueFile.getCharset());
    }

    @Test
    void hasContent() throws IOException {
        Path path = tempDir.asPath().resolve("testHasContent.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write("test");
        assertTrue(singleValueFile.hasContent());

        singleValueFile.delete();
    }

    @Test
    void hasContentNeg() throws IOException {
        Path path = tempDir.asPath().resolve("testHasContentNeg.txt");
        SingleValueFile singleValueFile = new SingleValueFile(path);
        singleValueFile.write("");

        assertTrue(singleValueFile.exists());
        assertFalse(singleValueFile.hasContent());

        singleValueFile.delete();
    }

}