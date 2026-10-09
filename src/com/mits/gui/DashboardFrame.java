package com.mits.gui;

import com.mits.concurrent.ConcurrencyDemo;
import com.mits.concurrent.ReportGeneratorThread;
import com.mits.exception.*;
import com.mits.model.*;
import com.mits.network.InventoryClient;
import com.mits.report.ReportService;
import com.mits.service.InventoryService;
import com.mits.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Main application window with JTabbedPane for all system screens.
 * Demonstrates:
 * - Unit 5: Swing GUI (JFrame, JTabbedPane, JTable, JComboBox, JSpinner, JTextField, JButton,
 *           JProgressBar, JTextArea, JOptionPane, JFileChooser, TableRowSorter)
 * - Unit 5: Layout managers (BorderLayout, GridLayout, GridBagLayout, FlowLayout, CardLayout)
 * - Unit 5: Event handling (ActionListener via lambda)
 * - Unit 5: AWT (Color, Font, Graphics2D via CategoryBarChartPanel)
 * - Unit 5: Role-based access control on tab visibility
 * - Unit 4: Background thread for report generation (SwingUtilities.invokeLater)
 * - Unit 4: Live alert listener callback from daemon threads
 */
public class DashboardFrame extends JFrame {
    private final InventoryService inventoryService;
    private final User user;

    // Dashboard tab
    private StatusCardPanel totalMedicinesCard, totalValueCard, lowStockCard, expiringSoonCard, expiredCard;
    private JTextArea alertsArea;

    // Add/Edit tab
    private JComboBox<String> typeCombo;
    private JTextField codeField, nameField, descField;
    private JSpinner unitPriceSpinner, reorderLevelSpinner;
    private JComboBox<String> supplierIdCombo;
    private CardLayout specificFieldsLayout;
    private JPanel specificFieldsPanel;
    private JSpinner stripSizeSpinner, bottleVolumeSpinner, tubeWeightSpinner;
    private JCheckBox chewableCheck, cupIncludedCheck, coldChainCheck, prescriptionCheck;
    private JTextField vialTypeField, baseTypeField, spectrumField;

    // Search tab
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private MedicineTableModel searchTableModel;

    // Reports tab
    private JComboBox<ReportGeneratorThread.ReportType> reportTypeCombo;
    private JProgressBar reportProgress;
    private JTextArea reportArea;
    private JButton generateReportBtn, exportReportBtn;

    // Remote tab
    private InventoryClient inventoryClient;
    private JTextArea remoteResponseArea;

