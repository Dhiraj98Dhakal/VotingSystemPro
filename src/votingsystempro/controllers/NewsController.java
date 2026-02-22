package votingsystempro.controllers;

import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.News;
import votingsystempro.models.NewsComment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsController {
    
    // ==================== NEWS MANAGEMENT ====================
    
    public boolean addNews(News news) {
        String query = "INSERT INTO news (title, content, summary, image_path, author_id, author_name, is_featured) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, news.getTitle());
            pstmt.setString(2, news.getContent());
            pstmt.setString(3, news.getSummary());
            pstmt.setString(4, news.getImagePath());
            pstmt.setInt(5, news.getAuthorId());
            pstmt.setString(6, news.getAuthorName());
            pstmt.setBoolean(7, news.isFeatured());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding news: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<News> getAllNews() {
        List<News> newsList = new ArrayList<>();
        String query = "SELECT * FROM news WHERE is_active = true ORDER BY is_featured DESC, published_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                newsList.add(extractNewsFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all news: " + e.getMessage());
            e.printStackTrace();
        }
        return newsList;
    }
    
 public News getNewsById(int newsId) {
    News news = null;
    String query = "SELECT * FROM news WHERE news_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, newsId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            news = extractNewsFromResultSet(rs);
        }
    } catch (SQLException e) {
        System.err.println("Error getting news by ID: " + e.getMessage());
        e.printStackTrace();
    }
    
    // Increment view count in a separate thread with its own connection
    if (news != null) {
        final int id = newsId;
        new Thread(() -> incrementViewCount(id)).start();
    }
    
    return news;
}
    
    public boolean updateNews(News news) {
        String query = "UPDATE news SET title=?, content=?, summary=?, image_path=?, is_featured=? WHERE news_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, news.getTitle());
            pstmt.setString(2, news.getContent());
            pstmt.setString(3, news.getSummary());
            pstmt.setString(4, news.getImagePath());
            pstmt.setBoolean(5, news.isFeatured());
            pstmt.setInt(6, news.getNewsId());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating news: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteNews(int newsId) {
        String query = "UPDATE news SET is_active = false WHERE news_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newsId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting news: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean permanentDeleteNews(int newsId) {
        String query = "DELETE FROM news WHERE news_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newsId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error permanently deleting news: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
private void incrementViewCount(int newsId) {
    // Use try-with-resources to auto-close connection
    String query = "UPDATE news SET views = views + 1 WHERE news_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, newsId);
        pstmt.executeUpdate();
        
    } catch (SQLException e) {
        System.err.println("Error incrementing view count: " + e.getMessage());
        // Don't print stack trace for connection errors
    }
}
    
    // ==================== LIKE/DISLIKE SYSTEM - PER USER PER POST ====================
    
    /**
     * Add like/dislike for a news article
     * Each user can only have ONE reaction per news
     * Toggle functionality: Clicking same reaction removes it
     * Switching: Changing from like to dislike updates counts accordingly
     */
    public boolean addLike(int newsId, int userId, String username, String likeType) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if user already reacted to this news
            String checkQuery = "SELECT like_type FROM news_likes WHERE news_id = ? AND user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, newsId);
                checkStmt.setInt(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // User already has a reaction
                    String existingType = rs.getString("like_type");
                    
                    if (existingType.equals(likeType)) {
                        // Same reaction - TOGGLE OFF (remove it)
                        String deleteQuery = "DELETE FROM news_likes WHERE news_id = ? AND user_id = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, newsId);
                            deleteStmt.setInt(2, userId);
                            deleteStmt.executeUpdate();
                        }
                        
                        // Decrement the appropriate count - FIXED: use correct column names
                        if ("like".equals(likeType)) {
                            updateNewsCount(conn, newsId, "likes_count", -1);
                        } else {
                            updateNewsCount(conn, newsId, "dislikes_count", -1);
                        }
                        
                        System.out.println("User " + userId + " removed their " + likeType + " from news " + newsId);
                        
                    } else {
                        // Different reaction - UPDATE (switch from like to dislike or vice versa)
                        String updateQuery = "UPDATE news_likes SET like_type = ? WHERE news_id = ? AND user_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, likeType);
                            updateStmt.setInt(2, newsId);
                            updateStmt.setInt(3, userId);
                            updateStmt.executeUpdate();
                        }
                        
                        // Adjust counts based on switch - FIXED: use correct column names
                        if ("like".equals(likeType)) {
                            // Switching from dislike to like
                            updateNewsCount(conn, newsId, "likes_count", 1);
                            updateNewsCount(conn, newsId, "dislikes_count", -1);
                            System.out.println("User " + userId + " switched from dislike to like on news " + newsId);
                        } else {
                            // Switching from like to dislike
                            updateNewsCount(conn, newsId, "dislikes_count", 1);
                            updateNewsCount(conn, newsId, "likes_count", -1);
                            System.out.println("User " + userId + " switched from like to dislike on news " + newsId);
                        }
                    }
                } else {
                    // New reaction - INSERT
                    String insertQuery = "INSERT INTO news_likes (news_id, user_id, username, like_type) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, newsId);
                        insertStmt.setInt(2, userId);
                        insertStmt.setString(3, username);
                        insertStmt.setString(4, likeType);
                        insertStmt.executeUpdate();
                    }
                    
                    // Increment the appropriate count - FIXED: use correct column names
                    if ("like".equals(likeType)) {
                        updateNewsCount(conn, newsId, "likes_count", 1);
                    } else {
                        updateNewsCount(conn, newsId, "dislikes_count", 1);
                    }
                    System.out.println("User " + userId + " added new " + likeType + " to news " + newsId);
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
                ex.printStackTrace();
            }
            System.err.println("Error in addLike: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Helper method to update news counts - FIXED: using correct column names
     */
    private void updateNewsCount(Connection conn, int newsId, String countField, int delta) throws SQLException {
        // Make sure countField is either 'likes_count' or 'dislikes_count'
        if (!countField.equals("likes_count") && !countField.equals("dislikes_count")) {
            System.err.println("Invalid count field: " + countField);
            return;
        }
        
        String query = "UPDATE news SET " + countField + " = " + countField + " + ? WHERE news_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, delta);
            pstmt.setInt(2, newsId);
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("Updated " + countField + " for news " + newsId + " by " + delta);
            }
        }
    }
    
    /**
     * Get user's reaction to a specific news
     * Returns "like", "dislike", or null if no reaction
     */
    public String getUserReaction(int newsId, int userId) {
        String query = "SELECT like_type FROM news_likes WHERE news_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newsId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String reaction = rs.getString("like_type");
                System.out.println("User " + userId + " reaction to news " + newsId + ": " + reaction);
                return reaction;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user reaction: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("User " + userId + " has no reaction to news " + newsId);
        return null;
    }
    
    /**
     * Get all users who liked a specific news
     */
    public List<String> getUsersWhoLiked(int newsId) {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM news_likes WHERE news_id = ? AND like_type = 'like' ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newsId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users who liked: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get all users who disliked a specific news
     */
    public List<String> getUsersWhoDisliked(int newsId) {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM news_likes WHERE news_id = ? AND like_type = 'dislike' ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newsId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users who disliked: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    // ==================== COMMENT SYSTEM ====================
    
    public boolean addComment(int newsId, int userId, String username, String commentText) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String query = "INSERT INTO news_comments (news_id, user_id, username, comment_text) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, newsId);
                pstmt.setInt(2, userId);
                pstmt.setString(3, username);
                pstmt.setString(4, commentText);
                pstmt.executeUpdate();
            }
            
            // Increment comments count in news table
            String updateQuery = "UPDATE news SET comments_count = comments_count + 1 WHERE news_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, newsId);
                updateStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("User " + userId + " added comment to news " + newsId);
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error adding comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<NewsComment> getComments(int newsId) {
        List<NewsComment> comments = new ArrayList<>();
        String query = "SELECT * FROM news_comments WHERE news_id = ? AND is_approved = true ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newsId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                NewsComment comment = new NewsComment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setNewsId(rs.getInt("news_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setUsername(rs.getString("username"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setLikes(rs.getInt("likes"));
                comment.setDislikes(rs.getInt("dislikes"));
                comment.setApproved(rs.getBoolean("is_approved"));
                comment.setCreatedDate(rs.getTimestamp("created_date"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return comments;
    }
    
    public boolean deleteComment(int commentId) {
        String query = "DELETE FROM news_comments WHERE comment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== STATISTICS ====================
    
    public int getTotalNewsCount() {
        String query = "SELECT COUNT(*) FROM news WHERE is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total news count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public int getFeaturedNewsCount() {
        String query = "SELECT COUNT(*) FROM news WHERE is_active = true AND is_featured = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting featured news count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public int getTotalCommentsCount() {
        String query = "SELECT SUM(comments_count) FROM news WHERE is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total comments count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public int getTotalLikesCount() {
        String query = "SELECT SUM(likes_count) FROM news WHERE is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total likes count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public int getTotalDislikesCount() {
        String query = "SELECT SUM(dislikes_count) FROM news WHERE is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total dislikes count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // ==================== SEARCH ====================
    
    public List<News> searchNews(String keyword) {
        List<News> newsList = new ArrayList<>();
        String query = "SELECT * FROM news WHERE is_active = true AND (title LIKE ? OR content LIKE ? OR summary LIKE ?) " +
                       "ORDER BY is_featured DESC, published_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                newsList.add(extractNewsFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching news: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newsList;
    }
    
    public List<News> getFeaturedNews() {
        List<News> newsList = new ArrayList<>();
        String query = "SELECT * FROM news WHERE is_active = true AND is_featured = true ORDER BY published_date DESC LIMIT 10";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                newsList.add(extractNewsFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting featured news: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newsList;
    }
    
    // ==================== UTILITY METHODS ====================
    
    private News extractNewsFromResultSet(ResultSet rs) throws SQLException {
        News news = new News();
        news.setNewsId(rs.getInt("news_id"));
        news.setTitle(rs.getString("title"));
        news.setContent(rs.getString("content"));
        news.setSummary(rs.getString("summary"));
        news.setImagePath(rs.getString("image_path"));
        news.setAuthorId(rs.getInt("author_id"));
        news.setAuthorName(rs.getString("author_name"));
        news.setViews(rs.getInt("views"));
        news.setLikesCount(rs.getInt("likes_count"));
        news.setDislikesCount(rs.getInt("dislikes_count"));
        news.setCommentsCount(rs.getInt("comments_count"));
        news.setActive(rs.getBoolean("is_active"));
        news.setFeatured(rs.getBoolean("is_featured"));
        news.setPublishedDate(rs.getTimestamp("published_date"));
        news.setUpdatedDate(rs.getTimestamp("updated_date"));
        return news;
    }
}