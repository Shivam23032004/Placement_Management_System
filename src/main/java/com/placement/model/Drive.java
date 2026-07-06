package com.placement.model;

import java.util.List;

public class Drive {
    private int driveId;
    private int companyId;
    private String jobRole;
    private double packageLpa;
    private double minCgpa;
    private int maxBacklogs;
    private String driveDate;
    private String status;
    private List<String> eligibleBranches;

    public Drive(int driveId, int companyId, String jobRole, double packageLpa,
                 double minCgpa, int maxBacklogs, String driveDate, String status) {
        this.driveId = driveId;
        this.companyId = companyId;
        this.jobRole = jobRole;
        this.packageLpa = packageLpa;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.driveDate = driveDate;
        this.status = status;
    }

    // Getters
    public int getDriveId() { return driveId; }
    public int getCompanyId() { return companyId; }
    public String getJobRole() { return jobRole; }
    public double getPackageLpa() { return packageLpa; }
    public double getMinCgpa() { return minCgpa; }
    public int getMaxBacklogs() { return maxBacklogs; }
    public String getDriveDate() { return driveDate; }
    public String getStatus() { return status; }
    public List<String> getEligibleBranches() { return eligibleBranches; }

    // Setters
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }
    public void setPackageLpa(double packageLpa) { this.packageLpa = packageLpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }
    public void setMaxBacklogs(int maxBacklogs) { this.maxBacklogs = maxBacklogs; }
    public void setDriveDate(String driveDate) { this.driveDate = driveDate; }
    public void setStatus(String status) { this.status = status; }
    public void setEligibleBranches(List<String> eligibleBranches) { this.eligibleBranches = eligibleBranches; }

    @Override
    public String toString() {
        return "Drive{" +
                "driveId=" + driveId +
                ", jobRole='" + jobRole + '\'' +
                ", packageLpa=" + packageLpa +
                ", minCgpa=" + minCgpa +
                '}';
    }
}