package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import businesslogic.Inventory;
import model.Medicine;

public class SearchScreen extends JFrame implements ActionListener {

    private Inventory inventory;
    private JTextField keyField = new JTextField(18);
    private JComboBox<String> modeBox = new JComboBox<String>(
            new String[] {"By Name", "By ID"});
    private DefaultTableModel model;
    private JLabel resultLabel = new JLabel("Type a keyword and press Search");

    public SearchScreen(Inventory inventory) {
        this.inventory = inventory;
        setTitle("Search Medicine");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Ui.header("Search Medicine"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout());
        top.setBackground(Ui.BG);
        top.add(modeBox);
        top.add(keyField);
        JButton searchBtn = Ui.button("Search", Ui.HEADER);
        searchBtn.addActionListener(this);
        top.add(searchBtn);

        model = new DefaultTableModel(new String[] {"ID", "Name", "Quantity", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        Ui.styleTable(table, new int[] {80, 340, 90, 270});
        table.setDefaultRenderer(Object.class, new StatusRenderer(3));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Ui.BG);
        center.add(top, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        resultLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(resultLabel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        model.setRowCount(0);
        String text = keyField.getText().trim();
        if (text.isEmpty()) {
            resultLabel.setText("Type something to search");
            return;
        }
        try {
            if (modeBox.getSelectedItem().toString().equals("By ID")) {
                int number = Integer.parseInt(text);
                Medicine m = inventory.searchMedicine(number);
                if (m == null) {
                    resultLabel.setText("No medicine with id " + number);
                } else {
                    addRow(m);
                    resultLabel.setText("Found 1 medicine");
                }
            } else {
                ArrayList<Medicine> found = inventory.searchMedicine(text);
                for (Medicine m : found) {
                    addRow(m);
                }
                resultLabel.setText("Found " + found.size() + " medicine(s)");
            }
        } catch (NumberFormatException e2) {
            resultLabel.setText("ID must be a number");
        }
    }

    private void addRow(Medicine m) {
        model.addRow(new Object[] {m.getId(), m.getName(), m.getQty(), m.getStatus()});
    }
}
