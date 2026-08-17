package com.elctrovotesuperx;

import com.elctrovotesuperx.view.LoginPageView.Login;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Only ONE Stage
        Login.loginStage = stage;

        Login login = new Login();

        Scene loginScene = login.getScene(() -> {
            stage.close();
        });

        stage.setTitle("ElectraVote");
        stage.setScene(loginScene);
        stage.setMaximized(true);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}