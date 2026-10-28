package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import businesslogic.ExpiryMonitor;
import businesslogic.Inventory;
import model.Medicine;
import usermanagement.User;
import utils.SortUtil;

public class Dashboard extends JFrame {

    private Inventory inventory;
    private SummaryCards cards;
    private JLabel statusLabel;
    DefaultTableModel model;
    JTable table;

    public Dashboard(Inventory inventory, User user) {
        this.inventory = inventory;
        setTitle("Medical Inventory - Dashboard");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel heading = new JPanel(new BorderLayout());
        heading.add(Ui.header("Dashboard - " + user.getRole() + " ("
                + user.getUsername() + ")"), BorderLayout.NORTH);
        heading.add(new JLabel(" Kaggle catalog (Nov 2022 base prices); stock, expiry, limits and suppliers are demo data."),
                BorderLayout.SOUTH);
        add(heading, BorderLayout.NORTH);

        cards = new SummaryCards();
        cards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[] {"ID", "Name", "Type", "Price",
                "Quantity", "Expiry", "Reorder Level", "Supplier", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        Ui.styleTable(table, new int[] {45, 225, 70, 75, 80, 95, 100, 115, 210});
        table.setDefaultRenderer(Object.class, new StatusRenderer(8));
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Ui.BG);
        center.add(cards, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        DashboardActions actions = new DashboardActions(this, inventory);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttons.setBackground(Ui.BG);
        addButton(buttons, "Add", Ui.GREEN, actions);
        addButton(buttons, "Update", Ui.GREEN, actions);
        JButton deleteBtn = addButton(buttons, "Delete", Ui.RED, actions);
        addButton(buttons, "Search", Ui.HEADER, actions);
        addButton(buttons, "Reports", Ui.HEADER, actions);
        addButton(buttons, "Sell", Ui.ORANGE, actions);
        JButton backupBtn = addButton(buttons, "Backup", Ui.HEADER, actions);
        JButton restoreBtn = addButton(buttons, "Restore", Ui.HEADER, actions);
        addButton(buttons, "Refresh", Ui.GREEN, actions);

        if (!user.isAdmin()) {
            deleteBtn.setEnabled(false);
            backupBtn.setEnabled(false);
            restoreBtn.setEnabled(false);
        }

        statusLabel = new JLabel(" Ready");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Ui.BG);
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(Ui.BG);
        south.add(buttons, BorderLayout.NORTH);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        refreshTable();
        startMonitor();
    }

    private JButton addButton(JPanel panel, String text, Color color, DashboardActions actions) {
        JButton b = Ui.button(text, color);
        b.addActionListener(actions);
        panel.add(b);
        return b;
    }

    void refreshTable() {
        model.setRowCount(0);
        ArrayList<Medicine> list = inventory.getMedicines();
        SortUtil.sortByExpiry(list);
        for (Medicine m : list) {
            model.addRow(new Object[] {
                m.getId(), m.getName(), m.getClass().getSimpleName(),
                String.format("%.2f", m.calculatePrice()), m.getQty(),
                m.getExpiryDate().toString(), m.getThreshold(),
                m.getSupplier().getName(), m.getStatus()
            });
        }
        cards.refresh(inventory);
    }

    private void startMonitor() {
        Runnable refreshView = new Runnable() {
            public void run() {
                refreshTable();
            }
        };
        ExpiryMonitor monitor = new ExpiryMonitor(inventory, statusLabel, refreshView);
        Thread thread = new Thread(monitor);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
}