    public DashboardFrame(InventoryService inventoryService, User user) {
        this.inventoryService = inventoryService;
        this.user = user;

        setTitle(Constants.APP_TITLE + " - " + user.getFullName() + " (" + user.getRole() + ")");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        refreshDashboard();

        // Register live alert listener — pushes to GUI via SwingUtilities.invokeLater (Unit 4)
        inventoryService.addAlertListener(alert -> SwingUtilities.invokeLater(() -> {
            if (alertsArea != null) {
                alertsArea.append(alert + "\n");
                alertsArea.setCaretPosition(alertsArea.getDocument().getLength());
            }
        }));
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Tab 1: Dashboard — visible to all
        tabbedPane.addTab("Dashboard", createDashboardTab());

        // Tab 2 & 3: Data entry and stock ops — ADMIN and PHARMACIST only
        if (user.canManageStock()) {
            tabbedPane.addTab("Add/Edit Medicine", createAddEditTab());
            tabbedPane.addTab("Stock Operations", createStockOpsTab());
        }

        // Tab 4: Search — visible to all
        tabbedPane.addTab("Search", createSearchTab());

        // Tab 5: Reports — visible to all
        tabbedPane.addTab("Reports", createReportsTab());

        // Tab 6: Remote Server — visible to all
        tabbedPane.addTab("Remote Server", createRemoteTab());

        // Tab 7: Backup & Tools — ADMIN only
        if (user.isAdmin()) {
            tabbedPane.addTab("Backup & Tools", createBackupToolsTab());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==========================================
    // TAB 1: DASHBOARD
    // ==========================================
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Summary cards row
        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        cardsPanel.setOpaque(false);

        totalMedicinesCard = new StatusCardPanel("Total Medicines", "0", "SKUs registered", new Color(52, 152, 219));
        totalValueCard = new StatusCardPanel("Stock Value", "₹0", "Inventory valuation", new Color(46, 204, 113));
        lowStockCard = new StatusCardPanel("Low Stock", "0", "Need reorder", new Color(243, 156, 18));
        expiringSoonCard = new StatusCardPanel("Expiring Soon", "0", "Within 30 days", new Color(230, 126, 34));
        expiredCard = new StatusCardPanel("Expired", "0", "Quarantine needed", new Color(231, 76, 60));

        cardsPanel.add(totalMedicinesCard);
        cardsPanel.add(totalValueCard);
        cardsPanel.add(lowStockCard);
        cardsPanel.add(expiringSoonCard);
        cardsPanel.add(expiredCard);
        panel.add(cardsPanel, BorderLayout.NORTH);

        // Bottom: chart + alerts
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.setOpaque(false);

        CategoryBarChartPanel chartPanel = new CategoryBarChartPanel(inventoryService);
        bottomPanel.add(chartPanel);

        alertsArea = new JTextArea();
        alertsArea.setEditable(false);
        alertsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        JScrollPane alertsScroll = new JScrollPane(alertsArea);
        alertsScroll.setBorder(BorderFactory.createTitledBorder("Live Alerts (auto-updated by background threads)"));
        bottomPanel.add(alertsScroll);

        panel.add(bottomPanel, BorderLayout.CENTER);
        return panel;
    }

    private void refreshDashboard() {
        List<Medicine> all = inventoryService.getAllMedicines();
        totalMedicinesCard.setValue(String.valueOf(all.size()));

        double totalVal = 0; int lowCnt = 0, expSoon = 0, expCnt = 0;
        for (Medicine m : all) {
            totalVal += m.getTotalStock() * m.getUnitPrice();
            if (m.needsReorder()) lowCnt++;
            if (m.isExpired()) expCnt++;
            else if (m.daysToExpiry() >= 0 && m.daysToExpiry() <= 30) expSoon++;
        }
        totalValueCard.setValue(String.format("₹%,.2f", totalVal));
        lowStockCard.setValue(String.valueOf(lowCnt));
        expiringSoonCard.setValue(String.valueOf(expSoon));
        expiredCard.setValue(String.valueOf(expCnt));
    }

    // ==========================================
    // TAB 2: ADD / EDIT MEDICINE
    // ==========================================
    private JPanel createAddEditTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        typeCombo = new JComboBox<>(new String[]{"Tablet", "Syrup", "Injection", "Ointment", "Antibiotic"});
        addFormRow(formPanel, gbc, row++, "Type:", typeCombo);

        codeField = new JTextField(15);
        addFormRow(formPanel, gbc, row++, "Code (MED-XXX):", codeField);

        nameField = new JTextField(15);
        addFormRow(formPanel, gbc, row++, "Name:", nameField);

        descField = new JTextField(15);
        addFormRow(formPanel, gbc, row++, "Description:", descField);

        unitPriceSpinner = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 99999.0, 0.5));
        addFormRow(formPanel, gbc, row++, "Unit Price (₹):", unitPriceSpinner);

        reorderLevelSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 9999, 1));
        addFormRow(formPanel, gbc, row++, "Reorder Level:", reorderLevelSpinner);

        supplierIdCombo = new JComboBox<>();
        for (Supplier s : inventoryService.getSupplierRepository().findAll()) {
            supplierIdCombo.addItem(s.getId() + " - " + s.getName());
        }
        addFormRow(formPanel, gbc, row++, "Supplier:", supplierIdCombo);

        // Type-specific fields using CardLayout
        specificFieldsLayout = new CardLayout();
        specificFieldsPanel = new JPanel(specificFieldsLayout);
        specificFieldsPanel.setBorder(BorderFactory.createTitledBorder("Type-Specific Fields"));

        // Tablet panel
        JPanel tabletPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        stripSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        chewableCheck = new JCheckBox("Chewable");
        tabletPanel.add(new JLabel("Strip Size:")); tabletPanel.add(stripSizeSpinner);
        tabletPanel.add(chewableCheck);
        specificFieldsPanel.add(tabletPanel, "Tablet");

        // Syrup panel
        JPanel syrupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bottleVolumeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 2000, 10));
        cupIncludedCheck = new JCheckBox("Measuring Cup Included");
        syrupPanel.add(new JLabel("Volume (ml):")); syrupPanel.add(bottleVolumeSpinner);
        syrupPanel.add(cupIncludedCheck);
        specificFieldsPanel.add(syrupPanel, "Syrup");

        // Injection panel
        JPanel injPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        vialTypeField = new JTextField("Vial", 8);
        coldChainCheck = new JCheckBox("Cold Chain Required");
        injPanel.add(new JLabel("Vial Type:")); injPanel.add(vialTypeField);
        injPanel.add(coldChainCheck);
        specificFieldsPanel.add(injPanel, "Injection");

        // Ointment panel
        JPanel ointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tubeWeightSpinner = new JSpinner(new SpinnerNumberModel(20.0, 1.0, 500.0, 1.0));
        baseTypeField = new JTextField("Cream", 8);
        ointPanel.add(new JLabel("Weight (g):")); ointPanel.add(tubeWeightSpinner);
        ointPanel.add(new JLabel("Base:")); ointPanel.add(baseTypeField);
        specificFieldsPanel.add(ointPanel, "Ointment");

        // Antibiotic panel (multilevel: extends Tablet)
        JPanel antiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        spectrumField = new JTextField("Broad-Spectrum", 12);
        prescriptionCheck = new JCheckBox("Prescription Required", true);
        antiPanel.add(new JLabel("Spectrum:")); antiPanel.add(spectrumField);
        antiPanel.add(prescriptionCheck);
        specificFieldsPanel.add(antiPanel, "Antibiotic");

        typeCombo.addActionListener(e -> specificFieldsLayout.show(specificFieldsPanel, (String) typeCombo.getSelectedItem()));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(specificFieldsPanel, gbc);
        gbc.gridwidth = 1;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn = new JButton("Add Medicine");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        addBtn.addActionListener(e -> {
            try {
                Medicine m = buildMedicineFromForm();
                inventoryService.addMedicine(m);
                JOptionPane.showMessageDialog(this, "Medicine added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                Medicine m = buildMedicineFromForm();
                inventoryService.updateMedicine(m);
                JOptionPane.showMessageDialog(this, "Medicine updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                inventoryService.deleteMedicine(codeField.getText().trim());
                JOptionPane.showMessageDialog(this, "Medicine deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearBtn.addActionListener(e -> {
            codeField.setText(""); nameField.setText(""); descField.setText("");
            unitPriceSpinner.setValue(10.0); reorderLevelSpinner.setValue(20);
        });

        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn); btnPanel.add(clearBtn);

        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private Medicine buildMedicineFromForm() throws InvalidInputException {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        double price = ((Number) unitPriceSpinner.getValue()).doubleValue();
        int reorder = ((Number) reorderLevelSpinner.getValue()).intValue();
        String suppItem = (String) supplierIdCombo.getSelectedItem();
        String supplierId = suppItem != null ? suppItem.split(" - ")[0].trim() : "SUP-001";
        String type = (String) typeCombo.getSelectedItem();

        if (code.isEmpty() || name.isEmpty()) {
            throw new InvalidInputException("code/name", "Code and Name cannot be empty.");
        }

        String id = "M-" + System.currentTimeMillis() % 10000;
        Medicine m;

        switch (type) {
            case "Tablet":
                m = new Tablet(id, code, name, price, reorder, supplierId, desc,
                        ((Number) stripSizeSpinner.getValue()).intValue(), chewableCheck.isSelected());
                break;
            case "Syrup":
                m = new Syrup(id, code, name, price, reorder, supplierId, desc,
                        ((Number) bottleVolumeSpinner.getValue()).intValue(), cupIncludedCheck.isSelected());
                break;
            case "Injection":
                m = new Injection(id, code, name, price, reorder, supplierId, desc,
                        vialTypeField.getText().trim(), coldChainCheck.isSelected());
                break;
            case "Ointment":
                m = new Ointment(id, code, name, price, reorder, supplierId, desc,
                        ((Number) tubeWeightSpinner.getValue()).doubleValue(), baseTypeField.getText().trim());
                break;
            case "Antibiotic":
                m = new Antibiotic(id, code, name, price, reorder, supplierId, desc,
                        ((Number) stripSizeSpinner.getValue()).intValue(), false,
                        spectrumField.getText().trim(), prescriptionCheck.isSelected());
                break;
            default:
                throw new InvalidInputException("type", "Unknown medicine type: " + type);
        }
        return m;
    }

    // ==========================================
    // TAB 3: STOCK OPERATIONS
    // ==========================================
    private JPanel createStockOpsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Issue Stock sub-panel
        JPanel issuePanel = new JPanel(new GridBagLayout());
        issuePanel.setBorder(BorderFactory.createTitledBorder("Issue Stock (Dispense)"));
        issuePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField issueCodeField = new JTextField(12);
        JSpinner issueQtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        JTextField remarksField = new JTextField(12);
        JButton issueBtn = new JButton("Issue Stock");

        addFormRow(issuePanel, gbc, 0, "Medicine Code:", issueCodeField);
        addFormRow(issuePanel, gbc, 1, "Quantity:", issueQtySpinner);
        addFormRow(issuePanel, gbc, 2, "Remarks:", remarksField);
        gbc.gridx = 1; gbc.gridy = 3;
        issuePanel.add(issueBtn, gbc);

        issueBtn.addActionListener(e -> {
            try {
                String code = issueCodeField.getText().trim();
                int qty = (Integer) issueQtySpinner.getValue();
                inventoryService.issueStock(code, qty, user.getUsername(), remarksField.getText());
                Medicine m = inventoryService.getMedicine(code);
                JOptionPane.showMessageDialog(this,
                        "Issued " + qty + " units of " + m.getName() + "\nRemaining: " + m.getTotalStock(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Issue Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(issuePanel);

        // Restock sub-panel
        JPanel restockPanel = new JPanel(new GridBagLayout());
        restockPanel.setBorder(BorderFactory.createTitledBorder("Restock (Add Inventory)"));
        restockPanel.setOpaque(false);

        JTextField restockCodeField = new JTextField(12);
        JSpinner restockQtySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 9999, 1));
        JTextField batchField = new JTextField("B-NEW-001", 12);
        JTextField expiryField = new JTextField(LocalDate.now().plusYears(1).toString(), 12);
        JButton restockBtn = new JButton("Restock");

        addFormRow(restockPanel, gbc, 0, "Medicine Code:", restockCodeField);
        addFormRow(restockPanel, gbc, 1, "Quantity:", restockQtySpinner);
        addFormRow(restockPanel, gbc, 2, "Batch Number:", batchField);
        addFormRow(restockPanel, gbc, 3, "Expiry (YYYY-MM-DD):", expiryField);
        gbc.gridx = 1; gbc.gridy = 4;
        restockPanel.add(restockBtn, gbc);

        restockBtn.addActionListener(e -> {
            try {
                LocalDate exp = LocalDate.parse(expiryField.getText().trim());
                inventoryService.restock(restockCodeField.getText().trim(),
                        (Integer) restockQtySpinner.getValue(),
                        batchField.getText().trim(), exp, user.getUsername());
                JOptionPane.showMessageDialog(this, "Restock successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(restockPanel);
        return panel;
    }

    // ==========================================
    // TAB 4: SEARCH
    // ==========================================
    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setOpaque(false);
        searchField = new JTextField(18);
        categoryCombo = new JComboBox<>(new String[]{"ALL", "TABLET", "ANTIBIOTIC", "SYRUP", "INJECTION", "OINTMENT"});
        JButton searchBtn = new JButton("Search");

        topPanel.add(new JLabel("Query:")); topPanel.add(searchField);
        topPanel.add(new JLabel("Category:")); topPanel.add(categoryCombo);
        topPanel.add(searchBtn);

        searchTableModel = new MedicineTableModel(inventoryService.getAllMedicines());
        JTable table = new JTable(searchTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.setDefaultRenderer(Object.class, new MedicineTableCellRenderer());
        table.setDefaultRenderer(Integer.class, new MedicineTableCellRenderer());
        table.setDefaultRenderer(Double.class, new MedicineTableCellRenderer());
        table.setDefaultRenderer(Long.class, new MedicineTableCellRenderer());
        TableRowSorter<MedicineTableModel> sorter = new TableRowSorter<>(searchTableModel);
        table.setRowSorter(sorter);

        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            String cat = (String) categoryCombo.getSelectedItem();
            List<Medicine> results = "ALL".equals(cat)
                    ? inventoryService.search(q)
                    : inventoryService.search(q, cat);
            searchTableModel.setMedicines(results);
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB 5: REPORTS
    // ==========================================
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setOpaque(false);
        reportTypeCombo = new JComboBox<>(ReportGeneratorThread.ReportType.values());
        generateReportBtn = new JButton("Generate Report");
        exportReportBtn = new JButton("Export to File");
        exportReportBtn.setEnabled(false);
        topPanel.add(new JLabel("Report:")); topPanel.add(reportTypeCombo);
        topPanel.add(generateReportBtn); topPanel.add(exportReportBtn);

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportArea.setEditable(false);

        reportProgress = new JProgressBar(0, 100);
        reportProgress.setStringPainted(true);

        generateReportBtn.addActionListener(e -> {
            ReportGeneratorThread.ReportType type = (ReportGeneratorThread.ReportType) reportTypeCombo.getSelectedItem();
            generateReportBtn.setEnabled(false);
            reportArea.setText("Generating report...\n");
            reportProgress.setValue(0);

            ReportGeneratorThread thread = new ReportGeneratorThread(
                    inventoryService, type,
                    result -> SwingUtilities.invokeLater(() -> {
                        reportArea.setText(result);
                        generateReportBtn.setEnabled(true);
                        exportReportBtn.setEnabled(true);
                        reportProgress.setValue(100);
                    }),
                    (progress, msg) -> SwingUtilities.invokeLater(() -> {
                        reportProgress.setValue(progress);
                        reportProgress.setString(msg);
                    })
            );
            thread.start();
        });

        exportReportBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(".");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    new ReportService(inventoryService).exportReportToFile(reportArea.getText(), chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Report exported successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        panel.add(reportProgress, BorderLayout.SOUTH);
        return panel;
    }

    // ==========================================
    // TAB 6: REMOTE SERVER CLIENT
    // ==========================================
    private JPanel createRemoteTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setOpaque(false);
        JTextField hostField = new JTextField(Constants.DEFAULT_SERVER_HOST, 10);
        JTextField portField = new JTextField(String.valueOf(Constants.DEFAULT_SERVER_PORT), 5);
        JButton connectBtn = new JButton("Connect");
        JButton disconnectBtn = new JButton("Disconnect");
        disconnectBtn.setEnabled(false);

        topPanel.add(new JLabel("Host:")); topPanel.add(hostField);
        topPanel.add(new JLabel("Port:")); topPanel.add(portField);
        topPanel.add(connectBtn); topPanel.add(disconnectBtn);

        remoteResponseArea = new JTextArea();
        remoteResponseArea.setEditable(false);
        remoteResponseArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        JPanel cmdPanel = new JPanel(new BorderLayout(5, 0));
        JTextField commandField = new JTextField();
        JButton sendBtn = new JButton("Send");
        sendBtn.setEnabled(false);
        cmdPanel.add(new JLabel(" Cmd: "), BorderLayout.WEST);
        cmdPanel.add(commandField, BorderLayout.CENTER);
        cmdPanel.add(sendBtn, BorderLayout.EAST);

        inventoryClient = new InventoryClient();

        connectBtn.addActionListener(e -> {
            try {
                String banner = inventoryClient.connect(hostField.getText().trim(), Integer.parseInt(portField.getText().trim()));
                remoteResponseArea.append(banner + "\n");
                connectBtn.setEnabled(false);
                disconnectBtn.setEnabled(true);
                sendBtn.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        disconnectBtn.addActionListener(e -> {
            inventoryClient.disconnect();
            remoteResponseArea.append("Disconnected.\n");
            connectBtn.setEnabled(true);
            disconnectBtn.setEnabled(false);
            sendBtn.setEnabled(false);
        });

        sendBtn.addActionListener(e -> {
            String cmd = commandField.getText().trim();
            if (!cmd.isEmpty()) {
                try {
                    java.util.List<String> resp = inventoryClient.sendCommand(cmd);
                    remoteResponseArea.append("> " + cmd + "\n");
                    for (String line : resp) remoteResponseArea.append("  " + line + "\n");
                    commandField.setText("");
                } catch (IOException ex) {
                    remoteResponseArea.append("Error: " + ex.getMessage() + "\n");
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(remoteResponseArea), BorderLayout.CENTER);
        panel.add(cmdPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==========================================
    // TAB 7: BACKUP & TOOLS
    // ==========================================
    private JPanel createBackupToolsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setOpaque(false);

        JButton backupBtn = new JButton("Backup Now");
        JButton restoreBtn = new JButton("Restore Last Backup");
        JButton concDemoBtn = new JButton("Run Concurrency Demo");
        JButton excDemoBtn = new JButton("Run Exception Demo");

        btnPanel.add(backupBtn); btnPanel.add(restoreBtn);
        btnPanel.add(concDemoBtn); btnPanel.add(excDemoBtn);

        JTextArea outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setEditable(false);

        backupBtn.addActionListener(e -> {
            try {
                String path = inventoryService.triggerBackup();
                outputArea.append("Backup created: " + path + "\n");
            } catch (Exception ex) {
                outputArea.append("Backup failed: " + ex.getMessage() + "\n");
            }
        });

        restoreBtn.addActionListener(e -> {
            try {
                inventoryService.restoreBackup();
                outputArea.append("Backup restored successfully.\n");
                refreshDashboard();
            } catch (Exception ex) {
                outputArea.append("Restore failed: " + ex.getMessage() + "\n");
            }
        });

        concDemoBtn.addActionListener(e -> runDemoCaptured(() -> ConcurrencyDemo.runDemo(), "Concurrency Demo", outputArea));
        excDemoBtn.addActionListener(e -> runDemoCaptured(() -> com.mits.exception.ExceptionDemo.runAll(), "Exception Demo", outputArea));

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Captures System.out output from a demo method and appends it to a text area.
     */
    private void runDemoCaptured(Runnable demoMethod, String title, JTextArea outputArea) {
        new Thread(() -> {
            PrintStream originalOut = System.out;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                System.setOut(ps);
                demoMethod.run();
                System.out.flush();
                System.setOut(originalOut);
                SwingUtilities.invokeLater(() -> outputArea.append("\n--- " + title + " ---\n" + baos + "\n"));
            } catch (Exception ex) {
                System.setOut(originalOut);
                SwingUtilities.invokeLater(() -> outputArea.append(title + " error: " + ex.getMessage() + "\n"));
            }
        }, "Demo-" + title).start();
    }
}
