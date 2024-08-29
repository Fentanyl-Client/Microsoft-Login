/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
