-- =====================================================================
-- Placement Management System — Large Seeding script for demonstration
-- =====================================================================

CREATE DATABASE IF NOT EXISTS placement_management_system;
USE placement_management_system;

-- Disable foreign key checks for clean teardown
SET FOREIGN_KEY_CHECKS = 0;

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

-- Drive Branches Table
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

-- Seed Admins (user_id 1 - 3, 61 is short alias)
INSERT INTO users (user_id, username, password, role) VALUES 
(1, 'admin1', 'admin123', 'admin'),
(2, 'admin2', 'admin123', 'admin'),
(3, 'admin3', 'admin123', 'admin'),
(61, 'admin', 'admin123', 'admin');

-- Seed Students (user_id 4 - 45)
INSERT INTO users (user_id, username, password, role) VALUES 
(4, 'shashank', 'pass123', 'student'),
(5, 'shivam', 'pass123', 'student'),
(6, 'ayush', 'pass123', 'student'),
(7, 'aditya', 'pass123', 'student'),
(8, 'rahul', 'pass123', 'student'),
(9, 'priya', 'pass123', 'student'),
(10, 'sneha', 'pass123', 'student'),
(11, 'amit', 'pass123', 'student'),
(12, 'neha', 'pass123', 'student'),
(13, 'vikram', 'pass123', 'student'),
(14, 'pooja', 'pass123', 'student'),
(15, 'rohit', 'pass123', 'student'),
(16, 'divya', 'pass123', 'student'),
(17, 'anjali', 'pass123', 'student'),
(18, 'manish', 'pass123', 'student'),
(19, 'kavita', 'pass123', 'student'),
(20, 'rajesh', 'pass123', 'student'),
(21, 'sunita', 'pass123', 'student'),
(22, 'arjun', 'pass123', 'student'),
(23, 'deepa', 'pass123', 'student'),
(24, 'manoj', 'pass123', 'student'),
(25, 'sandeep', 'pass123', 'student'),
(26, 'vivek', 'pass123', 'student'),
(27, 'harish', 'pass123', 'student'),
(28, 'tanvi', 'pass123', 'student'),
(29, 'ritu', 'pass123', 'student'),
(30, 'alok', 'pass123', 'student'),
(31, 'rohan', 'pass123', 'student'),
(32, 'shruti', 'pass123', 'student'),
(33, 'nikhil', 'pass123', 'student'),
(34, 'meera', 'pass123', 'student'),
(35, 'sanjay', 'pass123', 'student'),
(36, 'anil', 'pass123', 'student'),
(37, 'madhuri', 'pass123', 'student'),
(38, 'juhi', 'pass123', 'student'),
(39, 'preity', 'pass123', 'student'),
(40, 'raveena', 'pass123', 'student'),
(41, 'karisma', 'pass123', 'student'),
(42, 'kajol', 'pass123', 'student'),
(43, 'tabu', 'pass123', 'student'),
(44, 'aishwarya', 'pass123', 'student'),
(45, 'sushmita', 'pass123', 'student');

