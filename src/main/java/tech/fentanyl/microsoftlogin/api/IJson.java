package tech.fentanyl.microsoftlogin.api;

import com.google.gson.JsonObject;

public interface IJson {
    JsonObject toJson();

    void fromJson(JsonObject json);
}
