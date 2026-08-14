package com.electravote.admin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ElectionPage extends VBox {

    private VBox electionList;

    // Election -> Positions
    private Map<String, List<String>> positions =
            new LinkedHashMap<>();

    // Election -> Position Label
    private Map<String, Label> positionLabels =
            new LinkedHashMap<>();

    // Election -> Card
    private Map<String, HBox> electionCards =
            new LinkedHashMap<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ElectionPage() {

        setSpacing(20);
        setPadding(new Insets(25));

        setStyle(
                "-fx-background-color: #f5f7fb;"
        );

        HBox header = new HBox();

        header.setAlignment(Pos.CENTER_LEFT);

        Label title =new Label("Elections Management");

        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Region headerSpace =new Region();

        HBox.setHgrow(headerSpace,Priority.ALWAYS);

        Button createElectionButton =new Button("+ Create Election");

        createElectionButton.setStyle("-fx-background-color: #1464F4;-fx-text-fill: white; -fx-padding: 10 15;-fx-background-radius: 6;");

        createElectionButton.setOnAction(e -> 
                createElection());

        header.getChildren().addAll(
                title,
                headerSpace,
                createElectionButton
        );

        HBox actions =new HBox(10);


        Button positionButton =new Button("Create Multiple Positions");

        positionButton.setOnAction(e -> 
                createPositions());


        actions.getChildren().add(positionButton);

        electionList =new VBox(10);


        ScrollPane electionScroll =new ScrollPane(electionList);

        electionScroll.setFitToWidth(true);

        electionScroll.setStyle("-fx-background-color: transparent;");


        VBox.setVgrow(electionScroll,Priority.ALWAYS);

        // ADD TO PAGE

        getChildren().addAll(
                header,
                actions,
                electionScroll
        );
    }

    // ADD ELECTION CARD
    private void addElection(String name,String description,String start,String end,String status) {

        HBox card =new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white;-fx-background-radius: 8;");

        // INFORMATION

        VBox information =new VBox(6);

        Label nameLabel =new Label(name);

        nameLabel.setStyle("-fx-font-size: 17px;-fx-font-weight: bold;");


        Label descriptionLabel =new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );


        // ==============================
        // POSITION LABEL
        // ==============================

        Label positionLabel =
                new Label(
                        "Positions: None"
                );

        positionLabel.setWrapText(true);

        positionLabel.setStyle(
                "-fx-text-fill: #334155;"
        );


        // Store label

        positionLabels.put(
                name,
                positionLabel
        );


        // Create empty position list

        positions.put(
                name,
                new ArrayList<>()
        );



        // VOTING TIME

        Label startLabel =new Label("Voting Start: " + start);


        Label endLabel =new Label("Voting End: " + end);

        // STATUS

        Label statusLabel =new Label(status);

        statusLabel.setStyle("-fx-text-fill: #1464F4;-fx-font-weight: bold;");
        // ADD INFORMATION

        information.getChildren().addAll(
                nameLabel,
                descriptionLabel,
                positionLabel,
                startLabel,
                endLabel,
                statusLabel
        );

        // SPACE

        Region cardSpace =
                new Region();

        HBox.setHgrow(cardSpace,Priority.ALWAYS);

        // BUTTONS
        Button viewButton =new Button("View");
        Button editButton =new Button("Edit");
        Button deleteButton =new Button("Delete");


        viewButton.setStyle("-fx-background-color: #eef4ff; -fx-text-fill: #1464F4;");
        editButton.setStyle("-fx-background-color: #f1f5f9;");


        deleteButton.setStyle("-fx-background-color: #fee2e2;-fx-text-fill: #dc2626;");

        // VIEW
        viewButton.setOnAction(e -> {

            Alert alert =new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Election Details");
            alert.setHeaderText(nameLabel.getText());
            alert.setContentText(

                    descriptionLabel.getText()
                    + "\n\n"
                    + positionLabel.getText()
                    + "\n\n"
                    + startLabel.getText()
                    + "\n"
                    + endLabel.getText()
                    + "\n\n"
                    + "Status: "
                    + statusLabel.getText());


            alert.showAndWait();
        });


        // ==============================
        // EDIT
        // ==============================

        editButton.setOnAction(e -> {

            editElection(
                    nameLabel,
                    descriptionLabel,
                    startLabel,
                    endLabel
            );
        });


        // ==============================
        // DELETE
        // ==============================

        deleteButton.setOnAction(e -> {

            Alert alert =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );


            alert.setTitle(
                    "Delete Election"
            );


            alert.setHeaderText(
                    "Delete " +
                    nameLabel.getText() +
                    "?"
            );


            alert.setContentText(
                    "This election will be removed."
            );


            alert.showAndWait()
                    .ifPresent(result -> {

                        if (result ==
                                ButtonType.OK) {

                            electionList
                                    .getChildren()
                                    .remove(card);


                            String electionName =
                                    nameLabel.getText();


                            positions.remove(
                                    electionName
                            );


                            positionLabels.remove(
                                    electionName
                            );


                            electionCards.remove(
                                    electionName
                            );
                        }
                    });
        });


        // ==============================
        // ADD BUTTONS TO CARD
        // ==============================

        card.getChildren().addAll(

                information,

                cardSpace,

                viewButton,

                editButton,

                deleteButton

        );


        // Store card

        electionCards.put(
                name,
                card
        );


        // Add to list

        electionList
                .getChildren()
                .add(card);
    }


    // =========================================================
    // CREATE ELECTION
    // =========================================================

    private void createElection() {

        Dialog<ButtonType> dialog =
                new Dialog<>();


        dialog.setTitle(
                "Create New Election"
        );


        dialog.setHeaderText(
                "Enter Election Details"
        );


        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );


        // ==============================
        // ELECTION NAME
        // ==============================

        TextField name =
                new TextField();

        name.setPromptText(
                "Enter election name"
        );


        // ==============================
        // DESCRIPTION
        // ==============================

        TextArea description =
                new TextArea();

        description.setPromptText(
                "Enter election description"
        );

        description.setPrefRowCount(3);

        description.setWrapText(true);


        // ==============================
        // START DATE & TIME
        // ==============================

        DatePicker startDate =
                new DatePicker();


        TextField startTime =
                new TextField();

        startTime.setPromptText(
                "HH:mm"
        );


        HBox startBox =
                new HBox(
                        10,
                        startDate,
                        startTime
                );


        // ==============================
        // END DATE & TIME
        // ==============================

        DatePicker endDate =
                new DatePicker();


        TextField endTime =
                new TextField();

        endTime.setPromptText(
                "HH:mm"
        );


        HBox endBox =
                new HBox(
                        10,
                        endDate,
                        endTime
                );


        // ==============================
        // FORM
        // ==============================

        box.getChildren().addAll(

                new Label(
                        "Election Name"
                ),

                name,


                new Label(
                        "Description"
                ),

                description,


                new Label(
                        "Voting Start"
                ),

                startBox,


                new Label(
                        "Voting End"
                ),

                endBox

        );


        dialog.getDialogPane()
                .setContent(box);


        // ==============================
        // BUTTONS
        // ==============================

        ButtonType saveButton =
                new ButtonType(
                        "Create Election",
                        ButtonBar.ButtonData.OK_DONE
                );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(

                        ButtonType.CANCEL,

                        saveButton

                );


        // ==============================
        // SAVE
        // ==============================

        dialog.showAndWait()
                .ifPresent(result -> {

                    if (result ==
                            saveButton) {


                        // Validate name

                        if (name.getText()
                                .trim()
                                .isEmpty()) {

                            showError(
                                    "Enter election name."
                            );

                            return;
                        }


                        // Validate dates

                        if (startDate.getValue()
                                == null ||
                            endDate.getValue()
                                == null) {

                            showError(
                                    "Select voting dates."
                            );

                            return;
                        }


                        // Validate times

                        if (startTime.getText()
                                .trim()
                                .isEmpty() ||
                            endTime.getText()
                                .trim()
                                .isEmpty()) {

                            showError(
                                    "Enter voting times."
                            );

                            return;
                        }


                        String electionName =
                                name.getText()
                                .trim();


                        // Check duplicate

                        if (electionCards
                                .containsKey(
                                        electionName
                                )) {

                            showError(
                                    "Election already exists."
                            );

                            return;
                        }


                        String electionDescription =
                                description
                                .getText()
                                .trim();


                        String start =
                                startDate.getValue()
                                + " "
                                + startTime
                                .getText()
                                .trim();


                        String end =
                                endDate.getValue()
                                + " "
                                + endTime
                                .getText()
                                .trim();


                        // Add election

                        addElection(

                                electionName,

                                electionDescription,

                                start,

                                end,

                                "Draft"

                        );
                    }
                });
    }


    // =========================================================
    // CREATE MULTIPLE POSITIONS
    // =========================================================

    private void createPositions() {

        Dialog<ButtonType> dialog =
                new Dialog<>();


        dialog.setTitle(
                "Create Multiple Positions"
        );


        dialog.setHeaderText(
                "Add Positions to Election"
        );


        VBox mainBox =
                new VBox(12);

        mainBox.setPadding(
                new Insets(20)
        );


        // ==============================
        // SELECT ELECTION
        // ==============================

        ComboBox<String> election =
                new ComboBox<>();


        // Get all currently created elections

        election.getItems().addAll(
                electionCards.keySet()
        );


        election.setPromptText(
                "Select Election"
        );


        election.setMaxWidth(
                Double.MAX_VALUE
        );


        // ==============================
        // POSITION BOX
        // ==============================

        VBox positionBox =
                new VBox(8);


        positionBox.setPadding(
                new Insets(5)
        );


        // Add first position

        addPositionField(
                positionBox
        );


        // ==============================
        // SCROLL
        // ==============================

        ScrollPane positionScroll =
                new ScrollPane(
                        positionBox
                );


        positionScroll.setFitToWidth(
                true
        );


        positionScroll.setPrefHeight(
                250
        );


        positionScroll.setMaxHeight(
                250
        );


        positionScroll.setStyle(
                "-fx-background-color: #f8fafc;"
        );


        // ==============================
        // ADD POSITION
        // ==============================

        Button addPositionButton =
                new Button(
                        "+ Add Position"
                );


        addPositionButton.setStyle(
                "-fx-background-color: #eef4ff;" +
                "-fx-text-fill: #1464F4;"
        );


        addPositionButton.setOnAction(
                e -> addPositionField(
                        positionBox
                )
        );


        // ==============================
        // FORM
        // ==============================

        mainBox.getChildren().addAll(

                new Label(
                        "Select Election"
                ),

                election,


                new Label(
                        "Position Name"
                ),

                positionScroll,


                addPositionButton

        );


        dialog.getDialogPane()
                .setContent(mainBox);


        // ==============================
        // SAVE BUTTON
        // ==============================

        ButtonType saveButton =
                new ButtonType(
                        "Save Positions",
                        ButtonBar.ButtonData.OK_DONE
                );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(

                        ButtonType.CANCEL,

                        saveButton

                );


        // ==============================
        // SAVE POSITIONS
        // ==============================

        dialog.showAndWait()
                .ifPresent(result -> {

                    if (result ==
                            saveButton) {


                        // Check election

                        if (election.getValue()
                                == null) {

                            showError(
                                    "Select an election."
                            );

                            return;
                        }


                        List<String> newPositions =
                                new ArrayList<>();


                        // Read all fields

                        for (var node :
                                positionBox
                                .getChildren()) {


                            if (node instanceof HBox) {

                                HBox row =
                                        (HBox) node;


                                if (row.getChildren()
                                        .get(0)
                                        instanceof TextField) {


                                    TextField field =
                                            (TextField)
                                            row.getChildren()
                                            .get(0);


                                    String value =
                                            field.getText()
                                            .trim();


                                    if (!value
                                            .isEmpty()) {

                                        newPositions.add(
                                                value
                                        );
                                    }
                                }
                            }
                        }


                        // Validate

                        if (newPositions
                                .isEmpty()) {

                            showError(
                                    "Add at least one position."
                            );

                            return;
                        }


                        // ==============================
                        // SAVE TO MAP
                        // ==============================

                        String electionName =
                                election.getValue();


                        positions
                                .get(electionName)
                                .clear();


                        positions
                                .get(electionName)
                                .addAll(
                                        newPositions
                                );


                        // ==============================
                        // UPDATE CARD
                        // ==============================

                        updatePositionLabel(
                                electionName
                        );
                    }
                });
    }


    // =========================================================
    // ADD POSITION FIELD
    // =========================================================

    private void addPositionField(
            VBox positionBox) {


        HBox row =
                new HBox(10);


        row.setAlignment(
                Pos.CENTER_LEFT
        );


        TextField position =
                new TextField();


        position.setPromptText(
                "Enter Position Name"
        );


        HBox.setHgrow(
                position,
                Priority.ALWAYS
        );


        Button removeButton =
                new Button(
                        "Remove"
                );


        removeButton.setStyle(
                "-fx-text-fill: #dc2626;"
        );


        removeButton.setOnAction(
                e -> positionBox
                        .getChildren()
                        .remove(row)
        );


        row.getChildren().addAll(

                position,

                removeButton

        );


        positionBox
                .getChildren()
                .add(row);
    }


    // =========================================================
    // UPDATE POSITION ON CARD
    // =========================================================

    private void updatePositionLabel(
            String electionName) {


        Label label =
                positionLabels.get(
                        electionName
                );


        if (label == null) {
            return;
        }


        List<String> list =
                positions.get(
                        electionName
                );


        if (list == null ||
                list.isEmpty()) {

            label.setText(
                    "Positions: None"
            );

            return;
        }


        StringBuilder text =
                new StringBuilder(
                        "Positions: "
                );


        for (int i = 0;
             i < list.size();
             i++) {


            text.append(
                    list.get(i)
            );


            if (i <
                    list.size() - 1) {

                text.append(
                        " • "
                );
            }
        }


        label.setText(
                text.toString()
        );
    }


    // =========================================================
    // EDIT ELECTION
    // =========================================================

    private void editElection(
            Label nameLabel,
            Label descriptionLabel,
            Label startLabel,
            Label endLabel) {


        String oldName =
                nameLabel.getText();


        Dialog<ButtonType> dialog =
                new Dialog<>();


        dialog.setTitle(
                "Edit Election"
        );


        dialog.setHeaderText(
                "Update Election"
        );


        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );


        TextField name =
                new TextField(
                        oldName
                );


        TextArea description =
                new TextArea(
                        descriptionLabel
                        .getText()
                );


        description.setWrapText(
                true
        );


        DatePicker startDate =
                new DatePicker();


        TextField startTime =
                new TextField();


        startTime.setPromptText(
                "HH:mm"
        );


        DatePicker endDate =
                new DatePicker();


        TextField endTime =
                new TextField();


        endTime.setPromptText(
                "HH:mm"
        );


        box.getChildren().addAll(

                new Label(
                        "Election Name"
                ),

                name,


                new Label(
                        "Description"
                ),

                description,


                new Label(
                        "Voting Start"
                ),

                startDate,

                startTime,


                new Label(
                        "Voting End"
                ),

                endDate,

                endTime

        );


        dialog.getDialogPane()
                .setContent(box);


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(

                        ButtonType.CANCEL,

                        ButtonType.OK

                );


        dialog.showAndWait()
                .ifPresent(result -> {

                    if (result ==
                            ButtonType.OK) {


                        String newName =
                                name.getText()
                                .trim();


                        if (newName.isEmpty()) {

                            showError(
                                    "Election name cannot be empty."
                            );

                            return;
                        }


                        // ==============================
                        // NAME CHANGED
                        // ==============================

                        if (!newName.equals(
                                oldName)) {


                            if (electionCards
                                    .containsKey(
                                            newName
                                    )) {

                                showError(
                                        "Election already exists."
                                );

                                return;
                            }


                            List<String> oldPositions =
                                    positions.remove(
                                            oldName
                                    );


                            Label oldPositionLabel =
                                    positionLabels.remove(
                                            oldName
                                    );


                            HBox oldCard =
                                    electionCards.remove(
                                            oldName
                                    );


                            positions.put(
                                    newName,
                                    oldPositions
                            );


                            positionLabels.put(
                                    newName,
                                    oldPositionLabel
                            );


                            electionCards.put(
                                    newName,
                                    oldCard
                            );


                            nameLabel.setText(
                                    newName
                            );
                        }


                        // ==============================
                        // DESCRIPTION
                        // ==============================

                        descriptionLabel.setText(
                                description
                                .getText()
                        );


                        // ==============================
                        // START
                        // ==============================

                        if (startDate.getValue()
                                != null) {

                            startLabel.setText(
                                    "Voting Start: "
                                    + startDate
                                    .getValue()
                                    + " "
                                    + startTime
                                    .getText()
                            );
                        }


                        // ==============================
                        // END
                        // ==============================

                        if (endDate.getValue()
                                != null) {

                            endLabel.setText(
                                    "Voting End: "
                                    + endDate
                                    .getValue()
                                    + " "
                                    + endTime
                                    .getText()
                            );
                        }
                    }
                });
    }


    // =========================================================
    // ERROR ALERT
    // =========================================================

    private void showError(
            String message) {


        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "Invalid Information"
        );


        alert.setHeaderText(
                null
        );


        alert.setContentText(
                message
        );


        alert.showAndWait();
    }
}