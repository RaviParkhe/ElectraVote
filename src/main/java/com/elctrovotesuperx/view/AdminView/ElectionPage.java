// package com.elctrovotesuperx.view.AdminView;

// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.control.*;
// import javafx.scene.layout.*;

// public class ElectionPage extends VBox {

//         private VBox electionList;

//         // Election -> Positions
//         private Map<String, List<String>> positions = new LinkedHashMap<>();

//         // Election -> Position Label
//         private Map<String, Label> positionLabels = new LinkedHashMap<>();

//         // Election -> Card
//         private Map<String, HBox> electionCards = new LinkedHashMap<>();

//         // =========================================================
//         // CONSTRUCTOR
//         // =========================================================

//         public ElectionPage() {

//                 setSpacing(20);
//                 setPadding(new Insets(25));

//                 setStyle(
//                                 "-fx-background-color: #f5f7fb;");

//                 HBox header = new HBox();

//                 header.setAlignment(Pos.CENTER_LEFT);

//                 Label title = new Label("Elections Management");

//                 title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

//                 Region headerSpace = new Region();

//                 HBox.setHgrow(headerSpace, Priority.ALWAYS);

//                 Button createElectionButton = new Button("+ Create Election");

//                 createElectionButton.setStyle(
//                                 "-fx-background-color: #1464F4;-fx-text-fill: white; -fx-padding: 10 15;-fx-background-radius: 6;");

//                 createElectionButton.setOnAction(e -> createElection());

//                 header.getChildren().addAll(
//                                 title,
//                                 headerSpace,
//                                 createElectionButton);

//                 HBox actions = new HBox(10);

//                 Button positionButton = new Button("Create Multiple Positions");

//                 positionButton.setOnAction(e -> createPositions());

//                 actions.getChildren().add(positionButton);

//                 electionList = new VBox(10);

//                 ScrollPane electionScroll = new ScrollPane(electionList);

//                 electionScroll.setFitToWidth(true);

//                 electionScroll.setStyle("-fx-background-color: transparent;");

//                 VBox.setVgrow(electionScroll, Priority.ALWAYS);

//                 // ADD TO PAGE

//                 getChildren().addAll(
//                                 header,
//                                 actions,
//                                 electionScroll);
//         }

//         // ADD ELECTION CARD
//         private void addElection(String name, String description, String start, String end, String status) {

//                 HBox card = new HBox(15);
//                 card.setPadding(new Insets(15));
//                 card.setAlignment(Pos.CENTER_LEFT);
//                 card.setStyle("-fx-background-color: white;-fx-background-radius: 8;");

//                 // INFORMATION

//                 VBox information = new VBox(6);

//                 Label nameLabel = new Label(name);

//                 nameLabel.setStyle("-fx-font-size: 17px;-fx-font-weight: bold;");

//                 Label descriptionLabel = new Label(description);

//                 descriptionLabel.setWrapText(true);

//                 descriptionLabel.setStyle(
//                                 "-fx-text-fill: #64748b;");

//                 // ==============================
//                 // POSITION LABEL
//                 // ==============================

//                 Label positionLabel = new Label(
//                                 "Positions: None");

//                 positionLabel.setWrapText(true);

//                 positionLabel.setStyle(
//                                 "-fx-text-fill: #334155;");

//                 // Store label

//                 positionLabels.put(
//                                 name,
//                                 positionLabel);

//                 // Create empty position list

//                 positions.put(
//                                 name,
//                                 new ArrayList<>());

//                 // VOTING TIME

//                 Label startLabel = new Label("Voting Start: " + start);

//                 Label endLabel = new Label("Voting End: " + end);

//                 // STATUS

//                 Label statusLabel = new Label(status);

//                 statusLabel.setStyle("-fx-text-fill: #1464F4;-fx-font-weight: bold;");
//                 // ADD INFORMATION

//                 information.getChildren().addAll(
//                                 nameLabel,
//                                 descriptionLabel,
//                                 positionLabel,
//                                 startLabel,
//                                 endLabel,
//                                 statusLabel);

//                 // SPACE

//                 Region cardSpace = new Region();

//                 HBox.setHgrow(cardSpace, Priority.ALWAYS);

//                 // BUTTONS
//                 Button viewButton = new Button("View");
//                 Button editButton = new Button("Edit");
//                 Button deleteButton = new Button("Delete");

//                 viewButton.setStyle("-fx-background-color: #eef4ff; -fx-text-fill: #1464F4;");
//                 editButton.setStyle("-fx-background-color: #f1f5f9;");

//                 deleteButton.setStyle("-fx-background-color: #fee2e2;-fx-text-fill: #dc2626;");

//                 // VIEW
//                 viewButton.setOnAction(e -> {

//                         Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                         alert.setTitle("Election Details");
//                         alert.setHeaderText(nameLabel.getText());
//                         alert.setContentText(

//                                         descriptionLabel.getText()
//                                                         + "\n\n"
//                                                         + positionLabel.getText()
//                                                         + "\n\n"
//                                                         + startLabel.getText()
//                                                         + "\n"
//                                                         + endLabel.getText()
//                                                         + "\n\n"
//                                                         + "Status: "
//                                                         + statusLabel.getText());

//                         alert.showAndWait();
//                 });

//                 // ==============================
//                 // EDIT
//                 // ==============================

//                 editButton.setOnAction(e -> {

//                         editElection(
//                                         nameLabel,
//                                         descriptionLabel,
//                                         startLabel,
//                                         endLabel);
//                 });

//                 // ==============================
//                 // DELETE
//                 // ==============================

//                 deleteButton.setOnAction(e -> {

//                         Alert alert = new Alert(
//                                         Alert.AlertType.CONFIRMATION);

//                         alert.setTitle(
//                                         "Delete Election");

//                         alert.setHeaderText(
//                                         "Delete " +
//                                                         nameLabel.getText() +
//                                                         "?");

//                         alert.setContentText(
//                                         "This election will be removed.");

//                         alert.showAndWait()
//                                         .ifPresent(result -> {

//                                                 if (result == ButtonType.OK) {

//                                                         electionList
//                                                                         .getChildren()
//                                                                         .remove(card);

//                                                         String electionName = nameLabel.getText();

//                                                         positions.remove(
//                                                                         electionName);

//                                                         positionLabels.remove(
//                                                                         electionName);

//                                                         electionCards.remove(
//                                                                         electionName);
//                                                 }
//                                         });
//                 });

//                 // ==============================
//                 // ADD BUTTONS TO CARD
//                 // ==============================

//                 card.getChildren().addAll(

//                                 information,

//                                 cardSpace,

//                                 viewButton,

//                                 editButton,

//                                 deleteButton

//                 );

//                 // Store card

//                 electionCards.put(
//                                 name,
//                                 card);

//                 // Add to list

//                 electionList
//                                 .getChildren()
//                                 .add(card);
//         }

//         // =========================================================
//         // CREATE ELECTION
//         // =========================================================

//         private void createElection() {

//                 Dialog<ButtonType> dialog = new Dialog<>();

//                 dialog.setTitle(
//                                 "Create New Election");

//                 dialog.setHeaderText(
//                                 "Enter Election Details");

//                 VBox box = new VBox(12);

//                 box.setPadding(
//                                 new Insets(20));

//                 // ==============================
//                 // ELECTION NAME
//                 // ==============================

//                 TextField name = new TextField();

//                 name.setPromptText(
//                                 "Enter election name");

//                 // ==============================
//                 // DESCRIPTION
//                 // ==============================

//                 TextArea description = new TextArea();

