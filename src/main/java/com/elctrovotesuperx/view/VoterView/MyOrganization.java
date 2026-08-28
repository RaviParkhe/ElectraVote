package com.elctrovotesuperx.view.VoterView;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.elctrovotesuperx.model.AdminModel.ElectionData;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

public class MyOrganization {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN = "#10B981";
    private static final String PURPLE = "#7B4DFF";
    private static final String BLUE = "#1464F4";

    public static VBox createMyOrganizationView(List<Map<String, String>> legacyRecords) {
        return createMyOrganizationView();
    }

    /**
     * Dynamically generates the My Organizations view using live SessionManager and Firestore data.
     */
    public static VBox createMyOrganizationView() {
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
                "Manage your institutional memberships and inspect active organization details and elections.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        VBox orgsList = new VBox(16);

        String orgName = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                ? SessionManager.organizationName
                : "Registered Organization";
        String joinCode = SessionManager.joinCode != null ? SessionManager.joinCode : "N/A";
        String status = SessionManager.voterStatus != null ? SessionManager.voterStatus : "Verified Voter";

        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(20));
        loadingBox.getChildren().addAll(new ProgressIndicator(), new Label("Connecting to organization..."));
        orgsList.getChildren().add(loadingBox);

        Thread t = new Thread(() -> {
            int electionCount = 0;
            try {
                if (SessionManager.joinCode != null) {
                    List<ElectionData> elecs = ElectionDAO.getElectionsByOrg(SessionManager.joinCode, SessionManager.idToken);
                    electionCount = elecs.size();
                }
            } catch (Exception ignored) {}

            int finalCount = electionCount;
            Platform.runLater(() -> {
                orgsList.getChildren().clear();
                String details = "Join Code: " + joinCode + " • " + finalCount + " Active Election(s)";
                orgsList.getChildren().add(createOrgCard(orgName, "Institutional Member (" + status + ")", details, PURPLE, status, String.valueOf(finalCount)));
            });
        });
        t.setDaemon(true);
        t.start();

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
        Text avatarInitial = new Text(!orgName.isEmpty() ? orgName.substring(0, 1).toUpperCase() : "O");
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

        Label statusBadge = new Label("Active Context ✓");
        statusBadge.setStyle(
                "-fx-background-color: #ECFDF5;" +
                        "-fx-text-fill: #059669;" +
                        "-fx-font-size: 11.5px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 12;" +
                        "-fx-background-radius: 12;");

        topRow.getChildren().addAll(avatarPane, textBox, spacer, statusBadge);

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