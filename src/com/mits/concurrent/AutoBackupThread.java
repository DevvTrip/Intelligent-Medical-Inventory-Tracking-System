package com.mits.concurrent;

import com.mits.service.InventoryService;
import com.mits.util.StringProcessor;

/**
 * Background daemon thread performing periodic automated snapshots/backups.
 * Demonstrates:
 * - Unit 4: Extending Thread
 * - Unit 4: Daemon thread
 * - Unit 4: Background automated tasks
 */
public class AutoBackupThread extends Thread {
    private final InventoryService inventoryService;
    private final long intervalMillis;
    private volatile boolean running = true;

    public AutoBackupThread(InventoryService inventoryService, long intervalMillis) {
        super("AutoBackup-Thread");
        this.inventoryService = inventoryService;
        this.intervalMillis = Math.max(5000, intervalMillis);
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY); // Lowest priority background maintenance
    }

    @Override
    public void run() {
        StringProcessor.logActivity("AutoBackupThread initialized. Interval: " + (intervalMillis / 1000) + "s");
        while (running && !isInterrupted()) {
            try {
                Thread.sleep(intervalMillis);
                String backupPath = inventoryService.triggerBackup();
                StringProcessor.logActivity("Auto-backup executed: " + backupPath);
            } catch (InterruptedException e) {
                StringProcessor.logActivity("AutoBackupThread interrupted.");
                break;
            } catch (Exception e) {
                System.err.println("AutoBackupThread error: " + e.getMessage());
            }
        }
    }

    public void stopBackup() {
        this.running = false;
        interrupt();
    }
}
