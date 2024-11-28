package de.arthurpicht.utils.io.stream;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * A limited ByteArrayOutputStream. Buffers only the last passed bytes as specified by maxSize.
 * Implemented as a ring buffer.
 */
public class LimitedByteArrayOutputStream extends OutputStream {

    private final int maxSize;
    private final byte[] buffer;

    private int count = 0;
    private int start = 0;

    public LimitedByteArrayOutputStream(int maxSize) {
        if (maxSize <= 0) throw new IllegalArgumentException("maxSize must be greater than 0");
        this.maxSize = maxSize;
        this.buffer = new byte[maxSize];
    }

    @Override
    public synchronized void write(int b) {
        buffer[(start + count) % maxSize] = (byte) b;
        if (count < maxSize) {
            count++;
        } else {
            start = (start + 1) % maxSize;
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

    @Override
    public synchronized void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override
    public synchronized String toString() {
        byte[] bytes = toByteArray();
        return new String(bytes);
    }

    public synchronized String toString(String charsetName) throws UnsupportedEncodingException {
        byte[] bytes = toByteArray();
        return new String(bytes, charsetName);
    }

    public synchronized String toString(Charset charset) {
        byte[] bytes = toByteArray();
        return new String(bytes, charset);
    }

    public synchronized byte[] toByteArray() {
        byte[] result = new byte[count];
        for (int i = 0; i < count; i++) {
            result[i] = buffer[(start + i) % maxSize];
        }
        return result;
    }

    public synchronized int size() {
        return count;
    }

    public synchronized void reset() {
        count = 0;
        start = 0;
    }

}
