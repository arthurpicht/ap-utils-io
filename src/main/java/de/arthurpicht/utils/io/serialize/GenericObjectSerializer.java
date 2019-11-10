package de.arthurpicht.utils.io.serialize;

import java.io.*;

public class GenericObjectSerializer {

    /**
     * Serializes specified object to specified file.
     *
     * @param object Object of type T, to be serialized
     * @param file destination file
     * @throws SerializerException - if an error occurs
     */
    public static<T> void serialize(T object, File file) throws SerializerException {

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            throw new SerializerException(e);
        }
    }

    /**
     * Reads stream from passed file and deserializes stream
     * to instance of passed transient object.
     *
     * @param file source file
     * @return Object of type T
     * @throws SerializerException - if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(File file) throws SerializerException {

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializerException(e);
        }
    }

}