-- Seed Student Profiles (student_id 1 - 42)
INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link) VALUES
(1, 4, 'Shashank Pandey', 'CSE', 9.20, 0, '9876543210', 'shashank@gmail.com', 'https://resume.com/shashank'),
(2, 5, 'Shivam Shukla', 'CSE', 8.80, 0, '9876543211', 'shivam@gmail.com', 'https://resume.com/shivam'),
(3, 6, 'Ayush Mishra', 'IT', 9.00, 0, '9876543212', 'ayush@gmail.com', 'https://resume.com/ayush'),
(4, 7, 'Aditya Singh', 'CSE', 9.50, 0, '8765432101', 'aditya@gmail.com', 'https://resume.com/aditya'),
(5, 8, 'Rahul Sharma', 'IT', 8.20, 1, '8765432102', 'rahul@gmail.com', 'https://resume.com/rahul'),
(6, 9, 'Priya Patel', 'ECE', 7.80, 2, '8765432103', 'priya@gmail.com', 'https://resume.com/priya'),
(7, 10, 'Sneha Gupta', 'CSE', 9.80, 0, '8765432104', 'sneha@gmail.com', 'https://resume.com/sneha'),
(8, 11, 'Amit Verma', 'ME', 7.50, 0, '8765432105', 'amit@gmail.com', 'https://resume.com/amit'),
(9, 12, 'Neha Sen', 'EE', 8.50, 1, '8765432106', 'neha@gmail.com', 'https://resume.com/neha'),
(10, 13, 'Vikram Rathore', 'IT', 8.90, 0, '8765432107', 'vikram@gmail.com', 'https://resume.com/vikram'),
(11, 14, 'Pooja Joshi', 'CSE', 9.10, 0, '8765432108', 'pooja@gmail.com', 'https://resume.com/pooja'),
(12, 15, 'Rohit Kumar', 'CE', 6.80, 0, '8765432109', 'rohit@gmail.com', 'https://resume.com/rohit'),
(13, 16, 'Divya Mehta', 'ECE', 8.40, 0, '8765432110', 'divya@gmail.com', 'https://resume.com/divya'),
(14, 17, 'Anjali Desai', 'CSE', 9.40, 0, '9822334455', 'anjali@gmail.com', 'https://resume.com/anjali'),
(15, 18, 'Manish Tiwari', 'IT', 8.70, 0, '9833445566', 'manish@gmail.com', 'https://resume.com/manish'),
(16, 19, 'Kavita Nair', 'ECE', 8.60, 0, '9844556677', 'kavita@gmail.com', 'https://resume.com/kavita'),
(17, 20, 'Rajesh Iyer', 'ME', 7.90, 1, '9855667788', 'rajesh@gmail.com', 'https://resume.com/rajesh'),
(18, 21, 'Sunita Rao', 'EE', 8.30, 0, '9866778899', 'sunita@gmail.com', 'https://resume.com/sunita'),
(19, 22, 'Arjun Reddy', 'CSE', 9.00, 0, '9877889900', 'arjun@gmail.com', 'https://resume.com/arjun'),
(20, 23, 'Deepa Choudhury', 'CE', 7.20, 2, '9888990011', 'deepa@gmail.com', 'https://resume.com/deepa'),
(21, 24, 'Manoj Bajpayee', 'IT', 8.10, 0, '9899001122', 'manoj@gmail.com', 'https://resume.com/manoj'),
(22, 25, 'Sandeep Kumar', 'ECE', 8.25, 0, '9900112233', 'sandeep@gmail.com', 'https://resume.com/sandeep'),
(23, 26, 'Vivek Agnihotri', 'ME', 7.80, 0, '9911223344', 'vivek@gmail.com', 'https://resume.com/vivek'),
(24, 27, 'Harish Kalyan', 'EE', 8.40, 1, '9922334455', 'harish@gmail.com', 'https://resume.com/harish'),
(25, 28, 'Tanvi Shah', 'CSE', 9.35, 0, '9933445566', 'tanvi@gmail.com', 'https://resume.com/tanvi'),
(26, 29, 'Ritu Phogat', 'CE', 7.60, 0, '9944556677', 'ritu@gmail.com', 'https://resume.com/ritu'),
(27, 30, 'Alok Nath', 'IT', 8.00, 3, '9955667788', 'alok@gmail.com', 'https://resume.com/alok'),
(28, 31, 'Rohan Gavaskar', 'ECE', 7.95, 0, '9966778899', 'rohan@gmail.com', 'https://resume.com/rohan'),
(29, 32, 'Shruti Haasan', 'CSE', 9.15, 0, '9977889900', 'shruti@gmail.com', 'https://resume.com/shruti'),
(30, 33, 'Nikhil D\'Souza', 'ME', 8.20, 0, '9988990011', 'nikhil@gmail.com', 'https://resume.com/nikhil'),
(31, 34, 'Meera Bai', 'EE', 8.75, 0, '9999001122', 'meera@gmail.com', 'https://resume.com/meera'),
(32, 35, 'Sanjay Dutt', 'CE', 6.90, 1, '9000112233', 'sanjay@gmail.com', 'https://resume.com/sanjay'),
(33, 36, 'Anil Kapoor', 'IT', 8.65, 0, '9011223344', 'anil@gmail.com', 'https://resume.com/anil'),
(34, 37, 'Madhuri Dixit', 'CSE', 9.70, 0, '9022334455', 'madhuri@gmail.com', 'https://resume.com/madhuri'),
(35, 38, 'Juhi Chawla', 'ECE', 8.35, 0, '9033445566', 'juhi@gmail.com', 'https://resume.com/juhi'),
(36, 39, 'Preity Zinta', 'IT', 8.90, 0, '9044556677', 'preity@gmail.com', 'https://resume.com/preity'),
(37, 40, 'Raveena Tandon', 'ME', 7.40, 0, '9055667788', 'raveena@gmail.com', 'https://resume.com/raveena'),
(38, 41, 'Karisma Kapoor', 'EE', 8.15, 0, '9066778899', 'karisma@gmail.com', 'https://resume.com/karisma'),
(39, 42, 'Kajol Devgan', 'CSE', 8.85, 0, '9077889900', 'kajol@gmail.com', 'https://resume.com/kajol'),
(40, 43, 'Tabu Hashmi', 'CE', 7.50, 1, '9088990011', 'tabu@gmail.com', 'https://resume.com/tabu'),
(41, 44, 'Aishwarya Rai', 'IT', 9.60, 0, '9099001122', 'aishwarya@gmail.com', 'https://resume.com/aishwarya'),
(42, 45, 'Sushmita Sen', 'ECE', 9.10, 0, '9100112233', 'sushmita@gmail.com', 'https://resume.com/sushmita');

