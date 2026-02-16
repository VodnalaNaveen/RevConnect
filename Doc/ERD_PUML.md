# 🧩 REVCONNECT MODULAR DESIGN
___

## 📦 Module 1: User & Authentication Module
```text

User/
├── UserDAO.java                    # User persistence operations
└── User.java                       # User entity (PERSONAL/CREATOR/BUSINESS)

```
### Responsibilities 

 User registration with email/username uniqueness

 Secure login with password verification

 Forgot password using security questions

 User type management (Personal/Creator/Business)

 Profile privacy controls

 Account deletion
___
## 🧩 Module 2: Post Management Module
```text
Post/
├── PostDAO.java                    # Post CRUD operations
└── Post.java                       # Post entity

```
### Responsibilities

Create posts with content and hashtags

Update/delete user's own posts

Generate personalized user feed

Track post engagement metrics

Support promotional content for businesses

___
## 🧩 Module 3: Social Interaction Module
```text
Social/
├── LikeDAO.java                    # Like/unlike operations
├── CommentDAO.java                 # Comment operations
├── RepostDAO.java                  # Share/repost operations
└── Comment.java                    # Comment model

```
### Responsibilities

Like/unlike posts with validation

Add comments to posts

Delete user's own comments

Repost/sharing with duplicate prevention

Real-time interaction updates
___
## 🧩 Module 4: Follow Management Module
```text
Follow/
├── FollowDAO.java                  # Follow operations

```
### Responsibilities

Send follow requests

Accept/reject follow requests

Unfollow users

Manage follower/following relationships

Track follow status (pending/accepted)
___
## 🧩 Module 5: Notification System Module
```text
Notification/
├── NotificationDAO.java            # Notification operations

```
### Responsibilities

Generate notifications for all social interactions

Track unread notifications

Format notifications with icons and timestamps

Mark notifications as read

Clean notification history
___
## 🧩 Module 6: Database & Utilities Module
```text
Payment & Notification/
├── PaymentService.java     (UPI / CARD / COD simulation)
└── NotificationService.java (Order notifications)
```
### Responsibilities

Database connection pooling

Resource cleanup and management

SQL execution with error handling

Application logging

