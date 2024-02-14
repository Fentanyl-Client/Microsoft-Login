package tech.fentanyl.microsoftlogin.impl.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.api.manager.Manager;
import tech.fentanyl.microsoftlogin.api.profile.IProfile;
import tech.fentanyl.microsoftlogin.impl.util.ProfileUtil;

public class ProfileManager extends Manager<IProfile> {
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();

        for (IProfile profile : this.list) {
            array.add(profile.toJson());
        }

        json.add("profiles", array);
        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonArray array = json.getAsJsonArray("profiles");

        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            IProfile profile = ProfileUtil.dynamicCreate(object);
            this.list.add(profile);
        }
    }
}
