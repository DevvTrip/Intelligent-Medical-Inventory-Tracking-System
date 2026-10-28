package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.SwingUtilities;
import businesslogic.ExpiredMedicineException;
import businesslogic.InvalidInputException;
import businesslogic.OutOfStockException;
import network.CounterClient;

public class SellActions implements ActionListener {
    private SellDialog dialog;

    public SellActions(SellDialog dialog) {
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("Close")) {
            dialog.dispose();
            return;
        }
        try {
            String id = dialog.idField.getText().trim();
            int quantity = Integer.parseInt(dialog.qtyField.getText().trim());
            if (command.equals("Sell")) {
                dialog.inventory.getStockService().sellMedicine(id, quantity);
                dialog.msgLabel.setText("Stock updated");
                dialog.dashboard.refreshTable();
            } else {
                sendToServer(id, quantity);
            }
        } catch (NumberFormatException e) {
            dialog.msgLabel.setText("Quantity must be a number");
        } catch (OutOfStockException | ExpiredMedicineException | InvalidInputException e) {
            dialog.msgLabel.setText(e.getMessage());
        } catch (IOException e) {
            dialog.msgLabel.setText("Could not save stock: " + e.getMessage());
        }
    }

    // network call alag thread pe chalta hai, warna window freeze ho jati hai
    private void sendToServer(final String id, final int quantity) {
        dialog.sellBtn.setEnabled(false);
        dialog.serverBtn.setEnabled(false);
        dialog.msgLabel.setText("Sending request...");
        Thread counter = new Thread(new Runnable() {
            public void run() {
                String reply = CounterClient.sendSell(id, quantity);
                String message = reply == null ? "No reply from server" : reply;
                if (reply != null && reply.startsWith("OK ")) {
                    dialog.inventory.getStockService().recordSale(id, quantity);
                    try {
                        dialog.inventory.refresh();
                        message = "Sold on server, remaining quantity " + reply.substring(3);
                    } catch (IOException e) {
                        message = "Sale completed, but refresh failed: " + e.getMessage();
                    }
                }
                final String result = message;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.msgLabel.setText(result);
                        dialog.sellBtn.setEnabled(true);
                        dialog.serverBtn.setEnabled(true);
                        dialog.dashboard.refreshTable();
                    }
                });
            }
        });
        counter.start();
    }
}
