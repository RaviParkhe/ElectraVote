package com.elctrovotesuperx.view.OfflineView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomepageOffline extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        BorderPane borderpane = new BorderPane();

        HBox hb1 = new HBox();
        hb1.setPrefWidth(255);
        hb1.setPrefHeight(64);
        hb1.setAlignment(Pos.CENTER_LEFT);
        hb1.setPadding(new Insets(0, 20, 0, 25));
        hb1.setStyle("-fx-Background-color : #121d35;");

        try {
            Image img = new Image("assects\\logo\\new logo.jpeg");
            ImageView imageview = new ImageView(img);
            imageview.setFitWidth(195);
            imageview.setFitHeight(45);
            hb1.getChildren().addAll(imageview);
        } catch (Exception ignored) {
            Label logoFallback = new Label("ElectraVote Offline");
            logoFallback.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
            hb1.getChildren().add(logoFallback);
        }

        Text text1 = new Text("General");
        Button Home = new Button("⌂   Home");
        Button organizations = new Button("▥    Organizations");

        VBox vb11 = new VBox();
        vb11.getChildren().addAll(text1, Home, organizations);

        VBox vb1 = new VBox();
        vb1.getChildren().addAll(vb11);
        vb1.setPrefWidth(255);
        vb1.setPrefHeight(729);
        vb1.setPadding(new Insets(12, 16, 12, 16));
        vb1.setStyle("-fx-Background-color : #52d5c2");

        VBox vbox = new VBox();
        vbox.setPrefWidth(255);
        vbox.setPrefHeight(729.600);
        vbox.setStyle("-fx-Background-color : #0c121f");
        vbox.getChildren().addAll(hb1,
                vb1);

        borderpane.setLeft(vbox);
        Scene sc = new Scene(borderpane);
        stage.setScene(sc);
        stage.setMaximized(true);
        stage.setTitle("Member Dashboard");
        stage.show();
    }
}
