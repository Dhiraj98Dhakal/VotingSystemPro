package votingsystempro.models;

public class User {
    private int userId;
    private String voterId;
    private String password;
    private String userType;
    private String createdAt;
    
    public User() {}
    
    public User(int userId, String voterId, String password, String userType, String createdAt) {
        this.userId = userId;
        this.voterId = voterId;
        this.password = password;
        this.userType = userType;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getVoterId() {
        return voterId;
    }
    
    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", voterId='" + voterId + '\'' +
                ", userType='" + userType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}