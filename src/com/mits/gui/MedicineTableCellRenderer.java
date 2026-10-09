package com.mits.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom TableCellRenderer that visually flags low-stock rows in soft red
 * and near-expiry rows in soft orange/amber.
 * Demonstrates:
 * - Unit 5: JTable customization with TableCellRenderer
 * - Unit 5: AWT Color manipulation based on business rules
 */
public class MedicineTableCellRenderer extends DefaultTableCellRenderer {
    private static final Color COLOR_EXPIRED = new Color(255, 205, 210);    // Light red
    private static final Color COLOR_LOW_STOCK = new Color(255, 224, 230);  // Soft pink
    private static final Color COLOR_NEAR_EXPIRY = new Color(255, 243, 205);// Soft amber
    private static final Color COLOR_NORMAL_EVEN = Color.WHITE;
    private static final Color COLOR_NORMAL_ODD = new Color(248, 249, 250);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            int modelRow = table.convertRowIndexToModel(row);
            MedicineTableModel model = (MedicineTableModel) table.getModel();

            Object stockObj = model.getValueAt(modelRow, 3);
            Object reorderObj = model.getValueAt(modelRow, 5);
            Object daysLeftObj = model.getValueAt(modelRow, 7);

            int stock = (stockObj instanceof Integer) ? (Integer) stockObj : 0;
            int reorder = (reorderObj instanceof Integer) ? (Integer) reorderObj : 0;
            long daysLeft = (daysLeftObj instanceof Long) ? (Long) daysLeftObj : Long.MAX_VALUE;

            if (daysLeft < 0) {
                // Expired
                c.setBackground(COLOR_EXPIRED);
                c.setForeground(new Color(178, 34, 34)); // Dark red text
            } else if (stock <= reorder) {
                // Low stock
                c.setBackground(COLOR_LOW_STOCK);
                c.setForeground(new Color(153, 0, 0));
            } else if (daysLeft <= 30) {
                // Near expiry (< 30 days)
                c.setBackground(COLOR_NEAR_EXPIRY);
                c.setForeground(new Color(133, 100, 4)); // Dark amber text
            } else {
                c.setBackground(row % 2 == 0 ? COLOR_NORMAL_EVEN : COLOR_NORMAL_ODD);
                c.setForeground(new Color(33, 37, 41));
            }
        }

        // Align numbers right, text left
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        return c;
    }
}
