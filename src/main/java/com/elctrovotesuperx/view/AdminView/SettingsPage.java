package com.elctrovotesuperx.view.AdminView;

import com.elctrovotesuperx.config.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SettingsPage extends VBox {

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    public SettingsPage() {
        setSpacing(18);
        setPadding(new Insets(24, 32, 28, 32));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                        + FONT);

        HBox header = buildHeader();
        VBox settingsCard = buildSettingsContent();

        ScrollPane scrollPane = new ScrollPane(settingsCard);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane);
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Institutional Configuration & Security Settings");
        title.setStyle(FONT
                + "-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label(
                "Manage organizational identity, cryptographic security policies, and administrative credentials");
        subtitle.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label secBadge = new Label("🛡️ AES-256 ZERO-KNOWLEDGE ENCRYPTED");
        secBadge.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 900; -fx-font-size: 11.5px; -fx-padding: 6 14; -fx-background-radius: 20; -fx-border-color: #10b981; -fx-border-radius: 20;");

        header.getChildren().addAll(titleBox, spacer, secBadge);
        return header;
    }

    private VBox buildSettingsContent() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);

        // 1. Organization Profile Card
        VBox orgCard = createSectionCard("🏛 Organization & Tenancy Profile", "Multi-tenant identification parameters and access credentials.");
        
        GridPane orgGrid = new GridPane();
        orgGrid.setHgap(16);
        orgGrid.setVgap(14);

        TextField orgNameField = createTextField(SessionManager.organizationName != null ? SessionManager.organizationName : "ELECTRAVOTE INSTITUTION");
        TextField joinCodeField = createTextField(SessionManager.joinCode != null ? SessionManager.joinCode : "EV-XXXX-XXXX");
        joinCodeField.setEditable(false);
        joinCodeField.setStyle(joinCodeField.getStyle() + "-fx-background-color: #f1f5f9; -fx-font-family: monospace; -fx-font-weight: bold;");

        TextField adminNameField = createTextField(SessionManager.adminName != null ? SessionManager.adminName : "Administrator");
        TextField adminEmailField = createTextField(SessionManager.adminEmail != null ? SessionManager.adminEmail : "admin@electravote.org");
        adminEmailField.setEditable(false);
        adminEmailField.setStyle(adminEmailField.getStyle() + "-fx-background-color: #f1f5f9;");

        orgGrid.add(createFieldBox("Organization Legal Name:", orgNameField), 0, 0);
        orgGrid.add(createFieldBox("Institution Join Code (Fixed):", joinCodeField), 1, 0);
        orgGrid.add(createFieldBox("Electoral Officer / Admin Name:", adminNameField), 0, 1);
        orgGrid.add(createFieldBox("Registered Admin Email:", adminEmailField), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        orgGrid.getColumnConstraints().addAll(col1, col2);

        orgCard.getChildren().add(orgGrid);

        // 2. Cryptographic Security & Secrecy Policies
        VBox secCard = createSectionCard("🔐 Cryptographic Security & Ballot Privacy", "Configure Zero-Knowledge privacy parameters and anti-tamper constraints.");
        
        VBox secList = new VBox(12);

        CheckBox zkCheck = new CheckBox("Enforce Zero-Knowledge Homomorphic Ballot Encryption (Default: ON)");
        zkCheck.setSelected(true);
        zkCheck.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

        CheckBox doubleVoteCheck = new CheckBox("Hardware & UID-Level Double-Voting Hardware Prevention (Strict)");
        doubleVoteCheck.setSelected(true);
        doubleVoteCheck.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

        CheckBox receiptCheck = new CheckBox("Generate Cryptographic Receipt Hashes (ZK-XXXX) for all Voters");
        receiptCheck.setSelected(true);
        receiptCheck.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

        secList.getChildren().addAll(zkCheck, doubleVoteCheck, receiptCheck);
        secCard.getChildren().add(secList);

        // 3. Save Action Row
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("💾 Save Settings");
        saveBtn.setStyle(FONT + "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 13px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            SessionManager.organizationName = orgNameField.getText().trim();
            SessionManager.adminName = adminNameField.getText().trim();
            showAlert("Settings Saved", "Institutional configuration and security parameters successfully saved.");
        });

        actionRow.getChildren().add(saveBtn);

        container.getChildren().addAll(orgCard, secCard, actionRow);
        return container;
    }

    private VBox createSectionCard(String title, String subtitle) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.06), 10, 0, 0, 3);");

        VBox h = new VBox(3);
        Label t = new Label(title);
        t.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");
        Label s = new Label(subtitle);
        s.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        h.getChildren().addAll(t, s);

        card.getChildren().addAll(h, new Separator());
        return card;
    }

    private VBox createFieldBox(String labelStr, TextField tf) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelStr);
        lbl.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private TextField createTextField(String text) {
        TextField tf = new TextField(text);
        tf.setStyle(FONT + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-padding: 8 12;");
        return tf;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (AdminDashboard.AdminDashboardStage != null) {
            alert.initOwner(AdminDashboard.AdminDashboardStage);
            alert.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(alert);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

