package tech.fentanyl.microsoftlogin.impl.login.easymc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EasyMCLogic {
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();

    private static final Gson GSON = new Gson();

    private static final String REDEEM_URL = "https://api.easymc.io/v1/token/redeem";

    @SneakyThrows
    public EasyMCProfile     redeem(String token) {
        Request request = new Request.Builder()
                .url(REDEEM_URL)
                .header("Content-Type", "application/json")
                .post(okhttp3.RequestBody.create("{\"token\":\"" + token + "\"}", okhttp3.MediaType.parse("application/json")))
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                throw new RuntimeException("redeem response body is null");
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException(String.format("redeem response: %s, data: %s", response.code(), response.body().string()));
            }

            JsonObject json = GSON.fromJson(response.body().string(), JsonObject.class);
            if (json.has("error")) {
                throw new RuntimeException("redeem error: " + json.get("error").getAsString());
            }

            return new EasyMCProfile(json.get("mcName").getAsString(), json.get("session").getAsString(), json.get("uuid").getAsString());
        }
    }
}
