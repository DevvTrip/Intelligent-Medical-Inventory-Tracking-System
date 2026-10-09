package com.mits.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a medicine supplier or pharmaceutical distributor.
 * Demonstrates:
 * - Association relationship: Medicine references Supplier, but Supplier exists independently.
 * - Encapsulation with private fields and standard getters/setters.
 */
public class Supplier implements Serializable, Comparable<Supplier> {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;

    /** Default constructor */
    public Supplier() {
        this.id = "SUP-000";
        this.name = "Unknown Supplier";
        this.contactPerson = "";
        this.phone = "";
        this.email = "";
        this.address = "";
    }

    /** Parameterized constructor */
    public Supplier(String id, String name, String contactPerson, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int compareTo(Supplier other) {
        if (other == null) return 1;
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Supplier supplier)) return false;
        return Objects.equals(id, supplier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Contact: %s [%s]", name, id, contactPerson, phone);
    }
}
