/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.login.web;

import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import tech.fentanyl.microsoftlogin.api.login.Login;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WebLogin extends Login<WebProfile> {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    public static final String CLIENT_ID = "54fd49e4-2103-4044-9603-2b028c814ec3"; // In-Game Account Switcher Client ID
    public static final int PORT = 59125;

    public static HttpServer server;
    public static Consumer<WebProfile> callback;

    private final boolean incognito;

    public WebLogin() {
        this.incognito = false;
    }

    public WebLogin(boolean incognito) {
        this.incognito = incognito;
    }

    @Override
    @SneakyThrows
    public WebProfile login() {
        AtomicReference<WebProfile> profile = new AtomicReference<>(null);
        this.login(profile::set);

        while (profile.get() == null) {
            Thread.sleep(100);
        }

        return profile.get();
    }

    @SneakyThrows
    public void login(Consumer<WebProfile> callback) {
        shutdown();

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", new WebHandler());
        server.setExecutor(EXECUTOR_SERVICE);
        server.start();

        WebLogin.callback = callback;
        this.open(this.incognito);
    }

    @SneakyThrows
    public WebProfile refresh(WebProfile profile) {
        return new WebLogic().refresh(profile.getRefreshToken());
    }

    public static void shutdown() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private void open(boolean incognito) {
        if (server == null) {
            throw new IllegalStateException("Server not initialized");
        }

        String url = "https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:" + PORT + "&prompt=select_account";

        if (incognito) {
            WebUtil.openIncognito(url);
        } else {
            WebUtil.open(url);
        }
    }
}
