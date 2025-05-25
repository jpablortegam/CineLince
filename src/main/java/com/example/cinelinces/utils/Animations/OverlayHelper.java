package com.example.cinelinces.utils.Animations;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Maneja el overlay de fondo (fondo semitransparente y click-to-hide).
 */
public class OverlayHelper {
    private final Rectangle overlay;

    public OverlayHelper(Pane container) {
        this.overlay   = new Rectangle();

        // Configuración inicial del rectángulo semitransparente
        overlay.setFill(Color.BLACK);
        overlay.setOpacity(0);
        overlay.widthProperty().bind(container.widthProperty());
        overlay.heightProperty().bind(container.heightProperty());
        overlay.setVisible(false);

        // Insertar siempre al fondo del Pane
        container.getChildren().add(0, overlay);

        // Click para ocultar el overlay (sin callback adicional)
        overlay.setOnMouseClicked(e -> hide(
                Duration.millis(200),
                Interpolator.EASE_BOTH,
                null
        ));
    }

    /** Lleva el overlay al frente de los demás hijos del container */
    public void bringToFront() {
        overlay.toFront();
    }

    /**
     * Muestra el overlay animando su opacidad desde 0 hasta targetOpacity.
     *
     * @param duration       Duración de la animación
     * @param targetOpacity  Opacidad final (0.0–1.0)
     * @param interp         Interpolador a usar
     */
    public void show(Duration duration, double targetOpacity, Interpolator interp) {
        bringToFront();
        overlay.setVisible(true);
        FadeTransition ft = new FadeTransition(duration, overlay);
        ft.setFromValue(0);
        ft.setToValue(targetOpacity);
        ft.setInterpolator(interp);
        ft.play();
    }

    /**
     * Oculta el overlay animando su opacidad hasta 0,
     * luego lo hace invisible y corre onFinished.
     *
     * @param duration   Duración de la animación
     * @param interp     Interpolador a usar
     * @param onFinished Callback a ejecutar cuando termine (puede ser null)
     */
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

    /** @return El Rectangle interno que actúa como overlay */
    public Rectangle getOverlay() {
        return overlay;
    }
}
