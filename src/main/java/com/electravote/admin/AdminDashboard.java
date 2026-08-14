package com.electravote.admin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminDashboard extends Application {

    private Label electionValue;
    private Label voterValue;
    private Label votesValue;
    private Label turnoutValue;

    public static Stage AdminDashboardStage;
    public static Scene AdminDashboardScene;

    @Override
    public void start(Stage stage) {

        AdminDashboardStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fb;");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);

        sidebar.setStyle("-fx-background-color: #08264A;");

        Label logo = new Label("ElectraVote");

        logo.setStyle("-fx-text-fill: white;-fx-font-size: 24px;-fx-font-weight: bold;");

        Label organization = new Label("ABC College\nSuper Admin");

        organization.setStyle("-fx-text-fill: white;-fx-font-size: 14px;");

        Button dashboardBtn =new Button("Dashboard");
        Button electionsBtn =new Button("Elections");
        Button votersBtn =new Button("Voters");
        Button candidatesBtn =new Button("Candidates");
        Button liveVotingBtn =new Button("Live Voting");
        Button resultsBtn =new Button("Results");
        Button reportsBtn =new Button("Reports");
        Button settingsBtn =new Button("Settings");
        Button logoutBtn =new Button("Logout");

        Button[] buttons = {dashboardBtn,electionsBtn,votersBtn,candidatesBtn,liveVotingBtn,resultsBtn,reportsBtn,settingsBtn,logoutBtn};

        for (Button button : buttons) {

            button.setMaxWidth(Double.MAX_VALUE);

            button.setAlignment(Pos.CENTER_LEFT);

            button.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-font-size:14px;");
        }
        dashboardBtn.setStyle("-fx-background-color: #1464F4; -fx-text-fill: white;-fx-font-size: 14px;-fx-background-radius: 6;");
        

        Region sidebarSpace = new Region();

        VBox.setVgrow(sidebarSpace,Priority.ALWAYS);

        sidebar.getChildren().addAll(
                logo,
                organization,
                dashboardBtn,
                electionsBtn,
                votersBtn,
                candidatesBtn,
                liveVotingBtn,
                resultsBtn,
                reportsBtn,
                settingsBtn,
                sidebarSpace,
                logoutBtn
        );

        HBox topBar = new HBox();

        topBar.setPadding(new Insets(15, 25, 15, 25));

        topBar.setAlignment(Pos.CENTER_LEFT);

        topBar.setStyle("-fx-background-color: white;");

        Label pageTitle =new Label("Admin Dashboard");

        pageTitle.setStyle("-fx-font-size: 22px;-fx-font-weight: bold;");

        Region topSpace = new Region();

        HBox.setHgrow(topSpace,Priority.ALWAYS);

        Label admin =new Label("Admin");

        admin.setStyle("-fx-font-size: 14px;");


        topBar.getChildren().addAll(pageTitle,topSpace,admin);

        VBox content = new VBox(20);

        content.setPadding(new Insets(25));

        root.setLeft(sidebar);
        BorderPane center =new BorderPane();
        center.setTop(topBar);
        center.setCenter(content);
        root.setCenter(center);
        
        dashboardBtn.setOnAction(event -> {
            System.out.println("Clicked on Dashboard");
            center.setCenter(content);
        });        
        

        ElectionPage electionPage = new ElectionPage();

        electionsBtn.setOnAction(e->{
            System.out.println("Clicked On Election Button");
            center.setCenter(electionPage);
        });











        Label welcome =new Label("Welcome Admin!");

        welcome.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;");

        Label description =new Label("Here's what's happening with your elections today.");

        description.setStyle("-fx-text-fill: #6b7280;-fx-font-size: 13px;");

        HBox cards = new HBox(15);

        VBox electionsCard =new VBox(8);

        electionsCard.setPadding(new Insets(18));

        electionsCard.setStyle("-fx-background-color: white;-fx-background-radius: 10;");

        Label electionTitle =new Label("Total Elections");
        Label electionValue =new Label("12");
        electionValue.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");

        electionsCard.getChildren().addAll(electionTitle,electionValue);

        VBox votersCard =new VBox(8);

        votersCard.setPadding(new Insets(18));

        votersCard.setStyle("-fx-background-color: white;-fx-background-radius: 10;");

        Label voterTitle =new Label("Total Voters");

        Label voterValue =new Label("4,582");

        voterValue.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");

        votersCard.getChildren().addAll(voterTitle,voterValue);

        VBox votesCard =new VBox(8);
        votesCard.setPadding(new Insets(18));

        votesCard.setStyle("-fx-background-color: white;-fx-background-radius: 10;");

        Label votesTitle =new Label("Votes Cast");

        Label votesValue =new Label("2,947");

        votesValue.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");

        votesCard.getChildren().addAll(votesTitle,votesValue);

        VBox turnoutCard =new VBox(8);

        turnoutCard.setPadding(new Insets(18));

        turnoutCard.setStyle("-fx-background-color: white;-fx-background-radius: 10;");

        Label turnoutTitle =new Label("Turnout");

        Label turnoutValue =new Label("64.28%");

        turnoutValue.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");
        turnoutCard.getChildren().addAll(turnoutTitle,turnoutValue);

        HBox.setHgrow(electionsCard,Priority.ALWAYS);

        HBox.setHgrow(votersCard,Priority.ALWAYS);

        HBox.setHgrow(votesCard,Priority.ALWAYS);

        HBox.setHgrow(turnoutCard,Priority.ALWAYS);

        cards.getChildren().addAll(electionsCard,votersCard,votesCard,turnoutCard);

        HBox lower = new HBox(20);

        VBox chartBox = new VBox(10);

        chartBox.setPadding(new Insets(20));

        chartBox.setStyle("-fx-background-color: white;-fx-background-radius: 10;");

        Label chartTitle =new Label("Live Voting Turnout");

        chartTitle.setStyle("-fx-font-size: 17px;-fx-font-weight: bold;");

        NumberAxis xAxis =new NumberAxis();

        NumberAxis yAxis =new NumberAxis(0, 100, 20);

        LineChart<Number, Number> chart =new LineChart<>(xAxis,yAxis);

        chart.setLegendVisible(false);

        chart.setAnimated(false);

        chart.setPrefHeight(300);


        XYChart.Series<Number, Number>series =new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>(1, 10));

        series.getData().add(new XYChart.Data<>(2, 20));

        series.getData().add(new XYChart.Data<>(3, 35));

        series.getData().add(new XYChart.Data<>(4, 45));

        series.getData().add(new XYChart.Data<>(5, 55));

        series.getData().add(new XYChart.Data<>(6, 64));

        chart.getData().add(series);

        chartBox.getChildren().addAll(chartTitle,chart);

        VBox ongoing =new VBox(15);

        ongoing.setPadding(new Insets(20));

        ongoing.setStyle("-fx-background-color: white;-fx-background-radius: 10;");


        Label ongoingTitle =new Label("Ongoing Elections");

        ongoingTitle.setStyle("-fx-font-size: 17px;-fx-font-weight: bold;");


        Label election1 =new Label("Student Council Election\n" +"Turnout: 64.28%");
        Label election2 =new Label("Cultural Committee Election\n" +"Turnout: 48.13%");
        Label election3 =new Label("Sports Committee Election\n" +"Turnout: 36.91%");

        election1.setStyle("-fx-padding: 12;-fx-background-color: #f5f7fb;");

        election2.setStyle("-fx-padding: 12;-fx-background-color: #f5f7fb;");

        election3.setStyle("-fx-padding: 12;-fx-background-color: #f5f7fb;");

        ongoing.getChildren().addAll(ongoingTitle,election1,election2,election3);


        HBox.setHgrow(chartBox,Priority.ALWAYS);

        HBox.setHgrow(ongoing,Priority.ALWAYS);


        lower.getChildren().addAll(chartBox,ongoing);

        content.getChildren().addAll(welcome,description,cards,lower);

        

        Scene scene =new Scene(root,1200,750);
        AdminDashboardScene  = scene;

        AdminDashboardStage.setTitle("ElectroVote Admin Dashboard");
        AdminDashboardStage.setScene(scene);
        AdminDashboardStage.show();
    }
}