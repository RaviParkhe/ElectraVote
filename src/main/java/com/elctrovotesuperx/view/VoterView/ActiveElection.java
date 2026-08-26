// package com.elctrovotesuperx.view.VoterView;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.Separator;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontPosture;
// import javafx.scene.text.FontWeight;
// import javafx.scene.text.Text;
// import javafx.stage.FileChooser;

// import java.io.File;
// import java.io.PrintWriter;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// public class ActiveElection {

//     private static final String TEXT = "#172033";
//     private static final String SECONDARY = "#6B7280";
//     private static final String BORDER = "#E2E8F0";
//     private static final String BLUE = "#1464F4";
//     private static final String GREEN = "#10B981";
//     private static final String ORANGE = "#F59E0B";
//     private static final String RED = "#EF4444";

//     public static VBox createActiveElectionView() {
//         VBox content = new VBox(20);
//         content.setPadding(new Insets(25));
//         content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

//         ScrollPane scrollPane = new ScrollPane(content);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

//         VBox heading = new VBox(6);
//         Text title = new Text("Active & Upcoming Elections 🗳");
//         title.setFill(Color.web(TEXT));
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

//         Text subtitle = new Text(
//                 "Explore ongoing polls and register your candidacy for future elections. Note: Candidacy applications are only open for upcoming elections.");
//         subtitle.setFill(Color.web(SECONDARY));
//         subtitle.setFont(Font.font(13));
//         subtitle.setWrappingWidth(900);
//         heading.getChildren().addAll(title, subtitle);

//         VBox electionsList = new VBox(16);

//         electionsList.getChildren().add(createElectionCard(
//                 "Student Council General Election 2026",
//                 "ABC College • Status: Upcoming • Opens in 5 Days",
//                 "Open to all enrolled students meeting academic prerequisites. Positions available include President, Vice President, and Treasurer.",
//                 true));

//         electionsList.getChildren().add(createElectionCard(
//                 "Department Tech Board Representative",
//                 "Computer Science Faculty • Status: Live Now",
//                 "Voting is currently underway for this election. Candidacy applications are closed. Please cast your ballot in the Vote tab.",
//                 false));

//         content.getChildren().addAll(heading, new Separator(), electionsList);

//         VBox wrapper = new VBox(scrollPane);
//         VBox.setVgrow(scrollPane, Priority.ALWAYS);
//         return wrapper;
//     }

//     private static VBox createElectionCard(String name, String meta, String description, boolean isUpcoming) {
//         VBox card = new VBox(12);
//         card.setPadding(new Insets(18));
//         card.setStyle(
//                 "-fx-background-color: white;" +
//                         "-fx-background-radius: 12;" +
//                         "-fx-border-color: " + BORDER + ";" +
//                         "-fx-border-radius: 12;" +
//                         "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 3);");

//         HBox topRow = new HBox();
//         topRow.setAlignment(Pos.CENTER_LEFT);

//         VBox titleBox = new VBox(3);
//         Text elecName = new Text(name);
//         elecName.setFill(Color.web(TEXT));
//         elecName.setFont(Font.font("Arial", FontWeight.BOLD, 17));

//         Text elecMeta = new Text(meta);
//         elecMeta.setFill(Color.web(isUpcoming ? GREEN : ORANGE));
//         elecMeta.setFont(Font.font("Arial", FontWeight.BOLD, 12));
//         titleBox.getChildren().addAll(elecName, elecMeta);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);

//         Button applyBtn = new Button();
//         if (isUpcoming) {
//             applyBtn.setText("Apply as Candidate ➔");
//             applyBtn.setStyle(
//                     "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
//                             "-fx-text-fill: white;" +
//                             "-fx-font-weight: bold;" +
//                             "-fx-font-size: 12.5px;" +
//                             "-fx-padding: 9 16;" +
//                             "-fx-background-radius: 7;" +
//                             "-fx-cursor: hand;");
//             applyBtn.setOnAction(e -> {
//                 VBox formView = createCandidateApplicationForm(name);
//                 VoterDashboard.dashboardCenter.setCenter(formView);
//             });
//         } else {
//             applyBtn.setText("Applications Closed (Live)");
//             applyBtn.setDisable(true);
//             applyBtn.setStyle(
//                     "-fx-background-color: #F1F5F9;" +
//                             "-fx-text-fill: #94A3B8;" +
//                             "-fx-font-weight: bold;" +
//                             "-fx-font-size: 12px;" +
//                             "-fx-padding: 9 16;" +
//                             "-fx-background-radius: 7;");
//         }

