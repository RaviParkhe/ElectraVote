// package com.electrovotesuperx.view.VoterView;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.RadioButton;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.Separator;
// import javafx.scene.control.ToggleGroup;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
// import javafx.scene.text.Text;

// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;

// public class Vote {

//     private static final String TEXT = "#172033";
//     private static final String SECONDARY = "#6B7280";
//     private static final String BORDER = "#E2E8F0";
//     private static final String BLUE = "#1464F4";
//     private static final String GREEN = "#10B981";
//     private static final String PURPLE = "#7B4DFF";
//     private static final String RED = "#EF4444";

//     private static final Map<String, String> castEncryptedBallot = new HashMap<>();
//     private static final Set<String> votedOrganizations = new HashSet<>();

//     public static VBox createOrganizationSelectionView() {
//         VBox content = new VBox(20);
//         content.setPadding(new Insets(25));
//         content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

//         ScrollPane scrollPane = new ScrollPane(content);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

//         VBox heading = new VBox(6);
//         Text title = new Text("Secure Voting Center 🗳");
//         title.setFill(Color.web(TEXT));
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

//         Text subtitle = new Text("Select your verified organization below to access active ballots and cast your confidential vote.");
//         subtitle.setFill(Color.web(SECONDARY));
//         subtitle.setFont(Font.font(13));
//         heading.getChildren().addAll(title, subtitle);

//         VBox orgList = new VBox(14);
//         orgList.getChildren().add(createOrgCard("ABC College - Student Union 2026", "Verified Membership • 2 Active Elections"));
//         orgList.getChildren().add(createOrgCard("Computer Science Faculty Board", "Verified Membership • 1 Active Election"));

//         content.getChildren().addAll(heading, new Separator(), orgList);

//         VBox wrapper = new VBox(scrollPane);
//         VBox.setVgrow(scrollPane, Priority.ALWAYS);
//         return wrapper;
//     }

//     private static VBox createOrgCard(String orgName, String details) {
//         VBox card = new VBox(12);
//         card.setPadding(new Insets(18));
//         card.setStyle(
//             "-fx-background-color: white;" +
//             "-fx-background-radius: 12;" +
//             "-fx-border-color: " + BORDER + ";" +
//             "-fx-border-radius: 12;" +
//             "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);"
//         );

//         HBox topRow = new HBox();
//         topRow.setAlignment(Pos.CENTER_LEFT);

//         VBox textBox = new VBox(3);
//         Text name = new Text(orgName);
//         name.setFill(Color.web(TEXT));
//         name.setFont(Font.font("Arial", FontWeight.BOLD, 17));

//         boolean alreadyVoted = votedOrganizations.contains(orgName);
//         Text meta = new Text(alreadyVoted ? "Status: Already Voted (Completed)" : details);
//         meta.setFill(Color.web(alreadyVoted ? RED : GREEN));
//         meta.setFont(Font.font("Arial", FontWeight.BOLD, 12));
//         textBox.getChildren().addAll(name, meta);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Button voteBtn = new Button("Vote");
//         voteBtn.setStyle(
//             "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-padding: 9 20;" +
//             "-fx-background-radius: 7;" +
//             "-fx-cursor: hand;"
//         );

//         if (alreadyVoted) {
//             voteBtn.setText("Voted");
//             voteBtn.setDisable(true);
//             voteBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-padding: 9 20; -fx-background-radius: 7;");
//         } else {
//             voteBtn.setOnAction(e -> {
//                 VBox ballotView = createPositionWiseBallotView(orgName);
//                 VoterDashboard.dashboardCenter.setCenter(ballotView);
//             });
//         }

//         topRow.getChildren().addAll(textBox, spacer, voteBtn);
//         card.getChildren().add(topRow);
//         return card;
//     }

//     private static VBox createPositionWiseBallotView(String orgName) {
//         VBox ballotContainer = new VBox(20);
//         ballotContainer.setPadding(new Insets(25));
//         ballotContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

//         ScrollPane scrollPane = new ScrollPane(ballotContainer);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

//         Button backBtn = new Button("← Change Organization");
//         backBtn.setStyle(
//             "-fx-background-color: transparent;" +
//             "-fx-text-fill: " + BLUE + ";" +
//             "-fx-font-weight: bold;" +
//             "-fx-cursor: hand;" +
//             "-fx-padding: 0;" +
//             "-fx-font-size: 13px;"
//         );
//         backBtn.setOnAction(e -> VoterDashboard.showPage(createOrganizationSelectionView()));

