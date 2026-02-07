package com.revconnect.dao;

import com.revconnect.entities.Post;
import com.revconnect.entities.User;
import com.revconnect.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public boolean createPost(Post post) {
        String sql = "INSERT INTO posts (user_id, content, hashtags, is_promotional) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            pstmt.setInt(1, post.getUserId());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getHashtags());
            pstmt.setInt(4, post.isPromotional() ? 1 : 0);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    post.setId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Post creation failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePost(int postId, int userId, String newContent) {
        String sql = "UPDATE posts SET content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, postId);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Post update failed: " + e.getMessage());
        }
        return false;
    }

    public boolean deletePost(int postId, int userId) {
        String sql = "DELETE FROM posts WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Post deletion failed: " + e.getMessage());
        }
        return false;
    }

    public List<Post> getUserFeed(int userId, int limit) {
        List<Post> feed = new ArrayList<>();


        String sql = "SELECT p.id, p.user_id, p.content, p.hashtags, p.is_promotional, p.created_at, " +
                "u.username, u.name, u.user_type, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.post_id = p.id) AS like_count, " +
                "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count, " +
                "(SELECT COUNT(*) FROM reposts r WHERE r.original_post_id = p.id) AS share_count " +
                "FROM posts p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.user_id = ? " + // User's own posts
                "OR p.user_id IN (SELECT followed_id FROM follows WHERE follower_id = ?) " + // Following
                "OR p.user_id IN (SELECT requester_id FROM connections WHERE receiver_id = ? AND status = 'ACCEPTED') " + // Connections
                "OR p.user_id IN (SELECT receiver_id FROM connections WHERE requester_id = ? AND status = 'ACCEPTED') " + // Connections
                "ORDER BY p.created_at DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);
            pstmt.setInt(5, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                feed.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            System.err.println("Feed fetch failed: " + e.getMessage());
        }
        return feed;
    }


public List<Post> getUserPosts(int userId) {
    List<Post> posts = new ArrayList<>();
    // Simplified query without problematic columns
    String sql = "SELECT p.id, p.user_id, p.content, p.hashtags, p.is_promotional, p.created_at, " +
            "(SELECT COUNT(*) FROM likes l WHERE l.post_id = p.id) AS like_count, " +
            "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count, " +
            "(SELECT COUNT(*) FROM reposts r WHERE r.original_post_id = p.id) AS share_count " +
            "FROM posts p WHERE p.user_id = ? ORDER BY p.created_at DESC";

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Post post = new Post();
            post.setId(rs.getInt("id"));
            post.setUserId(rs.getInt("user_id"));
            post.setContent(rs.getString("content"));
            post.setHashtags(rs.getString("hashtags"));
            post.setPromotional(rs.getInt("is_promotional") == 1);
            post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            post.setLikeCount(rs.getInt("like_count"));
            post.setCommentCount(rs.getInt("comment_count"));
            post.setShareCount(rs.getInt("share_count"));

            // Create a basic user object
            User user = new User();
            user.setUsername(getUsernameFromId(userId));
            post.setUser(user);

            posts.add(post);
        }
    } catch (SQLException e) {
        System.err.println("User posts fetch failed: " + e.getMessage());
    }
    return posts;
}

    // Helper method to get username
    private String getUsernameFromId(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            // Silent fail
        }
        return "Unknown";
    }


private Post mapResultSetToPost(ResultSet rs) throws SQLException {
    Post post = new Post();
    post.setId(rs.getInt("id"));
    post.setUserId(rs.getInt("user_id"));
    post.setContent(rs.getString("content"));
    post.setHashtags(rs.getString("hashtags"));
    post.setPromotional(rs.getInt("is_promotional") == 1);
    post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

    // Create a basic user object
    User user = new User();
    user.setUsername(getUsernameFromId(rs.getInt("user_id"))); // Get username from helper method
    post.setUser(user);

    post.setLikeCount(rs.getInt("like_count"));
    post.setCommentCount(rs.getInt("comment_count"));
    post.setShareCount(rs.getInt("share_count"));
    return post;
}

}