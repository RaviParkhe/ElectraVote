package com.electrovotesuperx.view.CommonView;

import com.electrovotesuperx.utils.Navigation;
import com.electrovotesuperx.view.OfflineView.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.nio.file.Files;
import java.nio.file.Path;

public class Sidebar extends VBox {

        // =========================================================
        // SIDEBAR SCROLL POSITION
        // =========================================================

        private static double sidebarScroll = 0.0;

        private final ScrollPane scrollPane = new ScrollPane();
        private final VBox menu = new VBox(6);

        // =========================================================
        // NORMAL BUTTON STYLE
        // =========================================================

        private static final String NORMAL = "-fx-background-color: transparent;" +
                        "-fx-text-fill:#CFD4DD;" +
                        "-fx-font-size:14;" +
                        "-fx-font-weight:600;" +
                        "-fx-alignment:CENTER_LEFT;" +
                        "-fx-padding:12 14 12 18;" +
                        "-fx-background-radius:8;";

        // =========================================================
        // ACTIVE BUTTON STYLE
        // =========================================================

        private static final String ACTIVE = "-fx-background-color:#E6F8FC;" +
                        "-fx-text-fill:#0D1538;" +
                        "-fx-font-size:14;" +
                        "-fx-font-weight:700;" +
                        "-fx-alignment:CENTER_LEFT;" +
                        "-fx-padding:12 14 12 18;" +
                        "-fx-background-radius:8;";

        // =========================================================
        // CONSTRUCTOR
        // =========================================================

        public Sidebar(String activePage) {

                // =====================================================
                // SIDEBAR SIZE
                // =====================================================

                setPrefWidth(250);
                setMinWidth(250);
                setMaxWidth(250);

                setStyle(
                                "-fx-background-color:#0D1538;");

                // =====================================================
                // LOGO SECTION
                // =====================================================

                VBox logoBox = new VBox();
                logoBox.setAlignment(Pos.CENTER);
                logoBox.setPadding(
                                new Insets(30, 10, 0, 10));

                // =====================================================
                // LOAD LOGO
                // =====================================================

                Image logoImage = null;

                try {
                        var stream = Sidebar.class.getResourceAsStream("/assests/images/final logo chatgpt.png");
                        if (stream == null) {
                                stream = Sidebar.class.getResourceAsStream("/assets/images/final logo chatgpt.png");
                        }
                        if (stream != null) {
                                logoImage = new Image(stream);
                        }
                } catch (Exception ignored) {
                }

                if (logoImage == null) {
                        Path logoPath = Path.of(
                                        "src",
                                        "main",
                                        "resources",
                                        "assests",
                                        "images",
                                        "final logo chatgpt.png");

                        if (Files.exists(logoPath)) {
                                try {
                                        logoImage = new Image(logoPath.toUri().toString());
                                } catch (Exception ignored) {
                                }
                        }
                }

                if (logoImage != null && !logoImage.isError()) {
                        ImageView logo = new ImageView(logoImage);

                        // Logo size
                        logo.setFitWidth(230);
                        logo.setFitHeight(130);

                        // Keep original image proportions
                        logo.setPreserveRatio(true);

                        // Smooth image
                        logo.setSmooth(true);

                        // Center logo
                        logoBox.getChildren().add(logo);
                }

                // =====================================================
                // TOP SECTION
                // =====================================================

                VBox top = new VBox();

                top.setAlignment(Pos.CENTER);

                top.setStyle(
                                "-fx-border-color:transparent transparent #273252 transparent;" +
                                                "-fx-border-width:0 0 2 0;");

                top.setPadding(
                                new Insets(0, 0, 20, 0));

                // Add logo
                top.getChildren().add(logoBox);

                // =====================================================
                // MENU BUTTONS
                // =====================================================

                Button home = createButton(
                                "⌂   Home");

                Button election = createButton(
                                "◫   Elections");

                Button members = createButton(
                                "♙   Members");

                Button verify = createButton(
                                "✓   Voter Verification");

                Button status = createButton(
                                "▣   Token / Status");

                // =====================================================
                // ACTIVE PAGE
                // =====================================================

                if ("HomePage".equals(activePage)) {
                        home.setStyle(ACTIVE);
                }

                if ("ElectionPage".equals(activePage)) {
                        election.setStyle(ACTIVE);
                }

                if ("MemberPage".equals(activePage)) {
                        members.setStyle(ACTIVE);
                }

                if ("VerificationPage".equals(activePage)) {
                        verify.setStyle(ACTIVE);
                }

                if ("StatusPage".equals(activePage)) {
                        status.setStyle(ACTIVE);
                }

                // =====================================================
                // NAVIGATION
                // =====================================================

                home.setOnAction(e -> navigate(
                                home,
                                new OfflineHomePage().getScene()));

                election.setOnAction(e -> navigate(
                                election,
                                new OfflineElectionPage().getScene()));

                members.setOnAction(e -> navigate(
                                members,
                                new OfflineMemberPage().getScene()));

                verify.setOnAction(e -> navigate(
                                verify,
                                new OfflineVerification().getScene()));

                status.setOnAction(e -> navigate(
                                status,
                                new OfflineStatusPage().getScene()));

                Button exitToHome = createButton(
                                "🚪   Exit to Main");
                exitToHome.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill:#F87171;" +
                                                "-fx-font-size:14;" +
                                                "-fx-font-weight:600;" +
                                                "-fx-alignment:CENTER_LEFT;" +
                                                "-fx-padding:12 14 12 18;" +
                                                "-fx-background-radius:8;");

