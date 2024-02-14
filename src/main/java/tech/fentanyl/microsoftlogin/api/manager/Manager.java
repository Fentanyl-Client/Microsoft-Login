package tech.fentanyl.microsoftlogin.api.manager;

import java.util.ArrayList;
import java.util.List;

public abstract class Manager<T> implements IManager<T>  {
    protected final List<T> list;

    public Manager() {
        this.list = new ArrayList<>();
    }

    @Override
    public List<T> get() {
        return this.list;
    }

    @Override
    public void add(T profile) {
        this.list.add(profile);
    }

    @Override
    public void remove(T profile) {
        this.list.remove(profile);
    }

    @Override
    public void update(T profile, T newProfile) {
        this.list.set(this.list.indexOf(profile), newProfile);
    }

    @Override
    public void clear() {
        this.list.clear();
    }
}
