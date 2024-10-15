package de.arthurpicht.utils.io.server.httpsFileServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpsFileServerHandler implements HttpHandler {

    private final Path basePath;

    public HttpsFileServerHandler(Path basePath) {
        this.basePath = basePath;
        if (!FileUtils.isExistingDirectory(this.basePath))
            throw new IllegalArgumentException("Base directory [" + this.basePath.toAbsolutePath() + "] not existing.");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("handle https request ...");
        try {
            work(exchange);
        } catch (Exception e) {
            System.out.println("Error dudu: " + e.getMessage());
            System.out.println("haaaaalo!");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            String sStackTrace = sw.toString();
            System.out.println("stacktrace: " + sStackTrace);
        }
    }

    private void work(HttpExchange exchange) throws IOException {

        System.out.println("URI:" + exchange.getRequestURI());
        System.out.println("base Path: " + this.basePath);

        String uri = exchange.getRequestURI().toString();
        if (uri.startsWith("/")) uri = uri.substring(1);

        Path requestedFile = this.basePath.resolve(uri);

        System.out.println("Requested file: " + requestedFile.toAbsolutePath());

        if (!FileUtils.isChild(this.basePath, requestedFile) && !requestedFile.equals(this.basePath)) {
            System.out.println("no child of content dir");

            String response = "400 (Bad Request)";
            exchange.sendResponseHeaders(400, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        if (FileUtils.isExistingRegularFile(requestedFile)) {
            System.out.println("legal file ...");

            byte[] fileContent = Files.readAllBytes(requestedFile);
            exchange.sendResponseHeaders(200, fileContent.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileContent);
            os.close();
        } else if (FileUtils.isExistingDirectory(requestedFile)) {
            System.out.println("directory requested");

            String response = "400 (Bad Request); Requested file is directory";
            exchange.sendResponseHeaders(400, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            System.out.println("file not found");

            String response = "404 (Not found)";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
