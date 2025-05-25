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

    // Expand/Collapse durations (ajustadas para ser similares a DialogAnimationHelper)
    public static final Duration EXPAND_DURATION   = Duration.millis(600); // Antes 450
    public static final Duration COLLAPSE_DURATION = Duration.millis(450); // Antes 350

    // Valores de sombra “sutil” al volver de hover/collapse
    private static final double   SUBTLE_SHADOW_RADIUS  = 8;
    private static final double   SUBTLE_SHADOW_OFFSETY = 2;

    // Interpolators (como en DialogAnimationHelper)
    public static final Interpolator SMOOTH    = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    private static final Interpolator BOUNCE    = Interpolator.SPLINE(0.68, 0.1, 0.265, 0.99);
    private static final Interpolator BACK_EASE = Interpolator.SPLINE(0.68, 0.1, 0.32, 0.9);


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
     * @param card       La tarjeta (debe ya estar colocada en overlayPane)
     * @param overlay    El Pane que actúa de capa superior
     * @param targetX    Posición X final en coordenadas del overlay
     * @param targetY    Posición Y final en coordenadas del overlay
     * @param scaleX     Factor X final
     * @param scaleY     Factor Y final
     */
    public static ParallelTransition expand(Node card, Pane overlay,
                                            double targetX, double targetY,
                                            double scaleX, double scaleY) {
        // Asumiendo que card.setLayoutX/Y ya fue llamado en el controlador
        // para posicionar la tarjeta en el overlay.
        // targetX y targetY son el offset desde la posición inicial de la tarjeta
        // en el overlay para que quede centrada.

        TranslateTransition move   = TransitionFactory.translate(card, EXPAND_DURATION, targetX, targetY, BACK_EASE);
        FadeTransition      fadeIn = TransitionFactory.fade(card, EXPAND_DURATION.multiply(0.6), 0, 1, EXPAND_DURATION.multiply(0.2), SMOOTH);

        // Rebote de escala (como en DialogAnimationHelper)
        Timeline scaleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.scaleXProperty(), card.getScaleX()), // Inicia con la escala actual
                        new KeyValue(card.scaleYProperty(), card.getScaleY())
                ),
                new KeyFrame(EXPAND_DURATION.multiply(0.7),
                        new KeyValue(card.scaleXProperty(), scaleX * 1.08, SMOOTH),
                        new KeyValue(card.scaleYProperty(), scaleY * 1.08, SMOOTH)
                ),
                new KeyFrame(EXPAND_DURATION,
                        new KeyValue(card.scaleXProperty(), scaleX, BOUNCE),
                        new KeyValue(card.scaleYProperty(), scaleY, BOUNCE)
                )
        );

        return new ParallelTransition(move, fadeIn, scaleAnim);
    }


    // ---------------- COLLAPSE ----------------

    /**
     * Anima la tarjeta de vuelta a su lugar original y la reinserta en su contenedor.
     *
     * @param card        Tarjeta dentro de overlayPane
     * @param overlay     El mismo overlayPane que usaste en expand
     * @param placeholder Region placeholder que dejaste en el container original
     * @param origOverlayX  Coordenada X en overlay antes de moverla
     * @param origOverlayY  Coordenada Y en overlay antes de moverla
     * @param subtle      El DropShadow “sutil” con que debe terminar
     */
    public static ParallelTransition collapse(Node card,
                                              Pane overlay,
                                              Node placeholder,
                                              double origOverlayX,
                                              double origOverlayY,
                                              DropShadow subtle) {
        // El destino es la posición original de la tarjeta DENTRO del overlay
        double currentTranslateX = card.getTranslateX();
        double currentTranslateY = card.getTranslateY();

        TranslateTransition moveBack = TransitionFactory.translate(
                card, COLLAPSE_DURATION,
                origOverlayX - (card.getLayoutX() + currentTranslateX),
                origOverlayY - (card.getLayoutY() + currentTranslateY),
                BACK_EASE
        );
        ScaleTransition scaleBack = TransitionFactory.scale(
                card, COLLAPSE_DURATION,
                card.getScaleX(), card.getScaleY(),
                1.0, 1.0, // Scale back to 1.0
                SMOOTH
        );
        FadeTransition fadeOut = TransitionFactory.fade(
                card, COLLAPSE_DURATION.multiply(0.7),
                card.getOpacity(), 0,
                COLLAPSE_DURATION.multiply(0.2),
                SMOOTH
        );


        ParallelTransition pt = new ParallelTransition(moveBack, scaleBack, fadeOut);
        pt.setOnFinished(e -> {
            // Asegurarse de que el placeholder existe y tiene un padre
            if (placeholder != null && placeholder.getParent() instanceof Pane) {
                Pane originalParent = (Pane) placeholder.getParent();

                // 1) Reset transforms and opacity
                card.setTranslateX(0);
                card.setTranslateY(0);
                card.setScaleX(1);
                card.setScaleY(1);
                card.setOpacity(1); // La opacidad final se restablece a 1 para que sea visible

                // 2) Restore shadow
                card.setEffect(subtle);

                // 3) Reinsert en el container original
                // Primero, remover la tarjeta del overlay
                overlay.getChildren().remove(card);

                // Obtener el índice del placeholder en el padre original
                int idx = originalParent.getChildren().indexOf(placeholder);

                // Remover el placeholder
                originalParent.getChildren().remove(placeholder);

                // Añadir la tarjeta en la posición correcta.
                // Si el índice es -1 (no se encontró el placeholder), añadir al final.
                if (idx != -1 && idx <= originalParent.getChildren().size()) {
                    originalParent.getChildren().add(idx, card);
                } else {
                    originalParent.getChildren().add(card); // Añadir al final si el índice es inválido
                }

                // *** CLAVE: Forzar un layout en el padre para que se redibuje inmediatamente ***
                // Esto es especialmente útil para FlowPane o VBox/HBox donde los elementos
                // pueden necesitar un recálculo de posición después de añadir/quitar nodos.
                originalParent.requestLayout(); // <--- AÑADIR ESTA LÍNEA

                // Aunque ya pusimos la opacidad a 1, si hay alguna animación de fade out que termina
                // justo antes de esto, es bueno asegurar que el nodo está visible.
                card.setVisible(true); // <--- AÑADIR ESTA LÍNEA (seguridad)


            } else {
                // Si el placeholder no existe o no tiene padre, la tarjeta no se reinsertará.
                System.err.println("Advertencia: Placeholder no válido o sin padre al intentar colapsar la tarjeta. La tarjeta permanecerá en el overlay.");
                // Opcional: remover la tarjeta del overlay de todos modos si quieres que desaparezca
                overlay.getChildren().remove(card);
            }
        });
        return pt;
    }
}