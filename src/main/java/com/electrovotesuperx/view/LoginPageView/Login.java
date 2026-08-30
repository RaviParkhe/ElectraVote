package com.electrovotesuperx.view.LoginPageView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.exception.AuthenticationException;
import com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO;
import com.electrovotesuperx.service.RoleDetector;
import com.electrovotesuperx.utils.Navigation;
import com.electrovotesuperx.view.Page;
import com.electrovotesuperx.view.AdminView.AdminDashboard;
import com.electrovotesuperx.view.HomePageView.HomePage;
import com.electrovotesuperx.view.OfflineView.OfflineHomePage;
import com.electrovotesuperx.view.VoterView.VoterDashboard;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
                            "/assests/images/login_image.jpeg"));
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
        signUpLine.setPrefWidth(90);
        signUpLine.setPrefHeight(3);
        signUpLine.setStyle("-fx-background-color: #2563EB");

        Region signInLine = new Region();
        signInLine.setPrefWidth(90);
        signInLine.setPrefHeight(3);
        signInLine.setStyle("-fx-background-color: #2563EB");
        signInLine.setVisible(false);

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setStyle(
                "-fx-font-size: 25;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #172554;" +
                        "-fx-cursor: hand;");

        Label signInLabel = new Label("Sign In");
        signInLabel.setStyle(
                "-fx-font-size: 25;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #172554;" +
                        "-fx-cursor: hand;");

        VBox signUpTab = new VBox(5, signUpLabel, signUpLine);
        signUpTab.setAlignment(Pos.CENTER);

        VBox signInTab = new VBox(5, signInLabel, signInLine);
        signInTab.setAlignment(Pos.CENTER);

        HBox tabsBox = new HBox(100, signUpTab, signInTab);
        tabsBox.setAlignment(Pos.CENTER);

        // =========================================
        // SIGN UP FIELDS
        // =========================================

        TextField name = new TextField();
        name.setPromptText("Full Name");
        name.setPrefWidth(500);
        name.setPrefHeight(40);
        name.setStyle("-fx-background-radius: 20;");

        TextField signUpEmail = new TextField();
        signUpEmail.setPromptText("Email Address");
        signUpEmail.setPrefWidth(500);
        signUpEmail.setPrefHeight(40);
        signUpEmail.setStyle("-fx-background-radius: 20;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date of Birth");
        datePicker.setPrefWidth(500);
        datePicker.setPrefHeight(40);
        datePicker.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;");

        PasswordField signUpPassword = new PasswordField();
        signUpPassword.setPromptText("Password");
        signUpPassword.setPrefWidth(500);
        signUpPassword.setPrefHeight(40);
        signUpPassword.setStyle("-fx-background-radius: 20;");

        Button signUpSubmitBtn = new Button("Submit");
        signUpSubmitBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;");
        signUpSubmitBtn.setPrefWidth(500);
        signUpSubmitBtn.setPrefHeight(40);

        signUpSubmitBtn.setOnMouseEntered(e -> {
            signUpSubmitBtn.setScaleX(1.05);
            signUpSubmitBtn.setScaleY(1.05);
        });

        signUpSubmitBtn.setOnMouseExited(e -> {
            signUpSubmitBtn.setScaleX(1);
            signUpSubmitBtn.setScaleY(1);
        });

        // =========================================
        // SIGN IN FIELDS
        // =========================================

        TextField signInEmail = new TextField();
        signInEmail.setPromptText("Email Address");
        signInEmail.setPrefWidth(500);
        signInEmail.setPrefHeight(40);
        signInEmail.setStyle("-fx-background-radius: 20;");

        PasswordField signInPassword = new PasswordField();
        signInPassword.setPromptText("Password");
        signInPassword.setPrefWidth(500);
        signInPassword.setPrefHeight(40);
        signInPassword.setStyle("-fx-background-radius: 20;");

        // =========================================
        // ROLE SELECTOR
        // =========================================

        final String[] selectedRole = { "admin" };

        String roleBtnDefault = "-fx-background-color: #F1F5F9;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16;";

        String roleBtnActiveAdmin = "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16;";

        String roleBtnActiveVoter = "-fx-background-color: #7C3AED;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16;";

        String roleBtnActiveOffline = "-fx-background-color: #059669;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16;";

        String roleBtnActiveNewUser = "-fx-background-color: #EA580C;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16;";

        Button adminRoleBtn = new Button("🏛  Admin");
        adminRoleBtn.setStyle(roleBtnActiveAdmin);
        adminRoleBtn.setPrefWidth(115);
        adminRoleBtn.setPrefHeight(36);

        Button voterRoleBtn = new Button("🗳  Voter");
        voterRoleBtn.setStyle(roleBtnDefault);
        voterRoleBtn.setPrefWidth(115);
        voterRoleBtn.setPrefHeight(36);

        Button offlineRoleBtn = new Button("🔒  Offline");
        offlineRoleBtn.setStyle(roleBtnDefault);
        offlineRoleBtn.setPrefWidth(115);
        offlineRoleBtn.setPrefHeight(36);

        Button newUserRoleBtn = new Button("✨  New User");
        newUserRoleBtn.setStyle(roleBtnDefault);
        newUserRoleBtn.setPrefWidth(115);
        newUserRoleBtn.setPrefHeight(36);

        Label roleLabel = new Label("Select Your Role");
        roleLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #172554;");

        HBox roleButtons = new HBox(8,
                adminRoleBtn, voterRoleBtn, offlineRoleBtn, newUserRoleBtn);
        roleButtons.setAlignment(Pos.CENTER);

        VBox roleSelector = new VBox(8, roleLabel, roleButtons);
        roleSelector.setAlignment(Pos.CENTER);

        // Role selector click handlers

        adminRoleBtn.setOnAction(e -> {
            selectedRole[0] = "admin";
            adminRoleBtn.setStyle(roleBtnActiveAdmin);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        voterRoleBtn.setOnAction(e -> {
            selectedRole[0] = "voter";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnActiveVoter);
            offlineRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        offlineRoleBtn.setOnAction(e -> {
            selectedRole[0] = "offline";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnActiveOffline);
            newUserRoleBtn.setStyle(roleBtnDefault);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        newUserRoleBtn.setOnAction(e -> {
            selectedRole[0] = "newUser";
            adminRoleBtn.setStyle(roleBtnDefault);
            voterRoleBtn.setStyle(roleBtnDefault);
            offlineRoleBtn.setStyle(roleBtnDefault);
            newUserRoleBtn.setStyle(roleBtnActiveNewUser);
            signInEmail.setDisable(false);
            signInPassword.setDisable(false);
        });

        // =========================================
        // SIGN IN SUBMIT BUTTON
        // =========================================

        Button signInSubmitBtn = new Button("Submit");
        signInSubmitBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;");
        signInSubmitBtn.setPrefWidth(500);
        signInSubmitBtn.setPrefHeight(40);

        signInSubmitBtn.setOnMouseEntered(e -> {
            signInSubmitBtn.setScaleX(1.05);
            signInSubmitBtn.setScaleY(1.05);
        });

        signInSubmitBtn.setOnMouseExited(e -> {
            signInSubmitBtn.setScaleX(1);
            signInSubmitBtn.setScaleY(1);
        });

        // =========================================
        // OUTPUT MESSAGE
        // =========================================

        Text output = new Text();
        output.setStyle("-fx-font-size: 14;");

        // =========================================
        // SIGN UP FORM
        // =========================================

        VBox signUpBox = new VBox(50,
                name, signUpEmail, datePicker,
                signUpPassword, signUpSubmitBtn);
        signUpBox.setFillWidth(false);
        signUpBox.setAlignment(Pos.CENTER);

        // =========================================
        // SIGN IN FORM
        // =========================================

        VBox signInBox = new VBox(25,
                signInEmail, signInPassword,
                roleSelector, signInSubmitBtn);
        signInBox.setFillWidth(false);
        signInBox.setAlignment(Pos.CENTER);
        signInBox.setVisible(false);

        // =========================================
        // FORM AREA
        // =========================================

        StackPane formArea = new StackPane();
        formArea.setPrefHeight(450);
        formArea.setMinHeight(450);
        formArea.setMaxHeight(450);
        formArea.getChildren().addAll(signUpBox, signInBox);

        // =========================================
        // SIGN UP ACTION
        // =========================================

        signUpSubmitBtn.setOnAction(e -> {

            if (name.getText().isEmpty()
                    || signUpEmail.getText().isEmpty()
                    || datePicker.getValue() == null
                    || signUpPassword.getText().isEmpty()) {

                output.setFill(Color.RED);
                output.setText("Please fill all fields.");
                shakeButton(signUpSubmitBtn);
                return;
            }

            signUpSubmitBtn.setDisable(true);
            output.setFill(Color.web("#2563EB"));
            output.setText("Creating account...");

            new Thread(() -> {

                try {

                    FirebaseAuthService.AuthResult result = FirebaseAuthService.createUser(
                            signUpEmail.getText().trim(),
                            signUpPassword.getText());

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

                            output.setFill(Color.web("#059669"));
                            output.setText(
                                    "Account created successfully! " +
                                            "Switch to Sign In.");

                        } else {

                            output.setFill(Color.RED);
                            output.setText(result.getMessage());
                        }
                    });

                } catch (AuthenticationException ex) {

                    javafx.application.Platform.runLater(() -> {

                        signUpSubmitBtn.setDisable(false);
                        output.setFill(Color.RED);
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

            // ─── All roles (Admin / Voter / Offline / New User) require email + password
            // ───

            if (signInEmail.getText().isEmpty()
                    || signInPassword.getText().isEmpty()) {

                output.setFill(Color.RED);
                output.setText("Please fill all fields.");
                shakeButton(signInSubmitBtn);
                return;
            }

            signInSubmitBtn.setDisable(true);
            output.setFill(Color.web("#2563EB"));
            output.setText("Signing in...");

            new Thread(() -> {

                try {

                    FirebaseAuthService.AuthResult result = FirebaseAuthService.signIn(
                            signInEmail.getText().trim(),
                            signInPassword.getText());

                    if (!result.isSuccess()) {

                        javafx.application.Platform.runLater(() -> {
                            signInSubmitBtn.setDisable(false);
                            output.setFill(Color.RED);
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
                        output.setFill(Color.web("#2563EB"));
                        output.setText("Verifying authorization...");
                    });

                    RoleDetector.RoleResult detected = RoleDetector.detectRole(
                            result.getLocalId(),
                            result.getEmail(),
                            result.getIdToken());

                    javafx.application.Platform.runLater(() -> {

                        signInSubmitBtn.setDisable(false);

                        if ("admin".equals(chosenRole)) {

                            if (detected == RoleDetector.RoleResult.ADMIN) {

                                // Verified as admin → AdminDashboard
                                AdminDashboard adminDashboard = new AdminDashboard();
                                adminDashboard.start(loginStage);

                            } else {

                                output.setFill(Color.RED);
                                output.setText(
                                        "Access denied. You are not registered " +
                                                "as an admin. Please register your " +
                                                "organization first.");
                                shakeButton(signInSubmitBtn);
                            }

                        } else if ("voter".equals(chosenRole)) {

                            if (detected == RoleDetector.RoleResult.VOTER) {

                                // Verified as voter → VoterDashboard
                                VoterDashboard.loadVoterData(
                                        SessionManager.voterName,
                                        "Eligible Voter",
                                        SessionManager.voterStatus != null
                                                ? SessionManager.voterStatus
                                                : "Verified",
                                        "0",
                                        SessionManager.organizationName);

                                VoterDashboard voterDashboard = new VoterDashboard();
                                voterDashboard.start(loginStage);

                            } else {

                                output.setFill(Color.RED);
                                output.setText(
                                        "Access denied. You are not registered " +
                                                "as a voter. Please sign up through " +
                                                "the Organization Portal first.");
                                shakeButton(signInSubmitBtn);
                            }

                        } else if ("offline".equals(chosenRole)) {

                            // Authenticated for Offline Mode → OfflineHomePage
                            SessionManager.idToken = result.getIdToken();
                            SessionManager.currentRole = "offline";
                            Navigation.init(loginStage);
                            OfflineHomePage offline = new OfflineHomePage();
                            loginStage.setScene(offline.getScene());
                            loginStage.setMaximized(true);

                        } else {

                            // New User mode → HomePage (Home Portal)
                            SessionManager.idToken = result.getIdToken();
                            SessionManager.loggedInEmail = result.getEmail() != null
                                    ? result.getEmail()
                                    : signInEmail.getText().trim();

                            HomePage homepage = new HomePage(loginStage);
                            loginStage.setScene(
                                    homepage.getScene(() -> {
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
                        output.setFill(Color.RED);
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
        // FORM CARD
        // =========================================

        VBox formBox = new VBox(30,
                tabsBox, formArea, output);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(520);
        formBox.setPrefHeight(700);
        formBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.94);" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 30;" +
                        "-fx-effect: dropshadow(gaussian, " +
                        "rgba(0,0,0,0.20), 20, 0, 0, 5);");

        // =========================================
        // ROOT
        // =========================================

        StackPane root = new StackPane();

        if (loginImage != null && !loginImage.isError()) {

            // Bind image to fill the window
            imageView.fitWidthProperty().bind(
                    root.widthProperty());
            imageView.fitHeightProperty().bind(
                    root.heightProperty());

            root.getChildren().add(imageView);
        } else {

            root.setStyle(
                    "-fx-background-color: #F5F7FB;");
        }

        root.getChildren().add(formBox);
        StackPane.setAlignment(formBox, Pos.CENTER_RIGHT);
        StackPane.setMargin(formBox, new Insets(0, 50, 0, 0));

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
}