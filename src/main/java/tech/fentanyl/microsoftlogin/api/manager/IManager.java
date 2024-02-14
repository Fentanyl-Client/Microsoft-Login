package tech.fentanyl.microsoftlogin.api.manager;

import com.google.gson.JsonObject;
import tech.fentanyl.microsoftlogin.api.IJson;

import java.util.List;

public interface IManager<T> extends IJson {
    List<T> get();

    void add(T profile);

    void remove(T profile);

    void update(T profile, T newProfile);

    void clear();
}
