package com.placement.ui;

import com.placement.dao.CompanyDAO;
import com.placement.dao.StudentDAO;
import com.placement.dao.UserDAO;
import com.placement.model.Company;
import com.placement.model.Student;
import com.placement.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * RegisterFrame: Unified premium registration window for Student and Recruiter.
 * Integrates properly with FlatLaf styling and database entities.
 */
public class RegisterFrame extends JDialog {

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final CompanyDAO companyDAO;

    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;

    // CardLayout Panel for conditional forms
    private JPanel formCardPanel;
    private CardLayout cardLayout;

    // Student fields
    private JTextField studentNameField;
    private JComboBox<String> branchComboBox;
    private JTextField cgpaField;
    private JTextField backlogsField;
    private JTextField studentPhoneField;
    private JTextField studentEmailField;
    private JTextField resumeField;

    // Recruiter / Company fields
    private JTextField companyNameField;
    private JTextField companyDescField;
    private JTextField companyWebField;

    public RegisterFrame(JFrame parent) {
        super(parent, "Create New Account", true);
        userDAO = new UserDAO();
        studentDAO = new StudentDAO();
        companyDAO = new CompanyDAO();

        setSize(480, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel with Premium Gradient Mock (using FlatLaf or themed solid color background)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(79, 70, 229)); // Primary Indigo
        headerPanel.setPreferredSize(new Dimension(480, 65));
        headerPanel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("Join Placement Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Create a student or recruiter account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(224, 231, 255)); // Indigo 100
        
        GridBagConstraints hGbc = new GridBagConstraints();
        hGbc.gridx = 0;
        hGbc.gridy = 0;
        headerPanel.add(titleLabel, hGbc);
        hGbc.gridy = 1;
        headerPanel.add(subtitleLabel, hGbc);
        
        add(headerPanel, BorderLayout.NORTH);

        // Core content panel
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Row 1: Account Role Select
        JPanel roleRow = new JPanel(new GridLayout(1, 2, 10, 10));
        roleRow.setMaximumSize(new Dimension(440, 35));
        
        JLabel roleLabel = new JLabel("Register As:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleRow.add(roleLabel);
        roleComboBox = new JComboBox<>(new String[]{"Student", "Recruiter"});
        roleComboBox.addActionListener(e -> toggleFormRole());
        roleRow.add(roleComboBox);
        bodyPanel.add(roleRow);
        bodyPanel.add(Box.createVerticalStrut(10));

        // Row 2: Username
        JPanel userRow = new JPanel(new GridLayout(1, 2, 10, 10));
        userRow.setMaximumSize(new Dimension(440, 35));
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userRow.add(userLabel);
        usernameField = new JTextField();
        userRow.add(usernameField);
        bodyPanel.add(userRow);
        bodyPanel.add(Box.createVerticalStrut(10));

        // Row 3: Password
        JPanel passRow = new JPanel(new GridLayout(1, 2, 10, 10));
        passRow.setMaximumSize(new Dimension(440, 35));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passRow.add(passLabel);
        passwordField = new JPasswordField();
        passRow.add(passwordField);
        bodyPanel.add(passRow);
        bodyPanel.add(Box.createVerticalStrut(15));

        // CardLayout Panel initialization
        cardLayout = new CardLayout();
        formCardPanel = new JPanel(cardLayout);

        // Build Student Form
        formCardPanel.add(buildStudentFormPanel(), "student");

        // Build Recruiter Form
        formCardPanel.add(buildRecruiterFormPanel(), "recruiter");

        bodyPanel.add(formCardPanel);
        add(bodyPanel, BorderLayout.CENTER);

        // Footer buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(100, 116, 139)); // Slate 500
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(90, 32));
        cancelBtn.addActionListener(e -> dispose());
        
        JButton registerBtn = new JButton("Register Now");
        registerBtn.setPreferredSize(new Dimension(120, 32));
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerBtn.addActionListener(this::handleRegistration);
        
        footerPanel.add(cancelBtn);
        footerPanel.add(registerBtn);
        add(footerPanel, BorderLayout.SOUTH);

        // Default layout selection
        cardLayout.show(formCardPanel, "student");
    }

    private JPanel buildStudentFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Student Details"));

        String[] branchOptions = {"CSE", "IT", "ECE", "ME", "CE", "EE"};

        p.add(createFormField("Full Name *:", studentNameField = new JTextField()));
        p.add(Box.createVerticalStrut(8));

        JPanel branchRow = new JPanel(new GridLayout(1, 2, 10, 5));
        branchRow.setMaximumSize(new Dimension(420, 30));
        branchRow.add(new JLabel("Branch:"));
        branchComboBox = new JComboBox<>(branchOptions);
        branchRow.add(branchComboBox);
        p.add(branchRow);
        p.add(Box.createVerticalStrut(8));

        p.add(createFormField("Current CGPA *:", cgpaField = new JTextField()));
        p.add(Box.createVerticalStrut(8));
        p.add(createFormField("Active Backlogs *:", backlogsField = new JTextField("0")));
        p.add(Box.createVerticalStrut(8));
        p.add(createFormField("Phone Number:", studentPhoneField = new JTextField()));
        p.add(Box.createVerticalStrut(8));
        p.add(createFormField("Email Address *:", studentEmailField = new JTextField()));
        p.add(Box.createVerticalStrut(8));
        p.add(createFormField("Resume Link (URL):", resumeField = new JTextField()));

        return p;
    }

