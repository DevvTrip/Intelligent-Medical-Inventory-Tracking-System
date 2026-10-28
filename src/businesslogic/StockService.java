package businesslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import model.Medicine;
import utils.FileUtil;
import utils.InventoryFileLock;

public class StockService {
    private Inventory inventory;
    private HashMap<String, Integer> soldCount = new HashMap<String, Integer>();

    StockService(Inventory inventory) {
        this.inventory = inventory;
    }

    public synchronized int sellMedicine(String id, int qty)
            throws IOException, OutOfStockException, ExpiredMedicineException, InvalidInputException {
        if (qty <= 0) {
            throw new InvalidInputException("Quantity must be more than 0");
        }
        // lock ke andar fresh data load karo, warna server ka sale miss ho jata hai
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            ArrayList<Medicine> list = FileUtil.loadMedicines();
            for (Medicine medicine : list) {
                if (medicine.getId().equals(id)) {
                    if (medicine.isExpired()) {
                        throw new ExpiredMedicineException(medicine.getName() + " is expired");
                    }
                    if (medicine.getQty() < qty) {
                        throw new OutOfStockException("Not enough stock, only " + medicine.getQty() + " left");
                    }
                    medicine.setQty(medicine.getQty() - qty);
                    inventory.saveAndUpdate(list, "sold " + qty + " of " + medicine.getName());
                    recordSale(id, qty);
                    return medicine.getQty();
                }
            }
            throw new InvalidInputException("Medicine not found: " + id);
        }
    }

    public synchronized void recordSale(String id, int qty) {
        Integer old = soldCount.get(id);
        if (old == null) {
            soldCount.put(id, qty);
        } else {
            soldCount.put(id, old + qty);
        }
    }

    public synchronized HashMap<String, Integer> getSoldCount() {
        return new HashMap<String, Integer>(soldCount);
    }
}
