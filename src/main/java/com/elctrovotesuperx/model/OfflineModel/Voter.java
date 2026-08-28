package com.electrovotesuperx.model.OfflineModel;

public class Voter {

    // =====================================================
    // FIELDS
    // =====================================================

    private final String voterId;
    private final String fullName;
    private final String phone;
    private final String status;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Voter(
            String voterId,
            String fullName,
            String phone,
            String status) {

        this.voterId = voterId;
        this.fullName = fullName;
        this.phone = phone;
        this.status = status;
    }

    // =====================================================
    // GET VOTER ID
    // =====================================================

    public String getVoterId() {
        return voterId;
    }

    // =====================================================
    // GET FULL NAME
    // =====================================================

    public String getFullName() {
        return fullName;
    }

    // =====================================================
    // GET PHONE
    // =====================================================

    public String getPhone() {
        return phone;
    }

    // =====================================================
    // GET ACCOUNT STATUS
    // =====================================================

    public String getStatus() {
        return status;
    }

    // =====================================================
    // CHECK ACTIVE
    // =====================================================

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    // =====================================================
    // CHECK SUSPENDED
    // =====================================================

    public boolean isSuspended() {
        return "SUSPENDED".equalsIgnoreCase(status);
    }

    // =====================================================
    // TO STRING
    // =====================================================

    @Override
    public String toString() {
        return voterId + " - " + fullName;
    }
}