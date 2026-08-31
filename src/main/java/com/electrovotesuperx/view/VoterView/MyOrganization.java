package com.electrovotesuperx.view.VoterView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.model.AdminModel.ElectionData;
import com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * My Organizations View:
 * Displays ALL organizations that the user has previously joined or is an active member/admin of.
 * Enables seamless one-click switching of active organization context across all voter dashboard tabs.
 */
public class MyOrganization {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN = "#10B981";
    private static final String BLUE = "#1464F4";
    private static final String AMBER = "#F59E0B";
    private static final String RED = "#EF4444";

    private static final String[] THEME_PALETTE = {
            "#7B4DFF", "#1464F4", "#059669", "#D97706", "#DC2626", "#0284C7", "#4F46E5", "#0D9488"
    };

    public static VBox createMyOrganizationView(List<Map<String, String>> legacyRecords) {
        return createMyOrganizationView();
    }

    /**
     * Dynamically generates the My Organizations view with real-time multi-organization discovery.
     */
    public static VBox createMyOrganizationView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Top Heading & Quick Actions
        HBox topHeaderRow = new HBox(15);
        topHeaderRow.setAlignment(Pos.CENTER_LEFT);

        VBox heading = new VBox(6);
        Text title = new Text("My Organizations 🏢");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text(
                "Manage your institutional memberships, switch active organization context, and inspect election details.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(700);
        heading.getChildren().addAll(title, subtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button joinNewBtn = new Button("＋ Join Another Organization");
        joinNewBtn.setStyle(
                "-fx-background-color: #1464F4; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        joinNewBtn.setOnAction(e -> VoterDashboard.showPage(JoinOrganization.createJoinOrganizationView()));

        topHeaderRow.getChildren().addAll(heading, topSpacer, joinNewBtn);

        // Feedback Banner Container (for smooth switch notifications)
        VBox feedbackContainer = new VBox();

        VBox orgsList = new VBox(16);

        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(35));
        loadingBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
        
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(36, 36);
        Label loadingText = new Label("Discovering your organization memberships from cloud...");
        loadingText.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: bold;");
        loadingBox.getChildren().addAll(spinner, loadingText);
        orgsList.getChildren().add(loadingBox);

        // Asynchronous multi-org fetch
        Thread fetchThread = new Thread(() -> {
            String uid = SessionManager.voterUid != null ? SessionManager.voterUid : SessionManager.adminUid;
            String email = SessionManager.voterEmail != null && !SessionManager.voterEmail.isBlank() 
                    ? SessionManager.voterEmail 
                    : (SessionManager.loggedInEmail != null ? SessionManager.loggedInEmail : SessionManager.adminEmail);
            String idToken = SessionManager.idToken;

            List<UserOrganizationMembership> memberships = FirebaseDatabaseService.getUserOrganizations(uid, email, idToken);

            // Fetch live election counts for each organization
            for (UserOrganizationMembership m : memberships) {
                try {
                    List<ElectionData> elecs = ElectionDAO.getElectionsByOrg(m.getJoinCode(), idToken);
                    m.setActiveElectionsCount(elecs != null ? elecs.size() : 0);
                } catch (Exception ignored) {
                    m.setActiveElectionsCount(0);
                }
            }

            Platform.runLater(() -> {
                orgsList.getChildren().clear();

                if (memberships.isEmpty()) {
                    VBox emptyBox = new VBox(12);
                    emptyBox.setAlignment(Pos.CENTER);
                    emptyBox.setPadding(new Insets(35));
                    emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
                    
                    Label emptyIcon = new Label("🏢");
                    emptyIcon.setFont(Font.font(36));
                    Text emptyTitle = new Text("No Organizations Found");
                    emptyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                    emptyTitle.setFill(Color.web(TEXT));
                    Text emptySub = new Text("You have not joined any organizations yet. Click 'Join Another Organization' to enter a join code.");
                    emptySub.setFill(Color.web(SECONDARY));
                    emptySub.setFont(Font.font(13));

                    Button emptyJoinBtn = new Button("Join an Organization Now");
                    emptyJoinBtn.setStyle("-fx-background-color: #1464F4; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                    emptyJoinBtn.setOnAction(e -> VoterDashboard.showPage(JoinOrganization.createJoinOrganizationView()));

                    emptyBox.getChildren().addAll(emptyIcon, emptyTitle, emptySub, emptyJoinBtn);
                    orgsList.getChildren().add(emptyBox);
                } else {
                    for (int i = 0; i < memberships.size(); i++) {
                        UserOrganizationMembership m = memberships.get(i);
                        String themeColor = THEME_PALETTE[Math.abs(m.getJoinCode().hashCode()) % THEME_PALETTE.length];
                        orgsList.getChildren().add(createMembershipCard(m, themeColor, feedbackContainer, content));
                    }
                }
            });
        });
        fetchThread.setDaemon(true);
        fetchThread.start();

        content.getChildren().addAll(topHeaderRow, feedbackContainer, new Separator(), orgsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createMembershipCard(
            UserOrganizationMembership m,
            String themeColor,
            VBox feedbackContainer,
            VBox parentContent) {

        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        
        boolean isActive = m.getJoinCode().equalsIgnoreCase(SessionManager.joinCode);

        String cardBorder = isActive ? "#0284C7" : BORDER;
        String cardBg = isActive ? "#F0F9FF" : "white";

        card.setStyle(
                "-fx-background-color: " + cardBg + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + cardBorder + ";" +
                "-fx-border-width: " + (isActive ? "1.5;" : "1;") +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.05), 8, 0, 0, 3);");

        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web(themeColor));
        String initial = !m.getOrganizationName().isEmpty() ? m.getOrganizationName().substring(0, 1).toUpperCase() : "O";
        Text avatarInitial = new Text(initial);
        avatarInitial.setFill(Color.WHITE);
        avatarInitial.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        StackPane avatarPane = new StackPane(avatarCircle, avatarInitial);

        // Text info
        VBox textBox = new VBox(3);
        Text name = new Text(m.getOrganizationName());
        name.setFill(Color.web(TEXT));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        String roleTitle = "ADMIN".equalsIgnoreCase(m.getRole()) 
                ? "Organization Administrator (" + m.getStatus() + ")"
                : "Institutional Member (" + m.getStatus() + ")";
        Text role = new Text(roleTitle);
        role.setFill(Color.web(SECONDARY));
        role.setFont(Font.font("Arial", 12.5));
        textBox.getChildren().addAll(name, role);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side action or active context badge
        if (isActive) {
            String activeBg = "PENDING".equalsIgnoreCase(m.getStatus()) ? "#FFFBEB" : "#ECFDF5";
            String activeText = "PENDING".equalsIgnoreCase(m.getStatus()) ? "#D97706" : "#059669";
            String activeBorder = "PENDING".equalsIgnoreCase(m.getStatus()) ? "#FDE68A" : "#A7F3D0";
            String activeLabel = "PENDING".equalsIgnoreCase(m.getStatus()) ? "Active Context (Pending Approval) ⏳" : "Active Context ✓";

            Label statusBadge = new Label(activeLabel);
            statusBadge.setStyle(
                    "-fx-background-color: " + activeBg + ";" +
                    "-fx-text-fill: " + activeText + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 7 14;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + activeBorder + ";" +
                    "-fx-border-radius: 12;");
            topRow.getChildren().addAll(avatarPane, textBox, spacer, statusBadge);
        } else if ("PENDING".equalsIgnoreCase(m.getStatus())) {
            Button pendingBtn = new Button("🔒 Pending Admin Approval ⏳");
            pendingBtn.setStyle(
                    "-fx-background-color: #FFFBEB; -fx-text-fill: #D97706; -fx-font-weight: bold; " +
                    "-fx-font-size: 12px; -fx-padding: 7 14; -fx-background-radius: 8; -fx-border-color: #FDE68A; -fx-border-radius: 8; -fx-cursor: hand;");

            pendingBtn.setOnAction(e -> {
                showPendingAlertBanner(feedbackContainer, m.getOrganizationName(), m.getJoinCode());
            });

            topRow.getChildren().addAll(avatarPane, textBox, spacer, pendingBtn);
        } else {
            Button switchBtn = new Button("🔄 Switch to this Organization");
            switchBtn.setStyle(
                    "-fx-background-color: #0284C7; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-font-size: 12.5px; -fx-padding: 7 14; -fx-background-radius: 8; -fx-cursor: hand;");

            switchBtn.setOnMouseEntered(e -> switchBtn.setStyle(
                    "-fx-background-color: #0369A1; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-font-size: 12.5px; -fx-padding: 7 14; -fx-background-radius: 8; -fx-cursor: hand;"));
            switchBtn.setOnMouseExited(e -> switchBtn.setStyle(
                    "-fx-background-color: #0284C7; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-font-size: 12.5px; -fx-padding: 7 14; -fx-background-radius: 8; -fx-cursor: hand;"));

            switchBtn.setOnAction(e -> {
                // Switch Active Session
                SessionManager.joinCode = m.getJoinCode();
                SessionManager.organizationName = m.getOrganizationName();
                SessionManager.voterStatus = m.getStatus();
                if (m.getMemberName() != null && !m.getMemberName().isBlank()) {
                    SessionManager.voterName = m.getMemberName();
                }

                // Update Voter Dashboard
                VoterDashboard.loadVoterData(
                        SessionManager.voterName,
                        SessionManager.currentRole != null ? SessionManager.currentRole : "voter",
                        m.getStatus(),
                        String.valueOf(m.getActiveElectionsCount()),
                        m.getOrganizationName()
                );

                // Show feedback banner & refresh
                showSwitchNotification(feedbackContainer, m.getOrganizationName(), m.getJoinCode());

                // Re-render view to show new active context
                Platform.runLater(() -> {
                    VoterDashboard.showPage(MyOrganization.createMyOrganizationView());
                });
            });

            topRow.getChildren().addAll(avatarPane, textBox, spacer, switchBtn);
        }

        // Details Row
        HBox detailsRow = new HBox(12);
        detailsRow.setAlignment(Pos.CENTER_LEFT);

        Text codeBadge = new Text("📌 Join Code: " + m.getJoinCode());
        codeBadge.setFill(Color.web("#0284C7"));
        codeBadge.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Text elecBadge = new Text("• " + m.getActiveElectionsCount() + " Active Election(s)");
        elecBadge.setFill(Color.web(GREEN));
        elecBadge.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        String statusColor = "ACCEPTED".equalsIgnoreCase(m.getStatus()) ? GREEN : ("PENDING".equalsIgnoreCase(m.getStatus()) ? AMBER : RED);
        Text statusBadgeText = new Text("• Status: " + m.getStatus());
        statusBadgeText.setFill(Color.web(statusColor));
        statusBadgeText.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        detailsRow.getChildren().addAll(codeBadge, elecBadge, statusBadgeText);

        card.getChildren().addAll(topRow, new Separator(), detailsRow);
        return card;
    }

    private static void showSwitchNotification(VBox container, String orgName, String joinCode) {
        container.getChildren().clear();

        HBox banner = new HBox(10);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(12, 16, 12, 16));
        banner.setStyle(
                "-fx-background-color: #ECFDF5; -fx-background-radius: 8; -fx-border-color: #10B981; -fx-border-radius: 8;");

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 14px;");

        Label msg = new Label("Active Organization context switched to: " + orgName + " (" + joinCode + ")");
        msg.setStyle("-fx-font-size: 13px; -fx-text-fill: #065F46; -fx-font-weight: bold;");

        banner.getChildren().addAll(icon, msg);
        container.getChildren().add(banner);

        FadeTransition ft = new FadeTransition(Duration.millis(400), banner);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private static void showPendingAlertBanner(VBox container, String orgName, String joinCode) {
        container.getChildren().clear();

        HBox banner = new HBox(12);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(14, 18, 14, 18));
        banner.setStyle(
                "-fx-background-color: #FFFBEB; -fx-background-radius: 10; -fx-border-color: #F59E0B; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.15), 6, 0, 0, 2);");

        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 20px;");

        VBox textCol = new VBox(2);
        Label title = new Label("Membership Approval Required (" + joinCode + ")");
        title.setStyle("-fx-font-size: 13.5px; -fx-text-fill: #92400E; -fx-font-weight: 800;");

        Label msg = new Label("Your request to join '" + orgName + "' is currently PENDING. The administrator of this organization must review and ACCEPT your request in their Admin Portal (Admin > Voters) before you can access elections or cast votes in this organization.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: #78350F; -fx-font-weight: 500;");

        textCol.getChildren().addAll(title, msg);
        banner.getChildren().addAll(icon, textCol);
        container.getChildren().add(banner);

        FadeTransition ft = new FadeTransition(Duration.millis(400), banner);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}