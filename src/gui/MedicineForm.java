package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import businesslogic.Inventory;
import model.Medicine;

public class MedicineForm extends JFrame {
    Inventory inventory;
    Dashboard dashboard;
    boolean updateMode;
    JTextField idField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField priceField = new JTextField();
    JTextField qtyField = new JTextField();
    JTextField expiryField = new JTextField(LocalDate.now().plusYears(2).toString());
    JTextField limitField = new JTextField();
    JTextField supplierField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField extraField = new JTextField();
    JComboBox<String> typeBox = new JComboBox<String>(new String[] {"Tablet", "Syrup", "Injection"});

    public MedicineForm(Inventory inventory, Medicine medicine, Dashboard dashboard) {
        this.inventory = inventory;
        this.dashboard = dashboard;
        updateMode = medicine != null;
        String title = medicine == null ? "Add Medicine" : "Update Medicine";
        setTitle(title);
        setSize(440, 520);
        setLocationRelativeTo(dashboard);
        setLayout(new BorderLayout());
        add(Ui.header(title), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(10, 2, 6, 6));
        form.setBackground(Ui.BG);
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        addField(form, "Medicine ID:", idField);
        addField(form, "Name:", nameField);
        form.add(new JLabel("Type:"));
        form.add(typeBox);
        addField(form, "Base price (Rs):", priceField);
        addField(form, "Quantity:", qtyField);
        addField(form, "Expiry (yyyy-MM-dd):", expiryField);
        addField(form, "Reorder level:", limitField);
        addField(form, "Supplier name:", supplierField);
        addField(form, "Supplier phone:", phoneField);
        addField(form, "Extra (strip/ml/mg):", extraField);
        add(form, BorderLayout.CENTER);
        MedicineFormActions actions = new MedicineFormActions(this);
        JButton saveBtn = Ui.button("Save", Ui.GREEN);
        saveBtn.addActionListener(actions);
        JButton closeBtn = Ui.button("Close", Ui.HEADER);
        closeBtn.addActionListener(actions);
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(Ui.BG);
        bottom.add(saveBtn);
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);
        if (medicine != null) {
            loadMedicine(medicine);
        }
    }

    private void addField(JPanel form, String label, JTextField field) {
        form.add(new JLabel(label));
        form.add(field);
    }

    private void loadMedicine(Medicine medicine) {
        idField.setText(medicine.getId());
        idField.setEditable(false);
        nameField.setText(medicine.getName());
        typeBox.setSelectedItem(medicine.getClass().getSimpleName());
        priceField.setText(String.valueOf(medicine.getPrice()));
        qtyField.setText(String.valueOf(medicine.getQty()));
        expiryField.setText(medicine.getExpiryDate().toString());
        limitField.setText(String.valueOf(medicine.getThreshold()));
        supplierField.setText(medicine.getSupplier().getName());
        phoneField.setText(medicine.getSupplier().getPhone());
        extraField.setText(String.valueOf(medicine.getExtra()));
    }
}
