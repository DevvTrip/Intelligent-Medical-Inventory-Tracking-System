package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import businesslogic.Inventory;
import businesslogic.InvalidInputException;
import model.Medicine;
import utils.BackupUtil;

public class DashboardActions implements ActionListener {
    private Dashboard dashboard;
    private Inventory inventory;

    public DashboardActions(Dashboard dashboard, Inventory inventory) {
        this.dashboard = dashboard;
        this.inventory = inventory;
    }

    public void actionPerformed(ActionEvent event) {
        try {
            String command = event.getActionCommand();
            if (command.equals("Restore")) {
                restoreBackup();
                return;
            }
            if (command.equals("Backup")) {
                BackupUtil.backup();
                JOptionPane.showMessageDialog(dashboard, "Backup saved");
                return;
            }
            inventory.refresh();
            if (command.equals("Add")) {
                new MedicineForm(inventory, null, dashboard).setVisible(true);
            } else if (command.equals("Update")) {
                editSelected();
            } else if (command.equals("Delete")) {
                deleteSelected();
            } else if (command.equals("Search")) {
                new SearchScreen(inventory).setVisible(true);
            } else if (command.equals("Reports")) {
                new ReportScreen(inventory).setVisible(true);
            } else if (command.equals("Sell")) {
                new SellDialog(inventory, dashboard).setVisible(true);
            } else if (command.equals("Backup")) {
                BackupUtil.backup();
                JOptionPane.showMessageDialog(dashboard, "Backup saved");
            } else if (command.equals("Restore")) {
                restoreBackup();
            } else if (command.equals("Refresh")) {
                dashboard.refreshTable();
            }
        } catch (IOException | InvalidInputException e) {
            JOptionPane.showMessageDialog(dashboard, e.getMessage());
        }
    }

    private String selectedId() {
        int row = dashboard.table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(dashboard, "Select a medicine first");
            return null;
        }
        return dashboard.model.getValueAt(row, 0).toString();
    }

    private void editSelected() {
        String id = selectedId();
        if (id == null) {
            return;
        }
        Medicine medicine = inventory.getById(id);
        if (medicine == null) {
            JOptionPane.showMessageDialog(dashboard, "Medicine no longer exists. Refresh the table");
        } else {
            new MedicineForm(inventory, medicine, dashboard).setVisible(true);
        }
    }

    private void deleteSelected() throws IOException, InvalidInputException {
        String id = selectedId();
        if (id == null) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(dashboard, "Delete medicine " + id + "?");
        if (answer == JOptionPane.YES_OPTION) {
            inventory.deleteMedicine(id);
            dashboard.refreshTable();
        }
    }

    private void restoreBackup() throws IOException {
        int answer = JOptionPane.showConfirmDialog(dashboard, "Replace current data with the backup?");
        if (answer == JOptionPane.YES_OPTION) {
            BackupUtil.restore();
            inventory.refresh();
            dashboard.refreshTable();
            JOptionPane.showMessageDialog(dashboard, "Data restored from backup");
        }
    }
}
