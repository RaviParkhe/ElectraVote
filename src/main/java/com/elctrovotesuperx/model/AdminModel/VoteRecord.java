package com.elctrovotesuperx.model.AdminModel;

/**
 * VoteRecord represents a single cast vote stored in Firestore.
 * Firestore path: Votes/{id}
 * Voter UID is stored for audit trail and to prevent double voting.
 */
public class VoteRecord {

    private String id;           // Firestore document ID
    private String electionId;
    private String voterUid;     // Firebase Auth UID of voter
    private String position;     // Which position this vote is for
    private String candidateId;  // Which candidate was chosen
    private String candidateName;
    private String joinCode;
    private long votedAt;

    public VoteRecord() {}

    public VoteRecord(String id, String electionId, String voterUid,
                      String position, String candidateId, String candidateName, String joinCode) {
        this.id = id;
        this.electionId = electionId;
        this.voterUid = voterUid;
        this.position = position;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.joinCode = joinCode;
        this.votedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getVoterUid() { return voterUid; }
    public void setVoterUid(String voterUid) { this.voterUid = voterUid; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public long getVotedAt() { return votedAt; }
    public void setVotedAt(long votedAt) { this.votedAt = votedAt; }
}
