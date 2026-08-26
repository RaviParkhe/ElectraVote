package com.elctrovotesuperx.view.AdminView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

public class ResultPage extends VBox {

    private ComboBox<String> electionCombo;
    private VBox resultList;

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

    public ResultPage() {
        initializeContainerStyle();

        HBox header = buildHeaderSection();
        HBox electionBar = buildElectionActionBar();
        ScrollPane scrollPane = buildResultScrollPane();

        getChildren().addAll(header, electionBar, scrollPane);
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
                "Certified final results, position-wise breakdown analytics, and secure report generation");
        subtitle.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Label resultStatus = new Label("FINAL RESULT CERTIFIED");
        resultStatus.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 900; -fx-font-size: 11.5px; -fx-padding: 6 14; -fx-background-radius: 20; -fx-border-color: #10b981; -fx-border-radius: 20;");

        header.getChildren().addAll(titleBox, headerSpace, resultStatus);
        return header;
    }

    private HBox buildElectionActionBar() {
        HBox electionBar = new HBox(16);
        electionBar.setAlignment(Pos.CENTER_LEFT);
        electionBar.setPadding(new Insets(12, 20, 12, 20));
        electionBar.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 3);");

        Label selectLabel = new Label("Select Election:");
        selectLabel.setStyle(FONT + "-fx-font-size: 13.5px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        electionCombo = new ComboBox<>();
        electionCombo.setPromptText("Select certified election...");
        electionCombo.setPrefWidth(340);
        electionCombo.getItems().addAll(
                "Student Council Election",
                "Cultural Committee Election",
                "Sports Committee Election");
        electionCombo.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 4 8;");
        electionCombo.setOnAction(e -> showResult());

        Region barSpace = new Region();
        HBox.setHgrow(barSpace, Priority.ALWAYS);

        Button summarizeButton = createActionButton("📊 Summarize Winners", "#2563eb", e -> showSummary());
        Button exportReportBtn = createActionButton("📥 Export Certified Tally (.txt)", "#ecfdf5",
                e -> exportResultReport());

        exportReportBtn.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");

        electionBar.getChildren().addAll(selectLabel, electionCombo, barSpace, summarizeButton, exportReportBtn);
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
                "Select a certified election from the dropdown above to examine detailed position metrics and vote distributions.");
        text.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 13px;");

        message.getChildren().addAll(icon, title, text);
        resultList.getChildren().add(message);
    }

    private void showResult() {
        resultList.getChildren().clear();
        String selectedElection = electionCombo.getValue();

        if (selectedElection == null) {
            showSelectElectionMessage();
            return;
        }

        ElectionResult result = getElectionResult(selectedElection);

        Label electionName = new Label(result.electionName);
        electionName.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label subtitle = new Label("Certified final breakdown • Position-wise vote analytics & candidate rankings");
        subtitle.setStyle(FONT + "-fx-text-fill: #4338ca; -fx-font-size: 13px; -fx-font-weight: 600;");

        VBox electionHeader = new VBox(4);
        electionHeader.setPadding(new Insets(4, 0, 6, 0));
        electionHeader.getChildren().addAll(electionName, subtitle);

        resultList.getChildren().add(electionHeader);

        for (PositionResult position : result.positions) {
            resultList.getChildren().add(createPositionResult(position));
        }
    }

    private void exportResultReport() {
        String selected = electionCombo.getValue();
        if (selected == null) {
            showStyledAlert("Select Election", "Please select an election before exporting the certified report.", "⚠️",
                    "#d97706", "#fffbeb");
            return;
        }

        ElectionResult result = getElectionResult(selected);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Certified Election Report");
        fileChooser.setInitialFileName("ElectraVote_Certified_Tally_" + System.currentTimeMillis() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(AdminDashboard.AdminDashboardStage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("====================================================================\n");
                writer.write("                ELECTRAVOTE SaaS ELECTION PLATFORM                  \n");
                writer.write("                 OFFICIAL CERTIFIED RESULT TALLY                    \n");
                writer.write("====================================================================\n\n");
                writer.write("Tenant Organization     : ABC College\n");
                writer.write("Election Title          : " + result.electionName + "\n");
                writer.write("Certification Timestamp : "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")) + "\n");
                writer.write("Ballot Privacy Standard : Zero-Knowledge Decoupled (Voter-to-Choice Blind)\n\n");
                writer.write("--------------------------------------------------------------------\n");
                writer.write("POSITION-WISE RESULTS BREAKDOWN\n");
                writer.write("--------------------------------------------------------------------\n\n");

                for (PositionResult pos : result.positions) {
                    writer.write("Office: " + pos.positionName + " (Total Ballots: " + pos.totalVotes + ")\n");
                    int r = 1;
                    for (CandidateResult c : pos.candidates) {
                        writer.write(
                                String.format("  #%d %-20s : %4d votes (%.1f%%)\n", r, c.name, c.votes, c.percentage));
                        r++;
                    }
                    writer.write("\n");
                }
                writer.write("====================================================================\n");
                writer.write("End of Certified Report. Verified by ElectraVote Zero-Knowledge Engine.\n");

                showStyledAlert("Export Successful",
                        "Certified election result report successfully saved to disk:\n" + file.getAbsolutePath(), "✓",
                        "#16a34a", "#dcfce7");
            } catch (IOException e) {
                showStyledAlert("Export Failed", "Unable to save file: " + e.getMessage(), "✕", "#dc2626", "#fee2e2");
            }
        }
    }

    private void showSummary() {
        String selectedElection = electionCombo.getValue();

        if (selectedElection == null) {
            showStyledAlert("Select Election", "Please select an election before generating the summary report.", "⚠️",
                    "#d97706", "#fffbeb");
            return;
        }

        ElectionResult result = getElectionResult(selectedElection);

        Dialog<ButtonType> dialog = new Dialog<>();
        if (AdminDashboard.AdminDashboardStage != null) {
            dialog.initOwner(AdminDashboard.AdminDashboardStage);
        }
        dialog.setTitle("Election Summary");

        VBox summaryBox = new VBox(14);
        summaryBox.setPadding(new Insets(24));
        summaryBox.setPrefWidth(520);
        summaryBox.setStyle("-fx-background-color: #ffffff; " + FONT);

        Label electionName = new Label(result.electionName);
        electionName.setStyle(FONT + "-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label summarySubtitle = new Label("Official Position-Wise Winner Summary");
        summarySubtitle.setStyle(FONT + "-fx-text-fill: #4338ca; -fx-font-size: 12.5px; -fx-font-weight: 600;");

        summaryBox.getChildren().addAll(electionName, summarySubtitle, new Separator());

        for (PositionResult position : result.positions) {
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

        Label winnerLabel = new Label("Elected Winner: " + winner.name);
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

        int maxVotes = -1;
        for (CandidateResult c : position.candidates) {
            if (c.votes > maxVotes)
                maxVotes = c.votes;
        }

        int rank = 1;
        for (CandidateResult candidate : position.candidates) {
            boolean isWinner = (candidate.votes == maxVotes);
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
        double barWidth = Math.max(12, 260 * (candidate.percentage / 100.0));
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

    private ElectionResult getElectionResult(String election) {
        if (election.equals("Student Council Election")) {
            PositionResult president = new PositionResult("President", new CandidateResult[] {
                    new CandidateResult("Rahul Sharma", 1250, 62.5),
                    new CandidateResult("Amit Kulkarni", 650, 32.5),
                    new CandidateResult("Vikas Patil", 100, 5.0)
            });
            PositionResult secretary = new PositionResult("Secretary", new CandidateResult[] {
                    new CandidateResult("Priya Patil", 1100, 55.0),
                    new CandidateResult("Rohit More", 900, 45.0)
            });
            PositionResult treasurer = new PositionResult("Treasurer", new CandidateResult[] {
                    new CandidateResult("Sneha Joshi", 1400, 70.0),
                    new CandidateResult("Neha Deshmukh", 600, 30.0)
            });
            return new ElectionResult(election, new PositionResult[] { president, secretary, treasurer });
        }

        if (election.equals("Cultural Committee Election")) {
            PositionResult president = new PositionResult("President", new CandidateResult[] {
                    new CandidateResult("Neha Deshmukh", 900, 60.0),
                    new CandidateResult("Akash More", 450, 30.0),
                    new CandidateResult("Pooja Patil", 150, 10.0)
            });
            PositionResult secretary = new PositionResult("Secretary", new CandidateResult[] {
                    new CandidateResult("Kunal Joshi", 700, 53.8),
                    new CandidateResult("Pooja Patil", 600, 46.2)
            });
            return new ElectionResult(election, new PositionResult[] { president, secretary });
        }

        if (election.equals("Sports Committee Election")) {
            PositionResult president = new PositionResult("President", new CandidateResult[] {
                    new CandidateResult("Rohan Sharma", 720, 60.0),
                    new CandidateResult("Aditya More", 360, 30.0),
                    new CandidateResult("Sahil Patil", 120, 10.0)
            });
            PositionResult secretary = new PositionResult("Secretary", new CandidateResult[] {
                    new CandidateResult("Om Kulkarni", 650, 54.2),
                    new CandidateResult("Sahil Patil", 550, 45.8)
            });
            return new ElectionResult(election, new PositionResult[] { president, secretary });
        }

        return new ElectionResult(election, new PositionResult[] {});
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

        PositionResult(String positionName, CandidateResult[] candidates) {
            this.positionName = positionName;
            this.candidates = candidates;
            for (CandidateResult candidate : candidates) {
                totalVotes += candidate.votes;
            }
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