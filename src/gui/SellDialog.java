package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import businesslogic.Inventory;

public class SellDialog extends JFrame {
    Inventory inventory;
    Dashboard dashboard;
    JTextField idField = new JTextField();
    JTextField qtyField = new JTextField();
    JLabel msgLabel = new JLabel("Enter ID and quantity");
    JButton sellBtn = Ui.button("Sell", Ui.GREEN);
    JButton serverBtn = Ui.button("Send to Server", Ui.HEADER);

    public SellDialog(Inventory inventory, Dashboard dashboard) {
        this.inventory = inventory;
        this.dashboard = dashboard;
        setTitle("Sell Stock");
        setSize(400, 280);
        setLocationRelativeTo(dashboard);
        setLayout(new BorderLayout());
        add(Ui.header("Sell Stock"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBackground(Ui.BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        form.add(new JLabel("Medicine ID:"));
        form.add(idField);
        form.add(new JLabel("Quantity:"));
        form.add(qtyField);
        add(form, BorderLayout.CENTER);
        SellActions actions = new SellActions(this);
        sellBtn.addActionListener(actions);
        serverBtn.addActionListener(actions);
        JButton closeBtn = Ui.button("Close", Ui.RED);
        closeBtn.addActionListener(actions);
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setBackground(Ui.BG);
        buttons.add(sellBtn);
        buttons.add(serverBtn);
        buttons.add(closeBtn);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Ui.BG);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(msgLabel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }
}
