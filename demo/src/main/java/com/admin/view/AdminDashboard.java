package com.admin.view;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboard extends Application {

    public static Stage AdminDashboardStage;
    public static Scene AdminDashboardScene;

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    private static final String NAV_DEFAULT = FONT +
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-size: 13.5px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;";

    private static final String NAV_ACTIVE = FONT +
            "-fx-background-color: #3b82f6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;";

    @Override
    public void start(Stage stage) {
        AdminDashboardStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0b1120; " + FONT);

        // =========================================================================
        // 1. SIDEBAR WITH ICONS
        // =========================================================================
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #0b1329;");

        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 8, 12, 8));

        Circle brandCircle = new Circle(14, Color.web("#3b82f6"));
        Label brandLetter = new Label("⚡");
        brandLetter.setStyle(FONT + "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        StackPane brandLogo = new StackPane(brandCircle, brandLetter);

        Label logo = new Label("ElectraVote");
        logo.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: 800;");
        brand.getChildren().addAll(brandLogo, logo);

        VBox orgBadge = new VBox(2);
        orgBadge.setPadding(new Insets(10, 12, 10, 12));
        orgBadge.setStyle("-fx-background-color: #17223b; -fx-background-radius: 8;");
        Label orgName = new Label("ABC College");
        orgName.setStyle(FONT + "-fx-text-fill: #f8fafc; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label orgRole = new Label("Tenant Admin");
        orgRole.setStyle(FONT + "-fx-text-fill: #38bdf8; -fx-font-size: 11px; -fx-font-weight: 600;");
        orgBadge.getChildren().addAll(orgName, orgRole);

        // Navigation Buttons
        Button dashboardBtn = new Button("⊞  Dashboard");
        Button electionsBtn = new Button("🗳  Elections");
        Button votersBtn = new Button("👥  Voters");
        Button candidatesBtn = new Button("👤  Candidates");
        Button liveVotingBtn = new Button("📊  Live Voting");
        Button resultsBtn = new Button("🏆  Results");
        Button reportsBtn = new Button("📑  Reports");
        Button settingsBtn = new Button("⚙  Settings");
        Button logoutBtn = new Button("🚪  Logout");

        Button[] buttons = {dashboardBtn, electionsBtn, votersBtn, candidatesBtn, liveVotingBtn, resultsBtn, reportsBtn, settingsBtn, logoutBtn};

        for (Button btn : buttons) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle(NAV_DEFAULT);
            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().contains("-fx-background-color: #3b82f6;")) {
                    btn.setStyle(NAV_DEFAULT + "-fx-background-color: #17223b; -fx-text-fill: #ffffff;");
                }
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains("-fx-background-color: #3b82f6;")) {
                    btn.setStyle(NAV_DEFAULT);
                }
            });
        }
        dashboardBtn.setStyle(NAV_ACTIVE);

        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        logoutBtn.setStyle(NAV_DEFAULT + "-fx-text-fill: #f87171;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(NAV_DEFAULT + "-fx-background-color: #450a0a; -fx-text-fill: #fca5a5;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(NAV_DEFAULT + "-fx-text-fill: #f87171;"));

        sidebar.getChildren().addAll(
                brand, orgBadge, new Region() {{ setPrefHeight(10); }},
                dashboardBtn, electionsBtn, votersBtn, candidatesBtn,
                liveVotingBtn, resultsBtn, reportsBtn, settingsBtn,
                sidebarSpacer, logoutBtn
        );

        // =========================================================================
        // 2. TOP BAR
        // =========================================================================
        HBox topBar = new HBox(16);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-border-color: #cbd5e1; -fx-border-width: 0 0 1.5 0;");

        VBox titleBox = new VBox(2);
        Label pageTitle = new Label("Electoral Command Center");
        pageTitle.setStyle(FONT + "-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        Label pageSubtitle = new Label("Online voting telemetry, turnout monitoring & election overview");
        pageSubtitle.setStyle(FONT + "-fx-font-size: 11.5px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(pageTitle, pageSubtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍  Search elections, candidates, results...");
        searchInput.setPrefWidth(240);
        searchInput.setStyle(FONT + "-fx-background-color: #f8fafc; -fx-border-color: #94a3b8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 10; -fx-font-size: 12px; -fx-font-weight: 500;");

        // --- ANIMATED "ONLINE VOTING LIVE" PILL ---
        HBox liveIndicator = new HBox(8);
        liveIndicator.setAlignment(Pos.CENTER);
        liveIndicator.setPadding(new Insets(5, 12, 5, 12));
        liveIndicator.setStyle("-fx-background-color: #ecfdf5; -fx-background-radius: 20; -fx-border-color: #10b981; -fx-border-width: 1.5; -fx-border-radius: 20; -fx-cursor: hand;");

        Circle pulseRing = new Circle(6, Color.web("#34d399", 0.5));
        Circle liveDot = new Circle(4, Color.web("#10b981"));

        DropShadow dotGlow = new DropShadow();
        dotGlow.setColor(Color.web("#10b981", 0.8));
        dotGlow.setRadius(6);
        dotGlow.setSpread(0.4);
        liveDot.setEffect(dotGlow);

        StackPane beaconStack = new StackPane(pulseRing, liveDot);

        ScaleTransition pulseScale = new ScaleTransition(Duration.millis(1100), pulseRing);
        pulseScale.setFromX(0.8);
        pulseScale.setFromY(0.8);
        pulseScale.setToX(1.8);
        pulseScale.setToY(1.8);
        pulseScale.setAutoReverse(true);
        pulseScale.setCycleCount(Animation.INDEFINITE);
        pulseScale.play();

        FadeTransition pulseFade = new FadeTransition(Duration.millis(1100), pulseRing);
        pulseFade.setFromValue(0.8);
        pulseFade.setToValue(0.1);
        pulseFade.setAutoReverse(true);
        pulseFade.setCycleCount(Animation.INDEFINITE);
        pulseFade.play();

        Label liveText = new Label("ONLINE VOTING LIVE");
        liveText.setStyle(FONT + "-fx-text-fill: #047857; -fx-font-weight: 900; -fx-font-size: 10.5px; -fx-letter-spacing: 0.4;");

        DropShadow pillGlow = new DropShadow();
        pillGlow.setColor(Color.web("#10b981", 0.25));
        pillGlow.setRadius(8);
        pillGlow.setSpread(0.2);
        liveIndicator.setEffect(pillGlow);

        Timeline pillPulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(pillGlow.radiusProperty(), 5), new KeyValue(pillGlow.colorProperty(), Color.web("#10b981", 0.2))),
                new KeyFrame(Duration.millis(1200), new KeyValue(pillGlow.radiusProperty(), 12), new KeyValue(pillGlow.colorProperty(), Color.web("#10b981", 0.55)))
        );
        pillPulse.setAutoReverse(true);
        pillPulse.setCycleCount(Animation.INDEFINITE);
        pillPulse.play();

        liveIndicator.getChildren().addAll(beaconStack, liveText);

        HBox userBadge = new HBox(8);
        userBadge.setAlignment(Pos.CENTER_LEFT);
        userBadge.setPadding(new Insets(4, 10, 4, 10));
        userBadge.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-width: 1.2; -fx-border-radius: 20; -fx-background-radius: 20;");

        Circle avatar = new Circle(12, Color.web("#0f172a"));
        Label avText = new Label("AD");
        avText.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 9.5px; -fx-font-weight: bold;");
        StackPane avPane = new StackPane(avatar, avText);

        Label userRoleLabel = new Label("Administrator");
        userRoleLabel.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        userBadge.getChildren().addAll(avPane, userRoleLabel);

        topBar.getChildren().addAll(titleBox, topSpacer, searchInput, liveIndicator, userBadge);

        // =========================================================================
        // 3. CENTER CONTENT
        // =========================================================================
        BorderPane center = new BorderPane();
        center.setTop(topBar);

        ElectionPage electionPage = new ElectionPage();
        CandidatePage candidatePage = new CandidatePage();
        VoterPage voterPage = new VoterPage();
        LiveVotingPage liveVotingPage = new LiveVotingPage();
        ResultPage resultPage = new ResultPage();

        ScrollPane dashboardScroll = new ScrollPane();
        dashboardScroll.setFitToWidth(true);
        dashboardScroll.setStyle("-fx-background-color: transparent; -fx-background: linear-gradient(to bottom right, #f1f5f9 0%, #e0e7ff 40%, #ede9fe 75%, #fce7f3 100%);");

        VBox content = buildModernCenterContent(stage);
        dashboardScroll.setContent(content);

        center.setCenter(dashboardScroll);
        root.setLeft(sidebar);
        root.setCenter(center);

        dashboardBtn.setOnAction(event -> {
            setActiveNav(dashboardBtn, buttons);
            center.setCenter(dashboardScroll);
        });

        electionsBtn.setOnAction(e -> {
            setActiveNav(electionsBtn, buttons);
            center.setCenter(electionPage);
        });

        candidatesBtn.setOnAction(e -> {
            setActiveNav(candidatesBtn, buttons);
            center.setCenter(candidatePage);
        });

        votersBtn.setOnAction(e -> {
            setActiveNav(votersBtn, buttons);
            center.setCenter(voterPage);
        });

        liveVotingBtn.setOnAction(e -> {
            setActiveNav(liveVotingBtn, buttons);
            center.setCenter(liveVotingPage);
        });

        resultsBtn.setOnAction(e -> {
            setActiveNav(resultsBtn, buttons);
            center.setCenter(resultPage);
        });

        Scene scene = new Scene(root, 1280, 820);
        AdminDashboardScene = scene;

        AdminDashboardStage.setTitle("ElectraVote SaaS Admin Portal");
        AdminDashboardStage.setScene(scene);
        AdminDashboardStage.show();
    }

    private void setActiveNav(Button target, Button[] all) {
        for (Button btn : all) {
            btn.setStyle(NAV_DEFAULT);
        }
        target.setStyle(NAV_ACTIVE);
    }

    private VBox buildModernCenterContent(Stage currentStage) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 26, 26, 26));

        // 1. Hero Organization Status Banner (Slimmed Down)
        HBox heroBanner = new HBox(16);
        heroBanner.setAlignment(Pos.CENTER_LEFT);
        heroBanner.setPadding(new Insets(14, 20, 14, 20));
        heroBanner.setStyle("-fx-background-color: linear-gradient(to right, #0f172a 0%, #1e1b4b 60%, #312e81 100%); " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #6366f1; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 1.2; " +
                "-fx-effect: dropshadow(gaussian, rgba(49,46,129,0.25), 12, 0, 0, 4);");

        VBox heroText = new VBox(3);
        Label heroTitle = new Label("Welcome back to ABC College Portal");
        heroTitle.setStyle(FONT + "-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: #ffffff;");
        Label heroSub = new Label("Online elections are actively receiving cast ballots. Double-voting checks and cryptographic locks are nominal.");
        heroSub.setStyle(FONT + "-fx-font-size: 11.5px; -fx-text-fill: #a5b4fc; -fx-font-weight: 500;");
        heroText.getChildren().addAll(heroTitle, heroSub);

        Region heroSpacer = new Region();
        HBox.setHgrow(heroSpacer, Priority.ALWAYS);

        Button quickAuditBtn = new Button("⚡ Generate Turnout Report");
        quickAuditBtn.setStyle(FONT + "-fx-background-color: linear-gradient(to right, #3b82f6, #6366f1); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: 800; " +
                "-fx-font-size: 11.5px; " +
                "-fx-padding: 8 16; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #818cf8; " +
                "-fx-border-radius: 8; " +
                "-fx-border-width: 1; " +
                "-fx-cursor: hand;");
        quickAuditBtn.setOnAction(e -> showTurnoutReportModal(currentStage));

        heroBanner.getChildren().addAll(heroText, heroSpacer, quickAuditBtn);

        // 2. Metric Cards (Compact 85px Height)
        HBox metricsGrid = new HBox(12);

        VBox cardElections = buildColorfulMetricCard("🗳", "TOTAL ELECTIONS", "12", "+2 Scheduled", "#1d4ed8", "#eff6ff", "#3b82f6", "#93c5fd");
        VBox cardVoters = buildColorfulMetricCard("👥", "TOTAL VOTERS", "4,582", "98.4% Active Members", "#0f172a", "#f8fafc", "#475569", "#cbd5e1");
        VBox cardVotes = buildColorfulMetricCard("🔒", "VOTES CAST", "2,947", "Zero-Knowledge Stored", "#047857", "#ecfdf5", "#10b981", "#6ee7b7");
        VBox cardTurnout = buildColorfulMetricCard("📈", "OVERALL TURNOUT", "64.28%", "Target: 70.00%", "#6d28d9", "#f5f3ff", "#8b5cf6", "#c4b5fd");

        HBox.setHgrow(cardElections, Priority.ALWAYS);
        HBox.setHgrow(cardVoters, Priority.ALWAYS);
        HBox.setHgrow(cardVotes, Priority.ALWAYS);
        HBox.setHgrow(cardTurnout, Priority.ALWAYS);

        metricsGrid.getChildren().addAll(cardElections, cardVoters, cardVotes, cardTurnout);

        // 3. Lower Section (Compact 240px Card Containers)
        HBox lowerGrid = new HBox(14);

        VBox electionOverviewCard = buildScrollableElectionOverviewCard();
        VBox ongoingCard = buildOngoingElectionsCard();

        HBox.setHgrow(electionOverviewCard, Priority.ALWAYS);
        HBox.setHgrow(ongoingCard, Priority.ALWAYS);
        lowerGrid.getChildren().addAll(electionOverviewCard, ongoingCard);

        content.getChildren().addAll(heroBanner, metricsGrid, lowerGrid);
        return content;
    }

    private VBox buildColorfulMetricCard(String icon, String title, String val, String sub, String themeColor, String iconBg, String borderBase, String borderAccent) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setMinHeight(85);
        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + borderBase + "; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1.4; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);");

        HBox header = new HBox(6);
        header.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(FONT + "-fx-font-size: 12px; -fx-padding: 3 6; -fx-background-color: " + iconBg + "; -fx-border-color: " + borderAccent + "; -fx-border-radius: 6; -fx-border-width: 1; -fx-background-radius: 6;");

        Label t = new Label(title);
        t.setStyle(FONT + "-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-text-fill: " + themeColor + "; -fx-letter-spacing: 0.3;");
        header.getChildren().addAll(iconLabel, t);

        Label v = new Label(val);
        v.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + themeColor + ";");

        Label s = new Label(sub);
        s.setStyle(FONT + "-fx-font-size: 10px; -fx-text-fill: #475569; -fx-font-weight: 700;");

        card.getChildren().addAll(header, v, s);
        return card;
    }

    private VBox buildScrollableElectionOverviewCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setMinHeight(240);
        card.setPrefHeight(240);
        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #818cf8; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 10, 0, 0, 3);");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("🗳  Featured Elections Directory");
        title.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label activeBadge = new Label("ORGANIZATION ELECTIONS");
        activeBadge.setStyle(FONT + "-fx-background-color: linear-gradient(to right, #4338ca, #312e81); -fx-text-fill: #ffffff; -fx-font-size: 9.5px; -fx-font-weight: 800; -fx-padding: 3 8; -fx-background-radius: 14;");
        header.getChildren().addAll(title, sp, activeBadge);

        VBox electionNamesList = new VBox(6);
        electionNamesList.setPadding(new Insets(2, 4, 4, 2));

        electionNamesList.getChildren().addAll(
                buildColorfulElectionNameRow("Student Council General Election 2026", "#2563eb", "#eff6ff", "#bfdbfe", "VOTING LIVE"),
                buildColorfulElectionNameRow("Cultural Affairs & Arts Committee Election", "#7c3aed", "#f5f3ff", "#ddd6fe", "ACTIVE"),
                buildColorfulElectionNameRow("Sports & Athletics Committee Election", "#059669", "#ecfdf5", "#a7f3d0", "ACTIVE"),
                buildColorfulElectionNameRow("Computer Engineering Dept Rep Poll", "#d97706", "#fffbeb", "#fde68a", "SCHEDULED"),
                buildColorfulElectionNameRow("Faculty Executive Council Election", "#0f172a", "#f8fafc", "#e2e8f0", "UPCOMING"),
                buildColorfulElectionNameRow("Postgraduate Senate Election", "#2563eb", "#eff6ff", "#bfdbfe", "DRAFT")
        );

        ScrollPane electionScroll = new ScrollPane(electionNamesList);
        electionScroll.setFitToWidth(true);
        electionScroll.setPrefHeight(160);
        electionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        electionScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        electionScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(electionScroll, Priority.ALWAYS);

        card.getChildren().addAll(header, electionScroll);
        return card;
    }

    private HBox buildColorfulElectionNameRow(String electionName, String accentColor, String bgAccent, String borderColor, String statusText) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: " + bgAccent + "; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + borderColor + " " + borderColor + " " + borderColor + " " + accentColor + "; " +
                "-fx-border-width: 1 1 1 3.5; " +
                "-fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 4, 0, 0, 1);");

        Label name = new Label(electionName);
        name.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(statusText);
        status.setStyle(FONT + "-fx-font-size: 9.5px; -fx-font-weight: 900; -fx-text-fill: " + accentColor + "; -fx-padding: 2 6; -fx-background-color: white; -fx-border-color: " + accentColor + "; -fx-border-radius: 4; -fx-background-radius: 4;");

        row.getChildren().addAll(name, spacer, status);
        return row;
    }

    private VBox buildOngoingElectionsCard() {
        VBox ongoingCard = new VBox(10);
        ongoingCard.setPadding(new Insets(14));
        ongoingCard.setMinHeight(240);
        ongoingCard.setPrefHeight(240);
        ongoingCard.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #34d399; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(52,211,153,0.08), 10, 0, 0, 3);");

        Label ongoingTitle = new Label("📊  Active Online Turnout");
        ongoingTitle.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");

        VBox activeList = new VBox(6);
        activeList.getChildren().addAll(
                buildColorfulOngoingRow("Student Council Election", "64.28% Turnout", "LIVE", "#059669", "#ecfdf5", "#a7f3d0"),
                buildColorfulOngoingRow("Cultural Committee Election", "48.13% Turnout", "LIVE", "#059669", "#ecfdf5", "#a7f3d0"),
                buildColorfulOngoingRow("Sports Committee Election", "36.91% Turnout", "CLOSING", "#d97706", "#fffbeb", "#fde68a")
        );

        ScrollPane turnoutScroll = new ScrollPane(activeList);
        turnoutScroll.setFitToWidth(true);
        turnoutScroll.setPrefHeight(160);
        turnoutScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        turnoutScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(turnoutScroll, Priority.ALWAYS);

        ongoingCard.getChildren().addAll(ongoingTitle, turnoutScroll);
        return ongoingCard;
    }

    private HBox buildColorfulOngoingRow(String name, String meta, String badge, String badgeColor, String badgeBg, String borderColor) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: " + badgeBg + "; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + borderColor + "; " +
                "-fx-border-width: 1.2; " +
                "-fx-border-radius: 8;");

        VBox text = new VBox(2);
        Label n = new Label(name);
        n.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        Label m = new Label(meta);
        m.setStyle(FONT + "-fx-font-size: 10.5px; -fx-text-fill: #047857; -fx-font-weight: 700;");
        text.getChildren().addAll(n, m);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label b = new Label(badge);
        b.setStyle(FONT + "-fx-background-color: " + badgeColor + "; -fx-text-fill: white; -fx-font-size: 9.5px; -fx-font-weight: 900; -fx-padding: 3 7; -fx-background-radius: 4;");

        row.getChildren().addAll(text, sp, b);
        return row;
    }

    // =========================================================================
    // TURNOUT REPORT MODAL
    // =========================================================================
    private void showTurnoutReportModal(Stage ownerStage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(ownerStage);
        dialog.setTitle("ElectraVote Official Turnout Audit Report");

        VBox modalContent = new VBox(14);
        modalContent.setPadding(new Insets(20));
        modalContent.setPrefWidth(600);
        modalContent.setStyle("-fx-background-color: #ffffff; " + FONT);

        HBox reportHeader = new HBox(12);
        reportHeader.setAlignment(Pos.CENTER_LEFT);

        Circle reportIconBg = new Circle(18, Color.web("#eff6ff"));
        Label reportIcon = new Label("📊");
        reportIcon.setStyle(FONT + "-fx-font-size: 15px;");
        StackPane iconStack = new StackPane(reportIconBg, reportIcon);

        VBox titleArea = new VBox(2);
        Label title = new Label("Official Online Turnout Audit Summary");
        title.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");
        Label timestamp = new Label("Generated: " + LocalDateTime.now().format(dtf) + "  •  Tenant: ABC College");
        timestamp.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
        titleArea.getChildren().addAll(title, timestamp);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label verifiedTag = new Label("AUDITED & SEALED");
        verifiedTag.setStyle(FONT + "-fx-background-color: #ecfdf5; -fx-text-fill: #059669; -fx-font-weight: 800; -fx-font-size: 10px; -fx-padding: 4 10; -fx-background-radius: 16; -fx-border-color: #a7f3d0; -fx-border-radius: 16;");

        reportHeader.getChildren().addAll(iconStack, titleArea, headerSpacer, verifiedTag);

        HBox metricStrip = new HBox(8);
        metricStrip.getChildren().addAll(
                createReportMetricBadge("TOTAL REGISTERED", "4,582", "#0f172a", "#f8fafc"),
                createReportMetricBadge("BALLOTS CAST", "2,947", "#2563eb", "#eff6ff"),
                createReportMetricBadge("REMAINING", "1,635", "#d97706", "#fffbeb"),
                createReportMetricBadge("OVERALL TURNOUT", "64.28%", "#059669", "#ecfdf5")
        );

        VBox privacyStandardBox = new VBox(6);
        privacyStandardBox.setPadding(new Insets(10, 14, 10, 14));
        privacyStandardBox.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 10;");

        HBox privacyHeader = new HBox(6);
        privacyHeader.setAlignment(Pos.CENTER_LEFT);
        Label lockIcon = new Label("🔒");
        lockIcon.setStyle(FONT + "-fx-font-size: 12px;");
        Label privacyTitle = new Label("Ballot Privacy Standard");
        privacyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #ffffff;");
        Region privSp = new Region();
        HBox.setHgrow(privSp, Priority.ALWAYS);
        Label zeroKnowledgeTag = new Label("ZERO-KNOWLEDGE DECOUPLED");
        zeroKnowledgeTag.setStyle(FONT + "-fx-background-color: #1e293b; -fx-text-fill: #38bdf8; -fx-font-weight: 800; -fx-font-size: 9px; -fx-padding: 2 6; -fx-background-radius: 4;");
        privacyHeader.getChildren().addAll(lockIcon, privacyTitle, privSp, zeroKnowledgeTag);

        Label privacyDesc = new Label("Zero-Link Cryptographic Architecture: Voter participation records are decoupled from candidate selections in isolated collections.");
        privacyDesc.setWrapText(true);
        privacyDesc.setStyle(FONT + "-fx-font-size: 10.5px; -fx-text-fill: #94a3b8;");

        privacyStandardBox.getChildren().addAll(privacyHeader, privacyDesc);

        VBox breakdownSection = new VBox(8);
        Label breakdownTitle = new Label("Election-Wise Turnout Breakdown");
        breakdownTitle.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        VBox breakdownList = new VBox(6);
        breakdownList.getChildren().addAll(
                createModernBreakdownRow("Student Council General Election 2026", 1286, 2000, 0.6428, "#2563eb"),
                createModernBreakdownRow("Cultural Affairs Committee Election", 722, 1500, 0.4813, "#7c3aed"),
                createModernBreakdownRow("Sports & Athletics Committee Election", 443, 1200, 0.3691, "#059669"),
                createModernBreakdownRow("Faculty Executive Council Poll", 496, 882, 0.5623, "#d97706")
        );
        breakdownSection.getChildren().addAll(breakdownTitle, breakdownList);

        modalContent.getChildren().addAll(reportHeader, metricStrip, privacyStandardBox, breakdownSection);

        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff;");

        ButtonType exportButtonType = new ButtonType("Export Report (.txt)", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, closeButtonType);

        Button exportBtn = (Button) dialog.getDialogPane().lookupButton(exportButtonType);
        if (exportBtn != null) {
            exportBtn.setStyle(FONT + "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;");
        }

        Button closeBtn = (Button) dialog.getDialogPane().lookupButton(closeButtonType);
        if (closeBtn != null) {
            closeBtn.setStyle(FONT + "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: 600; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;");
        }

        dialog.showAndWait().ifPresent(response -> {
            if (response == exportButtonType) {
                exportReportToFile(ownerStage, LocalDateTime.now().format(dtf));
            }
        });
    }

    private VBox createReportMetricBadge(String label, String value, String colorHex, String bgHex) {
        VBox box = new VBox(2);
        box.setPadding(new Insets(8, 10, 8, 10));
        box.setStyle("-fx-background-color: " + bgHex + "; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label l = new Label(label);
        l.setStyle(FONT + "-fx-font-size: 9.5px; -fx-font-weight: 700; -fx-text-fill: #64748b;");

        Label v = new Label(value);
        v.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + colorHex + ";");

        box.getChildren().addAll(l, v);
        return box;
    }

    private HBox createModernBreakdownRow(String electionName, int voted, int total, double progress, String accentColor) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-border-color: #e2e8f0; -fx-border-radius: 6;");

        VBox text = new VBox(1);
        Label name = new Label(electionName);
        name.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");
        Label part = new Label(voted + " cast of " + total + " voters");
        part.setStyle(FONT + "-fx-font-size: 10px; -fx-text-fill: #64748b;");
        text.getChildren().addAll(name, part);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ProgressBar bar = new ProgressBar(progress);
        bar.setPrefWidth(90);
        bar.setPrefHeight(6);
        bar.setStyle("-fx-accent: " + accentColor + ";");

        Label perc = new Label(String.format("%.1f%%", progress * 100));
        perc.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-text-fill: " + accentColor + "; -fx-min-width: 40px; -fx-alignment: CENTER_RIGHT;");

        row.getChildren().addAll(text, spacer, bar, perc);
        return row;
    }

    private void exportReportToFile(Stage ownerStage, String timestamp) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Turnout Audit Report");
        fileChooser.setInitialFileName("ElectraVote_Turnout_Report_" + System.currentTimeMillis() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(ownerStage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("====================================================================\n");
                writer.write("                ELECTRAVOTE SaaS ELECTION PLATFORM                  \n");
                writer.write("                   OFFICIAL TURNOUT AUDIT REPORT                    \n");
                writer.write("====================================================================\n\n");
                writer.write("Tenant Organization     : ABC College\n");
                writer.write("Audit Report Timestamp  : " + timestamp + "\n");
                writer.write("Online System Status    : OPERATIONAL & NOMINAL\n");
                writer.write("Ballot Privacy Standard : Zero-Knowledge Decoupled\n\n");
                writer.write("Total Registered Voters : 4,582 Eligible Members\n");
                writer.write("Total Ballots Recorded  : 2,947 Valid Submissions\n");
                writer.write("Aggregate Turnout Rate  : 64.28%\n");
                writer.write("====================================================================\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}