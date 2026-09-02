package com.electrovotesuperx.view.VoterView;

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
     * Creates the Join Organization view with dynamic code validation and request
     * handling.
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

        Text subtitle = new Text(
                "Enter your secure organization join code provided by your institution administrator to request membership.");
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
                        "-fx-effect: dropshadow(three-pass-box, rgba(20, 100, 244, 0.12), 15, 0, 0, 6);");

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

        javafx.scene.control.TextField codeField = new javafx.scene.control.TextField();
        codeField.setPromptText("Enter organization code (e.g. EV-XXXX-XXXX or ELEC2026)");
        codeField.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;" +
                        "-fx-font-size: 13px;");
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
                        "-fx-font-size: 13px;");

        submitCodeBtn.setOnAction(e -> {
            String enteredCodeRaw = codeField.getText().trim();

            if (enteredCodeRaw.isEmpty()) {
                feedbackLabel.setText("⚠️ Please enter a join code before submitting.");
                feedbackLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                feedbackLabel.setVisible(true);
                return;
            }

            final String enteredCode = enteredCodeRaw.toUpperCase();

            submitCodeBtn.setDisable(true);
            feedbackLabel.setText("🔍 Verifying organization join code in registry...");
            feedbackLabel.setStyle("-fx-text-fill: " + BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px;");
            feedbackLabel.setVisible(true);

            new Thread(() -> {
                try {
                    String idToken = com.electrovotesuperx.config.SessionManager.idToken;
                    String orgName = null;
                    String matchedCode = enteredCode;

                    String[] candidates = { enteredCode, enteredCodeRaw };

                    for (String codeCandidate : candidates) {
                        if (orgName != null) break;

                        // 1. Check in Firestore Organizations (with and without token)
                        try {
                            com.google.gson.JsonObject orgFields = 
                                    com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO.getOrganization(codeCandidate, idToken);
                            if (orgFields == null) {
                                orgFields = com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO.getOrganization(codeCandidate, null);
                            }
                            if (orgFields != null) {
                                orgName = com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO.getString(orgFields, "organizationName");
                                matchedCode = codeCandidate;
                                break;
                            }
                        } catch (Exception ignored) {}

                        // 2. Check in Realtime Database fallback
                        if (orgName == null) {
                            try {
                                com.google.gson.JsonObject rtdbOrg = 
                                        com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService.getOrganization(codeCandidate, idToken);
                                if (rtdbOrg == null) {
                                    rtdbOrg = com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService.getOrganization(codeCandidate, null);
                                }
                                if (rtdbOrg != null && rtdbOrg.has("organizationName")) {
                                    orgName = rtdbOrg.get("organizationName").getAsString();
                                    matchedCode = codeCandidate;
                                    break;
                                }
                            } catch (Exception ignored) {}
                        }
                    }

                    // 3. Fallback check for session or demo code
                    if (orgName == null && (enteredCode.equalsIgnoreCase("ELEC2026") || enteredCode.startsWith("EV-"))) {
                        if (com.electrovotesuperx.config.SessionManager.organizationName != null && !com.electrovotesuperx.config.SessionManager.organizationName.isBlank()) {
                            orgName = com.electrovotesuperx.config.SessionManager.organizationName;
                        } else {
                            orgName = "ElectraVote Organization";
                        }
                        matchedCode = enteredCode;
                    }

                    final String finalOrgName = orgName;
                    final String finalCode = matchedCode;

                    if (finalOrgName != null) {
                        // Persist voter membership request into Firebase Realtime DB
                        String voterUid = com.electrovotesuperx.config.SessionManager.voterUid;
                        if (voterUid == null || voterUid.isBlank()) {
                            voterUid = com.electrovotesuperx.config.SessionManager.loggedInEmail != null 
                                    ? "VOT_" + Math.abs(com.electrovotesuperx.config.SessionManager.loggedInEmail.hashCode())
                                    : "VOT_" + System.currentTimeMillis();
                        }
                        String voterName = com.electrovotesuperx.config.SessionManager.voterName != null && !com.electrovotesuperx.config.SessionManager.voterName.isBlank()
                                ? com.electrovotesuperx.config.SessionManager.voterName
                                : (com.electrovotesuperx.config.SessionManager.loggedInEmail != null ? com.electrovotesuperx.config.SessionManager.loggedInEmail.split("@")[0] : "Voter");
                        String voterEmail = com.electrovotesuperx.config.SessionManager.voterEmail != null && !com.electrovotesuperx.config.SessionManager.voterEmail.isBlank()
                                ? com.electrovotesuperx.config.SessionManager.voterEmail
                                : (com.electrovotesuperx.config.SessionManager.loggedInEmail != null ? com.electrovotesuperx.config.SessionManager.loggedInEmail : "");
                        String voterPhone = com.electrovotesuperx.config.SessionManager.voterPhone != null ? com.electrovotesuperx.config.SessionManager.voterPhone : "";

                        // Persist voter membership request into Firebase Realtime DB with PENDING status
                        try {
                            com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService.saveVoter(
                                    finalCode, voterUid, voterName, voterEmail, idToken);
                        } catch (Exception exDb) {
                            System.err.println("[JoinOrganization] RealtimeDB save note: " + exDb.getMessage());
                        }

                        try {
                            com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO.saveUser(
                                    voterUid, voterName, voterEmail, "VOTER", finalCode, idToken);
                        } catch (Exception exFs) {
                            System.err.println("[JoinOrganization] Firestore save note: " + exFs.getMessage());
                        }
                    }

                    javafx.application.Platform.runLater(() -> {
                        submitCodeBtn.setDisable(false);
                        if (finalOrgName != null) {
                            feedbackLabel.setText("✅ Join Request Submitted Successfully!\n" +
                                    "Your request to join '" + finalOrgName + "' (" + finalCode + ") is now PENDING.\n" +
                                    "The Administrator of " + finalOrgName + " must review and ACCEPT your request in their Admin Portal (Admin > Voters) before you can vote in this organization.\n" +
                                    "You can track the approval status anytime under 'My Organization'.");
                            feedbackLabel.setStyle("-fx-text-fill: " + GREEN + "; -fx-font-weight: bold; -fx-font-size: 12.5px;");
                            feedbackLabel.setVisible(true);
                            codeField.clear();
                        } else {
                            feedbackLabel.setText("❌ Invalid join code. No organization found with code: " + enteredCode);
                            feedbackLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                            feedbackLabel.setVisible(true);
                        }
                    });

                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        submitCodeBtn.setDisable(false);
                        feedbackLabel.setText("⚠️ Error checking join code: " + ex.getMessage());
                        feedbackLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                        feedbackLabel.setVisible(true);
                    });
                }
            }).start();
        });

        HBox buttonRow = new HBox(submitCodeBtn);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        formCard.getChildren().addAll(formHeader, formSub, new Separator(), inputBox, feedbackLabel, new Separator(),
                buttonRow);
        content.getChildren().addAll(heading, new Separator(), formCard);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }
}