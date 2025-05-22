package com.example.cinelinces.utils;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Aplica efecto hover de escalado a un botón.
 */
public class ButtonHoverAnimator {
    private static final Duration HOVER_DUR = Duration.millis(150);
    private static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);

    public static void applyHoverEffect(Button btn) {
        ScaleTransition in = new ScaleTransition(HOVER_DUR, btn);
        ScaleTransition out = new ScaleTransition(HOVER_DUR, btn);
        in.setToX(1.05); in.setToY(1.05);
        out.setToX(1.0); out.setToY(1.0);
        in.setInterpolator(EASE);
        out.setInterpolator(EASE);
        btn.setOnMouseEntered(e -> { if (!btn.isDisable()) in.play(); });
        btn.setOnMouseExited(e -> { if (!btn.isDisable()) out.play(); });
    }
}