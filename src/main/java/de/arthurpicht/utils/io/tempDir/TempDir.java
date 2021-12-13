package de.arthurpicht.utils.io.tempDir;

import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempDir {

    private final Path tempDir;
    private final boolean autoRemove;

    public TempDir(Path tempPath, boolean autoRemove) {
        this.tempDir = tempPath;
        this.autoRemove = autoRemove;
    }

    public Path asPath() {
        return this.tempDir;
    }

    public File asFile() {
        return this.tempDir.toFile();
    }

    public boolean isAutoRemove() {
        return this.autoRemove;
    }

    public void remove() {
        FileUtils.forceDeleteSilently(this.tempDir);
    }

    public boolean exists() {
        return Files.exists(this.tempDir) && Files.isDirectory(this.tempDir);
    }

}
