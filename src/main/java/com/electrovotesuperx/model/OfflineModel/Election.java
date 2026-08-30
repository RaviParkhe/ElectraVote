package com.electrovotesuperx.model.OfflineModel;

public class Election {
    private final String electionId;
    private final String name;
    private final String status;

    public Election(String electionId, String name, String status) {
        this.electionId = electionId;
        this.name = name;
        this.status = status;
    }

    public String getElectionId() { return electionId; }
    public String getName() { return name; }
    public String getStatus() { return status; }
}
