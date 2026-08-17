package com.elctrovotesuperx.view.CommonView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Sidebar {

    private final VBox sidebar;

    private final String NAV_STYLE =
        "-fx-background-color: transparent;" +
        "-fx-text-fill: #B5C0D4;" +
        "-fx-font-size: 15px;" +
        "-fx-alignment: CENTER_LEFT;" +
        "-fx-padding: 0 14 0 18;";

    private final String ACTIVE_STYLE =
        "-fx-background-color: #3264E5;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 15px;" +
        "-fx-alignment: CENTER_LEFT;" +
        "-fx-padding: 0 14 0 18;" +
        "-fx-background-radius: 16;";

    // ========================================
    // BUTTONS
    // ========================================

    private final Button home;
    private final Button organizations;

    private final Button admin;
    private final Button members;
    private final Button candidates;
    private final Button elections;
    private final Button results;
    private final Button reports;

    private final Button notifications;
    private final Button profile;

    private final Button logout;

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public Sidebar() {

        sidebar = new VBox(5);

        sidebar.setPrefWidth(300);
        sidebar.setMinWidth(300);

        sidebar.setPadding(
            new Insets(0, 15, 10, 15)
        );

        sidebar.setStyle(
            "-fx-background-color: #0F1B33;"
        );

        // ========================================
        // BRAND
        // ========================================

        HBox brand =
                new HBox(12);

        brand.setAlignment(
            Pos.CENTER_LEFT
        );

        brand.setPadding(
            new Insets(20, 10, 20, 10)
        );

        Circle logo =
                new Circle(
                    23,
                    Color.web("#6C4DE8")
                );

        Label brandText =
                new Label("ElectraVote");

        brandText.setFont(
            Font.font("Arial", 21)
        );

        brandText.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        brand.getChildren().addAll(
            logo,
            brandText
        );

        // ========================================
        // GENERAL
        // ========================================

        Label general =
                section("GENERAL");

        home =
                nav("⌂", "Home", true);

        organizations =
                nav("▥", "Organizations", false);

        // ========================================
        // ADMIN
        // ========================================

        Label adminTitle =
                section("ADMIN");

        admin =
                nav("▦", "Admin Dashboard", false);

        members =
                nav("♧", "Members", false);

        candidates =
                nav("♙", "Candidates", false);

        elections =
                nav("☑", "Elections", false);

        results =
                nav("◷", "Results", false);

        reports =
                nav("▤", "Reports", false);

        // ========================================
        // ACCOUNT
        // ========================================

        Label account =
                section("ACCOUNT");

        notifications =
                nav("♧", "Notifications", false);

        profile =
                nav("◎", "Profile", false);

        Region spacer =
                new Region();

        VBox.setVgrow(
            spacer,
            Priority.ALWAYS
        );

        logout =
                nav("↪", "Logout", false);

        // ========================================
        // SIDEBAR ITEMS
        // ========================================

        sidebar.getChildren().addAll(

            brand,

            general,
            home,
            organizations,

            adminTitle,
            admin,
            members,
            candidates,
            elections,
            results,
            reports,

            account,
            notifications,
            profile,

            spacer,
            logout
        );
    }

    // ========================================
    // GET SIDEBAR
    // ========================================

    public VBox getSidebar() {

        return sidebar;
    }

    // ========================================
    // GET BUTTONS
    // ========================================

    public Button getHome() {

        return home;
    }

    public Button getOrganizations() {

        return organizations;
    }

    public Button getAdmin() {

        return admin;
    }

    public Button getMembers() {

        return members;
    }

    public Button getCandidates() {

        return candidates;
    }

    public Button getElections() {

        return elections;
    }

    public Button getResults() {

        return results;
    }

    public Button getReports() {

        return reports;
    }

    public Button getNotifications() {

        return notifications;
    }

    public Button getProfile() {

        return profile;
    }

    public Button getLogout() {

        return logout;
    }

    // ========================================
    // ACTIVE BUTTON
    // ========================================

    public void setActive(Button activeButton) {

        home.setStyle(NAV_STYLE);
        organizations.setStyle(NAV_STYLE);

        admin.setStyle(NAV_STYLE);
        members.setStyle(NAV_STYLE);
        candidates.setStyle(NAV_STYLE);
        elections.setStyle(NAV_STYLE);
        results.setStyle(NAV_STYLE);
        reports.setStyle(NAV_STYLE);

        notifications.setStyle(NAV_STYLE);
        profile.setStyle(NAV_STYLE);

        logout.setStyle(NAV_STYLE);

        activeButton.setStyle(ACTIVE_STYLE);
    }

    // ========================================
    // SECTION
    // ========================================

    private Label section(String text) {

        Label label =
                new Label(text);

        label.setPadding(
            new Insets(18, 0, 6, 15)
        );

        label.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #72809C;"
        );

        return label;
    }

    // ========================================
    // NAV BUTTON
    // ========================================

    private Button nav(
            String icon,
            String text,
            boolean active) {

        Button button =
                new Button(
                    icon + "    " + text
                );

        button.setPrefWidth(270);
        button.setMinWidth(270);
        button.setPrefHeight(46);

        button.setStyle(
            active
                ? ACTIVE_STYLE
                : NAV_STYLE
        );

        return button;
    }
}