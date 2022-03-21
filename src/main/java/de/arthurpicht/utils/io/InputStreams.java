package de.arthurpicht.utils.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputStreams {

    public static List<String> toStrings(InputStream inputStream) throws IOException {
        List<String> strings = new ArrayList<>();
        try (Reader isr = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strings.add(line);
            }
        }
        return strings;
    }

    public static InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = InputStreams.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in resources: [" + fileName + "].");
        } else {
            return inputStream;
        }
    }

}
