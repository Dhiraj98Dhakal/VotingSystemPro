package votingsystempro.models;

import java.util.Date;

public class NewsLike {
    private int likeId;
    private int newsId;
    private int userId;
    private int voterId;
    private String username;
    private String likeType; // "like" or "dislike"
    private Date createdDate;
    
    public NewsLike() {}
    
    // Getters and Setters
    public int getLikeId() { return likeId; }
    public void setLikeId(int likeId) { this.likeId = likeId; }
    
    public int getNewsId() { return newsId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getVoterId() { return voterId; }
    public void setVoterId(int voterId) { this.voterId = voterId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getLikeType() { return likeType; }
    public void setLikeType(String likeType) { this.likeType = likeType; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}