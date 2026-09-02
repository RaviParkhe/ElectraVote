package com.electrovotesuperx.model.OnlineVotingModel;

/**
 * Model representing an organization membership for a voter/user.
 */
public class UserOrganizationMembership {

    private String joinCode;
    private String organizationName;
    private String role;
    private String status;
    private String memberName;
    private String memberEmail;
    private int activeElectionsCount;
    private boolean activeContext;

    public UserOrganizationMembership() {
    }

    public UserOrganizationMembership(
            String joinCode,
            String organizationName,
            String role,
            String status,
            String memberName,
            String memberEmail,
            int activeElectionsCount,
            boolean activeContext) {
        this.joinCode = joinCode;
        this.organizationName = organizationName;
        this.role = role;
        this.status = status;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.activeElectionsCount = activeElectionsCount;
        this.activeContext = activeContext;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRole() {
        return role != null ? role : "VOTER";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status != null ? status : "ACCEPTED";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public int getActiveElectionsCount() {
        return activeElectionsCount;
    }

    public void setActiveElectionsCount(int activeElectionsCount) {
        this.activeElectionsCount = activeElectionsCount;
    }

    public boolean isActiveContext() {
        return activeContext;
    }

    public void setActiveContext(boolean activeContext) {
        this.activeContext = activeContext;
    }
}
