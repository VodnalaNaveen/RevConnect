package com.revconnect.dao;

import com.revconnect.utils.DatabaseUtil;
import java.sql.*;

public class LikeDAO {
    public boolean likePost(int userId, int postId) {
        // Check if already liked
        if (hasLiked(userId, postId)) {
            return false; // Already liked
        }

        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Like failed: " + e.getMessage());
        }
        return false;
    }

    public boolean unlikePost(int userId, int postId) {
        String sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Unlike failed: " + e.getMessage());
        }
        return false;
    }

    public boolean hasLiked(int userId, int postId) {
        String sql = "SELECT 1 FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Like check failed: " + e.getMessage());
        }
        return false;
    }
}