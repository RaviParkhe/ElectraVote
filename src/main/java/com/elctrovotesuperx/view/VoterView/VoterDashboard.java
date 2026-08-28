package com.elctrovotesuperx.view.VoterView;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.elctrovotesuperx.model.AdminModel.ElectionData;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
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
import javafx.stage.Stage;

import java.util.List;

public class VoterDashboard extends Application {

    private final String SIDEBAR = "#08264A";
    private final String SIDEBAR_LIGHT = "#0D3565";
    private final String BLUE = "#1464F4";
    private final String GREEN = "#19B66A";
    private final String PURPLE = "#7B4DFF";
    private final String ORANGE = "#F59E0B";
    private final String RED = "#EF4444";

    private final String BACKGROUND = "#F7F9FC";
    private final String TEXT = "#172033";
    private final String SECONDARY = "#6B7280";
    private final String BORDER = "#E6EAF0";

    public static Stage VoterDashboardStage;
    public static BorderPane dashboardCenter;

    private static Text sidebarOrgNameText;
    private static Text sidebarRoleText;
    private static Text homeWelcomeTitleText;
    private static Text statValue1; // Verified Status
    private static Text statValue2; // Elections Participated / Active Ballots
    private static Text statValue3; // Current Membership / Organization Name

    private static Button homeBtnRef;
    private static VBox menuRef;