-- Seed Recruiters/Companies (user_id 46 - 60)
INSERT INTO users (user_id, username, password, role) VALUES 
(46, 'tcs', 'pass123', 'recruiter'),
(47, 'google', 'pass123', 'recruiter'),
(48, 'microsoft', 'pass123', 'recruiter'),
(49, 'cognizant', 'pass123', 'recruiter'),
(50, 'amazon', 'pass123', 'recruiter'),
(51, 'infosys', 'pass123', 'recruiter'),
(52, 'wipro', 'pass123', 'recruiter'),
(53, 'accenture', 'pass123', 'recruiter'),
(54, 'hcltech', 'pass123', 'recruiter'),
(55, 'techmahindra', 'pass123', 'recruiter'),
(56, 'capgemini', 'pass123', 'recruiter'),
(57, 'oracle', 'pass123', 'recruiter'),
(58, 'cisco', 'pass123', 'recruiter'),
(59, 'zoho', 'pass123', 'recruiter'),
(60, 'ltimindtree', 'pass123', 'recruiter');

-- Seed Company Profiles (company_id 1 - 15)
INSERT INTO companies (company_id, user_id, name, description, website) VALUES
(1, 46, 'TCS', 'Tata Consultancy Services - IT consulting and business services.', 'https://www.tcs.com'),
(2, 47, 'Google', 'Google LLC - Search, AI, cloud computing, and consumer tech.', 'https://www.google.com'),
(3, 48, 'Microsoft', 'Microsoft Corporation - software, personal computers, and cloud.', 'https://www.microsoft.com'),
(4, 49, 'Cognizant', 'Cognizant - digital transition systems, custom IT consulting.', 'https://www.cognizant.com'),
(5, 50, 'Amazon', 'Amazon - global e-commerce, cloud web platforms (AWS) and devices.', 'https://www.amazon.com'),
(6, 51, 'Infosys', 'Infosys Limited - outsourcing services, consulting, next-gen IT solutions.', 'https://www.infosys.com'),
(7, 52, 'Wipro', 'Wipro Limited - leading global information technology, consulting and business process services company.', 'https://www.wipro.com'),
(8, 53, 'Accenture', 'Accenture plc - global professional services company with leading capabilities in digital, cloud and security.', 'https://www.accenture.com'),
(9, 54, 'HCLTech', 'HCL Technologies - global technology company that helps enterprises reimagine their businesses for the digital age.', 'https://www.hcltech.com'),
(10, 55, 'Tech Mahindra', 'Tech Mahindra - digital transformation, consulting and business re-engineering services provider.', 'https://www.techmahindra.com'),
(11, 56, 'Capgemini', 'Capgemini - global leader in partnering with companies to transform and manage their business through technology.', 'https://www.capgemini.com'),
(12, 57, 'Oracle', 'Oracle Corporation - computer technology corporation best known for its database software and technology.', 'https://www.oracle.com'),
(13, 58, 'Cisco', 'Cisco Systems - worldwide leader in technology that powers the Internet.', 'https://www.cisco.com'),
(14, 59, 'Zoho', 'Zoho Corporation - Indian multinational technology company that makes web-based business tools.', 'https://www.zoho.com'),
(15, 60, 'LTI Mindtree', 'LTIMindtree - global technology consulting and digital solutions company.', 'https://www.ltimindtree.com');

