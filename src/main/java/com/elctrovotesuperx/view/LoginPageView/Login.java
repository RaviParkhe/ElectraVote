package com.elctrovotesuperx.view.LoginPageView;

import com.elctrovotesuperx.view.Page;
import com.elctrovotesuperx.view.HomePageView.Homepage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Login implements Page {

    public static Stage loginStage;

    private Scene loginScene;

    @Override
    public Scene getScene(Runnable ignored) {

        BorderPane root = new BorderPane();

        root.setStyle(
            "-fx-background-color: #F5F7FB;"
        );

        VBox card = new VBox(16);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(35));
        card.setMaxWidth(430);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 18;"
        );

        Label title = new Label("ElectraVote");

        title.setFont(
            Font.font("Arial", 30)
        );

        title.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        Label subtitle = new Label(
            "Secure Multi-Organization Election Platform"
        );

        subtitle.setWrapText(true);

        subtitle.setStyle(
            "-fx-text-fill: #64748B;"
        );

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        ComboBox<String> role = new ComboBox<>();

        role.getItems().addAll(
            "Member / Voter",
            "Admin",
            "Candidate"
        );

        role.setValue("Member / Voter");
        role.setMaxWidth(Double.MAX_VALUE);

        Button loginButton = new Button("Login");

        loginButton.setPrefHeight(45);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setStyle(
            "-fx-background-color: #3264E5;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 10;"
        );

        Label message = new Label();

        message.setStyle(
            "-fx-text-fill: #DC2626;"
        );

        // =========================================
        // LOGIN
        // =========================================

        loginButton.setOnAction(e -> {

    if (username.getText().isBlank()
            || password.getText().isBlank()) {

        message.setText(
            "Enter username and password."
        );

        return;
    }

    Homepage homepage = new Homepage(loginStage);
    loginStage.setScene(
        homepage.getScene(() -> {

            // Homepage -> Logout -> Login

            loginStage.setScene(loginScene);

        })
    );
});

        card.getChildren().addAll(

            title,
            subtitle,

            new Label("Username"),
            username,

            new Label("Password"),
            password,

            new Label("Role"),
            role,

            loginButton,
            message
        );

        StackPane center =
                new StackPane(card);

        center.setPadding(
            new Insets(30)
        );

        root.setCenter(center);

        loginScene =
                new Scene(
                    root,
                    1200,
                    700
                );

        return loginScene;
    }
}