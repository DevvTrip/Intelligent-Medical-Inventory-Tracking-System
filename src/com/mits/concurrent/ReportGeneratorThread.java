package com.mits.concurrent;

import com.mits.report.ReportService;
import com.mits.service.InventoryService;
import com.mits.util.StringProcessor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Worker thread for generating comprehensive reports asynchronously in the background.
 * Prevents UI freezes during heavy report synthesis.
 * Demonstrates:
 * - Unit 4: Background processing
 * - Unit 4: Asynchronous callbacks to UI thread
 */
public class ReportGeneratorThread extends Thread {
    public enum ReportType {
        LOW_STOCK,
        EXPIRY_RISK,
        JAGGED_CONSUMPTION,
        SUPPLIER_SUMMARY,
        FULL_INVENTORY
    }

    private final InventoryService inventoryService;
    private final ReportType reportType;
    private final Consumer<String> onCompleted;
    private final BiConsumer<Integer, String> onProgress;

    public ReportGeneratorThread(InventoryService inventoryService,
                                 ReportType reportType,
                                 Consumer<String> onCompleted,
                                 BiConsumer<Integer, String> onProgress) {
        super("ReportGenerator-Worker");
        this.inventoryService = inventoryService;
        this.reportType = reportType;
        this.onCompleted = onCompleted;
        this.onProgress = onProgress;
    }

    @Override
    public void run() {
        StringProcessor.logActivity("ReportGeneratorThread started for report: " + reportType);
        try {
            updateProgress(20, "Extracting inventory dataset...");
            Thread.sleep(300); // Simulate analytical computation

            updateProgress(50, "Synthesizing statistics and applying algorithms...");
            ReportService reportService = new ReportService(inventoryService);
            String resultText;

            switch (reportType) {
                case LOW_STOCK:
                    resultText = reportService.generateLowStockReport();
                    break;
                case EXPIRY_RISK:
                    resultText = reportService.generateExpiryRiskReport();
                    break;
                case JAGGED_CONSUMPTION:
                    resultText = reportService.generateJaggedConsumptionReport();
                    break;
                case SUPPLIER_SUMMARY:
                    resultText = reportService.generateSupplierReport();
                    break;
                case FULL_INVENTORY:
                default:
                    resultText = reportService.generateFullInventoryValuationReport();
                    break;
            }

            updateProgress(85, "Formatting ASCII document tables with StringBuilder...");
            Thread.sleep(200);

            updateProgress(100, "Report generated successfully!");
            if (onCompleted != null) {
                onCompleted.accept(resultText);
            }
            StringProcessor.logActivity("ReportGeneratorThread completed report: " + reportType);
        } catch (InterruptedException e) {
            StringProcessor.logActivity("Report generation interrupted.");
        } catch (Exception e) {
            if (onCompleted != null) {
                onCompleted.accept("Error generating report: " + e.getMessage());
            }
        }
    }

    private void updateProgress(int percent, String message) {
        if (onProgress != null) {
            onProgress.accept(percent, message);
        }
    }
}
