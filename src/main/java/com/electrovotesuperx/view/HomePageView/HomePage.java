// package com.electrovotesuperx.view.HomePageView;

// import com.electrovotesuperx.view.Page;
// import com.electrovotesuperx.view.OrganizationView.OrganizationPortal;
// import com.electrovotesuperx.view.OfflineView.OfflineHomePage;
// import com.electrovotesuperx.utils.Navigation;
// import com.electrovotesuperx.view.QuickPollView.QuickPoll;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle;
// import javafx.scene.text.Font;
// import javafx.stage.Stage;

// public class HomePage implements Page {

//     private final Stage stage;
//     private Scene homeScene;

//     // ========================================
//     // COLORS
//     // ========================================

//     private static final String NAVY = "#0F1B33";
//     private static final String BLUE = "#2563EB";
//     private static final String TEXT = "#172554";
//     private static final String SUBTEXT = "#64748B";

//     // ========================================
//     // CONSTRUCTOR
//     // ========================================

//     public HomePage(Stage stage) {
//         this.stage = stage;
//     }

//     // ========================================
//     // GET SCENE
//     // ========================================

//     @Override
//     public Scene getScene(Runnable loginCallback) {

//         BorderPane root = new BorderPane();

//         root.setStyle(
//             "-fx-background-color: #F7F9FC;"
//         );

//         // ========================================
//         // HEADER
//         // ========================================

//         HBox header = new HBox();

//         header.setAlignment(
//             Pos.CENTER_LEFT
//         );

//         header.setPadding(
//             new Insets(18, 35, 18, 35)
//         );

//         header.setStyle(
//             "-fx-background-color: white;" +
//             "-fx-border-color: #E2E8F0;" +
//             "-fx-border-width: 0 0 1 0;"
//         );

//         // Logo circle

//         Circle logo =
//                 new Circle(
//                     22,
//                     Color.web("#2563EB")
//                 );

//         Label logoSymbol =
//                 new Label("✓");

//         logoSymbol.setFont(
//             Font.font("Arial", 20)
//         );

//         logoSymbol.setStyle(
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: white;"
//         );

//         // Put symbol over circle

//         javafx.scene.layout.StackPane logoPane =
//                 new javafx.scene.layout.StackPane(
//                     logo,
//                     logoSymbol
//                 );

//         Label brand =
//                 new Label("ElectraVote");

//         brand.setFont(
//             Font.font("Arial", 24)
//         );

//         brand.setStyle(
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: #172554;"
//         );

//         HBox brandBox =
//                 new HBox(
//                     10,
//                     logoPane,
//                     brand
//                 );

//         brandBox.setAlignment(
//             Pos.CENTER_LEFT
//         );

//         // Right header text

//         Label security =
//                 new Label(
//                     "Secure  •  Transparent  •  Democratic"
//                 );

//         security.setStyle(
//             "-fx-text-fill: #475569;" +
//             "-fx-font-size: 13px;"
//         );

//         javafx.scene.layout.Region headerSpacer =
//                 new javafx.scene.layout.Region();

//         HBox.setHgrow(
//             headerSpacer,
//             javafx.scene.layout.Priority.ALWAYS
//         );

//         header.getChildren().addAll(
//             brandBox,
//             headerSpacer,
//             security
//         );

//         // ========================================
//         // MAIN CONTENT
//         // ========================================

//         VBox content =
//                 new VBox(20);

//         content.setAlignment(
//             Pos.TOP_CENTER
//         );

//         content.setPadding(
//             new Insets(45, 40, 35, 40)
//         );

//         // ========================================
//         // TITLE
//         // ========================================

//         Label title =
//                 new Label(
//                     "Welcome to ElectraVote"
//                 );

//         title.setFont(
//             Font.font("Arial", 32)
//         );

//         title.setStyle(
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: #172554;"
//         );

//         Label subtitle =
//                 new Label(
//                     "Modern. Secure. Multi-Tenant Voting Platform."
//                 );

//         subtitle.setFont(
//             Font.font("Arial", 17)
//         );

//         subtitle.setStyle(
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: #2563EB;"
//         );

//         Label description =
//                 new Label(
//                     "Empowering organizations with secure and transparent " +
//                     "voting solutions."
//                 );

//         description.setStyle(
//             "-fx-font-size: 14px;" +
//             "-fx-text-fill: #64748B;"
//         );

//         content.getChildren().addAll(
//             title,
//             subtitle,
//             description
//         );

//         // ========================================
//         // THREE MAIN BUTTONS
//         // ========================================

//         HBox cards =
//                 new HBox(22);

//         cards.setAlignment(
//             Pos.CENTER
//         );

