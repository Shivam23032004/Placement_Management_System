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
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // screen ke center mein khulega
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Placement Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Buttons Row
        gbc.gridwidth = 1;
        gbc.gridy = 3;

        // Login Button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        panel.add(loginButton, gbc);

        // Register Button
        JButton registerButton = new JButton("Register");
        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        // Status label (error messages dikhane ke liye)
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        add(panel);

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