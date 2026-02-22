package votingsystempro.controllers;

import votingsystempro.models.LiveVoteData;
import votingsystempro.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoteController {
    
    private static final Logger LOGGER = Logger.getLogger(VoteController.class.getName());
    private Connection connection;
    
    public VoteController() {
        try {
            connection = DatabaseConnection.getConnection();
            LOGGER.info("Database connection established successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
        }
    }
    
    public LiveVoteData getLiveVoteData() {
        LiveVoteData data = new LiveVoteData();
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getConnection();
            }
            
            // Get total votes (from both FPTP and PR tables)
            data.setTotalVotes(getTotalVotes());
            LOGGER.info("Total votes: " + data.getTotalVotes());
            
            // Get FPTP votes
            data.setFptpVotes(getFPTPVotes());
            LOGGER.info("FPTP votes: " + data.getFptpVotes());
            
            // Get PR votes
            data.setPrVotes(getPRVotes());
            LOGGER.info("PR votes: " + data.getPrVotes());
            
            // Get turnout percentage
            data.setTurnoutPercentage(getTurnoutPercentage());
            LOGGER.info("Turnout: " + data.getTurnoutPercentage() + "%");
            
            // Get leading party
            data.setLeadingParty(getLeadingParty());
            LOGGER.info("Leading party: " + data.getLeadingParty());
            
            // Get leading candidate
            data.setLeadingCandidate(getLeadingCandidate());
            LOGGER.info("Leading candidate: " + data.getLeadingCandidate());
            
            // Get FPTP results
            data.setFptpResults(getFPTPResults());
            LOGGER.info("FPTP results count: " + data.getFptpResults().size());
            
            // Get PR results
            data.setPrResults(getPRResults());
            LOGGER.info("PR results count: " + data.getPrResults().size());
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading live vote data", e);
        }
        
        return data;
    }
    
    private int getTotalVotes() throws SQLException {
        String query = "SELECT (SELECT COUNT(*) FROM votes_fptp) + (SELECT COUNT(*) FROM votes_pr) as total";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    private int getFPTPVotes() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM votes_fptp";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    private int getPRVotes() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM votes_pr";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    private int getTurnoutPercentage() throws SQLException {
        String voterQuery = "SELECT COUNT(*) as total FROM voters WHERE is_approved = true";
        String voteQuery = "SELECT (SELECT COUNT(*) FROM votes_fptp) + (SELECT COUNT(*) FROM votes_pr) as total";
        
        int totalVoters = 0;
        int totalVotes = 0;
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(voterQuery);
            if (rs.next()) {
                totalVoters = rs.getInt("total");
            }
            
            rs = stmt.executeQuery(voteQuery);
            if (rs.next()) {
                totalVotes = rs.getInt("total");
            }
        }
        
        if (totalVoters > 0) {
            return (int) Math.round((totalVotes * 100.0) / totalVoters);
        }
        return 0;
    }
    
    private String getLeadingParty() throws SQLException {
        String query = "SELECT p.party_name, COUNT(vp.vote_id) as vote_count " +
                      "FROM parties p " +
                      "LEFT JOIN votes_pr vp ON p.party_id = vp.party_id " +
                      "GROUP BY p.party_id, p.party_name " +
                      "ORDER BY vote_count DESC LIMIT 1";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getInt("vote_count") > 0) {
                return rs.getString("party_name") + " (" + rs.getInt("vote_count") + " votes)";
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting leading party: " + e.getMessage());
        }
        return "No votes yet";
    }
    
    private String getLeadingCandidate() throws SQLException {
        String query = "SELECT c.candidate_name, COUNT(vf.vote_id) as vote_count " +
                      "FROM candidates c " +
                      "LEFT JOIN votes_fptp vf ON c.candidate_id = vf.candidate_id " +
                      "WHERE c.position = 'fptp' " +
                      "GROUP BY c.candidate_id, c.candidate_name " +
                      "ORDER BY vote_count DESC LIMIT 1";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getInt("vote_count") > 0) {
                return rs.getString("candidate_name") + " (" + rs.getInt("vote_count") + " votes)";
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting leading candidate: " + e.getMessage());
        }
        return "No votes yet";
    }
    
    private List<Object[]> getFPTPResults() throws SQLException {
        List<Object[]> results = new ArrayList<>();
        
        String query = "SELECT " +
                      "c.candidate_name, " +
                      "COALESCE(p.party_name, 'Independent') as party_name, " +
                      "CONCAT(d.district_name, ' - ', cn.constituency_number) as constituency, " +
                      "COUNT(vf.vote_id) as vote_count, " +
                      "ROUND(COUNT(vf.vote_id) * 100.0 / NULLIF(SUM(COUNT(vf.vote_id)) OVER (PARTITION BY cn.constituency_id), 0), 1) as percentage " +
                      "FROM candidates c " +
                      "LEFT JOIN parties p ON c.party_id = p.party_id " +
                      "LEFT JOIN constituencies cn ON c.constituency_id = cn.constituency_id " +
                      "LEFT JOIN districts d ON cn.district_id = d.district_id " +
                      "LEFT JOIN votes_fptp vf ON c.candidate_id = vf.candidate_id " +
                      "WHERE c.position = 'fptp' " +
                      "GROUP BY c.candidate_id, c.candidate_name, p.party_name, cn.constituency_id, d.district_name, cn.constituency_number " +
                      "ORDER BY vote_count DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("candidate_name") != null ? rs.getString("candidate_name") : "Unknown";
                row[1] = rs.getString("party_name") != null ? rs.getString("party_name") : "Independent";
                row[2] = rs.getString("constituency") != null ? rs.getString("constituency") : "Unknown";
                row[3] = rs.getInt("vote_count");
                row[4] = rs.getDouble("percentage");
                results.add(row);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting FPTP results: " + e.getMessage());
        }
        
        return results;
    }
    
    private List<Object[]> getPRResults() throws SQLException {
        List<Object[]> results = new ArrayList<>();
        
        String query = "SELECT " +
                      "p.party_name, " +
                      "COUNT(vp.vote_id) as vote_count, " +
                      "ROUND(COUNT(vp.vote_id) * 100.0 / NULLIF(SUM(COUNT(vp.vote_id)) OVER (), 0), 1) as percentage, " +
                      "FLOOR(COUNT(vp.vote_id) / 50000) as seats_estimated " +
                      "FROM parties p " +
                      "LEFT JOIN votes_pr vp ON p.party_id = vp.party_id " +
                      "GROUP BY p.party_id, p.party_name " +
                      "HAVING COUNT(vp.vote_id) > 0 " +
                      "ORDER BY vote_count DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("party_name") != null ? rs.getString("party_name") : "Unknown";
                row[1] = rs.getInt("vote_count");
                row[2] = rs.getDouble("percentage");
                row[3] = rs.getInt("seats_estimated");
                results.add(row);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting PR results: " + e.getMessage());
        }
        
        return results;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing connection", e);
        }
    }
}