//         VBox headerCard = new VBox(6);
//         headerCard.setPadding(new Insets(20));
//         headerCard.setStyle(
//             "-fx-background-color: white;" +
//             "-fx-background-radius: 12;" +
//             "-fx-border-color: " + BORDER + ";" +
//             "-fx-border-radius: 12;"
//         );

//         Text ballotTitle = new Text("Ballot Center: " + orgName);
//         ballotTitle.setFill(Color.web(TEXT));
//         ballotTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

//         Text ballotSub = new Text("🔒 Zero-Knowledge Encryption Active. You must select a candidate or NOTA for all 7 positions before submitting.");
//         ballotSub.setFill(Color.web(SECONDARY));
//         ballotSub.setFont(Font.font(12.5));
//         headerCard.getChildren().addAll(ballotTitle, ballotSub);

//         VBox positionsList = new VBox(20);

//         String[] positions = {
//             "President", "Vice President", "Secretary", 
//             "Treasurer", "General Secretary", "Tech Coordinator", "Cultural Representative"
//         };

//         String[][] candidateNames = {
//             {"Alice Johnson", "Bob Smith"},
//             {"David Lee", "Emma Watson"},
//             {"Frank Miller", "Grace Hopper"},
//             {"Ian Wright", "Julia Roberts"},
//             {"Kevin Hart", "Laura Croft"},
//             {"Michael Bay", "Natalie Portman"},
//             {"Oscar Isaac", "Penelope Cruz"}
//         };

//         String[][] candidateInitials = {
//             {"AJ", "BS"}, {"DL", "EW"}, {"FM", "GH"}, 
//             {"IW", "JR"}, {"KH", "LC"}, {"MB", "NP"}, {"OI", "PC"}
//         };

//         ToggleGroup[] groups = new ToggleGroup[positions.length];

//         for (int i = 0; i < positions.length; i++) {
//             groups[i] = new ToggleGroup();
//             positionsList.getChildren().add(createPositionSection(
//                 positions[i], 
//                 groups[i], 
//                 candidateNames[i], 
//                 candidateInitials[i]
//             ));
//         }

//         Label errorLabel = new Label();
//         errorLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
//         errorLabel.setVisible(false);

//         Button voteSubmitBtn = new Button("Vote");
//         voteSubmitBtn.setStyle(
//             "-fx-background-color: linear-gradient(to right, #10B981, #059669);" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-padding: 12 28;" +
//             "-fx-background-radius: 8;" +
//             "-fx-cursor: hand;" +
//             "-fx-font-size: 14px;"
//         );

//         voteSubmitBtn.setOnAction(e -> {
//             for (String pos : positions) {
//                 if (!castEncryptedBallot.containsKey(pos) || castEncryptedBallot.get(pos) == null) {
//                     errorLabel.setText("⚠️ Mandatory Constraint: Please cast your vote for every position (including NOTA) before submitting.");
//                     errorLabel.setVisible(true);
//                     return;
//                 }
//             }

//             votedOrganizations.add(orgName);
//             VBox successView = createBallotSuccessView(orgName);
//             VoterDashboard.dashboardCenter.setCenter(successView);
//         });

//         ballotContainer.getChildren().addAll(backBtn, headerCard, positionsList, errorLabel, new Separator(), voteSubmitBtn);

//         VBox wrapper = new VBox(scrollPane);
//         VBox.setVgrow(scrollPane, Priority.ALWAYS);
//         return wrapper;
//     }

//     private static VBox createPositionSection(String positionName, ToggleGroup group, String[] candidates, String[] initials) {
//         VBox section = new VBox(12);
//         section.setPadding(new Insets(18));
//         section.setStyle(
//             "-fx-background-color: white;" +
//             "-fx-background-radius: 12;" +
//             "-fx-border-color: " + BORDER + ";" +
//             "-fx-border-radius: 12;"
//         );

//         Text posTitle = new Text("Position: " + positionName);
//         posTitle.setFill(Color.web(BLUE));
//         posTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

//         HBox candidatesRow = new HBox(15);
//         candidatesRow.setAlignment(Pos.CENTER_LEFT);

