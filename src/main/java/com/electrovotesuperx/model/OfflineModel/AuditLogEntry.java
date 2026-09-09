package com.electrovotesuperx.model.OfflineModel;

/**
 * Model representing an audit log entry in the local SQLite database.
 */
public class AuditLogEntry {

    private final int id;
    private final String action;
    private final String voterId;
    private final String electionId;
    private final String token;
    private final String details;
    private final String createdAt;

    public AuditLogEntry(int id, String action, String voterId, String electionId,
                         String token, String details, String createdAt) {
        this.id = id;
        this.action = action;
        this.voterId = voterId;
        this.electionId = electionId;
        this.token = token;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getVoterId() {
        return voterId != null ? voterId : "-";
    }

    public String getElectionId() {
        return electionId != null ? electionId : "-";
    }

    public String getToken() {
        return token != null ? token : "-";
    }

    public String getDetails() {
        return details != null ? details : "";
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
