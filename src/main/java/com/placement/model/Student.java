package com.placement.model;

public class Student {
    private int studentId;
    private int userId;
    private String name;
    private String branch;
    private double cgpa;
    private int backlogCount;
    private String phone;
    private String email;
    private String resumeLink;

    // Constructor
    public Student(int studentId, int userId, String name, String branch,
                   double cgpa, int backlogCount, String phone, String email, String resumeLink) {
        this.studentId = studentId;
        this.userId = userId;
        this.name = name;
        this.branch = branch;
        this.cgpa = cgpa;
        this.backlogCount = backlogCount;
        this.phone = phone;
        this.email = email;
        this.resumeLink = resumeLink;
    }

    // Getters
    public int getStudentId() { return studentId; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getBranch() { return branch; }
    public double getCgpa() { return cgpa; }
    public int getBacklogCount() { return backlogCount; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getResumeLink() { return resumeLink; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public void setBacklogCount(int backlogCount) { this.backlogCount = backlogCount; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setResumeLink(String resumeLink) { this.resumeLink = resumeLink; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", branch='" + branch + '\'' +
                ", cgpa=" + cgpa +
                '}';
    }
}