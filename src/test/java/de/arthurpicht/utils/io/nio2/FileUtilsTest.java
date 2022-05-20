package de.arthurpicht.utils.io.nio2;

import de.arthurpicht.utils.io.assertions.PathAssertionException;
import de.arthurpicht.utils.io.tempDir.TempDir;
import de.arthurpicht.utils.io.tempDir.TempDirs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    private static final String PROJECT_TEMP_DIR = "testTemp";

    private static Path rootOfTree;

    @BeforeAll
    public static void setUp() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path rootOfTree = tempDir.asPath().resolve("rootOfTree");
        Path level_1 = Files.createDirectories(rootOfTree.resolve("level_1"));
        Files.createFile(level_1.resolve("file_1__1.txt"));
        Path level_1_1_1 = Files.createDirectories(rootOfTree.resolve("level_1/level_1_1/level_1_1_1"));
        Files.createFile(level_1_1_1.resolve("file_1_1_1__1.txt"));
        Path level_1_2_1 = Files.createDirectories(rootOfTree.resolve("level_1/level_1_2/level_1_2_1"));
        Files.createFile(level_1_2_1.resolve("file_1_2_1__1.txt"));
        Path level_1_2_1_1 = Files.createDirectories(rootOfTree.resolve("level_1/level_1_2/level_1_2_1/level_1_2_1_1"));
        Files.createFile(level_1_2_1_1.resolve("file_1_2_1_1__1.txt"));
        Path level_1_3 = Files.createDirectories(rootOfTree.resolve("level_1/level_3"));
        Files.createFile(level_1_3.resolve("file_1_3__1.txt"));
        FileUtilsTest.rootOfTree = rootOfTree;
    }

    @Test
    void rmDirR() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();

        Files.createDirectories(tempDir.resolve("a/b/c"));
        Files.createFile(tempDir.resolve("a/file_a.txt"));
        Files.createFile(tempDir.resolve("a/b/c/file_c.txt"));


        Path path = tempDir.resolve("a");
        assertTrue(Files.exists(path));
        FileUtils.rmDir(path);
        assertFalse(Files.exists(path));
    }

    @Test
    void rmDirR_notExisting_neg() {
        Path noPath = Paths.get(UUID.randomUUID().toString());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FileUtils.rmDir(noPath));
        assertTrue(e.getMessage().contains("No such directory"));
    }

    @Test
    void rmDirR_noDir_neg() throws IOException {
        // prepare
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();
        Path file = tempDir.resolve("testFile.txt");
        Files.createFile(file);
        assertTrue(Files.exists(file));
        // test
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FileUtils.rmDir(file));
        assertTrue(e.getMessage().contains("No such directory"));
    }

    @Test
    void findDeepest1() throws IOException {
        Path deepest = FileUtils.findDeepest(rootOfTree);
        assertEquals(7, deepest.getNameCount());
        Path deepestSubpath = deepest.subpath(2, deepest.getNameCount());
        assertEquals("rootOfTree/level_1/level_1_2/level_1_2_1/level_1_2_1_1", deepestSubpath.toString());
    }

    @Test
    void findDeepest2() throws IOException {
        Path queryPath = rootOfTree.resolve("level_1/level_1_2/level_1_2_1/level_1_2_1_1");
        Path deepest = FileUtils.findDeepest(queryPath);
        assertEquals(7, deepest.getNameCount());
        Path deepestSubpath = deepest.subpath(2, deepest.getNameCount());
        assertEquals("rootOfTree/level_1/level_1_2/level_1_2_1/level_1_2_1_1", deepestSubpath.toString());
    }

    @Test
    void findDeepest_notExisting_neg() {
        Path noPath = Paths.get(UUID.randomUUID().toString());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FileUtils.findDeepest(noPath));
        assertTrue(e.getMessage().contains("No such directory"));
    }

    @Test
    void findDeepest_noDirectory_neg() {
        Path existingFile = rootOfTree.resolve("level_1/level_1_2/level_1_2_1/file_1_2_1__1.txt");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> FileUtils.findDeepest(existingFile));
        assertTrue(e.getMessage().contains("No such directory"));
    }

    @Test
    void getDepthLeaf() throws IOException {
        Path deepestLeaf = rootOfTree.resolve("level_1/level_1_2/level_1_2_1/level_1_2_1_1");
        int depth = FileUtils.getDepth(deepestLeaf);
        assertEquals(0, depth);
    }

    @Test
    void getDepthLeafMinusOne() throws IOException {
        Path parentOfDeepestLeaf = rootOfTree.resolve("level_1/level_1_2/level_1_2_1");
        int depth = FileUtils.getDepth(parentOfDeepestLeaf);
        assertEquals(1, depth);
    }

    @Test
    void getDepthLeafMinusTwo() throws IOException {
        Path deepestLeafMinusTwo = rootOfTree.resolve("level_1/level_1_2");
        int depth = FileUtils.getDepth(deepestLeafMinusTwo);
        assertEquals(2, depth);
    }

    @Test
    void getDepthLeafMinusFour() throws IOException {
        int depth = FileUtils.getDepth(rootOfTree);
        assertEquals(4, depth);
    }

    @Test
    void getDepth_notExisting_neg() {
        Path noPath = Paths.get(UUID.randomUUID().toString());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FileUtils.getDepth(noPath));
        assertTrue(e.getMessage().contains("No such directory"));
    }

    @Test
    void getDepth_noDirectory_neg() {
        Path existingFile = rootOfTree.resolve("level_1/level_1_2/level_1_2_1/file_1_2_1__1.txt");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> FileUtils.getDepth(existingFile));
        assertTrue(e.getMessage().contains("No such directory"));
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

    @Test
    void isPathEndingWithFileName() {
        Path path = Paths.get("/a/b/c");
        String filename = "c";
        assertTrue(FileUtils.isPathEndingWithFileName(path, filename));
    }

    @Test
    void isPathEndingWithFileNameSlash() {
        Path path = Paths.get("/a/b/c/");
        String filename = "c";
        assertTrue(FileUtils.isPathEndingWithFileName(path, filename));
    }

    @Test
    void isPathEndingWithFileName_neg() {
        Path path = Paths.get("/a/b/c");
        String filename = "x";
        assertFalse(FileUtils.isPathEndingWithFileName(path, filename));
    }

    @Test
    void copyDirectory_destinationNotExisting() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();

        Path source = Files.createDirectories(tempDir.resolve("source"));
        Files.createDirectories(source.resolve("b/c"));
        Files.createFile(source.resolve("b/file_a.txt"));
        Files.createFile(source.resolve("b/c/file_c.txt"));

        Path destination = tempDir.resolve("here/the/destination");

        FileUtils.copyDirectory(source, destination);

        assertTrue(FileUtils.isExistingDirectory(destination.resolve("b/c")));
        assertTrue(FileUtils.isExistingRegularFile(destination.resolve("b/file_a.txt")));
        assertTrue(FileUtils.isExistingRegularFile(destination.resolve("b/c/file_c.txt")));
    }

    @Test
    void copyDirectory_destinationPreExisting() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();

        Path source = Files.createDirectories(tempDir.resolve("source"));
        Files.createDirectories(source.resolve("b/c"));
        Files.createFile(source.resolve("b/file_a.txt"));
        Files.createFile(source.resolve("b/c/file_c.txt"));

        Path destination = Files.createDirectories(tempDir.resolve("here/the/destination"));

        FileUtils.copyDirectory(source, destination);

        assertTrue(FileUtils.isExistingDirectory(destination.resolve("b/c")));
        assertTrue(FileUtils.isExistingRegularFile(destination.resolve("b/file_a.txt")));
        assertTrue(FileUtils.isExistingRegularFile(destination.resolve("b/c/file_c.txt")));
    }

    @Test
    void getContainingFiles() throws IOException {
        List<Path> files = FileUtils.getContainingFiles(rootOfTree);
        assertEquals(5, files.size());
    }

    @Test
    void hasSubdirectories() throws IOException {
        boolean hasSubdirectories = FileUtils.hasSubdirectories(rootOfTree);
        assertTrue(hasSubdirectories);
    }

    @Test
    void hasSubdirectoriesNeg() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();
        boolean hasSubdirectories = FileUtils.hasSubdirectories(tempDir);
        assertFalse(hasSubdirectories);
    }

    @Test
    void forceDelete() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();
        Path targetDir = Files.createDirectories(tempDir.resolve("test"));
        Files.createFile(targetDir.resolve("test.txt"));

        FileUtils.forceDelete(targetDir);

        assertFalse(Files.exists(targetDir));
    }

    @Test
    void forceDeleteNeg() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();
        Path targetDir = tempDir.resolve("test");

        IllegalArgumentException e
                = assertThrows(IllegalArgumentException.class, () -> FileUtils.forceDelete(targetDir));
        assertTrue(e.getMessage().startsWith("No such file or directory: "));
    }

    @Test
    void forceDeleteSilently() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();
        Path targetDir = Files.createDirectories(tempDir.resolve("test"));
        Files.createFile(targetDir.resolve("test.txt"));

        FileUtils.forceDeleteSilently(targetDir);

        assertFalse(Files.exists(targetDir));
    }

    @Test
    void forceDeleteSilentlyNotExisting() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();
        Path targetDir = tempDir.resolve("test");

        FileUtils.forceDeleteSilently(targetDir);
    }

    @Test
    void getSubdirectories() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);

        Path subdirectory1 = tempDir.asPath().resolve(UUID.randomUUID().toString());
        Files.createDirectory(subdirectory1);
        assertTrue(FileUtils.isExistingDirectory(subdirectory1));

        Path subdirectory2 = tempDir.asPath().resolve(UUID.randomUUID().toString());
        Files.createDirectory(subdirectory2);
        assertTrue(FileUtils.isExistingDirectory(subdirectory2));

        Path subdirectory3 = tempDir.asPath().resolve(UUID.randomUUID().toString());
        Files.createDirectory(subdirectory3);
        assertTrue(FileUtils.isExistingDirectory(subdirectory3));

        List<Path> subdirectories = FileUtils.getSubdirectories(tempDir.asPath());

        assertEquals(3, subdirectories.size());
        assertTrue(subdirectories.contains(subdirectory1));
        assertTrue(subdirectories.contains(subdirectory2));
        assertTrue(subdirectories.contains(subdirectory3));
    }

    @Test
    void getSubdirectoriesEmpty() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        List<Path> subdirectories = FileUtils.getSubdirectories(tempDir.asPath());
        assertEquals(0, subdirectories.size());
    }

    @Test
    void getSubdirectories_neg() throws IOException {
        try {
            FileUtils.getSubdirectories(Paths.get(UUID.randomUUID().toString()));
            fail(PathAssertionException.class.getSimpleName() + " expected.");
        } catch (PathAssertionException e) {
            // din
        }
    }

    @Test
    void getSubdirectoriesNotEndingWithTilde() throws IOException {
        Path tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR).asPath();

        Path subdirectory1 = tempDir.resolve(UUID.randomUUID().toString());
        Files.createDirectory(subdirectory1);
        assertTrue(FileUtils.isExistingDirectory(subdirectory1));

        Path subdirectory2Deactivated = tempDir.resolve(UUID.randomUUID() + "~");
        assertTrue(subdirectory2Deactivated.getFileName().toString().endsWith("~"));

        Files.createDirectory(subdirectory2Deactivated);
        assertTrue(FileUtils.isExistingDirectory(subdirectory2Deactivated));

        Path subdirectory3 = tempDir.resolve(UUID.randomUUID().toString());
        Files.createDirectory(subdirectory3);
        assertTrue(FileUtils.isExistingDirectory(subdirectory3));

        List<Path> subdirectories = FileUtils.getSubdirectoriesNotEndingWithTilde(tempDir);

        assertEquals(2, subdirectories.size());
        assertTrue(subdirectories.contains(subdirectory1));
        assertTrue(subdirectories.contains(subdirectory3));
    }

    @Test
    void isExistingDir() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        assertTrue(FileUtils.isExistingDirectory(tempDir.asPath()));
    }

    @Test
    void isExistingDir_neg() {
        assertFalse(FileUtils.isExistingDirectory((Paths.get(UUID.randomUUID().toString()))));
    }

    @Test
    void isSubdirectory() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = tempDir.asPath().resolve("sub1");
        Files.createDirectory(subDir);

        assertTrue(FileUtils.isSubdirectory(tempDir.asPath(), subDir));
    }

    @Test
    void isSubdirectorySubSub() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = tempDir.asPath().resolve("sub1").resolve("sub2");
        Files.createDirectories(subDir);

        assertTrue(FileUtils.isSubdirectory(tempDir.asPath(), subDir));
    }

    @Test
    void isSubdirectory_neg() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        assertFalse(FileUtils.isSubdirectory(tempDir.asPath(), tempDir.asPath()));
        assertFalse(FileUtils.isSubdirectory(tempDir.asPath(), tempDir.asPath().resolve("..")));
    }

    @Test
    void isSubdirectory_notExistingSubDir_neg() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        try {
            FileUtils.isSubdirectory(tempDir.asPath(), tempDir.asPath().resolve("not_existing"));
            fail(PathAssertionException.class.getSimpleName() + " expected");
        } catch (PathAssertionException e) {
            // din
        }
    }

    @Test
    void isSubdirectory_notExistingDir_neg() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        try {
            FileUtils.isSubdirectory(Paths.get(UUID.randomUUID().toString()), tempDir.asPath());
            fail(PathAssertionException.class.getSimpleName() + " expected");
        } catch (PathAssertionException e) {
            // din
        }
    }

    @Test
    void isDirectSubdirectory() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = tempDir.asPath().resolve("sub");
        Files.createDirectory(subDir);

        assertTrue(FileUtils.isDirectSubdirectory(tempDir.asPath(), subDir));
    }

    @Test
    void isDirectSubdirectorySubSub() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = tempDir.asPath().resolve("sub1").resolve("sub2");
        Files.createDirectories(subDir);

        assertFalse(FileUtils.isDirectSubdirectory(tempDir.asPath(), subDir));
    }

    @Test
    void isDirectSubdirectoryNotNormalized() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = tempDir.asPath().resolve("sub1");
        Files.createDirectory(subDir);

        assertTrue(FileUtils.isDirectSubdirectory(tempDir.asPath(), tempDir.asPath().resolve("sub1").resolve("..").resolve("sub1")));
    }

    @Test
    void isDirectSubdirectoryNotNormalizedSubSub() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Path subDir = tempDir.asPath().resolve("sub1").resolve("sub2");
        Files.createDirectories(subDir);

        assertFalse(FileUtils.isDirectSubdirectory(tempDir.asPath(), tempDir.asPath().resolve("sub1").resolve("..").resolve("sub1").resolve("sub2")));
    }

    @Test
    void getRegularNonHiddenFilesInDirectory() throws IOException {
        TempDir tempDir = TempDirs.createUniqueTempDirAutoRemove(PROJECT_TEMP_DIR);
        Files.createFile(tempDir.asPath().resolve("a"));
        Files.createFile(tempDir.asPath().resolve("b"));
        Files.createFile(tempDir.asPath().resolve(".c"));
        Files.createDirectory(tempDir.asPath().resolve("dir"));

        List<Path> fileList = FileUtils.getRegularNonHiddenFilesInDirectory(tempDir.asPath());

        assertEquals(2, fileList.size());
    }

}