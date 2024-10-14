package de.arthurpicht.utils.io.server.https;

import de.arthurpicht.utils.io.nio2.FileUtils;

public class DemoHttpsServer {

    public static void main(String[] args) {

        APHttpsServer apHttpsServer = new APHttpsServer(
                new DemoHttpHandler(),
                4433,
                FileUtils.getHomeDir().resolve("work/20241014-httpsserver/keystore.jks"),
                "geheim",
                "/"
        );

        try {
            apHttpsServer.start();
        } catch (APHttpsServerException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
