package com.mits.gui;

import com.mits.model.Medicine;
import com.mits.service.InventoryService;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Swing component that renders a category stock distribution bar chart using Graphics2D.
 * Demonstrates:
 * - Unit 5: AWT / Graphics2D custom rendering
 * - Unit 5: Swing component hierarchy (JPanel paintComponent)
 */
public class CategoryBarChartPanel extends JPanel {
    private final InventoryService inventoryService;

    // Palette for distinct medicine categories
    private static final Color[] BAR_COLORS = {
            new Color(41, 128, 185),  // Blue (Tablet)
            new Color(230, 126, 34),  // Orange (Antibiotic)
            new Color(39, 174, 96),   // Green (Syrup)
            new Color(192, 57, 43),   // Red (Injection)
            new Color(142, 68, 173),  // Purple (Ointment)
            new Color(22, 160, 133)   // Teal (General)
    };

    public CategoryBarChartPanel(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        setPreferredSize(new Dimension(500, 260));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Enable antialiasing for crisp text and graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        Map<String, Integer> categoryStock = computeStockByCategory();
        if (categoryStock.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString("No inventory data available to chart.", getWidth() / 3, getHeight() / 2);
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int paddingLeft = 55;
        int paddingBottom = 45;
        int paddingTop = 35;
        int paddingRight = 20;

        int chartWidth = width - paddingLeft - paddingRight;
        int chartHeight = height - paddingTop - paddingBottom;

        // Chart Title
        g2.setColor(new Color(44, 62, 80));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("Stock Units Distribution by Category (Graphics2D)", paddingLeft, 20);

        // Find max stock for y-axis scaling
        int maxStock = 1;
        for (int qty : categoryStock.values()) {
            if (qty > maxStock) maxStock = qty;
        }
        // Round max up to nearest convenient interval
        int yMax = (int) (Math.ceil(maxStock / 100.0) * 100);
        if (yMax <= 0) yMax = 100;

        // Draw horizontal grid lines and Y-axis scale labels
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int gridSteps = 4;
        for (int i = 0; i <= gridSteps; i++) {
            int yVal = (yMax * i) / gridSteps;
            int yPos = height - paddingBottom - (int) ((double) i / gridSteps * chartHeight);

            g2.setColor(new Color(235, 238, 242));
            g2.drawLine(paddingLeft, yPos, width - paddingRight, yPos);

            g2.setColor(new Color(127, 140, 141));
            String label = String.valueOf(yVal);
            g2.drawString(label, paddingLeft - 35, yPos + 4);
        }

        // Draw axes lines
        g2.setColor(new Color(189, 195, 199));
        g2.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom); // X axis
        g2.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom);                       // Y axis

        // Draw bars
        int barCount = categoryStock.size();
        int barWidth = Math.max(20, (chartWidth / (barCount * 2)));
        int gap = (chartWidth - (barCount * barWidth)) / (barCount + 1);

        int index = 0;
        for (Map.Entry<String, Integer> entry : categoryStock.entrySet()) {
            String category = entry.getKey();
            int qty = entry.getValue();

            int barHeight = (int) (((double) qty / yMax) * chartHeight);
            int x = paddingLeft + gap + index * (barWidth + gap);
            int y = height - paddingBottom - barHeight;

            Color baseColor = BAR_COLORS[index % BAR_COLORS.length];

            // Render gradient bar
            GradientPaint gradient = new GradientPaint(x, y, baseColor, x + barWidth, y + barHeight, baseColor.darker());
            g2.setPaint(gradient);
            g2.fillRoundRect(x, y, barWidth, barHeight, 6, 6);

            // Bar border
            g2.setColor(baseColor.darker());
            g2.drawRoundRect(x, y, barWidth, barHeight, 6, 6);

            // Value text above bar
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String valStr = String.valueOf(qty);
            int valW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, x + (barWidth - valW) / 2, Math.max(paddingTop + 10, y - 4));

            // Category text below bar (abbreviated if needed)
            g2.setColor(new Color(52, 73, 94));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String catLabel = category.length() > 8 ? category.substring(0, 7) + "." : category;
            int catW = g2.getFontMetrics().stringWidth(catLabel);
            g2.drawString(catLabel, x + (barWidth - catW) / 2, height - paddingBottom + 16);

            index++;
        }
    }

    private Map<String, Integer> computeStockByCategory() {
        Map<String, Integer> map = new LinkedHashMap<>();
        List<Medicine> medicines = inventoryService.getAllMedicines();
        for (Medicine m : medicines) {
            String cat = m.getCategory();
            map.put(cat, map.getOrDefault(cat, 0) + m.getTotalStock());
        }
        return map;
    }
}
