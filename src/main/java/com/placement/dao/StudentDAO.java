package com.placement.dao;

import com.placement.db.DBConnection;
import com.placement.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 1. Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
        return students;
    }

    // 2. Get a single student by ID
    public Student getStudentById(int studentId) {
        String query = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching student: " + e.getMessage());
        }
        return null;
    }

    // user_id se student dhundo (login ke baad dashboard kholne ke liye zaroori)
    public Student getStudentByUserId(int userId) {
        String query = "SELECT * FROM students WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching student by user ID: " + e.getMessage());
        }
        return null;
    }

    // 3. Add a new student
    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (user_id, name, branch, cgpa, backlog_count, phone, email, resume_link) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getBranch());
            pstmt.setDouble(4, student.getCgpa());
            pstmt.setInt(5, student.getBacklogCount());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getResumeLink());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    // 4. Update an existing student
    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET name=?, branch=?, cgpa=?, backlog_count=?, phone=?, email=?, resume_link=? " +
                       "WHERE student_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getBranch());
            pstmt.setDouble(3, student.getCgpa());
            pstmt.setInt(4, student.getBacklogCount());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getResumeLink());
            pstmt.setInt(8, student.getStudentId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    // 5. Delete a student
    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    // Helper method: converts a database row into a Student object
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("branch"),
                rs.getDouble("cgpa"),
                rs.getInt("backlog_count"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("resume_link")
        );
    }
}