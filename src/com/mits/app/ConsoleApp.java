package com.mits.app;

import com.mits.exception.*;
import com.mits.model.*;
import com.mits.report.ReportService;
import com.mits.service.InventoryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven console application for the Medical Inventory Tracking System.
 * Demonstrates:
 * - Unit 1: Command-line arguments (--seed, --report, --user, --help)
 * - Unit 1: Switch statements, loops, Scanner input
 * - Unit 2: Method overloading (search by name, by category, by price range)
 * - Unit 3: Exception handling with custom exceptions
 */
public class ConsoleApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static InventoryService inventoryService;
    private static ReportService reportService;
    private static User currentUser;

    public static void main(String[] args) {
        inventoryService = new InventoryService();
        reportService = new ReportService(inventoryService);

        String prefillUser = null;
        boolean forceSeed = false;
        boolean reportAndExit = false;

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--seed":
                    forceSeed = true;
                    break;
                case "--report":
                    reportAndExit = true;
                    break;
                case "--user":
                    if (i + 1 < args.length) {
                        prefillUser = args[++i];
                    } else {
                        System.err.println("Error: --user requires a username argument.");
                        printUsage();
                        System.exit(1);
                    }
                    break;
                case "--help":
                    printUsage();
                    System.exit(0);
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    printUsage();
                    System.exit(1);
            }
        }

        if (forceSeed) {
            inventoryService.seedRealisticMedicines();
            System.out.println("Seed data regenerated.");
        }

        if (reportAndExit) {
            System.out.println(reportService.generateFullInventoryValuationReport());
            System.exit(0);
        }

        // Login loop
        while (currentUser == null) {
            try {
                String username = prefillUser;
                if (username == null) {
                    System.out.print("Username: ");
                    username = scanner.nextLine().trim();
                }
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();

                currentUser = inventoryService.authenticate(username, password);
                System.out.println("Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")!");
            } catch (AuthenticationException e) {
                System.out.println("Login failed: " + e.getMessage());
                prefillUser = null; // Clear prefill so user can retry
            }
        }

        // Main menu loop
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": viewAllMedicines(); break;
                    case "2": searchMedicine(); break;
                    case "3": addMedicine(); break;
                    case "4": issueStock(); break;
                    case "5": restock(); break;
                    case "6": viewAlerts(); break;
                    case "7": generateReport(); break;
                    case "8": backup(); break;
                    case "9": restore(); break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Enter 0-9.");
                }
            } catch (MedicineNotFoundException e) {
                System.out.println("[Not Found] " + e.getMessage());
            } catch (InsufficientStockException e) {
                System.out.println("[Stock Error] " + e.getMessage());
            } catch (ExpiredMedicineException e) {
                System.out.println("[Expired] " + e.getMessage());
            } catch (DuplicateMedicineException e) {
                System.out.println("[Duplicate] " + e.getMessage());
            } catch (InvalidInputException e) {
                System.out.println("[Invalid Input] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[Format Error] Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("[Error] " + e.getMessage());
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java com.mits.app.ConsoleApp [options]");
        System.out.println("Options:");
        System.out.println("  --seed          Force re-seed of sample medicine data");
        System.out.println("  --report        Print full inventory report and exit");
        System.out.println("  --user <name>   Pre-fill username for login prompt");
        System.out.println("  --help          Print this help and exit");
    }

    private static void printMenu() {
        System.out.println("\n========= MITS Main Menu =========");
        System.out.println("  1. View All Medicines");
        System.out.println("  2. Search Medicine");
        System.out.println("  3. Add Medicine");
        System.out.println("  4. Issue Stock");
        System.out.println("  5. Restock");
        System.out.println("  6. View Alerts");
        System.out.println("  7. Generate Report");
        System.out.println("  8. Backup Now");
        System.out.println("  9. Restore Backup");
        System.out.println("  0. Exit");
        System.out.println("==================================");
    }

    private static void viewAllMedicines() {
        List<Medicine> medicines = inventoryService.getAllMedicines();
        if (medicines.isEmpty()) {
            System.out.println("No medicines in inventory.");
            return;
        }
        for (Medicine m : medicines) {
            System.out.println(m.generateSummary());
        }
        System.out.println("Total: " + medicines.size() + " medicines");
    }

    // Demonstrates method overloading: search(String), search(String,String), search(double,double)
    private static void searchMedicine() {
        System.out.println("  1. Search by Name/Code");
        System.out.println("  2. Search by Name + Category");
        System.out.println("  3. Search by Price Range");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        List<Medicine> results;
        switch (c) {
            case "1":
                System.out.print("Enter search query: ");
                String q = scanner.nextLine().trim();
                results = inventoryService.search(q); // Overload 1
                break;
            case "2":
                System.out.print("Enter name/query: ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter category (TABLET/ANTIBIOTIC/SYRUP/INJECTION/OINTMENT): ");
                String cat = scanner.nextLine().trim();
                results = inventoryService.search(name, cat); // Overload 2
                break;
            case "3":
                System.out.print("Enter min price: ");
                double min = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Enter max price: ");
                double max = Double.parseDouble(scanner.nextLine().trim());
                results = inventoryService.search(min, max); // Overload 3
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            for (Medicine m : results) {
                System.out.println(m.generateSummary());
            }
            System.out.println("Found: " + results.size() + " medicine(s)");
        }
    }

    private static void addMedicine() throws DuplicateMedicineException, InvalidInputException {
        System.out.println("Medicine Type:");
        System.out.println("  1. Tablet     2. Syrup     3. Injection");
        System.out.println("  4. Ointment   5. Antibiotic");
        System.out.print("Choice: ");
        String type = scanner.nextLine().trim();

        System.out.print("Code (e.g. MED-029): ");
        String code = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Unit Price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Reorder Level: ");
        int reorder = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Supplier ID (e.g. SUP-001): ");
        String supplierId = scanner.nextLine().trim();
        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();

        String id = "M-" + System.currentTimeMillis() % 1000;
        Medicine medicine;

        switch (type) {
            case "1":
                System.out.print("Strip Size: ");
                int strip = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Chewable? (y/n): ");
                boolean chew = scanner.nextLine().trim().equalsIgnoreCase("y");
                medicine = new Tablet(id, code, name, price, reorder, supplierId, desc, strip, chew);
                break;
            case "2":
                System.out.print("Bottle Volume (ml): ");
                int vol = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Measuring Cup Included? (y/n): ");
                boolean cup = scanner.nextLine().trim().equalsIgnoreCase("y");
                medicine = new Syrup(id, code, name, price, reorder, supplierId, desc, vol, cup);
                break;
            case "3":
                System.out.print("Vial Type (Vial/Ampoule): ");
                String vial = scanner.nextLine().trim();
                System.out.print("Cold Chain Required? (y/n): ");
                boolean cold = scanner.nextLine().trim().equalsIgnoreCase("y");
                medicine = new Injection(id, code, name, price, reorder, supplierId, desc, vial, cold);
                break;
            case "4":
                System.out.print("Tube Weight (grams): ");
                double weight = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Base Type (Cream/Gel/Ointment): ");
                String base = scanner.nextLine().trim();
                medicine = new Ointment(id, code, name, price, reorder, supplierId, desc, weight, base);
                break;
            case "5":
                System.out.print("Strip Size: ");
                int aStrip = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Spectrum (Broad/Narrow): ");
                String spec = scanner.nextLine().trim();
                System.out.print("Prescription Required? (y/n): ");
                boolean rx = scanner.nextLine().trim().equalsIgnoreCase("y");
                medicine = new Antibiotic(id, code, name, price, reorder, supplierId, desc, aStrip, false, spec, rx);
                break;
            default:
                System.out.println("Invalid type.");
                return;
        }

        inventoryService.addMedicine(medicine);
        System.out.println("Medicine '" + name + "' added successfully!");
    }

    private static void issueStock() throws MedicineNotFoundException, InsufficientStockException, ExpiredMedicineException {
        System.out.print("Medicine Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Quantity to issue: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Remarks: ");
        String remarks = scanner.nextLine().trim();

        inventoryService.issueStock(code, qty, currentUser.getUsername(), remarks);
        Medicine m = inventoryService.getMedicine(code);
        System.out.println("Issued " + qty + " units. Remaining stock: " + m.getTotalStock());
    }

    private static void restock() throws MedicineNotFoundException, InvalidInputException {
        System.out.print("Medicine Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Batch Number: ");
        String batch = scanner.nextLine().trim();
        System.out.print("Expiry Date (YYYY-MM-DD): ");
        LocalDate expiry = LocalDate.parse(scanner.nextLine().trim());

        inventoryService.restock(code, qty, batch, expiry, currentUser.getUsername());
        System.out.println("Restocked " + qty + " units for " + code + " successfully!");
    }

    private static void viewAlerts() {
        List<Alert> alerts = inventoryService.getActiveAlerts();
        if (alerts.isEmpty()) {
            System.out.println("No active alerts.");
        } else {
            System.out.println("--- Active Alerts (" + alerts.size() + ") ---");
            for (Alert a : alerts) {
                System.out.println(a);
            }
        }
    }

    private static void generateReport() {
        System.out.println("  1. Low Stock     2. Expiry Risk     3. Consumption (Jagged Array)");
        System.out.println("  4. Supplier      5. Full Inventory");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        String report;
        switch (c) {
            case "1": report = reportService.generateLowStockReport(); break;
            case "2": report = reportService.generateExpiryRiskReport(); break;
            case "3": report = reportService.generateJaggedConsumptionReport(); break;
            case "4": report = reportService.generateSupplierReport(); break;
            case "5": report = reportService.generateFullInventoryValuationReport(); break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        System.out.println(report);
    }

    private static void backup() {
        try {
            String path = inventoryService.triggerBackup();
            System.out.println("Backup created: " + path);
        } catch (Exception e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    private static void restore() {
        try {
            inventoryService.restoreBackup();
            System.out.println("Backup restored successfully.");
        } catch (Exception e) {
            System.out.println("Restore failed: " + e.getMessage());
        }
    }
}
