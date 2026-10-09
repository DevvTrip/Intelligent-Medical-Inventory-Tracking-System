package com.mits.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tracks an inventory stock transaction (issuance or restocking).
 * Demonstrates:
 * - Unit 1: Encapsulation, fields, constructors, getters/setters.
 * - Unit 5: Used in ArrayList transaction histories.
 */
public class StockTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        ISSUE,
        RESTOCK,
        EXPIRED_DISPOSAL
    }

    private String transactionId;
    private String medicineCode;
    private String medicineName;
    private Type type;
    private int quantity;
    private LocalDateTime timestamp;
    private String performedBy;
    private String remarks;

    public StockTransaction() {
        this.transactionId = "TXN-" + System.currentTimeMillis();
        this.timestamp = LocalDateTime.now();
    }

    public StockTransaction(String transactionId, String medicineCode, String medicineName,
                            Type type, int quantity, LocalDateTime timestamp,
                            String performedBy, String remarks) {
        this.transactionId = transactionId;
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.performedBy = performedBy != null ? performedBy : "system";
        this.remarks = remarks != null ? remarks : "";
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s | %s (%s) | Qty: %d | By: %s | %s",
                timestamp.format(fmt), type, medicineName, medicineCode, quantity, performedBy, remarks);
    }
}
