package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.User;
import votingsystempro.models.Voter;
import votingsystempro.utils.EmailUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthController {
    
    public Object login(String voterId, String password) {
        String query = "SELECT u.*, v.voter_id as voter_table_id FROM users u " +
                      "LEFT JOIN voters v ON u.user_id = v.user_id " +
                      "WHERE u.voter_id = ? AND u.password = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, voterId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("user_type");
                if ("admin".equals(userType)) {
                    return "admin";
                } else {
                    int voterTableId = rs.getInt("voter_table_id");
                    if (voterTableId > 0) {
                        if (isVoterApproved(voterTableId)) {
                            return voterTableId;
                        } else {
                            return "Your registration is pending approval from admin";
                        }
                    } else {
                        return "Voter record not found";
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean isVoterApproved(int voterId) {
        String query = "SELECT is_approved FROM voters WHERE voter_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, voterId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_approved");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean registerVoter(Voter voter, String password) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            if (isCitizenshipExists(voter.getCitizenshipNumber())) {
                throw new SQLException("Citizenship number already registered");
            }
            
            if (isEmailExists(voter.getEmail())) {
                throw new SQLException("Email already registered");
            }
            
            String userQuery = "INSERT INTO users (voter_id, password, user_type) VALUES (?, ?, 'voter')";
            PreparedStatement userPstmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            
            String generatedVoterId = generateVoterId();
            userPstmt.setString(1, generatedVoterId);
            userPstmt.setString(2, password);
            userPstmt.executeUpdate();
            
            ResultSet rs = userPstmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            
            String voterQuery = "INSERT INTO voters (user_id, full_name, date_of_birth, age, citizenship_number, " +
                               "father_name, mother_name, address, phone_number, email, province_id, district_id, " +
                               "constituency_id, photo_path, is_approved) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement voterPstmt = conn.prepareStatement(voterQuery);
            voterPstmt.setInt(1, userId);
            voterPstmt.setString(2, voter.getFullName());
            voterPstmt.setDate(3, new java.sql.Date(voter.getDateOfBirth().getTime()));
            voterPstmt.setInt(4, voter.getAge());
            voterPstmt.setString(5, voter.getCitizenshipNumber());
            voterPstmt.setString(6, voter.getFatherName());
            voterPstmt.setString(7, voter.getMotherName());
            voterPstmt.setString(8, voter.getAddress());
            voterPstmt.setString(9, voter.getPhoneNumber());
            voterPstmt.setString(10, voter.getEmail());
            voterPstmt.setInt(11, voter.getProvinceId());
            voterPstmt.setInt(12, voter.getDistrictId());
            voterPstmt.setInt(13, voter.getConstituencyId());
            voterPstmt.setString(14, voter.getPhotoPath());
            voterPstmt.setBoolean(15, false);
            
            voterPstmt.executeUpdate();
            
            logActivity(userId, "Registered as voter");
            
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
    
    public boolean sendVoterIdEmail(int voterId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            String query = "SELECT v.*, u.voter_id as login_id FROM voters v " +
                          "JOIN users u ON v.user_id = u.user_id " +
                          "WHERE v.voter_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, voterId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String email = rs.getString("email");
                    String fullName = rs.getString("full_name");
                    String generatedVoterId = rs.getString("login_id");
                    int userId = rs.getInt("user_id");
                    
                    if (email == null || email.trim().isEmpty()) {
                        logActivity(userId, "Failed to send Voter ID email: No email address");
                        return false;
                    }
                    
                    boolean emailSent = EmailUtil.sendVoterIdEmail(email, generatedVoterId, fullName);
                    
                    if (emailSent) {
                        logActivity(userId, "Voter ID email sent successfully to: " + email);
                        return true;
                    } else {
                        logActivity(userId, "Failed to send Voter ID email to: " + email);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    private boolean isCitizenshipExists(String citizenshipNumber) {
        String query = "SELECT COUNT(*) FROM voters WHERE citizenship_number = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, citizenshipNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM voters WHERE email = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String generateVoterId() {
        return "VOT" + System.currentTimeMillis();
    }
    
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE user_id = ? AND password = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            pstmt.setString(3, oldPassword);
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                logActivity(userId, "Password changed successfully");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void logActivity(int userId, String action) {
        String query = "INSERT INTO activity_log (user_id, action, ip_address) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, "localhost");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY created_at DESC";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setVoterId(rs.getString("voter_id"));
                user.setPassword(rs.getString("password"));
                user.setUserType(rs.getString("user_type"));
                user.setCreatedAt(rs.getString("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    /**
     * Check email configuration status
     * Updated to work with the new EmailUtil
     */
    public String checkEmailConfigStatus() {
        try {
            // Define config file path
            String configPath = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\email-config.properties";
            File configFile = new File(configPath);
            
            // Check if file exists
            if (!configFile.exists()) {
                return "⚠️ Email config file not found at: " + configPath;
            }
            
            // Try to test email configuration
            boolean isWorking = EmailUtil.testEmailConfiguration(null);
            
            if (isWorking) {
                return "✅ Email configuration is working correctly";
            } else {
                return "⚠️ Email configuration found but not working (check credentials)";
            }
            
        } catch (Exception e) {
            System.err.println("Error checking email config: " + e.getMessage());
            return "❌ Email configuration error: " + e.getMessage();
        }
    }
}