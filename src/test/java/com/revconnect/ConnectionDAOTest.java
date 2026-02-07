package com.revconnect;

import com.revconnect.dao.ConnectionDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.entities.User;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConnectionDAOTest {
    private ConnectionDAO connectionDAO;
    private UserDAO userDAO;
    private User user1;
    private User user2;

    @BeforeAll
    void setupAll() {
        connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO();
        System.out.println("ConnectionDAOTest: Initialized");
    }


    @BeforeEach
    void setupEach() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        user1 = new User(
                "user1" + timestamp + "@example.com",
                "password123",
                "user1_" + timestamp,
                User.UserType.PERSONAL,
                "User One"
        );
        user2 = new User(
                "user2" + timestamp + "@example.com",
                "password123",
                "user2_" + timestamp,
                User.UserType.PERSONAL,
                "User Two"
        );

        // Add security info for both users
        user1.setSecurityQuestion("What is your mother's maiden name?");
        user1.setSecurityAnswer("testanswer1");
        user1.setPasswordHint("testhint1");

        user2.setSecurityQuestion("What is your mother's maiden name?");
        user2.setSecurityAnswer("testanswer2");
        user2.setPasswordHint("testhint2");

        userDAO.register(user1);
        userDAO.register(user2);
    }

    @Test
    void testSendConnectionRequest() {
        boolean sent = connectionDAO.sendConnectionRequest(user1.getId(), user2.getId());
        assertTrue(sent, "Connection request should be sent");
    }

    @Test
    void testSendConnectionToSelf() {
        boolean sent = connectionDAO.sendConnectionRequest(user1.getId(), user1.getId());
        assertFalse(sent, "Should not send connection request to self");
    }

    @Test
    void testAcceptConnectionRequest() {
        connectionDAO.sendConnectionRequest(user1.getId(), user2.getId());
        boolean accepted = connectionDAO.acceptRequest(user1.getId(), user2.getId());
        assertTrue(accepted, "Connection request should be accepted");
    }

    @Test
    void testGetPendingRequests() {
        connectionDAO.sendConnectionRequest(user1.getId(), user2.getId());
        List<Integer> pending = connectionDAO.getPendingRequests(user2.getId());
        assertFalse(pending.isEmpty(), "Should have pending requests");
        assertEquals(1, pending.size());
    }

    @AfterAll
    void cleanupAll() {
        System.out.println("All ConnectionDAOTest completed");
    }
}