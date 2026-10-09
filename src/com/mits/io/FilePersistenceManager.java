package com.mits.io;

import com.mits.model.*;
import com.mits.util.Constants;
import com.mits.util.StringProcessor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles all file I/O operations, snapshots, CSV import/export, and backup/recovery.
 * Demonstrates:
 * - Unit 4: Byte Streams (FileInputStream, FileOutputStream, BufferedInputStream, BufferedOutputStream, ObjectInputStream, ObjectOutputStream)
 * - Unit 4: Character Streams (FileReader, FileWriter, BufferedReader, BufferedWriter, PrintWriter)
 * - Unit 4: Backup & Recovery: timestamped backups, restore latest, graceful corrupted file handling.
 */
public class FilePersistenceManager {

    public FilePersistenceManager() {
        ensureDirectories();
    }

    /**
     * Ensures ./data and ./data/backup directories exist.
     */
    public static void ensureDirectories() {
        File dataDir = new File(Constants.DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File backupDir = new File(Constants.BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }

    // ==========================================
    // BYTE STREAMS: Object Serialization (Unit 4)
    // ==========================================

    /**
     * Saves Inventory object graph to disk using Byte Streams.
     */
    public synchronized void saveInventoryBinary(Inventory inventory, String filePath) throws IOException {
        ensureDirectories();
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(inventory);
            oos.flush();
            StringProcessor.logActivity("Inventory serialized to binary file: " + filePath);
        }
    }

    /**
     * Loads Inventory object graph from disk using Byte Streams.
     * Gracefully handles corrupted files.
     */
    public synchronized Inventory loadInventoryBinary(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            throw new FileNotFoundException("Binary file does not exist or is empty: " + filePath);
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object obj = ois.readObject();
            if (obj instanceof Inventory inventory) {
                StringProcessor.logActivity("Inventory deserialized successfully from: " + filePath);
                return inventory;
            } else {
                throw new IOException("Corrupted file format: deserialized object is not an Inventory");
            }
        }
    }

    // ==========================================
    // CHARACTER STREAMS: CSV Import / Export (Unit 4)
    // ==========================================