//                 description.setPromptText(
//                                 "Enter election description");

//                 description.setPrefRowCount(3);

//                 description.setWrapText(true);

//                 // ==============================
//                 // START DATE & TIME
//                 // ==============================

//                 DatePicker startDate = new DatePicker();

//                 TextField startTime = new TextField();

//                 startTime.setPromptText(
//                                 "HH:mm");

//                 HBox startBox = new HBox(
//                                 10,
//                                 startDate,
//                                 startTime);

//                 // ==============================
//                 // END DATE & TIME
//                 // ==============================

//                 DatePicker endDate = new DatePicker();

//                 TextField endTime = new TextField();

//                 endTime.setPromptText(
//                                 "HH:mm");

//                 HBox endBox = new HBox(
//                                 10,
//                                 endDate,
//                                 endTime);

//                 // ==============================
//                 // FORM
//                 // ==============================

//                 box.getChildren().addAll(

//                                 new Label(
//                                                 "Election Name"),

//                                 name,

//                                 new Label(
//                                                 "Description"),

//                                 description,

//                                 new Label(
//                                                 "Voting Start"),

//                                 startBox,

//                                 new Label(
//                                                 "Voting End"),

//                                 endBox

//                 );

//                 dialog.getDialogPane()
//                                 .setContent(box);

//                 // ==============================
//                 // BUTTONS
//                 // ==============================

//                 ButtonType saveButton = new ButtonType(
//                                 "Create Election",
//                                 ButtonBar.ButtonData.OK_DONE);

//                 dialog.getDialogPane()
//                                 .getButtonTypes()
//                                 .addAll(

//                                                 ButtonType.CANCEL,

//                                                 saveButton

//                                 );

//                 // ==============================
//                 // SAVE
//                 // ==============================

//                 dialog.showAndWait()
//                                 .ifPresent(result -> {

//                                         if (result == saveButton) {

//                                                 // Validate name

//                                                 if (name.getText()
//                                                                 .trim()
//                                                                 .isEmpty()) {

//                                                         showError(
//                                                                         "Enter election name.");

//                                                         return;
//                                                 }

//                                                 // Validate dates

//                                                 if (startDate.getValue() == null ||
//                                                                 endDate.getValue() == null) {

//                                                         showError(
//                                                                         "Select voting dates.");

//                                                         return;
//                                                 }

//                                                 // Validate times

//                                                 if (startTime.getText()
//                                                                 .trim()
//                                                                 .isEmpty() ||
//                                                                 endTime.getText()
//                                                                                 .trim()
//                                                                                 .isEmpty()) {

//                                                         showError(
//                                                                         "Enter voting times.");

//                                                         return;
//                                                 }

//                                                 String electionName = name.getText()
//                                                                 .trim();

//                                                 // Check duplicate

//                                                 if (electionCards
//                                                                 .containsKey(
//                                                                                 electionName)) {

//                                                         showError(
//                                                                         "Election already exists.");

//                                                         return;
//                                                 }

//                                                 String electionDescription = description
//                                                                 .getText()
//                                                                 .trim();

//                                                 String start = startDate.getValue()
//                                                                 + " "
//                                                                 + startTime
//                                                                                 .getText()
//                                                                                 .trim();

//                                                 String end = endDate.getValue()
//                                                                 + " "
//                                                                 + endTime
//                                                                                 .getText()
//                                                                                 .trim();

//                                                 // Add election

//                                                 addElection(

//                                                                 electionName,

//                                                                 electionDescription,

//                                                                 start,

//                                                                 end,

//                                                                 "Draft"

//                                 );
//                                         }
//                                 });
//         }

//         // =========================================================
//         // CREATE MULTIPLE POSITIONS
//         // =========================================================

//         private void createPositions() {

//                 Dialog<ButtonType> dialog = new Dialog<>();

//                 dialog.setTitle(
//                                 "Create Multiple Positions");

//                 dialog.setHeaderText(
//                                 "Add Positions to Election");

//                 VBox mainBox = new VBox(12);

//                 mainBox.setPadding(
//                                 new Insets(20));

//                 // ==============================
//                 // SELECT ELECTION
//                 // ==============================

//                 ComboBox<String> election = new ComboBox<>();

//                 // Get all currently created elections

//                 election.getItems().addAll(
//                                 electionCards.keySet());

//                 election.setPromptText(
//                                 "Select Election");

//                 election.setMaxWidth(
//                                 Double.MAX_VALUE);

//                 // ==============================
//                 // POSITION BOX
//                 // ==============================

//                 VBox positionBox = new VBox(8);

//                 positionBox.setPadding(
//                                 new Insets(5));

//                 // Add first position

//                 addPositionField(
//                                 positionBox);

//                 // ==============================
//                 // SCROLL
//                 // ==============================

//                 ScrollPane positionScroll = new ScrollPane(
//                                 positionBox);

//                 positionScroll.setFitToWidth(
//                                 true);

//                 positionScroll.setPrefHeight(
//                                 250);

//                 positionScroll.setMaxHeight(
//                                 250);

//                 positionScroll.setStyle(
//                                 "-fx-background-color: #f8fafc;");

//                 // ==============================
//                 // ADD POSITION
//                 // ==============================

//                 Button addPositionButton = new Button(
//                                 "+ Add Position");

//                 addPositionButton.setStyle(
//                                 "-fx-background-color: #eef4ff;" +
//                                                 "-fx-text-fill: #1464F4;");

//                 addPositionButton.setOnAction(
//                                 e -> addPositionField(
//                                                 positionBox));

//                 // ==============================
//                 // FORM
//                 // ==============================

//                 mainBox.getChildren().addAll(

//                                 new Label(
//                                                 "Select Election"),

//                                 election,

//                                 new Label(
//                                                 "Position Name"),

//                                 positionScroll,

//                                 addPositionButton

//                 );

//                 dialog.getDialogPane()
//                                 .setContent(mainBox);

//                 // ==============================
//                 // SAVE BUTTON
//                 // ==============================

//                 ButtonType saveButton = new ButtonType(
//                                 "Save Positions",
//                                 ButtonBar.ButtonData.OK_DONE);

//                 dialog.getDialogPane()
//                                 .getButtonTypes()
//                                 .addAll(

//                                                 ButtonType.CANCEL,

//                                                 saveButton

//                                 );

//                 // ==============================
//                 // SAVE POSITIONS
//                 // ==============================

//                 dialog.showAndWait()
//                                 .ifPresent(result -> {

//                                         if (result == saveButton) {

//                                                 // Check election

//                                                 if (election.getValue() == null) {

//                                                         showError(
//                                                                         "Select an election.");

//                                                         return;
//                                                 }

//                                                 List<String> newPositions = new ArrayList<>();

//                                                 // Read all fields

//                                                 for (var node : positionBox
//                                                                 .getChildren()) {

//                                                         if (node instanceof HBox) {

//                                                                 HBox row = (HBox) node;

//                                                                 if (row.getChildren()
//                                                                                 .get(0) instanceof TextField) {

//                                                                         TextField field = (TextField) row.getChildren()
//                                                                                         .get(0);

//                                                                         String value = field.getText()
//                                                                                         .trim();

//                                                                         if (!value
//                                                                                         .isEmpty()) {

//                                                                                 newPositions.add(
//                                                                                                 value);
//                                                                         }
//                                                                 }
//                                                         }
//                                                 }

//                                                 // Validate

//                                                 if (newPositions
//                                                                 .isEmpty()) {

//                                                         showError(
//                                                                         "Add at least one position.");