Configuration management
___
# 📐 4. CLASS DIAGRAM (SIMPLIFIED)
```text
┌─────────────────────────┐
│   RevConnectApp         │
├─────────────────────────┤
│ +main()                 │
│ +showMainMenu()         │
│ +showUserMenu()         │
│ +handleConnections()    │
│ +interactWithPost()     │
│ +viewOtherProfile()     │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│     UserDAO             │
├─────────────────────────┤
│ -HASH_PREFIX            │
│ +register()             │
│ +login()                │
│ +searchUsers()          │
│ +updateProfile()        │
│ +recoverPassword()      │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│     PostDAO             │
├─────────────────────────┤
│ +createPost()           │
│ +updatePost()           │
│ +deletePost()           │
│ +getUserFeed()          │
│ +getUserPosts()         │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│   ConnectionDAO         │
├─────────────────────────┤
│ -userDAO                │
│ -notificationDAO        │
│ +sendConnectionRequest()│
│ +acceptRequest()        │
│ +getConnections()       │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│     FollowDAO           │
├─────────────────────────┤
│ -userDAO                │
│ -notificationDAO        │
│ +sendFollowRequest()    │
│ +acceptFollowRequest()  │
│ +getFollowers()         │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│  NotificationDAO        │
├─────────────────────────┤
│ +createNotification()   │
│ +notifyLike()           │
│ +notifyComment()        │
│ +getUnreadCount()       │
│ +getNotifications()     │
└─────────────────────────┘
            │
┌───────────▼─────────────┐
│    DatabaseUtil         │
├─────────────────────────┤
│ -URL, USERNAME, PASSWORD│
│ +getConnection()        │
│ +closeConnection()      │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│   Oracle Database       │
└─────────────────────────┘
```
___
# 🧩 5. COMPONENT DIAGRAM
```text
┌─────────────────────────────────────────────────┐
│           REVCONNECT CONSOLE APPLICATION        │
├─────────────────────────────────────────────────┤
│  User Input → Menu System → Service Coordination│
│                                                 │
│  ┌────────────────────┐  ┌────────────────────┐ │
│  │   User Management  │  │  Post Management   │ │
│  │ • Registration     │  │ • Create/Edit/Delte│ │
│  │ • Login/Recovery   │  │ • Feed Generation  │ │
│  │ • Profile Updates  │  │ • Statistics       │ │
│  └────────────────────┘  └────────────────────┘ │
│                                                 │
│  ┌────────────────────┐  ┌────────────────────┐ │
│  │ Social Interactions│  │  Network Management│ │
│  │ • Likes/Comments   │  │ • Follow System    │ │
│  │ • Shares/Reposts   │  │ • Connections      │ │
│  │ • Notifications    │  │ • Search Users     │ │
│  └────────────────────┘  └────────────────────┘ │
│                                                 │
│  ┌─────────────────────────────────────────────┐│
│  │        Security & Privacy Module            ││
│  │ • Password Management                       ││
│  │ • Security Questions                        ││
│  │ • Profile Privacy Settings                  ││
│  └─────────────────────────────────────────────┘│
└─────────────────────────┬───────────────────────┘
                          │
┌─────────────────────────▼───────────────────────┐
│              JDBC DAO LAYER                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐         │
│  │ UserDAO  │ │ PostDAO  │ │Connection│         │
│  └──────────┘ └──────────┘ │   DAO    │         │
│  ┌──────────┐ ┌──────────┐ └──────────┘         │
│  │ FollowDAO│ │ Comment  │ ┌──────────┐         │
│  └──────────┘ │   DAO    │ │  LikeDAO │         │
│  ┌──────────┐ └──────────┘ └──────────┘         │
│  │  Notif.  │ ┌──────────┐ ┌──────────┐         │
│  │   DAO    │ │ RepostDAO│ │          │         │
│  └──────────┘ └──────────┘ └──────────┘         │
└─────────────────────────┬───────────────────────┘
                          │
┌─────────────────────────▼───────────────────────┐
│            DATABASE CONNECTION LAYER            │
│              DatabaseUtil.java                  │
│          • Connection Pooling                   │
│          • Resource Management                  │
│          • Error Handling                       │
└─────────────────────────┬───────────────────────┘
                          │
┌─────────────────────────▼───────────────────────┐
│              ORACLE DATABASE                    │
│        ┌────────────────────────────┐           │
│        │       8 Normalized Tables  │           │
│        │ ┌─────┐ ┌─────┐ ┌───────┐  │           │
│        │ │Users│ │Posts│ │Comment│  │           │
│        │ └─────┘ └─────┘ └───────┘  │           │
│        │ ┌──────┐ ┌─────┐┌───────┐  │           │
│        │ │Follow│ │Likes││Connect│  │           │
│        │ └──────┘ └─────┘└───────┘  │           │
│        │ ┌───────┐ ┌─────────────┐  │           │
│        │ │Reposts│ │Notifications│  │           │
│        │ └───────┘ └─────────────┘  │           │
│        └────────────────────────────┘           │
└─────────────────────────────────────────────────┘
```
___
# ⚡ 6. SEQUENCE DIAGRAM – Registration FLOW
```text
Console Interface → User Module → Database Module
      ↓
Registration Success → Notification Module (optional welcome)
```
___
# ⚡ 7. SEQUENCE DIAGRAM – Post Creation Flow
```text
Console Interface → Post Module → Database
      ↓
Success → Notification Module (notify followers)
```

___
# 🔐 8. SECURITY ARCHITECTURE
```text
Layer 1: Input Validation (Console input)
Layer 2: Authentication (Username/Password + Security Questions)
Layer 3: Authorization (User-specific permissions)
Layer 4: Data Security (Password hashing, secure answers)
Layer 5: Database Security (Prepared statements, connection security)
Layer 6: Audit Logging (Log4j2 for all operations)
```


