package com.electrovotesuperx.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Simple UI Animation Helper for smooth hover, scale, and fade animations.
 * Written with basic JavaFX transitions (No complex code, No external CSS).
 */
public class UIAnimationHelper {

    /**
     * Adds smooth scale-up hover effect to any UI component.
     */
    public static void addScaleHover(Node node, double scaleFactor) {
        node.setCursor(Cursor.HAND);

        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(140), node);
            st.setToX(scaleFactor);
            st.setToY(scaleFactor);
            st.play();
        });

        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(140), node);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    /**
     * Adds a card hover effect (lifts the card up slightly by 3px on hover).
     */
    public static void addCardHover(Node card) {
        card.setCursor(Cursor.HAND);

        card.setOnMouseEntered(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(160), card);
            tt.setToY(-4);
            tt.play();
        });

        card.setOnMouseExited(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(160), card);
            tt.setToY(0);
            tt.play();
        });
    }

    /**
     * Plays a smooth fade-in and slide-up entrance animation.
     */
    public static void fadeInSlideUp(Node node, double delayMs) {
        node.setOpacity(0);
        node.setTranslateY(18);

        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setDelay(Duration.millis(delayMs));

        TranslateTransition tt = new TranslateTransition(Duration.millis(400), node);
        tt.setFromY(18);
        tt.setToY(0);
        tt.setDelay(Duration.millis(delayMs));

        ft.play();
        tt.play();
    }

    /**
     * Adds gentle pulse animation to important badges (e.g. Crown / Winning badge).
     */
    public static void addPulse(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(900), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.08);
        pulse.setToY(1.08);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    /**
     * Plays a celebration confetti falling animation across the screen.
     * Drops colorful confetti particles smoothly from the top.
     */
    public static void playCelebrationConfetti(javafx.scene.layout.Pane targetPane) {
        if (targetPane == null) return;

        javafx.scene.paint.Color[] colors = new javafx.scene.paint.Color[]{
                javafx.scene.paint.Color.web("#F59E0B"), // Gold
                javafx.scene.paint.Color.web("#10B981"), // Emerald Green
                javafx.scene.paint.Color.web("#3B82F6"), // Sky Blue
                javafx.scene.paint.Color.web("#8B5CF6"), // Royal Purple
                javafx.scene.paint.Color.web("#EC4899"), // Rose Pink
                javafx.scene.paint.Color.web("#EF4444")  // Crimson Red
        };

        java.util.Random random = new java.util.Random();
        int particleCount = 40;

        for (int i = 0; i < particleCount; i++) {
            javafx.scene.shape.Shape particle;
            if (i % 2 == 0) {
                javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(7 + random.nextInt(5), 10 + random.nextInt(6));
                rect.setArcWidth(3);
                rect.setArcHeight(3);
                particle = rect;
            } else {
                particle = new javafx.scene.shape.Circle(4 + random.nextInt(3));
            }

            particle.setFill(colors[random.nextInt(colors.length)]);
            particle.setMouseTransparent(true);

            double startX = 20 + random.nextDouble() * 950;
            double startY = -25 - random.nextDouble() * 120;
            particle.setTranslateX(startX);
            particle.setTranslateY(startY);

            targetPane.getChildren().add(particle);

            double duration = 2400 + random.nextInt(1800);
            TranslateTransition fall = new TranslateTransition(Duration.millis(duration), particle);
            fall.setFromY(startY);
            fall.setToY(850);
            fall.setFromX(startX);
            fall.setToX(startX + (random.nextDouble() * 80 - 40));
            fall.setDelay(Duration.millis(random.nextInt(500)));

            javafx.animation.RotateTransition rotate = new javafx.animation.RotateTransition(Duration.millis(duration), particle);
            rotate.setByAngle(360 + random.nextInt(360));

            FadeTransition fade = new FadeTransition(Duration.millis(600), particle);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setDelay(Duration.millis(duration - 400));

            fall.setOnFinished(e -> targetPane.getChildren().remove(particle));

            fall.play();
            rotate.play();
            fade.play();
        }
    }
}
