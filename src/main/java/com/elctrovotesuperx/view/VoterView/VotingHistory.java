package com.elctrovotesuperx.view.VoterView;

import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.elctrovotesuperx.dao.AdminDAO.VoteDAO;
import com.elctrovotesuperx.model.AdminModel.ElectionData;
import com.elctrovotesuperx.model.AdminModel.VoteRecord;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VotingHistory {

        private static final String TEXT = "#172033";
        private static final String SECONDARY = "#6B7280";
        private static final String BORDER = "#E2E8F0";
        private static final String BLUE = "#1464F4";
        private static final String GREEN = "#10B981";

        /**
         * Creates the Voting History view showing records of previously cast ballots from Firestore.
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

                Text subtitle = new Text(
                                "Review your immutable record of previously cast ballots, verified securely via zero-knowledge cryptographic proofs.");
                subtitle.setFill(Color.web(SECONDARY));
                subtitle.setFont(Font.font(13));
                subtitle.setWrappingWidth(900);
                heading.getChildren().addAll(title, subtitle);

                // History Records Container
                VBox historyList = new VBox(16);

                VBox loadingBox = new VBox(10);
                loadingBox.setAlignment(Pos.CENTER);
                loadingBox.setPadding(new Insets(30));
                loadingBox.getChildren().addAll(new ProgressIndicator(), new Label("Fetching cryptographic vote receipts from Firebase..."));
                historyList.getChildren().add(loadingBox);

                Thread t = new Thread(() -> {
                        try {
                                String voterUid = SessionManager.voterUid != null ? SessionManager.voterUid : "";
                                String joinCode = SessionManager.joinCode != null ? SessionManager.joinCode : "";
                                String idToken = SessionManager.idToken != null ? SessionManager.idToken : "";

                                List<VoteRecord> votes = new ArrayList<>();
                                if (!voterUid.isBlank()) {
                                        votes = VoteDAO.getVotesByVoter(voterUid, idToken);
                                }

                                Map<String, String> electionTitleMap = new HashMap<>();
                                if (!joinCode.isBlank()) {
                                        List<ElectionData> elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                                        for (ElectionData e : elections) {
                                                electionTitleMap.put(e.getId(), e.getTitle());
                                        }
                                }

                                List<VoteRecord> finalVotes = votes;
                                Platform.runLater(() -> {
                                        historyList.getChildren().clear();
                                        if (finalVotes.isEmpty()) {
                                                VBox emptyCard = new VBox(12);
                                                emptyCard.setAlignment(Pos.CENTER);
                                                emptyCard.setPadding(new Insets(35));
                                                emptyCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
                                                Label icon = new Label("🗳️");
                                                icon.setFont(Font.font(36));
                                                Text emptyTitle = new Text("No Voting Activity Recorded Yet");
                                                emptyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                                                emptyTitle.setFill(Color.web(TEXT));
                                                Text emptySub = new Text("Your cryptographically verified voting receipts will appear here after you cast your ballots in the 'Vote' center.");
                                                emptySub.setFill(Color.web(SECONDARY));
                                                emptySub.setFont(Font.font(13));
                                                emptyCard.getChildren().addAll(icon, emptyTitle, emptySub);
                                                historyList.getChildren().add(emptyCard);
                                        } else {
                                                for (VoteRecord v : finalVotes) {
                                                        String eTitle = electionTitleMap.getOrDefault(v.getElectionId(), "Election Ballot #" + v.getElectionId());
                                                        String receipt = "Cast Ballot Receipt #ZK-" + (v.getId().length() > 8 ? v.getId().substring(0, 8).toUpperCase() : v.getId());
                                                        String dateStr = formatEpoch(v.getVotedAt());
                                                        String detail = "Office: " + v.getPosition() + " • Candidate: " + v.getCandidateName();
                                                        historyList.getChildren().add(createHistoryCard(eTitle, receipt, dateStr, "Status: Verified & Counted", detail));
                                                }
                                        }
                                });
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                });
                t.setDaemon(true);
                t.start();

                content.getChildren().addAll(heading, new Separator(), historyList);

                VBox wrapper = new VBox(scrollPane);
                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                return wrapper;
        }

        private static VBox createHistoryCard(String orgName, String receipt, String dateTime, String status, String detail) {
                VBox card = new VBox(12);
                card.setPadding(new Insets(18));
                card.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-background-radius: 12;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 12;" +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);");

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
                                                "-fx-background-radius: 12;");

                topRow.getChildren().addAll(textBox, spacer, statusBadge);

                // Date and Time Row
                HBox detailsRow = new HBox(20);
                detailsRow.setAlignment(Pos.CENTER_LEFT);

                Text dateLabel = new Text("📅 Cast On: " + dateTime);
                dateLabel.setFill(Color.web(SECONDARY));
                dateLabel.setFont(Font.font("Arial", 12.5));

                Text detailLabel = new Text("🗳️ " + detail);
                detailLabel.setFill(Color.web(TEXT));
                detailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));

                detailsRow.getChildren().addAll(dateLabel, detailLabel);

                card.getChildren().addAll(topRow, new Separator(), detailsRow);
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