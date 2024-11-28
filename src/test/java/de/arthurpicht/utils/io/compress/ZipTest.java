package de.arthurpicht.utils.io.compress;

import de.arthurpicht.utils.io.nio2.FileUtils;
import de.arthurpicht.utils.io.tempDir.TempDir;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipTest {

    private static TempDir tempDir;
    private static Path zipDir;

    @BeforeAll
    public static void prepare() throws IOException {
        tempDir = new TempDir.Creator()
                .withParentDir("testTemp")
                .create();
        zipDir = tempDir.asPath().resolve("testMaterial/zip");
        Path aDir = Files.createDirectories(zipDir.resolve("a"));
        Path bDir = Files.createDirectories(zipDir.resolve("b"));
        Files.createDirectories(zipDir.resolve("c"));
        Files.writeString(zipDir.resolve("test1.txt"), "content of test1.txt");
        Files.writeString(aDir.resolve("test_a.txt"), "content of test_a.txt");
        Files.writeString(bDir.resolve("test_b.txt"), "content of test_b.txt");
    }

    @AfterAll
    public static void cleanup() {
        tempDir.remove();
    }

    @Test
    public void testZipWithRootDir() throws IOException {
        if (!Files.exists(zipDir)) throw new IllegalStateException("TempDir not existing.");

        Path source = zipDir;
        Path destination = tempDir.asPath().resolve("testZipDir.zip");
        if (Files.exists(destination)) Files.delete(destination);

        Zip.zip(source, destination, true);

        List<? extends ZipEntry> zipEntryList = Zip.getZipEntryList(destination);
        assertEquals(7, zipEntryList.size());

        Set<String> entryNames = Zip.getZipEntryNames(destination);
        assertTrue(entryNames.contains("zip/"));
        assertTrue(entryNames.contains("zip/a/"));
        assertTrue(entryNames.contains("zip/a/test_a.txt"));
        assertTrue(entryNames.contains("zip/b/"));
        assertTrue(entryNames.contains("zip/b/test_b.txt"));
        assertTrue(entryNames.contains("zip/test1.txt"));
        assertTrue(entryNames.contains("zip/c/"));

        Files.delete(destination);
    }

    @Test
    public void testZipWithoutRootDir() throws IOException {
        if (!Files.exists(zipDir)) throw new IllegalStateException("TempDir not existing.");

        Path source = zipDir;
        Path destination = tempDir.asPath().resolve("testZipWithoutDir.zip");
        if (Files.exists(destination)) Files.delete(destination);

        Zip.zip(source, destination, false);

        List<? extends ZipEntry> zipEntryList = Zip.getZipEntryList(destination);
        assertEquals(7, zipEntryList.size());

        Set<String> zipEntryNames = Zip.getZipEntryNames(destination);
        assertTrue(zipEntryNames.contains("/"));
        assertTrue(zipEntryNames.contains("a/"));
        assertTrue(zipEntryNames.contains("a/test_a.txt"));
        assertTrue(zipEntryNames.contains("b/"));
        assertTrue(zipEntryNames.contains("b/test_b.txt"));
        assertTrue(zipEntryNames.contains("test1.txt"));
        assertTrue(zipEntryNames.contains("c/"));

        Files.delete(destination);
    }

    @Test
    public void testZipSingleFile() throws IOException {
        Path source = zipDir.resolve("test1.txt");
        if (!Files.exists(source)) throw new IllegalStateException(source.toAbsolutePath() + " not found.");

        Path destination = tempDir.asPath().resolve("testZipSingle.zip");
        if (Files.exists(destination)) Files.delete(destination);

        Zip.zip(source, destination, false);

        List<? extends ZipEntry> zipEntryList = Zip.getZipEntryList(destination);
        assertEquals(1, zipEntryList.size());
        assertEquals("test1.txt", zipEntryList.getFirst().getName());

        Files.delete(destination);
    }

    @Test
    public void testUnzip() throws IOException {
        Path source = zipDir;
        if (!Files.exists(zipDir)) throw new IllegalStateException(source.toAbsolutePath() + " not found.");

        Path zipFile = tempDir.asPath().resolve("testZipDir.zip");
        if (Files.exists(zipFile)) Files.delete(zipFile);

        Zip.zip(source, zipFile, true);

        Path unzipDestination = tempDir.asPath().resolve("unzip");

        Zip.unzip(zipFile, unzipDestination);

        assertTrue(Files.exists(unzipDestination.resolve("zip/a/test_a.txt")));
        assertTrue(Files.exists(unzipDestination.resolve("zip/b/test_b.txt")));
        assertTrue(Files.exists(unzipDestination.resolve("zip/c")));
        assertTrue(Files.exists(unzipDestination.resolve("zip/test1.txt")));

        Files.delete(zipFile);
        FileUtils.rmDir(unzipDestination);
    }

}