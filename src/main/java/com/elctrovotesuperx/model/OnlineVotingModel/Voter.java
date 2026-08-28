package com.elctrovotesuperx.model.OnlineVotingModel;

public class Voter {

    private String uid;
    private String fullName;
    private String email;
    private String password;
    private String joinCode;
    private String role;
    private String organizationName;
    private String status;
    private String phone;

    public Voter() {
    }

    public Voter(String fullName, String email, String password, String joinCode) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.joinCode = joinCode;
        this.role = "VOTER";
        this.status = "APPROVED";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

