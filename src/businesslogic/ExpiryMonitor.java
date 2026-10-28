package businesslogic;

import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import model.Medicine;

public class ExpiryMonitor implements Runnable {
    private Inventory inventory;
    private JLabel statusLabel;
    private Runnable refreshView;

    public ExpiryMonitor(Inventory inventory, JLabel statusLabel, Runnable refreshView) {
        this.inventory = inventory;
        this.statusLabel = statusLabel;
        this.refreshView = refreshView;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String status;
            boolean refreshed = false;
            try {
                inventory.refresh();
                int low = 0;
                int expiring = 0;
                for (Medicine medicine : inventory.getMedicines()) {
                    if (medicine.isLowStock() && !medicine.isExpired()) {
                        low++;
                    }
                    if (medicine.isExpiringSoon()) {
                        expiring++;
                    }
                }
                status = "Monitor: " + low + " low stock, " + expiring + " expiring in 30 days";
                refreshed = true;
            } catch (IOException e) {
                status = "Could not refresh inventory: " + e.getMessage();
            }
            final String message = status;
            final boolean updateTable = refreshed;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    statusLabel.setText(message);
                    if (updateTable) {
                        refreshView.run();
                    }
                }
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
