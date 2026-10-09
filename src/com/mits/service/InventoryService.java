package com.mits.service;

import com.mits.exception.*;
import com.mits.io.FilePersistenceManager;
import com.mits.model.*;
import com.mits.util.Constants;
import com.mits.util.StringProcessor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Core business service orchestrating inventory operations, transactions,
 * Jagged Arrays (Unit 2), Method Overloading (Unit 2), Synchronization (Unit 4),
 * and Seed Data initialization.
 */
public class InventoryService {

    private Inventory inventory;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final FilePersistenceManager persistenceManager;

    // Listeners for UI alert notifications (push model via Observer / Callbacks)
    private final List<Consumer<Alert>> alertListeners = new CopyOnWriteArrayList<>();

    public InventoryService() {
        this.supplierRepository = new SupplierRepository();
        this.userRepository = new UserRepository();
        this.persistenceManager = new FilePersistenceManager();
        this.inventory = new Inventory();
        initDefaultData();
    }

    /**
     * Initializes default suppliers, users, and seeds 25+ realistic medicines if no data exists.
     */
    public synchronized void initDefaultData() {
        // Seed default users
        if (userRepository.size() == 0) {
            userRepository.add(new User("admin", "admin123", "Chief Administrator", Role.ADMIN));
            userRepository.add(new User("pharmacist", "pharma123", "Head Pharmacist", Role.PHARMACIST));
            userRepository.add(new User("doctor", "doctor123", "Dr. A. Sharma (MD)", Role.DOCTOR));
        }

        // Seed default suppliers
        if (supplierRepository.size() == 0) {
            supplierRepository.add(new Supplier("SUP-001", "MedLife Pharmaceuticals", "Dr. Rajesh Kumar", "+91-9876543210", "rajesh@medlife.com", "Okhla Phase III, New Delhi"));
            supplierRepository.add(new Supplier("SUP-002", "Apex Healthcare Ltd", "Priya Verma", "+91-9812345678", "priya@apexhealth.in", "Sector 62, Noida"));
            supplierRepository.add(new Supplier("SUP-003", "BioCure Laboratories", "Vikram Malhotra", "+91-9988776655", "v.malhotra@biocure.org", "Peenya Industrial Area, Bengaluru"));
            supplierRepository.add(new Supplier("SUP-004", "SunPharma Distributors", "Ananya Deshmukh", "+91-9765432100", "ananya@sunpharma.com", "Andheri East, Mumbai"));
        }

        // Check if serialized inventory file exists; if so, load it
        File serFile = new File(Constants.INVENTORY_SER_FILE);
        if (serFile.exists() && serFile.length() > 0) {
            try {
                this.inventory = persistenceManager.loadInventoryBinary(Constants.INVENTORY_SER_FILE);
                StringProcessor.logActivity("Loaded existing inventory containing " + inventory.getMedicineCount() + " medicines.");
                return;
            } catch (Exception e) {
                System.err.println("Could not load inventory.ser: " + e.getMessage() + ". Initializing realistic seed data...");
            }
        }

        // Otherwise generate 28 realistic medicines across all categories
        seedRealisticMedicines();
        saveSnapshotSafe();
    }

