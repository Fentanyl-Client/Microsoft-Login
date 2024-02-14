# Microsoft Login
A Java library for Minecraft cheats to log into Microsoft. This is designed to simplify the overall process.

## Usage
### Cracked Login
Cracked login is just a stub. Many clients include support for cracked accounts but they have no login process.

```java
import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.impl.cracked.CrackedLogin;
import tech.fentanyl.microsoftlogin.impl.cracked.CrackedProfile;

public class Main {
    public static void main(String[] args) {
        CrackedLogin login = new CrackedLogin("username");
        CrackedProfile profile = login.login();

        Session session = new Session(profile.getUsername(), "", "", "mojang");
    }
}
```

### Cookie Login
Cookie login parses a file containing cookies (stored in the Netscape format) and logs in using them.

```java
import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.impl.cookie.CookieLogin;

public class Main {
    public static void main(String[] args) {
        CookieLogin login = new CookieLogin("cookies.txt");
        CookieProfile profile = login.login();

        Session session = new Session(profile.getUsername(), profile.getId(), profile.getAccessToken(), "microsoft");
    }
}
```

### Web Login
Web login opens a web browser and prompts the user to login to Microsoft.

```java
import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.impl.web.WebLogin;

public class Main {
    public static void main(String[] args) {
        WebLogin login = new WebLogin(true); // true/false indicates the browser to open in incognito mode (Windows only)
        WebProfile profile = login.login();
        
        Session session = new Session(profile.getUsername(), profile.getId(), profile.getAccessToken(), "microsoft");
    }
}
```


### Profile
The profile object contains the username and any additional information required for the session.

They all implement the `IProfile` interface and can be stored as JSON.

```java
import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.impl.cookie.WebProfile;
import tech.fentanyl.microsoftlogin.impl.web.WebProfile;

public class Main {
    public static void main(String[] args) {
        WebLogin login = new WebLogin(true);
        WebProfile profile = login.login();

        JsonObject json = profile.toJson();
        WebProfile profile2 = new WebProfile().fromJson(json);
    }
}
```

## Changelog
### 1.0.0
- Initial release

## License
This project is licensed under the MIT Licence - see the [LICENCE](LICENSE) file for details
```
Copyright (C) 2024, darraghd493

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```