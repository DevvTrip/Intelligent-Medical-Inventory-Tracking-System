package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import businesslogic.Inventory;
import model.Medicine;

public class SummaryCards extends JPanel {

    private JLabel totalValue;
    private JLabel lowValue;
    private JLabel expiringValue;
    private JLabel expiredValue;

    public SummaryCards() {
        setLayout(new GridLayout(1, 4, 10, 10));
        setBackground(Ui.BG);
        totalValue = addCard("Total Medicines", Ui.HEADER);
        lowValue = addCard("Low Stock", Ui.ORANGE);
        expiringValue = addCard("Expiring Soon", Ui.ORANGE);
        expiredValue = addCard("Expired", Ui.RED);
    }

    private JLabel addCard(String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(color);
        titleLabel.setFont(Ui.NORMAL_FONT);
        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(Ui.TITLE_FONT);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        add(card);
        return valueLabel;
    }

    public void refresh(Inventory inventory) {
        int total = 0;
        int low = 0;
        int expiring = 0;
        int expired = 0;
        for (Medicine m : inventory.getMedicines()) {
            total++;
            if (m.isLowStock() && !m.isExpired()) {
                low++;
            }
            if (m.isExpiringSoon()) {
                expiring++;
            }
            if (m.isExpired()) {
                expired++;
            }
        }
        totalValue.setText(String.valueOf(total));
        lowValue.setText(String.valueOf(low));
        expiringValue.setText(String.valueOf(expiring));
        expiredValue.setText(String.valueOf(expired));
    }
}
