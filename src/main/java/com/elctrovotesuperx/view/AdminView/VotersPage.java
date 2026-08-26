package com.elctrovotesuperx.view.AdminView;

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

public class VotersPage extends VBox {

    // =========================================================
    // MULTI-TENANCY CONTEXT & DATA STATE
    // =========================================================
    private final String currentOrganizationId = "ORG_ABC_COLLEGE";
    private final List<VoterModel> voters = new ArrayList<>();

    private final VBox requestList = new VBox(10);
    private final VBox approvedVoterList = new VBox(10);
    private final Label requestCount = new Label("0 Record(s)");
    private String currentFilter = "PENDING";

    private TextField searchField;
    private Button pendingBtn;
    private Button acceptedBtn;
    private Button rejectedBtn;
    private ProgressIndicator loadingSpinner;

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public VotersPage() {
        setSpacing(18);
        setPadding(new Insets(20, 28, 24, 28));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                        + FONT);

        HBox header = buildHeader();
        HBox filterBar = buildFilterBar();
        VBox requestSection = buildRequestSection();
        VBox approvedSection = buildApprovedSection();

        getChildren().addAll(
                header,
                filterBar,
                requestSection,
                new Separator() {
                    {
                        setStyle("-fx-background-color: #cbd5e1; -fx-opacity: 0.6;");
                    }
                },
                approvedSection);

