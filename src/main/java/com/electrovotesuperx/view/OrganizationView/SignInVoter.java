package com.electrovotesuperx.view.OrganizationView;

import com.electrovotesuperx.controller.OrganizationController.VoterController;
import com.electrovotesuperx.dao.OrganizationDAO.VoterDAO;
import com.electrovotesuperx.model.OnlineVotingModel.Voter;
import com.electrovotesuperx.view.Page;
import com.electrovotesuperx.view.VoterView.VoterDashboard;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SignInVoter implements Page {

    private Scene scene;
    public static Scene voterDashboardScene; // Caching the scene

    @Override
    public Scene getScene(Runnable backCallback) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F7FB;");

        VBox container = new VBox(22);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));

        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefSize(76, 76);
        iconBox.setMaxSize(76, 76);
        iconBox.setStyle("-fx-background-color: #7C3AED; -fx-background-radius: 50;");
        Label icon = new Label("♙");
        icon.setFont(Font.font("Arial", 30));
        icon.setTextFill(Color.WHITE);
        iconBox.getChildren().add(icon);

        Label title = new Label("Sign In as Voter");
        title.setFont(Font.font("Arial", 30));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #172554;");

        Label subtitle = new Label("Join your organization and securely access your voting dashboard.");
        subtitle.setStyle("-fx-text-fill: #64748B; -fx-font-size: 15px;");

        VBox card = new VBox(16);
        card.setPrefWidth(500);
        card.setMaxWidth(500);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #DCE3F0;" +
                        "-fx-border-radius: 16;");

        // Join Code
        Label codeLabel = new Label("Organization Join Code");
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        TextField joinCode = new TextField();
        joinCode.setPromptText("Enter organization join code");
        styleField(joinCode);

        // Email / Voter ID
        Label voterIdLabel = new Label("Email Address");
        voterIdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        TextField voterId = new TextField();
        voterId.setPromptText("Enter registered email");
        if (com.electrovotesuperx.config.SessionManager.loggedInEmail != null
                && !com.electrovotesuperx.config.SessionManager.loggedInEmail.isBlank()) {
            voterId.setText(com.electrovotesuperx.config.SessionManager.loggedInEmail);
        }
        styleField(voterId);

        // Password
        Label passwordLabel = new Label("Password / Voting PIN");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        PasswordField password = new PasswordField();
        password.setPromptText("Enter password or voting PIN");
        styleField(password);

        // Status label for loading feedback
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        Button signIn = new Button("Sign In as Voter  →");
        signIn.setPrefWidth(Double.MAX_VALUE);
        signIn.setPrefHeight(48);
        signIn.setStyle(
                "-fx-background-color: #7C3AED;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;");

        signIn.setOnAction(e -> {
            String code = joinCode.getText().trim();
            String id = voterId.getText().trim();
            String pass = password.getText();

            if (code.isEmpty() || id.isEmpty() || pass.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter all fields.");
                return;
            }

            // Disable button and show loading state
            signIn.setDisable(true);
            signIn.setText("Signing in...");
            statusLabel.setText("Authenticating credentials...");

            // Run Firebase calls on a background thread
            Thread thread = new Thread(() -> {

                VoterDAO.SignInResult result = VoterController.signIn(id, pass, code);

                Platform.runLater(() -> {

                    signIn.setDisable(false);
                    signIn.setText("Sign In as Voter  →");
                    statusLabel.setText("");

                    if (result.isSuccess()) {

                        Voter voter = result.getVoter();

                        // Set SessionManager for voter
                        com.electrovotesuperx.config.SessionManager.idToken = result.getIdToken();
                        com.electrovotesuperx.config.SessionManager.currentRole = "voter";
                        com.electrovotesuperx.config.SessionManager.joinCode = voter.getJoinCode();
                        com.electrovotesuperx.config.SessionManager.organizationName = voter.getOrganizationName();
                        com.electrovotesuperx.config.SessionManager.voterUid = voter.getUid();
                        com.electrovotesuperx.config.SessionManager.voterEmail = voter.getEmail();
                        com.electrovotesuperx.config.SessionManager.voterName = voter.getFullName();
                        com.electrovotesuperx.config.SessionManager.voterStatus = voter.getStatus();
                        com.electrovotesuperx.config.SessionManager.voterPhone = voter.getPhone();
                        com.electrovotesuperx.config.SessionManager.adminUid = null;
                        com.electrovotesuperx.config.SessionManager.adminEmail = null;
                        com.electrovotesuperx.config.SessionManager.adminName = null;

                        // Load voter data into the dashboard
                        VoterDashboard.loadVoterData(
                                voter.getFullName(),
                                "Eligible Voter",
                                voter.getStatus() != null ? voter.getStatus() : "Verified",
                                "0",
                                voter.getOrganizationName()
                        );

                        // Open VoterDashboard using start(stage)
                        VoterDashboard dashboard = new VoterDashboard();
                        Stage stage = (Stage) ((scene != null && scene.getWindow() != null)
                                ? scene.getWindow()
                                : (root.getScene() != null ? root.getScene().getWindow() : null));
                        try {
                            if (stage != null) {
                                dashboard.start(stage);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Error",
                                    "Could not open Voter Dashboard: " + ex.getMessage());
                        }

                    } else {
                        showAlert(Alert.AlertType.ERROR, "Authentication Failed", result.getMessage());
                    }
                });
            });

            thread.setDaemon(true);
            thread.start();
        });

        Button signUp = new Button("Don't have an account? Sign Up");
        signUp.setPrefWidth(Double.MAX_VALUE);
        signUp.setPrefHeight(44);
        signUp.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #10B981;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #10B981;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;");

        signUp.setOnAction(e -> {
            Stage currentStage = (Stage) ((scene != null && scene.getWindow() != null)
                    ? scene.getWindow()
                    : (root.getScene() != null ? root.getScene().getWindow() : null));
            SignUpVoter signUpPage = new SignUpVoter();
            Scene suScene = signUpPage.getScene(() -> {
                if (currentStage != null) {
                    currentStage.setScene(scene);
                    currentStage.setMaximized(true);
                }
            });
            if (currentStage != null) {
                currentStage.setScene(suScene);
                currentStage.setMaximized(true);
            }
        });

        Button back = new Button("← Back to Organization Portal");
        back.setPrefWidth(Double.MAX_VALUE);
        back.setPrefHeight(44);
        back.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #475569;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;");

        back.setOnAction(e -> backCallback.run());

        card.getChildren().addAll(
                codeLabel, joinCode,
                voterIdLabel, voterId,
                passwordLabel, password,
                statusLabel,
                signIn, signUp, back);

        container.getChildren().addAll(iconBox, title, subtitle, card);
        root.setCenter(container);

        scene = new Scene(root);
        return scene;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(45);
        field.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 12;" +
                        "-fx-font-size: 14px;");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        com.electrovotesuperx.utils.Navigation.attachOwner(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}