//         // ----------------------------------------
//         // ORGANIZATION PORTAL
//         // ----------------------------------------

//         VBox organizationCard =
//                 createCard(
//                     "🏛",
//                     "Organization Portal",
//                     "Register your organization or sign in " +
//                     "to manage elections, members and more.",
//                     BLUE
//                 );

//         Button organizationButton =
//                 createCardButton(
//                     "Get Started  →",
//                     BLUE
//                 );

//         organizationButton.setOnAction(e -> {

//             OrganizationPortal portal =
//                     new OrganizationPortal(stage);

//             stage.setScene(
//                 portal.getScene(() -> {

//                     stage.setScene(homeScene);
//                     stage.setMaximized(true);

//                 })
//             );
//             stage.setMaximized(true);

//         });

//         organizationCard.getChildren().add(
//             organizationButton
//         );

//         // ----------------------------------------
//         // OFFLINE VOTING
//         // ----------------------------------------

//         VBox offlineCard =
//                 createCard(
//                     "🗳",
//                     "Offline Voting",
//                     "Participate in elections conducted " +
//                     "in offline mode using secure verification.",
//                     "#059669"
//                 );

//         Button offlineButton =
//                 createCardButton(
//                     "Get Started  →",
//                     "#059669"
//                 );
//         offlineButton.setOnAction(e -> {
//             Navigation.init(stage);
//             OfflineHomePage offline = new OfflineHomePage();
//             stage.setScene(offline.getScene());
//             stage.setMaximized(true);
//         });

//         offlineCard.getChildren().add(
//             offlineButton
//         );

//         // ----------------------------------------
//         // QUICK POLL
//         // ----------------------------------------

//         VBox quickPollCard =
//                 createCard(
//                     "📊",
//                     "Quick Poll",
//                     "Create quick polls and get instant " +
//                     "feedback from your audience.",
//                     "#7C3AED"
//                 );

//         Button quickPollButton =
//                 createCardButton(
//                     "Get Started  →",
//                     "#7C3AED"
//                 );
//         quickPollButton.setOnAction(e -> {
//             QuickPoll quickPoll = new QuickPoll();
//             stage.setScene(
//                 quickPoll.getScene(() -> {
//                     stage.setScene(homeScene);
//                     stage.setMaximized(true);
//                 })
//             );
//             stage.setMaximized(true);
//         });

//         quickPollCard.getChildren().add(
//             quickPollButton
//         );





//         Button backToLoginButton =
//                 new Button("Back to Login");
//         backToLoginButton.setOnAction(e -> {
//             loginCallback.run();
//         });
    

//         cards.getChildren().addAll(
//             organizationCard,
//             offlineCard,
//             quickPollCard
//         );

//         content.getChildren().add(
//             cards
//         );

//         // ========================================
//         // FOOTER
//         // ========================================

//         Label footer =
//                 new Label(
//                     "© 2026 ElectraVote. All rights reserved."
//                 );

//         footer.setStyle(
//             "-fx-text-fill: #64748B;" +
//             "-fx-font-size: 12px;"
//         );

//         BorderPane.setAlignment(
//             footer,
//             Pos.CENTER
//         );

//         BorderPane.setMargin(
//             footer,
//             new Insets(12)
//         );

//         // ========================================
//         // ROOT
//         // ========================================

//         root.setTop(header);
//         root.setCenter(content);
//         root.setBottom(footer);

//         // ========================================
//         // SCENE
//         // ========================================

//         homeScene = new Scene(root);

//         return homeScene;
//     }

//     // ========================================
//     // CREATE CARD
//     // ========================================

//     private VBox createCard(
//             String icon,
//             String titleText,
//             String descriptionText,
//             String accentColor) {

//         VBox card =
//                 new VBox(14);

//         card.setAlignment(
//             Pos.CENTER
//         );

//         card.setPrefWidth(330);
//         card.setMinWidth(330);
//         card.setPrefHeight(280);

//         card.setPadding(
//             new Insets(25)
//         );

//         card.setStyle(
//             "-fx-background-color: white;" +
//             "-fx-background-radius: 16;" +
//             "-fx-border-color: #DCE5F2;" +
//             "-fx-border-radius: 16;"
//         );

//         // Icon

//         Circle iconCircle =
//                 new Circle(
//                     38,
//                     Color.web(
//                         accentColor
//                     )
//                 );

//         Label iconLabel =
//                 new Label(icon);

//         iconLabel.setFont(
//             Font.font("Arial", 24)
//         );

//         javafx.scene.layout.StackPane iconPane =
//                 new javafx.scene.layout.StackPane(
//                     iconCircle,
//                     iconLabel
//                 );

//         // Title

//         Label title =
//                 new Label(titleText);

