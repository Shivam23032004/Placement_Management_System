package com.placement.ui;

import com.placement.dao.ApplicationDAO;
import com.placement.dao.CompanyDAO;
import com.placement.dao.DriveDAO;
import com.placement.dao.StudentDAO;
import com.placement.dsa.InterviewPriorityQueue;
import com.placement.model.Application;
import com.placement.model.Company;
import com.placement.model.Drive;
import com.placement.model.Student;
import com.placement.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecruiterDashboard extends JFrame {

    private Company company;
    private CompanyDAO companyDAO;
    private DriveDAO driveDAO;
    private ApplicationDAO applicationDAO;
    private StudentDAO studentDAO;
    private com.placement.dao.ApplicationHistoryDAO historyDAO;

    private DefaultTableModel drivesTableModel;
    private JTable drivesTable;

    private JComboBox<String> candidatesDriveBox;
    private DefaultTableModel candidatesTableModel;
    private JTable candidatesTable;

    private JComboBox<String> queueDriveBox;
    private DefaultTableModel queueTableModel;
    private JTable queueTable;

    private InterviewPriorityQueue currentQueue;
    private List<Application> currentDriveApplications;

    private static final String[] STATUS_OPTIONS = {"applied", "shortlisted", "interview_scheduled", "selected", "rejected"};

    public RecruiterDashboard(User recruiterUser) {
        companyDAO = new CompanyDAO();
        driveDAO = new DriveDAO();
        applicationDAO = new ApplicationDAO();
        studentDAO = new StudentDAO();
        historyDAO = new com.placement.dao.ApplicationHistoryDAO();

        this.company = companyDAO.getCompanyByUserId(recruiterUser.getUserId());

        if (this.company == null) {
            JOptionPane.showMessageDialog(null,
                "Company record not found for this recruiter. Please contact admin.",
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setTitle("Recruiter Dashboard - " + company.getName());
        setSize(850, 550); // slightly larger
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel (Premium Deep Slate)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate 900
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        
        JLabel welcomeLabel = new JLabel("Recruiter Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel statsLabel = new JLabel("Company: " + company.getName() + "   •   Website: " + company.getWebsite());
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
        tabbedPane.addTab("My Drives", buildDrivesPanel());
        tabbedPane.addTab("Candidates", buildCandidatesPanel());
        tabbedPane.addTab("Interview Queue", buildInterviewQueuePanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadDrives();
        refreshDriveDropdowns();
    }

    // ================= MY DRIVES TAB =================
    private JPanel buildDrivesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Job Role", "Package", "Min CGPA", "Max Backlogs", "Date", "Status", "Branches"};
        drivesTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        drivesTable = new JTable(drivesTableModel);
        panel.add(new JScrollPane(drivesTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadDrives() {
        drivesTableModel.setRowCount(0);
        for (Drive d : driveDAO.getDrivesByCompany(company.getCompanyId())) {
            drivesTableModel.addRow(new Object[]{
                    d.getDriveId(), d.getJobRole(), d.getPackageLpa(), d.getMinCgpa(),
                    d.getMaxBacklogs(), d.getDriveDate(), d.getStatus(), d.getEligibleBranches()
            });
        }
    }

    // ================= CANDIDATES TAB =================
    private JPanel buildCandidatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Drive:"));
        candidatesDriveBox = new JComboBox<>();
        topPanel.add(candidatesDriveBox);

        JButton loadButton = new JButton("Load Candidates");
        loadButton.addActionListener(e -> loadCandidates());
        topPanel.add(loadButton);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"App ID", "Student ID", "Name", "Branch", "CGPA", "Backlogs", "Status"};
        candidatesTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        candidatesTable = new JTable(candidatesTableModel);
        panel.add(new JScrollPane(candidatesTable), BorderLayout.CENTER);

        JButton updateButton = new JButton("Update Status");
        updateButton.setBackground(new Color(13, 148, 136)); // Teal 600
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateButton.addActionListener(e -> updateSelectedCandidateStatus());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(updateButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCandidates() {
        int driveId = getSelectedDriveId(candidatesDriveBox);
        if (driveId == -1) return;

        candidatesTableModel.setRowCount(0);

        List<Application> apps = applicationDAO.getApplicationsByDrive(driveId);
        for (Application app : apps) {
            Student s = studentDAO.getStudentById(app.getStudentId());
            if (s != null) {
                candidatesTableModel.addRow(new Object[]{
                        app.getApplicationId(), s.getStudentId(), s.getName(), s.getBranch(),
                        s.getCgpa(), s.getBacklogCount(), app.getStatus()
                });
            }
        }
    }

    private void updateSelectedCandidateStatus() {
        int selectedRow = candidatesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate first.");
            return;
        }

        int applicationId = (int) candidatesTableModel.getValueAt(selectedRow, 0);

        String newStatus = (String) JOptionPane.showInputDialog(this, "Select new status:",
                "Update Candidate Status", JOptionPane.PLAIN_MESSAGE, null,
                STATUS_OPTIONS, STATUS_OPTIONS[0]);

        if (newStatus != null) {
            boolean updated = applicationDAO.updateStatus(applicationId, newStatus);
            if (updated) {
                historyDAO.addHistoryEntry(applicationId, newStatus);
                JOptionPane.showMessageDialog(this, "Status updated successfully!");
                loadCandidates();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update status.");
            }
        }
    }

    // ================= INTERVIEW QUEUE TAB =================
    private JPanel buildInterviewQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Drive:"));
        queueDriveBox = new JComboBox<>();
        topPanel.add(queueDriveBox);

        JButton buildButton = new JButton("Build Queue (Shortlisted Only)");
        buildButton.setBackground(new Color(147, 51, 234)); // Purple 600
        buildButton.setForeground(Color.WHITE);
        buildButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        buildButton.addActionListener(e -> buildInterviewQueue());
        topPanel.add(buildButton);

        JButton callNextButton = new JButton("Call Next for Interview");
        callNextButton.setBackground(new Color(16, 185, 129)); // Emerald Green 500
        callNextButton.setForeground(Color.WHITE);
        callNextButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        callNextButton.addActionListener(e -> callNextForInterview());
        topPanel.add(callNextButton);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Position", "Student ID", "Name", "CGPA", "Branch"};
        queueTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        queueTable = new JTable(queueTableModel);
        panel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        return panel;
    }

    // Priority Queue banao sirf shortlisted candidates se, CGPA ke hisaab se order karke
    private void buildInterviewQueue() {
        int driveId = getSelectedDriveId(queueDriveBox);
        if (driveId == -1) return;

        currentDriveApplications = applicationDAO.getApplicationsByDrive(driveId);
        currentQueue = new InterviewPriorityQueue();

        for (Application app : currentDriveApplications) {
            if (app.getStatus().equals("shortlisted")) {
                Student s = studentDAO.getStudentById(app.getStudentId());
                if (s != null) {
                    currentQueue.addToQueue(s);
                }
            }
        }

        if (currentQueue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No shortlisted candidates found for this drive.");
        }

        refreshQueueTable();
    }

    // Queue mein se sabse high-priority candidate ko interview ke liye bulao
    private void callNextForInterview() {
        if (currentQueue == null || currentQueue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Queue is empty. Build the queue first.");
            return;
        }

        Student next = currentQueue.getNextForInterview();

        // Uski application status ko "interview_scheduled" mein update karo
        for (Application app : currentDriveApplications) {
            if (app.getStudentId() == next.getStudentId() && app.getStatus().equals("shortlisted")) {
                applicationDAO.updateStatus(app.getApplicationId(), "interview_scheduled");
                historyDAO.addHistoryEntry(app.getApplicationId(), "interview_scheduled");
                break;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Calling for interview: " + next.getName() + " (CGPA: " + next.getCgpa() + ")");

        refreshQueueTable();
    }

    private void refreshQueueTable() {
        queueTableModel.setRowCount(0);

        if (currentQueue == null) return;

        List<Student> ordered = currentQueue.viewFullOrder();
        int position = 1;
        for (Student s : ordered) {
            queueTableModel.addRow(new Object[]{
                    position++, s.getStudentId(), s.getName(), s.getCgpa(), s.getBranch()
            });
        }
    }

    // ================= HELPERS =================
    private void refreshDriveDropdowns() {
        candidatesDriveBox.removeAllItems();
        queueDriveBox.removeAllItems();

        for (Drive d : driveDAO.getDrivesByCompany(company.getCompanyId())) {
            String item = d.getDriveId() + " - " + d.getJobRole();
            candidatesDriveBox.addItem(item);
            queueDriveBox.addItem(item);
        }
    }

    private int getSelectedDriveId(JComboBox<String> box) {
        String selected = (String) box.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No drive available. Please select a drive.");
            return -1;
        }
        return Integer.parseInt(selected.split(" - ")[0]);
    }
}