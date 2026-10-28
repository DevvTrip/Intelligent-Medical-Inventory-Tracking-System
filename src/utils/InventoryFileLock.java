package utils;

import java.io.File;
import java.io.IOException;

public class InventoryFileLock implements AutoCloseable {
    private File lockFile = new File("data/inventory.lock");
    private boolean locked = false;

    // jab tak lock file hai, koi aur program CSV change nahi karega
    public static InventoryFileLock acquire() throws IOException {
        InventoryFileLock lock = new InventoryFileLock();
        new File("data").mkdirs();
        for (int i = 0; i < 50; i++) {
            if (lock.lockFile.createNewFile()) {
                lock.locked = true;
                return lock;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        throw new IOException("Inventory file is busy, try again after some time");
    }

    public void close() throws IOException {
        if (locked) {
            lockFile.delete();
            locked = false;
        }
    }
}
