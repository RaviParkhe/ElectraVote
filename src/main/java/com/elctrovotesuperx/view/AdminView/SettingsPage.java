package com.elctrovotesuperx.view.AdminView;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsPage extends VBox {
    public SettingsPage() {
        this.setAlignment(Pos.CENTER);
        Label label = new Label("Settings Page");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        this.getChildren().add(label);
    }
}
