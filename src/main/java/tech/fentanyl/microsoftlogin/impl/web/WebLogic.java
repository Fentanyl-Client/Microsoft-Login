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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

public class WebLogic {
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();

    private static final Gson GSON = new Gson();

    private static final String REDIRECT_URI = "http://localhost:" + WebLogin.PORT;
    private static final String TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    private static final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MINECRAFT_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String PROFILE_XBOX_URL = "https://profile.xboxlive.com/users/me/profile/settings?settings=GameDisplayName,AppDisplayName,AppDisplayPicRaw,GameDisplayPicRaw,"
            + "PublicGamerpic,ShowUserAsAvatar,Gamerscore,Gamertag,ModernGamertag,ModernGamertagSuffix,UniqueModernGamertag,AccountTier,TenureLevel,XboxOneRep,"
            + "PreferredColor,Location,Bio,Watermarks,RealName,RealNameOverride,IsQuarantined";
    private static final String MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";

    @SneakyThrows
    public WebProfile auth(String query) {
        if (query == null || query.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.") || !query.startsWith("code=")) return null;

        Map.Entry<String, String> authRefreshTokens = codeToToken(query.replace("code=", ""));
        String refreshToken = authRefreshTokens.getValue();
        String xblToken = authXBL(authRefreshTokens.getKey());
        Map.Entry<String, String> xstsTokenUserhash = authXSTS(xblToken);
        loadXboxProfile(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
        Map.Entry<String, String> accessToken = authMinecraft(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
        Map.Entry<UUID, String> profile = getProfile(accessToken.getValue(), accessToken.getKey());
        return new WebProfile(profile.getValue(), profile.getKey().toString(), accessToken.getKey(), refreshToken);
    }

    @SneakyThrows
    public WebProfile refresh(String refreshToken) {
        Map.Entry<String, String> authRefreshTokens = refreshToken(refreshToken);
        String newRefreshToken = authRefreshTokens.getValue();
        String xblToken = authXBL(authRefreshTokens.getKey());
        Map.Entry<String, String> xstsTokenUserhash = authXSTS(xblToken);
        loadXboxProfile(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
        Map.Entry<String, String> accessToken = authMinecraft(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
        Map.Entry<UUID, String> profile = getProfile(accessToken.getValue(), accessToken.getKey());
        return new WebProfile(profile.getValue(), profile.getKey().toString(), accessToken.getKey(), newRefreshToken);
    }

    private Map.Entry<String, String> codeToToken(String code) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("client_id", WebLogin.CLIENT_ID)
                .add("code", code)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", REDIRECT_URI)
                .add("scope", "XboxLive.signin XboxLive.offline_access")
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(formBody)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("codeToToken response: " + response.code() + ", data: " + response.body().string());

            return getAccessRefreshTokens(response);
        }
    }

    private Map.Entry<String, String> refreshToken(String refreshToken) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("client_id", WebLogin.CLIENT_ID)
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .add("redirect_uri", REDIRECT_URI)
                .add("scope", "XboxLive.signin XboxLive.offline_access")
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(formBody)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("refreshToken response: " + response.code() + ", data: " + response.body().string());
            }

            return getAccessRefreshTokens(response);
        }
    }

    private String authXBL(String authToken) throws IOException {
        JsonObject req = new JsonObject();
        JsonObject reqProps = new JsonObject();
        reqProps.addProperty("AuthMethod", "RPS");
        reqProps.addProperty("SiteName", "user.auth.xboxlive.com");
        reqProps.addProperty("RpsTicket", "d=" + authToken);
        req.add("Properties", reqProps);
        req.addProperty("RelyingParty", "http://auth.xboxlive.com");
        req.addProperty("TokenType", "JWT");

        RequestBody requestBody = RequestBody.create(req.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(XBL_AUTH_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("authXBL response: " + response.code() + ", data: " + response.body().string());
            }

            JsonObject resp = GSON.fromJson(response.body().charStream(), JsonObject.class);
            return resp.get("Token").getAsString();
        }
    }

    private Map.Entry<String, String> authXSTS(String xblToken) throws IOException {
        JsonObject req = new JsonObject();
        JsonObject reqProps = new JsonObject();
        JsonArray userTokens = new JsonArray();
        userTokens.add(xblToken);
        reqProps.add("UserTokens", userTokens);
        reqProps.addProperty("SandboxId", "RETAIL");
        req.add("Properties", reqProps);
        req.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        req.addProperty("TokenType", "JWT");

        RequestBody requestBody = RequestBody.create(req.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(XSTS_AUTH_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("authXSTS response: " + response.code() + ", data: " + response.body().string());
            }

            JsonObject resp = GSON.fromJson(response.body().charStream(), JsonObject.class);

            JsonObject displayClaims = resp.getAsJsonObject("DisplayClaims");
            JsonArray xuiArray = displayClaims.getAsJsonArray("xui");
            JsonObject xuiObject = xuiArray.get(0).getAsJsonObject();

            String token = resp.get("Token").getAsString();
            String uhs = xuiObject.get("uhs").getAsString();

            return new AbstractMap.SimpleImmutableEntry<>(token, uhs);
        }
    }

    private Map.Entry<String, String> authMinecraft(String userHash, String xstsToken) throws IOException {
        JsonObject req = new JsonObject();
        req.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);

        RequestBody requestBody = RequestBody.create(req.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(MINECRAFT_AUTH_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("authMinecraft response: " + response.code() + ", data: " + response.body().string());
            }

            JsonObject resp = GSON.fromJson(response.body().charStream(), JsonObject.class);

            String accessToken = resp.get("access_token").getAsString();
            String tokenType = resp.get("token_type").getAsString();

            return new AbstractMap.SimpleImmutableEntry<>(accessToken, tokenType);
        }
    }

    private static void loadXboxProfile(String userHash, String xstsToken) throws IOException {
        Request request = new Request.Builder()
                .url(PROFILE_XBOX_URL)
                .addHeader("Authorization", "XBL3.0 x=" + userHash + ";" + xstsToken)
                .addHeader("Accept", "application/json")
                .addHeader("x-xbl-contract-version", "3")
                .get()
                .build();

        CLIENT.newCall(request).execute(); // We don't care about the response
    }

    private Map.Entry<UUID, String> getProfile(String authorisation, String accessToken) throws IOException {
        Request request = new Request.Builder()
                .url(MINECRAFT_PROFILE_URL)
                .addHeader("Authorization", authorisation + " " + accessToken)
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("getProfile response: " + response.code() + ", data: " + response.body().string());
            }

            JsonObject resp = GSON.fromJson(response.body().charStream(), JsonObject.class);

            String idString = resp.get("id").getAsString();
            UUID uuid = UUID.fromString(idString.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
            String name = resp.get("name").getAsString();

            return new AbstractMap.SimpleImmutableEntry<>(uuid, name);
        }
    }

    private Map.Entry<String, String> getAccessRefreshTokens(Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            if (responseBody == null) {
                throw new IOException("Response body is null");
            }

            JsonObject resp = GSON.fromJson(responseBody.charStream(), JsonObject.class);
            return new AbstractMap.SimpleImmutableEntry<>(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
        }
    }
}
