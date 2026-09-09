package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO;
import com.electrovotesuperx.model.OfflineModel.PollingOfficerRequest;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService;
import com.electrovotesuperx.service.OfflineService.PollingOfficerService.OfficerRegistrationResult;
import com.electrovotesuperx.utils.Navigation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PollingOfficerGatewayDialog {

    private static final String FONT = "-fx-font-family: 'Segoe UI', system-ui, sans-serif; ";

    public static void show(Stage ownerStage) {
        show(ownerStage, "", "");
    }

    public static void show(Stage ownerStage, String prefillEmail, String prefillPassword) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (ownerStage != null) {
            dialog.initOwner(ownerStage);
        }
        dialog.setTitle("Polling Officer Verification Gateway");
        dialog.setResizable(true);
        dialog.setWidth(530);
        dialog.setHeight(610);

        VBox box = new VBox(14);
        box.setPadding(new Insets(22, 26, 22, 26));
        box.setStyle("-fx-background-color: #ffffff; " + FONT);

        // Header Banner
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🔒");
        icon.setMinSize(46, 46);
        icon.setMaxSize(46, 46);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: #ecfdf5; -fx-font-size: 22px; -fx-background-radius: 23; -fx-border-color: #a7f3d0; -fx-border-radius: 23;");

        VBox titleBox = new VBox(2);
        Label title = new Label("Offline Polling Officer Gateway");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #064e3b;");
        Label subtitle = new Label("Approval managed by: " + PollingOfficerService.CHIEF_APPROVER_EMAIL);
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #047857; -fx-font-weight: 700;");
        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(icon, titleBox);

        // Tab Switcher (Sign In vs Register Tabs)
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

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(18, 18);
        spinner.setVisible(false);

        // ==========================================
        // FORM 1: SIGN IN FORM
        // ==========================================
        VBox signInForm = new VBox(11);

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

        // 6-digit Secret Approval PIN Section (Mandatory on Every Login)
        VBox pinApprovalSection = new VBox(6);
        pinApprovalSection.setPadding(new Insets(10, 12, 10, 12));
        pinApprovalSection.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label pinPrompt = new Label("🔑 6-Digit Approval PIN (Required • Valid for 8 hours)");
        pinPrompt.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-text-fill: #166534;");

        TextField pinInputField = new TextField();
        pinInputField.setPromptText("Enter 6-digit Approval PIN from Admin");
        pinInputField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #86efac; -fx-border-radius: 6; -fx-padding: 8 10; -fx-font-size: 13px; -fx-font-weight: bold;");

        pinApprovalSection.getChildren().addAll(pinPrompt, pinInputField);

        Button unlockBtn = new Button("🔒  Verify PIN & Launch Station");
        unlockBtn.setMaxWidth(Double.MAX_VALUE);
        unlockBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-font-size: 13px; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Label siStatusLabel = new Label();
        siStatusLabel.setWrapText(true);
        siStatusLabel.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700;");
        siStatusLabel.setVisible(false);

        signInForm.getChildren().addAll(siEmailBox, siPassBox, pinApprovalSection, unlockBtn, siStatusLabel);

        // ==========================================
        // FORM 2: REGISTER FORM
        // ==========================================
        VBox registerForm = new VBox(10);
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

        // Pre-fill values
        if (prefillEmail != null && !prefillEmail.isBlank()) {
            siEmailField.setText(prefillEmail);
            regEmailField.setText(prefillEmail);
        } else if (SessionManager.loggedInEmail != null && !SessionManager.loggedInEmail.isBlank()) {
            siEmailField.setText(SessionManager.loggedInEmail);
            regEmailField.setText(SessionManager.loggedInEmail);
        }

        if (prefillPassword != null && !prefillPassword.isBlank()) {
            siPassField.setText(prefillPassword);
            regPassField.setText(prefillPassword);
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

        // Unified Verification & Launch Station Action (PIN is strictly mandatory)
        unlockBtn.setOnAction(e -> {
            String email = siEmailField.getText().trim();
            String pass = siPassField.getText();
            String pin = pinInputField.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                siStatusLabel.setText("❌ Please enter Officer Email and Password.");
                siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                siStatusLabel.setVisible(true);
                return;
            }

            // PIN is strictly required on every login
            if (pin.isEmpty() || pin.length() != 6) {
                siStatusLabel.setText("❌ Approval PIN is required. Please enter the 6-digit Secret PIN issued by Admin (" + PollingOfficerService.CHIEF_APPROVER_EMAIL + ").");
                siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                siStatusLabel.setVisible(true);
                return;
            }

            unlockBtn.setDisable(true);
            spinner.setVisible(true);
            siStatusLabel.setText("Verifying Polling Officer credentials & 8-hour approval PIN...");
            siStatusLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11.5px; -fx-font-weight: 700;");
            siStatusLabel.setVisible(true);

            new Thread(() -> {
                try {
                    // Step 1: Authenticate Firebase credentials
                    FirebaseAuthService.AuthResult authResult = FirebaseAuthService.signIn(email, pass);
                    if (!authResult.isSuccess()) {
                        Platform.runLater(() -> {
                            unlockBtn.setDisable(false);
                            spinner.setVisible(false);
                            siStatusLabel.setText("❌ Authentication failed: " + authResult.getMessage());
                            siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                        });
                        return;
                    }

                    // Step 2: Verify 6-digit Secret PIN against local SQLite
                    PollingOfficerDAO sqliteDao = new PollingOfficerDAO();
                    PollingOfficerRequest localReq = sqliteDao.findByUidOrEmail(email);

                    if (localReq != null && localReq.getApprovalPin() != null && localReq.getApprovalPin().trim().equals(pin)) {
                        if (localReq.isPinExpired()) {
                            Platform.runLater(() -> {
                                unlockBtn.setDisable(false);
                                spinner.setVisible(false);
                                siStatusLabel.setText("❌ Approval PIN has expired (valid for 8 hours). Please ask Admin (" + PollingOfficerService.CHIEF_APPROVER_EMAIL + ") to renew your PIN in Admin Dashboard.");
                                siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                            });
                            return;
                        }

                        // PIN is correct and within 8 hours!
                        sqliteDao.updateStatus(email, "APPROVED");
                        SessionManager.idToken = authResult.getIdToken();
                        SessionManager.currentRole = "polling_officer";
                        SessionManager.officerUid = localReq.getUid() != null ? localReq.getUid() : authResult.getLocalId();
                        SessionManager.officerEmail = localReq.getEmail() != null ? localReq.getEmail() : email;
                        SessionManager.officerName = localReq.getName() != null ? localReq.getName() : "Polling Officer";
                        SessionManager.officerStation = localReq.getStationName() != null ? localReq.getStationName() : "Main Station";

                        Platform.runLater(() -> {
                            dialog.close();
                            Stage mainStage = ownerStage != null ? ownerStage : Navigation.getStage();
                            Navigation.init(mainStage);
                            OfflineHomePage offline = new OfflineHomePage();
                            mainStage.setScene(offline.getScene());
                            mainStage.setMaximized(true);
                        });
                        return;
                    }

                    // Step 3: Fallback check with cloud Firestore / RTDB
                    boolean approvedWithCloudPin = PollingOfficerService.verifyAndApproveWithPin(
                            authResult.getLocalId(),
                            authResult.getEmail(),
                            pin,
                            authResult.getIdToken());

                    if (approvedWithCloudPin) {
                        Platform.runLater(() -> {
                            dialog.close();
                            Stage mainStage = ownerStage != null ? ownerStage : Navigation.getStage();
                            Navigation.init(mainStage);
                            OfflineHomePage offline = new OfflineHomePage();
                            mainStage.setScene(offline.getScene());
                            mainStage.setMaximized(true);
                        });
                        return;
                    }

                    // Step 4: If PIN check failed
                    Platform.runLater(() -> {
                        unlockBtn.setDisable(false);
                        spinner.setVisible(false);
                        siStatusLabel.setText("❌ Invalid or expired Approval PIN. Please enter the valid 6-digit PIN renewed by Admin (" + PollingOfficerService.CHIEF_APPROVER_EMAIL + ").");
                        siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        unlockBtn.setDisable(false);
                        spinner.setVisible(false);
                        siStatusLabel.setText("❌ Verification error: " + ex.getMessage());
                        siStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    });
                }
            }).start();
        });

        // Register Officer Action
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
                        siEmailField.setText(email);
                    } else {
                        regStatusLabel.setText("❌ " + regResult.getMessage());
                        regStatusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11.5px; -fx-font-weight: 700;");
                    }
                });
            }).start();
        });

        box.getChildren().addAll(header, tabBox, signInForm, registerForm, bottomBox);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        Scene scene = new Scene(scroll);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
