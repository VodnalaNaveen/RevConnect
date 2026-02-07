package com.revconnect.dao;

import com.revconnect.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean createNotification(int userId, String type, String message) {
        String sql = "INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setString(3, message);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Notification creation failed: " + e.getMessage());
        }
        return false;
    }

    public void notifyLike(int postOwnerId, int likerId, int postId) {
        String likerName = getUsername(likerId);
        String message = likerName + " liked your post";
        createNotification(postOwnerId, "LIKE", message);
    }

    public void notifyComment(int postOwnerId, int commenterId, int postId) {
        String commenterName = getUsername(commenterId);
        String message = commenterName + " commented on your post";
        createNotification(postOwnerId, "COMMENT", message);
    }

    public void notifyShare(int postOwnerId, int sharerId, int postId) {
        String sharerName = getUsername(sharerId);
        String message = sharerName + " shared your post";
        createNotification(postOwnerId, "SHARE", message);
    }

    public void notifyFollow(int followedId, int followerId) {
        String followerName = getUsername(followerId);
        String message = followerName + " started following you";
        createNotification(followedId, "FOLLOW", message);
    }

    public void notifyConnectionRequest(int receiverId, int requesterId) {
        String requesterName = getUsername(requesterId);
        String message = requesterName + " sent you a connection request";
        createNotification(receiverId, "CONNECTION_REQUEST", message);
    }

    public void notifyConnectionAccept(int requesterId, int acceptorId) {
        String acceptorName = getUsername(acceptorId);
        String message = acceptorName + " accepted your connection request";
        createNotification(requesterId, "CONNECTION_ACCEPT", message);
    }

    public void notifyProfileView(int viewedUserId, int viewerId) {
        String viewerName = getUsername(viewerId);
        String message = viewerName + " viewed your profile";
        createNotification(viewedUserId, "PROFILE_VIEW", message);
    }

    // Helper method to get username
    private String getUsername(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "@" + rs.getString("username");
            }
        } catch (SQLException e) {
            // Silent fail - return "Someone" if username not found
        }
        return "Someone";
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Unread count failed: " + e.getMessage());
        }
        return 0;
    }

    public List<String> getNotifications(int userId, int limit) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT type, message, created_at, is_read FROM notifications WHERE user_id = ? ORDER BY created_at DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                String message = rs.getString("message");
                Timestamp timestamp = rs.getTimestamp("created_at");
                boolean isRead = rs.getInt("is_read") == 1;

                String timeStr = timestamp.toString().substring(11, 16); // HH:MM format
                String typeIcon = getTypeIcon(type);
                String readIndicator = isRead ? "" : "🔴 "; // Red dot for unread

                String display = String.format("%s%s %s %s",
                        readIndicator, typeIcon, timeStr, message);
                list.add(display);
            }
        } catch (SQLException e) {
            System.err.println("Notifications fetch failed: " + e.getMessage());
        }
        return list;
    }

    private String getTypeIcon(String type) {
        if (type == null) return "🔔";

        switch(type.toUpperCase()) {
            case "LIKE": return "❤️";
            case "COMMENT": return "💬";
            case "SHARE": return "🔄";
            case "FOLLOW": return "👤";
            case "CONNECTION_REQUEST": return "🤝";
            case "CONNECTION_ACCEPT": return "✅";
            case "PROFILE_VIEW": return "👀";
            default: return "🔔";
        }
    }

    public void markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Mark all read failed: " + e.getMessage());
        }
    }
    // Add these methods to your NotificationDAO.java
    public void notifyFollowRequest(int followedId, int followerId) {
        String followerName = getUsername(followerId);
        String message = followerName + " sent you a follow request";
        createNotification(followedId, "FOLLOW_REQUEST", message);
    }

    public void notifyFollowAccept(int followerId, int followedId) {
        String followedName = getUsername(followedId);
        String message = followedName + " accepted your follow request";
        createNotification(followerId, "FOLLOW_ACCEPT", message);
    }
}


