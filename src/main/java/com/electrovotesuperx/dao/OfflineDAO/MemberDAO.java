package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberDAO {

    // =========================================================
    // GET ALL MEMBERS
    // =========================================================

    public List<Member> getAllMembers() throws SQLException {

        List<Member> members = new ArrayList<>();

        String sql = """
                SELECT
                    voter_id,
                    full_name,
                    email,
                    gender,
                    phone,
                    status
                FROM voters
                ORDER BY
                    CASE
                        WHEN UPPER(status) = 'SUSPENDED' THEN 1
                        ELSE 0
                    END,
                    full_name COLLATE NOCASE
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                members.add(createMemberWithoutElection(rs));
            }
        }

        return members;
    }

    // =========================================================
    // GET MEMBERS FOR ONE ELECTION
    // =========================================================

    public List<Member> getMembersForElection(
            String electionId) throws SQLException {

        List<Member> members = new ArrayList<>();

        if (electionId == null ||
                electionId.isBlank()) {
            return members;
        }

        /*
         * IMPORTANT:
         *
         * voters.status
         * = Account Status
         *
         * election_voters.status
         * = Voting Status for this election
         *
         * Therefore:
         *
         * ACTIVE / SUSPENDED
         * comes from voters.
         *
         * VOTED / NOT_VOTED
         * comes from election_voters.
         */

        String sql = """
                SELECT
                    v.voter_id,
                    v.full_name,
                    v.email,
                    v.gender,
                    v.phone,
                    v.status AS account_status,
                    ev.status AS voting_status
                FROM voters v
                INNER JOIN election_voters ev
                    ON v.voter_id = ev.voter_id
                WHERE ev.election_id = ?
                ORDER BY
                    CASE
                        WHEN UPPER(v.status) = 'SUSPENDED' THEN 1
                        ELSE 0
                    END,
                    v.full_name COLLATE NOCASE
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    electionId.trim());

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    members.add(
                            createMemberForElection(rs));
                }
            }
        }

        return members;
    }

    // =========================================================
    // ADD MEMBER
    // =========================================================
    //
    // voter_id belongs to the PERSON.
    //
    // Example:
    //
    // Rahul -> VOT-AB12CD34
    //
    // Election A -> VOT-AB12CD34
    // Election B -> VOT-AB12CD34
    // Election C -> VOT-AB12CD34
    //
    // =========================================================

    public boolean addMember(
            String electionId,
            String name,
            String email,
            String gender,
            String phone) {

        if (electionId == null ||
                electionId.isBlank()) {
            return false;
        }

        if (name == null ||
                name.isBlank()) {
            return false;
        }

        if (gender == null ||
                gender.isBlank()) {
            return false;
        }

        if (phone == null ||
                phone.isBlank()) {
            return false;
        }

        String cleanElectionId = electionId.trim();
        String cleanName = name.trim();

        String cleanEmail = email == null
                ? ""
                : email.trim();

        String cleanGender = gender.trim();
        String cleanPhone = phone.trim();

        try (
                Connection connection = DatabaseConfig.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // =================================================
                // 1. CHECK ELECTION EXISTS AND IS IN DRAFT
                //    (enrollment window — roll not yet frozen)
                // =================================================

                String electionCheck = """
                        SELECT COUNT(*)
                        FROM elections
                        WHERE election_id = ?
                        AND UPPER(status) = 'DRAFT'
                        """;

                try (
                        PreparedStatement ps = connection.prepareStatement(
                                electionCheck)) {

                    ps.setString(
                            1,
                            cleanElectionId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next() ||
                                rs.getInt(1) == 0) {

                            connection.rollback();
                            return false;
                        }
                    }
                }

                // =================================================
                // 2. FIND EXISTING PERSON
                // =================================================

                String voterId = findVoterId(
                        connection,
                        cleanName,
                        cleanEmail);

                // =================================================
                // 3. NEW PERSON
                // =================================================

                if (voterId == null) {

                    // Generate voter ID ONLY ONCE
                    voterId = generateVoterId();

                    String insertVoter = """
                            INSERT INTO voters
                            (
                                voter_id,
                                full_name,
                                email,
                                gender,
                                phone,
                                status
                            )
                            VALUES
                            (
                                ?,
                                ?,
                                ?,
                                ?,
                                ?,
                                'ACTIVE'
                            )
                            """;

                    try (
                            PreparedStatement ps = connection.prepareStatement(
                                    insertVoter)) {

                        ps.setString(
                                1,
                                voterId);

                        ps.setString(
                                2,
                                cleanName);

                        if (cleanEmail.isEmpty()) {

                            ps.setNull(
                                    3,
                                    Types.VARCHAR);

                        } else {

                            ps.setString(
                                    3,
                                    cleanEmail);
                        }

                        ps.setString(
                                4,
                                cleanGender);

                        ps.setString(
                                5,
                                cleanPhone);

                        ps.executeUpdate();
                    }

                } else {

                    // =================================================
                    // 4. EXISTING PERSON
                    // =================================================

                    /*
                     * DO NOT generate another voter ID.
                     */

                    String updateVoter = """
                            UPDATE voters
                            SET
                                full_name = ?,
                                email = ?,
                                gender = ?,
                                phone = ?
                            WHERE voter_id = ?
                            """;

                    try (
                            PreparedStatement ps = connection.prepareStatement(
                                    updateVoter)) {

                        ps.setString(
                                1,
                                cleanName);

                        if (cleanEmail.isEmpty()) {

                            ps.setNull(
                                    2,
                                    Types.VARCHAR);

                        } else {

                            ps.setString(
                                    2,
                                    cleanEmail);
                        }

                        ps.setString(
                                3,
                                cleanGender);

                        ps.setString(
                                4,
                                cleanPhone);

                        ps.setString(
                                5,
                                voterId);

                        ps.executeUpdate();
                    }
                }

                // =================================================
                // 5. CHECK ELECTION MEMBERSHIP
                // =================================================

                String checkMembership = """
                        SELECT COUNT(*)
                        FROM election_voters
                        WHERE election_id = ?
                        AND voter_id = ?
                        """;

                try (
                        PreparedStatement ps = connection.prepareStatement(
                                checkMembership)) {

                    ps.setString(
                            1,
                            cleanElectionId);

                    ps.setString(
                            2,
                            voterId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next() &&
                                rs.getInt(1) > 0) {

                            connection.rollback();
                            return false;
                        }
                    }
                }

                // =================================================
                // 6. LINK PERSON TO ELECTION
                // =================================================

                String insertElectionVoter = """
                        INSERT INTO election_voters
                        (
                            election_id,
                            voter_id,
                            status
                        )
                        VALUES
                        (
                            ?,
                            ?,
                            'NOT_VOTED'
                        )
                        """;

                try (
                        PreparedStatement ps = connection.prepareStatement(
                                insertElectionVoter)) {

                    ps.setString(
                            1,
                            cleanElectionId);

                    ps.setString(
                            2,
                            voterId);

                    ps.executeUpdate();
                }

                // =================================================
                // 7. COMMIT
                // =================================================

                connection.commit();

                return true;

            } catch (SQLException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    rollbackError.printStackTrace();
                }

                System.err.println(
                        "Error adding member: "
                                + e.getMessage());

                return false;
            }

        } catch (SQLException e) {

            System.err.println(
                    "Database error while adding member: "
                            + e.getMessage());

            return false;
        }
    }

    // =========================================================
    // FIND EXISTING VOTER
    // =========================================================

    private String findVoterId(
            Connection connection,
            String name,
            String email) throws SQLException {

        // =====================================================
        // 1. FIND BY EMAIL
        // =====================================================

        if (email != null &&
                !email.isBlank()) {

            String emailSql = """
                    SELECT voter_id
                    FROM voters
                    WHERE LOWER(TRIM(email))
                        = LOWER(TRIM(?))
                    LIMIT 1
                    """;

            try (
                    PreparedStatement ps = connection.prepareStatement(
                            emailSql)) {

                ps.setString(
                        1,
                        email.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        return rs.getString(
                                "voter_id");
                    }
                }
            }
        }

        // =====================================================
        // 2. FIND BY NAME
        // =====================================================

        if (name != null &&
                !name.isBlank()) {

            String nameSql = """
                    SELECT voter_id
                    FROM voters
                    WHERE LOWER(TRIM(full_name))
                        = LOWER(TRIM(?))
                    LIMIT 1
                    """;

            try (
                    PreparedStatement ps = connection.prepareStatement(
                            nameSql)) {

                ps.setString(
                        1,
                        name.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        return rs.getString(
                                "voter_id");
                    }
                }
            }
        }

        return null;
    }

    // =========================================================
    // GENERATE VOTER ID
    // =========================================================

    private String generateVoterId() {

        return "VOT-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    // =========================================================
    // UPDATE MEMBER
    // =========================================================

    public boolean updateMember(Member member) {

        if (member == null ||
                member.getVoterId() == null ||
                member.getVoterId().isBlank()) {

            return false;
        }

        if (member.getName() == null ||
                member.getName().isBlank()) {

            return false;
        }

        String sql = """
                UPDATE voters
                SET
                    full_name = ?,
                    email = ?,
                    gender = ?,
                    phone = ?
                WHERE voter_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();

                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    member.getName().trim());

            if (member.getEmail() == null ||
                    member.getEmail().isBlank()) {

                statement.setNull(
                        2,
                        Types.VARCHAR);

            } else {

                statement.setString(
                        2,
                        member.getEmail().trim());
            }

            statement.setString(
                    3,
                    member.getGender());

            statement.setString(
                    4,
                    member.getPhone());

            statement.setString(
                    5,
                    member.getVoterId().trim());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error updating member: "
                            + e.getMessage());

            return false;
        }
    }

    // =========================================================
    // SUSPEND MEMBER
    // =========================================================

    public boolean suspend(Member member) {

        if (member == null ||
                member.getVoterId() == null ||
                member.getVoterId().isBlank()) {

            return false;
        }

        return updateStatus(
                member.getVoterId(),
                "SUSPENDED");
    }

    // =========================================================
    // ACTIVATE MEMBER
    // =========================================================

    public boolean activate(Member member) {

        if (member == null ||
                member.getVoterId() == null ||
                member.getVoterId().isBlank()) {

            return false;
        }

        return updateStatus(
                member.getVoterId(),
                "ACTIVE");
    }

    // =========================================================
    // UPDATE ACCOUNT STATUS
    // =========================================================

    private boolean updateStatus(
            String voterId,
            String status) {

        if (voterId == null ||
                voterId.isBlank()) {

            return false;
        }

        String sql = """
                UPDATE voters
                SET status = ?
                WHERE voter_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();

                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    status.toUpperCase());

            statement.setString(
                    2,
                    voterId.trim());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error updating member status: "
                            + e.getMessage());

            return false;
        }
    }

    // =========================================================
    // DELETE MEMBER
    // =========================================================

    public boolean deleteMember(Member member) {

        if (member == null ||
                member.getVoterId() == null ||
                member.getVoterId().isBlank()) {

            return false;
        }

        String voterId = member.getVoterId().trim();

        try (
                Connection connection = DatabaseConfig.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // =================================================
                // DELETE ELECTION LINKS
                // =================================================

                String deleteLinks = """
                        DELETE FROM election_voters
                        WHERE voter_id = ?
                        """;

                try (
                        PreparedStatement ps = connection.prepareStatement(
                                deleteLinks)) {

                    ps.setString(
                            1,
                            voterId);

                    ps.executeUpdate();
                }

                // =================================================
                // DELETE PERSON
                // =================================================

                String deleteVoter = """
                        DELETE FROM voters
                        WHERE voter_id = ?
                        """;

                int deleted;

                try (
                        PreparedStatement ps = connection.prepareStatement(
                                deleteVoter)) {

                    ps.setString(
                            1,
                            voterId);

                    deleted = ps.executeUpdate();
                }

                connection.commit();

                return deleted > 0;

            } catch (SQLException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    rollbackError.printStackTrace();
                }

                System.err.println(
                        "Error deleting member: "
                                + e.getMessage());

                return false;
            }

        } catch (SQLException e) {

            System.err.println(
                    "Database error while deleting member: "
                            + e.getMessage());

            return false;
        }
    }

    // =========================================================
    // CREATE MEMBER - WITHOUT ELECTION
    // =========================================================

    private Member createMemberWithoutElection(
            ResultSet rs) throws SQLException {

        String databaseStatus = rs.getString("status");

        String displayStatus = normalizeAccountStatus(databaseStatus);

        return new Member(
                rs.getString("voter_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("gender"),
                rs.getString("phone"),
                displayStatus,
                "Non-Voted");
    }

    // =========================================================
    // CREATE MEMBER - FOR SELECTED ELECTION
    // =========================================================

    private Member createMemberForElection(
            ResultSet rs) throws SQLException {

        String accountStatus = rs.getString("account_status");

        String votingStatus = rs.getString("voting_status");

        String displayAccountStatus = normalizeAccountStatus(accountStatus);

        String displayVotingStatus = normalizeVotingStatus(votingStatus);

        return new Member(
                rs.getString("voter_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("gender"),
                rs.getString("phone"),
                displayAccountStatus,
                displayVotingStatus);
    }

    // =========================================================
    // NORMALIZE ACCOUNT STATUS
    // =========================================================

    private String normalizeAccountStatus(
            String status) {

        if ("SUSPENDED".equalsIgnoreCase(status)) {
            return "Suspended";
        }

        return "Active";
    }

    // =========================================================
    // NORMALIZE VOTING STATUS
    // =========================================================

    private String normalizeVotingStatus(
            String status) {

        if ("VOTED".equalsIgnoreCase(status)) {
            return "Voted";
        }

        return "Non-Voted";
    }
}