        fetchVotersFromFirebase();
    }

    // =========================================================
    // 1. HEADER SECTION
    // =========================================================
    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Voter Management & Registration");
        title.setStyle(FONT
                + "-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label(
                "Review incoming member eligibility requests, verify identities, and manage approved voters");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Button syncBtn = new Button("↻ Sync Data");
        syncBtn.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 11.5px; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand;");
        syncBtn.setOnAction(e -> fetchVotersFromFirebase());

        header.getChildren().addAll(titleBox, headerSpace, syncBtn);
        return header;
    }

    // =========================================================
    // 2. FILTER & SEARCH TOOLBAR
    // =========================================================
    private HBox buildFilterBar() {
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10, 16, 10, 16));
        filterBar.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 10, 0, 0, 2);");

        searchField = new TextField();
        searchField.setPromptText("🔍  Search voters by name, ID, or department...");
        searchField.setPrefWidth(300);
        searchField.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-text-fill: #0f172a; -fx-prompt-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px; -fx-font-weight: 600;");
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            refreshRequests();
            refreshApprovedVoters();
        });

        loadingSpinner = new ProgressIndicator();
        loadingSpinner.setPrefSize(18, 18);
        loadingSpinner.setVisible(false);

        Region barSpace = new Region();
        HBox.setHgrow(barSpace, Priority.ALWAYS);

        pendingBtn = new Button("Pending Requests");
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
            refreshRequests();
        });

        acceptedBtn.setOnAction(e -> {
            currentFilter = "ACCEPTED";
            setActiveButton(acceptedBtn, "#059669", "#ecfdf5", "#10b981");
            setNormalButton(pendingBtn, "#f8fafc", "#2563eb", "#cbd5e1");
            setNormalButton(rejectedBtn, "#f8fafc", "#dc2626", "#cbd5e1");
            refreshRequests();
        });

        rejectedBtn.setOnAction(e -> {
            currentFilter = "REJECTED";
            setActiveButton(rejectedBtn, "#dc2626", "#fef2f2", "#ef4444");
            setNormalButton(pendingBtn, "#f8fafc", "#2563eb", "#cbd5e1");
            setNormalButton(acceptedBtn, "#f8fafc", "#059669", "#cbd5e1");
            refreshRequests();
        });

        filterBar.getChildren().addAll(searchField, loadingSpinner, barSpace, pendingBtn, acceptedBtn, rejectedBtn);
        return filterBar;
    }

    // =========================================================
    // 3. REQUEST LIST SECTION
    // =========================================================
    private VBox buildRequestSection() {
        VBox section = new VBox(8);

        HBox requestHeader = new HBox();
        requestHeader.setAlignment(Pos.CENTER_LEFT);

        Label requestTitle = new Label("Voter Requests Directory");
        requestTitle.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region requestSpace = new Region();
        HBox.setHgrow(requestSpace, Priority.ALWAYS);

        requestCount.setStyle(FONT
                + "-fx-text-fill: #4338ca; -fx-font-size: 11.5px; -fx-font-weight: 800; -fx-background-color: #e0e7ff; -fx-padding: 3 10; -fx-background-radius: 12;");

        requestHeader.getChildren().addAll(requestTitle, requestSpace, requestCount);

        ScrollPane requestScroll = new ScrollPane(requestList);
        requestScroll.setFitToWidth(true);
        requestScroll.setPrefHeight(260);
        requestScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        section.getChildren().addAll(requestHeader, requestScroll);
        return section;
    }

    // =========================================================
    // 4. APPROVED VOTERS SECTION
    // =========================================================
    private VBox buildApprovedSection() {
        VBox section = new VBox(8);

        HBox approvedHeader = new HBox();
        approvedHeader.setAlignment(Pos.CENTER_LEFT);

        Label approvedTitle = new Label("Approved & Verified Voters");
        approvedTitle.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label verifiedBadge = new Label("ELIGIBLE VOTERS LEDGER");
        verifiedBadge.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 800; -fx-font-size: 10px; -fx-padding: 3 10; -fx-background-radius: 16; -fx-border-color: #10b981; -fx-border-radius: 16;");

        approvedHeader.getChildren().addAll(approvedTitle, space, verifiedBadge);

        ScrollPane approvedScroll = new ScrollPane(approvedVoterList);
        approvedScroll.setFitToWidth(true);
        approvedScroll.setPrefHeight(260);
        approvedScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        section.getChildren().addAll(approvedHeader, approvedScroll);
        return section;
    }

    // =========================================================
    // BUTTON STYLING HELPERS
    // =========================================================
    private void setNormalButton(Button button, String background, String textColor, String borderColor) {
        button.setStyle(FONT +
                "-fx-background-color: " + background + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 8;" +
                "-fx-font-weight: 800;" +
                "-fx-font-size: 11.5px;" +
                "-fx-padding: 6 14;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");
    }

    private void setActiveButton(Button button, String textColor, String bgAccent, String borderColor) {
        button.setStyle(FONT +
                "-fx-background-color: " + bgAccent + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 1.5;" +
                "-fx-font-weight: 900;" +
                "-fx-font-size: 11.5px;" +
                "-fx-padding: 6 14;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");
    }

    // =========================================================
    // ASYNCHRONOUS FIREBASE OPERATIONS
    // =========================================================
    private void fetchVotersFromFirebase() {
        loadingSpinner.setVisible(true);

        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }

            return getInitialFallbackData();
        }).thenAccept(fetchedList -> Platform.runLater(() -> {
            voters.clear();
            voters.addAll(fetchedList);
            loadingSpinner.setVisible(false);
            refreshRequests();
            refreshApprovedVoters();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                showBaseAlert("Sync Error", "Failed to fetch data from Firebase: " + ex.getMessage());
            });
            return null;
        });
    }

    private void updateVoterStatusInFirebase(VoterModel voter, String newStatus) {
        loadingSpinner.setVisible(true);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }

            voter.status = newStatus;
        }).thenRun(() -> Platform.runLater(() -> {
            loadingSpinner.setVisible(false);
            refreshRequests();
            refreshApprovedVoters();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                showBaseAlert("Update Error", "Failed to update record in Firebase: " + ex.getMessage());
            });
            return null;
        });
    }

    // =========================================================
    // REFRESH REQUESTS VIEW
    // =========================================================
    private void refreshRequests() {
        requestList.getChildren().clear();
        String query = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        int count = 0;

        for (VoterModel voter : voters) {
            boolean matchesFilter = voter.status.equalsIgnoreCase(currentFilter);
            boolean matchesSearch = query.isEmpty()
                    || voter.name.toLowerCase().contains(query)
                    || voter.id.toLowerCase().contains(query)
                    || voter.department.toLowerCase().contains(query);

            if (matchesFilter && matchesSearch) {
                requestList.getChildren().add(createRequestCard(voter));
                count++;
            }
        }

        requestCount.setText(count + " Record(s)");

        if (count == 0) {
            VBox emptyBox = new VBox(4);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefHeight(90);
            emptyBox.setStyle(
                    "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

            Label emptyTitle = new Label("No " + currentFilter.toLowerCase() + " voter requests found");
            emptyTitle.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #475569;");

            Label emptyText = new Label("Incoming membership requests will appear in this feed.");
            emptyText.setStyle(FONT + "-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

            emptyBox.getChildren().addAll(emptyTitle, emptyText);
            requestList.getChildren().add(emptyBox);
        }
    }

    // =========================================================
    // COMPACT REQUEST CARD
    // =========================================================
    private HBox createRequestCard(VoterModel voter) {
        HBox card = new HBox(12);
        card.setMinHeight(82);
        card.setPadding(new Insets(10, 16, 10, 16));
        card.setAlignment(Pos.CENTER_LEFT);

        String borderAccentColor;
        String leftHighlightBar;
        if ("ACCEPTED".equalsIgnoreCase(voter.status)) {
            borderAccentColor = "#34d399";
            leftHighlightBar = "#059669";
        } else if ("REJECTED".equalsIgnoreCase(voter.status)) {
            borderAccentColor = "#fca5a5";
            leftHighlightBar = "#dc2626";
        } else {
            borderAccentColor = "#93c5fd";
            leftHighlightBar = "#2563eb";
        }

        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + borderAccentColor + " " + borderAccentColor + " " + borderAccentColor + " "
                + leftHighlightBar + "; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1 1 1 4; " +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label avatar = new Label(voter.name.isEmpty() ? "?" : voter.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-font-size: 15px; -fx-font-weight: 900; -fx-background-radius: 20; -fx-border-color: #bfdbfe; -fx-border-radius: 20; -fx-border-width: 1.2;");

        VBox leftInfo = new VBox(2);
        Label name = new Label(voter.name);
        name.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label voterId = new Label("ID: " + voter.id + " • " + voter.category);
        voterId.setStyle(FONT + "-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 800;");

        Label category = new Label(voter.department);
        category.setStyle(FONT + "-fx-text-fill: #475569; -fx-font-size: 10.5px; -fx-font-weight: 600;");

        leftInfo.getChildren().addAll(name, voterId, category);

        VBox middleInfo = new VBox(2);
        Label contact = new Label("✉ " + voter.email);
        contact.setStyle(FONT + "-fx-text-fill: #334155; -fx-font-size: 10.5px; -fx-font-weight: 600;");

        Label phone = new Label("✆ " + voter.phone);
        phone.setStyle(FONT + "-fx-text-fill: #334155; -fx-font-size: 10.5px; -fx-font-weight: 600;");

        Label date = new Label("Date: " + voter.date);
        date.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 600;");

        middleInfo.getChildren().addAll(contact, phone, date);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button view = new Button("View");
        view.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 6; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(voter));
        actions.getChildren().add(view);

        if ("PENDING".equalsIgnoreCase(voter.status)) {
            Button approve = new Button("Approve");
            Button reject = new Button("Reject");

            approve.setStyle(FONT
                    + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 6; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6; -fx-cursor: hand;");
            reject.setStyle(FONT
                    + "-fx-background-color: #fef2f2; -fx-text-fill: #b91c1c; -fx-border-color: #ef4444; -fx-border-radius: 6; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6; -fx-cursor: hand;");

            approve.setOnAction(e -> approveVoter(voter));
            reject.setOnAction(e -> rejectVoter(voter));

            actions.getChildren().addAll(approve, reject);
        }

        card.getChildren().addAll(avatar, leftInfo, middleInfo, space, actions);
        return card;
    }

    // =========================================================
    // REFRESH APPROVED VOTERS VIEW
    // =========================================================
    private void refreshApprovedVoters() {
        approvedVoterList.getChildren().clear();
        String query = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        int count = 0;

        for (VoterModel voter : voters) {
            boolean isApproved = "ACCEPTED".equalsIgnoreCase(voter.status);
            boolean matchesSearch = query.isEmpty()
                    || voter.name.toLowerCase().contains(query)
                    || voter.id.toLowerCase().contains(query)
                    || voter.department.toLowerCase().contains(query);

            if (isApproved && matchesSearch) {
                approvedVoterList.getChildren().add(createApprovedVoterCard(voter));
                count++;
            }
        }

        if (count == 0) {
            VBox emptyBox = new VBox(4);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefHeight(90);
            emptyBox.setStyle(
                    "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

            Label title = new Label("No approved voters to display");
            title.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #475569;");

            Label message = new Label("Approved members will appear in this certified voting roll.");
            message.setStyle(FONT + "-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

            emptyBox.getChildren().addAll(title, message);
            approvedVoterList.getChildren().add(emptyBox);
        }
    }

    // =========================================================
    // COMPACT APPROVED VOTER CARD
    // =========================================================
    private HBox createApprovedVoterCard(VoterModel voter) {
        HBox card = new HBox(12);
        card.setMinHeight(68);
        card.setPadding(new Insets(8, 16, 8, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #86efac #86efac #86efac #16a34a; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1 1 1 4; " +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label avatar = new Label(voter.name.isEmpty() ? "?" : voter.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(36, 36);
        avatar.setMaxSize(36, 36);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-size: 14px; -fx-font-weight: 900; -fx-background-radius: 18; -fx-border-color: #a7f3d0; -fx-border-radius: 18; -fx-border-width: 1.2;");

        VBox info = new VBox(2);
        Label name = new Label(voter.name);
        name.setStyle(FONT + "-fx-font-size: 13.5px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label id = new Label("ID: " + voter.id + " • " + voter.category + " • " + voter.department);
        id.setStyle(FONT + "-fx-text-fill: #059669; -fx-font-size: 10.5px; -fx-font-weight: 800;");

        info.getChildren().addAll(name, id);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label status = new Label("VERIFIED");
        status.setStyle(FONT
                + "-fx-background-color: #f0fdf4; -fx-text-fill: #15803d; -fx-font-weight: 900; -fx-font-size: 9.5px; -fx-padding: 3 8; -fx-background-radius: 14; -fx-border-color: #22c55e; -fx-border-radius: 14; -fx-border-width: 1;");

        Button view = new Button("View");
        view.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 6; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
        view.setOnAction(e -> showDetails(voter));

        card.getChildren().addAll(avatar, info, space, status, view);
        return card;
    }

    // =========================================================
    // DIALOGS & CONFIRMATIONS
    // =========================================================
    private void showDetails(VoterModel voter) {
        Dialog<ButtonType> dialog = createBaseDialog("Voter Registration Details",
                "Verified Member Profile & Membership Information", "👥", "#2563eb", "#eff6ff");

        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setPrefWidth(460);
        box.setStyle("-fx-background-color: #ffffff; " + FONT);

        VBox profileCard = new VBox(3);
        profileCard.setPadding(new Insets(10, 14, 10, 14));
        profileCard.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #818cf8; -fx-border-radius: 10; -fx-border-width: 1.2;");

        Label name = new Label(voter.name);
        name.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label idBadge = new Label("Official Voter ID: " + voter.id);
        idBadge.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-text-fill: #2563eb;");
        profileCard.getChildren().addAll(name, idBadge);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

        grid.add(createMetaField("Email Address", voter.email), 0, 0);
        grid.add(createMetaField("Phone Number", voter.phone), 0, 1);
        grid.add(createMetaField("Membership Category", voter.category), 1, 0);
        grid.add(createMetaField("Assigned Department", voter.department), 1, 1);
        grid.add(createMetaField("Year / Designation", voter.yearOrRole), 0, 2);
        grid.add(createMetaField("Request Date", voter.date), 1, 2);

        HBox statusStrip = new HBox(8);
        statusStrip.setAlignment(Pos.CENTER_LEFT);
        statusStrip.setPadding(new Insets(8, 12, 8, 12));
        statusStrip.setStyle(
                "-fx-background-color: #eff6ff; -fx-background-radius: 8; -fx-border-color: #bfdbfe; -fx-border-radius: 8;");

        Label statusTag = new Label("Current Authorization Status: " + voter.status);
        statusTag.setStyle(FONT + "-fx-font-weight: 800; -fx-text-fill: #1d4ed8; -fx-font-size: 11.5px;");
        statusStrip.getChildren().add(statusTag);

        box.getChildren().addAll(profileCard, grid, statusStrip);
        dialog.getDialogPane().setContent(box);

        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeBtn);

        styleDialogButtons(dialog, "#2563eb", closeBtn);
        dialog.showAndWait();
    }

    private void approveVoter(VoterModel voter) {
        Dialog<ButtonType> confirmation = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            confirmation.initOwner(AdminDashboard.AdminDashboardStage);
        }
        confirmation.setTitle("Approve Voter Request");

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
        Label title = new Label("Approve " + voter.name + "?");
        title.setWrapText(true);
        title.setStyle(FONT
                + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #064e3b; -fx-text-alignment: CENTER;");

        Label desc = new Label(
                "This member will be issued official voting authorization in Firebase for all organizational elections.");
        desc.setWrapText(true);
        desc.setStyle(FONT
                + "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-text-alignment: CENTER; -fx-line-spacing: 1.5px;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        confirmation.getDialogPane().setContent(contentBox);
        confirmation.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #10b981; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType approveBtnType = new ButtonType("Approve Voter", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getDialogPane().getButtonTypes().addAll(cancelBtnType, approveBtnType);

        styleDialogButtons(confirmation, "#059669", approveBtnType);

        confirmation.showAndWait().ifPresent(result -> {
            if (result == approveBtnType) {
                updateVoterStatusInFirebase(voter, "ACCEPTED");
            }
        });
    }

    private void rejectVoter(VoterModel voter) {
        Dialog<ButtonType> confirmation = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            confirmation.initOwner(AdminDashboard.AdminDashboardStage);
        }
        confirmation.setTitle("Reject Voter Request");

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
        Label title = new Label("Reject " + voter.name + "?");
        title.setWrapText(true);
        title.setStyle(FONT
                + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #991b1b; -fx-text-alignment: CENTER;");

        Label desc = new Label("This member's registration request will be denied in Firebase and marked as rejected.");
        desc.setWrapText(true);
        desc.setStyle(FONT
                + "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-text-alignment: CENTER; -fx-line-spacing: 1.5px;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        confirmation.getDialogPane().setContent(contentBox);
        confirmation.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType rejectBtnType = new ButtonType("Reject Request", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getDialogPane().getButtonTypes().addAll(cancelBtnType, rejectBtnType);

        styleDialogButtons(confirmation, "#dc2626", rejectBtnType);

        confirmation.showAndWait().ifPresent(result -> {
            if (result == rejectBtnType) {
                updateVoterStatusInFirebase(voter, "REJECTED");
            }
        });
    }

    private void errorIconStyle(Label icon) {
        icon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 900;");
    }

    private VBox createMetaField(String title, String val) {
        VBox v = new VBox(2);
        Label l = new Label(title);
        l.setStyle(FONT + "-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-text-fill: #64748b;");

        Label value = new Label(val);
        value.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        v.getChildren().addAll(l, value);
        return v;
    }

    private Dialog<ButtonType> createBaseDialog(String title, String headerSubtitle, String iconGlyph, String iconColor,
            String iconBgHex) {
        Dialog<ButtonType> dialog = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            dialog.initOwner(AdminDashboard.AdminDashboardStage);
        }
        dialog.setTitle(title);

        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(14, 20, 14, 20));
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
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #818cf8; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12; "
                        + FONT);

        return dialog;
    }

    private void styleDialogButtons(Dialog<ButtonType> dialog, String primaryColor, ButtonType primaryType) {
        Button primaryBtn = (Button) dialog.getDialogPane().lookupButton(primaryType);
        if (primaryBtn != null) {
            primaryBtn.setStyle(FONT + "-fx-background-color: " + primaryColor
                    + "; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;");
        }

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setStyle(FONT
                    + "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: 800; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-border-color: #cbd5e1; -fx-border-radius: 8;");
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
    private List<VoterModel> getInitialFallbackData() {
        List<VoterModel> list = new ArrayList<>();
        list.add(new VoterModel("DOC_001", "VOT001", "Arjun Sharma", "arjun@gmail.com", "9876500001", "Student",
                "Computer Engineering", "Third Year", "14 Aug 2026", "PENDING"));
        list.add(new VoterModel("DOC_002", "VOT002", "Sneha Patil", "sneha@gmail.com", "9876500002", "Student",
                "Information Technology", "Third Year", "14 Aug 2026", "PENDING"));
        list.add(new VoterModel("DOC_003", "VOT003", "Dr. Rahul Joshi", "rahul@college.edu", "9876500003", "Faculty",
                "Information Technology", "Professor", "13 Aug 2026", "PENDING"));
        list.add(new VoterModel("DOC_004", "VOT004", "Priya Kulkarni", "priya@gmail.com", "9876500004", "Student",
                "Computer Engineering", "Final Year", "12 Aug 2026", "ACCEPTED"));
        list.add(new VoterModel("DOC_005", "VOT005", "Rohit More", "rohit@gmail.com", "9876500005", "Student",
                "Mechanical Engineering", "Second Year", "11 Aug 2026", "REJECTED"));
        list.add(new VoterModel("DOC_006", "VOT006", "Amit Deshmukh", "amit@college.edu", "9876500006", "Faculty",
                "Mechanical Engineering", "Assistant Professor", "10 Aug 2026", "ACCEPTED"));
        return list;
    }

    // =========================================================
    // FIRESTORE ENTITY MODEL CLASS (DIRECT FIELD ACCESS)
    // =========================================================
    public static class VoterModel {
        public String documentId;
        public String id;
        public String name;
        public String email;
        public String phone;
        public String category;
        public String department;
        public String yearOrRole;
        public String date;
        public String status;

        public VoterModel(String documentId, String id, String name, String email, String phone, String category,
                String department, String yearOrRole, String date, String status) {
            this.documentId = documentId;
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.category = category;
            this.department = department;
            this.yearOrRole = yearOrRole;
            this.date = date;
            this.status = status;
        }

        public static VoterModel fromFirestoreDocument(String docId, Map<String, Object> data) {
            return new VoterModel(
                    docId,
                    (String) data.getOrDefault("voterId", "N/A"),
                    (String) data.getOrDefault("fullName", "Unknown"),
                    (String) data.getOrDefault("email", ""),
                    (String) data.getOrDefault("phone", ""),
                    (String) data.getOrDefault("category", "Student"),
                    (String) data.getOrDefault("department", "General"),
                    (String) data.getOrDefault("yearOrRole", "Member"),
                    (String) data.getOrDefault("joinedAt",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))),
                    (String) data.getOrDefault("status", "PENDING"));
        }

        public Map<String, Object> toFirestoreMap(String organizationId) {
            Map<String, Object> map = new HashMap<>();
            map.put("organizationId", organizationId);
            map.put("voterId", id);
            map.put("fullName", name);
            map.put("email", email);
            map.put("phone", phone);
            map.put("category", category);
            map.put("department", department);
            map.put("yearOrRole", yearOrRole);
            map.put("joinedAt", date);
            map.put("status", status);
            return map;
        }
    }
}