package com.elctrovotesuperx.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ElectionPage {

    private final Stage stage;
    private final Scene scene;

    public ElectionPage(Stage stage) {

        this.stage = stage;

        Label title = new Label("Election Management");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        Button backButton = new Button("Back to Home");

        backButton.setOnAction(event -> {

            HomePage homePage =
                    new HomePage(stage);

            stage.setScene(homePage.getScene());
        });

        VBox root = new VBox(
                20,
                title,
                backButton
        );

        root.setAlignment(Pos.CENTER);

        scene = new Scene(root, 1200, 700);
    }

    public Scene getScene() {
        return scene;
    }
}