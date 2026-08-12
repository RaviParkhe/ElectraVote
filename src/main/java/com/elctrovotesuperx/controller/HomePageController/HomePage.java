package com.elctrovotesuperx.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage {

    public final Stage stage;
    public final Scene scene ;

    public Scene getScene(Scene scene) {

        

        // =========================
        // Header
        // =========================

        Label title = new Label("ElectraVote");
        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Secure Multi-Organization Election Platform"
        );

        VBox logoBox = new VBox(5, title, subtitle);

        // =========================
        // Navigation Buttons
        // =========================

        Button homeButton = new Button("Home");
        Button electionButton = new Button("Elections");
        Button pollButton = new Button("Quick Poll");
        Button voterButton = new Button("Voter Dashboard");
        Button adminButton = new Button("Admin Dashboard");
        Button candidateButton = new Button("Candidate Dashboard");
        Button logoutButton = new Button("Logout");

        // Button sizes

        homeButton.setPrefWidth(150);
        electionButton.setPrefWidth(150);
        pollButton.setPrefWidth(150);
        voterButton.setPrefWidth(150);
        adminButton.setPrefWidth(150);
        candidateButton.setPrefWidth(150);
        logoutButton.setPrefWidth(150);

        // =========================
        // Navigation Bar
        // =========================

        HBox navigation = new HBox(
                10,
                homeButton,
                electionButton,
                pollButton,
                voterButton,
                adminButton,
                candidateButton,
                logoutButton
        );

        navigation.setAlignment(Pos.CENTER);
        navigation.setPadding(new Insets(15));

        // =========================
        // Welcome Section
        // =========================

        Label welcome = new Label("Welcome to ElectraVote");

        welcome.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label description = new Label(
                "A secure platform for elections, voting and democratic governance."
        );

        Button startVotingButton = new Button("View Elections");

        startVotingButton.setPrefWidth(180);
        startVotingButton.setPrefHeight(40);

        VBox centerContent = new VBox(
                20,
                welcome,
                description,
                startVotingButton
        );

        centerContent.setAlignment(Pos.CENTER);

        // =========================
        // Statistics
        // =========================

        Label elections = new Label("Active Elections\n3");
        Label voters = new Label("Registered Voters\n1,250");
        Label polls = new Label("Quick Polls\n8");

        elections.setStyle("-fx-font-size: 18px;");
        voters.setStyle("-fx-font-size: 18px;");
        polls.setStyle("-fx-font-size: 18px;");

        HBox statistics = new HBox(
                50,
                elections,
                voters,
                polls
        );

        statistics.setAlignment(Pos.CENTER);

        // =========================
        // Main Layout
        // =========================

        BorderPane root = new BorderPane();

        root.setTop(new VBox(10, logoBox, navigation));

        root.setCenter(centerContent);

        root.setBottom(statistics);

        BorderPane.setAlignment(logoBox, Pos.CENTER_LEFT);

        root.setPadding(new Insets(20));

        // =========================
        // Navigation Actions
        // =========================

        homeButton.setOnAction(event -> {
            stage.setScene(scene);
        });

        electionButton.setOnAction(event -> {
            ElectionPage electionPage = new ElectionPage(stage);
            stage.setScene(electionPage.getScene());
        });

        pollButton.setOnAction(event -> {
            System.out.println("Quick Poll Page is under construction.");
            // QuickPollPage pollPage = new QuickPollPage(stage);
            // stage.setScene(pollPage.getScene());
        });

        voterButton.setOnAction(event -> {
            System.out.println("Voter Dashboard is under construction.");

        //     VoterDashboard voterDashboard =
        //             new VoterDashboard(stage);

        //     stage.setScene(voterDashboard.getScene());
         });

        adminButton.setOnAction(event -> {
            System.out.println("Admin Dashboard is under construction.");
            // AdminDashboard adminDashboard =
            //         new AdminDashboard(stage);

            // stage.setScene(adminDashboard.getScene());
        });

         candidateButton.setOnAction(event -> {
            System.out.println("Candidate Dashboard is under construction.");
        //     CandidateDashboard candidateDashboard =
        //             new CandidateDashboard(stage);

        //     stage.setScene(candidateDashboard.getScene());
        });

        startVotingButton.setOnAction(event -> {
            System.out.println("Election Page is under construction.");

            // ElectionPage electionPage =
            //         new ElectionPage(stage);

            // stage.setScene(electionPage.getScene());
        });

        logoutButton.setOnAction(event -> {
            System.out.println("Logout functionality is under construction.");
            // LoginPage loginPage =
            //         new LoginPage(stage);

            // stage.setScene(loginPage.getScene());
        });

        // =========================
        // Scene
        // =========================

        scene = new Scene(root, 1200, 700);
    

     
        return scene;
    }
}