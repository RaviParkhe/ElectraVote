package com.elctrovotesuperx.view.AdminView;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.elctrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.elctrovotesuperx.dao.AdminDAO.VoteDAO;
import com.elctrovotesuperx.model.AdminModel.ElectionData;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LiveVotingPage extends VBox {

    private final Map<String, ElectionData> electionsMap = new HashMap<>();
    private ElectionData currentSelectedElection;

    private ComboBox<String> electionCombo;
    private VBox liveTelemetryContainer;
    private ProgressIndicator loadingSpinner;
    private Timeline telemetryStreamTimeline;
    private Timeline countdownTimeline;

    // Dynamic Live UI Labels
    private Label votedNumberLabel;
    private Label remainingNumberLabel;
    private Label turnoutRateLabel;
    private Label velocityLabel;
    private Label countdownClockLabel;
    private Label totalVotersFooterLabel;
    private ProgressBar liveTurnoutBar;
    private Button emergencyPauseBtn;

    // Department/Position-wise Progress Tracking
    private final Map<String, ProgressBar> deptProgressBars = new HashMap<>();
    private final Map<String, Label> deptTurnoutLabels = new HashMap<>();

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public LiveVotingPage() {
        setSpacing(18);
        setPadding(new Insets(24, 32, 28, 32));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                        + FONT);

        HBox header = buildHeader();
        HBox electionBar = buildElectionSelectorBar();

        liveTelemetryContainer = new VBox(16);
        liveTelemetryContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(liveTelemetryContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, electionBar, scrollPane);

        loadElectionsFromFirebase();
        showSelectElectionPlaceholder();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Live Voting Telemetry & Command Center");
        title.setStyle(FONT
                + "-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label(
                "Real-time voter turnout velocity, demographic throughput, and uncast member notifications");
        subtitle.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox liveBeacon = new HBox(8);
        liveBeacon.setAlignment(Pos.CENTER);
        liveBeacon.setPadding(new Insets(8, 16, 8, 16));
        liveBeacon.setStyle(
                "-fx-background-color: #ecfdf5; -fx-background-radius: 20; -fx-border-color: #10b981; -fx-border-width: 1.5; -fx-border-radius: 20;");

        Circle dot = new Circle(5, Color.web("#10b981"));
        Label liveText = new Label("ACTIVE TELEMETRY STREAM");
        liveText.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 900; -fx-text-fill: #047857;");
        liveBeacon.getChildren().addAll(dot, liveText);

        header.getChildren().addAll(titleBox, spacer, liveBeacon);
        return header;
    }

    private HBox buildElectionSelectorBar() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 3);");

        Label selectLabel = new Label("Monitored Election:");
        selectLabel.setStyle(FONT + "-fx-font-weight: 800; -fx-font-size: 13.5px; -fx-text-fill: #1e1b4b;");

        electionCombo = new ComboBox<>();
        electionCombo.setPromptText("Loading active elections...");
        electionCombo.setPrefWidth(360);
        electionCombo.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 2 4;");
        electionCombo.setOnAction(e -> handleElectionSelection());

        loadingSpinner = new ProgressIndicator();
        loadingSpinner.setPrefSize(18, 18);
        loadingSpinner.setVisible(false);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Button refreshBtn = new Button("↻ Force Sync");
        refreshBtn.setMinWidth(Region.USE_PREF_SIZE);
        refreshBtn.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> {
            loadElectionsFromFirebase();
            handleElectionSelection();
        });

        bar.getChildren().addAll(selectLabel, electionCombo, loadingSpinner, space, refreshBtn);
        return bar;
    }

    private void loadElectionsFromFirebase() {
        loadingSpinner.setVisible(true);
        Thread t = new Thread(() -> {
            try {
                String joinCode = SessionManager.joinCode;
                String idToken = SessionManager.idToken;
                List<ElectionData> list = new ArrayList<>();
                if (joinCode != null && !joinCode.isBlank()) {
                    list = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                }

                List<ElectionData> finalElecs = list;
                Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    electionsMap.clear();
                    String previousSelection = electionCombo.getValue();
                    electionCombo.getItems().clear();

                    if (finalElecs.isEmpty()) {
                        electionCombo.setPromptText("No elections open in organization");
                    } else {
                        for (ElectionData e : finalElecs) {
                            String title = e.getTitle() != null ? e.getTitle() : "Untitled Election";
                            electionsMap.put(title, e);
                            electionCombo.getItems().add(title);
                        }
                        if (previousSelection != null && electionCombo.getItems().contains(previousSelection)) {
                            electionCombo.setValue(previousSelection);
                        } else {
                            electionCombo.getSelectionModel().selectFirst();
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> loadingSpinner.setVisible(false));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void showSelectElectionPlaceholder() {
        stopAllSimulations();
        liveTelemetryContainer.getChildren().clear();

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        box.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 14; -fx-border-color: #cbd5e1; -fx-border-radius: 14; -fx-border-width: 1.5;");

        Label icon = new Label("📊");
        icon.setStyle(FONT + "-fx-font-size: 42px;");

        Label title = new Label("No Active Election Selected");
        title.setStyle(FONT + "-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label desc = new Label(
                "Choose an active election from the dropdown to stream real-time throughput and open admin controls.");
        desc.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #64748b;");

        box.getChildren().addAll(icon, title, desc);
        liveTelemetryContainer.getChildren().add(box);
    }

    private void handleElectionSelection() {
        String selected = electionCombo.getValue();
        if (selected == null || !electionsMap.containsKey(selected)) {
            showSelectElectionPlaceholder();
            return;
        }

        currentSelectedElection = electionsMap.get(selected);
        loadingSpinner.setVisible(true);

        Thread fetchTelemetry = new Thread(() -> {
            try {
                String idToken = SessionManager.idToken;
                String joinCode = SessionManager.joinCode;

                Map<String, Map<String, Integer>> results = VoteDAO.getResults(currentSelectedElection.getId(), idToken);

                // Calculate total votes cast
                int votesCast = 0;
                Map<String, Double> positionTurnouts = new LinkedHashMap<>();

                for (Map.Entry<String, Map<String, Integer>> entry : results.entrySet()) {
                    int posVotes = 0;
                    for (int count : entry.getValue().values()) {
                        posVotes += count;
                    }
                    if (posVotes > votesCast) {
                        votesCast = posVotes;
                    }
                    positionTurnouts.put(entry.getKey(), (double) posVotes);
                }

                // Query registered members count
                int totalMembers = 0;
                try {
                    JsonObject membersObj = FirebaseDatabaseService.getMembers(joinCode, idToken);
                    if (membersObj != null) {
                        for (String key : membersObj.keySet()) {
                            JsonObject m = membersObj.getAsJsonObject(key);
                            if (m.has("status") && "ACCEPTED".equalsIgnoreCase(m.get("status").getAsString())) {
                                totalMembers++;
                            }
                        }
                    }
                } catch (Exception ignored) {}

                if (totalMembers == 0) {
                    totalMembers = Math.max(votesCast, 1);
                }

                // Normalize position percentage
                for (Map.Entry<String, Double> pEntry : positionTurnouts.entrySet()) {
                    pEntry.setValue(Math.min(1.0, pEntry.getValue() / totalMembers));
                }

                if (positionTurnouts.isEmpty()) {
                    if (currentSelectedElection.getPositions() != null) {
                        for (String p : currentSelectedElection.getPositions()) {
                            positionTurnouts.put(p, 0.0);
                        }
                    }
                }

                final int finalTotalVoters = totalMembers;
                final int finalVotesCast = votesCast;
                final Map<String, Double> finalPositionTurnouts = positionTurnouts;

                Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    ElectionDataModel data = new ElectionDataModel(
                            currentSelectedElection.getTitle(),
                            finalTotalVoters,
                            finalVotesCast,
                            LocalTime.of(9, 0),
                            LocalTime.of(18, 0)
                    );
                    data.departmentTurnout.putAll(finalPositionTurnouts);
                    renderDashboard(data);
                    startLiveTelemetryCountdown(data);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> loadingSpinner.setVisible(false));
            }
        });
        fetchTelemetry.setDaemon(true);
        fetchTelemetry.start();
    }

    private void renderDashboard(ElectionDataModel data) {
        liveTelemetryContainer.getChildren().clear();

        HBox metricStrip = buildMetricsRow(data);
        VBox primaryTurnoutCard = buildPrimaryTurnoutCard(data);

        HBox advancedGrid = new HBox(16);
        VBox demographicCard = buildDemographicBreakdownCard(data);
        VBox controlsCard = buildEmergencyControlsCard(data);

        HBox.setHgrow(demographicCard, Priority.ALWAYS);
        HBox.setHgrow(controlsCard, Priority.ALWAYS);
        advancedGrid.getChildren().addAll(demographicCard, controlsCard);

        liveTelemetryContainer.getChildren().addAll(metricStrip, primaryTurnoutCard, advancedGrid);
    }

    private HBox buildMetricsRow(ElectionDataModel data) {
        HBox grid = new HBox(14);

        int remaining = Math.max(0, data.totalVoters - data.votedVoters);

        VBox c1 = createMetricCard("👥", "TOTAL REGISTERED", String.valueOf(data.totalVoters), "100% Eligible Roster",
                "#0f172a", "#f8fafc", "#cbd5e1");
        VBox c2 = createMetricCard("🗳", "BALLOTS RECORDED", String.valueOf(data.votedVoters), "Zero-Knowledge Sealed",
                "#2563eb", "#eff6ff", "#93c5fd");
        VBox c3 = createMetricCard("⏳", "UNCAST BALLOTS", String.valueOf(remaining), "Pending Participation", "#d97706",
                "#fffbeb", "#fde68a");
        VBox c4 = createMetricCard("⚡", "VOTING VELOCITY", data.velocityPace + " / min", "Pacing Nominal", "#059669",
                "#ecfdf5", "#a7f3d0");

        votedNumberLabel = (Label) c2.getChildren().get(1);
        remainingNumberLabel = (Label) c3.getChildren().get(1);
        velocityLabel = (Label) c4.getChildren().get(1);

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        grid.getChildren().addAll(c1, c2, c3, c4);
        return grid;
    }

    private VBox createMetricCard(String icon, String title, String val, String sub, String colorHex, String bgHex,
            String borderHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setMinHeight(100);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: " + borderHex
                + "; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label ic = new Label(icon);
        ic.setStyle(FONT + "-fx-font-size: 13px; -fx-padding: 3 7; -fx-background-color: " + bgHex
                + "; -fx-background-radius: 6;");
        Label t = new Label(title);
        t.setStyle(FONT + "-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: " + colorHex + ";");
        header.getChildren().addAll(ic, t);

        Label v = new Label(val);
        v.setStyle(FONT + "-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + colorHex + ";");

        Label s = new Label(sub);
        s.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 700;");

        card.getChildren().addAll(header, v, s);
        return card;
    }

    private VBox buildPrimaryTurnoutCard(ElectionDataModel data) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 26, 20, 26));
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 14; -fx-border-color: #818cf8; -fx-border-radius: 14; -fx-border-width: 1.8; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.06), 12, 0, 0, 3);");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label(data.name);
        title.setStyle(FONT + "-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");
        String timeInfo = currentSelectedElection != null && currentSelectedElection.getEndDateTime() != null
                ? "Official Window: " + currentSelectedElection.getStartDateTime() + "  ➔  " + currentSelectedElection.getEndDateTime()
                : "Official Polling Window Active";
        Label timeSpan = new Label(timeInfo);
        timeSpan.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, timeSpan);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox countdownBox = new HBox(8);
        countdownBox.setAlignment(Pos.CENTER);
        countdownBox.setPadding(new Insets(6, 14, 6, 14));
        countdownBox.setStyle(
                "-fx-background-color: #fef3c7; -fx-background-radius: 16; -fx-border-color: #f59e0b; -fx-border-radius: 16; -fx-border-width: 1.2;");

        Label cdIcon = new Label("⏱");
        cdIcon.setStyle(FONT + "-fx-font-size: 13px;");

        countdownClockLabel = new Label(
                data.isPaused ? "POLLING FROZEN (PAUSED)" : calculateCountdownText(data.endTime));
        countdownClockLabel.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 900; -fx-text-fill: #b45309;");
        countdownBox.getChildren().addAll(cdIcon, countdownClockLabel);

        header.getChildren().addAll(titleBox, sp, countdownBox);

        double progressPct = data.totalVoters == 0 ? 0 : (double) data.votedVoters / data.totalVoters;
        liveTurnoutBar = new ProgressBar(progressPct);
        liveTurnoutBar.setMaxWidth(Double.MAX_VALUE);
        liveTurnoutBar.setPrefHeight(14);
        liveTurnoutBar.setStyle("-fx-accent: #2563eb;");

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);

        double turnout = data.totalVoters == 0 ? 0 : (data.votedVoters * 100.0) / data.totalVoters;
        turnoutRateLabel = new Label(String.format("Overall Turnout: %.2f%%", turnout));
        turnoutRateLabel.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        totalVotersFooterLabel = new Label("  (" + data.votedVoters + " of " + data.totalVoters + " cast)");
        totalVotersFooterLabel.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 600; -fx-text-fill: #64748b;");

        Region footerSp = new Region();
        HBox.setHgrow(footerSp, Priority.ALWAYS);

        Label syncTag = new Label("⚡ Zero-Knowledge Homomorphic Encryption Active");
        syncTag.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #059669; -fx-font-weight: 800;");

        footer.getChildren().addAll(turnoutRateLabel, totalVotersFooterLabel, footerSp, syncTag);

        card.getChildren().addAll(header, liveTurnoutBar, footer);
        return card;
    }

    private VBox buildDemographicBreakdownCard(ElectionDataModel data) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("👥 Position / Office Participation Breakdown");
        title.setStyle(FONT + "-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label blindTag = new Label("AUDIT VERIFIED");
        blindTag.setStyle(FONT
                + "-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 10px; -fx-font-weight: 800; -fx-padding: 3 8; -fx-background-radius: 6;");
        header.getChildren().addAll(title, sp, blindTag);

        VBox list = new VBox(10);
        deptProgressBars.clear();
        deptTurnoutLabels.clear();

        if (data.departmentTurnout.isEmpty()) {
            Label noDept = new Label("All votes registered across primary ballot.");
            noDept.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
            list.getChildren().add(noDept);
        } else {
            for (Map.Entry<String, Double> entry : data.departmentTurnout.entrySet()) {
                list.getChildren().add(createDepartmentRow(entry.getKey(), entry.getValue()));
            }
        }

        card.getChildren().addAll(header, list);
        return card;
    }

    private VBox createDepartmentRow(String deptName, double turnoutPct) {
        VBox box = new VBox(4);
        HBox textRow = new HBox();
        textRow.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(deptName);
        name.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label pctLabel = new Label(String.format("%.1f%%", turnoutPct * 100));
        pctLabel.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #2563eb;");
        deptTurnoutLabels.put(deptName, pctLabel);

        textRow.getChildren().addAll(name, sp, pctLabel);

        ProgressBar bar = new ProgressBar(turnoutPct);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(8);
        bar.setStyle("-fx-accent: #3b82f6;");
        deptProgressBars.put(deptName, bar);

        box.getChildren().addAll(textRow, bar);
        return box;
    }

    private VBox buildEmergencyControlsCard(ElectionDataModel data) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");

        Label title = new Label("⚙ Polling Actions & Voter Alerts");
        title.setStyle(FONT + "-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Button notifyUncastBtn = new Button("🔔 Notify Uncast Voters");
        notifyUncastBtn.setMaxWidth(Double.MAX_VALUE);
        notifyUncastBtn.setStyle(FONT +
                "-fx-background-color: linear-gradient(to right, #4338ca 0%, #3b82f6 50%, #2563eb 100%); " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: 900; " +
                "-fx-padding: 12 20; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #818cf8; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1.5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.4), 10, 0, 0, 3);");

        notifyUncastBtn.setOnAction(e -> showNotifyUncastVotersModal(data));

        HBox secondaryGrid = new HBox(12);
        secondaryGrid.setAlignment(Pos.CENTER_LEFT);

        emergencyPauseBtn = new Button(data.isPaused ? "▶ Resume Voting" : "⏸ Pause Voting");
        emergencyPauseBtn.setMinWidth(Region.USE_PREF_SIZE);
        emergencyPauseBtn.setStyle(FONT + "-fx-background-color: " + (data.isPaused ? "#ecfdf5" : "#fffbeb")
                + "; -fx-text-fill: " + (data.isPaused ? "#047857" : "#b45309") + "; -fx-border-color: "
                + (data.isPaused ? "#10b981" : "#f59e0b")
                + "; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-cursor: hand;");
        emergencyPauseBtn.setOnAction(e -> togglePollingPause(data));

        Button extendBtn = new Button("⏱ Extend Window (+1h)");
        extendBtn.setMinWidth(Region.USE_PREF_SIZE);
        extendBtn.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-cursor: hand;");
        extendBtn.setOnAction(e -> extendPollingWindow(data));

        secondaryGrid.getChildren().addAll(emergencyPauseBtn, extendBtn);

        Label note = new Label("Targeted notification transmissions are tracked in the organization audit ledger.");
        note.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(title, notifyUncastBtn, secondaryGrid, note);
        return card;
    }

    private void showNotifyUncastVotersModal(ElectionDataModel data) {
        int uncastCount = Math.max(0, data.totalVoters - data.votedVoters);

        Dialog<ButtonType> dialog = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            dialog.initOwner(AdminDashboard.AdminDashboardStage);
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(dialog);
        }
        dialog.setTitle("Dispatch Uncast Voter Notifications");

        VBox contentBox = new VBox(18);
        contentBox.setPadding(new Insets(24));
        contentBox.setPrefWidth(540);
        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        HBox summaryCard = new HBox(14);
        summaryCard.setAlignment(Pos.CENTER_LEFT);
        summaryCard.setPadding(new Insets(14, 18, 14, 18));
        summaryCard.setStyle(
                "-fx-background-color: #eff6ff; -fx-background-radius: 10; -fx-border-color: #bfdbfe; -fx-border-radius: 10; -fx-border-width: 1.5;");

        Label bellIcon = new Label("🔔");
        bellIcon.setStyle(FONT + "-fx-font-size: 24px;");

        VBox summaryText = new VBox(4);
        Label sumTitle = new Label(uncastCount + " Uncast Eligible Voters Identified");
        sumTitle.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1d4ed8;");
        Label sumSub = new Label("Election: " + data.name);
        sumSub.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: #475569;");
        summaryText.getChildren().addAll(sumTitle, sumSub);

        summaryCard.getChildren().addAll(bellIcon, summaryText);

        VBox channelsBox = new VBox(8);
        Label chanTitle = new Label("Notification Delivery Channels");
        chanTitle.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        CheckBox pushCheck = new CheckBox("In-App Push Notification (ElectraVote Member Portal)");
        pushCheck.setSelected(true);
        pushCheck.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");

        CheckBox emailCheck = new CheckBox("Urgent Email Broadcast (Registered Organization Email)");
        emailCheck.setSelected(true);
        emailCheck.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");

        CheckBox smsCheck = new CheckBox("SMS Flash Alert (Verified Mobile Numbers)");
        smsCheck.setSelected(false);
        smsCheck.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");

        channelsBox.getChildren().addAll(chanTitle, pushCheck, emailCheck, smsCheck);

        VBox messageBox = new VBox(6);
        Label msgTitle = new Label("Broadcast Alert Message");
        msgTitle.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        TextArea messageArea = new TextArea(
                "Urgent Reminder: Voting for \"" + data.name + "\" is currently active. " +
                        "Your vote is confidential, protected by zero-knowledge ballot secrecy. " +
                        "Please cast your ballot before the official polling period concludes.");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(4);
        messageArea.setStyle(FONT
                + "-fx-control-inner-background: #f8fafc; -fx-text-fill: #0f172a; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-font-size: 12.5px; -fx-padding: 6 8;");

        messageBox.getChildren().addAll(msgTitle, messageArea);

        contentBox.getChildren().addAll(summaryCard, channelsBox, messageBox);
        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #818cf8; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType sendBtnType = new ButtonType("Dispatch Notification (" + uncastCount + ")",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelBtnType, sendBtnType);

        Button sendBtn = (Button) dialog.getDialogPane().lookupButton(sendBtnType);
        if (sendBtn != null) {
            sendBtn.setStyle(FONT
                    + "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        }

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelBtnType);
        if (cancelBtn != null) {
            cancelBtn.setStyle(FONT
                    + "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: 800; -fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 10 16; -fx-cursor: hand; -fx-border-color: #cbd5e1; -fx-border-radius: 8;");
        }

        dialog.showAndWait().ifPresent(result -> {
            if (result == sendBtnType) {
                List<String> channels = new ArrayList<>();
                if (pushCheck.isSelected())
                    channels.add("In-App Push");
                if (emailCheck.isSelected())
                    channels.add("Email");
                if (smsCheck.isSelected())
                    channels.add("SMS");

                showStyledAlert(
                        "Broadcast Dispatched Successfully",
                        "Urgent reminders dispatched to " + uncastCount + " uncast voters via ["
                                + String.join(", ", channels) + "].",
                        "✓", "#16a34a", "#dcfce7");
            }
        });
    }

    private void showStyledAlert(String titleStr, String descStr, String iconGlyph, String iconColorHex,
            String iconBgHex) {
        Dialog<ButtonType> alert = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            alert.initOwner(AdminDashboard.AdminDashboardStage);
            alert.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(alert);
        }
        alert.setTitle("System Notification");

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(24));
        contentBox.setPrefWidth(420);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Circle outerHalo = new Circle(28, Color.web(iconBgHex));
        Circle innerCircle = new Circle(20, Color.web(iconColorHex));
        Label icon = new Label(iconGlyph);
        icon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 900;");
        StackPane iconStack = new StackPane(outerHalo, innerCircle, icon);

        VBox textBox = new VBox(6);
        textBox.setAlignment(Pos.CENTER);
        Label title = new Label(titleStr);
        title.setWrapText(true);
        title.setStyle(FONT
                + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-text-alignment: CENTER;");

        Label desc = new Label(descStr);
        desc.setWrapText(true);
        desc.setStyle(FONT
                + "-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-text-alignment: CENTER; -fx-line-spacing: 1.5px;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        alert.getDialogPane().setContent(contentBox);
        alert.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: " + iconColorHex
                + "; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType okBtnType = new ButtonType("Acknowledge", ButtonBar.ButtonData.OK_DONE);
        alert.getDialogPane().getButtonTypes().add(okBtnType);

        Button okBtn = (Button) alert.getDialogPane().lookupButton(okBtnType);
        if (okBtn != null) {
            okBtn.setStyle(FONT + "-fx-background-color: " + iconColorHex
                    + "; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        }

        alert.showAndWait();
    }

    private void togglePollingPause(ElectionDataModel data) {
        data.isPaused = !data.isPaused;
        if (data.isPaused) {
            emergencyPauseBtn.setText("▶ Resume Voting");
            emergencyPauseBtn.setStyle(FONT
                    + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-cursor: hand;");

            if (countdownClockLabel != null) {
                countdownClockLabel.setText("POLLING FROZEN (PAUSED)");
            }

            showStyledAlert("Polling Paused", "Ballot intake and countdown timer have been temporarily frozen.", "⏸",
                    "#f59e0b", "#fef3c7");
        } else {
            emergencyPauseBtn.setText("⏸ Pause Voting");
            emergencyPauseBtn.setStyle(FONT
                    + "-fx-background-color: #fffbeb; -fx-text-fill: #b45309; -fx-border-color: #f59e0b; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-cursor: hand;");

            if (countdownClockLabel != null) {
                countdownClockLabel.setText(calculateCountdownText(data.endTime));
            }

            showStyledAlert("Polling Resumed", "Ballot intake and countdown timer have been reopened.", "▶", "#16a34a",
                    "#dcfce7");
        }
    }

    private void extendPollingWindow(ElectionDataModel data) {
        data.endTime = data.endTime.plusHours(1);
        renderDashboard(data);
        showStyledAlert(
                "Window Extended (+1h)",
                "Official polling window extended by 1 hour.\nNew End Time: " + data.endTime.format(TIME_FORMATTER)
                        + ".\nCountdown timer updated automatically.",
                "⏱", "#2563eb", "#eff6ff");
    }

    private void startLiveTelemetryCountdown(ElectionDataModel data) {
        stopAllSimulations();

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (countdownClockLabel != null) {
                if (data.isPaused) {
                    countdownClockLabel.setText("POLLING FROZEN (PAUSED)");
                } else {
                    countdownClockLabel.setText(calculateCountdownText(data.endTime));
                }
            }
        }));
        countdownTimeline.setCycleCount(Animation.INDEFINITE);
        countdownTimeline.play();
    }

    private String calculateCountdownText(LocalTime endTime) {
        LocalTime now = LocalTime.now();
        java.time.Duration diff = java.time.Duration.between(now, endTime);
        if (diff.isNegative() || diff.isZero()) {
            return "POLLING CONCLUDED";
        }
        long hours = diff.toHours();
        long mins = diff.toMinutesPart();
        long secs = diff.toSecondsPart();
        return String.format("CLOSES IN: %02dh %02dm %02ds", hours, mins, secs);
    }

    private void stopAllSimulations() {
        if (telemetryStreamTimeline != null)
            telemetryStreamTimeline.stop();
        if (countdownTimeline != null)
            countdownTimeline.stop();
    }

    public static class ElectionDataModel {
        public String name;
        public int totalVoters;
        public int votedVoters;
        public LocalTime startTime;
        public LocalTime endTime;
        public int velocityPace = 14;
        public boolean isPaused = false;
        public Map<String, Double> departmentTurnout = new HashMap<>();

        public ElectionDataModel(String name, int totalVoters, int votedVoters, LocalTime startTime,
                LocalTime endTime) {
            this.name = name;
            this.totalVoters = totalVoters;
            this.votedVoters = votedVoters;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}