                exitToHome.setOnAction(e -> {
                        com.elctrovotesuperx.config.SessionManager.clearSession();
                        Stage currentStage = Navigation.getStage();
                        com.elctrovotesuperx.view.HomePageView.HomePage homePage = new com.elctrovotesuperx.view.HomePageView.HomePage(currentStage);
                        Navigation.goTo(homePage.getScene(() -> {
                                if (currentStage != null) currentStage.close();
                        }));
                });

                // =====================================================
                // MENU
                // =====================================================

                menu.setPadding(
                                new Insets(36, 10, 20, 10));

                menu.getChildren().addAll(
                                home,
                                election,
                                members,
                                verify,
                                status,
                                exitToHome);

                menu.setSpacing(11);

                // =====================================================
                // SCROLLPANE
                // =====================================================

                scrollPane.setContent(menu);

                scrollPane.setFitToWidth(true);

                scrollPane.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scrollPane.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scrollPane.setStyle(
                                "-fx-background:transparent;" +
                                                "-fx-background-color:transparent;" +
                                                "-fx-border-color:transparent;");

                VBox.setVgrow(
                                scrollPane,
                                Priority.ALWAYS);

                // =====================================================
                // ADD TO SIDEBAR
                // =====================================================

                getChildren().addAll(
                                top,
                                scrollPane);

                // =====================================================
                // RESTORE SCROLL POSITION
                // =====================================================

                Platform.runLater(() -> {
                        scrollPane.setVvalue(sidebarScroll);
                });
        }

        // =========================================================
        // STATIC METHOD
        // =========================================================

        public static Sidebar getSidebar(String activePage) {
                return new Sidebar(activePage);
        }

        // =========================================================
        // CREATE BUTTON
        // =========================================================

        private Button createButton(String text) {

                Button button = new Button(text);

                button.setMaxWidth(
                                Double.MAX_VALUE);

                button.setPrefHeight(46);

                button.setStyle(NORMAL);

                // =====================================================
                // HOVER
                // =====================================================

                button.setOnMouseEntered(e -> {

                        button.setScaleX(1.03);
                        button.setScaleY(1.03);

                });

                button.setOnMouseExited(e -> {

                        button.setScaleX(1.0);
                        button.setScaleY(1.0);

                });

                return button;
        }

        // =========================================================
        // NAVIGATION
        // =========================================================

        private void navigate(
                        Button active,
                        Scene scene) {

                // =====================================================
                // SAVE CURRENT SIDEBAR SCROLL POSITION
                // =====================================================

                sidebarScroll = scrollPane.getVvalue();

                // =====================================================
                // RESET ALL BUTTONS
                // =====================================================

                for (Node node : menu.getChildren()) {

                        if (node instanceof Button button) {

                                button.setStyle(NORMAL);
                        }
                }

                // =====================================================
                // MAKE CLICKED BUTTON ACTIVE
                // =====================================================

                active.setStyle(ACTIVE);

                // =====================================================
                // OPEN NEW PAGE
                // =====================================================

                Navigation.run(() -> {
                        Navigation.goTo(scene);
                });
        }
}