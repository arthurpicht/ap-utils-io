package de.arthurpicht.utils.io.server.https;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.arthurpicht.utils.core.strings.Strings;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DemoHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {

        Headers requestHeaders = t.getRequestHeaders();
        for (String key : requestHeaders.keySet()) {
            List<String> headers = requestHeaders.get(key);
            System.out.println(key);
            System.out.println(Strings.listing(headers, "\n", "", "", "    ", ""));
        }

        System.out.println("Request URI: " + t.getRequestURI().toString());

        String response = "This is a dummy HTTP response!\n";
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        t.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

}
