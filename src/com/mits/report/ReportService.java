package com.mits.report;

import com.mits.model.Medicine;
import com.mits.model.Supplier;
import com.mits.service.InventoryService;
import com.mits.util.StringProcessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Report generation service utilizing StringBuilder, Jagged Arrays, and Character Streams.
 * Demonstrates:
 * - Unit 3: StringBuilder for high-performance formatted text reports.
 * - Unit 2: Jagged Arrays for monthly consumption reporting with totals and averages.
 * - Unit 4: Character Streams (FileWriter, BufferedWriter, PrintWriter) for report export.
 */
public class ReportService {

    private final InventoryService inventoryService;

    public ReportService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Generates Low Stock Report.
     */
    public String generateLowStockReport() {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb, "LOW STOCK REORDER ADVISORY REPORT");

        List<Medicine> all = inventoryService.getAllMedicines();
        List<Medicine> lowStock = new ArrayList<>();
        for (Medicine m : all) {
            if (m.needsReorder()) {
                lowStock.add(m);
            }
        }

        sb.append(String.format("Found %d medicines requiring immediate replenishment:%n%n", lowStock.size()));
        sb.append(String.format("%-10s | %-28s | %-12s | %8s | %8s | %10s | %10s%n",
                "Code", "Name", "Category", "Current", "Reorder", "Order Qty", "Unit Price"));
        sb.append(StringProcessor.repeatChar('-', 96)).append("\n");

        double totalReorderCost = 0.0;
        for (Medicine m : lowStock) {
            int reorderQty = m.reorderQuantity();
            double cost = reorderQty * m.getUnitPrice();
            totalReorderCost += cost;
            sb.append(String.format("%-10s | %-28s | %-12s | %8d | %8d | %10d | ₹%9.2f%n",
                    m.getCode(),
                    StringProcessor.padRight(m.getName(), 28),
                    m.getCategory(),
                    m.getTotalStock(),
                    m.getReorderLevel(),
                    reorderQty,
                    m.getUnitPrice()));
        }

