package com.electrovotesuperx.dao.OfflineDAO;

/**
 * Facade for the Offline Verification data layer.
 * Feature-specific DAOs remain separate so each class has one responsibility.
 */
public class OfflineDAO {
    private final VoterDAO voterDAO = new VoterDAO();
    private final ElectionDAO electionDAO = new ElectionDAO();
    private final ElectionVoterDAO electionVoterDAO = new ElectionVoterDAO();
    private final TokenDAO tokenDAO = new TokenDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public VoterDAO voters() { return voterDAO; }
    public ElectionDAO elections() { return electionDAO; }
    public ElectionVoterDAO electionVoters() { return electionVoterDAO; }
    public TokenDAO tokens() { return tokenDAO; }
    public AuditLogDAO audit() { return auditLogDAO; }
}
