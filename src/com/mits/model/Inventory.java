package com.mits.model;

import java.io.Serializable;
import java.util.*;

/**
 * Aggregates all inventory items, transactions, and alert histories.
 * Demonstrates:
 * - Unit 1: Aggregation relationship (Inventory aggregates Medicines; medicines exist independently).
 * - Unit 5: Collections Framework:
 *     * HashMap (code -> Medicine lookup)
 *     * ArrayList (transactions list)
 *     * LinkedList (alert history queue)
 *     * HashSet (unique categories & suppliers)
 *     * TreeSet (medicines sorted by expiry date via Comparator)
 *     * PriorityQueue (medicines prioritized by lowest stock for reorder)
 *     * Iterator usage
 *     * Collections.sort usage
 * - Unit 4: Synchronization on shared collection access.
 */
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    // Collections framework usage
    private final Map<String, Medicine> medicineMap;             // HashMap: Key = Code, Value = Medicine
    private final List<StockTransaction> transactions;          // ArrayList: Historical audit trail
    private final LinkedList<Alert> alertHistory;               // LinkedList: Double-ended alert history
    private final Set<String> categories;                       // HashSet: Unique categories

    public Inventory() {
        this.medicineMap = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.alertHistory = new LinkedList<>();
        this.categories = new HashSet<>();
    }

    // ==========================================
    // SYNCHRONIZED MEDICINE MANAGEMENT
    // ==========================================

    public synchronized void addMedicine(Medicine medicine) {
        if (medicine == null || medicine.getCode() == null) return;
        medicineMap.put(medicine.getCode().toUpperCase().trim(), medicine);
        if (medicine.getCategory() != null) {
            categories.add(medicine.getCategory().toUpperCase().trim());
        }
    }

    public synchronized Medicine getMedicine(String code) {
        if (code == null) return null;
        return medicineMap.get(code.toUpperCase().trim());
    }

    public synchronized boolean containsMedicine(String code) {
        if (code == null) return false;
        return medicineMap.containsKey(code.toUpperCase().trim());
    }

    public synchronized Medicine removeMedicine(String code) {
        if (code == null) return null;
        Medicine removed = medicineMap.remove(code.toUpperCase().trim());
        recomputeCategories();
        return removed;
    }

    public synchronized List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>(medicineMap.values());
        Collections.sort(list); // Natural sort by code
        return list;
    }

    public synchronized int getMedicineCount() {
        return medicineMap.size();
    }

    private void recomputeCategories() {
        categories.clear();
        for (Medicine m : medicineMap.values()) {
            if (m.getCategory() != null) {
                categories.add(m.getCategory().toUpperCase().trim());
            }
        }
    }

    // ==========================================
    // ADVANCED COLLECTIONS: TreeSet, PriorityQueue, Iterator
    // ==========================================

    /**
     * Demonstrates TreeSet with custom Comparator: returns medicines sorted by earliest expiry date.
     * @return TreeSet of medicines sorted by expiry
     */
    public synchronized TreeSet<Medicine> getExpirySortedMedicines() {
        // Comparator sorting by expiry date ascending
        Comparator<Medicine> expiryComparator = (m1, m2) -> {
            int dateCmp = m1.getExpiryDate().compareTo(m2.getExpiryDate());
            if (dateCmp != 0) return dateCmp;
            return m1.getCode().compareTo(m2.getCode());
        };

        TreeSet<Medicine> treeSet = new TreeSet<>(expiryComparator);
        treeSet.addAll(medicineMap.values());
        return treeSet;
    }

    /**
     * Demonstrates PriorityQueue: returns medicines needing reorder,
     * with lowest stock ratio having highest priority.
     * @return PriorityQueue ordered by restock urgency
     */
    public synchronized PriorityQueue<Medicine> getReorderPriorityQueue() {
        // Priority comparator: lower stock / reorderLevel ratio comes first
        Comparator<Medicine> priorityComparator = Comparator.comparingDouble(
                m -> (double) m.getTotalStock() / Math.max(1, m.getReorderLevel())
        );

        PriorityQueue<Medicine> queue = new PriorityQueue<>(priorityComparator);
        for (Medicine m : medicineMap.values()) {
            if (m.needsReorder()) {
                queue.add(m);
            }
        }
        return queue;
    }

    /**
     * Demonstrates HashSet: returns unique set of categories in inventory.
     */
    public synchronized Set<String> getCategories() {
        return new HashSet<>(categories);
    }

    /**
     * Demonstrates HashSet & Iterator: returns unique set of supplier IDs.
     */
    public synchronized Set<String> getUniqueSupplierIds() {
        Set<String> supplierIds = new HashSet<>();
        // Explicit Iterator usage (Unit 5 syllabus)
        Iterator<Medicine> iterator = medicineMap.values().iterator();
        while (iterator.hasNext()) {
            Medicine m = iterator.next();
            if (m.getSupplierId() != null && !m.getSupplierId().isBlank()) {
                supplierIds.add(m.getSupplierId());
            }
        }
        return supplierIds;
    }

    // ==========================================
    // TRANSACTION MANAGEMENT (ArrayList)
    // ==========================================

    public synchronized void addTransaction(StockTransaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    public synchronized List<StockTransaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // ==========================================
    // ALERT HISTORY (LinkedList)
    // ==========================================

    public synchronized void addAlert(Alert alert) {
        if (alert != null) {
            // Keep recent alerts at front of LinkedList
            alertHistory.addFirst(alert);
            // Cap alert history at 100 to avoid unbounded growth
            if (alertHistory.size() > 100) {
                alertHistory.removeLast();
            }
        }
    }

    public synchronized List<Alert> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    public synchronized void clearAlerts() {
        alertHistory.clear();
    }

    // ==========================================
    // AGGREGATE CALCULATIONS
    // ==========================================

    public synchronized double getTotalValuation() {
        double total = 0.0;
        for (Medicine m : medicineMap.values()) {
            total += m.getTotalStock() * m.getUnitPrice();
        }
        return total;
    }

    public synchronized int getTotalStockUnits() {
        int total = 0;
        for (Medicine m : medicineMap.values()) {
            total += m.getTotalStock();
        }
        return total;
    }

    public synchronized int getLowStockCount() {
        int count = 0;
        for (Medicine m : medicineMap.values()) {
            if (m.needsReorder()) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getExpiringCount(int daysThreshold) {
        int count = 0;
        for (Medicine m : medicineMap.values()) {
            long days = m.daysToExpiry();
            if (days >= 0 && days <= daysThreshold) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getExpiredCount() {
        int count = 0;
        for (Medicine m : medicineMap.values()) {
            if (m.isExpired()) {
                count++;
            }
        }
        return count;
    }
}
