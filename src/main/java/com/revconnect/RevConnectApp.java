package com.revconnect;

import com.revconnect.dao.*;
import com.revconnect.entities.*;
import com.revconnect.models.Comment;

import java.util.List;
import java.util.Scanner;

public class RevConnectApp {
    private static final Scanner sc = new Scanner(System.in);

    private static final UserDAO userDAO = new UserDAO();
    private static final PostDAO postDAO = new PostDAO();
    private static final LikeDAO likeDAO = new LikeDAO();
    private static final CommentDAO commentDAO = new CommentDAO();
    private static final RepostDAO repostDAO = new RepostDAO();
    private static final NotificationDAO notificationDAO = new NotificationDAO();
    private static final FollowDAO followDAO = new FollowDAO();
    private static final ConnectionDAO connectionDAO = new ConnectionDAO();

    private static User currentUser;

    public static void main(String[] args) {
        System.out.println("🚀 Welcome to RevConnect - Social Media Console App!");
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("🔥 REVCONNECT MAIN MENU");
            System.out.println("==================================================");
            System.out.println("1. 📧 Register");
            System.out.println("2. 🔑 Login");
            System.out.println("3. 🔓 Forgot Password");
            System.out.println("4. ❌ Exit");
            System.out.print("\nChoose option (1-4): ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                register();
            } else if (choice == 2) {
                login();
            } else if (choice == 3) {
                forgotPassword();
            } else if (choice == 4) {
                System.out.println("👋 Goodbye!");
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }

    private static void register() {
        System.out.print("📧 Email: ");
        String email = sc.nextLine();
        System.out.print("🔑 Password: ");
        String password = sc.nextLine();
        System.out.print("👤 Username: ");
        String username = sc.nextLine();
        System.out.print("👤 Name: ");
        String name = sc.nextLine();
        System.out.print("User Type (1=Personal, 2=Creator, 3=Business): ");
        int typeChoice = sc.nextInt();
        sc.nextLine();

        User.UserType type;
        if (typeChoice == 1) {
            type = User.UserType.PERSONAL;
        } else if (typeChoice == 2) {
            type = User.UserType.CREATOR;
        } else if (typeChoice == 3) {
            type = User.UserType.BUSINESS;
        } else {
            type = User.UserType.PERSONAL;
        }

        User user = new User(email, password, username, type, name);
        user.setSecurityQuestion("What is your mother's maiden name?");
        user.setSecurityAnswer("default");
        user.setPasswordHint("You set this during registration");
        if (userDAO.register(user)) {
            System.out.println("✅ Registration successful!");
            System.out.println("💡 Please update your security questions in the Security & Privacy menu.");
        } else {
            System.out.println("❌ Registration failed. Email/username may already exist.");
        }
    }

    private static void login() {
        System.out.print("📧 Email/Username: ");
        String emailOrUsername = sc.nextLine();
        System.out.print("🔑 Password: ");
        String password = sc.nextLine();

        currentUser = userDAO.login(emailOrUsername, password);
        if (currentUser != null) {
            System.out.println("✅ Welcome back, @" + currentUser.getUsername() + "!");
            showUserMenu();
        } else {
            System.out.println("❌ Invalid credentials!");
        }
    }

    private static void showUserMenu() {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("👤 Logged in as: @" + currentUser.getUsername());
            System.out.println("1. 📝 Create Post");
            System.out.println("2. 👀 View Feed");
            System.out.println("3. 📱 My Profile");
            System.out.println("4. 🔍 Search Users");
            System.out.println("5. 👥 Connections");
            System.out.println("6. 🔔 Notifications");
            System.out.println("7. 🔐 Security & Privacy");
            System.out.println("8. 🗑️ Delete Account");
            System.out.println("9. 🚪 Logout");
            System.out.print("\nChoose option (1-9): ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                createPost();
            } else if (choice == 2) {
                viewFeed();
            } else if (choice == 3) {
                viewProfile();
            } else if (choice == 4) {
                searchUsers();
            } else if (choice == 5) {
                handleConnections();
            } else if (choice == 6) {
                viewNotifications();
            } else if (choice == 7) {
                securityAndPrivacyMenu();
            } else if (choice == 8) {
                deleteAccount();
            } else if (choice == 9) {
                currentUser = null;
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }

    private static void deleteAccount() {
        System.out.print("⚠️ Are you sure you want to delete your account? This cannot be undone! (yes/no): ");
        String confirmation = sc.nextLine().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            System.out.print("Enter your password to confirm: ");
            String password = sc.nextLine();

            User verifiedUser = userDAO.login(currentUser.getUsername(), password);
            if (verifiedUser != null) {
                if (userDAO.deleteAccount(currentUser.getId())) {
                    System.out.println("✅ Account deleted successfully!");
                    currentUser = null;
                    return;
                } else {
                    System.out.println("❌ Failed to delete account!");
                }
            } else {
                System.out.println("❌ Incorrect password!");
            }
        } else {
            System.out.println("Account deletion cancelled.");
        }
    }

    private static void securityAndPrivacyMenu() {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("🔐 SECURITY & PRIVACY SETTINGS");
            System.out.println("1. 🔑 Change Password");
            System.out.println("2. ❓ Set Security Questions & Hint");
            System.out.println("3. 👁️ Profile Privacy Settings");
            System.out.println("4. ⬅ Back to Main Menu");
            System.out.print("\nChoose option (1-4): ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                changePassword();
            } else if (choice == 2) {
                setSecurityInfo();
            } else if (choice == 3) {
                privacySettings();
            } else if (choice == 4) {
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }

    private static void changePassword() {
        System.out.println("\n🔑 CHANGE PASSWORD");
        System.out.print("Enter current password: ");
        String currentPass = sc.nextLine();

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        System.out.print("Confirm new password: ");
        String confirmPass = sc.nextLine();

        if (!newPass.equals(confirmPass)) {
            System.out.println("❌ New passwords don't match!");
            return;
        }

        if (newPass.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters!");
            return;
        }

        if (userDAO.changePassword(currentUser.getId(), currentPass, newPass)) {
            System.out.println("✅ Password changed successfully!");
        } else {
            System.out.println("❌ Failed to change password. Check your current password.");
        }
    }

    private static final String[] SECURITY_QUESTIONS = {
            "What is your mother's maiden name?",
            "What was the name of your first pet?",
            "What city were you born in?",
            "What is your favorite book?",
            "What was your childhood nickname?",
            "What is the name of your elementary school?",
            "What is your favorite movie?",
            "What street did you grow up on?"
    };

    private static void showSecurityQuestions() {
        System.out.println("\n🔐 Choose a security question:");
        for (int i = 0; i < SECURITY_QUESTIONS.length; i++) {
            System.out.println("[" + (i + 1) + "] " + SECURITY_QUESTIONS[i]);
        }
    }


    private static void setSecurityInfo() {
        System.out.println("\n❓ SET SECURITY INFORMATION");
        System.out.println("(For password recovery if you forget your password)");


        if (currentUser.getSecurityQuestion() != null && !currentUser.getSecurityQuestion().isEmpty()) {
            System.out.println("Current Security Question: " + currentUser.getSecurityQuestion());
        }


        System.out.println("\n🔐 Choose a security question:");
        for (int i = 0; i < SECURITY_QUESTIONS.length; i++) {
            System.out.println("[" + (i + 1) + "] " + SECURITY_QUESTIONS[i]);
        }

        System.out.print("\nSelect question number (1-" + SECURITY_QUESTIONS.length + "): ");
        int questionChoice = sc.nextInt();
        sc.nextLine();

        if (questionChoice < 1 || questionChoice > SECURITY_QUESTIONS.length) {
            System.out.println("❌ Invalid selection!");
            return;
        }

        String question = SECURITY_QUESTIONS[questionChoice - 1];

        System.out.print("Answer: ");
        String answer = sc.nextLine();

        System.out.print("Password Hint (optional): ");
        String hint = sc.nextLine();

        if (userDAO.updateSecurityInfo(currentUser.getId(), question, answer, hint)) {
            // Update current user object
            currentUser.setSecurityQuestion(question);
            currentUser.setPasswordHint(hint);
            System.out.println("✅ Security information saved!");
        } else {
            System.out.println("❌ Failed to save security information.");
        }
    }

    private static void privacySettings() {
        System.out.println("\n👁️ PROFILE PRIVACY SETTINGS");
        System.out.println("Current setting: " + (currentUser.isPublic() ? "🌐 PUBLIC" : "🔒 PRIVATE"));
        System.out.println("\nPublic Profile: Anyone can see your posts and follow you");
        System.out.println("Private Profile: Only approved followers can see your posts");

        System.out.print("\nChange to (1=Public, 2=Private, 3=Cancel): ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            currentUser.setPublic(true);
            if (userDAO.updateProfile(currentUser)) {
                System.out.println("✅ Profile set to PUBLIC");
            }
        } else if (choice == 2) {
            currentUser.setPublic(false);
            if (userDAO.updateProfile(currentUser)) {
                System.out.println("✅ Profile set to PRIVATE");
            }
        } else if (choice == 3) {
            System.out.println("No changes made.");
        } else {
            System.out.println("❌ Invalid option!");
        }
    }

    private static void forgotPassword() {
        System.out.println("\n🔓 PASSWORD RECOVERY");
        System.out.print("Enter your email: ");
        String email = sc.nextLine();

        // Get the user's stored security question first
        String storedQuestion = userDAO.getSecurityQuestion(email);
        if (storedQuestion == null || storedQuestion.isEmpty()) {
            System.out.println("⚠️ No security question set for this account.");
            return;
        }

        // Show all security questions
        System.out.println("\n🔐 Select your security question:");
        for (int i = 0; i < SECURITY_QUESTIONS.length; i++) {
            System.out.println("[" + (i + 1) + "] " + SECURITY_QUESTIONS[i]);
        }

        System.out.print("\nSelect your security question (1-" + SECURITY_QUESTIONS.length + "): ");
        int questionChoice = sc.nextInt();
        sc.nextLine();

        if (questionChoice < 1 || questionChoice > SECURITY_QUESTIONS.length) {
            System.out.println("❌ Invalid selection!");
            return;
        }

        String selectedQuestion = SECURITY_QUESTIONS[questionChoice - 1];


        if (!selectedQuestion.equals(storedQuestion)) {
            System.out.println("❌ The selected question does not match your security question.");
            return;
        }

        System.out.print("Enter your answer: ");
        String answer = sc.nextLine();

        User user = userDAO.recoverPassword(email, answer);
        if (user != null) {
            System.out.println("\n✅ Identity verified! You can now reset your password.");

            System.out.print("Enter new password: ");
            String newPass = sc.nextLine();

            System.out.print("Confirm new password: ");
            String confirmPass = sc.nextLine();

            if (newPass.equals(confirmPass)) {
                if (newPass.length() < 6) {
                    System.out.println("❌ Password must be at least 6 characters!");
                    return;
                }

                if (userDAO.resetPassword(user.getId(), newPass)) {
                    System.out.println("✅ Password reset successfully! You can now login.");
                } else {
                    System.out.println("❌ Failed to reset password.");
                }
            } else {
                System.out.println("❌ Passwords don't match!");
            }
        } else {
            System.out.println("❌ Incorrect answer.");
        }
    }
    // ============== CONNECTIONS MENU METHODS ==============

private static void handleConnections() {
    while (true) {
        System.out.println("\n🤝 FOLLOW MANAGEMENT");
        System.out.println("1. 👀 View Follow Requests");
        System.out.println("2. 👥 Followers");
        System.out.println("3. 👣 Following");
        System.out.println("4. ⬅ Back to Main Menu");

        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            viewFollowRequests();
        } else if (choice == 2) {
            viewFollowers();
        } else if (choice == 3) {
            viewFollowing();
        } else if (choice == 4) {
            return;
        } else {
            System.out.println("❌ Invalid option!");
        }
    }
}

    private static void viewFollowRequests() {
        List<Integer> pendingRequests = followDAO.getPendingRequests(currentUser.getId());

        if (pendingRequests.isEmpty()) {
            System.out.println("📭 No pending follow requests.");
            return;
        }

        System.out.println("\n📨 PENDING FOLLOW REQUESTS (" + pendingRequests.size() + "):");
        for (int i = 0; i < pendingRequests.size(); i++) {
            User requester = userDAO.findById(pendingRequests.get(i));
            if (requester != null) {
                System.out.println("[" + (i + 1) + "] @" + requester.getUsername() + " - " + requester.getName());
            }
        }

        System.out.print("\nSelect request to respond (1-" + pendingRequests.size() + ") or 0 to go back: ");
        int requestChoice = sc.nextInt();
        sc.nextLine();

        if (requestChoice == 0) return;

        if (requestChoice < 1 || requestChoice > pendingRequests.size()) {
            System.out.println("❌ Invalid selection!");
            return;
        }

        int followerId = pendingRequests.get(requestChoice - 1);
        User follower = userDAO.findById(followerId);

        if (follower == null) {
            System.out.println("❌ User not found!");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("Follow request from: @" + follower.getUsername());
        System.out.println("Name: " + follower.getName());
        System.out.println("User Type: " + follower.getUserType());

        System.out.println("\nOptions:");
        System.out.println("1. ✅ Accept Request");
        System.out.println("2. 🤝 Accept & Follow Back");
        System.out.println("3. ❌ Reject Request");
        System.out.println("4. 👀 View Profile");
        System.out.println("5. ⬅ Back");

        System.out.print("Choose: ");
        int response = sc.nextInt();
        sc.nextLine();

        if (response == 1) {
            if (followDAO.acceptFollowRequest(followerId, currentUser.getId())) {
                System.out.println("✅ Follow request accepted!");
            }
        } else if (response == 2) {
            // Accept their request AND follow them back
            if (followDAO.acceptFollowRequest(followerId, currentUser.getId())) {
                System.out.println("✅ Follow request accepted!");

                // Check if we're already following them or have pending request
                if (!followDAO.isFollowing(currentUser.getId(), followerId) &&
                        !followDAO.hasPendingRequest(currentUser.getId(), followerId)) {

                    // Send follow request back to them
                    if (followDAO.sendFollowRequest(currentUser.getId(), followerId)) {
                        System.out.println("✅ Also sent follow request to @" + follower.getUsername());
                        // Auto-accept our follow request (optional)
                        followDAO.acceptFollowRequest(currentUser.getId(), followerId);
                        System.out.println("🤝 You're now following each other!");
                    }
                } else if (followDAO.hasPendingRequest(currentUser.getId(), followerId)) {
                    // We already sent a request, just accept it
                    followDAO.acceptFollowRequest(currentUser.getId(), followerId);
                    System.out.println("🤝 You're now following each other!");
                } else {
                    System.out.println("🤝 You're already following @" + follower.getUsername());
                }
            }
        } else if (response == 3) {
            if (followDAO.rejectFollowRequest(followerId, currentUser.getId())) {
                System.out.println("❌ Follow request rejected.");
            }
        } else if (response == 4) {
            viewOtherProfile(follower);
        } else if (response == 5) {
            return;
        } else {
            System.out.println("❌ Invalid option!");
        }
    }

    private static void viewFollowers() {
        List<Integer> followerIds = followDAO.getFollowers(currentUser.getId());
        System.out.println("\n👥 YOUR FOLLOWERS (" + followerIds.size() + "):");

        if (followerIds.isEmpty()) {
            System.out.println("No followers yet.");
            return;
        }

        for (int i = 0; i < followerIds.size(); i++) {
            User user = userDAO.findById(followerIds.get(i));
            if (user != null) {
                System.out.println("[" + (i + 1) + "] @" + user.getUsername() + " (" + user.getName() + ")");
            }
        }


        System.out.print("\nEnter follower number to view profile or 0 to go back: ");
        int followerChoice = sc.nextInt();
        sc.nextLine();

        if (followerChoice > 0 && followerChoice <= followerIds.size()) {
            User follower = userDAO.findById(followerIds.get(followerChoice - 1));
            if (follower != null) {
                viewOtherProfile(follower);
            }
        }
    }

    private static void viewFollowing() {
        List<Integer> followingIds = followDAO.getFollowing(currentUser.getId());
        System.out.println("\n👣 YOU'RE FOLLOWING (" + followingIds.size() + "):");

        if (followingIds.isEmpty()) {
            System.out.println("Not following anyone yet.");
            return;
        }

        for (int i = 0; i < followingIds.size(); i++) {
            User user = userDAO.findById(followingIds.get(i));
            if (user != null) {
                System.out.println("[" + (i + 1) + "] @" + user.getUsername() + " (" + user.getName() + ")");
            }
        }


        System.out.print("\nEnter following number to interact or 0 to go back: ");
        int followingChoice = sc.nextInt();
        sc.nextLine();

        if (followingChoice > 0 && followingChoice <= followingIds.size()) {
            User following = userDAO.findById(followingIds.get(followingChoice - 1));
            if (following != null) {
                System.out.println("\nOptions for @" + following.getUsername() + ":");
                System.out.println("1. 👀 View Profile");
                System.out.println("2. ❌ Unfollow");
                System.out.println("3. ⬅ Back");

                System.out.print("Choose: ");
                int option = sc.nextInt();
                sc.nextLine();

                if (option == 1) {
                    viewOtherProfile(following);
                } else if (option == 2) {
                    System.out.print("Are you sure you want to unfollow @" + following.getUsername() + "? (yes/no): ");
                    String confirm = sc.nextLine().toLowerCase();
                    if (confirm.equals("yes") || confirm.equals("y")) {
                        if (followDAO.unfollowUser(currentUser.getId(), following.getId())) {
                            System.out.println("✅ Unfollowed @" + following.getUsername());
                            // Refresh the list
                            viewFollowing();
                        }
                    }
                }
            }
        }
    }
    private static void handleFollows() {
        while (true) {
            System.out.println("\n👥 FOLLOW MANAGEMENT");
            System.out.println("1. 👤 Followers");
            System.out.println("2. 👣 Following");
            System.out.println("3. ⬅ Back to Connections Menu");

            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                List<Integer> followerIds = followDAO.getFollowers(currentUser.getId());
                System.out.println("\n👥 YOUR FOLLOWERS (" + followerIds.size() + "):");
                if (followerIds.isEmpty()) {
                    System.out.println("No followers yet.");
                } else {
                    for (int i = 0; i < followerIds.size(); i++) {
                        User user = userDAO.findById(followerIds.get(i));
                        if (user != null) {
                            System.out.println("[" + (i + 1) + "] @" + user.getUsername() + " (" + user.getName() + ")");
                        }
                    }


                    System.out.print("\nEnter follower number to view profile or 0 to go back: ");
                    int followerChoice = sc.nextInt();
                    sc.nextLine();

                    if (followerChoice > 0 && followerChoice <= followerIds.size()) {
                        User follower = userDAO.findById(followerIds.get(followerChoice - 1));
                        if (follower != null) {
                            viewOtherProfile(follower);
                        }
                    }
                }
            } else if (choice == 2) {
                List<Integer> followingIds = followDAO.getFollowing(currentUser.getId());
                System.out.println("\n👣 YOU'RE FOLLOWING (" + followingIds.size() + "):");
                if (followingIds.isEmpty()) {
                    System.out.println("Not following anyone yet.");
                } else {
                    for (int i = 0; i < followingIds.size(); i++) {
                        User user = userDAO.findById(followingIds.get(i));
                        if (user != null) {
                            System.out.println("[" + (i + 1) + "] @" + user.getUsername() + " (" + user.getName() + ")");
                        }
                    }


                    System.out.print("\nEnter following number to interact or 0 to go back: ");
                    int followingChoice = sc.nextInt();
                    sc.nextLine();

                    if (followingChoice > 0 && followingChoice <= followingIds.size()) {
                        User following = userDAO.findById(followingIds.get(followingChoice - 1));
                        if (following != null) {
                            System.out.println("\nOptions for @" + following.getUsername() + ":");
                            System.out.println("1. 👀 View Profile");
                            System.out.println("2. ❌ Unfollow");
                            System.out.println("3. ⬅ Back");

                            System.out.print("Choose: ");
                            int option = sc.nextInt();
                            sc.nextLine();

                            if (option == 1) {
                                viewOtherProfile(following);
                            } else if (option == 2) {
                                System.out.print("Are you sure you want to unfollow @" + following.getUsername() + "? (yes/no): ");
                                String confirm = sc.nextLine().toLowerCase();
                                if (confirm.equals("yes") || confirm.equals("y")) {
                                    if (followDAO.unfollowUser(currentUser.getId(), following.getId())) {
                                        System.out.println("✅ Unfollowed @" + following.getUsername());
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (choice == 3) {
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }

    // ============== POST METHODS ==============

    private static void createPost() {
        System.out.print("💭 What's on your mind? (max 280 chars): ");
        String content = sc.nextLine();
        System.out.print("Hashtags (comma separated, optional): ");
        String hashtags = sc.nextLine();

        Post post = new Post(currentUser.getId(), content, hashtags, false);
        if (postDAO.createPost(post)) {
            System.out.println("✅ Post created successfully! ID: " + post.getId());
        } else {
            System.out.println("❌ Failed to create post.");
        }
    }

    private static void viewFeed() {
        while (true) {
            List<Post> feed = postDAO.getUserFeed(currentUser.getId(), 10);
            if (feed.isEmpty()) {
                System.out.println("No posts in your feed yet. Follow/connect with users!");
                return;
            }

            System.out.println("\n=============== YOUR FEED ================");
            for (int i = 0; i < feed.size(); i++) {
                Post post = feed.get(i);

                boolean userLiked = likeDAO.hasLiked(currentUser.getId(), post.getId());

                System.out.println("\n----------------------------------------");
                System.out.println("[" + (i + 1) + "] Post ID: " + post.getId());
                System.out.println("@" + post.getUser().getUsername());
                System.out.println(post.getContent());
                System.out.println("Likes: " + post.getLikeCount() +
                        " | Comments: " + post.getCommentCount() +
                        " | Shares: " + post.getShareCount());
                System.out.println(post.getHashtags());
                System.out.println("----------------------------------------");
            }

            System.out.print("\nSelect post number to interact (1-" + feed.size() + ") or 0 to go back: ");
            int postChoice = sc.nextInt();
            sc.nextLine();

            if (postChoice == 0) {
                return;
            }

            if (postChoice < 1 || postChoice > feed.size()) {
                System.out.println("❌ Invalid selection!");
                continue;
            }

            Post selectedPost = feed.get(postChoice - 1);
            interactWithPost(selectedPost);
        }
    }


    private static void interactWithPost(Post post) {
        while (true) {
            boolean alreadyLiked = likeDAO.hasLiked(currentUser.getId(), post.getId());

            System.out.println("\n--- Post Options ---");
            System.out.println("1. " + (alreadyLiked ? "💔 Unlike" : "❤️ Like"));
            System.out.println("2. 💬 Comment");
            System.out.println("3. 🔄 Share");
            System.out.println("4. 🗑️ Delete Post");
            System.out.println("5. ✏️ Update Post");
            System.out.println("6. 👁️ View/Delete Comments");
            System.out.println("7. ⬅ Back to Feed");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                if (alreadyLiked) {
                    if (likeDAO.unlikePost(currentUser.getId(), post.getId())) {
                        System.out.println("✅ Unliked!");
                        post.setLikeCount(post.getLikeCount() - 1);
                    }
                } else {
                    if (likeDAO.likePost(currentUser.getId(), post.getId())) {
                        System.out.println("✅ Liked!");
                        if (post.getUserId() != currentUser.getId()) {
                            notificationDAO.notifyLike(post.getUserId(), currentUser.getId(), post.getId());
                        }
                        post.setLikeCount(post.getLikeCount() + 1);
                    }
                }
            } else if (choice == 2) {
                System.out.print("Enter comment: ");
                String comment = sc.nextLine();
                if (commentDAO.addComment(currentUser.getId(), post.getId(), comment)) {
                    System.out.println("✅ Comment added!");
                    if (post.getUserId() != currentUser.getId()) {
                        notificationDAO.notifyComment(post.getUserId(), currentUser.getId(), post.getId());
                    }
                    post.setCommentCount(post.getCommentCount() + 1);
                }
            } else if (choice == 3) {
                if (repostDAO.repost(currentUser.getId(), post.getId())) {
                    System.out.println("✅ Shared!");
                    if (post.getUserId() != currentUser.getId()) {
                        notificationDAO.notifyShare(post.getUserId(), currentUser.getId(), post.getId());
                    }
                    post.setShareCount(post.getShareCount() + 1);
                } else {
                    System.out.println("❌ Already shared or failed!");
                }
            } else if (choice == 4) {
                if (post.getId() > 0) {
                    if (postDAO.deletePost(post.getId(), currentUser.getId())) {
                        System.out.println("✅ Post deleted!");
                        return;
                    } else {
                        System.out.println("❌ Can't delete others' posts!");
                    }
                } else {
                    System.out.println("❌ Invalid post!");
                }
            } else if (choice == 5) {
                System.out.print("Enter new content: ");
                String newContent = sc.nextLine();
                if (postDAO.updatePost(post.getId(), currentUser.getId(), newContent)) {
                    System.out.println("✅ Post updated!");
                } else {
                    System.out.println("❌ Update failed!");
                }
            } else if (choice == 6) {
                viewAndDeleteComments(post);
            } else if (choice == 7) {
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }

            alreadyLiked = likeDAO.hasLiked(currentUser.getId(), post.getId());

            System.out.println("\nUpdated: Likes: " + post.getLikeCount() +
                    " | Comments: " + post.getCommentCount() +
                    " | Shares: " + post.getShareCount());
        }
    }


    private static void viewAndDeleteComments(Post post) {
        while (true) {
            // Get all comments for the post
            java.util.List<Comment> comments = commentDAO.getComments(post.getId());

            if (comments == null || comments.isEmpty()) {
                System.out.println("\n💬 No comments on this post yet.");
                return;
            }

            System.out.println("\n💬 COMMENTS ON POST ID " + post.getId() + ":");
            System.out.println("=========================================");

            // Display all comments
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                String prefix;
                if (comment.getUserId() == currentUser.getId()) {
                    prefix = "👤 YOU";
                } else if (comment.getUsername() != null) {
                    prefix = "@" + comment.getUsername();
                } else {
                    prefix = "Unknown User";
                }
                System.out.println("[" + (i + 1) + "] " + prefix + ": " + comment.getContent());
                System.out.println("    Comment ID: " + comment.getId());
            }

            System.out.println("\nOptions:");
            System.out.println("1. ❌ Delete my comment");
            System.out.println("2. ⬅ Back to post options");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                // Create list for user's comments
                java.util.List<Comment> myComments = new java.util.ArrayList<>();

                // Filter user's comments
                for (Comment comment : comments) {
                    if (comment.getUserId() == currentUser.getId()) {
                        myComments.add(comment);
                    }
                }

                if (myComments.isEmpty()) {
                    System.out.println("❌ You have no comments on this post to delete.");
                    continue;
                }

                System.out.println("\n🗑️ YOUR COMMENTS TO DELETE:");
                for (int i = 0; i < myComments.size(); i++) {
                    Comment comment = myComments.get(i);
                    System.out.println("[" + (i + 1) + "] " + comment.getContent() +
                            " (ID: " + comment.getId() + ")");
                }

                System.out.print("\nEnter comment number to delete (1-" + myComments.size() + ") or 0 to cancel: ");
                int commentChoice = sc.nextInt();
                sc.nextLine();

                if (commentChoice == 0) {
                    continue;
                }

                if (commentChoice < 1 || commentChoice > myComments.size()) {
                    System.out.println("❌ Invalid selection!");
                    continue;
                }

                Comment commentToDelete = myComments.get(commentChoice - 1);
                System.out.print("Are you sure you want to delete this comment? (yes/no): ");
                String confirm = sc.nextLine().toLowerCase();

                if (confirm.equals("yes") || confirm.equals("y")) {
                    if (commentDAO.deleteComment(commentToDelete.getId(), currentUser.getId())) {
                        System.out.println("✅ Comment deleted!");
                        post.setCommentCount(post.getCommentCount() - 1);
                    } else {
                        System.out.println("❌ Failed to delete comment!");
                    }
                } else {
                    System.out.println("Comment deletion cancelled.");
                }
            } else if (choice == 2) {
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }
    // ============== PROFILE METHODS ==============

    private static void viewProfile() {
        while (true) {
            // Get counts
            int followerCount = followDAO.getFollowers(currentUser.getId()).size();
            int followingCount = followDAO.getFollowing(currentUser.getId()).size();

            System.out.println("\n👤 YOUR PROFILE:");
            System.out.println("ID: " + currentUser.getId());
            System.out.println("Username: @" + currentUser.getUsername());
            System.out.println("Name: " + currentUser.getName());
            System.out.println("Type: " + currentUser.getUserType());
            System.out.println("Bio: " + (currentUser.getBio() != null ? currentUser.getBio() : "N/A"));
            System.out.println("Location: " + (currentUser.getLocation() != null ? currentUser.getLocation() : "N/A"));
            System.out.println("👥 Followers: " + followerCount + " | 👣 Following: " + followingCount);

            List<Post> posts = postDAO.getUserPosts(currentUser.getId());
            System.out.println("\n📝 YOUR POSTS (" + posts.size() + "):");

            if (posts.isEmpty()) {
                System.out.println("No posts yet. Create your first post!");
            } else {
                for (Post post : posts) {
                    System.out.println("- " + post.getContent() + " (ID: " + post.getId() + ")");
                }
            }

            System.out.println("\nOptions: 1. ✏️ Edit Profile  2. 🗑️ Delete Post  3. ✏️ Update Post  4. 👥 View Followers/Following  5. ⬅ Back");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                editProfile();
            } else if (choice == 2) {
                if (posts.isEmpty()) {
                    System.out.println("❌ You have no posts to delete.");
                    continue;
                }
                System.out.print("Enter Post ID to delete: ");
                int postId = sc.nextInt();
                sc.nextLine();
                if (postDAO.deletePost(postId, currentUser.getId())) {
                    System.out.println("✅ Post deleted.");
                } else {
                    System.out.println("❌ Failed to delete post. Make sure you own this post.");
                }
            } else if (choice == 3) {
                if (posts.isEmpty()) {
                    System.out.println("❌ You have no posts to update.");
                    continue;
                }
                System.out.print("Enter Post ID to update: ");
                int postId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter new content: ");
                String newContent = sc.nextLine();
                if (postDAO.updatePost(postId, currentUser.getId(), newContent)) {
                    System.out.println("✅ Post updated.");
                } else {
                    System.out.println("❌ Failed to update post. Make sure you own this post.");
                }
            } else if (choice == 4) {
                handleFollows();
            } else if (choice == 5) {
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }

    private static void editProfile() {
        System.out.print("Enter new name (leave blank to keep current): ");
        String name = sc.nextLine();
        if (!name.isEmpty()) currentUser.setName(name);

        System.out.print("Enter new bio (leave blank to keep current): ");
        String bio = sc.nextLine();
        if (!bio.isEmpty()) currentUser.setBio(bio);

        System.out.print("Enter new location (leave blank to keep current): ");
        String location = sc.nextLine();
        if (!location.isEmpty()) currentUser.setLocation(location);

        System.out.print("Enter new website (leave blank to keep current): ");
        String website = sc.nextLine();
        if (!website.isEmpty()) currentUser.setWebsite(website);

        if (userDAO.updateProfile(currentUser)) {
            System.out.println("✅ Profile updated successfully!");
        } else {
            System.out.println("❌ Failed to update profile.");
        }
    }

    // ============== SEARCH & USER PROFILE METHODS ==============

    private static void searchUsers() {
        System.out.print("🔍 Search users: ");
        String query = sc.nextLine();
        List<User> users = userDAO.searchUsers(query);

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n👥 SEARCH RESULTS:");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println("[" + (i + 1) + "] @" + user.getUsername() + " - " + user.getName() + " (" + user.getUserType() + ")");
        }

        System.out.print("\nSelect user number to view profile (1-" + users.size() + ") or 0 to go back: ");
        int userChoice = sc.nextInt();
        sc.nextLine();

        if (userChoice == 0) {
            return;
        }

        if (userChoice < 1 || userChoice > users.size()) {
            System.out.println("❌ Invalid selection!");
            return;
        }

        User selectedUser = users.get(userChoice - 1);
        viewOtherProfile(selectedUser);
    }

    private static void viewOtherProfile(User user) {
        notificationDAO.notifyProfileView(user.getId(), currentUser.getId());

        boolean isFollowing = followDAO.isFollowing(currentUser.getId(), user.getId());
        boolean hasPendingRequest = followDAO.hasPendingRequest(currentUser.getId(), user.getId());

        while (true) {
            System.out.println("\n==================================================");
            System.out.println("👤 PROFILE: @" + user.getUsername());
            System.out.println("Name: " + user.getName());
            System.out.println("Bio: " + (user.getBio() != null ? user.getBio() : "N/A"));
            System.out.println("Location: " + (user.getLocation() != null ? user.getLocation() : "N/A"));
            System.out.println("Type: " + user.getUserType());

            // Show follow status
            if (isFollowing) {
                System.out.println("📌 You are following this user");
            }
            if (hasPendingRequest) {
                System.out.println("⏳ Follow request pending");
            }

            System.out.println("\nOptions:");
            if (isFollowing) {
                System.out.println("1. ❌ Unfollow");
            } else if (hasPendingRequest) {
                System.out.println("1. ⏳ Request Pending");
            } else {
                System.out.println("1. ✅ Send Follow Request");
            }

            System.out.println("2. 📝 View User's Posts");
            System.out.println("3. ⬅ Back to Search");

            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                if (isFollowing) {
                    System.out.print("Are you sure you want to unfollow @" + user.getUsername() + "? (yes/no): ");
                    String confirm = sc.nextLine().toLowerCase();
                    if (confirm.equals("yes") || confirm.equals("y")) {
                        if (followDAO.unfollowUser(currentUser.getId(), user.getId())) {
                            System.out.println("✅ Unfollowed!");
                            isFollowing = false;
                        }
                    }
                } else if (!hasPendingRequest) {
                    // Send follow request
                    if (followDAO.sendFollowRequest(currentUser.getId(), user.getId())) {
                        System.out.println("✅ Follow request sent!");
                        hasPendingRequest = true;
                    }
                } else {
                    System.out.println("⏳ Follow request is already pending!");
                }
            } else if (choice == 2) {
                viewUserPosts(user);
            } else if (choice == 3) {
                return;
            } else {
                System.out.println("❌ Invalid option!");
            }
        }
    }
    private static void viewUserPosts(User user) {
        List<Post> posts = postDAO.getUserPosts(user.getId());
        System.out.println("\n📝 POSTS BY @" + user.getUsername() + " (" + posts.size() + "):");
        if (posts.isEmpty()) {
            System.out.println("No posts yet.");
        } else {
            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                System.out.println("\n----------------------------------------");
                System.out.println("[" + (i + 1) + "] " + post.getContent());
                System.out.println("Likes: " + post.getLikeCount() + " | Comments: " + post.getCommentCount() + " | Shares: " + post.getShareCount());
                System.out.println("Posted on: " + post.getCreatedAt());
                System.out.println(post.getHashtags());
            }
        }

        if (!posts.isEmpty()) {
            System.out.print("\nEnter post number to interact (1-" + posts.size() + ") or 0 to go back: ");
            int postChoice = sc.nextInt();
            sc.nextLine();

            if (postChoice > 0 && postChoice <= posts.size()) {
                Post selectedPost = posts.get(postChoice - 1);
                interactWithPost(selectedPost);
            }
        }
    }

    // ============== NOTIFICATION METHODS ==============

    private static void viewNotifications() {
        int unread = notificationDAO.getUnreadCount(currentUser.getId());
        System.out.println("🔔 You have " + unread + " unread notifications.");

        List<String> notifications = notificationDAO.getNotifications(currentUser.getId(), 10);
        if (notifications.isEmpty()) {
            System.out.println("No notifications yet.");
        } else {
            for (String n : notifications) {
                System.out.println(n);
            }
        }

        System.out.println("\nOptions: 1. Mark all as read  2. ⬅ Back");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            notificationDAO.markAllRead(currentUser.getId());
            System.out.println("✅ All notifications marked as read.");
        } else if (choice == 2) {
            return;
        }
    }
}