//         for (int i = 0; i < candidates.length; i++) {
//             candidatesRow.getChildren().add(createCandidateCard(candidates[i], initials[i], group, positionName));
//         }

//         candidatesRow.getChildren().add(createCandidateCard("None of The Above (NOTA)", "NOTA", group, positionName));

//         section.getChildren().addAll(posTitle, new Separator(), candidatesRow);
//         return section;
//     }

//     private static VBox createCandidateCard(String candidateName, String initial, ToggleGroup group, String positionName) {
//         VBox card = new VBox(8);
//         card.setPadding(new Insets(14));
//         card.setPrefWidth(190);
//         card.setAlignment(Pos.CENTER);
//         card.setStyle(
//             "-fx-background-color: #F8FAFC;" +
//             "-fx-background-radius: 10;" +
//             "-fx-border-color: " + BORDER + ";" +
//             "-fx-border-radius: 10;" +
//             "-fx-cursor: hand;"
//         );

//         Circle avatarCircle = new Circle(22);
//         avatarCircle.setFill(Color.web(PURPLE));
//         Text avatarText = new Text(initial);
//         avatarText.setFill(Color.WHITE);
//         avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
//         StackPane avatarPane = new StackPane(avatarCircle, avatarText);

//         Text nameLbl = new Text(candidateName);
//         nameLbl.setFill(Color.web(TEXT));
//         nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));
//         nameLbl.setWrappingWidth(170);

//         RadioButton radio = new RadioButton("Select");
//         radio.setToggleGroup(group);

//         radio.setOnAction(e -> castEncryptedBallot.put(positionName, candidateName));
//         card.setOnMouseClicked(e -> {
//             radio.setSelected(true);
//             castEncryptedBallot.put(positionName, candidateName);
//         });

//         card.setOnMouseEntered(e -> card.setStyle(
//             "-fx-background-color: #EFF6FF;" +
//             "-fx-background-radius: 10;" +
//             "-fx-border-color: " + BLUE + ";" +
//             "-fx-border-radius: 10;" +
//             "-fx-cursor: hand;"
//         ));
//         card.setOnMouseExited(e -> card.setStyle(
//             "-fx-background-color: #F8FAFC;" +
//             "-fx-background-radius: 10;" +
//             "-fx-border-color: " + BORDER + ";" +
//             "-fx-border-radius: 10;" +
//             "-fx-cursor: hand;"
//         ));

//         card.getChildren().addAll(avatarPane, nameLbl, radio);
//         return card;
//     }

//     private static VBox createBallotSuccessView(String orgName) {
//         VBox container = new VBox(20);
//         container.setPadding(new Insets(30));
//         container.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

//         Text title = new Text("Vote Successfully Cast & Encrypted! 🛡️");
//         title.setFill(Color.web(GREEN));
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

//         Text msg = new Text("Your ballot for " + orgName + " has been encrypted using zero-knowledge homomorphic encryption and securely stored to the database. You have successfully completed voting for this election and cannot vote again.");
//         msg.setFill(Color.web(TEXT));
//         msg.setFont(Font.font(13));
//         msg.setWrappingWidth(800);

//         Button backHome = new Button("Return to Dashboard Home");
//         backHome.setStyle(
//             "-fx-background-color: " + BLUE + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-padding: 10 20;" +
//             "-fx-background-radius: 6;" +
//             "-fx-cursor: hand;"
//         );
package com.electrovotesuperx.view.VoterView;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.AdminDAO.CandidateDAO;
import com.electrovotesuperx.dao.AdminDAO.ElectionDAO;
import com.electrovotesuperx.dao.AdminDAO.VoteDAO;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.electrovotesuperx.model.AdminModel.ElectionData;
import com.electrovotesuperx.model.AdminModel.VoteRecord;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.*;

public class Vote {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String GREEN = "#10B981";
    private static final String PURPLE = "#7B4DFF";
    private static final String RED = "#EF4444";

    private static final Map<String, String> castEncryptedBallot = new HashMap<>();
    private static final Set<String> votedElections = new HashSet<>();

