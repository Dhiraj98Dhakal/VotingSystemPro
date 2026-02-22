package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationController {
    
    public Map<Integer, String> getAllProvinces() {
        Map<Integer, String> provinces = new HashMap<>();
        String query = "SELECT province_id, province_name FROM provinces ORDER BY province_number";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                provinces.put(rs.getInt("province_id"), rs.getString("province_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return provinces;
    }
    
    public Map<Integer, String> getDistrictsByProvince(int provinceId) {
        Map<Integer, String> districts = new HashMap<>();
        String query = "SELECT district_id, district_name FROM districts WHERE province_id = ? ORDER BY district_name";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, provinceId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                districts.put(rs.getInt("district_id"), rs.getString("district_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return districts;
    }
    
    public Map<Integer, String> getConstituenciesByDistrict(int districtId) {
        Map<Integer, String> constituencies = new HashMap<>();
        String query = "SELECT constituency_id, constituency_number FROM constituencies WHERE district_id = ? ORDER BY constituency_number";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, districtId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                constituencies.put(rs.getInt("constituency_id"), 
                    "Constituency No. " + rs.getInt("constituency_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return constituencies;
    }
    
    // New method: Get constituency details with district and province
    public Map<String, Object> getConstituencyDetails(int constituencyId) {
        Map<String, Object> details = new HashMap<>();
        String query = "SELECT c.*, d.district_name, p.province_name, p.province_number " +
                      "FROM constituencies c " +
                      "JOIN districts d ON c.district_id = d.district_id " +
                      "JOIN provinces p ON d.province_id = p.province_id " +
                      "WHERE c.constituency_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, constituencyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                details.put("constituency_id", rs.getInt("constituency_id"));
                details.put("constituency_number", rs.getInt("constituency_number"));
                details.put("district_id", rs.getInt("district_id"));
                details.put("district_name", rs.getString("district_name"));
                details.put("province_name", rs.getString("province_name"));
                details.put("province_number", rs.getInt("province_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    // New method: Get total constituencies count
    public int getTotalConstituencies() {
        String query = "SELECT COUNT(*) FROM constituencies";
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
    
    // New method: Get constituencies by province
    public Map<Integer, String> getConstituenciesByProvince(int provinceId) {
        Map<Integer, String> constituencies = new HashMap<>();
        String query = "SELECT c.constituency_id, c.constituency_number, d.district_name " +
                      "FROM constituencies c " +
                      "JOIN districts d ON c.district_id = d.district_id " +
                      "WHERE d.province_id = ? " +
                      "ORDER BY d.district_name, c.constituency_number";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, provinceId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                constituencies.put(rs.getInt("constituency_id"), 
                    rs.getString("district_name") + " - Constituency " + rs.getInt("constituency_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return constituencies;
    }
    
    public boolean addProvince(String provinceName, int provinceNumber) {
        String query = "INSERT INTO provinces (province_name, province_number) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, provinceName);
            pstmt.setInt(2, provinceNumber);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateProvince(int provinceId, String provinceName, int provinceNumber) {
        String query = "UPDATE provinces SET province_name=?, province_number=? WHERE province_id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, provinceName);
            pstmt.setInt(2, provinceNumber);
            pstmt.setInt(3, provinceId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProvince(int provinceId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First delete all constituencies in districts of this province
            String deleteConstituencies = "DELETE c FROM constituencies c " +
                                         "INNER JOIN districts d ON c.district_id = d.district_id " +
                                         "WHERE d.province_id = ?";
            PreparedStatement deleteConstStmt = conn.prepareStatement(deleteConstituencies);
            deleteConstStmt.setInt(1, provinceId);
            deleteConstStmt.executeUpdate();
            
            // Delete all districts in this province
            String deleteDistricts = "DELETE FROM districts WHERE province_id = ?";
            PreparedStatement deleteDistStmt = conn.prepareStatement(deleteDistricts);
            deleteDistStmt.setInt(1, provinceId);
            deleteDistStmt.executeUpdate();
            
            // Delete province
            String deleteProvince = "DELETE FROM provinces WHERE province_id = ?";
            PreparedStatement deleteProvStmt = conn.prepareStatement(deleteProvince);
            deleteProvStmt.setInt(1, provinceId);
            deleteProvStmt.executeUpdate();
            
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
    
    public boolean addDistrict(String districtName, int provinceId) {
        String query = "INSERT INTO districts (district_name, province_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, districtName);
            pstmt.setInt(2, provinceId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateDistrict(int districtId, String districtName, int provinceId) {
        String query = "UPDATE districts SET district_name=?, province_id=? WHERE district_id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, districtName);
            pstmt.setInt(2, provinceId);
            pstmt.setInt(3, districtId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDistrict(int districtId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete constituencies first
            String deleteConstituencies = "DELETE FROM constituencies WHERE district_id = ?";
            PreparedStatement deleteConstStmt = conn.prepareStatement(deleteConstituencies);
            deleteConstStmt.setInt(1, districtId);
            deleteConstStmt.executeUpdate();
            
            // Delete district
            String deleteDistrict = "DELETE FROM districts WHERE district_id = ?";
            PreparedStatement deleteDistStmt = conn.prepareStatement(deleteDistrict);
            deleteDistStmt.setInt(1, districtId);
            deleteDistStmt.executeUpdate();
            
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
    
    public boolean addConstituency(int constituencyNumber, int districtId) {
        String query = "INSERT INTO constituencies (constituency_number, district_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, constituencyNumber);
            pstmt.setInt(2, districtId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateConstituency(int constituencyId, int constituencyNumber, int districtId) {
        String query = "UPDATE constituencies SET constituency_number=?, district_id=? WHERE constituency_id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, constituencyNumber);
            pstmt.setInt(2, districtId);
            pstmt.setInt(3, constituencyId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteConstituency(int constituencyId) {
        String query = "DELETE FROM constituencies WHERE constituency_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, constituencyId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getAllDistrictsWithProvince() {
        List<String> districts = new ArrayList<>();
        String query = "SELECT d.district_name, p.province_name FROM districts d " +
                      "INNER JOIN provinces p ON d.province_id = p.province_id " +
                      "ORDER BY p.province_number, d.district_name";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                districts.add(rs.getString("district_name") + " (" + rs.getString("province_name") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return districts;
    }
}