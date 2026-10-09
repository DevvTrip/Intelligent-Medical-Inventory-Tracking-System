package com.mits.model;

/**
 * Concrete class representing tablet/pill formulations.
 * Demonstrates:
 * - Unit 2: Inheritance (extends Medicine)
 * - Unit 2: Method Overriding (overrides getStorageCondition, calculateDiscountedPrice, getDosageFormDetails)
 */
public class Tablet extends Medicine {
    private static final long serialVersionUID = 1L;

    private int stripSize;       // e.g. 10 tablets per strip
    private boolean isChewable;

    public Tablet() {
        super();
        this.category = "TABLET";
        this.stripSize = 10;
        this.isChewable = false;
    }

    public Tablet(String id, String code, String name, double unitPrice,
                  int reorderLevel, String supplierId, String description,
                  int stripSize, boolean isChewable) {
        super(id, code, name, "TABLET", unitPrice, reorderLevel, supplierId, description);
        this.stripSize = Math.max(1, stripSize);
        this.isChewable = isChewable;
    }

    public int getStripSize() {
        return stripSize;
    }

    public void setStripSize(int stripSize) {
        this.stripSize = Math.max(1, stripSize);
    }

    public boolean isChewable() {
        return isChewable;
    }

    public void setChewable(boolean chewable) {
        isChewable = chewable;
    }

    // Overriding abstract methods (Polymorphism)
    @Override
    public String getStorageCondition() {
        return "Store at room temperature below 25°C in a dry place. Protect from moisture.";
    }

    @Override
    public double calculateDiscountedPrice(int quantity) {
        double subtotal = unitPrice * quantity;
        // Bulk discount: 5% off if 100+ units, 10% off if 500+ units
        if (quantity >= 500) {
            return subtotal * 0.90;
        } else if (quantity >= 100) {
            return subtotal * 0.95;
        }
        return subtotal;
    }

    @Override
    public String getDosageFormDetails() {
        return String.format("Tablet [Strip Size: %d pills, %s]",
                stripSize, (isChewable ? "Chewable" : "Swallow with water"));
    }
}
