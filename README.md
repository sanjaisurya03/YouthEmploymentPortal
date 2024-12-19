# Youth Employment Portal

This project is a **Youth Employment Portal** built using **Java Swings** for the graphical user interface and **JDBC (Java Database Connectivity)** for database interactions. The project is developed in the **Eclipse IDE**, making it suitable for managing and enhancing the system. It is designed to help youths find job opportunities and employers to post job openings.

## Features

### 1. **User Registration and Login**
- **Job Seekers** and **Employers** can register and log in with unique credentials.
- Secure password storage with database hashing techniques.

### 2. **Job Listings**
- Employers can post job openings with details such as:
  - Job title
  - Description
  - Required skills
  - Location
  - Salary range
- Job seekers can browse, search, and apply for jobs.

### 3. **Profile Management**
- **Job Seekers** can update their profiles, upload resumes, and specify skill sets.
- **Employers** can manage company profiles and view applicant details.

### 4. **Search Functionality**
- Advanced filtering options based on:
  - Skills
  - Job location
  - Experience level

### 5. **Application Tracking**
- Job seekers can track the status of their applications (e.g., Pending, Reviewed, Rejected, Accepted).
- Employers can manage and review applications received.

### 6. **Admin Panel**
- Manage users, job postings, and ensure platform moderation.

---

## Technologies Used

### 1. **Programming Language**
- **Java** for backend and frontend logic.

### 2. **Frameworks and Tools**
- **Swing**: For creating the user interface.
- **JDBC**: For database connectivity.
- **Eclipse IDE**: For development and debugging.

### 3. **Database**
- **MySQL** (or any preferred RDBMS): For storing user data, job postings, and application information.

---

## Prerequisites

1. **Java Development Kit (JDK)**: Version 8 or later.
2. **Eclipse IDE**: Latest version recommended.
3. **MySQL Server**: Installed and configured.
4. **JDBC Driver**: Added to the project classpath.

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/youth-employment-portal.git
   ```
2. Open the project in **Eclipse IDE**.
3. Configure the **JDBC connection**:
   - Open the `DBConnection.java` file.
   - Update the database URL, username, and password as per your MySQL configuration.
   ```java
   String url = "jdbc:mysql://localhost:3306/your-database-name";
   String user = "your-username";
   String password = "your-password";
   ```
4. Import the database schema:
   - Run the SQL file provided in the `/db` folder to create tables.

5. Run the project:
   - Right-click on the `Main.java` file and select **Run As > Java Application**.
