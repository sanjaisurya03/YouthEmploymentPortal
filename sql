CREATE DATABASE youth_employment;
USE youth_employment;
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('job_seeker', 'company') NOT NULL,
    full_name VARCHAR(255),
    phone_number VARCHAR(20),
    UNIQUE (email)
);
CREATE TABLE profile (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone_number VARCHAR(20),
    age INT,
    experience TEXT,
    skills TEXT,
    FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE
);
CREATE TABLE jobs (
    job_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    company_email VARCHAR(255) NOT NULL,
    FOREIGN KEY (company_email) REFERENCES users(email) ON DELETE CASCADE
);
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    job_id INT NOT NULL,
    status ENUM('Pending', 'Accepted', 'Rejected') DEFAULT 'Pending',
    FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE
);
