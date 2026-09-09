package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.dao.OfflineDAO.AuditLogDAO;
import com.electrovotesuperx.dao.OfflineDAO.ElectionDAO;
import com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO;
import com.electrovotesuperx.dao.OfflineDAO.VoterDAO;
import com.electrovotesuperx.model.OfflineModel.AuditLogEntry;
import com.electrovotesuperx.model.OfflineModel.Election;
import com.electrovotesuperx.model.OfflineModel.Member;
import com.electrovotesuperx.model.OfflineModel.PollingOfficerRequest;
import com.electrovotesuperx.utils.EncryptionUtil;
import com.electrovotesuperx.utils.Navigation;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OfflineAdminDashboard {

    private final Scene scene;
    private final BorderPane root;

    // DAOs
    private final PollingOfficerDAO officerDAO = new PollingOfficerDAO();
    private final ElectionDAO electionDAO = new ElectionDAO();
    private final VoterDAO voterDAO = new VoterDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    // Active View State
    private String currentTab = "OFFICERS";
    private boolean showRawEncryptedVoterData = false;

    // Colors & Styles
    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";
    private static final String BG_COLOR = "#F8FAFC";
    private static final String SIDEBAR_BG = "#0D1538";
    private static final String PRIMARY_BLUE = "#2563EB";
    private static final String SUCCESS_GREEN = "#059669";
    private static final String DANGER_RED = "#DC2626";
    private static final String WARNING_AMBER = "#D97706";

    // Nav Buttons
    private Button btnOfficers;
    private Button btnElections;
    private Button btnVoters;
    private Button btnAudit;

    private StackPane contentArea;

    public OfflineAdminDashboard() {
        // Ensure tables exist on entry
        DatabaseConfig.initializeDatabase();

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + "; " + FONT);

        // Sidebar
        root.setLeft(createSidebar());

        // Center Content Area
        VBox centerBox = new VBox();
        centerBox.getChildren().add(createTopHeader());

        contentArea = new StackPane();
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        centerBox.getChildren().add(contentArea);

        root.setCenter(centerBox);

        // Default to Officers Tab
        showOfficersTab();

        scene = new Scene(root, 1200, 780);
    }

    public Scene getScene() {
        return scene;
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================

    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        // Brand
        Label brandTitle = new Label("⚡ Offline Admin");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 900;");

        Label brandSub = new Label("SQLite Local Controller");
        brandSub.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: 600;");

        VBox brandBox = new VBox(3, brandTitle, brandSub);
        brandBox.setPadding(new Insets(8, 8, 20, 8));
        brandBox.setStyle("-fx-border-color: transparent transparent #1E293B transparent; -fx-border-width: 0 0 1 0;");

        // Menu Buttons
        btnOfficers = createNavButton("📋   Officer Requests");
        btnElections = createNavButton("◫   Elections");
        btnVoters = createNavButton("👥   Voters & Import");
        btnAudit = createNavButton("📜   Audit History");




        btnOfficers.setOnAction(e -> showOfficersTab());
        btnElections.setOnAction(e -> showElectionsTab());
        btnVoters.setOnAction(e -> showVotersTab());
        btnAudit.setOnAction(e -> showAuditTab());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // About Us Button

        Button btnAbout = createNavButton("🗳   About Us");
    
        btnAbout.setStyle(getNavStyle(false) + "-fx-text-fill: #34D399;");
        btnAbout.setOnAction(e -> {
            Navigation.goTo(new AboutUs().getScene());
         });


         Button btnLogout = createNavButton("🚪   Log Out");
        btnLogout.setStyle(getNavStyle(false) + "-fx-text-fill: #F87171;");
        btnLogout.setOnAction(e -> {
            com.electrovotesuperx.config.SessionManager.clearSession();
            Stage stage = Navigation.getStage();
            if (stage != null) {
                com.electrovotesuperx.view.LoginPageView.Login.loginStage = stage;
                com.electrovotesuperx.utils.Navigation.init(stage);
                com.electrovotesuperx.view.LoginPageView.Login login = new com.electrovotesuperx.view.LoginPageView.Login();
                Scene loginScene = login.getScene(() -> stage.close());
                stage.setTitle("ElectraVote");
                stage.setScene(loginScene);
                stage.setMaximized(true);
                stage.show();
            }
        });

        sidebar.getChildren().addAll(
                brandBox,
                new Region() {{ setPrefHeight(10); }},
                btnOfficers,
                btnElections,
                btnVoters,
                btnAudit,
                spacer,
                btnAbout,
                btnLogout
        );

        return sidebar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(getNavStyle(false));
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }

    private String getNavStyle(boolean active) {
        if (active) {
            return FONT + "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 13.5px; -fx-font-weight: bold; -fx-padding: 10 14; -fx-background-radius: 8;";
        } else {
            return FONT + "-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 13.5px; -fx-font-weight: 600; -fx-padding: 10 14; -fx-background-radius: 8;";
        }
    }

    private void updateNavStyles() {
        btnOfficers.setStyle(getNavStyle("OFFICERS".equals(currentTab)));
        btnElections.setStyle(getNavStyle("ELECTIONS".equals(currentTab)));
        btnVoters.setStyle(getNavStyle("VOTERS".equals(currentTab)));
        btnAudit.setStyle(getNavStyle("AUDIT".equals(currentTab)));
    }

    // =========================================================
    // TOP HEADER
    // =========================================================

    private HBox createTopHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 28, 16, 28));
        header.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        VBox titleBox = new VBox(2);
        Label title = new Label("Offline Main Admin Dashboard");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Authorized administrator portal for offline SQLite election operations");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Database Status Badge
        HBox dbPill = new HBox(6);
        dbPill.setAlignment(Pos.CENTER);
        dbPill.setPadding(new Insets(6, 12, 6, 12));
        dbPill.setStyle("-fx-background-color: #ECFDF5; -fx-background-radius: 20; -fx-border-color: #A7F3D0; -fx-border-radius: 20;");

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #059669; -fx-font-size: 12px;");
        Label dbLabel = new Label("Database: OFFLINE Connected (Local)");
        dbLabel.setStyle(FONT + "-fx-text-fill: #065F46; -fx-font-size: 11.5px; -fx-font-weight: bold;");
        dbPill.getChildren().addAll(dot, dbLabel);

        header.getChildren().addAll(titleBox, spacer, dbPill);
        return header;
    }

    // =========================================================
    // 1. POLLING OFFICERS TAB (ACCEPT / REJECT)
    // =========================================================

    private void showOfficersTab() {
        currentTab = "OFFICERS";
        updateNavStyles();

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 28, 28, 28));

        // Stats Cards
        int total = officerDAO.countByStatus("ALL");
        int pending = officerDAO.countByStatus("PENDING");
        int approved = officerDAO.countByStatus("APPROVED");
        int rejected = officerDAO.countByStatus("REJECTED");

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                createStatCard("Total Requests", String.valueOf(total), "#2563EB", "👥"),
                createStatCard("Pending Approval", String.valueOf(pending), "#D97706", "⏳"),
                createStatCard("Approved Officers", String.valueOf(approved), "#059669", "✓"),
                createStatCard("Rejected", String.valueOf(rejected), "#DC2626", "✗")
        );

        // Table
        TableView<PollingOfficerRequest> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12;");

        TableColumn<PollingOfficerRequest, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<PollingOfficerRequest, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<PollingOfficerRequest, String> colStation = new TableColumn<>("Station Name");
        colStation.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStationName()));

        TableColumn<PollingOfficerRequest, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));

        TableColumn<PollingOfficerRequest, String> colPin = new TableColumn<>("Approval PIN (8h)");
        colPin.setCellValueFactory(d -> {
            PollingOfficerRequest req = d.getValue();
            if (req.getApprovalPin() == null || req.getApprovalPin().isBlank()) {
                return new SimpleStringProperty("—");
            }
            if (req.isPinExpired()) {
                return new SimpleStringProperty(req.getApprovalPin() + " ❌ Expired");
            } else {
                return new SimpleStringProperty(req.getApprovalPin() + " 🟢 (" + req.getRemainingHours() + "h left)");
            }
        });
        colPin.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label pinLabel = new Label(item);
                    if (item.contains("Expired")) {
                        pinLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DC2626; -fx-font-size: 11px;");
                    } else if (item.contains("left")) {
                        pinLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #059669; -fx-font-size: 11px;");
                    } else {
                        pinLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 11px;");
                    }
                    setGraphic(pinLabel);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<PollingOfficerRequest, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(3, 8, 3, 8));
                    if ("APPROVED".equalsIgnoreCase(status)) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<PollingOfficerRequest, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(260);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnAccept = new Button("✓ Accept");
            private final Button btnRenewPin = new Button("🔄 Renew PIN");
            private final Button btnReject = new Button("✗ Reject");
            private final HBox box = new HBox(6, btnAccept, btnRenewPin, btnReject);

            {
                btnAccept.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 6; -fx-cursor: hand;");
                btnRenewPin.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 6; -fx-cursor: hand;");
                btnReject.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 6; -fx-cursor: hand;");
                box.setAlignment(Pos.CENTER);

                btnAccept.setOnAction(e -> {
                    PollingOfficerRequest req = getTableView().getItems().get(getIndex());
                    String newPin = officerDAO.renewApprovalPin(req.getEmail());
                    try {
                        auditLogDAO.log("OFFICER_APPROVED", null, null, null,
                                "Admin approved Polling Officer: " + req.getName() + " (" + req.getEmail() + ") - PIN: " + (newPin != null ? newPin : req.getApprovalPin()));
                    } catch (Exception ignored) {}
                    showOfficersTab();
                });

                btnRenewPin.setOnAction(e -> {
                    PollingOfficerRequest req = getTableView().getItems().get(getIndex());
                    String newPin = officerDAO.renewApprovalPin(req.getEmail());
                    try {
                        auditLogDAO.log("PIN_RENEWED", null, null, null,
                                "Admin renewed 8-hour Approval PIN for Polling Officer: " + req.getName() + " (" + req.getEmail() + ") - New PIN: " + newPin);
                    } catch (Exception ignored) {}
                    showOfficersTab();
                });

                btnReject.setOnAction(e -> {
                    PollingOfficerRequest req = getTableView().getItems().get(getIndex());
                    officerDAO.updateStatus(req.getEmail(), "REJECTED");
                    try {
                        auditLogDAO.log("OFFICER_REJECTED", null, null, null,
                                "Admin rejected Polling Officer: " + req.getName() + " (" + req.getEmail() + ")");
                    } catch (Exception ignored) {}
                    showOfficersTab();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colName, colEmail, colStation, colPhone, colPin, colStatus, colActions);

        // Filter Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, email, or station...");
        searchField.setPrefWidth(280);
        searchField.setStyle(FONT + "-fx-padding: 8 12; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("ALL", "PENDING", "APPROVED", "REJECTED");
        statusFilter.setValue("ALL");
        statusFilter.setStyle(FONT + "-fx-padding: 4 8; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(FONT + "-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-padding: 7 14; -fx-cursor: hand;");

        Runnable reloadData = () -> {
            List<PollingOfficerRequest> list = officerDAO.getAllRequests();
            String st = statusFilter.getValue();
            String q = searchField.getText().trim().toLowerCase();

            ObservableList<PollingOfficerRequest> filtered = FXCollections.observableArrayList();
            for (PollingOfficerRequest r : list) {
                boolean matchStatus = "ALL".equalsIgnoreCase(st) || st.equalsIgnoreCase(r.getStatus());
                boolean matchSearch = q.isEmpty() ||
                        (r.getName() != null && r.getName().toLowerCase().contains(q)) ||
                        (r.getEmail() != null && r.getEmail().toLowerCase().contains(q)) ||
                        (r.getStationName() != null && r.getStationName().toLowerCase().contains(q));
                if (matchStatus && matchSearch) {
                    filtered.add(r);
                }
            }
            table.setItems(filtered);
        };

        searchField.textProperty().addListener((obs, old, n) -> reloadData.run());
        statusFilter.setOnAction(e -> reloadData.run());
        btnRefresh.setOnAction(e -> reloadData.run());

        filterBar.getChildren().addAll(searchField, statusFilter, btnRefresh);

        reloadData.run();

        content.getChildren().addAll(statsRow, filterBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        contentArea.getChildren().setAll(content);
    }

    // =========================================================
    // 2. ELECTIONS TAB (CREATE / EDIT / CLOSE)
    // =========================================================

    private void showElectionsTab() {
        currentTab = "ELECTIONS";
        updateNavStyles();

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 28, 28, 28));

        // Stats
        List<Election> allElections = new ArrayList<>();
        try {
            allElections = electionDAO.getAllElections();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long openCount = allElections.stream().filter(el -> "OPEN".equalsIgnoreCase(el.getStatus())).count();
        long closedCount = allElections.stream().filter(el -> "CLOSED".equalsIgnoreCase(el.getStatus())).count();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                createStatCard("Total Elections", String.valueOf(allElections.size()), "#2563EB", "◫"),
                createStatCard("Active / Open", String.valueOf(openCount), "#059669", "🟢"),
                createStatCard("Closed Elections", String.valueOf(closedCount), "#64748B", "🔒")
        );

        // Action & Filter Bar
        HBox actionBar = new HBox(12);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button btnCreate = new Button("+ Create New Election");
        btnCreate.setStyle(FONT + "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        btnCreate.setOnAction(e -> showCreateElectionDialog());

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("Search elections...");
        searchField.setPrefWidth(260);
        searchField.setStyle(FONT + "-fx-padding: 8 12; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        actionBar.getChildren().addAll(btnCreate, sp, searchField);

        // Elections Table
        TableView<Election> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12;");

        TableColumn<Election, String> colId = new TableColumn<>("Election ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getElectionId()));

        TableColumn<Election, String> colName = new TableColumn<>("Election Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Election, String> colVoters = new TableColumn<>("Enrolled Voters");
        colVoters.setCellValueFactory(d -> {
            int c = electionDAO.getVoterCountForElection(d.getValue().getElectionId());
            return new SimpleStringProperty(c + " voters");
        });

        TableColumn<Election, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(3, 8, 3, 8));
                    if ("OPEN".equalsIgnoreCase(status)) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Election, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(250);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏ Edit");
            private final Button btnToggleStatus = new Button();
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnToggleStatus, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-padding: 5 10; -fx-font-size: 11px; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 6; -fx-cursor: hand;");
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    Election el = getTableView().getItems().get(getIndex());
                    showEditElectionDialog(el);
                });

                btnToggleStatus.setOnAction(e -> {
                    Election el = getTableView().getItems().get(getIndex());
                    try {
                        if ("OPEN".equalsIgnoreCase(el.getStatus())) {
                            electionDAO.closeElection(el.getElectionId());
                            auditLogDAO.log("ELECTION_CLOSED", null, el.getElectionId(), null,
                                    "Admin closed election: " + el.getName());
                        } else {
                            electionDAO.reopenElection(el.getElectionId());
                            auditLogDAO.log("ELECTION_REOPENED", null, el.getElectionId(), null,
                                    "Admin reopened election: " + el.getName());
                        }
                        showElectionsTab();
                    } catch (SQLException ex) {
                        showError("Failed to update election status: " + ex.getMessage());
                    }
                });

                btnDelete.setOnAction(e -> {
                    Election el = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete '" + el.getName() + "' (" + el.getElectionId() + ")?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText("Delete Election Confirmation");
                    confirm.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.YES) {
                            try {
                                electionDAO.deleteElection(el.getElectionId());
                                auditLogDAO.log("ELECTION_DELETED", null, el.getElectionId(), null,
                                        "Admin deleted election: " + el.getName());
                                showElectionsTab();
                            } catch (SQLException ex) {
                                showError("Failed to delete election: " + ex.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Election el = getTableView().getItems().get(getIndex());
                    if ("OPEN".equalsIgnoreCase(el.getStatus())) {
                        btnToggleStatus.setText("🔒 Close");
                        btnToggleStatus.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6; -fx-cursor: hand;");
                    } else {
                        btnToggleStatus.setText("🔓 Reopen");
                        btnToggleStatus.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6; -fx-cursor: hand;");
                    }
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colId, colName, colVoters, colStatus, colActions);

        Runnable refreshTable = () -> {
            try {
                List<Election> list = electionDAO.getAllElections();
                String q = searchField.getText().trim().toLowerCase();
                ObservableList<Election> filtered = FXCollections.observableArrayList();
                for (Election el : list) {
                    if (q.isEmpty() || el.getName().toLowerCase().contains(q) || el.getElectionId().toLowerCase().contains(q)) {
                        filtered.add(el);
                    }
                }
                table.setItems(filtered);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        };

        searchField.textProperty().addListener((obs, old, n) -> refreshTable.run());
        refreshTable.run();

        content.getChildren().addAll(statsRow, actionBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        contentArea.getChildren().setAll(content);
    }

    private void showCreateElectionDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create Election");
        dialog.setHeaderText("Create a New Offline Election");

        ButtonType btnTypeSave = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeSave, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField tfName = new TextField();
        tfName.setPromptText("e.g. Student Council Election 2026");
        tfName.setPrefWidth(320);

        form.getChildren().addAll(new Label("Election Title / Name:"), tfName);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeSave) {
                return tfName.getText().trim();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isEmpty()) {
                try {
                    if (electionDAO.electionExists(name)) {
                        showError("An election with this name already exists.");
                        return;
                    }
                    electionDAO.createElection(name);
                    auditLogDAO.log("ELECTION_CREATED", null, null, null, "Admin created election: " + name);
                    showElectionsTab();
                } catch (SQLException ex) {
                    showError("Failed to create election: " + ex.getMessage());
                }
            }
        });
    }

    private void showEditElectionDialog(Election election) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Election");
        dialog.setHeaderText("Edit Election Details: " + election.getElectionId());

        ButtonType btnTypeSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeSave, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField tfName = new TextField(election.getName());
        tfName.setPrefWidth(320);

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("OPEN", "CLOSED");
        cbStatus.setValue(election.getStatus());

        form.getChildren().addAll(
                new Label("Election Name:"), tfName,
                new Label("Status:"), cbStatus
        );
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeSave) {
                return tfName.getText().trim();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isEmpty()) {
                try {
                    electionDAO.updateElection(election.getElectionId(), newName, cbStatus.getValue());
                    auditLogDAO.log("ELECTION_EDITED", null, election.getElectionId(), null,
                            "Admin edited election: " + newName + " (" + cbStatus.getValue() + ")");
                    showElectionsTab();
                } catch (SQLException ex) {
                    showError("Failed to update election: " + ex.getMessage());
                }
            }
        });
    }

    // =========================================================
    // 3. VOTERS TAB (ADD / CSV IMPORT / ENCRYPTION TOGGLE)
    // =========================================================

    private void showVotersTab() {
        currentTab = "VOTERS";
        updateNavStyles();

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 28, 28, 28));

        int totalVoters = 0;
        try {
            totalVoters = voterDAO.count();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                createStatCard("Registered Voters", String.valueOf(totalVoters), "#2563EB", "👥")
               
        );

        // Action Toolbar
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnAddVoter = new Button("+ Add Voter");
        btnAddVoter.setStyle(FONT + "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAddVoter.setOnAction(e -> showAddVoterDialog());

        Button btnImportCsv = new Button("📁 Import Voters (CSV)");
        btnImportCsv.setStyle(FONT + "-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        btnImportCsv.setOnAction(e -> handleCsvImport());

        // Button btnToggleEnc = new Button(showRawEncryptedVoterData ? "🔓 Show Decrypted View" : "🔒 Show Raw View (Encrypted)");
        // btnToggleEnc.setStyle(FONT + "-fx-background-color: white; -fx-border-color: #7C3AED; -fx-text-fill: #7C3AED; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 7 14; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;");
        // btnToggleEnc.setOnAction(e -> {
        //     showRawEncryptedVoterData = !showRawEncryptedVoterData;
        //     showVotersTab();
        // });

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("Search voters...");
        searchField.setPrefWidth(240);
        searchField.setStyle(FONT + "-fx-padding: 8 12; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        toolbar.getChildren().addAll(btnAddVoter, btnImportCsv, sp, searchField);

        // Table
        TableView<Member> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12;");

        TableColumn<Member, String> colVoterId = new TableColumn<>("Voter ID");
        colVoterId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVoterId()));

        TableColumn<Member, String> colName = new TableColumn<>(showRawEncryptedVoterData ? "Full Name " : "Full Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Member, String> colEmail = new TableColumn<>(showRawEncryptedVoterData ? "Email " : "Email ");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail() != null ? d.getValue().getEmail() : "—"));

        TableColumn<Member, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender()));

        TableColumn<Member, String> colPhone = new TableColumn<>(showRawEncryptedVoterData ? "Phone " : "Phone ");
        colPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));

        TableColumn<Member, String> colElections = new TableColumn<>("Assigned Elections");
        colElections.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVotingStatus()));

        TableColumn<Member, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(3, 8, 3, 8));
                    if ("ACTIVE".equalsIgnoreCase(status)) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Member, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(140);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggleStatus = new Button("Toggle");
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnToggleStatus, btnDelete);

            {
                btnToggleStatus.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-padding: 4 8; -fx-font-size: 11px; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 8; -fx-background-radius: 6; -fx-cursor: hand;");
                box.setAlignment(Pos.CENTER);

                btnToggleStatus.setOnAction(e -> {
                    Member m = getTableView().getItems().get(getIndex());
                    String newStatus = "ACTIVE".equalsIgnoreCase(m.getStatus()) ? "SUSPENDED" : "ACTIVE";
                    try {
                        voterDAO.updateStatus(m.getVoterId(), newStatus);
                        auditLogDAO.log("VOTER_STATUS_CHANGED", m.getVoterId(), null, null,
                                "Admin changed status to: " + newStatus);
                        showVotersTab();
                    } catch (SQLException ex) {
                        showError("Failed to update status: " + ex.getMessage());
                    }
                });

                btnDelete.setOnAction(e -> {
                    Member m = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete voter " + m.getVoterId() + " from all offline elections?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.YES) {
                            try {
                                voterDAO.deleteVoter(m.getVoterId());
                                auditLogDAO.log("VOTER_DELETED", m.getVoterId(), null, null,
                                        "Admin deleted voter: " + m.getVoterId());
                                showVotersTab();
                            } catch (SQLException ex) {
                                showError("Failed to delete voter: " + ex.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colVoterId, colName, colEmail, colGender, colPhone, colElections, colStatus, colActions);

        Runnable refreshTable = () -> {
            try {
                List<Member> list = voterDAO.getAllVoters(!showRawEncryptedVoterData);
                String q = searchField.getText().trim().toLowerCase();
                ObservableList<Member> filtered = FXCollections.observableArrayList();
                for (Member m : list) {
                    if (q.isEmpty() ||
                            (m.getVoterId() != null && m.getVoterId().toLowerCase().contains(q)) ||
                            (m.getName() != null && m.getName().toLowerCase().contains(q)) ||
                            (m.getPhone() != null && m.getPhone().toLowerCase().contains(q))) {
                        filtered.add(m);
                    }
                }
                table.setItems(filtered);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        };

        searchField.textProperty().addListener((obs, old, n) -> refreshTable.run());
        refreshTable.run();

        content.getChildren().addAll(statsRow, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        contentArea.getChildren().setAll(content);
    }

    private void showAddVoterDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Individual Voter");
        dialog.setHeaderText("Add New Voter with SQLite Encryption");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfName = new TextField();
        TextField tfEmail = new TextField();
        ComboBox<String> cbGender = new ComboBox<>();
        cbGender.getItems().addAll("Male", "Female", "Other");
        cbGender.setValue("Male");

        TextField tfPhone = new TextField();

        ComboBox<String> cbElection = new ComboBox<>();
        try {
            for (Election el : electionDAO.getAllElections()) {
                cbElection.getItems().add(el.getElectionId() + " - " + el.getName());
            }
        } catch (Exception ignored) {}
        if (!cbElection.getItems().isEmpty()) {
            cbElection.getSelectionModel().selectFirst();
        }

        grid.add(new Label("Full Name: *"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(tfEmail, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(cbGender, 1, 2);
        grid.add(new Label("Phone: *"), 0, 3);
        grid.add(tfPhone, 1, 3);
        grid.add(new Label("Assign Election:"), 0, 4);
        grid.add(cbElection, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                String name = tfName.getText().trim();
                String email = tfEmail.getText().trim();
                String gender = cbGender.getValue();
                String phone = tfPhone.getText().trim();
                String selectedEl = cbElection.getValue();
                String electionId = (selectedEl != null && selectedEl.contains(" - "))
                        ? selectedEl.split(" - ")[0] : null;

                if (name.isEmpty() || phone.isEmpty()) {
                    showError("Name and Phone are required fields.");
                    return;
                }

                try {
                    String voterId = voterDAO.insertVoterEncrypted(name, email, gender, phone, electionId);
                    auditLogDAO.log("VOTER_ADDED", voterId, electionId, null,
                            "Admin added encrypted voter: " + name + " (ID: " + voterId + ")");
                    showVotersTab();
                } catch (SQLException ex) {
                    showError("Failed to add voter: " + ex.getMessage());
                }
            }
        });
    }

    private void handleCsvImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Voter CSV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = Navigation.getStage();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        // Ask for target election
        List<Election> elections = new ArrayList<>();
        try {
            elections = electionDAO.getAllElections();
        } catch (SQLException ignored) {}

        ChoiceDialog<String> elChoice = new ChoiceDialog<>();
        elChoice.setTitle("Assign Imported Voters");
        elChoice.setHeaderText("Choose election for imported voters:");
        for (Election el : elections) {
            elChoice.getItems().add(el.getElectionId() + " - " + el.getName());
        }
        if (!elections.isEmpty()) {
            elChoice.setSelectedItem(elChoice.getItems().get(0));
        }

        Optional<String> chosen = elChoice.showAndWait();
        String electionId = chosen.map(s -> s.split(" - ")[0]).orElse(null);

        // Read and parse CSV
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Support comma, semicolon, tab
                String delimiter = line.contains(",") ? "," : (line.contains(";") ? ";" : "\t");
                String[] parts = line.split(delimiter, -1);

                // Skip header if first line looks like header
                if (firstLine) {
                    firstLine = false;
                    String headerFirst = parts[0].toLowerCase();
                    if (headerFirst.contains("name") || headerFirst.contains("voter") || headerFirst.contains("first")) {
                        continue;
                    }
                }
                rows.add(parts);
            }
        } catch (Exception ex) {
            showError("Error reading CSV file: " + ex.getMessage());
            return;
        }

        if (rows.isEmpty()) {
            showError("No voter records found in selected file.");
            return;
        }

        VoterDAO.BatchImportResult result = voterDAO.batchImportVoters(rows, electionId);

        try {
            auditLogDAO.log("VOTERS_BATCH_IMPORTED", null, electionId, null,
                    "Admin batch imported " + result.imported + " voters from file: " + file.getName());
        } catch (Exception ignored) {}

        Alert summary = new Alert(Alert.AlertType.INFORMATION);
        summary.setTitle("Voter Import Complete");
        summary.setHeaderText("Batch Import Summary");
        summary.setContentText("Successfully imported and encrypted: " + result.imported + " voters.\n"
                + "Skipped / Invalid rows: " + result.skipped + "\n"
                + (result.errors.isEmpty() ? "" : "Errors encountered: " + String.join(", ", result.errors)));
        summary.showAndWait();

        showVotersTab();
    }

    // =========================================================
    // 4. AUDIT HISTORY TAB
    // =========================================================

    private void showAuditTab() {
        currentTab = "AUDIT";
        updateNavStyles();

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 28, 28, 28));

        int totalLogs = auditLogDAO.countLogs();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                createStatCard("Total Audit Events", String.valueOf(totalLogs), "#2563EB", "📜"),
                createStatCard("Storage Engine", "Offline ", "#059669", "💾"),
                createStatCard("Tamper Protection", "Immutable Chronological", "#7C3AED", "🔒")
        );

        // Filter Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search logs by keyword, ID, token...");
        searchField.setPrefWidth(280);
        searchField.setStyle(FONT + "-fx-padding: 8 12; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        ComboBox<String> actionFilter = new ComboBox<>();
        actionFilter.getItems().addAll("ALL", "VOTER_VERIFIED", "VOTE_COMPLETED", "VERIFICATION_FAILED",
                "OFFICER_APPROVED", "OFFICER_REJECTED", "ELECTION_CREATED", "ELECTION_CLOSED", "VOTER_ADDED", "VOTERS_BATCH_IMPORTED");
        actionFilter.setValue("ALL");
        actionFilter.setStyle(FONT + "-fx-padding: 4 8; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        Button btnExport = new Button("📥 Export Audit to CSV");
        btnExport.setStyle(FONT + "-fx-background-color: white; -fx-border-color: #2563EB; -fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-padding: 7 14; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;");

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(FONT + "-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-padding: 7 14; -fx-cursor: hand;");

        filterBar.getChildren().addAll(searchField, actionFilter, btnExport, btnRefresh);

        // Table
        TableView<AuditLogEntry> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12;");

        TableColumn<AuditLogEntry, String> colTime = new TableColumn<>("Timestamp");
        colTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCreatedAt()));

        TableColumn<AuditLogEntry, String> colAction = new TableColumn<>("Action");
        colAction.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAction()));
        colAction.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String action, boolean empty) {
                super.updateItem(action, empty);
                if (empty || action == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(action);
                    badge.setPadding(new Insets(3, 8, 3, 8));
                    if (action.contains("APPROVED") || action.contains("COMPLETED")) {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else if (action.contains("REJECTED") || action.contains("FAILED") || action.contains("DELETED")) {
                        badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else if (action.contains("CREATED") || action.contains("IMPORTED") || action.contains("ADDED")) {
                        badge.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: bold; -fx-background-radius: 12; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<AuditLogEntry, String> colVoter = new TableColumn<>("Voter ID");
        colVoter.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVoterId()));

        TableColumn<AuditLogEntry, String> colElection = new TableColumn<>("Election ID");
        colElection.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getElectionId()));

        TableColumn<AuditLogEntry, String> colToken = new TableColumn<>("Token");
        colToken.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getToken()));

        TableColumn<AuditLogEntry, String> colDetails = new TableColumn<>("Details");
        colDetails.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDetails()));

        table.getColumns().addAll(colTime, colAction, colVoter, colElection, colToken, colDetails);

        Runnable reloadData = () -> {
            String act = actionFilter.getValue();
            String q = searchField.getText().trim();
            List<AuditLogEntry> logs = q.isEmpty()
                    ? ("ALL".equalsIgnoreCase(act) ? auditLogDAO.getAllLogs() : auditLogDAO.getLogsByAction(act))
                    : auditLogDAO.searchLogs(q);

            if (!"ALL".equalsIgnoreCase(act) && !q.isEmpty()) {
                logs.removeIf(l -> !act.equalsIgnoreCase(l.getAction()));
            }

            table.setItems(FXCollections.observableArrayList(logs));
        };

        btnExport.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Audit Log Export");
            fc.setInitialFileName("audit_logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            File saveFile = fc.showSaveDialog(Navigation.getStage());
            if (saveFile != null) {
                try (FileWriter fw = new FileWriter(saveFile)) {
                    fw.write("Timestamp,Action,VoterID,ElectionID,Token,Details\n");
                    for (AuditLogEntry l : table.getItems()) {
                        fw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                                l.getCreatedAt(), l.getAction(), l.getVoterId(), l.getElectionId(), l.getToken(),
                                l.getDetails().replace("\"", "\"\"")));
                    }
                    Alert ok = new Alert(Alert.AlertType.INFORMATION, "Audit logs exported successfully to:\n" + saveFile.getAbsolutePath());
                    ok.showAndWait();
                } catch (Exception ex) {
                    showError("Export failed: " + ex.getMessage());
                }
            }
        });

        searchField.textProperty().addListener((obs, old, n) -> reloadData.run());
        actionFilter.setOnAction(e -> reloadData.run());
        btnRefresh.setOnAction(e -> reloadData.run());

        reloadData.run();

        content.getChildren().addAll(statsRow, filterBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        contentArea.getChildren().setAll(content);
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private VBox createStatCard(String label, String value, String accentColor, String icon) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setPrefWidth(240);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 16px;");

        Label lbl = new Label(label);
        lbl.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #64748B;");

        top.getChildren().addAll(iconLbl, lbl);

        Label valLbl = new Label(value);
        valLbl.setStyle(FONT + "-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + accentColor + ";");

        card.getChildren().addAll(top, valLbl);
        return card;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Operation Error");
        alert.showAndWait();
    }
}
