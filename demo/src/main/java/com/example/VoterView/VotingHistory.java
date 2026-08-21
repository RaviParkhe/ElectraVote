package com.example.VoterView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class VotingHistory {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String GREEN = "#10B981";

    /**
     * Creates the Voting History view showing records of previously cast ballots.
     */
    public static VBox createVotingHistoryView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header Section
        VBox heading = new VBox(6);
        Text title = new Text("Voting History & Audit Log 🕒");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text("Review your immutable record of previously cast ballots, verified securely via zero-knowledge cryptographic proofs.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        // History Records Container
        VBox historyList = new VBox(16);

        // Mock Historical Records (Ready to be fetched dynamically via DAO/Controller)
        historyList.getChildren().add(createHistoryCard(
            "ABC College - Student Union Election 2025",
            "Cast Ballot Receipt #ZK-9821-AZ",
            "March 14, 2025 at 02:45 PM",
            "Status: Verified & Counted Successfully"
        ));

        historyList.getChildren().add(createHistoryCard(
            "Computer Science Faculty Board Poll",
            "Cast Ballot Receipt #ZK-4432-CS",
            "November 10, 2024 at 11:15 AM",
            "Status: Verified & Counted Successfully"
        ));

        historyList.getChildren().add(createHistoryCard(
            "Campus Cultural Fest Board",
            "Cast Ballot Receipt #ZK-1109-CF",
            "August 22, 2024 at 04:30 PM",
            "Status: Verified & Counted Successfully"
        ));

        content.getChildren().addAll(heading, new Separator(), historyList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createHistoryCard(String orgName, String receipt, String dateTime, String status) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);"
        );

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(4);
        Text name = new Text(orgName);
        name.setFill(Color.web(TEXT));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        Text recText = new Text(receipt);
        recText.setFill(Color.web(BLUE));
        recText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        textBox.getChildren().addAll(name, recText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(status);
        statusBadge.setStyle(
            "-fx-background-color: #ECFDF5;" +
            "-fx-text-fill: #059669;" +
            "-fx-font-size: 11.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 12;" +
            "-fx-background-radius: 12;"
        );

        topRow.getChildren().addAll(textBox, spacer, statusBadge);

        // Date and Time Row
        HBox detailsRow = new HBox(10);
        detailsRow.setAlignment(Pos.CENTER_LEFT);

        Text dateLabel = new Text("📅 Cast On: " + dateTime);
        dateLabel.setFill(Color.web(SECONDARY));
        dateLabel.setFont(Font.font("Arial", 12.5));

        detailsRow.getChildren().add(dateLabel);

        card.getChildren().addAll(topRow, new Separator(), detailsRow);
        return card;
    }
}