package com.placement.dao;

import com.placement.db.DBConnection;
import com.placement.model.StatusHistoryEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationHistoryDAO {

    public boolean addHistoryEntry(int applicationId, String status) {
        String query = "INSERT INTO application_history (application_id, status) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, applicationId);
            pstmt.setString(2, status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding history entry: " + e.getMessage());
            return false;
        }
    }

    public List<StatusHistoryEntry> getHistoryForApplication(int applicationId) {
        List<StatusHistoryEntry> history = new ArrayList<>();
        String query = "SELECT * FROM application_history WHERE application_id = ? ORDER BY changed_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, applicationId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.add(new StatusHistoryEntry(
                        rs.getInt("history_id"),
                        rs.getInt("application_id"),
                        rs.getString("status"),
                        rs.getString("changed_at")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
        return history;
    }
}