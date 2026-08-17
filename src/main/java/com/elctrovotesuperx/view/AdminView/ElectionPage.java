package com.elctrovotesuperx.view.AdminView;


import com.elctrovotesuperx.view.Page;
import com.elctrovotesuperx.view.HomePageView.Homepage;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ElectionPage implements Page {

    private Scene scene;

    @Override
    public Scene getScene(Runnable adminCallback) {

        Label title =
                new Label("Election Management");

        title.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;"
        );

        Button backButton =
                new Button("Back to Admin Dashboard");

        backButton.setOnAction(event -> {

            adminCallback.run();

        });

        VBox root =
                new VBox(
                    20,
                    title,
                    backButton
                );

        root.setAlignment(
            Pos.CENTER
        );

        scene =
                new Scene(
                    root,
                    1200,
                    700
                );

        return scene;
    }
}