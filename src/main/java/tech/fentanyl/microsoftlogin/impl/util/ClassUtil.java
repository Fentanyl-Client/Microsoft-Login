package tech.fentanyl.microsoftlogin.impl.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil {
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
