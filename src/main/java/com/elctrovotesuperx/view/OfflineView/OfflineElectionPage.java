package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.controller.OfflineController.OfflineMembersController;
import com.electrovotesuperx.dao.OfflineDAO.ElectionDAO;
import com.electrovotesuperx.model.OfflineModel.Election;
import com.electrovotesuperx.view.CommonView.Header;
import com.electrovotesuperx.view.CommonView.Sidebar;
import com.electrovotesuperx.utils.Navigation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class OfflineElectionPage {

        // =========================================================
        // SCENE
        // =========================================================

        private final Scene scene;

        // =========================================================
        // DATABASE
        // =========================================================

        private final ElectionDAO electionDAO = new ElectionDAO();

        // =========================================================
        // MEMBER CONTROLLER
        // =========================================================

        private final OfflineMembersController membersController = new OfflineMembersController();

        // =========================================================
        // ELECTION DATA
        // =========================================================

        private final ObservableList<ElectionData> elections = FXCollections.observableArrayList();

        // =========================================================
        // ELECTION LIST UI
        // =========================================================

        private final VBox electionList = new VBox(0);

        // =========================================================
        // CONSTRUCTOR
        // =========================================================

        public OfflineElectionPage() {

                // =========================================================
                // ROOT
                // =========================================================

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color: #F7F9FC;");

                // =========================================================
                // SIDEBAR
                // KEEP SIDEBAR UNCHANGED
                // =========================================================

                root.setLeft(
                                Sidebar.getSidebar("ElectionPage"));

                // =========================================================
                // CENTER
                // =========================================================

                VBox center = new VBox();

                // =========================================================
                // HEADER
                // KEEP HEADER UNCHANGED
                // =========================================================

                HBox topBar = new HBox();

                topBar.getChildren().add(
                                Header.getHeader(
                                                "Elections",
                                                "Create and manage offline elections"));

                // =========================================================
                // PAGE CONTENT
                // =========================================================

                VBox content = new VBox(24);

                content.setPadding(
                                new Insets(30, 35, 40, 35));

                content.setStyle(
                                "-fx-background-color: #F7F9FC;");

                // =========================================================
                // PAGE TITLE
                // =========================================================

                Label title = new Label(
                                "Elections");

                title.setStyle(
                                "-fx-font-size: 30px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #172B4D;");

                // =========================================================
                // PAGE SUBTITLE
                // =========================================================

                Label subtitle = new Label(
                                "Create and manage elections available for offline voter verification.");

                subtitle.setStyle(
                                "-fx-font-size: 14px;" +
                                                "-fx-text-fill: #71829B;");

                subtitle.setWrapText(true);

                // =========================================================
                // TITLE SECTION
                // =========================================================

                VBox titleBox = new VBox(6);

                titleBox.getChildren().addAll(
                                title,
                                subtitle);

                // =========================================================
                // CREATE ELECTION BUTTON
                // =========================================================

                Button createButton = new Button(
                                "+  Create Election");

                createButton.setPrefHeight(44);
                createButton.setPrefWidth(175);

                createButton.setStyle(
                                "-fx-background-color: #356AE6;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-size: 14px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-background-radius: 10px;" +
                                                "-fx-cursor: hand;");

                // =========================================================
                // CREATE BUTTON HOVER
                // =========================================================

                createButton.setOnMouseEntered(event -> {

                        createButton.setStyle(
                                        "-fx-background-color: #2857C7;" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-size: 14px;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-background-radius: 10px;" +
                                                        "-fx-cursor: hand;");
                });

                createButton.setOnMouseExited(event -> {

                        createButton.setStyle(
                                        "-fx-background-color: #356AE6;" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-size: 14px;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-background-radius: 10px;" +
                                                        "-fx-cursor: hand;");
                });

                createButton.setOnAction(
                                event -> showCreateElectionDialog());

                // =========================================================
                // TOP ACTION AREA
                // =========================================================

                HBox actionBar = new HBox();

                actionBar.setAlignment(
                                Pos.CENTER_LEFT);

                actionBar.getChildren().addAll(
                                titleBox);

                Region actionSpacer = new Region();

                HBox.setHgrow(
                                actionSpacer,
                                Priority.ALWAYS);

                actionBar.getChildren().add(
                                actionSpacer);

                actionBar.getChildren().add(
                                createButton);

                // =========================================================
                // ELECTION LIST CARD
                // =========================================================

                VBox card = new VBox();

                card.setPadding(
                                new Insets(0));

                card.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-background-radius: 16px;" +
                                                "-fx-border-color: #E5ECF4;" +
                                                "-fx-border-radius: 16px;");

                // =========================================================
                // TABLE HEADER
                // =========================================================

                GridPane headerRow = createGrid();

                Label electionHeader = headerLabel("Election");

                Label memberHeader = headerLabel("Members");

                Label statusHeader = headerLabel("Status");

                Label actionHeader = headerLabel("Action");

                headerRow.add(
                                electionHeader,
                                0,
                                0);

                headerRow.add(
                                memberHeader,
                                1,
                                0);

                headerRow.add(
                                statusHeader,
                                2,
                                0);

                headerRow.add(
                                actionHeader,
                                3,
                                0);

                headerRow.setPadding(
                                new Insets(
                                                0,
                                                22,
                                                0,
                                                22));

                headerRow.setStyle(
                                "-fx-background-color: #F8FAFC;" +
                                                "-fx-background-radius: 16px 16px 0 0;" +
                                                "-fx-border-color: #E5ECF4;" +
                                                "-fx-border-width: 0 0 1 0;");

                // =========================================================
                // LOAD ELECTIONS
                // =========================================================

                loadElections();

                // =========================================================
                // LIST SCROLL
                // =========================================================

                ScrollPane listScroll = new ScrollPane(electionList);

                listScroll.setFitToWidth(true);

                listScroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                listScroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                listScroll.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background: transparent;" +
                                                "-fx-border-color: transparent;");

                VBox.setVgrow(
                                listScroll,
                                Priority.ALWAYS);

                // =========================================================
                // ADD TABLE CONTENT
                // =========================================================

                card.getChildren().addAll(
                                headerRow,
                                listScroll);

                VBox.setVgrow(
                                card,
                                Priority.ALWAYS);

                // =========================================================
                // ADD PAGE CONTENT
                // =========================================================

                content.getChildren().addAll(
                                actionBar,
                                card);

                VBox.setVgrow(
                                card,
                                Priority.ALWAYS);

                // =========================================================
                // PAGE SCROLL
                // =========================================================

                ScrollPane pageScroll = new ScrollPane(content);

                pageScroll.setFitToWidth(true);

                pageScroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                pageScroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                pageScroll.setStyle(
                                "-fx-background-color: #F7F9FC;" +
                                                "-fx-background: #F7F9FC;");

                VBox.setVgrow(
                                pageScroll,
                                Priority.ALWAYS);

                // =========================================================
                // CENTER CONTENT
                // =========================================================

                center.getChildren().addAll(
                                topBar,
                                pageScroll);

                // =========================================================
                // ROOT CENTER
                // =========================================================

                root.setCenter(center);

                // =========================================================
                // SCENE
                // =========================================================

                scene = new Scene(root);
        }

        // =============================================================
        // CREATE GRID
        // =============================================================

        private GridPane createGrid() {

                GridPane grid = new GridPane();

                // ---------------------------------------------------------
                // Election = 45%
                // ---------------------------------------------------------

                ColumnConstraints first = new ColumnConstraints();

                first.setPercentWidth(45);

                // ---------------------------------------------------------
                // Members = 15%
                // ---------------------------------------------------------

                ColumnConstraints second = new ColumnConstraints();

                second.setPercentWidth(15);

                // ---------------------------------------------------------
                // Status = 15%
                // ---------------------------------------------------------

                ColumnConstraints third = new ColumnConstraints();

                third.setPercentWidth(15);

                // ---------------------------------------------------------
                // Action = 25%
                // ---------------------------------------------------------

                ColumnConstraints fourth = new ColumnConstraints();

                fourth.setPercentWidth(25);

                grid.getColumnConstraints().addAll(
                                first,
                                second,
                                third,
                                fourth);

                return grid;
        }

        // =============================================================
        // HEADER LABEL
        // =============================================================

        private Label headerLabel(String text) {

                Label label = new Label(text);

                label.setStyle(
                                "-fx-font-size: 12px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #64748B;");

                label.setPadding(
                                new Insets(
                                                16,
                                                0,
                                                16,
                                                0));

                return label;
        }

        // =============================================================
        // LOAD ELECTIONS
        // =============================================================

        private void loadElections() {

                try {

                        elections.clear();

                        List<Election> databaseElections = electionDAO.getAllElections();

                        for (Election election : databaseElections) {

                                // =================================================
                                // GET MEMBER COUNT
                                // =================================================

                                int memberCount = 0;

                                try {

                                        List<?> members = membersController.getMembersForElection(
                                                        election.getElectionId());

                                        if (members != null) {
                                                memberCount = members.size();
                                        }

                                } catch (Exception memberException) {

                                        memberException.printStackTrace();

                                        memberCount = 0;
                                }

                                // =================================================
                                // ADD ELECTION
                                // =================================================

                                elections.add(
                                                new ElectionData(
                                                                election.getElectionId(),
                                                                election.getName(),
                                                                election.getStatus(),
                                                                memberCount));
                        }

                        // =================================================
                        // SORT ELECTIONS
                        // =================================================
                        //
                        // OPEN elections come first.
                        // CLOSED elections go last.
                        //
                        // =================================================

                        elections.sort(
                                        Comparator.comparing(
                                                        election -> "CLOSED".equalsIgnoreCase(
                                                                        election.status)));

                        // =================================================
                        // REFRESH UI
                        // =================================================

                        refreshElectionList();

                } catch (SQLException e) {

                        e.printStackTrace();

                        showMessage(
                                        Alert.AlertType.ERROR,
                                        "Database Error",
                                        "Could not load elections from SQLite.\n\n"
                                                        + e.getMessage());
                }
        }

        // =============================================================
        // REFRESH ELECTION LIST
        // =============================================================

        private void refreshElectionList() {

                electionList.getChildren().clear();

                // =========================================================
                // EMPTY STATE
                // =========================================================

                if (elections.isEmpty()) {

                        VBox emptyBox = new VBox(10);

                        emptyBox.setAlignment(
                                        Pos.CENTER);

                        emptyBox.setPadding(
                                        new Insets(60));

                        Label icon = new Label("＋");

                        icon.setStyle(
                                        "-fx-font-size: 30px;" +
                                                        "-fx-text-fill: #94A3B8;");

                        Label emptyTitle = new Label(
                                        "No elections found");

                        emptyTitle.setStyle(
                                        "-fx-font-size: 16px;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-text-fill: #475569;");

                        Label emptyText = new Label(
                                        "Create your first election using the button above.");

                        emptyText.setStyle(
                                        "-fx-font-size: 13px;" +
                                                        "-fx-text-fill: #94A3B8;");

                        emptyBox.getChildren().addAll(
                                        icon,
                                        emptyTitle,
                                        emptyText);

                        electionList.getChildren().add(
                                        emptyBox);

                        return;
                }

                // =========================================================
                // ADD ROWS
                // =========================================================

                for (ElectionData election : elections) {

                        electionList.getChildren().add(
                                        createElectionRow(election));
                }
        }

        // =============================================================
        // CREATE ELECTION ROW
        // =============================================================

        private GridPane createElectionRow(
                        ElectionData election) {

                GridPane grid = createGrid();

                grid.setPadding(
                                new Insets(
                                                18,
                                                22,
                                                18,
                                                22));

                grid.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-border-color: #EEF2F7;" +
                                                "-fx-border-width: 0 0 1 0;");

                // =========================================================
                // ELECTION INFORMATION
                // =========================================================

                VBox electionInfo = new VBox(4);

                Label name = new Label(
                                election.name);

                name.setStyle(
                                "-fx-font-size: 15px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #172B4D;");

                Label id = new Label(
                                "ID: " + election.electionId);

                id.setStyle(
                                "-fx-font-size: 11px;" +
                                                "-fx-text-fill: #94A3B8;");

                electionInfo.getChildren().addAll(
                                name,
                                id);

                // =========================================================
                // MEMBER COUNT
                // =========================================================

                HBox memberBox = new HBox(8);

                memberBox.setAlignment(
                                Pos.CENTER_LEFT);

                Label memberNumber = new Label(
                                String.valueOf(
                                                election.memberCount));

                memberNumber.setStyle(
                                "-fx-font-size: 15px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #356AE6;");

                Label memberText = new Label(
                                election.memberCount == 1
                                                ? "Member"
                                                : "Members");

                memberText.setStyle(
                                "-fx-font-size: 12px;" +
                                                "-fx-text-fill: #64748B;");

                memberBox.getChildren().addAll(
                                memberNumber,
                                memberText);

                // =========================================================
                // STATUS
                // =========================================================

                Label status = createStatusBadge(
                                election.status);

                // =========================================================
                // ACTIONS
                // =========================================================

                HBox actions = new HBox(10);

                actions.setAlignment(
                                Pos.CENTER_LEFT);

                // =========================================================
                // OPEN ELECTION
                // =========================================================

                if ("OPEN".equalsIgnoreCase(
                                election.status)) {

                        Button close = new Button(
                                        "Close Election");

                        close.setPrefHeight(36);

                        close.setStyle(
                                        "-fx-background-color: #FFF1F0;" +
                                                        "-fx-text-fill: #B42318;" +
                                                        "-fx-font-size: 12px;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-background-radius: 8px;" +
                                                        "-fx-border-color: #FECACA;" +
                                                        "-fx-border-radius: 8px;" +
                                                        "-fx-cursor: hand;");

                        close.setOnMouseEntered(
                                        event -> {

                                                close.setStyle(
                                                                "-fx-background-color: #FEE4E2;" +
                                                                                "-fx-text-fill: #912018;" +
                                                                                "-fx-font-size: 12px;" +
                                                                                "-fx-font-weight: bold;" +
                                                                                "-fx-background-radius: 8px;" +
                                                                                "-fx-border-color: #FCA5A5;" +
                                                                                "-fx-border-radius: 8px;" +
                                                                                "-fx-cursor: hand;");
                                        });

                        close.setOnMouseExited(
                                        event -> {

                                                close.setStyle(
                                                                "-fx-background-color: #FFF1F0;" +
                                                                                "-fx-text-fill: #B42318;" +
                                                                                "-fx-font-size: 12px;" +
                                                                                "-fx-font-weight: bold;" +
                                                                                "-fx-background-radius: 8px;" +
                                                                                "-fx-border-color: #FECACA;" +
                                                                                "-fx-border-radius: 8px;" +
                                                                                "-fx-cursor: hand;");
                                        });

                        close.setOnAction(
                                        event -> closeElection(election));

                        actions.getChildren().add(
                                        close);

                } else {

                        // =====================================================
                        // CLOSED
                        // =====================================================

                        Label closed = new Label(
                                        "Election Closed");

                        closed.setStyle(
                                        "-fx-font-size: 12px;" +
                                                        "-fx-text-fill: #94A3B8;");

                        actions.getChildren().add(
                                        closed);
                }

                // =========================================================
                // ADD CELLS
                // =========================================================

                grid.add(
                                electionInfo,
                                0,
                                0);

                grid.add(
                                memberBox,
                                1,
                                0);

                grid.add(
                                status,
                                2,
                                0);

                grid.add(
                                actions,
                                3,
                                0);

                // =========================================================
                // ROW HOVER
                // =========================================================

                grid.setOnMouseEntered(
                                event -> {

                                        grid.setStyle(
                                                        "-fx-background-color: #F8FAFF;" +
                                                                        "-fx-border-color: #DDE6F3;" +
                                                                        "-fx-border-width: 0 0 1 0;");
                                });

                grid.setOnMouseExited(
                                event -> {

                                        grid.setStyle(
                                                        "-fx-background-color: white;" +
                                                                        "-fx-border-color: #EEF2F7;" +
                                                                        "-fx-border-width: 0 0 1 0;");
                                });

                return grid;
        }

        // =============================================================
        // STATUS BADGE
        // =============================================================

        private Label createStatusBadge(
                        String status) {

                Label badge = new Label(
                                status.toUpperCase());

                badge.setPadding(
                                new Insets(
                                                6,
                                                12,
                                                6,
                                                12));

                if ("OPEN".equalsIgnoreCase(status)) {

                        badge.setStyle(
                                        "-fx-background-color: #ECFDF3;" +
                                                        "-fx-text-fill: #027A48;" +
                                                        "-fx-background-radius: 20px;" +
                                                        "-fx-font-size: 11px;" +
                                                        "-fx-font-weight: bold;");

                } else {

                        badge.setStyle(
                                        "-fx-background-color: #F1F5F9;" +
                                                        "-fx-text-fill: #64748B;" +
                                                        "-fx-background-radius: 20px;" +
                                                        "-fx-font-size: 11px;" +
                                                        "-fx-font-weight: bold;");
                }

                return badge;
        }

        // =============================================================
        // CREATE ELECTION DIALOG
        // =============================================================

        private void showCreateElectionDialog() {

                Dialog<ButtonType> dialog = new Dialog<>();
                Navigation.attachOwner(dialog);

                dialog.setTitle(
                                "Create Election");

                dialog.setHeaderText(
                                "Create New Election");

                // =========================================================
                // INPUT
                // =========================================================

                TextField electionName = new TextField();

                electionName.setPromptText(
                                "Enter election name");

                electionName.setPrefHeight(
                                42);

                electionName.setPrefWidth(
                                420);

                electionName.setStyle(
                                "-fx-font-size: 13px;" +
                                                "-fx-background-color: white;" +
                                                "-fx-border-color: #CBD5E1;" +
                                                "-fx-border-radius: 8px;" +
                                                "-fx-background-radius: 8px;");

                // =========================================================
                // LABEL
                // =========================================================

                Label nameLabel = new Label(
                                "Election Name");

                nameLabel.setStyle(
                                "-fx-font-size: 13px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: #334155;");

                // =========================================================
                // FORM
                // =========================================================

                VBox box = new VBox(10);

                box.setPadding(
                                new Insets(20));

                box.getChildren().addAll(
                                nameLabel,
                                electionName);

                dialog.getDialogPane().setContent(
                                box);

                // =========================================================
                // BUTTONS
                // =========================================================

                ButtonType create = new ButtonType(
                                "Create",
                                ButtonBar.ButtonData.OK_DONE);

                dialog.getDialogPane()
                                .getButtonTypes()
                                .addAll(
                                                create,
                                                ButtonType.CANCEL);

                // =========================================================
                // WAIT FOR RESULT
                // =========================================================

                dialog.showAndWait()
                                .ifPresent(result -> {

                                        if (result != create) {
                                                return;
                                        }

                                        // =================================================
                                        // GET NAME
                                        // =================================================

                                        String name = electionName
                                                        .getText()
                                                        .trim();

                                        // =================================================
                                        // VALIDATION
                                        // =================================================

                                        if (name.isEmpty()) {

                                                showMessage(
                                                                Alert.AlertType.WARNING,
                                                                "Election Name Required",
                                                                "Please enter an election name.");

                                                return;
                                        }

                                        // =================================================
                                        // SAVE
                                        // =================================================

                                        try {

                                                if (electionDAO.electionExists(name)) {

                                                        showMessage(
                                                                        Alert.AlertType.WARNING,
                                                                        "Election Already Exists",
                                                                        "An election with this name already exists.");

                                                        return;
                                                }

                                                // =============================================
                                                // SAVE INTO SQLITE
                                                // =============================================

                                                electionDAO.createElection(name);

                                                // =============================================
                                                // RELOAD LIST
                                                // =============================================

                                                loadElections();

                                                // =============================================
                                                // SUCCESS
                                                // =============================================

                                                showMessage(
                                                                Alert.AlertType.INFORMATION,
                                                                "Election Created",
                                                                "Election \"" +
                                                                                name +
                                                                                "\" has been saved successfully.");

                                        } catch (SQLException e) {

                                                e.printStackTrace();

                                                showMessage(
                                                                Alert.AlertType.ERROR,
                                                                "Database Error",
                                                                "Could not save election.\n\n"
                                                                                + e.getMessage());
                                        }
                                });
        }

        // =============================================================
        // CLOSE ELECTION
        // =============================================================

        private void closeElection(
                        ElectionData election) {

                Alert confirmation = new Alert(
                                Alert.AlertType.CONFIRMATION);
                Navigation.attachOwner(confirmation);

                confirmation.setTitle(
                                "Close Election");

                confirmation.setHeaderText(
                                "Close Election?");

                confirmation.setContentText(
                                "Are you sure you want to close:\n\n" +
                                                election.name +
                                                "\n\n" +
                                                "After closing, this election will no longer " +
                                                "be available for voter verification.");

                ButtonType closeButton = new ButtonType(
                                "Close Election",
                                ButtonBar.ButtonData.OK_DONE);

                ButtonType cancelButton = new ButtonType(
                                "Cancel",
                                ButtonBar.ButtonData.CANCEL_CLOSE);

                confirmation
                                .getButtonTypes()
                                .setAll(
                                                closeButton,
                                                cancelButton);

                confirmation
                                .showAndWait()
                                .ifPresent(result -> {

                                        if (result != closeButton) {
                                                return;
                                        }

                                        try {

                                                // =================================================
                                                // CLOSE ELECTION
                                                // =================================================

                                                electionDAO.closeElection(
                                                                election.electionId);

                                                // =================================================
                                                // RELOAD
                                                // =================================================
                                                //
                                                // loadElections() sorts OPEN first
                                                // and CLOSED last.
                                                //
                                                // =================================================

                                                loadElections();

                                                // =================================================
                                                // SUCCESS
                                                // =================================================

                                                showMessage(
                                                                Alert.AlertType.INFORMATION,
                                                                "Election Closed",
                                                                "Election \"" +
                                                                                election.name +
                                                                                "\" has been closed.");

                                        } catch (SQLException e) {

                                                e.printStackTrace();

                                                showMessage(
                                                                Alert.AlertType.ERROR,
                                                                "Database Error",
                                                                "Could not close election.\n\n"
                                                                                + e.getMessage());
                                        }
                                });
        }

        // =============================================================
        // SHOW MESSAGE
        // =============================================================

        private void showMessage(
                        Alert.AlertType type,
                        String title,
                        String message) {

                Alert alert = new Alert(type);
                Navigation.attachOwner(alert);

                alert.setTitle(title);

                alert.setHeaderText(title);

                alert.setContentText(message);

                alert.showAndWait();
        }

        // =============================================================
        // GET SCENE
        // =============================================================

        public Scene getScene() {

                return scene;
        }

        // =============================================================
        // UI MODEL
        // =============================================================

        private static class ElectionData {

                String electionId;

                String name;

                String status;

                int memberCount;

                ElectionData(
                                String electionId,
                                String name,
                                String status,
                                int memberCount) {

                        this.electionId = electionId;

                        this.name = name;

                        this.status = status;

                        this.memberCount = memberCount;
                }
        }
}