package com.placement.ui;

import com.placement.dao.ApplicationDAO;
import com.placement.dao.ApplicationHistoryDAO;
import com.placement.dao.ApplicationQueueDAO;
import com.placement.dao.CompanyDAO;
import com.placement.dao.DriveDAO;
import com.placement.dao.StudentDAO;
import com.placement.dao.UserDAO;
import com.placement.dsa.ApplicationQueue;
import com.placement.dsa.CompanyTrie;
import com.placement.dsa.DriveNavigator;
import com.placement.dsa.MergeSortUtil;
import com.placement.dsa.PlacementGraph;
import com.placement.dsa.StatusHistoryLinkedList;
import com.placement.dsa.StudentBST;
import com.placement.dsa.StudentHashIndex;
import com.placement.dsa.UndoAction;
import com.placement.dsa.UndoStack;
import com.placement.model.Application;
import com.placement.model.Company;
import com.placement.model.Drive;
import com.placement.model.StatusHistoryEntry;
import com.placement.model.Student;
import com.placement.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Admin Dashboard — wires together all DSA implementations.
 *
 * DSA Summary (for viva / report):
 *
 *  Shashank:
 *   - Stack<UndoAction>    : undo last status change
 *   - Queue (ApplicationQueue + DB) : pending application FIFO queue
 *   - Doubly Linked List (DriveNavigator) : bidirectional drive browsing
 *
 *  Shivam:
 *   - BST (StudentBST)     : CGPA range search, insert, delete, height
 *   - Trie (CompanyTrie)   : company name prefix search (autocomplete)
 *
 *  Ayush:
 *   - HashMap + HashSet (StudentHashIndex) : O(1) student lookup by ID / email
 *   - Merge Sort (MergeSortUtil)           : O(n log n) custom divide & conquer sort
 *   - Graph + BFS (PlacementGraph)         : placement network traversal
 */
public class AdminDashboard extends JFrame {

    // ========== DAOs ==========
    private final StudentDAO          studentDAO;
    private final CompanyDAO          companyDAO;
    private final DriveDAO            driveDAO;
    private final ApplicationDAO      applicationDAO;
    private final UserDAO             userDAO;
    private final ApplicationHistoryDAO historyDAO;
    private final ApplicationQueueDAO queueDAO;       // Shashank — Queue persistence

    // ========== DSA Layer ==========
    private       StudentBST          studentBST;     // Shivam   — BST (CGPA)
    private final UndoStack<UndoAction> undoStack;    // Shashank — Stack (undo)
    private       StudentHashIndex    hashIndex;      // Ayush    — HashMap O(1)
    private       CompanyTrie         companyTrie;    // Shivam   — Trie (prefix)
    private       DriveNavigator      driveNavigator; // Shashank — Doubly Linked List
    private       ApplicationQueue    appQueue;       // Shashank — Queue (FIFO, DB-backed)
    private       PlacementGraph      placementGraph; // Ayush    — Graph + BFS

    // ========== UI Components ==========
    private DefaultTableModel studentsTableModel;
    private JTable            studentsTable;
    private JTextField        minCgpaField, maxCgpaField;
    private JLabel            bstStatsLabel;

    private DefaultTableModel companiesTableModel;
    private JTable            companiesTable;
    private JTextField        trieSearchField;

    private DefaultTableModel drivesTableModel;
    private JTable            drivesTable;

    private DefaultTableModel applicationsTableModel;
    private JTable            applicationsTable;
    private List<Application> currentApplicationsList = new ArrayList<>();

    // ========== Constants ==========
    private static final String[] BRANCH_OPTIONS = {"CSE", "IT", "ECE", "ME", "CE", "EE"};
    private static final String[] STATUS_OPTIONS  =
        {"applied", "shortlisted", "interview_scheduled", "selected", "rejected"};

