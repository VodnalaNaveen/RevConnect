package com.revconnect;

import com.revconnect.dao.UserDAO;
import com.revconnect.entities.User;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDAOTest {
    private UserDAO userDAO;
    private User testUser;

    @BeforeAll
    void setupAll() {
        userDAO = new UserDAO();
        System.out.println("UserDAOTest: Initialized");
    }

    @BeforeEach
    void setupEach() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testUser = new User(
                "test" + timestamp + "@example.com",
                "password123",
                "testuser" + timestamp,
                User.UserType.PERSONAL,
                "Test User " + timestamp
        );


        testUser.setSecurityQuestion("What is your mother's maiden name?");
        testUser.setSecurityAnswer("testanswer123");  // Use specific answer
        testUser.setPasswordHint("test hint");
    }

    @Test
    void testRegisterSuccess() {
        boolean registered = userDAO.register(testUser);
        assertTrue(registered, "User registration should succeed");
        assertTrue(testUser.getId() > 0, "User ID should be assigned");
    }

    @Test
    void testLoginSuccess() {

        userDAO.register(testUser);


        User loggedIn = userDAO.login(testUser.getUsername(), "password123");
        assertNotNull(loggedIn, "Login with username should succeed");
        assertEquals(testUser.getUsername(), loggedIn.getUsername());


        loggedIn = userDAO.login(testUser.getEmail(), "password123");
        assertNotNull(loggedIn, "Login with email should succeed");
        assertEquals(testUser.getEmail(), loggedIn.getEmail());
    }

    @Test
    void testLoginFailure() {
        User loggedIn = userDAO.login("nonexistent", "wrongpassword");
        assertNull(loggedIn, "Login with wrong credentials should fail");
    }

    @Test
    void testFindById() {
        userDAO.register(testUser);
        User found = userDAO.findById(testUser.getId());
        assertNotNull(found, "User should be found by ID");
        assertEquals(testUser.getId(), found.getId());
        assertEquals(testUser.getUsername(), found.getUsername());
    }

    @Test
    void testSearchUsers() {
        userDAO.register(testUser);


        List<User> results = userDAO.searchUsers(testUser.getUsername().substring(0, 4));
        assertFalse(results.isEmpty(), "Search should return results");


        results = userDAO.searchUsers(testUser.getName().substring(0, 4));
        assertFalse(results.isEmpty(), "Search should return results");


        results = userDAO.searchUsers("nonexistentxyz123");
        assertTrue(results.isEmpty(), "Search for non-existent should return empty");
    }

    @Test
    void testUpdateProfile() {
        userDAO.register(testUser);

        // Update user profile
        testUser.setBio("Updated bio");
        testUser.setLocation("Updated location");
        testUser.setWebsite("https://example.com");

        boolean updated = userDAO.updateProfile(testUser);
        assertTrue(updated, "Profile update should succeed");

        // Verify update
        User updatedUser = userDAO.findById(testUser.getId());
        assertEquals("Updated bio", updatedUser.getBio());
        assertEquals("Updated location", updatedUser.getLocation());
        assertEquals("https://example.com", updatedUser.getWebsite());
    }

    @Test
    void testPasswordRecovery() {
        System.out.println("DEBUG: Test user security question: " + testUser.getSecurityQuestion());
        System.out.println("DEBUG: Test user security answer: " + testUser.getSecurityAnswer());

        boolean registered = userDAO.register(testUser);
        System.out.println("DEBUG: Registration result: " + registered);
        System.out.println("DEBUG: User ID after registration: " + testUser.getId());

        // Try to find the user first
        User foundUser = userDAO.findById(testUser.getId());
        System.out.println("DEBUG: Found user security question: " +
                (foundUser != null ? foundUser.getSecurityQuestion() : "User not found"));

        // Test getSecurityQuestion
        String question = userDAO.getSecurityQuestion(testUser.getEmail());
        System.out.println("DEBUG: Retrieved security question: " + question);

        // Test recovery
        User recovered = userDAO.recoverPassword(testUser.getEmail(), "testanswer123");
        System.out.println("DEBUG: Recovered user: " + (recovered != null ? "Success" : "Failed"));

        assertNotNull(recovered, "Should recover user with correct answer");
        assertEquals(testUser.getEmail(), recovered.getEmail());
    }

    @AfterEach
    void cleanupEach() {
        System.out.println("Test completed for: " + testUser.getUsername());
    }

    @AfterAll
    void cleanupAll() {
        System.out.println("All UserDAOTest completed");
    }
}