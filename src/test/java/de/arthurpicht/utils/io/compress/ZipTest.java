package de.arthurpicht.utils.io.compress;

import de.arthurpicht.utils.io.nio2.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

class ZipTest {

    @BeforeAll
    public static void prepare() {

    }

    @Test
    public void testZipWithRootDir() throws IOException {
        Path source = Paths.get("testMaterial/zip");
        assertTrue(Files.exists(source));

        Path destination = Paths.get("testTemp/testZipDir.zip");
        if (Files.exists(destination)) Files.delete(destination);
        assertTrue(Files.exists(destination.getParent()), "prepare test");
        assertFalse(Files.exists(destination), "prepare test");

        Zip.zip(source, destination, true);

        List<? extends ZipEntry> zipEntryList = Zip.getZipEntryList(destination);
        assertEquals(7, zipEntryList.size());
        assertEquals("zip/", zipEntryList.get(0).getName());
        assertEquals("zip/a/", zipEntryList.get(1).getName());
        assertEquals("zip/a/test_a.txt", zipEntryList.get(2).getName());
        assertEquals("zip/b/", zipEntryList.get(3).getName());
        assertEquals("zip/b/test_b.txt", zipEntryList.get(4).getName());
        assertEquals("zip/test1.txt", zipEntryList.get(5).getName());
        assertEquals("zip/c/", zipEntryList.get(6).getName());

        Files.delete(destination);
    }

    @Test
    public void testZipWithoutRootDir() throws IOException {
        Path source = Paths.get("testMaterial/zip");
        assertTrue(Files.exists(source), "prepare test");

        Path destination = Paths.get("testTemp/testZipWithoutDir.zip");
        if (Files.exists(destination)) Files.delete(destination);
        assertTrue(Files.exists(destination.getParent()), "prepare test");
        assertFalse(Files.exists(destination), "prepare test");

        Zip.zip(source, destination, false);

        List<? extends ZipEntry> zipEntryList = Zip.getZipEntryList(destination);
        assertEquals(7, zipEntryList.size());
        assertEquals("/", zipEntryList.get(0).getName());
        assertEquals("a/", zipEntryList.get(1).getName());
        assertEquals("a/test_a.txt", zipEntryList.get(2).getName());
        assertEquals("b/", zipEntryList.get(3).getName());
        assertEquals("b/test_b.txt", zipEntryList.get(4).getName());
        assertEquals("test1.txt", zipEntryList.get(5).getName());
        assertEquals("c/", zipEntryList.get(6).getName());

        Files.delete(destination);
    }

    @Test
    public void testZipSingleFile() throws IOException {
        Path source = Paths.get("testMaterial/zip/test1.txt");
        assertTrue(Files.exists(source), "prepare test");

        Path destination = Paths.get("testTemp/testZipSingle.zip");
        if (Files.exists(destination)) Files.delete(destination);
        assertTrue(Files.exists(destination.getParent()), "prepare test");
        assertFalse(Files.exists(destination), "prepare test");

        Zip.zip(source, destination, false);

        List<? extends ZipEntry> zipEntryList = Zip.getZipEntryList(destination);
        assertEquals(1, zipEntryList.size());
        assertEquals("test1.txt", zipEntryList.get(0).getName());

        Files.delete(destination);
    }

    @Test
    public void testUnzip() throws IOException {

        Path source = Paths.get("testMaterial/zip");
        assertTrue(Files.exists(source), "prepareTest");

        Path zipFile = Paths.get("testTemp/testZipDir.zip");
        if (Files.exists(zipFile)) Files.delete(zipFile);
        assertTrue(Files.exists(zipFile.getParent()), "prepare test");
        assertFalse(Files.exists(zipFile), "prepare test");

        Zip.zip(source, zipFile, true);

        Path unzipDestination = Files.createDirectories(zipFile.getParent().resolve("unzip"));

        Zip.unzip(zipFile, unzipDestination);

        assertTrue(Files.exists(unzipDestination.resolve("zip/a/test_a.txt")));
        assertTrue(Files.exists(unzipDestination.resolve("zip/b/test_b.txt")));
        assertTrue(Files.exists(unzipDestination.resolve("zip/c")));
        assertTrue(Files.exists(unzipDestination.resolve("zip/test1.txt")));

        Files.delete(zipFile);
        FileUtils.rmDir(unzipDestination);
    }

}