package com.example.cinelinces.utils.Animations;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class OverlayHelper {
    private final Rectangle overlay;

    public OverlayHelper(Pane container) {
        this.overlay = new Rectangle();

        overlay.setFill(Color.BLACK);
        overlay.setOpacity(0);
        overlay.widthProperty().bind(container.widthProperty());
        overlay.heightProperty().bind(container.heightProperty());
        overlay.setVisible(false);

        container.getChildren().add(0, overlay);

        overlay.setOnMouseClicked(e -> hide(
                Duration.millis(200),
                Interpolator.EASE_BOTH,
                null
        ));
    }

    public void bringToFront() {
        overlay.toFront();
    }

    public void show(Duration duration, double targetOpacity, Interpolator interp) {
        bringToFront();
        overlay.setVisible(true);
        FadeTransition ft = new FadeTransition(duration, overlay);
        ft.setFromValue(0);
        ft.setToValue(targetOpacity);
        ft.setInterpolator(interp);
        ft.play();
    }

    public void hide(Duration duration, Interpolator interp, Runnable onFinished) {
        FadeTransition ft = new FadeTransition(duration, overlay);
        ft.setFromValue(overlay.getOpacity());
        ft.setToValue(0);
        ft.setInterpolator(interp);
        ft.setOnFinished(e -> {
            overlay.setVisible(false);
            if (onFinished != null) {
                onFinished.run();
            }
        });
        ft.play();
    }

    public Rectangle getOverlay() {
        return overlay;
    }
}
