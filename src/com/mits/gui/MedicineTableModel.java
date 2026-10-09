package com.mits.gui;

import com.mits.model.Medicine;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel for Medicine entities supporting JTable sorting and filtering.
 * Demonstrates:
 * - Unit 5: Swing JTable integration and AbstractTableModel customization
 */
public class MedicineTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {
            "Code", "Name", "Category", "Stock", "Price (₹)", "Reorder", "Earliest Expiry", "Days Left", "Storage Condition"
    };

    private List<Medicine> medicines;

    public MedicineTableModel(List<Medicine> medicines) {
        this.medicines = medicines != null ? new ArrayList<>(medicines) : new ArrayList<>();
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines != null ? new ArrayList<>(medicines) : new ArrayList<>();
        fireTableDataChanged();
    }

    public Medicine getMedicineAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < medicines.size()) {
            return medicines.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return medicines.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 3: // Stock
            case 5: // Reorder
                return Integer.class;
            case 4: // Price
                return Double.class;
            case 7: // Days Left
                return Long.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= medicines.size()) return null;
        Medicine m = medicines.get(rowIndex);
        switch (columnIndex) {
            case 0: return m.getCode();
            case 1: return m.getName();
            case 2: return m.getCategory();
            case 3: return m.getTotalStock();
            case 4: return m.getUnitPrice();
            case 5: return m.getReorderLevel();
            case 6: return m.getExpiryDate().toString();
            case 7: return m.daysToExpiry();
            case 8: return m.getStorageCondition();
            default: return null;
        }
    }
}
