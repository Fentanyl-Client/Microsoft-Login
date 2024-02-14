package tech.fentanyl.microsoftlogin.impl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WebHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange request) throws IOException {
        if (request.getRequestMethod().equals("GET")) {
            try {
                WebProfile account = new WebLogic().auth(request.getRequestURI().getQuery());
                if (account != null) {
                    WebLogin.callback.accept(account);
                    this.write(request, "Successfully authenticated with Microsoft. You may now close this window.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.write(request, "Failed to authenticate with Microsoft. Please try again.");
        }

        WebLogin.shutdown();
    }

    private void write(HttpExchange request, String string) throws IOException {
        OutputStream out = request.getResponseBody();

        request.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        request.sendResponseHeaders(200, string.length());

        out.write(string.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
