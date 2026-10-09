package com.mits.exception;

/**
 * Checked exception thrown when a medicine with the given code or name is not found in inventory.
 * Part of Unit 3: Exception Handling.
 */
public class MedicineNotFoundException extends Exception {
    private final String query;

    public MedicineNotFoundException(String query) {
        super(String.format("Medicine not found in inventory for identifier/query: '%s'", query));
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
