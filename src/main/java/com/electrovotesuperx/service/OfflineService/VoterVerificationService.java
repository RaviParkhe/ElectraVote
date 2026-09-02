package com.electrovotesuperx.service.OfflineService;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.exception.DatabaseException;
import com.electrovotesuperx.dao.OfflineDAO.AuditLogDAO;
import com.electrovotesuperx.dao.OfflineDAO.ElectionDAO;
import com.electrovotesuperx.dao.OfflineDAO.ElectionVoterDAO;
import com.electrovotesuperx.dao.OfflineDAO.OfflineDAO;
import com.electrovotesuperx.dao.OfflineDAO.TokenDAO;
import com.electrovotesuperx.dao.OfflineDAO.VoterDAO;

import com.electrovotesuperx.model.OfflineModel.Election;
import com.electrovotesuperx.model.OfflineModel.ElectionVoter;
import com.electrovotesuperx.model.OfflineModel.Token;
import com.electrovotesuperx.model.OfflineModel.Voter;

import com.electrovotesuperx.utils.TokenGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoterVerificationService {

    // =====================================================
    // DAOs
    // =====================================================

    private final VoterDAO voterDAO;
    private final ElectionDAO electionDAO;
    private final ElectionVoterDAO electionVoterDAO;
    private final TokenDAO tokenDAO;
    private final AuditLogDAO auditLogDAO;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public VoterVerificationService() {

        OfflineDAO dao = new OfflineDAO();

        voterDAO = dao.voters();
        electionDAO = dao.elections();
        electionVoterDAO = dao.electionVoters();
        tokenDAO = dao.tokens();
        auditLogDAO = dao.audit();
    }

    // =====================================================
    // VALIDATE VOTER ELIGIBILITY (PRE-VERIFICATION)
    // =====================================================

    public EligibilityResult validateEligibility(
            String voterId,
            String electionId) throws DatabaseException {

        try {
            if (voterId == null || voterId.isBlank()) {
                return EligibilityResult.fail("Enter a Voter ID.");
            }

            if (electionId == null || electionId.isBlank()) {
                return EligibilityResult.fail("Select an election.");
            }

            String cleanVoterId = voterId.trim();
            String cleanElectionId = electionId.trim();

            // Find Election
            Election election = electionDAO.findById(cleanElectionId);
            if (election == null) {
                return EligibilityResult.fail("Election was not found.");
            }

            if (!"OPEN".equalsIgnoreCase(election.getStatus())) {
                return EligibilityResult.fail("This election is not open.");
            }

            // Find Voter
            Voter voter = voterDAO.findById(cleanVoterId);
            if (voter == null) {
                auditLogDAO.log(
                        "VERIFICATION_FAILED",
                        cleanVoterId,
                        cleanElectionId,
                        null,
                        "Voter ID was not found in voters table");
                return EligibilityResult.fail("Voter ID not found.");
            }

            if (!voter.isActive()) {
                auditLogDAO.log(
                        "VERIFICATION_FAILED",
                        voter.getVoterId(),
                        cleanElectionId,
                        null,
                        "Voter account is suspended");
                return EligibilityResult.fail("Voter account is suspended.");
            }

            // Find Election Registration
            ElectionVoter registration = electionVoterDAO.find(
                    cleanElectionId,
                    voter.getVoterId());

            if (registration == null) {
                auditLogDAO.log(
                        "VERIFICATION_FAILED",
                        voter.getVoterId(),
                        cleanElectionId,
                        null,
                        "Voter is not registered for this election");
                return EligibilityResult.fail("Voter is not registered for this election.");
            }

            if ("VOTED".equalsIgnoreCase(registration.getStatus())) {
                auditLogDAO.log(
                        "VERIFICATION_BLOCKED",
                        voter.getVoterId(),
                        cleanElectionId,
                        null,
                        "Voter has already voted in this election");
                return EligibilityResult.alreadyVoted(voter, "This voter has already voted in this election.");
            }

            return EligibilityResult.eligible(voter, election);

        } catch (SQLException e) {
            throw new DatabaseException("Database error during eligibility check.", e);
        }
    }

    // =====================================================
    // ISSUE TOKEN AFTER SUCCESSFUL VERIFICATION
    // =====================================================

    public VerificationResult issueTokenAfterVerification(
            Voter voter,
            String electionId,
            String authMethod) throws DatabaseException {

        try {
            if (voter == null || electionId == null || electionId.isBlank()) {
                return VerificationResult.fail("Invalid voter or election.");
            }

            String cleanElectionId = electionId.trim();

            // Find existing active token
            Token existing = findActiveTokenForVoter(voter.getVoterId(), cleanElectionId);
            if (existing != null) {
                auditLogDAO.log(
                        "VERIFICATION_EXISTING_TOKEN",
                        voter.getVoterId(),
                        cleanElectionId,
                        existing.getToken(),
                        "Existing active token returned after " + authMethod + " verification");

                return VerificationResult.success(
                        voter,
                        existing.getToken(),
                        "Voter verified via " + authMethod + ". Existing active token is shown.");
            }

            // Generate and save new token
            String token = TokenGenerator.generateToken();
            tokenDAO.create(token, cleanElectionId, voter.getVoterId());

            auditLogDAO.log(
                    "VOTER_VERIFIED",
                    voter.getVoterId(),
                    cleanElectionId,
                    token,
                    "Voter verified via " + authMethod + " and authorization token generated");

            return VerificationResult.success(
                    voter,
                    token,
                    "Voter verified successfully via " + authMethod + ".");

        } catch (SQLException e) {
            throw new DatabaseException("Database error during token generation.", e);
        }
    }

    // =====================================================
    // VERIFY VOTER
    //
    // RULE:
    //
    // Same Voter ID can participate in multiple elections.
    //
    // Each election gets its OWN token.
    //
    // Example:
    //
    // Voter ID = VOT-001
    //
    // Election A -> TOKEN-AAA
    // Election B -> TOKEN-BBB
    // Election C -> TOKEN-CCC
    //
    // =====================================================

    public VerificationResult verify(
            String voterId,
            String electionId)
            throws DatabaseException {

        try {

            // =================================================
            // VALIDATE VOTER ID
            // =================================================

            if (voterId == null || voterId.isBlank()) {

                return VerificationResult.fail(
                        "Enter a Voter ID.");
            }

            // =================================================
            // VALIDATE ELECTION
            // =================================================

            if (electionId == null || electionId.isBlank()) {

                return VerificationResult.fail(
                        "Select an election.");
            }

            // =================================================
            // CLEAN INPUT
            // =================================================

            String cleanVoterId = voterId.trim();
            String cleanElectionId = electionId.trim();

            // =================================================
            // FIND ELECTION
            // =================================================

            Election election = electionDAO.findById(cleanElectionId);

            if (election == null) {

                return VerificationResult.fail(
                        "Election was not found.");
            }

            // =================================================
            // ELECTION MUST BE OPEN
            // =================================================

            if (!"OPEN".equalsIgnoreCase(
                    election.getStatus())) {

                return VerificationResult.fail(
                        "This election is not open.");
            }

            // =================================================
            // FIND VOTER
            //
            // IMPORTANT:
            //
            // This retrieves the SAME Voter ID that was
            // generated on the Member page.
            //
            // We NEVER generate a new Voter ID here.
            // =================================================

            Voter voter = voterDAO.findById(cleanVoterId);

            // =================================================
            // VOTER NOT FOUND
            // =================================================

            if (voter == null) {

                auditLogDAO.log(
                        "VERIFICATION_FAILED",
                        cleanVoterId,
                        cleanElectionId,
                        null,
                        "Voter ID was not found in voters table");

                return VerificationResult.fail(
                        "Voter ID not found.");
            }

            // =================================================
            // CHECK ACCOUNT STATUS
            //
            // voters.status:
            //
            // ACTIVE
            // SUSPENDED
            // =================================================

            if (!voter.isActive()) {

                auditLogDAO.log(
                        "VERIFICATION_FAILED",
                        voter.getVoterId(),
                        cleanElectionId,
                        null,
                        "Voter account is suspended");

                return VerificationResult.fail(
                        "Voter account is suspended.");
            }

            // =================================================
            // FIND ELECTION REGISTRATION
            //
            // IMPORTANT:
            //
            // We check:
            //
            // election_id + voter_id
            //
            // NOT just voter_id.
            //
            // This allows the same voter to participate
            // in different elections.
            // =================================================

            ElectionVoter registration = electionVoterDAO.find(
                    cleanElectionId,
                    voter.getVoterId());

            // =================================================
            // VOTER NOT REGISTERED FOR THIS ELECTION
            // =================================================

            if (registration == null) {

                auditLogDAO.log(
                        "VERIFICATION_FAILED",
                        voter.getVoterId(),
                        cleanElectionId,
                        null,
                        "Voter is not registered for this election");

                return VerificationResult.fail(
                        "Voter is not registered for this election.");
            }

            // =================================================
            // CHECK ELECTION-SPECIFIC VOTING STATUS
            //
            // IMPORTANT:
            //
            // We DO NOT check voter.hasVoted().
            //
            // Voting status belongs to:
            //
            // election_id + voter_id
            //
            // =================================================

            if ("VOTED".equalsIgnoreCase(
                    registration.getStatus())) {

                auditLogDAO.log(
                        "VERIFICATION_BLOCKED",
                        voter.getVoterId(),
                        cleanElectionId,
                        null,
                        "Voter has already voted in this election");

                return VerificationResult.alreadyVoted(
                        voter);
            }

            // =================================================
            // FIND EXISTING ACTIVE TOKEN
            //
            // IMPORTANT:
            //
            // Search by BOTH:
            //
            // voter_id
            // election_id
            //
            // This prevents duplicate tokens for the same
            // voter in the same election.
            //
            // But another election can still get a new token.
            // =================================================

            Token existing = findActiveTokenForVoter(
                    voter.getVoterId(),
                    cleanElectionId);

            if (existing != null) {

                auditLogDAO.log(
                        "VERIFICATION_EXISTING_TOKEN",
                        voter.getVoterId(),
                        cleanElectionId,
                        existing.getToken(),
                        "Existing active token returned for this voter and election");

                return VerificationResult.success(
                        voter,
                        existing.getToken(),
                        "Voter already verified for this election. Existing active token is shown.");
            }

            // =================================================
            // GENERATE NEW TOKEN
            //
            // This happens when:
            //
            // voter + election
            //
            // does NOT already have an active token.
            //
            // =================================================

            String token = TokenGenerator.generateToken();

            // =================================================
            // SAVE TOKEN
            //
            // Token is linked to:
            //
            // election_id
            // voter_id
            //
            // =================================================

            tokenDAO.create(
                    token,
                    cleanElectionId,
                    voter.getVoterId());

            // =================================================
            // AUDIT
            // =================================================

            auditLogDAO.log(
                    "VOTER_VERIFIED",
                    voter.getVoterId(),
                    cleanElectionId,
                    token,
                    "Voter verified and new election-specific authorization token generated");

            // =================================================
            // SUCCESS
            // =================================================

            return VerificationResult.success(
                    voter,
                    token,
                    "Voter verified successfully.");

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Database error during voter verification.", e);
        }
    }

    // =====================================================
    // FIND ACTIVE TOKEN FOR:
    //
    // voter_id + election_id
    //
    // =====================================================

    private Token findActiveTokenForVoter(
            String voterId,
            String electionId)
            throws SQLException {

        String sql = """
                SELECT
                    token,
                    election_id,
                    voter_id,
                    status,
                    created_at,
                    used_at
                FROM tokens
                WHERE voter_id = ?
                  AND election_id = ?
                  AND status = 'ACTIVE'
                ORDER BY created_at DESC
                LIMIT 1
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();

                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    voterId);

            statement.setString(
                    2,
                    electionId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Token(
                            result.getString("token"),
                            result.getString("election_id"),
                            result.getString("voter_id"),
                            result.getString("status"),
                            result.getString("created_at"),
                            result.getString("used_at"));
                }
            }
        }

        return null;
    }

    // =====================================================
    // COMPLETE VOTE
    //
    // Only the selected election is marked as VOTED.
    //
    // =====================================================

    public CompletionResult completeVote(
            String token)
            throws DatabaseException {

        try {

            // =================================================
            // VALIDATE TOKEN
            // =================================================

            if (token == null || token.isBlank()) {

                return CompletionResult.fail(
                        "Enter a token.");
            }

            String cleanToken = token.trim();

            // =================================================
            // FIND TOKEN
            // =================================================

            Token tokenRecord = tokenDAO.find(cleanToken);

            if (tokenRecord == null) {

                return CompletionResult.fail(
                        "Token not found.");
            }

            // =================================================
            // TOKEN MUST BE ACTIVE
            // =================================================

            if (!tokenRecord.isActive()) {

                return CompletionResult.fail(
                        "Token is already used.");
            }

            // =================================================
            // FIND ELECTION REGISTRATION
            //
            // Using:
            //
            // election_id + voter_id
            // =================================================

            ElectionVoter registration = electionVoterDAO.find(
                    tokenRecord.getElectionId(),
                    tokenRecord.getVoterId());

            if (registration == null) {

                return CompletionResult.fail(
                        "Voter is not registered for this election.");
            }

            // =================================================
            // CHECK ELECTION-SPECIFIC VOTE STATUS
            // =================================================

            if ("VOTED".equalsIgnoreCase(
                    registration.getStatus())) {

                return CompletionResult.fail(
                        "Voter has already voted in this election.");
            }

            // =================================================
            // MARK THIS TOKEN AS USED
            // =================================================

            tokenDAO.markUsed(cleanToken);

            // =================================================
            // MARK ONLY THIS ELECTION AS VOTED
            //
            // IMPORTANT:
            //
            // We DO NOT mark the voter globally.
            //
            // Example:
            //
            // Election A -> VOTED
            // Election B -> NOT_VOTED
            //
            // =================================================

            electionVoterDAO.markVoted(
                    tokenRecord.getElectionId(),
                    tokenRecord.getVoterId());

            // =================================================
            // DO NOT CALL:
            //
            // voterDAO.markVoted(...)
            //
            // because voters.has_voted does not exist.
            // =================================================

            // =================================================
            // AUDIT LOG
            // =================================================

            auditLogDAO.log(
                    "VOTE_COMPLETED",
                    tokenRecord.getVoterId(),
                    tokenRecord.getElectionId(),
                    cleanToken,
                    "Polling process confirmed voting completed for this election");

            // =================================================
            // SUCCESS
            // =================================================

            return CompletionResult.success(
                    "Vote completion recorded. Token is now USED.");

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Database error during vote completion.", e);
        }
    }

    // =====================================================
    // VERIFICATION RESULT
    // =====================================================

    public record VerificationResult(
            boolean success,
            boolean alreadyVoted,
            Voter voter,
            String token,
            String message) {

        // =================================================
        // FAILURE
        // =================================================

        public static VerificationResult fail(
                String message) {

            return new VerificationResult(
                    false,
                    false,
                    null,
                    null,
                    message);
        }

        // =================================================
        // SUCCESS
        // =================================================

        public static VerificationResult success(
                Voter voter,
                String token,
                String message) {

            return new VerificationResult(
                    true,
                    false,
                    voter,
                    token,
                    message);
        }

        // =================================================
        // ALREADY VOTED
        // =================================================

        public static VerificationResult alreadyVoted(
                Voter voter) {

            return new VerificationResult(
                    false,
                    true,
                    voter,
                    null,
                    "This voter has already voted in this election.");
        }
    }

    // =====================================================
    // COMPLETION RESULT
    // =====================================================

    public record CompletionResult(
            boolean success,
            String message) {

        // =================================================
        // SUCCESS
        // =================================================

        public static CompletionResult success(
                String message) {

            return new CompletionResult(
                    true,
                    message);
        }

        // =================================================
        // FAILURE
        // =================================================

        public static CompletionResult fail(
                String message) {

            return new CompletionResult(
                    false,
                    message);
        }
    }

    // =====================================================
    // ELIGIBILITY RESULT (FOR PRE-OTP CHECK)
    // =====================================================

    public record EligibilityResult(
            boolean eligible,
            boolean alreadyVoted,
            Voter voter,
            Election election,
            String message) {

        public static EligibilityResult eligible(Voter voter, Election election) {
            return new EligibilityResult(true, false, voter, election, "Voter is eligible to vote.");
        }

        public static EligibilityResult fail(String message) {
            return new EligibilityResult(false, false, null, null, message);
        }

        public static EligibilityResult alreadyVoted(Voter voter, String message) {
            return new EligibilityResult(false, true, voter, null, message);
        }
    }
}