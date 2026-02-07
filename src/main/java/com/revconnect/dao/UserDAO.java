package com.revconnect.dao;

import com.revconnect.entities.User;
import com.revconnect.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String HASH_PREFIX = "revconnect_";

public boolean register(User user) {
    String sql = "INSERT INTO users (email, password, username, user_type, name, security_question, security_answer, password_hint) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"ID"})) {

        pstmt.setString(1, user.getEmail());
        pstmt.setString(2, hashPassword(user.getPassword()));
        pstmt.setString(3, user.getUsername());
        pstmt.setString(4, user.getUserType().name());
        pstmt.setString(5, user.getName());

        // THESE 3 LINES ARE CRITICAL:
        pstmt.setString(6, user.getSecurityQuestion());
        pstmt.setString(7, hashPassword(user.getSecurityAnswer()));  // Hash the security answer too!
        pstmt.setString(8, user.getPasswordHint());

        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
                return true;
            }
        }
    } catch (SQLException e) {
        if (e.getErrorCode() == 1) {
            System.err.println("Registration failed: Email or username already exists");
        } else {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();  // Add this for debugging
        }
    }
    return false;
}


    public User login(String emailOrUsername, String password) {
        String sql = "SELECT * FROM users WHERE (email = ? OR username = ?) AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emailOrUsername);
            pstmt.setString(2, emailOrUsername);
            pstmt.setString(3, hashPassword(password));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public List<User> searchUsers(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE name LIKE ? OR username LIKE ? FETCH FIRST 10 ROWS ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
        return users;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET name=?, bio=?, location=?, website=?, is_private=?, is_public=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getLocation());
            pstmt.setString(4, user.getWebsite());
            pstmt.setInt(5, user.isPrivate() ? 1 : 0);
            pstmt.setInt(6, user.isPublic() ? 1 : 0);
            pstmt.setInt(7, user.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Profile update failed: " + e.getMessage());
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("username"));
        user.setUserType(User.UserType.valueOf(rs.getString("user_type")));
        user.setName(rs.getString("name"));
        user.setBio(rs.getString("bio"));
        user.setLocation(rs.getString("location"));
        user.setWebsite(rs.getString("website"));
        user.setPrivate(rs.getInt("is_private") == 1);
        user.setPublic(rs.getInt("is_public") == 1);
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setPasswordHint(rs.getString("password_hint"));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setPasswordHint(rs.getString("password_hint"));
        user.setPublic(rs.getInt("is_public") == 1);
        return user;
    }

    public boolean deleteAccount(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Account deletion failed: " + e.getMessage());
            return false;
        }
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("User fetch failed: " + e.getMessage());
        }
        return null;
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        String sql = "SELECT id FROM users WHERE id = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, hashPassword(currentPassword));

            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                return false;
            }

            String updateSql = "UPDATE users SET password = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, hashPassword(newPassword));
                updateStmt.setInt(2, userId);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Password change failed: " + e.getMessage());
            return false;
        }
    }


    public boolean updateSecurityInfo(int userId, String securityQuestion, String securityAnswer, String passwordHint) {
        String sql = "UPDATE users SET security_question = ?, security_answer = ?, password_hint = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, securityQuestion);
            pstmt.setString(2, hashPassword(securityAnswer)); // IMPORTANT: Hash the answer
            pstmt.setString(3, passwordHint);
            pstmt.setInt(4, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Security info update failed: " + e.getMessage());
            return false;
        }
    }


    public User recoverPassword(String email, String securityAnswer) {
        String sql = "SELECT * FROM users WHERE email = ? AND security_answer = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, hashPassword(securityAnswer)); // Note: hash the answer for comparison

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Password recovery failed: " + e.getMessage());
        }
        return null;
    }
    public boolean resetPassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Password reset failed: " + e.getMessage());
            return false;
        }
    }

//    public String getPasswordHint(String email) {
//        String sql = "SELECT password_hint FROM users WHERE email = ?";
//        try (Connection conn = DatabaseUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, email);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getString("password_hint");
//            }
//        } catch (SQLException e) {
//            // Silent fail
//        }
//        return null;
//    }

    public String getSecurityQuestion(String email) {
        String sql = "SELECT security_question FROM users WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("security_question");
            }
        } catch (SQLException e) {
            System.err.println("Error getting security question: " + e.getMessage());
            e.printStackTrace();  // Add this
        }
        return null;
    }
    private String hashPassword(String password) {
        return HASH_PREFIX + password.hashCode();
    }
}