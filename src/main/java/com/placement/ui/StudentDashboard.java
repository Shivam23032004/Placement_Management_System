package com.placement.ui;

import com.placement.dao.ApplicationDAO;
import com.placement.dao.CompanyDAO;
import com.placement.dao.DriveDAO;
import com.placement.dao.StudentDAO;
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
        sorter = new EligibilitySorter();

        setTitle("Student Dashboard - " + student.getName());
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + student.getName() +
                " | Branch: " + student.getBranch() + " | CGPA: " + student.getCgpa());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(welcomeLabel, BorderLayout.NORTH);

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

        return panel;
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

        boolean success = applicationDAO.applyToDrive(student.getStudentId(), driveId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            loadMyApplications();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not apply. You may have already applied to this drive.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}