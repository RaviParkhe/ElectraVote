package com.electrovotesuperx.view.CommonView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Reusable top header shown on every page.
 * Contains the page title/description on the left and a search box,
 * notification icon, and administrator name on the right.
 */
public class Header {

        public static HBox getHeader(String title, String description) {

                HBox header = new HBox();

                header.setPrefHeight(72);
                header.setMinHeight(72);
                header.setPrefWidth(1300);

                // IMPORTANT:
                // Do not give the header a fixed width.
                // This allows it to occupy the complete width of the CENTER VBox.
                header.setMaxWidth(Double.MAX_VALUE);

                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(8, 28, 8, 28));

                header.setStyle(
                                "-fx-background-color: #bdcbbc79; " +
                                                "-fx-border-width: 0 0 3px 0;");

                header.setSpacing(20);

                // Left: title + description
                Label lblTitle = new Label(title);
                lblTitle.setStyle(
                                "-fx-font-size: 19px; " +
                                                "-fx-font-weight: bold; " +
                                                "-fx-text-fill: #111827;");

                Label lblDesc = new Label(description);
                lblDesc.setStyle(
                                "-fx-font-size: 12px; " +
                                                "-fx-text-fill: #6B7280;");

                VBox titleBox = new VBox(2, lblTitle, lblDesc);

                // Spacer to push right side to the end
                HBox spacer = new HBox();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // =========================================================
                // PROFILE ICON
                // =========================================================
                Label profileIcon = new Label("A");
                profileIcon.setMinSize(38, 38);
                profileIcon.setAlignment(Pos.CENTER);
                profileIcon.setStyle(
                                "-fx-background-color: #E0E7FF;" +
                                                "-fx-background-radius: 50%;" +
                                                "-fx-text-fill: #4F46E5;" +
                                                "-fx-font-weight: bold;");

                HBox hb1 = new HBox(profileIcon);
                hb1.setAlignment(Pos.CENTER);
                // =========================================================
                // ADMIN PROFILE BLOCK
                // =========================================================

                Label admin = new Label("polling Officer");
                admin.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                admin.setTextFill(Color.web("#172033"));

                Label adminSub = new Label("Offline Voting");
                adminSub.setFont(Font.font("Arial", 10));
                adminSub.setTextFill(Color.web("#6B7280"));

                VBox adminBox = new VBox(admin, adminSub);

                HBox hb = new HBox(hb1, adminBox);
                hb.setSpacing(9);
                adminBox.setPadding(new Insets(11, 0, 0, 0));

                // =========================================================
                // ADD EVERYTHING TO HEADER
                // =========================================================

                header.getChildren().addAll(
                                titleBox,
                                spacer,
                                hb);

                return header;
        }
}