/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.util;

import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import tech.fentanyl.microsoftlogin.api.profile.IProfile;
import tech.fentanyl.microsoftlogin.api.profile.ProfileType;
import tech.fentanyl.microsoftlogin.impl.login.cracked.CrackedProfile;
import tech.fentanyl.microsoftlogin.impl.login.web.WebProfile;

@UtilityClass
public class ProfileUtil {
    public static IProfile dynamicCreate(JsonObject json) {
        ProfileType type = ProfileType.valueOf(json.get("type").getAsString());
        IProfile profile = null;

        switch (type) {
            case CRACKED:
                profile = new CrackedProfile();
                break;
            case WEB:
                profile = new WebProfile();
                break;
        }

        if (profile == null) {
            throw new IllegalArgumentException("dynamicCreate: profile is null");
        }

        if (profile.getType() == null) {
            throw new NullPointerException("dynamicCreate: profile type is null");
        }

        profile.fromJson(json);
        return profile;
    }
}
