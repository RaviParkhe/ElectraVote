

// package com.elctrovotesuperx.view.OrganizationView;

// import com.elctrovotesuperx.config.firebaseConfig.FirebaseAuthService;
// import com.elctrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
// import com.elctrovotesuperx.view.Page;

// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.PasswordField;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.VBox;
// import javafx.scene.text.Font;

// public class RegisterOrganization implements Page {

//     private Scene scene;

//     @Override
//     public Scene getScene(Runnable backCallback) {

//         BorderPane root =
//                 new BorderPane();

//         root.setStyle(
//                 "-fx-background-color: #F5F7FB;"
//         );

//         VBox content =
//                 new VBox(18);

//         content.setPadding(
//                 new Insets(40, 70, 40, 70)
//         );

//         content.setAlignment(
//                 Pos.TOP_LEFT
//         );

//         Label title =
//                 new Label(
//                         "Register Organization"
//                 );

//         title.setFont(
//                 Font.font("Arial", 30)
//         );

//         title.setStyle(
//                 "-fx-font-weight: bold;" +
//                 "-fx-text-fill: #172554;"
//         );

//         Label subtitle =
//                 new Label(
//                         "Create your organization and become its administrator."
//                 );

//         subtitle.setStyle(
//                 "-fx-font-size: 15px;" +
//                 "-fx-text-fill: #64748B;"
//         );

//         VBox form =
//                 new VBox(12);

//         form.setPrefWidth(600);

//         Label adminNameLabel =
//                 fieldLabel("Admin Name");

//         TextField adminName =
//                 new TextField();

//         adminName.setPromptText(
//                 "Enter administrator name"
//         );

//         styleField(adminName);

//         Label organizationLabel =
//                 fieldLabel("Organization Name");

//         TextField organizationName =
//                 new TextField();

//         organizationName.setPromptText(
//                 "Enter organization name"
//         );

//         styleField(organizationName);

//         // =====================================================
//         // JOIN CODE
//         // =====================================================

//         Label joinCodeLabel =
//                 fieldLabel(
//                         "Unique Organization Join Code"
//                 );

//         TextField joinCode =
//                 new TextField();

//         joinCode.setPromptText(
//                 "Generated automatically"
//         );

//         joinCode.setEditable(false);

//         styleField(joinCode);

//         Button generateCode =
//                 new Button(
//                         "Generate Join Code"
//                 );

//         generateCode.setPrefHeight(40);

//         generateCode.setStyle(
//                 "-fx-background-color: #EDE9FE;" +
//                 "-fx-text-fill: #6D28D9;" +
//                 "-fx-font-weight: bold;" +
//                 "-fx-background-radius: 8;"
//         );

//         generateCode.setOnAction(e -> {

//             joinCode.setText(
//                     FirebaseDatabaseService
//                             .generateJoinCode()
//             );
//         });

//         Label emailLabel =
//                 fieldLabel("Admin Email");

//         TextField email =
//                 new TextField();

//         email.setPromptText(
//                 "Enter administrator email"
//         );

//         styleField(email);

//         Label passwordLabel =
//                 fieldLabel("Password");

//         PasswordField password =
//                 new PasswordField();

//         password.setPromptText(
//                 "Create password"
//         );

//         styleField(password);

//         Label confirmPasswordLabel =
//                 fieldLabel(
//                         "Confirm Password"
//                 );

//         PasswordField confirmPassword =
//                 new PasswordField();

//         confirmPassword.setPromptText(
//                 "Confirm password"
//         );

//         styleField(confirmPassword);

//         HBox buttons =
//                 new HBox(15);

//         buttons.setPadding(
//                 new Insets(10, 0, 0, 0)
//         );

//         Button back =
//                 new Button("← Back");

//         back.setPrefWidth(130);
//         back.setPrefHeight(45);

//         back.setStyle(
//                 "-fx-background-color: #E2E8F0;" +
//                 "-fx-text-fill: #172554;" +
//                 "-fx-font-size: 14px;" +
//                 "-fx-background-radius: 9;"
//         );

//         back.setOnAction(e -> {

//             if (backCallback != null) {
//                 backCallback.run();
//             }
//         });

