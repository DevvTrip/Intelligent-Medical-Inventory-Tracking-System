package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import businesslogic.Inventory;
import usermanagement.LoginCheck;
import usermanagement.User;

public class LoginScreen extends JFrame implements ActionListener {

    private JTextField userField;
    private JPasswordField passField;
    private Inventory inventory;

    public LoginScreen(Inventory inventory) {
        this.inventory = inventory;
        setTitle("Medical Inventory - Login");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(Ui.header("Intelligent Medical Inventory"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBackground(Ui.BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        form.add(new JLabel("Username:"));
        userField = new JTextField();
        form.add(userField);
        form.add(new JLabel("Password:"));
        passField = new JPasswordField();
        form.add(passField);
        add(form, BorderLayout.CENTER);

        JButton loginBtn = Ui.button("Login", Ui.HEADER);
        loginBtn.addActionListener(this);
        JLabel hint = new JLabel("admin/admin123 or pharma/pharma123", SwingConstants.CENTER);
        JPanel bottom = new JPanel(new GridLayout(2, 1, 5, 5));
        bottom.setBackground(Ui.BG);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        bottom.add(loginBtn);
        bottom.add(hint);
        add(bottom, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        String name = userField.getText();
        String pass = new String(passField.getPassword());
        User user = LoginCheck.check(name, pass);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Wrong username or password");
            return;
        }
        dispose();
        new Dashboard(inventory, user).setVisible(true);
    }
}
