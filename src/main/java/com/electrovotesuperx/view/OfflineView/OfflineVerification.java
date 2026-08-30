package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.controller.OfflineController.OfflineVerificationController;
import com.electrovotesuperx.dao.OfflineDAO.ElectionDAO;
import com.electrovotesuperx.exception.DatabaseException;
import com.electrovotesuperx.model.OfflineModel.Election;
import com.electrovotesuperx.service.OfflineService.VoterVerificationService;
import com.electrovotesuperx.view.CommonView.Header;
import com.electrovotesuperx.view.CommonView.Sidebar;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OfflineVerification {

        // =========================================================
        // FIELDS
        // =========================================================

        private final Scene scene;

        private final OfflineVerificationController controller = new OfflineVerificationController();

        private final ElectionDAO electionDAO = new ElectionDAO();

        private final ComboBox<Election> electionBox = new ComboBox<>();

        private final TextField voterIdField = new TextField();

        /*
         * VBox is used instead of Label so that the success
         * result can contain voter information and Copy Token button.
         */
        private final VBox result = new VBox(10);

        private final Button verify = new Button("✓  Verify Voter");

        /** Timer for OTP countdown — cancelled when OTP is verified or view is reset */
        private Timer otpCountdownTimer;

        /** Stores the pending verification result while OTP is being verified */
        private VoterVerificationService.VerificationResult pendingResult;

        // =========================================================
        // CONSTRUCTOR
        // =========================================================

        public OfflineVerification() {

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
                                Sidebar.getSidebar("VerificationPage"));

                // =====================================================
                // CENTER
                // =====================================================

                VBox center = new VBox();

                center.setFillWidth(true);

                // =====================================================
                // HEADER
                // =====================================================

                HBox topBar = new HBox();

                topBar.setMaxWidth(Double.MAX_VALUE);

                topBar.getChildren().add(
                                Header.getHeader(
                                                "Voter Verification",
                                                "Verify voter eligibility"));

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

                Label title = new Label(
                                "Verify Voter Eligibility");

                title.setStyle(
                                "-fx-font-size:28px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#172B4D;");

                Label help = new Label(
                                "Select an active election and verify the voter's " +
                                                "eligibility before issuing a voting authorization.");

                help.setWrapText(true);

                help.setMaxWidth(760);

                help.setStyle(
                                "-fx-font-size:14px;" +
                                                "-fx-text-fill:#71829B;");

                // =====================================================
                // TITLE CONTAINER
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

                HBox sessionVerifyBox = createSessionStatus();

                // =====================================================
                // MAIN VERIFICATION CARD
                // =====================================================

                VBox card = createVerificationCard();

                // =====================================================
                // ADD CONTENT VERTICALLY
                // =====================================================

                content.getChildren().addAll(
                                titleBox,
                                sessionVerifyBox,
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
                // LOAD ELECTIONS
                // =====================================================

                loadElections();

                // =====================================================
                // SCENE
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
                                "Voter Verification Required");
                status.setStyle(
                                "-fx-font-size:13px;" +
                                                "-fx-text-fill:#64748B;");

                HBox box = new HBox(
                                8,
                                dot,
                                title,
                                separator,
                                status);

                box.setAlignment(Pos.CENTER_LEFT);

                // SAME WIDTH AND ALIGNMENT AS VERIFY VOTER CARD
                box.setMaxWidth(760);
                box.setPrefWidth(760);
                box.setMinWidth(760);

                box.setPadding(
                                new Insets(10, 14, 10, 14));

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

                VBox card = new VBox(16);

                card.setMaxWidth(760);

                card.setPrefWidth(760);

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

                Label checkIcon = new Label("✓");

                checkIcon.setMinWidth(36);

                checkIcon.setPrefWidth(36);

                checkIcon.setMinHeight(36);

                checkIcon.setPrefHeight(36);

                checkIcon.setAlignment(
                                Pos.CENTER);

                checkIcon.setStyle(
                                "-fx-background-color:#E8F8FB;" +
                                                "-fx-text-fill:#079AB7;" +
                                                "-fx-font-size:17px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:18;");

                VBox cardTitleBox = new VBox(3);

                Label cardTitle = new Label(
                                "Voter Eligibility");

                cardTitle.setStyle(
                                "-fx-font-size:17px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#172B4D;");

                Label cardSubtitle = new Label(
                                "Verify the voter before continuing.");

                cardSubtitle.setStyle(
                                "-fx-font-size:13px;" +
                                                "-fx-text-fill:#71829B;");

                cardTitleBox.getChildren().addAll(
                                cardTitle,
                                cardSubtitle);

                cardHeader.getChildren().addAll(
                                checkIcon,
                                cardTitleBox);

                // =====================================================
                // DIVIDER
                // =====================================================

                Region divider = new Region();

                divider.setPrefHeight(1);

                divider.setMaxWidth(
                                Double.MAX_VALUE);

                divider.setStyle(
                                "-fx-background-color:#E8EDF3;");

                // =====================================================
                // ELECTION LABEL
                // =====================================================

                Label electionLabel = new Label("ACTIVE ELECTION");

                electionLabel.setStyle(
                                "-fx-font-size:11px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#526B91;");

                // =====================================================
                // ACTIVE STATUS
                // =====================================================

                HBox activeStatus = new HBox(5);

                activeStatus.setAlignment(
                                Pos.CENTER_LEFT);

                Label activeDot = new Label("●");

                activeDot.setStyle(
                                "-fx-text-fill:#16A36A;" +
                                                "-fx-font-size:11px;");

                Label activeText = new Label("ACTIVE");

                activeText.setStyle(
                                "-fx-font-size:11px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#087443;");

                activeStatus.getChildren().addAll(
                                activeDot,
                                activeText);

                HBox electionHeading = new HBox();

                Region electionSpacer = new Region();

                HBox.setHgrow(
                                electionSpacer,
                                Priority.ALWAYS);

                electionHeading.getChildren().addAll(
                                electionLabel,
                                electionSpacer,
                                activeStatus);

                // =====================================================
                // ELECTION COMBOBOX
                // =====================================================

                electionBox.setPrefHeight(46);

                electionBox.setMaxWidth(
                                Double.MAX_VALUE);

                electionBox.setPromptText(
                                "Select an active election");

                electionBox.setStyle(
                                "-fx-background-color:#FFFFFF;" +
                                                "-fx-border-color:#CBD5E1;" +
                                                "-fx-border-radius:7;" +
                                                "-fx-background-radius:7;" +
                                                "-fx-font-size:13px;");

                // =====================================================
                // ELECTION CELL
                // =====================================================

                electionBox.setCellFactory(
                                list -> new ListCell<Election>() {

                                        @Override
                                        protected void updateItem(
                                                        Election election,
                                                        boolean empty) {

                                                super.updateItem(
                                                                election,
                                                                empty);

                                                if (empty || election == null) {

                                                        setText(null);

                                                } else {

                                                        setText(
                                                                        election.getElectionId()
                                                                                        + " — "
                                                                                        + election.getName());

                                                        setStyle(
                                                                        "-fx-font-size:13px;" +
                                                                                        "-fx-text-fill:#172B4D;");
                                                }
                                        }
                                });

                // =====================================================
                // SELECTED ELECTION CELL
                // =====================================================

                electionBox.setButtonCell(
                                new ListCell<Election>() {

                                        @Override
                                        protected void updateItem(
                                                        Election election,
                                                        boolean empty) {

                                                super.updateItem(
                                                                election,
                                                                empty);

                                                if (empty || election == null) {

                                                        setText(null);

                                                } else {

                                                        setText(
                                                                        election.getElectionId()
                                                                                        + " — "
                                                                                        + election.getName());

                                                        setStyle(
                                                                        "-fx-font-size:13px;" +
                                                                                        "-fx-text-fill:#172B4D;");
                                                }
                                        }
                                });

                // =====================================================
                // VOTER ID LABEL
                // =====================================================

                Label voterLabel = new Label("VOTER ID");

                voterLabel.setStyle(
                                "-fx-font-size:11px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#526B91;");

                // =====================================================
                // VOTER ID FIELD
                // =====================================================

                voterIdField.setPromptText(
                                "Enter voter ID, e.g. VOT-A3030351");

                voterIdField.setPrefHeight(46);

                voterIdField.setMinHeight(46);

                voterIdField.setMaxWidth(
                                Double.MAX_VALUE);

                voterIdField.setStyle(
                                "-fx-background-color:#FFFFFF;" +
                                                "-fx-border-color:#CBD5E1;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:7;" +
                                                "-fx-background-radius:7;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-padding:0 12 0 12;" +
                                                "-fx-text-fill:#172B4D;");

                // =====================================================
                // VOTER FIELD FOCUS
                // =====================================================

                voterIdField.focusedProperty().addListener(
                                (obs, oldValue, focused) -> {

                                        if (focused) {

                                                voterIdField.setStyle(
                                                                "-fx-background-color:#FFFFFF;" +
                                                                                "-fx-border-color:#4B78B8;" +
                                                                                "-fx-border-width:1.5;" +
                                                                                "-fx-border-radius:7;" +
                                                                                "-fx-background-radius:7;" +
                                                                                "-fx-font-size:13px;" +
                                                                                "-fx-padding:0 12 0 12;" +
                                                                                "-fx-text-fill:#172B4D;");

                                        } else {

                                                voterIdField.setStyle(
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
                // VERIFY BUTTON
                // =====================================================

                verify.setPrefWidth(190);

                verify.setPrefHeight(46);

                verify.setDisable(true);

                verify.setStyle(
                                "-fx-background-color:#10B7D4;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-cursor:hand;");

                // =====================================================
                // VERIFY HOVER
                // =====================================================

                verify.setOnMouseEntered(e -> {

                        if (!verify.isDisabled()) {

                                verify.setStyle(
                                                "-fx-background-color:#0FA5C0;" +
                                                                "-fx-text-fill:white;" +
                                                                "-fx-font-size:13px;" +
                                                                "-fx-font-weight:800;" +
                                                                "-fx-background-radius:8;" +
                                                                "-fx-cursor:hand;");

                                verify.setScaleX(1.02);

                                verify.setScaleY(1.02);
                        }
                });

                verify.setOnMouseExited(e -> {

                        if (!verify.isDisabled()) {

                                verify.setStyle(
                                                "-fx-background-color:#10B7D4;" +
                                                                "-fx-text-fill:white;" +
                                                                "-fx-font-size:13px;" +
                                                                "-fx-font-weight:800;" +
                                                                "-fx-background-radius:8;" +
                                                                "-fx-cursor:hand;");
                        }

                        verify.setScaleX(1);

                        verify.setScaleY(1);
                });

                // =====================================================
                // VERIFY ACTION
                // =====================================================

                verify.setOnAction(
                                e -> performVerification());

                // =====================================================
                // ENTER KEY
                // =====================================================

                voterIdField.setOnAction(e -> {

                        if (!verify.isDisabled()) {

                                performVerification();
                        }
                });

                // =====================================================
                // ENABLE / DISABLE VERIFY BUTTON
                // =====================================================

                Runnable updateButtonState = () -> {

                        boolean valid = electionBox.getValue() != null
                                        &&
                                        !voterIdField
                                                        .getText()
                                                        .trim()
                                                        .isEmpty();

                        verify.setDisable(!valid);
                };

                electionBox.valueProperty().addListener(
                                (obs, oldValue, newValue) -> {

                                        clearResult();

                                        updateButtonState.run();
                                });

                voterIdField.textProperty().addListener(
                                (obs, oldValue, newValue) -> {

                                        clearResult();

                                        updateButtonState.run();
                                });

                // =====================================================
                // RESULT
                // =====================================================

                result.setVisible(false);

                result.setManaged(false);

                result.setMaxWidth(
                                Double.MAX_VALUE);

                result.setFillWidth(true);

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
                                                "Voter information is processed locally.");

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
                                electionHeading,
                                electionBox,
                                voterLabel,
                                voterIdField,
                                verify,
                                result,
                                securityBox);

                // =====================================================
                // RETURN CARD
                // =====================================================

                return card;
        }

        // =========================================================
        // LOAD ACTIVE ELECTIONS
        // =========================================================

        private void loadElections() {

                try {

                        List<Election> elections = electionDAO.findOpenElections();

                        electionBox
                                        .getItems()
                                        .setAll(elections);

                        if (elections.isEmpty()) {

                                electionBox.setDisable(true);

                                showError(
                                                "No active elections are available.");

                        } else {

                                electionBox.setDisable(false);

                                electionBox
                                                .getSelectionModel()
                                                .selectFirst();

                                clearResult();
                        }

                } catch (Exception e) {

                        e.printStackTrace();

                        electionBox.setDisable(true);

                        verify.setDisable(true);

                        showError(
                                        "Could not load elections.\n"
                                                        + e.getMessage());
                }
        }

        // =========================================================
        // VERIFY VOTER
        // =========================================================

        private void performVerification() {

                Election election = electionBox.getValue();

                if (election == null) {

                        showError(
                                        "Please select an active election.");

                        return;
                }

                String voterId = voterIdField
                                .getText()
                                .trim();

                if (voterId.isEmpty()) {

                        showError(
                                        "Please enter the Voter ID.");

                        return;
                }

                // =====================================================
                // DISABLE WHILE VERIFYING
                // =====================================================

                verify.setDisable(true);

                verify.setText(
                                "Verifying...");

                try {

                        VoterVerificationService.VerificationResult verification = controller.verify(
                                        voterId,
                                        election.getElectionId());

                        if (verification.success()) {

                                // =========================================
                                // OTP STEP
                                // Store the pending result and show OTP card
                                // =========================================

                                pendingResult = verification;

                                String otp = controller.generateOtp(
                                                election.getElectionId(),
                                                voterId);

                                // Live SMS Dispatch (Background thread)
                                final String finalOtp = otp;
                                final String voterName = verification.voter() != null ? verification.voter().getFullName() : "Voter";
                                final String voterPhone = verification.voter() != null ? verification.voter().getPhone() : null;
                                new Thread(() -> {
                                    try {
                                        if (voterPhone != null && !voterPhone.isBlank()) {
                                            com.electrovotesuperx.service.OfflineService.ClerkOtpService.sendSmsOtp(voterPhone, finalOtp, voterName);
                                        }
                                    } catch (Throwable ignored) {}
                                }).start();

                                showOtpCard(
                                                otp,
                                                election.getElectionId(),
                                                voterId,
                                                verification.voter().getFullName(),
                                                verification.voter().getVoterId(),
                                                voterPhone,
                                                verification.token());

                        } else {

                                showError(
                                                verification.message());

                                verify.setText(
                                                "✓  Verify Voter");

                                verify.setDisable(false);
                        }

                } catch (DatabaseException e) {

                        showError(
                                        "Unable to verify voter. Database error occurred.");

                        verify.setText(
                                        "✓  Verify Voter");

                        verify.setDisable(false);

                } catch (Exception e) {

                        showError(
                                        "Unable to verify voter.\n"
                                                        + e.getMessage());

                        verify.setText(
                                        "✓  Verify Voter");

                        verify.setDisable(false);
                }
        }

        // =========================================================
        // OTP VERIFICATION CARD WITH ANIMATIONS
        // =========================================================

        private void showOtpCard(
                        String generatedOtp,
                        String electionId,
                        String voterId,
                        String voterName,
                        String voterIdDisplay,
                        String voterPhone,
                        String token) {

                result.getChildren().clear();
                result.setVisible(true);
                result.setManaged(true);
                result.setMaxWidth(Double.MAX_VALUE);
                result.setFillWidth(true);
                result.setPadding(new Insets(20));
                result.setStyle(
                                "-fx-background-color:#F0F7FF;" +
                                                "-fx-background-radius:12;" +
                                                "-fx-border-color:#BDD5F5;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:12;");

                // Slide-in animation
                result.setTranslateY(30);
                result.setOpacity(0);

                TranslateTransition slideIn = new TranslateTransition(
                                Duration.millis(400), result);
                slideIn.setFromY(30);
                slideIn.setToY(0);

                FadeTransition fadeIn = new FadeTransition(
                                Duration.millis(400), result);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                slideIn.play();
                fadeIn.play();

                // =====================================================
                // OTP HEADER
                // =====================================================

                HBox otpHeader = new HBox(10);
                otpHeader.setAlignment(Pos.CENTER_LEFT);

                Label lockIcon = new Label("📲");
                lockIcon.setStyle("-fx-font-size:24px;");

                VBox otpTitleBox = new VBox(2);

                Label otpTitle = new Label("OTP Sent to Voter's Mobile Device");
                otpTitle.setStyle(
                                "-fx-font-size:16px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#1E3A5F;");

                String maskedPhone = (voterPhone != null && voterPhone.length() >= 4)
                        ? "ending in •••• " + voterPhone.substring(voterPhone.length() - 4)
                        : "registered mobile number";

                Label otpSubtitle = new Label(
                                "A 6-digit OTP was dispatched via Clerk SMS to " + voterName + " (" + maskedPhone + "). Ask the voter to provide the code.");
                otpSubtitle.setStyle(
                                "-fx-font-size:12.5px;" +
                                                "-fx-text-fill:#3B82F6;" +
                                                "-fx-font-weight:600;");

                otpTitleBox.getChildren().addAll(otpTitle, otpSubtitle);
                otpHeader.getChildren().addAll(lockIcon, otpTitleBox);

                // =====================================================
                // SECURE SMS DISPATCH STATUS & OFFICER OVERRIDE TOGGLE
                // =====================================================

                HBox smsStatusBox = new HBox(10);
                smsStatusBox.setAlignment(Pos.CENTER);
                smsStatusBox.setPadding(new Insets(10, 14, 10, 14));
                smsStatusBox.setStyle("-fx-background-color: #ECFDF5; -fx-background-radius: 8; -fx-border-color: #10B981; -fx-border-radius: 8;");

                Label smsSentIcon = new Label("✅ Live SMS Dispatched");
                smsSentIcon.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#065F46;");

                Region overrideSpacer = new Region();
                HBox.setHgrow(overrideSpacer, Priority.ALWAYS);

                Label overrideToggle = new Label("👁️ View OTP (Officer Override)");
                overrideToggle.setStyle("-fx-font-size:11px; -fx-text-fill:#64748B; -fx-cursor:hand; -fx-underline:true;");

                Label revealedOtpLabel = new Label("Code: " + generatedOtp);
                revealedOtpLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#1E3A5F; -fx-background-color:#DBEAFE; -fx-padding:2 6; -fx-background-radius:4;");
                revealedOtpLabel.setVisible(false);
                revealedOtpLabel.setManaged(false);

                overrideToggle.setOnMouseClicked(ev -> {
                    boolean show = !revealedOtpLabel.isVisible();
                    revealedOtpLabel.setVisible(show);
                    revealedOtpLabel.setManaged(show);
                    overrideToggle.setText(show ? "🙈 Hide OTP" : "👁️ View OTP (Officer Override)");
                });

                smsStatusBox.getChildren().addAll(smsSentIcon, overrideSpacer, revealedOtpLabel, overrideToggle);

                // =====================================================
                // COUNTDOWN TIMER
                // =====================================================

                Label countdownLabel = new Label("⏱ 60s remaining");
                countdownLabel.setStyle(
                                "-fx-font-size:12px;" +
                                                "-fx-font-weight:700;" +
                                                "-fx-text-fill:#4A7FAA;");

                // Progress bar for countdown
                Region progressTrack = new Region();
                progressTrack.setPrefHeight(4);
                progressTrack.setMaxWidth(Double.MAX_VALUE);
                progressTrack.setStyle(
                                "-fx-background-color:#DAE6F2;" +
                                                "-fx-background-radius:2;");

                Region progressBar = new Region();
                progressBar.setPrefHeight(4);
                progressBar.setMaxWidth(Double.MAX_VALUE);
                progressBar.setStyle(
                                "-fx-background-color:#3B82C4;" +
                                                "-fx-background-radius:2;");

                StackPane progressStack = new StackPane();
                progressStack.setAlignment(Pos.CENTER_LEFT);
                progressStack.getChildren().addAll(progressTrack, progressBar);
                progressStack.setMaxWidth(Double.MAX_VALUE);

                // Animate progress bar over 60 seconds
                Timeline progressTimeline = new Timeline(
                                new KeyFrame(Duration.ZERO,
                                                new KeyValue(progressBar.maxWidthProperty(),
                                                                Double.MAX_VALUE)),
                                new KeyFrame(Duration.seconds(60),
                                                new KeyValue(progressBar.maxWidthProperty(),
                                                                0)));
                progressTimeline.play();

                // =====================================================
                // 6 OTP INPUT BOXES
                // =====================================================

                Label enterLabel = new Label("Enter OTP:");
                enterLabel.setStyle(
                                "-fx-font-size:12px;" +
                                                "-fx-font-weight:700;" +
                                                "-fx-text-fill:#2D5F8A;");

                HBox otpInputRow = new HBox(8);
                otpInputRow.setAlignment(Pos.CENTER);

                TextField[] otpInputFields = new TextField[6];

                for (int i = 0; i < 6; i++) {

                        TextField field = new TextField();
                        field.setPrefWidth(46);
                        field.setMinWidth(46);
                        field.setMaxWidth(46);
                        field.setPrefHeight(50);
                        field.setMinHeight(50);
                        field.setAlignment(Pos.CENTER);
                        field.setStyle(
                                        "-fx-background-color:#FFFFFF;" +
                                                        "-fx-border-color:#C8D9EC;" +
                                                        "-fx-border-radius:8;" +
                                                        "-fx-background-radius:8;" +
                                                        "-fx-font-size:20px;" +
                                                        "-fx-font-weight:800;" +
                                                        "-fx-text-fill:#1E3A5F;");

                        // Auto-advance on digit entry
                        final int idx = i;
                        field.textProperty().addListener((obs, oldVal, newVal) -> {

                                if (newVal.length() > 1) {
                                        field.setText(newVal.substring(0, 1));
                                        return;
                                }

                                if (!newVal.isEmpty() && idx < 5) {
                                        otpInputFields[idx + 1].requestFocus();
                                }
                        });

                        // Focus style
                        field.focusedProperty().addListener((obs, oldVal, focused) -> {

                                if (focused) {
                                        field.setStyle(
                                                        "-fx-background-color:#FFFFFF;" +
                                                                        "-fx-border-color:#3B82C4;" +
                                                                        "-fx-border-width:2;" +
                                                                        "-fx-border-radius:8;" +
                                                                        "-fx-background-radius:8;" +
                                                                        "-fx-font-size:20px;" +
                                                                        "-fx-font-weight:800;" +
                                                                        "-fx-text-fill:#1E3A5F;");
                                } else {
                                        field.setStyle(
                                                        "-fx-background-color:#FFFFFF;" +
                                                                        "-fx-border-color:#C8D9EC;" +
                                                                        "-fx-border-radius:8;" +
                                                                        "-fx-background-radius:8;" +
                                                                        "-fx-font-size:20px;" +
                                                                        "-fx-font-weight:800;" +
                                                                        "-fx-text-fill:#1E3A5F;");
                                }
                        });

                        // Backspace to go to previous field
                        field.setOnKeyPressed(event -> {
                                if (event.getCode().toString().equals("BACK_SPACE")
                                                && field.getText().isEmpty()
                                                && idx > 0) {
                                        otpInputFields[idx - 1].requestFocus();
                                }
                        });

                        otpInputFields[i] = field;
                        otpInputRow.getChildren().add(field);
                }

                // =====================================================
                // OTP ERROR LABEL
                // =====================================================

                Label otpError = new Label();
                otpError.setWrapText(true);
                otpError.setVisible(false);
                otpError.setManaged(false);
                otpError.setStyle(
                                "-fx-text-fill:#DC2626;" +
                                                "-fx-font-size:12px;" +
                                                "-fx-font-weight:700;");

                // =====================================================
                // VERIFY OTP BUTTON
                // =====================================================

                Button verifyOtpBtn = new Button("🔓  Verify OTP");
                verifyOtpBtn.setPrefWidth(170);
                verifyOtpBtn.setPrefHeight(42);
                verifyOtpBtn.setStyle(
                                "-fx-background-color:#1D6FA5;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-cursor:hand;");

                verifyOtpBtn.setOnMouseEntered(e -> verifyOtpBtn.setStyle(
                                "-fx-background-color:#155A87;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-cursor:hand;"));

                verifyOtpBtn.setOnMouseExited(e -> verifyOtpBtn.setStyle(
                                "-fx-background-color:#1D6FA5;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-cursor:hand;"));

                // =====================================================
                // RESEND OTP BUTTON
                // =====================================================

                Button resendBtn = new Button("↻ Resend OTP");
                resendBtn.setStyle(
                                "-fx-background-color:transparent;" +
                                                "-fx-text-fill:#3B82C4;" +
                                                "-fx-font-size:12px;" +
                                                "-fx-font-weight:700;" +
                                                "-fx-cursor:hand;" +
                                                "-fx-underline:true;");

                // =====================================================
                // BUTTONS ROW
                // =====================================================

                HBox otpButtonRow = new HBox(14);
                otpButtonRow.setAlignment(Pos.CENTER_LEFT);
                otpButtonRow.getChildren().addAll(verifyOtpBtn, resendBtn);

                // =====================================================
                // COUNTDOWN TIMER LOGIC
                // =====================================================

                cancelOtpTimer();

                otpCountdownTimer = new Timer(true);
                final long startTime = System.currentTimeMillis();

                otpCountdownTimer.scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run() {

                                long elapsed = System.currentTimeMillis() - startTime;
                                int remaining = 60 - (int) (elapsed / 1000);

                                if (remaining <= 0) {
                                        javafx.application.Platform.runLater(() -> {
                                                countdownLabel.setText("⏱ OTP Expired");
                                                countdownLabel.setStyle(
                                                                "-fx-font-size:12px;" +
                                                                                "-fx-font-weight:700;" +
                                                                                "-fx-text-fill:#DC2626;");
                                                verifyOtpBtn.setDisable(true);
                                        });
                                        cancel();
                                } else {
                                        javafx.application.Platform.runLater(() -> {
                                                countdownLabel.setText("⏱ " + remaining + "s remaining");
                                        });
                                }
                        }
                }, 1000, 1000);

                // =====================================================
                // VERIFY OTP ACTION
                // =====================================================

                verifyOtpBtn.setOnAction(e -> {

                        StringBuilder inputOtp = new StringBuilder();
                        for (TextField f : otpInputFields) {
                                inputOtp.append(f.getText());
                        }

                        if (inputOtp.length() < 6) {
                                otpError.setText("Please enter all 6 digits.");
                                otpError.setVisible(true);
                                otpError.setManaged(true);
                                shakeNode(otpInputRow);
                                return;
                        }

                        boolean valid = controller.verifyOtp(
                                        electionId, voterId, inputOtp.toString());

                        if (valid) {

                                cancelOtpTimer();
                                progressTimeline.stop();

                                showOtpSuccess(
                                                voterName,
                                                voterIdDisplay,
                                                token);

                        } else {

                                if (controller.isOtpExpired(electionId, voterId)) {
                                        otpError.setText("OTP has expired. Click Resend OTP.");
                                } else {
                                        otpError.setText("Incorrect OTP. Please try again.");
                                }

                                otpError.setVisible(true);
                                otpError.setManaged(true);
                                shakeNode(otpInputRow);

                                // Flash input borders red
                                for (TextField f : otpInputFields) {
                                        f.setStyle(
                                                        "-fx-background-color:#FFFFFF;" +
                                                                        "-fx-border-color:#DC2626;" +
                                                                        "-fx-border-width:2;" +
                                                                        "-fx-border-radius:8;" +
                                                                        "-fx-background-radius:8;" +
                                                                        "-fx-font-size:20px;" +
                                                                        "-fx-font-weight:800;" +
                                                                        "-fx-text-fill:#1E3A5F;");
                                }

                                PauseTransition resetBorder = new PauseTransition(
                                                Duration.seconds(1.5));
                                resetBorder.setOnFinished(ev -> {
                                        for (TextField f : otpInputFields) {
                                                f.setStyle(
                                                                "-fx-background-color:#FFFFFF;" +
                                                                                "-fx-border-color:#C8D9EC;" +
                                                                                "-fx-border-radius:8;" +
                                                                                "-fx-background-radius:8;" +
                                                                                "-fx-font-size:20px;" +
                                                                                "-fx-font-weight:800;" +
                                                                                "-fx-text-fill:#1E3A5F;");
                                        }
                                });
                                resetBorder.play();
                        }
                });

                // =====================================================
                // RESEND OTP ACTION
                // =====================================================

                resendBtn.setOnAction(e -> {

                        cancelOtpTimer();
                        progressTimeline.stop();

                        // Clear inputs
                        for (TextField f : otpInputFields) {
                                f.clear();
                        }

                        otpError.setVisible(false);
                        otpError.setManaged(false);

                        // Generate new OTP
                        String newOtp = controller.generateOtp(
                                        electionId, voterId);

                        // Trigger SMS for Resend
                        new Thread(() -> {
                            try {
                                if (voterPhone != null && !voterPhone.isBlank()) {
                                    com.electrovotesuperx.service.OfflineService.ClerkOtpService.sendSmsOtp(voterPhone, newOtp, voterName);
                                }
                            } catch (Throwable ignored) {}
                        }).start();

                        showOtpCard(
                                        newOtp,
                                        electionId,
                                        voterId,
                                        voterName,
                                        voterIdDisplay,
                                        voterPhone,
                                        token);
                });

                // =====================================================
                // DIVIDER
                // =====================================================

                Region otpDivider = new Region();
                otpDivider.setPrefHeight(1);
                otpDivider.setMaxWidth(Double.MAX_VALUE);
                otpDivider.setStyle("-fx-background-color:#C8D9EC;");

                // =====================================================
                // ASSEMBLE OTP CARD
                // =====================================================

                result.getChildren().addAll(
                                otpHeader,
                                otpDivider,
                                smsStatusBox,
                                progressStack,
                                countdownLabel,
                                createVerticalSpace(6),
                                enterLabel,
                                otpInputRow,
                                otpError,
                                createVerticalSpace(4),
                                otpButtonRow);

                // Focus first input
                PauseTransition focusDelay = new PauseTransition(
                                Duration.millis(500));
                focusDelay.setOnFinished(ev -> otpInputFields[0].requestFocus());
                focusDelay.play();
        }

        // =========================================================
        // OTP SUCCESS ANIMATION → THEN SHOW TOKEN
        // =========================================================

        private void showOtpSuccess(
                        String voterName,
                        String voterId,
                        String token) {

                result.getChildren().clear();
                result.setStyle(
                                "-fx-background-color:#ECFDF3;" +
                                                "-fx-background-radius:12;" +
                                                "-fx-border-color:#B7E7CE;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:12;");

                // =====================================================
                // ANIMATED CHECKMARK
                // =====================================================

                Label checkmark = new Label("✓");
                checkmark.setMinWidth(52);
                checkmark.setPrefWidth(52);
                checkmark.setMinHeight(52);
                checkmark.setPrefHeight(52);
                checkmark.setAlignment(Pos.CENTER);
                checkmark.setStyle(
                                "-fx-background-color:#16A34A;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-size:24px;" +
                                                "-fx-font-weight:900;" +
                                                "-fx-background-radius:26;");

                // Pop-in animation
                checkmark.setScaleX(0);
                checkmark.setScaleY(0);

                ScaleTransition popIn = new ScaleTransition(
                                Duration.millis(400), checkmark);
                popIn.setFromX(0);
                popIn.setFromY(0);
                popIn.setToX(1);
                popIn.setToY(1);

                Label successText = new Label("OTP Verified Successfully!");
                successText.setStyle(
                                "-fx-font-size:16px;" +
                                                "-fx-font-weight:800;" +
                                                "-fx-text-fill:#087443;");
                successText.setOpacity(0);

                VBox successContent = new VBox(12);
                successContent.setAlignment(Pos.CENTER);
                successContent.setPadding(new Insets(20));
                successContent.getChildren().addAll(checkmark, successText);

                result.getChildren().add(successContent);

                popIn.play();

                FadeTransition textFade = new FadeTransition(
                                Duration.millis(400), successText);
                textFade.setFromValue(0);
                textFade.setToValue(1);
                textFade.setDelay(Duration.millis(300));
                textFade.play();

                // After animation, show the full token result
                PauseTransition showTokenDelay = new PauseTransition(
                                Duration.seconds(1.5));

                showTokenDelay.setOnFinished(ev -> {
                        showSuccess(voterName, voterId, token);
                });

                showTokenDelay.play();
        }

        // =========================================================
        // SHAKE ANIMATION (for incorrect OTP)
        // =========================================================

        private void shakeNode(javafx.scene.Node node) {

                TranslateTransition shake = new TranslateTransition(
                                Duration.millis(60), node);
                shake.setFromX(0);
                shake.setByX(10);
                shake.setCycleCount(6);
                shake.setAutoReverse(true);

                shake.setOnFinished(e -> node.setTranslateX(0));

                shake.play();
        }

        // =========================================================
        // CANCEL OTP TIMER
        // =========================================================

        private void cancelOtpTimer() {

                if (otpCountdownTimer != null) {
                        otpCountdownTimer.cancel();
                        otpCountdownTimer = null;
                }
        }

        // =========================================================
        // SUCCESS RESULT
        // =========================================================

        private void showSuccess(
                        String voterName,
                        String voterId,
                        String token) {

                // =====================================================
                // CLEAR PREVIOUS RESULT
                // =====================================================

                result.getChildren().clear();

                result.setVisible(true);

                result.setManaged(true);

                result.setMaxWidth(
                                Double.MAX_VALUE);

                result.setFillWidth(true);

                result.setPadding(
                                new Insets(15));

                result.setStyle(
                                "-fx-background-color:#ECFDF3;" +
                                                "-fx-background-radius:10;" +
                                                "-fx-border-color:#B7E7CE;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:10;");

                // =====================================================
                // VOTER VERIFIED
                // =====================================================

                Label verifiedLabel = new Label(
                                "✓  Voter Verified");

                verifiedLabel.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;");

                // =====================================================
                // VOTER NAME
                // =====================================================

                Label voterLabel = new Label(
                                "Voter: " + voterName);

                voterLabel.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;");

                // =====================================================
                // VOTER ID
                // =====================================================

                Label voterIdLabel = new Label(
                                "Voter ID: " + voterId);

                voterIdLabel.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;");

                // =====================================================
                // ELIGIBILITY TITLE
                // =====================================================

                Label eligibilityTitle = new Label(
                                "Eligibility");

                eligibilityTitle.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;");

                // =====================================================
                // ELIGIBLE
                // =====================================================

                Label eligibleLabel = new Label(
                                "✓  Eligible to vote");

                eligibleLabel.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;");

                // =====================================================
                // TOKEN LABEL
                // =====================================================

                Label tokenLabel = new Label(
                                "Token:");

                tokenLabel.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:800;");

                // =====================================================
                // TOKEN VALUE
                // =====================================================

                Label tokenValue = new Label(token);

                tokenValue.setStyle(
                                "-fx-text-fill:#087443;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;");

                // =====================================================
                // COPY TOKEN BUTTON
                // =====================================================

                Button copyTokenButton = new Button("⧉");

                copyTokenButton.setMinWidth(30);

                copyTokenButton.setPrefWidth(30);

                copyTokenButton.setMaxWidth(30);

                copyTokenButton.setMinHeight(30);

                copyTokenButton.setPrefHeight(30);

                copyTokenButton.setMaxHeight(30);

                copyTokenButton.setTooltip(
                                new Tooltip("Copy Token"));

                copyTokenButton.setStyle(
                                "-fx-background-color:#EEF4FF;" +
                                                "-fx-text-fill:#356AE6;" +
                                                "-fx-font-size:14px;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-background-radius:7;" +
                                                "-fx-border-color:#D8E5FF;" +
                                                "-fx-border-radius:7;" +
                                                "-fx-padding:2;" +
                                                "-fx-cursor:hand;");

                // =====================================================
                // COPY TOKEN ACTION
                // =====================================================

                copyTokenButton.setOnAction(e -> {

                        if (token == null || token.isBlank()) {
                                return;
                        }

                        Clipboard clipboard = Clipboard.getSystemClipboard();

                        ClipboardContent clipboardContent = new ClipboardContent();

                        clipboardContent.putString(token);

                        clipboard.setContent(
                                        clipboardContent);

                        // =================================================
                        // SHOW COPIED STATE
                        // =================================================

                        copyTokenButton.setText("✓");

                        copyTokenButton.setTooltip(
                                        new Tooltip("Copied!"));

                        copyTokenButton.setStyle(
                                        "-fx-background-color:#DFF5E8;" +
                                                        "-fx-text-fill:#16A34A;" +
                                                        "-fx-font-size:14px;" +
                                                        "-fx-font-weight:bold;" +
                                                        "-fx-background-radius:7;" +
                                                        "-fx-border-color:#B8E6C8;" +
                                                        "-fx-border-radius:7;" +
                                                        "-fx-padding:2;" +
                                                        "-fx-cursor:hand;");

                        // =================================================
                        // RESTORE AFTER 1 SECOND
                        // =================================================

                        PauseTransition pause = new PauseTransition(
                                        Duration.seconds(1));

                        pause.setOnFinished(event -> {

                                copyTokenButton.setText("⧉");

                                copyTokenButton.setTooltip(
                                                new Tooltip("Copy Token"));

                                copyTokenButton.setStyle(
                                                "-fx-background-color:#EEF4FF;" +
                                                                "-fx-text-fill:#356AE6;" +
                                                                "-fx-font-size:14px;" +
                                                                "-fx-font-weight:bold;" +
                                                                "-fx-background-radius:7;" +
                                                                "-fx-border-color:#D8E5FF;" +
                                                                "-fx-border-radius:7;" +
                                                                "-fx-padding:2;" +
                                                                "-fx-cursor:hand;");
                        });

                        pause.play();
                });

                // =====================================================
                // COPY BUTTON HOVER
                // =====================================================

                copyTokenButton.setOnMouseEntered(e -> {

                        if (!"✓".equals(
                                        copyTokenButton.getText())) {

                                copyTokenButton.setStyle(
                                                "-fx-background-color:#DCE8FF;" +
                                                                "-fx-text-fill:#2458C6;" +
                                                                "-fx-font-size:14px;" +
                                                                "-fx-font-weight:bold;" +
                                                                "-fx-background-radius:7;" +
                                                                "-fx-border-color:#C5D8FF;" +
                                                                "-fx-border-radius:7;" +
                                                                "-fx-padding:2;" +
                                                                "-fx-cursor:hand;");
                        }
                });

                copyTokenButton.setOnMouseExited(e -> {

                        if (!"✓".equals(
                                        copyTokenButton.getText())) {

                                copyTokenButton.setStyle(
                                                "-fx-background-color:#EEF4FF;" +
                                                                "-fx-text-fill:#356AE6;" +
                                                                "-fx-font-size:14px;" +
                                                                "-fx-font-weight:bold;" +
                                                                "-fx-background-radius:7;" +
                                                                "-fx-border-color:#D8E5FF;" +
                                                                "-fx-border-radius:7;" +
                                                                "-fx-padding:2;" +
                                                                "-fx-cursor:hand;");
                        }
                });

                // =====================================================
                // TOKEN ROW
                // =====================================================

                HBox tokenBox = new HBox(8);

                tokenBox.setAlignment(
                                Pos.CENTER_LEFT);

                tokenBox.getChildren().addAll(
                                tokenLabel,
                                tokenValue,
                                copyTokenButton);

                // =====================================================
                // RESULT CONTENT
                // =====================================================

                result.getChildren().addAll(
                                verifiedLabel,
                                voterLabel,
                                voterIdLabel,

                                createVerticalSpace(8),

                                eligibilityTitle,
                                eligibleLabel,

                                createVerticalSpace(8),

                                tokenBox);

                // =====================================================
                // KEEP VERIFY BUTTON DISABLED
                // =====================================================

                verify.setText(
                                "✓  Voter Verified");

                verify.setDisable(true);
        }

        // =========================================================
        // ERROR
        // =========================================================

        private void showError(
                        String text) {

                result.getChildren().clear();

                Label errorLabel = new Label(text);

                errorLabel.setWrapText(true);

                errorLabel.setMaxWidth(
                                Double.MAX_VALUE);

                errorLabel.setStyle(
                                "-fx-text-fill:#B42318;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-font-weight:700;");

                result.getChildren().add(
                                errorLabel);

                result.setVisible(true);

                result.setManaged(true);

                result.setMaxWidth(
                                Double.MAX_VALUE);

                result.setFillWidth(true);

                result.setPadding(
                                new Insets(15));

                result.setStyle(
                                "-fx-background-color:#FFF1F0;" +
                                                "-fx-background-radius:10;" +
                                                "-fx-border-color:#F3C4BF;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:10;");
        }

        // =========================================================
        // CLEAR RESULT
        // =========================================================

        private void clearResult() {

                result.getChildren().clear();

                result.setVisible(false);

                result.setManaged(false);

                result.setPadding(
                                Insets.EMPTY);
        }

        // =========================================================
        // VERTICAL SPACE
        // =========================================================

        private Region createVerticalSpace(
                        double height) {

                Region space = new Region();

                space.setMinHeight(height);

                space.setPrefHeight(height);

                space.setMaxHeight(height);

                return space;
        }

        // =========================================================
        // GET SCENE
        // =========================================================

        public Scene getScene() {

                return scene;
        }
}