//         Button register =
//                 new Button(
//                         "Register1 Organization"
//                 );

//         register.setPrefWidth(230);
//         register.setPrefHeight(45);

//         register.setStyle(
//                 "-fx-background-color: #2563EB;" +
//                 "-fx-text-fill: white;" +
//                 "-fx-font-size: 14px;" +
//                 "-fx-font-weight: bold;" +
//                 "-fx-background-radius: 9;"
//         );

//         // =====================================================
//         // REGISTER
//         // =====================================================

//         register.setOnAction(e -> {

//             String admin =
//                     adminName.getText().trim();

//             String org =
//                     organizationName
//                             .getText()
//                             .trim();

//             String code =
//                     joinCode.getText().trim();

//             String adminEmail =
//                     email.getText().trim();

//             String pass =
//                     password.getText();

//             String confirm =
//                     confirmPassword.getText();

//             // =================================================
//             // VALIDATION
//             // =================================================

//             if (admin.isEmpty()
//                     || org.isEmpty()
//                     || adminEmail.isEmpty()
//                     || pass.isEmpty()
//                     || confirm.isEmpty()) {

//                 showAlert(
//                         Alert.AlertType.WARNING,
//                         "Missing Information",
//                         "Please fill all required fields."
//                 );

//                 return;
//             }

//             if (code.isEmpty()) {

//                 code =
//                         FirebaseDatabaseService
//                                 .generateJoinCode();

//                 joinCode.setText(code);
//             }

//             if (!pass.equals(confirm)) {

//                 showAlert(
//                         Alert.AlertType.WARNING,
//                         "Password Mismatch",
//                         "Password and confirm password must match."
//                 );

//                 return;
//             }

//             if (pass.length() < 6) {

//                 showAlert(
//                         Alert.AlertType.WARNING,
//                         "Weak Password",
//                         "Password must contain at least 6 characters."
//                 );

//                 return;
//             }

//             register.setDisable(true);

//             String finalCode = code;

//             // =================================================
//             // RUN FIREBASE IN BACKGROUND
//             // =================================================

//             Thread thread =
//                     new Thread(() -> {

//                         try {

//                             // =================================
//                             // STEP 1
//                             // CREATE FIREBASE AUTH ACCOUNT
//                             // =================================

//                             FirebaseAuthService.AuthResult auth =
//                                     FirebaseAuthService.createUser(
//                                             adminEmail,
//                                             pass
//                                     );

//                             if (!auth.isSuccess()) {

//                                 Platform.runLater(() -> {

//                                     register.setDisable(false);

//                                     showAlert(
//                                             Alert.AlertType.ERROR,
//                                             "Registration Failed",
//                                             auth.getMessage()
//                                     );
//                                 });

//                                 return;
//                             }

//                             // =================================
//                             // STEP 2
//                             // SAVE ORGANIZATION
//                             // =================================

//                             boolean organizationSaved =
//                                     FirebaseDatabaseService
//                                             .saveOrganization(
//                                                     finalCode,
//                                                     org,
//                                                     admin,
//                                                     adminEmail,
//                                                     auth.getLocalId(),
//                                                     auth.getIdToken()
//                                             );

//                             if (!organizationSaved) {

//                                 Platform.runLater(() -> {

//                                     register.setDisable(false);

//                                     showAlert(
//                                             Alert.AlertType.ERROR,
//                                             "Database Error",
//                                             "Firebase account was created, " +
//                                             "but organization data could not be saved."
//                                     );
//                                 });

//                                 return;
//                             }

//                             // =================================
//                             // STEP 3
//                             // SAVE ADMIN MEMBERSHIP
//                             // =================================

//                             boolean membershipSaved =
//                                     FirebaseDatabaseService
//                                             .saveAdminMembership(
//                                                     finalCode,
//                                                     auth.getLocalId(),
//                                                     admin,
//                                                     adminEmail,
//                                                     auth.getIdToken()
//                                             );

//                             if (!membershipSaved) {

//                                 Platform.runLater(() -> {

//                                     register.setDisable(false);

