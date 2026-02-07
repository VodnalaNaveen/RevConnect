package com.revconnect.entities;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String email;
    private String password;
    private String username;
    private UserType userType;
    private String name;
    private String bio;
    private String profilePicture;
    private String location;
    private String website;
    private String businessName;
    private String category;
    private String businessAddress;
    private String contactInfo;
    private String businessHours;
    private LocalDateTime createdAt;
    private boolean isPrivate;
    private String securityQuestion;
    private String securityAnswer;
    private String passwordHint;
    private boolean isPublic = true;
    public enum UserType {
        PERSONAL, CREATOR, BUSINESS
    }

    // Constructors
    public User() {}

    public User(String email, String password, String username, UserType userType, String name) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userType = userType;
        this.name = name;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    // Additional getters/setters...
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    public String getPasswordHint() { return passwordHint; }
    public void setPasswordHint(String passwordHint) { this.passwordHint = passwordHint; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }




    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "Username: @" + username + "\n" +
                "Name: " + name + "\n" +
                "Type: " + userType + "\n" +
                "Bio: " + (bio != null ? bio : "N/A") + "\n" +
                "Location: " + (location != null ? location : "N/A");
    }

}
