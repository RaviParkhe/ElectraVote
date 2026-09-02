package com.electrovotesuperx.model.OfflineModel;

public class ElectionVoter {
    private final String electionId;
    private final String voterId;
    private final String status;

    public ElectionVoter(String electionId, String voterId, String status) {
        this.electionId = electionId;
        this.voterId = voterId;
        this.status = status;
    }

    public String getElectionId() { return electionId; }
    public String getVoterId() { return voterId; }
    public String getStatus() { return status; }
    public boolean hasVoted() { return "VOTED".equals(status); }
}
