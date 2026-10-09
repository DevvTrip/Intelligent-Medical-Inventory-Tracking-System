package com.mits.util;

import java.io.File;

/**
 * Global application constants.
 */
public final class Constants {
    private Constants() {}

    // File directories
    public static final String DATA_DIR = "data";
    public static final String BACKUP_DIR = "data" + File.separator + "backup";

    // File paths
    public static final String INVENTORY_SER_FILE = DATA_DIR + File.separator + "inventory.ser";
    public static final String MEDICINES_CSV_FILE = DATA_DIR + File.separator + "medicines.csv";
    public static final String SUPPLIERS_CSV_FILE = DATA_DIR + File.separator + "suppliers.csv";
    public static final String USERS_CSV_FILE = DATA_DIR + File.separator + "users.csv";
    public static final String AUDIT_LOG_FILE = DATA_DIR + File.separator + "audit.log";

    // Network defaults
    public static final int DEFAULT_SERVER_PORT = 5000;
    public static final String DEFAULT_SERVER_HOST = "localhost";

    // System application name
    public static final String APP_TITLE = "Intelligent Medical Inventory Tracking System (MITS)";
    public static final String APP_VERSION = "2.0 (Capstone PBL)";
}
