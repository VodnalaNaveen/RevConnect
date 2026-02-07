package com.revconnect;

import com.revconnect.dao.FollowDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.entities.User;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FollowDAOTest {
    private FollowDAO followDAO;
    private UserDAO userDAO;
    private User user1;
    private User user2;

    @BeforeAll
    void setupAll() {
        followDAO = new FollowDAO();
        userDAO = new UserDAO();
        System.out.println("FollowDAOTest: Initialized");
    }


    @BeforeEach
    void setupEach() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        user1 = new User(
                "follow1" + timestamp + "@example.com",
                "password123",
                "follower" + timestamp,
                User.UserType.PERSONAL,
                "Follower User"
        );
        user2 = new User(
                "follow2" + timestamp + "@example.com",
                "password123",
                "followed" + timestamp,
                User.UserType.PERSONAL,
                "Followed User"
        );


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
    void testSendFollowRequest() {
        boolean requestSent = followDAO.sendFollowRequest(user1.getId(), user2.getId());
        assertTrue(requestSent, "Follow request should be sent successfully");
    }

    @Test
    void testSendFollowRequestToSelf() {
        boolean requestSent = followDAO.sendFollowRequest(user1.getId(), user1.getId());
        assertFalse(requestSent, "Should not send follow request to self");
    }

    @Test
    void testSendFollowRequestTwice() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        boolean requestSentAgain = followDAO.sendFollowRequest(user1.getId(), user2.getId());
        assertFalse(requestSentAgain, "Should not send follow request twice");
    }

    @Test
    void testAcceptFollowRequest() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        boolean accepted = followDAO.acceptFollowRequest(user1.getId(), user2.getId());
        assertTrue(accepted, "Follow request should be accepted");
    }

    @Test
    void testRejectFollowRequest() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        boolean rejected = followDAO.rejectFollowRequest(user1.getId(), user2.getId());
        assertTrue(rejected, "Follow request should be rejected");
    }

    @Test
    void testGetPendingRequests() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        List<Integer> pending = followDAO.getPendingRequests(user2.getId());
        assertFalse(pending.isEmpty(), "Should have pending requests");
        assertTrue(pending.contains(user1.getId()), "Should contain follower ID in pending requests");
    }

    @Test
    void testGetFollowers() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        followDAO.acceptFollowRequest(user1.getId(), user2.getId());

        List<Integer> followers = followDAO.getFollowers(user2.getId());
        assertFalse(followers.isEmpty(), "Should have followers after acceptance");
        assertTrue(followers.contains(user1.getId()), "Should contain follower ID");
    }

    @Test
    void testGetFollowing() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        followDAO.acceptFollowRequest(user1.getId(), user2.getId());

        List<Integer> following = followDAO.getFollowing(user1.getId());
        assertFalse(following.isEmpty(), "Should be following someone after acceptance");
        assertTrue(following.contains(user2.getId()), "Should contain followed user ID");
    }

    @Test
    void testIsFollowing() {
        assertFalse(followDAO.isFollowing(user1.getId(), user2.getId()),
                "Initially not following");

        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        assertFalse(followDAO.isFollowing(user1.getId(), user2.getId()),
                "Should not be following while request is pending");

        followDAO.acceptFollowRequest(user1.getId(), user2.getId());
        assertTrue(followDAO.isFollowing(user1.getId(), user2.getId()),
                "Should be following after request accepted");
    }

    @Test
    void testHasPendingRequest() {
        assertFalse(followDAO.hasPendingRequest(user1.getId(), user2.getId()),
                "Initially no pending request");

        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        assertTrue(followDAO.hasPendingRequest(user1.getId(), user2.getId()),
                "Should have pending request after sending");

        followDAO.acceptFollowRequest(user1.getId(), user2.getId());
        assertFalse(followDAO.hasPendingRequest(user1.getId(), user2.getId()),
                "Should not have pending request after acceptance");
    }

    @Test
    void testUnfollowUser() {
        followDAO.sendFollowRequest(user1.getId(), user2.getId());
        followDAO.acceptFollowRequest(user1.getId(), user2.getId());

        boolean unfollowed = followDAO.unfollowUser(user1.getId(), user2.getId());
        assertTrue(unfollowed, "User should be unfollowed successfully");
    }

    @Test
    void testUnfollowNotFollowing() {
        boolean unfollowed = followDAO.unfollowUser(user1.getId(), user2.getId());
        assertFalse(unfollowed, "Should not unfollow user not being followed");
    }

    @AfterAll
    void cleanupAll() {
        System.out.println("All FollowDAOTest completed");
    }
}