//         topRow.getChildren().addAll(titleBox, spacer, applyBtn);

//         Text descText = new Text(description);
//         descText.setFill(Color.web(SECONDARY));
//         descText.setFont(Font.font(13));
//         descText.setWrappingWidth(880);

//         card.getChildren().addAll(topRow, new Separator(), descText);
//         return card;
//     }

//     private static VBox createCandidateApplicationForm(String electionTitle) {
//         VBox formContainer = new VBox(20);
//         formContainer.setPadding(new Insets(25));
//         formContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

//         ScrollPane scrollPane = new ScrollPane(formContainer);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

//         Button backBtn = new Button("← Back to Elections List");
//         backBtn.setStyle(
//                 "-fx-background-color: transparent;" +
//                         "-fx-text-fill: " + BLUE + ";" +
//                         "-fx-font-weight: bold;" +
//                         "-fx-cursor: hand;" +
//                         "-fx-padding: 0;" +
//                         "-fx-font-size: 13px;");
//         backBtn.setOnAction(e -> VoterDashboard.showPage(createActiveElectionView()));

//         VBox formCard = new VBox(18);
//         formCard.setPadding(new Insets(30));
//         formCard.setStyle(
//                 "-fx-background-color: white;" +
//                         "-fx-background-radius: 14;" +
//                         "-fx-border-color: #3B82F6;" +
//                         "-fx-border-width: 1.8;" +
//                         "-fx-border-radius: 14;" +
//                         "-fx-effect: dropshadow(three-pass-box, rgba(20, 100, 244, 0.12), 15, 0, 0, 6);");

//         VBox headerBox = new VBox(4);
//         Text formHeader = new Text("Candidate Application Portal");
//         formHeader.setFill(Color.web(TEXT));
//         formHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

//         Text formSub = new Text("Target Election: " + electionTitle);
//         formSub.setFill(Color.web(BLUE));
//         formSub.setFont(Font.font("Arial", FontWeight.BOLD, 13.5));
//         headerBox.getChildren().addAll(formHeader, formSub);

//         VBox nameBox = createFormField("Full Name", "Enter your full registered name");
//         TextField nameField = (TextField) nameBox.getChildren().get(1);

//         VBox mobileBox = createFormField("Mobile Number", "Enter your active contact number");
//         TextField mobileField = (TextField) mobileBox.getChildren().get(1);

//         VBox emailBox = createFormField("Email Address", "Enter your institutional email address");
//         TextField emailField = (TextField) emailBox.getChildren().get(1);

//         VBox posBox = createFormField("Desired Position", "Enter position you are contesting for (e.g., President)");
//         TextField posField = (TextField) posBox.getChildren().get(1);

//         VBox descFieldBox = new VBox(6);
//         Label descLabel = new Label("Candidate Statement / Manifesto");
//         descLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 12.5px;");

//         TextArea descArea = new TextArea();
//         descArea.setPromptText(
//                 "Detail your background, qualifications, and manifesto objectives for voters to review...");
//         descArea.setPrefRowCount(5);
//         descArea.setStyle(
//                 "-fx-background-color: #F8FAFC;" +
//                         "-fx-border-color: " + BORDER + ";" +
//                         "-fx-border-radius: 8;" +
//                         "-fx-background-radius: 8;" +
//                         "-fx-font-size: 13px;");
//         descFieldBox.getChildren().addAll(descLabel, descArea);

//         Label errorLabel = new Label();
//         errorLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 12px;");
//         errorLabel.setVisible(false);

//         HBox buttonRow = new HBox(12);
//         buttonRow.setAlignment(Pos.CENTER_RIGHT);

