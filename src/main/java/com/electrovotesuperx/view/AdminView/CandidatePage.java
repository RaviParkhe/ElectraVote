package com.electrovotesuperx.view.AdminView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.electrovotesuperx.model.AdminModel.ElectionData;

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
    private final List<CandidateModel> applications = new ArrayList<>();
    private final List<ElectionData> loadedElections = new ArrayList<>();

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
        applicationList.setPadding(new Insets(4, 4, 6, 4));
        approvedList.setPadding(new Insets(4, 4, 6, 4));
        electionCandidateList.setPadding(new Insets(4, 4, 6, 4));

        setSpacing(16);
        setPadding(new Insets(20, 24, 24, 24));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                        + FONT);

        HBox header = buildHeader();

        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: transparent; -fx-box-border: transparent; -fx-padding: 0;");
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        VBox leftColumn = new VBox(12);
        leftColumn.setPadding(new Insets(0, 8, 0, 0));
        leftColumn.getChildren().addAll(
                buildElectionSelector(),
                buildElectionCandidateSection());
        VBox.setVgrow(leftColumn.getChildren().get(1), Priority.ALWAYS);

        VBox rightColumn = new VBox(12);
        rightColumn.setPadding(new Insets(0, 0, 0, 8));

        HBox filterBar = buildFilterBar();
        VBox appSection = buildApplicationSection();
        VBox approvedSection = buildApprovedSection();

        appSection.setMinHeight(200);
        approvedSection.setMinHeight(200);
        appSection.setPrefHeight(300);
        approvedSection.setPrefHeight(300);

        VBox.setVgrow(appSection, Priority.ALWAYS);
        VBox.setVgrow(approvedSection, Priority.ALWAYS);

        rightColumn.getChildren().addAll(filterBar, appSection, approvedSection);

        splitPane.getItems().addAll(leftColumn, rightColumn);
        splitPane.setDividerPositions(0.34);

        getChildren().addAll(header, splitPane);

        fetchCandidatesFromFirebase();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Candidate Nominations & Review");
        title.setStyle(FONT
                + "-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label(
                "Screen candidate filings, review manifestos, and certify nominations for active elections");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Button syncBtn = new Button("↻ Sync Data");
        syncBtn.setMinWidth(Region.USE_PREF_SIZE);
        syncBtn.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 8; -fx-cursor: hand;");
        syncBtn.setOnAction(e -> fetchCandidatesFromFirebase());

        header.getChildren().addAll(titleBox, headerSpace, syncBtn);
        return header;
    }

    private VBox buildElectionSelector() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 2);");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label selectLabel = new Label("Target Election Filter");
        selectLabel.setStyle(FONT + "-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #1e1b4b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label badge = new Label("BALLOT NOMINEES");
        badge.setStyle(FONT
                + "-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-weight: 800; -fx-font-size: 9.5px; -fx-padding: 3 8; -fx-background-radius: 10;");
        top.getChildren().addAll(selectLabel, sp, badge);

        electionCombo = new ComboBox<>();
        electionCombo.setPromptText("Loading elections...");
        electionCombo.setMaxWidth(Double.MAX_VALUE);
        electionCombo.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 4 8;");
        electionCombo.setOnAction(e -> refreshElectionCandidates());

        card.getChildren().addAll(top, electionCombo);
        return card;
    }

    private VBox buildElectionCandidateSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(12, 14, 12, 14));
        section.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");
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

    private HBox buildFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(8, 14, 8, 14));
        filterBar.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 2);");

        searchField = new TextField();
        searchField.setPromptText("🔍  Search candidates by name, position...");
        searchField.setPrefWidth(240);
        searchField.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-text-fill: #0f172a; -fx-prompt-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 10; -fx-font-size: 12px; -fx-font-weight: 600;");
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

    private VBox buildApplicationSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(12, 14, 12, 14));
        section.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox appHeader = new HBox();
        appHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Candidate Application");
        title.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        applicationCount.setStyle(FONT
                + "-fx-text-fill: #4338ca; -fx-font-size: 11px; -fx-font-weight: 800; -fx-background-color: #e0e7ff; -fx-padding: 3 8; -fx-background-radius: 8;");

        appHeader.getChildren().addAll(title, space, applicationCount);

        ScrollPane applicationScroll = new ScrollPane(applicationList);
        applicationScroll.setFitToWidth(true);
        applicationScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(applicationScroll, Priority.ALWAYS);

        section.getChildren().addAll(appHeader, applicationScroll);
        return section;
    }

    private VBox buildApprovedSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(12, 14, 12, 14));
        section.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox approvedHeader = new HBox();
        approvedHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Approved Candidates");
        title.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label certifiedBadge = new Label("OFFICIAL BALLOT");
        certifiedBadge.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 800; -fx-font-size: 9.5px; -fx-padding: 3 8; -fx-background-radius: 8; -fx-border-color: #10b981; -fx-border-radius: 8;");

        approvedHeader.getChildren().addAll(title, space, certifiedBadge);

        ScrollPane approvedScroll = new ScrollPane(approvedList);
        approvedScroll.setFitToWidth(true);
        approvedScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(approvedScroll, Priority.ALWAYS);

        section.getChildren().addAll(approvedHeader, approvedScroll);
        return section;
    }

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
                "-fx-cursor: hand;");
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
                "-fx-cursor: hand;");
    }

    private void fetchCandidatesFromFirebase() {
        loadingSpinner.setVisible(true);

        CompletableFuture.supplyAsync(() -> {
            String joinCode = SessionManager.joinCode;
            String idToken = SessionManager.idToken;
            List<ElectionData> elections = new ArrayList<>();
            List<Candidate> candidateDocs = new ArrayList<>();

            try {
                if (joinCode != null && !joinCode.isBlank()) {
                    elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                    candidateDocs = CandidateDAO.getCandidatesByOrg(joinCode, idToken);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new Object[]{elections, candidateDocs};
        }).thenAccept(results -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            List<ElectionData> elections = (List<ElectionData>) results[0];
            @SuppressWarnings("unchecked")
            List<Candidate> candidateDocs = (List<Candidate>) results[1];

            loadedElections.clear();
            loadedElections.addAll(elections);

            if (electionCombo != null) {
                electionCombo.getItems().clear();
                for (ElectionData e : elections) {
                    electionCombo.getItems().add(e.getTitle() != null ? e.getTitle() : "Untitled Election");
                }
                if (!elections.isEmpty()) {
                    electionCombo.setValue(elections.get(0).getTitle());
                } else {
                    electionCombo.setPromptText("No elections created");
                }
            }

            applications.clear();
            for (Candidate c : candidateDocs) {
                String appliedDateStr = formatEpoch(c.getAppliedAt());
                String electionName = (c.getElectionTitle() != null && !c.getElectionTitle().isBlank())
                        ? c.getElectionTitle()
                        : (c.getElectionId() != null ? c.getElectionId() : "General Election");

                applications.add(new CandidateModel(
                        c.getId(),
                        c.getName() != null ? c.getName() : "Unknown Candidate",
                        c.getEmail() != null ? c.getEmail() : "",
                        c.getPhone() != null ? c.getPhone() : "",
                        electionName,
                        c.getPosition() != null ? c.getPosition() : "Nominee",
                        appliedDateStr,
                        c.getStatus() != null ? c.getStatus().toUpperCase() : "PENDING",
                        c.getBio() != null ? c.getBio() : "No manifesto submitted."
                ));
            }

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

    private String formatEpoch(long epochMillis) {
        if (epochMillis <= 0) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
    }

    private void updateCandidateStatusInFirebase(CandidateModel app, String newStatus) {
        loadingSpinner.setVisible(true);

        CompletableFuture.runAsync(() -> {
            try {
                String idToken = SessionManager.idToken != null ? SessionManager.idToken : "";
                CandidateDAO.updateStatus(app.documentId, newStatus, idToken);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            app.status = newStatus;
        }).thenRun(() -> Platform.runLater(() -> {
            loadingSpinner.setVisible(false);
            refreshApplications();
            refreshApprovedCandidates();
            refreshElectionCandidates();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                showBaseAlert("Update Error", "Failed to update candidate status: " + ex.getMessage());
            });
            return null;
        });
    }

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
            emptyBox.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            Label emptyTitle = new Label("No " + currentFilter.toLowerCase() + " applications found.");
            emptyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #64748b;");
            emptyBox.getChildren().add(emptyTitle);
            applicationList.getChildren().add(emptyBox);
        }
    }

    HBox createApplicationCard(CandidateModel app) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setMinHeight(Region.USE_PREF_SIZE);
        card.setAlignment(Pos.CENTER_LEFT);

        String borderAccentColor = "ACCEPTED".equalsIgnoreCase(app.status) ? "#34d399"
                : ("REJECTED".equalsIgnoreCase(app.status) ? "#fca5a5" : "#93c5fd");
        String leftHighlightBar = "ACCEPTED".equalsIgnoreCase(app.status) ? "#059669"
                : ("REJECTED".equalsIgnoreCase(app.status) ? "#dc2626" : "#2563eb");

        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + borderAccentColor + " " + borderAccentColor + " " + borderAccentColor + " "
                + leftHighlightBar + "; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1.5 1.5 1.5 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label avatar = new Label(app.name.isEmpty() ? "?" : app.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-font-size: 16px; -fx-font-weight: 900; -fx-background-radius: 20; -fx-border-color: #bfdbfe; -fx-border-radius: 20; -fx-border-width: 1.5;");

        VBox leftInfo = new VBox(2);
        leftInfo.setMinWidth(0);
        HBox.setHgrow(leftInfo, Priority.ALWAYS);

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label posLabel = new Label("Contesting: " + app.position + "  •  " + app.election);
        posLabel.setStyle(FONT + "-fx-text-fill: #2563eb; -fx-font-size: 11.5px; -fx-font-weight: 800;");

        Label contact = new Label("✉ " + (app.email.isEmpty() ? "No email provided" : app.email) + "  •  Applied: " + app.date);
        contact.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: 600;");

        leftInfo.getChildren().addAll(name, posLabel, contact);

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setMinWidth(Region.USE_PREF_SIZE);

        Button view = new Button("View Details");
        view.setMinWidth(Region.USE_PREF_SIZE);
        view.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(app));
        actions.getChildren().add(view);

        if ("PENDING".equalsIgnoreCase(app.status)) {
            Button accept = new Button("Accept");
            Button reject = new Button("Reject");

            accept.setMinWidth(Region.USE_PREF_SIZE);
            reject.setMinWidth(Region.USE_PREF_SIZE);

            accept.setStyle(FONT
                    + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
            reject.setStyle(FONT
                    + "-fx-background-color: #fef2f2; -fx-text-fill: #b91c1c; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");

            accept.setOnAction(e -> acceptApplication(app));
            reject.setOnAction(e -> rejectApplication(app));

            actions.getChildren().addAll(accept, reject);
        }

        card.getChildren().addAll(avatar, leftInfo, actions);
        return card;
    }

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
            emptyBox.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            Label emptyTitle = new Label("No approved candidates.");
            emptyTitle.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #64748b;");
            emptyBox.getChildren().add(emptyTitle);
            approvedList.getChildren().add(emptyBox);
        }
    }

    private HBox createApprovedCard(CandidateModel app) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setMinHeight(Region.USE_PREF_SIZE);
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
        avatar.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-size: 15px; -fx-font-weight: 900; -fx-background-radius: 18; -fx-border-color: #a7f3d0; -fx-border-radius: 18; -fx-border-width: 1.5;");

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
        status.setStyle(FONT
                + "-fx-background-color: #f0fdf4; -fx-text-fill: #15803d; -fx-font-weight: 900; -fx-font-size: 10px; -fx-padding: 4 10; -fx-background-radius: 14; -fx-border-color: #22c55e; -fx-border-radius: 14; -fx-border-width: 1.2;");

        Button view = new Button("View Details");
        view.setMinWidth(Region.USE_PREF_SIZE);
        view.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(app));

        rightBox.getChildren().addAll(status, view);
        card.getChildren().addAll(avatar, info, rightBox);
        return card;
    }

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
            if ("ACCEPTED".equalsIgnoreCase(app.status) && app.election.equals(selected)
                    && !positions.contains(app.position)) {
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
            positionBox.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 1.2;");

            Label positionTitle = new Label(position);
            positionTitle.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

            VBox candidateBox = new VBox(4);
            for (CandidateModel app : applications) {
                if ("ACCEPTED".equalsIgnoreCase(app.status) && app.election.equals(selected)
                        && app.position.equals(position)) {
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
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        Label avatar = new Label(app.name.isEmpty() ? "?" : app.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(28, 28);
        avatar.setMaxSize(28, 28);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 14; -fx-border-color: #bfdbfe; -fx-border-radius: 14;");

        VBox info = new VBox(1);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-weight: 800; -fx-font-size: 12px; -fx-text-fill: #0f172a;");
        Label email = new Label(app.email.isEmpty() ? "Verified Candidate" : app.email);
        email.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 10.5px;");
        info.getChildren().addAll(name, email);

        Button view = new Button("View");
        view.setMinWidth(Region.USE_PREF_SIZE);
        view.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #93c5fd; -fx-border-radius: 6; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(app));

        card.getChildren().addAll(avatar, info, view);
        return card;
    }

    private void showDetails(CandidateModel app) {
        Dialog<ButtonType> dialog = createBaseDialog("Candidate Dossier", "Official Application & Statement File", "👤",
                "#2563eb", "#eff6ff");

        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setPrefWidth(480);
        box.setStyle("-fx-background-color: #ffffff; " + FONT);

        VBox profileCard = new VBox(4);
        profileCard.setPadding(new Insets(10, 14, 10, 14));
        profileCard.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #818cf8; -fx-border-radius: 10; -fx-border-width: 1.5;");

        Label name = new Label(app.name);
        name.setStyle(FONT + "-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label idBadge = new Label("Contesting: " + app.position + " • " + app.election);
        idBadge.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #2563eb;");
        profileCard.getChildren().addAll(name, idBadge);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

        grid.add(createMetaField("Email Address", app.email.isEmpty() ? "N/A" : app.email), 0, 0);
        grid.add(createMetaField("Phone Number", app.phone.isEmpty() ? "N/A" : app.phone), 0, 1);
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
        statement.setStyle(FONT
                + "-fx-control-inner-background: #f8fafc; -fx-text-fill: #0f172a; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-font-size: 12px; -fx-padding: 6 8;");
        manifestoBox.getChildren().addAll(manTitle, statement);

        box.getChildren().addAll(profileCard, grid, manifestoBox);
        dialog.getDialogPane().setContent(box);

        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeBtn);

        styleDialogButtons(dialog, "#2563eb", closeBtn);
        dialog.showAndWait();
    }

    private void acceptApplication(CandidateModel app) {
        Dialog<ButtonType> confirmation = createBaseDialog("Accept Candidate", "Confirm approval", "✓", "#10b981", "#ecfdf5");

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
        Label desc = new Label("This candidate will be approved and published to the live ballot.");
        desc.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        confirmation.getDialogPane().setContent(contentBox);

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
        Dialog<ButtonType> confirmation = createBaseDialog("Reject Candidate", "Confirm rejection", "✕", "#ef4444", "#fee2e2");

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(400);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Circle outerHalo = new Circle(26, Color.web("#fee2e2"));
        Circle innerCircle = new Circle(18, Color.web("#dc2626"));
        Label icon = new Label("✕");
        icon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 900;");
        StackPane iconStack = new StackPane(outerHalo, innerCircle, icon);

        VBox textBox = new VBox(4);
        textBox.setAlignment(Pos.CENTER);
        Label title = new Label("Reject " + app.name + "?");
        title.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #991b1b;");
        Label desc = new Label("This candidate will be rejected and excluded from the ballot.");
        desc.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        confirmation.getDialogPane().setContent(contentBox);

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

    private VBox createMetaField(String label, String value) {
        VBox box = new VBox(2);
        Label l = new Label(label);
        l.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: 700;");
        Label v = new Label(value);
        v.setStyle(FONT + "-fx-text-fill: #1e1b4b; -fx-font-size: 12.5px; -fx-font-weight: 800;");
        box.getChildren().addAll(l, v);
        return box;
    }

    private Dialog<ButtonType> createBaseDialog(String titleStr, String subtitleStr, String iconGlyph,
            String colorHex, String bgHex) {
        Dialog<ButtonType> dialog = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            dialog.initOwner(AdminDashboard.AdminDashboardStage);
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(dialog);
        }
        dialog.setTitle(titleStr);

        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: " + colorHex
                + "; -fx-border-width: 1.5; -fx-border-radius: 14; -fx-background-radius: 14;");
        return dialog;
    }

    private void styleDialogButtons(Dialog<ButtonType> dialog, String primaryColorHex, ButtonType okType) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okType);
        if (okButton != null) {
            okButton.setStyle(FONT + "-fx-background-color: " + primaryColorHex
                    + "; -fx-text-fill: white; -fx-font-weight: 800; -fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 6 16; -fx-cursor: hand;");
        }
    }

    private void showBaseAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (AdminDashboard.AdminDashboardStage != null) {
            alert.initOwner(AdminDashboard.AdminDashboardStage);
            alert.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(alert);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

        public CandidateModel(String documentId, String name, String email, String phone, String election,
                String position, String date, String status, String statement) {
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