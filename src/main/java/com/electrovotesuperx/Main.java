package com.electrovotesuperx;

import com.electrovotesuperx.view.LoginPageView.Login;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        com.electrovotesuperx.config.FirebaseConfig.initFirebase();
        com.electrovotesuperx.config.DatabaseConfig.initializeDatabase();

        // Only ONE Stage
        Login.loginStage = stage;
        com.electrovotesuperx.utils.Navigation.init(stage);

        Login login = new Login();

        Scene loginScene = login.getScene(() -> {
            stage.close();
        });

        stage.setTitle("ElectraVote");
        stage.setScene(loginScene);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.show();
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}