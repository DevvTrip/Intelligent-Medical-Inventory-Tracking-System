package dataprocessing;

import java.time.LocalDate;
import java.util.ArrayList;
import model.Injection;
import model.Medicine;
import model.Supplier;
import model.Syrup;
import model.Tablet;

public class SampleData {

    // dates aaj ki date se banayi hain, taaki expiry warning demo me hamesha dikhe
    public static ArrayList<Medicine> getMedicines() {
        ArrayList<Medicine> list = new ArrayList<Medicine>();
        LocalDate today = LocalDate.now();

        Supplier s1 = new Supplier("MedPlus Distributors", "9810011111");
        Supplier s2 = new Supplier("HealthCare Supplies", "9820022222");
        Supplier s3 = new Supplier("Sun Pharma Depot", "9830033333");

        list.add(new Tablet("101", "Paracetamol 500", 22.5, 120,
                today.plusMonths(10), 30, s1, 10));
        list.add(new Tablet("102", "Amoxicillin 250", 45.0, 18,
                today.plusMonths(6), 25, s2, 10));
        list.add(new Syrup("103", "Cough Syrup Benadryl", 95.0, 40,
                today.plusDays(10), 15, s1, 100));
        list.add(new Injection("104", "Tetanus Toxoid", 150.0, 12,
                today.plusMonths(11), 10, s3, 500));
        list.add(new Tablet("105", "Metformin 500", 35.0, 8,
                today.plusMonths(4), 20, s2, 15));
        list.add(new Syrup("106", "ORS Solution", 40.0, 60,
                today.minusDays(10), 20, s3, 200));
        list.add(new Injection("107", "Insulin Injection", 320.0, 6,
                today.plusDays(20), 10, s1, 100));
        list.add(new Tablet("108", "Cetirizine 10", 15.0, 150,
                today.plusMonths(14), 40, s2, 10));
        list.add(new Syrup("109", "Antacid Gel", 70.0, 9,
                today.plusMonths(3), 12, s3, 170));
        list.add(new Injection("110", "Dexamethasone", 60.0, 25,
                today.minusDays(45), 10, s1, 4));
        return list;
    }
}
