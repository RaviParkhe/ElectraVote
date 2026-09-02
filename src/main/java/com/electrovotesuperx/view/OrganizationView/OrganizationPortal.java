package com.electrovotesuperx.view.OrganizationView;

import com.electrovotesuperx.view.Page;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class OrganizationPortal implements Page {

    private final Stage stage;

    private Scene portalScene;

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public OrganizationPortal(Stage stage) {
        this.stage = stage;
    }

    // ========================================
    // GET SCENE
    // ========================================

    @Override
    public Scene getScene(Runnable homeCallback) {

        BorderPane root =
                new BorderPane();

        root.setStyle(
            "-fx-background-color: #F7F9FC;"
        );

        // ========================================
        // HEADER
        // ========================================

        HBox header =
                new HBox();

        header.setAlignment(
            Pos.CENTER_LEFT
        );

        header.setPadding(
            new Insets(18, 35, 18, 35)
        );

        header.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // ========================================
        // LOGO
        // ========================================

        Circle logo =
                new Circle(
                    22,
                    Color.web("#2563EB")
                );

        Label logoText =
                new Label("✓");

        logoText.setFont(
            Font.font("Arial", 20)
        );

        logoText.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        javafx.scene.layout.StackPane logoPane =
                new javafx.scene.layout.StackPane(
                    logo,
                    logoText
                );

        // ========================================
        // BRAND
        // ========================================

        Label brand =
                new Label("ElectraVote");

        brand.setFont(
            Font.font("Arial", 24)
        );

        brand.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        HBox brandBox =
                new HBox(
                    10,
                    logoPane,
                    brand
                );

        brandBox.setAlignment(
            Pos.CENTER_LEFT
        );

        // ========================================
        // HEADER SPACER
        // ========================================

        Region spacer =
                new Region();

        HBox.setHgrow(
            spacer,
            Priority.ALWAYS
        );

        // ========================================
        // BACK TO HOME BUTTON
        // ========================================

        Button back =
                new Button(
                    "←  Back to Home"
                );

        back.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #172554;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        back.setOnAction(e -> {

            if (homeCallback != null) {
                homeCallback.run();
            }

        });

        header.getChildren().addAll(
            brandBox,
            spacer,
            back
        );

        // ========================================
        // MAIN CONTENT
        // ========================================

        VBox content =
                new VBox(25);

        content.setAlignment(
            Pos.TOP_CENTER
        );

        content.setPadding(
            new Insets(45, 50, 40, 50)
        );

        // ========================================
        // TITLE
        // ========================================

        Label title =
                new Label(
                    "Organization Portal"
                );

        title.setFont(
            Font.font("Arial", 31)
        );

        title.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        Label subtitle =
                new Label(
                    "Create your organization or sign in to access " +
                    "your organization services."
                );

        subtitle.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #64748B;"
        );

        content.getChildren().addAll(
            title,
            subtitle
        );

        // ========================================
        // TOP TWO CARDS
        // ========================================

        HBox topCards =
                new HBox(25);

        topCards.setAlignment(
            Pos.CENTER
        );

        // ========================================
        // REGISTER ORGANIZATION
        // ========================================

        VBox registerCard =
                createPortalCard(
                    "🏛",
                    "Register as Organization",
                    "Create a new organization and " +
                    "set up your administrator account.",
                    "#2563EB"
                );

        Button registerButton =
                createPortalButton(
                    "Register Organization  →",
                    "#2563EB"
                );

        registerButton.setOnAction(e -> {

            RegisterOrganization registerPage =
                    new RegisterOrganization();

            stage.setScene(
                registerPage.getScene(() -> {

                    // Return to Organization Portal
                    stage.setScene(portalScene);
                    stage.setMaximized(true);

                })
            );
            stage.setMaximized(true);

        });



        registerCard.getChildren().add(
            registerButton
        );

        // ========================================
        // SIGN IN ORGANIZATION
        // ========================================

        VBox organizationSignIn =
                createPortalCard(
                    "🔐",
                    "Sign In as Organization",
                    "Already have an organization? " +
                    "Sign in to access your dashboard.",
                    "#059669"
                );

        Button organizationSignInButton =
                createPortalButton(
                    "Sign In  →",
                    "#059669"
                );

        organizationSignInButton.setOnAction(e -> {

            SignInOrganization signInPage =
                    new SignInOrganization(stage);

            stage.setScene(
                signInPage.getScene(() -> {

                    // Return to Organization Portal
                    stage.setScene(portalScene);
                    stage.setMaximized(true);

                })
            );
            stage.setMaximized(true);

        });

        // ========================================
        // ORGANIZATION SIGN-IN ACTION
        // ========================================
        //
        // Add your OrganizationLogin page here
        // when it is created.
        //

        // organizationSignInButton.setOnAction(e -> {

        //     System.out.println(
        //         "Organization Sign In clicked."
        //     );

        // });

        organizationSignIn.getChildren().add(
            organizationSignInButton
        );

        // ========================================
        // ADD TOP CARDS
        // ========================================

        topCards.getChildren().addAll(
            registerCard,
            organizationSignIn
        );

        content.getChildren().add(
            topCards
        );

        // ========================================
        // VOTER CARD
        // ========================================

        VBox voterCard =
                new VBox(15);

        voterCard.setAlignment(
            Pos.CENTER
        );

        voterCard.setPrefWidth(700);
        voterCard.setPrefHeight(150);

        voterCard.setPadding(
            new Insets(20)
        );

        voterCard.setStyle(
            "-fx-background-color: #F5F0FF;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #DDD0FF;" +
            "-fx-border-radius: 16;"
        );

        // ========================================
        // VOTER ICON
        // ========================================

        Circle voterCircle =
                new Circle(
                    30,
                    Color.web("#7C3AED")
                );

        Label voterIcon =
                new Label("👤");

        voterIcon.setFont(
            Font.font("Arial", 19)
        );

        javafx.scene.layout.StackPane voterIconPane =
                new javafx.scene.layout.StackPane(
                    voterCircle,
                    voterIcon
                );

        // ========================================
        // VOTER TITLE
        // ========================================

        Label voterTitle =
                new Label(
                    "Sign In as Voter"
                );

        voterTitle.setFont(
            Font.font("Arial", 20)
        );

        voterTitle.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #4C1D95;"
        );

        // ========================================
        // VOTER DESCRIPTION
        // ========================================

        Label voterDescription =
                new Label(
                    "Join your organization using the unique " +
                    "organization Join Code and cast your vote."
                );

        voterDescription.setWrapText(true);

        voterDescription.setTextAlignment(
            TextAlignment.CENTER
        );

        voterDescription.setStyle(
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 13px;"
        );

        // ========================================
        // VOTER BUTTON
        // ========================================

        Button voterButton =
                createPortalButton(
                    "Sign In as Voter  →",
                    "#7C3AED"
                );

        // ========================================
        // VOTER SIGN-IN ACTION
        // ========================================
        //
        // Add your VoterLogin page here
        // when it is created.
        //

        voterButton.setOnAction(e -> {

            SignInVoter signInVoterPage =
                    new SignInVoter();

            stage.setScene(
                signInVoterPage.getScene(() -> {

                    // Return to Organization Portal
                    stage.setScene(portalScene);
                    stage.setMaximized(true);
                           })
            );
            stage.setMaximized(true);

        });
        voterCard.getChildren().addAll(
            voterIconPane,
            voterTitle,
            voterDescription,
            voterButton
        );

        content.getChildren().add(
            voterCard
        );

        // ========================================
        // INFORMATION
        // ========================================

        Label information =
                new Label(
                    "Each organization operates as an independent tenant. "
                    +
                    "Your role and access are determined by your "
                    +
                    "organization membership."
                );

        information.setWrapText(true);

        information.setMaxWidth(750);

        information.setAlignment(
            Pos.CENTER
        );

        information.setTextAlignment(
            TextAlignment.CENTER
        );

        information.setStyle(
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 12px;"
        );

        content.getChildren().add(
            information
        );

        // ========================================
        // ROOT
        // ========================================

        root.setTop(header);
        root.setCenter(content);

        // ========================================
        // SCENE
        // ========================================

        portalScene =
                new Scene(
                    root
                );

        return portalScene;
    }

    // ========================================
    // CREATE PORTAL CARD
    // ========================================

    private VBox createPortalCard(
            String icon,
            String titleText,
            String descriptionText,
            String color) {

        VBox card =
                new VBox(14);

        card.setAlignment(
            Pos.CENTER
        );

        card.setPrefWidth(410);
        card.setMinWidth(410);
        card.setPrefHeight(255);

        card.setPadding(
            new Insets(25)
        );

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #DCE5F2;" +
            "-fx-border-radius: 16;"
        );

        // ========================================
        // ICON
        // ========================================

        Circle circle =
                new Circle(
                    35,
                    Color.web(color)
                );

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
            Font.font("Arial", 23)
        );

        javafx.scene.layout.StackPane iconPane =
                new javafx.scene.layout.StackPane(
                    circle,
                    iconLabel
                );

        // ========================================
        // TITLE
        // ========================================

        Label title =
                new Label(titleText);

        title.setFont(
            Font.font("Arial", 19)
        );

        title.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        // ========================================
        // DESCRIPTION
        // ========================================

        Label description =
                new Label(descriptionText);

        description.setWrapText(true);

        description.setTextAlignment(
            TextAlignment.CENTER
        );

        description.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #64748B;"
        );

        // ========================================
        // ADD CARD CONTENT
        // ========================================

        card.getChildren().addAll(
            iconPane,
            title,
            description
        );

        return card;
    }

    // ========================================
    // CREATE BUTTON
    // ========================================

    private Button createPortalButton(
            String text,
            String color) {

        Button button =
                new Button(text);

        button.setPrefWidth(300);
        button.setPrefHeight(44);

        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 9;"
        );

        return button;
    }
}
