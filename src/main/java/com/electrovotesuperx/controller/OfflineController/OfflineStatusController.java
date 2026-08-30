package com.electrovotesuperx.controller.OfflineController;

import com.electrovotesuperx.exception.DatabaseException;
import com.electrovotesuperx.service.OfflineService.VoterVerificationService;

public class OfflineStatusController {

    private final VoterVerificationService service = new VoterVerificationService();

    public VoterVerificationService.CompletionResult completeVote(
            String token) throws DatabaseException {

        return service.completeVote(token);
    }
}