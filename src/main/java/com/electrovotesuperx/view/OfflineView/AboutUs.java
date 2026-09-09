
package com.electrovotesuperx.view.OfflineView;

import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/** Shared project story and acknowledgements for ElectraVote SuperX. */
public class AboutUs {

    private final Scene scene;

    public AboutUs() {
        ScrollPane scroll = createView();
        scene = new Scene(scroll, 950, 680);
    }

    public VBox createContent() {

        VBox page = new VBox(24);
        page.setPadding(new Insets(36));
        page.setMinWidth(0);
        page.setStyle(
                "-fx-background-color: #0B1733; "
                + "-fx-background-radius: 20;"
        );

        VBox intro = new VBox(
                8,
                text(
                        "THE PEOPLE BEHIND ELECTROVOTE",
                        11,
                        true,
                        "#A99FFF"
                ),
                text(
                        "About Us",
                        34,
                        true,
                        "#F8FAFF"
                ),
                text(
                        "Securing democratic voting with privacy and integrity.",
                        16,
                        false,
                        "#B8C3DE"
                )
        );

        // ---------------------------------------------------------
        // Center Shashi Sir Photo & Gratitude Card
        // ---------------------------------------------------------

        StackPane photo = createShashiPhotoPane();

        VBox thanks = card();
        thanks.setAlignment(Pos.TOP_CENTER);

        Label tribute = text(
                "With heartfelt gratitude",
                12,
                true,
                "#C4B5FD"
        );

        Label shashi = text(
                "Thank you, Shashi Sir",
                26,
                true,
                "#F8FAFF"
        );

        Label core2web = text(
                "Core2Web",
                18,
                true,
                "#A99FFF"
        );

        Label message = text(
                "Our sincere thanks to Shashi Sir and Core2Web for the guidance, "
                + "encouragement, and learning environment that helped us bring "
                + "ElectraVote SuperX to life.",
                15,
                false,
                "#CBD5E1"
        );

        message.setMaxWidth(700);
        message.setAlignment(Pos.CENTER);
        message.setTextAlignment(
                javafx.scene.text.TextAlignment.CENTER
        );

        // Center the photo inside the gratitude card
        photo.setAlignment(Pos.CENTER);
        photo.setMaxWidth(Double.MAX_VALUE);

        thanks.getChildren().addAll(
                tribute,
                photo,
                shashi,
                core2web,
                message
        );

        // ---------------------------------------------------------
        // Our Project
        // ---------------------------------------------------------

        VBox project = section(
                "Our Project",
                "ElectraVote SuperX is a next-generation electronic voting system "
                + "that combines zero-knowledge ballot encryption, live voting telemetry, "
                + "and multi-organization election oversight into a unified platform.",
                "Built with JavaFX, Firebase, and SQLite local engine, the system provides "
                + "dedicated portals for administrators, voters, and polling officers for "
                + "both online and offline election governance."
        );

        // ---------------------------------------------------------
// Our Team
// ---------------------------------------------------------

VBox ourteam = section(
        "Our Team",
        "Ravikumar Parkhe  •  Group Lead\n"
        + "Sandesh Jangam\n"
        + "Martand Satao",
        "Together, our team worked on the design, development, security, "
        + "and implementation of ElectraVote SuperX."
);
        // ---------------------------------------------------------
        // Co-Founders
        // ---------------------------------------------------------

        VBox founders = section(
                "Co-Founders ",
                "Shashikant Bagal   •  Co-Founder & CEO\n"
                + "Pramod Bansode   •  Co-Founder & CTO\n"
                + "Sachin Patil     •  Co-Founder & COO\n"
                + "Akashay Jagtap   •  Co-Founder",
                "Our leadership team brings together application engineering, "
                + "systems design, and security architecture to build a trustworthy "
                + "electoral experience."
        );


        // ---------------------------------------------------------
        // Super Mentors
        // ---------------------------------------------------------

        VBox superMentors = section(
                "Thanks to Our Super Mentors",
                "Shivkumar Tengse        •  Super Mentor\n"
                + "Subodh Yelgandharwar  •  Super Mentor",
                "Thank you for your continuous encouragement, practical guidance, "
                + "and support in overcoming technical challenges."
        );

        // ---------------------------------------------------------
        // Mentors & Team Leads
        // ---------------------------------------------------------

        VBox mentors = section(
                "Thanks to Our Mentors & Team Leads",
                "To all our mentors and team leads: thank you for your time, feedback, "
                + "patience, and support. Your guidance helped us learn, collaborate, "
                + "and move this project forward."
        );

        // ---------------------------------------------------------
        // Footer
        // ---------------------------------------------------------

        Label footer = text(
                "Made with gratitude. Built to make a difference.",
                13,
                false,
                "#A99FFF"
        );

        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setAlignment(Pos.CENTER);

        page.getChildren().addAll(
                intro,
                thanks,
                project,
                ourteam,
                founders,
                superMentors,
                mentors,
                footer
        );

        return page;
    }

