package de.arthurpicht.utils.io.server.httpsFileServer;

import de.arthurpicht.utils.io.server.https.APHttpsServer;
import de.arthurpicht.utils.io.server.https.APHttpsServerException;

public class HttpsFileServer {

    private final APHttpsServer httpsServer;

    public static class Builder {

        private APHttpsServer httpsServer;

        public Builder withHttpsServer(APHttpsServer httpsServer) {
            this.httpsServer = httpsServer;
            return this;
        }

        public HttpsFileServer build() {
            if (this.httpsServer == null) throw new IllegalStateException("APHttpsServer not specified.");
            return new HttpsFileServer(this.httpsServer);
        }

    }

    private HttpsFileServer(
            APHttpsServer apHttpsServer
    ) {
        this.httpsServer = apHttpsServer;
    }

    public void start() throws APHttpsServerException {
        this.httpsServer.start();
    }

}
