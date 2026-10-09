package com.mits.concurrent;

import com.mits.model.Alert;
import com.mits.model.Medicine;
import com.mits.service.InventoryService;
import com.mits.util.StringProcessor;

import java.time.LocalDateTime;

/**
 * Task for checking inventory reorder thresholds and generating low-stock alerts.
 * Demonstrates:
 * - Unit 4: Task creation implementing the Runnable interface
 */
public class LowStockMonitor implements Runnable {
    private final InventoryService inventoryService;
    private final AlertNotificationQueue alertQueue;
    private final long intervalMillis;
    private volatile boolean running = true;

    public LowStockMonitor(InventoryService inventoryService, AlertNotificationQueue alertQueue, long intervalMillis) {
        this.inventoryService = inventoryService;
        this.alertQueue = alertQueue;
        this.intervalMillis = Math.max(2000, intervalMillis);
    }

    @Override
    public void run() {
        StringProcessor.logActivity("LowStockMonitor Runnable started.");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                scanLowStock();
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                StringProcessor.logActivity("LowStockMonitor interrupted.");
                break;
            } catch (Exception e) {
                System.err.println("Error in LowStockMonitor: " + e.getMessage());
            }
        }
        StringProcessor.logActivity("LowStockMonitor stopped.");
    }

    private void scanLowStock() {
        for (Medicine m : inventoryService.getAllMedicines()) {
            if (m.needsReorder()) {
                Alert alert = new Alert(
                        "ALT-STK-" + m.getCode() + "-" + System.currentTimeMillis() % 10000,
                        m.getCode(),
                        m.getName(),
                        Alert.Type.LOW_STOCK,
                        Alert.Severity.WARNING,
                        String.format("Current Stock: %d <= Reorder Level: %d (Order %d units)",
                                m.getTotalStock(), m.getReorderLevel(), m.reorderQuantity()),
                        LocalDateTime.now()
                );
                inventoryService.dispatchAlert(alert);
                if (alertQueue != null) alertQueue.offer(alert);
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
