package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import businesslogic.InvalidInputException;
import businesslogic.MedicineValidation;
import model.Injection;
import model.Medicine;
import model.Supplier;
import model.Syrup;
import model.Tablet;

public class FileUtil {
    public static final String DATA_FILE = "data/medicines.csv";
    public static final String REPORT_FILE = "data/report.txt";
    private static final String HEADER = "type,id,name,price,qty,expiry,threshold,supplierName,supplierPhone,extra";

    public static void saveReport(String text) throws IOException {
        new File("data").mkdirs();
        FileWriter writer = new FileWriter(REPORT_FILE);
        try {
            writer.write(text);
        } finally {
            writer.close();
        }
    }

    // pehle temp file me likho, phir asli file se replace karo taaki data corrupt na ho
    public static void saveMedicines(ArrayList<Medicine> list) throws IOException {
        File temporary = new File(DATA_FILE + ".tmp");
        new File("data").mkdirs();
        FileWriter writer = new FileWriter(temporary);
        writer.write(HEADER + "\n");
        for (Medicine medicine : list) {
            writer.write(medicine.toCsv() + "\n");
        }
        writer.close();
        File target = new File(DATA_FILE);
        target.delete();
        if (!temporary.renameTo(target)) {
            throw new IOException("Could not replace the medicines file");
        }
    }

    public static ArrayList<Medicine> loadMedicines() throws IOException {
        return loadMedicines(DATA_FILE);
    }

    static ArrayList<Medicine> loadMedicines(String filename) throws IOException {
        ArrayList<Medicine> list = new ArrayList<Medicine>();
        HashSet<String> ids = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            if (!HEADER.equals(reader.readLine())) {
                throw new IOException("Invalid CSV header in " + filename);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Medicine medicine = readMedicine(line);
                    if (!ids.add(medicine.getId())) {
                        throw new IOException("Duplicate medicine ID: " + medicine.getId());
                    }
                    list.add(medicine);
                }
            }
        } finally {
            reader.close();
        }
        return list;
    }

    private static Medicine readMedicine(String line) throws IOException {
        String[] fields = line.split(",", -1);
        if (fields.length != 10) {
            throw new IOException("Each medicine row must have 10 columns");
        }
        try {
            Medicine medicine;
            try {
                double price = Double.parseDouble(fields[3]);
                int quantity = Integer.parseInt(fields[4]);
                LocalDate expiry = LocalDate.parse(fields[5]);
                int limit = Integer.parseInt(fields[6]);
                int extra = Integer.parseInt(fields[9]);
                Supplier supplier = new Supplier(fields[7], fields[8]);
                if (fields[0].equals("Tablet")) {
                    medicine = new Tablet(fields[1], fields[2], price, quantity, expiry, limit, supplier, extra);
                } else if (fields[0].equals("Syrup")) {
                    medicine = new Syrup(fields[1], fields[2], price, quantity, expiry, limit, supplier, extra);
                } else if (fields[0].equals("Injection")) {
                    medicine = new Injection(fields[1], fields[2], price, quantity, expiry, limit, supplier, extra);
                } else {
                    throw new IOException("Unknown medicine type: " + fields[0]);
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                throw new IOException("Invalid number or date in medicine row", e);
            }
            MedicineValidation.check(medicine);
            return medicine;
        } catch (InvalidInputException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
