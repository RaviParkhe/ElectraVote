package com.elctrovotesuperx.model.OrganizationModel;

public class Organization {
    private String organizationName;
    private String adminName;
    private String adminEmail;
    private String password;
    private String joinCode;
    
    public Organization() {
    }

    public Organization(String organizationName, String adminName, String adminEmail, String password, String joinCode) {
        this.organizationName = organizationName;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.password = password;
        this.joinCode = joinCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

}
