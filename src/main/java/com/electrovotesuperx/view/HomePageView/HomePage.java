package com.electrovotesuperx.view.HomePageView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.exception.AuthenticationException;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService.OfficerApprovalStatus;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService.OfficerRegistrationResult;
import com.electrovotesuperx.service.RoleDetector;
import com.electrovotesuperx.utils.Navigation;
import com.electrovotesuperx.view.Page;
import com.electrovotesuperx.view.LoginPageView.Login;
import com.electrovotesuperx.view.OfflineView.OfflineHomePage;
import com.electrovotesuperx.view.OrganizationView.OrganizationPortal;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HomePage implements Page {

    private final Stage stage;
    private Scene homeScene;

    // ========================================
    // COLORS & FONTS
    // ========================================

    private static final String BLUE = "#2563EB";
    private static final String TEXT = "#172554";
    private static final String SUBTEXT = "#64748B";
    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public HomePage(Stage stage) {
        this.stage = stage;
    }

    // ========================================
    // GET SCENE
    // ========================================

    @Override
    public Scene getScene(Runnable loginCallback) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F7F9FC; " + FONT);

        // ========================================
        // HEADER
        // ========================================

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 35, 18, 35));
        header.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        Circle logo = new Circle(22, Color.web("#2563EB"));
        Label logoSymbol = new Label("✓");
        logoSymbol.setFont(Font.font("Arial", 20));
        logoSymbol.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        StackPane logoPane = new StackPane(logo, logoSymbol);

        Label brand = new Label("ElectraVote");
        brand.setFont(Font.font("Arial", 24));
        brand.setStyle("-fx-font-weight: bold; -fx-text-fill: #172554;");

        HBox brandBox = new HBox(10, logoPane, brand);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Label security = new Label("Secure  •  Transparent  •  Democratic");
        security.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        header.getChildren().addAll(brandBox, headerSpacer, security);

        // ========================================
        // MAIN CONTENT
        // ========================================

        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(45, 40, 35, 40));

        Label title = new Label("Welcome to ElectraVote");
        title.setFont(Font.font("Arial", 32));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #172554;");

        Label subtitle = new Label("Modern. Secure. Multi-Tenant Voting Platform.");
        subtitle.setFont(Font.font("Arial", 17));
        subtitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        Label description = new Label("Empowering organizations with secure and transparent voting solutions.");
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        content.getChildren().addAll(title, subtitle, description);

        // ========================================
        // THREE MAIN PORTAL CARDS
        // ========================================

        HBox cards = new HBox(22);
        cards.setAlignment(Pos.CENTER);

        // 1. ORGANIZATION PORTAL
        VBox organizationCard = createCard(
                "🏛",
                "Organization Portal",
                "Register your organization or sign in to manage elections, members and more.",
                BLUE);

        Button organizationButton = createCardButton("Get Started  →", BLUE);
        organizationButton.setOnAction(e -> {
            OrganizationPortal portal = new OrganizationPortal(stage);
            stage.setScene(portal.getScene(() -> {
                stage.setScene(homeScene);
                stage.setMaximized(true);
            }));
            stage.setMaximized(true);
        });
        organizationCard.getChildren().add(organizationButton);

        // 2. OFFLINE VOTING (ROLE-GATED WITH EMAIL APPROVAL)
        VBox offlineCard = createCard(
                "🗳",
                "Offline Voting",
                "Participate in elections conducted in offline mode with verified Polling Officer access.",
                "#059669");

        Button offlineButton = createCardButton("Get Started  →", "#059669");
        offlineButton.setOnAction(e -> {
            if (SessionManager.isPollingOfficer()) {
                Navigation.init(stage);
                OfflineHomePage offline = new OfflineHomePage();
                stage.setScene(offline.getScene());
                stage.setMaximized(true);
            } else {
                showPollingOfficerAuthDialog(stage);
            }
        });
        offlineCard.getChildren().add(offlineButton);

        cards.getChildren().addAll(organizationCard, offlineCard);
        content.getChildren().add(cards);

        // ========================================
        // BACK TO LOGIN BUTTON
        // ========================================

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setPrefWidth(180);
        backToLoginButton.setPrefHeight(40);
        backToLoginButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #2563EB; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-color: #2563EB; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        backToLoginButton.setOnAction(e -> {
            Login.loginStage = stage;
            Login login = new Login();
            stage.setScene(login.getScene(() -> {}));
            stage.setMaximized(true);
        });

        content.getChildren().add(backToLoginButton);

        // ========================================
        // FOOTER
        // ========================================

        Label footer = new Label("© 2026 ElectraVote. All rights reserved.");
        footer.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(footer, new Insets(12));

        root.setTop(header);
        root.setCenter(content);
        root.setBottom(footer);

        homeScene = new Scene(root);
        return homeScene;
    }

    // ========================================
    // POLLING OFFICER AUTH & REGISTRATION MODAL
    // ========================================

    private void showPollingOfficerAuthDialog(Stage ownerStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(ownerStage);
        dialog.setTitle("Polling Officer Verification Gateway");
        dialog.setResizable(true);
        dialog.setWidth(520);
        dialog.setHeight(560);

        VBox box = new VBox(12);
        box.setPadding(new Insets(20, 24, 20, 24));
        box.setStyle("-fx-background-color: #ffffff; " + FONT);

        // Top Banner
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🔒");
        icon.setMinSize(44, 44);
        icon.setMaxSize(44, 44);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: #ecfdf5; -fx-font-size: 20px; -fx-background-radius: 22; -fx-border-color: #a7f3d0; -fx-border-radius: 22;");

        VBox titleBox = new VBox(2);
        Label title = new Label("Offline Polling Officer Gateway");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");
        Label subtitle = new Label("Approval managed by: " + PollingOfficerService.CHIEF_APPROVER_EMAIL);
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #047857; -fx-font-weight: 700;");
        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(icon, titleBox);

        // Mode Switcher (Sign In vs Register Tabs)
        HBox tabBox = new HBox(8);
        tabBox.setAlignment(Pos.CENTER);
        tabBox.setPadding(new Insets(4, 0, 8, 0));

        Button signInTabBtn = new Button("Sign In as Officer");
        Button registerTabBtn = new Button("Register as Officer");

        String activeTabStyle = "-fx-background-color: #059669; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 8; -fx-cursor: hand;";
        String inactiveTabStyle = "-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 700; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 8; -fx-cursor: hand;";

        signInTabBtn.setStyle(activeTabStyle);
        registerTabBtn.setStyle(inactiveTabStyle);
        tabBox.getChildren().addAll(signInTabBtn, registerTabBtn);

        // Feedback / Status Label
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700;");
        statusLabel.setVisible(false);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(18, 18);
        spinner.setVisible(false);

        // ==========================================
        // FORM 1: SIGN IN FORM
        // ==========================================
        VBox signInForm = new VBox(10);

        VBox siEmailBox = new VBox(4);
        Label siEmailLabel = new Label("Officer Email");
        siEmailLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        TextField siEmailField = new TextField();
        siEmailField.setPromptText("officer@institution.edu");
        siEmailField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px;");
        siEmailBox.getChildren().addAll(siEmailLabel, siEmailField);

        VBox siPassBox = new VBox(4);
        Label siPassLabel = new Label("Password / Security PIN");
        siPassLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        PasswordField siPassField = new PasswordField();
        siPassField.setPromptText("••••••••");
        siPassField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px;");
        siPassBox.getChildren().addAll(siPassLabel, siPassField);

        Button unlockBtn = new Button("🔓  Verify & Launch Station");
        unlockBtn.setMaxWidth(Double.MAX_VALUE);
        unlockBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-font-size: 12.5px; -fx-padding: 9 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Label siStatusLabel = new Label();
        siStatusLabel.setWrapText(true);
        siStatusLabel.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700;");
        siStatusLabel.setVisible(false);

        // Approval PIN verification section
        VBox pinApprovalSection = new VBox(6);
        pinApprovalSection.setPadding(new Insets(10, 12, 10, 12));
        pinApprovalSection.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label pinPrompt = new Label("🔑 Have the Approval PIN from " + PollingOfficerService.CHIEF_APPROVER_EMAIL + "?");
        pinPrompt.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-text-fill: #166534;");

        HBox pinInputRow = new HBox(8);
        TextField pinInputField = new TextField();
        pinInputField.setPromptText("Enter 6-digit Secret PIN");
        pinInputField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #86efac; -fx-border-radius: 6; -fx-padding: 6 10; -fx-font-size: 12px;");
        HBox.setHgrow(pinInputField, Priority.ALWAYS);

        Button verifyPinBtn = new Button("Verify & Unlock");
        verifyPinBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");

        pinInputRow.getChildren().addAll(pinInputField, verifyPinBtn);
        pinApprovalSection.getChildren().addAll(pinPrompt, pinInputRow);

        signInForm.getChildren().addAll(siEmailBox, siPassBox, unlockBtn, pinApprovalSection, siStatusLabel);

        // ==========================================
        // FORM 2: REGISTER FORM
        // ==========================================
        VBox registerForm = new VBox(9);
        registerForm.setVisible(false);
        registerForm.setManaged(false);

        VBox regNameBox = new VBox(4);
        Label regNameLabel = new Label("Full Name");
        regNameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        TextField regNameField = new TextField();
        regNameField.setPromptText("e.g. Prof. Rajesh Sharma");
        regNameField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px;");
        regNameBox.getChildren().addAll(regNameLabel, regNameField);

        VBox regEmailBox = new VBox(4);
        Label regEmailLabel = new Label("Officer Email");
        regEmailLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        TextField regEmailField = new TextField();
        regEmailField.setPromptText("officer@institution.edu");
        regEmailField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px;");
        regEmailBox.getChildren().addAll(regEmailLabel, regEmailField);

        VBox regPassBox = new VBox(4);
        Label regPassLabel = new Label("Password / PIN (min 6 characters)");
        regPassLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        PasswordField regPassField = new PasswordField();
        regPassField.setPromptText("••••••••");
        regPassField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px;");
        regPassBox.getChildren().addAll(regPassLabel, regPassField);

        HBox regMetaBox = new HBox(10);
        VBox stationBox = new VBox(4);
        Label stationLabel = new Label("Station / College Name");
        stationLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        TextField stationField = new TextField();
        stationField.setPromptText("Station 1 / Zeal College");
        stationField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px;");
        stationBox.getChildren().addAll(stationLabel, stationField);
        HBox.setHgrow(stationBox, Priority.ALWAYS);

        VBox phoneBox = new VBox(4);
        Label phoneLabel = new Label("Contact Number");
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        TextField phoneField = new TextField();
        phoneField.setPromptText("9876543210");
        phoneField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px;");
        phoneBox.getChildren().addAll(phoneLabel, phoneField);
        HBox.setHgrow(phoneBox, Priority.ALWAYS);

        regMetaBox.getChildren().addAll(stationBox, phoneBox);

        Button registerSubmitBtn = new Button("📨  Submit Registration for Approval");
        registerSubmitBtn.setMaxWidth(Double.MAX_VALUE);
        registerSubmitBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-font-size: 12.5px; -fx-padding: 9 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Label regStatusLabel = new Label();
        regStatusLabel.setWrapText(true);
        regStatusLabel.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700;");
        regStatusLabel.setVisible(false);

        registerForm.getChildren().addAll(regNameBox, regEmailBox, regPassBox, regMetaBox, registerSubmitBtn, regStatusLabel);

        // Auto-fill default email and name from current user session
        String defaultEmail = SessionManager.loggedInEmail != null && !SessionManager.loggedInEmail.isBlank()
                ? SessionManager.loggedInEmail
                : (SessionManager.officerEmail != null && !SessionManager.officerEmail.isBlank()
                        ? SessionManager.officerEmail
                        : (SessionManager.voterEmail != null && !SessionManager.voterEmail.isBlank()
                                ? SessionManager.voterEmail
                                : (SessionManager.adminEmail != null && !SessionManager.adminEmail.isBlank()
                                        ? SessionManager.adminEmail
                                        : "")));

        if (!defaultEmail.isBlank()) {
            siEmailField.setText(defaultEmail);
            regEmailField.setText(defaultEmail);
        }

        String defaultName = SessionManager.voterName != null && !SessionManager.voterName.isBlank()
                ? SessionManager.voterName
                : (SessionManager.adminName != null && !SessionManager.adminName.isBlank()
                        ? SessionManager.adminName
                        : (SessionManager.officerName != null && !SessionManager.officerName.isBlank()
                                ? SessionManager.officerName
                                : ""));

        if (!defaultName.isBlank() && !defaultName.equalsIgnoreCase("Eligible Voter") && !defaultName.equalsIgnoreCase("Voter") && !defaultName.equalsIgnoreCase("Administrator")) {
            regNameField.setText(defaultName);
        }

        // Tab Switching Actions
        signInTabBtn.setOnAction(e -> {
            signInTabBtn.setStyle(activeTabStyle);
            registerTabBtn.setStyle(inactiveTabStyle);
            signInForm.setVisible(true);
            signInForm.setManaged(true);
            registerForm.setVisible(false);
            registerForm.setManaged(false);
            siStatusLabel.setVisible(false);
            regStatusLabel.setVisible(false);
        });

        registerTabBtn.setOnAction(e -> {
            registerTabBtn.setStyle(activeTabStyle);
            signInTabBtn.setStyle(inactiveTabStyle);
            registerForm.setVisible(true);
            registerForm.setManaged(true);
            signInForm.setVisible(false);
            signInForm.setManaged(false);
            siStatusLabel.setVisible(false);
            regStatusLabel.setVisible(false);
        });

        // Bottom Controls
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Close");
        cancelBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7 14; -fx-background-radius: 8; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());
        bottomBox.getChildren().addAll(spinner, cancelBtn);

        // Action: Sign In Handler
        unlockBtn.setOnAction(e -> {
            String email = siEmailField.getText().trim();
            String pass = siPassField.getText();

            if (email.isEmpty() || pass.isEmpty()) {
                siStatusLabel.setText("Please enter both Officer Email and Password.");
                siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                siStatusLabel.setVisible(true);
                return;
            }

            unlockBtn.setDisable(true);
            spinner.setVisible(true);
            siStatusLabel.setText("Verifying Polling Officer credentials and approval...");
            siStatusLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11.5px; -fx-font-weight: 700;");
            siStatusLabel.setVisible(true);

            new Thread(() -> {
                try {
                    FirebaseAuthService.AuthResult result = FirebaseAuthService.signIn(email, pass);
                    if (!result.isSuccess()) {
                        Platform.runLater(() -> {
                            unlockBtn.setDisable(false);
                            spinner.setVisible(false);
                            siStatusLabel.setText(result.getMessage());
                            siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        });
                        return;
                    }

                    // Check Polling Officer Approval Status
                    OfficerApprovalStatus status = PollingOfficerService.checkOfficerApprovalStatus(
                            result.getLocalId(),
                            result.getEmail(),
                            result.getIdToken());

                    Platform.runLater(() -> {
                        unlockBtn.setDisable(false);
                        spinner.setVisible(false);

                        if (status == OfficerApprovalStatus.APPROVED) {
                            dialog.close();
                            Navigation.init(ownerStage);
                            OfflineHomePage offline = new OfflineHomePage();
                            ownerStage.setScene(offline.getScene());
                            ownerStage.setMaximized(true);
                        } else if (status == OfficerApprovalStatus.PENDING) {
                            siStatusLabel.setText("⏳ Application Pending: Your registration is awaiting approval by "
                                    + PollingOfficerService.CHIEF_APPROVER_EMAIL + ".");
                            siStatusLabel.setStyle("-fx-text-fill: #d97706; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        } else if (status == OfficerApprovalStatus.REJECTED) {
                            siStatusLabel.setText("❌ Access Denied: Your Polling Officer registration was rejected.");
                            siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        } else {
                            siStatusLabel.setText("❌ Account not found in Polling Officer directory. Please register using the 'Register Officer' tab.");
                            siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        }
                    });

                } catch (AuthenticationException ex) {
                    Platform.runLater(() -> {
                        unlockBtn.setDisable(false);
                        spinner.setVisible(false);
                        siStatusLabel.setText("Authentication failed: " + ex.getMessage());
                        siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    });
                }
            }).start();
        });

        // Action: Verify PIN Handler
        verifyPinBtn.setOnAction(e -> {
            String email = siEmailField.getText().trim();
            String pass = siPassField.getText();
            String pin = pinInputField.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                siStatusLabel.setText("Please enter your Officer Email and Password first.");
                siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                siStatusLabel.setVisible(true);
                return;
            }

            if (pin.length() != 6) {
                siStatusLabel.setText("Please enter the 6-digit Secret PIN sent to " + PollingOfficerService.CHIEF_APPROVER_EMAIL);
                siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                siStatusLabel.setVisible(true);
                return;
            }

            verifyPinBtn.setDisable(true);
            spinner.setVisible(true);
            siStatusLabel.setText("Verifying approval PIN with Firebase...");
            siStatusLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11.5px; -fx-font-weight: 700;");
            siStatusLabel.setVisible(true);

            new Thread(() -> {
                try {
                    FirebaseAuthService.AuthResult auth = FirebaseAuthService.signIn(email, pass);
                    if (!auth.isSuccess()) {
                        Platform.runLater(() -> {
                            verifyPinBtn.setDisable(false);
                            spinner.setVisible(false);
                            siStatusLabel.setText("Authentication failed: " + auth.getMessage());
                            siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        });
                        return;
                    }

                    boolean approved = PollingOfficerService.verifyAndApproveWithPin(
                            auth.getLocalId(),
                            auth.getEmail(),
                            pin,
                            auth.getIdToken());

                    Platform.runLater(() -> {
                        verifyPinBtn.setDisable(false);
                        spinner.setVisible(false);

                        if (approved) {
                            dialog.close();
                            Navigation.init(ownerStage);
                            OfflineHomePage offline = new OfflineHomePage();
                            ownerStage.setScene(offline.getScene());
                            ownerStage.setMaximized(true);
                        } else {
                            siStatusLabel.setText("❌ Invalid Approval PIN. Please enter the correct 6-digit PIN sent to " + PollingOfficerService.CHIEF_APPROVER_EMAIL);
                            siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        verifyPinBtn.setDisable(false);
                        spinner.setVisible(false);
                        siStatusLabel.setText("Verification failed: " + ex.getMessage());
                        siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    });
                }
            }).start();
        });

        // Action: Register Handler
        registerSubmitBtn.setOnAction(e -> {
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String pass = regPassField.getText();
            String station = stationField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || station.isEmpty()) {
                regStatusLabel.setText("Please fill all required registration fields.");
                regStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                regStatusLabel.setVisible(true);
                return;
            }

            if (pass.length() < 6) {
                regStatusLabel.setText("Password must contain at least 6 characters.");
                regStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                regStatusLabel.setVisible(true);
                return;
            }

            registerSubmitBtn.setDisable(true);
            spinner.setVisible(true);
            regStatusLabel.setText("Submitting Polling Officer registration to " + PollingOfficerService.CHIEF_APPROVER_EMAIL + "...");
            regStatusLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11.5px; -fx-font-weight: 700;");
            regStatusLabel.setVisible(true);

            new Thread(() -> {
                OfficerRegistrationResult regResult = PollingOfficerService.registerOfficer(
                        name, email, pass, station, phone);

                Platform.runLater(() -> {
                    registerSubmitBtn.setDisable(false);
                    spinner.setVisible(false);

                    if (regResult.isSuccess()) {
                        regStatusLabel.setText("✅ " + regResult.getMessage());
                        regStatusLabel.setStyle("-fx-text-fill: #059669; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        regPassField.clear();
                    } else {
                        regStatusLabel.setText("❌ " + regResult.getMessage());
                        regStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    }
                });
            }).start();
        });

        box.getChildren().addAll(header, tabBox, signInForm, registerForm, bottomBox);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        Scene scene = new Scene(scroll);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ========================================
    // CREATE CARD HELPER
    // ========================================

    private VBox createCard(String icon, String titleText, String descriptionText, String accentColor) {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(330);
        card.setMinWidth(330);
        card.setPrefHeight(280);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #DCE5F2; -fx-border-radius: 16;");

        Circle iconCircle = new Circle(38, Color.web(accentColor));
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 24));
        StackPane iconPane = new StackPane(iconCircle, iconLabel);

        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", 19));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SUBTEXT + ";");

        card.getChildren().addAll(iconPane, title, description);
        return card;
    }

    // ========================================
    // CREATE CARD BUTTON HELPER
    // ========================================

    private Button createCardButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(260);
        button.setPrefHeight(45);
        button.setStyle(
                "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 9; -fx-cursor: hand;");
        return button;
    }
}