    @Override
    public void start(Stage stage) {
        VoterDashboardStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        VBox sidebar = createSidebar();
        HBox topBar = createTopBar();

        VBox dashboardContent = createVoterHomeContentStatic();

        dashboardCenter = new BorderPane();
        dashboardCenter.setTop(topBar);
        dashboardCenter.setCenter(dashboardContent);

        root.setLeft(sidebar);
        root.setCenter(dashboardCenter);

        Scene scene = new Scene(root);

        stage.setTitle("ElectraVote - Voter Portal");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
        stage.toFront();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setSpacing(6);
        sidebar.setPadding(new Insets(20, 12, 15, 12));
        sidebar.setStyle("-fx-background-color: " + SIDEBAR + ";");

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Text logoIcon = new Text("▣");
        logoIcon.setFill(Color.WHITE);
        logoIcon.setFont(Font.font(25));

        Text logo = new Text("Electra");
        logo.setFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 23));

        Text voteTextLogo = new Text("Vote");
        voteTextLogo.setFill(Color.web("#19B66A"));
        voteTextLogo.setFont(Font.font("Arial", FontWeight.BOLD, 23));

        HBox logoText = new HBox(0, logo, voteTextLogo);
        logoBox.getChildren().addAll(logoIcon, logoText);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #23466D;");

        HBox organization = new HBox(10);
        organization.setPadding(new Insets(12, 5, 12, 5));
        organization.setAlignment(Pos.CENTER_LEFT);

        Circle orgCircle = new Circle(20);
        orgCircle.setFill(Color.WHITE);

        Text orgIcon = new Text("V");
        orgIcon.setFill(Color.web(SIDEBAR));
        orgIcon.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        StackPane orgAvatar = new StackPane(orgCircle, orgIcon);
        VBox orgText = new VBox(2);

        String currentOrg = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                ? SessionManager.organizationName
                : "Active Organization";
        sidebarOrgNameText = new Text(currentOrg);
        sidebarOrgNameText.setFill(Color.WHITE);
        sidebarOrgNameText.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));

        String voterName = SessionManager.voterName != null && !SessionManager.voterName.isBlank()
                ? SessionManager.voterName
                : "Eligible Voter";
        String voterStatus = SessionManager.voterStatus != null ? SessionManager.voterStatus : "Eligible Voter";
        sidebarRoleText = new Text(voterName + " (" + voterStatus + ")");
        sidebarRoleText.setFill(Color.web("#A9BCD3"));
        sidebarRoleText.setFont(Font.font(10.5));

        orgText.getChildren().addAll(sidebarRoleText, sidebarOrgNameText);

        Region orgSpacer = new Region();
        HBox.setHgrow(orgSpacer, Priority.ALWAYS);
        organization.getChildren().addAll(orgAvatar, orgText, orgSpacer);

        VBox menu = new VBox(4);
        menuRef = menu;

        homeBtnRef = createMenuButton("⌂", "Home", true);
        Button activeElecBtn = createMenuButton("▣", "Active Elections", false);
        Button voteBtn = createMenuButton("🗳", "Vote", false);
        Button applyCandBtn = createMenuButton("✍", "Apply as Candidate", false);
        Button appBtn = createMenuButton("📄", "My Applications", false);
        Button historyBtn = createMenuButton("◷", "Voting History", false);
        Button orgBtn = createMenuButton("🏢", "My Organization", false);
        Button joinOrgBtn = createMenuButton("＋", "Join Organization", false);
        Button notifBtn = createMenuButton("🔔", "Notifications", false);

        homeBtnRef.setOnAction(e -> {
            setActiveMenu(menu, homeBtnRef);
            showPage(createVoterHomeContentStatic());
        });

        activeElecBtn.setOnAction(e -> {
            setActiveMenu(menu, activeElecBtn);
            showPage(ActiveElection.createActiveElectionView());
        });

        voteBtn.setOnAction(e -> {
            setActiveMenu(menu, voteBtn);
            showPage(Vote.createOrganizationSelectionView());
        });

        applyCandBtn.setOnAction(e -> {
            setActiveMenu(menu, applyCandBtn);
            showPage(ApplyCandidateView.createApplyCandidateView(null));
        });

        appBtn.setOnAction(e -> {
            setActiveMenu(menu, appBtn);
            showPage(MyApplication.createMyApplicationView());
        });

        historyBtn.setOnAction(e -> {
            setActiveMenu(menu, historyBtn);
            showPage(VotingHistory.createVotingHistoryView());
        });

        orgBtn.setOnAction(e -> {
            setActiveMenu(menu, orgBtn);
            showPage(MyOrganization.createMyOrganizationView());
        });

        joinOrgBtn.setOnAction(e -> {
            setActiveMenu(menu, joinOrgBtn);
            showPage(JoinOrganization.createJoinOrganizationView());
        });

        Button quickPollBtn = createMenuButton("📊", "Quick Polls", false);
        quickPollBtn.setOnAction(e -> {
            com.elctrovotesuperx.view.QuickPollView.QuickPoll qp = new com.elctrovotesuperx.view.QuickPollView.QuickPoll();
            Scene prevScene = VoterDashboardStage.getScene();
            VoterDashboardStage.setScene(qp.getScene(() -> {
                VoterDashboardStage.setScene(prevScene);
                VoterDashboardStage.setMaximized(true);
            }));
            VoterDashboardStage.setMaximized(true);
        });

        menu.getChildren().addAll(homeBtnRef, activeElecBtn, voteBtn, applyCandBtn, appBtn, historyBtn, orgBtn, joinOrgBtn, notifBtn, quickPollBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator bottomSeparator = new Separator();
        bottomSeparator.setStyle("-fx-background-color: #23466D;");

        Button logout = createMenuButton("⇥", "Logout", false);
        logout.setOnAction(e -> {
            System.out.println("Voter logging out...");
            com.elctrovotesuperx.config.SessionManager.clearSession();
            Stage currentStage = VoterDashboardStage != null ? VoterDashboardStage : com.electrovotesuperx.utils.Navigation.getStage();
            com.elctrovotesuperx.view.HomePageView.HomePage home = new com.elctrovotesuperx.view.HomePageView.HomePage(currentStage);
            Scene homeScene = home.getScene(() -> {
                if (currentStage != null) currentStage.close();
            });
            if (currentStage != null) {
                currentStage.setScene(homeScene);
                currentStage.setMaximized(true);
                currentStage.show();
            }
        });

        sidebar.getChildren().addAll(logoBox, separator, organization, menu, spacer, bottomSeparator, logout);
        return sidebar;
    }

    private static void setActiveMenu(VBox menuBox, Button selectedButton) {
        for (javafx.scene.Node node : menuBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-background-radius: 7;" +
                                "-fx-cursor: hand;");
            }
        }
        selectedButton.setStyle(
                "-fx-background-color: #1464F4;" +
                        "-fx-background-radius: 7;" +
                        "-fx-cursor: hand;");
    }

    private Button createMenuButton(String icon, String text, boolean selected) {
        Button button = new Button();
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(38);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        Text iconText = new Text(icon);
        iconText.setFill(Color.WHITE);
        iconText.setFont(Font.font(16));

        Text label = new Text(text);
        label.setFill(Color.WHITE);
        label.setFont(Font.font("Arial", selected ? FontWeight.BOLD : FontWeight.NORMAL, 12.5));

        HBox content = new HBox(12, iconText, label);
        content.setAlignment(Pos.CENTER_LEFT);
        button.setGraphic(content);

        if (selected) {
            button.setStyle(
                    "-fx-background-color: " + BLUE + ";" +
                            "-fx-background-radius: 7;" +
                            "-fx-cursor: hand;");
        } else {
            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-radius: 7;" +
                            "-fx-cursor: hand;");
            button.setOnMouseEntered(e -> {
                if (!button.getStyle().contains(BLUE)) {
                    button.setStyle(
                            "-fx-background-color: " + SIDEBAR_LIGHT + "; -fx-background-radius: 7; -fx-cursor: hand;");
                }
            });
            button.setOnMouseExited(e -> {
                if (!button.getStyle().contains(BLUE)) {
                    button.setStyle("-fx-background-color: transparent; -fx-background-radius: 7;");
                }
            });
        }
        return button;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 25, 0, 25));
        topBar.setPrefHeight(65);
        topBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;");

        Label pageTitle = new Label("Voter Portal & Ballot Center");
        pageTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label securityBadge = new Label("🔒 Zero-Knowledge Ballot Encrypted");
        securityBadge.setStyle(
                "-fx-background-color: #ECFDF5;" +
                        "-fx-text-fill: #059669;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 12;" +
                        "-fx-background-radius: 12;");

        topBar.getChildren().addAll(pageTitle, spacer, securityBadge);
        return topBar;
    }

    public static VBox createVoterHomeContentStatic() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EDF2F7);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        String voterName = SessionManager.voterName != null && !SessionManager.voterName.isBlank()
                ? SessionManager.voterName : "Voter";
        String voterStatus = SessionManager.voterStatus != null ? SessionManager.voterStatus : "Verified";
        String orgName = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                ? SessionManager.organizationName : "Active Organization";

        VBox heading = new VBox(4);
        homeWelcomeTitleText = new Text("Welcome back, " + voterName + "! 👋");
        homeWelcomeTitleText.setFill(Color.web("#172033"));
        homeWelcomeTitleText.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Text subtitle = new Text("Here is a quick summary of your voter account status, live elections, and ballot readiness.");
        subtitle.setFill(Color.web("#6B7280"));
        subtitle.setFont(Font.font(12.5));
        heading.getChildren().addAll(homeWelcomeTitleText, subtitle);

        if (statValue1 == null) statValue1 = new Text(voterStatus);
        else statValue1.setText(voterStatus);

        if (statValue2 == null) statValue2 = new Text("...");
        if (statValue3 == null) statValue3 = new Text(orgName);
        else statValue3.setText(orgName);

        HBox statsRow = new HBox(15);
        statsRow.getChildren().addAll(
                createCustomStatCard("Verified Status", statValue1, "ID Confirmed", "#19B66A"),
                createCustomStatCard("Active Ballots", statValue2, "Pending ballots", "#1464F4"),
                createCustomStatCard("Current Membership", statValue3, "Active Context", "#7B4DFF"));

        // Fetch real active ballots count from Firestore
        Thread t = new Thread(() -> {
            try {
                if (SessionManager.joinCode != null && !SessionManager.joinCode.isBlank()) {
                    List<ElectionData> elecs = ElectionDAO.getElectionsByOrg(SessionManager.joinCode, SessionManager.idToken);
                    Platform.runLater(() -> {
                        if (statValue2 != null) statValue2.setText(String.valueOf(elecs.size()));
                    });
                } else {
                    Platform.runLater(() -> {
                        if (statValue2 != null) statValue2.setText("0");
                    });
                }
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();

        VBox banner = new VBox(8);
        banner.setPadding(new Insets(16));
        banner.setStyle(
                "-fx-background-color: linear-gradient(to right, #08264A, #0D3565);" +
                        "-fx-background-radius: 10;");

        Text bannerTitle = new Text("Portal Guidelines & Cryptographic Security");
        bannerTitle.setFill(Color.WHITE);
        bannerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Text bannerDesc = new Text(
                "ElectraVote utilizes client-side zero-knowledge proofs and homomorphic encryption. Your cryptographic receipts ensure your eligibility is verified while keeping your vote selections private. Check the 'Active Elections' or 'Vote' tabs to participate in ongoing sessions.");
        bannerDesc.setFill(Color.web("#E2E8F0"));
        bannerDesc.setFont(Font.font(12));
        bannerDesc.setWrappingWidth(900);

        banner.getChildren().addAll(bannerTitle, bannerDesc);
        content.getChildren().addAll(heading, statsRow, banner);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createCustomStatCard(String title, Text valText, String sub, String color) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E6EAF0;" +
                        "-fx-border-radius: 10;");

        Text titleLbl = new Text(title);
        titleLbl.setFill(Color.web("#6B7280"));
        titleLbl.setFont(Font.font(11.5));

        valText.setFill(Color.web("#172033"));
        valText.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Text subLbl = new Text(sub);
        subLbl.setFill(Color.web(color));
        subLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        card.getChildren().addAll(titleLbl, valText, subLbl);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    public static void loadVoterData(String fullName, String role, String verificationStatus,
            String totalElectionsVoted, String organizationName) {
        if (sidebarOrgNameText != null)
            sidebarOrgNameText.setText(organizationName);
        if (sidebarRoleText != null)
            sidebarRoleText.setText(fullName + " (" + role + ")");
        if (homeWelcomeTitleText != null)
            homeWelcomeTitleText.setText("Welcome back, " + fullName + "! 👋");
        if (statValue1 != null)
            statValue1.setText(verificationStatus);
        if (statValue2 != null)
            statValue2.setText(totalElectionsVoted);
        if (statValue3 != null)
            statValue3.setText(organizationName);
    }

    public static void updateActiveOrganization(String orgName, String status, String activeBallotsCount) {
        if (sidebarOrgNameText != null) {
            sidebarOrgNameText.setText(orgName);
        }
        if (statValue1 != null)
            statValue1.setText(status);
        if (statValue2 != null)
            statValue2.setText(activeBallotsCount);
        if (statValue3 != null)
            statValue3.setText(orgName);

        returnHomeFromVoting();
    }

    public static void returnHomeFromVoting() {
        if (menuRef != null && homeBtnRef != null) {
            setActiveMenu(menuRef, homeBtnRef);
        }
        showPage(createVoterHomeContentStatic());
    }

    public static void showPage(javafx.scene.Node page) {
        if (dashboardCenter != null) {
            dashboardCenter.setCenter(page);
        }
    }
}