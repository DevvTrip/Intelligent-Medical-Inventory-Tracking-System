package gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusRenderer extends DefaultTableCellRenderer {

    private int statusColumn;

    public StatusRenderer(int statusColumn) {
        this.statusColumn = statusColumn;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean selected, boolean focused, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                selected, focused, row, column);
        String status = table.getValueAt(row, statusColumn).toString();
        if (!selected) {
            if (status.equals("EXPIRED")) {
                c.setBackground(new Color(255, 210, 210));
            } else if (status.equals("OK")) {
                c.setBackground(new Color(215, 245, 215));
            } else {
                c.setBackground(new Color(255, 235, 190));
            }
        }
        return c;
    }
}
