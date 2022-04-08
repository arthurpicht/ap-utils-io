package de.arthurpicht.utils.io.checksum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.CRC32;

public class Checksums {

    public static long computeCrc32(Path path) throws IOException {
        CRC32 crc32 = new CRC32();
        InputStream inputStream = new FileInputStream(path.toFile());
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            crc32.update(buffer, 0, bytesRead);
        }
        return crc32.getValue();
    }

    public static long computeCrc32(List<Path> paths) throws IOException {
        CRC32 crc32 = new CRC32();
        for (Path file : paths) {
            InputStream inputStream = new FileInputStream(file.toFile());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, bytesRead);
            }
        }
        return crc32.getValue();
    }

}