    /**
     * Populates 28 realistic medicines across categories, including expired, near-expiry, and low-stock items.
     */
    public synchronized void seedRealisticMedicines() {
        inventory = new Inventory();
        LocalDate now = LocalDate.now();

        // 1. Tablets (Pain relief, fever, chronic care)
        Tablet t1 = new Tablet("M-01", "MED-001", "Paracetamol 650mg", 30.0, 50, "SUP-001", "Antipyretic & Analgesic for fever and pain", 15, false);
        t1.addStock(120, new Batch("B-PCM-101", 120, now.minusMonths(3), now.plusMonths(18)));
        inventory.addMedicine(t1);

        Tablet t2 = new Tablet("M-02", "MED-002", "Ibuprofen 400mg", 45.0, 30, "SUP-001", "NSAID pain reliever and anti-inflammatory", 10, false);
        t2.addStock(80, new Batch("B-IBU-201", 80, now.minusMonths(6), now.plusMonths(12)));
        inventory.addMedicine(t2);

        Tablet t3 = new Tablet("M-03", "MED-003", "Cetirizine 10mg", 25.0, 40, "SUP-002", "Antihistamine for allergy and cold symptoms", 10, false);
        t3.addStock(15, new Batch("B-CET-301", 15, now.minusMonths(10), now.plusMonths(8))); // LOW STOCK (15 <= 40)
        inventory.addMedicine(t3);

        Tablet t4 = new Tablet("M-04", "MED-004", "Metformin 500mg", 55.0, 50, "SUP-003", "First-line oral antidiabetic medicine", 20, false);
        t4.addStock(200, new Batch("B-MET-401", 200, now.minusMonths(4), now.plusMonths(20)));
        inventory.addMedicine(t4);

        Tablet t5 = new Tablet("M-05", "MED-005", "Amlodipine 5mg", 42.0, 35, "SUP-003", "Calcium channel blocker for hypertension", 15, false);
        t5.addStock(90, new Batch("B-AML-501", 90, now.minusMonths(5), now.plusMonths(14)));
        inventory.addMedicine(t5);

        Tablet t6 = new Tablet("M-06", "MED-006", "Pantoprazole 40mg", 85.0, 40, "SUP-002", "Proton-pump inhibitor for acid reflux/GERD", 10, false);
        t6.addStock(150, new Batch("B-PAN-601", 150, now.minusMonths(2), now.plusMonths(22)));
        inventory.addMedicine(t6);

        Tablet t7 = new Tablet("M-07", "MED-007", "Vitamin C 500mg Chewable", 60.0, 60, "SUP-004", "Immunity booster chewable tablet", 20, true);
        t7.addStock(25, new Batch("B-VTC-701", 25, now.minusMonths(8), now.plusDays(15))); // NEAR EXPIRY (< 30d) and LOW STOCK (25 <= 60)
        inventory.addMedicine(t7);

        Tablet t8 = new Tablet("M-08", "MED-008", "Aspirin 75mg Low Dose", 22.0, 45, "SUP-001", "Blood thinner and cardiovascular protection", 14, false);
        t8.addStock(110, new Batch("B-ASP-801", 110, now.minusMonths(7), now.plusMonths(11)));
        inventory.addMedicine(t8);

        // 2. Antibiotics (Inherits from Tablet)
        Antibiotic a1 = new Antibiotic("M-09", "MED-009", "Amoxicillin 500mg", 110.0, 35, "SUP-001", "Broad-spectrum beta-lactam antibiotic", 10, false, "Broad-Spectrum", true);
        a1.addStock(95, new Batch("B-AMX-901", 95, now.minusMonths(5), now.plusMonths(10)));
        inventory.addMedicine(a1);

        Antibiotic a2 = new Antibiotic("M-10", "MED-100", "Azithromycin 500mg", 130.0, 25, "SUP-002", "Macrolide antibiotic for respiratory infections", 5, false, "Broad-Spectrum", true);
        a2.addStock(12, new Batch("B-AZI-101", 12, now.minusMonths(9), now.plusMonths(9))); // LOW STOCK (12 <= 25)
        inventory.addMedicine(a2);

        Antibiotic a3 = new Antibiotic("M-11", "MED-101", "Ciprofloxacin 500mg", 95.0, 30, "SUP-003", "Fluoroquinolone antibiotic for bacterial infections", 10, false, "Broad-Spectrum", true);
        a3.addStock(75, new Batch("B-CIP-111", 75, now.minusMonths(4), now.plusMonths(16)));
        inventory.addMedicine(a3);

        Antibiotic a4 = new Antibiotic("M-12", "MED-102", "Doxycycline 100mg", 75.0, 20, "SUP-004", "Tetracycline class antibiotic for skin & tick infections", 10, false, "Broad-Spectrum", true);
        a4.addStock(40, new Batch("B-DOX-121", 40, now.minusMonths(11), now.plusDays(20))); // NEAR EXPIRY (< 30 days)
        inventory.addMedicine(a4);

        Antibiotic a5 = new Antibiotic("M-13", "MED-103", "Cephalexin 500mg", 140.0, 25, "SUP-002", "First-generation cephalosporin antibiotic", 10, false, "Narrow-Spectrum", true);
        a5.addStock(50, new Batch("B-CEP-131", 50, now.minusMonths(14), now.minusDays(5))); // EXPIRED MEDICINE (Expired 5 days ago)
        inventory.addMedicine(a5);

        // 3. Syrups (Pediatric and cough formulations)
        Syrup s1 = new Syrup("M-14", "MED-104", "Cough Relief Syrup", 85.0, 30, "SUP-001", "Expectorant and decongestant cough formula", 100, true);
        s1.addStock(85, new Batch("B-CSY-141", 85, now.minusMonths(3), now.plusMonths(15)));
        inventory.addMedicine(s1);

        Syrup s2 = new Syrup("M-15", "MED-105", "Paracetamol Paediatric 120mg", 45.0, 40, "SUP-001", "Child fever suspension with pleasant strawberry taste", 60, true);
        s2.addStock(60, new Batch("B-PP-151", 60, now.minusMonths(2), now.plusMonths(18)));
        inventory.addMedicine(s2);

        Syrup s3 = new Syrup("M-16", "MED-106", "Antacid Mint Suspension", 90.0, 35, "SUP-002", "Liquid antacid with magnesium and aluminum hydroxides", 200, false);
        s3.addStock(18, new Batch("B-ANT-161", 18, now.minusMonths(6), now.plusMonths(10))); // LOW STOCK (18 <= 35)
        inventory.addMedicine(s3);

        Syrup s4 = new Syrup("M-17", "MED-107", "Multivitamin Iron Syrup", 125.0, 25, "SUP-004", "Nutritional tonic for hemoglobin and energy", 200, true);
        s4.addStock(45, new Batch("B-MVI-171", 45, now.minusMonths(5), now.plusMonths(13)));
        inventory.addMedicine(s4);

        Syrup s5 = new Syrup("M-18", "MED-108", "Bromhexine Mucolytic Syrup", 70.0, 20, "SUP-003", "Thins mucus in chest congestion", 100, true);
        s5.addStock(30, new Batch("B-BRM-181", 30, now.minusMonths(10), now.plusDays(10))); // NEAR EXPIRY (< 30 days)
        inventory.addMedicine(s5);

        // 4. Injections (Critical care, emergency, cold-chain)
        Injection inj1 = new Injection("M-19", "MED-109", "Insulin Human Regular 100IU", 320.0, 40, "SUP-003", "Fast-acting recombinant human insulin", "Vial", true);
        inj1.addStock(65, new Batch("B-INS-191", 65, now.minusMonths(2), now.plusMonths(8)));
        inventory.addMedicine(inj1);

        Injection inj2 = new Injection("M-20", "MED-110", "Tetanus Toxoid 0.5ml", 35.0, 50, "SUP-001", "Vaccine injection preventing tetanus infection", "Ampoule", true);
        inj2.addStock(140, new Batch("B-TT-201", 140, now.minusMonths(3), now.plusMonths(24)));
        inventory.addMedicine(inj2);

        Injection inj3 = new Injection("M-21", "MED-111", "Adrenaline 1mg/ml Emergency", 80.0, 30, "SUP-002", "Emergency epinephrine for anaphylaxis and cardiac arrest", "Ampoule", false);
        inj3.addStock(8, new Batch("B-ADR-211", 8, now.minusMonths(7), now.plusMonths(6))); // LOW STOCK (8 <= 30)
        inventory.addMedicine(inj3);

        Injection inj4 = new Injection("M-22", "MED-112", "Ceftriaxone 1g IV/IM", 150.0, 35, "SUP-004", "Third-generation cephalosporin injectable antibiotic", "Vial", false);
        inj4.addStock(70, new Batch("B-CTX-221", 70, now.minusMonths(4), now.plusMonths(14)));
        inventory.addMedicine(inj4);

        Injection inj5 = new Injection("M-23", "MED-113", "Diclofenac Sodium 75mg/3ml", 45.0, 40, "SUP-001", "Injectable NSAID for severe post-operative pain", "Ampoule", false);
        inj5.addStock(55, new Batch("B-DIC-231", 55, now.minusMonths(12), now.minusDays(20))); // EXPIRED (Expired 20 days ago)
        inventory.addMedicine(inj5);

        Injection inj6 = new Injection("M-24", "MED-114", "Heparin Sodium 5000IU/ml", 280.0, 20, "SUP-003", "Anticoagulant injection preventing deep vein thrombosis", "Vial", false);
        inj6.addStock(35, new Batch("B-HEP-241", 35, now.minusMonths(5), now.plusMonths(15)));
        inventory.addMedicine(inj6);

        // 5. Ointments (Topical dermatological products)
        Ointment o1 = new Ointment("M-25", "MED-115", "Betamethasone 0.1% Cream", 65.0, 30, "SUP-002", "Topical corticosteroid for eczema and dermatitis", 20.0, "Cream");
        o1.addStock(50, new Batch("B-BET-251", 50, now.minusMonths(4), now.plusMonths(16)));
        inventory.addMedicine(o1);

        Ointment o2 = new Ointment("M-26", "MED-116", "Mupirocin 2% Ointment", 115.0, 25, "SUP-003", "Antibacterial ointment for impetigo and skin cuts", 15.0, "Ointment");
        o2.addStock(6, new Batch("B-MUP-261", 6, now.minusMonths(8), now.plusMonths(7))); // LOW STOCK (6 <= 25)
        inventory.addMedicine(o2);

        Ointment o3 = new Ointment("M-27", "MED-117", "Diclofenac Pain Relief Gel", 75.0, 35, "SUP-004", "Topical pain relieving gel for joint and muscle aches", 30.0, "Gel");
        o3.addStock(40, new Batch("B-DFG-271", 40, now.minusMonths(9), now.plusDays(25))); // NEAR EXPIRY (< 30 days)
        inventory.addMedicine(o3);

        Ointment o4 = new Ointment("M-28", "MED-118", "Silver Sulfadiazine 1% Burn Cream", 95.0, 20, "SUP-001", "Topical antimicrobial for prevention of burn wound sepsis", 50.0, "Cream");
        o4.addStock(32, new Batch("B-SSD-281", 32, now.minusMonths(3), now.plusMonths(19)));
        inventory.addMedicine(o4);

        // Populate initial alerts
        refreshAlerts();

        StringProcessor.logActivity("Initialized 28 realistic seed medicines across 5 categories.");
    }

