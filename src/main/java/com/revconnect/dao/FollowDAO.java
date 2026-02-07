package com.revconnect.dao;

import com.revconnect.entities.User;
import com.revconnect.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    private UserDAO userDAO = new UserDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    public boolean sendFollowRequest(int followerId, int followedId) {
        if (followerId == followedId) {
            System.out.println("❌ You cannot follow yourself!");
            return false;
        }

        if (hasPendingRequest(followerId, followedId)) {
            System.out.println("⚠️ Follow request already sent!");
            return false;
        }

        if (isFollowing(followerId, followedId)) {
            System.out.println("⚠️ You're already following this user!");
            return false;
        }

        String sql = "INSERT INTO follows (follower_id, followed_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                notificationDAO.notifyFollowRequest(followedId, followerId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Follow request failed: " + e.getMessage());
            return false;
        }
    }

    public boolean acceptFollowRequest(int followerId, int followedId) {
        String sql = "UPDATE follows SET status = 'ACCEPTED' WHERE follower_id = ? AND followed_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                notificationDAO.notifyFollowAccept(followerId, followedId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Accept follow request failed: " + e.getMessage());
        }
        return false;
    }

    public boolean rejectFollowRequest(int followerId, int followedId) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND followed_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Reject follow request failed: " + e.getMessage());
            return false;
        }
    }

    public List<Integer> getPendingRequests(int userId) {
        List<Integer> requests = new ArrayList<>();
        String sql = "SELECT follower_id FROM follows WHERE followed_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(rs.getInt("follower_id"));
            }
        } catch (SQLException e) {
            System.err.println("Pending requests fetch failed: " + e.getMessage());
        }
        return requests;
    }

    public List<Integer> getFollowers(int userId) {
        List<Integer> followers = new ArrayList<>();
        String sql = "SELECT follower_id FROM follows WHERE followed_id = ? AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                followers.add(rs.getInt("follower_id"));
            }
        } catch (SQLException e) {
            System.err.println("Followers fetch failed: " + e.getMessage());
        }
        return followers;
    }

    public List<Integer> getFollowing(int userId) {
        List<Integer> following = new ArrayList<>();
        String sql = "SELECT followed_id FROM follows WHERE follower_id = ? AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                following.add(rs.getInt("followed_id"));
            }
        } catch (SQLException e) {
            System.err.println("Following fetch failed: " + e.getMessage());
        }
        return following;
    }

    public boolean isFollowing(int followerId, int followedId) {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND followed_id = ? AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hasPendingRequest(int followerId, int followedId) {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND followed_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean unfollowUser(int followerId, int followedId) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND followed_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Unfollow failed: " + e.getMessage());
            return false;
        }
    }

    // Keep existing showFollowers and showFollowing methods
    public void showFollowers(int userId) {
        List<Integer> followerIds = getFollowers(userId);
        System.out.println("\n👥 YOUR FOLLOWERS (" + followerIds.size() + "):");
        if (followerIds.isEmpty()) {
            System.out.println("No followers yet.");
        } else {
            for (int id : followerIds) {
                User user = userDAO.findById(id);
                if (user != null) {
                    System.out.println("- @" + user.getUsername() + " (" + user.getName() + ")");
                }
            }
        }
    }

    public void showFollowing(int userId) {
        List<Integer> followingIds = getFollowing(userId);
        System.out.println("\n👣 YOU'RE FOLLOWING (" + followingIds.size() + "):");
        if (followingIds.isEmpty()) {
            System.out.println("Not following anyone yet.");
        } else {
            for (int id : followingIds) {
                User user = userDAO.findById(id);
                if (user != null) {
                    System.out.println("- @" + user.getUsername() + " (" + user.getName() + ")");
                }
            }
        }
    }
}