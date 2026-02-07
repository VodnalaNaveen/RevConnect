//package com.revconnect.entities;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class Post {
//    private int id;
//    private int userId;
//    private String content;
//    private String hashtags;
//    private boolean isPromotional;
//    private LocalDateTime scheduledTime;
//    private boolean pinned;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private User user; // For display purposes
//    private int likeCount;
//    private int commentCount;
//    private int shareCount;
//
//    // Constructors
//    public Post() {}
//
//    public Post(int userId, String content, String hashtags) {
//        this.userId = userId;
//        this.content = content;
//        this.hashtags = hashtags;
//    }
//
//    // Getters and Setters
//    public int getId() { return id; }
//    public void setId(int id) { this.id = id; }
//    public int getUserId() { return userId; }
//    public void setUserId(int userId) { this.userId = userId; }
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//    public String getHashtags() { return hashtags; }
//    public void setHashtags(String hashtags) { this.hashtags = hashtags; }
//    public boolean isPromotional() { return isPromotional; }
//    public void setPromotional(boolean promotional) { isPromotional = promotional; }
//    public LocalDateTime getScheduledTime() { return scheduledTime; }
//    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
//    public boolean isPinned() { return pinned; }
//    public void setPinned(boolean pinned) { this.pinned = pinned; }
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//    public LocalDateTime getUpdatedAt() { return updatedAt; }
//    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
//    public User getUser() { return user; }
//    public void setUser(User user) { this.user = user; }
//    public int getLikeCount() { return likeCount; }
//    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
//    public int getCommentCount() { return commentCount; }
//    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
//    public int getShareCount() { return shareCount; }
//    public void setShareCount(int shareCount) { this.shareCount = shareCount; }
//
//    @Override
//    public String toString() {
//        return "Post ID: " + id + "\n" +
//                (user != null ? "@" + user.getUsername() : "Unknown") + "\n" +
//                content + "\n" +
//                "Likes: " + likeCount + " | Comments: " + commentCount + " | Shares: " + shareCount + "\n" +
//                (hashtags != null ? "#" + hashtags.replace(",", " #") : "");
//    }
//
//}



package com.revconnect.entities;

import java.time.LocalDateTime;

public class Post {
    private int id;
    private int userId;
    private String content;
    private String hashtags;
    private boolean isPromotional;
    private LocalDateTime createdAt;
    private User user; // reference to post owner
    private int likeCount;
    private int commentCount;
    private int shareCount;

    public Post() {}

    // ✅ Constructor you need
    public Post(int userId, String content, String hashtags, boolean isPromotional) {
        this.userId = userId;
        this.content = content;
        this.hashtags = hashtags;
        this.isPromotional = isPromotional;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }

    public boolean isPromotional() { return isPromotional; }
    public void setPromotional(boolean promotional) { isPromotional = promotional; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public int getShareCount() { return shareCount; }
    public void setShareCount(int shareCount) { this.shareCount = shareCount; }
}
