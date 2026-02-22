package votingsystempro.models;

import java.util.Date;
import java.util.List;

public class News {
    private int newsId;
    private String title;
    private String content;
    private String summary;
    private String imagePath;
    private int authorId;
    private String authorName;
    private int views;
    private int likesCount;
    private int dislikesCount;
    private int commentsCount;
    private boolean isActive;
    private boolean isFeatured;
    private Date publishedDate;
    private Date updatedDate;
    
    // For UI
    private List<NewsComment> comments;
    private String userReaction; // "like", "dislike", or null
    
    public News() {}
    
    public News(int newsId, String title, String content, String summary, String imagePath,
                int authorId, String authorName, Date publishedDate) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.imagePath = imagePath;
        this.authorId = authorId;
        this.authorName = authorName;
        this.publishedDate = publishedDate;
    }
    
    // Getters and Setters
    public int getNewsId() { return newsId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    
    public int getDislikesCount() { return dislikesCount; }
    public void setDislikesCount(int dislikesCount) { this.dislikesCount = dislikesCount; }
    
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }
    
    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }
    
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    
    public List<NewsComment> getComments() { return comments; }
    public void setComments(List<NewsComment> comments) { this.comments = comments; }
    
    public String getUserReaction() { return userReaction; }
    public void setUserReaction(String userReaction) { this.userReaction = userReaction; }
    
    public String getFormattedDate() {
        return new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(publishedDate);
    }
}