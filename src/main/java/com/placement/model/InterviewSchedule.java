package com.placement.model;

public class InterviewSchedule {
    private int scheduleId;
    private int applicationId;
    private String interviewDate;
    private String interviewTime;
    private int priorityRank;   // lower number = higher priority (Priority Queue isi pe kaam karega)

    public InterviewSchedule(int scheduleId, int applicationId, String interviewDate,
                             String interviewTime, int priorityRank) {
        this.scheduleId = scheduleId;
        this.applicationId = applicationId;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.priorityRank = priorityRank;
    }

    // Getters
    public int getScheduleId() { return scheduleId; }
    public int getApplicationId() { return applicationId; }
    public String getInterviewDate() { return interviewDate; }
    public String getInterviewTime() { return interviewTime; }
    public int getPriorityRank() { return priorityRank; }

    // Setters
    public void setInterviewDate(String interviewDate) { this.interviewDate = interviewDate; }
    public void setInterviewTime(String interviewTime) { this.interviewTime = interviewTime; }
    public void setPriorityRank(int priorityRank) { this.priorityRank = priorityRank; }

    @Override
    public String toString() {
        return "InterviewSchedule{" +
                "applicationId=" + applicationId +
                ", priorityRank=" + priorityRank +
                '}';
    }
}