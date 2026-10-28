package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BackupUtil {
    public static final String BACKUP_FILE = "backup/medicines_backup.csv";

    public static void backup() throws IOException {
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            FileUtil.loadMedicines();
            copyFile(FileUtil.DATA_FILE, BACKUP_FILE);
        }
    }

    public static void restore() throws IOException {
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            FileUtil.loadMedicines(BACKUP_FILE);
            copyFile(BACKUP_FILE, FileUtil.DATA_FILE);
        }
    }

    private static void copyFile(String source, String target) throws IOException {
        new File(target).getParentFile().mkdirs();
        try (FileInputStream input = new FileInputStream(source);
                FileOutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        }
    }
}
