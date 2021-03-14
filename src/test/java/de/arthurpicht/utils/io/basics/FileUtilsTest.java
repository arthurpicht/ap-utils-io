package de.arthurpicht.utils.io.basics;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void rmDirR() {

    }

    @Test
    void testRmDirR() {
    }

    @Test
    void testRmDirR1() {
    }

    @Test
    void testRmDirR2() {
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

}