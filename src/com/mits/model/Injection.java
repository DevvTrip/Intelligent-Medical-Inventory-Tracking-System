package com.mits.model;

/**
 * Concrete class representing parenteral / injectable formulations.
 * Demonstrates:
 * - Unit 2: Inheritance (extends Medicine)
 * - Unit 2: Method Overriding (overrides getStorageCondition, calculateDiscountedPrice, getDosageFormDetails)
 */
public class Injection extends Medicine {
    private static final long serialVersionUID = 1L;

    private String vialType;              // "Vial" or "Ampoule"
    private boolean isColdChainRequired;  // true if requires 2°C - 8°C refrigeration

    public Injection() {
        super();
        this.category = "INJECTION";
        this.vialType = "Vial";
        this.isColdChainRequired = false;
    }

    public Injection(String id, String code, String name, double unitPrice,
                     int reorderLevel, String supplierId, String description,
                     String vialType, boolean isColdChainRequired) {
        super(id, code, name, "INJECTION", unitPrice, reorderLevel, supplierId, description);
        this.vialType = vialType != null ? vialType : "Vial";
        this.isColdChainRequired = isColdChainRequired;
    }

    public String getVialType() {
        return vialType;
    }

    public void setVialType(String vialType) {
        this.vialType = vialType;
    }

    public boolean isColdChainRequired() {
        return isColdChainRequired;
    }

    public void setColdChainRequired(boolean coldChainRequired) {
        isColdChainRequired = coldChainRequired;
    }

    @Override
    public String getStorageCondition() {
        if (isColdChainRequired) {
            return "COLD CHAIN: Store in refrigerator at 2°C - 8°C. Do not freeze. Protect from light.";
        }
        return "Store below 25°C in original packaging. Single use sterile container.";
    }

    @Override
    public double calculateDiscountedPrice(int quantity) {
        double subtotal = unitPrice * quantity;
        if (quantity >= 100) {
            return subtotal * 0.90; // 10% discount for hospital bulk orders
        }
        return subtotal;
    }

    @Override
    public String getDosageFormDetails() {
        return String.format("Injection [%s, %s]",
                vialType, (isColdChainRequired ? "Cold Chain 2-8°C" : "Room Temp"));
    }
}
