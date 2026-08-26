package com.elctrovotesuperx.dao.OrganizationDAO;

import com.elctrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.elctrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.elctrovotesuperx.model.OrganizationModel.Organization;


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
            return auth;
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