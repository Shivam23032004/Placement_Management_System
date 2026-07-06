package com.placement;

import com.formdev.flatlaf.FlatLightLaf;
import com.placement.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Modern look and feel apply karo poore app mein
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}