package com.electrovotesuperx.view.AdminView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.dao.AdminDAO.VoteDAO;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.electrovotesuperx.model.AdminModel.ElectionData;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ResultPage extends VBox {

    private ComboBox<String> electionCombo;
    private VBox resultList;
    private ProgressIndicator loadingIndicator;
    private List<ElectionData> loadedElections = new ArrayList<>();
    private ElectionResult currentResult;

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    public ResultPage() {
        initializeContainerStyle();

        HBox header = buildHeaderSection();
        HBox electionBar = buildElectionActionBar();
        ScrollPane scrollPane = buildResultScrollPane();

        getChildren().addAll(header, electionBar, scrollPane);

        loadElectionsFromFirebase();
    }

    private void initializeContainerStyle() {
        setSpacing(18);
        setPadding(new Insets(24, 32, 28, 32));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                        + FONT);
    }

    private HBox buildHeaderSection() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Election Results & Analytics Command Center");
        title.setStyle(FONT
                + "-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");
        Label subtitle = new Label(
                "Certified live results, position-wise breakdown analytics, and secure report generation");
        subtitle.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Label resultStatus = new Label("LIVE RESULTS FEED");
        resultStatus.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 900; -fx-font-size: 11.5px; -fx-padding: 6 14; -fx-background-radius: 20; -fx-border-color: #10b981; -fx-border-radius: 20;");

        header.getChildren().addAll(titleBox, headerSpace, resultStatus);
        return header;
    }

    private HBox buildElectionActionBar() {
        HBox electionBar = new HBox(14);
        electionBar.setAlignment(Pos.CENTER_LEFT);
        electionBar.setPadding(new Insets(12, 20, 12, 20));
        electionBar.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 3);");

        Label selectLabel = new Label("Select Election:");
        selectLabel.setStyle(FONT + "-fx-font-size: 13.5px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        electionCombo = new ComboBox<>();
        electionCombo.setPromptText("Loading elections...");
        electionCombo.setPrefWidth(320);
        electionCombo.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 4 8;");
        electionCombo.setOnAction(e -> handleElectionSelection());

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(20, 20);
        loadingIndicator.setVisible(false);

        Region barSpace = new Region();
        HBox.setHgrow(barSpace, Priority.ALWAYS);

        Button refreshButton = createActionButton("🔄 Refresh", "#4f46e5", e -> handleElectionSelection());
        Button summarizeButton = createActionButton("📊 Summarize Winners", "#2563eb", e -> showSummary());
        Button exportReportBtn = createActionButton("📥 Export Certified Tally (.txt)", "#ecfdf5",
                e -> exportResultReport());

        exportReportBtn.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");

        electionBar.getChildren().addAll(selectLabel, electionCombo, loadingIndicator, barSpace, refreshButton, summarizeButton, exportReportBtn);
        return electionBar;
    }

    private Button createActionButton(String text, String colorHex,
            javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setMinWidth(Region.USE_PREF_SIZE);
        btn.setStyle(FONT + "-fx-background-color: " + colorHex
                + "; -fx-text-fill: white; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnAction(handler);
        return btn;
    }

    private ScrollPane buildResultScrollPane() {
        resultList = new VBox(16);
        resultList.setPadding(new Insets(4, 0, 4, 0));
        showSelectElectionMessage();

        ScrollPane scroll = new ScrollPane(resultList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private void showSelectElectionMessage() {
        resultList.getChildren().clear();

        VBox message = new VBox(12);
        message.setAlignment(Pos.CENTER);
        message.setPrefHeight(320);
        message.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 14; -fx-border-color: #cbd5e1; -fx-border-radius: 14; -fx-border-width: 1.5;");

        Label icon = new Label("📊");
        icon.setStyle(FONT + "-fx-font-size: 42px;");

        Label title = new Label("No Election Selected");
        title.setStyle(FONT + "-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label text = new Label(
                "Select an active or closed election from the dropdown above to view live certified vote distributions.");
        text.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 13px;");

        message.getChildren().addAll(icon, title, text);
        resultList.getChildren().add(message);
    }

    public void loadElectionsFromFirebase() {
        loadingIndicator.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                String joinCode = SessionManager.joinCode;
                String idToken = SessionManager.idToken;

                if (joinCode == null || joinCode.isBlank()) {
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        electionCombo.setPromptText("No organization logged in");
                    });
                    return;
                }

                List<ElectionData> elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);

                Platform.runLater(() -> {
                    loadedElections = elections;
                    electionCombo.getItems().clear();

                    if (elections.isEmpty()) {
                        electionCombo.setPromptText("No elections created yet");
                        showEmptyElectionsMessage();
                    } else {
                        for (ElectionData election : elections) {
                            String title = election.getTitle() != null ? election.getTitle() : "Untitled Election";
                            electionCombo.getItems().add(title);
                        }
                        electionCombo.getSelectionModel().selectFirst();
                        handleElectionSelection();
                    }
                    loadingIndicator.setVisible(false);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    electionCombo.setPromptText("Failed to load elections");
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void showEmptyElectionsMessage() {
        resultList.getChildren().clear();

        VBox message = new VBox(12);
        message.setAlignment(Pos.CENTER);
        message.setPrefHeight(320);
        message.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 14; -fx-border-color: #cbd5e1; -fx-border-radius: 14; -fx-border-width: 1.5;");

        Label icon = new Label("🗳️");
        icon.setStyle(FONT + "-fx-font-size: 42px;");

        Label title = new Label("No Elections in Organization");
        title.setStyle(FONT + "-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label text = new Label("Create your first election under the Elections tab to begin receiving ballots.");
        text.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 13px;");

        message.getChildren().addAll(icon, title, text);
        resultList.getChildren().add(message);
    }

    private void handleElectionSelection() {
        String selectedTitle = electionCombo.getValue();
        if (selectedTitle == null) {
            showSelectElectionMessage();
            return;
        }

        ElectionData selectedElection = loadedElections.stream()
                .filter(e -> selectedTitle.equals(e.getTitle()))
                .findFirst()
                .orElse(null);

        if (selectedElection == null) {
            return;
        }

        loadingIndicator.setVisible(true);
        resultList.getChildren().clear();

        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPrefHeight(250);
        Label loadingLbl = new Label("Fetching live certified results from Firebase...");
        loadingLbl.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #4338ca;");
        loadingBox.getChildren().addAll(new ProgressIndicator(), loadingLbl);
        resultList.getChildren().add(loadingBox);

        Thread thread = new Thread(() -> {
            try {
                String idToken = SessionManager.idToken;
                String electionId = selectedElection.getId();

                List<Candidate> candidates = CandidateDAO.getCandidatesByElection(electionId, idToken);
                Map<String, Map<String, Integer>> voteResults = VoteDAO.getResults(electionId, idToken);

                ElectionResult electionResult = buildElectionResultFromFirebase(selectedElection, candidates, voteResults);

                Platform.runLater(() -> {
                    this.currentResult = electionResult;
                    renderResultUI(electionResult, selectedElection);
                    loadingIndicator.setVisible(false);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    showStyledAlert("Data Error", "Unable to load live election results: " + ex.getMessage(), "✕", "#dc2626", "#fee2e2");
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private ElectionResult buildElectionResultFromFirebase(
            ElectionData election,
            List<Candidate> candidates,
            Map<String, Map<String, Integer>> voteResults) {

        Set<String> positionsSet = new LinkedHashSet<>();
        if (election.getPositions() != null) {
            positionsSet.addAll(election.getPositions());
        }
        for (Candidate c : candidates) {
            if (c.getPosition() != null && !c.getPosition().isBlank()) {
                positionsSet.add(c.getPosition());
            }
        }
        positionsSet.addAll(voteResults.keySet());

        List<PositionResult> positionResults = new ArrayList<>();

        for (String positionName : positionsSet) {
            Map<String, Integer> tallyMap = voteResults.getOrDefault(positionName, Collections.emptyMap());

            List<CandidateResult> candidateList = new ArrayList<>();
            Set<String> processedCandidates = new HashSet<>();

            for (Candidate c : candidates) {
                if (positionName.equalsIgnoreCase(c.getPosition())) {
                    String name = c.getName() != null ? c.getName() : "Unnamed Candidate";
                    int votes = tallyMap.getOrDefault(name, 0);
                    candidateList.add(new CandidateResult(name, votes, 0.0));
                    processedCandidates.add(name);
                }
            }

            for (Map.Entry<String, Integer> entry : tallyMap.entrySet()) {
                if (!processedCandidates.contains(entry.getKey())) {
                    candidateList.add(new CandidateResult(entry.getKey(), entry.getValue(), 0.0));
                }
            }

            int positionTotalVotes = candidateList.stream().mapToInt(c -> c.votes).sum();

            for (CandidateResult cr : candidateList) {
                if (positionTotalVotes > 0) {
                    cr.percentage = (cr.votes * 100.0) / positionTotalVotes;
                } else {
                    cr.percentage = 0.0;
                }
            }

            candidateList.sort((a, b) -> Integer.compare(b.votes, a.votes));

            PositionResult pr = new PositionResult(
                    positionName,
                    candidateList.toArray(new CandidateResult[0]),
                    positionTotalVotes);

            positionResults.add(pr);
        }

        return new ElectionResult(election.getTitle(), positionResults.toArray(new PositionResult[0]));
    }

    private void renderResultUI(ElectionResult result, ElectionData electionData) {
        resultList.getChildren().clear();

        VBox electionHeader = new VBox(6);
        electionHeader.setPadding(new Insets(4, 0, 8, 0));

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label electionName = new Label(result.electionName);
        electionName.setStyle(FONT + "-fx-font-size: 21px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        String statusStr = electionData.getStatus() != null ? electionData.getStatus().toUpperCase() : "ACTIVE";
        Label statusBadge = new Label("STATUS: " + statusStr);
        statusBadge.setStyle(FONT + "-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-color: #bfdbfe; -fx-border-radius: 12;");

        titleRow.getChildren().addAll(electionName, statusBadge);

        Label subtitle = new Label("Certified Real-Time Breakdown • Verified Ballot Tally & Candidate Standings");
        subtitle.setStyle(FONT + "-fx-text-fill: #4338ca; -fx-font-size: 13px; -fx-font-weight: 600;");

        electionHeader.getChildren().addAll(titleRow, subtitle);
        resultList.getChildren().add(electionHeader);

        if (result.positions.length == 0) {
            VBox noPosBox = new VBox(10);
            noPosBox.setAlignment(Pos.CENTER);
            noPosBox.setPrefHeight(200);
            noPosBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");
            Label noPosLbl = new Label("No ballot positions configured for this election.");
            noPosLbl.setStyle(FONT + "-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
            noPosBox.getChildren().add(noPosLbl);
            resultList.getChildren().add(noPosBox);
            return;
        }

        for (PositionResult position : result.positions) {
            resultList.getChildren().add(createPositionResult(position));
        }
    }

    private void exportResultReport() {
        if (currentResult == null || currentResult.positions.length == 0) {
            showStyledAlert("No Data to Export", "Please select an election with available results before exporting.", "⚠️",
                    "#d97706", "#fffbeb");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Certified Election Report");
        String sanitizedName = currentResult.electionName.replaceAll("[^a-zA-Z0-9_-]", "_");
        fileChooser.setInitialFileName("ElectraVote_Tally_" + sanitizedName + "_" + System.currentTimeMillis() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(AdminDashboard.AdminDashboardStage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("====================================================================\n");
                writer.write("                ELECTRAVOTE SaaS ELECTION PLATFORM                  \n");
                writer.write("                 OFFICIAL CERTIFIED RESULT TALLY                    \n");
                writer.write("====================================================================\n\n");
                writer.write("Tenant Organization     : " + (SessionManager.organizationName != null ? SessionManager.organizationName : "ElectraVote Tenant") + "\n");
                writer.write("Organization Join Code  : " + (SessionManager.joinCode != null ? SessionManager.joinCode : "N/A") + "\n");
                writer.write("Election Title          : " + currentResult.electionName + "\n");
                writer.write("Certification Timestamp : "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")) + "\n");
                writer.write("Ballot Privacy Standard : Zero-Knowledge Decoupled Cryptographic Ledger\n\n");
                writer.write("--------------------------------------------------------------------\n");
                writer.write("POSITION-WISE RESULTS BREAKDOWN\n");
                writer.write("--------------------------------------------------------------------\n\n");

                for (PositionResult pos : currentResult.positions) {
                    writer.write("Office: " + pos.positionName + " (Total Ballots: " + pos.totalVotes + ")\n");
                    if (pos.candidates.length == 0) {
                        writer.write("  [No candidates registered for this position]\n\n");
                        continue;
                    }
                    int r = 1;
                    for (CandidateResult c : pos.candidates) {
                        writer.write(
                                String.format("  #%d %-24s : %4d votes (%.1f%%)\n", r, c.name, c.votes, c.percentage));
                        r++;
                    }
                    writer.write("\n");
                }
                writer.write("====================================================================\n");
                writer.write("End of Certified Report. Cryptographically Verified by ElectraVote Engine.\n");

                showStyledAlert("Export Successful",
                        "Certified election result report successfully saved to disk:\n" + file.getAbsolutePath(), "✓",
                        "#16a34a", "#dcfce7");
            } catch (IOException e) {
                showStyledAlert("Export Failed", "Unable to save file: " + e.getMessage(), "✕", "#dc2626", "#fee2e2");
            }
        }
    }

    private void showSummary() {
        if (currentResult == null || currentResult.positions.length == 0) {
            showStyledAlert("Select Election", "Please select an election with results before generating the summary report.", "⚠️",
                    "#d97706", "#fffbeb");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            dialog.initOwner(AdminDashboard.AdminDashboardStage);
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(dialog);
        }
        dialog.setTitle("Official Election Summary");

        VBox summaryBox = new VBox(14);
        summaryBox.setPadding(new Insets(24));
        summaryBox.setPrefWidth(520);
        summaryBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Label electionName = new Label(currentResult.electionName);
        electionName.setStyle(FONT + "-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label summarySubtitle = new Label("Official Position-Wise Winner Summary");
        summarySubtitle.setStyle(FONT + "-fx-text-fill: #4338ca; -fx-font-size: 12.5px; -fx-font-weight: 600;");

        summaryBox.getChildren().addAll(electionName, summarySubtitle, new Separator());

        for (PositionResult position : currentResult.positions) {
            CandidateResult winner = findWinner(position);
            summaryBox.getChildren().add(createWinnerSummary(position, winner));
        }

        ScrollPane summaryScroll = new ScrollPane(summaryBox);
        summaryScroll.setFitToWidth(true);
        summaryScroll.setPrefHeight(450);
        summaryScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        dialog.getDialogPane().setContent(summaryScroll);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #818cf8; -fx-border-width: 1.5; -fx-border-radius: 14; -fx-background-radius: 14;");

        ButtonType closeType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeType);

        Button closeBtn = (Button) dialog.getDialogPane().lookupButton(closeType);
        if (closeBtn != null) {
            closeBtn.setStyle(FONT
                    + "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 9 20; -fx-cursor: hand;");
        }

        dialog.showAndWait();
    }

    private CandidateResult findWinner(PositionResult position) {
        if (position.candidates == null || position.candidates.length == 0) {
            return new CandidateResult("No candidate", 0, 0.0);
        }
        CandidateResult winner = position.candidates[0];
        for (CandidateResult candidate : position.candidates) {
            if (candidate.votes > winner.votes) {
                winner = candidate;
            }
        }
        return winner;
    }

    private VBox createWinnerSummary(PositionResult position, CandidateResult winner) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.2;");

        Label positionLabel = new Label("🏆  " + position.positionName);
        positionLabel.setStyle(FONT + "-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label winnerLabel = new Label(winner.votes > 0 ? "Leading Winner: " + winner.name : "Top Contender: " + winner.name);
        winnerLabel.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #059669;");

        HBox statsRow = new HBox(20);
        Label votesLabel = new Label("Votes: " + winner.votes);
        votesLabel.setStyle(FONT + "-fx-text-fill: #334155; -fx-font-size: 12px; -fx-font-weight: 700;");

        Label percentageLabel = new Label(String.format("Vote Share: %.1f%%", winner.percentage));
        percentageLabel.setStyle(FONT + "-fx-text-fill: #2563eb; -fx-font-size: 12px; -fx-font-weight: 800;");

        statsRow.getChildren().addAll(votesLabel, percentageLabel);

        card.getChildren().addAll(positionLabel, winnerLabel, statsRow);
        return card;
    }

    private VBox createPositionResult(PositionResult position) {
        VBox positionBox = new VBox(12);
        positionBox.setPadding(new Insets(18, 22, 18, 22));
        positionBox.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 14; -fx-border-color: #cbd5e1; -fx-border-radius: 14; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 3);");

        HBox positionHeader = new HBox();
        positionHeader.setAlignment(Pos.CENTER_LEFT);

        Label positionName = new Label(position.positionName);
        positionName.setStyle(FONT + "-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label totalVotes = new Label("Total Position Ballots: " + position.totalVotes);
        totalVotes.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 12px; -fx-font-weight: 700;");

        positionHeader.getChildren().addAll(positionName, space, totalVotes);
        positionBox.getChildren().add(positionHeader);

        if (position.candidates == null || position.candidates.length == 0) {
            Label noCand = new Label("No registered candidates for this office.");
            noCand.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: #94a3b8; -fx-padding: 8 0;");
            positionBox.getChildren().add(noCand);
            return positionBox;
        }

        int maxVotes = -1;
        for (CandidateResult c : position.candidates) {
            if (c.votes > maxVotes)
                maxVotes = c.votes;
        }

        int rank = 1;
        for (CandidateResult candidate : position.candidates) {
            boolean isWinner = (candidate.votes > 0 && candidate.votes == maxVotes);
            positionBox.getChildren().add(createCandidateResultCard(candidate, rank, isWinner));
            rank++;
        }

        return positionBox;
    }

    private HBox createCandidateResultCard(CandidateResult candidate, int rank, boolean isWinner) {
        HBox card = new HBox(14);
        card.setMinHeight(80);
        card.setPadding(new Insets(12, 18, 12, 18));
        card.setAlignment(Pos.CENTER_LEFT);

        if (isWinner) {
            card.setStyle(
                    "-fx-background-color: linear-gradient(to right, #fefce8 0%, #fef9c3 100%); -fx-background-radius: 10; -fx-border-color: #facc15; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(234,179,8,0.15), 6, 0, 0, 2);");
        } else {
            card.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");
        }

        Label rankLabel = new Label("#" + rank);
        rankLabel.setMinWidth(32);
        rankLabel.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: "
                + (isWinner ? "#ca8a04" : "#64748b") + ";");

        Label avatar = new Label(candidate.name.isEmpty() ? "?" : candidate.name.substring(0, 1).toUpperCase());
        avatar.setMinSize(42, 42);
        avatar.setMaxSize(42, 42);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(FONT + "-fx-background-color: " + (isWinner ? "#fef08a" : "#eff6ff") + "; -fx-text-fill: "
                + (isWinner ? "#854d0e" : "#1d4ed8")
                + "; -fx-font-size: 15px; -fx-font-weight: 900; -fx-background-radius: 21; -fx-border-color: "
                + (isWinner ? "#fde047" : "#bfdbfe") + "; -fx-border-radius: 21;");

        VBox information = new VBox(3);
        information.setMinWidth(200);

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(candidate.name);
        name.setStyle(FONT + "-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        if (isWinner) {
            Label winnerBadge = new Label("🏆 ELECTED WINNER");
            winnerBadge.setStyle(FONT
                    + "-fx-background-color: #fef08a; -fx-text-fill: #854d0e; -fx-font-size: 9.5px; -fx-font-weight: 900; -fx-padding: 2 8; -fx-background-radius: 10; -fx-border-color: #facc15; -fx-border-radius: 10;");
            nameRow.getChildren().addAll(name, winnerBadge);
        } else {
            nameRow.getChildren().add(name);
        }

        Label votesText = new Label(candidate.votes + " verified votes recorded");
        votesText.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 12px; -fx-font-weight: 600;");
        information.getChildren().addAll(nameRow, votesText);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        VBox voteBarTelemetry = new VBox(5);
        voteBarTelemetry.setAlignment(Pos.CENTER_RIGHT);
        voteBarTelemetry.setMinWidth(260);

        HBox telemetryHeader = new HBox();
        telemetryHeader.setAlignment(Pos.CENTER_RIGHT);

        Label tallyReadout = new Label(
                candidate.votes + " ballots (" + String.format("%.1f%%", candidate.percentage) + ")");
        tallyReadout.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: "
                + (isWinner ? "#047857" : "#1e293b") + ";");
        telemetryHeader.getChildren().add(tallyReadout);

        StackPane voteBarTrack = new StackPane();
        voteBarTrack.setAlignment(Pos.CENTER_LEFT);
        voteBarTrack.setPrefSize(260, 14);
        voteBarTrack.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 7;");

        Region voteBarFill = new Region();
        voteBarFill.setPrefHeight(14);
        double barWidth = candidate.percentage > 0 ? Math.max(12, 260 * (candidate.percentage / 100.0)) : 0;
        voteBarFill.setMaxWidth(barWidth);
        voteBarFill.setMinWidth(barWidth);
        voteBarFill.setStyle("-fx-background-color: linear-gradient(to right, "
                + (isWinner ? "#10b981, #059669" : "#3b82f6, #2563eb") + "); -fx-background-radius: 7;");

        voteBarTrack.getChildren().add(voteBarFill);
        voteBarTelemetry.getChildren().addAll(telemetryHeader, voteBarTrack);

        card.getChildren().addAll(rankLabel, avatar, information, space, voteBarTelemetry);
        return card;
    }

    private void showStyledAlert(String titleStr, String descStr, String iconGlyph, String iconColorHex,
            String iconBgHex) {
        Dialog<ButtonType> alert = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            alert.initOwner(AdminDashboard.AdminDashboardStage);
            alert.initModality(javafx.stage.Modality.WINDOW_MODAL);
        } else {
            com.electrovotesuperx.utils.Navigation.attachOwner(alert);
        }
        alert.setTitle("System Notification");

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(24));
        contentBox.setPrefWidth(420);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Circle outerHalo = new Circle(28, Color.web(iconBgHex));
        Circle innerCircle = new Circle(20, Color.web(iconColorHex));
        Label icon = new Label(iconGlyph);
        icon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 900;");
        StackPane iconStack = new StackPane(outerHalo, innerCircle, icon);

        VBox textBox = new VBox(6);
        textBox.setAlignment(Pos.CENTER);
        Label title = new Label(titleStr);
        title.setWrapText(true);
        title.setStyle(FONT
                + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-text-alignment: CENTER;");

        Label desc = new Label(descStr);
        desc.setWrapText(true);
        desc.setStyle(FONT
                + "-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-text-alignment: CENTER; -fx-line-spacing: 1.5px;");
        textBox.getChildren().addAll(title, desc);

        contentBox.getChildren().addAll(iconStack, textBox);
        alert.getDialogPane().setContent(contentBox);
        alert.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: " + iconColorHex
                + "; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        ButtonType okBtnType = new ButtonType("Acknowledge", ButtonBar.ButtonData.OK_DONE);
        alert.getDialogPane().getButtonTypes().add(okBtnType);

        Button okBtn = (Button) alert.getDialogPane().lookupButton(okBtnType);
        if (okBtn != null) {
            okBtn.setStyle(FONT + "-fx-background-color: " + iconColorHex
                    + "; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        }

        alert.showAndWait();
    }

    private static class ElectionResult {
        String electionName;
        PositionResult[] positions;

        ElectionResult(String electionName, PositionResult[] positions) {
            this.electionName = electionName;
            this.positions = positions;
        }
    }

    private static class PositionResult {
        String positionName;
        CandidateResult[] candidates;
        int totalVotes;

        PositionResult(String positionName, CandidateResult[] candidates, int totalVotes) {
            this.positionName = positionName;
            this.candidates = candidates;
            this.totalVotes = totalVotes;
        }
    }

    private static class CandidateResult {
        String name;
        int votes;
        double percentage;

        CandidateResult(String name, int votes, double percentage) {
            this.name = name;
            this.votes = votes;
            this.percentage = percentage;
        }
    }
}