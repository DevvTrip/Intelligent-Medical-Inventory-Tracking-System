package com.mits.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents system alerts raised for low stock, near expiry, and expired medicines.
 * Part of Unit 1 and Unit 4 (Concurrent alert notifications queue).
 */
public class Alert implements Serializable, Comparable<Alert> {
    private static final long serialVersionUID = 1L;

    public enum Type {
        LOW_STOCK,
        NEAR_EXPIRY,
        EXPIRED
    }

    public enum Severity {
        CRITICAL,
        WARNING,
        INFO
    }

    private String alertId;
    private String medicineCode;
    private String medicineName;
    private Type type;
    private Severity severity;
    private String message;
    private LocalDateTime timestamp;

    public Alert() {
        this.alertId = "ALT-" + System.currentTimeMillis();
        this.timestamp = LocalDateTime.now();
        this.severity = Severity.INFO;
    }

    public Alert(String alertId, String medicineCode, String medicineName,
                 Type type, Severity severity, String message, LocalDateTime timestamp) {
        this.alertId = alertId;
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.type = type;
        this.severity = severity != null ? severity : Severity.INFO;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters and Setters
    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
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

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Alert other) {
        if (other == null) return 1;
        // Most recent alerts first
        return other.timestamp.compareTo(this.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alert alert)) return false;
        return Objects.equals(alertId, alert.alertId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] [%s] %s: %s (%s)",
                timestamp.format(fmt), severity, type, medicineName, message);
    }
}
