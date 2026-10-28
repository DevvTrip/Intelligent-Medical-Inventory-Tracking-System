package businesslogic;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import model.Medicine;
import model.Supplier;
import utils.FileUtil;
import utils.InventoryFileLock;

public class Inventory {
    private DataStore<Medicine> store = new DataStore<Medicine>();
    private HashMap<String, Medicine> medMap = new HashMap<String, Medicine>();
    private HashSet<Supplier> supplierSet = new HashSet<Supplier>();
    private TreeSet<Medicine> expirySet = new TreeSet<Medicine>();
    private LinkedList<String> activityLog = new LinkedList<String>();
    private StockService stockService = new StockService(this);

    public void initialize(ArrayList<Medicine> sample) throws IOException {
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            if (!new File(FileUtil.DATA_FILE).exists()) {
                FileUtil.saveMedicines(sample);
            }
            install(FileUtil.loadMedicines());
        }
    }

    public void refresh() throws IOException {
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            install(FileUtil.loadMedicines());
        }
    }

    public void addMedicine(Medicine medicine)
            throws IOException, DuplicateMedicineException, InvalidInputException {
        MedicineValidation.check(medicine);
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            ArrayList<Medicine> list = FileUtil.loadMedicines();
            for (Medicine item : list) {
                if (item.getId().equals(medicine.getId())) {
                    throw new DuplicateMedicineException("ID " + medicine.getId() + " already exists");
                }
            }
            list.add(medicine);
            saveAndUpdate(list, "added " + medicine.getName());
        }
    }

    public void updateMedicine(Medicine medicine)
            throws IOException, InvalidInputException {
        MedicineValidation.check(medicine);
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            ArrayList<Medicine> list = FileUtil.loadMedicines();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(medicine.getId())) {
                    list.set(i, medicine);
                    saveAndUpdate(list, "updated " + medicine.getName());
                    return;
                }
            }
            throw new InvalidInputException("Medicine no longer exists");
        }
    }

    public void deleteMedicine(String id) throws IOException, InvalidInputException {
        try (InventoryFileLock lock = InventoryFileLock.acquire()) {
            ArrayList<Medicine> list = FileUtil.loadMedicines();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(id)) {
                    Medicine removed = list.remove(i);
                    saveAndUpdate(list, "deleted " + removed.getName());
                    return;
                }
            }
            throw new InvalidInputException("Medicine no longer exists");
        }
    }

    public synchronized ArrayList<Medicine> searchMedicine(String keyword) {
        ArrayList<Medicine> found = new ArrayList<Medicine>();
        String word = keyword.toLowerCase();
        for (Medicine medicine : store.getAll()) {
            if (medicine.getName().toLowerCase().contains(word) || medicine.getId().contains(word)) {
                found.add(medicine);
            }
        }
        return found;
    }
    public synchronized Medicine searchMedicine(int idNumber) {
        return medMap.get(String.valueOf(idNumber));
    }
    public synchronized Medicine getById(String id) { return medMap.get(id); }

    synchronized void saveAndUpdate(ArrayList<Medicine> list, String message) throws IOException {
        FileUtil.saveMedicines(list);
        install(list);
        activityLog.addFirst(LocalTime.now().withNano(0) + " " + message);
        if (activityLog.size() > 30) {
            activityLog.removeLast();
        }
    }
    private synchronized void install(ArrayList<Medicine> list) {
        store.clear();
        medMap.clear();
        expirySet.clear();
        supplierSet.clear();
        for (Medicine medicine : list) {
            store.add(medicine);
            medMap.put(medicine.getId(), medicine);
            expirySet.add(medicine);
            supplierSet.add(medicine.getSupplier());
        }
    }
    public synchronized ArrayList<Medicine> getMedicines() { return new ArrayList<Medicine>(store.getAll()); }
    public synchronized ArrayList<Medicine> getByExpiryOrder() { return new ArrayList<Medicine>(expirySet); }
    public synchronized ArrayList<Supplier> getSupplierList() { return new ArrayList<Supplier>(supplierSet); }
    public synchronized LinkedList<String> getActivityLog() { return new LinkedList<String>(activityLog); }
    public StockService getStockService() { return stockService; }
}
