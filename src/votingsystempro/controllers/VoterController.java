package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.Voter;
import votingsystempro.utils.EmailUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoterController {
    
    private AuthController authController;
    
    public VoterController() {
        this.authController = new AuthController();
    }
    
    public Voter getVoterById(int voterId) {
        String query = "SELECT v.*, p.province_name, d.district_name, c.constituency_number " +
                      "FROM voters v " +
                      "LEFT JOIN provinces p ON v.province_id = p.province_id " +
                      "LEFT JOIN districts d ON v.district_id = d.district_id " +
                      "LEFT JOIN constituencies c ON v.constituency_id = c.constituency_id " +
                      "WHERE v.voter_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, voterId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractVoterFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Voter getVoterByUserId(int userId) {
        String query = "SELECT v.*, p.province_name, d.district_name, c.constituency_number " +
                      "FROM voters v " +
                      "LEFT JOIN provinces p ON v.province_id = p.province_id " +
                      "LEFT JOIN districts d ON v.district_id = d.district_id " +
                      "LEFT JOIN constituencies c ON v.constituency_id = c.constituency_id " +
                      "WHERE v.user_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractVoterFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Voter> getAllVoters() {
        List<Voter> voters = new ArrayList<>();
        String query = "SELECT v.*, p.province_name, d.district_name, c.constituency_number " +
                      "FROM voters v " +
                      "LEFT JOIN provinces p ON v.province_id = p.province_id " +
                      "LEFT JOIN districts d ON v.district_id = d.district_id " +
                      "LEFT JOIN constituencies c ON v.constituency_id = c.constituency_id " +
                      "ORDER BY v.registration_date DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                voters.add(extractVoterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voters;
    }
    
    public List<Voter> getPendingApprovalVoters() {
        List<Voter> voters = new ArrayList<>();
        String query = "SELECT v.*, p.province_name, d.district_name, c.constituency_number " +
                      "FROM voters v " +
                      "LEFT JOIN provinces p ON v.province_id = p.province_id " +
                      "LEFT JOIN districts d ON v.district_id = d.district_id " +
                      "LEFT JOIN constituencies c ON v.constituency_id = c.constituency_id " +
                      "WHERE v.is_approved = false " +
                      "ORDER BY v.registration_date DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                voters.add(extractVoterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voters;
    }
    
    public List<Voter> getApprovedVoters() {
        List<Voter> voters = new ArrayList<>();
        String query = "SELECT v.*, p.province_name, d.district_name, c.constituency_number " +
                      "FROM voters v " +
                      "LEFT JOIN provinces p ON v.province_id = p.province_id " +
                      "LEFT JOIN districts d ON v.district_id = d.district_id " +
                      "LEFT JOIN constituencies c ON v.constituency_id = c.constituency_id " +
                      "WHERE v.is_approved = true " +
                      "ORDER BY v.registration_date DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                voters.add(extractVoterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voters;
    }
    
    /**
     * Approve voter and send Voter ID email
     */
    public boolean approveVoter(int voterId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First check if voter exists and is not already approved
            String checkQuery = "SELECT is_approved, email, full_name FROM voters WHERE voter_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, voterId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    if (rs.getBoolean("is_approved")) {
                        System.out.println("Voter " + voterId + " is already approved");
                        return false;
                    }
                    
                    String email = rs.getString("email");
                    String fullName = rs.getString("full_name");
                    
                    // Update voter approval status
                    String updateQuery = "UPDATE voters SET is_approved = true WHERE voter_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, voterId);
                        int affected = updateStmt.executeUpdate();
                        
                        if (affected > 0) {
                            // Get user_id for logging
                            String userQuery = "SELECT user_id FROM voters WHERE voter_id = ?";
                            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                                userStmt.setInt(1, voterId);
                                ResultSet userRs = userStmt.executeQuery();
                                if (userRs.next()) {
                                    int userId = userRs.getInt("user_id");
                                    
                                    // Log activity
                                    logActivity(voterId, "Voter approved by admin");
                                    
                                    // Commit the transaction
                                    conn.commit();
                                    
                                    // Send email with voter ID in a separate thread
                                    System.out.println("Starting email thread for voter: " + voterId);
                                    new Thread(() -> {
                                        try {
                                            System.out.println("Sending Voter ID email to: " + email);
                                            boolean emailSent = authController.sendVoterIdEmail(voterId);
                                            
                                            if (emailSent) {
                                                System.out.println("✅ Voter ID email sent successfully to " + email);
                                                // Log email success in a new connection
                                                logEmailStatus(voterId, true, email);
                                            } else {
                                                System.err.println("❌ Failed to send Voter ID email to " + email);
                                                logEmailStatus(voterId, false, email);
                                            }
                                        } catch (Exception e) {
                                            System.err.println("Error in email thread: " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }).start();
                                    
                                    return true;
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("Voter not found with ID: " + voterId);
                }
            }
            
            // If we reach here, something failed
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error approving voter: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== BULK EMAIL METHODS ====================
    
    /**
     * Send bulk emails to all approved voters
     * @return Number of emails sent successfully
     */
    public int sendBulkEmailsToApprovedVoters() {
        List<Voter> voters = getApprovedVoters();
        List<String> emails = new ArrayList<>();
        List<String> voterIds = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<Integer> voterIdList = new ArrayList<>();
        
        System.out.println("📋 Found " + voters.size() + " approved voters");
        
        for (Voter voter : voters) {
            if (voter.getEmail() != null && !voter.getEmail().isEmpty()) {
                emails.add(voter.getEmail());
                voterIdList.add(voter.getVoterId());
                
                // Get voter's login ID from users table
                String loginId = getVoterLoginId(voter.getVoterId());
                voterIds.add(loginId);
                
                names.add(voter.getFullName());
                
                System.out.println("   → " + voter.getFullName() + " (" + voter.getEmail() + ")");
            } else {
                System.out.println("   ⚠️ " + voter.getFullName() + " has no email address");
            }
        }
        
        if (emails.isEmpty()) {
            System.out.println("⚠️ No voters with email addresses found");
            return 0;
        }
        
        System.out.println("📧 Sending bulk emails to " + emails.size() + " voters...");
        
        // Send emails using EmailUtil
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            String voterId = voterIds.get(i);
            String fullName = names.get(i);
            int voterDbId = voterIdList.get(i);
            
            System.out.println("📤 Sending to " + (i+1) + "/" + emails.size() + ": " + email);
            
            boolean sent = authController.sendVoterIdEmail(voterDbId);
            
            if (sent) {
                successCount++;
                logBulkEmailStatus(voterDbId, true, email);
                System.out.println("   ✅ Sent");
            } else {
                failCount++;
                logBulkEmailStatus(voterDbId, false, email);
                System.out.println("   ❌ Failed");
            }
            
            // Small delay to avoid rate limiting
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Log the bulk email action
        logBulkEmailAction(successCount, failCount);
        
        System.out.println("📊 Bulk Email Summary - Success: " + successCount + ", Failed: " + failCount);
        return successCount;
    }
    
    /**
     * Send bulk emails to selected voters
     * @param voterIds List of voter IDs to send emails to
     * @return Number of emails sent successfully
     */
    public int sendBulkEmailsToSelectedVoters(List<Integer> voterIds) {
        if (voterIds == null || voterIds.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        int failCount = 0;
        
        System.out.println("📋 Sending bulk emails to " + voterIds.size() + " selected voters");
        
        for (int voterId : voterIds) {
            Voter voter = getVoterById(voterId);
            
            if (voter != null && voter.getEmail() != null && !voter.getEmail().isEmpty()) {
                System.out.println("📤 Sending to: " + voter.getEmail());
                
                boolean sent = authController.sendVoterIdEmail(voterId);
                
                if (sent) {
                    successCount++;
                    logBulkEmailStatus(voterId, true, voter.getEmail());
                    System.out.println("   ✅ Sent");
                } else {
                    failCount++;
                    logBulkEmailStatus(voterId, false, voter.getEmail());
                    System.out.println("   ❌ Failed");
                }
                
                // Small delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Voter ID " + voterId + " has no valid email");
                failCount++;
            }
        }
        
        logBulkEmailAction(successCount, failCount);
        return successCount;
    }
    
    /**
     * Send test email to verify bulk email functionality
     * @param testEmail Email address to send test to
     * @return true if test email sent successfully
     */
    public boolean sendTestBulkEmail(String testEmail) {
        if (testEmail == null || testEmail.isEmpty()) {
            return false;
        }
        
        System.out.println("📧 Sending test bulk email to: " + testEmail);
        
        // Create test voter data
        List<String> emails = new ArrayList<>();
        List<String> voterIds = new ArrayList<>();
        List<String> names = new ArrayList<>();
        
        emails.add(testEmail);
        voterIds.add("TEST001");
        names.add("Test User");
        
        // Use EmailUtil directly
        boolean result = EmailUtil.sendBulkEmails(emails, voterIds, names);
        
        if (result) {
            System.out.println("✅ Test email sent successfully");
        } else {
            System.err.println("❌ Test email failed");
        }
        
        return result;
    }
    
    /**
     * Get voter's login ID from users table
     */
    private String getVoterLoginId(int voterId) {
        String query = "SELECT u.voter_id FROM users u " +
                       "JOIN voters v ON u.user_id = v.user_id " +
                       "WHERE v.voter_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, voterId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("voter_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "VOT" + voterId;
    }
    
    /**
     * Log bulk email status for individual voter
     */
    private void logBulkEmailStatus(int voterId, boolean success, String email) {
        String action = success ? 
            "Bulk email sent successfully to: " + email : 
            "Bulk email failed to: " + email;
        
        String query = "INSERT INTO activity_log (user_id, action, ip_address) " +
                      "VALUES ((SELECT user_id FROM voters WHERE voter_id=?), ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, voterId);
            pstmt.setString(2, action);
            pstmt.setString(3, "localhost");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Log bulk email action summary
     */
    private void logBulkEmailAction(int success, int failed) {
        String action = "Bulk email completed - Success: " + success + ", Failed: " + failed;
        
        String query = "INSERT INTO activity_log (user_id, action, ip_address) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, 1); // admin user ID
            pstmt.setString(2, action);
            pstmt.setString(3, "localhost");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get email sending statistics
     */
    public String getEmailStats() {
        StringBuilder stats = new StringBuilder();
        
        String query = "SELECT " +
                      "SUM(CASE WHEN action LIKE 'Voter ID email sent successfully%' THEN 1 ELSE 0 END) as single_success, " +
                      "SUM(CASE WHEN action LIKE 'Failed to send Voter ID email%' THEN 1 ELSE 0 END) as single_failed, " +
                      "SUM(CASE WHEN action LIKE 'Bulk email sent successfully%' THEN 1 ELSE 0 END) as bulk_success, " +
                      "SUM(CASE WHEN action LIKE 'Bulk email failed%' THEN 1 ELSE 0 END) as bulk_failed, " +
                      "SUM(CASE WHEN action LIKE 'Bulk email completed%' THEN 1 ELSE 0 END) as bulk_batches " +
                      "FROM activity_log WHERE action LIKE '%email%'";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                stats.append("📧 EMAIL STATISTICS\n");
                stats.append("==================\n\n");
                stats.append("Single Emails:\n");
                stats.append("  • Successful: ").append(rs.getInt("single_success")).append("\n");
                stats.append("  • Failed: ").append(rs.getInt("single_failed")).append("\n\n");
                stats.append("Bulk Emails:\n");
                stats.append("  • Successful: ").append(rs.getInt("bulk_success")).append("\n");
                stats.append("  • Failed: ").append(rs.getInt("bulk_failed")).append("\n");
                stats.append("  • Batches: ").append(rs.getInt("bulk_batches")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats.toString();
    }
    
    /**
     * Get voters who have not received email yet
     */
    public List<Voter> getVotersWithoutEmail() {
        List<Voter> voters = new ArrayList<>();
        String query = "SELECT v.* FROM voters v " +
                      "WHERE v.is_approved = true " +
                      "AND NOT EXISTS (SELECT 1 FROM activity_log WHERE action LIKE 'Voter ID email sent%' AND user_id = v.user_id)";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                voters.add(extractVoterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voters;
    }
    
    /**
     * Resend Voter ID email to specific voter
     */
    public boolean resendVoterIdEmail(int voterId) {
        return authController.sendVoterIdEmail(voterId);
    }
    
    /**
     * Approve multiple voters at once
     */
    public int approveMultipleVoters(List<Integer> voterIds) {
        int successCount = 0;
        for (int voterId : voterIds) {
            if (approveVoter(voterId)) {
                successCount++;
                try {
                    Thread.sleep(1000); // Wait 1 second between emails
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return successCount;
    }
    
    /**
     * Reject voter (delete from system)
     */
    public boolean rejectVoter(int voterId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get voter details for logging
            String getVoterQuery = "SELECT full_name, email, user_id FROM voters WHERE voter_id = ?";
            try (PreparedStatement getStmt = conn.prepareStatement(getVoterQuery)) {
                getStmt.setInt(1, voterId);
                ResultSet rs = getStmt.executeQuery();
                
                if (rs.next()) {
                    String fullName = rs.getString("full_name");
                    String email = rs.getString("email");
                    int userId = rs.getInt("user_id");
                    
                    // Delete from votes_fptp
                    String deleteFPTP = "DELETE FROM votes_fptp WHERE voter_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteFPTP)) {
                        stmt.setInt(1, voterId);
                        stmt.executeUpdate();
                    }
                    
                    // Delete from votes_pr
                    String deletePR = "DELETE FROM votes_pr WHERE voter_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deletePR)) {
                        stmt.setInt(1, voterId);
                        stmt.executeUpdate();
                    }
                    
                    // Delete from voters
                    String deleteVoter = "DELETE FROM voters WHERE voter_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteVoter)) {
                        stmt.setInt(1, voterId);
                        stmt.executeUpdate();
                    }
                    
                    // Delete from users
                    String deleteUser = "DELETE FROM users WHERE user_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteUser)) {
                        stmt.setInt(1, userId);
                        stmt.executeUpdate();
                    }
                    
                    conn.commit();
                    
                    // Send rejection email
                    sendRejectionEmail(email, fullName);
                    
                    return true;
                }
            }
            
            return false;
            
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
    
    /**
     * Send rejection email
     */
    private void sendRejectionEmail(String email, String fullName) {
        new Thread(() -> {
            try {
                String subject = "Voter Registration Status - Election Commission of Nepal";
                String content = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    ".header { background: #dc3545; color: white; padding: 20px; text-align: center; }" +
                    ".content { padding: 20px; background: #f9f9f9; }" +
                    ".footer { background: #333; color: white; padding: 10px; text-align: center; font-size: 12px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h2>Election Commission of Nepal</h2>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<h3>Dear " + fullName + ",</h3>" +
                    "<p>We regret to inform you that your voter registration has been <strong style='color:#dc3545;'>REJECTED</strong>.</p>" +
                    "<p>Possible reasons for rejection:</p>" +
                    "<ul>" +
                    "<li>Invalid or incomplete information provided</li>" +
                    "<li>Citizenship verification failed</li>" +
                    "<li>Duplicate registration detected</li>" +
                    "<li>Age verification failed (must be 18+)</li>" +
                    "</ul>" +
                    "<p>Please contact the Election Commission office for more information or to appeal this decision.</p>" +
                    "<p><strong>Contact:</strong> 01-4227799 or visit our office with your citizenship certificate.</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Election Commission of Nepal</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
                
                EmailUtil.sendCustomEmail(email, subject, content);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Log email sending status
     */
    private void logEmailStatus(int voterId, boolean success, String email) {
        String query = "INSERT INTO activity_log (user_id, action, ip_address) " +
                      "VALUES ((SELECT user_id FROM voters WHERE voter_id=?), ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, voterId);
            if (success) {
                pstmt.setString(2, "Voter ID email sent successfully to: " + email);
            } else {
                pstmt.setString(2, "Failed to send Voter ID email to: " + email);
            }
            pstmt.setString(3, "localhost");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean updateVoter(Voter voter) {
        String query = "UPDATE voters SET full_name=?, date_of_birth=?, age=?, citizenship_number=?, " +
                      "father_name=?, mother_name=?, address=?, phone_number=?, email=?, " +
                      "province_id=?, district_id=?, constituency_id=?, photo_path=? " +
                      "WHERE voter_id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, voter.getFullName());
            pstmt.setDate(2, new java.sql.Date(voter.getDateOfBirth().getTime()));
            pstmt.setInt(3, voter.getAge());
            pstmt.setString(4, voter.getCitizenshipNumber());
            pstmt.setString(5, voter.getFatherName());
            pstmt.setString(6, voter.getMotherName());
            pstmt.setString(7, voter.getAddress());
            pstmt.setString(8, voter.getPhoneNumber());
            pstmt.setString(9, voter.getEmail());
            pstmt.setInt(10, voter.getProvinceId());
            pstmt.setInt(11, voter.getDistrictId());
            pstmt.setInt(12, voter.getConstituencyId());
            pstmt.setString(13, voter.getPhotoPath());
            pstmt.setInt(14, voter.getVoterId());
            
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                logActivity(voter.getVoterId(), "Voter information updated");
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteVoter(int voterId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get user_id first
            String getUserQuery = "SELECT user_id, full_name, email FROM voters WHERE voter_id = ?";
            try (PreparedStatement getUserStmt = conn.prepareStatement(getUserQuery)) {
                getUserStmt.setInt(1, voterId);
                ResultSet rs = getUserStmt.executeQuery();
                
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String fullName = rs.getString("full_name");
                    String email = rs.getString("email");
                    
                    // Delete votes first (due to foreign key constraints)
                    String deleteVotesFPTP = "DELETE FROM votes_fptp WHERE voter_id = ?";
                    try (PreparedStatement deleteFPTPStmt = conn.prepareStatement(deleteVotesFPTP)) {
                        deleteFPTPStmt.setInt(1, voterId);
                        deleteFPTPStmt.executeUpdate();
                    }
                    
                    String deleteVotesPR = "DELETE FROM votes_pr WHERE voter_id = ?";
                    try (PreparedStatement deletePRStmt = conn.prepareStatement(deleteVotesPR)) {
                        deletePRStmt.setInt(1, voterId);
                        deletePRStmt.executeUpdate();
                    }
                    
                    // Delete from voters
                    String deleteVoterQuery = "DELETE FROM voters WHERE voter_id = ?";
                    try (PreparedStatement deleteVoterStmt = conn.prepareStatement(deleteVoterQuery)) {
                        deleteVoterStmt.setInt(1, voterId);
                        deleteVoterStmt.executeUpdate();
                    }
                    
                    // Delete from users
                    String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
                    try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {
                        deleteUserStmt.setInt(1, userId);
                        deleteUserStmt.executeUpdate();
                    }
                    
                    conn.commit();
                    
                    // Send deletion notification
                    sendDeletionEmail(email, fullName);
                    
                    return true;
                }
            }
            
            return false;
            
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
    
    /**
     * Send deletion notification email
     */
    private void sendDeletionEmail(String email, String fullName) {
        new Thread(() -> {
            try {
                String subject = "Account Deleted - Election Commission of Nepal";
                String content = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    ".header { background: #6c757d; color: white; padding: 20px; text-align: center; }" +
                    ".content { padding: 20px; background: #f9f9f9; }" +
                    ".footer { background: #333; color: white; padding: 10px; text-align: center; font-size: 12px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h2>Election Commission of Nepal</h2>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<h3>Dear " + fullName + ",</h3>" +
                    "<p>Your voter registration has been <strong>DELETED</strong> from our system.</p>" +
                    "<p>If you did not request this deletion, please contact the Election Commission immediately.</p>" +
                    "<p><strong>Contact:</strong> 01-4227799</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Election Commission of Nepal</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
                
                EmailUtil.sendCustomEmail(email, subject, content);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public String getProvinceName(int provinceId) {
        String query = "SELECT province_name FROM provinces WHERE province_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, provinceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("province_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    
    public String getDistrictName(int districtId) {
        String query = "SELECT district_name FROM districts WHERE district_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, districtId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("district_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    
    public String getConstituencyNumber(int constituencyId) {
        String query = "SELECT constituency_number FROM constituencies WHERE constituency_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, constituencyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "Constituency " + rs.getInt("constituency_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    
    private Voter extractVoterFromResultSet(ResultSet rs) throws SQLException {
        Voter voter = new Voter();
        voter.setVoterId(rs.getInt("voter_id"));
        voter.setUserId(rs.getInt("user_id"));
        voter.setFullName(rs.getString("full_name"));
        voter.setDateOfBirth(rs.getDate("date_of_birth"));
        voter.setAge(rs.getInt("age"));
        voter.setCitizenshipNumber(rs.getString("citizenship_number"));
        voter.setFatherName(rs.getString("father_name"));
        voter.setMotherName(rs.getString("mother_name"));
        voter.setAddress(rs.getString("address"));
        voter.setPhoneNumber(rs.getString("phone_number"));
        voter.setEmail(rs.getString("email"));
        voter.setProvinceId(rs.getInt("province_id"));
        voter.setDistrictId(rs.getInt("district_id"));
        voter.setConstituencyId(rs.getInt("constituency_id"));
        voter.setPhotoPath(rs.getString("photo_path"));
        voter.setApproved(rs.getBoolean("is_approved"));
        voter.setHasVotedFptp(rs.getBoolean("has_voted_fptp"));
        voter.setHasVotedPr(rs.getBoolean("has_voted_pr"));
        voter.setRegistrationDate(rs.getString("registration_date"));
        
        // Set names if available from joins
        try {
            voter.setProvinceName(rs.getString("province_name"));
            voter.setDistrictName(rs.getString("district_name"));
            voter.setConstituencyNumber(rs.getString("constituency_number"));
        } catch (SQLException e) {
            // These columns might not be in the result set
        }
        
        return voter;
    }
    
    private void logActivity(int voterId, String action) {
        String query = "INSERT INTO activity_log (user_id, action, ip_address) VALUES ((SELECT user_id FROM voters WHERE voter_id=?), ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, voterId);
            pstmt.setString(2, action);
            pstmt.setString(3, "localhost");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get voter count statistics
     */
    public int getVoterCount(String status) {
        String query = "";
        switch (status) {
            case "total":
                query = "SELECT COUNT(*) FROM voters";
                break;
            case "approved":
                query = "SELECT COUNT(*) FROM voters WHERE is_approved = true";
                break;
            case "pending":
                query = "SELECT COUNT(*) FROM voters WHERE is_approved = false";
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