//                                                         return;
//                                                 }

//                                                 // ==============================
//                                                 // SAVE TO MAP
//                                                 // ==============================

//                                                 String electionName = election.getValue();

//                                                 positions
//                                                                 .get(electionName)
//                                                                 .clear();

//                                                 positions
//                                                                 .get(electionName)
//                                                                 .addAll(
//                                                                                 newPositions);

//                                                 // ==============================
//                                                 // UPDATE CARD
//                                                 // ==============================

//                                                 updatePositionLabel(
//                                                                 electionName);
//                                         }
//                                 });
//         }

//         // =========================================================
//         // ADD POSITION FIELD
//         // =========================================================

//         private void addPositionField(
//                         VBox positionBox) {

//                 HBox row = new HBox(10);

//                 row.setAlignment(
//                                 Pos.CENTER_LEFT);

//                 TextField position = new TextField();

//                 position.setPromptText(
//                                 "Enter Position Name");

//                 HBox.setHgrow(
//                                 position,
//                                 Priority.ALWAYS);

//                 Button removeButton = new Button(
//                                 "Remove");

//                 removeButton.setStyle(
//                                 "-fx-text-fill: #dc2626;");

//                 removeButton.setOnAction(
//                                 e -> positionBox
//                                                 .getChildren()
//                                                 .remove(row));

//                 row.getChildren().addAll(

//                                 position,

//                                 removeButton

//                 );

//                 positionBox
//                                 .getChildren()
//                                 .add(row);
//         }

//         // =========================================================
//         // UPDATE POSITION ON CARD
//         // =========================================================

//         private void updatePositionLabel(
//                         String electionName) {

//                 Label label = positionLabels.get(
//                                 electionName);

//                 if (label == null) {
//                         return;
//                 }

//                 List<String> list = positions.get(
//                                 electionName);

//                 if (list == null ||
//                                 list.isEmpty()) {

//                         label.setText(
//                                         "Positions: None");

//                         return;
//                 }

//                 StringBuilder text = new StringBuilder(
//                                 "Positions: ");

//                 for (int i = 0; i < list.size(); i++) {

//                         text.append(
//                                         list.get(i));

//                         if (i < list.size() - 1) {

//                                 text.append(
//                                                 " • ");
//                         }
//                 }

//                 label.setText(
//                                 text.toString());
//         }

//         // =========================================================
//         // EDIT ELECTION
//         // =========================================================

//         private void editElection(
//                         Label nameLabel,
//                         Label descriptionLabel,
//                         Label startLabel,
//                         Label endLabel) {

//                 String oldName = nameLabel.getText();

//                 Dialog<ButtonType> dialog = new Dialog<>();

//                 dialog.setTitle(
//                                 "Edit Election");

//                 dialog.setHeaderText(
//                                 "Update Election");

//                 VBox box = new VBox(12);

//                 box.setPadding(
//                                 new Insets(20));

//                 TextField name = new TextField(
//                                 oldName);

//                 TextArea description = new TextArea(
//                                 descriptionLabel
//                                                 .getText());

//                 description.setWrapText(
//                                 true);

//                 DatePicker startDate = new DatePicker();

//                 TextField startTime = new TextField();

//                 startTime.setPromptText(
//                                 "HH:mm");

//                 DatePicker endDate = new DatePicker();

//                 TextField endTime = new TextField();

//                 endTime.setPromptText(
//                                 "HH:mm");

//                 box.getChildren().addAll(

//                                 new Label(
//                                                 "Election Name"),

//                                 name,

//                                 new Label(
//                                                 "Description"),

//                                 description,

//                                 new Label(
//                                                 "Voting Start"),

//                                 startDate,

//                                 startTime,

//                                 new Label(
//                                                 "Voting End"),

//                                 endDate,

//                                 endTime

//                 );

//                 dialog.getDialogPane()
//                                 .setContent(box);

//                 dialog.getDialogPane()
//                                 .getButtonTypes()
//                                 .addAll(

//                                                 ButtonType.CANCEL,

//                                                 ButtonType.OK

//                                 );

//                 dialog.showAndWait()
//                                 .ifPresent(result -> {

//                                         if (result == ButtonType.OK) {

//                                                 String newName = name.getText()
//                                                                 .trim();

//                                                 if (newName.isEmpty()) {

//                                                         showError(
//                                                                         "Election name cannot be empty.");

//                                                         return;
//                                                 }

//                                                 // ==============================
//                                                 // NAME CHANGED
//                                                 // ==============================

//                                                 if (!newName.equals(
//                                                                 oldName)) {

//                                                         if (electionCards
//                                                                         .containsKey(
//                                                                                         newName)) {

//                                                                 showError(
//                                                                                 "Election already exists.");

//                                                                 return;
//                                                         }

//                                                         List<String> oldPositions = positions.remove(
//                                                                         oldName);

//                                                         Label oldPositionLabel = positionLabels.remove(
//                                                                         oldName);

//                                                         HBox oldCard = electionCards.remove(
//                                                                         oldName);

//                                                         positions.put(
//                                                                         newName,
//                                                                         oldPositions);

//                                                         positionLabels.put(
//                                                                         newName,
//                                                                         oldPositionLabel);

//                                                         electionCards.put(
//                                                                         newName,
//                                                                         oldCard);

//                                                         nameLabel.setText(
//                                                                         newName);
//                                                 }

//                                                 // ==============================
//                                                 // DESCRIPTION
//                                                 // ==============================

//                                                 descriptionLabel.setText(
//                                                                 description
//                                                                                 .getText());

//                                                 // ==============================
//                                                 // START
//                                                 // ==============================

//                                                 if (startDate.getValue() != null) {

//                                                         startLabel.setText(
//                                                                         "Voting Start: "
//                                                                                         + startDate
//                                                                                                         .getValue()
//                                                                                         + " "
//                                                                                         + startTime
//                                                                                                         .getText());
//                                                 }

//                                                 // ==============================
//                                                 // END
//                                                 // ==============================

//                                                 if (endDate.getValue() != null) {

//                                                         endLabel.setText(
//                                                                         "Voting End: "
//                                                                                         + endDate
//                                                                                                         .getValue()
//                                                                                         + " "
//                                                                                         + endTime
//                                                                                                         .getText());
//                                                 }
//                                         }
//                                 });
//         }

//         // =========================================================
//         // ERROR ALERT
//         // =========================================================

//         private void showError(
//                         String message) {

//                 Alert alert = new Alert(
//                                 Alert.AlertType.ERROR);

//                 alert.setTitle(
//                                 "Invalid Information");

//                 alert.setHeaderText(
//                                 null);

//                 alert.setContentText(
//                                 message);

//                 alert.showAndWait();
//         }
// }

package com.elctrovotesuperx.view.AdminView;

