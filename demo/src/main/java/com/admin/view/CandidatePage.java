package com.admin.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CandidatePage extends VBox {

    // =========================================================
    // MULTI-TENANCY CONTEXT & DATA STATE
    // =========================================================
    private final String currentOrganizationId = "ORG_ABC_COLLEGE";
    private final List<CandidateModel> applications = new ArrayList<>();

    private final VBox applicationList = new VBox(10);
    private final VBox approvedList = new VBox(10);
    private final VBox electionCandidateList = new VBox(10);
    private final Label applicationCount = new Label("0 Record(s)");
    private String currentFilter = "PENDING";

    private TextField searchField;
    private ComboBox<String> electionCombo;
    private Button pendingBtn;
    private Button acceptedBtn;
    private Button rejectedBtn;
    private ProgressIndicator loadingSpinner;

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public CandidatePage() {
        setSpacing(16);
        setPadding(new Insets(20, 24, 24, 24));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); " + FONT);

        // 1. Header Section
        HBox header = buildHeader();

        // 2. Responsive Split View (Left Column vs Right Column)
        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: transparent; -fx-box-border: transparent; -fx-padding: 0;");
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        // Left Column: Election Selector & Position Tree
        VBox leftColumn = new VBox(12);
        leftColumn.setPadding(new Insets(0, 8, 0, 0));
        leftColumn.getChildren().addAll(
                buildElectionSelector(),
                buildElectionCandidateSection()
        );
        VBox.setVgrow(leftColumn.getChildren().get(1), Priority.ALWAYS);

        // Right Column: Search, Applications, and Approved Rosters
        VBox rightColumn = new VBox(12);
        rightColumn.setPadding(new Insets(0, 0, 0, 8));
        rightColumn.getChildren().addAll(
                buildFilterBar(),
                buildApplicationSection(),
                buildApprovedSection()
        );
        VBox.setVgrow(rightColumn.getChildren().get(1), Priority.ALWAYS);
        VBox.setVgrow(rightColumn.getChildren().get(2), Priority.ALWAYS);

        splitPane.getItems().addAll(leftColumn, rightColumn);
        splitPane.setDividerPositions(0.34);

        getChildren().addAll(header, splitPane);

        fetchCandidatesFromFirebase();
    }

    // =========================================================
    // 1. HEADER SECTION
    // =========================================================
    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Candidate Nominations & Review");
        title.setStyle(FONT + "-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Screen candidate filings, review manifestos, and certify nominations for active elections");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Button syncBtn = new Button("↻ Sync Data");
        syncBtn.setMinWidth(Region.USE_PREF_SIZE);
        syncBtn.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 8; -fx-cursor: hand;");
        syncBtn.setOnAction(e -> fetchCandidatesFromFirebase());

        header.getChildren().addAll(titleBox, headerSpace, syncBtn);
        return header;
    }

    // =========================================================
    // 2. ELECTION SELECTOR CARD (LEFT)
    // =========================================================
    private VBox buildElectionSelector() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 2);");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label selectLabel = new Label("Target Election Filter");
        selectLabel.setStyle(FONT + "-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #1e1b4b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label badge = new Label("BALLOT NOMINEES");
        badge.setStyle(FONT + "-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-weight: 800; -fx-font-size: 9.5px; -fx-padding: 3 8; -fx-background-radius: 10;");
        top.getChildren().addAll(selectLabel, sp, badge);

        electionCombo = new ComboBox<>();
        electionCombo.setPromptText("Select an election...");
        electionCombo.setMaxWidth(Double.MAX_VALUE);
        electionCombo.getItems().addAll(
                "Student Council Election",
                "Cultural Committee Election",
                "Sports Committee Election"
        );
        electionCombo.setStyle(FONT + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 4 8;");
        electionCombo.setOnAction(e -> refreshElectionCandidates());

        card.getChildren().addAll(top, electionCombo);
        return card;
    }

    // =========================================================
    // 3. ELECTION-WISE CANDIDATES (LEFT COLUMN)
    // =========================================================
    private VBox buildElectionCandidateSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(12, 14, 12, 14));
        section.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");
        VBox.setVgrow(section, Priority.ALWAYS);

        Label title = new Label("Nominees by Position");
        title.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        ScrollPane electionScroll = new ScrollPane(electionCandidateList);
        electionScroll.setFitToWidth(true);
        electionScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(electionScroll, Priority.ALWAYS);

        section.getChildren().addAll(title, electionScroll);
        return section;
    }

    // =========================================================
    // 4. FILTER & SEARCH TOOLBAR (RIGHT COLUMN TOP)
    // =========================================================
    private HBox buildFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(8, 14, 8, 14));
        filterBar.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 2);");

        searchField = new TextField();
        searchField.setPromptText("🔍  Search candidates by name, position...");
        searchField.setPrefWidth(240);
        searchField.setStyle(FONT + "-fx-background-color: #f8fafc; -fx-text-fill: #0f172a; -fx-prompt-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 10; -fx-font-size: 12px; -fx-font-weight: 600;");
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            refreshApplications();
            refreshApprovedCandidates();
        });

        loadingSpinner = new ProgressIndicator();
        loadingSpinner.setPrefSize(16, 16);
        loadingSpinner.setVisible(false);

        Region barSpace = new Region();
        HBox.setHgrow(barSpace, Priority.ALWAYS);

        pendingBtn = new Button("Pending");
        acceptedBtn = new Button("Accepted");
        rejectedBtn = new Button("Rejected");

        setActiveButton(pendingBtn, "#2563eb", "#eff6ff", "#3b82f6");
        setNormalButton(acceptedBtn, "#f8fafc", "#059669", "#cbd5e1");
        setNormalButton(rejectedBtn, "#f8fafc", "#dc2626", "#cbd5e1");

        pendingBtn.setOnAction(e -> {
            currentFilter = "PENDING";
            setActiveButton(pendingBtn, "#2563eb", "#eff6ff", "#3b82f6");
            setNormalButton(acceptedBtn, "#f8fafc", "#059669", "#cbd5e1");
            setNormalButton(rejectedBtn, "#f8fafc", "#dc2626", "#cbd5e1");
            refreshApplications();
        });

        acceptedBtn.setOnAction(e -> {
            currentFilter = "ACCEPTED";
            setActiveButton(acceptedBtn, "#059669", "#ecfdf5", "#10b981");
            setNormalButton(pendingBtn, "#f8fafc", "#2563eb", "#cbd5e1");
            setNormalButton(rejectedBtn, "#f8fafc", "#dc2626", "#cbd5e1");
            refreshApplications();
        });

        rejectedBtn.setOnAction(e -> {
            currentFilter = "REJECTED";
            setActiveButton(rejectedBtn, "#dc2626", "#fef2f2", "#ef4444");
            setNormalButton(pendingBtn, "#f8fafc", "#2563eb", "#cbd5e1");
            setNormalButton(acceptedBtn, "#f8fafc", "#059669", "#cbd5e1");
            refreshApplications();
        });

        filterBar.getChildren().addAll(searchField, loadingSpinner, barSpace, pendingBtn, acceptedBtn, rejectedBtn);
        return filterBar;
    }

    // =========================================================
    // 5. APPLICATION SECTION (RIGHT COLUMN MIDDLE)
    // =========================================================
    private VBox buildApplicationSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(12, 14, 12, 14));
        section.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox appHeader = new HBox();
        appHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Candidate Application");
        title.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        applicationCount.setStyle(FONT + "-fx-text-fill: #4338ca; -fx-font-size: 11px; -fx-font-weight: 800; -fx-background-color: #e0e7ff; -fx-padding: 3 8; -fx-background-radius: 8;");

        appHeader.getChildren().addAll(title, space, applicationCount);

        ScrollPane applicationScroll = new ScrollPane(applicationList);
        applicationScroll.setFitToWidth(true);
        applicationScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(applicationScroll, Priority.ALWAYS);

        section.getChildren().addAll(appHeader, applicationScroll);
        return section;
    }

    // =========================================================
    // 6. APPROVED CANDIDATES SECTION (RIGHT COLUMN BOTTOM)
    // =========================================================
    private VBox buildApprovedSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(12, 14, 12, 14));
        section.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox approvedHeader = new HBox();
        approvedHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Approved Candidates");
        title.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label certifiedBadge = new Label("OFFICIAL BALLOT");
        certifiedBadge.setStyle(FONT + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 800; -fx-font-size: 9.5px; -fx-padding: 3 8; -fx-background-radius: 8; -fx-border-color: #10b981; -fx-border-radius: 8;");

        approvedHeader.getChildren().addAll(title, space, certifiedBadge);

        ScrollPane approvedScroll = new ScrollPane(approvedList);
        approvedScroll.setFitToWidth(true);
        approvedScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(approvedScroll, Priority.ALWAYS);

        section.getChildren().addAll(approvedHeader, approvedScroll);
        return section;
    }

    // =========================================================
    // BUTTON STYLING HELPERS
    // =========================================================
    private void setNormalButton(Button button, String background, String textColor, String borderColor) {
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setStyle(FONT +
                "-fx-background-color: " + background + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 8;" +
                "-fx-font-weight: 800;" +
                "-fx-font-size: 11.5px;" +
                "-fx-padding: 5 12;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );
    }

    private void setActiveButton(Button button, String textColor, String bgAccent, String borderColor) {
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setStyle(FONT +
                "-fx-background-color: " + bgAccent + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 1.5;" +
                "-fx-font-weight: 900;" +
                "-fx-font-size: 11.5px;" +
                "-fx-padding: 5 12;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );
    }

    // =========================================================
    // ASYNCHRONOUS FIREBASE LOGIC
    // =========================================================
    private void fetchCandidatesFromFirebase() {
        loadingSpinner.setVisible(true);

        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}
            return getInitialFallbackData();
        }).thenAccept(fetchedList -> Platform.runLater(() -> {
            applications.clear();
            applications.addAll(fetchedList);
            loadingSpinner.setVisible(false);
            refreshApplications();
            refreshApprovedCandidates();
            refreshElectionCandidates();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                showBaseAlert("Sync Error", "Failed to fetch candidates: " + ex.getMessage());
            });
            return null;
        });
    }

    private void updateCandidateStatusInFirebase(CandidateModel app, String newStatus) {
        loadingSpinner.setVisible(true);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
            app.status = newStatus;
        }).thenRun(() -> Platform.runLater(() -> {
            loadingSpinner.setVisible(false);
            refreshApplications();
            refreshApprovedCandidates();
            refreshElectionCandidates();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                showBaseAlert("Update Error", "Failed to update status in Firebase: " + ex.getMessage());
            });
            return null;
        });
    }

    // =========================================================
    // APPLICATION CARDS (UN-CLIPPED FULL-TEXT BUTTONS)
    // =========================================================
    void refreshApplications() {
        applicationList.getChildren().clear();
        String query = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        int count = 0;

        for (CandidateModel app : applications) {
            boolean matchesFilter = app.status.equalsIgnoreCase(currentFilter);
            boolean matchesSearch = query.isEmpty()
                    || app.name.toLowerCase().contains(query)
                    || app.position.toLowerCase().contains(query)
                    || app.election.toLowerCase().contains(query);

            if (matchesFilter && matchesSearch) {
                applicationList.getChildren().add(createApplicationCard(app));
                count++;
            }
        }

        applicationCount.setText(count + " Record(s)");

        if (count == 0) {
            VBox emptyBox = new VBox(4);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefHeight(60);
            emptyBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            Label emptyTitle = new Label("No " + currentFilter.toLowerCase() + " applications found.");
            emptyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #64748b;");
            emptyBox.getChildren().add(emptyTitle);
            applicationList.getChildren().add(emptyBox);
        }
    }

    HBox createApplicationCard(CandidateModel app) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setMinHeight(76);
        card.setAlignment(Pos.CENTER_LEFT);

        String borderAccentColor = "ACCEPTED".equalsIgnoreCase(app.status) ? "#34d399" : ("REJECTED".equalsIgnoreCase(app.status) ? "#fca5a5" : "#93c5fd");
        String leftHighlightBar = "ACCEPTED".equalsIgnoreCase(app.status) ? "#059669" : ("REJECTED".equalsIgnoreCase(app.status) ? "#dc2626" : "#2563eb");

        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + borderAccentColor + " " + borderAccentColor + " " + borderAccentColor + " " + leftHighlightBar + "; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1.5 1.5 1.5 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label avatar = new Label(app.name.isEmpty() ? "?" : app.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-font-size: 16px; -fx-font-weight: 900; -fx-background-radius: 20; -fx-border-color: #bfdbfe; -fx-border-radius: 20; -fx-border-width: 1.5;");

        VBox leftInfo = new VBox(2);
        leftInfo.setMinWidth(0);
        HBox.setHgrow(leftInfo, Priority.ALWAYS);

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label posLabel = new Label("Contesting: " + app.position + "  •  " + app.election);
        posLabel.setStyle(FONT + "-fx-text-fill: #2563eb; -fx-font-size: 11.5px; -fx-font-weight: 800;");

        Label contact = new Label("✉ " + app.email + "  •  Applied: " + app.date);
        contact.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: 600;");

        leftInfo.getChildren().addAll(name, posLabel, contact);

        // ACTION BUTTONS CONTAINER (PROTECTED FROM SHRINKING)
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setMinWidth(Region.USE_PREF_SIZE);

        Button view = new Button("View Details");
        view.setMinWidth(Region.USE_PREF_SIZE);
        view.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(app));
        actions.getChildren().add(view);

        if ("PENDING".equalsIgnoreCase(app.status)) {
            Button accept = new Button("Accept");
            Button reject = new Button("Reject");

            accept.setMinWidth(Region.USE_PREF_SIZE);
            reject.setMinWidth(Region.USE_PREF_SIZE);

            accept.setStyle(FONT + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
            reject.setStyle(FONT + "-fx-background-color: #fef2f2; -fx-text-fill: #b91c1c; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");

            accept.setOnAction(e -> acceptApplication(app));
            reject.setOnAction(e -> rejectApplication(app));

            actions.getChildren().addAll(accept, reject);
        }

        card.getChildren().addAll(avatar, leftInfo, actions);
        return card;
    }

    // =========================================================
    // APPROVED CANDIDATES (UN-CLIPPED FULL-TEXT VIEW BUTTON)
    // =========================================================
    private void refreshApprovedCandidates() {
        approvedList.getChildren().clear();
        String query = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        int count = 0;

        for (CandidateModel app : applications) {
            boolean isApproved = "ACCEPTED".equalsIgnoreCase(app.status);
            boolean matchesSearch = query.isEmpty()
                    || app.name.toLowerCase().contains(query)
                    || app.position.toLowerCase().contains(query)
                    || app.election.toLowerCase().contains(query);

            if (isApproved && matchesSearch) {
                approvedList.getChildren().add(createApprovedCard(app));
                count++;
            }
        }

        if (count == 0) {
            VBox emptyBox = new VBox(4);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefHeight(60);
            emptyBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            Label emptyTitle = new Label("No approved candidates.");
            emptyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #64748b;");
            emptyBox.getChildren().add(emptyTitle);
            approvedList.getChildren().add(emptyBox);
        }
    }

    private HBox createApprovedCard(CandidateModel app) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setMinHeight(68);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #86efac #86efac #86efac #16a34a; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1.5 1.5 1.5 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label avatar = new Label(app.name.isEmpty() ? "?" : app.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(36, 36);
        avatar.setMaxSize(36, 36);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-size: 15px; -fx-font-weight: 900; -fx-background-radius: 18; -fx-border-color: #a7f3d0; -fx-border-radius: 18; -fx-border-width: 1.5;");

        VBox info = new VBox(2);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label id = new Label(app.position + "  •  " + app.election);
        id.setStyle(FONT + "-fx-text-fill: #059669; -fx-font-size: 11.5px; -fx-font-weight: 800;");
        info.getChildren().addAll(name, id);

        HBox rightBox = new HBox(10);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setMinWidth(Region.USE_PREF_SIZE);

        Label status = new Label("CERTIFIED");
        status.setStyle(FONT + "-fx-background-color: #f0fdf4; -fx-text-fill: #15803d; -fx-font-weight: 900; -fx-font-size: 10px; -fx-padding: 4 10; -fx-background-radius: 14; -fx-border-color: #22c55e; -fx-border-radius: 14; -fx-border-width: 1.2;");

        Button view = new Button("View Details");
        view.setMinWidth(Region.USE_PREF_SIZE);
        view.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(app));

        rightBox.getChildren().addAll(status, view);
        card.getChildren().addAll(avatar, info, rightBox);
        return card;
    }

    // =========================================================
    // REFRESH ELECTION-WISE CANDIDATES (LEFT COLUMN)
    // =========================================================
    private void refreshElectionCandidates() {
        electionCandidateList.getChildren().clear();
        String selected = electionCombo.getValue();

        if (selected == null) {
            Label emptyTitle = new Label("Select an election above to view candidates.");
            emptyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-padding: 10;");
            electionCandidateList.getChildren().add(emptyTitle);
            return;
        }

        List<String> positions = new ArrayList<>();
        for (CandidateModel app : applications) {
            if ("ACCEPTED".equalsIgnoreCase(app.status) && app.election.equals(selected) && !positions.contains(app.position)) {
                positions.add(app.position);
            }
        }

        if (positions.isEmpty()) {
            Label emptyTitle = new Label("No approved nominees for this election.");
            emptyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-padding: 10;");
            electionCandidateList.getChildren().add(emptyTitle);
            return;
        }

        for (String position : positions) {
            VBox positionBox = new VBox(6);
            positionBox.setPadding(new Insets(10, 12, 10, 12));
            positionBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 1.2;");

            Label positionTitle = new Label(position);
            positionTitle.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

            VBox candidateBox = new VBox(4);
            for (CandidateModel app : applications) {
                if ("ACCEPTED".equalsIgnoreCase(app.status) && app.election.equals(selected) && app.position.equals(position)) {
                    candidateBox.getChildren().add(createElectionCandidateCard(app));
                }
            }

            positionBox.getChildren().addAll(positionTitle, candidateBox);
            electionCandidateList.getChildren().add(positionBox);
        }
    }

    private HBox createElectionCandidateCard(CandidateModel app) {
        HBox card = new HBox(8);
        card.setPadding(new Insets(6, 10, 6, 10));
        card.setMinHeight(42);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        Label avatar = new Label(app.name.isEmpty() ? "?" : app.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(28, 28);
        avatar.setMaxSize(28, 28);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 14; -fx-border-color: #bfdbfe; -fx-border-radius: 14;");

        VBox info = new VBox(1);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-weight: 800; -fx-font-size: 12px; -fx-text-fill: #0f172a;");
        Label email = new Label(app.email);
        email.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 10.5px;");
        info.getChildren().addAll(name, email);

        Button view = new Button("View");
        view.setMinWidth(Region.USE_PREF_SIZE);
        view.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #93c5fd; -fx-border-radius: 6; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(app));

        card.getChildren().addAll(avatar, info, view);
        return card;
    }

    // =========================================================
    // DIALOGS & CONFIRMATIONS
    // =========================================================
    private void showDetails(CandidateModel app) {
        Dialog<ButtonType> dialog = createBaseDialog("Candidate Dossier", "Official Application & Statement File", "👤", "#2563eb", "#eff6ff");

        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setPrefWidth(480);
        box.setStyle("-fx-background-color: #ffffff; " + FONT);

        VBox profileCard = new VBox(4);
        profileCard.setPadding(new Insets(10, 14, 10, 14));
        profileCard.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #818cf8; -fx-border-radius: 10; -fx-border-width: 1.5;");

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label idBadge = new Label("Contesting: " + app.position + " • " + app.election);
        idBadge.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #2563eb;");
        profileCard.getChildren().addAll(name, idBadge);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

        grid.add(createMetaField("Email Address", app.email), 0, 0);
        grid.add(createMetaField("Phone Number", app.phone), 0, 1);
        grid.add(createMetaField("Target Election", app.election), 1, 0);
        grid.add(createMetaField("Target Office", app.position), 1, 1);
        grid.add(createMetaField("Application Date", app.date), 0, 2);
        grid.add(createMetaField("Review Status", app.status), 1, 2);

        VBox manifestoBox = new VBox(4);
        Label manTitle = new Label("Candidate Statement");
        manTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        TextArea statement = new TextArea(app.statement);
        statement.setWrapText(true);
        statement.setEditable(false);
        statement.setPrefRowCount(3);
        statement.setStyle(FONT + "-fx-control-inner-background: #f8fafc; -fx-text-fill: #0f172a; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-font-size: 12px; -fx-padding: 6 8;");
        manifestoBox.getChildren().addAll(manTitle, statement);

        box.getChildren().addAll(profileCard, grid, manifestoBox);
        dialog.getDialogPane().setContent(box);

        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeBtn);

        styleDialogButtons(dialog, "#2563eb", closeBtn);
        dialog.showAndWait();
    }

    private void acceptApplication(CandidateModel app) {
        Dialog<ButtonType> confirmation = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            confirmation.initOwner(AdminDashboard.AdminDashboardStage);
        }
        confirmation.setTitle("Accept Candidate Application");

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(400);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Circle outerHalo = new Circle(26, Color.web("#dcfce7"));
        Circle innerCircle = new Circle(18, Color.web("#16a34a"));
        Label icon = new Label("✓");
        icon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 900;");
        StackPane iconStack = new StackPane(outerHalo, innerCircle, icon);

        VBox textBox = new VBox(4);
        textBox.setAlignment(Pos.CENTER);
        Label title = new Label("Accept " + app.name + "?");
        title.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");
        Label desc = new Label("This applicant will be officially placed on the election ballot in Firebase.");
        desc.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        confirmation.getDialogPane().setContent(contentBox);
        confirmation.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: #10b981; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType acceptBtnType = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getDialogPane().getButtonTypes().addAll(cancelBtnType, acceptBtnType);

        styleDialogButtons(confirmation, "#059669", acceptBtnType);

        confirmation.showAndWait().ifPresent(result -> {
            if (result == acceptBtnType) {
                updateCandidateStatusInFirebase(app, "ACCEPTED");
            }
        });
    }

    private void rejectApplication(CandidateModel app) {
        Dialog<ButtonType> confirmation = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            confirmation.initOwner(AdminDashboard.AdminDashboardStage);
        }
        confirmation.setTitle("Reject Candidate Application");

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(400);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Circle outerHalo = new Circle(26, Color.web("#fee2e2"));
        Circle innerCircle = new Circle(18, Color.web("#ef4444"));
        Label icon = new Label("✕");
        errorIconStyle(icon);
        StackPane iconStack = new StackPane(outerHalo, innerCircle, icon);

        VBox textBox = new VBox(4);
        textBox.setAlignment(Pos.CENTER);
        Label title = new Label("Reject " + app.name + "?");
        title.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #991b1b;");
        Label desc = new Label("This application will be denied and marked as rejected in Firebase.");
        desc.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        confirmation.getDialogPane().setContent(contentBox);
        confirmation.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType rejectBtnType = new ButtonType("Reject", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getDialogPane().getButtonTypes().addAll(cancelBtnType, rejectBtnType);

        styleDialogButtons(confirmation, "#dc2626", rejectBtnType);

        confirmation.showAndWait().ifPresent(result -> {
            if (result == rejectBtnType) {
                updateCandidateStatusInFirebase(app, "REJECTED");
            }
        });
    }

    private void errorIconStyle(Label icon) {
        icon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 900;");
    }

    private VBox createMetaField(String title, String val) {
        VBox v = new VBox(2);
        Label l = new Label(title);
        l.setStyle(FONT + "-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #64748b;");

        Label value = new Label(val);
        value.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        v.getChildren().addAll(l, value);
        return v;
    }

    private Dialog<ButtonType> createBaseDialog(String title, String headerSubtitle, String iconGlyph, String iconColor, String iconBgHex) {
        Dialog<ButtonType> dialog = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            dialog.initOwner(AdminDashboard.AdminDashboardStage);
        }
        dialog.setTitle(title);

        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(14, 18, 14, 18));
        headerBox.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1.2 0;");

        Circle iconCircle = new Circle(16, Color.web(iconBgHex));
        Label icon = new Label(iconGlyph);
        icon.setStyle(FONT + "-fx-font-size: 14px;");
        StackPane iconPane = new StackPane(iconCircle, icon);

        VBox titleArea = new VBox(2);
        Label mainTitle = new Label(title);
        mainTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");
        Label sub = new Label(headerSubtitle);
        sub.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleArea.getChildren().addAll(mainTitle, sub);

        headerBox.getChildren().addAll(iconPane, titleArea);
        dialog.getDialogPane().setHeader(headerBox);
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: #818cf8; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12; " + FONT);

        return dialog;
    }

    private void styleDialogButtons(Dialog<ButtonType> dialog, String primaryColor, ButtonType primaryType) {
        Button primaryBtn = (Button) dialog.getDialogPane().lookupButton(primaryType);
        if (primaryBtn != null) {
            primaryBtn.setMinWidth(Region.USE_PREF_SIZE);
            primaryBtn.setStyle(FONT + "-fx-background-color: " + primaryColor + "; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;");
        }

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setMinWidth(Region.USE_PREF_SIZE);
            cancelBtn.setStyle(FONT + "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: 800; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-border-color: #cbd5e1; -fx-border-radius: 8;");
        }
    }

    private void showBaseAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =========================================================
    // FALLBACK / SAMPLE DATA
    // =========================================================
    private List<CandidateModel> getInitialFallbackData() {
        List<CandidateModel> list = new ArrayList<>();
        list.add(new CandidateModel("DOC_C01", "Rahul Sharma", "rahul@gmail.com", "9876543210", "Student Council Election", "President", "14 Aug 2026", "PENDING", "I want to represent students and foster transparent academic dialogue."));
        list.add(new CandidateModel("DOC_C02", "Priya Patil", "priya@gmail.com", "9876543211", "Student Council Election", "Secretary", "14 Aug 2026", "PENDING", "Dedicated to improving campus infrastructure, student grievances, and library resources."));
        list.add(new CandidateModel("DOC_C03", "Amit Kulkarni", "amit@gmail.com", "9876543212", "Student Council Election", "President", "13 Aug 2026", "PENDING", "Focused on industry mentorship, tech club expansions, and internship opportunities."));
        list.add(new CandidateModel("DOC_C04", "Sneha Joshi", "sneha@gmail.com", "9876543213", "Student Council Election", "Treasurer", "12 Aug 2026", "ACCEPTED", "Ensuring transparent club budgeting, audit disclosures, and event sponsorships."));
        list.add(new CandidateModel("DOC_C05", "Rohit Patil", "rohit@gmail.com", "9876543214", "Student Council Election", "Secretary", "11 Aug 2026", "REJECTED", "Looking to coordinate sports meets and inter-collegiate events."));
        list.add(new CandidateModel("DOC_C06", "Neha Deshmukh", "neha@gmail.com", "9876543215", "Cultural Committee Election", "President", "10 Aug 2026", "ACCEPTED", "Organizing annual arts exhibitions, drama competitions, and cultural festivals."));
        list.add(new CandidateModel("DOC_C07", "Akash More", "akash@gmail.com", "9876543216", "Cultural Committee Election", "Secretary", "10 Aug 2026", "ACCEPTED", "Streamlining venue approvals, equipment setups, and sound systems."));
        return list;
    }

    // =========================================================
    // FIRESTORE ENTITY MODEL CLASS (DIRECT FIELD ACCESS)
    // =========================================================
    public static class CandidateModel {
        public String documentId;
        public String name;
        public String email;
        public String phone;
        public String election;
        public String position;
        public String date;
        public String status;
        public String statement;

        public CandidateModel(String documentId, String name, String email, String phone, String election, String position, String date, String status, String statement) {
            this.documentId = documentId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.election = election;
            this.position = position;
            this.date = date;
            this.status = status;
            this.statement = statement;
        }

        public static CandidateModel fromFirestoreDocument(String docId, Map<String, Object> data) {
            return new CandidateModel(
                    docId,
                    (String) data.getOrDefault("candidateName", "Unknown"),
                    (String) data.getOrDefault("email", ""),
                    (String) data.getOrDefault("phone", ""),
                    (String) data.getOrDefault("electionTitle", "General Election"),
                    (String) data.getOrDefault("position", "Nominee"),
                    (String) data.getOrDefault("appliedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))),
                    (String) data.getOrDefault("status", "PENDING"),
                    (String) data.getOrDefault("statement", "No statement provided.")
            );
        }

        public Map<String, Object> toFirestoreMap(String organizationId) {
            Map<String, Object> map = new HashMap<>();
            map.put("organizationId", organizationId);
            map.put("candidateName", name);
            map.put("email", email);
            map.put("phone", phone);
            map.put("electionTitle", election);
            map.put("position", position);
            map.put("appliedAt", date);
            map.put("status", status);
            map.put("statement", statement);
            return map;
        }
    }
}