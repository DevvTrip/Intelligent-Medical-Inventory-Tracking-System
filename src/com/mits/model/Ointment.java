package com.mits.model;

/**
 * Concrete class representing topical ointment / cream / gel formulations.
 * Demonstrates:
 * - Unit 2: Inheritance (extends Medicine)
 * - Unit 2: Method Overriding (overrides getStorageCondition, calculateDiscountedPrice, getDosageFormDetails)
 */
public class Ointment extends Medicine {
    private static final long serialVersionUID = 1L;

    private double tubeWeightGrams;      // e.g. 15g, 30g, 50g
    private String baseType;             // "Cream", "Gel", "Ointment"

    public Ointment() {
        super();
        this.category = "OINTMENT";
        this.tubeWeightGrams = 20.0;
        this.baseType = "Ointment";
    }

    public Ointment(String id, String code, String name, double unitPrice,
                    int reorderLevel, String supplierId, String description,
                    double tubeWeightGrams, String baseType) {
        super(id, code, name, "OINTMENT", unitPrice, reorderLevel, supplierId, description);
        this.tubeWeightGrams = Math.max(1.0, tubeWeightGrams);
        this.baseType = baseType != null ? baseType : "Ointment";
    }

    public double getTubeWeightGrams() {
        return tubeWeightGrams;
    }

    public void setTubeWeightGrams(double tubeWeightGrams) {
        this.tubeWeightGrams = Math.max(1.0, tubeWeightGrams);
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType != null ? baseType : "Ointment";
    }

    @Override
    public String getStorageCondition() {
        return "Store below 30°C. For external use only. Keep cap tightly closed.";
    }

    @Override
    public double calculateDiscountedPrice(int quantity) {
        double subtotal = unitPrice * quantity;
        if (quantity >= 40) {
            return subtotal * 0.93; // 7% bulk discount
        }
        return subtotal;
    }

    @Override
    public String getDosageFormDetails() {
        return String.format("Topical [%.1fg tube, Base: %s]", tubeWeightGrams, baseType);
    }
}
