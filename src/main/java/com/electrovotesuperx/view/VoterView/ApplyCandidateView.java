package com.electrovotesuperx.view.VoterView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.electrovotesuperx.model.AdminModel.ElectionData;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApplyCandidateView {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String GREEN = "#10B981";
    private static final String RED = "#EF4444";

    public static VBox createApplyCandidateView(String preselectedElectionTitle) {
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(25));
        formContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(formContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header
        VBox heading = new VBox(6);
        Text title = new Text("Apply for Candidacy ✍️");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text(
                "Submit your official candidate nomination and manifesto to the organization electoral committee for review and certification.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(900);
        heading.getChildren().addAll(title, subtitle);

        // Security check: Only ACCEPTED or VERIFIED voters are authorized to apply as candidate
        boolean isApproved = "ACCEPTED".equalsIgnoreCase(SessionManager.voterStatus) || "VERIFIED".equalsIgnoreCase(SessionManager.voterStatus);
        if (!isApproved) {
            VBox pendingCard = new VBox(14);
            pendingCard.setAlignment(Pos.CENTER);
            pendingCard.setPadding(new Insets(35));
            pendingCard.setStyle("-fx-background-color: #FFFBEB; -fx-background-radius: 12; -fx-border-color: #F59E0B; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.12), 8, 0, 0, 3);");

            Label warnIcon = new Label("⏳");
            warnIcon.setFont(Font.font(36));

            Text warnTitle = new Text("Candidate Application Locked: Approval Required");
            warnTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            warnTitle.setFill(Color.web("#92400E"));

            String currentOrgName = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                    ? SessionManager.organizationName : (SessionManager.joinCode != null ? SessionManager.joinCode : "your organization");
            String currentCode = SessionManager.joinCode != null ? SessionManager.joinCode : "";

            Text warnMsg = new Text(
                    "Your membership in '" + currentOrgName + "' (" + currentCode + ") is currently PENDING approval.\n\n" +
                    "Your organization's Administrator must review and ACCEPT your voter registration before you can submit candidate nominations or manifestos.");
            warnMsg.setFill(Color.web("#78350F"));
            warnMsg.setFont(Font.font(13));
            warnMsg.setWrappingWidth(680);
            warnMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Button myOrgBtn = new Button("🏢 Manage Memberships & Track Status in 'My Organization'");
            myOrgBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
            myOrgBtn.setOnAction(ev -> VoterDashboard.showPage(MyOrganization.createMyOrganizationView()));

            pendingCard.getChildren().addAll(warnIcon, warnTitle, warnMsg, myOrgBtn);
            formContainer.getChildren().addAll(heading, new Separator(), pendingCard);

            VBox wrapper = new VBox(scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            return wrapper;
        }

        // Form Card
        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(28));
        formCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #818CF8;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 14;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(99, 102, 241, 0.08), 12, 0, 0, 4);");

        // Locked Election Alert Banner (initially hidden or shown when active election is selected)
        VBox lockedElectionBanner = new VBox(8);
        lockedElectionBanner.setPadding(new Insets(14, 18, 14, 18));
        lockedElectionBanner.setStyle(
                "-fx-background-color: #FEF2F2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #EF4444;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 10;"
        );
        lockedElectionBanner.setVisible(false);
        lockedElectionBanner.setManaged(false);

        Label lockedTitle = new Label("🔒 Candidate Nominations Closed for This Election");
        lockedTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #991B1B; -fx-font-size: 14px;");

        Label lockedDesc = new Label(
                "Voting is currently OPEN (or completed) for this election. Under ElectraVote governance rules, " +
                "candidate rosters are strictly frozen once voting commences to prevent mid-election ballot manipulation. " +
                "New candidate nominations can only be submitted during the Draft / Planning phase before voting opens.");
        lockedDesc.setStyle("-fx-text-fill: #B91C1C; -fx-font-size: 12px;");
        lockedDesc.setWrapText(true);
        lockedElectionBanner.getChildren().addAll(lockedTitle, lockedDesc);

        // 1. Election Selection Field
        VBox electionBox = new VBox(6);
        Label electionLabel = new Label("Target Election *");
        electionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 13px;");

        ComboBox<String> electionDropdown = new ComboBox<>();
        electionDropdown.setPromptText("Loading organization elections...");
        electionDropdown.setMaxWidth(Double.MAX_VALUE);
        electionDropdown.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-font-size: 13px;");

        electionBox.getChildren().addAll(electionLabel, electionDropdown);

        // 2. Position Field
        VBox posBox = new VBox(6);
        Label posLabel = new Label("Contested Office / Position *");
        posLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 13px;");

        ComboBox<String> positionDropdown = new ComboBox<>();
        positionDropdown.setPromptText("Select or enter position (e.g. President, Secretary)");
        positionDropdown.setEditable(true);
        positionDropdown.setMaxWidth(Double.MAX_VALUE);
        positionDropdown.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-font-size: 13px;");

        posBox.getChildren().addAll(posLabel, positionDropdown);

        // 3. Name Field
        VBox nameBox = createFormField("Full Candidate Name *", "Enter your legal registered name");
        TextField nameField = (TextField) nameBox.getChildren().get(1);
        if (SessionManager.voterName != null && !SessionManager.voterName.isBlank()) {
            nameField.setText(SessionManager.voterName);
        }

        // 4. Mobile Field
        VBox mobileBox = createFormField("Contact Mobile Number *", "Enter your active phone number");
        TextField mobileField = (TextField) mobileBox.getChildren().get(1);
        if (SessionManager.voterPhone != null && !SessionManager.voterPhone.isBlank()) {
            mobileField.setText(SessionManager.voterPhone);
        }

        // 5. Email Field
        VBox emailBox = createFormField("Institutional Email Address *", "Enter your verified email address");
        TextField emailField = (TextField) emailBox.getChildren().get(1);
        if (SessionManager.voterEmail != null && !SessionManager.voterEmail.isBlank()) {
            emailField.setText(SessionManager.voterEmail);
        }

        // 6. Manifesto Statement
        VBox descFieldBox = new VBox(6);
        Label descLabel = new Label("Candidate Statement & Manifesto *");
        descLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 13px;");

        TextArea descArea = new TextArea();
        descArea.setPromptText(
                "Present your qualifications, vision, and core campaign promises for eligible voters to review...");
        descArea.setPrefRowCount(4);
        descArea.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;");
        descFieldBox.getChildren().addAll(descLabel, descArea);

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        statusMsg.setVisible(false);

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #F1F5F9;" +
                        "-fx-text-fill: #334155;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;");
        cancelBtn.setOnAction(e -> VoterDashboard.returnHomeFromVoting());

        Button submitBtn = new Button("Submit Nomination Application 🚀");
        submitBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #10B981, #059669);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 24;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;");

        // Helper to check if an election is active/live/open/closed/started (locked for nominations)
        java.util.function.Predicate<ElectionData> isElectionLocked = el -> {
            if (el == null) return false;
            String st = el.getStatus() != null ? el.getStatus().trim() : "Draft";
            if ("Active".equalsIgnoreCase(st) || "Live".equalsIgnoreCase(st) ||
                "Open".equalsIgnoreCase(st) || "OPEN".equalsIgnoreCase(st) ||
                "Closed".equalsIgnoreCase(st) || "CLOSED".equalsIgnoreCase(st) ||
                "Ended".equalsIgnoreCase(st)) {
                return true;
            }

            // Chronological Check: If current date/time >= startDateTime, nominations are CLOSED!
            String startStr = el.getStartDateTime();
            if (startStr != null && !startStr.isBlank()) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", java.util.Locale.ENGLISH);
                    java.time.LocalDateTime startTime = java.time.LocalDateTime.parse(startStr.trim(), formatter);
                    if (java.time.LocalDateTime.now().isAfter(startTime) || java.time.LocalDateTime.now().isEqual(startTime)) {
                        return true;
                    }
                } catch (Exception ignored) {}
            }

            String endStr = el.getEndDateTime();
            if (endStr != null && !endStr.isBlank()) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", java.util.Locale.ENGLISH);
                    java.time.LocalDateTime endTime = java.time.LocalDateTime.parse(endStr.trim(), formatter);
                    if (java.time.LocalDateTime.now().isAfter(endTime) || java.time.LocalDateTime.now().isEqual(endTime)) {
                        return true;
                    }
                } catch (Exception ignored) {}
            }

            return false;
        };

        // Load elections from Firestore asynchronously
        List<ElectionData> loadedElections = new ArrayList<>();
        Thread loadThread = new Thread(() -> {
            try {
                String joinCode = SessionManager.joinCode;
                String idToken = SessionManager.idToken;
                if (joinCode != null && !joinCode.isBlank()) {
                    List<ElectionData> elecs = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                    Platform.runLater(() -> {
                        loadedElections.clear();
                        loadedElections.addAll(elecs);
                        electionDropdown.getItems().clear();

                        if (elecs.isEmpty()) {
                            electionDropdown.setPromptText("No elections found in organization");
                        } else {
                            for (ElectionData e : elecs) {
                                String t = e.getTitle() != null ? e.getTitle() : "Untitled Election";
                                String st = e.getStatus() != null ? e.getStatus().trim() : "Draft";
                                if (isElectionLocked.test(e)) {
                                    electionDropdown.getItems().add(t + "  [🔒 ELECTION STARTED / ACTIVE - NOMINATIONS CLOSED]");
                                } else {
                                    electionDropdown.getItems().add(t + "  [✅ DRAFT - OPEN FOR NOMINATIONS]");
                                }
                            }

                            // Match preselected title or select first
                            if (preselectedElectionTitle != null) {
                                for (String item : electionDropdown.getItems()) {
                                    if (item.startsWith(preselectedElectionTitle)) {
                                        electionDropdown.setValue(item);
                                        break;
                                    }
                                }
                            }
                            if (electionDropdown.getValue() == null && !electionDropdown.getItems().isEmpty()) {
                                electionDropdown.getSelectionModel().selectFirst();
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();

        // Update positions & locked state whenever an election is selected
        electionDropdown.setOnAction(e -> {
            String selItem = electionDropdown.getValue();
            if (selItem != null) {
                ElectionData match = loadedElections.stream()
                        .filter(el -> selItem.startsWith(el.getTitle()))
                        .findFirst()
                        .orElse(null);

                boolean locked = isElectionLocked.test(match);

                if (locked) {
                    lockedElectionBanner.setVisible(true);
                    lockedElectionBanner.setManaged(true);
                    submitBtn.setDisable(true);
                    submitBtn.setStyle("-fx-background-color: #94A3B8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8;");
                    statusMsg.setText("🔒 Candidate nominations are locked because this election is currently OPEN for voting (or closed).");
                    statusMsg.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                    statusMsg.setVisible(true);
                } else {
                    lockedElectionBanner.setVisible(false);
                    lockedElectionBanner.setManaged(false);
                    submitBtn.setDisable(false);
                    submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #10B981, #059669); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
                    statusMsg.setVisible(false);
                }

                positionDropdown.getItems().clear();
                if (match != null && match.getPositions() != null && !match.getPositions().isEmpty()) {
                    positionDropdown.getItems().addAll(match.getPositions());
                    positionDropdown.getSelectionModel().selectFirst();
                } else {
                    positionDropdown.getItems().addAll("President", "Vice President", "Secretary", "Treasurer",
                            "Representative");
                    positionDropdown.getSelectionModel().selectFirst();
                }
            }
        });

        submitBtn.setOnAction(e -> {
            String selectedDropdownItem = electionDropdown.getValue();
            if (selectedDropdownItem == null) {
                statusMsg.setText("⚠️ Please select an election.");
                statusMsg.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                statusMsg.setVisible(true);
                return;
            }

            ElectionData match = loadedElections.stream()
                    .filter(el -> selectedDropdownItem.startsWith(el.getTitle()))
                    .findFirst()
                    .orElse(null);

            // Strict Edge-Case Guard: Block any nomination if election is Open/Active/Live/Closed
            if (isElectionLocked.test(match)) {
                statusMsg.setText("❌ Action Rejected: This election is OPEN for voting. Candidates cannot apply to open elections.");
                statusMsg.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                statusMsg.setVisible(true);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nominations Closed");
                alert.setHeaderText("Voting is Currently Active");
                alert.setContentText("Nominations cannot be accepted for an active/open election. Candidates can only be nominated while an election is in Draft status.");
                alert.showAndWait();
                return;
            }

            String selectedElection = match != null ? match.getTitle() : selectedDropdownItem;
            String selectedPos = positionDropdown.getValue() != null ? positionDropdown.getValue().toString().trim() : "";
            String name = nameField.getText().trim();
            String mobile = mobileField.getText().trim();
            String email = emailField.getText().trim();
            String manifesto = descArea.getText().trim();

            if (selectedPos.isEmpty() || name.isEmpty() || mobile.isEmpty() || email.isEmpty() || manifesto.isEmpty()) {
                statusMsg.setText("⚠️ Please fill in all required nomination fields.");
                statusMsg.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                statusMsg.setVisible(true);
                return;
            }

            submitBtn.setDisable(true);
            submitBtn.setText("Submitting nomination...");
            statusMsg.setText("Submitting nomination application...");
            statusMsg.setStyle("-fx-text-fill: " + BLUE + "; -fx-font-weight: bold;");
            statusMsg.setVisible(true);

            String candidateId = UUID.randomUUID().toString();
            String joinCode = SessionManager.joinCode != null ? SessionManager.joinCode : "DEMO_ORG";
            String idToken = SessionManager.idToken != null ? SessionManager.idToken : "";
            String electionId = match != null ? match.getId() : selectedElection;

            Candidate candidate = new Candidate(
                    candidateId,
                    electionId,
                    selectedElection,
                    selectedPos,
                    name,
                    email,
                    mobile,
                    manifesto,
                    joinCode);

            Thread submitThread = new Thread(() -> {
                try {
                    boolean ok = CandidateDAO.saveCandidate(candidate, idToken);
                    Platform.runLater(() -> {
                        submitBtn.setDisable(false);
                        submitBtn.setText("Submit Nomination Application 🚀");
                        if (ok) {
                            showApplicationSuccessScreen(selectedElection, selectedPos, name, email, manifesto);
                        } else {
                            statusMsg.setText("✕ Submission failed. Please verify your connection.");
                            statusMsg.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        submitBtn.setDisable(false);
                        submitBtn.setText("Submit Nomination Application 🚀");
                        statusMsg.setText("✕ Error submitting: " + ex.getMessage());
                        statusMsg.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                    });
                }
            });
            submitThread.setDaemon(true);
            submitThread.start();
        });

        buttonRow.getChildren().addAll(cancelBtn, submitBtn);

        formCard.getChildren().addAll(
                lockedElectionBanner, electionBox, posBox, nameBox, mobileBox, emailBox, descFieldBox, statusMsg, new Separator(), buttonRow);

        formContainer.getChildren().addAll(heading, new Separator(), formCard);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static void showApplicationSuccessScreen(String election, String position, String name, String email,
            String manifesto) {
        VBox successBox = new VBox(18);
        successBox.setAlignment(Pos.CENTER);
        successBox.setPadding(new Insets(40));
        successBox.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(32));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #10B981;" +
                        "-fx-border-width: 1.8;" +
                        "-fx-border-radius: 14;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(16, 185, 129, 0.12), 16, 0, 0, 5);");

        Label icon = new Label("🎉");
        icon.setFont(Font.font(46));

        Text title = new Text("Nomination Submitted Successfully!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setFill(Color.web("#064E3B"));

        Text desc = new Text("Your candidate application for " + position + " (" + election
                + ") has been submitted successfully and is pending review by the election administrator.");
        desc.setFont(Font.font(13));
        desc.setFill(Color.web(SECONDARY));
        desc.setWrappingWidth(480);
        desc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER);

        Button viewMyApps = new Button("View My Applications 📄");
        viewMyApps.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;");
        viewMyApps.setOnAction(e -> VoterDashboard.showPage(MyApplication.createMyApplicationView()));

        Button homeBtn = new Button("Return to Home ⌂");
        homeBtn.setStyle(
                "-fx-background-color: #F1F5F9;" +
                        "-fx-text-fill: #334155;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;");
        homeBtn.setOnAction(e -> VoterDashboard.returnHomeFromVoting());

        btnRow.getChildren().addAll(homeBtn, viewMyApps);

        card.getChildren().addAll(icon, title, desc, new Separator(), btnRow);
        successBox.getChildren().add(card);

        VoterDashboard.showPage(successBox);
    }

    private static VBox createFormField(String labelText, String promptText) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 13px;");

        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;" +
                        "-fx-font-size: 13px;");
        box.getChildren().addAll(label, textField);
        return box;
    }
}
