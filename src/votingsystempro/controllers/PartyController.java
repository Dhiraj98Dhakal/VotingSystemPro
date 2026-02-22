package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.Party;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartyController {
    
    public boolean addParty(Party party) {
        String query = "INSERT INTO parties (party_name, party_logo_path, description, established_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, party.getPartyName());
            pstmt.setString(2, party.getPartyLogoPath());
            pstmt.setString(3, party.getDescription());
            pstmt.setDate(4, party.getEstablishedDate() != null ? 
                new java.sql.Date(party.getEstablishedDate().getTime()) : null);
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Party> getAllParties() {
        List<Party> parties = new ArrayList<>();
        String query = "SELECT * FROM parties ORDER BY party_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Party party = new Party();
                party.setPartyId(rs.getInt("party_id"));
                party.setPartyName(rs.getString("party_name"));
                party.setPartyLogoPath(rs.getString("party_logo_path"));
                party.setDescription(rs.getString("description"));
                party.setEstablishedDate(rs.getDate("established_date"));
                parties.add(party);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parties;
    }
    
    public Party getPartyById(int partyId) {
        String query = "SELECT * FROM parties WHERE party_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, partyId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Party party = new Party();
                party.setPartyId(rs.getInt("party_id"));
                party.setPartyName(rs.getString("party_name"));
                party.setPartyLogoPath(rs.getString("party_logo_path"));
                party.setDescription(rs.getString("description"));
                party.setEstablishedDate(rs.getDate("established_date"));
                return party;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateParty(Party party) {
        String query = "UPDATE parties SET party_name=?, party_logo_path=?, description=?, established_date=? WHERE party_id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, party.getPartyName());
            pstmt.setString(2, party.getPartyLogoPath());
            pstmt.setString(3, party.getDescription());
            pstmt.setDate(4, party.getEstablishedDate() != null ? 
                new java.sql.Date(party.getEstablishedDate().getTime()) : null);
            pstmt.setInt(5, party.getPartyId());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteParty(int partyId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First check if party has candidates
            String checkQuery = "SELECT COUNT(*) FROM candidates WHERE party_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, partyId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Delete associated candidates first
                String deleteCandidatesQuery = "DELETE FROM candidates WHERE party_id = ?";
                PreparedStatement deleteCandidatesStmt = conn.prepareStatement(deleteCandidatesQuery);
                deleteCandidatesStmt.setInt(1, partyId);
                deleteCandidatesStmt.executeUpdate();
            }
            
            // Delete party
            String deletePartyQuery = "DELETE FROM parties WHERE party_id = ?";
            PreparedStatement deletePartyStmt = conn.prepareStatement(deletePartyQuery);
            deletePartyStmt.setInt(1, partyId);
            deletePartyStmt.executeUpdate();
            
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
    
    public List<Party> getPartiesWithCandidates() {
        List<Party> parties = new ArrayList<>();
        String query = "SELECT DISTINCT p.* FROM parties p " +
                      "INNER JOIN candidates c ON p.party_id = c.party_id " +
                      "ORDER BY p.party_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Party party = new Party();
                party.setPartyId(rs.getInt("party_id"));
                party.setPartyName(rs.getString("party_name"));
                party.setPartyLogoPath(rs.getString("party_logo_path"));
                parties.add(party);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parties;
    }
    
    public int getTotalVotesForParty(int partyId) {
        String query = "SELECT COUNT(*) FROM votes_pr WHERE party_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, partyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}