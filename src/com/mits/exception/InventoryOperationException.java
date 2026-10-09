package com.mits.exception;

/**
 * Unchecked (runtime) exception representing unexpected operational failures.
 * Part of Unit 3: Exception Handling (Unchecked vs Checked exceptions).
 */
public class InventoryOperationException extends RuntimeException {
    public InventoryOperationException(String message) {
        super(message);
    }

    public InventoryOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
