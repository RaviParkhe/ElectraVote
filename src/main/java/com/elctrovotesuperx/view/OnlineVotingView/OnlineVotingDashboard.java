package com.elctrovotesuperx.view.OnlineVotingView;

import com.elctrovotesuperx.view.Page;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class OnlineVotingDashboard implements Page {

    private Scene scene;

    @Override
    public Scene getScene(Runnable backCallback) {

        VBox box = new VBox(18);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(35));
        box.setStyle("-fx-background-color: #F5F7FB;");

        Label title = new Label("Online Voting");
        title.setFont(Font.font("Arial", 28));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #172554;");

        Label description = new Label(
            "ElectraVote module: Online Voting. This screen is ready for your business logic."
        );
        description.setStyle("-fx-text-fill: #64748B;");

        Label status = new Label("Ready");
        status.setStyle("-fx-text-fill: #3264E5;");

        Button back = new Button("← Back to Home");
        back.setPrefWidth(180);
        back.setPrefHeight(42);
        back.setStyle(
            "-fx-background-color: #3264E5; -fx-text-fill: white;" +
            "-fx-background-radius: 9;"
        );
        back.setOnAction(e -> backCallback.run());

        box.getChildren().addAll(title, description, status);

        Button vote = new Button("Cast Secret Ballot");
        vote.setOnAction(e -> status.setText(
            "Demo token generated → vote recorded → token burned."
        ));
        box.getChildren().add(vote);

        box.getChildren().add(back);

        scene = new Scene(box, 1200, 700);
        return scene;
    }
}
