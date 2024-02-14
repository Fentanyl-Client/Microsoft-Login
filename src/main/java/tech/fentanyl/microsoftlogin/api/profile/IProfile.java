package tech.fentanyl.microsoftlogin.api.profile;

import com.google.gson.JsonObject;

public interface IProfile {
    String getUsername();

    JsonObject toJson();

    void fromJson(JsonObject json);
}
