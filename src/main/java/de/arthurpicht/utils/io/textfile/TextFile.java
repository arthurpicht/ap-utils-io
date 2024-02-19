package de.arthurpicht.utils.io.textfile;

import de.arthurpicht.utils.io.file.TextFileUtils;

import java.io.*;
import java.util.List;

public class TextFile {

    @Deprecated
    /**
     * Use readLinesAsStrings instead.
     */
    public static List<String> getLinesAsStrings(File file) throws IOException {
        return TextFileUtils.readLinesAsStrings(file.toPath());
    }

}
