package com.electrovotesuperx.controller.OfflineController;

import com.electrovotesuperx.dao.OfflineDAO.MemberDAO;
import com.electrovotesuperx.model.OfflineModel.Member;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class OfflineMembersController {

    // =========================================================
    // DAO
    // =========================================================

    private final MemberDAO memberDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OfflineMembersController() {
        this.memberDAO = new MemberDAO();
    }

    // =========================================================
    // GET ALL MEMBERS
    // =========================================================

    public List<Member> getMembers() {

        try {

            return memberDAO.getAllMembers();

        } catch (SQLException e) {

            System.err.println(
                    "Controller error while loading members: "
                            + e.getMessage());

            return Collections.emptyList();
        }
    }

    // =========================================================
    // GET MEMBERS FOR SELECTED ELECTION
    // =========================================================

    public List<Member> getMembersForElection(
            String electionId) {

        if (electionId == null ||
                electionId.isBlank()) {

            return Collections.emptyList();
        }

        try {

            return memberDAO.getMembersForElection(
                    electionId.trim());

        } catch (SQLException e) {

            System.err.println(
                    "Controller error while loading election members: "
                            + e.getMessage());

            return Collections.emptyList();
        }
    }

    // =========================================================
    // ADD MEMBER
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

        return memberDAO.addMember(
                electionId.trim(),
                name.trim(),
                email == null ? "" : email.trim(),
                gender.trim(),
                phone.trim());
    }

    // =========================================================
    // UPDATE MEMBER
    // =========================================================

    public boolean updateMember(Member member) {

        if (member == null) {
            return false;
        }

        return memberDAO.updateMember(member);
    }

    // =========================================================
    // SUSPEND MEMBER
    // =========================================================

    public boolean suspend(Member member) {

        if (member == null) {
            return false;
        }

        return memberDAO.suspend(member);
    }

    // =========================================================
    // ACTIVATE MEMBER
    // =========================================================

    public boolean activate(Member member) {

        if (member == null) {
            return false;
        }

        return memberDAO.activate(member);
    }

    // =========================================================
    // DELETE MEMBER
    // =========================================================

    public boolean deleteMember(Member member) {

        if (member == null) {
            return false;
        }

        return memberDAO.deleteMember(member);
    }
}