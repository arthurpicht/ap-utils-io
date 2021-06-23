package de.arthurpicht.utils.io.nio2;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void rmDirR() throws IOException {
        try {
            Files.createDirectories(Paths.get("testTemp/a/b/c"));
            Files.createFile(Paths.get("testTemp/a/file_a.txt"));
            Files.createFile(Paths.get("testTemp/a/b/c/file_c.txt"));
        } catch (FileAlreadyExistsException e) {
            // din
        }

        Path path = Paths.get("testTemp/a");
        assertTrue(Files.exists(path));
        FileUtils.rmDir(path);
        assertFalse(Files.exists(path));
    }

    @Test
    void rmDirR_notExisting_neg() throws IOException {
        Path path = Paths.get("testTemp/not_existing");
        assertFalse(Files.exists(path));
        try {
            FileUtils.rmDir(path);
            fail(IllegalArgumentException.class.getSimpleName() + " expected.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No such directory"));
        }
    }

    @Test
    void rmDirR_noDir_neg() throws IOException {
        Path path = Paths.get("testTemp/testFile.txt");
        Files.createFile(path);
        assertTrue(Files.exists(path));
        try {
            FileUtils.rmDir(path);
            fail(IllegalArgumentException.class.getSimpleName() + " expected.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No such directory"));
        }
        Files.delete(path);
        assertFalse(Files.exists(path));
    }

    @Test
    void findDeepest1() throws IOException {
        Path deepest = FileUtils.findDeepest(Paths.get("testMaterial"));
        assertEquals("testMaterial/level1/level1_2/level1_2_1/level1_2_1_1", deepest.toString());
    }

    @Test
    void findDeepest2() throws IOException {
        Path deepest = FileUtils.findDeepest(Paths.get("testMaterial/level1/level1_2/level1_2_1/level1_2_1_1"));
        assertEquals("testMaterial/level1/level1_2/level1_2_1/level1_2_1_1", deepest.toString());
    }

    @Test
    void findDeepest_notExisting_neg() throws IOException {
        try {
            FileUtils.findDeepest(Paths.get("testMaterial/notExisting"));
            fail(IllegalArgumentException.class.getSimpleName() + " expected.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No such directory"));
        }
    }

    @Test
    void findDeepest_noDirectory_neg() throws IOException {
        try {
            FileUtils.findDeepest(Paths.get("testMaterial/level1/level1_2/level1_2_1/file1_2_1__1.txt"));
            fail(IllegalArgumentException.class.getSimpleName() + " expected.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No such directory"));
        }
    }


    @Test
    void getDepthLeaf() throws IOException {
        int depth = FileUtils.getDepth(Paths.get("testMaterial/level1/level1_2/level1_2_1/level1_2_1_1"));
        assertEquals(0, depth);
    }

    @Test
    void getDepthLeafMinusOne() throws IOException {
        int depth = FileUtils.getDepth(Paths.get("testMaterial/level1/level1_2/level1_2_1"));
        assertEquals(1, depth);
    }

    @Test
    void getDepthLeafMinusTwo() throws IOException {
        int depth = FileUtils.getDepth(Paths.get("testMaterial/level1/level1_2"));
        assertEquals(2, depth);
    }

    @Test
    void getDepthLeafMinusFour() throws IOException {
        int depth = FileUtils.getDepth(Paths.get("testMaterial"));
        assertEquals(4, depth);
    }

    @Test
    void getDepth_notExisting_neg() throws IOException {
        try {
            FileUtils.getDepth(Paths.get("testMaterial/notExisting"));
            fail(IllegalArgumentException.class.getSimpleName() + " expected.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No such directory"));
        }
    }

    @Test
    void getDepth_noDirectory_neg() throws IOException {
        try {
            FileUtils.getDepth(Paths.get("testMaterial/level1/level1_2/level1_2_1/file1_2_1__1.txt"));
            fail(IllegalArgumentException.class.getSimpleName() + " expected.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No such directory"));
        }
    }

    @Test
    void toCanonicalPath1() {
        Path path = Paths.get("test.txt");
        Path canonicalPath = FileUtils.toCanonicalPath(path);

        assertTrue(canonicalPath.isAbsolute());
        assertFalse(canonicalPath.toString().contentEquals("." + File.separator));
        assertFalse(canonicalPath.toString().contentEquals(".." + File.separator));
    }

    @Test
    void toCanonicalPath2() {
        Path path = Paths.get("../test.txt");
        Path canonicalPath = FileUtils.toCanonicalPath(path);

        assertTrue(canonicalPath.isAbsolute());
        assertFalse(canonicalPath.toString().contentEquals("." + File.separator));
        assertFalse(canonicalPath.toString().contentEquals(".." + File.separator));
    }

    @Test
    void toCanonicalPath3() {
        Path path = Paths.get("some/../test.txt");
        Path canonicalPath = FileUtils.toCanonicalPath(path);

        assertTrue(canonicalPath.isAbsolute());
        assertFalse(canonicalPath.toString().contentEquals("." + File.separator));
        assertFalse(canonicalPath.toString().contentEquals(".." + File.separator));
    }

    @Test
    void toCanonicalPath4() {
        Path path = Paths.get("/some/../test.txt");
        Path canonicalPath = FileUtils.toCanonicalPath(path);

        assertTrue(canonicalPath.isAbsolute());
        assertFalse(canonicalPath.toString().contentEquals("." + File.separator));
        assertFalse(canonicalPath.toString().contentEquals(".." + File.separator));
    }

    @Test
    void toCanonicalPath5() {
        Path path = Paths.get("./test.txt");
        Path canonicalPath = FileUtils.toCanonicalPath(path);

        assertTrue(canonicalPath.isAbsolute());
        assertFalse(canonicalPath.toString().contentEquals("." + File.separator));
        assertFalse(canonicalPath.toString().contentEquals(".." + File.separator));
    }

    @Test
    void getWorkingDir() {
        Path path = FileUtils.getWorkingDir();
        assertTrue(path.isAbsolute());
        assertFalse(path.toString().contentEquals("." + File.separator));
        assertFalse(path.toString().contentEquals(".." + File.separator));
        assertEquals("utils-io", path.getFileName().toString());
    }

    @Test
    void isChild() {
        Path reference = FileUtils.getWorkingDir();
        Path element = Paths.get("some.txt");
        assertTrue(FileUtils.isChild(reference, element));
    }

    @Test
    void isChild_neg() {
        Path reference = FileUtils.getWorkingDir();
        Path element = FileUtils.getWorkingDir();
        assertFalse(FileUtils.isChild(reference, element));
    }

    @Test
    void isChild_neg2() {
        Path reference = Paths.get("/a/b/c");
        Path element = Paths.get("/a/b/some.txt");
        assertFalse(FileUtils.isChild(reference, element));
    }

    @Test
    void isChild1() {
        Path reference = Paths.get("/a/b/c");
        Path element = Paths.get("/a/b/c/some.txt");
        assertTrue(FileUtils.isChild(reference, element));
    }

}