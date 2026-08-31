package com.electrovotesuperx;

import com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserOrganizationsTest {

    @Test
    void testUserOrganizationMembershipModel() {
        UserOrganizationMembership mem = new UserOrganizationMembership(
                "EV-FG7E-PATQ",
                "St. Xavier's University",
                "VOTER",
                "ACCEPTED",
                "Voter 1",
                "voter1@gmail.com",
                3,
                true
        );

        assertEquals("EV-FG7E-PATQ", mem.getJoinCode());
        assertEquals("St. Xavier's University", mem.getOrganizationName());
        assertEquals("VOTER", mem.getRole());
        assertEquals("ACCEPTED", mem.getStatus());
        assertEquals("Voter 1", mem.getMemberName());
        assertEquals("voter1@gmail.com", mem.getMemberEmail());
        assertEquals(3, mem.getActiveElectionsCount());
        assertTrue(mem.isActiveContext());

        mem.setActiveContext(false);
        assertFalse(mem.isActiveContext());
    }

    @Test
    void testMultipleOrganizationSwitching() {
        UserOrganizationMembership org1 = new UserOrganizationMembership(
                "EV-ORG1-AAAA", "Organization Alpha", "VOTER", "ACCEPTED", "Voter 1", "v1@test.com", 2, true
        );
        UserOrganizationMembership org2 = new UserOrganizationMembership(
                "EV-ORG2-BBBB", "Organization Beta", "ADMIN", "ACCEPTED", "Voter 1", "v1@test.com", 5, false
        );

        assertTrue(org1.isActiveContext());
        assertFalse(org2.isActiveContext());

        // Simulate context switch
        org1.setActiveContext(false);
        org2.setActiveContext(true);

        assertFalse(org1.isActiveContext());
        assertTrue(org2.isActiveContext());
        assertEquals("ADMIN", org2.getRole());
    }
}
