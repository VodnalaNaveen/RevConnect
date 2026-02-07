package com.revconnect.dao;

import com.revconnect.utils.DatabaseUtil;
import com.revconnect.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.*;

public class ConnectionDAO {

    private UserDAO userDAO = new UserDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    public List<Integer> getPendingRequests(int userId) {
        List<Integer> requests = new ArrayList<>();
        String sql = "SELECT requester_id FROM connections WHERE receiver_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(rs.getInt("requester_id"));
            }
        } catch (SQLException e) {
            System.err.println("Pending requests fetch failed: " + e.getMessage());
        }
        return requests;
    }

    public boolean sendConnectionRequest(int requesterId, int receiverId) {
        if (requesterId == receiverId) {
            System.out.println("❌ You cannot send connection request to yourself!");
            return false;
        }

        // Check if receiver is a personal user (only personal users can connect)
        User receiver = userDAO.findById(receiverId);
        if (receiver != null && receiver.getUserType() != User.UserType.PERSONAL) {
            System.out.println("❌ You can only send connection requests to personal users!");
            return false;
        }

        if (hasPendingRequest(requesterId, receiverId) || hasPendingRequest(receiverId, requesterId)) {
            System.out.println("⚠️ Connection request already exists!");
            return false;
        }

        if (isConnected(requesterId, receiverId)) {
            System.out.println("⚠️ You're already connected!");
            return false;
        }

        String sql = "INSERT INTO connections (requester_id, receiver_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, receiverId);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                notificationDAO.notifyConnectionRequest(receiverId, requesterId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Connection request failed: " + e.getMessage());
        }
        return false;
    }

    public boolean acceptRequest(int requesterId, int receiverId) {
        String sql = "UPDATE connections SET status = 'ACCEPTED', is_connected = 1 WHERE requester_id = ? AND receiver_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, receiverId);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                notificationDAO.notifyConnectionAccept(requesterId, receiverId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Accept request failed: " + e.getMessage());
        }
        return false;
    }

    public boolean rejectRequest(int requesterId, int receiverId) {
        String sql = "DELETE FROM connections WHERE requester_id = ? AND receiver_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, receiverId);
            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Connection request rejected!");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Reject request failed: " + e.getMessage());
        }
        return false;
    }

    public boolean removeConnection(int userId1, int userId2) {
        String sql = "DELETE FROM connections WHERE ((requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)) AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            pstmt.setInt(3, userId2);
            pstmt.setInt(4, userId1);
            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Connection removed!");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Remove connection failed: " + e.getMessage());
        }
        return false;
    }

    public List<Integer> getConnections(int userId) {
        List<Integer> connections = new ArrayList<>();
        String sql = "SELECT requester_id, receiver_id FROM connections WHERE (requester_id = ? OR receiver_id = ?) AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int requester = rs.getInt("requester_id");
                int receiver = rs.getInt("receiver_id");
                int otherUser = (requester == userId) ? receiver : requester;
                connections.add(otherUser);
            }
        } catch (SQLException e) {
            System.err.println("Connections fetch failed: " + e.getMessage());
        }
        return connections;
    }

public void showConnections(int userId) {
    List<Integer> connectionIds = getConnections(userId);
    System.out.println("\n🤝 YOUR CONNECTIONS (" + connectionIds.size() + "):");
    if (connectionIds.isEmpty()) {
        System.out.println("No connections yet.");
    } else {
        for (int i = 0; i < connectionIds.size(); i++) {
            User user = userDAO.findById(connectionIds.get(i));
            if (user != null) {
                System.out.println("[" + (i + 1) + "] @" + user.getUsername() + " (" + user.getName() + ")");
            }
        }
    }
    // REMOVE the connection removal code that was here
}
    public boolean isConnected(int userId1, int userId2) {
        String sql = "SELECT 1 FROM connections WHERE ((requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)) AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            pstmt.setInt(3, userId2);
            pstmt.setInt(4, userId1);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hasPendingRequest(int requesterId, int receiverId) {
        String sql = "SELECT 1 FROM connections WHERE requester_id = ? AND receiver_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, receiverId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}