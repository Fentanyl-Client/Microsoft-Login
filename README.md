# Microsoft Login
A Java library for Minecraft cheats to log into Microsoft. This is designed to simplify the overall process.

## Installation
You can install this package using [Jitpack](https://jitpack.io/#Fentanyl-Client/Microsoft-Login).

### Gradle
```gradle
dependencies {
    implementation 'com.github.Fentanyl-Client:Microsoft-Login:1.0.6'
}
```

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.github.Fentanyl-Client</groupId>
        <artifactId>Microsoft-Login</artifactId>
        <version>1.0.6</version>
    </dependency>
</dependencies>
```

or you can check the [releases](https://github.com/Fentanyl-Client/Microsoft-Login/releases/latest) for a compiled .jar.

### Note
You may need to install the dependencies seperately if you experience errors.

## Usage
### Cracked Login
Cracked login is just a stub. Many clients include support for cracked accounts, but they have no login process.

```java
import net.minecraft.util.Session;
import tech.fentanyl.microsoftlogin.impl.login.cracked.CrackedLogin;
import tech.fentanyl.microsoftlogin.impl.login.cracked.CrackedProfile;

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
import net.minecraft.util.Session;
import tech.fentanyl.microsoftlogin.impl.login.cookie.CookieLogin;
import tech.fentanyl.microsoftlogin.impl.login.cookie.CookieProfile;

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
import net.minecraft.util.Session;
import tech.fentanyl.microsoftlogin.impl.login.web.WebLogin;
import tech.fentanyl.microsoftlogin.impl.login.web.WebProfile;

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
import tech.fentanyl.microsoftlogin.impl.login.web.WebLogin;
import tech.fentanyl.microsoftlogin.impl.login.web.WebProfile;

public class Main {
    public static void main(String[] args) {
        WebLogin login = new WebLogin(true);
        WebProfile profile = login.login();

        JsonObject json = profile.toJson();
        WebProfile profile2 = new WebProfile().fromJson(json);
    }
}
```

### Manager
The manager is a simple way to manage multiple accounts.

```java
import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.impl.profile.ProfileManager;
import tech.fentanyl.microsoftlogin.impl.login.web.WebProfile;
import tech.fentanyl.microsoftlogin.impl.login.web.WebLogin;

public class Main {
    public static void main(String[] args) {
        ProfileManager manager = new ProfileManager();

        WebLogin login = new WebLogin(true);
        WebProfile profile = login.login();
        manager.addProfile(profile);
        
        JsonObject json = manager.toJson();
        ProfileManager manager2 = new ProfileManager().fromJson(json);
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
