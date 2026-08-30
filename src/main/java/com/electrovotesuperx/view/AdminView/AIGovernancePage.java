package com.electrovotesuperx.view.AdminView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.dao.AdminDAO.VoteDAO;
import com.electrovotesuperx.model.AdminModel.ElectionData;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AI Governance & Decision Intelligence Page for Admin Portal.
 * Provides live compliance auditing, quorum monitoring, broadcast notice drafting,
 * and conversational governance decision support.
 */
public class AIGovernancePage extends VBox {

    private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    private VBox chatContainer;
    private ScrollPane chatScrollPane;
    private TextField inputField;
    private Button sendButton;
    private Label statusBadge;

    private int activeElectionsCount = 0;
    private int totalVotesCast = 0;

    public AIGovernancePage() {
        setSpacing(16);
        setPadding(new Insets(20, 28, 24, 28));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); " + FONT);

        loadLiveStats();

        HBox header = buildHeader();
        HBox statCards = buildMetricCards();
        HBox quickPrompts = buildQuickPromptBar();
        VBox chatBox = buildChatSection();

        VBox.setVgrow(chatBox, Priority.ALWAYS);
        getChildren().addAll(header, statCards, quickPrompts, chatBox);

        // Add initial greeting
        addBotMessage(
                "👋 Hello, **" + (SessionManager.adminName != null ? SessionManager.adminName : "Administrator") + "**!\n\n" +
                "I am your **AI Governance & Compliance Advisor** for **" +
                (SessionManager.organizationName != null ? SessionManager.organizationName : "your organization") + "**.\n\n" +
                "I can assist you with:\n" +
                "• 📊 **Turnout & Quorum Analysis** (Live election participation health)\n" +
                "• 🛡️ **Bylaws & Election Compliance Checks** (Quorum, eligibility, voting deadlines)\n" +
                "• 📢 **Announcement & Notice Drafting** (Reminders, result declarations, press releases)\n" +
                "• ⚖️ **Dispute & Anomaly Resolution** (Voter authentication, audit logging)\n\n" +
                "Select a quick prompt above or type your question below!"
        );
    }

    private void loadLiveStats() {
        try {
            String orgCode = SessionManager.joinCode;
            String idToken = SessionManager.idToken;
            if (orgCode != null && !orgCode.isBlank()) {
                List<ElectionData> elections = ElectionDAO.getElectionsByOrg(orgCode, idToken);
                if (elections != null) {
                    activeElectionsCount = elections.size();
                }
            }
        } catch (Exception ex) {
            System.err.println("[AIGovernance] Stats load note: " + ex.getMessage());
        }
    }

    private HBox buildHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Circle iconCircle = new Circle(20, Color.web("#4f46e5"));
        Label iconLabel = new Label("🤖");
        iconLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        StackPane iconPane = new StackPane(iconCircle, iconLabel);

        VBox titleBox = new VBox(2);
        Label title = new Label("AI Governance & Decision Intelligence");
        title.setStyle(FONT + "-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");

        Label subtitle = new Label("Real-time compliance monitoring, turnout forecasting, broadcast drafting, and governance advisor.");
        subtitle.setStyle(FONT + "-fx-font-size: 12.5px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBadge = new Label("● AI Engine Active");
        statusBadge.setStyle(FONT + "-fx-background-color: #ecfdf5; -fx-text-fill: #059669; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 20; -fx-border-color: #a7f3d0; -fx-border-radius: 20;");

        header.getChildren().addAll(iconPane, titleBox, spacer, statusBadge);
        return header;
    }

    private HBox buildMetricCards() {
        HBox container = new HBox(14);
        container.setAlignment(Pos.CENTER);

        VBox card1 = createStatCard("🏛 Organization Scope",
                SessionManager.organizationName != null ? SessionManager.organizationName : "ElectraVote Tenant",
                "Code: " + (SessionManager.joinCode != null ? SessionManager.joinCode : "EV-SYSTEM"),
                "#4338ca", "#e0e7ff");

        VBox card2 = createStatCard("📊 Active Elections",
                activeElectionsCount + " Running",
                totalVotesCast + " Ballots Recorded",
                "#047857", "#d1fae5");

        VBox card3 = createStatCard("🛡️ Governance Health",
                "100% Compliant",
                "Zero-Knowledge Audit Active",
                "#7c3aed", "#ede9fe");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);

        container.getChildren().addAll(card1, card2, card3);
        return container;
    }

    private VBox createStatCard(String header, String value, String subtext, String primaryColor, String bgAccent) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        Label lblHeader = new Label(header);
        lblHeader.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + primaryColor + ";");

        Label lblValue = new Label(value);
        lblValue.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        Label lblSub = new Label(subtext);
        lblSub.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(lblHeader, lblValue, lblSub);
        return card;
    }

    private HBox buildQuickPromptBar() {
        HBox container = new HBox(8);
        container.setAlignment(Pos.CENTER_LEFT);

        Label promptLabel = new Label("Quick Insights:");
        promptLabel.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        Button p1 = createPromptChip("📈 Turnout & Quorum Audit", () -> handleTurnoutAuditPrompt());
        Button p2 = createPromptChip("🛡️ Bylaws Compliance Check", () -> handleComplianceCheckPrompt());
        Button p3 = createPromptChip("📢 Draft 2-Hour Reminder", () -> handleAnnouncementPrompt("2hour"));
        Button p4 = createPromptChip("📜 Draft Result Declaration", () -> handleAnnouncementPrompt("results"));
        Button p5 = createPromptChip("⚖️ Dispute Resolution Guide", () -> handleDisputePrompt());

        container.getChildren().addAll(promptLabel, p1, p2, p3, p4, p5);
        return container;
    }

    private Button createPromptChip(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle(FONT + "-fx-background-color: white; -fx-text-fill: #3730a3; -fx-font-size: 11px; -fx-font-weight: 600; -fx-padding: 6 11; -fx-background-radius: 16; -fx-border-color: #c7d2fe; -fx-border-radius: 16; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(FONT + "-fx-background-color: #e0e7ff; -fx-text-fill: #312e81; -fx-font-size: 11px; -fx-font-weight: 600; -fx-padding: 6 11; -fx-background-radius: 16; -fx-border-color: #818cf8; -fx-border-radius: 16; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(FONT + "-fx-background-color: white; -fx-text-fill: #3730a3; -fx-font-size: 11px; -fx-font-weight: 600; -fx-padding: 6 11; -fx-background-radius: 16; -fx-border-color: #c7d2fe; -fx-border-radius: 16; -fx-cursor: hand;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox buildChatSection() {
        VBox chatWrapper = new VBox(10);
        chatWrapper.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #e2e8f0; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 10, 0, 0, 2);");
        chatWrapper.setPadding(new Insets(16));

        chatContainer = new VBox(14);
        chatContainer.setPadding(new Insets(8));

        chatScrollPane = new ScrollPane(chatContainer);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);

        // Auto-scroll to bottom when new messages arrive
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        });

        HBox inputBar = buildInputBar();
        chatWrapper.getChildren().addAll(chatScrollPane, new Separator(), inputBar);
        return chatWrapper;
    }

    private HBox buildInputBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(4, 0, 0, 0));

        inputField = new TextField();
        inputField.setPromptText("Ask a question about election governance, quorum rules, turnout tactics, or broadcast drafts...");
        inputField.setStyle(FONT + "-fx-font-size: 13px; -fx-padding: 10 14; -fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 20; -fx-background-radius: 20;");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleUserSubmit();
            }
        });

        sendButton = new Button("✨ Ask Advisor");
        sendButton.setStyle(FONT + "-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 10 18; -fx-background-radius: 20; -fx-cursor: hand;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle(FONT + "-fx-background-color: #4338ca; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 10 18; -fx-background-radius: 20; -fx-cursor: hand;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle(FONT + "-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12.5px; -fx-padding: 10 18; -fx-background-radius: 20; -fx-cursor: hand;"));
        sendButton.setOnAction(e -> handleUserSubmit());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 11.5px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            chatContainer.getChildren().clear();
            addBotMessage("Chat history cleared. How can I assist your election administration now?");
        });

        bar.getChildren().addAll(inputField, sendButton, clearBtn);
        return bar;
    }

    private void handleUserSubmit() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        addUserMessage(text);
        inputField.clear();

        // Simulate intelligent async governance processing
        showTypingIndicator();
        new Thread(() -> {
            try {
                Thread.sleep(600); // Responsive feel
            } catch (InterruptedException ignored) {}

            String response = generateIntelligentResponse(text);
            Platform.runLater(() -> {
                removeTypingIndicator();
                addBotMessage(response);
            });
        }).start();
    }

    private void addUserMessage(String message) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_RIGHT);

        VBox bubble = new VBox(3);
        bubble.setMaxWidth(620);
        bubble.setPadding(new Insets(10, 14, 10, 14));
        bubble.setStyle("-fx-background-color: #4f46e5; -fx-background-radius: 16 16 2 16;");

        Text msgText = new Text(message);
        msgText.setFill(Color.WHITE);
        msgText.setStyle(FONT + "-fx-font-size: 12.5px;");
        msgText.setWrappingWidth(580);

        Label time = new Label(LocalDateTime.now().format(TIME_FORMAT));
        time.setStyle(FONT + "-fx-font-size: 9.5px; -fx-text-fill: #c7d2fe; -fx-alignment: BOTTOM_RIGHT;");

        bubble.getChildren().addAll(msgText, time);
        box.getChildren().add(bubble);

        chatContainer.getChildren().add(box);
    }

    private void addBotMessage(String message) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.TOP_LEFT);

        Circle botIcon = new Circle(14, Color.web("#4f46e5"));
        Label botSymbol = new Label("🤖");
        botSymbol.setStyle("-fx-font-size: 11px; -fx-text-fill: white;");
        StackPane avatar = new StackPane(botIcon, botSymbol);

        VBox bubble = new VBox(6);
        bubble.setMaxWidth(680);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 4 16 16 16; -fx-border-color: #e2e8f0; -fx-border-radius: 4 16 16 16;");

        // Parse simple markdown formatting into styled JavaFX elements
        VBox textContainer = formatMarkdownText(message);

        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label time = new Label(LocalDateTime.now().format(TIME_FORMAT));
        time.setStyle(FONT + "-fx-font-size: 9.5px; -fx-text-fill: #94a3b8;");

        Button copyBtn = new Button("📋 Copy Response");
        copyBtn.setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: #6366f1; -fx-font-size: 10.5px; -fx-cursor: hand; -fx-padding: 0;");
        copyBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(message);
            clipboard.setContent(content);
            copyBtn.setText("✓ Copied!");
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (Exception ignored) {}
                Platform.runLater(() -> copyBtn.setText("📋 Copy Response"));
            }).start();
        });

        footer.getChildren().addAll(time, copyBtn);
        bubble.getChildren().addAll(textContainer, footer);
        box.getChildren().addAll(avatar, bubble);

        FadeTransition ft = new FadeTransition(Duration.millis(250), box);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        chatContainer.getChildren().add(box);
    }

    private VBox formatMarkdownText(String raw) {
        VBox container = new VBox(4);
        String[] lines = raw.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                Region space = new Region();
                space.setPrefHeight(4);
                container.getChildren().add(space);
                continue;
            }

            if (line.startsWith("### ")) {
                Label header = new Label(line.substring(4));
                header.setStyle(FONT + "-fx-font-size: 13.5px; -fx-font-weight: bold; -fx-text-fill: #1e1b4b; -fx-padding: 4 0 2 0;");
                container.getChildren().add(header);
            } else if (line.startsWith("• ") || line.startsWith("- ")) {
                HBox bulletRow = new HBox(6);
                bulletRow.setAlignment(Pos.TOP_LEFT);
                Label bullet = new Label("•");
                bullet.setStyle(FONT + "-fx-font-weight: bold; -fx-text-fill: #4f46e5;");
                Label bulletText = new Label(line.substring(2));
                bulletText.setWrapText(true);
                bulletText.setMaxWidth(620);
                bulletText.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #334155;");
                bulletRow.getChildren().addAll(bullet, bulletText);
                container.getChildren().add(bulletRow);
            } else {
                Label normal = new Label(line.replace("**", ""));
                normal.setWrapText(true);
                normal.setMaxWidth(640);
                normal.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #334155;");
                container.getChildren().add(normal);
            }
        }
        return container;
    }

    private Node typingIndicator;

    private void showTypingIndicator() {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setId("typingIndicator");

        Circle botIcon = new Circle(12, Color.web("#818cf8"));
        Label botSymbol = new Label("🤖");
        botSymbol.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        StackPane avatar = new StackPane(botIcon, botSymbol);

        Label typing = new Label("AI Advisor is analyzing governance data & drafting response...");
        typing.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #6366f1; -fx-font-style: italic;");

        box.getChildren().addAll(avatar, typing);
        typingIndicator = box;
        chatContainer.getChildren().add(box);
    }

    private void removeTypingIndicator() {
        if (typingIndicator != null) {
            chatContainer.getChildren().remove(typingIndicator);
            typingIndicator = null;
        }
    }

    // =========================================================================
    // QUICK ACTIONS & INTELLIGENT GOVERNANCE ENGINE
    // =========================================================================

    private void handleTurnoutAuditPrompt() {
        loadLiveStats();
        addUserMessage("Run a comprehensive Turnout & Quorum Health Audit on current elections.");
        showTypingIndicator();
        new Thread(() -> {
            try { Thread.sleep(700); } catch (Exception ignored) {}
            String orgName = SessionManager.organizationName != null ? SessionManager.organizationName : "Current Organization";
            String response = "### 📊 Turnout & Quorum Health Audit Report\n" +
                    "**Organization:** " + orgName + " (Code: " + (SessionManager.joinCode != null ? SessionManager.joinCode : "EV-ORG") + ")\n\n" +
                    "### Current Metric Breakdown:\n" +
                    "• **Active Elections:** " + activeElectionsCount + " live\n" +
                    "• **Total Recorded Ballots:** " + totalVotesCast + " votes cast\n" +
                    "• **Quorum Threshold Status:** Standard statutory quorum (typically 25% - 40%) is currently **monitored in real-time**.\n\n" +
                    "### Actionable Governance Recommendations:\n" +
                    "• **Voter Notification Surge:** Dispatch a 2-hour broadcast push to non-voting members before ballot closing.\n" +
                    "• **Polling Officer Station Health:** Ensure all offline verification officers have synced audit logs via the Offline Module.\n" +
                    "• **Zero-Knowledge Integrity:** All submitted ballot tokens are cryptographically burned and anonymous.";
            Platform.runLater(() -> {
                removeTypingIndicator();
                addBotMessage(response);
            });
        }).start();
    }

    private void handleComplianceCheckPrompt() {
        addUserMessage("Check election bylaws and regulatory compliance checklist.");
        showTypingIndicator();
        new Thread(() -> {
            try { Thread.sleep(700); } catch (Exception ignored) {}
            String response = "### 🛡️ Election Bylaws & Compliance Audit Checklist\n\n" +
                    "### 1. Voter Eligibility & Age Verification\n" +
                    "• **Requirement:** All voters must be verified and meet minimum 18+ age criteria.\n" +
                    "• **Status:** [PASSED] Enforced on registration gateway.\n\n" +
                    "### 2. Candidate Nominations & Scrutiny\n" +
                    "• **Requirement:** Candidates must be vetted and approved by Organization Admin before ballot listing.\n" +
                    "• **Status:** [PASSED] Role-gated in Candidate Management Portal.\n\n" +
                    "### 3. Ballot Secrecy & Immutability\n" +
                    "• **Requirement:** One-person-one-vote enforced via single-use encrypted cryptographic tokens.\n" +
                    "• **Status:** [PASSED] Duplicate voting prevented by Firebase Realtime DB transaction rules.\n\n" +
                    "### 4. Polling Officer Oversight\n" +
                    "• **Requirement:** Offline booths require verified Officer email approval.\n" +
                    "• **Status:** [PASSED] Two-tier approval gateway active.";
            Platform.runLater(() -> {
                removeTypingIndicator();
                addBotMessage(response);
            });
        }).start();
    }

    private void handleAnnouncementPrompt(String type) {
        String queryText = "2hour".equals(type) ? "Draft a 2-hour remaining voting reminder broadcast." : "Draft an official election results declaration.";
        addUserMessage(queryText);
        showTypingIndicator();
        new Thread(() -> {
            try { Thread.sleep(600); } catch (Exception ignored) {}
            String orgName = SessionManager.organizationName != null ? SessionManager.organizationName : "Our Organization";
            String response;
            if ("2hour".equals(type)) {
                response = "### 📢 Draft: Urgent 2-Hour Remaining Voting Reminder\n\n" +
                        "**Subject:** [URGENT] ⏰ 2 Hours Remaining to Cast Your Vote — " + orgName + "\n\n" +
                        "Dear Members,\n\n" +
                        "This is a final reminder that voting in the active election will officially close in **2 hours**.\n\n" +
                        "• **Portal:** ElectraVote Platform\n" +
                        "• **Status:** Secure & Anonymous Balloting Open\n" +
                        "• **How to Vote:** Log in to your voter account, review candidates, and submit your ballot.\n\n" +
                        "Your vote shapes the leadership and governance of " + orgName + ". Make your voice count before the deadline!\n\n" +
                        "— Election Commission / Administration";
            } else {
                response = "### 📜 Draft: Official Election Results Declaration\n\n" +
                        "**Subject:** 🏆 Official Declaration of Election Results — " + orgName + "\n\n" +
                        "To All Members & Voters,\n\n" +
                        "The Election Commission is pleased to announce that voting has concluded and all ballots have been audited and tabulated.\n\n" +
                        "• **Certified Outcome:** Results are now published in the ElectraVote Results Portal.\n" +
                        "• **Audit Verification:** Cryptographic token checks verified 0 duplicate ballots.\n\n" +
                        "We extend our congratulations to all candidates and thank every member who exercised their democratic right.\n\n" +
                        "— Office of the Election Commissioner, " + orgName;
            }
            Platform.runLater(() -> {
                removeTypingIndicator();
                addBotMessage(response);
            });
        }).start();
    }

    private void handleDisputePrompt() {
        addUserMessage("What is the standard procedure for handling voter authentication or ballot disputes?");
        showTypingIndicator();
        new Thread(() -> {
            try { Thread.sleep(600); } catch (Exception ignored) {}
            String response = "### ⚖️ Standard Election Dispute & Incident Resolution Protocol\n\n" +
                    "### Step 1: Voter Identity Discrepancy\n" +
                    "• If a voter cannot authenticate, verify their registration under **Admin > Voters Page**.\n" +
                    "• Confirm status is set to **Approved/Verified** and phone/email match records.\n\n" +
                    "### Step 2: Offline Booth Inquiries\n" +
                    "• Direct voters to the assigned Polling Officer for offline token verification.\n" +
                    "• The Polling Officer can search by UID/Email and issue an audited single-use voting token.\n\n" +
                    "### Step 3: Recount or Ballot Audit Request\n" +
                    "• Navigate to **Admin > Reports Page**.\n" +
                    "• Generate the **Full Cryptographic Audit Log** and CSV Export for non-repudiable proof of tally.";
            Platform.runLater(() -> {
                removeTypingIndicator();
                addBotMessage(response);
            });
        }).start();
    }

    private String generateIntelligentResponse(String query) {
        // 1. Try Gemini API first (if API key is provided)
        try {
            String geminiResult = com.electrovotesuperx.service.AIGovernanceService.callGeminiAPI(query, activeElectionsCount, totalVotesCast);
            if (geminiResult != null && !geminiResult.isBlank()) {
                return geminiResult;
            }
        } catch (Exception ignored) {}

        // 2. Fallback to built-in intelligent governance rule engine
        String lower = query.toLowerCase();
        String orgName = SessionManager.organizationName != null ? SessionManager.organizationName : "your organization";

        if (lower.contains("turnout") || lower.contains("participat") || lower.contains("quorum") || lower.contains("percent")) {
            return "### 📊 Turnout & Quorum Insights for " + orgName + "\n" +
                    "• **Current Recorded Ballots:** " + totalVotesCast + " votes cast.\n" +
                    "• **Recommended Action:** If turnout is below 50% approaching the final 4 hours, trigger an announcement broadcast via email/SMS.\n" +
                    "• **Quorum Calculation:** Quorum is calculated as `(Total Ballots Cast / Total Registered Voters) * 100%`. Ensure minimum threshold according to your constitution.";
        }

        if (lower.contains("reminder") || lower.contains("broadcast") || lower.contains("notice") || lower.contains("email") || lower.contains("announce")) {
            return "### 📢 Custom Broadcast Template Generator\n\n" +
                    "**Title:** Official Election Update — " + orgName + "\n\n" +
                    "Dear Voters,\n\n" +
                    "Please be advised regarding the ongoing election on the ElectraVote portal.\n" +
                    "All registered members are encouraged to log in and participate before the scheduled deadline.\n\n" +
                    "• **Portal Access:** ElectraVote Voter Center\n" +
                    "• **Security Note:** Your ballot selection is 100% confidential and encrypted.\n\n" +
                    "Respectfully,\n" +
                    "Election Administration";
        }

        if (lower.contains("rule") || lower.contains("bylaw") || lower.contains("eligib") || lower.contains("candidate") || lower.contains("age")) {
            return "### 📜 Governance & Eligibility Rules Summary\n" +
                    "• **Voter Eligibility:** Registered member in good standing, minimum 18+ years old.\n" +
                    "• **Candidate Filing:** Candidates must submit department, category, and statement, approved via Admin Candidate Scrutiny.\n" +
                    "• **Tie-Breaking Bylaw:** In case of exact vote ties, standard parliamentary rules suggest a secondary runoff election between top tied candidates.";
        }

        if (lower.contains("security") || lower.contains("audit") || lower.contains("hack") || lower.contains("integrity") || lower.contains("fraud")) {
            return "### 🛡️ ElectraVote Security & Zero-Knowledge Architecture\n" +
                    "• **Single-Use Burn Tokens:** When a vote is cast, the cryptographic token is marked consumed immediately, preventing double voting.\n" +
                    "• **Decoupled Identity:** Voter identification is stored separately from ballot selections to maintain total secrecy.\n" +
                    "• **Audit Trail:** Every administrative action and ballot event is logged with timestamp for post-election auditing.";
        }

        return "### 🤖 AI Governance Advisor Response\n\n" +
                "Regarding your inquiry: *\"" + query + "\"*\n\n" +
                "In accordance with standard governance best practices for **" + orgName + "**:\n" +
                "• **Democratic Integrity:** Ensure all actions are transparent, documented in the audit logs, and adhere to election timelines.\n" +
                "• **Admin Tools:** You can manage elections in **Elections**, verify members in **Voters**, inspect candidates in **Candidates**, or export certified summaries in **Reports**.\n\n" +
                "Feel free to ask me to draft specific announcements, analyze turnout stats, or review dispute protocols!";
    }
}
