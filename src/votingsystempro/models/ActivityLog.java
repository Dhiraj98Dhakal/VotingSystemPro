package votingsystempro.models;

import java.util.Date;

public class ActivityLog {
    private int logId;
    private int userId;
    private String voterId;
    private String userName;
    private String action;
    private Date timestamp;
    private String ipAddress;
    
    public ActivityLog() {}
    
    public ActivityLog(int logId, int userId, String action, Date timestamp, String ipAddress) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }
    
    // Getters and Setters
    public int getLogId() {
        return logId;
    }
    
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getFormattedTimestamp() {
        if (timestamp != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        }
        return "N/A";
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s", getFormattedTimestamp(), voterId, action);
    }
}