    // ==========================================
    // SYNCHRONIZED STOCK OPERATIONS (Unit 4: Concurrency)
    // ==========================================

    /**
     * Issues stock safely with synchronization to prevent race conditions.
     */
    public synchronized void issueStock(String code, int quantity, String performedBy, String remarks)
            throws MedicineNotFoundException, InsufficientStockException, ExpiredMedicineException {
        Medicine med = inventory.getMedicine(code);
        if (med == null) {
            throw new MedicineNotFoundException(code);
        }

        // Deducts batches via FIFO inside synchronized med.issueStock
        med.issueStock(quantity);

        // Record transaction
        StockTransaction tx = new StockTransaction(
                "TXN-" + System.currentTimeMillis(),
                med.getCode(),
                med.getName(),
                StockTransaction.Type.ISSUE,
                quantity,
                LocalDateTime.now(),
                performedBy,
                remarks
        );
        inventory.addTransaction(tx);
        persistenceManager.appendAuditLog(String.format("ISSUED %d units of %s (%s) by %s", quantity, med.getName(), code, performedBy));

        // Check if stock dropped below reorder level
        if (med.needsReorder()) {
            Alert alert = new Alert(
                    "ALT-" + System.currentTimeMillis(),
                    med.getCode(),
                    med.getName(),
                    Alert.Type.LOW_STOCK,
                    Alert.Severity.WARNING,
                    String.format("Stock dropped to %d units (Reorder Threshold: %d)", med.getTotalStock(), med.getReorderLevel()),
                    LocalDateTime.now()
            );
            dispatchAlert(alert);
        }

        saveSnapshotSafe();
    }

