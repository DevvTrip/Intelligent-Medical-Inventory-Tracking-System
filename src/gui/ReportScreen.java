package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import utils.FileUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import businesslogic.Inventory;
import dataprocessing.SalesData;
import reporting.AnalyticsReport;
import reporting.ReportMaker;

public class ReportScreen extends JFrame implements ActionListener {

    private Inventory inventory;
    private JComboBox<String> typeBox = new JComboBox<String>(new String[] {
            "Stock Report", "Expiry Report", "Reorder Report", "Fast Moving",
            "Supplier List", "Activity Log", "Weekly Sales"});
    private JTextArea area = new JTextArea();
    private JLabel msgLabel = new JLabel("Choose a report and press Generate");

    public ReportScreen(Inventory inventory) {
        this.inventory = inventory;
        setTitle("Reports and Analytics");
        setSize(700, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Ui.header("Reports and Analytics"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout());
        top.setBackground(Ui.BG);
        top.add(new JLabel("Report:"));
        top.add(typeBox);
        JButton generateBtn = Ui.button("Generate", Ui.HEADER);
        generateBtn.addActionListener(this);
        top.add(generateBtn);
        JButton saveBtn = Ui.button("Save Text", Ui.GREEN);
        saveBtn.addActionListener(this);
        top.add(saveBtn);
        JButton closeBtn = Ui.button("Close", Ui.RED);
        closeBtn.addActionListener(this);
        top.add(closeBtn);

        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Ui.BG);
        center.add(top, BorderLayout.NORTH);
        center.add(new JScrollPane(area), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        msgLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(msgLabel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Close")) {
            dispose();
        } else {
            generate();
            if (e.getActionCommand().equals("Save Text")) {
                try {
                    FileUtil.saveReport(area.getText());
                    msgLabel.setText("Saved to " + FileUtil.REPORT_FILE);
                } catch (IOException ex) {
                    msgLabel.setText("Could not save report: " + ex.getMessage());
                }
            }
        }
    }

    private void generate() {
        String type = typeBox.getSelectedItem().toString();
        if (type.equals("Stock Report")) {
            area.setText(ReportMaker.stockReport(inventory));
        } else if (type.equals("Expiry Report")) {
            area.setText(ReportMaker.expiryReport(inventory));
        } else if (type.equals("Reorder Report")) {
            area.setText(ReportMaker.reorderReport(inventory));
        } else if (type.equals("Fast Moving")) {
            area.setText(AnalyticsReport.fastMovingReport(inventory));
        } else if (type.equals("Supplier List")) {
            area.setText(AnalyticsReport.supplierReport(inventory));
        } else if (type.equals("Activity Log")) {
            area.setText(AnalyticsReport.activityReport(inventory));
        } else {
            area.setText(new SalesData().getReport());
        }
        msgLabel.setText("Report generated");
    }
}
