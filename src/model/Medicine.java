package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import reporting.Alertable;
import reporting.Reportable;

public abstract class Medicine implements Alertable, Reportable, Comparable<Medicine> {

    private String id;
    private String name;
    private double price;
    private int qty;
    private LocalDate expiryDate;
    private int threshold;
    private Supplier supplier;

    public Medicine(String id, String name, double price, int qty,
            LocalDate expiryDate, int threshold, Supplier supplier) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.expiryDate = expiryDate;
        this.threshold = threshold;
        this.supplier = supplier;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQty() { return qty; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public int getThreshold() { return threshold; }
    public Supplier getSupplier() { return supplier; }

    public void setQty(int qty) { this.qty = qty; }
    public void setPrice(double price) { this.price = price; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public abstract String getStorageInfo();
    public abstract int getExtra();

    public double calculatePrice() {
        return price;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        return days >= 0 && days <= 30;
    }

    public boolean isLowStock() {
        return qty < threshold;
    }

    public String getStatus() {
        if (isExpired()) {
            return "EXPIRED";
        }
        if (isExpiringSoon() && isLowStock()) {
            return "EXPIRING SOON + LOW STOCK";
        }
        if (isExpiringSoon()) {
            return "EXPIRING SOON";
        }
        if (isLowStock()) {
            return "LOW STOCK";
        }
        return "OK";
    }

    public String getReportLine() {
        return id + " | " + name + " | qty " + qty + " | " + expiryDate + " | " + getStatus();
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(",");
        sb.append(id).append(",");
        sb.append(name).append(",");
        sb.append(price).append(",");
        sb.append(qty).append(",");
        sb.append(expiryDate).append(",");
        sb.append(threshold).append(",");
        sb.append(supplier.getName()).append(",");
        sb.append(supplier.getPhone()).append(",");
        sb.append(getExtra());
        return sb.toString();
    }

    // agar expiry same hai to id se compare karo, warna TreeSet ek medicine ko hata deta hai
    public int compareTo(Medicine other) {
        int result = expiryDate.compareTo(other.expiryDate);
        if (result == 0) {
            result = id.compareTo(other.id);
        }
        return result;
    }

    public String toString() {
        return id + " - " + name + " qty " + qty;
    }
}
