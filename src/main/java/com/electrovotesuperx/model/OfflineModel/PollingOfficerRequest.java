package com.electrovotesuperx.model.OfflineModel;

/**
 * Model representing a polling officer approval request stored in SQLite.
 */
public class PollingOfficerRequest {

    private int id;
    private String uid;
    private String name;
    private String email;
    private String stationName;
    private String phone;
    private String status;
    private String approvalPin;
    private String createdAt;
    private String reviewedAt;

    private String pinGeneratedAt;

    public PollingOfficerRequest(int id, String uid, String name, String email,
                                 String stationName, String phone, String status,
                                 String approvalPin, String createdAt, String reviewedAt) {
        this(id, uid, name, email, stationName, phone, status, approvalPin, createdAt, reviewedAt, createdAt);
    }

    public PollingOfficerRequest(int id, String uid, String name, String email,
                                 String stationName, String phone, String status,
                                 String approvalPin, String createdAt, String reviewedAt, String pinGeneratedAt) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.stationName = stationName;
        this.phone = phone;
        this.status = status;
        this.approvalPin = approvalPin;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
        this.pinGeneratedAt = pinGeneratedAt != null ? pinGeneratedAt : createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStationName() {
        return stationName;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalPin() {
        return approvalPin;
    }

    public void setApprovalPin(String approvalPin) {
        this.approvalPin = approvalPin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(String reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getPinGeneratedAt() {
        return pinGeneratedAt;
    }

    public void setPinGeneratedAt(String pinGeneratedAt) {
        this.pinGeneratedAt = pinGeneratedAt;
    }

    public boolean isPinExpired() {
        String ts = (pinGeneratedAt != null && !pinGeneratedAt.isBlank()) ? pinGeneratedAt : createdAt;
        if (ts == null || ts.isBlank()) return false;
        try {
            java.time.LocalDateTime gen = java.time.LocalDateTime.parse(ts);
            return java.time.Duration.between(gen, java.time.LocalDateTime.now()).toHours() >= 8;
        } catch (Exception e) {
            return false;
        }
    }

    public long getRemainingHours() {
        String ts = (pinGeneratedAt != null && !pinGeneratedAt.isBlank()) ? pinGeneratedAt : createdAt;
        if (ts == null || ts.isBlank()) return 8;
        try {
            java.time.LocalDateTime gen = java.time.LocalDateTime.parse(ts);
            long hoursPassed = java.time.Duration.between(gen, java.time.LocalDateTime.now()).toHours();
            return Math.max(0, 8 - hoursPassed);
        } catch (Exception e) {
            return 8;
        }
    }
}
