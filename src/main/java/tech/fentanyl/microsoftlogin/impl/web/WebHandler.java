/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
