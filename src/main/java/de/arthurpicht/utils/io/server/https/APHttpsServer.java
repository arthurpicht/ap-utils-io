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
 * openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout private.key -out domain.crt
 * Java keystore is built based on:
 * - private.key
 * - domain.crt
 * - domain.ca-bundle (optionally)
 * Build java keystore from files:
 * openssl pkcs12 -export -out keystore.pkcs12 -inkey private.key -certfile domain.ca-bundle -in domain.crt
 * keytool -v -importkeystore -srckeystore keystore.pkcs12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype pkcs12
 */
public class APHttpsServer {

    private final HttpHandler httpHandler;
    private final int port;
    private final Path keystorePath;
    private final char[] keystorePassword;
    private final String httpPath;

    public APHttpsServer(HttpHandler httpHandler, int port, Path keystorePath, String keystorePassword, String httpPath) {
        this.httpHandler = httpHandler;
        this.port = port;
        this.keystorePath = keystorePath;
        if (Strings.isSpecified(keystorePassword)) {
            this.keystorePassword = keystorePassword.toCharArray();
        } else {
            this.keystorePassword = null;
        }
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
