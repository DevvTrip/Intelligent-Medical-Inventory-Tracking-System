package com.mits.app;

import com.mits.service.InventoryService;
import com.mits.gui.LoginFrame;
import com.mits.concurrent.*;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Swing GUI application.
 * Starts background daemon threads and shows the login window.
 */
public class GuiApp {
    public static void main(String[] args) {
        // Create the core inventory service (auto-seeds data if no file exists)
        InventoryService inventoryService = new InventoryService();

        // Create a shared alert notification queue
        AlertNotificationQueue alertQueue = new AlertNotificationQueue();

        // Start daemon background threads
        // ExpiryMonitorThread extends Thread, uses 30000ms (30 sec) interval
        ExpiryMonitorThread expiryMonitor = new ExpiryMonitorThread(inventoryService, alertQueue, 30000);
        expiryMonitor.start();

        // LowStockMonitor implements Runnable, wrap in daemon Thread
        LowStockMonitor lowStockTask = new LowStockMonitor(inventoryService, alertQueue, 30000);
        Thread lowStockThread = new Thread(lowStockTask, "LowStockMonitor-Thread");
        lowStockThread.setDaemon(true);
        lowStockThread.start();

        // AutoBackupThread extends Thread, 5 minute interval (300000ms)
        AutoBackupThread backupThread = new AutoBackupThread(inventoryService, 300000);
        backupThread.start();

        // Launch GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(inventoryService);
            loginFrame.setVisible(true);
        });
    }
}
