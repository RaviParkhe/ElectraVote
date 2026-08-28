package com.elctrovotesuperx.model.AdminModel;

/**
 * Candidate represents a candidate application in Firestore.
 * Firestore path: Candidates/{id}
 */
public class Candidate {

    private String id;          // Firestore document ID
    private String electionId;  // References ElectionData.id
    private String electionTitle;
    private String position;    // Which position they're running for
    private String name;
    private String email;
    private String phone;
    private String bio;
    private String status;      // PENDING | ACCEPTED | REJECTED
    private String joinCode;    // Org context
    private long appliedAt;     // epoch millis

    public Candidate() {}

    public Candidate(String id, String electionId, String position,
                     String name, String bio, String joinCode) {
        this.id = id;
        this.electionId = electionId;
        this.position = position;
        this.name = name;
        this.bio = bio;
        this.status = "PENDING";
        this.joinCode = joinCode;
        this.appliedAt = System.currentTimeMillis();
    }

    public Candidate(String id, String electionId, String electionTitle, String position,
                     String name, String email, String phone, String bio, String joinCode) {
        this.id = id;
        this.electionId = electionId;
        this.electionTitle = electionTitle;
        this.position = position;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bio = bio;
        this.status = "PENDING";
        this.joinCode = joinCode;
        this.appliedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getElectionTitle() { return electionTitle; }
    public void setElectionTitle(String electionTitle) { this.electionTitle = electionTitle; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public long getAppliedAt() { return appliedAt; }
    public void setAppliedAt(long appliedAt) { this.appliedAt = appliedAt; }
}

