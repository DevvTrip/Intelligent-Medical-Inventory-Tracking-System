package com.mits.exception;

import java.time.LocalDate;

/**
 * Checked exception thrown when an attempt is made to dispense expired medicine.
 * Part of Unit 3: Exception Handling.
 */
public class ExpiredMedicineException extends Exception {
    private final String medicineCode;
    private final String medicineName;
    private final String batchNumber;
    private final LocalDate expiryDate;

    public ExpiredMedicineException(String medicineCode, String medicineName, String batchNumber, LocalDate expiryDate) {
        super(String.format("Safety Alert: Medicine %s (%s), Batch %s has expired on %s and cannot be dispensed!",
                medicineName, medicineCode, batchNumber, expiryDate));
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
