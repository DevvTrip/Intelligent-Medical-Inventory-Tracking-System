package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Ui {

    public static final Color HEADER = new Color(0, 105, 148);
    public static final Color BG = new Color(240, 248, 250);
    public static final Color GREEN = new Color(46, 160, 67);
    public static final Color ORANGE = new Color(230, 145, 30);
    public static final Color RED = new Color(210, 55, 55);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 13);

    public static JLabel header(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(HEADER);
        label.setForeground(Color.WHITE);
        label.setFont(TITLE_FONT);
        label.setPreferredSize(new Dimension(100, 50));
        return label;
    }

    public static JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setFont(NORMAL_FONT);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return b;
    }

    public static void styleTable(JTable table, int[] widths) {
        table.setRowHeight(24);
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public static void look() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Look and feel not changed");
        }
    }
}
