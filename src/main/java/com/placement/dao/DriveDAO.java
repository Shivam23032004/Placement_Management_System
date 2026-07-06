package com.placement.dao;

import com.placement.db.DBConnection;
import com.placement.model.Drive;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriveDAO {

    // 1. Get all drives (with eligible branches attached)
    public List<Drive> getAllDrives() {
        List<Drive> drives = new ArrayList<>();
        String query = "SELECT * FROM drives";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Drive drive = mapResultSetToDrive(rs);
                drive.setEligibleBranches(getEligibleBranches(drive.getDriveId()));
                drives.add(drive);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching drives: " + e.getMessage());
        }
        return drives;
    }

    // 2. Get a single drive by ID
    public Drive getDriveById(int driveId) {
        String query = "SELECT * FROM drives WHERE drive_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, driveId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Drive drive = mapResultSetToDrive(rs);
                drive.setEligibleBranches(getEligibleBranches(driveId));
                return drive;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching drive: " + e.getMessage());
        }
        return null;
    }
    // Ek specific company ke saare drives lao
    public List<Drive> getDrivesByCompany(int companyId) {
        List<Drive> drives = new ArrayList<>();
        String query = "SELECT * FROM drives WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Drive drive = mapResultSetToDrive(rs);
                drive.setEligibleBranches(getEligibleBranches(drive.getDriveId()));
                drives.add(drive);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching drives by company: " + e.getMessage());
        }
        return drives;
    }

    // 3. Add a new drive (plus its eligible branches)
    public boolean addDrive(Drive drive) {
        String query = "INSERT INTO drives (company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, drive.getCompanyId());
            pstmt.setString(2, drive.getJobRole());
            pstmt.setDouble(3, drive.getPackageLpa());
            pstmt.setDouble(4, drive.getMinCgpa());
            pstmt.setInt(5, drive.getMaxBacklogs());
            pstmt.setString(6, drive.getDriveDate());
            pstmt.setString(7, drive.getStatus());

            int rows = pstmt.executeUpdate();

            if (rows > 0 && drive.getEligibleBranches() != null) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    int newDriveId = keys.getInt(1);
                    insertEligibleBranches(newDriveId, drive.getEligibleBranches());
                }
            }
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error adding drive: " + e.getMessage());
            return false;
        }
    }

    // 4. Delete a drive
    public boolean deleteDrive(int driveId) {
        String query = "DELETE FROM drives WHERE drive_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, driveId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting drive: " + e.getMessage());
            return false;
        }
    }

    // Helper: fetch eligible branches for a drive from drive_branches table
    private List<String> getEligibleBranches(int driveId) {
        List<String> branches = new ArrayList<>();
        String query = "SELECT branch FROM drive_branches WHERE drive_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, driveId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                branches.add(rs.getString("branch"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching eligible branches: " + e.getMessage());
        }
        return branches;
    }

    // Helper: insert eligible branches into drive_branches table
    private void insertEligibleBranches(int driveId, List<String> branches) {
        String query = "INSERT INTO drive_branches (drive_id, branch) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (String branch : branches) {
                pstmt.setInt(1, driveId);
                pstmt.setString(2, branch);
                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (SQLException e) {
            System.out.println("Error inserting eligible branches: " + e.getMessage());
        }
    }

    private Drive mapResultSetToDrive(ResultSet rs) throws SQLException {
        return new Drive(
                rs.getInt("drive_id"),
                rs.getInt("company_id"),
                rs.getString("job_role"),
                rs.getDouble("package_lpa"),
                rs.getDouble("min_cgpa"),
                rs.getInt("max_backlogs"),
                rs.getString("drive_date"),
                rs.getString("status")
        );
    }
}