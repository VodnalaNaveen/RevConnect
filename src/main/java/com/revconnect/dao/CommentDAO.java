package com.revconnect.dao;

import com.revconnect.utils.DatabaseUtil;
import java.sql.*;
import com.revconnect.models.Comment;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    public boolean addComment(int userId, int postId, String content) {
        String sql = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, content);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add comment failed: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM comments WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete comment failed: " + e.getMessage());
        }
        return false;
    }

    // Also add a method to get comments for a post (for displaying)
    public List<Comment> getComments(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.id, c.post_id, c.user_id, c.content, c.created_at, u.username " +
                "FROM comments c JOIN users u ON c.user_id = u.id " +
                "WHERE c.post_id = ? ORDER BY c.created_at ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setPostId(rs.getInt("post_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));
                comment.setUsername(rs.getString("username"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Comments fetch failed: " + e.getMessage());
        }
        return comments;
    }
}
