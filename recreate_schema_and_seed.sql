-- =====================================================================
-- Placement Management System — Schema Re-creation and Data Seeding
-- =====================================================================

CREATE DATABASE IF NOT EXISTS placement_management_system;
USE placement_management_system;

-- Disable foreign key checks for clean teardown
SET FOREIGN_KEY_CHECKS = 0;

-- Drop incorrect tables from any other schema versions
DROP TABLE IF EXISTS activity_logs;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS interviews;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS offers;
DROP TABLE IF EXISTS interview_schedule;
DROP TABLE IF EXISTS placements;

-- Drop correct tables to clean slate
DROP TABLE IF EXISTS application_history;
DROP TABLE IF EXISTS application_queue;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS drive_branches;
DROP TABLE IF EXISTS drives;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- 1. CREATE TABLES
-- =====================================================================

-- Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'student', 'recruiter') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students Table
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    branch VARCHAR(50) NOT NULL,
    cgpa DECIMAL(3,2) NOT NULL,
    backlog_count INT DEFAULT 0,
    phone VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    resume_link VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Companies Table
CREATE TABLE companies (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    website VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Drives Table
CREATE TABLE drives (
    drive_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    job_role VARCHAR(100) NOT NULL,
    package_lpa DECIMAL(5,2),
    min_cgpa DECIMAL(3,2) DEFAULT 0.00,
    max_backlogs INT DEFAULT 0,
    drive_date DATE,
    status ENUM('upcoming', 'ongoing', 'completed') DEFAULT 'upcoming',
    FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE
);

-- Drive Branches Table (many-to-many relationship mapping)
CREATE TABLE drive_branches (
    drive_id INT NOT NULL,
    branch VARCHAR(50) NOT NULL,
    PRIMARY KEY (drive_id, branch),
    FOREIGN KEY (drive_id) REFERENCES drives(drive_id) ON DELETE CASCADE
);

-- Applications Table
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    drive_id INT NOT NULL,
    application_date DATE DEFAULT (CURDATE()),
    status ENUM('applied', 'shortlisted', 'interview_scheduled', 'selected', 'rejected') DEFAULT 'applied',
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id) REFERENCES drives(drive_id) ON DELETE CASCADE
);

-- Application History Table
CREATE TABLE application_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(application_id) ON DELETE CASCADE
);

-- Application Queue Table
CREATE TABLE application_queue (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    drive_id INT NOT NULL,
    queued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_drive (student_id, drive_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id) REFERENCES drives(drive_id) ON DELETE CASCADE
);

-- =====================================================================
-- 2. SEED MOCK DATA
-- =====================================================================

-- Seed Users
INSERT INTO users (user_id, username, password, role) VALUES (1, 'admin1', 'admin123', 'admin');