import com.elctrovotesuperx.controller.AdminController.ElectionController;
import com.elctrovotesuperx.model.AdminModel.ElectionData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ElectionPage extends VBox {

        private VBox electionList;
        private TextField searchField;

        // Election -> Positions
        private final Map<String, List<String>> positions = new LinkedHashMap<>();

        // Election -> Position Label
        private final Map<String, Label> positionLabels = new LinkedHashMap<>();

        // Election -> Card
        private final Map<String, HBox> electionCards = new LinkedHashMap<>();

        // Election title -> Firestore document ID
        private final Map<String, String> electionIds = new LinkedHashMap<>();

        private static final String FONT = "-fx-font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;";

        // =========================================================
        // CONSTRUCTOR
        // =========================================================
        public ElectionPage() {
                setSpacing(18);
                setPadding(new Insets(20, 28, 24, 28));
                setStyle("-fx-background-color: linear-gradient(to bottom right, #f8fafc 0%, #eef2ff 35%, #e0e7ff 70%, #f5f3ff 100%); "
                                + FONT);

                // 1. Header Section
                HBox header = buildHeader();

                // 2. Action & Filter Toolbar
                HBox actionToolbar = buildActionToolbar();

                // 3. Election Directory Card Container
                VBox directoryCard = buildDirectorySection();
                VBox.setVgrow(directoryCard, Priority.ALWAYS);

                getChildren().addAll(header, actionToolbar, directoryCard);

                // Load elections from Firestore on a background thread
                loadElectionsFromFirestore();
        }

        public void loadElectionsFromFirestore() {
                Thread loadThread = new Thread(() -> {
                        List<ElectionData> elections = ElectionController.loadElections();
                        Platform.runLater(() -> {
                                electionList.getChildren().clear();
                                electionCards.clear();
                                electionIds.clear();
                                positions.clear();
                                positionLabels.clear();

                                if (elections.isEmpty()) {
                                        Label empty = new Label("No elections found. Click '+ Create Election' to establish ballot parameters.");
                                        empty.setStyle(FONT + "-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-padding: 20; -fx-font-weight: 600;");
                                        electionList.getChildren().add(empty);
                                } else {
                                        for (ElectionData e : elections) {
                                                addElection(e.getId(), e.getTitle(), e.getDescription(),
                                                        e.getStartDateTime(), e.getEndDateTime(),
                                                        e.getStatus(), e.getPositions());
                                        }
                                }
                        });
                });
                loadThread.setDaemon(true);
                loadThread.start();
        }

        // =========================================================
        // 1. HEADER SECTION
        // =========================================================
        private HBox buildHeader() {
                HBox header = new HBox();
                header.setAlignment(Pos.CENTER_LEFT);

                VBox titleBox = new VBox(2);
                Label title = new Label("Elections Management & Setup");
                title.setStyle(FONT
                                + "-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-letter-spacing: -0.5px;");

                Label subtitle = new Label(
                                "Create, schedule, and structure institutional voting ballots and position rosters");
                subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
                titleBox.getChildren().addAll(title, subtitle);

                Region headerSpace = new Region();
                HBox.setHgrow(headerSpace, Priority.ALWAYS);

                Button createElectionButton = new Button("+ Create Election");
                createElectionButton.setStyle(FONT +
                                "-fx-background-color: #2563eb; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: 800; " +
                                "-fx-font-size: 12px; " +
                                "-fx-padding: 8 16; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.3), 8, 0, 0, 2);");
                createElectionButton.setOnAction(e -> createElection());

                header.getChildren().addAll(titleBox, headerSpace, createElectionButton);
                return header;
        }

        // =========================================================
        // 2. ACTION & FILTER TOOLBAR
        // =========================================================
        private HBox buildActionToolbar() {
                HBox toolbar = new HBox(12);
                toolbar.setAlignment(Pos.CENTER_LEFT);
                toolbar.setPadding(new Insets(10, 16, 10, 16));
                toolbar.setStyle(
                                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #818cf8; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.08), 8, 0, 0, 2);");

                searchField = new TextField();
                searchField.setPromptText("🔍  Search elections by title or details...");
                searchField.setPrefWidth(300);
                searchField.setStyle(FONT
                                + "-fx-background-color: #f8fafc; -fx-text-fill: #0f172a; -fx-prompt-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 12px; -fx-font-weight: 600;");
                searchField.textProperty().addListener((obs, oldV, newV) -> filterElections(newV));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button positionButton = new Button("⚙ Configure Positions");
                positionButton.setStyle(FONT +
                                "-fx-background-color: #eff6ff; " +
                                "-fx-text-fill: #1d4ed8; " +
                                "-fx-border-color: #3b82f6; " +
                                "-fx-border-radius: 8; " +
                                "-fx-font-weight: 800; " +
                                "-fx-font-size: 11.5px; " +
                                "-fx-padding: 6 14; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand;");
                positionButton.setOnAction(e -> createPositions());

                toolbar.getChildren().addAll(searchField, spacer, positionButton);
                return toolbar;
        }

        // =========================================================
        // 3. ELECTION DIRECTORY SECTION
        // =========================================================
        private VBox buildDirectorySection() {
                VBox section = new VBox(8);
                section.setPadding(new Insets(12, 14, 12, 14));
                section.setStyle(
                                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1.5;");

                HBox dirHeader = new HBox();
                dirHeader.setAlignment(Pos.CENTER_LEFT);

                Label dirTitle = new Label("Configured Elections Directory");
                dirTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Label badge = new Label("BALLOT REGISTRY");
                badge.setStyle(FONT
                                + "-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-weight: 800; -fx-font-size: 9.5px; -fx-padding: 3 8; -fx-background-radius: 10;");

                dirHeader.getChildren().addAll(dirTitle, sp, badge);

                electionList = new VBox(10);
                ScrollPane electionScroll = new ScrollPane(electionList);
                electionScroll.setFitToWidth(true);
                electionScroll.setStyle(
                                "-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
                VBox.setVgrow(electionScroll, Priority.ALWAYS);

                section.getChildren().addAll(dirHeader, electionScroll);
                return section;
        }

        // =========================================================
        // ADD ELECTION CARD
        // =========================================================
        private void addElection(String id, String name, String description, String start, String end, String status, List<String> existingPositions) {
                HBox card = new HBox(14);
                card.setPadding(new Insets(12, 16, 12, 16));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setMinHeight(90);

                String badgeBg;
                String badgeText;
                String badgeBorder;
                String leftHighlight;

                if ("Active".equalsIgnoreCase(status)) {
                        badgeBg = "#ecfdf5";
                        badgeText = "#047857";
                        badgeBorder = "#10b981";
                        leftHighlight = "#059669";
                } else if ("Upcoming".equalsIgnoreCase(status)) {
                        badgeBg = "#eff6ff";
                        badgeText = "#1d4ed8";
                        badgeBorder = "#3b82f6";
                        leftHighlight = "#2563eb";
                } else {
                        badgeBg = "#f8fafc";
                        badgeText = "#475569";
                        badgeBorder = "#94a3b8";
                        leftHighlight = "#64748b";
                }

                card.setStyle("-fx-background-color: #ffffff; " +
                                "-fx-background-radius: 10; " +
                                "-fx-border-color: #cbd5e1 #cbd5e1 #cbd5e1 " + leftHighlight + "; " +
                                "-fx-border-radius: 10; " +
                                "-fx-border-width: 1 1 1 4.5; " +
                                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.04), 8, 0, 0, 2);");

                Label icon = new Label("🗳");
                icon.setMinSize(40, 40);
                icon.setMaxSize(40, 40);
                icon.setAlignment(Pos.CENTER);
                icon.setStyle(FONT + "-fx-background-color: " + badgeBg
                                + "; -fx-font-size: 18px; -fx-background-radius: 20; -fx-border-color: " + badgeBorder
                                + "; -fx-border-radius: 20; -fx-border-width: 1.2;");

                VBox information = new VBox(3);
                information.setMinWidth(0);
                HBox.setHgrow(information, Priority.ALWAYS);

                HBox titleRow = new HBox(8);
                titleRow.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(name);
                nameLabel.setStyle(FONT + "-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

                Label statusLabel = new Label(status.toUpperCase());
                statusLabel.setStyle(FONT + "-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeText
                                + "; -fx-font-weight: 900; -fx-font-size: 9.5px; -fx-padding: 2 8; -fx-background-radius: 10; -fx-border-color: "
                                + badgeBorder + "; -fx-border-radius: 10;");

                titleRow.getChildren().addAll(nameLabel, statusLabel);

                Label descriptionLabel = new Label(description);
                descriptionLabel.setWrapText(true);
                descriptionLabel.setStyle(
                                FONT + "-fx-text-fill: #64748b; -fx-font-size: 11.5px; -fx-font-weight: 500;");

                Label positionLabel = new Label("Positions: None");
                positionLabel.setWrapText(true);
                positionLabel.setStyle(FONT + "-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 800;");

                Label startLabel = new Label("Voting Start: " + start);
                Label endLabel = new Label("Voting End: " + end);

                HBox timingRow = new HBox(12);
                Label scheduleLabel = new Label("⏱ " + start + "  ➔  " + end);
                scheduleLabel.setStyle(FONT + "-fx-text-fill: #475569; -fx-font-size: 10.5px; -fx-font-weight: 600;");
                timingRow.getChildren().add(scheduleLabel);

                positionLabels.put(name, positionLabel);
                List<String> posList = existingPositions != null ? new ArrayList<>(existingPositions) : new ArrayList<>();
                positions.put(name, posList);
                if (!posList.isEmpty()) {
                        positionLabel.setText("Positions: " + String.join(" • ", posList));
                }

                information.getChildren().addAll(titleRow, descriptionLabel, positionLabel, timingRow);

                Region cardSpace = new Region();
                HBox.setHgrow(cardSpace, Priority.ALWAYS);

                HBox actionBox = new HBox(8);
                actionBox.setAlignment(Pos.CENTER_RIGHT);
                actionBox.setMinWidth(Region.USE_PREF_SIZE);

                Button viewButton = new Button("View Details");
                viewButton.setMinWidth(Region.USE_PREF_SIZE);
                viewButton.setStyle(FONT
                                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 11.5px; -fx-padding: 6 12; -fx-background-radius: 8; -fx-cursor: hand;");

                Button editButton = new Button("Edit");
                editButton.setMinWidth(Region.USE_PREF_SIZE);
                editButton.setStyle(FONT
                                + "-fx-background-color: #f8fafc; -fx-text-fill: #334155; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 11.5px; -fx-padding: 6 12; -fx-background-radius: 8; -fx-cursor: hand;");

                Button deleteButton = new Button("Delete");
                deleteButton.setMinWidth(Region.USE_PREF_SIZE);
                deleteButton.setStyle(FONT
                                + "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-border-color: #fca5a5; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 11.5px; -fx-padding: 6 12; -fx-background-radius: 8; -fx-cursor: hand;");

                viewButton.setOnAction(e -> {
                        Dialog<ButtonType> dialog = createBaseDialog("Election Dossier",
                                        "Official Election Configuration & Timelines", "🗳", "#2563eb", "#eff6ff");

                        VBox box = new VBox(14);
                        box.setPadding(new Insets(20));
                        box.setPrefWidth(480);
                        box.setStyle("-fx-background-color: #ffffff; " + FONT);

                        VBox titleCard = new VBox(4);
                        titleCard.setPadding(new Insets(10, 14, 10, 14));
                        titleCard.setStyle(
                                        "-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #818cf8; -fx-border-radius: 10; -fx-border-width: 1.5;");

                        Label dtTitle = new Label(nameLabel.getText());
                        dtTitle.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");
                        Label dtStatus = new Label("Status: " + statusLabel.getText());
                        dtStatus.setStyle(
                                        FONT + "-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-text-fill: #2563eb;");
                        titleCard.getChildren().addAll(dtTitle, dtStatus);

                        GridPane grid = new GridPane();
                        grid.setHgap(14);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(12));
                        grid.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1.2;");

                        grid.add(createMetaField("Voting Starts", startLabel.getText().replace("Voting Start: ", "")),
                                        0, 0);
                        grid.add(createMetaField("Voting Closes", endLabel.getText().replace("Voting End: ", "")), 1,
                                        0);

                        VBox descBox = new VBox(4);
                        Label descTitle = new Label("Description");
                        descTitle.setStyle(FONT + "-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #64748b;");
                        Label descContent = new Label(descriptionLabel.getText());
                        descContent.setWrapText(true);
                        descContent.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #0f172a;");
                        descBox.getChildren().addAll(descTitle, descContent);

                        VBox posBox = new VBox(4);
                        Label posTitle = new Label("Configured Positions");
                        posTitle.setStyle(FONT + "-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #64748b;");
                        Label posContent = new Label(positionLabel.getText());
                        posContent.setWrapText(true);
                        posContent.setStyle(
                                        FONT + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #2563eb;");
                        posBox.getChildren().addAll(posTitle, posContent);

                        box.getChildren().addAll(titleCard, grid, descBox, posBox);
                        dialog.getDialogPane().setContent(box);

                        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                        dialog.getDialogPane().getButtonTypes().add(closeBtn);
                        styleDialogButtons(dialog, "#2563eb", closeBtn);
                        dialog.showAndWait();
                });

                editButton.setOnAction(
                                e -> editElection(nameLabel, descriptionLabel, startLabel, endLabel, scheduleLabel));

                deleteButton.setOnAction(e -> {
                        Dialog<ButtonType> confirmation = new Dialog<>();
                        if (AdminDashboard.AdminDashboardStage != null) {
                                confirmation.initOwner(AdminDashboard.AdminDashboardStage);
                        }
                        confirmation.setTitle("Delete Election");

                        VBox contentBox = new VBox(14);
                        contentBox.setPadding(new Insets(20));
                        contentBox.setPrefWidth(400);
                        contentBox.setAlignment(Pos.CENTER);
                        contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

                        Circle outerHalo = new Circle(26, Color.web("#fee2e2"));
                        Circle innerCircle = new Circle(18, Color.web("#ef4444"));
                        Label delIcon = new Label("✕");
                        delIcon.setStyle(FONT + "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 900;");
                        StackPane iconStack = new StackPane(outerHalo, innerCircle, delIcon);

                        VBox textBox = new VBox(4);
                        textBox.setAlignment(Pos.CENTER);
                        Label delTitle = new Label("Delete " + nameLabel.getText() + "?");
                        delTitle.setWrapText(true);
                        delTitle.setStyle(FONT
                                        + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #991b1b; -fx-text-alignment: CENTER;");

                        Label delDesc = new Label(
                                        "This election and all associated position metadata will be permanently removed.");
                        delDesc.setWrapText(true);
                        delDesc.setStyle(FONT
                                        + "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-text-alignment: CENTER;");
                        textBox.getChildren().addAll(delTitle, delDesc);

                        contentBox.getChildren().addAll(iconStack, textBox);
                        confirmation.getDialogPane().setContent(contentBox);
                        confirmation.getDialogPane().setStyle(
                                        "-fx-background-color: #ffffff; -fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

                        ButtonType confirmDeleteType = new ButtonType("Delete Election", ButtonBar.ButtonData.OK_DONE);
                        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                        confirmation.getDialogPane().getButtonTypes().addAll(cancelType, confirmDeleteType);

                        styleDialogButtons(confirmation, "#dc2626", confirmDeleteType);

                        confirmation.showAndWait().ifPresent(result -> {
                                if (result == confirmDeleteType) {
                                        String electionName = nameLabel.getText();
                                        String electionId = electionIds.get(electionName);

                                        // Remove from UI immediately
                                        electionList.getChildren().remove(card);
                                        positions.remove(electionName);
                                        positionLabels.remove(electionName);
                                        electionCards.remove(electionName);
                                        electionIds.remove(electionName);

                                        // Delete from Firestore in background
                                        if (electionId != null) {
                                                Thread t = new Thread(() -> {
                                                        String err = ElectionController.deleteElection(electionId);
                                                        if (err != null) {
                                                                Platform.runLater(() -> showError("Firestore: " + err));
                                                        }
                                                });
                                                t.setDaemon(true);
                                                t.start();
                                        }
                                }
                        });
                });

                actionBox.getChildren().addAll(viewButton, editButton, deleteButton);
                card.getChildren().addAll(icon, information, cardSpace, actionBox);

                electionCards.put(name, card);
                electionIds.put(name, id);
                electionList.getChildren().add(card);
        }

        private void filterElections(String query) {
                String q = query.toLowerCase().trim();
                for (Map.Entry<String, HBox> entry : electionCards.entrySet()) {
                        HBox card = entry.getValue();
                        boolean match = q.isEmpty() || entry.getKey().toLowerCase().contains(q);
                        card.setVisible(match);
                        card.setManaged(match);
                }
        }

        // =========================================================
        // VISIBLE & EDITABLE TIME PICKER COMPONENT
        // =========================================================
        private static class TimePickerBox extends HBox {
                private final ComboBox<String> hourCombo;
                private final ComboBox<String> minuteCombo;

                public TimePickerBox(String defaultHour, String defaultMinute) {
                        super(4);
                        setAlignment(Pos.CENTER_LEFT);

                        // HOUR SELECTOR
                        hourCombo = new ComboBox<>();
                        for (int i = 0; i < 24; i++) {
                                hourCombo.getItems().add(String.format("%02d", i));
                        }
                        hourCombo.setEditable(true);
                        hourCombo.setVisibleRowCount(8);
                        hourCombo.setPrefWidth(72);
                        hourCombo.setMinWidth(72);
                        hourCombo.setValue(defaultHour);

                        // MINUTE SELECTOR
                        minuteCombo = new ComboBox<>();
                        for (int i = 0; i < 60; i += 5) {
                                minuteCombo.getItems().add(String.format("%02d", i));
                        }
                        minuteCombo.setEditable(true);
                        minuteCombo.setVisibleRowCount(8);
                        minuteCombo.setPrefWidth(72);
                        minuteCombo.setMinWidth(72);
                        minuteCombo.setValue(defaultMinute);

                        // COMBO BOX BUTTON & CELL STYLING TO ENSURE TEXT IS ALWAYS VISIBLE
                        applyComboVisibilityStyle(hourCombo);
                        applyComboVisibilityStyle(minuteCombo);

                        Label separator = new Label(":");
                        separator.setStyle(FONT
                                        + "-fx-font-weight: 900; -fx-text-fill: #1e1b4b; -fx-font-size: 14px; -fx-padding: 0 1;");

                        getChildren().addAll(hourCombo, separator, minuteCombo);
                }

                private void applyComboVisibilityStyle(ComboBox<String> combo) {
                        combo.setStyle(FONT +
                                        "-fx-background-color: #ffffff; " +
                                        "-fx-border-color: #cbd5e1; " +
                                        "-fx-border-radius: 6; " +
                                        "-fx-background-radius: 6; " +
                                        "-fx-font-size: 12px; " +
                                        "-fx-font-weight: 700; " +
                                        "-fx-text-fill: #0f172a;");

                        // Ensure custom list cell explicitly renders dark bold text
                        combo.setButtonCell(new ListCell<String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty || item == null) {
                                                setText(null);
                                        } else {
                                                setText(item);
                                                setStyle(FONT + "-fx-text-fill: #0f172a; -fx-font-weight: 800; -fx-font-size: 12px;");
                                        }
                                }
                        });

                        combo.setCellFactory(lv -> new ListCell<String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty || item == null) {
                                                setText(null);
                                        } else {
                                                setText(item);
                                                setStyle(FONT + "-fx-text-fill: #0f172a; -fx-font-weight: 700; -fx-font-size: 12px; -fx-padding: 4 8;");
                                        }
                                }
                        });
                }

                public String getTimeString() {
                        String h = hourCombo.getEditor() != null && !hourCombo.getEditor().getText().trim().isEmpty()
                                        ? hourCombo.getEditor().getText().trim()
                                        : (hourCombo.getValue() != null ? hourCombo.getValue() : "00");

                        String m = minuteCombo.getEditor() != null
                                        && !minuteCombo.getEditor().getText().trim().isEmpty()
                                                        ? minuteCombo.getEditor().getText().trim()
                                                        : (minuteCombo.getValue() != null ? minuteCombo.getValue()
                                                                        : "00");

                        if (h.length() == 1)
                                h = "0" + h;
                        if (m.length() == 1)
                                m = "0" + m;

                        return h + ":" + m;
                }

                public void setTime(String time) {
                        if (time != null && time.contains(":")) {
                                String[] parts = time.trim().split(":");
                                if (parts.length >= 2) {
                                        String h = parts[0].trim();
                                        String m = parts[1].trim();
                                        if (h.length() == 1)
                                                h = "0" + h;
                                        if (m.length() == 1)
                                                m = "0" + m;
                                        hourCombo.setValue(h);
                                        if (hourCombo.getEditor() != null)
                                                hourCombo.getEditor().setText(h);
                                        minuteCombo.setValue(m);
                                        if (minuteCombo.getEditor() != null)
                                                minuteCombo.getEditor().setText(m);
                                }
                        }
                }
        }

        // =========================================================
        // CREATE ELECTION MODAL (WITH ACCESSIBLE TIME PICKERS)
        // =========================================================
        private void createElection() {
                Dialog<ButtonType> dialog = createBaseDialog("Create New Election",
                                "Establish schedule and ballot parameters", "🗳", "#2563eb", "#eff6ff");

                VBox box = new VBox(12);
                box.setPadding(new Insets(20));
                box.setPrefWidth(500);
                box.setStyle("-fx-background-color: #ffffff; " + FONT);

                TextField name = new TextField();
                name.setPromptText("e.g., Student Council General Election 2026");
                name.setStyle(FONT
                                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 10; -fx-font-size: 12px; -fx-text-fill: #0f172a;");

                TextArea description = new TextArea();
                description.setPromptText("Provide overview of election purpose and eligibility...");
                description.setPrefRowCount(3);
                description.setWrapText(true);
                description.setStyle(FONT
                                + "-fx-control-inner-background: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-font-size: 12px; -fx-padding: 6 8; -fx-text-fill: #0f172a;");

                // Start Date & Time Picker
                DatePicker startDate = new DatePicker(LocalDate.now());
                startDate.setStyle(FONT + "-fx-font-size: 12px;");
                startDate.setMaxWidth(Double.MAX_VALUE);
                TimePickerBox startTimePicker = new TimePickerBox("09", "00");

                HBox startBox = new HBox(10, startDate, startTimePicker);
                startBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(startDate, Priority.ALWAYS);

                // End Date & Time Picker
                DatePicker endDate = new DatePicker(LocalDate.now().plusDays(5));
                endDate.setStyle(FONT + "-fx-font-size: 12px;");
                endDate.setMaxWidth(Double.MAX_VALUE);
                TimePickerBox endTimePicker = new TimePickerBox("17", "00");

                HBox endBox = new HBox(10, endDate, endTimePicker);
                endBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(endDate, Priority.ALWAYS);

                box.getChildren().addAll(
                                createFieldLabel("Election Title"), name,
                                createFieldLabel("Description"), description,
                                createFieldLabel("Voting Window Start (Date & Time)"), startBox,
                                createFieldLabel("Voting Window End (Date & Time)"), endBox);

                dialog.getDialogPane().setContent(box);

                ButtonType saveButton = new ButtonType("Create Election", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, saveButton);
                styleDialogButtons(dialog, "#2563eb", saveButton);

                dialog.showAndWait().ifPresent(result -> {
                        if (result == saveButton) {
                                if (name.getText().trim().isEmpty()) {
                                        showError("Please enter an election title.");
                                        return;
                                }
                                if (startDate.getValue() == null || endDate.getValue() == null) {
                                        showError("Please select start and end dates.");
                                        return;
                                }

                                String electionName = name.getText().trim();
                                if (electionCards.containsKey(electionName)) {
                                        showError("An election with this name already exists.");
                                        return;
                                }

                                String electionDescription = description.getText().trim();
                                String start = startDate.getValue().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                                + " " + startTimePicker.getTimeString();
                                String end = endDate.getValue().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " "
                                                + endTimePicker.getTimeString();

                                // Save to Firestore in background thread
                                Thread t = new Thread(() -> {
                                        String error = ElectionController.createElection(
                                                electionName, electionDescription, start, end, "Draft", new ArrayList<>());
                                        Platform.runLater(() -> {
                                                if (error != null) {
                                                        showError("Firestore Error: " + error);
                                                } else {
                                                        // Reload elections from Firestore
                                                        loadElectionsFromFirestore();
                                                }
                                        });
                                });
                                t.setDaemon(true);
                                t.start();
                        }
                });
        }

        // =========================================================
        // CONFIGURE MULTIPLE POSITIONS MODAL
        // =========================================================
        private void createPositions() {
                Dialog<ButtonType> dialog = createBaseDialog("Configure Positions",
                                "Define electoral offices for active ballots", "⚙", "#2563eb", "#eff6ff");

                VBox mainBox = new VBox(12);
                mainBox.setPadding(new Insets(20));
                mainBox.setPrefWidth(480);
                mainBox.setStyle("-fx-background-color: #ffffff; " + FONT);

                ComboBox<String> election = new ComboBox<>();
                election.getItems().addAll(electionCards.keySet());
                election.setPromptText("Select Target Election...");
                election.setMaxWidth(Double.MAX_VALUE);
                election.setStyle(FONT
                                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 12px; -fx-padding: 4 8;");

                VBox positionBox = new VBox(8);
                positionBox.setPadding(new Insets(8));

                addPositionField(positionBox);

                ScrollPane positionScroll = new ScrollPane(positionBox);
                positionScroll.setFitToWidth(true);
                positionScroll.setPrefHeight(200);
                positionScroll.setStyle(
                                "-fx-background-color: #f8fafc; -fx-background: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

                Button addPositionButton = new Button("+ Add Another Office / Position");
                addPositionButton.setStyle(FONT
                                + "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-font-weight: 800; -fx-font-size: 11.5px; -fx-padding: 6 12; -fx-background-radius: 8; -fx-cursor: hand;");
                addPositionButton.setOnAction(e -> addPositionField(positionBox));

                mainBox.getChildren().addAll(
                                createFieldLabel("Select Target Election"), election,
                                createFieldLabel("Electoral Positions"), positionScroll,
                                addPositionButton);

                dialog.getDialogPane().setContent(mainBox);

                ButtonType saveButton = new ButtonType("Save Positions", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, saveButton);
                styleDialogButtons(dialog, "#2563eb", saveButton);

                dialog.showAndWait().ifPresent(result -> {
                        if (result == saveButton) {
                                if (election.getValue() == null) {
                                        showError("Please select a target election.");
                                        return;
                                }

                                List<String> newPositions = new ArrayList<>();
                                for (var node : positionBox.getChildren()) {
                                        if (node instanceof HBox) {
                                                HBox row = (HBox) node;
                                                if (row.getChildren().get(0) instanceof TextField) {
                                                        TextField field = (TextField) row.getChildren().get(0);
                                                        String value = field.getText().trim();
                                                        if (!value.isEmpty()) {
                                                                newPositions.add(value);
                                                        }
                                                }
                                        }
                                }

                                if (newPositions.isEmpty()) {
                                        showError("Please add at least one valid position.");
                                        return;
                                }

                                String electionName = election.getValue();
                                positions.get(electionName).clear();
                                positions.get(electionName).addAll(newPositions);
                                updatePositionLabel(electionName);

                                // Save positions to Firestore in background
                                String electionId = electionIds.get(electionName);
                                if (electionId != null) {
                                        Thread t = new Thread(() -> {
                                                String error = ElectionController.updatePositions(electionId, newPositions);
                                                if (error != null) {
                                                        Platform.runLater(() -> showError("Firestore Error: " + error));
                                                }
                                        });
                                        t.setDaemon(true);
                                        t.start();
                                }
                        }
                });
        }

        private void addPositionField(VBox positionBox) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                TextField position = new TextField();
                position.setPromptText("e.g., President, General Secretary, Treasurer");
                position.setStyle(FONT
                                + "-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12px; -fx-padding: 6 10;");
                HBox.setHgrow(position, Priority.ALWAYS);

                Button removeButton = new Button("✕");
                removeButton.setStyle(FONT
                                + "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-border-color: #fca5a5; -fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
                removeButton.setOnAction(e -> positionBox.getChildren().remove(row));

                row.getChildren().addAll(position, removeButton);
                positionBox.getChildren().add(row);
        }

        private void updatePositionLabel(String electionName) {
                Label label = positionLabels.get(electionName);
                if (label == null)
                        return;

                List<String> list = positions.get(electionName);
                if (list == null || list.isEmpty()) {
                        label.setText("Positions: None");
                        return;
                }

                label.setText("Positions: " + String.join(" • ", list));
        }

        // =========================================================
        // EDIT ELECTION MODAL (WITH PREPOPULATED TIME PICKERS)
        // =========================================================
        private void editElection(Label nameLabel, Label descriptionLabel, Label startLabel, Label endLabel,
                        Label scheduleLabel) {
                String oldName = nameLabel.getText();

                Dialog<ButtonType> dialog = createBaseDialog("Edit Election", "Modify election schedule and details",
                                "✏", "#2563eb", "#eff6ff");

                VBox box = new VBox(12);
                box.setPadding(new Insets(20));
                box.setPrefWidth(500);
                box.setStyle("-fx-background-color: #ffffff; " + FONT);

                TextField name = new TextField(oldName);
                name.setStyle(FONT
                                + "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 10; -fx-font-size: 12px; -fx-text-fill: #0f172a;");

                TextArea description = new TextArea(descriptionLabel.getText());
                description.setWrapText(true);
                description.setPrefRowCount(3);
                description.setStyle(FONT
                                + "-fx-control-inner-background: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-font-size: 12px; -fx-padding: 6 8; -fx-text-fill: #0f172a;");

                // Extract Existing Start Date & Time
                String rawStart = startLabel.getText().replace("Voting Start: ", "").trim();
                String startTimeStr = "09:00";
                if (rawStart.length() >= 5 && rawStart.contains(":")) {
                        startTimeStr = rawStart.substring(rawStart.lastIndexOf(" ") + 1);
                }

                DatePicker startDate = new DatePicker();
                startDate.setStyle(FONT + "-fx-font-size: 12px;");
                startDate.setMaxWidth(Double.MAX_VALUE);
                TimePickerBox startTimePicker = new TimePickerBox("09", "00");
                startTimePicker.setTime(startTimeStr);

                HBox startBox = new HBox(10, startDate, startTimePicker);
                startBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(startDate, Priority.ALWAYS);

                // Extract Existing End Date & Time
                String rawEnd = endLabel.getText().replace("Voting End: ", "").trim();
                String endTimeStr = "17:00";
                if (rawEnd.length() >= 5 && rawEnd.contains(":")) {
                        endTimeStr = rawEnd.substring(rawEnd.lastIndexOf(" ") + 1);
                }

                DatePicker endDate = new DatePicker();
                endDate.setStyle(FONT + "-fx-font-size: 12px;");
                endDate.setMaxWidth(Double.MAX_VALUE);
                TimePickerBox endTimePicker = new TimePickerBox("17", "00");
                endTimePicker.setTime(endTimeStr);

                HBox endBox = new HBox(10, endDate, endTimePicker);
                endBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(endDate, Priority.ALWAYS);

                box.getChildren().addAll(
                                createFieldLabel("Election Title"), name,
                                createFieldLabel("Description"), description,
                                createFieldLabel("Voting Window Start (Date & Time)"), startBox,
                                createFieldLabel("Voting Window End (Date & Time)"), endBox);

                dialog.getDialogPane().setContent(box);

                ButtonType updateBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, updateBtn);
                styleDialogButtons(dialog, "#2563eb", updateBtn);

                dialog.showAndWait().ifPresent(result -> {
                        if (result == updateBtn) {
                                String newName = name.getText().trim();
                                if (newName.isEmpty()) {
                                        showError("Election title cannot be empty.");
                                        return;
                                }

                                if (!newName.equals(oldName)) {
                                        if (electionCards.containsKey(newName)) {
                                                showError("An election with this name already exists.");
                                                return;
                                        }

                                        List<String> oldPositions = positions.remove(oldName);
                                        Label oldPositionLabel = positionLabels.remove(oldName);
                                        HBox oldCard = electionCards.remove(oldName);

                                        positions.put(newName, oldPositions);
                                        positionLabels.put(newName, oldPositionLabel);
                                        electionCards.put(newName, oldCard);

                                        nameLabel.setText(newName);
                                }

                                descriptionLabel.setText(description.getText());

                                String newStart = (startDate.getValue() != null)
                                                ? startDate.getValue()
                                                                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                                                + " " + startTimePicker.getTimeString()
                                                : (rawStart.contains(" ")
                                                                ? rawStart.substring(0, rawStart.lastIndexOf(" ")) + " "
                                                                                + startTimePicker.getTimeString()
                                                                : rawStart);

                                String newEnd = (endDate.getValue() != null)
                                                ? endDate.getValue().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                                                + " " + endTimePicker.getTimeString()
                                                : (rawEnd.contains(" ")
                                                                ? rawEnd.substring(0, rawEnd.lastIndexOf(" ")) + " "
                                                                                + endTimePicker.getTimeString()
                                                                : rawEnd);

                                startLabel.setText("Voting Start: " + newStart);
                                endLabel.setText("Voting End: " + newEnd);
                                scheduleLabel.setText("⏱ " + newStart + "  ➔  " + newEnd);
                        }
                });
        }

        // =========================================================
        // UI BUILDER HELPERS
        // =========================================================
        private Label createFieldLabel(String text) {
                Label label = new Label(text);
                label.setStyle(FONT + "-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-text-fill: #1e1b4b;");
                return label;
        }

        private VBox createMetaField(String title, String val) {
                VBox v = new VBox(2);
                Label l = new Label(title);
                l.setStyle(FONT + "-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-text-fill: #64748b;");

                Label value = new Label(val);
                value.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
                v.getChildren().addAll(l, value);
                return v;
        }

        private Dialog<ButtonType> createBaseDialog(String title, String headerSubtitle, String iconGlyph,
                        String iconColor, String iconBgHex) {
                Dialog<ButtonType> dialog = new Dialog<>();
                if (AdminDashboard.AdminDashboardStage != null) {
                        dialog.initOwner(AdminDashboard.AdminDashboardStage);
                }
                dialog.setTitle(title);

                HBox headerBox = new HBox(12);
                headerBox.setAlignment(Pos.CENTER_LEFT);
                headerBox.setPadding(new Insets(14, 20, 14, 20));
                headerBox.setStyle(
                                "-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1.2 0;");

                Circle iconCircle = new Circle(16, Color.web(iconBgHex));
                Label icon = new Label(iconGlyph);
                icon.setStyle(FONT + "-fx-font-size: 14px;");
                StackPane iconPane = new StackPane(iconCircle, icon);

                VBox titleArea = new VBox(2);
                Label mainTitle = new Label(title);
                mainTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1e1b4b;");
                Label sub = new Label(headerSubtitle);
                sub.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #4338ca; -fx-font-weight: 600;");
                titleArea.getChildren().addAll(mainTitle, sub);

                headerBox.getChildren().addAll(iconPane, titleArea);
                dialog.getDialogPane().setHeader(headerBox);
                dialog.getDialogPane().setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: #818cf8; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12; "
                                                + FONT);

                return dialog;
        }

        private void styleDialogButtons(Dialog<ButtonType> dialog, String primaryColor, ButtonType primaryType) {
                Button primaryBtn = (Button) dialog.getDialogPane().lookupButton(primaryType);
                if (primaryBtn != null) {
                        primaryBtn.setStyle(FONT + "-fx-background-color: " + primaryColor
                                        + "; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;");
                }

                Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                if (cancelBtn != null) {
                        cancelBtn.setStyle(FONT
                                        + "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: 800; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-border-color: #cbd5e1; -fx-border-radius: 8;");
                }
        }

        private void showError(String message) {
                Dialog<ButtonType> alert = new Dialog<>();
                if (AdminDashboard.AdminDashboardStage != null) {
                        alert.initOwner(AdminDashboard.AdminDashboardStage);
                }
                alert.setTitle("Invalid Information");

                VBox contentBox = new VBox(14);
                contentBox.setPadding(new Insets(20));
                contentBox.setPrefWidth(380);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setStyle("-fx-background-color: #ffffff; " + FONT);

                Circle outerHalo = new Circle(26, Color.web("#fee2e2"));
                Circle innerCircle = new Circle(18, Color.web("#ef4444"));
                Label icon = new Label("⚠️");
                icon.setStyle(FONT + "-fx-font-size: 14px;");
                StackPane iconStack = new StackPane(outerHalo, innerCircle, icon);

                VBox textBox = new VBox(4);
                textBox.setAlignment(Pos.CENTER);
                Label title = new Label("Action Required");
                title.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #991b1b;");
                Label desc = new Label(message);
                desc.setWrapText(true);
                desc.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-text-alignment: CENTER;");
                textBox.getChildren().addAll(title, desc);

                contentBox.getChildren().addAll(iconStack, textBox);
                alert.getDialogPane().setContent(contentBox);
                alert.getDialogPane().setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

                ButtonType okBtnType = new ButtonType("Acknowledge", ButtonBar.ButtonData.OK_DONE);
                alert.getDialogPane().getButtonTypes().add(okBtnType);

                Button okBtn = (Button) alert.getDialogPane().lookupButton(okBtnType);
                if (okBtn != null) {
                        okBtn.setStyle(FONT
                                        + "-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 7 16; -fx-cursor: hand;");
                }

                alert.showAndWait();
        }
}