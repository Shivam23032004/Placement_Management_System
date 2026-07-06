package com.placement.ui;

import com.placement.dao.ApplicationDAO;
import com.placement.dao.CompanyDAO;
import com.placement.dao.DriveDAO;
import com.placement.dao.StudentDAO;
import com.placement.dao.UserDAO;
import com.placement.dsa.StudentBST;
import com.placement.model.Application;
import com.placement.model.Company;
import com.placement.model.Drive;
import com.placement.model.Student;
import com.placement.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    private StudentDAO studentDAO;
    private CompanyDAO companyDAO;
    private DriveDAO driveDAO;
    private ApplicationDAO applicationDAO;
    private UserDAO userDAO;
    private StudentBST studentBST;

    private DefaultTableModel studentsTableModel;
    private JTable studentsTable;
    private JTextField minCgpaField, maxCgpaField;

    private DefaultTableModel companiesTableModel;
    private JTable companiesTable;

    private DefaultTableModel drivesTableModel;
    private JTable drivesTable;

    private DefaultTableModel applicationsTableModel;
    private JTable applicationsTable;

    private static final String[] BRANCH_OPTIONS = {"CSE", "IT", "ECE", "ME", "CE", "EE"};
    private static final String[] STATUS_OPTIONS = {"applied", "shortlisted", "interview_scheduled", "selected", "rejected"};

    public AdminDashboard(User adminUser) {
        studentDAO = new StudentDAO();
        companyDAO = new CompanyDAO();
        driveDAO = new DriveDAO();
        applicationDAO = new ApplicationDAO();
        userDAO = new UserDAO();
        studentBST = new StudentBST();

        setTitle("Admin Dashboard - " + adminUser.getUsername());
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Students", buildStudentsPanel());
        tabbedPane.addTab("Companies", buildCompaniesPanel());
        tabbedPane.addTab("Drives", buildDrivesPanel());
        tabbedPane.addTab("Applications", buildApplicationsPanel());

        add(tabbedPane);

        loadStudents();
        loadCompanies();
        loadDrives();
        loadApplications();
    }

    // ================= STUDENTS TAB =================
    private JPanel buildStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Branch", "CGPA", "Backlogs", "Email"};
        studentsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        studentsTable = new JTable(studentsTableModel);
        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("CGPA Range Search (BST):"));
        searchPanel.add(new JLabel("Min:"));
        minCgpaField = new JTextField(4);
        searchPanel.add(minCgpaField);
        searchPanel.add(new JLabel("Max:"));
        maxCgpaField = new JTextField(4);
        searchPanel.add(maxCgpaField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchStudentsByCgpaRange());
        searchPanel.add(searchButton);

        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> loadStudents());
        searchPanel.add(showAllButton);

        panel.add(searchPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStudents() {
        List<Student> allStudents = studentDAO.getAllStudents();

        studentBST = new StudentBST();
        for (Student s : allStudents) {
            studentBST.insert(s);
        }

        populateStudentsTable(studentBST.getAllSortedByCgpa());
    }

    private void searchStudentsByCgpaRange() {
        try {
            double min = Double.parseDouble(minCgpaField.getText().trim());
            double max = Double.parseDouble(maxCgpaField.getText().trim());
            populateStudentsTable(studentBST.rangeSearch(min, max));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid CGPA numbers.");
        }
    }

    private void populateStudentsTable(List<Student> students) {
        studentsTableModel.setRowCount(0);
        for (Student s : students) {
            studentsTableModel.addRow(new Object[]{
                    s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogCount(), s.getEmail()
            });
        }
    }

    // ================= COMPANIES TAB =================
    private JPanel buildCompaniesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Description", "Website"};
        companiesTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        companiesTable = new JTable(companiesTableModel);
        panel.add(new JScrollPane(companiesTable), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Company");
        addButton.addActionListener(e -> showAddCompanyDialog());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCompanies() {
        companiesTableModel.setRowCount(0);
        for (Company c : companyDAO.getAllCompanies()) {
            companiesTableModel.addRow(new Object[]{
                    c.getCompanyId(), c.getName(), c.getDescription(), c.getWebsite()
            });
        }
    }

    private void showAddCompanyDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField websiteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Recruiter Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Recruiter Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Company Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Website:"));
        panel.add(websiteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Company",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            String website = websiteField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, password, and company name are required.");
                return;
            }

            boolean userCreated = userDAO.registerUser(username, password, "recruiter");
            if (!userCreated) {
                JOptionPane.showMessageDialog(this, "Failed to create recruiter login (username may already exist).");
                return;
            }

            User newUser = userDAO.authenticate(username, password);
            if (newUser == null) {
                JOptionPane.showMessageDialog(this, "Recruiter created but could not be linked. Try again.");
                return;
            }

            Company company = new Company(0, newUser.getUserId(), name, description, website);
            boolean companyCreated = companyDAO.addCompany(company);

            if (companyCreated) {
                JOptionPane.showMessageDialog(this, "Company added successfully!");
                loadCompanies();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add company.");
            }
        }
    }

    // ================= DRIVES TAB =================
    private JPanel buildDrivesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Company", "Job Role", "Package", "Min CGPA", "Max Backlogs", "Date", "Status", "Branches"};
        drivesTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        drivesTable = new JTable(drivesTableModel);
        panel.add(new JScrollPane(drivesTable), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Drive");
        addButton.addActionListener(e -> showAddDriveDialog());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadDrives() {
        drivesTableModel.setRowCount(0);
        for (Drive d : driveDAO.getAllDrives()) {
            Company c = companyDAO.getCompanyById(d.getCompanyId());
            String companyName = (c != null) ? c.getName() : "Unknown";

            drivesTableModel.addRow(new Object[]{
                    d.getDriveId(), companyName, d.getJobRole(), d.getPackageLpa(),
                    d.getMinCgpa(), d.getMaxBacklogs(), d.getDriveDate(), d.getStatus(),
                    d.getEligibleBranches()
            });
        }
    }

    private void showAddDriveDialog() {
        List<Company> companies = companyDAO.getAllCompanies();
        if (companies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add a company first.");
            return;
        }

        JComboBox<String> companyBox = new JComboBox<>();
        for (Company c : companies) {
            companyBox.addItem(c.getCompanyId() + " - " + c.getName());
        }

        JTextField jobRoleField = new JTextField();
        JTextField packageField = new JTextField();
        JTextField minCgpaField = new JTextField();
        JTextField maxBacklogsField = new JTextField();
        JTextField dateField = new JTextField("2026-08-15");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"upcoming", "ongoing", "completed"});

        JPanel branchPanel = new JPanel(new GridLayout(0, 3));
        JCheckBox[] branchChecks = new JCheckBox[BRANCH_OPTIONS.length];
        for (int i = 0; i < BRANCH_OPTIONS.length; i++) {
            branchChecks[i] = new JCheckBox(BRANCH_OPTIONS[i]);
            branchPanel.add(branchChecks[i]);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Company:"));
        panel.add(companyBox);
        panel.add(new JLabel("Job Role:"));
        panel.add(jobRoleField);
        panel.add(new JLabel("Package (LPA):"));
        panel.add(packageField);
        panel.add(new JLabel("Min CGPA:"));
        panel.add(minCgpaField);
        panel.add(new JLabel("Max Backlogs:"));
        panel.add(maxBacklogsField);
        panel.add(new JLabel("Drive Date (yyyy-mm-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Status:"));
        panel.add(statusBox);
        panel.add(new JLabel("Eligible Branches:"));
        panel.add(branchPanel);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Drive",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String selectedCompany = (String) companyBox.getSelectedItem();
                int companyId = Integer.parseInt(selectedCompany.split(" - ")[0]);

                String jobRole = jobRoleField.getText().trim();
                double packageLpa = Double.parseDouble(packageField.getText().trim());
                double minCgpa = Double.parseDouble(minCgpaField.getText().trim());
                int maxBacklogs = Integer.parseInt(maxBacklogsField.getText().trim());
                String driveDate = dateField.getText().trim();
                String status = (String) statusBox.getSelectedItem();

                List<String> selectedBranches = new ArrayList<>();
                for (JCheckBox cb : branchChecks) {
                    if (cb.isSelected()) {
                        selectedBranches.add(cb.getText());
                    }
                }

                if (jobRole.isEmpty() || selectedBranches.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Job role and at least one branch are required.");
                    return;
                }

                Drive drive = new Drive(0, companyId, jobRole, packageLpa, minCgpa, maxBacklogs, driveDate, status);
                drive.setEligibleBranches(selectedBranches);

                boolean created = driveDAO.addDrive(drive);
                if (created) {
                    JOptionPane.showMessageDialog(this, "Drive added successfully!");
                    loadDrives();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add drive.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for package, CGPA, and backlogs.");
            }
        }
    }

    // ================= APPLICATIONS TAB =================
    private JPanel buildApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"App ID", "Student", "Company", "Job Role", "Status", "Applied On"};
        applicationsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        applicationsTable = new JTable(applicationsTableModel);
        panel.add(new JScrollPane(applicationsTable), BorderLayout.CENTER);

        JButton updateButton = new JButton("Update Status");
        updateButton.addActionListener(e -> updateSelectedApplicationStatus());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(updateButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadApplications() {
        applicationsTableModel.setRowCount(0);

        for (Application app : applicationDAO.getAllApplications()) {
            Student s = studentDAO.getStudentById(app.getStudentId());
            Drive d = driveDAO.getDriveById(app.getDriveId());

            String studentName = (s != null) ? s.getName() : "Unknown";
            String companyName = "Unknown";
            String jobRole = "Unknown";

            if (d != null) {
                Company c = companyDAO.getCompanyById(d.getCompanyId());
                companyName = (c != null) ? c.getName() : "Unknown";
                jobRole = d.getJobRole();
            }

            applicationsTableModel.addRow(new Object[]{
                    app.getApplicationId(), studentName, companyName, jobRole,
                    app.getStatus(), app.getApplicationDate()
            });
        }
    }

    private void updateSelectedApplicationStatus() {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an application first.");
            return;
        }

        int applicationId = (int) applicationsTableModel.getValueAt(selectedRow, 0);

        String newStatus = (String) JOptionPane.showInputDialog(this, "Select new status:",
                "Update Application Status", JOptionPane.PLAIN_MESSAGE, null,
                STATUS_OPTIONS, STATUS_OPTIONS[0]);

        if (newStatus != null) {
            boolean updated = applicationDAO.updateStatus(applicationId, newStatus);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Status updated successfully!");
                loadApplications();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update status.");
            }
        }
    }
}