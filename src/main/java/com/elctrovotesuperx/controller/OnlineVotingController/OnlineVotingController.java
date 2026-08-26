package com.elctrovotesuperx.controller.OnlineVotingController;

import com.elctrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.elctrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.elctrovotesuperx.dao.AdminDAO.VoteDAO;
import com.elctrovotesuperx.model.AdminModel.Candidate;
import com.elctrovotesuperx.model.AdminModel.ElectionData;
import com.elctrovotesuperx.model.AdminModel.VoteRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * OnlineVotingController handles voter-side election access and vote casting.
 * Uses idToken and voterUid provided by the voter's session (set at VoterDashboard login).
 */
public class OnlineVotingController {

    // =========================================================
    // LOAD ACTIVE ELECTIONS FOR VOTER'S ORG
    // =========================================================

    /**
     * Returns all Active elections for the given joinCode.
     * @param joinCode  the voter's organization join code
     * @param idToken   the voter's Firebase idToken
     */
    public static List<ElectionData> loadActiveElections(String joinCode, String idToken) {
        try {
            List<ElectionData> all = ElectionDAO.getElectionsByOrg(joinCode, idToken);
            List<ElectionData> active = new ArrayList<>();
            for (ElectionData e : all) {
                if ("Active".equalsIgnoreCase(e.getStatus())) {
                    active.add(e);
                }
            }
            return active;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================================================
    // LOAD APPROVED CANDIDATES FOR AN ELECTION + POSITION
    // =========================================================

    /**
     * Returns only Approved candidates for a given election.
     * @param electionId  the election's Firestore document ID
     * @param idToken     voter's Firebase idToken
     */
    public static List<Candidate> loadApprovedCandidates(String electionId, String idToken) {
        try {
            List<Candidate> all = CandidateDAO.getCandidatesByElection(electionId, idToken);
            List<Candidate> approved = new ArrayList<>();
            for (Candidate c : all) {
                if ("Approved".equalsIgnoreCase(c.getStatus())) {
                    approved.add(c);
                }
            }
            return approved;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================================================
    // CAST VOTE
    // Returns null on success, or an error message on failure
    // =========================================================

    /**
     * Cast a single vote for a candidate in a position.
     * Prevents double-voting per (electionId, voterUid, position).
     *
     * @param electionId    Firestore election ID
     * @param voterUid      voter's Firebase UID
     * @param position      the position being voted for
     * @param candidateId   the chosen candidate's Firestore ID
     * @param candidateName display name of the candidate
     * @param joinCode      org join code
     * @param idToken       voter's Firebase idToken
     */
    public static String castVote(String electionId, String voterUid,
                                  String position, String candidateId,
                                  String candidateName, String joinCode,
                                  String idToken) {
        try {
            // Check for duplicate vote
            boolean alreadyVoted = VoteDAO.hasVoted(electionId, voterUid, position, idToken);
            if (alreadyVoted) {
                return "You have already voted for the position: " + position;
            }

            VoteRecord vote = new VoteRecord(
                    UUID.randomUUID().toString(),
                    electionId, voterUid, position,
                    candidateId, candidateName, joinCode
            );

            boolean ok = VoteDAO.castVote(vote, idToken);
            return ok ? null : "Failed to record your vote. Please try again.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error casting vote: " + e.getMessage();
        }
    }
}
