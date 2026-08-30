package com.electrovotesuperx.view.QuickPollView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.dao.QuickPollDAO.QuickPollDAO;
import com.electrovotesuperx.view.Page;
import com.google.gson.JsonObject;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class QuickPoll implements Page {

    private Scene scene;
    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    // Static master registry of polls across sessions and cloud sync
    private static final Map<String, Poll> allPollsMap = new LinkedHashMap<>();
    private ProgressIndicator loadingIndicator;
    private VBox pollsListContainer;

    // Palette tokens
    private static final String PRIMARY = "#7C3AED";
    private static final String PRIMARY_LIGHT = "#EDE9FE";
    private static final String TEXT_DARK = "#0F172A";
    private static final String TEXT_SUB = "#64748B";
    private static final String BG = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";
    private static final String SUCCESS = "#059669";
    private static final String ERROR = "#DC2626";

    private static final String[] BAR_COLORS = {
            "#7C3AED", "#2563EB", "#059669",
            "#D97706", "#DC2626", "#DB2777",
            "#0891B2", "#4F46E5"
    };

    private String searchFilter = "";
    private String customOrgFilter = ""; // Used if user is guest/homepage without session joinCode
    private int activeTab = 0; // 0 = Browse & Vote, 1 = Create Studio

    public QuickPoll() {
        if (SessionManager.joinCode != null && !SessionManager.joinCode.isBlank()) {
            this.customOrgFilter = SessionManager.joinCode.trim();
        }
    }

    public QuickPoll(String initialJoinCode) {
        if (initialJoinCode != null && !initialJoinCode.isBlank()) {
            this.customOrgFilter = initialJoinCode.trim();
        } else if (SessionManager.joinCode != null && !SessionManager.joinCode.isBlank()) {
            this.customOrgFilter = SessionManager.joinCode.trim();
        }
    }

    public String getEffectiveJoinCode() {
        if (SessionManager.joinCode != null && !SessionManager.joinCode.isBlank()) {
            return SessionManager.joinCode.trim();
        }
        return customOrgFilter.trim();
    }

    @Override
    public Scene getScene(Runnable backCallback) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; " + FONT);

        // Header
        HBox header = buildHeader(backCallback);
        root.setTop(header);

        // Content Container
        VBox mainContent = new VBox(24);
        mainContent.setPadding(new Insets(28, 40, 36, 40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        rebuildContent(mainContent);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + BG + "; -fx-padding: 0;");

        root.setCenter(scroll);
        scene = new Scene(root, 1100, 750);

        // Initial fetch from Firestore / Firebase
        fetchPollsFromFirebase(mainContent);

        return scene;
    }

    private HBox buildHeader(Runnable backCallback) {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 36, 16, 36));
        header.setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: " + BORDER
                        + "; -fx-border-width: 0 0 1.5 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        Circle logo = new Circle(18, Color.web(PRIMARY));
        Label logoSymbol = new Label("📊");
        logoSymbol.setStyle(FONT + "-fx-font-size: 16px;");
        StackPane logoPane = new StackPane(logo, logoSymbol);

        VBox brandBox = new VBox(2);
        Label brand = new Label("Quick Poll Express");
        brand.setStyle(FONT + "-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Organization-scoped live polling with strict 1-vote verification");
        sub.setStyle(FONT + "-fx-font-size: 11.5px; -fx-text-fill: " + TEXT_SUB + ";");
        brandBox.getChildren().addAll(brand, sub);

        HBox titleGroup = new HBox(12, logoPane, brandBox);
        titleGroup.setAlignment(Pos.CENTER_LEFT);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Organization Badge Pill
        HBox orgBadge = buildOrgBadge();

        // Active Voter Identifier Pill / Change Name Dialog
        HBox voterBadge = buildVoterBadge();

        Button backButton = new Button("← Back");
        backButton.setPrefHeight(36);
        backButton.setStyle(FONT
                + "-fx-background-color: #0f172a; -fx-text-fill: white; -fx-font-size: 12.5px; -fx-font-weight: 800; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 0 16;");
        backButton.setOnAction(e -> backCallback.run());

        header.getChildren().addAll(titleGroup, headerSpacer, orgBadge, voterBadge, backButton);
        return header;
    }

    private HBox buildOrgBadge() {
        HBox orgBadge = new HBox(8);
        orgBadge.setAlignment(Pos.CENTER_LEFT);
        orgBadge.setPadding(new Insets(6, 14, 6, 14));
        orgBadge.setStyle(
                "-fx-background-color: #f0fdf4; -fx-background-radius: 20; -fx-border-color: #86efac; -fx-border-radius: 20; -fx-border-width: 1; -fx-cursor: hand;");

        Label orgIcon = new Label("🏢");
        String effCode = getEffectiveJoinCode();
        String displayOrg;
        if (!effCode.isEmpty()) {
            if (SessionManager.organizationName != null
                    && !SessionManager.organizationName.isBlank()
                    && !SessionManager.organizationName.equalsIgnoreCase(effCode)) {
                displayOrg = SessionManager.organizationName + " (" + effCode + ")";
            } else {
                displayOrg = "Org: " + effCode;
            }
        } else {
            displayOrg = "Set Organization Code";
        }

        Label orgLabel = new Label(displayOrg);
        orgLabel.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #166534;");
        orgBadge.getChildren().addAll(orgIcon, orgLabel);

        // Allow changing org code only if not locked by active session
        if (SessionManager.joinCode == null || SessionManager.joinCode.isBlank()) {
            orgBadge.setOnMouseClicked(e -> {
                TextInputDialog dialog = new TextInputDialog(customOrgFilter);
                dialog.setTitle("Organization Join Code");
                dialog.setHeaderText("Set Active Organization Join Code for Quick Polls");
                dialog.setContentText("Enter Organization Join Code (e.g. EV-1234-5678):");

                dialog.showAndWait().ifPresent(code -> {
                    customOrgFilter = code.trim();
                    orgLabel.setText(!customOrgFilter.isEmpty() ? "Org: " + customOrgFilter : "Set Organization Code");
                    if (scene != null && scene.getRoot() instanceof BorderPane bp) {
                        if (bp.getCenter() instanceof ScrollPane sp && sp.getContent() instanceof VBox content) {
                            rebuildContent(content);
                            fetchPollsFromFirebase(content);
                        }
                    }
                });
            });
        }
        return orgBadge;
    }

    private HBox buildVoterBadge() {
        HBox voterBadge = new HBox(8);
        voterBadge.setAlignment(Pos.CENTER_LEFT);
        voterBadge.setPadding(new Insets(6, 12, 6, 12));
        voterBadge.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT
                        + "; -fx-background-radius: 20; -fx-border-color: #C4B5FD; -fx-border-radius: 20; -fx-border-width: 1; -fx-cursor: hand;");

        Label voterIcon = new Label("👤");
        String voterDisplayName = getEffectiveVoterName();
        Label voterName = new Label("Voter: " + voterDisplayName);
        voterName.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: " + PRIMARY + ";");
        voterBadge.getChildren().addAll(voterIcon, voterName);

        voterBadge.setOnMouseClicked(e -> promptChangeVoterName(voterName));
        return voterBadge;
    }

    private void promptChangeVoterName(Label voterLabel) {
        TextInputDialog dialog = new TextInputDialog(getEffectiveVoterName());
        dialog.setTitle("Voter Identity");
        dialog.setHeaderText("Set Your Voting Identity for Quick Polls");
        dialog.setContentText("Enter your Full Name / Voter Handle:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isBlank()) {
                SessionManager.voterName = name.trim();
                if (SessionManager.voterUid == null || SessionManager.voterUid.isBlank()) {
                    SessionManager.voterUid = "VTR-" + Math.abs(name.trim().hashCode());
                }
                voterLabel.setText("Voter: " + SessionManager.voterName);
            }
        });
    }

    public static boolean canCreatePoll() {
        // Explicitly deny if user is a voter in any form
        if ("voter".equalsIgnoreCase(SessionManager.currentRole)) {
            return false;
        }
        if (SessionManager.voterUid != null && !SessionManager.voterUid.isBlank()) {
            return false;
        }
        if (SessionManager.voterName != null && !SessionManager.voterName.isBlank()
                && (SessionManager.adminUid == null || SessionManager.adminUid.isBlank())) {
            return false;
        }
        // ONLY Organization Admins with active admin session can create polls
        return SessionManager.isAdmin() && SessionManager.adminUid != null && !SessionManager.adminUid.isBlank();
    }

    private void rebuildContent(VBox mainContent) {
        mainContent.getChildren().clear();

        boolean allowCreate = canCreatePoll();
        if (!allowCreate) {
            activeTab = 0; // Force voter into Browse & Vote view
        }

        String effJoinCode = getEffectiveJoinCode();

        // 1. Hero Section
        VBox heroSection = new VBox(6);
        heroSection.setAlignment(Pos.CENTER);
        Label title = new Label("Quick Polls");
        title.setStyle(FONT + "-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + TEXT_DARK
                + "; -fx-letter-spacing: -0.5px;");

        String subtitleText = !effJoinCode.isEmpty()
                ? "Exclusive polls created for Organization Join Code: " + effJoinCode
                : "Real-time organization-specific democratic polling. Search or enter your Join Code to vote.";
        Label subtitle = new Label(subtitleText);
        subtitle.setStyle(FONT + "-fx-font-size: 13.5px; -fx-text-fill: " + TEXT_SUB + ";");
        heroSection.getChildren().addAll(title, subtitle);

        mainContent.getChildren().add(heroSection);

        // 2. Organization Scoping Notification Pill
        if (!effJoinCode.isEmpty()) {
            HBox orgScopeBanner = new HBox(8);
            orgScopeBanner.setAlignment(Pos.CENTER);
            orgScopeBanner.setPadding(new Insets(8, 16, 8, 16));
            orgScopeBanner.setMaxWidth(750);
            orgScopeBanner.setStyle(
                    "-fx-background-color: #ecfdf5; -fx-border-color: #a7f3d0; -fx-border-radius: 8; -fx-background-radius: 8;");
            Label orgBadgeIcon = new Label("🔒");
            orgBadgeIcon.setStyle(FONT + "-fx-font-size: 13px;");
            Label orgScopeText = new Label(
                    "Polls are private & strictly restricted to Organization Join Code: " + effJoinCode);
            orgScopeText.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #065f46;");
            orgScopeBanner.getChildren().addAll(orgBadgeIcon, orgScopeText);
            mainContent.getChildren().add(orgScopeBanner);
        }

        // 3. Segmented Navigation Bar (Displayed if user has Poll Creation permissions)
        if (allowCreate) {
            HBox tabSwitcher = buildTabSwitcher(mainContent);
            mainContent.getChildren().add(tabSwitcher);
        }

        if (activeTab == 0) {
            // TAB 0: VOTE & BROWSE (Available to all members)
            HBox filterBar = buildFilterBar(mainContent);

            pollsListContainer = new VBox(18);
            pollsListContainer.setMaxWidth(750);
            pollsListContainer.setAlignment(Pos.TOP_CENTER);

            loadingIndicator = new ProgressIndicator();
            loadingIndicator.setPrefSize(28, 28);
            loadingIndicator.setVisible(false);

            mainContent.getChildren().addAll(filterBar, loadingIndicator, pollsListContainer);
            renderPollCards(mainContent);
        } else {
            // TAB 1: CREATE STUDIO (Admin / Host Only)
            VBox createCard = buildCreatePollCard(mainContent);
            mainContent.getChildren().add(createCard);
        }
    }

    private HBox buildTabSwitcher(VBox mainContent) {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(4));
        bar.setMaxWidth(400);
        bar.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10;");

        Button browseTab = new Button("🗳️ Browse & Vote");
        browseTab.setPrefWidth(190);
        browseTab.setPrefHeight(36);

        Button createTab = new Button("⚡ Create Org Poll");
        createTab.setPrefWidth(190);
        createTab.setPrefHeight(36);

        if (activeTab == 0) {
            browseTab.setStyle(FONT + "-fx-background-color: white; -fx-text-fill: " + PRIMARY
                    + "; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 1);");
            createTab.setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SUB
                    + "; -fx-font-weight: 700; -fx-font-size: 13px; -fx-cursor: hand;");
        } else {
            browseTab.setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SUB
                    + "; -fx-font-weight: 700; -fx-font-size: 13px; -fx-cursor: hand;");
            createTab.setStyle(FONT + "-fx-background-color: white; -fx-text-fill: " + PRIMARY
                    + "; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 1);");
        }

        browseTab.setOnAction(e -> {
            activeTab = 0;
            rebuildContent(mainContent);
        });

        createTab.setOnAction(e -> {
            activeTab = 1;
            rebuildContent(mainContent);
        });

        bar.getChildren().addAll(browseTab, createTab);
        return bar;
    }

    private VBox buildCreatePollCard(VBox mainContent) {
        VBox createCard = new VBox(16);
        createCard.setPadding(new Insets(24));
        createCard.setMaxWidth(750);
        createCard.setStyle(
                "-fx-background-color: " + CARD_BG
                        + "; -fx-background-radius: 14; -fx-border-color: #818cf8; -fx-border-radius: 14; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 10, 0, 0, 3);");

        HBox cardTop = new HBox(8);
        cardTop.setAlignment(Pos.CENTER_LEFT);
        Label createIcon = new Label("⚡");
        createIcon.setStyle(FONT + "-fx-font-size: 16px;");
        Label createTitle = new Label("Create Poll for Organization");
        createTitle.setStyle(FONT + "-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: " + TEXT_DARK + ";");
        cardTop.getChildren().addAll(createIcon, createTitle);

        // 1. Organization Join Code Field
        VBox orgBox = new VBox(6);
        Label orgLabel = new Label("Organization Join Code (Poll Scoping):");
        orgLabel.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 800;");

        TextField orgJoinCodeField = new TextField();
        String currentSessionOrg = getEffectiveJoinCode();
        if (!currentSessionOrg.isEmpty()) {
            orgJoinCodeField.setText(currentSessionOrg);
            // If locked from active admin session, disable editing
            if (SessionManager.joinCode != null && !SessionManager.joinCode.isBlank()) {
                orgJoinCodeField.setDisable(true);
                orgJoinCodeField.setStyle(FONT + "-fx-background-color: #f1f5f9; -fx-border-color: " + BORDER
                        + "; -fx-border-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px; -fx-font-weight: bold;");
            } else {
                orgJoinCodeField.setStyle(FONT + "-fx-background-radius: 8; -fx-border-color: " + BORDER
                        + "; -fx-border-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px;");
            }
        } else {
            orgJoinCodeField.setPromptText("Enter Organization Join Code (e.g. EV-8472-9123)");
            orgJoinCodeField.setStyle(FONT + "-fx-background-radius: 8; -fx-border-color: " + BORDER
                    + "; -fx-border-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px;");
        }
        orgBox.getChildren().addAll(orgLabel, orgJoinCodeField);

        // 2. Question Field
        VBox qBox = new VBox(6);
        Label questionLabel = new Label("Poll Question:");
        questionLabel.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 800;");
        TextField questionField = new TextField();
        questionField.setPromptText("e.g. Which date should we schedule the annual organization hackathon?");
        questionField.setPrefHeight(40);
        questionField.setStyle(FONT + "-fx-background-radius: 8; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px;");
        qBox.getChildren().addAll(questionLabel, questionField);

        // 3. Options
        VBox optBox = new VBox(8);
        Label optionsLabel = new Label("Voting Choices (Minimum 2):");
        optionsLabel.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 800;");

        VBox optionFields = new VBox(8);
        optionFields.getChildren().addAll(
                createOptionField("Option 1: e.g. Friday, October 24"),
                createOptionField("Option 2: e.g. Saturday, October 25"));

        Button addOptionBtn = new Button("+ Add Another Choice");
        addOptionBtn.setStyle(FONT + "-fx-background-color: " + PRIMARY_LIGHT + "; -fx-text-fill: " + PRIMARY
                + "; -fx-font-size: 12px; -fx-font-weight: 800; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;");
        addOptionBtn.setOnAction(e -> {
            int count = optionFields.getChildren().size() + 1;
            optionFields.getChildren().add(createOptionField("Option " + count));
        });

        optBox.getChildren().addAll(optionsLabel, optionFields, addOptionBtn);

        Label statusMsg = new Label();
        statusMsg.setWrapText(true);
        statusMsg.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: bold;");

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button createBtn = new Button("🚀 Publish Organization Poll");
        createBtn.setPrefHeight(38);
        createBtn.setStyle(FONT + "-fx-background-color: " + PRIMARY
                + "; -fx-text-fill: white; -fx-font-size: 13.5px; -fx-font-weight: 900; -fx-padding: 8 22; -fx-background-radius: 8; -fx-cursor: hand;");

        createBtn.setOnAction(e -> {
            String orgJoinCode = orgJoinCodeField.getText().trim();
            if (orgJoinCode.isEmpty()) {
                statusMsg.setStyle(FONT + "-fx-text-fill: " + ERROR + "; -fx-font-weight: bold;");
                statusMsg.setText("❌ Please enter the Organization Join Code for this poll.");
                return;
            }

            String question = questionField.getText().trim();
            if (question.isEmpty()) {
                statusMsg.setStyle(FONT + "-fx-text-fill: " + ERROR + "; -fx-font-weight: bold;");
                statusMsg.setText("❌ Please enter a question for your poll.");
                return;
            }

            List<String> options = new ArrayList<>();
            for (var node : optionFields.getChildren()) {
                if (node instanceof TextField tf) {
                    String text = tf.getText().trim();
                    if (!text.isEmpty()) {
                        options.add(text);
                    }
                }
            }

            if (options.size() < 2) {
                statusMsg.setStyle(FONT + "-fx-text-fill: " + ERROR + "; -fx-font-weight: bold;");
                statusMsg.setText("❌ Please specify at least 2 distinct voting options.");
                return;
            }

            // Generate unique 6-digit code e.g. QP-48291
            String pollCode = "QP-" + (10000 + (int) (Math.random() * 90000));
            String creator = getEffectiveVoterName();

            // Set effective customOrgFilter if guest
            if (SessionManager.joinCode == null || SessionManager.joinCode.isBlank()) {
                customOrgFilter = orgJoinCode;
            }

            // Prepare JSON payload for Realtime Database
            JsonObject pollObj = new JsonObject();
            pollObj.addProperty("pollCode", pollCode);
            pollObj.addProperty("question", question);
            pollObj.addProperty("creatorName", creator);
            pollObj.addProperty("joinCode", orgJoinCode);
            pollObj.addProperty("createdAt", System.currentTimeMillis());

            JsonObject optionsObj = new JsonObject();
            for (String opt : options) {
                optionsObj.addProperty(opt, 0);
            }
            pollObj.add("options", optionsObj);
            pollObj.add("voters", new JsonObject());

            // Create local model immediately for zero-latency UI update
            Map<String, Integer> initialOptions = new LinkedHashMap<>();
            for (String opt : options) {
                initialOptions.put(opt, 0);
            }
            Poll localPoll = new Poll(pollCode, question, creator, orgJoinCode, initialOptions, new HashMap<>());
            allPollsMap.put(pollCode.toUpperCase(), localPoll);

            // Switch to Browse & Vote tab
            activeTab = 0;
            searchFilter = pollCode;
            rebuildContent(mainContent);

            // Save to Firestore and Realtime Database in background
            Thread t = new Thread(() -> {
                QuickPollDAO.savePoll(pollCode, question, creator, options, orgJoinCode);
                FirebaseDatabaseService.saveQuickPoll(pollCode, pollObj, SessionManager.idToken);
            });
            t.setDaemon(true);
            t.start();
        });

        btnRow.getChildren().addAll(statusMsg, createBtn);
        createCard.getChildren().addAll(cardTop, new Separator(), orgBox, qBox, optBox, btnRow);
        return createCard;
    }

    private HBox buildFilterBar(VBox mainContent) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(750);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 10;");

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle(FONT + "-fx-font-size: 14px;");

        TextField searchField = new TextField(searchFilter);
        searchField.setPromptText("Search by Question or Code (e.g. QP-48291)...");
        searchField.setStyle(FONT + "-fx-background-color: transparent; -fx-font-size: 12.5px; -fx-padding: 4 8;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            searchFilter = newV != null ? newV.trim().toLowerCase() : "";
            renderPollCards(mainContent);
        });

        Button refreshBtn = new Button("↻ Sync Org Polls");
        refreshBtn.setStyle(FONT + "-fx-background-color: " + PRIMARY_LIGHT + "; -fx-text-fill: " + PRIMARY
                + "; -fx-font-weight: 800; -fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> fetchPollsFromFirebase(mainContent));

        bar.getChildren().addAll(searchIcon, searchField, refreshBtn);
        return bar;
    }

    private void fetchPollsFromFirebase(VBox mainContent) {
        if (loadingIndicator != null)
            loadingIndicator.setVisible(true);

        String effJoinCode = getEffectiveJoinCode();

        Thread t = new Thread(() -> {
            List<Map<String, Object>> cloudList;
            if (!effJoinCode.isEmpty()) {
                cloudList = QuickPollDAO.getPollsByOrganization(effJoinCode);
            } else {
                cloudList = QuickPollDAO.getAllPolls();
            }

            List<Poll> freshList = new ArrayList<>();

            for (Map<String, Object> data : cloudList) {
                try {
                    String code = (String) data.getOrDefault("pollCode", "QP-UNKNOWN");
                    String question = (String) data.getOrDefault("question", "Untitled Poll");
                    String creator = (String) data.getOrDefault("creatorName", "Electoral Member");
                    String joinCode = (String) data.getOrDefault("joinCode", "");

                    Map<String, Integer> options = new LinkedHashMap<>();
                    Object rawOpts = data.get("options");
                    if (rawOpts instanceof Map<?, ?> map) {
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            int count = 0;
                            if (entry.getValue() instanceof Number n) {
                                count = n.intValue();
                            }
                            options.put(String.valueOf(entry.getKey()), count);
                        }
                    }

                    Map<String, String> voters = new HashMap<>();
                    Object rawVoters = data.get("voters");
                    if (rawVoters instanceof Map<?, ?> vMap) {
                        for (Map.Entry<?, ?> entry : vMap.entrySet()) {
                            String vId = String.valueOf(entry.getKey());
                            if (entry.getValue() instanceof Map<?, ?> vDetails) {
                                voters.put(vId, String.valueOf(vDetails.get("option")));
                            } else {
                                voters.put(vId, String.valueOf(entry.getValue()));
                            }
                        }
                    }

                    freshList.add(new Poll(code, question, creator, joinCode, options, voters));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                if (loadingIndicator != null)
                    loadingIndicator.setVisible(false);
                // Merge cloud polls into master allPollsMap
                for (Poll cloud : freshList) {
                    allPollsMap.put(cloud.pollCode.toUpperCase(), cloud);
                }
                renderPollCards(mainContent);
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void renderPollCards(VBox mainContent) {
        if (pollsListContainer == null)
            return;
        pollsListContainer.getChildren().clear();

        List<Poll> filtered = getFilteredPolls();
        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(36));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: " + BORDER
                    + "; -fx-border-radius: 14; -fx-border-width: 1.5;");
            Label icon = new Label("🗳");
            icon.setStyle(FONT + "-fx-font-size: 32px;");

            String effJoinCode = getEffectiveJoinCode();
            String emptyMsg;
            if (!effJoinCode.isEmpty()) {
                emptyMsg = "No active Quick Polls found for Organization [" + effJoinCode + "].";
            } else {
                emptyMsg = "No active Quick Polls yet. Publish your first organization poll above!";
            }

            Label msg = new Label(emptyMsg);
            msg.setStyle(FONT + "-fx-font-size: 13.5px; -fx-font-weight: 700; -fx-text-fill: " + TEXT_SUB + ";");
            emptyBox.getChildren().addAll(icon, msg);
            pollsListContainer.getChildren().add(emptyBox);
        } else {
            for (Poll poll : filtered) {
                VBox pollCard = buildPollCard(poll, mainContent);
                pollsListContainer.getChildren().add(pollCard);
            }
        }
    }

    private List<Poll> getFilteredPolls() {
        List<Poll> result = new ArrayList<>();
        String q = searchFilter.trim().toLowerCase();
        String qClean = q.replaceAll("[^a-zA-Z0-9]", "");
        String effJoinCode = getEffectiveJoinCode().toLowerCase();

        for (Poll p : allPollsMap.values()) {
            // Strict Organization Scoping: If an active joinCode is set, only match polls
            // with that joinCode
            if (!effJoinCode.isEmpty()) {
                String pJoin = p.joinCode != null ? p.joinCode.trim().toLowerCase() : "";
                if (!pJoin.equalsIgnoreCase(effJoinCode)) {
                    continue;
                }
            }

            if (q.isBlank()) {
                result.add(0, p); // Newest first
                continue;
            }

            String pCode = p.pollCode.toLowerCase();
            String pCodeClean = pCode.replaceAll("[^a-zA-Z0-9]", "");
            String pQuestion = p.question.toLowerCase();
            String pJoin = p.joinCode != null ? p.joinCode.toLowerCase() : "";

            if (pCode.contains(q) || pQuestion.contains(q) || pJoin.contains(q)
                    || (!qClean.isEmpty() && pCodeClean.contains(qClean))) {
                result.add(0, p);
            }
        }
        return result;
    }

    private VBox buildPollCard(Poll poll, VBox mainContent) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 14; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 14; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);");

        // Top Row: Unique Poll Code (#QP-XXXXX) & Organization Join Code & Creator
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label codeBadge = new Label("#" + poll.pollCode);
        codeBadge.setStyle(FONT + "-fx-background-color: " + PRIMARY_LIGHT + "; -fx-text-fill: " + PRIMARY
                + "; -fx-font-weight: 900; -fx-font-size: 11.5px; -fx-padding: 3 8; -fx-background-radius: 6;");

        Label orgBadge = new Label(
                "🏢 " + (poll.joinCode != null && !poll.joinCode.isBlank() ? poll.joinCode : "Public"));
        orgBadge.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #059669; -fx-font-weight: 800; -fx-font-size: 11.5px; -fx-padding: 3 8; -fx-background-radius: 6;");

        Label creatorLbl = new Label("Hosted by " + poll.creatorName);
        creatorLbl.setStyle(FONT + "-fx-font-size: 11.5px; -fx-text-fill: " + TEXT_SUB + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        int totalVotes = poll.getTotalVotes();
        Label totalBadge = new Label(totalVotes + (totalVotes == 1 ? " vote recorded" : " votes recorded"));
        totalBadge.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_DARK + ";");

        topRow.getChildren().addAll(codeBadge, orgBadge, creatorLbl, sp, totalBadge);

        // Question Title
        Label questionLabel = new Label(poll.question);
        questionLabel.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + TEXT_DARK + ";");
        questionLabel.setWrapText(true);

        card.getChildren().addAll(topRow, questionLabel, new Separator());

        // Check if CURRENT USER HAS ALREADY VOTED (Strict Anti-Double-Voting)
        String voterId = getEffectiveVoterId();
        boolean hasVoted = poll.hasUserVoted(voterId);
        String userChosenOption = poll.getUserVotedOption(voterId);

        if (hasVoted) {
            HBox votedBanner = new HBox(8);
            votedBanner.setAlignment(Pos.CENTER_LEFT);
            votedBanner.setPadding(new Insets(8, 12, 8, 12));
            votedBanner.setStyle(
                    "-fx-background-color: #ecfdf5; -fx-background-radius: 8; -fx-border-color: #a7f3d0; -fx-border-radius: 8; -fx-border-width: 1;");

            Label checkIcon = new Label("✔");
            checkIcon.setStyle(FONT + "-fx-font-size: 14px; -fx-text-fill: #059669; -fx-font-weight: bold;");

            Label votedText = new Label(
                    "Ballot Recorded: You voted for \"" + userChosenOption + "\" (1-Vote Enforced in Organization)");
            votedText.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #065f46;");

            votedBanner.getChildren().addAll(checkIcon, votedText);
            card.getChildren().add(votedBanner);
        }

        // Find leader
        int maxVotes = poll.getMaxVotes();

        // Options List
        int optionIndex = 0;
        for (Map.Entry<String, Integer> entry : poll.options.entrySet()) {
            String optionName = entry.getKey();
            int votes = entry.getValue();
            double percentage = totalVotes > 0 ? (double) votes / totalVotes * 100.0 : 0.0;
            String barColor = BAR_COLORS[optionIndex % BAR_COLORS.length];
            boolean isUserChoice = hasVoted && optionName.equals(userChosenOption);
            boolean isLeader = (maxVotes > 0 && votes == maxVotes);

            HBox optionRow = new HBox(12);
            optionRow.setAlignment(Pos.CENTER_LEFT);
            optionRow.setPadding(new Insets(6, 10, 6, 10));
            optionRow.setStyle(isUserChoice
                    ? "-fx-background-color: #f0fdf4; -fx-background-radius: 8; -fx-border-color: #86efac; -fx-border-radius: 8;"
                    : "-fx-background-color: #f8fafc; -fx-background-radius: 8;");

            // Vote Button / Status Badge
            Button voteBtn = new Button(isUserChoice ? "✔ Voted" : (hasVoted ? "Locked" : "Vote"));
            voteBtn.setPrefWidth(72);
            voteBtn.setPrefHeight(30);

            if (isUserChoice) {
                voteBtn.setStyle(FONT
                        + "-fx-background-color: #059669; -fx-text-fill: white; -fx-font-size: 11.5px; -fx-font-weight: 900; -fx-background-radius: 6;");
                voteBtn.setDisable(true);
            } else if (hasVoted) {
                voteBtn.setStyle(FONT
                        + "-fx-background-color: #cbd5e1; -fx-text-fill: #64748b; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-opacity: 0.7;");
                voteBtn.setDisable(true);
            } else {
                voteBtn.setStyle(FONT + "-fx-background-color: " + barColor
                        + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 900; -fx-background-radius: 6; -fx-cursor: hand;");
                voteBtn.setOnAction(e -> {
                    String curVoterId = getEffectiveVoterId();
                    String curVoterName = getEffectiveVoterName();

                    // Instant optimistic local vote registration
                    poll.voteLocally(curVoterId, optionName);
                    renderPollCards(mainContent);

                    // Sync vote to Firestore and RTDB in background
                    Thread t = new Thread(() -> {
                        QuickPollDAO.recordVote(poll.pollCode, curVoterId, curVoterName, optionName);
                        FirebaseDatabaseService.recordQuickPollVote(poll.pollCode, curVoterId, curVoterName, optionName,
                                SessionManager.idToken);
                    });
                    t.setDaemon(true);
                    t.start();
                });
            }

            // Option Name & Leader Badge
            HBox optLabelBox = new HBox(6);
            optLabelBox.setAlignment(Pos.CENTER_LEFT);
            optLabelBox.setMinWidth(160);

            Label optLabel = new Label(optionName);
            optLabel.setStyle(FONT + "-fx-font-size: 13.5px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 700;");

            optLabelBox.getChildren().add(optLabel);
            if (isLeader && totalVotes > 0) {
                Label winBadge = new Label("🏆");
                winBadge.setStyle(FONT + "-fx-font-size: 12px;");
                optLabelBox.getChildren().add(winBadge);
            }

            // Animated Progress Bar Container
            StackPane barContainer = new StackPane();
            barContainer.setMinHeight(24);
            barContainer.setPrefHeight(24);
            barContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(barContainer, Priority.ALWAYS);

            Rectangle barBg = new Rectangle();
            barBg.setHeight(24);
            barBg.setArcWidth(8);
            barBg.setArcHeight(8);
            barBg.setFill(Color.web("#E2E8F0"));
            barBg.widthProperty().bind(barContainer.widthProperty());

            Rectangle barFill = new Rectangle();
            barFill.setHeight(24);
            barFill.setArcWidth(8);
            barFill.setArcHeight(8);
            barFill.setFill(Color.web(barColor));

            double finalPercentage = percentage;
            barContainer.layoutBoundsProperty().addListener((obs, old, bounds) -> {
                barFill.setWidth(bounds.getWidth() * (finalPercentage / 100.0));
            });

            Label pctLabel = new Label(String.format("%.0f%%", percentage));
            pctLabel.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: 900; -fx-text-fill: "
                    + (percentage > 20 ? "white" : TEXT_DARK) + ";");
            pctLabel.setPadding(new Insets(0, 8, 0, 8));

            barContainer.getChildren().addAll(barBg, barFill, pctLabel);

            Label countLabel = new Label(votes + (votes == 1 ? " vote" : " votes"));
            countLabel.setMinWidth(60);
            countLabel.setAlignment(Pos.CENTER_RIGHT);
            countLabel.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_SUB + ";");

            optionRow.getChildren().addAll(voteBtn, optLabelBox, barContainer, countLabel);
            card.getChildren().add(optionRow);
            optionIndex++;
        }

        FadeTransition fade = new FadeTransition(Duration.millis(350), card);
        fade.setFromValue(0.2);
        fade.setToValue(1);
        fade.play();

        return card;
    }

    private TextField createOptionField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setStyle(FONT + "-fx-background-radius: 8; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-padding: 6 12; -fx-font-size: 12.5px;");
        return field;
    }

    private static String getEffectiveVoterId() {
        if (SessionManager.voterUid != null && !SessionManager.voterUid.isBlank()) {
            return SessionManager.voterUid;
        }
        if (SessionManager.adminUid != null && !SessionManager.adminUid.isBlank()) {
            return SessionManager.adminUid;
        }
        if (SessionManager.voterName != null && !SessionManager.voterName.isBlank()) {
            return "VTR-" + Math.abs(SessionManager.voterName.hashCode());
        }
        return "GUEST-" + System.currentTimeMillis();
    }

    private static String getEffectiveVoterName() {
        if (SessionManager.voterName != null && !SessionManager.voterName.isBlank()) {
            return SessionManager.voterName;
        }
        if (SessionManager.adminName != null && !SessionManager.adminName.isBlank()) {
            return SessionManager.adminName;
        }
        return "Electoral Member";
    }

    // =========================================
    // POLL DATA MODEL WITH ORG JOIN CODE SCOPING
    // =========================================

    public static class Poll {
        public final String pollCode;
        public final String question;
        public final String creatorName;
        public final String joinCode;
        public final Map<String, Integer> options;
        public final Map<String, String> voterBallots;

        public Poll(String pollCode, String question, String creatorName, String joinCode, Map<String, Integer> options,
                Map<String, String> voterBallots) {
            this.pollCode = pollCode;
            this.question = question;
            this.creatorName = creatorName;
            this.joinCode = joinCode;
            this.options = options;
            this.voterBallots = voterBallots;
        }

        public synchronized boolean hasUserVoted(String voterId) {
            return voterBallots.containsKey(voterId);
        }

        public synchronized String getUserVotedOption(String voterId) {
            return voterBallots.get(voterId);
        }

        public synchronized void voteLocally(String voterId, String option) {
            if (!hasUserVoted(voterId)) {
                voterBallots.put(voterId, option);
                options.put(option, options.getOrDefault(option, 0) + 1);
            }
        }

        public synchronized int getTotalVotes() {
            return options.values().stream().mapToInt(Integer::intValue).sum();
        }

        public synchronized int getMaxVotes() {
            return options.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        }
    }
}
