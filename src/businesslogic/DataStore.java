package businesslogic;

import java.util.ArrayList;

public class DataStore<T> implements Repository<T> {

    private ArrayList<T> items = new ArrayList<T>();

    public void add(T item) {
        items.add(item);
    }

    public void remove(T item) {
        items.remove(item);
    }

    public ArrayList<T> getAll() {
        return items;
    }

    public int size() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }
}
