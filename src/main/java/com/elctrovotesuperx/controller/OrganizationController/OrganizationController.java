package com.elctrovotesuperx.controller.OrganizationController;


import com.elctrovotesuperx.dao.OrganizationDAO.OrganizationDAO;
import com.elctrovotesuperx.exception.AuthenticationException;
import com.elctrovotesuperx.exception.FirestoreException;
import com.elctrovotesuperx.model.OrganizationModel.Organization;
import com.elctrovotesuperx.config.firebaseConfig.FirebaseAuthService;

public class OrganizationController {

    private final OrganizationDAO dao = new OrganizationDAO();

    public String generateJoinCode() {
        return dao.generateJoinCode();
    }

    public FirebaseAuthService.AuthResult register(
            Organization organization,
            String confirmPassword)
            throws AuthenticationException, FirestoreException, Exception {

        if (organization.getAdminName().isBlank()
                || organization.getOrganizationName().isBlank()
                || organization.getAdminEmail().isBlank()
                || organization.getPassword().isBlank()) {

            return FirebaseAuthService.AuthResult.failure(
                    "Please fill all required fields.");
        }

        if (!organization.getPassword().equals(confirmPassword)) {

            return FirebaseAuthService.AuthResult.failure(
                    "Password and confirm password must match.");
        }

        if (organization.getPassword().length() < 6) {

            return FirebaseAuthService.AuthResult.failure(
                    "Password must contain at least 6 characters.");
        }

        if (organization.getJoinCode() == null
                || organization.getJoinCode().isBlank()) {

            organization.setJoinCode(generateJoinCode());
        }

        return dao.registerOrganization(organization);
    }
}