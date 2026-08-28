package com.elctrovotesuperx.view.VoterView;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.elctrovotesuperx.model.AdminModel.Candidate;

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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyApplication {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN = "#10B981";
    private static final String BLUE = "#1464F4";
    private static final String RED = "#EF4444";
    private static final String ORANGE = "#F59E0B";

    /**
     * Backwards-compatible overload.
     */
    public static VBox createMyApplicationView(List<Map<String, String>> legacyRecords) {
        return createMyApplicationView();
    }

    /**
     * Dynamically generates the My Application view from live Firestore candidate records.
     */
    public static VBox createMyApplicationView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Header Section
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox heading = new VBox(6);
        Text title = new Text("My Candidate Filings 📄");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text(
                "Track the status of your candidate nominations, review administrative approvals, and inspect ballot certifications.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        subtitle.setWrappingWidth(700);
        heading.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button applyNewBtn = new Button("＋ Apply for Office");
        applyNewBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;");
        applyNewBtn.setOnAction(e -> VoterDashboard.showPage(ApplyCandidateView.createApplyCandidateView(null)));

        headerRow.getChildren().addAll(heading, spacer, applyNewBtn);

        // Applications Container
        VBox applicationsList = new VBox(16);

        // Loading indicator
        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(30));
        ProgressIndicator spinner = new ProgressIndicator();
        Label loadingLbl = new Label("Retrieving your candidate filings from Firebase...");
        loadingLbl.setStyle("-fx-text-fill: #4338CA; -fx-font-weight: bold; -fx-font-size: 13px;");
        loadingBox.getChildren().addAll(spinner, loadingLbl);
        applicationsList.getChildren().add(loadingBox);

        // Fetch asynchronously from Firestore
        Thread fetchThread = new Thread(() -> {
            try {
                String joinCode = SessionManager.joinCode;
                String idToken = SessionManager.idToken;
                String voterEmail = SessionManager.voterEmail != null ? SessionManager.voterEmail.trim().toLowerCase() : "";
                String voterName = SessionManager.voterName != null ? SessionManager.voterName.trim().toLowerCase() : "";

                List<Candidate> allCandidates = CandidateDAO.getCandidatesByOrg(joinCode, idToken);
                List<Candidate> myCandidates = new ArrayList<>();

                for (Candidate c : allCandidates) {
                    boolean emailMatch = c.getEmail() != null && !c.getEmail().isBlank() && c.getEmail().trim().equalsIgnoreCase(voterEmail);
                    boolean nameMatch = c.getName() != null && !c.getName().isBlank() && c.getName().trim().equalsIgnoreCase(voterName);

                    if (emailMatch || nameMatch || voterEmail.isEmpty()) {
                        myCandidates.add(c);
                    }
                }

                Platform.runLater(() -> {
                    applicationsList.getChildren().clear();
                    if (myCandidates.isEmpty()) {
                        VBox emptyCard = new VBox(14);
                        emptyCard.setAlignment(Pos.CENTER);
                        emptyCard.setPadding(new Insets(35));
                        emptyCard.setStyle(
                                "-fx-background-color: white;" +
                                        "-fx-background-radius: 14;" +
                                        "-fx-border-color: " + BORDER + ";" +
                                        "-fx-border-radius: 14;");

                        Label emptyIcon = new Label("🗳️");
                        emptyIcon.setFont(Font.font(38));

                        Text emptyText = new Text("No candidate nominations submitted yet.");
                        emptyText.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                        emptyText.setFill(Color.web(TEXT));

                        Text emptySub = new Text(
                                "Interested in representing your organization? Click 'Apply for Office' above to submit your candidacy application for active and upcoming elections.");
                        emptySub.setFill(Color.web(SECONDARY));
                        emptySub.setFont(Font.font(13));
                        emptySub.setWrappingWidth(550);
                        emptySub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                        Button applyNowBtn = new Button("Submit Candidate Nomination ➔");
                        applyNowBtn.setStyle(
                                "-fx-background-color: #2563EB;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 13px;" +
                                        "-fx-padding: 9 20;" +
                                        "-fx-background-radius: 8;" +
                                        "-fx-cursor: hand;");
                        applyNowBtn.setOnAction(e -> VoterDashboard.showPage(ApplyCandidateView.createApplyCandidateView(null)));

                        emptyCard.getChildren().addAll(emptyIcon, emptyText, emptySub, applyNowBtn);
                        applicationsList.getChildren().add(emptyCard);
                    } else {
                        for (Candidate c : myCandidates) {
                            applicationsList.getChildren().add(createCandidateCard(c));
                        }
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    applicationsList.getChildren().clear();
                    Label errLbl = new Label("Failed to load applications: " + ex.getMessage());
                    errLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    applicationsList.getChildren().add(errLbl);
                });
            }
        });
        fetchThread.setDaemon(true);
        fetchThread.start();

        content.getChildren().addAll(headerRow, new Separator(), applicationsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createCandidateCard(Candidate c) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));

        String status = c.getStatus() != null ? c.getStatus().toUpperCase() : "PENDING";
        String borderColor = "ACCEPTED".equals(status) ? "#10B981" : ("REJECTED".equals(status) ? "#EF4444" : "#3B82F6");

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web("ACCEPTED".equals(status) ? "#10B981" : ("REJECTED".equals(status) ? "#EF4444" : "#3B82F6")));
        String initial = c.getName() != null && !c.getName().isEmpty() ? c.getName().substring(0, 1).toUpperCase() : "C";
        Text avatarInitial = new Text(initial);
        avatarInitial.setFill(Color.WHITE);
        avatarInitial.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        StackPane avatarPane = new StackPane(avatarCircle, avatarInitial);

        VBox textBox = new VBox(3);
        textBox.setPadding(new Insets(0, 0, 0, 12));
        Text posName = new Text("Office: " + (c.getPosition() != null ? c.getPosition() : "Nominee"));
        posName.setFill(Color.web(TEXT));
        posName.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        String electionName = (c.getElectionTitle() != null && !c.getElectionTitle().isBlank())
                ? c.getElectionTitle()
                : (c.getElectionId() != null ? c.getElectionId() : "General Election");
        Text orgMeta = new Text("Election: " + electionName);
        orgMeta.setFill(Color.web(SECONDARY));
        orgMeta.setFont(Font.font("Arial", 12.5));
        textBox.getChildren().addAll(posName, orgMeta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status Badge
        Label statusBadge = new Label();
        if ("ACCEPTED".equals(status)) {
            statusBadge.setText("✔ Certified / Approved");
            statusBadge.setStyle(
                    "-fx-background-color: #ECFDF5;" +
                            "-fx-text-fill: #059669;" +
                            "-fx-font-size: 11.5px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 6 14;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #10B981;" +
                            "-fx-border-radius: 12;");
        } else if ("REJECTED".equals(status)) {
            statusBadge.setText("✕ Nomination Rejected");
            statusBadge.setStyle(
                    "-fx-background-color: #FEF2F2;" +
                            "-fx-text-fill: #DC2626;" +
                            "-fx-font-size: 11.5px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 6 14;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #EF4444;" +
                            "-fx-border-radius: 12;");
        } else {
            statusBadge.setText("⏳ Pending Administrative Review");
            statusBadge.setStyle(
                    "-fx-background-color: #EFF6FF;" +
                            "-fx-text-fill: #2563EB;" +
                            "-fx-font-size: 11.5px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 6 14;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #3B82F6;" +
                            "-fx-border-radius: 12;");
        }

        topRow.getChildren().addAll(avatarPane, textBox, spacer, statusBadge);

        // Details Row
        HBox detailsRow = new HBox(20);
        detailsRow.setAlignment(Pos.CENTER_LEFT);

        String appliedDateStr = formatEpoch(c.getAppliedAt());
        Text subDateText = new Text("📅 Applied Date: " + appliedDateStr);
        subDateText.setFill(Color.web(SECONDARY));
        subDateText.setFont(Font.font("Arial", 12));

        Text candidateEmailText = new Text("✉ Contact: " + (c.getEmail() != null ? c.getEmail() : "N/A"));
        candidateEmailText.setFill(Color.web(SECONDARY));
        candidateEmailText.setFont(Font.font("Arial", 12));

        detailsRow.getChildren().addAll(subDateText, candidateEmailText);

        VBox manifestoBox = new VBox(4);
        manifestoBox.setPadding(new Insets(8, 12, 8, 12));
        manifestoBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");
        Label manTitle = new Label("Candidate Statement / Manifesto:");
        manTitle.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        Label manContent = new Label(c.getBio() != null ? c.getBio() : "No statement submitted.");
        manContent.setWrapText(true);
        manContent.setStyle("-fx-font-size: 12px; -fx-text-fill: #1E293B;");
        manifestoBox.getChildren().addAll(manTitle, manContent);

        card.getChildren().addAll(topRow, new Separator(), detailsRow, manifestoBox);
        return card;
    }

    private static String formatEpoch(long epochMillis) {
        if (epochMillis <= 0) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        }
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        } catch (Exception e) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        }
    }
}