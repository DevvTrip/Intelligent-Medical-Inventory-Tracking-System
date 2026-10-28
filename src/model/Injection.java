package model;

import java.time.LocalDate;

public class Injection extends Medicine {

    private int doseMg;

    public Injection(String id, String name, double price, int qty,
            LocalDate expiryDate, int threshold, Supplier supplier, int doseMg) {
        super(id, name, price, qty, expiryDate, threshold, supplier);
        this.doseMg = doseMg;
    }

    public int getExtra() {
        return doseMg;
    }

    public String getStorageInfo() {
        return "Injection vial of " + doseMg + " mg, store in a cold place";
    }

    // cold storage ka extra 15 rupees charge add kiya hai
    public double calculatePrice() {
        return getPrice() + 15.0;
    }
}