-- Seed Drives (drive_id 1 - 25)
INSERT INTO drives (drive_id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, status) VALUES
(1, 1, 'System Engineer', 3.60, 6.00, 2, '2026-08-15', 'ongoing'),
(2, 1, 'Digital Developer', 7.00, 7.50, 0, '2026-08-20', 'upcoming'),
(3, 2, 'Software Engineer L3', 32.00, 8.50, 0, '2026-09-01', 'ongoing'),
(4, 3, 'Support Consultant', 12.00, 7.00, 1, '2026-09-10', 'upcoming'),
(5, 5, 'Software Development Engineer', 28.00, 8.00, 0, '2026-08-25', 'ongoing'),
(6, 6, 'Power Programmer', 9.50, 8.00, 0, '2026-07-05', 'completed'),
(7, 3, 'Software Engineer (Dynamics)', 14.50, 8.00, 0, '2026-08-10', 'ongoing'),
(8, 4, 'Programmer Analyst', 4.50, 6.50, 1, '2026-08-01', 'completed'),
(9, 7, 'Project Engineer', 3.50, 6.00, 2, '2026-08-12', 'ongoing'),
(10, 8, 'Associate Software Engineer', 4.50, 6.50, 1, '2026-08-18', 'upcoming'),
(11, 9, 'Graduate Engineer Trainee', 4.25, 6.50, 0, '2026-08-22', 'upcoming'),
(12, 10, 'Software Engineer', 5.50, 7.00, 0, '2026-08-30', 'upcoming'),
(13, 11, 'Analyst', 4.00, 6.00, 2, '2026-09-02', 'upcoming'),
(14, 12, 'Member Technical Staff', 18.00, 8.50, 0, '2026-09-05', 'upcoming'),
(15, 13, 'Technical Consulting Engineer', 22.00, 8.00, 0, '2026-09-12', 'upcoming'),
(16, 14, 'Software Developer', 8.40, 7.50, 1, '2026-08-28', 'upcoming'),
(17, 15, 'Software Engineer Trainee', 6.50, 7.00, 0, '2026-09-15', 'upcoming'),
(18, 2, 'APM (Associate Product Manager)', 25.00, 8.80, 0, '2026-09-18', 'upcoming'),
(19, 5, 'SDE-2', 45.00, 9.00, 0, '2026-09-20', 'upcoming'),
(20, 12, 'Application Developer', 12.00, 7.50, 0, '2026-07-10', 'completed'),
(21, 14, 'QA Engineer', 6.00, 7.00, 1, '2026-07-02', 'completed'),
(22, 4, 'Full Stack Developer', 6.75, 7.50, 0, '2026-07-12', 'completed'),
(23, 6, 'System Engineer Specialist', 5.00, 6.50, 1, '2026-08-08', 'completed'),
(24, 8, 'Security Analyst', 9.00, 8.00, 0, '2026-08-05', 'completed'),
(25, 13, 'Network Engineer', 15.00, 7.50, 0, '2026-08-04', 'completed');

-- Seed Drive Eligible Branches (drives mapped to branches)
INSERT INTO drive_branches (drive_id, branch) VALUES 
(1, 'CSE'), (1, 'IT'), (1, 'ECE'),
(2, 'CSE'), (2, 'IT'),
(3, 'CSE'), (3, 'IT'), (3, 'ECE'),
(4, 'CSE'), (4, 'IT'), (4, 'ECE'), (4, 'EE'),
(5, 'CSE'), (5, 'IT'),
(6, 'CSE'), (6, 'IT'),
(7, 'CSE'), (7, 'IT'), (7, 'ECE'),
(8, 'CSE'), (8, 'IT'), (8, 'ECE'), (8, 'ME'), (8, 'CE'), (8, 'EE'),
(9, 'CSE'), (9, 'IT'), (9, 'ECE'), (9, 'EE'),
(10, 'CSE'), (10, 'IT'), (10, 'ECE'), (10, 'EE'), (10, 'ME'),
(11, 'CSE'), (11, 'IT'), (11, 'ECE'),
(12, 'CSE'), (12, 'IT'), (12, 'ECE'),
(13, 'CSE'), (13, 'IT'), (13, 'ECE'), (13, 'ME'), (13, 'CE'), (13, 'EE'),
(14, 'CSE'), (14, 'IT'),
(15, 'CSE'), (15, 'IT'), (15, 'ECE'), (15, 'EE'),
(16, 'CSE'), (16, 'IT'), (16, 'ECE'),
(17, 'CSE'), (17, 'IT'), (17, 'ECE'),
(18, 'CSE'), (18, 'IT'), (18, 'ECE'),
(19, 'CSE'), (19, 'IT'),
(20, 'CSE'), (20, 'IT'),
(21, 'CSE'), (21, 'IT'), (21, 'ECE'),
(22, 'CSE'), (22, 'IT'), (22, 'ECE'),
(23, 'CSE'), (23, 'IT'), (23, 'ECE'), (23, 'EE'),
(24, 'CSE'), (24, 'IT'), (24, 'ECE'),
(25, 'CSE'), (25, 'IT'), (25, 'ECE'), (25, 'EE');

