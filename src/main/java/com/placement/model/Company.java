package com.placement.model;

public class Company {
    private int companyId;
    private int userId;
    private String name;
    private String description;
    private String website;

    public Company(int companyId, int userId, String name, String description, String website) {
        this.companyId = companyId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.website = website;
    }

    // Getters
    public int getCompanyId() { return companyId; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getWebsite() { return website; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setWebsite(String website) { this.website = website; }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", name='" + name + '\'' +
                '}';
    }
}