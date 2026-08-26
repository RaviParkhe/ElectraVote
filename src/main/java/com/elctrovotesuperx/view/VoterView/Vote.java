// package com.elctrovotesuperx.view.VoterView;

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

//         // Triggers return to home and transfers the active highlight state back to the Home sidebar button
//         backHome.setOnAction(e -> VoterDashboard.returnHomeFromVoting());

//         container.getChildren().addAll(title, msg, new Separator(), backHome);
//         return container;
//     }
// }

package com.elctrovotesuperx.view.VoterView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import com.elctrovotesuperx.config.SessionManager;
import com.elctrovotesuperx.controller.AdminController.ElectionController;
import com.elctrovotesuperx.model.AdminModel.ElectionData;

public class Vote {

    private static final String TEXT = "#172033";
    private static final String SECONDARY = "#6B7280";
    private static final String BORDER = "#E2E8F0";
    private static final String BLUE = "#1464F4";
    private static final String GREEN = "#10B981";
    private static final String PURPLE = "#7B4DFF";
    private static final String RED = "#EF4444";

    private static final Map<String, String> castEncryptedBallot = new HashMap<>();
    private static final Set<String> votedOrganizations = new HashSet<>();

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

        Text subtitle = new Text(
                "Select your verified organization below to access active ballots and cast your confidential vote.");
        subtitle.setFill(Color.web(SECONDARY));
        subtitle.setFont(Font.font(13));
        heading.getChildren().addAll(title, subtitle);

        VBox orgList = new VBox(14);

        Thread t = new Thread(() -> {
            List<ElectionData> elections = ElectionController.loadElections();
            Platform.runLater(() -> {
                orgList.getChildren().clear();
                String orgName = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                        ? SessionManager.organizationName
                        : "ABC College - Student Union 2026";
                int activeCount = 0;
                for (ElectionData e : elections) {
                    if ("Active".equalsIgnoreCase(e.getStatus()) || "Live".equalsIgnoreCase(e.getStatus())) {
                        activeCount++;
                    }
                }
                String detail = "Verified Membership • " + (elections.isEmpty() ? "2 Active Elections" : activeCount + " Active Election(s)");
                orgList.getChildren().add(createOrgCard(orgName, detail));
                if (elections.isEmpty()) {
                    orgList.getChildren().add(createOrgCard("Computer Science Faculty Board", "Verified Membership • 1 Active Election"));
                }
            });
        });
        t.setDaemon(true);
        t.start();

        content.getChildren().addAll(heading, new Separator(), orgList);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createOrgCard(String orgName, String details) {
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

        VBox textBox = new VBox(3);
        Text name = new Text(orgName);
        name.setFill(Color.web(TEXT));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        boolean alreadyVoted = votedOrganizations.contains(orgName);
        Text meta = new Text(alreadyVoted ? "Status: Already Voted (Completed)" : details);
        meta.setFill(Color.web(alreadyVoted ? RED : GREEN));
        meta.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        textBox.getChildren().addAll(name, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button voteBtn = new Button("Vote");
        voteBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 9 20;" +
                        "-fx-background-radius: 7;" +
                        "-fx-cursor: hand;");

        if (alreadyVoted) {
            voteBtn.setText("Voted");
            voteBtn.setDisable(true);
            voteBtn.setStyle(
                    "-fx-background-color: #E2E8F0; -fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-padding: 9 20; -fx-background-radius: 7;");
        } else {
            voteBtn.setOnAction(e -> {
                VBox ballotView = createPositionWiseBallotView(orgName);
                VoterDashboard.dashboardCenter.setCenter(ballotView);
            });
        }

        topRow.getChildren().addAll(textBox, spacer, voteBtn);
        card.getChildren().add(topRow);
        return card;
    }

