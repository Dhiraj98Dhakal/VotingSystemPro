package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.Candidate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateController {
    
    public boolean addCandidate(Candidate candidate) {
        String query = "INSERT INTO candidates (candidate_name, party_id, photo_path, position, " +
                      "province_id, district_id, constituency_id, biography) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, candidate.getCandidateName());
            pstmt.setInt(2, candidate.getPartyId());
            pstmt.setString(3, candidate.getPhotoPath());
            pstmt.setString(4, candidate.getPosition());
            setLocationParameters(pstmt, candidate, 5);
            pstmt.setString(8, candidate.getBiography());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String query = "SELECT c.*, p.party_name FROM candidates c " +
                      "LEFT JOIN parties p ON c.party_id = p.party_id " +
                      "ORDER BY c.candidate_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                candidates.add(extractCandidateFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }
    
    public List<Candidate> getCandidatesByPosition(String position) {
        List<Candidate> candidates = new ArrayList<>();
        String query = "SELECT c.*, p.party_name FROM candidates c " +
                      "LEFT JOIN parties p ON c.party_id = p.party_id " +
                      "WHERE c.position = ? ORDER BY c.candidate_name";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                candidates.add(extractCandidateFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }
    
    public List<Candidate> getCandidatesByConstituency(int constituencyId, String position) {
        List<Candidate> candidates = new ArrayList<>();
        String query = "SELECT c.*, p.party_name FROM candidates c " +
                      "LEFT JOIN parties p ON c.party_id = p.party_id " +
                      "WHERE c.constituency_id = ? AND c.position = ? " +
                      "ORDER BY c.candidate_name";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, constituencyId);
            pstmt.setString(2, position);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                candidates.add(extractCandidateFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }
    
    public Candidate getCandidateById(int candidateId) {
        String query = "SELECT c.*, p.party_name FROM candidates c " +
                      "LEFT JOIN parties p ON c.party_id = p.party_id " +
                      "WHERE c.candidate_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, candidateId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCandidateFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateCandidate(Candidate candidate) {
        String query = "UPDATE candidates SET candidate_name=?, party_id=?, photo_path=?, " +
                      "position=?, province_id=?, district_id=?, constituency_id=?, biography=? " +
                      "WHERE candidate_id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, candidate.getCandidateName());
            pstmt.setInt(2, candidate.getPartyId());
            pstmt.setString(3, candidate.getPhotoPath());
            pstmt.setString(4, candidate.getPosition());
            setLocationParameters(pstmt, candidate, 5);
            pstmt.setString(8, candidate.getBiography());
            pstmt.setInt(9, candidate.getCandidateId());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCandidate(int candidateId) {
        String query = "DELETE FROM candidates WHERE candidate_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, candidateId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void setLocationParameters(PreparedStatement pstmt, Candidate candidate, int startIndex) throws SQLException {
        if (candidate.getProvinceId() > 0) {
            pstmt.setInt(startIndex, candidate.getProvinceId());
        } else {
            pstmt.setNull(startIndex, Types.INTEGER);
        }
        
        if (candidate.getDistrictId() > 0) {
            pstmt.setInt(startIndex + 1, candidate.getDistrictId());
        } else {
            pstmt.setNull(startIndex + 1, Types.INTEGER);
        }
        
        if (candidate.getConstituencyId() > 0) {
            pstmt.setInt(startIndex + 2, candidate.getConstituencyId());
        } else {
            pstmt.setNull(startIndex + 2, Types.INTEGER);
        }
    }
    
    private Candidate extractCandidateFromResultSet(ResultSet rs) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(rs.getInt("candidate_id"));
        candidate.setCandidateName(rs.getString("candidate_name"));
        candidate.setPartyId(rs.getInt("party_id"));
        candidate.setPartyName(rs.getString("party_name"));
        candidate.setPhotoPath(rs.getString("photo_path"));
        candidate.setPosition(rs.getString("position"));
        candidate.setProvinceId(rs.getInt("province_id"));
        candidate.setDistrictId(rs.getInt("district_id"));
        candidate.setConstituencyId(rs.getInt("constituency_id"));
        candidate.setBiography(rs.getString("biography"));
        return candidate;
    }
    
    public boolean castVoteFPTP(int voterId, int candidateId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if already voted
            String checkQuery = "SELECT has_voted_fptp FROM voters WHERE voter_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, voterId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("has_voted_fptp")) {
                return false; // Already voted
            }
            
            // Insert vote
            String voteQuery = "INSERT INTO votes_fptp (voter_id, candidate_id, ip_address) VALUES (?, ?, ?)";
            PreparedStatement voteStmt = conn.prepareStatement(voteQuery);
            voteStmt.setInt(1, voterId);
            voteStmt.setInt(2, candidateId);
            voteStmt.setString(3, "localhost");
            voteStmt.executeUpdate();
            
            // Update voter status
            String updateQuery = "UPDATE voters SET has_voted_fptp = true WHERE voter_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, voterId);
            updateStmt.executeUpdate();
            
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
    
    public boolean castVotePR(int voterId, int partyId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if already voted
            String checkQuery = "SELECT has_voted_pr FROM voters WHERE voter_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, voterId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("has_voted_pr")) {
                return false; // Already voted
            }
            
            // Insert vote
            String voteQuery = "INSERT INTO votes_pr (voter_id, party_id, ip_address) VALUES (?, ?, ?)";
            PreparedStatement voteStmt = conn.prepareStatement(voteQuery);
            voteStmt.setInt(1, voterId);
            voteStmt.setInt(2, partyId);
            voteStmt.setString(3, "localhost");
            voteStmt.executeUpdate();
            
            // Update voter status
            String updateQuery = "UPDATE voters SET has_voted_pr = true WHERE voter_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, voterId);
            updateStmt.executeUpdate();
            
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
}