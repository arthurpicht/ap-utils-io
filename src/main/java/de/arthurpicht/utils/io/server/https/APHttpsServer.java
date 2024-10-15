package de.arthurpicht.utils.io.server.https;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import de.arthurpicht.utils.core.strings.Strings;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;

/**
 * Create private key and self-signed certificate:
 * > openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout private.key -out domain.crt
 * Java keystore is built based on:
 * - private.key
 * - domain.crt
 * - domain.ca-bundle (optionally)
 * Build keystore from files with bundle:
 * > openssl pkcs12 -export -out keystore.pkcs12 -inkey private.key -certfile domain.ca-bundle -in domain.crt
 * or without bundle:
 * > openssl pkcs12 -export -out keystore.pkcs12 -inkey private.key -in domain.crt
 * Build java keystore from pkcs12 keystore:
 * > keytool -v -importkeystore -srckeystore keystore.pkcs12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype pkcs12
 */
public class APHttpsServer {

    private final HttpHandler httpHandler;
    private final int port;
    private final Path keystorePath;
    private final char[] keystorePassword;
    private final String httpPath;

    public static class Builder {

        private HttpHandler httpHandler;
        private int port = 443;
        private Path keystorePath;
        private String keystorePassword;
        private String httpPath = "/";

        public Builder withHttpHandler(HttpHandler httpHandler) {
            this.httpHandler = httpHandler;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withKeystorePath(Path keystorePath) {
            this.keystorePath = keystorePath;
            return this;
        }

        public Builder withKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
            return this;
        }

        public Builder withHttpPath(String httpPath) {
            this.httpPath = httpPath;
            return this;
        }

        public APHttpsServer build() {
            if (this.httpHandler == null) throw new IllegalStateException("HttpHandler not specified.");
            if (this.keystorePath == null) throw new IllegalStateException("KeystorePath not specified.");
            char[] keystorePassword = null;
            if (Strings.isSpecified(this.keystorePassword)) {
                keystorePassword = this.keystorePassword.toCharArray();
            }
            return new APHttpsServer(
                    this.httpHandler,
                    this.port,
                    this.keystorePath,
                    keystorePassword,
                    this.httpPath
            );
        }

    }

    private APHttpsServer(HttpHandler httpHandler, int port, Path keystorePath, char[] keystorePassword, String httpPath) {
        this.httpHandler = httpHandler;
        this.port = port;
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        if (Strings.isSpecified(httpPath)) {
            this.httpPath = httpPath;
        } else {
            this.httpPath = "/";
        }
    }

    public void start() throws APHttpsServerException {

        try {
            InetSocketAddress address = new InetSocketAddress(this.port);

            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(this.keystorePath.toFile());
            ks.load(fis, this.keystorePassword);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, this.keystorePassword);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                @Override
                public void configure(HttpsParameters params) {
                    try {
                        SSLContext c = getSSLContext();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        SSLParameters sslParameters = c.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to prepare HttpConfigurator: " + ex.getMessage(), ex);
                    }
                }
            });
            httpsServer.createContext(this.httpPath, this.httpHandler);
            httpsServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            httpsServer.start();

        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException |
                 UnrecoverableKeyException | CertificateException e) {
            throw new APHttpsServerException("Failed to create HTTPS server on port " + this.port + ":" + e.getMessage(), e);
        }
    }

}