//         title.setFont(
//             Font.font("Arial", 19)
//         );

//         title.setStyle(
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: " + TEXT + ";"
//         );

//         // Description

//         Label description =
//                 new Label(descriptionText);

//         description.setWrapText(true);

//         description.setTextAlignment(
//             javafx.scene.text.TextAlignment.CENTER
//         );

//         description.setStyle(
//             "-fx-font-size: 13px;" +
//             "-fx-text-fill: " + SUBTEXT + ";"
//         );

//         card.getChildren().addAll(
//             iconPane,
//             title,
//             description
//         );

//         return card;
//     }

//     // ========================================
//     // CREATE CARD BUTTON
//     // ========================================

//     private Button createCardButton(
//             String text,
//             String color) {

//         Button button =
//                 new Button(text);

//         button.setPrefWidth(260);
//         button.setPrefHeight(45);

//         button.setStyle(
//             "-fx-background-color: " + color + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-font-size: 14px;" +
//             "-fx-font-weight: bold;" +
//             "-fx-background-radius: 9;"
//         );

//         return button;
//     }
// }


package com.electrovotesuperx.view.HomePageView;

import com.electrovotesuperx.view.Page;
import com.electrovotesuperx.view.LoginPageView.Login;
import com.electrovotesuperx.view.OrganizationView.OrganizationPortal;
import com.electrovotesuperx.view.OfflineView.OfflineHomePage;
import com.electrovotesuperx.utils.Navigation;
import com.electrovotesuperx.view.QuickPollView.QuickPoll;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HomePage implements Page {

    private final Stage stage;
    private Scene homeScene;

    // ========================================
    // COLORS
    // ========================================

    private static final String NAVY = "#0F1B33";
    private static final String BLUE = "#2563EB";
    private static final String TEXT = "#172554";
    private static final String SUBTEXT = "#64748B";

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public HomePage(Stage stage) {
        this.stage = stage;
    }

    // ========================================
    // GET SCENE
    // ========================================

    @Override
    public Scene getScene(Runnable loginCallback) {

        BorderPane root = new BorderPane();

        root.setStyle(
            "-fx-background-color: #F7F9FC;"
        );

        // ========================================
        // HEADER
        // ========================================

        HBox header = new HBox();

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

        // Logo circle

        Circle logo =
                new Circle(
                    22,
                    Color.web("#2563EB")
                );

        Label logoSymbol =
                new Label("✓");

        logoSymbol.setFont(
            Font.font("Arial", 20)
        );

        logoSymbol.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        // Put symbol over circle

        javafx.scene.layout.StackPane logoPane =
                new javafx.scene.layout.StackPane(
                    logo,
                    logoSymbol
                );

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

        // Right header text

        Label security =
                new Label(
                    "Secure  •  Transparent  •  Democratic"
                );

        security.setStyle(
            "-fx-text-fill: #475569;" +
            "-fx-font-size: 13px;"
        );

        javafx.scene.layout.Region headerSpacer =
                new javafx.scene.layout.Region();

        HBox.setHgrow(
            headerSpacer,
            javafx.scene.layout.Priority.ALWAYS
        );

        header.getChildren().addAll(
            brandBox,
            headerSpacer,
            security
        );

        // ========================================
        // MAIN CONTENT
        // ========================================

        VBox content =
                new VBox(20);

        content.setAlignment(
            Pos.TOP_CENTER
        );

        content.setPadding(
            new Insets(45, 40, 35, 40)
        );

        // ========================================
        // TITLE
        // ========================================

        Label title =
                new Label(
                    "Welcome to ElectraVote"
                );

        title.setFont(
            Font.font("Arial", 32)
        );

        title.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        Label subtitle =
                new Label(
                    "Modern. Secure. Multi-Tenant Voting Platform."
                );

        subtitle.setFont(
            Font.font("Arial", 17)
        );

        subtitle.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2563EB;"
        );

        Label description =
                new Label(
                    "Empowering organizations with secure and transparent " +
                    "voting solutions."
                );

        description.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #64748B;"
        );

        content.getChildren().addAll(
            title,
            subtitle,
            description
        );

        // ========================================
        // THREE MAIN BUTTONS
        // ========================================

        HBox cards =
                new HBox(22);

        cards.setAlignment(
            Pos.CENTER
        );

        // ----------------------------------------
        // ORGANIZATION PORTAL
        // ----------------------------------------

        VBox organizationCard =
                createCard(
                    "🏛",
                    "Organization Portal",
                    "Register your organization or sign in " +
                    "to manage elections, members and more.",
                    BLUE
                );

        Button organizationButton =
                createCardButton(
                    "Get Started  →",
                    BLUE
                );

        organizationButton.setOnAction(e -> {

            OrganizationPortal portal =
                    new OrganizationPortal(stage);

            stage.setScene(
                portal.getScene(() -> {

                    stage.setScene(homeScene);
                    stage.setMaximized(true);

                })
            );

            stage.setMaximized(true);

        });

        organizationCard.getChildren().add(
            organizationButton
        );

        // ----------------------------------------
        // OFFLINE VOTING
        // ----------------------------------------

        VBox offlineCard =
                createCard(
                    "🗳",
                    "Offline Voting",
                    "Participate in elections conducted " +
                    "in offline mode using secure verification.",
                    "#059669"
                );

        Button offlineButton =
                createCardButton(
                    "Get Started  →",
                    "#059669"
                );

        offlineButton.setOnAction(e -> {

            Navigation.init(stage);

            OfflineHomePage offline =
                    new OfflineHomePage();

            stage.setScene(
                offline.getScene()
            );

            stage.setMaximized(true);

        });

        offlineCard.getChildren().add(
            offlineButton
        );

        // ----------------------------------------
        // QUICK POLL
        // ----------------------------------------

        VBox quickPollCard =
                createCard(
                    "📊",
                    "Quick Poll",
                    "Create quick polls and get instant " +
                    "feedback from your audience.",
                    "#7C3AED"
                );
                
        Button quickPollButton =
                createCardButton(
                    "Get Started  →",
                    "#7C3AED"
                );

        quickPollButton.setOnAction(e -> {

            QuickPoll quickPoll =
                    new QuickPoll();

            stage.setScene(
                quickPoll.getScene(() -> {

                    stage.setScene(homeScene);
                    stage.setMaximized(true);

                })
            );

            stage.setMaximized(true);

        });

        quickPollCard.getChildren().add(
            quickPollButton
        );

        // ========================================
        // BACK TO LOGIN BUTTON
        // ========================================

        Button backToLoginButton =
                new Button("Back to Login");

        backToLoginButton.setPrefWidth(180);
        backToLoginButton.setPrefHeight(40);

        backToLoginButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #2563EB;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: #2563EB;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        backToLoginButton.setOnAction(e -> {

        Login.loginStage = stage;  
        Login login = new Login();

        stage.setScene(login.getScene(() -> {}));
        stage.setMaximized(true);
        });

        // ========================================
        // ADD CARDS
        // ========================================

        cards.getChildren().addAll(
            organizationCard,
            offlineCard,
            quickPollCard
        );

        content.getChildren().add(
            cards
        );

        // ========================================
        // ADD BACK TO LOGIN BUTTON
        // ========================================

        content.getChildren().add(
            backToLoginButton
        );

        // ========================================
        // FOOTER
        // ========================================

        Label footer =
                new Label(
                    "© 2026 ElectraVote. All rights reserved."
                );

        footer.setStyle(
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 12px;"
        );

        BorderPane.setAlignment(
            footer,
            Pos.CENTER
        );

        BorderPane.setMargin(
            footer,
            new Insets(12)
        );

        // ========================================
        // ROOT
        // ========================================

        root.setTop(header);
        root.setCenter(content);
        root.setBottom(footer);

        // ========================================
        // SCENE
        // ========================================

        homeScene =
                new Scene(root);

        return homeScene;
    }

    // ========================================
    // CREATE CARD
    // ========================================

    private VBox createCard(
            String icon,
            String titleText,
            String descriptionText,
            String accentColor) {

        VBox card =
                new VBox(14);

        card.setAlignment(
            Pos.CENTER
        );

        card.setPrefWidth(330);
        card.setMinWidth(330);
        card.setPrefHeight(280);

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

        Circle iconCircle =
                new Circle(
                    38,
                    Color.web(
                        accentColor
                    )
                );

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
            Font.font("Arial", 24)
        );

        javafx.scene.layout.StackPane iconPane =
                new javafx.scene.layout.StackPane(
                    iconCircle,
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
            "-fx-text-fill: " + TEXT + ";"
        );

        // ========================================
        // DESCRIPTION
        // ========================================

        Label description =
                new Label(descriptionText);

        description.setWrapText(
            true
        );

        description.setTextAlignment(
            javafx.scene.text.TextAlignment.CENTER
        );

        description.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + SUBTEXT + ";"
        );

        card.getChildren().addAll(
            iconPane,
            title,
            description
        );

        return card;
    }

    // ========================================
    // CREATE CARD BUTTON
    // ========================================

    private Button createCardButton(
            String text,
            String color) {

        Button button =
                new Button(text);

        button.setPrefWidth(
            260
        );

        button.setPrefHeight(
            45
        );

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