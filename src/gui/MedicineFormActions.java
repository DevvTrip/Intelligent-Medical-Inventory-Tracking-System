package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import businesslogic.DuplicateMedicineException;
import businesslogic.InvalidInputException;
import model.Injection;
import model.Medicine;
import model.Supplier;
import model.Syrup;
import model.Tablet;

public class MedicineFormActions implements ActionListener {
    private MedicineForm form;

    public MedicineFormActions(MedicineForm form) {
        this.form = form;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Close")) {
            form.dispose();
            return;
        }
        try {
            String id = form.idField.getText().trim();
            String name = form.nameField.getText().trim();
            double price = Double.parseDouble(form.priceField.getText().trim());
            int quantity = Integer.parseInt(form.qtyField.getText().trim());
            LocalDate expiry = LocalDate.parse(form.expiryField.getText().trim());
            int limit = Integer.parseInt(form.limitField.getText().trim());
            int extra = Integer.parseInt(form.extraField.getText().trim());
            Supplier supplier = new Supplier(form.supplierField.getText().trim(),
                    form.phoneField.getText().trim());
            String type = form.typeBox.getSelectedItem().toString();
            Medicine medicine;
            if (type.equals("Tablet")) {
                medicine = new Tablet(id, name, price, quantity, expiry, limit, supplier, extra);
            } else if (type.equals("Syrup")) {
                medicine = new Syrup(id, name, price, quantity, expiry, limit, supplier, extra);
            } else {
                medicine = new Injection(id, name, price, quantity, expiry, limit, supplier, extra);
            }
            String message;
            if (!form.updateMode) {
                form.inventory.addMedicine(medicine);
                message = "Medicine added";
            } else {
                form.inventory.updateMedicine(medicine);
                message = "Medicine updated";
            }
            form.dashboard.refreshTable();
            JOptionPane.showMessageDialog(form, message);
            form.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(form, "Price, quantity, reorder level and extra must be numbers");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(form, "Expiry must be yyyy-MM-dd");
        } catch (InvalidInputException | DuplicateMedicineException e) {
            JOptionPane.showMessageDialog(form, e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(form, "Could not save medicine: " + e.getMessage());
        }
    }
}
