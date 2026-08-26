package com.elctrovotesuperx.view.VoterView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
import java.util.Map; // Import for data structures coming from your DAO/Controller

public class MyOrganization {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN = "#10B981";
    private static final String PURPLE = "#7B4DFF";
    private static final String BLUE = "#1464F4";

    /**
     * Dynamically generates the My Organizations view using data fetched from your
     * Controller / DAO.
     * 
     * @param organizationRecords List of maps or custom Organization model objects
     *                            containing database data.
     */
    public static VBox createMyOrganizationView(List<Map<String, String>> organizationRecords) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox heading = new VBox(6);
        Text title = new Text("My Organizations 🏢");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text(
                "Manage your institutional memberships and switch your active voter portal context between organizations.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        VBox orgsList = new VBox(16);

        // Dynamically loop through records provided by the Controller / DAO
        if (organizationRecords != null && !organizationRecords.isEmpty()) {
            for (Map<String, String> org : organizationRecords) {
                String orgName = org.getOrDefault("orgName", "Unknown Organization");
                String roleMeta = org.getOrDefault("roleMeta", "Member");
                String details = org.getOrDefault("details", "No details available");
                String themeColor = org.getOrDefault("themeColor", PURPLE);
                String status = org.getOrDefault("status", "Verified");
                String electionsCount = org.getOrDefault("electionsCount", "0");

                orgsList.getChildren()
                        .add(createOrgCard(orgName, roleMeta, details, themeColor, status, electionsCount));
            }
        } else {
            // Fallback view if no records are found
            Text emptyText = new Text("No organizations found. Join an organization using a join code.");
            emptyText.setFill(Color.web(SECONDARY));
            emptyText.setFont(Font.font(13));
            orgsList.getChildren().add(emptyText);
        }

        content.getChildren().addAll(heading, new Separator(), orgsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createOrgCard(String orgName, String roleMeta, String details, String themeColor, String status,
            String electionsCount) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web(themeColor));
        Text avatarInitial = new Text(!orgName.isEmpty() ? orgName.substring(0, 1) : "O");
        avatarInitial.setFill(Color.WHITE);
        avatarInitial.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        StackPane avatarPane = new StackPane(avatarCircle, avatarInitial);

        VBox textBox = new VBox(3);
        textBox.setPadding(new Insets(0, 0, 0, 12));
        Text name = new Text(orgName);
        name.setFill(Color.web(TEXT));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        Text role = new Text(roleMeta);
        role.setFill(Color.web(SECONDARY));
        role.setFont(Font.font("Arial", 12.5));
        textBox.getChildren().addAll(name, role);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button switchOrgBtn = new Button("Switch to Organization 🔄");
        switchOrgBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-background-radius: 7;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 12px;");

        // Triggers context switch and updates dashboard metrics dynamically
        switchOrgBtn.setOnAction(e -> {
            VoterDashboard.updateActiveOrganization(orgName, status, electionsCount);
        });

        HBox actionBox = new HBox(10, switchOrgBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        topRow.getChildren().addAll(avatarPane, textBox, spacer, actionBox);

        HBox detailsRow = new HBox(10);
        detailsRow.setAlignment(Pos.CENTER_LEFT);

        Text detailText = new Text("📌 " + details);
        detailText.setFill(Color.web(GREEN));
        detailText.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        detailsRow.getChildren().add(detailText);

        card.getChildren().addAll(topRow, new Separator(), detailsRow);
        return card;
    }
}