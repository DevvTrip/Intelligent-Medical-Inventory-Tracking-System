package com.mits.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable Swing metric card component for displaying dashboard KPI summary numbers.
 * Demonstrates:
 * - Unit 5: AWT Layout Managers (BorderLayout, GridLayout, EmptyBorder)
 * - Unit 5: Fonts, Colors, and compound borders
 */
public class StatusCardPanel extends JPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatusCardPanel(String title, String initialValue, String subtitle, Color accentColor) {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)
                )
        ));

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(110, 120, 130));

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(33, 37, 41));

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(accentColor.darker());

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        centerPanel.setOpaque(false);
        centerPanel.add(valueLabel);
        centerPanel.add(subtitleLabel);

        add(titleLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}
