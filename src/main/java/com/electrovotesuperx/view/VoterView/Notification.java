package com.electrovotesuperx.view.VoterView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.dao.AdminDAO.VoteDAO;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.electrovotesuperx.model.AdminModel.ElectionData;

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

import java.util.*;

public class Notification {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String PURPLE = "#7B4DFF";
    private static final String GREEN = "#10B981";
    private static final String GOLD = "#D97706";
    private static final String RED = "#EF4444";

    public static VBox createNotificationView(List<Map<String, String>> legacyRecords) {
        return createNotificationView();
    }

    /**
     * Dynamically generates the Notification view using live session, election results, and candidate data.
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
                "Stay updated with live election winning standings, ballot schedules, announcements, and administrative reviews.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        // Notifications List Container
        VBox notificationsList = new VBox(16);

        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(25));
        loadingBox.getChildren().addAll(new ProgressIndicator(), new Label("Fetching real-time notifications & election winning standings..."));
        notificationsList.getChildren().add(loadingBox);

        Thread t = new Thread(() -> {
            String org = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                    ? SessionManager.organizationName : (SessionManager.joinCode != null ? SessionManager.joinCode : "Organization");
            String voterName = SessionManager.voterName != null ? SessionManager.voterName : "Voter";
            String voterEmail = SessionManager.voterEmail != null ? SessionManager.voterEmail.trim().toLowerCase() : "";
            String joinCode = SessionManager.joinCode;
            String idToken = SessionManager.idToken;

            List<VBox> cards = new ArrayList<>();

            // ─── 1. Live Election Winner & Leading Candidate Notifications ───
            try {
                if (joinCode != null && !joinCode.isBlank()) {
                    List<ElectionData> elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                    if (elections != null) {
                        for (ElectionData elec : elections) {
                            Map<String, Map<String, Integer>> results = null;
                            try {
                                results = VoteDAO.getResults(elec.getId(), idToken);
                            } catch (Exception ignored) {}

                            int totalElectionVotes = 0;
                            StringBuilder winnerSummary = new StringBuilder();
                            boolean hasWinners = false;

                            if (results != null && !results.isEmpty()) {
                                for (Map.Entry<String, Map<String, Integer>> posEntry : results.entrySet()) {
                                    String pos = posEntry.getKey();
                                    Map<String, Integer> candVotes = posEntry.getValue();
                                    int posTotal = 0;
                                    String topCand = null;
                                    int maxVotes = -1;

                                    for (Map.Entry<String, Integer> cv : candVotes.entrySet()) {
                                        posTotal += cv.getValue();
                                        totalElectionVotes += cv.getValue();
                                        if (cv.getValue() > maxVotes) {
                                            maxVotes = cv.getValue();
                                            topCand = cv.getKey();
                                        }
                                    }

                                    if (maxVotes > 0 && topCand != null) {
                                        hasWinners = true;
                                        double pct = posTotal > 0 ? ((double) maxVotes / posTotal) * 100.0 : 0.0;
                                        winnerSummary.append("• Position: ").append(pos)
                                                .append(" ➔ Current Winner/Leader: 👑 ").append(topCand)
                                                .append(" with ").append(maxVotes).append(" votes (")
                                                .append(String.format("%.1f", pct)).append("% of ")
                                                .append(posTotal).append(" ballots)\n");
                                    }
                                }
                            }

                            if (hasWinners) {
                                cards.add(createNotificationCard(
                                        org,
                                        "🏆 Election Winner / Leading Notice: " + elec.getTitle(),
                                        "Official live tally update for election '" + elec.getTitle() + "':\n\n"
                                                + winnerSummary.toString().trim()
                                                + "\n\nTotal ballots recorded: " + totalElectionVotes + " votes.",
                                        "👑 Live Winner",
                                        GOLD
                                ));
                            } else {
                                cards.add(createNotificationCard(
                                        org,
                                        "🗳️ Active Election Ballot Open: " + elec.getTitle(),
                                        "Voting is active for '" + elec.getTitle() + "'. No votes have been recorded yet. Log in to the Vote section to cast your confidential encrypted ballot!",
                                        "Live Ballot",
                                        BLUE
                                ));
                            }
                        }
                    }
                }
            } catch (Exception exElec) {
                System.err.println("[Notification] Election results load note: " + exElec.getMessage());
            }

            // ─── 2. Query Candidate Nomination Status ───
            try {
                if (joinCode != null) {
                    List<Candidate> candidates = CandidateDAO.getCandidatesByOrg(joinCode, idToken);
                    if (candidates != null) {
                        for (Candidate c : candidates) {
                            boolean match = (c.getEmail() != null && c.getEmail().trim().equalsIgnoreCase(voterEmail)) ||
                                    (c.getName() != null && c.getName().trim().equalsIgnoreCase(voterName));
                            if (match) {
                                String status = c.getStatus() != null ? c.getStatus().toUpperCase() : "PENDING";
                                if ("ACCEPTED".equals(status) || "APPROVED".equals(status)) {
                                    cards.add(createNotificationCard(
                                            org,
                                            "Candidate Nomination Approved 🎉",
                                            "Congratulations! Your candidate nomination for '" + c.getPosition() + "' has been approved by the election administrator and added to the official ballot.",
                                            "Approved",
                                            PURPLE
                                    ));
                                } else if ("REJECTED".equals(status)) {
                                    cards.add(createNotificationCard(
                                            org,
                                            "Candidate Nomination Status Update",
                                            "Your candidate application for '" + c.getPosition() + "' was reviewed and declined by the election committee.",
                                            "Notice",
                                            RED
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
                }
            } catch (Exception ignored) {}

            // ─── 3. Account Verified Notice ───
            cards.add(createNotificationCard(
                    org,
                    "Voter Enrollment Verified",
                    "Welcome, " + voterName + "! Your eligibility to cast ballots in " + org + " has been verified. You may access all active elections and ballot portals.",
                    "Active",
                    GREEN
            ));

            Platform.runLater(() -> {
                notificationsList.getChildren().clear();
                if (cards.isEmpty()) {
                    Text emptyText = new Text("No new notifications available.");
                    emptyText.setFill(Color.web(SECONDARY));
                    emptyText.setFont(Font.font(13));
                    notificationsList.getChildren().add(emptyText);
                } else {
                    int delay = 0;
                    for (VBox c : cards) {
                        com.electrovotesuperx.utils.UIAnimationHelper.fadeInSlideUp(c, delay);
                        delay += 90;
                        notificationsList.getChildren().add(c);
                    }
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
                        "-fx-border-color: " + (GOLD.equals(themeColor) ? "#FCD34D" : BORDER) + ";" +
                        "-fx-border-width: " + (GOLD.equals(themeColor) ? "1.6;" : "1;") +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.05), 8, 0, 0, 3);");

        com.electrovotesuperx.utils.UIAnimationHelper.addCardHover(card);

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
                "-fx-background-color: " + (GOLD.equals(themeColor) ? "#FEF3C7" : "#F1F5F9") + ";" +
                        "-fx-text-fill: " + (GOLD.equals(themeColor) ? "#B45309" : "#64748B") + ";" +
                        "-fx-font-size: 11.5px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 10;" +
                        "-fx-background-radius: 10;" +
                        (GOLD.equals(themeColor) ? "-fx-border-color: #FCD34D; -fx-border-radius: 10;" : ""));

        topRow.getChildren().addAll(avatarPane, titleBox, spacer, timeBadge);

        Text bodyText = new Text(messageBody);
        bodyText.setFill(Color.web(SECONDARY));
        bodyText.setFont(Font.font(12.5));
        bodyText.setWrappingWidth(850);

        card.getChildren().addAll(topRow, new Separator(), bodyText);
        return card;
    }
}