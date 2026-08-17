package com.elctrovotesuperx.view.AdminView;

import com.elctrovotesuperx.view.CommonView.Sidebar;
import com.elctrovotesuperx.view.OrganizationView.Organizations;
import com.elctrovotesuperx.view.Page;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AdminDashboard implements Page {

    private Scene adminScene;

    @Override
    public Scene getScene(Runnable homepageCallback) {

        // ========================================
        // ROOT
        // ========================================

        BorderPane root =
                new BorderPane();

        root.setStyle(
            "-fx-background-color: #F5F7FB;"
        );

        // ========================================
        // SIDEBAR
        // ========================================

        Sidebar sidebar =
                new Sidebar();

        root.setLeft(
            sidebar.getSidebar()
        );

        // ========================================
        // SIDEBAR BUTTONS
        // ========================================

        Button home =
                sidebar.getHome();

        Button organizations =
                sidebar.getOrganizations();

        Button admin =
                sidebar.getAdmin();

        Button members =
                sidebar.getMembers();

        Button candidates =
                sidebar.getCandidates();

        Button elections =
                sidebar.getElections();

        Button results =
                sidebar.getResults();

        Button reports =
                sidebar.getReports();

        Button notifications =
                sidebar.getNotifications();

        Button profile =
                sidebar.getProfile();

        Button logout =
                sidebar.getLogout();

        // ========================================
        // ACTIVE ADMIN
        // ========================================

        sidebar.setActive(admin);

        // ========================================
        // CONTENT
        // ========================================

        VBox content =
                new VBox(20);

        content.setPadding(
            new Insets(40)
        );

        Label title =
                new Label(
                    "Admin Dashboard"
                );

        title.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #17213A;"
        );

        Label subtitle =
                new Label(
                    "Manage elections, candidates and organizations"
                );

        subtitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #68748E;"
        );

        content.getChildren().addAll(
            title,
            subtitle
        );

        // ========================================
        // HOME
        // ========================================

        home.setOnAction(e -> {

            homepageCallback.run();

        });

        // ========================================
        // ORGANIZATIONS
        // ========================================

        organizations.setOnAction(e -> {

            Organizations page =
                    new Organizations();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // ADMIN DASHBOARD
        // ========================================

        admin.setOnAction(e -> {

            sidebar.setActive(admin);

            root.setCenter(content);
        });

        // ========================================
        // MEMBERS
        // ========================================

        members.setOnAction(e -> {

            Members page =
                    new Members();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // CANDIDATES
        // ========================================

        candidates.setOnAction(e -> {

            CandidateDashboard page =
                    new CandidateDashboard();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // ELECTIONS
        // ========================================

        elections.setOnAction(e -> {

            ElectionManagement page =
                    new ElectionManagement();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // RESULTS
        // ========================================

        results.setOnAction(e -> {

            Results page =
                    new Results();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // REPORTS
        // ========================================

        reports.setOnAction(e -> {

            Reports page =
                    new Reports();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // NOTIFICATIONS
        // ========================================

        notifications.setOnAction(e -> {

            Notifications page =
                    new Notifications();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // PROFILE
        // ========================================

        profile.setOnAction(e -> {

            Profile page =
                    new Profile();

            adminSceneRoot(
                page,
                sidebar
            );
        });

        // ========================================
        // LOGOUT
        // ========================================

        logout.setOnAction(e -> {

            homepageCallback.run();

        });

        // ========================================
        // ROOT
        // ========================================

        root.setCenter(
            content
        );

        // ========================================
        // SCENE
        // ========================================

        adminScene =
                new Scene(
                    root,
                    1200,
                    700
                );

        return adminScene;
    }

    // ========================================
    // OPEN ADMIN PAGE
    // ========================================

    private void adminSceneRoot(
            Page page,
            Sidebar sidebar) {

        sidebar.getSidebar().setDisable(false);

        // This method is intentionally not
        // replacing the global navigation logic.
        //
        // The selected page receives its own
        // callback to return to AdminDashboard.

        com.elctrovotesuperx.view.LoginPageView.Login.loginStage.setScene(

            page.getScene(() -> {

                com.elctrovotesuperx.view.LoginPageView.Login.loginStage.setScene(
                    adminScene
                );

            })
        );
    }
}