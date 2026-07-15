-- ============================================================
-- Placement Management System — Rich Mock Data Seeding Script
-- ============================================================

USE placement_management_system;

-- Disable constraints to safely clean tables
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE users;
TRUNCATE TABLE students;
TRUNCATE TABLE companies;
TRUNCATE TABLE drives;
TRUNCATE TABLE drive_branches;
TRUNCATE TABLE applications;
TRUNCATE TABLE application_history;
TRUNCATE TABLE application_queue;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. POPULATE USERS TABLE
-- ==========================================

-- Admin User (ID: 1)
INSERT INTO users (user_id, username, password, role) VALUES (1, 'admin1', 'admin123', 'admin');

-- Default Group Students (IDs: 2, 3, 4)
INSERT INTO users (user_id, username, password, role) VALUES (2, 'shashank', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (3, 'shivam', 'pass123', 'student');
INSERT INTO users (user_id, username, password, role) VALUES (4, 'ayush', 'pass123', 'student');

-- New Students (IDs: 5 to 14)
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

-- Recruiters (IDs: 15 to 20)
INSERT INTO users (user_id, username, password, role) VALUES (15, 'tcs_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (16, 'google_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (17, 'microsoft_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (18, 'cognizant_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (19, 'amazon_recruiter', 'pass123', 'recruiter');
INSERT INTO users (user_id, username, password, role) VALUES (20, 'infosys_recruiter', 'pass123', 'recruiter');

-- ==========================================
-- 2. POPULATE STUDENTS TABLE
-- ==========================================

-- Primary default students
INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (1, 2, 'Shashank Pandey', 'CSE', 9.20, 0, '9876543210', 'shashank@gmail.com', 'https://resume.com/shashank');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (2, 3, 'Shivam Shukla', 'CSE', 8.80, 0, '9876543211', 'shivam@gmail.com', 'https://resume.com/shivam');

INSERT INTO students (student_id, user_id, name, branch, cgpa, backlog_count, phone, email, resume_link)
VALUES (3, 4, 'Ayush Mishra', 'IT', 9.00, 0, '9876543212', 'ayush@gmail.com', 'https://resume.com/ayush');

-- Seed additional test students
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

-- ==========================================
-- 3. POPULATE COMPANIES TABLE
-- ==========================================

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

-- ==========================================
-- 4. POPULATE DRIVES TABLE
-- ==========================================

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

-- ==========================================
-- 5. POPULATE DRIVE_BRANCHES TABLE
-- ==========================================

-- Drive 1 (TCS SE): CSE, IT, ECE
INSERT INTO drive_branches (drive_id, branch) VALUES (1, 'CSE');
INSERT INTO drive_branches (drive_id, branch) VALUES (1, 'IT');
INSERT INTO drive_branches (drive_id, branch) VALUES (1, 'ECE');

-- Drive 2 (TCS Digital): CSE, IT
INSERT INTO drive_branches (drive_id, branch) VALUES (2, 'CSE');
INSERT INTO drive_branches (drive_id, branch) VALUES (2, 'IT');

-- Drive 3 (Google SWE): CSE, IT, ECE
INSERT INTO drive_branches (drive_id, branch) VALUES (3, 'CSE');
INSERT INTO drive_branches (drive_id, branch) VALUES (3, 'IT');
INSERT INTO drive_branches (drive_id, branch) VALUES (3, 'ECE');

-- Drive 4 (Microsoft Support): CSE, IT, ECE, EE
INSERT INTO drive_branches (drive_id, branch) VALUES (4, 'CSE');
INSERT INTO drive_branches (drive_id, branch) VALUES (4, 'IT');
INSERT INTO drive_branches (drive_id, branch) VALUES (4, 'ECE');
INSERT INTO drive_branches (drive_id, branch) VALUES (4, 'EE');

-- Drive 5 (Amazon SDE): CSE, IT
INSERT INTO drive_branches (drive_id, branch) VALUES (5, 'CSE');
INSERT INTO drive_branches (drive_id, branch) VALUES (5, 'IT');

-- Drive 6 (Infosys PP): CSE, IT
INSERT INTO drive_branches (drive_id, branch) VALUES (6, 'CSE');
INSERT INTO drive_branches (drive_id, branch) VALUES (6, 'IT');

-- ==========================================
-- 6. POPULATE APPLICATIONS TABLE
-- ==========================================

-- Student 1 (Shashank): applied to TCS System Eng (Drive 1), Google (Drive 3), Amazon (Drive 5)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (1, 1, 1, '2026-07-08', 'selected');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (2, 1, 3, '2026-07-09', 'shortlisted');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (3, 1, 5, '2026-07-10', 'applied');

-- Student 2 (Shivam): applied to TCS System Eng (Drive 1), Google (Drive 3)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (4, 2, 1, '2026-07-08', 'interview_scheduled');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (5, 2, 3, '2026-07-09', 'rejected');

-- Student 3 (Ayush): applied to TCS System Eng (Drive 1), Amazon (Drive 5)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (6, 3, 1, '2026-07-08', 'applied');

INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (7, 3, 5, '2026-07-10', 'shortlisted');

-- Student 4 (Aditya): applied to Google (Drive 3)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (8, 4, 3, '2026-07-09', 'selected');

-- Student 7 (Sneha): applied to Google (Drive 3)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (9, 7, 3, '2026-07-09', 'shortlisted');

-- Student 10 (Vikram): applied to TCS System Eng (Drive 1)
INSERT INTO applications (application_id, student_id, drive_id, application_date, status)
VALUES (10, 10, 1, '2026-07-08', 'rejected');

-- ==========================================
-- 7. POPULATE APPLICATION_HISTORY TABLE
-- ==========================================

-- Shashank App 1 History (TCS selected)
INSERT INTO application_history (application_id, status) VALUES (1, 'applied');
INSERT INTO application_history (application_id, status) VALUES (1, 'shortlisted');
INSERT INTO application_history (application_id, status) VALUES (1, 'interview_scheduled');
INSERT INTO application_history (application_id, status) VALUES (1, 'selected');

-- Shivam App 4 History (TCS interview_scheduled)
INSERT INTO application_history (application_id, status) VALUES (4, 'applied');
INSERT INTO application_history (application_id, status) VALUES (4, 'shortlisted');
INSERT INTO application_history (application_id, status) VALUES (4, 'interview_scheduled');

-- Ayush App 7 History (Amazon shortlisted)
INSERT INTO application_history (application_id, status) VALUES (7, 'applied');
INSERT INTO application_history (application_id, status) VALUES (7, 'shortlisted');

-- ==========================================
-- 8. POPULATE APPLICATION_QUEUE TABLE
-- ==========================================

-- Shivam (Student 2) for TCS Digital (Drive 2)
INSERT INTO application_queue (student_id, drive_id) VALUES (2, 2);

-- Ayush (Student 3) for Google (Drive 3)
INSERT INTO application_queue (student_id, drive_id) VALUES (3, 3);

-- Amit (Student 8) for TCS SE (Drive 1)
INSERT INTO application_queue (student_id, drive_id) VALUES (8, 1);

-- Sneha (Student 7) for Amazon SDE (Drive 5)
INSERT INTO application_queue (student_id, drive_id) VALUES (7, 5);

SELECT 'Rich seed data loaded successfully!' AS status;