-- Seed Applications (application_id 1 - 60)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status) VALUES
(1, 1, 1, '2026-07-08', 'selected'),
(2, 1, 3, '2026-07-09', 'shortlisted'),
(3, 1, 5, '2026-07-10', 'applied'),
(4, 2, 1, '2026-07-08', 'interview_scheduled'),
(5, 2, 3, '2026-07-09', 'rejected'),
(6, 3, 1, '2026-07-08', 'applied'),
(7, 3, 5, '2026-07-10', 'shortlisted'),
(8, 4, 3, '2026-07-09', 'selected'),
(9, 7, 3, '2026-07-09', 'shortlisted'),
(10, 10, 1, '2026-07-08', 'rejected'),
(11, 4, 5, '2026-07-10', 'selected'),
(12, 7, 5, '2026-07-10', 'interview_scheduled'),
(13, 11, 3, '2026-07-09', 'selected'),
(14, 14, 3, '2026-07-09', 'selected'),
(15, 15, 5, '2026-07-10', 'applied'),
(16, 16, 5, '2026-07-10', 'shortlisted'),
(17, 19, 3, '2026-07-09', 'selected'),
(18, 25, 3, '2026-07-09', 'shortlisted'),
(19, 29, 3, '2026-07-09', 'selected'),
(20, 34, 3, '2026-07-09', 'selected'),
(21, 41, 3, '2026-07-09', 'selected'),
(22, 42, 3, '2026-07-09', 'interview_scheduled'),
(23, 14, 7, '2026-07-11', 'applied'),
(24, 15, 7, '2026-07-11', 'shortlisted'),
(25, 16, 7, '2026-07-11', 'applied'),
(26, 19, 7, '2026-07-11', 'selected'),
(27, 25, 7, '2026-07-11', 'applied'),
(28, 29, 7, '2026-07-11', 'applied'),
(29, 34, 7, '2026-07-11', 'shortlisted'),
(30, 42, 7, '2026-07-11', 'interview_scheduled'),
(31, 5, 8, '2026-07-01', 'selected'),
(32, 10, 8, '2026-07-01', 'selected'),
(33, 15, 8, '2026-07-01', 'rejected'),
(34, 21, 8, '2026-07-01', 'selected'),
(35, 27, 8, '2026-07-01', 'rejected'),
(36, 33, 8, '2026-07-01', 'selected'),
(37, 41, 8, '2026-07-01', 'selected'),
(38, 6, 6, '2026-06-25', 'selected'),
(39, 10, 6, '2026-06-25', 'rejected'),
(40, 13, 6, '2026-06-25', 'selected'),
(41, 21, 6, '2026-06-25', 'rejected'),
(42, 22, 6, '2026-06-25', 'selected'),
(43, 28, 6, '2026-06-25', 'selected'),
(44, 42, 6, '2026-06-25', 'selected'),
(45, 11, 20, '2026-07-02', 'selected'),
(46, 14, 20, '2026-07-02', 'selected'),
(47, 25, 20, '2026-07-02', 'rejected'),
(48, 29, 20, '2026-07-02', 'selected'),
(49, 34, 20, '2026-07-02', 'selected'),
(50, 41, 20, '2026-07-02', 'selected'),
(51, 1, 9, '2026-07-08', 'applied'),
(52, 2, 9, '2026-07-08', 'shortlisted'),
(53, 3, 9, '2026-07-08', 'applied'),
(54, 4, 9, '2026-07-08', 'applied'),
(55, 6, 9, '2026-07-08', 'interview_scheduled'),
(56, 7, 9, '2026-07-08', 'applied'),
(57, 11, 9, '2026-07-08', 'selected'),
(58, 13, 9, '2026-07-08', 'applied'),
(59, 14, 9, '2026-07-08', 'selected'),
(60, 16, 9, '2026-07-08', 'rejected');

