package com.placement.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer that draws status strings as modern,
 * rounded colored badges (pills) similar to premium web/SaaS tables.
 */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (value == null) {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            return this;
        }

        String rawStatus = value.toString().trim();
        String status = rawStatus.toLowerCase();
        
        // Define soft badge colors
        Color bg = new Color(241, 245, 249); // Slate 100 (Default)
        Color fg = new Color(71, 85, 105);   // Slate 600 (Default)

        switch (status) {
            case "selected":
                bg = new Color(220, 252, 231); // Soft Green (Green 100)
                fg = new Color(21, 128, 61);   // Dark Green (Green 700)
                break;
            case "rejected":
                bg = new Color(254, 226, 226); // Soft Red (Red 100)
                fg = new Color(185, 28, 28);   // Dark Red (Red 700)
                break;
            case "applied":
                bg = new Color(241, 245, 249); // Soft Slate (Slate 100)
                fg = new Color(71, 85, 105);   // Slate 600
                break;
            case "shortlisted":
                bg = new Color(238, 242, 255); // Soft Indigo (Indigo 50)
                fg = new Color(79, 70, 229);   // Dark Indigo (Indigo 600)
                break;
            case "interview_scheduled":
            case "interview scheduled":
                bg = new Color(254, 243, 199); // Soft Amber (Amber 100)
                fg = new Color(180, 83, 9);    // Amber 700
                break;
            case "upcoming":
                bg = new Color(243, 244, 246); // Soft Gray (Gray 100)
                fg = new Color(75, 85, 99);    // Gray 600
                break;
            case "ongoing":
                bg = new Color(254, 243, 199); // Soft Amber (Yellow/Amber 100)
                fg = new Color(217, 119, 6);   // Amber 600
                break;
            case "completed":
                bg = new Color(240, 253, 250); // Soft Teal
                fg = new Color(13, 148, 136);  // Teal 600
                break;
        }

        final Color badgeBg = bg;
        final Color badgeFg = fg;

        // Create a custom JPanel representing the badge background
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background pill inside panel cell bounds
                g2.setColor(badgeBg);
                int w = getWidth() - 16;
                int h = getHeight() - 10;
                int x = (getWidth() - w) / 2;
                int y = (getHeight() - h) / 2;
                g2.fillRoundRect(x, y, w, h, 12, 12);
                g2.dispose();
            }
        };

        // Selection background fallback
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }

        JLabel textLabel = new JLabel(rawStatus.toUpperCase());
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        textLabel.setForeground(badgeFg);
        panel.add(textLabel);

        return panel;
    }
}
