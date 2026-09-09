package com.electrovotesuperx.view.AIAssistantView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.OfflineDAO.ElectionStatsDAO;
import com.electrovotesuperx.service.AIGovernanceService;
import com.electrovotesuperx.view.Page;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AI Election Assistant — Full chat UI powered by Gemini 1.5 Flash.
 *
 * Features:
 *  - Chat history with message bubbles (user = blue/right, AI = white/left)
 *  - Six suggestion chips for common admin queries
 *  - Live DB context (turnout %, election counts, risk flags) auto-injected
 *  - Typing indicator while waiting for Gemini response
 *  - Graceful error handling with fallback offline analysis
 */
public class AIGovernanceAssistant implements Page {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final List<String> SUGGESTIONS = List.of(
            "How many members haven't voted?",
            "Which election had the highest turnout?",
            "Show me elections with declining participation.",
            "Generate a turnout report.",
            "Why was turnout lower this year?",
            "Are there any risk flags today?"
    );

    private final ElectionStatsDAO statsDAO = new ElectionStatsDAO();

    @Override
    public Scene getScene(Runnable backCallback) {

        // =====================================================
        // ROOT
        // =====================================================
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0F172A;");

        // =====================================================
        // HEADER BAR
        // =====================================================
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 18, 24));
        header.setStyle(
                "-fx-background-color: #1E293B;" +
                "-fx-border-color: transparent transparent #334155 transparent;" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label aiIcon = new Label("🤖");
        aiIcon.setStyle("-fx-font-size: 28px;");

        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("AI Election Assistant");
        titleLabel.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: 800;" +
                "-fx-text-fill: #F1F5F9;"
        );
        Label subtitleLabel = new Label("Ask anything about your elections • Powered by Gemini AI");
        subtitleLabel.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #CBD5E1;" +
                "-fx-font-size: 13px; -fx-font-weight: 600;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
                "-fx-background-color: #475569; -fx-text-fill: #F1F5F9;" +
                "-fx-font-size: 13px; -fx-font-weight: 600;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #CBD5E1;" +
                "-fx-font-size: 13px; -fx-font-weight: 600;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16;"
        ));
        backBtn.setOnAction(e -> backCallback.run());

        header.getChildren().addAll(aiIcon, titleBox, headerSpacer, backBtn);

        // =====================================================
        // CHAT HISTORY (scrollable VBox of message bubbles)
        // =====================================================
        VBox chatBox = new VBox(12);
        chatBox.setPadding(new Insets(20, 24, 20, 24));
        chatBox.setFillWidth(true);
        chatBox.setStyle("-fx-background-color: #0F172A;");

        ScrollPane chatScroll = new ScrollPane(chatBox);
        chatScroll.setFitToWidth(true);
        chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScroll.setStyle(
                "-fx-background: #0F172A; -fx-background-color: #0F172A;" +
                "-fx-border-color: transparent;"
        );
        VBox.setVgrow(chatScroll, Priority.ALWAYS);

        // Welcome message
        addAiMessage(chatBox, chatScroll,
                "👋 Hello, " + (SessionManager.officerName != null ? SessionManager.officerName : "Officer") +
                "! I'm your AI Election Assistant.\n\n" +
                "I have access to your live election data — " +
                "ask me about turnout, members who haven't voted, participation trends, or generate reports.\n\n" +
                "What would you like to know?");

        // =====================================================
        // SUGGESTION CHIPS
        // =====================================================
        FlowPane chipsPane = new FlowPane(8, 8);
        chipsPane.setPadding(new Insets(0, 24, 12, 24));
        chipsPane.setStyle("-fx-background-color: #0F172A;");

        TextField inputRef[] = { null }; // reference filled below

        for (String suggestion : SUGGESTIONS) {
            Button chip = new Button(suggestion);
            chip.setStyle(
                    "-fx-background-color: #1E293B;" +
                    "-fx-text-fill: #94A3B8;" +
                    "-fx-font-size: 12px;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #334155;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 6 14;"
            );
            chip.setOnMouseEntered(e -> chip.setStyle(
                    "-fx-background-color: #334155;" +
                    "-fx-text-fill: #E2E8F0;" +
                    "-fx-font-size: 12px;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #4B5563;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 6 14;"
            ));
            chip.setOnMouseExited(e -> chip.setStyle(
                    "-fx-background-color: #1E293B;" +
                    "-fx-text-fill: #94A3B8;" +
                    "-fx-font-size: 12px;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #334155;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 6 14;"
            ));
            chip.setOnAction(e -> {
                if (inputRef[0] != null) {
                    inputRef[0].setText(suggestion);
                    sendMessage(suggestion, chatBox, chatScroll, inputRef[0]);
                }
            });
            chipsPane.getChildren().add(chip);
        }

        // =====================================================
        // INPUT BAR
        // =====================================================
        HBox inputBar = new HBox(12);
        inputBar.setPadding(new Insets(14, 24, 18, 24));
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setStyle(
                "-fx-background-color: #1E293B;" +
                "-fx-border-color: #334155 transparent transparent transparent;" +
                "-fx-border-width: 1 0 0 0;"
        );

        TextField input = new TextField();
        inputRef[0] = input;
        input.setPromptText("Ask about your election data...");
        input.setPrefHeight(44);
        input.setStyle(
                "-fx-background-color: #0F172A;" +
                "-fx-text-fill: #F1F5F9;" +
                "-fx-prompt-text-fill: #64748B;" +
                "-fx-border-color: #334155;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 13.5px;" +
                "-fx-padding: 0 14;"
        );
        HBox.setHgrow(input, Priority.ALWAYS);

        Button sendBtn = new Button("Send ↑");
        sendBtn.setPrefHeight(44);
        sendBtn.setPrefWidth(90);
        sendBtn.setStyle(
                "-fx-background-color: #3B82F6;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.35), 10, 0, 0, 2);"
        );
        sendBtn.setOnMouseEntered(e -> sendBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.45), 12, 0, 0, 3);"
        ));
        sendBtn.setOnMouseExited(e -> sendBtn.setStyle(
                "-fx-background-color: #3B82F6;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.35), 10, 0, 0, 2);"
        ));

        Runnable submitAction = () -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                sendMessage(text, chatBox, chatScroll, input);
            }
        };
        sendBtn.setOnAction(e -> submitAction.run());
        input.setOnAction(e -> submitAction.run());

        inputBar.getChildren().addAll(input, sendBtn);

        // =====================================================
        // ASSEMBLE
        // =====================================================
        VBox mainArea = new VBox();
        VBox.setVgrow(chatScroll, Priority.ALWAYS);
        mainArea.getChildren().addAll(chatScroll, chipsPane, inputBar);
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        root.setTop(header);
        root.setCenter(mainArea);

        return new Scene(root, 1280, 760);
    }

    // =========================================================
    // SEND MESSAGE — runs AI call on background thread
    // =========================================================

    private void sendMessage(String text, VBox chatBox, ScrollPane chatScroll, TextField input) {
        input.clear();
        addUserMessage(chatBox, chatScroll, text);

        // Typing indicator
        VBox typingBubble = addTypingIndicator(chatBox, chatScroll);

        // Call Gemini on a background thread
        Thread thread = new Thread(() -> {
            ElectionStatsDAO.ElectionContext context = statsDAO.getContext();
            String response = AIGovernanceService.chat(text, context);

            Platform.runLater(() -> {
                chatBox.getChildren().remove(typingBubble);

                if (response != null && !response.isBlank()) {
                    addAiMessage(chatBox, chatScroll, response);
                } else {
                    // Offline fallback — answer directly from DB context
                    addAiMessage(chatBox, chatScroll, generateOfflineAnswer(text, context));
                }
                scrollToBottom(chatScroll);
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    // =========================================================
    // ADD USER BUBBLE (blue, right-aligned)
    // =========================================================

    private void addUserMessage(VBox chatBox, ScrollPane scroll, String text) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(520);
        bubble.setStyle(
                "-fx-background-color: #3B82F6;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13.5px;" +
                "-fx-background-radius: 18 18 4 18;" +
                "-fx-padding: 12 16;"
        );

        Label time = new Label(LocalTime.now().format(TIME_FMT));
        time.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        VBox msgBox = new VBox(3, bubble, time);
        msgBox.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(msgBox);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(2, 0, 2, 60));

        chatBox.getChildren().add(row);
        scrollToBottom(scroll);
    }

    // =========================================================
    // ADD AI BUBBLE (white/card, left-aligned)
    // =========================================================

    private void addAiMessage(VBox chatBox, ScrollPane scroll, String text) {
        Label robotLabel = new Label("🤖");
        robotLabel.setStyle("-fx-font-size: 20px; -fx-padding: 4 0 0 0;");

        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(580);
        bubble.setTextAlignment(TextAlignment.LEFT);
        bubble.setStyle(
                "-fx-background-color: #1E293B;" +
                "-fx-text-fill: #E2E8F0;" +
                "-fx-font-size: 13.5px;" +
                "-fx-background-radius: 4 18 18 18;" +
                "-fx-padding: 12 16;" +
                "-fx-border-color: #334155;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 4 18 18 18;" +
                "-fx-line-spacing: 2;"
        );

        Label time = new Label(LocalTime.now().format(TIME_FMT));
        time.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        VBox msgBox = new VBox(3, bubble, time);
        msgBox.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(10, robotLabel, msgBox);
        row.setAlignment(Pos.TOP_LEFT);
        row.setPadding(new Insets(2, 60, 2, 0));

        chatBox.getChildren().add(row);
        scrollToBottom(scroll);
    }

    // =========================================================
    // TYPING INDICATOR (animated dots)
    // =========================================================

    private VBox addTypingIndicator(VBox chatBox, ScrollPane scroll) {
        Label robotLabel = new Label("🤖");
        robotLabel.setStyle("-fx-font-size: 20px; -fx-padding: 4 0 0 0;");

        Label dots = new Label("● ● ●");
        dots.setStyle(
                "-fx-background-color: #1E293B;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 4 18 18 18;" +
                "-fx-padding: 12 16;" +
                "-fx-border-color: #334155;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 4 18 18 18;"
        );

        // Simple pulse animation
        javafx.animation.FadeTransition pulse = new javafx.animation.FadeTransition(
                Duration.millis(600), dots);
        pulse.setFromValue(0.3);
        pulse.setToValue(1.0);
        pulse.setCycleCount(javafx.animation.FadeTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        HBox row = new HBox(10, robotLabel, dots);
        row.setAlignment(Pos.TOP_LEFT);
        row.setPadding(new Insets(2, 60, 2, 0));

        VBox wrapper = new VBox(row);
        chatBox.getChildren().add(wrapper);
        scrollToBottom(scroll);

        return wrapper;
    }

    // =========================================================
    // SCROLL TO BOTTOM
    // =========================================================

    private void scrollToBottom(ScrollPane scroll) {
        PauseTransition delay = new PauseTransition(Duration.millis(80));
        delay.setOnFinished(e -> scroll.setVvalue(1.0));
        delay.play();
    }

    // =========================================================
    // OFFLINE FALLBACK ANSWER (when API call fails)
    // =========================================================

    private String generateOfflineAnswer(String question, ElectionStatsDAO.ElectionContext ctx) {
        String q = question.toLowerCase();

        if (q.contains("haven't voted") || q.contains("not voted") || q.contains("didn't vote")) {
            int notVoted = ctx.totalMembers() - ctx.totalVoted();
            return String.format(
                    "📊 **Members Who Haven't Voted**\n\n" +
                    "• Total enrolled members: %d\n" +
                    "• Votes cast: %d\n" +
                    "• Members not yet voted: **%d** (%.1f%% of enrolled)\n\n" +
                    "Consider sending reminders to increase turnout.",
                    ctx.totalMembers(), ctx.totalVoted(), notVoted,
                    ctx.totalMembers() == 0 ? 0.0 : (notVoted * 100.0 / ctx.totalMembers()));
        }

        if (q.contains("highest turnout") || q.contains("best turnout")) {
            var best = ctx.perElection().stream()
                    .max(java.util.Comparator.comparingDouble(
                            ElectionStatsDAO.ElectionStat::turnoutPct))
                    .orElse(null);
            if (best != null) {
                return String.format(
                        "🏆 **Highest Turnout Election**\n\n" +
                        "• Election: %s — %s\n" +
                        "• Enrolled: %d  |  Voted: %d\n" +
                        "• Turnout: **%.1f%%**",
                        best.electionId(), best.name(),
                        best.enrolled(), best.voted(), best.turnoutPct());
            }
        }

        if (q.contains("turnout report") || q.contains("generate a report")) {
            StringBuilder sb = new StringBuilder("📋 **Election Turnout Report**\n\n");
            sb.append(String.format("Overall: %d / %d voted (%.1f%%)\n\n",
                    ctx.totalVoted(), ctx.totalMembers(), ctx.overallTurnoutPct()));
            for (var stat : ctx.perElection()) {
                sb.append(String.format("• %s — %s: %.1f%% (%d/%d)\n",
                        stat.name(), stat.status(), stat.turnoutPct(),
                        stat.voted(), stat.enrolled()));
            }
            return sb.toString();
        }

        if (q.contains("risk") || q.contains("flag")) {
            return String.format(
                    "⚠️ **Today's Risk Flags**\n\n" +
                    "Risk events logged today: **%d**\n\n" +
                    "Check the Audit Log section for detailed breakdown per voter and election.",
                    ctx.todayRiskFlags());
        }

        // Generic stats answer
        return String.format(
                "📊 **Current Election Overview**\n\n" +
                "• Total Elections: %d (OPEN: %d, DRAFT: %d, CLOSED: %d)\n" +
                "• Total Members Enrolled: %d\n" +
                "• Votes Cast: %d\n" +
                "• Overall Turnout: %.1f%%\n" +
                "• Today's Risk Flags: %d\n\n" +
                "_(AI API unavailable — showing live data from local database)_",
                ctx.totalElections(), ctx.openElections(),
                ctx.draftElections(), ctx.closedElections(),
                ctx.totalMembers(), ctx.totalVoted(),
                ctx.overallTurnoutPct(), ctx.todayRiskFlags());
    }
}
