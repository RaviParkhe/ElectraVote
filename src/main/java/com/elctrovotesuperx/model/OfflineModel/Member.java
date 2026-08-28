package com.electrovotesuperx.model.OfflineModel;

public class Member {

    // =========================================================
    // FIELDS
    // =========================================================

    // Unique ID of the person.
    // Same voter ID is used across all elections.
    private String voterId;

    private String name;
    private String email;
    private String gender;
    private String phone;

    // Account status of the person.
    // ACTIVE / SUSPENDED
    private String status;

    // Voting status for the SELECTED election.
    // VOTED / NON-VOTED
    private String votingStatus;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Member(
            String voterId,
            String name,
            String email,
            String gender,
            String phone,
            String status,
            String votingStatus) {

        this.voterId = voterId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.status = status;
        this.votingStatus = votingStatus;
    }

    // =========================================================
    // BACKWARD-COMPATIBLE CONSTRUCTOR
    // =========================================================

    public Member(
            String voterId,
            String name,
            String email,
            String gender,
            String phone,
            String status) {

        this(
                voterId,
                name,
                email,
                gender,
                phone,
                status,
                "Non-Voted");
    }

    // =========================================================
    // VOTER ID
    // =========================================================

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    // =========================================================
    // NAME
    // =========================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================================================
    // EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================================================
    // GENDER
    // =========================================================

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // =========================================================
    // PHONE
    // =========================================================

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // =========================================================
    // ACCOUNT STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =========================================================
    // VOTING STATUS
    // =========================================================

    public String getVotingStatus() {
        return votingStatus;
    }

    public void setVotingStatus(String votingStatus) {
        this.votingStatus = votingStatus;
    }

    // =========================================================
    // INITIALS
    // =========================================================

    public String getInitials() {

        if (name == null || name.isBlank()) {
            return "?";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        String first = parts[0].substring(0, 1);
        String last = parts[parts.length - 1]
                .substring(0, 1);

        return (first + last).toUpperCase();
    }

    // =========================================================
    // TO STRING
    // =========================================================

    @Override
    public String toString() {
        return name == null ? "" : name;
    }
}