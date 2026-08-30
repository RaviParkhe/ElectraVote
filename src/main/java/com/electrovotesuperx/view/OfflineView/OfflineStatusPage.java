package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.controller.OfflineController.OfflineStatusController;
import com.electrovotesuperx.view.CommonView.Header;
import com.electrovotesuperx.view.CommonView.Sidebar;
import com.electrovotesuperx.utils.Navigation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class OfflineStatusPage {

        // =========================================================
        // FIELDS
        // =========================================================

        private final Scene scene;

        private final OfflineStatusController controller = new OfflineStatusController();

        private final TextField tokenField = new TextField();

        private final Label result = new Label();

        private final Button completeButton = new Button("✓  Confirm Voting Completed");

        // =========================================================
        // CONSTRUCTOR
        // =========================================================

        public OfflineStatusPage() {

                // =====================================================
                // ROOT
                // =====================================================

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color:#F7F9FC;");

                // =====================================================
                // SIDEBAR
                // =====================================================

                root.setLeft(
                                Sidebar.getSidebar("StatusPage"));

                // =====================================================
                // CENTER
                // =====================================================

                VBox center = new VBox();

                center.setFillWidth(true);

                // =====================================================
                // HEADER
                // =====================================================

                HBox topBar = new HBox();

                topBar.setMaxWidth(
                                Double.MAX_VALUE);

                topBar.getChildren().add(
                                Header.getHeader(
                                                "Token",
                                                "Enter the digital token to verify identity and vote."));

                // =====================================================
                // CONTENT
                // =====================================================

                VBox content = new VBox(18);

                content.setPadding(
                                new Insets(32, 40, 40, 40));

                content.setFillWidth(true);

                content.setAlignment(
                                Pos.TOP_CENTER);

                content.setStyle(
                                "-fx-background-color:#F7F9FC;");

                // =====================================================
                // PAGE TITLE
                // =====================================================

                Label title = new Label("Complete Voter Verification");

                title.setStyle(
                                "-fx-font-size:28px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#172B4D;");

                // =====================================================
                // PAGE DESCRIPTION
                // =====================================================

                Label help = new Label(
                                "Confirm that the authorized voter has successfully " +
                                                "completed the voting process.");

                help.setWrapText(true);

                help.setMaxWidth(760);

                help.setStyle(
                                "-fx-font-size:14px;" +
                                                "-fx-text-fill:#71829B;");

                // =====================================================
                // TITLE CONTAINER
                // SAME STRUCTURE AS VERIFICATION PAGE
                // =====================================================

                VBox titleBox = new VBox(5);

                titleBox.setMaxWidth(760);

                titleBox.setPrefWidth(760);

                titleBox.setAlignment(
                                Pos.TOP_LEFT);

                titleBox.getChildren().addAll(
                                title,
                                help);

                // =====================================================
                // SESSION STATUS
                // =====================================================

                HBox sessionStatus = createSessionStatus();

                // =====================================================
                // MAIN VERIFICATION CARD
                // =====================================================

                VBox card = createVerificationCard();

                // =====================================================
                // ADD CONTENT VERTICALLY
                // SAME AS VERIFICATION PAGE
                // =====================================================

                content.getChildren().addAll(
                                titleBox,
                                sessionStatus,
                                card);

                // =====================================================
                // SCROLL PANE
                // =====================================================

                ScrollPane scroll = new ScrollPane(content);

                scroll.setFitToWidth(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scroll.setPannable(true);

                scroll.setStyle(
                                "-fx-background-color:#F7F9FC;" +
                                                "-fx-background:#F7F9FC;" +
                                                "-fx-border-color:transparent;");

                VBox.setVgrow(
                                scroll,
                                Priority.ALWAYS);

                // =====================================================
                // CENTER CONTENT
                // =====================================================

                center.getChildren().addAll(
                                topBar,
                                scroll);

                // =====================================================
                // ROOT CENTER
                // =====================================================

                root.setCenter(center);

                // =====================================================
                // SCENE
                // INCREASED COMFORTABLE SIZE
                // =====================================================

                scene = new Scene(
                                root,
                                1280,
                                760);
        }

        // =========================================================
        // SESSION STATUS
        // =========================================================

        private HBox createSessionStatus() {

                Label dot = new Label("●");

                dot.setStyle(
                                "-fx-font-size:12px;" +
                                                "-fx-text-fill:#16A34A;");

                Label title = new Label("Voting session active");

                title.setStyle(
                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;" +
                                                "-fx-text-fill:#166534;");

                Label separator = new Label("•");

                separator.setStyle(
                                "-fx-text-fill:#A1AFC0;" +
                                                "-fx-font-size:12px;");

                Label status = new Label(
                                "Authorization token required");

                status.setStyle(
                                "-fx-font-size:13px;" +
                                                "-fx-text-fill:#64748B;");

                HBox box = new HBox(
                                8,
                                dot,
                                title,
                                separator,
                                status);

                box.setAlignment(
                                Pos.CENTER_LEFT);

                // SAME WIDTH AS VERIFICATION PAGE

                box.setMaxWidth(760);

                box.setPrefWidth(760);

                box.setMinWidth(760);

                box.setPadding(
                                new Insets(
                                                10,
                                                14,
                                                10,
                                                14));

                box.setStyle(
                                "-fx-background-color:#F0FDF4;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-border-color:#DCFCE7;" +
                                                "-fx-border-radius:8;");

                return box;
        }

        // =========================================================
        // VERIFICATION CARD
        // =========================================================

        private VBox createVerificationCard() {

                // SAME WIDTH AS OFFLINE VERIFICATION

                VBox card = new VBox(16);

                card.setMaxWidth(760);

                card.setPrefWidth(760);

                card.setMinWidth(760);

                card.setPadding(
                                new Insets(28));

                card.setStyle(
                                "-fx-background-color:#FFFFFF;" +
                                                "-fx-background-radius:14;" +
                                                "-fx-border-color:#E2E8F0;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:14;");

                // =====================================================
                // CARD HEADER
                // =====================================================

                HBox cardHeader = new HBox(12);

                cardHeader.setAlignment(
                                Pos.CENTER_LEFT);

                Label icon = new Label("✓");

                icon.setMinWidth(36);

                icon.setPrefWidth(36);

                icon.setMaxWidth(36);

                icon.setMinHeight(36);

                icon.setPrefHeight(36);

                icon.setMaxHeight(36);

                icon.setAlignment(
                                Pos.CENTER);

                icon.setStyle(
                                "-fx-background-color:#E8F1FF;" +
                                                "-fx-background-radius:18;" +
                                                "-fx-text-fill:#172B4D;" +
                                                "-fx-font-size:17px;" +
                                                "-fx-font-weight:800;");
                // Path logoPath = Path.of(
                // "src",
                // "main",
                // "java",
                // "com",
                // "electrovotesuperx",
                // "resources",
                // "Assects",
                // "Images",
                // "padlock.png");

                VBox heading = new VBox(3);

                Label cardTitle = new Label(
                                "Voter Authorization");

                cardTitle.setStyle(
                                "-fx-font-size:17px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#172B4D;");

                Label cardSubtitle = new Label(
                                "Enter the authorization token provided after voting.");

                cardSubtitle.setWrapText(true);

                cardSubtitle.setStyle(
                                "-fx-font-size:13px;" +
                                                "-fx-text-fill:#71829B;");

                heading.getChildren().addAll(
                                cardTitle,
                                cardSubtitle);

                cardHeader.getChildren().addAll(
                                icon,
                                heading);

                // =====================================================
                // DIVIDER
                // SAME STRUCTURE AS VERIFICATION PAGE
                // =====================================================

                Region divider = new Region();

                divider.setPrefHeight(1);

                divider.setMaxWidth(
                                Double.MAX_VALUE);

                divider.setStyle(
                                "-fx-background-color:#E8EDF3;");

                // =====================================================
                // TOKEN LABEL
                // =====================================================

                Label tokenLabel = new Label("AUTHORIZATION TOKEN");

                tokenLabel.setStyle(
                                "-fx-font-size:11px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#526B91;");

                // =====================================================
                // TOKEN FIELD
                // =====================================================

                tokenField.setPromptText(
                                "Example: EV-ABCD2345");

                tokenField.setPrefHeight(46);

                tokenField.setMinHeight(46);

                tokenField.setMaxWidth(
                                Double.MAX_VALUE);

                tokenField.setStyle(
                                "-fx-background-color:#FFFFFF;" +
                                                "-fx-border-color:#CBD5E1;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:7;" +
                                                "-fx-background-radius:7;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-padding:0 12 0 12;" +
                                                "-fx-text-fill:#172B4D;");

                // =====================================================
                // TOKEN FIELD FOCUS
                // =====================================================

                tokenField.focusedProperty().addListener(
                                (obs, oldValue, focused) -> {

                                        if (focused) {

                                                tokenField.setStyle(
                                                                "-fx-background-color:#FFFFFF;" +
                                                                                "-fx-border-color:#4B78B8;" +
                                                                                "-fx-border-width:1.5;" +
                                                                                "-fx-border-radius:7;" +
                                                                                "-fx-background-radius:7;" +
                                                                                "-fx-font-size:13px;" +
                                                                                "-fx-padding:0 12 0 12;" +
                                                                                "-fx-text-fill:#172B4D;");

                                        } else {

                                                tokenField.setStyle(
                                                                "-fx-background-color:#FFFFFF;" +
                                                                                "-fx-border-color:#CBD5E1;" +
                                                                                "-fx-border-width:1;" +
                                                                                "-fx-border-radius:7;" +
                                                                                "-fx-background-radius:7;" +
                                                                                "-fx-font-size:13px;" +
                                                                                "-fx-padding:0 12 0 12;" +
                                                                                "-fx-text-fill:#172B4D;");
                                        }
                                });

                // =====================================================
                // TOKEN HELP
                // =====================================================

                Label tokenHelp = new Label(
                                "Use the token issued for this voting session.");

                tokenHelp.setStyle(
                                "-fx-font-size:12px;" +
                                                "-fx-text-fill:#94A3B8;");

                // =====================================================
                // COMPLETE BUTTON
                // =====================================================

                completeButton.setPrefWidth(240);

                completeButton.setPrefHeight(46);

                completeButton.setDisable(true);

                setCompleteButtonDisabledStyle();

                // =====================================================
                // TOKEN LISTENER
                // =====================================================

                tokenField.textProperty().addListener(
                                (observable, oldValue, newValue) -> {

                                        boolean empty = newValue == null ||
                                                        newValue.trim().isEmpty();

                                        completeButton.setDisable(empty);

                                        if (empty) {

                                                setCompleteButtonDisabledStyle();

                                        } else {

                                                setCompleteButtonEnabledStyle();
                                        }

                                        // Clear old result when token changes

                                        result.setText("");

                                        result.setVisible(false);

                                        result.setManaged(false);
                                });

                // =====================================================
                // COMPLETE BUTTON HOVER
                // =====================================================

                completeButton.setOnMouseEntered(e -> {

                        if (!completeButton.isDisabled()) {

                                completeButton.setStyle(
                                                "-fx-background-color:#233F67;" +
                                                                "-fx-text-fill:white;" +
                                                                "-fx-font-size:13px;" +
                                                                "-fx-font-weight:800;" +
                                                                "-fx-background-radius:8;" +
                                                                "-fx-cursor:hand;" +
                                                                "-fx-padding:0 18;");

                                completeButton.setScaleX(1.02);

                                completeButton.setScaleY(1.02);
                        }
                });

                completeButton.setOnMouseExited(e -> {

                        if (!completeButton.isDisabled()) {

                                setCompleteButtonEnabledStyle();
                        }

                        completeButton.setScaleX(1);

                        completeButton.setScaleY(1);
                });

                // =====================================================
                // COMPLETE ACTION
                // =====================================================

                completeButton.setOnAction(
                                e -> confirmCompletion());

                // =====================================================
                // ENTER KEY
                // =====================================================

                tokenField.setOnAction(e -> {

                        if (!completeButton.isDisabled()) {

                                confirmCompletion();
                        }
                });

                // =====================================================
                // RESULT
                // =====================================================

                result.setWrapText(true);

                result.setMaxWidth(
                                Double.MAX_VALUE);

                result.setVisible(false);

                result.setManaged(false);

                result.setMinHeight(
                                Region.USE_PREF_SIZE);

                // =====================================================
                // SECURITY INFORMATION
                // =====================================================

                HBox securityBox = new HBox(10);

                securityBox.setAlignment(
                                Pos.CENTER_LEFT);

                securityBox.setPadding(
                                new Insets(12));

                securityBox.setMaxWidth(
                                Double.MAX_VALUE);

                securityBox.setStyle(
                                "-fx-background-color:#F7FAFC;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-border-color:#E5ECF4;" +
                                                "-fx-border-radius:8;");

                Label lock = new Label("🔒");

                lock.setStyle(
                                "-fx-font-size:14px;");

                Label securityText = new Label(
                                "Secure verification\n" +
                                                "Authorization data is processed locally.");

                securityText.setWrapText(true);

                securityText.setStyle(
                                "-fx-font-size:12px;" +
                                                "-fx-text-fill:#526B91;" +
                                                "-fx-line-spacing:2;");

                securityBox.getChildren().addAll(
                                lock,
                                securityText);

                // =====================================================
                // FORM CONTENT
                // =====================================================

                card.getChildren().addAll(
                                cardHeader,
                                divider,
                                tokenLabel,
                                tokenField,
                                tokenHelp,
                                completeButton,
                                result,
                                securityBox);

                return card;
        }

        // =========================================================
        // BUTTON STYLES
        // =========================================================

        private void setCompleteButtonDisabledStyle() {

                completeButton.setStyle(
                                "-fx-background-color:#CBD5E1;" +
                                                "-fx-text-fill:#64748B;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-padding:0 18;");
        }

        private void setCompleteButtonEnabledStyle() {

                completeButton.setStyle(
                                "-fx-background-color:#172B4D;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-cursor:hand;" +
                                                "-fx-padding:0 18;");
        }

        // =========================================================
        // CONFIRM COMPLETION
        // =========================================================

        private void confirmCompletion() {

                String token = tokenField.getText().trim();

                if (token.isEmpty()) {

                        showError(
                                        "Please enter an authorization token.");

                        return;
                }

                Alert alert = new Alert(
                                Alert.AlertType.CONFIRMATION);
                Navigation.attachOwner(alert);

                alert.setTitle(
                                "Confirm Voting Completion");

                alert.setHeaderText(
                                "Complete this voting session?");

                Label message = new Label(
                                "Are you sure you want to mark this voting " +
                                                "session as completed?\n\n" +
                                                "Authorization token: " + token + "\n\n" +
                                                "Once completed, this action cannot be reversed.");

                message.setWrapText(true);

                message.setStyle(
                                "-fx-font-size:13px;" +
                                                "-fx-text-fill:#475569;");

                message.setMaxWidth(430);

                VBox dialogContent = new VBox(
                                10,
                                message);

                dialogContent.setPadding(
                                new Insets(5));

                alert.getDialogPane().setContent(
                                dialogContent);

                ButtonType cancelButton = new ButtonType("Cancel");

                ButtonType confirmButton = new ButtonType(
                                "Confirm & Complete");

                alert.getButtonTypes().setAll(
                                cancelButton,
                                confirmButton);

                Optional<ButtonType> response = alert.showAndWait();

                if (response.isPresent()
                                && response.get() == confirmButton) {

                        completeVote();
                }
        }

        // =========================================================
        // COMPLETE VOTE
        // =========================================================

        private void completeVote() {

                String token = tokenField.getText().trim();

                if (token.isEmpty()) {

                        showError(
                                        "Please enter an authorization token.");

                        return;
                }

                completeButton.setDisable(true);

                completeButton.setText(
                                "Processing...");

                try {

                        var r = controller.completeVote(token);

                        if (r.success()) {

                                showSuccess(
                                                r.message());

                                tokenField.setDisable(true);

                                completeButton.setText(
                                                "✓  Voting Completed");

                                completeButton.setDisable(true);

                        } else {

                                showError(
                                                r.message());

                                completeButton.setText(
                                                "✓  Confirm Voting Completed");

                                completeButton.setDisable(false);

                                setCompleteButtonEnabledStyle();
                        }

                } catch (Exception ex) {

                        ex.printStackTrace();

                        showError(
                                        "Unable to complete the voting session. " +
                                                        "Please try again.");

                        completeButton.setText(
                                        "✓  Confirm Voting Completed");

                        completeButton.setDisable(false);

                        setCompleteButtonEnabledStyle();
                }
        }

        // =========================================================
        // SUCCESS
        // =========================================================

        private void showSuccess(
                        String message) {

                result.setText(
                                "✓  " +
                                                message +
                                                "\n\n" +
                                                "The voter has been successfully marked as " +
                                                "having completed the voting process.");

                result.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;" +
                                                "-fx-padding:14;" +
                                                "-fx-background-color:#ECFDF3;" +
                                                "-fx-border-color:#BBF7D0;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:8;" +
                                                "-fx-background-radius:8;");

                result.setVisible(true);

                result.setManaged(true);
        }

        // =========================================================
        // ERROR
        // =========================================================

        private void showError(
                        String message) {

                result.setText(
                                "✕  " +
                                                message);

                result.setStyle(
                                "-fx-text-fill:#B42318;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;" +
                                                "-fx-padding:14;" +
                                                "-fx-background-color:#FFF1F0;" +
                                                "-fx-border-color:#FECDCA;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:8;" +
                                                "-fx-background-radius:8;");

                result.setVisible(true);

                result.setManaged(true);
        }

        // =========================================================
        // GET SCENE
        // =========================================================

        public Scene getScene() {

                return scene;
        }
}