    private JPanel buildRecruiterFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Recruiter & Company Profile"));

        p.add(createFormField("Company Name *:", companyNameField = new JTextField()));
        p.add(Box.createVerticalStrut(10));
        p.add(createFormField("Description:", companyDescField = new JTextField()));
        p.add(Box.createVerticalStrut(10));
        p.add(createFormField("Website URL:", companyWebField = new JTextField()));

        return p;
    }

    private JPanel createFormField(String labelText, JTextField field) {
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 5));
        row.setMaximumSize(new Dimension(420, 30));
        row.add(new JLabel(labelText));
        row.add(field);
        return row;
    }

    private void toggleFormRole() {
        String role = (String) roleComboBox.getSelectedItem();
        if ("Student".equals(role)) {
            cardLayout.show(formCardPanel, "student");
        } else {
            cardLayout.show(formCardPanel, "recruiter");
        }
    }

    private void handleRegistration(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Username and Password.", "Required Field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dbRole = "Student".equals(selectedRole) ? "student" : "recruiter";

        if ("student".equals(dbRole)) {
            String fullName = studentNameField.getText().trim();
            String branch = (String) branchComboBox.getSelectedItem();
            String cgpaText = cgpaField.getText().trim();
            String backlogText = backlogsField.getText().trim();
            String phone = studentPhoneField.getText().trim();
            String email = studentEmailField.getText().trim();
            String resume = resumeField.getText().trim();

            if (fullName.isEmpty() || cgpaText.isEmpty() || backlogText.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all mandatory (*) student fields.", "Required Field", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double cgpa;
            int backlogs;
            try {
                cgpa = Double.parseDouble(cgpaText);
                backlogs = Integer.parseInt(backlogText);
                if (cgpa < 0 || cgpa > 10) {
                    JOptionPane.showMessageDialog(this, "CGPA must be between 0.0 and 10.0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "CGPA and Backlogs must be valid numeric values.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Perform dynamic insertion
            boolean userCreated = userDAO.registerUser(username, password, dbRole);
            if (!userCreated) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different one.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User authenticatedUser = userDAO.authenticate(username, password);
            if (authenticatedUser == null) {
                JOptionPane.showMessageDialog(this, "User verification failed during registry process.", "System Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student studentObj = new Student(0, authenticatedUser.getUserId(), fullName, branch, cgpa, backlogs, phone, email, resume);
            boolean studentCreated = studentDAO.addStudent(studentObj);

            if (studentCreated) {
                JOptionPane.showMessageDialog(this, "Student Account Successfully Created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to insert Student profile details into database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if ("recruiter".equals(dbRole)) {
            String companyName = companyNameField.getText().trim();
            String desc = companyDescField.getText().trim();
            String web = companyWebField.getText().trim();

            if (companyName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Company Name is mandatory.", "Required Field", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean userCreated = userDAO.registerUser(username, password, dbRole);
            if (!userCreated) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different one.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User authenticatedUser = userDAO.authenticate(username, password);
            if (authenticatedUser == null) {
                JOptionPane.showMessageDialog(this, "User verification failed during registry process.", "System Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Company companyObj = new Company(0, authenticatedUser.getUserId(), companyName, desc, web);
            boolean companyCreated = companyDAO.addCompany(companyObj);

            if (companyCreated) {
                JOptionPane.showMessageDialog(this, "Recruiter Profile successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to insert Company profile details into database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
