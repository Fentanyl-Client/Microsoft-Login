/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.login.cookie;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class CookieLogic {
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();

    private static final Gson GSON = new Gson();

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0";

    private static final String XBL_REDIRECT_URL = "https://sisu.xboxlive.com/connect/XboxLive/?state=login&ru=https://www.minecraft.net/en-us/login";
    private static final String MINECRAFT_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";

    private final String cookie;

    public CookieLogic() {
        this.cookie = null;
    }

    public CookieLogic(List<Cookie> cookies) {
        this.cookie = CookieParser.formatCookies(cookies);
    }

    public CookieProfile auth() throws IOException {
        String redirect = this.getRedirectUrl(),
                xboxUrl = this.getXBLUrl(redirect),
                tokenData = this.authXBL(xboxUrl),
                accessToken = this.authMinecraft(tokenData);

        if (this.checkLicense(accessToken)) {
            return this.getProfile(accessToken, tokenData);
        } else {
            throw new IllegalStateException("authenticate: failed to check license");
        }
    }

    public CookieProfile refresh(String refreshToken) throws IOException {
        String accessToken = this.authMinecraft(refreshToken);

        if (this.checkLicense(accessToken)) {
            return this.getProfile(accessToken, refreshToken);
        } else {
            throw new IllegalStateException("refresh: failed to check license");
        }
    }

    private String getRedirectUrl() throws IOException {
        Request request = new Request.Builder()
                .header("User-Agent", USER_AGENT)
                .header("Cookie", this.cookie)
                .url(XBL_REDIRECT_URL)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.code() == 302 && response.header("Location").contains("oauth20_authorize.srf")) {
                return response.header("Location");
            }

            throw new UnsupportedOperationException("Redirect location not found");
        }
    }

    private String getXBLUrl(String url) throws IOException {
        Request request = new Request.Builder()
                .header("User-Agent", USER_AGENT)
                .header("Cookie", this.cookie)
                .url(url)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.code() == 302 && response.header("Location").contains("code=")) {
                return response.header("Location");
            }

            System.out.println("getXboxUrl response: " + response.code() + ", data: " + response.body().string());
            throw new UnsupportedOperationException("No code/redirect present");
        }
    }

    private String authXBL(String url) throws IOException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Cookie", this.cookie)
                .url(url)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IllegalArgumentException("getAccessToken response: " + response.code() + ", data: " + response.body().string());
            }

            if (response.code() == 401 && response.header("WWW-Authenticate").contains("account_creation_required")) {
                throw new IllegalStateException("getAccessToken response: " + response.code() + ", data: " + response.body().string() + " (account creation)");
            }

            if (response.code() == 302 && response.header("Location").contains("accessToken=")) {
                return response.header("Location").split("accessToken=")[1];
            }

            throw new IllegalArgumentException("getAccessToken response: " + response.code() + ", data: " + response.body().string());
        }
    }

    private Map.Entry<String, String> parseAccessToken(String accessToken) {
        JsonArray json = GSON.fromJson(new String(Base64.getDecoder().decode(accessToken)), JsonArray.class);
        String xui = json.get(1).getAsJsonObject().get("Item2").getAsJsonObject().get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
        String token = json.get(1).getAsJsonObject().get("Item2").getAsJsonObject().get("Token").getAsString();
        return new AbstractMap.SimpleImmutableEntry<>(xui, token);
    }

    private String authMinecraft(String accessToken) throws IOException {
        Map.Entry<String, String> entry = this.parseAccessToken(accessToken);

        JsonObject req = new JsonObject();
        req.addProperty("identityToken", "XBL3.0 x=" + entry.getKey() + ";" + entry.getValue());
        req.addProperty("ensureLegacyEnabled", "True");

        RequestBody requestBody = RequestBody.create(req.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .url(MINECRAFT_AUTH_URL)
                .post(requestBody)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("authMinecraft response: " + response.code() + ", data: " + response.body().string());
            }

            JsonObject resp = GSON.fromJson(response.body().charStream(), JsonObject.class);

            return resp.get("access_token").getAsString();
        }
    }

    private boolean checkLicense(String string) throws IOException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Authorization", "Bearer " + string)
                .url("https://api.minecraftservices.com/entitlements/license?requestId=checker")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IllegalArgumentException("checkLicense response: " + response.code() + ", data: " + response.body().string());
            }

            boolean valid = false;
            for (JsonElement element : GSON.fromJson(response.body().string(), JsonObject.class).getAsJsonObject().get("items").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                String source = object.get("source").getAsString();

                if (!source.equals("PURCHASE") && !source.equals("MC_PURCHASE")) continue;
                valid = true;
                break;
            }

            return valid;
        }
    }

    private CookieProfile getProfile(String accessToken, String refreshToken) throws IOException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(MINECRAFT_PROFILE_URL)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IllegalArgumentException("getProfile response: " + response.code() + ", data: " + response.body().string());
            }

            JsonObject resp = GSON.fromJson(response.body().string(), JsonObject.class).getAsJsonObject();

            return new CookieProfile(resp.get("name").getAsString(), resp.get("id").getAsString(), accessToken, refreshToken);
        }
    }
}
