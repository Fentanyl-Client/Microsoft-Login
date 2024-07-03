/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.login.easymc;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.fentanyl.microsoftlogin.api.profile.Profile;
import tech.fentanyl.microsoftlogin.api.profile.ProfileType;

@EqualsAndHashCode(callSuper = true)
@Data
public class EasyMCProfile extends Profile {
    private String session, uuid;

    public EasyMCProfile() {
        super(ProfileType.EASY_MC);
    }

    public EasyMCProfile(String username, String session, String uuid) {
        super(username, ProfileType.EASY_MC);
        this.session = session;
        this.uuid = uuid;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("session", this.session);
        json.addProperty("uuid", this.uuid);
        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        this.session = json.get("session").getAsString();
        this.uuid = json.get("uuid").getAsString();
    }
}
