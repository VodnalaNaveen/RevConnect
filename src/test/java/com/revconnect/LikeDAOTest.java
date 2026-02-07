package com.revconnect;

import com.revconnect.dao.LikeDAO;
import com.revconnect.dao.PostDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.entities.Post;
import com.revconnect.entities.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LikeDAOTest {
    private LikeDAO likeDAO;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private User testUser;
    private Post testPost;

    @BeforeAll
    void setupAll() {
        likeDAO = new LikeDAO();
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        System.out.println("LikeDAOTest: Initialized");
    }


    @BeforeEach
    void setupEach() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testUser = new User(
                "testlike" + timestamp + "@example.com",
                "password123",
                "likeuser" + timestamp,
                User.UserType.PERSONAL,
                "Like Test User"
        );


        testUser.setSecurityQuestion("What is your mother's maiden name?");
        testUser.setSecurityAnswer("testanswer");
        testUser.setPasswordHint("testhint");

        userDAO.register(testUser);

        testPost = new Post(testUser.getId(), "Test post for likes", "#test", false);
        postDAO.createPost(testPost);
    }

    @Test
    void testLikePost() {
        boolean liked = likeDAO.likePost(testUser.getId(), testPost.getId());
        assertTrue(liked, "Post should be liked successfully");
    }

    @Test
    void testLikePostTwice() {
        likeDAO.likePost(testUser.getId(), testPost.getId());
        boolean likedAgain = likeDAO.likePost(testUser.getId(), testPost.getId());
        assertFalse(likedAgain, "Should not like the same post twice");
    }

    @Test
    void testUnlikePost() {
        likeDAO.likePost(testUser.getId(), testPost.getId());
        boolean unliked = likeDAO.unlikePost(testUser.getId(), testPost.getId());
        assertTrue(unliked, "Post should be unliked successfully");
    }

    @Test
    void testUnlikeNotLikedPost() {
        boolean unliked = likeDAO.unlikePost(testUser.getId(), testPost.getId());
        assertFalse(unliked, "Should not unlike a post that wasn't liked");
    }

    @Test
    void testHasLiked() {
        assertFalse(likeDAO.hasLiked(testUser.getId(), testPost.getId()),
                "Initially should not have liked");

        likeDAO.likePost(testUser.getId(), testPost.getId());
        assertTrue(likeDAO.hasLiked(testUser.getId(), testPost.getId()),
                "Should return true after liking");
    }

    @Test
    void testHasLikedNonExistent() {
        assertFalse(likeDAO.hasLiked(testUser.getId(), 999999),
                "Should return false for non-existent post");
    }

    @AfterAll
    void cleanupAll() {
        System.out.println("All LikeDAOTest completed");
    }
}