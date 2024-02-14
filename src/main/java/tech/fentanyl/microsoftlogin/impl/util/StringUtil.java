package tech.fentanyl.microsoftlogin.impl.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {
    public static boolean contains(String content, String... strings) {
        for (String string : strings) {
            if (!string.equals(content)) continue;
            return true;
        }
        return false;
    }
}
