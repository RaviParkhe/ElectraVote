package com.elctrovotesuperx.view.AdminView;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReportsPage extends VBox {
    public ReportsPage() {
        this.setAlignment(Pos.CENTER);
        Label label = new Label("Reports Page");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        this.getChildren().add(label);
    }
}
