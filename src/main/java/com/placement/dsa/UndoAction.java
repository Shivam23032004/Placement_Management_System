package com.placement.dsa;

public class UndoAction {
    private int applicationId;
    private String previousStatus;

    public UndoAction(int applicationId, String previousStatus) {
        this.applicationId = applicationId;
        this.previousStatus = previousStatus;
    }

    public int getApplicationId() { return applicationId; }
    public String getPreviousStatus() { return previousStatus; }
}