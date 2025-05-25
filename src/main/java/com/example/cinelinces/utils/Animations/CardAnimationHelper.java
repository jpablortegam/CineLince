package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color; // Necesario para DropShadow
import javafx.util.Duration;

public class CardAnimationHelper {
    // Constantes de Hover
    private static final double HOVER_SCALE = 1.03;
    private static final double HOVER_TRANSLATE_Y = -5;
    private static final double HOVER_SHADOW_RADIUS = 12;
    private static final double HOVER_SHADOW_OFFSET_Y = 4;
    private static final Color HOVER_SHADOW_COLOR = Color.rgb(0,0,0,0.25); // Color de sombra más pronunciado para hover


    // Duraciones de Expandir/Colapsar
    public static final Duration EXPAND_DURATION = Duration.millis(380);
    public static final Duration COLLAPSE_DURATION = Duration.millis(280);

    // Valores de sombra sutil
    public static final double SUBTLE_SHADOW_RADIUS = 8;
    public static final double SUBTLE_SHADOW_OFFSETY = 2;
    public static final Color SUBTLE_SHADOW_COLOR = Color.rgb(0,0,0,0.15);


    // Interpoladores
    public static final Interpolator SMOOTH = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    private static final Interpolator SETTLE_INTERPOLATOR = Interpolator.EASE_OUT;
    private static final Interpolator MOVEMENT_INTERPOLATOR = SMOOTH;


    // ---------------- HOVER ----------------
    public static Timeline hoverIn(Node card, Duration hoverDur) {
        DropShadow ds = (DropShadow) card.getEffect();
        if (ds == null) { // Si no hay sombra, crear una sutil como base
            ds = new DropShadow(SUBTLE_SHADOW_RADIUS, SUBTLE_SHADOW_OFFSETY, 0, SUBTLE_SHADOW_COLOR);
            card.setEffect(ds);
        }
        // Guardar los valores originales de la sombra actual si es diferente a la sutil
        // Para este ejemplo, animamos hacia valores fijos de hover.
        return new Timeline(new KeyFrame(hoverDur,
                new KeyValue(card.scaleXProperty(), HOVER_SCALE, Interpolator.EASE_OUT),
                new KeyValue(card.scaleYProperty(), HOVER_SCALE, Interpolator.EASE_OUT),
                new KeyValue(card.translateYProperty(), HOVER_TRANSLATE_Y, Interpolator.EASE_OUT),
                new KeyValue(ds.radiusProperty(), HOVER_SHADOW_RADIUS, Interpolator.EASE_OUT),
                new KeyValue(ds.offsetYProperty(), HOVER_SHADOW_OFFSET_Y, Interpolator.EASE_OUT),
                new KeyValue(ds.colorProperty(), HOVER_SHADOW_COLOR, Interpolator.EASE_OUT)
        ));
    }

