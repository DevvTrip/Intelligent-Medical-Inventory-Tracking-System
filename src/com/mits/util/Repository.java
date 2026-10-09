package com.mits.util;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic repository class providing in-memory CRUD data management.
 * Demonstrates:
 * - Unit 5: Generic class <T, ID>
 * - Unit 5: Generic methods with bounded types (<E extends Comparable<E>>)
 * - Unit 5: Wildcards (? extends E)
 *
 * @param <T> entity type
 * @param <ID> identifier type
 */
public class Repository<T, ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Map<ID, T> storage = new LinkedHashMap<>();
    protected final Function<T, ID> idExtractor;

    public Repository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    public synchronized void add(T item) {
        if (item == null) return;
        ID id = idExtractor.apply(item);
        storage.put(id, item);
    }

    public synchronized Optional<T> findById(ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    public synchronized List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public synchronized boolean update(T item) {
        if (item == null) return false;
        ID id = idExtractor.apply(item);
        if (storage.containsKey(id)) {
            storage.put(id, item);
            return true;
        }
        return false;
    }

    public synchronized boolean deleteById(ID id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    public synchronized List<T> findBy(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : storage.values()) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public synchronized int size() {
        return storage.size();
    }

    public synchronized void clear() {
        storage.clear();
    }

    // ==========================================
    // GENERIC UTILITY METHODS (Unit 5: Generics)
    // ==========================================

    /**
     * Generic method with bounded type parameter: finds the maximum element in a list.
     * Demonstrates: <E extends Comparable<E>>
     */
    public static <E extends Comparable<E>> E findMax(List<E> list) {
        if (list == null || list.isEmpty()) return null;
        E max = list.get(0);
        for (E item : list) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    /**
     * Generic method using upper-bounded wildcard: prints all elements.
     * Demonstrates: Collection<? extends E>
     */
    public static <E> void printAll(Collection<? extends E> collection) {
        if (collection == null) return;
        for (E item : collection) {
            System.out.println(" - " + item);
        }
    }
}
