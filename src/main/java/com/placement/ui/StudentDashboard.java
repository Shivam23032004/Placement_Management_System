package com.placement.ui;

import com.placement.dao.ApplicationDAO;
import com.placement.dao.CompanyDAO;
import com.placement.dao.DriveDAO;
import com.placement.dao.StudentDAO;
import com.placement.dao.ApplicationHistoryDAO;
import com.placement.dsa.StatusHistoryLinkedList;
import com.placement.model.StatusHistoryEntry;
import com.placement.dsa.EligibilitySorter;
import com.placement.model.Application;
import com.placement.model.Company;
import com.placement.model.Drive;
import com.placement.model.Student;
import com.placement.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private Student student;
    private DriveDAO driveDAO;
    private CompanyDAO companyDAO;
    private ApplicationDAO applicationDAO;
    private ApplicationHistoryDAO historyDAO;
    private EligibilitySorter sorter;

    private DefaultTableModel drivesTableModel;
    private JTable drivesTable;

    private DefaultTableModel applicationsTableModel;
    private JTable applicationsTable;

    public StudentDashboard(User user) {
        StudentDAO studentDAO = new StudentDAO();
        this.student = studentDAO.getStudentByUserId(user.getUserId());

        driveDAO = new DriveDAO();
        companyDAO = new CompanyDAO();
        applicationDAO = new ApplicationDAO();
        historyDAO = new ApplicationHistoryDAO();
        sorter = new EligibilitySorter();

        if (this.student == null) {
            JOptionPane.showMessageDialog(null,
                "Student record not found for this user. Please contact admin.",
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setTitle("Student Dashboard - " + student.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Default full screen
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel (Premium Deep Slate)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate 900
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel statsLabel = new JLabel("Branch: " + student.getBranch() + "   •   CGPA: " + student.getCgpa());
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(new Color(148, 163, 184)); // Slate 400
        
        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 2, 2));
        titleContainer.setOpaque(false);
        titleContainer.add(welcomeLabel);
        titleContainer.add(statsLabel);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(239, 68, 68)); // Coral red
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setPreferredSize(new Dimension(85, 30));
        logoutBtn.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        
        headerPanel.add(titleContainer, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Available Drives", buildDrivesPanel());
        tabbedPane.addTab("My Applications", buildApplicationsPanel());
        add(tabbedPane, BorderLayout.CENTER);

        loadEligibleDrives();
        loadMyApplications();
    }

    private JPanel buildDrivesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Drive ID", "Company", "Job Role", "Package (LPA)", "Min CGPA", "Status"};
        drivesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        drivesTable = new JTable(drivesTableModel);
        panel.add(new JScrollPane(drivesTable), BorderLayout.CENTER);

        JButton applyButton = new JButton("Apply to Selected Drive");
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyButton.addActionListener(e -> applyToSelectedDrive());
 
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(applyButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
 
        return panel;
    }
 
    private JPanel buildApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
 
        String[] columns = {"Application ID", "Company", "Job Role", "Status", "Applied On"};
        applicationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        applicationsTable = new JTable(applicationsTableModel);
        panel.add(new JScrollPane(applicationsTable), BorderLayout.CENTER);
 
        JButton historyButton = new JButton("View Status History");
        historyButton.setBackground(new Color(13, 148, 136)); // Teal 600
        historyButton.setForeground(Color.WHITE);
        historyButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyButton.addActionListener(e -> viewSelectedApplicationHistory());
 
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(historyButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void viewSelectedApplicationHistory() {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an application first.");
            return;
        }

        int applicationId = (int) applicationsTableModel.getValueAt(selectedRow, 0);

        StatusHistoryLinkedList timeline = new StatusHistoryLinkedList();
        for (StatusHistoryEntry entry : historyDAO.getHistoryForApplication(applicationId)) {
            timeline.addStatusChange(entry);
        }

        List<String> steps = timeline.getTimeline();
        String message = steps.isEmpty() ? "No history found." : String.join("\n", steps);

        JOptionPane.showMessageDialog(this, message, "Status Timeline", JOptionPane.INFORMATION_MESSAGE);
    }

    // Saari drives lao, sirf eligible wali dikhao (DSA layer ka EligibilitySorter yahan use ho raha hai)
    private void loadEligibleDrives() {
        drivesTableModel.setRowCount(0);

        List<Drive> allDrives = driveDAO.getAllDrives();

        for (Drive drive : allDrives) {
            if (sorter.isEligible(student, drive)) {
                Company company = companyDAO.getCompanyById(drive.getCompanyId());
                String companyName = (company != null) ? company.getName() : "Unknown";

                drivesTableModel.addRow(new Object[]{
                        drive.getDriveId(),
                        companyName,
                        drive.getJobRole(),
                        drive.getPackageLpa(),
                        drive.getMinCgpa(),
                        drive.getStatus()
                });
            }
        }
    }

    // Student ki apni applications load karo
    private void loadMyApplications() {
        applicationsTableModel.setRowCount(0);

        List<Application> myApplications = applicationDAO.getApplicationsByStudent(student.getStudentId());

        for (Application app : myApplications) {
            Drive drive = driveDAO.getDriveById(app.getDriveId());
            String companyName = "Unknown";
            String jobRole = "Unknown";

            if (drive != null) {
                Company company = companyDAO.getCompanyById(drive.getCompanyId());
                companyName = (company != null) ? company.getName() : "Unknown";
                jobRole = drive.getJobRole();
            }

            applicationsTableModel.addRow(new Object[]{
                    app.getApplicationId(),
                    companyName,
                    jobRole,
                    app.getStatus(),
                    app.getApplicationDate()
            });
        }
    }

private void applyToSelectedDrive() {
        int selectedRow = drivesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a drive first.");
            return;
        }

        int driveId = (int) drivesTableModel.getValueAt(selectedRow, 0);

        int newApplicationId = applicationDAO.applyToDrive(student.getStudentId(), driveId);

        if (newApplicationId != -1) {
            historyDAO.addHistoryEntry(newApplicationId, "applied");
            JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            loadMyApplications();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not apply. You may have already applied to this drive.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}