INSERT INTO users (user_id, username, password, role) VALUES (2, 'shashank', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (3, 'shivam', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (4, 'ayush', 'pass123', 'student');

INSERT INTO users (user_id, username, password, role) VALUES (5, 'aditya', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (6, 'rahul', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (7, 'priya', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (8, 'sneha', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (9, 'amit', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (10, 'neha', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (11, 'vikram', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (12, 'pooja', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (13, 'rohit', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (14, 'divya', 'pass123', 'student');

INSERT INTO users (user_id, username, password, role) VALUES (15, 'tcs_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (16, 'google_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (17, 'microsoft_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (18, 'cognizant_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (19, 'amazon_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (20, 'infosys_recruiter', 'pass123', 'recruiter');

-- Seed Students
INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (1, 2, 'Shashank Pandey', 'CSE', 9.20, 0, '9876543210', 'shashank@gmail.com', 'https://resume.com/shashank');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (2, 3, 'Shivam Shukla', 'CSE', 8.80, 0, '9876543211', 'shivam@gmail.com', 'https://resume.com/shivam');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (3, 4, 'Ayush Mishra', 'IT', 9.00, 0, '9876543212', 'ayush@gmail.com', 'https://resume.com/ayush');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (4, 5, 'Aditya Singh', 'CSE', 9.50, 0, '8765432101', 'aditya@gmail.com', 'https://resume.com/aditya');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (5, 6, 'Rahul Sharma', 'IT', 8.20, 1, '8765432102', 'rahul@gmail.com', 'https://resume.com/rahul');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (6, 7, 'Priya Patel', 'ECE', 7.80, 2, '8765432103', 'priya@gmail.com', 'https://resume.com/priya');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (7, 8, 'Sneha Gupta', 'CSE', 9.80, 0, '8765432104', 'sneha@gmail.com', 'https://resume.com/sneha');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (8, 9, 'Amit Verma', 'ME', 7.50, 0, '8765432105', 'amit@gmail.com', 'https://resume.com/amit');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (9, 10, 'Neha Sen', 'EE', 8.50, 1, '8765432106', 'neha@gmail.com', 'https://resume.com/neha');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (10, 11, 'Vikram Rathore', 'IT', 8.90, 0, '8765432107', 'vikram@gmail.com', 'https://resume.com/vikram');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (11, 12, 'Pooja Joshi', 'CSE', 9.10, 0, '8765432108', 'pooja@gmail.com', 'https://resume.com/pooja');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (12, 13, 'Rohit Kumar', 'CE', 6.80, 0, '8765432109', 'rohit@gmail.com', 'https://resume.com/rohit');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (13, 14, 'Divya Mehta', 'ECE', 8.40, 0, '8765432110', 'divya@gmail.com', 'https://resume.com/divya');

-- Seed Companies
INSERT INTO companies (company_id, user_id, name, description, website)
VALUES (1, 15, 'TCS', 'Tata Consultancy Services - IT consulting and business services.', 'https://www.tcs.com');

INSERT INTO companies (company_id, user_id, name, description, website)
VALUES (2, 16, 'Google', 'Google LLC - Search, AI, cloud computing, and consumer tech.', 'https://www.google.com');

INSERT INTO companies (company_id, user_id, name, description, website)
VALUES (3, 17, 'Microsoft', 'Microsoft Corporation - software, personal computers, and cloud.', 'https://www.microsoft.com');

INSERT INTO companies (company_id, user_id, name, description, website)
VALUES (4, 18, 'Cognizant', 'Cognizant - digital transition systems, custom IT consulting.', 'https://www.cognizant.com');

INSERT INTO companies (company_id, user_id, name, description, website)
VALUES (5, 19, 'Amazon', 'Amazon - global e-commerce, cloud web platforms (AWS) and devices.', 'https://www.amazon.com');

INSERT INTO companies (company_id, user_id, name, description, website)
VALUES (6, 20, 'Infosys', 'Infosys Limited - outsourcing services, consulting, next-gen IT solutions.', 'https://www.infosys.com');

-- Seed Drives
INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status)
VALUES (1, 1, 'System Engineer', 3.60, 6.00, 2, '2026-08-15', 'ongoing');

INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status)
VALUES (2, 1, 'Digital Developer', 7.00, 7.50, 0, '2026-08-20', 'upcoming');

INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status)
VALUES (3, 2, 'Software Engineer L3', 32.00, 8.50, 0, '2026-09-01', 'ongoing');

INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status)
VALUES (4, 3, 'Support Consultant', 12.00, 7.00, 1, '2026-09-10', 'upcoming');

INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status)
VALUES (5, 5, 'Software Development Engineer', 28.00, 8.00, 0, '2026-08-25', 'ongoing');

INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status)
VALUES (6, 6, 'Power Programmer', 9.50, 8.00, 0, '2026-07-05', 'completed');

-- Seed Drive Eligible Branches
INSERT INTO drive_branches (drive_id, branch) VALUES (1, 'CSE'), (1, 'IT'), (1, 'ECE');
INSERT INTO drive_branches (drive_id, branch) VALUES (2, 'CSE'), (2, 'IT');
INSERT INTO drive_branches (drive_id, branch) VALUES (3, 'CSE'), (3, 'IT'), (3, 'ECE');
INSERT INTO drive_branches (drive_id, branch) VALUES (4, 'CSE'), (4, 'IT'), (4, 'ECE'), (4, 'EE');
INSERT INTO drive_branches (drive_id, branch) VALUES (5, 'CSE'), (5, 'IT');
INSERT INTO drive_branches (drive_id, branch) VALUES (6, 'CSE'), (6, 'IT');

-- Seed Applications
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (1, 1, 1, '2026-07-08', 'selected');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (2, 1, 3, '2026-07-09', 'shortlisted');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (3, 1, 5, '2026-07-10', 'applied');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (4, 2, 1, '2026-07-08', 'interview_scheduled');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (5, 2, 3, '2026-07-09', 'rejected');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (6, 3, 1, '2026-07-08', 'applied');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (7, 3, 5, '2026-07-10', 'shortlisted');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (8, 4, 3, '2026-07-09', 'selected');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (9, 7, 3, '2026-07-09', 'shortlisted');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (10, 10, 1, '2026-07-08', 'rejected');

-- Seed Application History
INSERT INTO application_history (application_id, status) VALUES (1, 'applied'), (1, 'shortlisted'), (1, 'interview_scheduled'), (1, 'selected');
INSERT INTO application_history (application_id, status) VALUES (4, 'applied'), (4, 'shortlisted'), (4, 'interview_scheduled');
INSERT INTO application_history (application_id, status) VALUES (7, 'applied'), (7, 'shortlisted');

-- Seed Application Queue Entries
INSERT INTO application_queue (student_id, drive_id) VALUES (2, 2);
INSERT INTO application_queue (student_id, drive_id) VALUES (3, 3);
INSERT INTO application_queue (student_id, drive_id) VALUES (8, 1);
INSERT INTO application_queue (student_id, drive_id) VALUES (7, 5);
