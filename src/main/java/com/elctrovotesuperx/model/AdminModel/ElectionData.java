package com.elctrovotesuperx.model.AdminModel;

import java.util.List;

/**
 * ElectionData represents a single election stored in Firestore.
 * Firestore path: Elections/{id}
 */
public class ElectionData {

    private String id;            // Firestore document ID (auto-generated UUID)
    private String title;
    private String description;
    private String startDateTime; // "dd MMM yyyy HH:mm"
    private String endDateTime;
    private String status;        // Draft | Upcoming | Active | Closed
    private List<String> positions;
    private String joinCode;      // Which org owns this election
    private long createdAt;       // epoch millis

    public ElectionData() {}

    public ElectionData(String id, String title, String description,
                        String startDateTime, String endDateTime,
                        String status, List<String> positions, String joinCode) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.positions = positions;
        this.joinCode = joinCode;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDateTime() { return startDateTime; }
    public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }

    public String getEndDateTime() { return endDateTime; }
    public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getPositions() { return positions; }
    public void setPositions(List<String> positions) { this.positions = positions; }

    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