//                                     showAlert(
//                                             Alert.AlertType.ERROR,
//                                             "Membership Error",
//                                             "Organization was created, " +
//                                             "but admin membership could not be saved."
//                                     );
//                                 });

//                                 return;
//                             }

//                             // =================================
//                             // SUCCESS
//                             // =================================

//                             Platform.runLater(() -> {

//                                 register.setDisable(false);

//                                 showAlert(
//                                         Alert.AlertType.INFORMATION,
//                                         "Organization Registered",
//                                         "Organization registered successfully!\n\n"
//                                                 + "Organization: "
//                                                 + org
//                                                 + "\n\n"
//                                                 + "Organization Join Code:\n"
//                                                 + finalCode
//                                                 + "\n\n"
//                                                 + "Save this Join Code. "
//                                                 + "It is required for organization "
//                                                 + "and voter sign-in."
//                                 );

//                                 if (backCallback != null) {
//                                     backCallback.run();
//                                 }
//                             });

//                         } catch (Exception ex) {

//                             ex.printStackTrace();

//                             Platform.runLater(() -> {

//                                 register.setDisable(false);

//                                 showAlert(
//                                         Alert.AlertType.ERROR,
//                                         "Firebase Error",
//                                         ex.getMessage()
//                                                 != null
//                                                 ? ex.getMessage()
//                                                 : "Unable to connect to Firebase."
//                                 );
//                             });
//                         }

//                     });

//             thread.setDaemon(true);
//             thread.start();
//         });

//         buttons.getChildren().addAll(
//                 back,
//                 register
//         );

//         form.getChildren().addAll(

//                 adminNameLabel,
//                 adminName,

//                 organizationLabel,
//                 organizationName,

//                 joinCodeLabel,
//                 joinCode,

//                 generateCode,

//                 emailLabel,
//                 email,

//                 passwordLabel,
//                 password,

//                 confirmPasswordLabel,
//                 confirmPassword,

//                 buttons
//         );

//         content.getChildren().addAll(
//                 title,
//                 subtitle,
//                 form
//         );

//         root.setCenter(content);

//         scene =
//                 new Scene(
//                         root
//                 );

//         return scene;
//     }

//     // =========================================================
//     // FIELD LABEL
//     // =========================================================

//     private Label fieldLabel(
//             String text) {

//         Label label =
//                 new Label(text);

//         label.setStyle(
//                 "-fx-font-size: 14px;" +
//                 "-fx-font-weight: bold;" +
//                 "-fx-text-fill: #334155;"
//         );

//         return label;
//     }

//     // =========================================================
//     // FIELD STYLE
//     // =========================================================

//     private void styleField(
//             TextField field) {

//         field.setPrefWidth(600);
//         field.setPrefHeight(45);

//         field.setStyle(
//                 "-fx-background-color: white;" +
//                 "-fx-border-color: #CBD5E1;" +
//                 "-fx-border-radius: 8;" +
//                 "-fx-background-radius: 8;" +
//                 "-fx-padding: 0 12;" +
//                 "-fx-font-size: 14px;"
//         );
//     }

//     // =========================================================
//     // ALERT
//     // =========================================================

//     private void showAlert(
//             Alert.AlertType type,
//             String title,
//             String message) {

//         Alert alert =
//                 new Alert(type);

//         alert.setTitle(title);
//         alert.setHeaderText(null);
//         alert.setContentText(message);

//         alert.showAndWait();
//     }
// }


package com.elctrovotesuperx.view.OrganizationView;

