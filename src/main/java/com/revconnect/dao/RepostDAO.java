package com.revconnect.dao;

import com.revconnect.utils.DatabaseUtil;
import java.sql.*;

public class RepostDAO {
    public boolean repost(int userId, int postId) {
        // Check if already reposted
        if (hasReposted(userId, postId)) {
//            System.out.println("⚠️ You've already shared this post!");
            return false;
        }

        String sql = "INSERT INTO reposts (original_post_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Repost failed: " + e.getMessage());
        }
        return false;
    }

    private boolean hasReposted(int userId, int postId) {
        String sql = "SELECT 1 FROM reposts WHERE original_post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}