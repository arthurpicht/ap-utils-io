package de.arthurpicht.utils.io.stream;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("resource")
class LimitedByteArrayOutputStreamTest {

    @Test
    public void singleBytes() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);

        stream.write(1);
        assertEquals(1, stream.size());
        byte[] result = stream.toByteArray();
        assertArrayEquals(new byte[]{1}, result);

        stream.write(2);
        assertEquals(2, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{1, 2}, result);

        stream.write(3);
        assertEquals(3, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{1, 2, 3}, result);

        stream.write(4);
        assertEquals(4, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{1, 2, 3, 4}, result);

        stream.write(5);
        assertEquals(5, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, result);

        stream.write(6);
        assertEquals(5, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{2, 3, 4, 5, 6}, result);

        stream.write(7);
        assertEquals(5, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{3, 4, 5, 6, 7}, result);

        stream.reset();
        assertEquals(0, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{}, result);
    }

    @Test
    public void writeMultipleBytes() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);

        stream.write(new byte[]{1}, 0, 1);
        assertEquals(1, stream.size());
        byte[] result = stream.toByteArray();
        assertArrayEquals(new byte[]{1}, result);

        stream.write(new byte[]{2}, 0, 1);
        assertEquals(2, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{1, 2}, result);

        stream.write(new byte[]{3, 4}, 0, 2);
        assertEquals(4, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{1, 2, 3, 4}, result);

        stream.write(new byte[]{5, 6, 7}, 0, 2);
        assertEquals(5, stream.size());
        result = stream.toByteArray();
        assertArrayEquals(new byte[]{2, 3, 4, 5, 6}, result);
    }

    @Test
    public void writeMultipleBytesExceedingInitially() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);

        stream.write(new byte[]{1, 2, 3, 4, 5, 6, 7}, 0, 7);
        assertEquals(5, stream.size());
        byte[] result = stream.toByteArray();
        assertArrayEquals(new byte[]{3, 4, 5, 6, 7}, result);
    }

    @Test
    public void writeMultipleBytesExceedingInitiallyBySimpleWrite() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);

        stream.write(new byte[]{1, 2, 3, 4, 5, 6, 7});
        assertEquals(5, stream.size());
        byte[] result = stream.toByteArray();
        assertArrayEquals(new byte[]{3, 4, 5, 6, 7}, result);
    }

    @Test
    public void reset() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);
        stream.write(new byte[]{1, 2, 3, 4, 5, 6, 7}, 0, 7);
        stream.reset();
        assertEquals(0, stream.size());
        byte[] result = stream.toByteArray();
        assertArrayEquals(new byte[]{}, result);
    }

    @Test
    public void illegalInput() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);
        assertThrows(IndexOutOfBoundsException.class, () -> stream.write(new byte[]{1, 2, 3, 4, 5}, 0, 7));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void toStringMethod() {
        LimitedByteArrayOutputStream stream = new LimitedByteArrayOutputStream(5);
        PrintStream printStream = new PrintStream(stream);

        printStream.print("Hello world!");
        String result = stream.toString();
        assertEquals("orld!", result);

        printStream.print("Some other longe text with blah blah.");
        result = stream.toString();
        assertEquals("blah.", result);

        printStream.println("Hello world!");
        result = stream.toString();
        assertTrue(result.endsWith("\n"));
    }


}