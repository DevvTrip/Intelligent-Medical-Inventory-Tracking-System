package com.mits.model;

/**
 * Concrete class representing liquid oral suspension/syrup formulations.
 * Demonstrates:
 * - Unit 2: Inheritance (extends Medicine)
 * - Unit 2: Method Overriding (overrides getStorageCondition, calculateDiscountedPrice, getDosageFormDetails)
 */
public class Syrup extends Medicine {
    private static final long serialVersionUID = 1L;

    private int bottleVolumeMl;                  // e.g. 60ml, 100ml, 200ml
    private boolean dosageMeasuringCupIncluded;

    public Syrup() {
        super();
        this.category = "SYRUP";
        this.bottleVolumeMl = 100;
        this.dosageMeasuringCupIncluded = true;
    }

    public Syrup(String id, String code, String name, double unitPrice,
                 int reorderLevel, String supplierId, String description,
                 int bottleVolumeMl, boolean dosageMeasuringCupIncluded) {
        super(id, code, name, "SYRUP", unitPrice, reorderLevel, supplierId, description);
        this.bottleVolumeMl = Math.max(10, bottleVolumeMl);
        this.dosageMeasuringCupIncluded = dosageMeasuringCupIncluded;
    }

    public int getBottleVolumeMl() {
        return bottleVolumeMl;
    }

    public void setBottleVolumeMl(int bottleVolumeMl) {
        this.bottleVolumeMl = Math.max(10, bottleVolumeMl);
    }

    public boolean isDosageMeasuringCupIncluded() {
        return dosageMeasuringCupIncluded;
    }

    public void setDosageMeasuringCupIncluded(boolean dosageMeasuringCupIncluded) {
        this.dosageMeasuringCupIncluded = dosageMeasuringCupIncluded;
    }

    @Override
    public String getStorageCondition() {
        return "Store below 25°C. Do not freeze. Shake well before use. Keep bottle tightly closed.";
    }

    @Override
    public double calculateDiscountedPrice(int quantity) {
        double subtotal = unitPrice * quantity;
        if (quantity >= 50) {
            return subtotal * 0.92; // 8% bulk discount for syrup cartons
        }
        return subtotal;
    }

    @Override
    public String getDosageFormDetails() {
        return String.format("Syrup [%d ml bottle, Measuring Cap: %s]",
                bottleVolumeMl, (dosageMeasuringCupIncluded ? "Included" : "Not Included"));
    }
}
