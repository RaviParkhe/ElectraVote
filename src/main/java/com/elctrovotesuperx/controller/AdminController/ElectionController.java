package com.elctrovotesuperx.controller.AdminController;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.elctrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.elctrovotesuperx.dao.AdminDAO.VoteDAO;
import com.elctrovotesuperx.exception.FirestoreException;
import com.elctrovotesuperx.model.AdminModel.Candidate;
import com.elctrovotesuperx.model.AdminModel.ElectionData;
import com.elctrovotesuperx.model.AdminModel.VoteRecord;

import java.util.*;

/**
 * ElectionController handles all business logic for elections,
 * candidates, and vote results. Reads session from SessionManager.
 *
 * All methods are synchronous and should be called from a background thread.
 */
public class ElectionController {

    // =========================================================
    // ELECTION CRUD
    // =========================================================

    /** Create a new election in Firestore. Returns error message or null on success. */
    public static String createElection(String title, String description,
                                        String startDateTime, String endDateTime,
                                        String status, List<String> positions) {
        if (!SessionManager.isLoggedIn())
            return "Not signed in. Please sign in as an organization first.";
        if (title == null || title.isBlank())
            return "Election title cannot be empty.";

        ElectionData election = new ElectionData(
                UUID.randomUUID().toString(),
                title.trim(), description.trim(),
                startDateTime, endDateTime,
                status != null ? status : "Draft",
                positions != null ? positions : new ArrayList<>(),
                SessionManager.joinCode
        );

        try {
            boolean ok = ElectionDAO.saveElection(election, SessionManager.idToken);
            return ok ? null : "Failed to save election to Firestore.";
        } catch (FirestoreException e) {
            return "Unable to save election. Please check your connection and try again.";
        }
    }

    /** Update an existing election by its ID. */
    public static String updateElection(ElectionData election) {
        if (!SessionManager.isLoggedIn()) return "Not signed in.";
        try {
            boolean ok = ElectionDAO.saveElection(election, SessionManager.idToken);
            return ok ? null : "Failed to update election.";
        } catch (FirestoreException e) {
            return "Unable to update election. Please check your connection and try again.";
        }
    }

    /** Update positions for an election. */
    public static String updatePositions(String electionId, List<String> positions) {
        if (!SessionManager.isLoggedIn()) return "Not signed in.";
        try {
            boolean ok = ElectionDAO.updatePositions(electionId, positions, SessionManager.idToken);
            return ok ? null : "Failed to update positions.";
        } catch (FirestoreException e) {
            return "Unable to update positions. Please check your connection and try again.";
        }
    }

    /** Delete an election by ID. */
    public static String deleteElection(String electionId) {
        if (!SessionManager.isLoggedIn()) return "Not signed in.";
        try {
            boolean ok = ElectionDAO.deleteElection(electionId, SessionManager.idToken);
            return ok ? null : "Failed to delete election.";
        } catch (FirestoreException e) {
            return "Unable to delete election. Please check your connection and try again.";
        }
    }

    /** Load all elections for the current org. */
    public static List<ElectionData> loadElections() {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        try {
            return ElectionDAO.getElectionsByOrg(SessionManager.joinCode, SessionManager.idToken);
        } catch (FirestoreException e) {
            return new ArrayList<>();
        }
    }

    // =========================================================
    // CANDIDATE MANAGEMENT
    // =========================================================

    /** Add a candidate to an election. Returns null on success, error string on failure. */
    public static String addCandidate(String electionId, String position, String name, String bio) {
        if (!SessionManager.isLoggedIn()) return "Not signed in.";
        if (name == null || name.isBlank()) return "Candidate name cannot be empty.";
        if (position == null || position.isBlank()) return "Please select a position.";

        Candidate c = new Candidate(
                UUID.randomUUID().toString(),
                electionId, position, name.trim(),
                bio != null ? bio.trim() : "",
                SessionManager.joinCode
        );
        c.setStatus("Approved"); // Admin-added candidates are auto-approved

        try {
            boolean ok = CandidateDAO.saveCandidate(c, SessionManager.idToken);
            return ok ? null : "Failed to save candidate.";
        } catch (FirestoreException e) {
            return "Unable to save candidate. Please check your connection and try again.";
        }
    }

    /** Update candidate status: Approved or Rejected. */
    public static String updateCandidateStatus(String candidateId, String status) {
        if (!SessionManager.isLoggedIn()) return "Not signed in.";
        try {
            boolean ok = CandidateDAO.updateStatus(candidateId, status, SessionManager.idToken);
            return ok ? null : "Failed to update candidate status.";
        } catch (FirestoreException e) {
            return "Unable to update candidate status. Please check your connection and try again.";
        }
    }

    /** Get all candidates for an election. */
    public static List<Candidate> loadCandidates(String electionId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        try {
            return CandidateDAO.getCandidatesByElection(electionId, SessionManager.idToken);
        } catch (FirestoreException e) {
            return new ArrayList<>();
        }
    }

    // =========================================================
    // VOTE RESULTS
    // =========================================================

    /** Get aggregated vote results: position → candidate → count */
    public static Map<String, Map<String, Integer>> getResults(String electionId) {
        if (!SessionManager.isLoggedIn()) return new LinkedHashMap<>();
        try {
            return VoteDAO.getResults(electionId, SessionManager.idToken);
        } catch (FirestoreException e) {
            return new LinkedHashMap<>();
        }
    }
}