//         Button cancelBtn = new Button("Cancel");
//         cancelBtn.setStyle(
//                 "-fx-background-color: #E2E8F0;" +
//                         "-fx-text-fill: #334155;" +
//                         "-fx-font-weight: bold;" +
//                         "-fx-padding: 10 20;" +
//                         "-fx-background-radius: 7;" +
//                         "-fx-cursor: hand;");
//         cancelBtn.setOnAction(e -> VoterDashboard.showPage(createActiveElectionView()));

//         Button submitBtn = new Button("Submit Application 🚀");
//         submitBtn.setStyle(
//                 "-fx-background-color: linear-gradient(to right, #10B981, #059669);" +
//                         "-fx-text-fill: white;" +
//                         "-fx-font-weight: bold;" +
//                         "-fx-padding: 10 22;" +
//                         "-fx-background-radius: 7;" +
//                         "-fx-cursor: hand;");

//         submitBtn.setOnAction(e -> {
//             String name = nameField.getText().trim();
//             String mobile = mobileField.getText().trim();
//             String email = emailField.getText().trim();
//             String position = posField.getText().trim();
//             String statement = descArea.getText().trim();

//             if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || position.isEmpty() || statement.isEmpty()) {
//                 errorLabel.setText("⚠️ Error: Please complete all fields before submitting your application.");
//                 errorLabel.setVisible(true);
//                 return;
//             }

//             String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//             // Hook up Controller / DAO integration here:
//             // CandidateDAO.submitApplication(electionTitle, name, mobile, email, position,
//             // statement, timestamp);

//             VBox successView = createApplicationSuccessView(electionTitle, name, mobile, email, position, statement,
//                     timestamp);
//             VoterDashboard.dashboardCenter.setCenter(successView);
//         });

//         buttonRow.getChildren().addAll(cancelBtn, submitBtn);

//         formCard.getChildren().addAll(headerBox, new Separator(), errorLabel, nameBox, mobileBox, emailBox, posBox,
//                 descFieldBox, new Separator(), buttonRow);
//         formContainer.getChildren().addAll(backBtn, formCard);

//         VBox wrapper = new VBox(scrollPane);
//         VBox.setVgrow(scrollPane, Priority.ALWAYS);
//         return wrapper;
//     }

//     private static VBox createApplicationSuccessView(String electionTitle, String name, String mobile, String email,
//             String position, String statement, String timestamp) {
//         VBox successContainer = new VBox(20);
//         successContainer.setPadding(new Insets(25));
//         successContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

//         ScrollPane scrollPane = new ScrollPane(successContainer);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//         scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

//         Button backBtn = new Button("← Return to Elections Dashboard");
//         backBtn.setStyle(
//                 "-fx-background-color: transparent;" +
//                         "-fx-text-fill: " + BLUE + ";" +
//                         "-fx-font-weight: bold;" +
//                         "-fx-cursor: hand;" +
//                         "-fx-padding: 0;" +
//                         "-fx-font-size: 13px;");
//         backBtn.setOnAction(e -> VoterDashboard.showPage(createActiveElectionView()));

//         VBox successCard = new VBox(16);
//         successCard.setPadding(new Insets(30));
//         successCard.setStyle(
//                 "-fx-background-color: white;" +
//                         "-fx-background-radius: 14;" +
//                         "-fx-border-color: #10B981;" +
//                         "-fx-border-width: 1.8;" +
//                         "-fx-border-radius: 14;" +
//                         "-fx-effect: dropshadow(three-pass-box, rgba(16, 185, 129, 0.12), 15, 0, 0, 6);");

//         VBox headingBox = new VBox(4);
//         Text successTitle = new Text("Application Submitted Successfully! 🎉");
//         successTitle.setFill(Color.web(GREEN));
//         successTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

//         Text elecNameLabel = new Text("Election: " + electionTitle);
//         elecNameLabel.setFill(Color.web(TEXT));
//         elecNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
//         headingBox.getChildren().addAll(successTitle, elecNameLabel);

