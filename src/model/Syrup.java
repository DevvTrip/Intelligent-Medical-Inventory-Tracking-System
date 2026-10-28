package model;

import java.time.LocalDate;

public class Syrup extends Medicine {

    private int bottleMl;

    public Syrup(String id, String name, double price, int qty,
            LocalDate expiryDate, int threshold, Supplier supplier, int bottleMl) {
        super(id, name, price, qty, expiryDate, threshold, supplier);
        this.bottleMl = bottleMl;
    }

    public int getExtra() {
        return bottleMl;
    }

    public String getStorageInfo() {
        return "Syrup bottle of " + bottleMl + " ml, keep away from sunlight";
    }

    // bottle ka charge 5% ke hisaab se add kiya hai
    public double calculatePrice() {
        return getPrice() * 1.05;
    }
}
