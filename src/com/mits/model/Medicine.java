package com.mits.model;

import com.mits.exception.ExpiredMedicineException;
import com.mits.exception.InsufficientStockException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class representing a pharmaceutical medicine.
 * Demonstrates:
 * - Unit 1: Classes, constructors, getters/setters, encapsulation, toString.
 * - Unit 1: Association (with Supplier), Composition (owns Batches), Generalization (super to concrete forms).
 * - Unit 2: Abstraction (abstract methods), Interfaces (Expirable, Reorderable, Reportable).
 * - Unit 2: Method Overloading (multiple addStock signatures).
 * - Unit 4: Synchronization on stock operations (issueStock, addStock).
 */
public abstract class Medicine implements Expirable, Reorderable, Reportable, Comparable<Medicine>, Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String code;          // e.g. MED-001
    protected String name;
    protected String category;      // TABLET, ANTIBIOTIC, SYRUP, INJECTION, OINTMENT
    protected double unitPrice;
    protected int reorderLevel;
    protected String supplierId;    // Association: references independent Supplier
    protected String description;

    // Composition: Medicine owns its Batches. A Batch cannot exist without its Medicine.
    protected List<Batch> batches;

    /** Default constructor */
    public Medicine() {
        this.id = "M-" + System.currentTimeMillis();
        this.code = "MED-000";
        this.name = "Generic Medicine";
        this.category = "GENERAL";
        this.unitPrice = 0.0;
        this.reorderLevel = 10;
        this.supplierId = "SUP-001";
        this.description = "";
        this.batches = new ArrayList<>();
    }

    /** Parameterized constructor */
    public Medicine(String id, String code, String name, String category,
                    double unitPrice, int reorderLevel, String supplierId, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.unitPrice = Math.max(0.0, unitPrice);
        this.reorderLevel = Math.max(0, reorderLevel);
        this.supplierId = supplierId;
        this.description = description != null ? description : "";
        this.batches = new ArrayList<>();
    }

    // ==========================================
    // ABSTRACT METHODS (Unit 2: Abstraction)
    // ==========================================

    /**
     * Returns required environmental storage condition (e.g. room temp, 2-8 deg C cold chain).
     * @return storage condition description
     */
    public abstract String getStorageCondition();

    /**
     * Calculates discounted total price based on volume purchased and medicine-specific policy.
     * @param quantity number of units
     * @return total discounted price
     */
    public abstract double calculateDiscountedPrice(int quantity);

    /**
     * Returns specific dosage form details (e.g. strip size, bottle ml, vial type).
     * @return dosage details description
     */
    public abstract String getDosageFormDetails();

    // ==========================================
    // CONCRETE STOCK METHODS & COMPOSITION
    // ==========================================

    /**
     * Calculates the total stock across all active batches.
     * @return total quantity on hand
     */
    public synchronized int getTotalStock() {
        int total = 0;
        for (Batch batch : batches) {
            total += batch.getQuantity();
        }
        return total;
    }

    /**
     * Adds a batch to this medicine (Composition relationship).
     * @param batch batch instance
     */
    public synchronized void addBatch(Batch batch) {
        if (batch != null) {
            this.batches.add(batch);
            Collections.sort(this.batches); // keep sorted by expiry
        }
    }

    public synchronized List<Batch> getBatches() {
        return new ArrayList<>(batches);
    }

    public synchronized void setBatches(List<Batch> batches) {
        this.batches = batches != null ? new ArrayList<>(batches) : new ArrayList<>();
        Collections.sort(this.batches);
    }

    // ==========================================
    // METHOD OVERLOADING (Unit 2: Polymorphism)
    // ==========================================

    /**
     * Overload 1: Adds stock with a default batch number and 1-year expiry.
     * @param quantity quantity to add
     */
    public synchronized void addStock(int quantity) {
        addStock(quantity, LocalDate.now().plusYears(1));
    }

    /**
     * Overload 2: Adds stock with specific expiry date.
     * @param quantity quantity to add
     * @param expiryDate expiration date
     */
    public synchronized void addStock(int quantity, LocalDate expiryDate) {
        String batchNum = "B-" + code + "-" + System.currentTimeMillis() % 10000;
        Batch b = new Batch(batchNum, quantity, LocalDate.now(), expiryDate);
        addStock(quantity, b);
    }

    /**
     * Overload 3: Adds stock with an explicit Batch object.
     * @param quantity quantity to add
     * @param batch the batch object
     */
    public synchronized void addStock(int quantity, Batch batch) {
        if (quantity <= 0) return;
        if (batch != null) {
            // Check if batch already exists in list
            boolean found = false;
            for (Batch existing : batches) {
                if (existing.getBatchNumber().equalsIgnoreCase(batch.getBatchNumber())) {
                    existing.addQuantity(quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                batch.setQuantity(quantity);
                batches.add(batch);
            }
            Collections.sort(batches);
        }
    }

    /**
     * Issues stock using FIFO (First-In, First-Out) based on batch expiry dates.
     * Thread-safe / Synchronized method (Unit 4).
     *
     * @param quantity amount of stock to dispense
     * @throws InsufficientStockException if requested quantity exceeds total stock
     * @throws ExpiredMedicineException if the stock available is already expired
     */
    public synchronized void issueStock(int quantity)
            throws InsufficientStockException, ExpiredMedicineException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to issue must be positive.");
        }

        int total = getTotalStock();
        if (quantity > total) {
            throw new InsufficientStockException(code, name, quantity, total);
        }

        // Sort batches by expiry date
        Collections.sort(batches);

        // Verify earliest available batch with quantity > 0 is not expired
        for (Batch b : batches) {
            if (b.getQuantity() > 0 && b.isExpired()) {
                throw new ExpiredMedicineException(code, name, b.getBatchNumber(), b.getExpiryDate());
            }
        }

        int remainingToDeduct = quantity;
        for (Batch batch : batches) {
            if (batch.getQuantity() > 0) {
                int deducted = batch.deductQuantity(remainingToDeduct);
                remainingToDeduct -= deducted;
                if (remainingToDeduct == 0) {
                    break;
                }
            }
        }
    }

    // ==========================================
    // INTERFACE IMPLEMENTATIONS (Unit 2)
    // ==========================================

    @Override
    public boolean isExpired() {
        LocalDate earliest = getExpiryDate();
        if (earliest == null) return false;
        return LocalDate.now().isAfter(earliest);
    }

    @Override
    public long daysToExpiry() {
        LocalDate earliest = getExpiryDate();
        if (earliest == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), earliest);
    }

    @Override
    public LocalDate getExpiryDate() {
        LocalDate earliest = null;
        for (Batch b : batches) {
            if (b.getQuantity() > 0) {
                if (earliest == null || b.getExpiryDate().isBefore(earliest)) {
                    earliest = b.getExpiryDate();
                }
            }
        }
        return earliest != null ? earliest : LocalDate.now().plusMonths(12);
    }

    @Override
    public boolean needsReorder() {
        return getTotalStock() <= reorderLevel;
    }

    @Override
    public int reorderQuantity() {
        int current = getTotalStock();
        int target = Math.max(reorderLevel * 3, 50);
        return Math.max(0, target - current);
    }

    @Override
    public String generateSummary() {
        return String.format("[%s] %-25s | Cat: %-10s | Stock: %4d | Reorder: %3d | Exp: %s (%3dd) | ₹%.2f",
                code, name, category, getTotalStock(), reorderLevel,
                getExpiryDate(), daysToExpiry(), unitPrice);
    }

    // Natural ordering by Medicine Code
    @Override
    public int compareTo(Medicine other) {
        if (other == null) return 1;
        return this.code.compareTo(other.code);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = Math.max(0.0, unitPrice);
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = Math.max(0, reorderLevel);
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine medicine)) return false;
        return Objects.equals(code, medicine.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("%s - %s [Stock: %d, ₹%.2f]", code, name, getTotalStock(), unitPrice);
    }
}
