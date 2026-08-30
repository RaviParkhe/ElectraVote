package com.electrovotesuperx.model.OfflineModel;

public class Token {

    private final String token;
    private final String electionId;
    private final String voterId;
    private final String status;
    private final String createdAt;
    private final String usedAt;

    public Token(
            String token,
            String electionId,
            String voterId,
            String status,
            String createdAt,
            String usedAt) {

        this.token = token;
        this.electionId = electionId;
        this.voterId = voterId;
        this.status = status;
        this.createdAt = createdAt;
        this.usedAt = usedAt;
    }

    public String getToken() {
        return token;
    }

    public String getElectionId() {
        return electionId;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUsedAt() {
        return usedAt;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}