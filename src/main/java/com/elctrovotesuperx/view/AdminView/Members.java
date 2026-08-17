package com.elctrovotesuperx.view.AdminView;

import com.elctrovotesuperx.view.CommonView.Sidebar;
import com.elctrovotesuperx.view.HomePageView.Homepage;
import com.elctrovotesuperx.view.LoginPageView.Login;
import com.elctrovotesuperx.view.OrganizationView.Organizations;
import com.elctrovotesuperx.view.Page;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Members implements Page {

    private Scene scene;

    @Override
    public Scene getScene(Runnable backCallback) {

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
        // CONTENT
        // ========================================

        VBox box =
                new VBox(18);

        box.setAlignment(
            Pos.TOP_LEFT
        );

        box.setPadding(
            new Insets(35)
        );

        box.setStyle(
            "-fx-background-color: #F5F7FB;"
        );

        Label title =
                new Label("Members");

        title.setFont(
            Font.font("Arial", 28)
        );

        title.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        Label description =
                new Label(
                    "ElectraVote module: Members. " +
                    "This screen is ready for your business logic."
                );

        description.setStyle(
            "-fx-text-fill: #64748B;"
        );

        Label status =
                new Label("Ready");

        status.setStyle(
            "-fx-text-fill: #3264E5;"
        );

        Button back =
                new Button("← Back to Home");

        back.setPrefWidth(180);
        back.setPrefHeight(42);

        back.setStyle(
            "-fx-background-color: #3264E5;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 9;"
        );

        back.setOnAction(e ->
            backCallback.run()
        );

        box.getChildren().addAll(
            title,
            description,
            status,
            back
        );

        // ========================================
        // SIDEBAR NAVIGATION
        // ========================================

        // HOME
        sidebar.getHome().setOnAction(e -> {

            Homepage homepage =
                    new Homepage(Login.loginStage);

            Login.loginStage.setScene(
                homepage.getScene(() ->
                    backCallback.run()
                )
            );
        });

        // ORGANIZATIONS
        sidebar.getOrganizations().setOnAction(e -> {

            Organizations page =
                    new Organizations();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // ADMIN DASHBOARD
        sidebar.getAdmin().setOnAction(e -> {

            AdminDashboard page =
                    new AdminDashboard();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // MEMBERS
        sidebar.getMembers().setOnAction(e -> {

            // Already on Members
            Login.loginStage.setScene(scene);
        });

        // CANDIDATES
        sidebar.getCandidates().setOnAction(e -> {

            CandidateDashboard page =
                    new CandidateDashboard();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // ELECTIONS
        sidebar.getElections().setOnAction(e -> {

            ElectionPage page =
                    new ElectionPage();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // RESULTS
        sidebar.getResults().setOnAction(e -> {

            Results page =
                    new Results();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // REPORTS
        sidebar.getReports().setOnAction(e -> {

            Reports page =
                    new Reports();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // NOTIFICATIONS
        sidebar.getNotifications().setOnAction(e -> {

            Notifications page =
                    new Notifications();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // PROFILE
        sidebar.getProfile().setOnAction(e -> {

            Profile page =
                    new Profile();

            Login.loginStage.setScene(
                page.getScene(() ->
                    Login.loginStage.setScene(scene)
                )
            );
        });

        // LOGOUT
        sidebar.getLogout().setOnAction(e -> {

            Login.loginStage.setScene(
                Login.loginStage.getScene()
            );
        });

        // ========================================
        // ROOT
        // ========================================

        root.setCenter(box);

        // ========================================
        // SCENE
        // ========================================

        scene =
                new Scene(
                    root,
                    1200,
                    700
                );

        return scene;
    }
}