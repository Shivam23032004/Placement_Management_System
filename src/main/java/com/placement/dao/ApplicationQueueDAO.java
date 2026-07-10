package com.placement.dao;

import com.placement.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for application_queue table.
 * Member: Shashank
 *
 * Handles DB persistence for the Queue DSA structure.
 * The queue is ordered by queue_id (AUTO_INCREMENT) — lowest ID = oldest = FIFO front.
 */
public class ApplicationQueueDAO {

    /**
     * Enqueue: insert a new entry into the DB queue.
     * Returns false if the (student, drive) pair is already queued (UNIQUE constraint).
     */
    public boolean enqueue(int studentId, int driveId) {
        String query = "INSERT INTO application_queue (student_id, drive_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, driveId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error enqueuing to DB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all pending entries in FIFO order (ordered by queue_id ASC).
     * Returns List of int[] where each array is {queue_id, student_id, drive_id}.
     */
    public List<int[]> getAllPending() {
        List<int[]> list = new ArrayList<>();
        String query = "SELECT queue_id, student_id, drive_id FROM application_queue ORDER BY queue_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new int[]{
                    rs.getInt("queue_id"),
                    rs.getInt("student_id"),
                    rs.getInt("drive_id")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error fetching queue from DB: " + e.getMessage());
        }
        return list;
    }

    /**
     * Dequeue (FIFO): fetch and remove the oldest entry (smallest queue_id).
     * Returns int[] {studentId, driveId}, or null if queue is empty.
     */
    public int[] dequeueOldest() {
        // Step 1: fetch the oldest (front) entry
        String selectQuery = "SELECT queue_id, student_id, drive_id FROM application_queue ORDER BY queue_id ASC LIMIT 1";
        int queueId = -1, studentId = -1, driveId = -1;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            if (rs.next()) {
                queueId   = rs.getInt("queue_id");
                studentId = rs.getInt("student_id");
                driveId   = rs.getInt("drive_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching queue front: " + e.getMessage());
            return null;
        }

        if (queueId == -1) return null; // queue is empty

        // Step 2: delete the fetched entry from DB
        String deleteQuery = "DELETE FROM application_queue WHERE queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, queueId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting from queue: " + e.getMessage());
        }

        return new int[]{ studentId, driveId };
    }

    /**
     * Returns the current number of entries in the queue.
     */
    public int getQueueSize() {
        String query = "SELECT COUNT(*) FROM application_queue";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error counting queue: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Remove a specific entry by student + drive (used for cleanup).
     */
    public boolean removeByStudentAndDrive(int studentId, int driveId) {
        String query = "DELETE FROM application_queue WHERE student_id = ? AND drive_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, driveId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error removing from queue: " + e.getMessage());
            return false;
        }
    }
}