-- Seed Application History Transitions (approx. 100 entries to simulate steps)
INSERT INTO application_history (application_id, status) VALUES
(1, 'applied'), (1, 'shortlisted'), (1, 'interview_scheduled'), (1, 'selected'),
(2, 'applied'), (2, 'shortlisted'),
(3, 'applied'),
(4, 'applied'), (4, 'shortlisted'), (4, 'interview_scheduled'),
(5, 'applied'), (5, 'rejected'),
(6, 'applied'),
(7, 'applied'), (7, 'shortlisted'),
(8, 'applied'), (8, 'shortlisted'), (8, 'interview_scheduled'), (8, 'selected'),
(9, 'applied'), (9, 'shortlisted'),
(10, 'applied'), (10, 'rejected'),
(11, 'applied'), (11, 'shortlisted'), (11, 'selected'),
(12, 'applied'), (12, 'shortlisted'), (12, 'interview_scheduled'),
(13, 'applied'), (13, 'shortlisted'), (13, 'selected'),
(14, 'applied'), (14, 'shortlisted'), (14, 'selected'),
(15, 'applied'),
(16, 'applied'), (16, 'shortlisted'),
(17, 'applied'), (17, 'shortlisted'), (17, 'selected'),
(18, 'applied'), (18, 'shortlisted'),
(19, 'applied'), (19, 'shortlisted'), (19, 'selected'),
(20, 'applied'), (20, 'shortlisted'), (20, 'selected'),
(21, 'applied'), (21, 'shortlisted'), (21, 'selected'),
(22, 'applied'), (22, 'shortlisted'), (22, 'interview_scheduled'),
(23, 'applied'),
(24, 'applied'), (24, 'shortlisted'),
(25, 'applied'),
(26, 'applied'), (26, 'shortlisted'), (26, 'selected'),
(27, 'applied'),
(28, 'applied'),
(29, 'applied'), (29, 'shortlisted'),
(30, 'applied'), (30, 'shortlisted'), (30, 'interview_scheduled'),
(31, 'applied'), (31, 'selected'),
(32, 'applied'), (32, 'selected'),
(33, 'applied'), (33, 'rejected'),
(34, 'applied'), (34, 'selected'),
(35, 'applied'), (35, 'rejected'),
(36, 'applied'), (36, 'selected'),
(37, 'applied'), (37, 'selected'),
(38, 'applied'), (38, 'selected'),
(39, 'applied'), (39, 'rejected'),
(40, 'applied'), (40, 'selected'),
(41, 'applied'), (41, 'rejected'),
(42, 'applied'), (42, 'selected'),
(43, 'applied'), (43, 'selected'),
(44, 'applied'), (44, 'selected'),
(45, 'applied'), (45, 'selected'),
(46, 'applied'), (46, 'selected'),
(47, 'applied'), (47, 'rejected'),
(48, 'applied'), (48, 'selected'),
(49, 'applied'), (49, 'selected'),
(50, 'applied'), (50, 'selected'),
(51, 'applied'),
(52, 'applied'), (52, 'shortlisted'),
(53, 'applied'),
(54, 'applied'),
(55, 'applied'), (55, 'shortlisted'), (55, 'interview_scheduled'),
(56, 'applied'),
(57, 'applied'), (57, 'selected'),
(58, 'applied'),
(59, 'applied'), (59, 'selected'),
(60, 'applied'), (60, 'rejected');

-- Seed Application Queue Entries
INSERT INTO application_queue (student_id, drive_id) VALUES
(2, 2),
(3, 3),
(7, 5),
(8, 1),
(14, 10),
(15, 10),
(16, 10),
(19, 11),
(25, 11),
(29, 12),
(34, 12),
(41, 14),
(42, 14),
(1, 15),
(2, 15);
