package de.arthurpicht.utils.io.net;

import java.io.IOException;
import java.net.Socket;

public class Sockets {

    public static boolean isPortListening(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
