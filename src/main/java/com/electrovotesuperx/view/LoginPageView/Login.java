package com.electrovotesuperx.view.LoginPageView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.exception.AuthenticationException;
import com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService.OfficerApprovalStatus;
import com.electrovotesuperx.service.RoleDetector;
import com.electrovotesuperx.utils.Navigation;
import com.electrovotesuperx.view.Page;
import com.electrovotesuperx.view.AdminView.AdminDashboard;
import com.electrovotesuperx.view.OfflineView.OfflineAdminDashboard;
import com.electrovotesuperx.view.OfflineView.OfflineHomePage;
import com.electrovotesuperx.view.OfflineView.PollingOfficerGatewayDialog;
import com.electrovotesuperx.view.OrganizationView.OrganizationPortal;
import com.electrovotesuperx.view.VoterView.VoterDashboard;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login implements Page {

    public static Stage loginStage;

    private Scene loginScene;

    // =========================================
    // SHARED STYLE CONSTANTS
    // =========================================

    private static final String FIELD_STYLE =
            "-fx-background-color: rgba(255,255,255,0.10);" +
            "-fx-border-color: rgba(255,255,255,0.35);" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255,255,255,0.55);" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 14;";

    private static final String EYE_BTN_STYLE =
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 2 6;";

    // =========================================
    // GET SCENE
    // =========================================

    @Override
    public Scene getScene(Runnable ignored) {

        // =========================================
        // BACKGROUND IMAGE
        // =========================================

        Image loginImage = null;

        try {
            loginImage = new Image(
                    getClass().getResourceAsStream(
                            "/assests/images/login_image.png"));
        } catch (Exception e) {
            // Image not found — continue without it
        }

        ImageView imageView = new ImageView();

        if (loginImage != null && !loginImage.isError()) {

            imageView.setImage(loginImage);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
        }

        // =========================================
        // SIGN UP / SIGN IN TABS
        // =========================================

        Region signUpLine = new Region();
        signUpLine.setPrefHeight(3);
        signUpLine.setStyle("-fx-background-color: white; -fx-background-radius: 2;");

        Region signInLine = new Region();
        signInLine.setPrefHeight(3);
        signInLine.setStyle("-fx-background-color: white; -fx-background-radius: 2;");
        signInLine.setVisible(false);

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setStyle(
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-cursor: hand;");

        Label signInLabel = new Label("Sign In");
        signInLabel.setStyle(
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: rgba(255,255,255,0.65);" +
                "-fx-cursor: hand;");

        VBox signUpTab = new VBox(6, signUpLabel, signUpLine);
        signUpTab.setAlignment(Pos.CENTER);

        VBox signInTab = new VBox(6, signInLabel, signInLine);
        signInTab.setAlignment(Pos.CENTER);

        HBox tabsBox = new HBox(60, signUpTab, signInTab);
        tabsBox.setAlignment(Pos.CENTER);

        // =========================================
        // SIGN UP FIELDS
        // =========================================

        TextField name = new TextField();
        name.setPromptText("Full Name");
        name.setStyle(FIELD_STYLE);
        name.setMaxWidth(Double.MAX_VALUE);

        TextField signUpEmail = new TextField();
        signUpEmail.setPromptText("Email Address");
        signUpEmail.setStyle(FIELD_STYLE);
        signUpEmail.setMaxWidth(Double.MAX_VALUE);

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date of Birth (18+ years)");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle(
                "-fx-background-color: rgba(255,255,255,0.10);" +
                "-fx-border-color: rgba(255,255,255,0.35);" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: rgba(255,255,255,0.55);" +
                "-fx-font-size: 14px;");

        // Restrict DatePicker calendar to only allow selection of dates for users 18
        // years or older
        LocalDate maxAllowedDob = LocalDate.now().minusYears(18);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isAfter(maxAllowedDob) || date.isBefore(LocalDate.now().minusYears(120)))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
                }
            }
        });

        // Sign-Up password with eye toggle
        HBox signUpPasswordRow = buildPasswordRow("Password");
        PasswordField signUpPassword = (PasswordField) signUpPasswordRow.getChildren().get(0);
        TextField signUpPasswordVisible = (TextField) signUpPasswordRow.getChildren().get(1);

        Button signUpSubmitBtn = buildSubmitButton("Create Account");

        // =========================================
        // SIGN IN FIELDS
        // =========================================

        TextField signInEmail = new TextField();
        signInEmail.setPromptText("Email Address");
        signInEmail.setStyle(FIELD_STYLE);
        signInEmail.setMaxWidth(Double.MAX_VALUE);

        // Sign-In password with eye toggle
        HBox signInPasswordRow = buildPasswordRow("Password");
        PasswordField signInPassword = (PasswordField) signInPasswordRow.getChildren().get(0);
        TextField signInPasswordVisible = (TextField) signInPasswordRow.getChildren().get(1);

        // =========================================
        // ROLE SELECTOR
        // =========================================

        final String[] selectedRole = { "admin" };

        String roleBtnDefault =
                "-fx-background-color: rgba(255,255,255,0.15);" +
                "-fx-text-fill: rgba(255,255,255,0.80);" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 7 14;";

        String roleBtnActiveAdmin =
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 7 14;";

        String roleBtnActiveVoter =
                "-fx-background-color: #7C3AED;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 7 14;";

        String roleBtnActiveOffline =
                "-fx-background-color: #059669;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 7 14;";

        String roleBtnActiveOfflineAdmin =
                "-fx-background-color: #4F46E5;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 7 14;";

        String roleBtnActiveNewUser =
                "-fx-background-color: #EA580C;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 7 14;";

        Button adminRoleBtn        = new Button("🏛  Admin");
        Button voterRoleBtn        = new Button("🗳  Voter");
        Button offlineRoleBtn      = new Button("🔒  Offline");
        Button offlineAdminRoleBtn = new Button("⚡  Offline Admin");
        Button newUserRoleBtn      = new Button("✨  New User");

        adminRoleBtn.setStyle(roleBtnActiveAdmin);
        voterRoleBtn.setStyle(roleBtnDefault);
        offlineRoleBtn.setStyle(roleBtnDefault);
        offlineAdminRoleBtn.setStyle(roleBtnDefault);
        newUserRoleBtn.setStyle(roleBtnDefault);

        Label roleLabel = new Label("Select Your Role");
        roleLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: rgba(255,255,255,0.85);");

        HBox roleButtonsRow1 = new HBox(8, adminRoleBtn, voterRoleBtn, offlineRoleBtn);
        roleButtonsRow1.setAlignment(Pos.CENTER);
        HBox roleButtonsRow2 = new HBox(8, offlineAdminRoleBtn, newUserRoleBtn);
        roleButtonsRow2.setAlignment(Pos.CENTER);

        VBox roleSelector = new VBox(8, roleLabel, roleButtonsRow1, roleButtonsRow2);
        roleSelector.setAlignment(Pos.CENTER);

        // Role selector click handlers

        adminRoleBtn.setOnAction(e -> {
            selectedRole[0] = "admin";
            adminRoleBtn.setStyle(roleBtnActiveAdmin);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnDefault);
            offlineAdminRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        voterRoleBtn.setOnAction(e -> {
            selectedRole[0] = "voter";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnActiveVoter);
            offlineRoleBtn.setStyle(roleBtnDefault);
            offlineAdminRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        offlineRoleBtn.setOnAction(e -> {
            selectedRole[0] = "offline";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnActiveOffline);
            offlineAdminRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);

            Stage targetStage = loginStage != null ? loginStage : (Stage) offlineRoleBtn.getScene().getWindow();
            String curEmail = signInEmail.getText().trim();
            String curPass = signInPassword.isVisible() ? signInPassword.getText() : signInPasswordVisible.getText();
            PollingOfficerGatewayDialog.show(targetStage, curEmail, curPass);
        });

        offlineAdminRoleBtn.setOnAction(e -> {
            selectedRole[0] = "offlineAdmin";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnDefault);
            offlineAdminRoleBtn.setStyle(roleBtnActiveOfflineAdmin);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        newUserRoleBtn.setOnAction(e -> {
            selectedRole[0] = "newUser";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnDefault);
            offlineAdminRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnActiveNewUser);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        // =========================================
        // SIGN IN SUBMIT BUTTON
        // =========================================

        Button signInSubmitBtn = buildSubmitButton("Sign In");

        // =========================================
        // OUTPUT MESSAGE
        // =========================================

        Text output = new Text();
        output.setStyle("-fx-font-size: 13;");
        output.setFill(Color.WHITE);

        // =========================================
        // SIGN UP FORM
        // =========================================

        VBox signUpBox = new VBox(18,
                name, signUpEmail, datePicker,
                signUpPasswordRow, signUpSubmitBtn);
        signUpBox.setFillWidth(true);
        signUpBox.setAlignment(Pos.CENTER);

        // =========================================
        // SIGN IN FORM
        // =========================================

        VBox signInBox = new VBox(18,
                signInEmail, signInPasswordRow,
                roleSelector, signInSubmitBtn);
        signInBox.setFillWidth(true);
        signInBox.setAlignment(Pos.CENTER);
        signInBox.setVisible(false);

        // =========================================
        // FORM AREA (StackPane for overlay transition)
        // =========================================

        StackPane formArea = new StackPane(signUpBox, signInBox);

        // =========================================
        // SIGN UP ACTION
        // =========================================

        signUpSubmitBtn.setOnAction(e -> {

            String passText = signUpPassword.isVisible()
                    ? signUpPassword.getText()
                    : signUpPasswordVisible.getText();

            if (name.getText().isEmpty()
                    || signUpEmail.getText().isEmpty()
                    || datePicker.getValue() == null
                    || passText.isEmpty()) {

                output.setFill(Color.web("#FCA5A5"));
                output.setText("Please fill all fields.");
                shakeButton(signUpSubmitBtn);
                return;
            }

            LocalDate dob = datePicker.getValue();
            LocalDate maxDob = LocalDate.now().minusYears(18);
            if (dob.isAfter(maxDob)) {
                output.setFill(Color.web("#FCA5A5"));
                output.setText("You must be at least 18 years old to register.");
                shakeButton(signUpSubmitBtn);
                return;
            }

            if (dob.isBefore(LocalDate.now().minusYears(120))) {
                output.setFill(Color.web("#FCA5A5"));
                output.setText("Please select a valid Date of Birth.");
                shakeButton(signUpSubmitBtn);
                return;
            }

            signUpSubmitBtn.setDisable(true);
            output.setFill(Color.web("#93C5FD"));
            output.setText("Creating account...");

            String finalPass = passText;
            new Thread(() -> {

                try {

                    FirebaseAuthService.AuthResult result = FirebaseAuthService.createUser(
                            signUpEmail.getText().trim(),
                            finalPass);

                    javafx.application.Platform.runLater(() -> {

                        signUpSubmitBtn.setDisable(false);

                        if (result.isSuccess()) {

                            SessionManager.loggedInEmail = signUpEmail.getText().trim();

                            // Save user profile in Firestore
                            new Thread(() -> {
                                try {
                                    FirestoreDAO.saveUser(
                                            result.getLocalId(),
                                            name.getText().trim(),
                                            signUpEmail.getText().trim(),
                                            "USER",
                                            "",
                                            result.getIdToken());
                                } catch (Exception ex) {
                                    System.err.println("[Login] Firestore save user note: " + ex.getMessage());
                                }
                            }).start();

                            output.setFill(Color.web("#6EE7B7"));
                            output.setText(
                                    "Account created successfully! " +
                                            "Switch to Sign In.");

                        } else {

                            output.setFill(Color.web("#FCA5A5"));
                            output.setText(result.getMessage());
                        }
                    });

                } catch (AuthenticationException ex) {

                    javafx.application.Platform.runLater(() -> {

                        signUpSubmitBtn.setDisable(false);
                        output.setFill(Color.web("#FCA5A5"));
                        output.setText(ex.getMessage());
                    });
                }

            }).start();
        });

        // =========================================
        // SIGN IN ACTION
        // =========================================

        signInSubmitBtn.setOnAction(e -> {

            String chosenRole = selectedRole[0];

            String passText = signInPassword.isVisible()
                    ? signInPassword.getText()
                    : signInPasswordVisible.getText();

            if (signInEmail.getText().isEmpty() || passText.isEmpty()) {

                output.setFill(Color.web("#FCA5A5"));
                output.setText("Please fill all fields.");
                shakeButton(signInSubmitBtn);
                return;
            }

            // Exclusive validation for Offline Admin role
            if ("offlineAdmin".equals(chosenRole)) {
                if (!"ravi.parkhe2006@gmail.com".equalsIgnoreCase(signInEmail.getText().trim())) {
                    output.setFill(Color.web("#FCA5A5"));
                    output.setText("Access denied: Offline Admin portal is restricted exclusively to Admin Email.");
                    shakeButton(signInSubmitBtn);
                    return;
                }

                // Authorized Offline Admin → Direct Access to OfflineAdminDashboard
                SessionManager.adminEmail = "ravi.parkhe2006@gmail.com";
                SessionManager.loggedInEmail = "ravi.parkhe2006@gmail.com";
                SessionManager.currentRole = "offline_admin";
                Navigation.init(loginStage);
                OfflineAdminDashboard offlineAdminDashboard = new OfflineAdminDashboard();
                loginStage.setScene(offlineAdminDashboard.getScene());
                loginStage.setMaximized(true);
                return;
            }

            // Offline Role (Polling Officer) → Open Polling Officer Verification Gateway
            if ("offline".equals(chosenRole)) {
                Stage targetStage = loginStage != null ? loginStage : (Stage) signInSubmitBtn.getScene().getWindow();
                PollingOfficerGatewayDialog.show(targetStage, signInEmail.getText().trim(), passText);
                return;
            }

            signInSubmitBtn.setDisable(true);
            output.setFill(Color.web("#93C5FD"));
            output.setText("Signing in...");

            String finalPass = passText;
            new Thread(() -> {

                try {

                    FirebaseAuthService.AuthResult result = FirebaseAuthService.signIn(
                            signInEmail.getText().trim(),
                            finalPass);

                    if (!result.isSuccess()) {

                        javafx.application.Platform.runLater(() -> {
                            signInSubmitBtn.setDisable(false);
                            output.setFill(Color.web("#FCA5A5"));
                            output.setText(result.getMessage());
                        });
                        return;
                    }

                    // Save authenticated user email
                    SessionManager.loggedInEmail = result.getEmail() != null
                            ? result.getEmail()
                            : signInEmail.getText().trim();

                    // ─── Role Verification ───

                    javafx.application.Platform.runLater(() -> {
                        output.setFill(Color.web("#93C5FD"));
                        output.setText("Verifying authorization...");
                    });

                    RoleDetector.RoleResult detected = RoleDetector.detectRole(
                            result.getLocalId(),
                            result.getEmail(),
                            result.getIdToken(),
                            chosenRole);

                    javafx.application.Platform.runLater(() -> {

                        signInSubmitBtn.setDisable(false);

                        if ("admin".equals(chosenRole)) {

                            if (detected == RoleDetector.RoleResult.ADMIN) {

                                // Verified as admin → AdminDashboard
                                AdminDashboard adminDashboard = new AdminDashboard();
                                adminDashboard.start(loginStage);

                            } else {

                                output.setFill(Color.web("#FCA5A5"));
                                output.setText(
                                        "Access denied. You are not registered " +
                                                "as an admin. Please register your " +
                                                "organization first.");
                                shakeButton(signInSubmitBtn);
                            }

                        } else if ("voter".equals(chosenRole)) {

                            if (detected == RoleDetector.RoleResult.VOTER || detected == RoleDetector.RoleResult.ADMIN) {

                                String vStatus = SessionManager.voterStatus != null ? SessionManager.voterStatus : "PENDING";
                                boolean isApproved = "ACCEPTED".equalsIgnoreCase(vStatus) || "VERIFIED".equalsIgnoreCase(vStatus) || detected == RoleDetector.RoleResult.ADMIN;

                                if (isApproved) {
                                    // Verified & Approved voter → VoterDashboard
                                    VoterDashboard.loadVoterData(
                                            SessionManager.voterName != null && !SessionManager.voterName.isBlank()
                                                    ? SessionManager.voterName
                                                    : (SessionManager.adminName != null ? SessionManager.adminName : "Eligible Voter"),
                                            "Eligible Voter",
                                            vStatus,
                                            "0",
                                            SessionManager.organizationName);

                                    VoterDashboard voterDashboard = new VoterDashboard();
                                    voterDashboard.start(loginStage);
                                } else if ("REJECTED".equalsIgnoreCase(vStatus)) {
                                    output.setFill(Color.web("#FCA5A5"));
                                    output.setText(
                                            "Access denied: Your voter registration for '"
                                                    + (SessionManager.organizationName != null ? SessionManager.organizationName : SessionManager.joinCode)
                                                    + "' was rejected by the administrator.");
                                    shakeButton(signInSubmitBtn);
                                } else {
                                    output.setFill(Color.web("#FCD34D"));
                                    output.setText(
                                            "⏳ Access denied: Your voter registration for '"
                                                    + (SessionManager.organizationName != null ? SessionManager.organizationName : SessionManager.joinCode)
                                                    + "' is awaiting approval.");
                                    shakeButton(signInSubmitBtn);
                                }

                            } else {

                                output.setFill(Color.web("#FCA5A5"));
                                output.setText(
                                        "Access denied. You are not registered " +
                                                "as a voter. Please sign up through " +
                                                "the Organization Portal first.");
                                shakeButton(signInSubmitBtn);
                            }

                        } else if ("offline".equals(chosenRole)) {

                            OfficerApprovalStatus status = PollingOfficerService.checkOfficerApprovalStatus(
                                     result.getLocalId(),
                                    result.getEmail(),
                                    result.getIdToken());

                            if (status == OfficerApprovalStatus.APPROVED) {
                                SessionManager.idToken = result.getIdToken();
                                SessionManager.currentRole = "polling_officer";
                                Navigation.init(loginStage);
                                OfflineHomePage offline = new OfflineHomePage();
                                loginStage.setScene(offline.getScene());
                                loginStage.setMaximized(true);
                            } else if (status == OfficerApprovalStatus.PENDING) {
                                output.setFill(Color.web("#FCD34D"));
                                output.setText(
                                        "Access denied: Your Polling Officer registration is awaiting approval by "
                                                + PollingOfficerService.CHIEF_APPROVER_EMAIL + ".");
                                shakeButton(signInSubmitBtn);
                            } else if (status == OfficerApprovalStatus.REJECTED) {
                                output.setFill(Color.web("#FCA5A5"));
                                output.setText(
                                        "Access denied: Your Polling Officer registration was rejected.");
                                shakeButton(signInSubmitBtn);
                            } else {
                                output.setFill(Color.web("#FCA5A5"));
                                output.setText(
                                        "Access denied. Offline Voting Portal is restricted exclusively to authorized Polling Officers. Please register first.");
                                shakeButton(signInSubmitBtn);
                            }

                        } else if ("offlineAdmin".equals(chosenRole)) {

                            String authedEmail = result.getEmail() != null ? result.getEmail() : signInEmail.getText().trim();
                            if (!"ravi.parkhe2006@gmail.com".equalsIgnoreCase(authedEmail)) {
                                output.setFill(Color.web("#FCA5A5"));
                                output.setText("Access denied: Offline Admin is restricted exclusively to ravi.parkhe2006@gmail.com.");
                                shakeButton(signInSubmitBtn);
                                return;
                            }

                            SessionManager.idToken = result.getIdToken();
                            SessionManager.adminEmail = "ravi.parkhe2006@gmail.com";
                            SessionManager.loggedInEmail = "ravi.parkhe2006@gmail.com";
                            SessionManager.currentRole = "offline_admin";
                            Navigation.init(loginStage);
                            OfflineAdminDashboard offlineAdminDashboard = new OfflineAdminDashboard();
                            loginStage.setScene(offlineAdminDashboard.getScene());
                            loginStage.setMaximized(true);

                        } else {

                            // New User mode → Direct access to Organization Portal
                            SessionManager.idToken = result.getIdToken();
                            SessionManager.loggedInEmail = result.getEmail() != null
                                     ? result.getEmail()
                                     : signInEmail.getText().trim();

                            OrganizationPortal portal = new OrganizationPortal(loginStage);
                            loginStage.setScene(
                                    portal.getScene(() -> {
                                        SessionManager.clear();
                                        loginStage.setScene(loginScene);
                                        loginStage.setMaximized(true);
                                    }));
                            loginStage.setMaximized(true);
                        }
                    });

                } catch (AuthenticationException ex) {

                    javafx.application.Platform.runLater(() -> {

                        signInSubmitBtn.setDisable(false);
                        output.setFill(Color.web("#FCA5A5"));
                        output.setText(ex.getMessage());
                    });
                }

            }).start();
        });

        // =========================================
        // TAB SWITCHING — SIGN UP
        // =========================================

        signUpLabel.setOnMouseClicked(e -> {

            output.setText("");

            signUpBox.setVisible(true);
            signInBox.setVisible(false);

            signUpLine.setVisible(true);
            signInLine.setVisible(false);

            signUpLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: white; -fx-cursor: hand;");
            signInLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: rgba(255,255,255,0.65); -fx-cursor: hand;");

            FadeTransition fade = new FadeTransition(
                    Duration.millis(300), signUpBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            TranslateTransition slide = new TranslateTransition(
                    Duration.millis(300), signUpBox);
            slide.setFromX(-50);
            slide.setToX(0);
            slide.play();
        });

        // =========================================
        // TAB SWITCHING — SIGN IN
        // =========================================

        signInLabel.setOnMouseClicked(e -> {

            output.setText("");

            signUpBox.setVisible(false);
            signInBox.setVisible(true);

            signUpLine.setVisible(false);
            signInLine.setVisible(true);

            signInLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: white; -fx-cursor: hand;");
            signUpLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: rgba(255,255,255,0.65); -fx-cursor: hand;");

            FadeTransition fade = new FadeTransition(
                    Duration.millis(300), signInBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            TranslateTransition slide = new TranslateTransition(
                    Duration.millis(300), signInBox);
            slide.setFromX(50);
            slide.setToX(0);
            slide.play();
        });

        // =========================================
        // FORM CARD  (glassmorphism dark panel)
        // =========================================

        VBox formBox = new VBox(22, tabsBox, formArea, output);
        formBox.setAlignment(Pos.CENTER);
        formBox.setFillWidth(true);
        formBox.setStyle(
                "-fx-background-color: rgba(10,20,50,0.65);" +
                "-fx-background-radius: 22;" +
                "-fx-border-color: rgba(255,255,255,0.18);" +
                "-fx-border-radius: 22;" +
                "-fx-border-width: 1;" +
                "-fx-padding: 35 40;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.50), 30, 0, 0, 8);");

        // Responsive: min 360, preferred 460, max 520
        formBox.setMinWidth(360);
        formBox.setPrefWidth(460);
        formBox.setMaxWidth(520);

        // =========================================
        // ROOT — image fills entire background
        // =========================================

        StackPane root = new StackPane();

        if (loginImage != null && !loginImage.isError()) {
            imageView.fitWidthProperty().bind(root.widthProperty());
            imageView.fitHeightProperty().bind(root.heightProperty());
            root.getChildren().add(imageView);
        } else {
            root.setStyle("-fx-background-color: #0A1432;");
        }

        // Subtle overlay to improve card readability
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0,0,10,0.38);");
        overlay.setMouseTransparent(true);

        root.getChildren().addAll(overlay, formBox);
        StackPane.setAlignment(formBox, Pos.CENTER_RIGHT);
        StackPane.setMargin(formBox, new Insets(30, 60, 30, 0));

        // Responsive: center card when window is narrow
        root.widthProperty().addListener((obs, oldW, newW) -> {
            if (newW.doubleValue() < 700) {
                StackPane.setAlignment(formBox, Pos.CENTER);
                StackPane.setMargin(formBox, new Insets(20));
            } else {
                StackPane.setAlignment(formBox, Pos.CENTER_RIGHT);
                StackPane.setMargin(formBox, new Insets(30, 60, 30, 0));
            }
        });

        // =========================================
        // SCENE
        // =========================================

        loginScene = new Scene(root);
        return loginScene;
    }

    // =========================================
    // SHAKE ANIMATION
    // =========================================

    private void shakeButton(Button button) {
        TranslateTransition shake = new TranslateTransition(
                Duration.millis(70), button);
        shake.setFromX(-10);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    // =========================================
    // PASSWORD ROW — field + eye toggle button
    // =========================================

    private HBox buildPasswordRow(String promptText) {

        PasswordField passField = new PasswordField();
        passField.setPromptText(promptText);
        passField.setStyle(FIELD_STYLE);
        HBox.setHgrow(passField, Priority.ALWAYS);

        TextField visibleField = new TextField();
        visibleField.setPromptText(promptText);
        visibleField.setStyle(FIELD_STYLE);
        visibleField.setVisible(false);
        visibleField.setManaged(false);
        HBox.setHgrow(visibleField, Priority.ALWAYS);

        // Sync text both ways
        passField.textProperty().addListener((obs, o, n) -> {
            if (passField.isVisible()) visibleField.setText(n);
        });
        visibleField.textProperty().addListener((obs, o, n) -> {
            if (visibleField.isVisible()) passField.setText(n);
        });

        // Eye icon button
        ImageView eyeIcon = null;
        try {
            Image eyeImg = new Image(
                    getClass().getResourceAsStream("/assests/images/eye.jpeg"), 20, 20, true, true);
            eyeIcon = new ImageView(eyeImg);
        } catch (Exception ignored) { }

        Button eyeBtn = new Button(eyeIcon != null ? "" : "👁");
        if (eyeIcon != null) eyeBtn.setGraphic(eyeIcon);
        eyeBtn.setStyle(EYE_BTN_STYLE);
        eyeBtn.setOpacity(0.75);

        final boolean[] shown = { false };
        eyeBtn.setOnAction(ev -> {
            shown[0] = !shown[0];
            if (shown[0]) {
                visibleField.setText(passField.getText());
                passField.setVisible(false);
                passField.setManaged(false);
                visibleField.setVisible(true);
                visibleField.setManaged(true);
                eyeBtn.setOpacity(1.0);
            } else {
                passField.setText(visibleField.getText());
                visibleField.setVisible(false);
                visibleField.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
                eyeBtn.setOpacity(0.75);
            }
        });

        HBox row = new HBox(6, passField, visibleField, eyeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.10);" +
                "-fx-border-color: rgba(255,255,255,0.35);" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 2 8 2 2;");
        row.setMaxWidth(Double.MAX_VALUE);

        return row;
    }

    // =========================================
    // SUBMIT BUTTON BUILDER
    // =========================================

    private Button buildSubmitButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #2563EB, #1D4ED8);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 12 0;");
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #1D4ED8, #1E40AF);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 15px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 12 0;");
            btn.setScaleX(1.02);
            btn.setScaleY(1.02);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #2563EB, #1D4ED8);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 15px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 12 0;");
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        return btn;
    }
}