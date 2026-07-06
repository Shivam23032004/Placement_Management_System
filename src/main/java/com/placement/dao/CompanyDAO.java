package com.placement.dao;

import com.placement.db.DBConnection;
import com.placement.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    // 1. Get all companies
    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT * FROM companies";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching companies: " + e.getMessage());
        }
        return companies;
    }

    // 2. Get a single company by ID
    public Company getCompanyById(int companyId) {
        String query = "SELECT * FROM companies WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching company: " + e.getMessage());
        }
        return null;
    }
    // user_id se company dhoondo (recruiter login ke baad dashboard kholne ke liye zaroori)
    public Company getCompanyByUserId(int userId) {
        String query = "SELECT * FROM companies WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching company by user ID: " + e.getMessage());
        }
        return null;
    }

    // 3. Add a new company
    public boolean addCompany(Company company) {
        String query = "INSERT INTO companies (user_id, name, description, website) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, company.getUserId());
            pstmt.setString(2, company.getName());
            pstmt.setString(3, company.getDescription());
            pstmt.setString(4, company.getWebsite());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding company: " + e.getMessage());
            return false;
        }
    }

    // 4. Update an existing company
    public boolean updateCompany(Company company) {
        String query = "UPDATE companies SET name=?, description=?, website=? WHERE company_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getDescription());
            pstmt.setString(3, company.getWebsite());
            pstmt.setInt(4, company.getCompanyId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating company: " + e.getMessage());
            return false;
        }
    }

    // 5. Delete a company
    public boolean deleteCompany(int companyId) {
        String query = "DELETE FROM companies WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, companyId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting company: " + e.getMessage());
            return false;
        }
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company(
                rs.getInt("company_id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("website")
        );
    }
}