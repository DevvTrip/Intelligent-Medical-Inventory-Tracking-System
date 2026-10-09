package com.mits.exception;

/**
 * Checked exception thrown when attempting to add a medicine with an existing unique code.
 * Part of Unit 3: Exception Handling.
 */
public class DuplicateMedicineException extends Exception {
    private final String medicineCode;

    public DuplicateMedicineException(String medicineCode) {
        super(String.format("Medicine code '%s' already exists in the inventory system.", medicineCode));
        this.medicineCode = medicineCode;
    }

    public String getMedicineCode() {
        return medicineCode;
    }
}
