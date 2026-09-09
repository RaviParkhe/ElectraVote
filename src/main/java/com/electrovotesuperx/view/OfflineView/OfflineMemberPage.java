package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.controller.OfflineController.OfflineMembersController;
import com.electrovotesuperx.dao.OfflineDAO.ElectionDAO;
import com.electrovotesuperx.model.OfflineModel.Election;
import com.electrovotesuperx.model.OfflineModel.Member;
import com.electrovotesuperx.view.CommonView.Header;
import com.electrovotesuperx.view.CommonView.Sidebar;
import com.electrovotesuperx.utils.Navigation;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OfflineMemberPage {

        // =========================================================
        // SCENE
        // =========================================================

        private final Scene scene;

        // =========================================================
        // CONTROLLERS
        // =========================================================

        private final OfflineMembersController controller;
        private final ElectionDAO electionDAO;

        // =========================================================
        // UI
        // =========================================================

        private final VBox memberList = new VBox(0);

        private final TextField searchField = new TextField();

        private final ComboBox<String> votingStatusFilter = new ComboBox<>();

        private final ComboBox<String> accountStatusFilter = new ComboBox<>();

        private final Label countLabel = new Label();

        private final ComboBox<ElectionOption> electionBox = new ComboBox<>();

        // =========================================================
        // DATA
        // =========================================================

        private final ObservableList<Member> members = FXCollections.observableArrayList();

        private String selectedElectionId;

        // =========================================================
        // PREVENT UNNECESSARY REFRESH
        // =========================================================

        private boolean changingElection = false;

        // =========================================================
        // CONSTRUCTOR
        // =========================================================

        public OfflineMemberPage() {

                controller = new OfflineMembersController();
                electionDAO = new ElectionDAO();

                // =====================================================
                // ROOT
                // =====================================================

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color:#F7F9FC;");

                // =====================================================
                // SIDEBAR
                // KEEP AS IT IS
                // =====================================================

                root.setLeft(
                                Sidebar.getSidebar("MemberPage"));

                // =====================================================
                // CENTER
                // =====================================================

                VBox center = new VBox();

                // =====================================================
                // HEADER
                // KEEP AS IT IS
                // =====================================================

                center.getChildren().add(
                                Header.getHeader(
                                                "Members",
                                                "Manage registered voters"));

                // =====================================================
                // PAGE CONTENT
                // =====================================================

                VBox content = new VBox(22);

                content.setPadding(
                                new Insets(28, 32, 35, 32));

                content.setStyle(
                                "-fx-background-color:#F7F9FC;");

                // =====================================================
                // ELECTION SELECTOR
                // =====================================================

                HBox electionSelector = createElectionSelector();

                // =====================================================
                // TITLE
                // =====================================================

                HBox titleRow = createTitleRow();

                // =====================================================
                // MEMBER CARD
                // =====================================================

                VBox memberCard = createMemberCard();

                // =====================================================
                // ADD CONTENT
                // =====================================================

                content.getChildren().addAll(
                                electionSelector,
                                titleRow,
                                memberCard);

                // =====================================================
                // SCROLL
                // =====================================================

                ScrollPane pageScroll = new ScrollPane(content);

                pageScroll.setFitToWidth(true);

                pageScroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                pageScroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                pageScroll.setStyle(
                                "-fx-background-color:#F7F9FC;" +
                                                "-fx-background:#F7F9FC;");

                VBox.setVgrow(
                                pageScroll,
                                Priority.ALWAYS);

                center.getChildren().add(
                                pageScroll);

                root.setCenter(center);

                // =====================================================
                // LOAD ELECTIONS
                // =====================================================

                loadElections();

                // =====================================================
                // SELECT FIRST ELECTION
                // =====================================================

                if (!electionBox.getItems().isEmpty()) {

                        electionBox
                                        .getSelectionModel()
                                        .selectFirst();

                        electionChanged();

                } else {

                        countLabel.setText(
                                        "No active elections available");
                }

                // =====================================================
                // SCENE
                // =====================================================

                scene = new Scene(root);
        }

        // =========================================================
        // ELECTION SELECTOR
        // =========================================================

        private HBox createElectionSelector() {

                HBox box = new HBox(16);

                box.setAlignment(
                                Pos.CENTER_LEFT);

                box.setPadding(
                                new Insets(16, 18, 16, 18));

                box.setStyle(
                                "-fx-background-color:white;" +
                                                "-fx-background-radius:14;" +
                                                "-fx-border-color:#E5EAF1;" +
                                                "-fx-border-radius:14;");

                // =====================================================
                // SMALL ICON
                // =====================================================

                Label icon = new Label("▣");

                icon.setPrefSize(
                                38,
                                38);

                icon.setAlignment(
                                Pos.CENTER);

                icon.setStyle(
                                "-fx-background-color:#EEF4FF;" +
                                                "-fx-background-radius:10;" +
                                                "-fx-text-fill:#356AE6;" +
                                                "-fx-font-size:17;" +
                                                "-fx-font-weight:bold;");

                // =====================================================
                // TEXT
                // =====================================================

                VBox textBox = new VBox(3);

                Label title = new Label(
                                "Election");

                title.setStyle(
                                "-fx-font-size:13;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-text-fill:#172B4D;");

                Label description = new Label(
                                "Select an election to view its members");

                description.setStyle(
                                "-fx-font-size:12;" +
                                                "-fx-text-fill:#94A3B8;");

                textBox.getChildren().addAll(
                                title,
                                description);

                // =====================================================
                // ELECTION COMBO
                // =====================================================

                electionBox.setPrefWidth(340);

                electionBox.setPrefHeight(40);

                electionBox.setStyle(
                                "-fx-background-color:#FFFFFF;" +
                                                "-fx-border-color:#D9E2EC;" +
                                                "-fx-border-radius:9;" +
                                                "-fx-background-radius:9;" +
                                                "-fx-font-size:13;");

                electionBox.setOnAction(
                                e -> electionChanged());

                // =====================================================
                // SPACER
                // =====================================================

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // =====================================================
                // ADD
                // =====================================================

                box.getChildren().addAll(
                                icon,
                                textBox,
                                electionBox,
                                spacer);

                return box;
        }

        // =========================================================
        // ELECTION CHANGED
        // =========================================================

        private void electionChanged() {

                changingElection = true;

                try {

                        searchField.clear();

                        votingStatusFilter.setValue(
                                        "All");

                        accountStatusFilter.setValue(
                                        "All");

                } finally {

                        changingElection = false;
                }

                ElectionOption selected = electionBox.getValue();

                if (selected == null) {

                        selectedElectionId = null;

                        members.clear();

                        refreshMembers();

                        return;
                }

                selectedElectionId = selected.getElectionId();

                loadMembers();

                refreshMembers();
        }

        // =========================================================
        // TITLE ROW
        // =========================================================

        private HBox createTitleRow() {

                HBox row = new HBox();

                row.setAlignment(
                                Pos.CENTER_LEFT);

                VBox titleBox = new VBox(3);

                Label title = new Label(
                                "Election Members");

                title.setStyle(
                                "-fx-font-size:27;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-text-fill:#172B4D;");

                countLabel.setStyle(
                                "-fx-font-size:13;" +
                                                "-fx-text-fill:#7B8BA1;");

                titleBox.getChildren().addAll(
                                title,
                                countLabel);

                row.getChildren().addAll(
                                titleBox);

                return row;
        }

        // =========================================================
        // MEMBER CARD
        // =========================================================

        private VBox createMemberCard() {

                VBox card = new VBox(14);

                card.setPadding(
                                new Insets(18));

                card.setStyle(
                                "-fx-background-color:white;" +
                                                "-fx-background-radius:16;" +
                                                "-fx-border-color:#E5EAF1;" +
                                                "-fx-border-radius:16;");

                // =====================================================
                // FILTERS
                // =====================================================

                HBox filters = createFilters();

                // =====================================================
                // HEADER
                // =====================================================

                GridPane header = createGrid();

                header.add(
                                headerLabel("MEMBER"),
                                0,
                                0);

                header.add(
                                headerLabel("VOTER ID"),
                                1,
                                0);

                header.add(
                                headerLabel("VOTING"),
                                2,
                                0);

                header.add(
                                headerLabel("ACCOUNT"),
                                3,
                                0);

                header.setPadding(
                                new Insets(
                                                8,
                                                8,
                                                8,
                                                8));

                // =====================================================
                // MEMBER SCROLL
                // =====================================================

                ScrollPane scroll = new ScrollPane(memberList);

                scroll.setFitToWidth(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scroll.setStyle(
                                "-fx-background-color:transparent;" +
                                                "-fx-background:transparent;");

                scroll.setPrefHeight(420);

                VBox.setVgrow(
                                scroll,
                                Priority.ALWAYS);

                card.getChildren().addAll(
                                filters,
                                header,
                                scroll);

                return card;
        }

        // =========================================================
        // FILTERS
        // =========================================================

        private HBox createFilters() {

                HBox box = new HBox(10);

                box.setAlignment(
                                Pos.CENTER_LEFT);

                // =====================================================
                // SEARCH FIELD
                // =====================================================

                searchField.setPromptText(
                                "Search members...");

                searchField.setPrefWidth(320);

                searchField.setPrefHeight(38);

                searchField.setStyle(
                                "-fx-background-color:#F8FAFC;" +
                                                "-fx-background-radius:9;" +
                                                "-fx-border-color:#E2E8F0;" +
                                                "-fx-border-radius:9;" +
                                                "-fx-font-size:13;" +
                                                "-fx-padding:0 12;");

                // =====================================================
                // VOTING STATUS
                // =====================================================

                votingStatusFilter.getItems().setAll(
                                "All",
                                "Voted",
                                "Non-Voted");

                votingStatusFilter.setValue(
                                "All");

                votingStatusFilter.setPrefWidth(
                                145);

                votingStatusFilter.setPrefHeight(
                                38);

                votingStatusFilter.setStyle(
                                "-fx-font-size:13;");

                setupFilterButtonCell(
                                votingStatusFilter,
                                "Voting Status");

                // =====================================================
                // ACCOUNT STATUS
                // =====================================================

                accountStatusFilter.getItems().setAll(
                                "All",
                                "Active",
                                "Suspended");

                accountStatusFilter.setValue(
                                "All");

                accountStatusFilter.setPrefWidth(
                                145);

                accountStatusFilter.setPrefHeight(
                                38);

                accountStatusFilter.setStyle(
                                "-fx-font-size:13;");

                setupFilterButtonCell(
                                accountStatusFilter,
                                "Account Status");

                // =====================================================
                // LISTENERS
                // =====================================================

                searchField.textProperty().addListener(
                                (obs, oldValue, newValue) -> {

                                        if (!changingElection) {

                                                refreshMembers();
                                        }
                                });

                votingStatusFilter.valueProperty().addListener(
                                (obs, oldValue, newValue) -> {

                                        if (!changingElection) {

                                                refreshMembers();
                                        }
                                });

                accountStatusFilter.valueProperty().addListener(
                                (obs, oldValue, newValue) -> {

                                        if (!changingElection) {

                                                refreshMembers();
                                        }
                                });

                box.getChildren().addAll(
                                searchField,
                                votingStatusFilter,
                                accountStatusFilter);

                return box;
        }

        // =========================================================
        // FILTER BUTTON CELL
        // =========================================================

        private void setupFilterButtonCell(
                        ComboBox<String> comboBox,
                        String defaultText) {

                comboBox.setButtonCell(
                                new ListCell<String>() {

                                        @Override
                                        protected void updateItem(
                                                        String item,
                                                        boolean empty) {

                                                super.updateItem(
                                                                item,
                                                                empty);

                                                if (empty ||
                                                                item == null ||
                                                                "All".equalsIgnoreCase(item)) {

                                                        setText(defaultText);

                                                        setStyle(
                                                                        "-fx-text-fill:#64748B;" +
                                                                                        "-fx-font-size:13;");

                                                } else {

                                                        setText(item);

                                                        setStyle(
                                                                        "-fx-text-fill:#172B4D;" +
                                                                                        "-fx-font-size:13;");
                                                }
                                        }
                                });
        }

        // =========================================================
        // GRID
        // =========================================================

        private GridPane createGrid() {

                GridPane grid = new GridPane();

                ColumnConstraints c1 = new ColumnConstraints();

                c1.setPercentWidth(36);

                ColumnConstraints c2 = new ColumnConstraints();

                c2.setPercentWidth(18);

                ColumnConstraints c3 = new ColumnConstraints();

                c3.setPercentWidth(15);

                ColumnConstraints c4 = new ColumnConstraints();

                c4.setPercentWidth(16);

                ColumnConstraints c5 = new ColumnConstraints();

                c5.setPercentWidth(15);

                grid.getColumnConstraints().addAll(
                                c1,
                                c2,
                                c3,
                                c4,
                                c5);

                return grid;
        }

        // =========================================================
        // HEADER LABEL
        // =========================================================

        private Label headerLabel(
                        String text) {

                Label label = new Label(text);

                label.setStyle(
                                "-fx-font-size:11;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-text-fill:#94A3B8;");

                return label;
        }

        // =========================================================
        // LOAD ELECTIONS
        // =========================================================

        private void loadElections() {

                electionBox.getItems().clear();

                try {

                        List<Election> databaseElections = electionDAO.getAllElections();

                        for (Election election : databaseElections) {

                                // Show DRAFT (enrollment window) AND OPEN elections.
                                // DRAFT = members can be added.
                                // OPEN  = roll is frozen, shown read-only.
                                // CLOSED elections are hidden from member management.
                                if ("DRAFT".equalsIgnoreCase(election.getStatus())
                                                || "OPEN".equalsIgnoreCase(election.getStatus())) {

                                        electionBox.getItems().add(
                                                        new ElectionOption(
                                                                        election.getElectionId(),
                                                                        election.getName(),
                                                                        election.getStatus()));
                                }

                        }

                } catch (SQLException e) {

                        e.printStackTrace();

                        showWarning(
                                        "Could not load elections.\n\n"
                                                        + e.getMessage());
                }
        }

        // =========================================================
        // LOAD MEMBERS
        // =========================================================

        private void loadMembers() {

                members.clear();

                if (selectedElectionId == null ||
                                selectedElectionId.isBlank()) {

                        return;
                }

                List<Member> list = controller.getMembersForElection(
                                selectedElectionId);

                if (list != null) {

                        members.addAll(list);
                }
        }

        // =========================================================
        // REFRESH MEMBERS
        // =========================================================

        private void refreshMembers() {

                memberList.getChildren().clear();

                String search = searchField.getText()
                                .toLowerCase()
                                .trim();

                String votingFilter = votingStatusFilter.getValue();

                if (votingFilter == null) {

                        votingFilter = "All";
                }

                String accountFilter = accountStatusFilter.getValue();

                if (accountFilter == null) {

                        accountFilter = "All";
                }

                final String finalVotingFilter = votingFilter;

                final String finalAccountFilter = accountFilter;

                List<Member> result = members.stream()

                                // SEARCH
                                .filter(member -> {

                                        if (search.isEmpty()) {

                                                return true;
                                        }

                                        String name = member.getName() == null
                                                        ? ""
                                                        : member.getName()
                                                                        .toLowerCase();

                                        String email = member.getEmail() == null
                                                        ? ""
                                                        : member.getEmail()
                                                                        .toLowerCase();

                                        String voterId = member.getVoterId() == null
                                                        ? ""
                                                        : member.getVoterId()
                                                                        .toLowerCase();

                                        return name.contains(search)
                                                        || email.contains(search)
                                                        || voterId.contains(search);
                                })

                                // VOTING FILTER
                                .filter(member -> {

                                        if ("All".equalsIgnoreCase(
                                                        finalVotingFilter)) {

                                                return true;
                                        }

                                        return finalVotingFilter
                                                        .equalsIgnoreCase(
                                                                        member.getVotingStatus());
                                })

                                // ACCOUNT FILTER
                                .filter(member -> {

                                        if ("All".equalsIgnoreCase(
                                                        finalAccountFilter)) {

                                                return true;
                                        }

                                        return finalAccountFilter
                                                        .equalsIgnoreCase(
                                                                        member.getStatus());
                                })

                                // SUSPENDED LAST
                                .sorted(
                                                Comparator.comparing(
                                                                member -> "Suspended"
                                                                                .equalsIgnoreCase(
                                                                                                member.getStatus())))

                                .collect(
                                                Collectors.toList());

                // =====================================================
                // COUNT
                // =====================================================

                String electionName = electionBox.getValue() == null
                                ? "selected election"
                                : electionBox
                                                .getValue()
                                                .getName();

                countLabel.setText(
                                result.size()
                                                + " members in "
                                                + electionName);

                // =====================================================
                // EMPTY
                // =====================================================

                if (result.isEmpty()) {

                        String message;

                        if (selectedElectionId == null) {

                                message = "Please select an election.";

                        } else if (!search.isEmpty()
                                        || !"All".equalsIgnoreCase(
                                                        finalVotingFilter)
                                        || !"All".equalsIgnoreCase(
                                                        finalAccountFilter)) {

                                message = "No members match the selected filters.";

                        } else {

                                message = "No members found for this election.";
                        }

                        Label empty = new Label(message);

                        empty.setStyle(
                                        "-fx-font-size:13;" +
                                                        "-fx-text-fill:#94A3B8;");

                        VBox emptyBox = new VBox(empty);

                        emptyBox.setAlignment(
                                        Pos.CENTER);

                        emptyBox.setPadding(
                                        new Insets(50));

                        memberList.getChildren().add(
                                        emptyBox);

                        return;
                }

                // =====================================================
                // CREATE ROWS
                // =====================================================

                for (Member member : result) {

                        memberList.getChildren().add(
                                        createMemberRow(member));
                }
        }

        // =========================================================
        // MEMBER ROW
        // =========================================================

        private GridPane createMemberRow(
                        Member member) {

                GridPane grid = createGrid();

                grid.setPadding(
                                new Insets(
                                                13,
                                                8,
                                                13,
                                                8));

                grid.setStyle(
                                "-fx-border-color:#EEF2F7;" +
                                                "-fx-border-width:0 0 1 0;");

                // =====================================================
                // MEMBER
                // =====================================================

                HBox memberBox = new HBox(10);

                memberBox.setAlignment(
                                Pos.CENTER_LEFT);

                Circle circle = new Circle(
                                20,
                                Color.web("#EAF0FF"));

                Label initials = new Label(
                                member.getInitials());

                initials.setStyle(
                                "-fx-text-fill:#356AE6;" +
                                                "-fx-font-size:12;" +
                                                "-fx-font-weight:bold;");

                StackPane avatar = new StackPane(
                                circle,
                                initials);

                VBox information = new VBox(2);

                Label name = new Label(
                                member.getName());

                name.setStyle(
                                "-fx-font-size:14;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-text-fill:#172B4D;");

                Label email = new Label(
                                member.getEmail() == null
                                                ? ""
                                                : member.getEmail());

                email.setStyle(
                                "-fx-font-size:12;" +
                                                "-fx-text-fill:#7B8BA1;");

                information.getChildren().addAll(
                                name,
                                email);

                memberBox.getChildren().addAll(
                                avatar,
                                information);

                // =====================================================
                // VOTER ID
                // =====================================================

                HBox voterIdBox = new HBox(6);

                voterIdBox.setAlignment(
                                Pos.CENTER_LEFT);

                String voterIdText = member.getVoterId() == null
                                ? ""
                                : member.getVoterId();

                Label voterId = new Label(voterIdText);

                voterId.setStyle(
                                "-fx-font-size:12;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-text-fill:#356AE6;");

                voterId.setWrapText(true);

                // =====================================================
                // COPY BUTTON
                // =====================================================

                Button copyButton = new Button("⧉");

                copyButton.setMinSize(
                                28,
                                28);

                copyButton.setPrefSize(
                                28,
                                28);

                copyButton.setTooltip(
                                new Tooltip(
                                                "Copy Voter ID"));

                setCopyButtonStyle(
                                copyButton,
                                false);

                copyButton.setOnAction(e -> {

                        if (voterIdText.isBlank()) {

                                return;
                        }

                        Clipboard clipboard = Clipboard.getSystemClipboard();

                        ClipboardContent content = new ClipboardContent();

                        content.putString(
                                        voterIdText);

                        clipboard.setContent(
                                        content);

                        copyButton.setText("✓");

                        copyButton.setTooltip(
                                        new Tooltip("Copied!"));

                        setCopyButtonStyle(
                                        copyButton,
                                        true);

                        PauseTransition pause = new PauseTransition(
                                        Duration.seconds(1));

                        pause.setOnFinished(
                                        event -> {

                                                copyButton.setText(
                                                                "⧉");

                                                copyButton.setTooltip(
                                                                new Tooltip(
                                                                                "Copy Voter ID"));

                                                setCopyButtonStyle(
                                                                copyButton,
                                                                false);
                                        });

                        pause.play();
                });

                voterIdBox.getChildren().addAll(
                                voterId,
                                copyButton);

                // =====================================================
                // VOTING STATUS
                // =====================================================

                Label votingStatus = createStatusBadge(
                                member.getVotingStatus(),
                                "Voted");

                // =====================================================
                // ACCOUNT STATUS
                // =====================================================

                Label accountStatus = createStatusBadge(
                                member.getStatus(),
                                "Active");

                // =====================================================
                // ACTIONS (view only for polling officer)
                // =====================================================

                HBox actions = new HBox(4);

                actions.setAlignment(
                                Pos.CENTER_LEFT);

                Button view = actionButton("👁");

                view.setTooltip(
                                new Tooltip("View details"));

                view.setOnAction(
                                e -> showDetails(member));

                actions.getChildren().add(view);

                // =====================================================
                // ADD CELLS
                // =====================================================

                grid.add(
                                memberBox,
                                0,
                                0);

                grid.add(
                                voterIdBox,
                                1,
                                0);

                grid.add(
                                votingStatus,
                                2,
                                0);

                grid.add(
                                accountStatus,
                                3,
                                0);

                grid.add(
                                actions,
                                4,
                                0);

                return grid;
        }

        // =========================================================
        // STATUS BADGE
        // =========================================================

        private Label createStatusBadge(
                        String value,
                        String positiveValue) {

                Label label = new Label(value);

                label.setPadding(
                                new Insets(
                                                4,
                                                9,
                                                4,
                                                9));

                if (positiveValue.equalsIgnoreCase(value)) {

                        label.setStyle(
                                        "-fx-background-color:#E8F7EE;" +
                                                        "-fx-text-fill:#16803C;" +
                                                        "-fx-background-radius:12;" +
                                                        "-fx-font-size:11;" +
                                                        "-fx-font-weight:bold;");

                } else {

                        label.setStyle(
                                        "-fx-background-color:#FFF4DE;" +
                                                        "-fx-text-fill:#B7791F;" +
                                                        "-fx-background-radius:12;" +
                                                        "-fx-font-size:11;" +
                                                        "-fx-font-weight:bold;");
                }

                return label;
        }

        // =========================================================
        // COPY BUTTON STYLE
        // =========================================================

        private void setCopyButtonStyle(
                        Button button,
                        boolean copied) {

                if (copied) {

                        button.setStyle(
                                        "-fx-background-color:#E8F7EE;" +
                                                        "-fx-text-fill:#16803C;" +
                                                        "-fx-font-size:13;" +
                                                        "-fx-font-weight:bold;" +
                                                        "-fx-background-radius:7;" +
                                                        "-fx-border-color:#C8EBD5;" +
                                                        "-fx-border-radius:7;" +
                                                        "-fx-padding:1;" +
                                                        "-fx-cursor:hand;");

                } else {

                        button.setStyle(
                                        "-fx-background-color:#F1F5FF;" +
                                                        "-fx-text-fill:#356AE6;" +
                                                        "-fx-font-size:13;" +
                                                        "-fx-font-weight:bold;" +
                                                        "-fx-background-radius:7;" +
                                                        "-fx-border-color:#DCE6FF;" +
                                                        "-fx-border-radius:7;" +
                                                        "-fx-padding:1;" +
                                                        "-fx-cursor:hand;");
                }
        }

        // =========================================================
        // ACTION BUTTON
        // =========================================================

        private Button actionButton(
                        String text) {

                Button button = new Button(text);

                button.setMinSize(
                                30,
                                30);

                button.setPrefSize(
                                30,
                                30);

                button.setStyle(
                                "-fx-background-color:#F8FAFC;" +
                                                "-fx-text-fill:#64748B;" +
                                                "-fx-font-size:13;" +
                                                "-fx-background-radius:7;" +
                                                "-fx-border-color:#E8EDF3;" +
                                                "-fx-border-radius:7;" +
                                                "-fx-padding:2;" +
                                                "-fx-cursor:hand;");

                button.setOnMouseEntered(e -> {

                        button.setStyle(
                                        "-fx-background-color:#EEF4FF;" +
                                                        "-fx-text-fill:#356AE6;" +
                                                        "-fx-font-size:13;" +
                                                        "-fx-background-radius:7;" +
                                                        "-fx-border-color:#D9E5FF;" +
                                                        "-fx-border-radius:7;" +
                                                        "-fx-padding:2;" +
                                                        "-fx-cursor:hand;");
                });

                button.setOnMouseExited(e -> {

                        button.setStyle(
                                        "-fx-background-color:#F8FAFC;" +
                                                        "-fx-text-fill:#64748B;" +
                                                        "-fx-font-size:13;" +
                                                        "-fx-background-radius:7;" +
                                                        "-fx-border-color:#E8EDF3;" +
                                                        "-fx-border-radius:7;" +
                                                        "-fx-padding:2;" +
                                                        "-fx-cursor:hand;");
                });

                return button;
        }

        // =========================================================
        // ADD MEMBER
        // =========================================================

        private void showAddDialog() {

                if (electionBox.getValue() == null) {

                        showWarning(
                                        "Please select an election in DRAFT status to add members.");

                        return;
                }

                // =====================================================
                // BLOCK ADDITIONS WHEN ELECTION IS OPEN
                // The electoral roll is frozen once voting starts.
                // =====================================================

                ElectionOption selected = electionBox.getValue();

                if ("OPEN".equalsIgnoreCase(selected.getStatus())) {

                        showWarning(
                                        "Electoral Roll Frozen\n\n" +
                                        "This election is currently OPEN (voting in progress).\n" +
                                        "Voter enrollment was closed when voting started.\n\n" +
                                        "To make changes, the election must be CLOSED first.");

                        return;
                }

                Dialog<ButtonType> dialog = new Dialog<>();
                Navigation.attachOwner(dialog);

                dialog.setTitle(
                                "Add Member");

                dialog.setHeaderText(
                                "Add Member to "
                                                + electionBox
                                                                .getValue()
                                                                .getName());

                TextField name = new TextField();

                name.setPromptText(
                                "Enter Full name");

                TextField email = new TextField();

                email.setPromptText(
                                "Enter email");

                ComboBox<String> gender = new ComboBox<>();

                gender.getItems().addAll(
                                "Male",
                                "Female",
                                "Other");

                gender.setPromptText(
                                "Select gender");

                gender.setMaxWidth(
                                Double.MAX_VALUE);

                TextField phone = new TextField();

                phone.setPromptText(
                                "Enter phone number");

                VBox box = new VBox(
                                10,
                                new Label("Name"),
                                name,
                                new Label("Email"),
                                email,
                                new Label("Gender"),
                                gender,
                                new Label("Phone"),
                                phone);

                box.setPadding(
                                new Insets(15));

                box.setPrefWidth(
                                380);

                dialog.getDialogPane()
                                .setContent(box);

                ButtonType add = new ButtonType(
                                "Add",
                                ButtonBar.ButtonData.OK_DONE);

                dialog.getDialogPane()
                                .getButtonTypes()
                                .addAll(
                                                add,
                                                ButtonType.CANCEL);

                dialog.showAndWait()
                                .ifPresent(result -> {

                                        if (result != add) {

                                                return;
                                        }

                                        String memberName = name.getText().trim();

                                        String memberEmail = email.getText().trim();

                                        String memberGender = gender.getValue();

                                        String memberPhone = phone.getText().trim();

                                        if (memberName.isEmpty()) {

                                                showWarning(
                                                                "Please enter member name.");

                                                return;
                                        }

                                        if (memberGender == null ||
                                                        memberGender.isBlank()) {

                                                showWarning(
                                                                "Please select gender.");

                                                return;
                                        }

                                        if (memberPhone.isEmpty()) {

                                                showWarning(
                                                                "Please enter phone number.");

                                                return;
                                        }

                                        boolean success = controller.addMember(
                                                        selectedElectionId,
                                                        memberName,
                                                        memberEmail,
                                                        memberGender,
                                                        memberPhone);

                                        if (success) {

                                                loadMembers();

                                                refreshMembers();

                                        } else {

                                                showWarning(
                                                                "Member could not be added.\n\n"
                                                                                + "The member may already exist "
                                                                                + "in this election.");
                                        }
                                });
        }

        // =========================================================
        // EDIT MEMBER
        // =========================================================

        private void editMember(
                        Member member) {

                Dialog<ButtonType> dialog = new Dialog<>();
                Navigation.attachOwner(dialog);

                dialog.setTitle(
                                "Edit Member");

                dialog.setHeaderText(
                                "Edit " + member.getName());

                TextField name = new TextField(
                                member.getName());

                TextField email = new TextField(
                                member.getEmail() == null
                                                ? ""
                                                : member.getEmail());

                ComboBox<String> gender = new ComboBox<>();

                gender.getItems().addAll(
                                "Male",
                                "Female",
                                "Other");

                gender.setValue(
                                member.getGender());

                gender.setMaxWidth(
                                Double.MAX_VALUE);

                TextField phone = new TextField(
                                member.getPhone() == null
                                                ? ""
                                                : member.getPhone());

                VBox box = new VBox(
                                10,
                                new Label("Name"),
                                name,
                                new Label("Email"),
                                email,
                                new Label("Gender"),
                                gender,
                                new Label("Phone"),
                                phone);

                box.setPadding(
                                new Insets(15));

                box.setPrefWidth(
                                380);

                dialog.getDialogPane()
                                .setContent(box);

                ButtonType save = new ButtonType(
                                "Save",
                                ButtonBar.ButtonData.OK_DONE);

                dialog.getDialogPane()
                                .getButtonTypes()
                                .addAll(
                                                save,
                                                ButtonType.CANCEL);

                dialog.showAndWait()
                                .ifPresent(result -> {

                                        if (result != save) {

                                                return;
                                        }

                                        String newName = name.getText().trim();

                                        String newEmail = email.getText().trim();

                                        String newGender = gender.getValue();

                                        String newPhone = phone.getText().trim();

                                        if (newName.isEmpty()) {

                                                showWarning(
                                                                "Please enter member name.");

                                                return;
                                        }

                                        if (newGender == null ||
                                                        newGender.isBlank()) {

                                                showWarning(
                                                                "Please select gender.");

                                                return;
                                        }

                                        if (newPhone.isEmpty()) {

                                                showWarning(
                                                                "Please enter phone number.");

                                                return;
                                        }

                                        member.setName(
                                                        newName);

                                        member.setEmail(
                                                        newEmail);

                                        member.setGender(
                                                        newGender);

                                        member.setPhone(
                                                        newPhone);

                                        boolean success = controller.updateMember(
                                                        member);

                                        if (success) {

                                                loadMembers();

                                                refreshMembers();

                                        } else {

                                                showWarning(
                                                                "Member could not be updated.");
                                        }
                                });
        }

        // =========================================================
        // CHANGE STATUS
        // =========================================================

        private void changeStatus(
                        Member member) {

                boolean active = "Active".equalsIgnoreCase(
                                member.getStatus());

                Alert alert = new Alert(
                                Alert.AlertType.CONFIRMATION);
                Navigation.attachOwner(alert);

                if (active) {

                        alert.setTitle(
                                        "Suspend Member");

                        alert.setHeaderText(
                                        "Suspend "
                                                        + member.getName()
                                                        + "?");

                        alert.setContentText(
                                        "This member will not be active.");

                        ButtonType suspend = new ButtonType(
                                        "Suspend",
                                        ButtonBar.ButtonData.OK_DONE);

                        ButtonType cancel = new ButtonType(
                                        "Cancel",
                                        ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes()
                                        .setAll(
                                                        suspend,
                                                        cancel);

                        alert.showAndWait()
                                        .ifPresent(result -> {

                                                if (result == suspend) {

                                                        if (controller.suspend(
                                                                        member)) {

                                                                loadMembers();

                                                                refreshMembers();
                                                        }
                                                }
                                        });

                } else {

                        alert.setTitle(
                                        "Activate Member");

                        alert.setHeaderText(
                                        "Activate "
                                                        + member.getName()
                                                        + "?");

                        alert.setContentText(
                                        "This member will become active again.");

                        ButtonType activate = new ButtonType(
                                        "Activate",
                                        ButtonBar.ButtonData.OK_DONE);

                        ButtonType cancel = new ButtonType(
                                        "Cancel",
                                        ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes()
                                        .setAll(
                                                        activate,
                                                        cancel);

                        alert.showAndWait()
                                        .ifPresent(result -> {

                                                if (result == activate) {

                                                        if (controller.activate(
                                                                        member)) {

                                                                loadMembers();

                                                                refreshMembers();
                                                        }
                                                }
                                        });
                }
        }

        // =========================================================
        // DELETE
        // =========================================================

        private void deleteMember(
                        Member member) {

                Alert alert = new Alert(
                                Alert.AlertType.CONFIRMATION);
                Navigation.attachOwner(alert);

                alert.setTitle(
                                "Delete Member");

                alert.setHeaderText(
                                "Delete "
                                                + member.getName()
                                                + "?");

                alert.setContentText(
                                "This action will remove the voter "
                                                + "and their election registrations.");

                ButtonType delete = new ButtonType(
                                "Delete",
                                ButtonBar.ButtonData.OK_DONE);

                ButtonType cancel = new ButtonType(
                                "Cancel",
                                ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes()
                                .setAll(
                                                delete,
                                                cancel);

                alert.showAndWait()
                                .ifPresent(result -> {

                                        if (result == delete) {

                                                if (controller.deleteMember(
                                                                member)) {

                                                        members.remove(
                                                                        member);

                                                        refreshMembers();
                                                }
                                        }
                                });
        }

        // =========================================================
        // DETAILS
        // =========================================================

        private void showDetails(
                        Member member) {

                Alert alert = new Alert(
                                Alert.AlertType.INFORMATION);
                Navigation.attachOwner(alert);

                alert.setTitle(
                                "Member Details");

                alert.setHeaderText(
                                member.getName());

                alert.setContentText(
                                "Voter ID : "
                                                + member.getVoterId()
                                                + "\nEmail : "
                                                + member.getEmail()
                                                + "\nGender : "
                                                + member.getGender()
                                                + "\nPhone : "
                                                + member.getPhone()
                                                + "\nVoting Status : "
                                                + member.getVotingStatus()
                                                + "\nAccount Status : "
                                                + member.getStatus());

                alert.showAndWait();
        }

        // =========================================================
        // WARNING
        // =========================================================

        private void showWarning(
                        String message) {

                Alert alert = new Alert(
                                Alert.AlertType.WARNING);
                Navigation.attachOwner(alert);

                alert.setTitle(
                                "Warning");

                alert.setHeaderText(null);

                alert.setContentText(
                                message);

                alert.showAndWait();
        }

        // =========================================================
        // GET SCENE
        // =========================================================

        public Scene getScene() {

                return scene;
        }

        // =========================================================
        // ELECTION OPTION
        // =========================================================

        private static class ElectionOption {

                private final String electionId;

                private final String name;

                private final String status;

                public ElectionOption(
                                String electionId,
                                String name,
                                String status) {

                        this.electionId = electionId;

                        this.name = name;

                        this.status = status;
                }

                public String getElectionId() {

                        return electionId;
                }

                public String getName() {

                        return name;
                }

                public String getStatus() {

                        return status;
                }

                @Override
                public String toString() {

                        // Show status badge next to name in the dropdown
                        String badge = "DRAFT".equalsIgnoreCase(status)
                                        ? " [DRAFT]"
                                        : " [OPEN ✓]";
                        return name + badge;
                }
        }
}