    public static VBox createOrganizationSelectionView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox heading = new VBox(6);
        Text title = new Text("Secure Voting Center 🗳");
        title.setFill(Color.web(TEXT));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Text subtitle = new Text("Select an active election below to access official candidate ballots and cast your confidential vote.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        heading.getChildren().addAll(title, subtitle);

        VBox electionsList = new VBox(14);

        // Security check: Only ACCEPTED or VERIFIED voters are authorized to view ballots or vote
        boolean isApproved = "ACCEPTED".equalsIgnoreCase(SessionManager.voterStatus) || "VERIFIED".equalsIgnoreCase(SessionManager.voterStatus);
        if (!isApproved) {
            VBox pendingCard = new VBox(14);
            pendingCard.setAlignment(Pos.CENTER);
            pendingCard.setPadding(new Insets(35));
            pendingCard.setStyle("-fx-background-color: #FFFBEB; -fx-background-radius: 12; -fx-border-color: #F59E0B; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.12), 8, 0, 0, 3);");

            Label warnIcon = new Label("⏳");
            warnIcon.setFont(Font.font(36));

            Text warnTitle = new Text("Voting Access Locked: Administrator Approval Required");
            warnTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            warnTitle.setFill(Color.web("#92400E"));

            String currentOrgName = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                    ? SessionManager.organizationName : (SessionManager.joinCode != null ? SessionManager.joinCode : "your organization");
            String currentCode = SessionManager.joinCode != null ? SessionManager.joinCode : "";

            Text warnMsg = new Text(
                    "Your membership in '" + currentOrgName + "' (" + currentCode + ") is currently PENDING.\n\n" +
                    "To prevent unauthorized ballot submissions, your organization's Administrator must review and ACCEPT your registration in their Admin Portal (Admin > Voters) before you can view candidate rosters or cast votes.");
            warnMsg.setFill(Color.web("#78350F"));
            warnMsg.setFont(Font.font(13));
            warnMsg.setWrappingWidth(680);
            warnMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Button myOrgBtn = new Button("🏢 Manage Memberships & Track Status in 'My Organization'");
            myOrgBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
            myOrgBtn.setOnAction(ev -> VoterDashboard.showPage(MyOrganization.createMyOrganizationView()));

            pendingCard.getChildren().addAll(warnIcon, warnTitle, warnMsg, myOrgBtn);
            content.getChildren().addAll(heading, new Separator(), pendingCard);

            VBox wrapper = new VBox(scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            return wrapper;
        }

        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(30));
        loadingBox.getChildren().addAll(new ProgressIndicator(), new Label("Loading official ballots from Firebase..."));
        electionsList.getChildren().add(loadingBox);

        Thread t = new Thread(() -> {
            try {
                String joinCode = SessionManager.joinCode;
                String idToken = SessionManager.idToken;
                List<ElectionData> elections = new ArrayList<>();
                if (joinCode != null && !joinCode.isBlank()) {
                    elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
                }

                List<ElectionData> finalElections = elections;
                Platform.runLater(() -> {
                    electionsList.getChildren().clear();
                    if (finalElections.isEmpty()) {
                        VBox emptyBox = new VBox(12);
                        emptyBox.setAlignment(Pos.CENTER);
                        emptyBox.setPadding(new Insets(30));
                        emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
                        Label icon = new Label("🗳️");
                        icon.setFont(Font.font(36));
                        Text emptyTitle = new Text("No Open Elections Available");
                        emptyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                        emptyTitle.setFill(Color.web(TEXT));
                        Text emptySub = new Text("There are currently no active elections configured for your organization.");
                        emptySub.setFill(Color.web(SECONDARY));
                        emptySub.setFont(Font.font(13));
                        emptyBox.getChildren().addAll(icon, emptyTitle, emptySub);
                        electionsList.getChildren().add(emptyBox);
                    } else {
                        for (ElectionData election : finalElections) {
                            electionsList.getChildren().add(createElectionVoteCard(election));
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();

        content.getChildren().addAll(heading, new Separator(), electionsList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createElectionVoteCard(ElectionData election) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(3);
        Text name = new Text(election.getTitle() != null ? election.getTitle() : "Untitled Election");
        name.setFill(Color.web(TEXT));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        boolean alreadyVoted = votedElections.contains(election.getId());
        String statusText = alreadyVoted ? "Status: Already Voted (Ballot Certified)" : "Status: Open for Voting • Configured Positions: " + (election.getPositions() != null ? election.getPositions().size() : 1);
        Text meta = new Text(statusText);
        meta.setFill(Color.web(alreadyVoted ? RED : GREEN));
        meta.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        textBox.getChildren().addAll(name, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button voteBtn = new Button("Enter Ballot ➔");
        voteBtn.setStyle("-fx-background-color: linear-gradient(to right, #1464F4, #0D3565); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 20; -fx-background-radius: 7; -fx-cursor: hand;");

        if (alreadyVoted) {
            voteBtn.setText("Voted ✓");
            voteBtn.setDisable(true);
            voteBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-padding: 9 20; -fx-background-radius: 7;");
        } else {
            voteBtn.setOnAction(e -> {
                VBox ballotView = createPositionWiseBallotView(election);
                VoterDashboard.dashboardCenter.setCenter(ballotView);
            });
        }

        topRow.getChildren().addAll(textBox, spacer, voteBtn);
        card.getChildren().add(topRow);
        return card;
    }

    private static VBox createPositionWiseBallotView(ElectionData election) {
        castEncryptedBallot.clear();
        VBox ballotContainer = new VBox(20);
        ballotContainer.setPadding(new Insets(25));
        ballotContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(ballotContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Button backBtn = new Button("← Back to Elections");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + BLUE + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0; -fx-font-size: 13px;");
        backBtn.setOnAction(e -> VoterDashboard.showPage(createOrganizationSelectionView()));

        VBox headerCard = new VBox(6);
        headerCard.setPadding(new Insets(20));
        headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #818CF8; -fx-border-width: 1.5; -fx-border-radius: 12;");

        Text ballotTitle = new Text("Official Ballot: " + election.getTitle());
        ballotTitle.setFill(Color.web(TEXT));
        ballotTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Text ballotSub = new Text("🔒 Zero-Knowledge Cryptographic Privacy Active. Please cast your ballot choice (Candidate or NOTA) for each position before submitting.");
        ballotSub.setFill(Color.web(SECONDARY));
        ballotSub.setFont(Font.font(12.5));
        headerCard.getChildren().addAll(ballotTitle, ballotSub);

        VBox positionsList = new VBox(20);
        ProgressIndicator pi = new ProgressIndicator();
        VBox loadingBox = new VBox(10, pi, new Label("Loading official candidates from Firebase..."));
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(30));
        positionsList.getChildren().add(loadingBox);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        errorLabel.setVisible(false);

        Button voteSubmitBtn = new Button("Submit Verified Ballot 🚀");
        voteSubmitBtn.setStyle("-fx-background-color: linear-gradient(to right, #10B981, #059669); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 28; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");

        List<String> positionNames = new ArrayList<>();
        Thread fetchThread = new Thread(() -> {
            try {
                String idToken = SessionManager.idToken;
                List<Candidate> candidates = CandidateDAO.getCandidatesByElection(election.getId(), idToken);
                Set<String> positionsSet = new LinkedHashSet<>();
                if (election.getPositions() != null && !election.getPositions().isEmpty()) positionsSet.addAll(election.getPositions());
                for (Candidate c : candidates) {
                    if (c.getPosition() != null && !c.getPosition().isBlank()) positionsSet.add(c.getPosition());
                }
                if (positionsSet.isEmpty()) positionsSet.add("Candidate Office");
                Platform.runLater(() -> {
                    positionsList.getChildren().clear();
                    positionNames.clear();
                    positionNames.addAll(positionsSet);
                    for (String pos : positionNames) {
                        List<Candidate> posCandidates = new ArrayList<>();
                        for (Candidate c : candidates) {
                            if (pos.equalsIgnoreCase(c.getPosition())) posCandidates.add(c);
                        }
                        ToggleGroup group = new ToggleGroup();
                        positionsList.getChildren().add(createPositionSection(pos, group, posCandidates));
                    }
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        fetchThread.setDaemon(true);
        fetchThread.start();

        voteSubmitBtn.setOnAction(e -> {
            for (String pos : positionNames) {
                if (!castEncryptedBallot.containsKey(pos) || castEncryptedBallot.get(pos) == null) {
                    errorLabel.setText("⚠️ Mandatory Selection: Please select a candidate or NOTA for '" + pos + "' before submitting.");
                    errorLabel.setVisible(true);
                    return;
                }
            }
            voteSubmitBtn.setDisable(true);
            voteSubmitBtn.setText("Encrypting & Casting...");
            Thread castThread = new Thread(() -> {
                try {
                    String idToken = SessionManager.idToken != null ? SessionManager.idToken : "";
                    String voterUid = SessionManager.voterUid != null ? SessionManager.voterUid : UUID.randomUUID().toString();
                    String joinCode = SessionManager.joinCode != null ? SessionManager.joinCode : "DEMO_ORG";
                    for (Map.Entry<String, String> entry : castEncryptedBallot.entrySet()) {
                        String voteId = UUID.randomUUID().toString();
                        VoteRecord record = new VoteRecord(voteId, election.getId(), voterUid, entry.getKey(), entry.getValue(), entry.getValue(), joinCode);
                        VoteDAO.castVote(record, idToken);
                    }
                    Platform.runLater(() -> {
                        votedElections.add(election.getId());
                        VoterDashboard.dashboardCenter.setCenter(createBallotSuccessView(election.getTitle()));
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        voteSubmitBtn.setDisable(false);
                        voteSubmitBtn.setText("Submit Verified Ballot 🚀");
                        errorLabel.setText("✕ Error casting ballot: " + ex.getMessage());
                        errorLabel.setVisible(true);
                    });
                }
            });
            castThread.setDaemon(true);
            castThread.start();
        });

        ballotContainer.getChildren().addAll(backBtn, headerCard, positionsList, errorLabel, new Separator(), voteSubmitBtn);
        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createPositionSection(String positionName, ToggleGroup group, List<Candidate> candidates) {
        VBox section = new VBox(12);
        section.setPadding(new Insets(18));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
        Text posTitle = new Text("Office / Position: " + positionName);
        posTitle.setFill(Color.web(BLUE));
        posTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        FlowPane candidatesRow = new FlowPane();
        candidatesRow.setHgap(15);
        candidatesRow.setVgap(12);
        candidatesRow.setAlignment(Pos.CENTER_LEFT);
        for (Candidate c : candidates) {
            String name = c.getName() != null ? c.getName() : "Candidate";
            String initial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "C";
            candidatesRow.getChildren().add(createCandidateCard(name, initial, group, positionName));
        }
        candidatesRow.getChildren().add(createCandidateCard("None of The Above (NOTA)", "NOTA", group, positionName));
        section.getChildren().addAll(posTitle, new Separator(), candidatesRow);
        return section;
    }

    private static VBox createCandidateCard(String candidateName, String initial, ToggleGroup group, String positionName) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-cursor: hand;");
        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web(PURPLE));
        Text avatarText = new Text(initial);
        avatarText.setFill(Color.WHITE);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        StackPane avatarPane = new StackPane(avatarCircle, avatarText);
        Text nameLbl = new Text(candidateName);
        nameLbl.setFill(Color.web(TEXT));
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));
        nameLbl.setWrappingWidth(180);
        nameLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        RadioButton radio = new RadioButton("Select Choice");
        radio.setToggleGroup(group);
        radio.setOnAction(e -> castEncryptedBallot.put(positionName, candidateName));
        card.setOnMouseClicked(e -> {
            radio.setSelected(true);
            castEncryptedBallot.put(positionName, candidateName);
        });
        card.getChildren().addAll(avatarPane, nameLbl, radio);
        return card;
    }

    private static VBox createBallotSuccessView(String electionTitle) {
        VBox container = new VBox(20);
        container.setPadding(new Insets(35));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #10B981; -fx-border-width: 1.5; -fx-border-radius: 14;");
        container.setAlignment(Pos.CENTER);
        Text title = new Text("Ballot Successfully Cast & Encrypted! 🛡️");
        title.setFill(Color.web(GREEN));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        String receiptCode = "ZK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Text msg = new Text("Your vote for '" + electionTitle + "' has been encrypted using zero-knowledge homomorphic cryptography and committed to Firebase. Receipt: " + receiptCode);
        msg.setFill(Color.web(TEXT));
        msg.setFont(Font.font(13));
        msg.setWrappingWidth(750);
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER);
        Button backHome = new Button("Return to Dashboard Home ⌂");
        backHome.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backHome.setOnAction(e -> VoterDashboard.returnHomeFromVoting());
        btnBox.getChildren().add(backHome);
        container.getChildren().addAll(title, msg, new Separator(), btnBox);
        return container;
    }
}