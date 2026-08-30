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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ActiveElection {

    private static final String TEXT = "#0F172A";
    private static final String SECONDARY = "#64748B";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#2563EB";
    private static final String GREEN = "#10B981";
    private static final String AMBER = "#F59E0B";
    private static final String PURPLE = "#8B5CF6";
    private static final String RED = "#EF4444";
    private static final String GOLD = "#D97706";

    /**
     * Creates the Active & Upcoming Elections view with live Firebase candidates
     * and winning telemetry.
     */
    public static VBox createActiveElectionView() {
        VBox content = new VBox(22);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom right, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header Section
        VBox heading = new VBox(6);
        Text title = new Text("Active & Upcoming Elections 🗳️");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));

        Text subtitle = new Text(
                "Inspect live candidate rosters, real-time winning telemetry, and ballot standings directly from Firebase.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font("Segoe UI", 13.5));
        subtitle.setWrappingWidth(920);
        heading.getChildren().addAll(title, subtitle);

        VBox electionsList = new VBox(20);
        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(40));
        ProgressIndicator pi = new ProgressIndicator();
        Label loadLabel = new Label("Connecting to Firebase & fetching live election telemetry...");
        loadLabel.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        loadingBox.getChildren().addAll(pi, loadLabel);
        electionsList.getChildren().add(loadingBox);

        Thread fetchThread = new Thread(() -> {
            String joinCode = SessionManager.joinCode;
            String idToken = SessionManager.idToken;
            List<ElectionData> elections = new ArrayList<>();
            Map<String, List<Candidate>> electionCandidatesMap = new HashMap<>();
            Map<String, Map<String, Map<String, Integer>>> electionResultsMap = new HashMap<>();

            try {
                if (joinCode != null && !joinCode.isBlank()) {
                    elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                    List<Candidate> allCandidates = CandidateDAO.getCandidatesByOrg(joinCode, idToken);

                    for (ElectionData elec : elections) {
                        // 1. Group candidates for this election
                        List<Candidate> thisElecCandidates = new ArrayList<>();
                        for (Candidate c : allCandidates) {
                            if ((c.getElectionId() != null && c.getElectionId().equalsIgnoreCase(elec.getId()))
                                    || (c.getElectionTitle() != null
                                            && c.getElectionTitle().equalsIgnoreCase(elec.getTitle()))) {
                                thisElecCandidates.add(c);
                            }
                        }
                        electionCandidatesMap.put(elec.getId(), thisElecCandidates);

                        // 2. Fetch live vote results from Firestore
                        try {
                            Map<String, Map<String, Integer>> results = VoteDAO.getResults(elec.getId(), idToken);
                            if (results != null) {
                                electionResultsMap.put(elec.getId(), results);
                            }
                        } catch (Exception exVote) {
                            System.err.println("[ActiveElection] Live vote fetch notice: " + exVote.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("[ActiveElection] Firebase fetch error: " + ex.getMessage());
            }

            final List<ElectionData> finalElections = elections;
            Platform.runLater(() -> {
                electionsList.getChildren().clear();
                if (finalElections.isEmpty()) {
                    VBox emptyBox = new VBox(12);
                    emptyBox.setAlignment(Pos.CENTER);
                    emptyBox.setPadding(new Insets(45));
                    emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: "
                            + BORDER + "; -fx-border-radius: 14;");
                    Label icon = new Label("🗳️");
                    icon.setFont(Font.font(40));
                    Text emptyTitle = new Text("No Elections Scheduled Yet");
                    emptyTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
                    emptyTitle.setFill(Color.web(TEXT));
                    Text emptySub = new Text(
                            "When the organization administrator creates an election in Admin Dashboard, it will appear here with live candidate lists and vote counts.");
                    emptySub.setFill(Color.web(SECONDARY));
                    emptySub.setFont(Font.font("Segoe UI", 13.5));
                    emptyBox.getChildren().addAll(icon, emptyTitle, emptySub);
                    electionsList.getChildren().add(emptyBox);
                } else {
                    int delay = 0;
                    boolean anyWinningVotes = false;
                    for (ElectionData elec : finalElections) {
                        List<Candidate> candList = electionCandidatesMap.getOrDefault(elec.getId(),
                                Collections.emptyList());
                        Map<String, Map<String, Integer>> results = electionResultsMap.getOrDefault(elec.getId(),
                                Collections.emptyMap());
                        if (results != null && !results.isEmpty()) {
                            anyWinningVotes = true;
                        }
                        VBox card = buildElectionTelemetryCard(elec, candList, results);
                        com.electrovotesuperx.utils.UIAnimationHelper.fadeInSlideUp(card, delay);
                        delay += 120;
                        electionsList.getChildren().add(card);
                    }
                    if (anyWinningVotes) {
                        com.electrovotesuperx.utils.UIAnimationHelper.playCelebrationConfetti(content);
                    }
                }
            });
        });
        fetchThread.setDaemon(true);
        fetchThread.start();

        content.getChildren().addAll(heading, new Separator(), electionsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    /**
     * Builds a comprehensive live Election Card with real Firebase candidates and
     * winning telemetry.
     */
    private static VBox buildElectionTelemetryCard(
            ElectionData election,
            List<Candidate> candidates,
            Map<String, Map<String, Integer>> voteResults) {

        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 14;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.06), 12, 0, 0, 4);");

        // ─── 1. Header Row ───
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Text elecName = new Text(election.getTitle() != null ? election.getTitle() : "Untitled Election");
        elecName.setFill(Color.web(TEXT));
        elecName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 19));

        HBox metaRow = new HBox(8);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        boolean isLive = "Active".equalsIgnoreCase(election.getStatus())
                || "Ongoing".equalsIgnoreCase(election.getStatus());
        Label statusPill = new Label(isLive ? "🟢 LIVE VOTING"
                : "🟣 " + (election.getStatus() != null ? election.getStatus().toUpperCase() : "UPCOMING"));
        statusPill.setStyle(
                "-fx-background-color: " + (isLive ? "#ECFDF5" : "#F3E8FF") + ";" +
                        "-fx-text-fill: " + (isLive ? "#059669" : "#7C3AED") + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 3 9;" +
                        "-fx-background-radius: 12;");

        String timeInfo = (election.getStartDateTime() != null && !election.getStartDateTime().isBlank())
                ? "Schedule: " + election.getStartDateTime() + " → "
                        + (election.getEndDateTime() != null ? election.getEndDateTime() : "TBD")
                : "Open Election";
        Label timeLabel = new Label(timeInfo);
        timeLabel.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-size: 12px;");

        metaRow.getChildren().addAll(statusPill, new Label("•"), timeLabel);
        titleBox.getChildren().addAll(elecName, metaRow);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button applyBtn = new Button("✍️ Apply as Candidate");
        applyBtn.setStyle(
                "-fx-background-color: #F1F5F9;" +
                        "-fx-text-fill: #1E293B;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 8 14;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8;");
        applyBtn.setOnAction(e -> {
            VBox formView = ApplyCandidateView.createApplyCandidateView(election.getTitle());
            VoterDashboard.dashboardCenter.setCenter(formView);
        });
        com.electrovotesuperx.utils.UIAnimationHelper.addScaleHover(applyBtn, 1.04);

        Button voteBtn = new Button("🗳️ Cast Ballot ➔");
        voteBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #2563EB, #1D4ED8);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12.5px;" +
                        "-fx-padding: 8 18;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;");
        voteBtn.setOnAction(e -> {
            VoterDashboard.showPage(Vote.createOrganizationSelectionView());
        });
        com.electrovotesuperx.utils.UIAnimationHelper.addScaleHover(voteBtn, 1.05);

        actionButtons.getChildren().addAll(applyBtn, voteBtn);
        topRow.getChildren().addAll(titleBox, topSpacer, actionButtons);

        // Description
        if (election.getDescription() != null && !election.getDescription().isBlank()) {
            Text descText = new Text(election.getDescription());
            descText.setFill(Color.web(SECONDARY));
            descText.setFont(Font.font("Segoe UI", 13));
            descText.setWrappingWidth(900);
            card.getChildren().addAll(topRow, descText, new Separator());
        } else {
            card.getChildren().addAll(topRow, new Separator());
        }

        // ─── 2. 👑 Live Winning Leader Telemetry Banner ───
        VBox winningSection = buildWinningLeaderSection(election, candidates, voteResults);
        card.getChildren().add(winningSection);

        // ─── 3. 👥 Candidates List from Firebase ───
        VBox candidatesSection = buildCandidatesSection(election, candidates, voteResults);
        card.getChildren().add(candidatesSection);

        com.electrovotesuperx.utils.UIAnimationHelper.addCardHover(card);

        return card;
    }

    /**
     * Analyzes voteResults and candidate data to display who is currently winning.
     */
    private static VBox buildWinningLeaderSection(
            ElectionData election,
            List<Candidate> candidates,
            Map<String, Map<String, Integer>> voteResults) {

        VBox container = new VBox(8);
        container.setPadding(new Insets(14, 18, 14, 18));
        container.setStyle(
                "-fx-background-color: linear-gradient(to right, #FFFBEB, #FEF3C7);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #FCD34D;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;");

        HBox headerRow = new HBox(8);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label crownIcon = new Label("👑");
        crownIcon.setStyle("-fx-font-size: 16px;");
        com.electrovotesuperx.utils.UIAnimationHelper.addPulse(crownIcon);
        Label sectionTitle = new Label("LIVE ELECTION LEADERBOARD & WINNING STANDINGS");
        sectionTitle.setStyle(
                "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-text-fill: #92400E; -fx-font-size: 12.5px; -fx-letter-spacing: 0.5px;");
        headerRow.getChildren().addAll(crownIcon, sectionTitle);

        VBox leadersList = new VBox(6);

        // Calculate leaders per position
        List<String> positions = election.getPositions() != null && !election.getPositions().isEmpty()
                ? election.getPositions()
                : (voteResults != null && !voteResults.isEmpty() ? new ArrayList<>(voteResults.keySet())
                        : Arrays.asList("General"));

        boolean anyVotesFound = false;

        for (String pos : positions) {
            Map<String, Integer> posVotes = voteResults != null ? voteResults.get(pos) : null;
            int totalPosVotes = 0;
            String topCandidate = null;
            int maxVotes = -1;

            if (posVotes != null && !posVotes.isEmpty()) {
                for (Map.Entry<String, Integer> entry : posVotes.entrySet()) {
                    totalPosVotes += entry.getValue();
                    if (entry.getValue() > maxVotes) {
                        maxVotes = entry.getValue();
                        topCandidate = entry.getKey();
                    }
                }
            }

            if (totalPosVotes > 0 && topCandidate != null) {
                anyVotesFound = true;
                double pct = totalPosVotes > 0 ? ((double) maxVotes / totalPosVotes) * 100.0 : 0.0;

                HBox leaderRow = new HBox(10);
                leaderRow.setAlignment(Pos.CENTER_LEFT);

                Label posLabel = new Label("📌 " + pos + ":");
                posLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #78350F; -fx-font-size: 13px;");

                Label winnerLabel = new Label("🏆 " + topCandidate);
                winnerLabel.setStyle("-fx-font-weight: 900; -fx-text-fill: #047857; -fx-font-size: 13.5px;");

                Label scoreLabel = new Label("—  " + maxVotes + " votes (" + String.format("%.1f", pct) + "% of "
                        + totalPosVotes + " total ballots)");
                scoreLabel.setStyle("-fx-text-fill: #451A03; -fx-font-weight: bold; -fx-font-size: 12.5px;");

                leaderRow.getChildren().addAll(posLabel, winnerLabel, scoreLabel);
                leadersList.getChildren().add(leaderRow);
            }
        }

        if (!anyVotesFound) {
            Label noVotesLabel = new Label(
                    "⏳ No votes recorded yet in Firebase. Voting is currently open — cast the first ballot to start the tally!");
            noVotesLabel.setStyle("-fx-text-fill: #B45309; -fx-font-size: 12.5px; -fx-font-style: italic;");
            leadersList.getChildren().add(noVotesLabel);
        }

        container.getChildren().addAll(headerRow, leadersList);
        return container;
    }

    /**
     * Builds the Candidate Roster section loaded directly from Firebase.
     */
    private static VBox buildCandidatesSection(
            ElectionData election,
            List<Candidate> candidates,
            Map<String, Map<String, Integer>> voteResults) {

        VBox container = new VBox(10);

        HBox sectionHeader = new HBox(8);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        Label sectionTitle = new Label("👥 Nominated Candidates (from Firebase Database)");
        sectionTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-text-fill: " + TEXT
                + "; -fx-font-size: 14px;");
        Label countPill = new Label(candidates.size() + " Candidate" + (candidates.size() == 1 ? "" : "s"));
        countPill.setStyle(
                "-fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2 7; -fx-background-radius: 10;");
        sectionHeader.getChildren().addAll(sectionTitle, countPill);

        VBox candidateCardsBox = new VBox(8);

        if (candidates.isEmpty()) {
            HBox emptyCandBox = new HBox(10);
            emptyCandBox.setAlignment(Pos.CENTER_LEFT);
            emptyCandBox.setPadding(new Insets(12, 16, 12, 16));
            emptyCandBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: " + BORDER
                    + "; -fx-border-radius: 8;");
            Label infoLabel = new Label(
                    "ℹ️ No candidate nominations added yet for this election in Firebase. You can submit an application using the button above.");
            infoLabel.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-size: 12.5px;");
            emptyCandBox.getChildren().add(infoLabel);
            candidateCardsBox.getChildren().add(emptyCandBox);
        } else {
            for (Candidate cand : candidates) {
                candidateCardsBox.getChildren().add(buildCandidateRow(cand, voteResults));
            }
        }

        container.getChildren().addAll(sectionHeader, candidateCardsBox);
        return container;
    }

    /**
     * Builds an individual candidate row with live vote counts, status, and winning
     * indicator.
     */
    private static HBox buildCandidateRow(
            Candidate candidate,
            Map<String, Map<String, Integer>> voteResults) {

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 8;");

        // Avatar
        String initial = candidate.getName() != null && !candidate.getName().isBlank()
                ? candidate.getName().substring(0, 1).toUpperCase()
                : "C";
        StackPane avatar = new StackPane();
        Circle bgCircle = new Circle(18, Color.web("#DBEAFE"));
        Text initialText = new Text(initial);
        initialText.setFill(Color.web(BLUE));
        initialText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        avatar.getChildren().addAll(bgCircle, initialText);

        // Candidate Details
        VBox details = new VBox(2);
        HBox nameLine = new HBox(6);
        nameLine.setAlignment(Pos.CENTER_LEFT);

        Text nameText = new Text(candidate.getName() != null ? candidate.getName() : "Candidate");
        nameText.setFill(Color.web(TEXT));
        nameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label posBadge = new Label(candidate.getPosition() != null ? candidate.getPosition() : "Contestant");
        posBadge.setStyle(
                "-fx-background-color: #EFF6FF; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2 6; -fx-background-radius: 6;");

        nameLine.getChildren().addAll(nameText, posBadge);

        String bioText = (candidate.getBio() != null && !candidate.getBio().isBlank())
                ? candidate.getBio()
                : (candidate.getEmail() != null ? candidate.getEmail() : "Registered Candidate");
        Label bioLabel = new Label(bioText);
        bioLabel.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-size: 12px;");
        bioLabel.setMaxWidth(400);

        details.getChildren().addAll(nameLine, bioLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Compute Vote Stats for this candidate
        String position = candidate.getPosition() != null ? candidate.getPosition() : "";
        Map<String, Integer> posVotes = voteResults != null ? voteResults.get(position) : null;
        int candVotes = 0;
        int totalPosVotes = 0;
        boolean isCurrentLeader = false;

        if (posVotes != null) {
            int maxVotes = -1;
            String topCand = "";
            for (Map.Entry<String, Integer> e : posVotes.entrySet()) {
                totalPosVotes += e.getValue();
                if (e.getValue() > maxVotes) {
                    maxVotes = e.getValue();
                    topCand = e.getKey();
                }
            }
            if (candidate.getName() != null && posVotes.containsKey(candidate.getName())) {
                candVotes = posVotes.get(candidate.getName());
            }
            if (maxVotes > 0 && candidate.getName() != null && candidate.getName().equalsIgnoreCase(topCand)) {
                isCurrentLeader = true;
            }
        }

        // Live Winning / Vote Badge
        HBox statsBox = new HBox(10);
        statsBox.setAlignment(Pos.CENTER_RIGHT);

        if (isCurrentLeader) {
            Label winningPill = new Label("👑 WINNING");
            winningPill.setStyle(
                    "-fx-background-color: #FEF3C7;" +
                            "-fx-text-fill: #B45309;" +
                            "-fx-font-weight: 900;" +
                            "-fx-font-size: 11.5px;" +
                            "-fx-padding: 4 8;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: #FCD34D;" +
                            "-fx-border-radius: 6;");
            statsBox.getChildren().add(winningPill);
        }

        VBox voteTallyBox = new VBox(2);
        voteTallyBox.setAlignment(Pos.CENTER_RIGHT);
        double pct = totalPosVotes > 0 ? ((double) candVotes / totalPosVotes) * 100.0 : 0.0;
        Label votesCountLabel = new Label(
                candVotes + " Vote" + (candVotes == 1 ? "" : "s") + " (" + String.format("%.1f", pct) + "%)");
        votesCountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (candVotes > 0 ? "#047857" : SECONDARY)
                + "; -fx-font-size: 12.5px;");

        ProgressBar pb = new ProgressBar(totalPosVotes > 0 ? (double) candVotes / totalPosVotes : 0.0);
        pb.setPrefWidth(100);
        pb.setPrefHeight(6);
        pb.setStyle("-fx-accent: " + (isCurrentLeader ? "#10B981" : "#3B82F6") + ";");

        voteTallyBox.getChildren().addAll(votesCountLabel, pb);

        // Status Badge
        String status = candidate.getStatus() != null ? candidate.getStatus().toUpperCase() : "ACCEPTED";
        Label statusBadge = new Label(status);
        boolean isAccepted = "ACCEPTED".equals(status) || "APPROVED".equals(status);
        statusBadge.setStyle(
                "-fx-background-color: " + (isAccepted ? "#DCFCE7" : "#FEF9C3") + ";" +
                        "-fx-text-fill: " + (isAccepted ? "#15803D" : "#A16207") + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 3 7;" +
                        "-fx-background-radius: 6;");

        statsBox.getChildren().addAll(voteTallyBox, statusBadge);
        row.getChildren().addAll(avatar, details, spacer, statsBox);
        com.electrovotesuperx.utils.UIAnimationHelper.addScaleHover(row, 1.015);
        return row;
    }
}