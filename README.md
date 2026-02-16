# 🤝 RevConnect – Console-Based Social Media Application (Java + JDBC)

RevConnect is a **console-based social media application** developed using **Core Java, JDBC, Oracle Database, and Maven**, implementing comprehensive social networking features with advanced security and privacy controls.  
The application simulates a real-world social platform supporting **personal users, creators, and businesses**, with robust connection management and notification systems.

---

## 🚀 Features

### 👤 User Features
- **Multi-type Registration**: Personal, Creator, and Business accounts
- **Secure Authentication** with password hashing
- **Profile Management**: Bio, location, website
- **Privacy Controls**: Public/Private profile settings
- **Security Questions & Password Recovery**
- **Password Hints** for user assistance

### 📱 Social Interactions
- **Post Creation** with hashtags
- **Like/Unlike** posts with notification triggers
- **Comment System**
- **Post Sharing/Reposting**
- **Post Editing & Deletion**

### 👥 Relationship Management
- **Follow System**: Request-based following with approval workflow
- **Connection System**: Personal user connections
- **Follow Back** automation
- **Pending Requests** management
- **Unfollow/Remove Connection** options

### 🔔 Notifications
- **Real-time notifications** for:
  - Likes, Comments, Shares
  - Follow requests and acceptances
  - Connection requests and acceptances
  - Profile views
- **Unread notification count**
- **Mark all as read** functionality

### 🔍 Discovery
- **User Search** by name or username
- **Personalized Feed** showing posts from:
  - Own posts
  - Followed users
  - Connections
- **Profile Viewing** with privacy respect
- **User Posts** browsing

---

## 🧱 Project Architecture

```text
RevConnect/
├── pom.xml
└── src/
   ├── main/
   │   ├── java/com/revconnect/
   │   │   ├── RevConnectApp.java              
   │   │   ├── dao/                            
   │   │   │   ├── UserDAO.java
   │   │   │   ├── PostDAO.java
   │   │   │   ├── ConnectionDAO.java
   │   │   │   ├── FollowDAO.java
   │   │   │   ├── CommentDAO.java
   │   │   │   ├── LikeDAO.java
   │   │   │   ├── NotificationDAO.java
   │   │   │   └── RepostDAO.java
   │   │   ├── entities/                      
   │   │   │   ├── User.java
   │   │   │   └── Post.java
   │   │   ├── models/                         
   │   │   │   └── Comment.java
   │   │   └── utils/                          
   │   │       └── DatabaseUtil.java
   │   └── resources/
   │       ├── database.sql                    
   │       └── log4j2.xml                      
   └── test/
       └── java/com/revconnect/                
           ├── UserDAOTest.java
           ├── PostDAOTest.java
           ├── FollowDAOTest.java
           └── ConnectionDAOTest.java
```
---

## 🛠️ Tech Stack

| Technology | Purpose |
|----------|--------|
| **Java 11** | Core application logic |
| **Oracle Database 21c XE** | Persistent storage |
| **JDBC** | Database connectivity |
| **Apache Maven** | Build automation & dependency management |
| **JUnit 5** | Unit testing framework |
| **Log4j 2** | Logging framework |
| **IntelliJ IDEA** | Development IDE |

---

## 🔐 Security Implementation

- **Password Hashing** – Custom hashing algorithm with secure prefix  
- **Security Questions** – Pre-defined questions with hashed answers  
- **Password Recovery** – Email + security question verification  
- **Privacy Settings** – Public / Private profile controls  
- **User Permissions** – Content ownership validation  
- **Input Validation** – Applied across all application layers  

---

## 🧪 Testing

### ✅ Test Coverage
- **28 comprehensive test cases** across **4 test classes**
- **100% pass rate** on core functionality
- **DAO Layer Testing** – Validates all database operations
- **Integration Testing** – End-to-end feature validation
- **Edge Case Handling** – Boundary conditions and error scenarios

### 🧩 Test Classes
- **UserDAOTest** – Registration, login, password recovery, security features  
- **PostDAOTest** – Post CRUD operations and feed retrieval  
- **FollowDAOTest** – Follow request workflow and management  
- **ConnectionDAOTest** – Connection system and status handling  

### ▶️ Run Tests
```bash
mvn test
```
---
## 🗄️ Database Schema

### 📌 Core Tables

- **users** – User accounts with security and privacy settings  
- **posts** – User posts with engagement metrics  
- **comments** – Post comments  
- **likes** – Post likes with unique constraints  
- **follows** – Follow relationships with status tracking  
- **connections** – Personal connection requests  
- **notifications** – User notifications  
- **reposts** – Shared posts  

---
## ▶️ How to Run
### 1️⃣ Prerequisites
```
# Install Oracle Database 21c XE
# Install JDK 11+
# Install Apache Maven
```
### 2️⃣ Clone & Setup

```
git clone https://github.com/yourusername/RevConnect.git
cd RevConnect
```

### 3️⃣ Database Configuration

```
-- Run the database script
@src/main/resources/database.sql
```

### 4️⃣ Update Database Credentials

Edit DatabaseUtil.java:

```
private static final String URL = "jdbc:oracle:thin:@//localhost:1521/xe";
private static final String USERNAME = "system";
private static final String PASSWORD = "yourpassword";
```

### 5️⃣ Build & Run

```
# Build the project
mvn clean compile


# Run the application
RevConnectApp.java
```

📊 Application Flow
```text
┌─────────────────┐
│   Main Menu     │
│ 1. Register     │
│ 2. Login        │
│ 3. Forgot Pass  │
│ 4. Exit         │
└────────┬────────┘
         │
┌────────▼────────┐
│   User Menu     │
│ 1. Create Post  │
│ 2. View Feed    │
│ 3. My Profile   │
│ 4. Search Users │
│ 5. Connections  │
│ 6. Notifications│
│ 7. Security     │
│ 8. Delete Acc   │
│ 9. Logout       │
└─────────────────┘
```
---
## 📈 Future Enhancements

**Convert to Spring Boot REST APIs**

**Web UI (React / Angular)**

**Real-time chat**

**Media uploads (Images/Videos)**

**Stories feature (24-hour content)**

**Groups and Communities**

**Mobile applications (iOS/Android)**

**Hashtag trending algorithms**

**Two-factor authentication**

**Accessibility features (Screen reader support)**

**Export data feature (GDPR compliance)**

**Third-party app integrations**

**Advanced reporting system**
