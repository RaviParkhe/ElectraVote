package com.elctrovotesuperx.view.OrganizationView;

import com.elctrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.elctrovotesuperx.dao.OrganizationDAO.FirestoreDAO;
import com.elctrovotesuperx.view.Page;
import com.elctrovotesuperx.view.AdminView.AdminDashboard;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.text.Font;

public class SignInOrganization implements Page {

        private Stage stage;
    private Scene scene;

    public SignInOrganization(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Scene getScene(Runnable backCallback) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#F5F7FB;");

        VBox container = new VBox(22);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));

        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefSize(76,76);
        iconBox.setStyle("-fx-background-color:#059669;-fx-background-radius:50;");

        Label icon = new Label("▣");
        icon.setFont(Font.font(28));
        icon.setTextFill(Color.WHITE);
        iconBox.getChildren().add(icon);

        Label title = new Label("Sign In as Organization");
        title.setFont(Font.font("Arial",30));
        title.setStyle("-fx-font-weight:bold;-fx-text-fill:#172554;");

        Label subtitle = new Label(
                "Sign in to access your organization's management portal.");
        subtitle.setStyle("-fx-text-fill:#64748B;-fx-font-size:15px;");

        VBox card = new VBox(16);
        card.setPrefWidth(500);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color:white;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#DCE3F0;" +
                "-fx-border-radius:16;");

        Label codeLabel = fieldLabel("Organization Join Code");
        TextField joinCode = new TextField();
        joinCode.setPromptText("EV-XXXX-XXXX");
        styleField(joinCode);

        Label emailLabel = fieldLabel("Admin Email");
        TextField email = new TextField();
        styleField(email);

        Label passwordLabel = fieldLabel("Password");
        PasswordField password = new PasswordField();
        styleField(password);

        Button signIn = new Button("Sign In as Organization →");
        signIn.setPrefSize(440,48);
        signIn.setStyle("-fx-background-color:#059669;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;");

        // =====================================================
        // SIGN IN
        // =====================================================

        signIn.setOnAction(e -> {

            String code = joinCode.getText().trim().toUpperCase();
            String adminEmail = email.getText().trim();
            String pass = password.getText();

            if(code.isEmpty() || adminEmail.isEmpty() || pass.isEmpty()){
                showAlert(Alert.AlertType.WARNING,
                        "Missing Information",
                        "Please enter Join Code, Email and Password.");
                return;
            }

            signIn.setDisable(true);

            Thread thread = new Thread(() -> {

                try {

                    // STEP 1 : Firebase Authentication
                    FirebaseAuthService.AuthResult auth =
                            FirebaseAuthService.signIn(adminEmail, pass);

                    if(!auth.isSuccess()){

                        Platform.runLater(() ->{
                            signIn.setDisable(false);
                            showAlert(Alert.AlertType.ERROR,
                                    "Login Failed",
                                    auth.getMessage());
                        });
                        return;
                    }

                    // STEP 2 : Read Organization from Firestore
                    JsonObject organization =
                            FirestoreDAO.getOrganization(code, auth.getIdToken());

                    if(organization == null){

                        Platform.runLater(() ->{
                            signIn.setDisable(false);
                            showAlert(Alert.AlertType.ERROR,
                                    "Invalid Join Code",
                                    "Organization not found.");
                        });
                        return;
                    }

                    // STEP 3 : Read Firestore fields
                    String adminUid = FirestoreDAO.getString(
                            organization,"adminUid");

                    String organizationName = FirestoreDAO.getString(
                            organization,"organizationName");

                    String firestoreEmail = FirestoreDAO.getString(
                            organization,"adminEmail");

                    // STEP 4 : Verify Admin
                    if(!auth.getLocalId().equals(adminUid)
                            || !adminEmail.equalsIgnoreCase(firestoreEmail)){

                        Platform.runLater(() ->{
                            signIn.setDisable(false);
                            showAlert(Alert.AlertType.ERROR,
                                    "Access Denied",
                                    "You are not the administrator of this organization.");
                        });
                        return;
                    }

                    // SUCCESS
                    //Platform.runLater(() ->{

                        // signIn.setDisable(false);

                        // showAlert(Alert.AlertType.INFORMATION,
                        //         "Login Successful",
                        //         "Welcome Administrator!\n\n"
                        //                 + "Organization : "
                        //                 + organizationName
                        //                 + "\nJoin Code : "
                        //                 + code);

                        // TODO : Open Admin Dashboard

                        
                   // });

                   // =====================================================
// SUCCESS → ADMIN DASHBOARD
// =====================================================

Platform.runLater(() -> {

    signIn.setDisable(false);

    // Persist session for all controllers
    com.elctrovotesuperx.config.SessionManager.idToken = auth.getIdToken();
    com.elctrovotesuperx.config.SessionManager.joinCode = code;
    com.elctrovotesuperx.config.SessionManager.organizationName = organizationName;
    com.elctrovotesuperx.config.SessionManager.adminUid = auth.getLocalId();
    com.elctrovotesuperx.config.SessionManager.adminEmail = adminEmail;

    Stage stage = (Stage) scene.getWindow();

    AdminDashboard adminDashboard = new AdminDashboard();

    adminDashboard.start(stage);
});
                } catch (Exception ex){

                    ex.printStackTrace();

                    Platform.runLater(() ->{
                        signIn.setDisable(false);

                        showAlert(Alert.AlertType.ERROR,
                                "Firebase Error",
                                ex.getMessage());
                    });
                }

            });

            thread.setDaemon(true);
            thread.start();
        });

        Button back = new Button("← Back to Organization Portal");
        back.setPrefSize(440,44);
        back.setStyle("-fx-background-color:transparent;" +
                "-fx-border-color:#CBD5E1;" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;");

        back.setOnAction(e ->{
            if(backCallback!=null){
                backCallback.run();
            }
        });

        card.getChildren().addAll(
                codeLabel,joinCode,
                emailLabel,email,
                passwordLabel,password,
                signIn,
                back
        );

        container.getChildren().addAll(
                iconBox,title,subtitle,card
        );

        root.setCenter(container);

        scene = new Scene(root);
        return scene;
    }

    private Label fieldLabel(String text){
        Label label = new Label(text);
        label.setStyle("-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#172554;");
        return label;
    }

    private void styleField(TextField field){
        field.setPrefHeight(45);
        field.setStyle("-fx-font-size:14px;" +
                "-fx-background-radius:8;" +
                "-fx-border-radius:8;" +
                "-fx-border-color:#CBD5E1;");
    }

    private void showAlert(Alert.AlertType type,
                           String title,
                           String message){

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}