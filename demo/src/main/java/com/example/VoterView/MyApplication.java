package com.example.VoterView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

public class MyApplication {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN = "#10B981";
    private static final String BLUE = "#1464F4";

    /**
     * Dynamically generates the My Application view for approved candidate applications.
     * @param approvedApplicationRecords List of maps containing application details fetched from DAO/Controller.
     */
    public static VBox createMyApplicationView(List<Map<String, String>> approvedApplicationRecords) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header Section
        VBox heading = new VBox(6);
        Text title = new Text("My Approved Applications 📄");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text("Here you can view candidate applications that have been officially reviewed and approved by administrators.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        // Applications Container
        VBox applicationsList = new VBox(16);

        // Populate dynamically from DAO/Controller data
        if (approvedApplicationRecords != null && !approvedApplicationRecords.isEmpty()) {
            for (Map<String, String> app : approvedApplicationRecords) {
                String orgName = app.getOrDefault("orgName", "Organization");
                String position = app.getOrDefault("position", "Candidate Position");
                String submittedDate = app.getOrDefault("submittedDate", "N/A");
                String approvalDate = app.getOrDefault("approvalDate", "N/A");
                String themeColor = app.getOrDefault("themeColor", BLUE);

                applicationsList.getChildren().add(createApplicationCard(orgName, position, submittedDate, approvalDate, themeColor));
            }
        } else {
            // Fallback view if no approved applications exist yet
            VBox emptyCard = new VBox(10);
            emptyCard.setPadding(new Insets(25));
            emptyCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
            );
            Text emptyText = new Text("No approved candidate applications found. Applications appear here once approved by an administrator.");
            emptyText.setFill(Color.web(SECONDARY));
            emptyText.setFont(Font.font(13));
            emptyCard.getChildren().add(emptyText);
            applicationsList.getChildren().add(emptyCard);
        }

        content.getChildren().addAll(heading, new Separator(), applicationsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createApplicationCard(String orgName, String position, String submittedDate, String approvalDate, String themeColor) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);"
        );

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web(themeColor));
        Text avatarInitial = new Text(!orgName.isEmpty() ? orgName.substring(0, 1) : "A");
        avatarInitial.setFill(Color.WHITE);
        avatarInitial.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        StackPane avatarPane = new StackPane(avatarCircle, avatarInitial);

        VBox textBox = new VBox(3);
        textBox.setPadding(new Insets(0, 0, 0, 12));
        Text posName = new Text("Position: " + position);
        posName.setFill(Color.web(TEXT));
        posName.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        Text orgMeta = new Text(orgName);
        orgMeta.setFill(Color.web(SECONDARY));
        orgMeta.setFont(Font.font("Arial", 12.5));
        textBox.getChildren().addAll(posName, orgMeta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status Badge (Admin Approved)
        Label statusBadge = new Label("✔ Admin Approved");
        statusBadge.setStyle(
            "-fx-background-color: #ECFDF5;" +
            "-fx-text-fill: #059669;" +
            "-fx-font-size: 11.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 14;" +
            "-fx-background-radius: 12;"
        );

        topRow.getChildren().addAll(avatarPane, textBox, spacer, statusBadge);

        // Details Row
        HBox detailsRow = new HBox(20);
        detailsRow.setAlignment(Pos.CENTER_LEFT);

        Text subDateText = new Text("📅 Submitted: " + submittedDate);
        subDateText.setFill(Color.web(SECONDARY));
        subDateText.setFont(Font.font("Arial", 12));

        Text appDateText = new Text("🛡️ Approved: " + approvalDate);
        appDateText.setFill(Color.web(GREEN));
        appDateText.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        detailsRow.getChildren().addAll(subDateText, appDateText);

        card.getChildren().addAll(topRow, new Separator(), detailsRow);
        return card;
    }
}