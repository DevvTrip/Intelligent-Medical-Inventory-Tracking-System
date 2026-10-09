package com.mits.model;

/**
 * Concrete class representing Antibiotic medicines.
 * Demonstrates:
 * - Unit 2: Multilevel Inheritance (Medicine -> Tablet -> Antibiotic)
 * - Unit 2: Method Overriding (customized storage, pricing, dosage details)
 */
public class Antibiotic extends Tablet {
    private static final long serialVersionUID = 1L;

    private String spectrum;               // "Broad-Spectrum" or "Narrow-Spectrum"
    private boolean requiresPrescription;  // strictly regulated schedule-H drug

    public Antibiotic() {
        super();
        this.category = "ANTIBIOTIC";
        this.spectrum = "Broad-Spectrum";
        this.requiresPrescription = true;
    }

    public Antibiotic(String id, String code, String name, double unitPrice,
                      int reorderLevel, String supplierId, String description,
                      int stripSize, boolean isChewable, String spectrum, boolean requiresPrescription) {
        super(id, code, name, unitPrice, reorderLevel, supplierId, description, stripSize, isChewable);
        this.category = "ANTIBIOTIC";
        this.spectrum = spectrum != null ? spectrum : "Broad-Spectrum";
        this.requiresPrescription = requiresPrescription;
    }

    public String getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(String spectrum) {
        this.spectrum = spectrum;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    @Override
    public String getStorageCondition() {
        return "Store in cool, dry place away from direct sunlight (15°C - 25°C). Keep sealed.";
    }

    @Override
    public double calculateDiscountedPrice(int quantity) {
        // Antibiotics have controlled pricing with minimal bulk discounts
        double subtotal = unitPrice * quantity;
        if (quantity >= 200) {
            return subtotal * 0.96; // 4% discount
        }
        return subtotal;
    }

    @Override
    public String getDosageFormDetails() {
        return String.format("Antibiotic Tablet [%s, Rx Required: %s, %d/strip]",
                spectrum, (requiresPrescription ? "YES (Schedule-H)" : "NO"), getStripSize());
    }
}