    private static VBox createPositionWiseBallotView(String orgName) {
        VBox ballotContainer = new VBox(20);
        ballotContainer.setPadding(new Insets(25));
        ballotContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

        ScrollPane scrollPane = new ScrollPane(ballotContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Button backBtn = new Button("← Change Organization");
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + BLUE + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0;" +
                        "-fx-font-size: 13px;");
        backBtn.setOnAction(e -> VoterDashboard.showPage(createOrganizationSelectionView()));

        VBox headerCard = new VBox(6);
        headerCard.setPadding(new Insets(20));
        headerCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;");

        Text ballotTitle = new Text("Ballot Center: " + orgName);
        ballotTitle.setFill(Color.web(TEXT));
        ballotTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Text ballotSub = new Text(
                "🔒 Zero-Knowledge Encryption Active. You must select a candidate or NOTA for all 7 positions before submitting.");
        ballotSub.setFill(Color.web(SECONDARY));
        ballotSub.setFont(Font.font(12.5));
        headerCard.getChildren().addAll(ballotTitle, ballotSub);

        VBox positionsList = new VBox(20);

        String[] positions = {
                "President", "Vice President", "Secretary",
                "Treasurer", "General Secretary", "Tech Coordinator", "Cultural Representative"
        };

        String[][] candidateNames = {
                { "Alice Johnson", "Bob Smith" },
                { "David Lee", "Emma Watson" },
                { "Frank Miller", "Grace Hopper" },
                { "Ian Wright", "Julia Roberts" },
                { "Kevin Hart", "Laura Croft" },
                { "Michael Bay", "Natalie Portman" },
                { "Oscar Isaac", "Penelope Cruz" }
        };

        String[][] candidateInitials = {
                { "AJ", "BS" }, { "DL", "EW" }, { "FM", "GH" },
                { "IW", "JR" }, { "KH", "LC" }, { "MB", "NP" }, { "OI", "PC" }
        };

        ToggleGroup[] groups = new ToggleGroup[positions.length];

        for (int i = 0; i < positions.length; i++) {
            groups[i] = new ToggleGroup();
            positionsList.getChildren().add(createPositionSection(
                    positions[i],
                    groups[i],
                    candidateNames[i],
                    candidateInitials[i]));
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        errorLabel.setVisible(false);

        Button voteSubmitBtn = new Button("Vote");
        voteSubmitBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #10B981, #059669);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 28;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 14px;");

        voteSubmitBtn.setOnAction(e -> {
            for (String pos : positions) {
                if (!castEncryptedBallot.containsKey(pos) || castEncryptedBallot.get(pos) == null) {
                    errorLabel.setText(
                            "⚠️ Mandatory Constraint: Please cast your vote for every position (including NOTA) before submitting.");
                    errorLabel.setVisible(true);
                    return;
                }
            }

            votedOrganizations.add(orgName);
            VBox successView = createBallotSuccessView(orgName);
            VoterDashboard.dashboardCenter.setCenter(successView);
        });

        ballotContainer.getChildren().addAll(backBtn, headerCard, positionsList, errorLabel, new Separator(),
                voteSubmitBtn);

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return wrapper;
    }

    private static VBox createPositionSection(String positionName, ToggleGroup group, String[] candidates,
            String[] initials) {
        VBox section = new VBox(12);
        section.setPadding(new Insets(18));
        section.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;");

        Text posTitle = new Text("Position: " + positionName);
        posTitle.setFill(Color.web(BLUE));
        posTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        HBox candidatesRow = new HBox(15);
        candidatesRow.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < candidates.length; i++) {
            candidatesRow.getChildren().add(createCandidateCard(candidates[i], initials[i], group, positionName));
        }

        candidatesRow.getChildren().add(createCandidateCard("None of The Above (NOTA)", "NOTA", group, positionName));

        section.getChildren().addAll(posTitle, new Separator(), candidatesRow);
        return section;
    }

    private static VBox createCandidateCard(String candidateName, String initial, ToggleGroup group,
            String positionName) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setPrefWidth(190);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;");

        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web(PURPLE));
        Text avatarText = new Text(initial);
        avatarText.setFill(Color.WHITE);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        StackPane avatarPane = new StackPane(avatarCircle, avatarText);

        Text nameLbl = new Text(candidateName);
        nameLbl.setFill(Color.web(TEXT));
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));
        nameLbl.setWrappingWidth(170);

        RadioButton radio = new RadioButton("Select");
        radio.setToggleGroup(group);

        radio.setOnAction(e -> castEncryptedBallot.put(positionName, candidateName));
        card.setOnMouseClicked(e -> {
            radio.setSelected(true);
            castEncryptedBallot.put(positionName, candidateName);
        });

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #EFF6FF;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + BLUE + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"));

        card.getChildren().addAll(avatarPane, nameLbl, radio);
        return card;
    }

    private static VBox createBallotSuccessView(String orgName) {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Text title = new Text("Vote Successfully Cast & Encrypted! 🛡️");
        title.setFill(Color.web(GREEN));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Text msg = new Text("Your ballot for " + orgName
                + " has been encrypted using zero-knowledge homomorphic encryption and securely stored to the database. You have successfully completed voting for this election and cannot vote again.");
        msg.setFill(Color.web(TEXT));
        msg.setFont(Font.font(13));
        msg.setWrappingWidth(800);

        Button backHome = new Button("Return to Dashboard Home");
        backHome.setStyle(
                "-fx-background-color: " + BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;");

        // Triggers return to home and transfers the active highlight state back to the
        // Home sidebar button
        backHome.setOnAction(e -> VoterDashboard.returnHomeFromVoting());

        container.getChildren().addAll(title, msg, new Separator(), backHome);
        return container;
    }
}