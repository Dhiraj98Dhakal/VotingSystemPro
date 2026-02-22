package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    
    public Admin getAdminByUserId(int userId) {
        String query = "SELECT * FROM admins WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUserId(rs.getInt("user_id"));
                admin.setFullName(rs.getString("full_name"));
                admin.setEmail(rs.getString("email"));
                admin.setPhoneNumber(rs.getString("phone_number"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String query = "SELECT * FROM admins ORDER BY created_at DESC";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUserId(rs.getInt("user_id"));
                admin.setFullName(rs.getString("full_name"));
                admin.setEmail(rs.getString("email"));
                admin.setPhoneNumber(rs.getString("phone_number"));
                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
    
    public boolean addAdmin(Admin admin, String password) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into users table
            String userQuery = "INSERT INTO users (voter_id, password, user_type) VALUES (?, ?, 'admin')";
            PreparedStatement userPstmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            
            String adminId = "ADM" + System.currentTimeMillis();
            userPstmt.setString(1, adminId);
            userPstmt.setString(2, password);
            userPstmt.executeUpdate();
            
            ResultSet rs = userPstmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            
            // Insert into admins table
            String adminQuery = "INSERT INTO admins (user_id, full_name, email, phone_number) VALUES (?, ?, ?, ?)";
            PreparedStatement adminPstmt = conn.prepareStatement(adminQuery);
            adminPstmt.setInt(1, userId);
            adminPstmt.setString(2, admin.getFullName());
            adminPstmt.setString(3, admin.getEmail());
            adminPstmt.setString(4, admin.getPhoneNumber());
            adminPstmt.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAdmin(Admin admin) {
        String query = "UPDATE admins SET full_name=?, email=?, phone_number=? WHERE admin_id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, admin.getFullName());
            pstmt.setString(2, admin.getEmail());
            pstmt.setString(3, admin.getPhoneNumber());
            pstmt.setInt(4, admin.getAdminId());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteAdmin(int adminId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get user_id first
            String getUserQuery = "SELECT user_id FROM admins WHERE admin_id = ?";
            PreparedStatement getUserStmt = conn.prepareStatement(getUserQuery);
            getUserStmt.setInt(1, adminId);
            ResultSet rs = getUserStmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                
                // Delete from admins
                String deleteAdminQuery = "DELETE FROM admins WHERE admin_id = ?";
                PreparedStatement deleteAdminStmt = conn.prepareStatement(deleteAdminQuery);
                deleteAdminStmt.setInt(1, adminId);
                deleteAdminStmt.executeUpdate();
                
                // Delete from users
                String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
                PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery);
                deleteUserStmt.setInt(1, userId);
                deleteUserStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getActivityLogs() {
        List<String> logs = new ArrayList<>();
        String query = "SELECT al.*, u.voter_id FROM activity_log al " +
                      "LEFT JOIN users u ON al.user_id = u.user_id " +
                      "ORDER BY al.timestamp DESC LIMIT 100";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String log = String.format("[%s] %s - %s", 
                    rs.getTimestamp("timestamp"),
                    rs.getString("voter_id"),
                    rs.getString("action"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    public int getDashboardStats(String statType) {
        String query = "";
        switch (statType) {
            case "total_voters":
                query = "SELECT COUNT(*) FROM voters";
                break;
            case "approved_voters":
                query = "SELECT COUNT(*) FROM voters WHERE is_approved = true";
                break;
            case "pending_voters":
                query = "SELECT COUNT(*) FROM voters WHERE is_approved = false";
                break;
            case "total_parties":
                query = "SELECT COUNT(*) FROM parties";
                break;
            case "total_candidates":
                query = "SELECT COUNT(*) FROM candidates";
                break;
            case "fptp_votes":
                query = "SELECT COUNT(*) FROM votes_fptp";
                break;
            case "pr_votes":
                query = "SELECT COUNT(*) FROM votes_pr";
                break;
            default:
                return 0;
        }
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}