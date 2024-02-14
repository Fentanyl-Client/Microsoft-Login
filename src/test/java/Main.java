import tech.fentanyl.microsoftlogin.impl.cookie.CookieLogin;
import tech.fentanyl.microsoftlogin.impl.cookie.CookieProfile;

public class Main {
    public static void main(String[] args) {
        CookieLogin cookieLogin = new CookieLogin("C:\\Users\\darra\\OneDrive\\Documents\\Minecraft Cookies\\JheMelo (sec ban lvl 10+).txt");
        CookieProfile profile = cookieLogin.login();

        System.out.println(profile.getUsername());
        System.out.println(profile.getAccessToken());
        System.out.println(profile.getRefreshToken());
        System.out.println(profile.getId());
    }
}
