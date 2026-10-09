package com.mits.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a specific manufactured batch of medicine.
 * Demonstrates:
 * - Composition relationship: Batches are owned by Medicine and managed inside it.
 * - Interface implementation: Implements Expirable and Comparable.
 * - Encapsulation with private fields and standard getters/setters.
 */
public class Batch implements Expirable, Comparable<Batch>, Serializable {
    private static final long serialVersionUID = 1L;

    private String batchNumber;
    private int quantity;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;

    /** Default constructor */
    public Batch() {
        this.batchNumber = "BATCH-DEFAULT";
        this.quantity = 0;
        this.manufactureDate = LocalDate.now();
        this.expiryDate = LocalDate.now().plusMonths(12);
    }

    /** Parameterized constructor */
    public Batch(String batchNumber, int quantity, LocalDate manufactureDate, LocalDate expiryDate) {
        this.batchNumber = batchNumber;
        this.quantity = Math.max(0, quantity);
        this.manufactureDate = manufactureDate != null ? manufactureDate : LocalDate.now();
        this.expiryDate = expiryDate != null ? expiryDate : LocalDate.now().plusMonths(12);
    }

    // Getters and Setters
    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    @Override
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Expirable interface implementation
    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public long daysToExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    /**
     * Deducts quantity from this batch.
     * @param amount quantity to deduct
     * @return actual amount deducted
     */
    public int deductQuantity(int amount) {
        if (amount <= 0) return 0;
        int deducted = Math.min(this.quantity, amount);
        this.quantity -= deducted;
        return deducted;
    }

    /**
     * Adds quantity to this batch.
     * @param amount quantity to add
     */
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    // Natural ordering by expiry date (FIFO for stock issuance)
    @Override
    public int compareTo(Batch other) {
        if (other == null) return 1;
        int dateCmp = this.expiryDate.compareTo(other.expiryDate);
        if (dateCmp != 0) return dateCmp;
        return this.batchNumber.compareTo(other.batchNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Batch batch)) return false;
        return Objects.equals(batchNumber, batch.batchNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchNumber);
    }

    @Override
    public String toString() {
        return String.format("Batch[%s, qty=%d, exp=%s, daysLeft=%d]",
                batchNumber, quantity, expiryDate, daysToExpiry());
    }
}
