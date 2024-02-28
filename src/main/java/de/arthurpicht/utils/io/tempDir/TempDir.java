package de.arthurpicht.utils.io.tempDir;

import de.arthurpicht.utils.io.assertions.PathAssertions;
import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Configurable functionality for managing a temporary directory with unique directory name.
 * Temporary directory is configured and created by using enclosed {@link Creator} class.
 * Use the resulting {@link TempDir} class for referencing and - if applicable - deleting.
 */
public class TempDir {

    private final Path tempDir;
    private final boolean autoRemove;

    public static class TempDirCreationException extends RuntimeException {
        public TempDirCreationException(Path tempDir, IOException e) {
            super("Exception on creating temporary directory [" + tempDir.toAbsolutePath() + "]: " + e.getMessage(), e);
        }
    }

    public static class Creator {
        private Path parentDir = Paths.get(System.getProperty("java.io.tmpdir"));
        private String tempDirNamePrefix = "";
        private String tempDirSuffix = "";
        private boolean autoRemove = true;

        /**
         * Parent directory. Must exist. Default: system temp dir.
         */
        public Creator withParentDir(Path parentDir) {
            this.parentDir = parentDir;
            return this;
        }

        /**
         * Parent directory. Default: system temp dir.
         */
        public Creator withParentDir(String first, String... more) {
            this.parentDir = Paths.get(first, more);
            return this;
        }

        /**
         * With no auto removal of temp directory on process termination.
         * Default: temp directory is removed automatically.
         */
        public Creator withNoAutoRemove() {
            this.autoRemove = false;
            return this;
        }

        /**
         * Specifies auto removal of temp directory on process termination.
         * Default: false.
         */
        public Creator withAutoRemove(boolean autoRemove) {
            this.autoRemove = autoRemove;
            return this;
        }

        /**
         * Specifies prefix of temp directory name. Default: empty.
         */
        public Creator withTempDirPrefix(String tempDirPrefix) {
            this.tempDirNamePrefix = tempDirPrefix;
            return this;
        }

        /**
         * Specifies suffix of temp directory name. Default: empty.
         */
        public Creator withTempDirSuffix(String tempDirSuffix) {
            this.tempDirSuffix = tempDirSuffix;
            return this;
        }

        /**
         * Create temporary directory.
         * @return TempDir class
         * @throws TempDirCreationException if creation fails
         */
        public TempDir create() {
            PathAssertions.assertIsExistingDirectory(this.parentDir);
            String dirName =
                    this.tempDirNamePrefix
                    + UUID.randomUUID()
                    + this.tempDirSuffix;
            Path tempDirPath = this.parentDir.resolve(dirName);
            try {
                Files.createDirectory(tempDirPath);
            } catch (IOException e) {
                throw new TempDirCreationException(tempDirPath, e);
            }
            if (this.autoRemove) FileUtils.forceDeleteOnShutdown(tempDirPath);
            return new TempDir(tempDirPath, this.autoRemove);
        }

    }

    // Set to private if deprecated class TempDirs is removed
    public TempDir(Path tempDir, boolean autoRemove) {
        this.tempDir = tempDir;
        this.autoRemove = autoRemove;
    }

    /**
     * @return temporary directory as object of Type {@link Path}
     */
    public Path asPath() {
        return this.tempDir;
    }

    /**
     * @return temporary directory as object of Type {@link File}
     */
    public File asFile() {
        return this.tempDir.toFile();
    }

    /**
     * @return if temporary directory is removed automatically on java process termination.
     */
    public boolean isAutoRemove() {
        return this.autoRemove;
    }

    /**
     * Removes temporary directory silently.
     */
    public void remove() {
        FileUtils.forceDeleteSilently(this.tempDir);
    }

    /**
     * @return checks if referenced temporary directory exists.
     */
    public boolean exists() {
        return Files.exists(this.tempDir) && Files.isDirectory(this.tempDir);
    }

}
