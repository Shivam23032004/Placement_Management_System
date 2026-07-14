package com.placement;

import com.formdev.flatlaf.FlatLightLaf;
import com.placement.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Setup modern colors and UI customizations in FlatLaf
        try {
            // Rounded corners globally
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("CheckBox.arc", 4);
            
            // Modern Slate / Indigo Palette
            Color primaryColor = new Color(79, 70, 229); // #4F46E5
            Color primaryHover = new Color(67, 56, 202); // #4338CA
            Color primaryActive = new Color(55, 48, 163); // #3730A3
            
            UIManager.put("Component.focusColor", primaryColor);
            UIManager.put("Component.focusedBorderColor", primaryColor);
            
            // Buttons Styling
            UIManager.put("Button.background", primaryColor);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", primaryHover);
            UIManager.put("Button.focusedBackground", primaryHover);
            UIManager.put("Button.selectedBackground", primaryActive);
            
            // TabbedPane Styling
            UIManager.put("TabbedPane.selectedBackground", new Color(238, 242, 255)); // Indigo 50
            UIManager.put("TabbedPane.selectedForeground", primaryColor);
            UIManager.put("TabbedPane.underlineColor", primaryColor);
            UIManager.put("TabbedPane.hoverColor", new Color(224, 231, 255)); // Indigo 100
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 12));
            
            // Tables Styling
            UIManager.put("Table.alternateRowColor", new Color(248, 250, 252)); // Slate 50
            UIManager.put("Table.selectionBackground", new Color(224, 231, 255)); // Selection highlight
            UIManager.put("Table.selectionForeground", new Color(30, 41, 59));
            UIManager.put("Table.gridColor", new Color(241, 245, 249)); // Slate 100
            UIManager.put("Table.rowHeight", 26);
            UIManager.put("TableHeader.background", new Color(241, 245, 249));
            UIManager.put("TableHeader.foreground", new Color(79, 70, 229)); // Indigo header text
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
            UIManager.put("TableHeader.separatorColor", new Color(226, 232, 240));
            
            // Scrollbar Styling
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumb", new Color(203, 213, 225));
            
            // Global default Font for clean look
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 12));
            
        } catch (Exception e) {
            System.err.println("Failed to initialize custom styles: " + e.getMessage());
        }

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}