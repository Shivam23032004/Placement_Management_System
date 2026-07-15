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
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Default full screen
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // screen ke center mein khulega
        setLayout(new BorderLayout());

        // Main container panel to split screen
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(248, 250, 252)); // Slate 50

        // Left Panel - Welcome Banner (Dark Indigo)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(79, 70, 229)); // Indigo
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.insets = new Insets(20, 40, 20, 40);
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.gridx = 0;

        JLabel logoLabel = new JLabel("🎓");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 0;
        leftPanel.add(logoLabel, leftGbc);

        JLabel leftTitle = new JLabel("Placement Portal");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        leftTitle.setForeground(Color.WHITE);
        leftTitle.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(10, 40, 10, 40);
        leftPanel.add(leftTitle, leftGbc);

        JLabel leftSubtitle = new JLabel("Manage, Track, and Optimize Placements");
        leftSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftSubtitle.setForeground(new Color(199, 210, 254)); // Indigo 200
        leftSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 2;
        leftGbc.insets = new Insets(5, 40, 25, 40);
        leftPanel.add(leftSubtitle, leftGbc);

        // Feature Highlights
        JLabel featuresLabel = new JLabel("<html><body style='text-align: center; color: #E0E7FF; font-family: Segoe UI;'>"
            + "<h3 style='margin:0; font-size: 13px; color: #FFFFFF;'>INTEGRATED DSA SYSTEMS</h3><br>"
            + "• <b>BST & Hash Indexing</b>: O(1) Student Lookup & CGPA Sorting<br>"
            + "• <b>Trie Prefix Tree</b>: Real-time Company Autocomplete Search<br>"
            + "• <b>Priority Queue</b>: CGPA-Ordered Interview Backlogs<br>"
            + "• <b>Graph BFS Traversal</b>: Overlapping Placements Network Mapping"
            + "</body></html>");
        featuresLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leftGbc.gridy = 3;
        leftGbc.insets = new Insets(10, 20, 10, 20);
        leftPanel.add(featuresLabel, leftGbc);

        // Right Panel - Login Card Panel (Sleek Slate Dark Card in Center)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(15, 23, 42)); // Slate 900

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(new Color(30, 41, 59)); // Slate 800
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true), // rounded border
            BorderFactory.createEmptyBorder(40, 45, 40, 45)
        ));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(10, 10, 10, 10);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginHeader = new JLabel("Welcome Back");
        loginHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        loginHeader.setForeground(new Color(248, 250, 252)); // Slate 50
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        cardPanel.add(loginHeader, cardGbc);

        JLabel loginSub = new JLabel("Please enter your credentials to login");
        loginSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginSub.setForeground(new Color(148, 163, 184)); // Slate 400
        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(0, 10, 20, 10);
        cardPanel.add(loginSub, cardGbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(new Color(148, 163, 184)); // Slate 400
        cardGbc.gridy = 2;
        cardGbc.gridwidth = 1;
        cardGbc.gridx = 0;
        cardGbc.insets = new Insets(8, 10, 8, 10);
        cardPanel.add(userLabel, cardGbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(220, 32));
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username");
        cardGbc.gridx = 1;
        cardPanel.add(usernameField, cardGbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(148, 163, 184)); // Slate 400
        cardGbc.gridx = 0;
        cardGbc.gridy = 3;
        cardPanel.add(passLabel, cardGbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(220, 32));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");
        cardGbc.gridx = 1;
        cardPanel.add(passwordField, cardGbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);
        
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(110, 34));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(110, 34));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerButton.setBackground(new Color(51, 65, 85)); // dark slate
        registerButton.setForeground(new Color(99, 102, 241)); // Indigo 500 text
        
        btnPanel.add(loginButton);
        btnPanel.add(registerButton);

        cardGbc.gridx = 0;
        cardGbc.gridy = 4;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(18, 10, 4, 10);
        cardPanel.add(btnPanel, cardGbc);

        // Status label (error messages)
        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(239, 68, 68)); // Tomato red
        cardGbc.gridx = 0;
        cardGbc.gridy = 5;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(4, 10, 4, 10);
        cardPanel.add(statusLabel, cardGbc);

        rightPanel.add(cardPanel);

        // Assemble split layouts
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridy = 0;
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weighty = 1.0;

        mainGbc.gridx = 0;
        mainGbc.weightx = 0.35; // 35% sidebar
        mainPanel.add(leftPanel, mainGbc);

        mainGbc.gridx = 1;
        mainGbc.weightx = 0.65; // 65% content
        mainPanel.add(rightPanel, mainGbc);

        add(mainPanel, BorderLayout.CENTER);

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