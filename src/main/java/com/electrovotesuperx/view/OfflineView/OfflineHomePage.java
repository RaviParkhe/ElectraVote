package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.utils.Navigation;
import com.electrovotesuperx.view.CommonView.Header;
import com.electrovotesuperx.view.CommonView.Sidebar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class OfflineHomePage {

        private final Scene scene;

        public OfflineHomePage() {

                // =========================================================
                // ROOT
                // =========================================================

                BorderPane root = new BorderPane();

                root.setStyle("-fx-background-color: #F7F9FC;");

                // SIDEBAR
                root.setLeft(
                                Sidebar.getSidebar("HomePage"));

                // =========================================================
                // CENTER
                // =========================================================

                VBox center = new VBox();

                // =========================================================
                // HEADER
                // DO NOT CHANGE
                // =========================================================

                HBox topBar = new HBox();

                topBar.getChildren().add(
                                Header.getHeader(
                                                "Welcome to ElectroVote",
                                                "Secure Offline Election Management"));

                // =========================================================
                // CONTENT
                // =========================================================

                VBox content = new VBox(25);

                content.setPadding(
                                new Insets(32, 35, 40, 35));

                content.setStyle(
                                "-fx-background-color: #F7F9FC;");

                // =========================================================
                // WELCOME SECTION
                // =========================================================

                VBox welcomeSection = new VBox(8);

                Label title = new Label(
                                "Offline Voter Verification");

                title.setStyle(
                                "-fx-font-size: 30px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #172B4D;");

                Label sub = new Label(
                                "A secure local tool for polling officers to manage elections, " +
                                                "members and voter verification.");

                sub.setWrapText(true);

                sub.setStyle(
                                "-fx-font-size: 14px;" +
                                                "-fx-text-fill: #71829B;");

                welcomeSection.getChildren().addAll(
                                title,
                                sub);

                // =========================================================
                // SMALL SECTION TITLE
                // =========================================================

                Label quickActions = new Label(
                                "Quick Actions");

                quickActions.setStyle(
                                "-fx-font-size: 18px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #172B4D;");

                // =========================================================
                // CARDS
                // =========================================================

                HBox cards = new HBox(20);

                cards.setAlignment(Pos.CENTER_LEFT);

                // =========================================================
                // CARD 1 - ELECTIONS
                // =========================================================

                VBox electionCard = createCard(
                                "1",
                                "Elections",
                                "Create and manage elections",
                                "#10B981",
                                () -> Navigation.goTo(
                                                new OfflineElectionPage().getScene()));

                // =========================================================
                // CARD 2 - MEMBERS
                // =========================================================

                // VBox memberCard = createCard(
                // "2",
                // "Members",
                // "Manage registered members and voter IDs",
                // "#3B82F6",
                // () -> Navigation.goTo(
                // new OfflineMemberPage().getScene()));

                // =========================================================
                // CARD 3 - VERIFY
                // =========================================================

                VBox verifyCard = createCard(
                                "2",
                                "Verify Voter",
                                "Check voter eligibility and generate an authorization token",
                                "#8B5CF6",
                                () -> Navigation.goTo(
                                                new OfflineVerification().getScene()));

                // =========================================================
                // CARD 3 - TOKEN / STATUS
                // =========================================================

                VBox tokenCard = createCard(
                                "3",
                                "Token / Status",
                                "Complete voting and update the authorization status",
                                "#F59E0B",
                                () -> Navigation.goTo(
                                                new OfflineStatusPage().getScene()));

                // =========================================================
                // ADD CARDS
                // =========================================================

                cards.getChildren().addAll(
                                electionCard,
                                // memberCard,
                                verifyCard,
                                tokenCard);

                // ADD EVERYTHING TO CONTENT
                // =========================================================

                content.getChildren().addAll(
                                welcomeSection,
                                quickActions,
                                cards);
                content.setSpacing(61);

                // =========================================================
                // SCROLL PANE
                // =========================================================

                ScrollPane scroll = new ScrollPane(
                                content);

                scroll.setFitToWidth(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scroll.setStyle(
                                "-fx-background-color: #F7F9FC;" +
                                                "-fx-background: #F7F9FC;");

                // =========================================================
                // SCROLL GROW
                // =========================================================

                VBox.setVgrow(
                                scroll,
                                Priority.ALWAYS);

                // =========================================================
                // CENTER CONTENT
                // =========================================================

                center.getChildren().addAll(
                                topBar,
                                scroll);

                // =========================================================
                // ROOT CENTER
                // =========================================================

                root.setCenter(center);

                // =========================================================
                // SCENE
                // =========================================================

                scene = new Scene(root);
        }

        // =============================================================
        // CREATE CARD
        // =============================================================

        private VBox createCard(
                        String number,
                        String title,
                        String description,
                        String accentColor,
                        Runnable action) {

                // =========================================================
                // MAIN CARD
                // =========================================================

                VBox card = new VBox(12);

                card.setPadding(
                                new Insets(22));

                card.setPrefWidth(250);
                card.setMinWidth(250);
                card.setMaxWidth(250);

                card.setMinHeight(185);
                card.setPrefHeight(185);

                card.setAlignment(
                                Pos.TOP_LEFT);

                card.setCursor(
                                Cursor.HAND);

                // =========================================================
                // CARD STYLE
                // =========================================================

                card.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-background-radius: 16px;" +
                                                "-fx-border-color: #E5ECF4;" +
                                                "-fx-border-radius: 16px;");

                // =========================================================
                // TOP ROW
                // =========================================================

                HBox topRow = new HBox();

                topRow.setAlignment(
                                Pos.CENTER_LEFT);

                // =========================================================
                // NUMBER CIRCLE
                // =========================================================

                Label numberLabel = new Label(
                                number);

                numberLabel.setAlignment(
                                Pos.CENTER);

                numberLabel.setPrefSize(
                                36,
                                36);

                numberLabel.setStyle(
                                "-fx-background-color: " + accentColor + ";" +
                                                "-fx-background-radius: 50%;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-size: 14px;" +
                                                "-fx-font-weight: bold;");

                // =========================================================
                // SPACER
                // =========================================================

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // =========================================================
                // ACTION LABEL
                // =========================================================

                Label actionLabel = new Label(
                                "OPEN");

                actionLabel.setStyle(
                                "-fx-font-size: 10px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + accentColor + ";");

                topRow.getChildren().addAll(
                                numberLabel,
                                spacer,
                                actionLabel);

                // =========================================================
                // CARD TITLE
                // =========================================================

                Label titleLabel = new Label(
                                title);

                titleLabel.setStyle(
                                "-fx-font-size: 19px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #172B4D;");

                // =========================================================
                // CARD DESCRIPTION
                // =========================================================

                Label descriptionLabel = new Label(
                                description);

                descriptionLabel.setWrapText(
                                true);

                descriptionLabel.setMaxWidth(
                                205);

                descriptionLabel.setStyle(
                                "-fx-font-size: 13px;" +
                                                "-fx-text-fill: #64748B;" +
                                                "-fx-line-spacing: 2px;");

                // =========================================================
                // BOTTOM SPACER
                // =========================================================

                Region bottomSpacer = new Region();

                VBox.setVgrow(
                                bottomSpacer,
                                Priority.ALWAYS);

                // =========================================================
                // ADD CARD CONTENT
                // =========================================================

                card.getChildren().addAll(
                                topRow,
                                titleLabel,
                                descriptionLabel,
                                bottomSpacer);

                // =========================================================
                // HOVER EFFECT
                // =========================================================

                card.setOnMouseEntered(
                                event -> {

                                        card.setScaleX(1.02);
                                        card.setScaleY(1.02);

                                        card.setStyle(
                                                        "-fx-background-color: white;" +
                                                                        "-fx-background-radius: 16px;" +
                                                                        "-fx-border-color: " + accentColor + ";" +
                                                                        "-fx-border-radius: 16px;" +
                                                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 4);");
                                });

                // =========================================================
                // REMOVE HOVER EFFECT
                // =========================================================

                card.setOnMouseExited(
                                event -> {

                                        card.setScaleX(1.0);
                                        card.setScaleY(1.0);

                                        card.setStyle(
                                                        "-fx-background-color: white;" +
                                                                        "-fx-background-radius: 16px;" +
                                                                        "-fx-border-color: #E5ECF4;" +
                                                                        "-fx-border-radius: 16px;");
                                });

                // =========================================================
                // CLICK
                // =========================================================

                card.setOnMouseClicked(
                                event -> action.run());

                return card;
        }

        // =============================================================
        // GET SCENE
        // =============================================================

        public Scene getScene() {

                return scene;
        }
}