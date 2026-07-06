package com.placement.model;

public class Application {
    private int applicationId;
    private int studentId;
    private int driveId;
    private String applicationDate;
    private String status;   // "applied", "shortlisted", "interview_scheduled", "selected", "rejected"

    public Application(int applicationId, int studentId, int driveId, String applicationDate, String status) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.driveId = driveId;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    // Getters
    public int getApplicationId() { return applicationId; }
    public int getStudentId() { return studentId; }
    public int getDriveId() { return driveId; }
    public String getApplicationDate() { return applicationDate; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", studentId=" + studentId +
                ", driveId=" + driveId +
                ", status='" + status + '\'' +
                '}';
    }
}