//         VBox noticeBox = new VBox(4);
//         noticeBox.setPadding(new Insets(12));
//         noticeBox.setStyle(
//                 "-fx-background-color: #FEF3C7; -fx-background-radius: 8; -fx-border-color: #F59E0B; -fx-border-radius: 8;");
//         Text noticeTitle = new Text("⏳ Status: Pending Administrative Approval");
//         noticeTitle.setFill(Color.web("#92400E"));
//         noticeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));
//         Text noticeText = new Text(
//                 "Your candidacy form has been registered securely. Please wait until an administrator reviews and approves your submission before public ballot listing.");
//         noticeText.setFill(Color.web("#B45309"));
//         noticeText.setFont(Font.font(12));
//         noticeText.setWrappingWidth(820);
//         noticeBox.getChildren().addAll(noticeTitle, noticeText);

//         VBox summaryBox = new VBox(8);
//         summaryBox.setPadding(new Insets(14));
//         summaryBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: " + BORDER
//                 + "; -fx-border-radius: 8;");

//         Text summaryHeading = new Text("Submitted Details Summary");
//         summaryHeading.setFill(Color.web(TEXT));
//         summaryHeading.setFont(Font.font("Arial", FontWeight.BOLD, 13.5));

//         summaryBox.getChildren().addAll(
//                 summaryHeading,
//                 new Separator(),
//                 createSummaryRow("Full Name:", name),
//                 createSummaryRow("Mobile Number:", mobile),
//                 createSummaryRow("Email Address:", email),
//                 createSummaryRow("Desired Position:", position),
//                 createSummaryRow("Statement:", statement));

//         Text timeLabel = new Text("📅 Applied on Date & Time: " + timestamp);
//         timeLabel.setFill(Color.web(SECONDARY));
//         timeLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));

//         Button downloadBtn = new Button("📥 Download Application Form (TXT)");
//         downloadBtn.setStyle(
//                 "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
//                         "-fx-text-fill: white;" +
//                         "-fx-font-weight: bold;" +
//                         "-fx-padding: 10 20;" +
//                         "-fx-background-radius: 7;" +
//                         "-fx-cursor: hand;" +
//                         "-fx-font-size: 13px;");

//         downloadBtn.setOnAction(e -> {
//             FileChooser fileChooser = new FileChooser();
//             fileChooser.setTitle("Save Application Form");
//             fileChooser.setInitialFileName(
//                     "Candidate_Application_" + electionTitle.replaceAll("[^a-zA-Z0-9]", "_") + ".txt");
//             File file = fileChooser.showSaveDialog(VoterDashboard.VoterDashboardStage);

//             if (file != null) {
//                 try (PrintWriter writer = new PrintWriter(file)) {
//                     writer.println("==================================================");
//                     writer.println("          ELECTRAVOTE - CANDIDATE APPLICATION     ");
//                     writer.println("==================================================");
//                     writer.println("Election Name : " + electionTitle);
//                     writer.println("Full Name     : " + name);
//                     writer.println("Mobile Number : " + mobile);
//                     writer.println("Email Address : " + email);
//                     writer.println("Position      : " + position);
//                     writer.println("Statement     : " + statement);
//                     writer.println("--------------------------------------------------");
//                     writer.println("Submitted At  : " + timestamp);
//                     writer.println("Status        : Pending Admin Approval");
//                     writer.println("==================================================");
//                 } catch (Exception ex) {
//                     ex.printStackTrace();
//                 }
//             }
//         });

//         HBox actionRow = new HBox(15);
//         actionRow.setAlignment(Pos.CENTER_LEFT);
//         actionRow.getChildren().add(downloadBtn);

//         successCard.getChildren().addAll(headingBox, new Separator(), noticeBox, summaryBox, timeLabel, new Separator(),
//                 actionRow);
//         successContainer.getChildren().addAll(backBtn, successCard);

//         VBox wrapper = new VBox(scrollPane);
//         VBox.setVgrow(scrollPane, Priority.ALWAYS);
//         return wrapper;
//     }

//     private static HBox createSummaryRow(String label, String value) {
//         HBox row = new HBox(10);
//         Text lbl = new Text(label);
//         lbl.setFill(Color.web(SECONDARY));
//         lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
//         // Replaced invalid setMinWidth on text with a fixed layout region control or
//         // wrapper constraint
//         Region labelRegion = new Region();
//         labelRegion.setMinWidth(130);
//         labelRegion.setMaxWidth(130);