    public static Timeline hoverOut(Node card, Duration hoverDur) {
        DropShadow ds = (DropShadow) card.getEffect();
        if (ds == null) { // Esto no debería pasar si hoverIn puso una, pero por si acaso.
            ds = new DropShadow(SUBTLE_SHADOW_RADIUS, SUBTLE_SHADOW_OFFSETY, 0, SUBTLE_SHADOW_COLOR);
            card.setEffect(ds);
        }
        // Volver a la sombra sutil
        Timeline tl = new Timeline(new KeyFrame(hoverDur,
                new KeyValue(card.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(card.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(card.translateYProperty(), 0.0, Interpolator.EASE_OUT),
                new KeyValue(ds.radiusProperty(), SUBTLE_SHADOW_RADIUS, Interpolator.EASE_OUT),
                new KeyValue(ds.offsetYProperty(), SUBTLE_SHADOW_OFFSETY, Interpolator.EASE_OUT),
                new KeyValue(ds.colorProperty(), SUBTLE_SHADOW_COLOR, Interpolator.EASE_OUT)
        ));
        // No es necesario ds.setSpread(0); aquí si la sombra sutil no tiene spread.
        // tl.setOnFinished(e -> card.setEffect(finalDs)); // Opcional si quieres resetear a una instancia específica
        return tl;
    }

    // ---------------- EXPAND ----------------
    public static ParallelTransition expand(Node card,
                                            Pane overlayPane, // Mantenido, aunque no se use activamente en esta implementación de expand
                                            double deltaX, double deltaY,
                                            double finalScaleX, double finalScaleY) {
        TranslateTransition move = TransitionFactory.translate(card, EXPAND_DURATION, deltaX, deltaY, MOVEMENT_INTERPOLATOR);
        FadeTransition fadeIn = TransitionFactory.fade(card, EXPAND_DURATION.multiply(0.7), 0, 1, EXPAND_DURATION.multiply(0.15), SMOOTH);
        Timeline scaleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.scaleXProperty(), 1.0), // Asume que la tarjeta está en escala 1.0 antes de esto
                        new KeyValue(card.scaleYProperty(), 1.0)
                ),
                new KeyFrame(EXPAND_DURATION.multiply(0.75),
                        new KeyValue(card.scaleXProperty(), finalScaleX * 1.05, SMOOTH), // Overshoot
                        new KeyValue(card.scaleYProperty(), finalScaleY * 1.05, SMOOTH)  // Overshoot
                ),
                new KeyFrame(EXPAND_DURATION,
                        new KeyValue(card.scaleXProperty(), finalScaleX, SETTLE_INTERPOLATOR),
                        new KeyValue(card.scaleYProperty(), finalScaleY, SETTLE_INTERPOLATOR)
                )
        );
        return new ParallelTransition(card, move, fadeIn, scaleAnim);
    }

    // ---------------- COLLAPSE ----------------
    public static ParallelTransition collapse(Node card,
                                              Pane overlayPane,
                                              Pane originalParent,
                                              Node placeholder,
                                              double targetOverlayX, // Estos no se usan si moveBack va a translate 0,0
                                              double targetOverlayY, // ya que layoutX/Y de la card en overlay es la base
                                              DropShadow finalEffect,
                                              Runnable onAllOperationsCompleted) {

        TranslateTransition moveBack = TransitionFactory.translate(
                card, COLLAPSE_DURATION, 0, 0, MOVEMENT_INTERPOLATOR // Revertir translateX/Y a 0
        );

        ScaleTransition scaleBack = TransitionFactory.scale(
                card, COLLAPSE_DURATION, 1.0, 1.0, SMOOTH // Escalar a 1.0, 1.0
        );

        FadeTransition fadeOut = TransitionFactory.fade(
                card, COLLAPSE_DURATION.multiply(0.8),
                card.getOpacity(), 0.7, // Baja la opacidad durante la transición
                Duration.ZERO, SMOOTH
        );

        ParallelTransition pt = new ParallelTransition(card, moveBack, scaleBack, fadeOut);
        pt.setOnFinished(e -> {
            Platform.runLater(() -> {
                boolean reinsertedCorrectly = false;
                if (placeholder != null && originalParent != null && placeholder.getParent() == originalParent) {
                    int idx = originalParent.getChildren().indexOf(placeholder);

                    overlayPane.getChildren().remove(card); // Primero remover del overlay
                    originalParent.getChildren().remove(placeholder); // Luego remover placeholder

                    // Resetear estado de la tarjeta ANTES de reinsertar
                    card.setTranslateX(0);
                    card.setTranslateY(0);
                    card.setScaleX(1);
                    card.setScaleY(1);
                    card.setOpacity(1.0); // Opacidad completa
                    card.setEffect(finalEffect); // Efecto final deseado (sombra sutil)

                    if (idx != -1 && idx <= originalParent.getChildren().size()) {
                        originalParent.getChildren().add(idx, card);
                    } else {
                        originalParent.getChildren().add(card); // fallback
                    }
                    reinsertedCorrectly = true;
                } else {
                    System.err.println("Advertencia: Placeholder/originalParent no válido o inconsistente al colapsar. Limpiando desde overlay.");
                    overlayPane.getChildren().remove(card);
                    card.setTranslateX(0); card.setTranslateY(0);
                    card.setScaleX(1); card.setScaleY(1);
                    card.setOpacity(1.0); // Opacidad completa
                    card.setEffect(finalEffect);
                }

                if (reinsertedCorrectly) {
                    card.setVisible(true); // Asegurar visibilidad
                    card.setManaged(true); // Asegurar que participa en el layout
                    originalParent.requestLayout();
                } else {
                    card.setVisible(true); // O false, si se considera un error irrecuperable
                }

                if (onAllOperationsCompleted != null) {
                    onAllOperationsCompleted.run();
                }
            });
        });
        return pt;
    }
}