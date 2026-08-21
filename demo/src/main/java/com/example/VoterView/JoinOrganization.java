package com.example.VoterView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class JoinOrganization {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String GREEN = "#10B981";
    private static final String RED = "#EF4444";

    /**
     * Creates the Join Organization view with dynamic code validation and request handling.
     */
    public static VBox createJoinOrganizationView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header Section
        VBox heading = new VBox(6);
        Text title = new Text("Join a New Organization ＋");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text("Enter your secure organization join code provided by your institution administrator to request membership.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        // Form Card Container
        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(30));
        formCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #3B82F6;" +
            "-fx-border-width: 1.8;" +
            "-fx-border-radius: 14;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(20, 100, 244, 0.12), 15, 0, 0, 6);"
        );

        Text formHeader = new Text("Organization Authorization");
        formHeader.setFill(Color.web(TEXT));
        formHeader.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Text formSub = new Text("Enter the unique alphanumeric join code below:");
        formSub.setFill(Color.web(SECONDARY));
        formSub.setFont(Font.font(13));

        // Code Input Field
        VBox inputBox = new VBox(6);
        Label inputLabel = new Label("Secure Join Code");
        inputLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 12.5px;");

        PasswordField codeField = new PasswordField();
        codeField.setPromptText("e.g., ELEC2026");
        codeField.setStyle(
            "-fx-background-color: #F8FAFC;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 13px;"
        );
        inputBox.getChildren().addAll(inputLabel, codeField);

        // Feedback Message Label
        Label feedbackLabel = new Label();
        feedbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        feedbackLabel.setVisible(false);

        // Action Button
        Button submitCodeBtn = new Button("Submit Join Request 🚀");
        submitCodeBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #10B981, #059669);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 22;" +
            "-fx-background-radius: 7;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        );

        // Mock valid join code for dynamic validation (Can be connected to your DAO/Controller later)
        final String VALID_JOIN_CODE = "ELEC2026";

        submitCodeBtn.setOnAction(e -> {
            String enteredCode = codeField.getText().trim();

            if (enteredCode.isEmpty()) {
                feedbackLabel.setText("⚠️ Please enter a join code before submitting.");
                feedbackLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                feedbackLabel.setVisible(true);
                return;
            }

            if (enteredCode.equals(VALID_JOIN_CODE)) {
                // Success message when correct join code is entered
                feedbackLabel.setText("✅ Your request is sent to admin for approval.");
                feedbackLabel.setStyle("-fx-text-fill: " + GREEN + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                feedbackLabel.setVisible(true);
                codeField.clear();
            } else {
                // Error message when wrong code is entered
                feedbackLabel.setText("❌ Please enter correct join code.");
                feedbackLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                feedbackLabel.setVisible(true);
            }
        });

        HBox buttonRow = new HBox(submitCodeBtn);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        formCard.getChildren().addAll(formHeader, formSub, new Separator(), inputBox, feedbackLabel, new Separator(), buttonRow);
        content.getChildren().addAll(heading, new Separator(), formCard);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }
}