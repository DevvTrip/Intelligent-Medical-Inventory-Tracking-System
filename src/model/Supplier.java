package model;

public class Supplier {

    private String name;
    private String phone;

    public Supplier(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Supplier)) {
            return false;
        }
        Supplier other = (Supplier) obj;
        return name.equalsIgnoreCase(other.name);
    }

    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    public String toString() {
        return name + " (" + phone + ")";
    }
}
