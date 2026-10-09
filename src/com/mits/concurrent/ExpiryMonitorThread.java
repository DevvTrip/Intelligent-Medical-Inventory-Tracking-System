package com.mits.concurrent;

import com.mits.model.Alert;
import com.mits.model.Medicine;
import com.mits.service.InventoryService;
import com.mits.util.StringProcessor;

import java.time.LocalDateTime;

/**
 * Background daemon thread that periodically monitors medicine expiration dates.
 * Demonstrates:
 * - Unit 4: Extending the Thread class
 * - Unit 4: Daemon thread (setDaemon(true))
 * - Unit 4: Thread priorities (setPriority)
 */
public class ExpiryMonitorThread extends Thread {
    private final InventoryService inventoryService;
    private final AlertNotificationQueue alertQueue;
    private final long intervalMillis;
    private volatile boolean running = true;

    public ExpiryMonitorThread(InventoryService inventoryService, AlertNotificationQueue alertQueue, long intervalMillis) {
        super("ExpiryMonitor-Thread");
        this.inventoryService = inventoryService;
        this.alertQueue = alertQueue;
        this.intervalMillis = Math.max(2000, intervalMillis);
        setDaemon(true); // Daemon thread terminates when main user threads exit
        setPriority(Thread.MIN_PRIORITY + 2); // Lower priority to avoid starving UI/business threads
    }

    @Override
    public void run() {
        StringProcessor.logActivity("ExpiryMonitorThread started with priority: " + getPriority());
        while (running && !isInterrupted()) {
            try {
                scanForExpiryRisks();
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                StringProcessor.logActivity("ExpiryMonitorThread interrupted.");
                break;
            } catch (Exception e) {
                System.err.println("Error in ExpiryMonitorThread: " + e.getMessage());
            }
        }
        StringProcessor.logActivity("ExpiryMonitorThread stopped.");
    }

    private void scanForExpiryRisks() {
        for (Medicine m : inventoryService.getAllMedicines()) {
            if (m.isExpired()) {
                Alert alert = new Alert(
                        "ALT-EXP-" + m.getCode() + "-" + System.currentTimeMillis() % 10000,
                        m.getCode(),
                        m.getName(),
                        Alert.Type.EXPIRED,
                        Alert.Severity.CRITICAL,
                        "Expired on " + m.getExpiryDate() + " - Immediate quarantine required!",
                        LocalDateTime.now()
                );
                inventoryService.dispatchAlert(alert);
                if (alertQueue != null) alertQueue.offer(alert);
            } else if (m.daysToExpiry() <= 30) {
                Alert alert = new Alert(
                        "ALT-NEXP-" + m.getCode() + "-" + System.currentTimeMillis() % 10000,
                        m.getCode(),
                        m.getName(),
                        Alert.Type.NEAR_EXPIRY,
                        Alert.Severity.WARNING,
                        "Expiring in " + m.daysToExpiry() + " days (" + m.getExpiryDate() + ")",
                        LocalDateTime.now()
                );
                inventoryService.dispatchAlert(alert);
                if (alertQueue != null) alertQueue.offer(alert);
            }
        }
    }

    public void stopMonitoring() {
        this.running = false;
        interrupt();
    }
}
