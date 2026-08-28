package com.elctrovotesuperx.view.LoginPageView;

import com.elctrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.elctrovotesuperx.exception.AuthenticationException;
import com.elctrovotesuperx.view.Page;
import com.elctrovotesuperx.view.HomePageView.HomePage;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login implements Page {

    public static Stage loginStage;

    private Scene loginScene;

    // =========================================
    // GET SCENE
    // =========================================

    @Override
    public Scene getScene(Runnable ignored) {

        // =========================================
        // BACKGROUND IMAGE
        // =========================================

        Image loginImage = null;

        try {
            loginImage = new Image(
                    getClass().getResourceAsStream(
                            "/assests/images/login_image.jpeg"));
        } catch (Exception e) {
            // Image not found — continue without it
        }

        ImageView imageView = new ImageView();

        if (loginImage != null && !loginImage.isError()) {

            imageView.setImage(loginImage);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
        }

        // =========================================
        // SIGN UP / SIGN IN TABS
        // =========================================

        Region signUpLine = new Region();
        signUpLine.setPrefWidth(90);
        signUpLine.setPrefHeight(3);
        signUpLine.setStyle("-fx-background-color: #2563EB");

        Region signInLine = new Region();
        signInLine.setPrefWidth(90);
        signInLine.setPrefHeight(3);
        signInLine.setStyle("-fx-background-color: #2563EB");
        signInLine.setVisible(false);

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setStyle(
                "-fx-font-size: 25;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #172554;" +
                        "-fx-cursor: hand;");

        Label signInLabel = new Label("Sign In");
        signInLabel.setStyle(
                "-fx-font-size: 25;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #172554;" +
                        "-fx-cursor: hand;");

        VBox signUpTab = new VBox(5, signUpLabel, signUpLine);
        signUpTab.setAlignment(Pos.CENTER);

        VBox signInTab = new VBox(5, signInLabel, signInLine);
        signInTab.setAlignment(Pos.CENTER);

        HBox tabsBox = new HBox(100, signUpTab, signInTab);
        tabsBox.setAlignment(Pos.CENTER);

        // =========================================
        // SIGN UP FIELDS
        // =========================================

        TextField name = new TextField();
        name.setPromptText("Full Name");
        name.setPrefWidth(500);
        name.setPrefHeight(40);
        name.setStyle("-fx-background-radius: 20;");

        TextField signUpEmail = new TextField();
        signUpEmail.setPromptText("Email Address");
        signUpEmail.setPrefWidth(500);
        signUpEmail.setPrefHeight(40);
        signUpEmail.setStyle("-fx-background-radius: 20;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date of Birth");
        datePicker.setPrefWidth(500);
        datePicker.setPrefHeight(40);
        datePicker.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;");

        PasswordField signUpPassword = new PasswordField();
        signUpPassword.setPromptText("Password");
        signUpPassword.setPrefWidth(500);
        signUpPassword.setPrefHeight(40);
        signUpPassword.setStyle("-fx-background-radius: 20;");

        Button signUpSubmitBtn = new Button("Submit");
        signUpSubmitBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;");
        signUpSubmitBtn.setPrefWidth(500);
        signUpSubmitBtn.setPrefHeight(40);

        signUpSubmitBtn.setOnMouseEntered(e -> {
            signUpSubmitBtn.setScaleX(1.05);
            signUpSubmitBtn.setScaleY(1.05);
        });

        signUpSubmitBtn.setOnMouseExited(e -> {
            signUpSubmitBtn.setScaleX(1);
            signUpSubmitBtn.setScaleY(1);
        });

        // =========================================
        // SIGN IN FIELDS
        // =========================================

        TextField signInEmail = new TextField();
        signInEmail.setPromptText("Email Address");
        signInEmail.setPrefWidth(500);
        signInEmail.setPrefHeight(40);
        signInEmail.setStyle("-fx-background-radius: 20;");

        PasswordField signInPassword = new PasswordField();
        signInPassword.setPromptText("Password");
        signInPassword.setPrefWidth(500);
        signInPassword.setPrefHeight(40);
        signInPassword.setStyle("-fx-background-radius: 20;");

        Button signInSubmitBtn = new Button("Submit");
        signInSubmitBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;");
        signInSubmitBtn.setPrefWidth(500);
        signInSubmitBtn.setPrefHeight(40);

        signInSubmitBtn.setOnMouseEntered(e -> {
            signInSubmitBtn.setScaleX(1.05);
            signInSubmitBtn.setScaleY(1.05);
        });

        signInSubmitBtn.setOnMouseExited(e -> {
            signInSubmitBtn.setScaleX(1);
            signInSubmitBtn.setScaleY(1);
        });

        // =========================================
        // OUTPUT MESSAGE
        // =========================================

        Text output = new Text();
        output.setStyle("-fx-font-size: 14;");

        // =========================================
        // SIGN UP FORM
        // =========================================

        VBox signUpBox = new VBox(50,
                name, signUpEmail, datePicker,
                signUpPassword, signUpSubmitBtn);
        signUpBox.setFillWidth(false);
        signUpBox.setAlignment(Pos.CENTER);

        // =========================================
        // SIGN IN FORM
        // =========================================

        VBox signInBox = new VBox(50,
                signInEmail, signInPassword,
                signInSubmitBtn);
        signInBox.setFillWidth(false);
        signInBox.setVisible(false);

        // =========================================
        // FORM AREA
        // =========================================

        StackPane formArea = new StackPane();
        formArea.setPrefHeight(400);
        formArea.setMinHeight(400);
        formArea.setMaxHeight(400);
        formArea.getChildren().addAll(signUpBox, signInBox);

        // =========================================
        // SIGN UP ACTION
        // =========================================

        signUpSubmitBtn.setOnAction(e -> {

            if (name.getText().isEmpty()
                    || signUpEmail.getText().isEmpty()
                    || datePicker.getValue() == null
                    || signUpPassword.getText().isEmpty()) {

                output.setFill(Color.RED);
                output.setText("Please fill all fields.");
                shakeButton(signUpSubmitBtn);
                return;
            }

            signUpSubmitBtn.setDisable(true);
            output.setFill(Color.web("#2563EB"));
            output.setText("Creating account...");

            new Thread(() -> {

                try {

                    FirebaseAuthService.AuthResult result =
                            FirebaseAuthService.createUser(
                                    signUpEmail.getText().trim(),
                                    signUpPassword.getText());

                    javafx.application.Platform.runLater(() -> {

                        signUpSubmitBtn.setDisable(false);

                        if (result.isSuccess()) {

                            output.setFill(Color.web("#059669"));
                            output.setText(
                                    "Account created successfully! " +
                                            "Switch to Sign In.");

                        } else {

                            output.setFill(Color.RED);
                            output.setText(result.getMessage());
                        }
                    });

                } catch (AuthenticationException ex) {

                    javafx.application.Platform.runLater(() -> {

                        signUpSubmitBtn.setDisable(false);
                        output.setFill(Color.RED);
                        output.setText(ex.getMessage());
                    });
                }

            }).start();
        });

        // =========================================
        // SIGN IN ACTION
        // =========================================

        signInSubmitBtn.setOnAction(e -> {

            if (signInEmail.getText().isEmpty()
                    || signInPassword.getText().isEmpty()) {

                output.setFill(Color.RED);
                output.setText("Please fill all fields.");
                shakeButton(signInSubmitBtn);
                return;
            }

            signInSubmitBtn.setDisable(true);
            output.setFill(Color.web("#2563EB"));
            output.setText("Signing in...");

            new Thread(() -> {

                try {

                    FirebaseAuthService.AuthResult result =
                            FirebaseAuthService.signIn(
                                    signInEmail.getText().trim(),
                                    signInPassword.getText());

                    javafx.application.Platform.runLater(() -> {

                        signInSubmitBtn.setDisable(false);

                        if (result.isSuccess()) {

                            // Navigate to HomePage
                            HomePage homepage =
                                    new HomePage(loginStage);

                            loginStage.setScene(
                                    homepage.getScene(() -> {

                                        // Logout → return to Login
                                        loginStage.setScene(loginScene);
                                        loginStage.setMaximized(true);
                                    }));

                            loginStage.setMaximized(true);

                        } else {

                            output.setFill(Color.RED);
                            output.setText(result.getMessage());
                        }
                    });

                } catch (AuthenticationException ex) {

                    javafx.application.Platform.runLater(() -> {

                        signInSubmitBtn.setDisable(false);
                        output.setFill(Color.RED);
                        output.setText(ex.getMessage());
                    });
                }

            }).start();
        });

        // =========================================
        // TAB SWITCHING — SIGN UP
        // =========================================

        signUpLabel.setOnMouseClicked(e -> {

            output.setText("");

            signUpBox.setVisible(true);
            signInBox.setVisible(false);

            signUpLine.setVisible(true);
            signInLine.setVisible(false);

            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(300), signUpBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            TranslateTransition slide =
                    new TranslateTransition(
                            Duration.millis(300), signUpBox);
            slide.setFromX(-50);
            slide.setToX(0);
            slide.play();
        });

        // =========================================
        // TAB SWITCHING — SIGN IN
        // =========================================

        signInLabel.setOnMouseClicked(e -> {

            output.setText("");

            signUpBox.setVisible(false);
            signInBox.setVisible(true);

            signUpLine.setVisible(false);
            signInLine.setVisible(true);

            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(300), signInBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            TranslateTransition slide =
                    new TranslateTransition(
                            Duration.millis(300), signInBox);
            slide.setFromX(50);
            slide.setToX(0);
            slide.play();
        });

        // =========================================
        // FORM CARD
        // =========================================

        VBox formBox = new VBox(50,
                tabsBox, formArea, output);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(500);
        formBox.setPrefHeight(650);
        formBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.94);" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 30;" +
                        "-fx-effect: dropshadow(gaussian, " +
                        "rgba(0,0,0,0.20), 20, 0, 0, 5);");

        // =========================================
        // ROOT
        // =========================================

        StackPane root = new StackPane();

        if (loginImage != null && !loginImage.isError()) {

            // Bind image to fill the window
            imageView.fitWidthProperty().bind(
                    root.widthProperty());
            imageView.fitHeightProperty().bind(
                    root.heightProperty());

            root.getChildren().add(imageView);
        } else {

            root.setStyle(
                    "-fx-background-color: #F5F7FB;");
        }

        root.getChildren().add(formBox);
        StackPane.setAlignment(formBox, Pos.CENTER_RIGHT);
        StackPane.setMargin(formBox, new Insets(0, 50, 0, 0));

        // =========================================
        // SCENE
        // =========================================

        loginScene = new Scene(root);

        return loginScene;
    }

    // =========================================
    // SHAKE ANIMATION
    // =========================================

    private void shakeButton(Button button) {

        TranslateTransition shake =
                new TranslateTransition(
                        Duration.millis(70), button);

        shake.setFromX(-10);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}