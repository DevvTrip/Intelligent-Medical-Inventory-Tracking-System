package com.mits.exception;

/**
 * Checked exception thrown when an issuance request exceeds available inventory stock.
 * Part of Unit 3: Exception Handling.
 */
public class InsufficientStockException extends Exception {
    private final String medicineCode;
    private final String medicineName;
    private final int requested;
    private final int available;

    public InsufficientStockException(String medicineCode, String medicineName, int requested, int available) {
        super(String.format("Insufficient stock for %s (%s). Requested: %d, Available: %d",
                medicineName, medicineCode, requested, available));
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.requested = requested;
        this.available = available;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