    /**
     * Adds stock to an existing medicine.
     */
    public synchronized void addStock(String code, int quantity, String performedBy, String remarks)
            throws MedicineNotFoundException, InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("quantity", "Must be positive, received: " + quantity);
        }
        Medicine med = inventory.getMedicine(code);
        if (med == null) {
            throw new MedicineNotFoundException(code);
        }

        med.addStock(quantity);

        StockTransaction tx = new StockTransaction(
                "TXN-" + System.currentTimeMillis(),
                med.getCode(),
                med.getName(),
                StockTransaction.Type.RESTOCK,
                quantity,
                LocalDateTime.now(),
                performedBy,
                remarks
        );
        inventory.addTransaction(tx);
        persistenceManager.appendAuditLog(String.format("RESTOCKED %d units of %s (%s) by %s", quantity, med.getName(), code, performedBy));

        saveSnapshotSafe();
    }

    /**
     * Restocks medicine with specific batch number and expiry date.
     */
    public synchronized void restock(String code, int quantity, String batchNumber, LocalDate expiryDate, String performedBy)
            throws MedicineNotFoundException, InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("quantity", "Must be positive");
        }
        if (batchNumber == null || batchNumber.isBlank()) {
            throw new InvalidInputException("batchNumber", "Batch number cannot be empty");
        }
        Medicine med = inventory.getMedicine(code);
        if (med == null) {
            throw new MedicineNotFoundException(code);
        }

        Batch batch = new Batch(batchNumber.trim(), quantity, LocalDate.now(), expiryDate);
        med.addStock(quantity, batch);

        StockTransaction tx = new StockTransaction(
                "TXN-" + System.currentTimeMillis(),
                med.getCode(),
                med.getName(),
                StockTransaction.Type.RESTOCK,
                quantity,
                LocalDateTime.now(),
                performedBy,
                "Restocked Batch " + batchNumber
        );
        inventory.addTransaction(tx);
        persistenceManager.appendAuditLog(String.format("RESTOCKED Batch %s (%d units) for %s by %s", batchNumber, quantity, med.getName(), performedBy));

        saveSnapshotSafe();
    }

    // ==========================================
    // MEDICINE CRUD WITH VALIDATION (Unit 3 & Unit 5)
    // ==========================================

    public synchronized void addMedicine(Medicine medicine) throws DuplicateMedicineException, InvalidInputException {
        if (medicine == null) {
            throw new InvalidInputException("medicine", "Medicine object cannot be null");
        }
        if (!StringProcessor.isValidMedicineCode(medicine.getCode())) {
            throw new InvalidInputException("code", "Invalid format. Expected MED-XXX (e.g. MED-001)");
        }
        if (medicine.getName() == null || medicine.getName().trim().length() < 2) {
            throw new InvalidInputException("name", "Name must be at least 2 characters long");
        }
        if (medicine.getUnitPrice() < 0) {
            throw new InvalidInputException("unitPrice", "Price cannot be negative");
        }
        if (inventory.containsMedicine(medicine.getCode())) {
            throw new DuplicateMedicineException(medicine.getCode());
        }

        inventory.addMedicine(medicine);
        persistenceManager.appendAuditLog("Added new medicine: " + medicine.getCode() + " - " + medicine.getName());
        saveSnapshotSafe();
    }

    public synchronized void updateMedicine(Medicine medicine) throws MedicineNotFoundException, InvalidInputException {
        if (medicine == null || medicine.getCode() == null) {
            throw new InvalidInputException("medicine", "Invalid medicine update payload");
        }
        if (!inventory.containsMedicine(medicine.getCode())) {
            throw new MedicineNotFoundException(medicine.getCode());
        }
        inventory.addMedicine(medicine);
        persistenceManager.appendAuditLog("Updated medicine: " + medicine.getCode() + " - " + medicine.getName());
        saveSnapshotSafe();
    }

    public synchronized void deleteMedicine(String code) throws MedicineNotFoundException {
        if (!inventory.containsMedicine(code)) {
            throw new MedicineNotFoundException(code);
        }
        Medicine removed = inventory.removeMedicine(code);
        persistenceManager.appendAuditLog("Deleted medicine: " + code + " (" + (removed != null ? removed.getName() : "") + ")");
        saveSnapshotSafe();
    }

    public synchronized Medicine getMedicine(String code) throws MedicineNotFoundException {
        Medicine med = inventory.getMedicine(code);
        if (med == null) {
            throw new MedicineNotFoundException(code);
        }
        return med;
    }

    public synchronized List<Medicine> getAllMedicines() {
        return inventory.getAllMedicines();
    }

    // ==========================================
    // METHOD OVERLOADING (Unit 2: Polymorphism)
    // ==========================================

    /**
     * Overload 1: Searches by query string matching code, name, or description.
     */
    public synchronized List<Medicine> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllMedicines();
        }
        List<Medicine> results = new ArrayList<>();
        for (Medicine m : inventory.getAllMedicines()) {
            if (StringProcessor.matchesQuery(m.getCode(), query)
                    || StringProcessor.matchesQuery(m.getName(), query)
                    || StringProcessor.matchesQuery(m.getDescription(), query)) {
                results.add(m);
            }
        }
        return results;
    }

    /**
     * Overload 2: Searches by query string filtered by specific Category.
     */
    public synchronized List<Medicine> search(String query, String category) {
        List<Medicine> initial = search(query);
        if (category == null || category.equalsIgnoreCase("ALL") || category.isBlank()) {
            return initial;
        }
        List<Medicine> filtered = new ArrayList<>();
        for (Medicine m : initial) {
            if (m.getCategory().equalsIgnoreCase(category.trim())) {
                filtered.add(m);
            }
        }
        return filtered;
    }

    /**
     * Overload 3: Searches medicines within a unit price range.
     */
    public synchronized List<Medicine> search(double minPrice, double maxPrice) {
        List<Medicine> results = new ArrayList<>();
        for (Medicine m : inventory.getAllMedicines()) {
            if (m.getUnitPrice() >= minPrice && m.getUnitPrice() <= maxPrice) {
                results.add(m);
            }
        }
        return results;
    }

    // ==========================================
    // JAGGED ARRAYS (Unit 2: Arrays & Jagged Arrays)
    // ==========================================

    /**
     * Demonstrates Jagged Arrays: Monthly consumption report.
     * Each medicine has an array of monthly consumption figures, with differing lengths
     * (e.g. newly introduced medicines have 3 months of data, older ones have 6 or 12 months).
     *
     * @return int[][] jagged array where row i corresponds to medicine i's monthly consumption
     */
    public synchronized int[][] getMonthlyConsumptionJaggedArray() {
        List<Medicine> medicines = getAllMedicines();
        int size = Math.min(medicines.size(), 8); // Top 8 medicines
        int[][] jagged = new int[size][];

        // Realistic seed lengths: medicines have differing number of recorded months
        int[] monthLengths = {6, 12, 3, 9, 6, 12, 4, 8};

        Random rand = new Random(42); // deterministic for testing reproducibility
        for (int i = 0; i < size; i++) {
            int months = monthLengths[i % monthLengths.length];
            jagged[i] = new int[months];
            for (int m = 0; m < months; m++) {
                // monthly consumption between 15 and 95 units
                jagged[i][m] = 15 + rand.nextInt(80);
            }
        }
        return jagged;
    }

    /**
     * Computes total consumption for each medicine in the jagged array.
     */
    public int[] getMedicineConsumptionTotals(int[][] jagged) {
        if (jagged == null) return new int[0];
        int[] totals = new int[jagged.length];
        for (int i = 0; i < jagged.length; i++) {
            int sum = 0;
            for (int val : jagged[i]) {
                sum += val;
            }
            totals[i] = sum;
        }
        return totals;
    }

    /**
     * Computes average monthly consumption for each medicine in the jagged array.
     */
    public double[] getMedicineConsumptionAverages(int[][] jagged) {
        if (jagged == null) return new double[0];
        double[] averages = new double[jagged.length];
        for (int i = 0; i < jagged.length; i++) {
            if (jagged[i].length == 0) {
                averages[i] = 0.0;
            } else {
                int sum = 0;
                for (int val : jagged[i]) {
                    sum += val;
                }
                averages[i] = (double) sum / jagged[i].length;
            }
        }
        return averages;
    }

    // ==========================================
    // ALERT MANAGEMENT & REAL-TIME MONITORING
    // ==========================================

    public void addAlertListener(Consumer<Alert> listener) {
        alertListeners.add(listener);
    }

    public void removeAlertListener(Consumer<Alert> listener) {
        alertListeners.remove(listener);
    }

    public synchronized void dispatchAlert(Alert alert) {
        inventory.addAlert(alert);
        for (Consumer<Alert> listener : alertListeners) {
            try {
                listener.accept(alert);
            } catch (Exception e) {
                // Ignore listener exceptions
            }
        }
    }

    /**
     * Scans inventory for low-stock, near-expiry, and expired medicines and generates alerts.
     */
    public synchronized List<Alert> refreshAlerts() {
        List<Alert> newAlerts = new ArrayList<>();
        for (Medicine m : inventory.getAllMedicines()) {
            if (m.isExpired()) {
                Alert a = new Alert("ALT-" + System.currentTimeMillis() + "-" + m.getCode(),
                        m.getCode(), m.getName(), Alert.Type.EXPIRED, Alert.Severity.CRITICAL,
                        "Expired on " + m.getExpiryDate() + "! Quarantined for disposal.",
                        LocalDateTime.now());
                dispatchAlert(a);
                newAlerts.add(a);
            } else if (m.daysToExpiry() <= 30) {
                Alert a = new Alert("ALT-" + System.currentTimeMillis() + "-" + m.getCode(),
                        m.getCode(), m.getName(), Alert.Type.NEAR_EXPIRY, Alert.Severity.WARNING,
                        "Expires in " + m.daysToExpiry() + " days (" + m.getExpiryDate() + ")",
                        LocalDateTime.now());
                dispatchAlert(a);
                newAlerts.add(a);
            }

            if (m.needsReorder()) {
                Alert a = new Alert("ALT-" + System.currentTimeMillis() + "-" + m.getCode(),
                        m.getCode(), m.getName(), Alert.Type.LOW_STOCK, Alert.Severity.WARNING,
                        "Stock low: " + m.getTotalStock() + " units (Threshold: " + m.getReorderLevel() + ")",
                        LocalDateTime.now());
                dispatchAlert(a);
                newAlerts.add(a);
            }
        }
        return newAlerts;
    }

    public synchronized List<Alert> getActiveAlerts() {
        return inventory.getAlertHistory();
    }

    // ==========================================
    // AUTHENTICATION (Unit 3: AuthenticationException)
    // ==========================================

    public User authenticate(String username, String password) throws AuthenticationException {
        if (username == null || password == null) {
            throw new AuthenticationException("null");
        }
        Optional<User> opt = userRepository.findById(username.trim());
        if (opt.isPresent()) {
            User user = opt.get();
            if (user.getPassword().equals(password.trim())) {
                StringProcessor.logActivity("User logged in successfully: " + username + " (" + user.getRole() + ")");
                return user;
            }
        }
        StringProcessor.logActivity("Failed login attempt for user: " + username);
        throw new AuthenticationException(username);
    }

    // ==========================================
    // PERSISTENCE & BACKUP HELPER
    // ==========================================

    public synchronized void saveSnapshotSafe() {
        try {
            persistenceManager.saveInventoryBinary(inventory, Constants.INVENTORY_SER_FILE);
            persistenceManager.exportMedicinesCsv(inventory.getAllMedicines(), Constants.MEDICINES_CSV_FILE);
        } catch (IOException e) {
            System.err.println("Warning: Auto-save failed: " + e.getMessage());
        }
    }

    public synchronized String triggerBackup() throws IOException {
        return persistenceManager.createTimestampedBackup(inventory);
    }

    public synchronized void restoreBackup() throws IOException, ClassNotFoundException {
        this.inventory = persistenceManager.restoreLatestBackup();
        saveSnapshotSafe();
    }

    // Accessors
    public Inventory getInventory() {
        return inventory;
    }

    public SupplierRepository getSupplierRepository() {
        return supplierRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public FilePersistenceManager getPersistenceManager() {
        return persistenceManager;
    }
}
