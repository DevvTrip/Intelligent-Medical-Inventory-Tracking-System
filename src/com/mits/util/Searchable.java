package com.mits.util;

import java.util.List;

/**
 * Generic interface defining search capability across entities.
 * Part of Unit 5: Generics.
 *
 * @param <T> type of entity being searched
 */
public interface Searchable<T> {
    /**
     * Searches entities matching the given query string.
     * @param query search query
     * @return list of matching items
     */
    List<T> search(String query);
}
