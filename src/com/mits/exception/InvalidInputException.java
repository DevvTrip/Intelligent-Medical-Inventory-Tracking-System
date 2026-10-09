package com.mits.exception;

/**
 * Checked exception thrown when user input fails business validation rules.
 * Part of Unit 3: Exception Handling.
 */
public class InvalidInputException extends Exception {
    private final String fieldName;
    private final String reason;

    public InvalidInputException(String fieldName, String reason) {
        super(String.format("Invalid input for field '%s': %s", fieldName, reason));
        this.fieldName = fieldName;
        this.reason = reason;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getReason() {
        return reason;
    }
}
