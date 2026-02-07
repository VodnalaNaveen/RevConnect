package com.revconnect;

import com.revconnect.dao.PostDAO;
import com.revconnect.entities.Post;
import com.revconnect.entities.User;
import com.revconnect.dao.UserDAO;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostDAOTest {
    private PostDAO postDAO;
    private UserDAO userDAO;
    private User testUser;
    private Post testPost;

    @BeforeAll
    void setupAll() {
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        System.out.println("PostDAOTest: Initialized");
    }

    @BeforeEach
    void setupEach() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testUser = new User(
                "testpost" + timestamp + "@example.com",
                "password123",
                "postuser" + timestamp,
                User.UserType.PERSONAL,
                "Post Test User"
        );

        // Add security info
        testUser.setSecurityQuestion("What is your mother's maiden name?");
        testUser.setSecurityAnswer("testanswer");
        testUser.setPasswordHint("testhint");

        userDAO.register(testUser);

        testPost = new Post(testUser.getId(), "Test post content", "#test", false);
    }

    @Test
    void testCreatePost() {
        boolean created = postDAO.createPost(testPost);
        assertTrue(created, "Post creation should succeed");
        assertTrue(testPost.getId() > 0, "Post ID should be assigned");
    }

    @Test
    void testGetUserPosts() {
        postDAO.createPost(testPost);
        List<Post> posts = postDAO.getUserPosts(testUser.getId());
        assertFalse(posts.isEmpty(), "Should retrieve user posts");
        assertEquals(testPost.getContent(), posts.get(0).getContent());
    }

    @Test
    void testGetUserFeed() {
        postDAO.createPost(testPost);
        List<Post> feed = postDAO.getUserFeed(testUser.getId(), 10);
        assertFalse(feed.isEmpty(), "Feed should contain posts");
    }

    @Test
    void testDeletePost() {
        postDAO.createPost(testPost);
        boolean deleted = postDAO.deletePost(testPost.getId(), testUser.getId());
        assertTrue(deleted, "Post deletion should succeed");
    }


    @Test
    void testDeleteOtherUserPost() {
        // Create another user WITH SECURITY FIELDS
        User anotherUser = new User(
                "another" + System.currentTimeMillis() + "@example.com",
                "password123",
                "anotheruser" + System.currentTimeMillis(),
                User.UserType.PERSONAL,
                "Another User"
        );
        // ADD THESE LINES:
        anotherUser.setSecurityQuestion("What is your mother's maiden name?");
        anotherUser.setSecurityAnswer("testanswer");
        anotherUser.setPasswordHint("test hint");

        userDAO.register(anotherUser);

        postDAO.createPost(testPost);
        boolean deleted = postDAO.deletePost(testPost.getId(), anotherUser.getId());
        assertFalse(deleted, "Should not delete other user's post");
    }

    @AfterAll
    void cleanupAll() {
        System.out.println("All PostDAOTest completed");
    }
}