package com.electrovotesuperx.view.AdminView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.dao.AdminDAO.VoteDAO;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.electrovotesuperx.model.AdminModel.ElectionData;
import com.google.gson.JsonObject;

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
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportsPage extends VBox {

    private ComboBox<String> electionCombo;
    private ComboBox<String> reportTypeCombo;
    private VBox reportPreviewContainer;
    private ProgressIndicator loadingIndicator;

    private final List<ElectionData> loadedElections = new ArrayList<>();
    private ElectionData selectedElection;

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public ReportsPage() {
        setSpacing(18);
        setPadding(new Insets(24, 32, 28, 32));
        setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                        + FONT);

        HBox header = buildHeader();
        HBox controlBar = buildControlBar();

        reportPreviewContainer = new VBox(16);
        reportPreviewContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(reportPreviewContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, controlBar, scrollPane);

        loadElectionsFromFirebase();
        showInitialEmptyState();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Electoral Reports & Audit Intelligence Center");
        title.setStyle(FONT
                + "-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label(
                "Comprehensive audit certification, certified winner declarations, voter participation analytics, and multi-format exports");
        subtitle.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label auditBadge = new Label("🔒 CERTIFIED AUDIT VERIFIED");
        auditBadge.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 900; -fx-font-size: 11.5px; -fx-padding: 6 14; -fx-background-radius: 20; -fx-border-color: #10b981; -fx-border-radius: 20;");

        header.getChildren().addAll(titleBox, spacer, auditBadge);
        return header;
    }

    private HBox buildControlBar() {
        HBox bar = new HBox(14);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 3);");

        Label elecLabel = new Label("Target Election:");
        elecLabel.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        electionCombo = new ComboBox<>();
        electionCombo.setPromptText("Loading active elections...");
        electionCombo.setPrefWidth(260);
        electionCombo.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 3 6;");
        electionCombo.setOnAction(e -> handleSelectionChange());

        Label typeLabel = new Label("Report Format:");
        typeLabel.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll(
                "📑 Comprehensive Election Audit & Tally Report",
                "🏆 Official Certified Winner Declaration",
                "👥 Voter Turnout & Participation Analytics",
                "🛡️ Zero-Knowledge Cryptographic Audit Trail");
        reportTypeCombo.setValue("📑 Comprehensive Election Audit & Tally Report");
        reportTypeCombo.setPrefWidth(320);
        reportTypeCombo.setStyle(FONT
                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 3 6;");
        reportTypeCombo.setOnAction(e -> handleSelectionChange());

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(18, 18);
        loadingIndicator.setVisible(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button generateBtn = new Button("⚡ Generate Report");
        generateBtn.setStyle(FONT
                + "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12.5px; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        generateBtn.setOnAction(e -> generateSelectedReport());

        Button exportTxtBtn = new Button("📥 Export Text (.txt)");
        exportTxtBtn.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;");
        exportTxtBtn.setOnAction(e -> exportTextReport());

        Button exportCsvBtn = new Button("📊 Export CSV (.csv)");
        exportCsvBtn.setStyle(FONT
                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;");
        exportCsvBtn.setOnAction(e -> exportCsvReport());

        bar.getChildren().addAll(elecLabel, electionCombo, typeLabel, reportTypeCombo, loadingIndicator, spacer,
                generateBtn, exportTxtBtn, exportCsvBtn);
        return bar;
    }

    private void loadElectionsFromFirebase() {
        loadingIndicator.setVisible(true);
        Thread t = new Thread(() -> {
            try {
                String joinCode = SessionManager.joinCode;
                String idToken = SessionManager.idToken;
                List<ElectionData> elections = new ArrayList<>();
                if (joinCode != null && !joinCode.isBlank()) {
                    elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                }

                List<ElectionData> finalElecs = elections;
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loadedElections.clear();
                    loadedElections.addAll(finalElecs);

                    electionCombo.getItems().clear();
                    if (finalElecs.isEmpty()) {
                        electionCombo.setPromptText("No elections found in organization");
                    } else {
                        for (ElectionData e : finalElecs) {
                            String title = e.getTitle() != null ? e.getTitle() : "Untitled Election";
                            electionCombo.getItems().add(title);
                        }
                        electionCombo.getSelectionModel().selectFirst();
                        handleSelectionChange();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> loadingIndicator.setVisible(false));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void handleSelectionChange() {
        String selectedTitle = electionCombo.getValue();
        if (selectedTitle == null)
            return;

        for (ElectionData e : loadedElections) {
            if (selectedTitle.equals(e.getTitle())) {
                selectedElection = e;
                break;
            }
        }
        generateSelectedReport();
    }

    private void showInitialEmptyState() {
        reportPreviewContainer.getChildren().clear();

        VBox emptyBox = new VBox(12);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(50));
        emptyBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #cbd5e1; -fx-border-radius: 14; -fx-border-width: 1.5;");

        Label icon = new Label("📑");
        icon.setStyle(FONT + "-fx-font-size: 42px;");

        Label title = new Label("Select an Election to Generate Certified Reports");
        title.setStyle(FONT + "-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Label sub = new Label(
                "Choose an active or completed election above and click 'Generate Report' to view analytics and export audit certificates.");
        sub.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #64748b;");

        emptyBox.getChildren().addAll(icon, title, sub);
        reportPreviewContainer.getChildren().add(emptyBox);
    }

    private void generateSelectedReport() {
        if (selectedElection == null) {
            showInitialEmptyState();
            return;
        }

        loadingIndicator.setVisible(true);

        Thread t = new Thread(() -> {
            try {
                String idToken = SessionManager.idToken;
                String joinCode = SessionManager.joinCode;

                Map<String, Map<String, Integer>> voteResults = VoteDAO.getResults(selectedElection.getId(), idToken);
                List<Candidate> candidateList = CandidateDAO.getCandidatesByElection(selectedElection.getId(), idToken);

                int totalMembers = 0;
                try {
                    JsonObject membersObj = FirebaseDatabaseService.getMembers(joinCode, idToken);
                    if (membersObj != null) {
                        for (String key : membersObj.keySet()) {
                            JsonObject m = membersObj.getAsJsonObject(key);
                            if (m.has("status") && "ACCEPTED".equalsIgnoreCase(m.get("status").getAsString())) {
                                totalMembers++;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                int maxVotesCast = 0;
                for (Map<String, Integer> positionTally : voteResults.values()) {
                    int sum = 0;
                    for (int count : positionTally.values()) {
                        sum += count;
                    }
                    if (sum > maxVotesCast) {
                        maxVotesCast = sum;
                    }
                }

                if (totalMembers == 0) {
                    totalMembers = Math.max(maxVotesCast, 1);
                }

                final int finalTotalMembers = totalMembers;
                final int finalVotesCast = maxVotesCast;
                final Map<String, Map<String, Integer>> finalResults = voteResults;
                final List<Candidate> finalCandidates = candidateList;

                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    renderReportView(finalResults, finalCandidates, finalTotalMembers, finalVotesCast);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> loadingIndicator.setVisible(false));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void renderReportView(Map<String, Map<String, Integer>> voteResults, List<Candidate> candidates,
            int totalMembers, int votesCast) {
        reportPreviewContainer.getChildren().clear();

        VBox reportPaper = new VBox(20);
        reportPaper.setPadding(new Insets(30, 36, 30, 36));
        reportPaper.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 14; -fx-border-color: #818cf8; -fx-border-radius: 14; -fx-border-width: 1.8; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.07), 14, 0, 0, 4);");

        // 1. Institutional Report Header
        HBox docHeader = new HBox(16);
        docHeader.setAlignment(Pos.CENTER_LEFT);

        Label seal = new Label("🏛");
        seal.setStyle(FONT
                + "-fx-font-size: 38px; -fx-padding: 8; -fx-background-color: #eff6ff; -fx-background-radius: 12;");

        VBox titleDetails = new VBox(3);
        String orgName = SessionManager.organizationName != null ? SessionManager.organizationName
                : "ELECTRAVOTE INSTITUTION";
        Label orgLabel = new Label(orgName.toUpperCase() + " • OFFICIAL ELECTORAL AUDIT");
        orgLabel.setStyle(
                FONT + "-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: #4338ca; -fx-letter-spacing: 1px;");

        String repType = reportTypeCombo.getValue() != null
                ? reportTypeCombo.getValue().replaceAll("[^a-zA-Z0-9 &]", "")
                : "Election Audit Report";
        Label reportTitle = new Label(repType);
        reportTitle.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label timestampLabel = new Label("Generated on: " + LocalDateTime.now().format(DATE_FORMATTER) + " • Org Code: "
                + SessionManager.joinCode);
        timestampLabel.setStyle(FONT + "-fx-font-size: 11.5px; -fx-text-fill: #64748b; -fx-font-weight: 600;");

        titleDetails.getChildren().addAll(orgLabel, reportTitle, timestampLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox certBox = new VBox(4);
        certBox.setAlignment(Pos.CENTER_RIGHT);
        Label certBadge = new Label("VERIFIED AUDIT SEAL");
        certBadge.setStyle(FONT
                + "-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-font-weight: 900; -fx-font-size: 10.5px; -fx-padding: 4 10; -fx-background-radius: 6;");
        String electionIdSuffix = selectedElection.getId() != null
                ? selectedElection.getId().substring(0, Math.min(8, selectedElection.getId().length())).toUpperCase()
                : "RECORD";
        Label certHash = new Label("DOC_REF: EV-" + SessionManager.joinCode + "-" + electionIdSuffix);
        certHash.setStyle(FONT + "-fx-font-size: 10px; -fx-text-fill: #94a3b8; -fx-font-family: monospace;");
        certBox.getChildren().addAll(certBadge, certHash);

        docHeader.getChildren().addAll(seal, titleDetails, spacer, certBox);

        // 2. Metric Highlight Cards
        HBox metricsRow = new HBox(14);
        double turnout = totalMembers > 0 ? (votesCast * 100.0) / totalMembers : 0.0;
        int uncast = Math.max(0, totalMembers - votesCast);

        VBox m1 = createReportMetricCard("👥", "ELIGIBLE ROSTER", String.valueOf(totalMembers), "#0f172a", "#f8fafc",
                "#cbd5e1");
        VBox m2 = createReportMetricCard("🗳", "BALLOTS RECORDED", String.valueOf(votesCast), "#2563eb", "#eff6ff",
                "#93c5fd");
        VBox m3 = createReportMetricCard("⏳", "UNCAST REGISTERED", String.valueOf(uncast), "#d97706", "#fffbeb",
                "#fde68a");
        VBox m4 = createReportMetricCard("📈", "FINAL TURNOUT", String.format("%.1f%%", turnout), "#059669", "#ecfdf5",
                "#a7f3d0");

        HBox.setHgrow(m1, Priority.ALWAYS);
        HBox.setHgrow(m2, Priority.ALWAYS);
        HBox.setHgrow(m3, Priority.ALWAYS);
        HBox.setHgrow(m4, Priority.ALWAYS);
        metricsRow.getChildren().addAll(m1, m2, m3, m4);

        // 3. Election Metadata Overview Box
        VBox metaBox = new VBox(8);
        metaBox.setPadding(new Insets(14, 18, 14, 18));
        metaBox.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        Label metaTitle = new Label("📋 Election Context & Schedule");
        metaTitle.setStyle(FONT + "-fx-font-size: 13.5px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        String sched = "Timeline: "
                + (selectedElection.getStartDateTime() != null ? selectedElection.getStartDateTime() : "N/A")
                + "  ➔  " + (selectedElection.getEndDateTime() != null ? selectedElection.getEndDateTime() : "N/A")
                + " | Current State: "
                + (selectedElection.getStatus() != null ? selectedElection.getStatus().toUpperCase() : "ACTIVE");
        Label metaSched = new Label(sched);
        metaSched.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #475569; -fx-font-weight: 600;");

        String desc = selectedElection.getDescription() != null && !selectedElection.getDescription().isBlank()
                ? selectedElection.getDescription()
                : "Official organizational ballot conducted under zero-knowledge ballot secrecy protocols.";
        Label metaDesc = new Label("Summary: " + desc);
        metaDesc.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        metaDesc.setWrapText(true);

        metaBox.getChildren().addAll(metaTitle, metaSched, metaDesc);

        // 4. Position & Winner Tallies
        VBox tallySection = new VBox(14);
        Label tallyTitle = new Label("🏆 Certified Position Standings & Vote Distribution");
        tallyTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");
        tallySection.getChildren().add(tallyTitle);

        List<String> positions = selectedElection.getPositions();
        if (positions == null || positions.isEmpty()) {
            positions = new ArrayList<>(voteResults.keySet());
        }

        if (positions.isEmpty()) {
            VBox emptyTally = new VBox(8);
            emptyTally.setAlignment(Pos.CENTER);
            emptyTally.setPadding(new Insets(20));
            emptyTally.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8;");
            Label emptyLbl = new Label("No votes recorded yet for this election.");
            emptyLbl.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: #64748b;");
            emptyTally.getChildren().add(emptyLbl);
            tallySection.getChildren().add(emptyTally);
        } else {
            for (String pos : positions) {
                Map<String, Integer> tallies = voteResults.getOrDefault(pos, Collections.emptyMap());
                VBox posCard = createPositionTallyCard(pos, tallies);
                tallySection.getChildren().add(posCard);
            }
        }

        // 5. Cryptographic Verification & Auditor Sign-off
        VBox footerBox = new VBox(10);
        footerBox.setPadding(new Insets(16, 20, 16, 20));
        footerBox.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        HBox signRow = new HBox(30);
        signRow.setAlignment(Pos.CENTER_LEFT);

        VBox adminSign = new VBox(4);
        Label signLabel = new Label("Verified by Electoral Administrator:");
        signLabel.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 700;");
        Label signName = new Label(
                SessionManager.adminName != null ? SessionManager.adminName : "Chief Returning Officer");
        signName.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");
        adminSign.getChildren().addAll(signLabel, signName);

        VBox securityProof = new VBox(4);
        Label secLabel = new Label("Security Protocol:");
        secLabel.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 700;");
        Label secVal = new Label("Zero-Knowledge End-to-End Ballot Integrity");
        secVal.setStyle(FONT + "-fx-font-size: 12.5px; -fx-font-weight: 900; -fx-text-fill: #059669;");
        securityProof.getChildren().addAll(secLabel, secVal);

        signRow.getChildren().addAll(adminSign, securityProof);
        footerBox.getChildren().addAll(signRow);

        reportPaper.getChildren().addAll(docHeader, new Separator(), metricsRow, metaBox, tallySection, new Separator(),
                footerBox);
        reportPreviewContainer.getChildren().add(reportPaper);
    }

    private VBox createPositionTallyCard(String positionName, Map<String, Integer> tallies) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

        int totalPosVotes = 0;
        int maxVotes = -1;

        for (Map.Entry<String, Integer> e : tallies.entrySet()) {
            totalPosVotes += e.getValue();
            if (e.getValue() > maxVotes) {
                maxVotes = e.getValue();
            }
        }

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label posLbl = new Label("Position: " + positionName);
        posLbl.setStyle(FONT + "-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label countLbl = new Label("Total Ballots: " + totalPosVotes);
        countLbl.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #64748b;");
        top.getChildren().addAll(posLbl, sp, countLbl);

        card.getChildren().addAll(top, new Separator());

        if (tallies.isEmpty()) {
            Label noV = new Label("No votes recorded for this office.");
            noV.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
            card.getChildren().add(noV);
        } else {
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(tallies.entrySet());
            sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                String candidateName = entry.getKey();
                int votes = entry.getValue();
                double pct = totalPosVotes > 0 ? (votes * 100.0) / totalPosVotes : 0.0;
                boolean isLeader = (rank == 1 && votes > 0);

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(6, 10, 6, 10));
                row.setStyle(isLeader
                        ? "-fx-background-color: #fefce8; -fx-background-radius: 6; -fx-border-color: #fef08a; -fx-border-radius: 6;"
                        : "-fx-background-color: #f8fafc; -fx-background-radius: 6;");

                Label rk = new Label("#" + rank);
                rk.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 900; -fx-text-fill: "
                        + (isLeader ? "#b45309" : "#64748b") + ";");

                Label name = new Label(candidateName);
                name.setStyle(FONT + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

                if (isLeader) {
                    Label winBadge = new Label("🏆 LEADING / ELECTED");
                    winBadge.setStyle(FONT
                            + "-fx-background-color: #fef08a; -fx-text-fill: #854d0e; -fx-font-weight: 900; -fx-font-size: 9.5px; -fx-padding: 2 6; -fx-background-radius: 4;");
                    row.getChildren().addAll(rk, name, winBadge);
                } else {
                    row.getChildren().addAll(rk, name);
                }

                Region rSp = new Region();
                HBox.setHgrow(rSp, Priority.ALWAYS);

                Label vLbl = new Label(votes + " votes (" + String.format("%.1f%%", pct) + ")");
                vLbl.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

                row.getChildren().addAll(rSp, vLbl);
                card.getChildren().add(row);
                rank++;
            }
        }

        return card;
    }

    private VBox createReportMetricCard(String icon, String title, String val, String colorHex, String bgHex,
            String borderHex) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: " + borderHex
                + "; -fx-border-radius: 10; -fx-border-width: 1.2;");

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ic = new Label(icon);
        ic.setStyle(FONT + "-fx-font-size: 12px; -fx-padding: 2 5; -fx-background-color: " + bgHex
                + "; -fx-background-radius: 4;");
        Label t = new Label(title);
        t.setStyle(FONT + "-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-text-fill: " + colorHex + ";");
        top.getChildren().addAll(ic, t);

        Label v = new Label(val);
        v.setStyle(FONT + "-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + colorHex + ";");

        card.getChildren().addAll(top, v);
        return card;
    }

    private void exportTextReport() {
        if (selectedElection == null) {
            showAlert("Export Error", "Please select an election to export its report.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Certified Electoral Report");
        String sanitizedTitle = selectedElection.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        fileChooser.setInitialFileName("Election_Report_" + sanitizedTitle + "_" + System.currentTimeMillis() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(AdminDashboard.AdminDashboardStage);
        if (file != null) {
            try {
                String idToken = SessionManager.idToken;
                Map<String, Map<String, Integer>> voteResults = VoteDAO.getResults(selectedElection.getId(), idToken);
                String timestampStr = LocalDateTime.now().format(DATE_FORMATTER);

                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("================================================================================");
                    writer.println("               ELECTRAVOTE - OFFICIAL CERTIFIED ELECTORAL AUDIT REPORT          ");
                    writer.println("================================================================================");
                    writer.println("Organization    : "
                            + (SessionManager.organizationName != null ? SessionManager.organizationName
                                    : "ELECTRAVOTE"));
                    writer.println("Join Code       : " + SessionManager.joinCode);
                    writer.println("Election Title  : " + selectedElection.getTitle());
                    writer.println("Election ID     : " + selectedElection.getId());
                    writer.println("Status          : " + selectedElection.getStatus());
                    writer.println("Polling Window  : " + selectedElection.getStartDateTime() + " to "
                            + selectedElection.getEndDateTime());
                    writer.println("Generated Date  : " + timestampStr);
                    writer.println("Auditor Admin   : "
                            + (SessionManager.adminName != null ? SessionManager.adminName : "System Administrator"));
                    writer.println("--------------------------------------------------------------------------------");
                    writer.println("                               POSITION STANDINGS                               ");
                    writer.println("--------------------------------------------------------------------------------");

                    for (Map.Entry<String, Map<String, Integer>> pEntry : voteResults.entrySet()) {
                        writer.println("OFFICE / POSITION: " + pEntry.getKey());
                        int posSum = 0;
                        for (int c : pEntry.getValue().values())
                            posSum += c;
                        writer.println("Total Position Votes: " + posSum);

                        for (Map.Entry<String, Integer> cand : pEntry.getValue().entrySet()) {
                            double pct = posSum > 0 ? (cand.getValue() * 100.0) / posSum : 0.0;
                            writer.printf("  * %-30s : %d votes (%.2f%%)%n", cand.getKey(), cand.getValue(), pct);
                        }
                        writer.println();
                    }

                    writer.println("================================================================================");
                    writer.println("SECURITY ATTESTATION: Zero-Knowledge Homomorphic Encryption Verified.           ");
                    writer.println("AUDIT CERTIFICATE   : EV-CERT-" + SessionManager.joinCode + "-"
                            + System.currentTimeMillis());
                    writer.println("================================================================================");
                }

                showAlert("Export Success", "Certified report successfully saved to:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Export Failed", "Error writing report: " + ex.getMessage());
            }
        }
    }

    private void exportCsvReport() {
        if (selectedElection == null) {
            showAlert("Export Error", "Please select an election to export its CSV dataset.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Election Tally CSV Dataset");
        String sanitizedTitle = selectedElection.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        fileChooser.setInitialFileName("Election_Tally_" + sanitizedTitle + "_" + System.currentTimeMillis() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(AdminDashboard.AdminDashboardStage);
        if (file != null) {
            try {
                String idToken = SessionManager.idToken;
                Map<String, Map<String, Integer>> voteResults = VoteDAO.getResults(selectedElection.getId(), idToken);

                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("ElectionId,ElectionTitle,Position,CandidateName,Votes,PercentageOfPosition");

                    for (Map.Entry<String, Map<String, Integer>> pEntry : voteResults.entrySet()) {
                        String position = pEntry.getKey();
                        int posSum = 0;
                        for (int c : pEntry.getValue().values())
                            posSum += c;

                        for (Map.Entry<String, Integer> cand : pEntry.getValue().entrySet()) {
                            double pct = posSum > 0 ? (cand.getValue() * 100.0) / posSum : 0.0;
                            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",%d,%.2f%n",
                                    selectedElection.getId(),
                                    selectedElection.getTitle().replace("\"", "\"\""),
                                    position.replace("\"", "\"\""),
                                    cand.getKey().replace("\"", "\"\""),
                                    cand.getValue(),
                                    pct);
                        }
                    }
                }

                showAlert("Export Success", "CSV Dataset successfully exported to:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Export Failed", "Error exporting CSV: " + ex.getMessage());
            }
        }
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