        sb.append(StringProcessor.repeatChar('=', 96)).append("\n");
        sb.append(String.format("Total Estimated Restocking Cost: ₹%,.2f%n", totalReorderCost));
        appendFooter(sb);
        return sb.toString();
    }

    /**
     * Generates Expiry Risk Report.
     */
    public String generateExpiryRiskReport() {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb, "MEDICINE EXPIRY RISK & QUARANTINE REPORT");

        List<Medicine> all = inventoryService.getAllMedicines();
        List<Medicine> expired = new ArrayList<>();
        List<Medicine> nearExpiry = new ArrayList<>();

        for (Medicine m : all) {
            if (m.isExpired()) {
                expired.add(m);
            } else if (m.daysToExpiry() <= 30) {
                nearExpiry.add(m);
            }
        }

        sb.append(String.format("CRITICAL ALERT - EXPIRED MEDICINES (%d found):%n", expired.size()));
        sb.append(String.format("%-10s | %-28s | %-12s | %8s | %12s | %s%n",
                "Code", "Name", "Category", "Stock", "Expiry Date", "Action Required"));
        sb.append(StringProcessor.repeatChar('-', 88)).append("\n");
        for (Medicine m : expired) {
            sb.append(String.format("%-10s | %-28s | %-12s | %8d | %12s | %s%n",
                    m.getCode(), StringProcessor.padRight(m.getName(), 28), m.getCategory(),
                    m.getTotalStock(), m.getExpiryDate(), "QUARANTINE & DISPOSE"));
        }

        sb.append("\n").append(String.format("WARNING - NEAR EXPIRY MEDICINES (Within 30 Days) (%d found):%n", nearExpiry.size()));
        sb.append(String.format("%-10s | %-28s | %-12s | %8s | %12s | %s%n",
                "Code", "Name", "Category", "Stock", "Expiry Date", "Days Remaining"));
        sb.append(StringProcessor.repeatChar('-', 88)).append("\n");
        for (Medicine m : nearExpiry) {
            sb.append(String.format("%-10s | %-28s | %-12s | %8d | %12s | %d days left%n",
                    m.getCode(), StringProcessor.padRight(m.getName(), 28), m.getCategory(),
                    m.getTotalStock(), m.getExpiryDate(), m.daysToExpiry()));
        }

        appendFooter(sb);
        return sb.toString();
    }

    /**
     * Demonstrates Jagged Array monthly consumption report (Unit 2).
     */
    public String generateJaggedConsumptionReport() {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb, "MONTHLY CONSUMPTION REPORT (JAGGED ARRAYS ANALYSIS)");

        int[][] jagged = inventoryService.getMonthlyConsumptionJaggedArray();
        int[] totals = inventoryService.getMedicineConsumptionTotals(jagged);
        double[] averages = inventoryService.getMedicineConsumptionAverages(jagged);
        List<Medicine> medicines = inventoryService.getAllMedicines();

        sb.append("This report demonstrates int[][] jagged arrays where each medicine tracks a variable\n");
        sb.append("number of operational months depending on its introduction timeline into the hospital.\n\n");

        sb.append(String.format("%-10s | %-24s | %-6s | %-32s | %7s | %8s%n",
                "Code", "Medicine Name", "Months", "Monthly Data Points (Units)", "Total", "Average"));
        sb.append(StringProcessor.repeatChar('-', 100)).append("\n");

        for (int i = 0; i < jagged.length; i++) {
            String medCode = (i < medicines.size()) ? medicines.get(i).getCode() : "MED-" + (i + 1);
            String medName = (i < medicines.size()) ? medicines.get(i).getName() : "Medicine " + (i + 1);

            StringBuilder dataPoints = new StringBuilder();
            for (int m = 0; m < jagged[i].length; m++) {
                dataPoints.append(jagged[i][m]);
                if (m < jagged[i].length - 1) dataPoints.append(", ");
            }

            sb.append(String.format("%-10s | %-24s | %6d | %-32s | %7d | %8.1f%n",
                    medCode,
                    StringProcessor.padRight(medName, 24),
                    jagged[i].length,
                    StringProcessor.padRight(dataPoints.toString(), 32),
                    totals[i],
                    averages[i]));
        }

        sb.append(StringProcessor.repeatChar('=', 100)).append("\n");
        appendFooter(sb);
        return sb.toString();
    }

    /**
     * Generates Supplier Breakdown Report.
     */
    public String generateSupplierReport() {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb, "SUPPLIER-WISE INVENTORY & PROCUREMENT REPORT");

        List<Supplier> suppliers = inventoryService.getSupplierRepository().findAll();
        List<Medicine> medicines = inventoryService.getAllMedicines();

        for (Supplier s : suppliers) {
            sb.append(String.format("Supplier: %s (%s) | Contact: %s | Tel: %s%n",
                    s.getName(), s.getId(), s.getContactPerson(), s.getPhone()));
            sb.append(StringProcessor.repeatChar('-', 85)).append("\n");

            int suppMedicinesCount = 0;
            int suppStockCount = 0;
            double suppTotalVal = 0.0;

            for (Medicine m : medicines) {
                if (s.getId().equalsIgnoreCase(m.getSupplierId())) {
                    suppMedicinesCount++;
                    suppStockCount += m.getTotalStock();
                    double val = m.getTotalStock() * m.getUnitPrice();
                    suppTotalVal += val;
                    sb.append(String.format("  • %-10s %-30s Stock: %4d | Price: ₹%7.2f | Value: ₹%9.2f%n",
                            m.getCode(), StringProcessor.padRight(m.getName(), 30),
                            m.getTotalStock(), m.getUnitPrice(), val));
                }
            }

            if (suppMedicinesCount == 0) {
                sb.append("  No active medicines assigned to this supplier.\n");
            } else {
                sb.append(String.format("  Summary: %d SKUs | Total Stock: %d units | Total Valuation: ₹%,.2f%n",
                        suppMedicinesCount, suppStockCount, suppTotalVal));
            }
            sb.append("\n");
        }

        appendFooter(sb);
        return sb.toString();
    }

    /**
     * Generates Full Inventory Valuation Report.
     */
    public String generateFullInventoryValuationReport() {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb, "COMPREHENSIVE INVENTORY VALUATION & AUDIT REPORT");

        List<Medicine> medicines = inventoryService.getAllMedicines();
        Map<String, Integer> categoryUnits = new TreeMap<>();
        Map<String, Double> categoryValues = new TreeMap<>();

        sb.append(String.format("%-10s | %-26s | %-12s | %6s | %8s | %10s | %s%n",
                "Code", "Name", "Category", "Stock", "Price", "Value", "Dosage & Form"));
        sb.append(StringProcessor.repeatChar('-', 100)).append("\n");

        double grandTotalValue = 0.0;
        int grandTotalUnits = 0;

        for (Medicine m : medicines) {
            int stock = m.getTotalStock();
            double val = stock * m.getUnitPrice();
            grandTotalUnits += stock;
            grandTotalValue += val;

            categoryUnits.put(m.getCategory(), categoryUnits.getOrDefault(m.getCategory(), 0) + stock);
            categoryValues.put(m.getCategory(), categoryValues.getOrDefault(m.getCategory(), 0.0) + val);

            sb.append(String.format("%-10s | %-26s | %-12s | %6d | ₹%7.2f | ₹%9.2f | %s%n",
                    m.getCode(),
                    StringProcessor.padRight(m.getName(), 26),
                    m.getCategory(),
                    stock,
                    m.getUnitPrice(),
                    val,
                    m.getDosageFormDetails()));
        }

        sb.append(StringProcessor.repeatChar('=', 100)).append("\n");
        sb.append(String.format("TOTAL MEDICINES: %d SKUs | TOTAL UNITS: %,d | GRAND VALUATION: ₹%,.2f%n%n",
                medicines.size(), grandTotalUnits, grandTotalValue));

        sb.append("CATEGORY-WISE SUMMARY:\n");
        for (String cat : categoryUnits.keySet()) {
            sb.append(String.format("  • %-14s : %5d units  (₹%,.2f)%n",
                    cat, categoryUnits.get(cat), categoryValues.get(cat)));
        }

        appendFooter(sb);
        return sb.toString();
    }

    /**
     * Exports any report text to a disk file using Character Streams (FileWriter, BufferedWriter, PrintWriter).
     */
    public void exportReportToFile(String reportContent, String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.print(reportContent);
            pw.flush();
        }
    }

    private void appendHeader(StringBuilder sb, String title) {
        String border = StringProcessor.repeatChar('=', 100);
        sb.append(border).append("\n");
        sb.append(" INTELLIGENT MEDICAL INVENTORY TRACKING SYSTEM (MITS)\n");
        sb.append(" ").append(title).append("\n");
        sb.append(" Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append(border).append("\n\n");
    }

    private void appendFooter(StringBuilder sb) {
        sb.append("\n").append(StringProcessor.repeatChar('-', 100)).append("\n");
        sb.append(" [END OF REPORT - CONFIDENTIAL HOSPITAL PHARMACY SYSTEM]\n");
    }
}