    // =====================================================================
    public AdminDashboard(User adminUser) {
        // Initialize DAOs
        studentDAO     = new StudentDAO();
        companyDAO     = new CompanyDAO();
        driveDAO       = new DriveDAO();
        applicationDAO = new ApplicationDAO();
        userDAO        = new UserDAO();
        historyDAO     = new ApplicationHistoryDAO();
        queueDAO       = new ApplicationQueueDAO();    // Shashank

        // Initialize DSA structures
        studentBST     = new StudentBST();             // Shivam
        undoStack      = new UndoStack<>();             // Shashank
        hashIndex      = new StudentHashIndex();        // Ayush
        companyTrie    = new CompanyTrie();             // Shivam
        driveNavigator = new DriveNavigator();          // Shashank
        appQueue       = new ApplicationQueue();        // Shashank
        placementGraph = new PlacementGraph();          // Ayush

        setTitle("Admin Dashboard — " + adminUser.getUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Default full screen
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel (Premium Deep Slate)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate 900
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        
        JLabel welcomeLabel = new JLabel("Admin Control Center");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel statsLabel = new JLabel("Admin Profile: " + adminUser.getUsername() + "   •   System Mode: Multi-DSA Integrated Platform");
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

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard",    buildOverviewPanel());
        tabs.addTab("Students",     buildStudentsPanel());
        tabs.addTab("Companies",    buildCompaniesPanel());
        tabs.addTab("Drives",       buildDrivesPanel());
        tabs.addTab("Applications", buildApplicationsPanel());
        add(tabs, BorderLayout.CENTER);

        loadStudents();
        loadCompanies();
        loadDrives();
        loadApplications();
    }

    // =====================================================================
    // STUDENTS TAB
    // DSA: BST (Shivam) + HashMap (Ayush) + Merge Sort (Ayush)
    // =====================================================================
    private JPanel buildStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Name", "Branch", "CGPA", "Backlogs", "Email"};
        studentsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentsTable = new JTable(studentsTableModel);
        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        // Stats label
        bstStatsLabel = new JLabel(" ");
        bstStatsLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        bstStatsLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        // ---- Row 1: Search Filters ----
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBorder(BorderFactory.createTitledBorder("Search Filters"));
        row1.add(new JLabel("CGPA Range: Min"));
        minCgpaField = new JTextField(4);
        row1.add(minCgpaField);
        row1.add(new JLabel("Max"));
        maxCgpaField = new JTextField(4);
        row1.add(maxCgpaField);
        
        JButton bstSearchBtn = new JButton("CGPA Range Search");
        bstSearchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bstSearchBtn.addActionListener(e -> searchStudentsByCgpaRange());
        row1.add(bstSearchBtn);
        
        JButton showAllBtn = new JButton("Show All");
        showAllBtn.setBackground(new Color(100, 116, 139)); // Slate 500
        showAllBtn.setForeground(Color.WHITE);
        showAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        showAllBtn.addActionListener(e -> loadStudents());
        row1.add(showAllBtn);
        
        JButton bstDeleteBtn = new JButton("Remove Filter Node");
        bstDeleteBtn.setBackground(new Color(239, 68, 68)); // Red 500
        bstDeleteBtn.setForeground(Color.WHITE);
        bstDeleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bstDeleteBtn.addActionListener(e -> deleteSelectedFromBst());
        row1.add(bstDeleteBtn);

        // ---- Row 2: Leaderboard Options ----
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBorder(BorderFactory.createTitledBorder("Leaderboard Options"));
        
        JButton mergeSortBtn = new JButton("Sort by CGPA ▼");
        mergeSortBtn.setBackground(new Color(147, 51, 234)); // Purple 600
        mergeSortBtn.setForeground(Color.WHITE);
        mergeSortBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mergeSortBtn.addActionListener(e -> mergeSortStudents());
        row2.add(mergeSortBtn);
        
        JButton hashLookupBtn = new JButton("Find Student by ID");
        hashLookupBtn.setBackground(new Color(13, 148, 136)); // Teal 600
        hashLookupBtn.setForeground(Color.WHITE);
        hashLookupBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hashLookupBtn.addActionListener(e -> findStudentByIdHash());
        row2.add(hashLookupBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(bstStatsLabel, BorderLayout.NORTH);
        JPanel rows = new JPanel(new GridLayout(2, 1));
        rows.add(row1);
        rows.add(row2);
        south.add(rows, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void loadStudents() {
        List<Student> all = studentDAO.getAllStudents();

        // BST rebuild (Shivam)
        studentBST = new StudentBST();
        for (Student s : all) studentBST.insert(s);

        // Hash Index rebuild (Ayush)
        hashIndex = new StudentHashIndex();
        hashIndex.buildIndex(all);

        populateStudentsTable(studentBST.getAllSortedByCgpa());
        updateBstStatsLabel();
    }

    private void updateBstStatsLabel() {
        bstStatsLabel.setText(
            "  Database Index Status: Active | Total Records: " + hashIndex.totalStudents() + " registered students."
        );
    }

    /** Shivam — BST range search */
    private void searchStudentsByCgpaRange() {
        try {
            double min = Double.parseDouble(minCgpaField.getText().trim());
            double max = Double.parseDouble(maxCgpaField.getText().trim());
            List<Student> result = studentBST.rangeSearch(min, max);
            populateStudentsTable(result);
            bstStatsLabel.setText("  Filtered Results for CGPA [" + min + " - " + max
                + "]  →  " + result.size() + " matches found.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid CGPA numbers.");
        }
    }

    /** Shivam — BST deletion (3-case algorithm) */
    private void deleteSelectedFromBst() {
        int row = studentsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a student."); return; }
        double cgpa = (double) studentsTableModel.getValueAt(row, 3);
        String name = (String) studentsTableModel.getValueAt(row, 1);
        int ok = JOptionPane.showConfirmDialog(this,
            "Temporarily hide \"" + name + "\" (CGPA " + cgpa + ") from active session?\n"
            + "Note: This will not delete the student from the database permanent records.",
            "Confirm Temporary De-index", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            studentBST.deleteStudent(cgpa);
            populateStudentsTable(studentBST.getAllSortedByCgpa());
            JOptionPane.showMessageDialog(this,
                "\"" + name + "\" removed from current session list.");
            updateBstStatsLabel();
        }
    }

    /** Ayush — Merge Sort (custom divide & conquer, O(n log n)) */
    private void mergeSortStudents() {
        List<Student> all = studentDAO.getAllStudents();
        List<Student> sorted = MergeSortUtil.mergeSort(all,
            Comparator.comparingDouble(Student::getCgpa).reversed());
        populateStudentsTable(sorted);
        JOptionPane.showMessageDialog(this,
            "Leaderboard generation complete!\n"
            + "────────────────────────────\n"
            + "Total items ordered: " + all.size() + "\n"
            + "Order: CGPA Descending\n"
            + "System State: Optimal",
            "Leaderboard Status", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Ayush — HashMap O(1) lookup by student ID */
    private void findStudentByIdHash() {
        String input = JOptionPane.showInputDialog(this,
            "Enter Student ID  (HashMap lookup — O(1) average):");
        if (input == null || input.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(input.trim());
            long t0 = System.nanoTime();
            Student s = hashIndex.findById(id);
            long ns = System.nanoTime() - t0;
            if (s != null) {
                populateStudentsTable(List.of(s));
                JOptionPane.showMessageDialog(this,
                    "Found: " + s.getName()
                    + "\nBranch: " + s.getBranch()
                    + "   CGPA: " + s.getCgpa()
                    + "\n────────────────────────────\n"
                    + "Search Execution Speed: " + (ns / 1000.0) + " microseconds",
                    "Record Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No student found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer ID.");
        }
    }

    private void populateStudentsTable(List<Student> list) {
        studentsTableModel.setRowCount(0);
        for (Student s : list) {
            studentsTableModel.addRow(new Object[]{
                s.getStudentId(), s.getName(), s.getBranch(),
                s.getCgpa(), s.getBacklogCount(), s.getEmail()
            });
        }
    }

    // =====================================================================
    // COMPANIES TAB
    // DSA: Trie (Shivam)
    // =====================================================================
    private JPanel buildCompaniesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Name", "Description", "Website"};
        companiesTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        companiesTable = new JTable(companiesTableModel);
        panel.add(new JScrollPane(companiesTable), BorderLayout.CENTER);

        // ---- Company Filter ----
        JPanel triePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        triePanel.setBorder(BorderFactory.createTitledBorder("Quick Company Search"));
        triePanel.add(new JLabel("Company name prefix:"));
        trieSearchField = new JTextField(14);
        triePanel.add(trieSearchField);
        JButton trieSearchBtn = new JButton("Search");
        trieSearchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        trieSearchBtn.addActionListener(e -> searchCompaniesByPrefix(trieSearchField.getText()));
        triePanel.add(trieSearchBtn);
        
        JButton trieResetBtn = new JButton("Show All");
        trieResetBtn.setBackground(new Color(100, 116, 139)); // Slate 500
        trieResetBtn.setForeground(Color.WHITE);
        trieResetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        trieResetBtn.addActionListener(e -> { trieSearchField.setText(""); loadCompanies(); });
        triePanel.add(trieResetBtn);

        // ---- Add Company ----
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Company");
        addBtn.setBackground(new Color(16, 185, 129)); // Emerald Green 500
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> showAddCompanyDialog());
        addPanel.add(addBtn);

        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(triePanel);
        south.add(addPanel);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCompanies() {
        companiesTableModel.setRowCount(0);
        companyTrie = new CompanyTrie(); // Shivam — rebuild trie
        for (Company c : companyDAO.getAllCompanies()) {
            companyTrie.insert(c.getName());
            companiesTableModel.addRow(new Object[]{
                c.getCompanyId(), c.getName(), c.getDescription(), c.getWebsite()
            });
        }
    }

    /** Shivam — Trie prefix search: O(m + k) */
    private void searchCompaniesByPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) { loadCompanies(); return; }
        List<String> matches = companyTrie.searchByPrefix(prefix.trim());
        companiesTableModel.setRowCount(0);
        for (Company c : companyDAO.getAllCompanies()) {
            if (matches.contains(c.getName())) {
                companiesTableModel.addRow(new Object[]{
                    c.getCompanyId(), c.getName(), c.getDescription(), c.getWebsite()
                });
            }
        }
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No companies found with prefix: \"" + prefix + "\"");
        }
    }

    private void showAddCompanyDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField    = new JTextField();
        JTextField descField    = new JTextField();
        JTextField websiteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Recruiter Username:")); panel.add(usernameField);
        panel.add(new JLabel("Recruiter Password:")); panel.add(passwordField);
        panel.add(new JLabel("Company Name:"));       panel.add(nameField);
        panel.add(new JLabel("Description:"));         panel.add(descField);
        panel.add(new JLabel("Website:"));             panel.add(websiteField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Add New Company",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String username    = usernameField.getText().trim();
        String password    = new String(passwordField.getPassword());
        String name        = nameField.getText().trim();
        String description = descField.getText().trim();
        String website     = websiteField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, password, and company name are required.");
            return;
        }
        if (!userDAO.registerUser(username, password, "recruiter")) {
            JOptionPane.showMessageDialog(this, "Failed to create recruiter login (username may already exist).");
            return;
        }
        User newUser = userDAO.authenticate(username, password);
        if (newUser == null) {
            JOptionPane.showMessageDialog(this, "Recruiter created but could not be linked.");
            return;
        }
        boolean ok = companyDAO.addCompany(new Company(0, newUser.getUserId(), name, description, website));
        if (ok) { JOptionPane.showMessageDialog(this, "Company added!"); loadCompanies(); }
        else      JOptionPane.showMessageDialog(this, "Failed to add company.");
    }

