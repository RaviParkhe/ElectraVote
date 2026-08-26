package com.elctrovotesuperx.view.OrganizationView;

import com.elctrovotesuperx.controller.OrganizationController.VoterController;
import com.elctrovotesuperx.view.Page;

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
import javafx.scene.text.Font;

public class SignUpVoter implements Page {

    private Scene scene;

    @Override
    public Scene getScene(Runnable backCallback) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F7FB;");

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));

        Label title = new Label("Voter Registration");
        title.setFont(Font.font("Arial", 30));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #172554;");

        Label subtitle = new Label("Register your details to join your organization's elections.");
        subtitle.setStyle("-fx-text-fill: #64748B; -fx-font-size: 15px;");

        VBox card = new VBox(15);
        card.setPrefWidth(500);
        card.setMaxWidth(500);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #DCE3F0;" +
                        "-fx-border-radius: 16;");

        // Full Name
        Label nameLabel = new Label("Full Name");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        styleField(nameField);

        // Join Code
        Label codeLabel = new Label("Organization Join Code");
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        TextField joinCode = new TextField();
        joinCode.setPromptText("Enter organization join code (e.g. EV-XXXX-XXXX)");
        styleField(joinCode);

        // Email
        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        styleField(emailField);

        // Password
        Label passwordLabel = new Label("Password / Voting PIN");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #172554;");
        PasswordField password = new PasswordField();
        password.setPromptText("Create a password (min. 6 characters)");
        styleField(password);

        // Status label for loading feedback
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        Button signUpBtn = new Button("Register as Voter");
        signUpBtn.setPrefWidth(Double.MAX_VALUE);
        signUpBtn.setPrefHeight(48);
        signUpBtn.setStyle(
                "-fx-background-color: #10B981;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;");

        signUpBtn.setOnAction(e -> {
            String fullName = nameField.getText().trim();
            String code = joinCode.getText().trim();
            String email = emailField.getText().trim();
            String pass = password.getText();

            if (fullName.isEmpty() || code.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all fields.");
                return;
            }

            // Disable button and show loading state
            signUpBtn.setDisable(true);
            signUpBtn.setText("Registering...");
            statusLabel.setText("Creating your account on Firebase...");

            // Run Firebase calls on a background thread
            Thread thread = new Thread(() -> {

                String error = VoterController.signUp(fullName, email, pass, code);

                Platform.runLater(() -> {

                    signUpBtn.setDisable(false);
                    signUpBtn.setText("Register as Voter");
                    statusLabel.setText("");

                    if (error == null) {
                        // Success
                        showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                                "You have been registered successfully!\n\n" +
                                "You can now sign in with your email and password.");
                        if (backCallback != null) {
                            backCallback.run(); // Navigate to Sign In
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Registration Failed", error);
                    }
                });
            });

            thread.setDaemon(true);
            thread.start();
        });

        Button backBtn = new Button("Already have an account? Sign In");
        backBtn.setPrefWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(44);
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #7C3AED;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;");

        backBtn.setOnAction(e -> {
            if (backCallback != null) {
                backCallback.run();
            }
        });

        card.getChildren().addAll(
                nameLabel, nameField,
                codeLabel, joinCode,
                emailLabel, emailField,
                passwordLabel, password,
                statusLabel,
                signUpBtn, backBtn);

        container.getChildren().addAll(title, subtitle, card);
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
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
