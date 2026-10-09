package com.mits.gui;

import com.mits.exception.AuthenticationException;
import com.mits.model.User;
import com.mits.service.InventoryService;
import com.mits.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Authentication dialog for MITS system.
 * Demonstrates:
 * - Unit 5: Swing components (JFrame, JPanel, JTextField, JPasswordField, JButton, JOptionPane)
 * - Unit 5: Event handling (ActionListener)
 * - Unit 3: AuthenticationException handling and user feedback dialogs
 */
public class LoginFrame extends JFrame {
    private final InventoryService inventoryService;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        initUI();
    }

    private void initUI() {
        setTitle("Login - " + Constants.APP_TITLE);
        setSize(440, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Medical Inventory System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(24, 43, 73));

        JLabel subtitleLabel = new JLabel("B.Tech Capstone Assessment • Sign In", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(110, 120, 135));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField("admin", 15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField("admin123", 15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);

        // Quick demo credential autofill panel
        JPanel quickFillPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        quickFillPanel.setOpaque(false);
        JLabel demoLabel = new JLabel("Demo Fill: ");
        demoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        demoLabel.setForeground(Color.GRAY);
        quickFillPanel.add(demoLabel);

        JButton adminBtn = createQuickButton("Admin", "admin", "admin123");
        JButton pharmaBtn = createQuickButton("Pharmacist", "pharmacist", "pharma123");
        JButton docBtn = createQuickButton("Doctor", "doctor", "doctor123");
        quickFillPanel.add(adminBtn);
        quickFillPanel.add(pharmaBtn);
        quickFillPanel.add(docBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 4, 0);
        formPanel.add(quickFillPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Actions Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setOpaque(false);

        JButton loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setBackground(new Color(30, 130, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(110, 34));
        loginButton.addActionListener(e -> performLogin());

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exitButton.setPreferredSize(new Dimension(90, 34));
        exitButton.addActionListener(e -> System.exit(0));

        actionPanel.add(loginButton);
        actionPanel.add(exitButton);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Bind ENTER key to login
        getRootPane().setDefaultButton(loginButton);

        setContentPane(mainPanel);
    }

    private JButton createQuickButton(String title, String user, String pass) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btn.setMargin(new Insets(2, 6, 2, 6));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            usernameField.setText(user);
            passwordField.setText(pass);
        });
        return btn;
    }

    private void performLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User authenticatedUser = inventoryService.authenticate(user, pass);
            // Open Dashboard
            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame(inventoryService, authenticatedUser);
                dashboard.setVisible(true);
            });
            dispose(); // Close login window
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password!\nPlease check your credentials and try again.\n(" + ex.getMessage() + ")",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
