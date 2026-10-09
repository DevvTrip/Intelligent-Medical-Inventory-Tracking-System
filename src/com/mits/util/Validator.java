package com.mits.util;

import com.mits.exception.InvalidInputException;

/**
 * Generic interface defining validation rules for entities.
 * Part of Unit 5: Generics.
 *
 * @param <T> type of entity to validate
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Validates the given item according to business rules.
     * @param item the item to validate
     * @throws InvalidInputException if validation fails
     */
    void validate(T item) throws InvalidInputException;
}
