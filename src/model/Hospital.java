package model;

import businesslogic.Inventory;

public class Hospital {

    private String name;
    private Inventory inventory;

    public Hospital(String name) {
        this.name = name;
        this.inventory = new Inventory();
    }

    public String getName() { return name; }
    public Inventory getInventory() { return inventory; }
}
