package votingsystempro.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    
    // Update these values according to your MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/votedb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root"; // Change this to your MySQL username
    private static final String PASSWORD = ""; // Change this to your MySQL password
    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Create connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LOGGER.info("Database connection established successfully to votedb");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new SQLException("MySQL JDBC Driver not found. Please add MySQL connector JAR to classpath.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to database", e);
            throw new SQLException("Failed to connect to database. Please check:\n" +
                                  "1. MySQL server is running\n" +
                                  "2. Database 'votedb' exists\n" +
                                  "3. Username/password is correct\n" +
                                  "4. MySQL connector JAR is in classpath", e);
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
    
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection test failed", e);
            return false;
        }
    }
}