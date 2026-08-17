
package com.elctrovotesuperx.view.HomePageView;

import com.elctrovotesuperx.view.Page;
import com.elctrovotesuperx.view.CommonView.Sidebar;

import com.elctrovotesuperx.view.OrganizationView.Organizations;
import com.elctrovotesuperx.view.AdminView.AdminDashboard;
import com.elctrovotesuperx.view.AdminView.Members;
import com.elctrovotesuperx.view.AdminView.CandidateDashboard;
import com.elctrovotesuperx.view.AdminView.ElectionPage;
import com.elctrovotesuperx.view.AdminView.Results;
import com.elctrovotesuperx.view.AdminView.Reports;
import com.elctrovotesuperx.view.AdminView.Notifications;
import com.elctrovotesuperx.view.AdminView.Profile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Homepage implements Page {

    private final Stage stage;

    private Scene homeScene;

    private Sidebar sidebar;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Homepage(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // GET SCENE
    // =========================================================

    @Override
    public Scene getScene(Runnable loginCallback) {

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setStyle(
            "-fx-background-color: #F5F7FB;"
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        sidebar = new Sidebar();

        root.setLeft(
            sidebar.getSidebar()
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content = new VBox(24);

        content.setPadding(
            new Insets(30, 35, 35, 35)
        );

        content.setStyle(
            "-fx-background-color: #F5F7FB;"
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header = new HBox();

        header.setAlignment(
            Pos.CENTER_LEFT
        );

        VBox headerText = new VBox(5);

        Label welcome = new Label(
            "Welcome back, Admin"
        );

        welcome.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                30
            )
        );

        welcome.setStyle(
            "-fx-text-fill: #172554;"
        );

        Label subtitle = new Label(
            "Manage your organization's elections, members and governance."
        );

        subtitle.setStyle(
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 14px;"
        );

        headerText.getChildren().addAll(
            welcome,
            subtitle
        );

        Region headerSpacer = new Region();

        HBox.setHgrow(
            headerSpacer,
            Priority.ALWAYS
        );

        // =====================================================
        // ORGANIZATION BADGE
        // =====================================================

        VBox organizationBox = new VBox(3);

        organizationBox.setAlignment(
            Pos.CENTER_RIGHT
        );

        Label organizationLabel = new Label(
            "CURRENT ORGANIZATION"
        );

        organizationLabel.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #94A3B8;"
        );

        Label organizationName = new Label(
            "Zeal College of Engineering"
        );

        organizationName.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        organizationBox.getChildren().addAll(
            organizationLabel,
            organizationName
        );

        header.getChildren().addAll(
            headerText,
            headerSpacer,
            organizationBox
        );

        // =====================================================
        // STATISTICS
        // =====================================================

        GridPane statsGrid = new GridPane();

        statsGrid.setHgap(18);
        statsGrid.setVgap(18);

        VBox membersCard = statCard(
            "♧",
            "Members",
            "245",
            "Registered members"
        );

        VBox candidatesCard = statCard(
            "♙",
            "Candidates",
            "18",
            "Active candidates"
        );

        VBox electionsCard = statCard(
            "☑",
            "Elections",
            "06",
            "Total elections"
        );

        VBox turnoutCard = statCard(
            "◷",
            "Voter Turnout",
            "78%",
            "Current participation"
        );

        statsGrid.add(
            membersCard,
            0,
            0
        );

        statsGrid.add(
            candidatesCard,
            1,
            0
        );

        statsGrid.add(
            electionsCard,
            2,
            0
        );

        statsGrid.add(
            turnoutCard,
            3,
            0
        );

        // =====================================================
        // MIDDLE SECTION
        // =====================================================

        HBox middleSection = new HBox(20);

        // =====================================================
        // ELECTION ACTIVITY
        // =====================================================

        VBox activityCard = new VBox(18);

        activityCard.setPadding(
            new Insets(22)
        );

        activityCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 14;"
        );

        HBox activityHeader = new HBox();

        Label activityTitle = new Label(
            "Election Activity"
        );

        activityTitle.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                18
            )
        );

        activityTitle.setStyle(
            "-fx-text-fill: #172554;"
        );

        Region activitySpacer = new Region();

        HBox.setHgrow(
            activitySpacer,
            Priority.ALWAYS
        );

        Label activityStatus = new Label(
            "LIVE"
        );

        activityStatus.setStyle(
            "-fx-background-color: #DCFCE7;" +
            "-fx-text-fill: #15803D;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 10 5 10;" +
            "-fx-background-radius: 10;"
        );

        activityHeader.getChildren().addAll(
            activityTitle,
            activitySpacer,
            activityStatus
        );

        // Fake static activity bars

        HBox chart = new HBox(
            14
        );

        chart.setAlignment(
            Pos.BOTTOM_LEFT
        );

        chart.setPrefHeight(150);

        chart.getChildren().addAll(
            chartBar(65),
            chartBar(90),
            chartBar(45),
            chartBar(115),
            chartBar(80),
            chartBar(125),
            chartBar(100)
        );

        HBox days = new HBox(
            27
        );

        days.getChildren().addAll(
            chartLabel("Mon"),
            chartLabel("Tue"),
            chartLabel("Wed"),
            chartLabel("Thu"),
            chartLabel("Fri"),
            chartLabel("Sat"),
            chartLabel("Sun")
        );

        activityCard.getChildren().addAll(
            activityHeader,
            chart,
            days
        );

        HBox.setHgrow(
            activityCard,
            Priority.ALWAYS
        );

        // =====================================================
        // ORGANIZATION HEALTH
        // =====================================================

        VBox healthCard = new VBox(18);

        healthCard.setPadding(
            new Insets(22)
        );

        healthCard.setPrefWidth(
            320
        );

        healthCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 14;"
        );

        Label healthTitle = new Label(
            "Organization Health"
        );

        healthTitle.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                18
            )
        );

        healthTitle.setStyle(
            "-fx-text-fill: #172554;"
        );

        VBox scoreBox = new VBox(5);

        scoreBox.setAlignment(
            Pos.CENTER
        );

        Circle scoreCircle = new Circle(
            58
        );

        scoreCircle.setFill(
            Color.web("#EEF2FF")
        );

        Label score = new Label(
            "92%"
        );

        score.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                26
            )
        );

        score.setStyle(
            "-fx-text-fill: #3264E5;"
        );

        HBox scoreContainer = new HBox(
            score
        );

        scoreContainer.setAlignment(
            Pos.CENTER
        );

        scoreBox.getChildren().addAll(
            scoreContainer
        );

        Label healthDescription = new Label(
            "Healthy governance environment"
        );

        healthDescription.setStyle(
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 13px;"
        );

        healthDescription.setWrapText(
            true
        );

        healthCard.getChildren().addAll(
            healthTitle,
            scoreBox,
            healthDescription
        );

        middleSection.getChildren().addAll(
            activityCard,
            healthCard
        );

        // =====================================================
        // RECENT ELECTIONS
        // =====================================================

        VBox electionsCardBox = new VBox(15);

        electionsCardBox.setPadding(
            new Insets(22)
        );

        electionsCardBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 14;"
        );

        Label recentTitle = new Label(
            "Recent Elections"
        );

        recentTitle.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                18
            )
        );

        recentTitle.setStyle(
            "-fx-text-fill: #172554;"
        );

        VBox election1 = electionRow(
            "Student Council Election",
            "245 voters",
            "ACTIVE"
        );

        VBox election2 = electionRow(
            "Board Election",
            "180 voters",
            "UPCOMING"
        );

        VBox election3 = electionRow(
            "General Secretary Election",
            "210 voters",
            "COMPLETED"
        );

        electionsCardBox.getChildren().addAll(
            recentTitle,
            election1,
            election2,
            election3
        );

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        HBox quickActions = new HBox(15);

        Label quickTitle = new Label(
            "Quick Actions"
        );

        quickTitle.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                18
            )
        );

        quickTitle.setStyle(
            "-fx-text-fill: #172554;"
        );

        Button createElection = actionButton(
            "☑  Create Election"
        );

        Button manageMembers = actionButton(
            "♧  Manage Members"
        );

        Button viewReports = actionButton(
            "▤  View Reports"
        );

        VBox quickBox = new VBox(15);

        quickBox.getChildren().add(
            quickTitle
        );

        quickActions.getChildren().addAll(
            createElection,
            manageMembers,
            viewReports
        );

        quickBox.getChildren().add(
            quickActions
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
            header,
            statsGrid,
            middleSection,
            electionsCardBox,
            quickBox
        );

        // =====================================================
        // SIDEBAR NAVIGATION
        // =====================================================

        sidebar.getHome().setOnAction(
            e -> stage.setScene(homeScene)
        );

        sidebar.getOrganizations().setOnAction(
            e -> open(new Organizations())
        );

        sidebar.getAdmin().setOnAction(
            e -> open(new AdminDashboard())
        );

        sidebar.getMembers().setOnAction(
            e -> open(new Members())
        );

        sidebar.getCandidates().setOnAction(
            e -> open(new CandidateDashboard())
        );

        sidebar.getElections().setOnAction(
            e -> open(new ElectionPage())
        );

        sidebar.getResults().setOnAction(
            e -> open(new Results())
        );

        sidebar.getReports().setOnAction(
            e -> open(new Reports())
        );

        sidebar.getNotifications().setOnAction(
            e -> open(new Notifications())
        );

        sidebar.getProfile().setOnAction(
            e -> open(new Profile())
        );

        sidebar.getLogout().setOnAction(
            e -> loginCallback.run()
        );

        // =====================================================
        // ROOT
        // =====================================================

        root.setCenter(
            content
        );

        // =====================================================
        // SCENE
        // =====================================================

        homeScene = new Scene(
            root,
            1200,
            700
        );

        return homeScene;
    }

    // =========================================================
    // OPEN PAGE
    // =========================================================

    private void open(Page page) {

        stage.setScene(
            page.getScene(
                () -> stage.setScene(homeScene)
            )
        );
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox statCard(
            String icon,
            String title,
            String value,
            String description) {

        VBox card = new VBox(10);

        card.setPadding(
            new Insets(18)
        );

        card.setPrefWidth(
            210
        );

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 14;"
        );

        Label iconLabel = new Label(
            icon
        );

        iconLabel.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-text-fill: #3264E5;"
        );

        Label titleLabel = new Label(
            title
        );

        titleLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(
            value
        );

        valueLabel.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                25
            )
        );

        valueLabel.setStyle(
            "-fx-text-fill: #172554;"
        );

        Label descriptionLabel = new Label(
            description
        );

        descriptionLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #94A3B8;"
        );

        card.getChildren().addAll(
            iconLabel,
            titleLabel,
            valueLabel,
            descriptionLabel
        );

        return card;
    }

    // =========================================================
    // CHART BAR
    // =========================================================

    private Region chartBar(
            double height) {

        Region bar = new Region();

        bar.setPrefWidth(
            32
        );

        bar.setPrefHeight(
            height
        );

        bar.setStyle(
            "-fx-background-color: #3264E5;" +
            "-fx-background-radius: 7 7 0 0;"
        );

        return bar;
    }

    // =========================================================
    // CHART LABEL
    // =========================================================

    private Label chartLabel(
            String text) {

        Label label = new Label(
            text
        );

        label.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-text-fill: #94A3B8;"
        );

        return label;
    }

    // =========================================================
    // ELECTION ROW
    // =========================================================

    private VBox electionRow(
            String name,
            String voters,
            String status) {

        VBox row = new VBox(5);

        row.setPadding(
            new Insets(12)
        );

        row.setStyle(
            "-fx-background-color: #F8FAFC;" +
            "-fx-background-radius: 10;"
        );

        HBox top = new HBox();

        top.setAlignment(
            Pos.CENTER_LEFT
        );

        Label electionName = new Label(
            name
        );

        electionName.setFont(
            Font.font(
                "Arial",
                FontWeight.BOLD,
                13
            )
        );

        electionName.setStyle(
            "-fx-text-fill: #172554;"
        );

        Region spacer = new Region();

        HBox.setHgrow(
            spacer,
            Priority.ALWAYS
        );

        Label statusLabel = new Label(
            status
        );

        statusLabel.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3264E5;"
        );

        top.getChildren().addAll(
            electionName,
            spacer,
            statusLabel
        );

        Label voterLabel = new Label(
            voters
        );

        voterLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #64748B;"
        );

        row.getChildren().addAll(
            top,
            voterLabel
        );

        return row;
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private Button actionButton(
            String text) {

        Button button = new Button(
            text
        );

        button.setPrefHeight(
            45
        );

        button.setPrefWidth(
            190
        );

        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #172554;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 10;"
        );

        return button;
    }
}