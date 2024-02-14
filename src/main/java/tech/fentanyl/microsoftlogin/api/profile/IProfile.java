package tech.fentanyl.microsoftlogin.api.profile;

import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.api.IJson;

public interface IProfile extends IJson {
    String getUsername();

    ProfileType getType();
}
