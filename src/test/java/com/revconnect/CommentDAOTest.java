package com.revconnect;

import com.revconnect.dao.CommentDAO;
import com.revconnect.dao.PostDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.entities.Post;
import com.revconnect.entities.User;
import com.revconnect.models.Comment;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentDAOTest {
    private CommentDAO commentDAO;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private User testUser;
    private Post testPost;

    @BeforeAll
    void setupAll() {
        commentDAO = new CommentDAO();
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        System.out.println("CommentDAOTest: Initialized");
    }

    @BeforeEach
    void setupEach() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testUser = new User(
                "testcomment" + timestamp + "@example.com",
                "password123",
                "commentuser" + timestamp,
                User.UserType.PERSONAL,
                "Comment Test User"
        );

        // Add security info
        testUser.setSecurityQuestion("What is your mother's maiden name?");
        testUser.setSecurityAnswer("testanswer");
        testUser.setPasswordHint("testhint");

        userDAO.register(testUser);

        testPost = new Post(testUser.getId(), "Test post for comments", "#test", false);
        postDAO.createPost(testPost);
    }

    @Test
    void testAddComment() {
        boolean added = commentDAO.addComment(testUser.getId(), testPost.getId(), "Test comment");
        assertTrue(added, "Comment should be added successfully");
    }

    @Test
    void testDeleteComment() {

        commentDAO.addComment(testUser.getId(), testPost.getId(), "Test comment");

        boolean deleted = commentDAO.deleteComment(1, testUser.getId());

    }

    @Test
    void testGetComments() {
        commentDAO.addComment(testUser.getId(), testPost.getId(), "Comment 1");
        commentDAO.addComment(testUser.getId(), testPost.getId(), "Comment 2");

        List<Comment> comments = commentDAO.getComments(testPost.getId());
        assertNotNull(comments, "Comments list should not be null");
        assertTrue(comments.size() >= 2, "Should retrieve at least 2 comments");
    }

    @Test
    void testGetCommentsEmptyPost() {
        List<Comment> comments = commentDAO.getComments(999999);
        assertNotNull(comments, "Should return empty list, not null");
        assertTrue(comments.isEmpty(), "Should return empty list for non-existent post");
    }


    @Test
    void testDeleteOtherUserComment() {
        User anotherUser = new User(
                "anothercomment" + System.currentTimeMillis() + "@example.com",
                "password123",
                "anothercommentuser" + System.currentTimeMillis(),
                User.UserType.PERSONAL,
                "Another Comment User"
        );

        anotherUser.setSecurityQuestion("What is your mother's maiden name?");
        anotherUser.setSecurityAnswer("testanswer");
        anotherUser.setPasswordHint("test hint");

        userDAO.register(anotherUser);


        commentDAO.addComment(testUser.getId(), testPost.getId(), "Test comment");


        boolean deleted = commentDAO.deleteComment(1, anotherUser.getId());
        assertFalse(deleted, "Should not delete other user's comment");
    }

    @AfterAll
    void cleanupAll() {
        System.out.println("All CommentDAOTest completed");
    }
}