//         Text val = new Text(value);
//         val.setFill(Color.web(TEXT));
//         val.setFont(Font.font("Arial", 12));
//         val.setWrappingWidth(680);

//         row.getChildren().addAll(lbl, val);
//         return row;
//     }

//     private static VBox createFormField(String labelText, String promptText) {
//         VBox box = new VBox(6);
//         Label label = new Label(labelText);
//         label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 12.5px;");

//         TextField textField = new TextField();
//         textField.setPromptText(promptText);
//         textField.setStyle(
//                 "-fx-background-color: #F8FAFC;" +
//                         "-fx-border-color: " + BORDER + ";" +
//                         "-fx-border-radius: 8;" +
//                         "-fx-background-radius: 8;" +
//                         "-fx-padding: 10;" +
//                         "-fx-font-size: 13px;");
//         box.getChildren().addAll(label, textField);
//         return box;
//     }
// }

package com.elctrovotesuperx.view.VoterView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.application.Platform;
import com.elctrovotesuperx.controller.AdminController.ElectionController;
import com.elctrovotesuperx.model.AdminModel.ElectionData;

public class ActiveElection {

        private static final String TEXT = "#172033";
        private static final String SECONDARY = "#6B7280";
        private static final String BORDER = "#E2E8F0";
        private static final String BLUE = "#1464F4";
        private static final String GREEN = "#10B981";
        private static final String ORANGE = "#F59E0B";
        private static final String RED = "#EF4444";

        public static VBox createActiveElectionView() {
                VBox content = new VBox(20);
                content.setPadding(new Insets(25));
                content.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                VBox heading = new VBox(6);
                Text title = new Text("Active & Upcoming Elections 🗳");
                title.setFill(Color.web(TEXT));
                title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

                Text subtitle = new Text(
                                "Explore ongoing polls and register your candidacy for future elections. Note: Candidacy applications are only open for upcoming elections.");
                subtitle.setFill(Color.web(SECONDARY));
                subtitle.setFont(Font.font(13));
                subtitle.setWrappingWidth(900);
                heading.getChildren().addAll(title, subtitle);

                VBox electionsList = new VBox(16);

                Thread t = new Thread(() -> {
                        List<ElectionData> elections = ElectionController.loadElections();
                        Platform.runLater(() -> {
                                electionsList.getChildren().clear();
                                if (elections.isEmpty()) {
                                        electionsList.getChildren().add(createElectionCard(
                                                "Student Council General Election 2026",
                                                "Status: Upcoming • Opens in 5 Days",
                                                "Open to all enrolled students. Positions available include President, Vice President, and Treasurer.",
                                                true));
                                        electionsList.getChildren().add(createElectionCard(
                                                "Department Tech Board Representative",
                                                "Status: Live Now",
                                                "Voting is currently underway for this election. Candidacy applications are closed. Please cast your ballot in the Vote tab.",
                                                false));
                                } else {
                                        for (ElectionData e : elections) {
                                                boolean isUpcoming = "Upcoming".equalsIgnoreCase(e.getStatus()) || "Draft".equalsIgnoreCase(e.getStatus());
                                                String meta = "Status: " + (e.getStatus() != null ? e.getStatus() : "Active") + " • " + e.getStartDateTime() + " to " + e.getEndDateTime();
                                                String desc = (e.getDescription() != null && !e.getDescription().isBlank())
                                                        ? e.getDescription()
                                                        : "Official election ballot. Configured positions: " + (e.getPositions() != null && !e.getPositions().isEmpty() ? String.join(", ", e.getPositions()) : "General");
                                                electionsList.getChildren().add(createElectionCard(e.getTitle(), meta, desc, isUpcoming));
                                        }
                                }
                        });
                });
                t.setDaemon(true);
                t.start();

                content.getChildren().addAll(heading, new Separator(), electionsList);

                VBox wrapper = new VBox(scrollPane);
                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                return wrapper;
        }

