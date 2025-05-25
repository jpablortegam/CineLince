package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Helper para animaciones de tarjetas (hover, expand, collapse).
 */
public class CardAnimationHelper {
    // Hover constants
    private static final double   HOVER_SCALE           = 1.03;
    private static final double   HOVER_TRANSLATE_Y     = -5;
    private static final double   HOVER_SHADOW_RADIUS   = 12;
    private static final double   HOVER_SHADOW_OFFSET_Y = 4;
    private static final Duration HOVER_DURATION        = Duration.millis(200);

    // Expand/Collapse durations
    private static final Duration EXPAND_DURATION   = Duration.millis(450);
    private static final Duration COLLAPSE_DURATION = Duration.millis(350);

    // Valores de sombra “sutil” al volver de hover/collapse
    private static final double   SUBTLE_SHADOW_RADIUS  = 8;
    private static final double   SUBTLE_SHADOW_OFFSETY = 2;

    // ---------------- HOVER ----------------

    public static Timeline hoverIn(Node card, Duration hoverDur) {
        DropShadow ds = (DropShadow)card.getEffect();
        return new Timeline(new KeyFrame(HOVER_DURATION,
                new KeyValue(card.scaleXProperty(),    HOVER_SCALE,           Interpolator.EASE_OUT),
                new KeyValue(card.scaleYProperty(),    HOVER_SCALE,           Interpolator.EASE_OUT),
                new KeyValue(card.translateYProperty(),HOVER_TRANSLATE_Y,     Interpolator.EASE_OUT),
                new KeyValue(ds.radiusProperty(),      HOVER_SHADOW_RADIUS,   Interpolator.EASE_OUT),
                new KeyValue(ds.offsetYProperty(),     HOVER_SHADOW_OFFSET_Y, Interpolator.EASE_OUT)
        ));
    }

    public static Timeline hoverOut(Node card, Duration hoverDur) {
        DropShadow ds = (DropShadow)card.getEffect();
        Timeline tl = new Timeline(new KeyFrame(HOVER_DURATION,
                new KeyValue(card.scaleXProperty(),     1.0,                   Interpolator.EASE_OUT),
                new KeyValue(card.scaleYProperty(),     1.0,                   Interpolator.EASE_OUT),
                new KeyValue(card.translateYProperty(), 0.0,                   Interpolator.EASE_OUT),
                new KeyValue(ds.radiusProperty(),       SUBTLE_SHADOW_RADIUS,  Interpolator.EASE_OUT),
                new KeyValue(ds.offsetYProperty(),      SUBTLE_SHADOW_OFFSETY, Interpolator.EASE_OUT)
        ));
        // Aseguramos que el efecto siga siendo el mismo DropShadow
        tl.setOnFinished(e -> card.setEffect(ds));
        return tl;
    }

    // ---------------- EXPAND ----------------

    /**
     * Crea la animación de expand: traslada y escala la tarjeta para
     * centrarla en el overlay.
     *
     * @param card    La tarjeta (debe ya estar colocada en overlayPane)
     * @param overlay El Pane que actúa de capa superior
     * @param scaleX  Factor X final
     * @param scaleY  Factor Y final
     */
    public static ParallelTransition expand(Node card, Pane overlay, double scaleX, double scaleY) {
        Bounds b = card.getBoundsInParent();
        double initX = b.getMinX(), initY = b.getMinY();

        // Esquinas target (centrado)
        double targetX = (overlay.getWidth()  - b.getWidth()*scaleX ) / 2 - initX;
        double targetY = (overlay.getHeight() - b.getHeight()*scaleY) / 2 - initY;

        TranslateTransition move = new TranslateTransition(EXPAND_DURATION, card);
        move.setToX(targetX);
        move.setToY(targetY);
        move.setInterpolator(Interpolator.SPLINE(0.2, 0, 0.2, 1));

        ScaleTransition scale = new ScaleTransition(EXPAND_DURATION, card);
        scale.setFromX(1.0);  scale.setFromY(1.0);
        scale.setToX(scaleX); scale.setToY(scaleY);
        scale.setInterpolator(Interpolator.SPLINE(0.2, 0, 0.2, 1));

        return new ParallelTransition(move, scale);
    }

    // ---------------- COLLAPSE ----------------

    /**
     * Anima la tarjeta de vuelta a su lugar original y la reinserta en su contenedor.
     *
     * @param card        Tarjeta dentro de overlayPane
     * @param overlay     El mismo overlayPane que usaste en expand
     * @param placeholder Region placeholder que dejaste en el container original
     * @param origSceneX  Coordenada X en escena antes de moverla a overlay
     * @param origSceneY  Coordenada Y en escena antes de moverla a overlay
     * @param subtle      El DropShadow “sutil” con que debe terminar
     */
    public static ParallelTransition collapse(Node card,
                                              Pane overlay,
                                              Node placeholder,
                                              double origSceneX,
                                              double origSceneY,
                                              DropShadow subtle) {
        // Destino en coordenadas del overlay
        Point2D dest = overlay.sceneToLocal(origSceneX, origSceneY);

        TranslateTransition move = new TranslateTransition(COLLAPSE_DURATION, card);
        move.setToX(dest.getX());
        move.setToY(dest.getY());
        move.setInterpolator(Interpolator.SPLINE(0.2, 0, 0.2, 1));

        ScaleTransition scale = new ScaleTransition(COLLAPSE_DURATION, card);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.SPLINE(0.2, 0, 0.2, 1));

        ParallelTransition pt = new ParallelTransition(move, scale);
        pt.setOnFinished(e -> {
            // 1) Reset transforms
            card.setTranslateX(0);
            card.setTranslateY(0);
            card.setScaleX(1);
            card.setScaleY(1);
            // 2) Restore shadow
            card.setEffect(subtle);
            // 3) Reinsert en el container original
            Pane originalParent = (Pane) placeholder.getParent();
            int idx = originalParent.getChildren().indexOf(placeholder);
            originalParent.getChildren().remove(placeholder);
            overlay.getChildren().remove(card);
            originalParent.getChildren().add(idx, card);
        });
        return pt;
    }
}
