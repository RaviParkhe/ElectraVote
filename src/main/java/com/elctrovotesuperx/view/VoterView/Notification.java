package com.elctrovotesuperx.view.VoterView;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.elctrovotesuperx.model.AdminModel.Candidate;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Notification {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String PURPLE = "#7B4DFF";
    private static final String GREEN = "#10B981";

    public static VBox createNotificationView(List<Map<String, String>> legacyRecords) {
        return createNotificationView();
    }

    /**
     * Dynamically generates the Notification view using live session and database data.
     */
    public static VBox createNotificationView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header Section
        VBox heading = new VBox(6);
        Text title = new Text("Notifications & Alerts 🔔");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text(
                "Stay updated with announcements, ballot schedules, and administrative candidate reviews from your organizations.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        // Notifications List Container
        VBox notificationsList = new VBox(16);

        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(20));
        loadingBox.getChildren().addAll(new ProgressIndicator(), new Label("Checking for notifications..."));
        notificationsList.getChildren().add(loadingBox);

        Thread t = new Thread(() -> {
            String org = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                    ? SessionManager.organizationName : "Organization";
            String voterName = SessionManager.voterName != null ? SessionManager.voterName : "Voter";
            String voterEmail = SessionManager.voterEmail != null ? SessionManager.voterEmail.trim().toLowerCase() : "";

            List<VBox> cards = new ArrayList<>();

            // 1. Account verified notice
            cards.add(createNotificationCard(
                    org,
                    "Voter Enrollment Verified",
                    "Welcome, " + voterName + "! Your eligibility to cast ballots in " + org + " has been verified. You may access all active elections and ballot portals.",
                    "Active",
                    GREEN
            ));

            // 2. Query candidate filings status
            try {
                if (SessionManager.joinCode != null) {
                    List<Candidate> candidates = CandidateDAO.getCandidatesByOrg(SessionManager.joinCode, SessionManager.idToken);
                    for (Candidate c : candidates) {
                        boolean match = (c.getEmail() != null && c.getEmail().trim().equalsIgnoreCase(voterEmail)) ||
                                (c.getName() != null && c.getName().trim().equalsIgnoreCase(voterName));
                        if (match) {
                            String status = c.getStatus() != null ? c.getStatus().toUpperCase() : "PENDING";
                            if ("ACCEPTED".equals(status)) {
                                cards.add(createNotificationCard(
                                        org,
                                        "Candidate Nomination Approved 🎉",
                                        "Congratulations! Your candidate nomination for '" + c.getPosition() + "' has been approved by the election administrator and added to the official ballot.",
                                        "Official",
                                        PURPLE
                                ));
                            } else if ("REJECTED".equals(status)) {
                                cards.add(createNotificationCard(
                                        org,
                                        "Candidate Nomination Status Update",
                                        "Your candidate application for '" + c.getPosition() + "' was reviewed and declined by the election committee.",
                                        "Notice",
                                        "#EF4444"
                                ));
                            } else {
                                cards.add(createNotificationCard(
                                        org,
                                        "Candidate Application Under Review",
                                        "Your candidate filing for '" + c.getPosition() + "' is currently pending review by the election administrator.",
                                        "Pending",
                                        BLUE
                                ));
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}

            Platform.runLater(() -> {
                notificationsList.getChildren().clear();
                if (cards.isEmpty()) {
                    Text emptyText = new Text("No new notifications available.");
                    emptyText.setFill(Color.web(SECONDARY));
                    emptyText.setFont(Font.font(13));
                    notificationsList.getChildren().add(emptyText);
                } else {
                    notificationsList.getChildren().addAll(cards);
                }
            });
        });
        t.setDaemon(true);
        t.start();

        content.getChildren().addAll(heading, new Separator(), notificationsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createNotificationCard(String orgName, String messageTitle, String messageBody, String timeAgo,
            String themeColor) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(20);
        avatarCircle.setFill(Color.web(themeColor));
        Text avatarInitial = new Text(!orgName.isEmpty() ? orgName.substring(0, 1).toUpperCase() : "N");
        avatarInitial.setFill(Color.WHITE);
        avatarInitial.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        StackPane avatarPane = new StackPane(avatarCircle, avatarInitial);

        VBox titleBox = new VBox(2);
        titleBox.setPadding(new Insets(0, 0, 0, 10));

        Text orgLabel = new Text(orgName);
        orgLabel.setFill(Color.web(BLUE));
        orgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Text msgTitle = new Text(messageTitle);
        msgTitle.setFill(Color.web(TEXT));
        msgTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        titleBox.getChildren().addAll(orgLabel, msgTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeBadge = new Label(timeAgo);
        timeBadge.setStyle(
                "-fx-background-color: #F1F5F9;" +
                        "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 10;" +
                        "-fx-background-radius: 10;");

        topRow.getChildren().addAll(avatarPane, titleBox, spacer, timeBadge);

        Text bodyText = new Text(messageBody);
        bodyText.setFill(Color.web(SECONDARY));
        bodyText.setFont(Font.font(12.5));
        bodyText.setWrappingWidth(850);

        card.getChildren().addAll(topRow, new Separator(), bodyText);
        return card;
    }
}