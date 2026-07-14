package com.placement.ui;

import com.placement.dao.UserDAO;
import com.placement.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();

        setTitle("Placement Management System - Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // screen ke center mein khulega
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel (Vibrant Indigo theme)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(79, 70, 229)); // Primary Indigo
        headerPanel.setPreferredSize(new Dimension(420, 70));
        headerPanel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("Placement Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Login to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(224, 231, 255)); // Indigo 100
        
        GridBagConstraints hGbc = new GridBagConstraints();
        hGbc.gridx = 0;
        hGbc.gridy = 0;
        headerPanel.add(titleLabel, hGbc);
        hGbc.gridy = 1;
        headerPanel.add(subtitleLabel, hGbc);
        
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel (Inputs)
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label & Field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(new Color(51, 65, 85)); // Slate 700
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(usernameField, gbc);

        // Password Label & Field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(51, 65, 85)); // Slate 700
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(passwordField, gbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 32));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 32));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Customize Register button color to make it look distinct (outline-style)
        registerButton.setBackground(new Color(241, 245, 249)); // light slate
        registerButton.setForeground(new Color(79, 70, 229)); // Indigo text
        
        btnPanel.add(loginButton);
        btnPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(12, 8, 4, 8);
        panel.add(btnPanel, gbc);

        // Status label (error messages)
        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(239, 68, 68)); // Tomato red
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 8, 4, 8);
        panel.add(statusLabel, gbc);

        add(panel, BorderLayout.CENTER);

        // Register button click handler
        registerButton.addActionListener(e -> {
            RegisterFrame regFrame = new RegisterFrame(this);
            regFrame.setVisible(true);
        });

        // Login button click hone par yeh chalega
        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password.");
                return;
            }

            User user = userDAO.authenticate(username, password);

            if (user == null) {
                statusLabel.setText("Invalid username or password.");
            } else {
                statusLabel.setText(" ");
                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");

                // Role ke hisaab se aage yahan dashboard khulega (abhi ke liye sirf message)
                this.dispose();  // login window band karo
                routeToDashboard(user);
            }
        });
    }

    private void routeToDashboard(User user) {
        switch (user.getRole()) {
            case "admin":
                SwingUtilities.invokeLater(() -> {
                    AdminDashboard dashboard = new AdminDashboard(user);
                    dashboard.setVisible(true);
                });
                break;
            case "student":
                SwingUtilities.invokeLater(() -> {
                    StudentDashboard dashboard = new StudentDashboard(user);
                    dashboard.setVisible(true);
                });
                break;
            case "recruiter":
                SwingUtilities.invokeLater(() -> {
                    RecruiterDashboard dashboard = new RecruiterDashboard(user);
                    dashboard.setVisible(true);
                });
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}