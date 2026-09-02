package com.electrovotesuperx.view.OfflineView;

import com.electrovotesuperx.controller.OfflineController.OfflineVerificationController;
import com.electrovotesuperx.dao.OfflineDAO.ElectionDAO;
import com.electrovotesuperx.exception.DatabaseException;
import com.electrovotesuperx.model.OfflineModel.Election;
import com.electrovotesuperx.model.OfflineModel.Voter;
import com.electrovotesuperx.service.OfflineService.TwilioOtpService;
import com.electrovotesuperx.service.OfflineService.VoterVerificationService;
import com.electrovotesuperx.view.CommonView.Header;
import com.electrovotesuperx.view.CommonView.Sidebar;
import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseConfig;
import com.google.gson.JsonObject;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OfflineVerification {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    // =========================================================
    // FIELDS
    // =========================================================

    private final Scene scene;
    private final OfflineVerificationController controller = new OfflineVerificationController();
    private final ElectionDAO electionDAO = new ElectionDAO();
    private final ComboBox<Election> electionBox = new ComboBox<>();
    private final TextField voterIdField = new TextField();
    private final VBox result = new VBox(12);
    private final Button verify = new Button("✓  Verify Voter");

    /** Timer for OTP countdown — cancelled when OTP is verified or view is reset */
    private Timer otpCountdownTimer;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OfflineVerification() {

        // =====================================================
        // ROOT CONTAINER
        // =====================================================
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F8FAFC;");

        // =====================================================
        // SIDEBAR
        // =====================================================
        root.setLeft(Sidebar.getSidebar("VerificationPage"));

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
        topBar.getChildren().add(Header.getHeader("Voter Verification", "Verify voter eligibility and issue voting tokens"));

        // =====================================================
        // CONTENT AREA
        // =====================================================
        VBox content = new VBox(20);
        content.setPadding(new Insets(32, 40, 48, 40));
        content.setFillWidth(true);
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #F8FAFC;");

        // =====================================================
        // PAGE TITLE
        // =====================================================
        Label title = new Label("Verify Voter Eligibility");
        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #0F172A;"
        );

        Label help = new Label(
                "Select an active election and verify voter eligibility to issue an official offline voting authorization token."
        );
        help.setWrapText(true);
        help.setMaxWidth(780);
        help.setStyle(
                "-fx-font-size: 13.5px;" +
                "-fx-text-fill: #64748B;"
        );

        VBox titleBox = new VBox(6);
        titleBox.setMaxWidth(780);
        titleBox.setPrefWidth(780);
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.getChildren().addAll(title, help);

        // =====================================================
        // SESSION STATUS BANNER
        // =====================================================
        HBox sessionVerifyBox = createSessionStatus();

        // =====================================================
        // MAIN VERIFICATION CARD
        // =====================================================
        VBox card = createVerificationCard();

        // =====================================================
        // ADD TO CONTENT
        // =====================================================
        content.getChildren().addAll(titleBox, sessionVerifyBox, card);

        // =====================================================
        // SCROLL PANE
        // =====================================================
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(true);
        scroll.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        center.getChildren().addAll(topBar, scroll);
        root.setCenter(center);

        // =====================================================
        // LOAD ACTIVE ELECTIONS
        // =====================================================
        loadElections();

        // =====================================================
        // SCENE
        // =====================================================
        scene = new Scene(root, 1280, 760);
    }

    // =========================================================
    // SESSION STATUS BANNER
    // =========================================================

    private HBox createSessionStatus() {
        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 12px; -fx-text-fill: #16A34A;");

        Label title = new Label("Active Polling Session");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #166534;");

        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        Label status = new Label("Voter Identity & SMS Verification Required");
        status.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        HBox box = new HBox(10, dot, title, separator, status);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(780);
        box.setPrefWidth(780);
        box.setMinWidth(780);
        box.setPadding(new Insets(12, 18, 12, 18));
        box.setStyle(
                "-fx-background-color: #F0FDF4;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #BBF7D0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
        );

        return box;
    }

    // =========================================================
    // MAIN VERIFICATION CARD
    // =========================================================

    private VBox createVerificationCard() {
        VBox card = new VBox(20);
        card.setMaxWidth(780);
        card.setPrefWidth(780);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.05), 12, 0, 0, 4);"
        );

        // =====================================================
        // CARD HEADER
        // =====================================================
        HBox cardHeader = new HBox(14);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label badgeIcon = new Label("🛡️");
        badgeIcon.setMinWidth(42);
        badgeIcon.setPrefWidth(42);
        badgeIcon.setMinHeight(42);
        badgeIcon.setPrefHeight(42);
        badgeIcon.setAlignment(Pos.CENTER);
        badgeIcon.setStyle(
                "-fx-background-color: #EFF6FF;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #DBEAFE;" +
                "-fx-border-radius: 12;"
        );

        VBox cardTitleBox = new VBox(3);
        Label cardTitle = new Label("Voter Eligibility Verification");
        cardTitle.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #0F172A;"
        );

        Label cardSubtitle = new Label("Verify voter credentials and authenticate via SMS code before issuing token.");
        cardSubtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748B;"
        );

        cardTitleBox.getChildren().addAll(cardTitle, cardSubtitle);
        cardHeader.getChildren().addAll(badgeIcon, cardTitleBox);

        // Divider
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: #F1F5F9;");

        // =====================================================
        // ELECTION HEADING
        // =====================================================
        Label electionLabel = new Label("ACTIVE ELECTION");
        electionLabel.setStyle(
                "-fx-font-size: 11.5px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #475569;" +
                "-fx-letter-spacing: 0.5px;"
        );

        HBox activeStatus = new HBox(5);
        activeStatus.setAlignment(Pos.CENTER_LEFT);
        activeStatus.setPadding(new Insets(3, 8, 3, 8));
        activeStatus.setStyle(
                "-fx-background-color: #ECFDF5;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #A7F3D0;" +
                "-fx-border-radius: 6;"
        );

        Label activeDot = new Label("●");
        activeDot.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 9px;");

        Label activeText = new Label("OPEN");
        activeText.setStyle("-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-text-fill: #065F46;");

        activeStatus.getChildren().addAll(activeDot, activeText);

        HBox electionHeading = new HBox();
        Region electionSpacer = new Region();
        HBox.setHgrow(electionSpacer, Priority.ALWAYS);
        electionHeading.getChildren().addAll(electionLabel, electionSpacer, activeStatus);

        // =====================================================
        // ELECTION COMBOBOX
        // =====================================================
        electionBox.setPrefHeight(46);
        electionBox.setMaxWidth(Double.MAX_VALUE);
        electionBox.setPromptText("Select an active election");
        electionBox.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13.5px;"
        );

        electionBox.setCellFactory(list -> new ListCell<Election>() {
            @Override
            protected void updateItem(Election election, boolean empty) {
                super.updateItem(election, empty);
                if (empty || election == null) {
                    setText(null);
                } else {
                    setText(election.getElectionId() + " — " + election.getName());
                    setStyle("-fx-font-size: 13.5px; -fx-text-fill: #0F172A; -fx-padding: 8 12;");
                }
            }
        });

        electionBox.setButtonCell(new ListCell<Election>() {
            @Override
            protected void updateItem(Election election, boolean empty) {
                super.updateItem(election, empty);
                if (empty || election == null) {
                    setText(null);
                } else {
                    setText(election.getElectionId() + " — " + election.getName());
                    setStyle("-fx-font-size: 13.5px; -fx-text-fill: #0F172A;");
                }
            }
        });

        // =====================================================
        // VOTER ID INPUT
        // =====================================================
        Label voterLabel = new Label("VOTER ID");
        voterLabel.setStyle(
                "-fx-font-size: 11.5px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #475569;" +
                "-fx-letter-spacing: 0.5px;"
        );

        voterIdField.setPromptText("Enter voter ID (e.g. VOT-A3030351)");
        voterIdField.setPrefHeight(46);
        voterIdField.setMinHeight(46);
        voterIdField.setMaxWidth(Double.MAX_VALUE);
        voterIdField.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-width: 1.2;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13.5px;" +
                "-fx-padding: 0 14 0 14;" +
                "-fx-text-fill: #0F172A;"
        );

        voterIdField.focusedProperty().addListener((obs, oldValue, focused) -> {
            if (focused) {
                voterIdField.setStyle(
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #0284C7;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-padding: 0 14 0 14;" +
                        "-fx-text-fill: #0F172A;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(2, 132, 199, 0.15), 6, 0, 0, 0);"
                );
            } else {
                voterIdField.setStyle(
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-padding: 0 14 0 14;" +
                        "-fx-text-fill: #0F172A;"
                );
            }
        });

        // =====================================================
        // VERIFY BUTTON
        // =====================================================
        verify.setPrefWidth(200);
        verify.setPrefHeight(46);
        verify.setDisable(true);
        verify.setStyle(
                "-fx-background-color: #0284C7;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(2, 132, 199, 0.25), 8, 0, 0, 2);"
        );

        verify.setOnMouseEntered(e -> {
            if (!verify.isDisabled()) {
                verify.setStyle(
                        "-fx-background-color: #0369A1;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(3, 105, 161, 0.35), 10, 0, 0, 3);"
                );
                verify.setScaleX(1.015);
                verify.setScaleY(1.015);
            }
        });

        verify.setOnMouseExited(e -> {
            if (!verify.isDisabled()) {
                verify.setStyle(
                        "-fx-background-color: #0284C7;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(2, 132, 199, 0.25), 8, 0, 0, 2);"
                );
            }
            verify.setScaleX(1.0);
            verify.setScaleY(1.0);
        });

        verify.setOnAction(e -> performVerification());
        voterIdField.setOnAction(e -> {
            if (!verify.isDisabled()) {
                performVerification();
            }
        });

        Runnable updateButtonState = () -> {
            boolean valid = electionBox.getValue() != null && !voterIdField.getText().trim().isEmpty();
            verify.setDisable(!valid);
            if (valid) {
                verify.setStyle(
                        "-fx-background-color: #0284C7;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(2, 132, 199, 0.25), 8, 0, 0, 2);"
                );
            } else {
                verify.setStyle(
                        "-fx-background-color: #CBD5E1;" +
                        "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-background-radius: 8;"
                );
            }
        };

        electionBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            clearResult();
            updateButtonState.run();
        });

        voterIdField.textProperty().addListener((obs, oldValue, newValue) -> {
            clearResult();
            updateButtonState.run();
        });

        // =====================================================
        // DYNAMIC RESULT CONTAINER
        // =====================================================
        result.setVisible(false);
        result.setManaged(false);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setMinHeight(Region.USE_PREF_SIZE);

        // =====================================================
        // SECURITY FOOTER INFORMATION
        // =====================================================
        HBox securityBox = new HBox(12);
        securityBox.setAlignment(Pos.CENTER_LEFT);
        securityBox.setPadding(new Insets(14, 16, 14, 16));
        securityBox.setMaxWidth(Double.MAX_VALUE);
        securityBox.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10;"
        );

        Label lock = new Label("🔒");
        lock.setStyle("-fx-font-size: 15px;");

        Label securityText = new Label("Encrypted Verification • Voter biometric & phone data is processed securely with local cryptographic tokens.");
        securityText.setWrapText(true);
        securityText.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-line-spacing: 2;");

        securityBox.getChildren().addAll(lock, securityText);

        // =====================================================
        // ASSEMBLE CARD
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
                securityBox
        );

        return card;
    }

    // =========================================================
    // LOAD ACTIVE ELECTIONS
    // =========================================================

    private void loadElections() {
        try {
            List<Election> elections = electionDAO.findOpenElections();
            electionBox.getItems().setAll(elections);

            if (elections.isEmpty()) {
                electionBox.setDisable(true);
                showError("No active elections are available.");
            } else {
                electionBox.setDisable(false);
                electionBox.getSelectionModel().selectFirst();
                clearResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
            electionBox.setDisable(true);
            verify.setDisable(true);
            showError("Could not load elections.\n" + e.getMessage());
        }
    }

    // =========================================================
    // VERIFY VOTER
    // =========================================================

    private void performVerification() {
        Election election = electionBox.getValue();
        if (election == null) {
            showError("Please select an active election.");
            return;
        }

        String voterId = voterIdField.getText().trim();
        if (voterId.isEmpty()) {
            showError("Please enter the Voter ID.");
            return;
        }

        verify.setDisable(true);
        verify.setText("Checking eligibility...");

        try {
            VoterVerificationService.EligibilityResult eligibility =
                    controller.validateEligibility(voterId, election.getElectionId());

            if (!eligibility.eligible()) {
                showError(eligibility.message());
                verify.setText("✓  Verify Voter");
                verify.setDisable(false);
                return;
            }

            Voter voter = eligibility.voter();
            String phone = voter.getPhone();

            if (phone == null || phone.isBlank()) {
                showPhonePrompt(election.getElectionId(), voter);
                verify.setText("✓  Verify Voter");
                verify.setDisable(false);
                return;
            }

            // Send direct carrier SMS OTP
            sendSmsOtp(election.getElectionId(), voter, phone);

        } catch (DatabaseException e) {
            showError("Unable to verify voter. Database error occurred: " + e.getMessage());
            verify.setText("✓  Verify Voter");
            verify.setDisable(false);
        } catch (Exception e) {
            showError("Unable to verify voter: " + e.getMessage());
            verify.setText("✓  Verify Voter");
            verify.setDisable(false);
        }
    }

    // =========================================================
    // INITIATE SMS OTP DISPATCH
    // =========================================================

    private void sendSmsOtp(String electionId, Voter voter, String phone) {
        showSmsSendingCard(electionId, voter, phone);

        controller.sendTwilioSmsOtp(
                electionId,
                voter.getVoterId(),
                voter.getFullName(),
                phone,
                new TwilioOtpService.TwilioOtpCallback() {
                    @Override
                    public void onOtpSent(String generatedOtp, String formattedPhone) {
                        Platform.runLater(() -> {
                            showOtpCard(electionId, voter, formattedPhone);
                            verify.setText("✓  Verify Voter");
                            verify.setDisable(false);
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Platform.runLater(() -> {
                            showOtpSendError(errorMessage, electionId, voter, phone);
                            verify.setText("✓  Verify Voter");
                            verify.setDisable(false);
                        });
                    }

                    @Override
                    public void onStatusUpdate(String status) {
                        System.out.println("[OfflineVerification] Status: " + status);
                    }
                });
    }

    // =========================================================
    // SMS DISPATCHING LOADING CARD
    // =========================================================

    private void showSmsSendingCard(String electionId, Voter voter, String phone) {
        result.getChildren().clear();
        result.setVisible(true);
        result.setManaged(true);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setPadding(new Insets(24));
        result.setStyle(
                "-fx-background-color: #F0F9FF;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #BAE6FD;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📡");
        icon.setStyle("-fx-font-size: 26px;");

        VBox titleBox = new VBox(3);
        Label title = new Label("Dispatching SMS Verification Code...");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #0369A1;");

        String masked = TwilioOtpService.maskPhoneNumber(phone);
        Label subtitle = new Label("Sending a 6-digit verification code directly to " + voter.getFullName() + " (" + masked + ") via secure carrier network.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #0284C7;");

        titleBox.getChildren().addAll(title, subtitle);
        header.getChildren().addAll(icon, titleBox);

        // Indeterminate smooth progress bar confined to card bounds
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(4);
        progressBar.setMinHeight(4);
        progressBar.setStyle(
                "-fx-accent: #0284C7;" +
                "-fx-control-inner-background: #E0F2FE;" +
                "-fx-background-color: #E0F2FE;" +
                "-fx-background-radius: 4;"
        );

        result.getChildren().addAll(header, createVerticalSpace(14), progressBar);
    }

    // =========================================================
    // PHONE NUMBER PROMPT (IF VOTER HAS NO REGISTERED PHONE)
    // =========================================================

    private void showPhonePrompt(String electionId, Voter voter) {
        result.getChildren().clear();
        result.setVisible(true);
        result.setManaged(true);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setPadding(new Insets(22));
        result.setStyle(
                "-fx-background-color: #FFFBEB;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #FDE68A;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        Label title = new Label("📱 Mobile Number Required for Verification");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #92400E;");

        Label desc = new Label("Voter " + voter.getFullName() + " has no mobile number registered. Enter a 10-digit mobile number to send the SMS verification code:");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #78350F;");

        TextField phoneInput = new TextField();
        phoneInput.setPromptText("Enter 10-digit mobile number, e.g. 9876543210");
        phoneInput.setPrefHeight(44);
        phoneInput.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13.5px;" +
                "-fx-padding: 0 12 0 12;"
        );

        Button sendBtn = new Button("Send SMS Code  →");
        sendBtn.setPrefHeight(44);
        sendBtn.setPrefWidth(180);
        sendBtn.setStyle(
                "-fx-background-color: #D97706;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: 800;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );
        sendBtn.setOnAction(e -> {
            String p = phoneInput.getText().trim();
            if (p.isEmpty()) {
                showError("Please enter a valid mobile number.");
                return;
            }
            sendSmsOtp(electionId, voter, p);
        });

        HBox row = new HBox(12, phoneInput, sendBtn);
        HBox.setHgrow(phoneInput, Priority.ALWAYS);

        result.getChildren().addAll(title, desc, createVerticalSpace(8), row);
    }

    // =========================================================
    // SMS DISPATCH ERROR VIEW
    // =========================================================

    private void showOtpSendError(String errorMessage, String electionId, Voter voter, String phone) {
        result.getChildren().clear();
        result.setVisible(true);
        result.setManaged(true);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setPadding(new Insets(22));
        result.setStyle(
                "-fx-background-color: #FEF2F2;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #FECACA;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        Label errTitle = new Label("❌ SMS Dispatch Notice");
        errTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #991B1B;");

        Label errMsg = new Label(errorMessage);
        errMsg.setWrapText(true);
        errMsg.setStyle("-fx-font-size: 13px; -fx-text-fill: #B91C1C; -fx-font-weight: 600;");

        Label helpMsg = new Label("If cellular networks are offline or testing without network access, you can proceed with Officer Emergency Override.");
        helpMsg.setWrapText(true);
        helpMsg.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F1D1D;");

        Button retryBtn = new Button("↻ Retry SMS Dispatch");
        retryBtn.setPrefHeight(40);
        retryBtn.setStyle(
                "-fx-background-color: #DC2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: 800;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );
        retryBtn.setOnAction(e -> sendSmsOtp(electionId, voter, phone));

        Button overrideBtn = new Button("🛡️ Officer Emergency Override");
        overrideBtn.setPrefHeight(40);
        overrideBtn.setStyle(
                "-fx-background-color: #475569;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: 800;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );
        overrideBtn.setOnAction(e -> executeEmergencyOverride(electionId, voter));

        HBox btnRow = new HBox(12, retryBtn, overrideBtn);

        result.getChildren().addAll(errTitle, errMsg, helpMsg, createVerticalSpace(10), btnRow);
    }

    // =========================================================
    // OTP VERIFICATION CARD (CLEAN & MODERN UI)
    // =========================================================

    private void showOtpCard(
            String electionId,
            Voter voter,
            String formattedPhone) {

        result.getChildren().clear();
        result.setVisible(true);
        result.setManaged(true);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setPadding(new Insets(24));
        result.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-width: 1.2;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.04), 8, 0, 0, 2);"
        );

        // Slide-in animation
        result.setTranslateY(20);
        result.setOpacity(0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), result);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), result);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        slideIn.play();
        fadeIn.play();

        // =====================================================
        // HEADER ROW
        // =====================================================
        HBox otpHeader = new HBox(14);
        otpHeader.setAlignment(Pos.CENTER_LEFT);

        Label phoneIconBadge = new Label("📲");
        phoneIconBadge.setMinWidth(42);
        phoneIconBadge.setPrefWidth(42);
        phoneIconBadge.setMinHeight(42);
        phoneIconBadge.setPrefHeight(42);
        phoneIconBadge.setAlignment(Pos.CENTER);
        phoneIconBadge.setStyle(
                "-fx-background-color: #E0F2FE;" +
                "-fx-font-size: 20px;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #BAE6FD;" +
                "-fx-border-radius: 12;"
        );

        VBox otpTitleBox = new VBox(3);
        Label otpTitle = new Label("SMS OTP Dispatched to Voter");
        otpTitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #0F172A;"
        );

        String maskedPhone = TwilioOtpService.maskPhoneNumber(formattedPhone);
        Label otpSubtitle = new Label(
                "A 6-digit SMS OTP was sent to " + voter.getFullName() + " (" + maskedPhone + "). Ask the voter for the code."
        );
        otpSubtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #0284C7;" +
                "-fx-font-weight: 600;"
        );

        otpTitleBox.getChildren().addAll(otpTitle, otpSubtitle);
        otpHeader.getChildren().addAll(phoneIconBadge, otpTitleBox);

        // =====================================================
        // CARRIER DISPATCH STATUS & OFFICER OVERRIDE TOGGLE
        // =====================================================
        HBox smsStatusBox = new HBox(12);
        smsStatusBox.setAlignment(Pos.CENTER_LEFT);
        smsStatusBox.setPadding(new Insets(10, 16, 10, 16));
        smsStatusBox.setStyle(
                "-fx-background-color: #F0FDF4;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #86EFAC;" +
                "-fx-border-radius: 8;"
        );

        Label smsSentIcon = new Label("✓ Live Carrier SMS Dispatched");
        smsSentIcon.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 800; -fx-text-fill: #15803D;");

        Region overrideSpacer = new Region();
        HBox.setHgrow(overrideSpacer, Priority.ALWAYS);

        Label overrideToggle = new Label("Officer Emergency Override");
        overrideToggle.setTooltip(new Tooltip("Authorize voter directly if cell network is unavailable"));
        overrideToggle.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: #475569;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;"
        );

        overrideToggle.setOnMouseEntered(ev -> overrideToggle.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;"
        ));

        overrideToggle.setOnMouseExited(ev -> overrideToggle.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: #475569;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;"
        ));

        overrideToggle.setOnMouseClicked(ev -> executeEmergencyOverride(electionId, voter));

        smsStatusBox.getChildren().addAll(smsSentIcon, overrideSpacer, overrideToggle);

        // =====================================================
        // COUNTDOWN TIMER
        // =====================================================
        Label countdownLabel = new Label("⏱ 60s remaining");
        countdownLabel.setStyle(
                "-fx-font-size: 12.5px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: #0369A1;"
        );

        // =====================================================
        // 6 OTP INPUT BOXES
        // =====================================================
        Label enterLabel = new Label("Enter 6-Digit SMS Code:");
        enterLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #1E293B;"
        );

        HBox otpInputRow = new HBox(10);
        otpInputRow.setAlignment(Pos.CENTER_LEFT);

        TextField[] otpInputFields = new TextField[6];

        for (int i = 0; i < 6; i++) {
            TextField field = new TextField();
            field.setPrefWidth(50);
            field.setMinWidth(50);
            field.setMaxWidth(50);
            field.setPrefHeight(54);
            field.setMinHeight(54);
            field.setAlignment(Pos.CENTER);
            field.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                    "-fx-border-color: #CBD5E1;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 22px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-text-fill: #0F172A;"
            );

            final int idx = i;

            // Auto-advance & 6-digit paste handling
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1) {
                    String cleaned = newVal.replaceAll("[^0-9]", "");
                    if (cleaned.length() >= 6) {
                        for (int k = 0; k < 6; k++) {
                            otpInputFields[k].setText(String.valueOf(cleaned.charAt(k)));
                        }
                        otpInputFields[5].requestFocus();
                        return;
                    }
                    field.setText(newVal.substring(0, 1));
                    return;
                }

                if (!newVal.isEmpty() && idx < 5) {
                    otpInputFields[idx + 1].requestFocus();
                }
            });

            // Focus glow styling
            field.focusedProperty().addListener((obs, oldVal, focused) -> {
                if (focused) {
                    field.setStyle(
                            "-fx-background-color: #FFFFFF;" +
                            "-fx-border-color: #0284C7;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-size: 22px;" +
                            "-fx-font-weight: 800;" +
                            "-fx-text-fill: #0F172A;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(2, 132, 199, 0.25), 8, 0, 0, 0);"
                    );
                } else {
                    field.setStyle(
                            "-fx-background-color: #FFFFFF;" +
                            "-fx-border-color: #CBD5E1;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-size: 22px;" +
                            "-fx-font-weight: 800;" +
                            "-fx-text-fill: #0F172A;"
                    );
                }
            });

            // Key handling: Backspace & Arrows
            field.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.BACK_SPACE && field.getText().isEmpty() && idx > 0) {
                    otpInputFields[idx - 1].requestFocus();
                } else if (event.getCode() == KeyCode.LEFT && idx > 0) {
                    otpInputFields[idx - 1].requestFocus();
                } else if (event.getCode() == KeyCode.RIGHT && idx < 5) {
                    otpInputFields[idx + 1].requestFocus();
                }
            });

            otpInputFields[i] = field;
            otpInputRow.getChildren().add(field);
        }

        // =====================================================
        // OTP ERROR / WARNING LABEL
        // =====================================================
        Label otpError = new Label();
        otpError.setWrapText(true);
        otpError.setVisible(false);
        otpError.setManaged(false);
        otpError.setStyle(
                "-fx-text-fill: #DC2626;" +
                "-fx-font-size: 12.5px;" +
                "-fx-font-weight: 700;"
        );

        // =====================================================
        // VERIFY OTP BUTTON
        // =====================================================
        Button verifyOtpBtn = new Button("🔒  Verify SMS OTP");
        verifyOtpBtn.setPrefWidth(210);
        verifyOtpBtn.setPrefHeight(44);
        verifyOtpBtn.setStyle(
                "-fx-background-color: #0284C7;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(2, 132, 199, 0.25), 8, 0, 0, 2);"
        );

        verifyOtpBtn.setOnMouseEntered(e -> verifyOtpBtn.setStyle(
                "-fx-background-color: #0369A1;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(3, 105, 161, 0.35), 10, 0, 0, 3);"
        ));

        verifyOtpBtn.setOnMouseExited(e -> verifyOtpBtn.setStyle(
                "-fx-background-color: #0284C7;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(2, 132, 199, 0.25), 8, 0, 0, 2);"
        ));

        // =====================================================
        // RESEND OTP BUTTON
        // =====================================================
        Button resendBtn = new Button("↻ Resend SMS");
        resendBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #0284C7;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;"
        );

        resendBtn.setOnMouseEntered(e -> resendBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #0369A1;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 800;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;"
        ));

        resendBtn.setOnMouseExited(e -> resendBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #0284C7;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;"
        ));

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
                    Platform.runLater(() -> {
                        countdownLabel.setText("⏱ OTP Expired");
                        countdownLabel.setStyle(
                                "-fx-font-size: 12.5px;" +
                                "-fx-font-weight: 700;" +
                                "-fx-text-fill: #DC2626;"
                        );
                    });
                    cancel();
                } else {
                    Platform.runLater(() -> {
                        countdownLabel.setText("⏱ " + remaining + "s remaining");
                        if (remaining <= 15) {
                            countdownLabel.setStyle(
                                    "-fx-font-size: 12.5px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-text-fill: #D97706;"
                            );
                        }
                    });
                }
            }
        }, 1000, 1000);

        // =====================================================
        // VERIFY OTP SUBMISSION ACTION
        // =====================================================
        Runnable executeVerificationAction = () -> {
            StringBuilder inputOtp = new StringBuilder();
            for (TextField f : otpInputFields) {
                inputOtp.append(f.getText().trim());
            }

            if (inputOtp.length() < 6) {
                otpError.setText("Please enter all 6 digits of the SMS OTP.");
                otpError.setVisible(true);
                otpError.setManaged(true);
                shakeNode(otpInputRow);
                return;
            }

            boolean valid = controller.verifyTwilioOtp(electionId, voter.getVoterId(), inputOtp.toString());

            if (valid) {
                cancelOtpTimer();

                try {
                    VoterVerificationService.VerificationResult tokenResult =
                            controller.issueTokenAfterVerification(voter, electionId, "SMS OTP");

                    if (tokenResult.success()) {
                        syncVerificationToFirebase(electionId, voter, tokenResult.token(), "SMS OTP");
                        showOtpSuccess(voter.getFullName(), voter.getVoterId(), tokenResult.token());
                    } else {
                        showError(tokenResult.message());
                    }
                } catch (Exception ex) {
                    showError("Error issuing voter token: " + ex.getMessage());
                }
            } else {
                if (controller.isOtpExpired(electionId, voter.getVoterId())) {
                    otpError.setText("OTP has expired. Click 'Resend SMS' to receive a new code.");
                } else {
                    otpError.setText("Incorrect OTP code. Please verify the 6-digit code on voter's phone.");
                }

                otpError.setVisible(true);
                otpError.setManaged(true);
                shakeNode(otpInputRow);
                flashInputBorders(otpInputFields);
            }
        };

        verifyOtpBtn.setOnAction(e -> executeVerificationAction.run());
        for (TextField f : otpInputFields) {
            f.setOnAction(e -> executeVerificationAction.run());
        }

        // =====================================================
        // RESEND OTP ACTION
        // =====================================================
        resendBtn.setOnAction(e -> {
            cancelOtpTimer();
            sendSmsOtp(electionId, voter, formattedPhone);
        });

        // Divider
        Region otpDivider = new Region();
        otpDivider.setPrefHeight(1);
        otpDivider.setMaxWidth(Double.MAX_VALUE);
        otpDivider.setStyle("-fx-background-color: #E2E8F0;");

        // Assemble OTP Card
        result.getChildren().addAll(
                otpHeader,
                otpDivider,
                smsStatusBox,
                countdownLabel,
                createVerticalSpace(4),
                enterLabel,
                otpInputRow,
                otpError,
                createVerticalSpace(4),
                otpButtonRow
        );

        // Auto-focus first input box
        PauseTransition focusDelay = new PauseTransition(Duration.millis(300));
        focusDelay.setOnFinished(ev -> otpInputFields[0].requestFocus());
        focusDelay.play();
    }

    // =========================================================
    // FLASH INPUT BORDERS ON ERROR
    // =========================================================

    private void flashInputBorders(TextField[] fields) {
        for (TextField f : fields) {
            f.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                    "-fx-border-color: #DC2626;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 22px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-text-fill: #DC2626;"
            );
        }

        PauseTransition resetBorder = new PauseTransition(Duration.seconds(1.5));
        resetBorder.setOnFinished(ev -> {
            for (TextField f : fields) {
                f.setStyle(
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-text-fill: #0F172A;"
                );
            }
        });
        resetBorder.play();
    }

    // =========================================================
    // EMERGENCY OFFICER OVERRIDE
    // =========================================================

    private void executeEmergencyOverride(String electionId, Voter voter) {
        try {
            VoterVerificationService.VerificationResult tokenResult =
                    controller.issueTokenAfterVerification(voter, electionId, "Officer Emergency Override");

            if (tokenResult.success()) {
                cancelOtpTimer();
                syncVerificationToFirebase(electionId, voter, tokenResult.token(), "Officer Emergency Override");
                showOtpSuccess(voter.getFullName(), voter.getVoterId(), tokenResult.token());
            } else {
                showError(tokenResult.message());
            }
        } catch (Exception ex) {
            showError("Failed to issue authorization token: " + ex.getMessage());
        }
    }

    // =========================================================
    // OTP SUCCESS ANIMATION → THEN SHOW TOKEN
    // =========================================================

    private void showOtpSuccess(String voterName, String voterId, String token) {
        result.getChildren().clear();
        result.setStyle(
                "-fx-background-color: #ECFDF5;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #A7F3D0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        Label checkmark = new Label("✓");
        checkmark.setMinWidth(56);
        checkmark.setPrefWidth(56);
        checkmark.setMinHeight(56);
        checkmark.setPrefHeight(56);
        checkmark.setAlignment(Pos.CENTER);
        checkmark.setStyle(
                "-fx-background-color: #16A34A;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 900;" +
                "-fx-background-radius: 28;"
        );

        checkmark.setScaleX(0);
        checkmark.setScaleY(0);

        ScaleTransition popIn = new ScaleTransition(Duration.millis(400), checkmark);
        popIn.setFromX(0);
        popIn.setFromY(0);
        popIn.setToX(1);
        popIn.setToY(1);

        Label successText = new Label("OTP Verified Successfully!");
        successText.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #166534;"
        );
        successText.setOpacity(0);

        VBox successContent = new VBox(12);
        successContent.setAlignment(Pos.CENTER);
        successContent.setPadding(new Insets(24));
        successContent.getChildren().addAll(checkmark, successText);

        result.getChildren().add(successContent);
        popIn.play();

        FadeTransition textFade = new FadeTransition(Duration.millis(400), successText);
        textFade.setFromValue(0);
        textFade.setToValue(1);
        textFade.setDelay(Duration.millis(300));
        textFade.play();

        PauseTransition showTokenDelay = new PauseTransition(Duration.seconds(1.2));
        showTokenDelay.setOnFinished(ev -> showSuccess(voterName, voterId, token));
        showTokenDelay.play();
    }

    // =========================================================
    // SHAKE ANIMATION
    // =========================================================

    private void shakeNode(javafx.scene.Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), node);
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
    // SUCCESS RESULT (ISSUED TOKEN)
    // =========================================================

    private void showSuccess(String voterName, String voterId, String token) {
        result.getChildren().clear();
        result.setVisible(true);
        result.setManaged(true);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setPadding(new Insets(22));
        result.setStyle(
                "-fx-background-color: #F0FDF4;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #86EFAC;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;"
        );

        Label verifiedLabel = new Label("✓  Voter Verified & Authorized");
        verifiedLabel.setStyle(
                "-fx-text-fill: #15803D;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 800;"
        );

        Label voterLabel = new Label("Voter: " + voterName);
        voterLabel.setStyle(
                "-fx-text-fill: #166534;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 700;"
        );

        Label voterIdLabel = new Label("Voter ID: " + voterId);
        voterIdLabel.setStyle(
                "-fx-text-fill: #166534;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 700;"
        );

        Label tokenLabel = new Label("Voting Authorization Token:");
        tokenLabel.setStyle(
                "-fx-text-fill: #166534;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 800;"
        );

        Label tokenValue = new Label(token);
        tokenValue.setStyle(
                "-fx-text-fill: #0F172A;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 800;" +
                "-fx-background-color: #FFFFFF;" +
                "-fx-padding: 6 12 6 12;" +
                "-fx-border-color: #BBF7D0;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;"
        );

        Button copyTokenButton = new Button("⧉");
        copyTokenButton.setMinWidth(34);
        copyTokenButton.setPrefWidth(34);
        copyTokenButton.setMinHeight(34);
        copyTokenButton.setPrefHeight(34);
        copyTokenButton.setTooltip(new Tooltip("Copy Token"));
        copyTokenButton.setStyle(
                "-fx-background-color: #EEF4FF;" +
                "-fx-text-fill: #2563EB;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-border-color: #BFDBFE;" +
                "-fx-border-radius: 7;" +
                "-fx-cursor: hand;"
        );

        copyTokenButton.setOnAction(e -> {
            if (token == null || token.isBlank()) return;
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(token);
            clipboard.setContent(clipboardContent);

            copyTokenButton.setText("✓");
            copyTokenButton.setTooltip(new Tooltip("Copied!"));
            copyTokenButton.setStyle(
                    "-fx-background-color: #DCFCE7;" +
                    "-fx-text-fill: #16A34A;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 7;" +
                    "-fx-border-color: #86EFAC;" +
                    "-fx-border-radius: 7;"
            );

            PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
            pause.setOnFinished(event -> {
                copyTokenButton.setText("⧉");
                copyTokenButton.setTooltip(new Tooltip("Copy Token"));
                copyTokenButton.setStyle(
                        "-fx-background-color: #EEF4FF;" +
                        "-fx-text-fill: #2563EB;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7;" +
                        "-fx-border-color: #BFDBFE;" +
                        "-fx-border-radius: 7;" +
                        "-fx-cursor: hand;"
                );
            });
            pause.play();
        });

        HBox tokenBox = new HBox(8, tokenLabel, tokenValue, copyTokenButton);
        tokenBox.setAlignment(Pos.CENTER_LEFT);

        result.getChildren().addAll(
                verifiedLabel,
                voterLabel,
                voterIdLabel,
                createVerticalSpace(6),
                tokenBox
        );

        verify.setText("✓  Voter Verified");
        verify.setDisable(true);
    }

    // =========================================================
    // ERROR RESULT
    // =========================================================

    private void showError(String text) {
        result.getChildren().clear();

        Label errorLabel = new Label(text);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setStyle(
                "-fx-text-fill: #B91C1C;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 700;"
        );

        result.getChildren().add(errorLabel);
        result.setVisible(true);
        result.setManaged(true);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setFillWidth(true);
        result.setPadding(new Insets(16));
        result.setStyle(
                "-fx-background-color: #FEF2F2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #FECACA;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
        );
    }

    // =========================================================
    // CLEAR RESULT
    // =========================================================

    private void clearResult() {
        result.getChildren().clear();
        result.setVisible(false);
        result.setManaged(false);
        result.setPadding(Insets.EMPTY);
    }

    // =========================================================
    // VERTICAL SPACE HELPER
    // =========================================================

    private Region createVerticalSpace(double height) {
        Region space = new Region();
        space.setMinHeight(height);
        space.setPrefHeight(height);
        space.setMaxHeight(height);
        return space;
    }

    // =========================================================
    // FIREBASE CLOUD SYNC
    // =========================================================

    private void syncVerificationToFirebase(String electionId, Voter voter, String token, String authMethod) {
        new Thread(() -> {
            try {
                String voterId = voter.getVoterId();
                String encodedElection = URLEncoder.encode(electionId, StandardCharsets.UTF_8);
                String encodedVoter = URLEncoder.encode(voterId, StandardCharsets.UTF_8);
                String path = "/offline_verifications/" + encodedElection + "/" + encodedVoter + ".json";
                String authParam = SessionManager.idToken != null && !SessionManager.idToken.isBlank()
                        ? "?auth=" + URLEncoder.encode(SessionManager.idToken, StandardCharsets.UTF_8)
                        : "";
                String url = FirebaseConfig.DATABASE_URL + path + authParam;

                JsonObject record = new JsonObject();
                record.addProperty("voterId", voterId);
                record.addProperty("voterName", voter.getFullName());
                record.addProperty("phone", voter.getPhone());
                record.addProperty("electionId", electionId);
                record.addProperty("token", token);
                record.addProperty("authMethod", authMethod);
                record.addProperty("status", "VERIFIED");
                record.addProperty("verifiedAt", System.currentTimeMillis());
                record.addProperty("verifiedBy", SessionManager.officerEmail != null ? SessionManager.officerEmail : "Offline Polling Officer");

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(record.toString(), StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> resp = HTTP_CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    System.out.println("[OfflineVerification] ☁️ Synced verified voter [" + voterId + "] to Firebase RTDB.");
                } else {
                    System.err.println("[OfflineVerification] Firebase sync note: " + resp.statusCode() + " -> " + resp.body());
                }
            } catch (Exception e) {
                System.err.println("[OfflineVerification] Firebase sync error: " + e.getMessage());
            }
        }).start();
    }

    // =========================================================
    // GET SCENE
    // =========================================================

    public Scene getScene() {
        return scene;
    }
}