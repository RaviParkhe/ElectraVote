package com.electrovotesuperx.dao.OrganizationDAO;

import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.model.OrganizationModel.Organization;


public class OrganizationDAO {

    public String generateJoinCode() {
        return FirebaseDatabaseService.generateJoinCode();
    }

    public FirebaseAuthService.AuthResult registerOrganization(
            Organization organization) throws Exception {

        FirebaseAuthService.AuthResult auth =
                FirebaseAuthService.createUser(
                        organization.getAdminEmail(),
                        organization.getPassword());

        if (!auth.isSuccess()) {
            // If email is already registered, authenticate with password to link org
            if (auth.getMessage() != null
                    && (auth.getMessage().contains("already registered")
                            || auth.getMessage().contains("EMAIL_EXISTS"))) {
                auth = FirebaseAuthService.signIn(
                        organization.getAdminEmail(),
                        organization.getPassword());
            }

            if (!auth.isSuccess()) {
                return auth;
            }
        }

        boolean orgSaved = FirestoreDAO.saveOrganization(
        organization.getJoinCode(),
        organization.getOrganizationName(),
        organization.getAdminName(),
        organization.getAdminEmail(),
        auth.getLocalId(),
        auth.getIdToken());

if (!orgSaved) {
    return FirebaseAuthService.AuthResult.failure(
            "Unable to save organization.");
}

boolean userSaved = FirestoreDAO.saveUser(
        auth.getLocalId(),
        organization.getAdminName(),
        organization.getAdminEmail(),
        "ADMIN",
        organization.getJoinCode(),
        auth.getIdToken());

if (!userSaved) {
    return FirebaseAuthService.AuthResult.failure(
            "Unable to save admin user.");
}

        boolean adminSaved =
                FirebaseDatabaseService.saveAdminMembership(
                        organization.getJoinCode(),
                        auth.getLocalId(),
                        organization.getAdminName(),
                        organization.getAdminEmail(),
                        auth.getIdToken());

        if (!adminSaved) {
            return FirebaseAuthService.AuthResult.failure(
                    "Unable to save admin membership.");
        }

        return auth;
    }
}