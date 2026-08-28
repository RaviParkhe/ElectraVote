package com.electrovotesuperx.utils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigation {

    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void goTo(Scene scene) {

        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized.");
        }

        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        });
    }

    public static Stage getStage() {
        return stage;
    }

    public static void attachOwner(javafx.scene.control.Dialog<?> dialog) {
        if (dialog != null && stage != null) {
            try {
                dialog.initOwner(stage);
                dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            } catch (Exception ignored) {
            }
        }
    }

    public static void run(Runnable action) {
        Platform.runLater(action);
    }
}