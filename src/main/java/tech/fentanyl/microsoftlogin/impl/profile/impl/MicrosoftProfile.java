/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.profile.impl;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.fentanyl.microsoftlogin.api.profile.Profile;
import tech.fentanyl.microsoftlogin.api.profile.ProfileType;

@EqualsAndHashCode(callSuper = true)
@Data
public class MicrosoftProfile extends Profile {
    private String id, accessToken, refreshToken;

    public MicrosoftProfile() {}

    public MicrosoftProfile(String username, String id, String accessToken, String refreshToken, ProfileType type) {
        super(username, type);
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("id", this.id);
        json.addProperty("accessToken", this.accessToken);
        json.addProperty("refreshToken", this.refreshToken);
        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        super.fromJson(json);
        this.id = json.get("id").getAsString();
        this.accessToken = json.get("accessToken").getAsString();
        this.refreshToken = json.get("refreshToken").getAsString();
    }
}
