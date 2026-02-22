package votingsystempro.models;

import java.util.Date;

public class NewsComment {
    private int commentId;
    private int newsId;
    private int userId;
    private int voterId;
    private String username;
    private String commentText;
    private int likes;
    private int dislikes;
    private boolean isApproved;
    private Date createdDate;
    
    public NewsComment() {}
    
    public NewsComment(int commentId, int newsId, int userId, String username, 
                       String commentText, Date createdDate) {
        this.commentId = commentId;
        this.newsId = newsId;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.createdDate = createdDate;
    }
    
    // Getters and Setters
    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }
    
    public int getNewsId() { return newsId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getVoterId() { return voterId; }
    public void setVoterId(int voterId) { this.voterId = voterId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public String getFormattedDate() {
        return new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(createdDate);
    }
}