    // -------------------------------------------------------------
    // Scroll View
    // -------------------------------------------------------------

    public ScrollPane createView() {

        ScrollPane scroll = new ScrollPane(createContent());

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background: #0B1733; "
                + "-fx-background-color: transparent; "
                + "-fx-padding: 0;"
        );

        return scroll;
    }

    // -------------------------------------------------------------
    // Shashi Sir Photo
    // -------------------------------------------------------------

    private StackPane createShashiPhotoPane() {

        ImageView portrait = new ImageView();

        /*
         * First location:
         * src/main/resources/ShashiSir.png
         *
         * Second location:
         * src/main/resources/assests/images/shashikant.png
         */
        InputStream stream =
                getClass().getResourceAsStream("/ShashiSir.png");

        if (stream == null) {
            stream =
                    getClass().getResourceAsStream(
                            "/assests/images/shashikant.png"
                    );
        }

        if (stream != null) {

            portrait.setImage(new Image(stream));

            portrait.setFitWidth(220);
            portrait.setFitHeight(240);

            portrait.setPreserveRatio(true);
            portrait.setSmooth(true);

            /*
             * Main photo container
             *
             * The image is centered both horizontally
             * and vertically.
             */
            StackPane photo = new StackPane();

            photo.setAlignment(Pos.CENTER);

            photo.getChildren().add(portrait);

            photo.setPrefWidth(260);
            photo.setPrefHeight(280);

            photo.setMaxWidth(Double.MAX_VALUE);

            photo.setStyle(
                    "-fx-background-color: #F3F0FF; "
                    + "-fx-background-radius: 18;"
            );

            return photo;

        } else {

            // -----------------------------------------------------
            // Fallback when image is not found
            // -----------------------------------------------------

            Circle bg = new Circle(
                    65,
                    Color.web("#8B5CF6")
            );

            Label avatarLabel = new Label(
                    "SHASHI SIR"
            );

            avatarLabel.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            16
                    )
            );

            avatarLabel.setStyle(
                    "-fx-text-fill: white;"
            );

            StackPane photo = new StackPane(
                    bg,
                    avatarLabel
            );

            photo.setAlignment(Pos.CENTER);

            photo.setPrefSize(
                    260,
                    280
            );

            photo.setMaxWidth(
                    Double.MAX_VALUE
            );

            photo.setStyle(
                    "-fx-background-color: #F3F0FF; "
                    + "-fx-background-radius: 18;"
            );

            return photo;
        }
    }

    // -------------------------------------------------------------
    // Section
    // -------------------------------------------------------------

    private VBox section(
            String title,
            String... paragraphs
    ) {

        VBox section = card();

        section.getChildren().add(
                text(
                        title,
                        21,
                        true,
                        "#F8FAFF"
                )
        );

        for (String paragraph : paragraphs) {

            section.getChildren().add(
                    text(
                            paragraph,
                            15,
                            false,
                            "#CBD5E1"
                    )
            );
        }

        return section;
    }

    // -------------------------------------------------------------
    // Card
    // -------------------------------------------------------------

    private VBox card() {

        VBox card = new VBox(14);

        card.setPadding(
                new Insets(26)
        );

        card.setMinWidth(0);

        card.setStyle(
                "-fx-background-color: #152447; "
                + "-fx-background-radius: 18; "
                + "-fx-border-color: #304366; "
                + "-fx-border-radius: 18;"
        );

        return card;
    }

    // -------------------------------------------------------------
    // Text Helper
    // -------------------------------------------------------------

    private Label text(
            String value,
            double size,
            boolean bold,
            String color
    ) {

        Label label = new Label(value);

        label.setWrapText(true);

        label.setMinWidth(0);

        label.setMinHeight(
                Region.USE_PREF_SIZE
        );

        label.setFont(
                Font.font(
                        "Arial",
                        bold
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        size
                )
        );

        label.setStyle(
                "-fx-text-fill: " + color + ";"
        );

        return label;
    }

    // -------------------------------------------------------------
    // Get Scene
    // -------------------------------------------------------------

    public Scene getScene() {
        return scene;
    }
}