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

        this.company = companyDAO.getCompanyByUserId(recruiterUser.getUserId());

        setTitle("Recruiter Dashboard - " + company.getName());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Drives", buildDrivesPanel());
        tabbedPane.addTab("Candidates", buildCandidatesPanel());
        tabbedPane.addTab("Interview Queue", buildInterviewQueuePanel());

        add(tabbedPane);

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
        buildButton.addActionListener(e -> buildInterviewQueue());
        topPanel.add(buildButton);

        JButton callNextButton = new JButton("Call Next for Interview");
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