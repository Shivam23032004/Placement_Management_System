package com.placement.dao;

import com.placement.db.DBConnection;
import com.placement.model.Application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    // 1. Get all applications
    public List<Application> getAllApplications() {
        List<Application> applications = new ArrayList<>();
        String query = "SELECT * FROM applications";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching applications: " + e.getMessage());
        }
        return applications;
    }

    // 2. Get all applications for a specific drive (useful for recruiter view)
    public List<Application> getApplicationsByDrive(int driveId) {
        List<Application> applications = new ArrayList<>();
        String query = "SELECT * FROM applications WHERE drive_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, driveId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching applications by drive: " + e.getMessage());
        }
        return applications;
    }

    // 3. Get all applications by a specific student (useful for student dashboard)
    public List<Application> getApplicationsByStudent(int studentId) {
        List<Application> applications = new ArrayList<>();
        String query = "SELECT * FROM applications WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching applications by student: " + e.getMessage());
        }
        return appli
    }

    // 4. Apply to a drive (insert new application)
    public boolean applyToDrive(int studentId, int driveId) {
        String query = "INSERT INTO applications (student_id, drive_id, status) VALUES (?, ?, 'applied')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, driveId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error applying to drive: " + e.getMessage());
            return false;
        }
    }

    // 5. Update application status (e.g. shortlisted, selected, rejected)
    public boolean updateStatus(int applicationId, String newStatus) {
        String query = "UPDATE applications SET status=? WHERE application_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, applicationId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating application status: " + e.getMessage());
            return false;
        }
    }

    private Application mapResultSetToApplication(ResultSet rs) throws SQLException {
        return new Application(
                rs.getInt("application_id"),
                rs.getInt("student_id"),
                rs.getInt("drive_id"),
                rs.getString("application_date"),
                rs.getString("status")
        );
    }
}