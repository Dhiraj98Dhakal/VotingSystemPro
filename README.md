# VotingSystemPro
The application uses SQLite embedded database - no server required!
# 🗳️ Voting System Nepal - Election Commission Management System

![Version](https://img.shields.io/badge/version-2.0.0-blue)
![Java](https://img.shields.io/badge/Java-11%2B-orange)
![SQLite](https://img.shields.io/badge/SQLite-3-green)
![License](https://img.shields.io/badge/license-Govt%20of%20Nepal-red)

A comprehensive **Desktop Voting System** designed for the Election Commission of Nepal to manage elections, voters, parties, candidates, and real-time vote counting.

---

## 📋 Table of Contents
- [Features](#-features)
- [Screenshots](#-screenshots)
- [System Requirements](#-system-requirements)
- [Installation](#-installation)
- [Usage Guide](#-usage-guide)
- [Default Login Credentials](#-default-login-credentials)
- [Database Configuration](#-database-configuration)
- [Email Configuration](#-email-configuration)
- [Project Structure](#-project-structure)
- [Building from Source](#-building-from-source)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

---

## ✨ Features

### 👤 **For Voters**
- ✅ **Voter Registration** - Online registration with citizenship details
- ✅ **Email Verification** - Get Voter ID via email after approval
- ✅ **Secure Login** - Login with Voter ID and password
- ✅ **FPTP & PR Voting** - Cast both FPTP and PR votes
- ✅ **Voter Dashboard** - View personal details and voting status
- ✅ **News & Updates** - Read latest election news with like/comment features
- ✅ **Download Voter ID** - Generate PDF of voter ID card

### 👑 **For Administrators**
- ✅ **Admin Dashboard** - Real-time statistics and quick actions
- ✅ **Voter Management** - Approve/reject voter registrations
- ✅ **Bulk Email** - Send Voter IDs to approved voters
- ✅ **Party Management** - Add/edit/delete political parties
- ✅ **Candidate Management** - Manage FPTP and PR candidates
- ✅ **Location Management** - Manage provinces, districts, constituencies
- ✅ **Live Vote Count** - Real-time vote counting dashboard
- ✅ **News Management** - Post news with images and manage comments
- ✅ **Activity Logs** - Track all system activities

---

## 📸 Screenshots

| Admin Dashboard | Voter Dashboard |
|-----------------|-----------------|
| ![Admin Dashboard](screenshots/admin-dashboard.png) | ![Voter Dashboard](screenshots/voter-dashboard.png) |

| Live Vote Count | Voting Screen |
|-----------------|---------------|
| ![Live Vote Count](screenshots/live-vote.png) | ![Voting](screenshots/voting.png) |

---

## 💻 System Requirements

### Minimum Requirements
- **OS:** Windows 7/8/10/11, macOS, or Linux
- **CPU:** Intel Core i3 or equivalent
- **RAM:** 2 GB
- **Storage:** 100 MB free space
- **Java:** JDK 11 or higher

### Recommended Requirements
- **OS:** Windows 10/11
- **CPU:** Intel Core i5 or higher
- **RAM:** 4 GB or more
- **Storage:** 500 MB free space
- **Java:** JDK 17 or higher
- **Internet:** Required for email features

---

## 📥 Installation

### Option 1: Download Pre-built JAR (Easy)

1. **Download** the latest release from [Releases Page](https://github.com/yourusername/voting-system-nepal/releases)
2. **Double-click** `VotingSystemPro-2.0.0.jar` to run
3. Database will be automatically created at: `C:\Users\[YourName]\VotingSystem\voting_system.db`

### Option 2: Build from Source

#### Prerequisites
- Java JDK 11+
- Maven 3.6+
- Git (optional)

#### Steps

```bash
# Clone the repository
git clone https://github.com/yourusername/voting-system-nepal.git

# Navigate to project directory
cd voting-system-nepal

# Build with Maven
mvn clean package

# Run the application
java -jar target/VotingSystemPro-2.0.0.jar
