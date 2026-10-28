package businesslogic;

import java.util.ArrayList;

public interface Repository<T> {
    void add(T item);
    void remove(T item);
    ArrayList<T> getAll();
    int size();
}
