package de.arthurpicht.utils.io.server.httpsFileServer;

import de.arthurpicht.utils.io.nio2.FileUtils;
import de.arthurpicht.utils.io.server.https.APHttpsServer;
import de.arthurpicht.utils.io.server.https.APHttpsServerException;

import java.nio.file.Paths;

public class DemoHttpsFileServer {

    public static void main(String[] args) {

        try {
            APHttpsServer apHttpsServer = new APHttpsServer.Builder()
                    .withHttpHandler(new HttpsFileServerHandler(FileUtils.getHomeDir().resolve("work/20241014-httpsserver/content")))
                    .withPort(4433)
                    .withKeystorePath(FileUtils.getHomeDir().resolve("work/20241014-httpsserver/keystore.jks"))
                    .withKeystorePassword("geheim")
                    .build();

            HttpsFileServer httpsFileServer = new HttpsFileServer.Builder()
                    .withHttpsServer(apHttpsServer)
                    .build();


            try {
                httpsFileServer.start();
            } catch (APHttpsServerException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