        private static VBox createElectionCard(String name, String meta, String description, boolean isUpcoming) {
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

                VBox titleBox = new VBox(3);
                Text elecName = new Text(name);
                elecName.setFill(Color.web(TEXT));
                elecName.setFont(Font.font("Arial", FontWeight.BOLD, 17));

                Text elecMeta = new Text(meta);
                elecMeta.setFill(Color.web(isUpcoming ? GREEN : ORANGE));
                elecMeta.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                titleBox.getChildren().addAll(elecName, elecMeta);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button applyBtn = new Button();
                if (isUpcoming) {
                        applyBtn.setText("Apply as Candidate ➔");
                        applyBtn.setStyle(
                                        "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-font-size: 12.5px;" +
                                                        "-fx-padding: 9 16;" +
                                                        "-fx-background-radius: 7;" +
                                                        "-fx-cursor: hand;");
                        applyBtn.setOnAction(e -> {
                                VBox formView = createCandidateApplicationForm(name);
                                VoterDashboard.dashboardCenter.setCenter(formView);
                        });
                } else {
                        applyBtn.setText("Applications Closed (Live)");
                        applyBtn.setDisable(true);
                        applyBtn.setStyle(
                                        "-fx-background-color: #F1F5F9;" +
                                                        "-fx-text-fill: #94A3B8;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-font-size: 12px;" +
                                                        "-fx-padding: 9 16;" +
                                                        "-fx-background-radius: 7;");
                }

                topRow.getChildren().addAll(titleBox, spacer, applyBtn);

                Text descText = new Text(description);
                descText.setFill(Color.web(SECONDARY));
                descText.setFont(Font.font(13));
                descText.setWrappingWidth(880);

                card.getChildren().addAll(topRow, new Separator(), descText);
                return card;
        }

        private static VBox createCandidateApplicationForm(String electionTitle) {
                VBox formContainer = new VBox(20);
                formContainer.setPadding(new Insets(25));
                formContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

                ScrollPane scrollPane = new ScrollPane(formContainer);
                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                Button backBtn = new Button("← Back to Elections List");
                backBtn.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + BLUE + ";" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-cursor: hand;" +
                                                "-fx-padding: 0;" +
                                                "-fx-font-size: 13px;");
                backBtn.setOnAction(e -> VoterDashboard.showPage(createActiveElectionView()));

