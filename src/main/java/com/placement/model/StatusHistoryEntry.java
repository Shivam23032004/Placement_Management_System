package com.placement.model;

public class StatusHistoryEntry {
    private int historyId;
    private int applicationId;
    private String status;
    private String changedAt;

    public StatusHistoryEntry(int historyId, int applicationId, String status, String changedAt) {
        this.historyId = historyId;
        this.applicationId = applicationId;
        this.status = status;
        this.changedAt = changedAt;
    }

    public int getHistoryId() { return historyId; }
    public int getApplicationId() { return applicationId; }
    public String getStatus() { return status; }
    public String getChangedAt() { return changedAt; }
}