    /**
     * Exports medicine list to CSV using Character Streams (FileWriter, BufferedWriter, PrintWriter).
     */
    public synchronized void exportMedicinesCsv(List<Medicine> medicines, String filePath) throws IOException {
        ensureDirectories();
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            // CSV Header
            pw.println("Type,ID,Code,Name,UnitPrice,ReorderLevel,SupplierId,Description,Extra1,Extra2,BatchNum,Quantity,MfgDate,ExpDate");

            for (Medicine m : medicines) {
                String extra1 = "";
                String extra2 = "";

                if (m instanceof Antibiotic a) {
                    extra1 = a.getSpectrum();
                    extra2 = String.valueOf(a.isRequiresPrescription());
                } else if (m instanceof Tablet t) {
                    extra1 = String.valueOf(t.getStripSize());
                    extra2 = String.valueOf(t.isChewable());
                } else if (m instanceof Syrup s) {
                    extra1 = String.valueOf(s.getBottleVolumeMl());
                    extra2 = String.valueOf(s.isDosageMeasuringCupIncluded());
                } else if (m instanceof Injection inj) {
                    extra1 = inj.getVialType();
                    extra2 = String.valueOf(inj.isColdChainRequired());
                } else if (m instanceof Ointment o) {
                    extra1 = String.valueOf(o.getTubeWeightGrams());
                    extra2 = o.getBaseType();
                }

                List<Batch> batches = m.getBatches();
                if (batches.isEmpty()) {
                    pw.printf("%s,%s,%s,%s,%.2f,%d,%s,%s,%s,%s,%s,%d,%s,%s%n",
                            m.getCategory(), m.getId(), m.getCode(), sanitizeCsv(m.getName()),
                            m.getUnitPrice(), m.getReorderLevel(), m.getSupplierId(),
                            sanitizeCsv(m.getDescription()), extra1, extra2,
                            "BATCH-01", 0, LocalDate.now(), LocalDate.now().plusYears(1));
                } else {
                    for (Batch b : batches) {
                        pw.printf("%s,%s,%s,%s,%.2f,%d,%s,%s,%s,%s,%s,%d,%s,%s%n",
                                m.getCategory(), m.getId(), m.getCode(), sanitizeCsv(m.getName()),
                                m.getUnitPrice(), m.getReorderLevel(), m.getSupplierId(),
                                sanitizeCsv(m.getDescription()), extra1, extra2,
                                b.getBatchNumber(), b.getQuantity(), b.getManufactureDate(), b.getExpiryDate());
                    }
                }
            }
            pw.flush();
            StringProcessor.logActivity("Medicines exported to CSV via Character Streams: " + filePath);
        }
    }

    /**
     * Imports medicines from CSV using Character Streams (FileReader, BufferedReader).
     */
    public synchronized List<Medicine> importMedicinesCsv(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("CSV file not found: " + filePath);
        }

        Map<String, Medicine> medicineMap = new LinkedHashMap<>();

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] tokens = StringProcessor.tokenizeCsv(line);
                if (tokens.length < 14) continue;

                String type = tokens[0].toUpperCase();
                String id = tokens[1];
                String code = tokens[2];
                String name = tokens[3];
                double unitPrice = parseDoubleSafe(tokens[4], 0.0);
                int reorderLevel = parseIntSafe(tokens[5], 10);
                String supplierId = tokens[6];
                String description = tokens[7];
                String extra1 = tokens[8];
                String extra2 = tokens[9];
                String batchNum = tokens[10];
                int batchQty = parseIntSafe(tokens[11], 0);
                LocalDate mfgDate = parseDateSafe(tokens[12], LocalDate.now());
                LocalDate expDate = parseDateSafe(tokens[13], LocalDate.now().plusYears(1));

                Medicine med = medicineMap.get(code);
                if (med == null) {
                    switch (type) {
                        case "ANTIBIOTIC":
                            med = new Antibiotic(id, code, name, unitPrice, reorderLevel, supplierId, description,
                                    10, false, extra1, Boolean.parseBoolean(extra2));
                            break;
                        case "TABLET":
                            med = new Tablet(id, code, name, unitPrice, reorderLevel, supplierId, description,
                                    parseIntSafe(extra1, 10), Boolean.parseBoolean(extra2));
                            break;
                        case "SYRUP":
                            med = new Syrup(id, code, name, unitPrice, reorderLevel, supplierId, description,
                                    parseIntSafe(extra1, 100), Boolean.parseBoolean(extra2));
                            break;
                        case "INJECTION":
                            med = new Injection(id, code, name, unitPrice, reorderLevel, supplierId, description,
                                    extra1, Boolean.parseBoolean(extra2));
                            break;
                        case "OINTMENT":
                            med = new Ointment(id, code, name, unitPrice, reorderLevel, supplierId, description,
                                    parseDoubleSafe(extra1, 20.0), extra2);
                            break;
                        default:
                            med = new Tablet(id, code, name, unitPrice, reorderLevel, supplierId, description, 10, false);
                            break;
                    }
                    medicineMap.put(code, med);
                }

                Batch batch = new Batch(batchNum, batchQty, mfgDate, expDate);
                med.addBatch(batch);
            }
        }
        return new ArrayList<>(medicineMap.values());
    }

    /**
     * Appends an audit message directly to disk log file using character streams.
     */
    public synchronized void appendAuditLog(String logMessage) {
        ensureDirectories();
        try (FileWriter fw = new FileWriter(Constants.AUDIT_LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            pw.printf("[%s] %s%n", timestamp, logMessage);
            pw.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to audit log file: " + e.getMessage());
        }
    }

    // ==========================================
    // BACKUP & RECOVERY (Unit 4)
    // ==========================================

    /**
     * Creates a timestamped backup copy in ./data/backup/
     */
    public synchronized String createTimestampedBackup(Inventory inventory) throws IOException {
        ensureDirectories();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFilePath = Constants.BACKUP_DIR + File.separator + "backup_" + timestamp + ".ser";
        saveInventoryBinary(inventory, backupFilePath);

        String csvBackupPath = Constants.BACKUP_DIR + File.separator + "backup_" + timestamp + ".csv";
        exportMedicinesCsv(inventory.getAllMedicines(), csvBackupPath);

        appendAuditLog("Backup created successfully: " + backupFilePath);
        return backupFilePath;
    }

    /**
     * Lists all available backup files sorted newest first.
     */
    public synchronized List<File> listBackups() {
        File dir = new File(Constants.BACKUP_DIR);
        File[] files = dir.listFiles((d, name) -> name.startsWith("backup_") && name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        List<File> list = new ArrayList<>(Arrays.asList(files));
        list.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        return list;
    }

    /**
     * Restores the most recent backup available. Gracefully handles corrupted files.
     */
    public synchronized Inventory restoreLatestBackup() throws IOException, ClassNotFoundException {
        List<File> backups = listBackups();
        if (backups.isEmpty()) {
            throw new FileNotFoundException("No backup files found in " + Constants.BACKUP_DIR);
        }

        // Try loading from the newest backup; if corrupt, fallback to previous
        for (File backupFile : backups) {
            try {
                Inventory inv = loadInventoryBinary(backupFile.getAbsolutePath());
                appendAuditLog("Restored inventory from backup: " + backupFile.getName());
                return inv;
            } catch (Exception ex) {
                System.err.println("Warning: Backup file " + backupFile.getName() + " was corrupted: " + ex.getMessage());
                appendAuditLog("Corrupted backup detected: " + backupFile.getName() + " -> trying previous backup...");
            }
        }
        throw new IOException("All available backup files failed to load.");
    }

    // Helper parsing utilities
    private String sanitizeCsv(String val) {
        if (val == null) return "";
        return val.replace(",", ";").replace("\n", " ").trim();
    }

    private double parseDoubleSafe(String s, double fallback) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private int parseIntSafe(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private LocalDate parseDateSafe(String s, LocalDate fallback) {
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}