                VBox formCard = new VBox(18);
                formCard.setPadding(new Insets(30));
                formCard.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-background-radius: 14;" +
                                                "-fx-border-color: #3B82F6;" +
                                                "-fx-border-width: 1.8;" +
                                                "-fx-border-radius: 14;" +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(20, 100, 244, 0.12), 15, 0, 0, 6);");

                VBox headerBox = new VBox(4);
                Text formHeader = new Text("Candidate Application Portal");
                formHeader.setFill(Color.web(TEXT));
                formHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

                Text formSub = new Text("Target Election: " + electionTitle);
                formSub.setFill(Color.web(BLUE));
                formSub.setFont(Font.font("Arial", FontWeight.BOLD, 13.5));
                headerBox.getChildren().addAll(formHeader, formSub);

                VBox nameBox = createFormField("Full Name", "Enter your full registered name");
                TextField nameField = (TextField) nameBox.getChildren().get(1);

                VBox mobileBox = createFormField("Mobile Number", "Enter your active contact number");
                TextField mobileField = (TextField) mobileBox.getChildren().get(1);

                VBox emailBox = createFormField("Email Address", "Enter your institutional email address");
                TextField emailField = (TextField) emailBox.getChildren().get(1);

                VBox posBox = createFormField("Desired Position",
                                "Enter position you are contesting for (e.g., President)");
                TextField posField = (TextField) posBox.getChildren().get(1);

                VBox descFieldBox = new VBox(6);
                Label descLabel = new Label("Candidate Statement / Manifesto");
                descLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 12.5px;");

                TextArea descArea = new TextArea();
                descArea.setPromptText(
                                "Detail your background, qualifications, and manifesto objectives for voters to review...");
                descArea.setPrefRowCount(5);
                descArea.setStyle(
                                "-fx-background-color: #F8FAFC;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 8;" +
                                                "-fx-background-radius: 8;" +
                                                "-fx-font-size: 13px;");
                descFieldBox.getChildren().addAll(descLabel, descArea);

                Label errorLabel = new Label();
                errorLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold; -fx-font-size: 12px;");
                errorLabel.setVisible(false);

                HBox buttonRow = new HBox(12);
                buttonRow.setAlignment(Pos.CENTER_RIGHT);

                Button cancelBtn = new Button("Cancel");
                cancelBtn.setStyle(
                                "-fx-background-color: #E2E8F0;" +
                                                "-fx-text-fill: #334155;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 10 20;" +
                                                "-fx-background-radius: 7;" +
                                                "-fx-cursor: hand;");
                cancelBtn.setOnAction(e -> VoterDashboard.showPage(createActiveElectionView()));

                Button submitBtn = new Button("Submit Application 🚀");
                submitBtn.setStyle(
                                "-fx-background-color: linear-gradient(to right, #10B981, #059669);" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 10 22;" +
                                                "-fx-background-radius: 7;" +
                                                "-fx-cursor: hand;");

                submitBtn.setOnAction(e -> {
                        String name = nameField.getText().trim();
                        String mobile = mobileField.getText().trim();
                        String email = emailField.getText().trim();
                        String position = posField.getText().trim();
                        String statement = descArea.getText().trim();

                        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || position.isEmpty()
                                        || statement.isEmpty()) {
                                errorLabel.setText(
                                                "⚠️ Error: Please complete all fields before submitting your application.");
                                errorLabel.setVisible(true);
                                return;
                        }

                        String timestamp = LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        // Hook up Controller / DAO integration here:
                        // CandidateDAO.submitApplication(electionTitle, name, mobile, email, position,
                        // statement, timestamp);

                        VBox successView = createApplicationSuccessView(electionTitle, name, mobile, email, position,
                                        statement, timestamp);
                        VoterDashboard.dashboardCenter.setCenter(successView);
                });

                buttonRow.getChildren().addAll(cancelBtn, submitBtn);

                formCard.getChildren().addAll(headerBox, new Separator(), errorLabel, nameBox, mobileBox, emailBox,
                                posBox, descFieldBox, new Separator(), buttonRow);
                formContainer.getChildren().addAll(backBtn, formCard);

                VBox wrapper = new VBox(scrollPane);
                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                return wrapper;
        }

        private static VBox createApplicationSuccessView(String electionTitle, String name, String mobile, String email,
                        String position, String statement, String timestamp) {
                VBox successContainer = new VBox(20);
                successContainer.setPadding(new Insets(25));
                successContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #F8FAFC, #EEF2F6);");

                ScrollPane scrollPane = new ScrollPane(successContainer);
                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                Button backBtn = new Button("← Return to Elections Dashboard");
                backBtn.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + BLUE + ";" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-cursor: hand;" +
                                                "-fx-padding: 0;" +
                                                "-fx-font-size: 13px;");
                backBtn.setOnAction(e -> VoterDashboard.showPage(createActiveElectionView()));

                VBox successCard = new VBox(16);
                successCard.setPadding(new Insets(30));
                successCard.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-background-radius: 14;" +
                                                "-fx-border-color: #10B981;" +
                                                "-fx-border-width: 1.8;" +
                                                "-fx-border-radius: 14;" +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(16, 185, 129, 0.12), 15, 0, 0, 6);");

                VBox headingBox = new VBox(4);
                Text successTitle = new Text("Application Submitted Successfully! 🎉");
                successTitle.setFill(Color.web(GREEN));
                successTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

                Text elecNameLabel = new Text("Election: " + electionTitle);
                elecNameLabel.setFill(Color.web(TEXT));
                elecNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                headingBox.getChildren().addAll(successTitle, elecNameLabel);

                VBox noticeBox = new VBox(4);
                noticeBox.setPadding(new Insets(12));
                noticeBox.setStyle(
                                "-fx-background-color: #FEF3C7; -fx-background-radius: 8; -fx-border-color: #F59E0B; -fx-border-radius: 8;");
                Text noticeTitle = new Text("⏳ Status: Pending Administrative Approval");
                noticeTitle.setFill(Color.web("#92400E"));
                noticeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));
                Text noticeText = new Text(
                                "Your candidacy form has been registered securely. Please wait until an administrator reviews and approves your submission before public ballot listing.");
                noticeText.setFill(Color.web("#B45309"));
                noticeText.setFont(Font.font(12));
                noticeText.setWrappingWidth(820);
                noticeBox.getChildren().addAll(noticeTitle, noticeText);

                VBox summaryBox = new VBox(8);
                summaryBox.setPadding(new Insets(14));
                summaryBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: "
                                + BORDER + "; -fx-border-radius: 8;");

                Text summaryHeading = new Text("Submitted Details Summary");
                summaryHeading.setFill(Color.web(TEXT));
                summaryHeading.setFont(Font.font("Arial", FontWeight.BOLD, 13.5));

                summaryBox.getChildren().addAll(
                                summaryHeading,
                                new Separator(),
                                createSummaryRow("Full Name:", name),
                                createSummaryRow("Mobile Number:", mobile),
                                createSummaryRow("Email Address:", email),
                                createSummaryRow("Desired Position:", position),
                                createSummaryRow("Statement:", statement));

                Text timeLabel = new Text("📅 Applied on Date & Time: " + timestamp);
                timeLabel.setFill(Color.web(SECONDARY));
                timeLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));

                Button downloadBtn = new Button("📥 Download Application Form (TXT)");
                downloadBtn.setStyle(
                                "-fx-background-color: linear-gradient(to right, #1464F4, #0D3565);" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 10 20;" +
                                                "-fx-background-radius: 7;" +
                                                "-fx-cursor: hand;" +
                                                "-fx-font-size: 13px;");

                downloadBtn.setOnAction(e -> {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save Application Form");
                        fileChooser.setInitialFileName("Candidate_Application_"
                                        + electionTitle.replaceAll("[^a-zA-Z0-9]", "_") + ".txt");
                        File file = fileChooser.showSaveDialog(VoterDashboard.VoterDashboardStage);

                        if (file != null) {
                                try (PrintWriter writer = new PrintWriter(file)) {
                                        writer.println("==================================================");
                                        writer.println("          ELECTRAVOTE - CANDIDATE APPLICATION     ");
                                        writer.println("==================================================");
                                        writer.println("Election Name : " + electionTitle);
                                        writer.println("Full Name     : " + name);
                                        writer.println("Mobile Number : " + mobile);
                                        writer.println("Email Address : " + email);
                                        writer.println("Position      : " + position);
                                        writer.println("Statement     : " + statement);
                                        writer.println("--------------------------------------------------");
                                        writer.println("Submitted At  : " + timestamp);
                                        writer.println("Status        : Pending Admin Approval");
                                        writer.println("==================================================");
                                } catch (Exception ex) {
                                        ex.printStackTrace();
                                }
                        }
                });

                HBox actionRow = new HBox(15);
                actionRow.setAlignment(Pos.CENTER_LEFT);
                actionRow.getChildren().add(downloadBtn);

                successCard.getChildren().addAll(headingBox, new Separator(), noticeBox, summaryBox, timeLabel,
                                new Separator(), actionRow);
                successContainer.getChildren().addAll(backBtn, successCard);

                VBox wrapper = new VBox(scrollPane);
                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                return wrapper;
        }

        private static HBox createSummaryRow(String label, String value) {
                HBox row = new HBox(10);
                Text lbl = new Text(label);
                lbl.setFill(Color.web(SECONDARY));
                lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                // Replaced invalid setMinWidth on text with a fixed layout region control or
                // wrapper constraint
                Region labelRegion = new Region();
                labelRegion.setMinWidth(130);
                labelRegion.setMaxWidth(130);

                Text val = new Text(value);
                val.setFill(Color.web(TEXT));
                val.setFont(Font.font("Arial", 12));
                val.setWrappingWidth(680);

                row.getChildren().addAll(lbl, val);
                return row;
        }

        private static VBox createFormField(String labelText, String promptText) {
                VBox box = new VBox(6);
                Label label = new Label(labelText);
                label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + "; -fx-font-size: 12.5px;");

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