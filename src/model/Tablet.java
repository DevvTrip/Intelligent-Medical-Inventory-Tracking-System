package model;

import java.time.LocalDate;

public class Tablet extends Medicine {

    private int tabletsPerStrip;

    public Tablet(String id, String name, double price, int qty,
            LocalDate expiryDate, int threshold, Supplier supplier, int tabletsPerStrip) {
        super(id, name, price, qty, expiryDate, threshold, supplier);
        this.tabletsPerStrip = tabletsPerStrip;
    }

    public int getExtra() {
        return tabletsPerStrip;
    }

    public String getStorageInfo() {
        return "Tablet strip of " + tabletsPerStrip + " tablets, keep in a cool dry place";
    }

    // packing ka 2 rupees ka charge price me add ho jata hai
    public double calculatePrice() {
        return getPrice() + 2.0;
    }
}
