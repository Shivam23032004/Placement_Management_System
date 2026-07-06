package com.placement.model;

public class Placement {
    private int placementId;
    private int studentId;
    private int companyId;
    private int driveId;
    private double packageLpa;
    private String placementDate;

    public Placement(int placementId, int studentId, int companyId, int driveId,
                     double packageLpa, String placementDate) {
        this.placementId = placementId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.driveId = driveId;
        this.packageLpa = packageLpa;
        this.placementDate = placementDate;
    }

    // Getters
    public int getPlacementId() { return placementId; }
    public int getStudentId() { return studentId; }
    public int getCompanyId() { return companyId; }
    public int getDriveId() { return driveId; }
    public double getPackageLpa() { return packageLpa; }
    public String getPlacementDate() { return placementDate; }

    @Override
    public String toString() {
        return "Placement{" +
                "studentId=" + studentId +
                ", companyId=" + companyId +
                ", packageLpa=" + packageLpa +
                '}';
    }
}