    // =====================================================================
    // DRIVES TAB
    // DSA: Doubly Linked List — DriveNavigator (Shashank)
    // =====================================================================
    private JPanel buildDrivesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Company", "Job Role", "Package", "Min CGPA",
                          "Max Backlogs", "Date", "Status", "Branches"};
        drivesTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        drivesTable = new JTable(drivesTableModel);
        drivesTable.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());
        panel.add(new JScrollPane(drivesTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addDriveBtn = new JButton("Add Drive");
        addDriveBtn.setBackground(new Color(16, 185, 129)); // Emerald Green 500
        addDriveBtn.setForeground(Color.WHITE);
        addDriveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addDriveBtn.addActionListener(e -> showAddDriveDialog());
        
        JButton dllBtn = new JButton("Browse Drives Sequence");
        dllBtn.setBackground(new Color(79, 70, 229)); // Primary Indigo
        dllBtn.setForeground(Color.WHITE);
        dllBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dllBtn.addActionListener(e -> showDriveNavigatorDialog());
        
        bottom.add(addDriveBtn);
        bottom.add(dllBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadDrives() {
        drivesTableModel.setRowCount(0);
        List<Drive> all = driveDAO.getAllDrives();

        driveNavigator.loadDrives(all); // Shashank — load into DLL

        for (Drive d : all) {
            Company c = companyDAO.getCompanyById(d.getCompanyId());
            drivesTableModel.addRow(new Object[]{
                d.getDriveId(), (c != null ? c.getName() : "Unknown"),
                d.getJobRole(), d.getPackageLpa(), d.getMinCgpa(),
                d.getMaxBacklogs(), d.getDriveDate(), d.getStatus(), d.getEligibleBranches()
            });
        }
    }

    /** Shashank — Doubly Linked List navigator dialog */
    private void showDriveNavigatorDialog() {
        if (driveNavigator.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No drives available.");
            return;
        }
        driveNavigator.reset();

        JDialog dlg = new JDialog(this,
            "Drive Navigator", true);
        dlg.setSize(500, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(6, 6));

        JLabel headerLabel = new JLabel("", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        JTextArea detail = new JTextArea();
        detail.setEditable(false);
        detail.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detail.setMargin(new Insets(8, 10, 8, 10));

        JButton prevBtn = new JButton("← Prev Node");
        JButton nextBtn = new JButton("Next Node →");

        // Runnable to refresh display whenever node changes
        Runnable refresh = () -> {
            Drive d = driveNavigator.getCurrent();
            if (d == null) return;
            Company c = companyDAO.getCompanyById(d.getCompanyId());
            headerLabel.setText("[ ←  prev  |  current node  |  next  → ]     "
                + "Total drives available: " + driveNavigator.size());
            detail.setText(
                "Drive ID      : " + d.getDriveId()   + "\n"
              + "Company       : " + (c != null ? c.getName() : "Unknown") + "\n"
              + "Job Role      : " + d.getJobRole()   + "\n"
              + "Package       : " + d.getPackageLpa() + " LPA\n"
              + "Min CGPA      : " + d.getMinCgpa()   + "\n"
              + "Max Backlogs  : " + d.getMaxBacklogs() + "\n"
              + "Date          : " + d.getDriveDate() + "\n"
              + "Status        : " + d.getStatus()    + "\n"
              + "Branches      : " + d.getEligibleBranches()
            );
            prevBtn.setEnabled(driveNavigator.hasPrev());
            nextBtn.setEnabled(driveNavigator.hasNext());
        };

        prevBtn.addActionListener(e -> { driveNavigator.getPrev(); refresh.run(); });
        nextBtn.addActionListener(e -> { driveNavigator.getNext(); refresh.run(); });

        refresh.run(); // show first node immediately

        JPanel navRow = new JPanel();
        navRow.add(prevBtn);
        navRow.add(Box.createHorizontalStrut(20));
        navRow.add(new JLabel("← Traverse →"));
        navRow.add(Box.createHorizontalStrut(20));
        navRow.add(nextBtn);

        dlg.add(headerLabel, BorderLayout.NORTH);
        dlg.add(new JScrollPane(detail), BorderLayout.CENTER);
        dlg.add(navRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showAddDriveDialog() {
        List<Company> companies = companyDAO.getAllCompanies();
        if (companies.isEmpty()) { JOptionPane.showMessageDialog(this, "Add a company first."); return; }

        JComboBox<String> companyBox = new JComboBox<>();
        for (Company c : companies) companyBox.addItem(c.getCompanyId() + " - " + c.getName());

        JTextField jobRoleField     = new JTextField();
        JTextField packageField     = new JTextField();
        JTextField minCgpaFieldD    = new JTextField();
        JTextField maxBacklogsField = new JTextField();
        JTextField dateField        = new JTextField("2026-08-15");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"upcoming","ongoing","completed"});

        JPanel branchPanel = new JPanel(new GridLayout(0, 3));
        JCheckBox[] checks = new JCheckBox[BRANCH_OPTIONS.length];
        for (int i = 0; i < BRANCH_OPTIONS.length; i++) {
            checks[i] = new JCheckBox(BRANCH_OPTIONS[i]);
            branchPanel.add(checks[i]);
        }
        JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
        p.add(new JLabel("Company:"));        p.add(companyBox);
        p.add(new JLabel("Job Role:"));       p.add(jobRoleField);
        p.add(new JLabel("Package (LPA):"));  p.add(packageField);
        p.add(new JLabel("Min CGPA:"));       p.add(minCgpaFieldD);
        p.add(new JLabel("Max Backlogs:"));   p.add(maxBacklogsField);
        p.add(new JLabel("Date (yyyy-mm-dd):")); p.add(dateField);
        p.add(new JLabel("Status:"));         p.add(statusBox);
        p.add(new JLabel("Eligible Branches:")); p.add(branchPanel);

        int res = JOptionPane.showConfirmDialog(this, p, "Add New Drive",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            int    companyId   = Integer.parseInt(((String) companyBox.getSelectedItem()).split(" - ")[0]);
            String jobRole     = jobRoleField.getText().trim();
            double pkgLpa      = Double.parseDouble(packageField.getText().trim());
            double minCgpa     = Double.parseDouble(minCgpaFieldD.getText().trim());
            int    maxBacklogs = Integer.parseInt(maxBacklogsField.getText().trim());
            String driveDate   = dateField.getText().trim();
            String status      = (String) statusBox.getSelectedItem();

            List<String> branches = new ArrayList<>();
            for (JCheckBox cb : checks) if (cb.isSelected()) branches.add(cb.getText());
            if (jobRole.isEmpty() || branches.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Job role and at least one branch are required.");
                return;
            }
            Drive drive = new Drive(0, companyId, jobRole, pkgLpa, minCgpa, maxBacklogs, driveDate, status);
            drive.setEligibleBranches(branches);
            if (driveDAO.addDrive(drive)) { JOptionPane.showMessageDialog(this, "Drive added!"); loadDrives(); }
            else JOptionPane.showMessageDialog(this, "Failed to add drive.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for package, CGPA, and backlogs.");
        }
    }

    // =====================================================================
    // APPLICATIONS TAB
    // DSA: Stack (Shashank) + Queue (Shashank) + Graph/BFS (Ayush)
    //      + Singly Linked List — status history timeline
    // =====================================================================
    private JPanel buildApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"App ID", "Student", "Company", "Job Role", "Status", "Applied On"};
        applicationsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        applicationsTable = new JTable(applicationsTableModel);
        applicationsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        panel.add(new JScrollPane(applicationsTable), BorderLayout.CENTER);

        // ---- Row 1: existing features ----
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBorder(BorderFactory.createTitledBorder("History Operations"));
        
        JButton updateBtn = new JButton("Update Status");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateBtn.addActionListener(e -> updateSelectedApplicationStatus());
        
        JButton historyBtn = new JButton("View Action History");
        historyBtn.setBackground(new Color(13, 148, 136)); // Teal/Blue 600
        historyBtn.setForeground(Color.WHITE);
        historyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyBtn.addActionListener(e -> viewSelectedApplicationHistory());
        
        JButton undoBtn = new JButton("Undo Last Change");
        undoBtn.setBackground(new Color(217, 119, 6)); // Amber 600
        undoBtn.setForeground(Color.WHITE);
        undoBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        undoBtn.addActionListener(e -> undoLastStatusChange());
        
        row1.add(updateBtn); row1.add(historyBtn); row1.add(undoBtn);

        // ---- Row 2: Queue + Graph ----
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBorder(BorderFactory.createTitledBorder("Pending Applications & Networks"));
        
        JButton addQueueBtn = new JButton("Add to Processing Queue");
        addQueueBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addQueueBtn.addActionListener(e -> addSelectedToQueue());
        
        JButton viewQueueBtn = new JButton("View Pending Queue");
        viewQueueBtn.setBackground(new Color(100, 116, 139)); // Slate 500
        viewQueueBtn.setForeground(Color.WHITE);
        viewQueueBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewQueueBtn.addActionListener(e -> viewQueue());
        
        JButton processBtn = new JButton("Process Next Candidate");
        processBtn.setBackground(new Color(16, 185, 129)); // Emerald Green 500
        processBtn.setForeground(Color.WHITE);
        processBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        processBtn.addActionListener(e -> processNextFromQueue());
        
        JButton graphBtn = new JButton("Show Relations Network");
        graphBtn.setBackground(new Color(147, 51, 234)); // Purple 600
        graphBtn.setForeground(Color.WHITE);
        graphBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        graphBtn.addActionListener(e -> viewPlacementGraph());
        
        row2.add(addQueueBtn); row2.add(viewQueueBtn); row2.add(processBtn); row2.add(graphBtn);

        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(row1);
        south.add(row2);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void loadApplications() {
        applicationsTableModel.setRowCount(0);
        currentApplicationsList = new ArrayList<>();

        // Rebuild graph (Ayush)
        placementGraph = new PlacementGraph();

        for (Application app : applicationDAO.getAllApplications()) {
            currentApplicationsList.add(app);

            Student s = studentDAO.getStudentById(app.getStudentId());
            Drive   d = driveDAO.getDriveById(app.getDriveId());

            String studentName = (s != null) ? s.getName()    : "Unknown";
            String companyName = "Unknown";
            String jobRole     = "Unknown";
            int    companyId   = -1;

            if (d != null) {
                Company c = companyDAO.getCompanyById(d.getCompanyId());
                companyName = (c != null) ? c.getName() : "Unknown";
                jobRole     = d.getJobRole();
                companyId   = d.getCompanyId();
            }

            // Graph edges (Ayush)
            placementGraph.addStudentNode(app.getStudentId());
            if (companyId != -1) {
                placementGraph.addCompanyNode(companyId);
                placementGraph.addEdge(app.getStudentId(), companyId);
            }

            applicationsTableModel.addRow(new Object[]{
                app.getApplicationId(), studentName, companyName, jobRole,
                app.getStatus(), app.getApplicationDate()
            });
        }

        // Reload in-memory Queue from DB (Shashank)
        loadQueueFromDb();
    }

    /** Shashank — sync in-memory Queue from DB on load */
    private void loadQueueFromDb() {
        appQueue = new ApplicationQueue();
        for (int[] entry : queueDAO.getAllPending()) {
            int     sid = entry[1], did = entry[2];
            Student s   = studentDAO.getStudentById(sid);
            Drive   d   = driveDAO.getDriveById(did);
            appQueue.enqueue(sid, did,
                (s != null ? s.getName()    : "Student#" + sid),
                (d != null ? d.getJobRole() : "Drive#"   + did));
        }
    }

    /** Shashank — Enqueue selected application (student+drive) into DB-backed Queue */
    private void addSelectedToQueue() {
        int row = applicationsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an application first."); return; }

        Application app = currentApplicationsList.get(row);
        Student s = studentDAO.getStudentById(app.getStudentId());
        Drive   d = driveDAO.getDriveById(app.getDriveId());
        String studentName = (s != null) ? s.getName()    : "Unknown";
        String driveName   = (d != null) ? d.getJobRole() : "Unknown";

        boolean added = queueDAO.enqueue(app.getStudentId(), app.getDriveId());
        if (added) {
            appQueue.enqueue(app.getStudentId(), app.getDriveId(), studentName, driveName);
            JOptionPane.showMessageDialog(this,
                "Enqueued to backlog:\n"
                + studentName + "  →  " + driveName + "\n"
                + "Queue size: " + appQueue.size() + "\n"
                + "Saved successfully to database.",
                "Queue Update", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Could not enqueue — may already be in queue or DB error.");
        }
    }

    /** Shashank — Show all entries in FIFO order */
    private void viewQueue() {
        loadQueueFromDb(); // sync from DB
        List<String> items = appQueue.viewAllInOrder();
        String body = items.isEmpty() ? "Queue is empty." : String.join("\n", items);
        JTextArea area = new JTextArea(
            "Pending Application Backlog Queue\n"
            + "Queue size: " + appQueue.size() + "\n"
            + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
            + body);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(450, 280));
        JOptionPane.showMessageDialog(this, scroll,
            "Queue Backlog", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shashank — Dequeue front (oldest) entry and process it */
    private void processNextFromQueue() {
        loadQueueFromDb();
        if (appQueue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Queue is empty. Nothing to process.");
            return;
        }
        String nextInfo = appQueue.peekInfo();
        int ok = JOptionPane.showConfirmDialog(this,
            "Process next from backlog queue?\n\n" + nextInfo,
            "Dequeue Candidate", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            int[] data = queueDAO.dequeueOldest(); // remove from DB
            appQueue.dequeue();                    // remove from in-memory Queue
            if (data != null) {
                Student s = studentDAO.getStudentById(data[0]);
                JOptionPane.showMessageDialog(this,
                    "Processed candidate:\n"
                    + (s != null ? s.getName() : "Student#" + data[0]) + "\n\n"
                    + "Remaining backlog: " + appQueue.size(),
                    "Process Complete", JOptionPane.INFORMATION_MESSAGE);
                loadApplications();
            }
        }
    }

    /** Ayush — Build adjacency list graph and run BFS, display in dialog */
    private void viewPlacementGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Placement Network Mapping ===\n");
        sb.append("Representation : Connection Network List\n");
        sb.append("Total Students : ").append(placementGraph.studentCount()).append("\n");
        sb.append("Total Companies: ").append(placementGraph.companyCount()).append("\n");
        sb.append("Total Linked Applications: ").append(placementGraph.edgeCount()).append("\n\n");

        sb.append("─── Active Applications Network ──────────────\n");
        for (Map.Entry<Integer, List<Integer>> entry : placementGraph.getAdjacencyList().entrySet()) {
            Student student = studentDAO.getStudentById(entry.getKey());
            String sName = (student != null) ? student.getName() : "Student#" + entry.getKey();
            List<String> cNames = new ArrayList<>();
            for (int cid : entry.getValue()) {
                Company co = companyDAO.getCompanyById(cid);
                cNames.add(co != null ? co.getName() : "Co#" + cid);
            }
            sb.append(sName).append("  →  ").append(String.join(", ", cNames)).append("\n");
        }

        // Run traversal from the first student in the graph
        if (!placementGraph.getAdjacencyList().isEmpty()) {
            int firstId = placementGraph.getAdjacencyList().keySet().iterator().next();
            Student fs   = studentDAO.getStudentById(firstId);
            String  fsName = (fs != null) ? fs.getName() : "Student#" + firstId;

            List<Integer> bfsCompanies = placementGraph.bfsCompaniesFromStudent(firstId);
            sb.append("\n─── Connected Companies from ").append(fsName).append(" ────────────────\n");
            sb.append("Applied Companies (").append(bfsCompanies.size()).append("):\n");
            for (int cid : bfsCompanies) {
                Company co = companyDAO.getCompanyById(cid);
                sb.append("  • ").append(co != null ? co.getName() : "Co#" + cid).append("\n");
            }

            List<Integer> related = placementGraph.getRelatedStudents(firstId);
            sb.append("\nStudents with overlapping application interests:\n");
            if (related.isEmpty()) {
                sb.append("  (none)\n");
            } else {
                for (int sid : related) {
                    Student rs = studentDAO.getStudentById(sid);
                    sb.append("  • ").append(rs != null ? rs.getName() : "Student#" + sid).append("\n");
                }
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(560, 400));
        JOptionPane.showMessageDialog(this, scroll,
            "Placement Network Map", JOptionPane.INFORMATION_MESSAGE);
    }

    // =====================================================================
    // STATUS UPDATE · HISTORY · UNDO  (Shashank — Stack + Linked List)
    // =====================================================================
    private void updateSelectedApplicationStatus() {
        int row = applicationsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an application first."); return; }

        int    appId         = (int)    applicationsTableModel.getValueAt(row, 0);
        String currentStatus = (String) applicationsTableModel.getValueAt(row, 4);

        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status:", "Update Application Status",
            JOptionPane.PLAIN_MESSAGE, null, STATUS_OPTIONS, STATUS_OPTIONS[0]);

        if (newStatus != null) {
            boolean ok = applicationDAO.updateStatus(appId, newStatus);
            if (ok) {
                undoStack.push(new UndoAction(appId, currentStatus)); // Shashank — Stack push
                historyDAO.addHistoryEntry(appId, newStatus);
                JOptionPane.showMessageDialog(this, "Status updated successfully!");
                loadApplications();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update status.");
            }
        }
    }

    private void viewSelectedApplicationHistory() {
        int row = applicationsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an application first."); return; }

        int appId = (int) applicationsTableModel.getValueAt(row, 0);
        StatusHistoryLinkedList timeline = new StatusHistoryLinkedList(); // Singly Linked List
        for (StatusHistoryEntry e : historyDAO.getHistoryForApplication(appId)) {
            timeline.addStatusChange(e);
        }
        List<String> steps = timeline.getTimeline();
        JOptionPane.showMessageDialog(this,
            steps.isEmpty() ? "No history found." : String.join("\n", steps),
            "Status Timeline — Singly Linked List", JOptionPane.INFORMATION_MESSAGE);
    }

    private void undoLastStatusChange() {
        if (undoStack.isEmpty()) { // Shashank — Stack
            JOptionPane.showMessageDialog(this, "No actions to undo. Stack is empty.");
            return;
        }
        UndoAction last = undoStack.pop(); // Stack pop (Shashank)
        boolean ok = applicationDAO.updateStatus(last.getApplicationId(), last.getPreviousStatus());
        if (ok) {
            historyDAO.addHistoryEntry(last.getApplicationId(), last.getPreviousStatus());
            JOptionPane.showMessageDialog(this,
                "Undone! Application #" + last.getApplicationId()
                + " reverted to: " + last.getPreviousStatus()
                + "\nStack size remaining: " + undoStack.size());
            loadApplications();
        } else {
            JOptionPane.showMessageDialog(this, "Undo failed.");
        }
    }

    private JPanel buildOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.setBackground(new Color(15, 23, 42)); // Slate 900
        
        // Welcome SaaS-style header
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("System Insights");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Grid of Cards
        JPanel grid = new JPanel(new GridLayout(1, 4, 18, 0));
        grid.setOpaque(false);
        
        // Let's count them
        int totalStudents = studentDAO.getAllStudents().size();
        int totalCompanies = companyDAO.getAllCompanies().size();
        int totalDrives = driveDAO.getAllDrives().size();
        int totalApps = applicationDAO.getAllApplications().size();
        
        grid.add(createStatsCard("Total Students", String.valueOf(totalStudents), "👤", new Color(59, 130, 246))); // Blue 500
        grid.add(createStatsCard("Corporate Partners", String.valueOf(totalCompanies), "💼", new Color(139, 92, 246))); // Purple 500
        grid.add(createStatsCard("Placement Drives", String.valueOf(totalDrives), "📅", new Color(245, 158, 11))); // Amber 500
        grid.add(createStatsCard("Applications Processed", String.valueOf(totalApps), "📥", new Color(16, 185, 129))); // Emerald 500
        
        panel.add(grid, BorderLayout.CENTER);
        
        // Add a beautiful motivational message or quick links at the bottom
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JLabel label = new JLabel("⚡ Integrated Multi-DSA Platform: BST Indexing, Trie Search, Priority Queue, Graph BFS Visualization");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        label.setForeground(new Color(148, 163, 184)); // Slate 400
        footer.add(label);
        panel.add(footer, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createStatsCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card body with rounded corners
                g2.setColor(new Color(30, 41, 59)); // Slate 800
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Draw a sleek left accent border
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLbl.setForeground(accentColor);
        card.add(iconLbl, BorderLayout.EAST);
        
        JPanel texts = new JPanel(new GridLayout(2, 1, 4, 4));
        texts.setOpaque(false);
        
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valLbl.setForeground(Color.WHITE);
        
        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(148, 163, 184)); // Slate 400
        
        texts.add(titleLbl);
        texts.add(valLbl);
        card.add(texts, BorderLayout.CENTER);
        
        return card;
    }
}