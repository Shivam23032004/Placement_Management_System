package com.placement;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.placement.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Setup modern Neon Dark colors and UI customizations in FlatLaf
        try {
            // Rounded corners globally for a premium card layout feel
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("CheckBox.arc", 4);
            
            // Premium Indigo / Violet Accent Brand Palette (glowing neon highlights)
            Color primaryColor = new Color(99, 102, 241); // #6366F1 (Indigo 500)
            Color primaryHover = new Color(124, 58, 237); // #7C3AED (Violet 600)
            Color primaryActive = new Color(109, 40, 217); // #6D28D9 (Violet 700)
            
            Color bgDark = new Color(15, 23, 42); // Slate 900
            Color cardDark = new Color(30, 41, 59); // Slate 800
            Color textLight = new Color(248, 250, 252); // Slate 50
            
            UIManager.put("Component.focusColor", primaryColor);
            UIManager.put("Component.focusedBorderColor", primaryColor);
            
            // Panel & background defaults
            UIManager.put("Panel.background", bgDark);
            UIManager.put("Label.foreground", textLight);
            UIManager.put("ScrollPane.background", bgDark);
            UIManager.put("ScrollPane.foreground", textLight);
            
            // Text inputs
            UIManager.put("TextField.background", cardDark);
            UIManager.put("TextField.foreground", textLight);
            UIManager.put("TextField.caretColor", textLight);
            UIManager.put("PasswordField.background", cardDark);
            UIManager.put("PasswordField.foreground", textLight);
            UIManager.put("PasswordField.caretColor", textLight);
            UIManager.put("ComboBox.background", cardDark);
            UIManager.put("ComboBox.foreground", textLight);
            
            // Buttons Styling
            UIManager.put("Button.background", primaryColor);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", primaryHover);
            UIManager.put("Button.focusedBackground", primaryHover);
            UIManager.put("Button.selectedBackground", primaryActive);
            
            // TabbedPane Styling
            UIManager.put("TabbedPane.selectedBackground", cardDark);
            UIManager.put("TabbedPane.selectedForeground", primaryColor);
            UIManager.put("TabbedPane.underlineColor", primaryColor);
            UIManager.put("TabbedPane.hoverColor", new Color(51, 65, 85)); // Slate 700
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabHeight", 40);
            
            // Tables Styling (SaaS Dark-UI-inspired clean lines)
            UIManager.put("Table.alternateRowColor", new Color(30, 41, 59)); // Slate 800
            UIManager.put("Table.background", bgDark);
            UIManager.put("Table.foreground", textLight);
            UIManager.put("Table.selectionBackground", new Color(99, 102, 241, 100)); // semi-transparent Indigo
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.gridColor", new Color(51, 65, 85)); // Slate 700
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            
            UIManager.put("TableHeader.background", cardDark);
            UIManager.put("TableHeader.foreground", textLight);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("TableHeader.separatorColor", new Color(51, 65, 85));
            UIManager.put("TableHeader.bottomSeparatorColor", primaryColor);
            
            // Scrollbar Styling
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumb", new Color(71, 85, 105)); // Slate 600
            UIManager.put("ScrollBar.track", bgDark);
            
            // Global default Font
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            
        } catch (Exception e) {
            System.err.println("Failed to initialize custom styles: " + e.getMessage());
        }

        FlatMacDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}