import com.elctrovotesuperx.controller.OrganizationController.OrganizationController;
import com.elctrovotesuperx.model.OrganizationModel.Organization;
import com.elctrovotesuperx.view.Page;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class RegisterOrganization implements Page {

    private Scene scene;
    private final OrganizationController controller = new OrganizationController();

    @Override
    public Scene getScene(Runnable backCallback) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#F5F7FB;");

        VBox content = new VBox(18);
        content.setPadding(new Insets(40,70,40,70));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Register Organization");
        title.setFont(Font.font("Arial",30));
        title.setStyle("-fx-font-weight:bold;-fx-text-fill:#172554;");

        Label subtitle = new Label(
                "Create your organization and become its administrator."
        );
        subtitle.setStyle("-fx-font-size:15px;-fx-text-fill:#64748B;");

        VBox form = new VBox(12);
        form.setPrefWidth(600);

        // Admin Name
        Label adminNameLabel = fieldLabel("Admin Name");
        TextField adminName = new TextField();
        adminName.setPromptText("Enter administrator name");
        styleField(adminName);

        // Organization Name
        Label orgLabel = fieldLabel("Organization Name");
        TextField organizationName = new TextField();
        organizationName.setPromptText("Enter organization name");
        styleField(organizationName);

        // Join Code
        Label joinLabel = fieldLabel("Organization Join Code");
        TextField joinCode = new TextField();
        joinCode.setPromptText("Generated automatically");
        joinCode.setEditable(false);
        styleField(joinCode);

        Button generate = new Button("Generate Join Code");
        generate.setPrefHeight(40);
        generate.setStyle(
                "-fx-background-color:#EDE9FE;" +
                "-fx-text-fill:#6D28D9;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:8;"
        );

        generate.setOnAction(e ->
                joinCode.setText(controller.generateJoinCode())
        );

        // Email
        Label emailLabel = fieldLabel("Admin Email");
        TextField email = new TextField();
        email.setPromptText("Enter administrator email");
        styleField(email);

        // Password
        Label passLabel = fieldLabel("Password");
        PasswordField password = new PasswordField();
        password.setPromptText("Create password");
        styleField(password);

        // Confirm Password
        Label confirmLabel = fieldLabel("Confirm Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm password");
        styleField(confirmPassword);

        // Buttons
        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(10,0,0,0));

        Button back = new Button("← Back");
        back.setPrefSize(130,45);
        back.setStyle(
                "-fx-background-color:#E2E8F0;" +
                "-fx-text-fill:#172554;" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:9;"
        );

        back.setOnAction(e -> {
            if(backCallback != null){
                backCallback.run();
            }
        });

        Button register = new Button("Register Organization");
        register.setPrefSize(230,45);
        register.setStyle(
                "-fx-background-color:#2563EB;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );

        // Register Action
        register.setOnAction(e -> {

            Organization org = new Organization();

            org.setAdminName(adminName.getText().trim());
            org.setOrganizationName(organizationName.getText().trim());
            org.setAdminEmail(email.getText().trim());
            org.setPassword(password.getText());
            org.setJoinCode(joinCode.getText().trim());

            register.setDisable(true);

            Thread thread = new Thread(() -> {

                try {

                    var result = controller.register(
                            org,
                            confirmPassword.getText()
                    );

                    Platform.runLater(() -> {

                        register.setDisable(false);

                        if(result.isSuccess()){

                            joinCode.setText(org.getJoinCode());

                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Registration Successful",
                                    "Organization registered successfully!\n\n" +
                                            "Organization: " +
                                            org.getOrganizationName() +
                                            "\n\nJoin Code:\n" +
                                            org.getJoinCode()
                            );

                            if(backCallback != null){
                                backCallback.run();
                            }

                        }else{

                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Registration Failed",
                                    result.getMessage()
                            );
                        }
                    });

                }catch(Exception ex){

                    Platform.runLater(() ->{

                        register.setDisable(false);

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Firebase Error",
                                ex.getMessage()
                        );
                    });
                }

            });

            thread.setDaemon(true);
            thread.start();
        });

        buttons.getChildren().addAll(back,register);

        form.getChildren().addAll(
                adminNameLabel,adminName,
                orgLabel,organizationName,
                joinLabel,joinCode,
                generate,
                emailLabel,email,
                passLabel,password,
                confirmLabel,confirmPassword,
                buttons
        );

        content.getChildren().addAll(title,subtitle,form);

        root.setCenter(content);

        scene = new Scene(root);

        return scene;
    }

    // Label Style
    private Label fieldLabel(String text){

        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#334155;"
        );

        return label;
    }

    // TextField Style
    private void styleField(TextField field){

        field.setPrefSize(600,45);

        field.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#CBD5E1;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-padding:0 12;" +
                "-fx-font-size:14px;"
        );
    }

    // Alert
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