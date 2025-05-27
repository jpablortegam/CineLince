package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CardAnimationHelper {

    // --- Constantes Generales ---
    private static final Duration HOVER_ANIM_DURATION = Duration.millis(150); // Duración para animaciones de hover

    // --- Constantes de Hover ---
    private static final double HOVER_SCALE_FACTOR = 1.03;
    private static final double HOVER_TRANSLATE_Y_DELTA = -6; // Un poco más de elevación
    private static final double HOVER_SHADOW_RADIUS_VALUE = 15; // Sombra más grande en hover
    private static final double HOVER_SHADOW_OFFSET_Y_VALUE = 5;
    private static final Color HOVER_SHADOW_EFFECT_COLOR = Color.rgb(0, 0, 0, 0.28);

    // --- Duraciones de Expandir/Colapsar ---
    // Duraciones ligeramente ajustadas para una sensación más suave
    public static final Duration EXPAND_ANIM_DURATION = Duration.millis(450); // Un poco más largo para movimiento y escala
    public static final Duration COLLAPSE_ANIM_DURATION = Duration.millis(350);

    // --- Efectos de Sombra ---
    // Sombra sutil para el estado normal de la tarjeta
    public static final DropShadow SUBTLE_SHADOW_EFFECT = new DropShadow(
            8, // radius
            2, // offsetY
            0, // offsetX
            Color.rgb(0, 0, 0, 0.12) // color más suave
    );
    // Sombra para el estado expandido (más pronunciada)
    public static final DropShadow EXPANDED_CARD_SHADOW_EFFECT = new DropShadow(
            25, // radius
            8,  // offsetY
            0,  // offsetX
            Color.rgb(0, 0, 0, 0.30)
    );

    // --- Interpoladores ---
    // Interpolador suave estándar para la mayoría de las animaciones de UI
    public static final Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0.0, 0.0, 0.2, 1.0); // Típico ease-out
    public static final Interpolator EASE_IN_OUT_INTERPOLATOR = Interpolator.SPLINE(0.42, 0.0, 0.58, 1.0); // Típico ease-in-out
    private static final Interpolator SMOOTH_PHYSICS_INTERPOLATOR = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94); // El que ya tenías, bueno para sensación orgánica

    // Se usarán principalmente EASE_OUT y EASE_IN_OUT para consistencia, SMOOTH_PHYSICS para el overshoot
    private static final Interpolator PRIMARY_MOVEMENT_INTERPOLATOR = EASE_IN_OUT_INTERPOLATOR;
    private static final Interpolator SCALE_SETTLE_INTERPOLATOR = EASE_OUT_INTERPOLATOR;
    private static final Interpolator FADE_INTERPOLATOR = EASE_OUT_INTERPOLATOR;


    // ---------------- HOVER ANIMATIONS ----------------

    public static Timeline createHoverInAnimation(Node cardNode) {
        DropShadow currentEffect = (DropShadow) cardNode.getEffect();
        if (currentEffect == null) {
            currentEffect = new DropShadow(); // Crear uno si no existe
            cardNode.setEffect(currentEffect);
        }

        return new Timeline(new KeyFrame(HOVER_ANIM_DURATION,
                new KeyValue(cardNode.scaleXProperty(), HOVER_SCALE_FACTOR, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.scaleYProperty(), HOVER_SCALE_FACTOR, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.translateYProperty(), HOVER_TRANSLATE_Y_DELTA, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.radiusProperty(), HOVER_SHADOW_RADIUS_VALUE, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.offsetYProperty(), HOVER_SHADOW_OFFSET_Y_VALUE, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.colorProperty(), HOVER_SHADOW_EFFECT_COLOR, EASE_OUT_INTERPOLATOR)
        ));
    }

    public static Timeline createHoverOutAnimation(Node cardNode) {
        DropShadow currentEffect = (DropShadow) cardNode.getEffect();
        if (currentEffect == null) { // No debería ocurrir si hoverIn lo estableció
            currentEffect = new DropShadow();
            cardNode.setEffect(currentEffect);
        }

        // Vuelve a la sombra sutil definida
        return new Timeline(new KeyFrame(HOVER_ANIM_DURATION,
                new KeyValue(cardNode.scaleXProperty(), 1.0, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.scaleYProperty(), 1.0, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.translateYProperty(), 0.0, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.radiusProperty(), SUBTLE_SHADOW_EFFECT.getRadius(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.offsetYProperty(), SUBTLE_SHADOW_EFFECT.getOffsetY(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.colorProperty(), SUBTLE_SHADOW_EFFECT.getColor(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.spreadProperty(), SUBTLE_SHADOW_EFFECT.getSpread(), EASE_OUT_INTERPOLATOR)
        ));
    }


    // ---------------- EXPAND CARD ANIMATION ----------------
    /**
     * Crea una animación para expandir una tarjeta.
     * La tarjeta debe ser movida al overlayPane y su layoutX/Y ajustado
     * para que aparezca en su posición original ANTES de iniciar esta animación.
     * Su opacidad inicial debería ser 0.
     *
     * @param card El nodo de la tarjeta a animar.
     * @param targetTranslateX El valor final de translateX para la tarjeta en el overlay.
     * @param targetTranslateY El valor final de translateY para la tarjeta en el overlay.
     * @param finalScaleX Escala X final.
     * @param finalScaleY Escala Y final.
     * @return Una ParallelTransition para la animación de expansión.
     */
    public static ParallelTransition createExpandAnimation(Node card,
                                                           double targetTranslateX, double targetTranslateY,
                                                           double finalScaleX, double finalScaleY) {

        // 1. Movimiento (Translate)
        // Anima desde su translateX/Y actual (debería ser 0 si se reseteó bien) a los valores finales.
        TranslateTransition move = new TranslateTransition(EXPAND_ANIM_DURATION, card);
        move.setToX(targetTranslateX);
        move.setToY(targetTranslateY);
        move.setInterpolator(PRIMARY_MOVEMENT_INTERPOLATOR);

        // 2. Aparición (FadeIn)
        // Asume que la tarjeta tiene opacidad 0 al inicio de esta animación.
        FadeTransition fadeIn = new FadeTransition(EXPAND_ANIM_DURATION.multiply(0.6), card); // Más rápido que el movimiento/escala
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(FADE_INTERPOLATOR);
        // fadeIn.setDelay(EXPAND_ANIM_DURATION.multiply(0.1)); // Opcional: un pequeño retraso

        // 3. Escalado con Efecto de "Overshoot" (Rebote)
        Timeline scaleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.scaleXProperty(), card.getScaleX()), // Empieza desde la escala actual (debería ser 1.0)
                        new KeyValue(card.scaleYProperty(), card.getScaleY())
                ),
                // Efecto de overshoot: se pasa un poco de la escala final
                new KeyFrame(EXPAND_ANIM_DURATION.multiply(0.75), // Momento del overshoot
                        new KeyValue(card.scaleXProperty(), finalScaleX * 1.05, SMOOTH_PHYSICS_INTERPOLATOR),
                        new KeyValue(card.scaleYProperty(), finalScaleY * 1.05, SMOOTH_PHYSICS_INTERPOLATOR)
                ),
                // Asentamiento a la escala final
                new KeyFrame(EXPAND_ANIM_DURATION,
                        new KeyValue(card.scaleXProperty(), finalScaleX, SCALE_SETTLE_INTERPOLATOR),
                        new KeyValue(card.scaleYProperty(), finalScaleY, SCALE_SETTLE_INTERPOLATOR)
                )
        );

        // 4. Animación de Sombra
        DropShadow currentEffect = (DropShadow) card.getEffect();
        if (currentEffect == null) { // Asegurar que hay un efecto para animar
            currentEffect = new DropShadow(); // Valores iniciales por defecto
            card.setEffect(currentEffect);
        }
        Timeline shadowAnim = new Timeline(
                new KeyFrame(EXPAND_ANIM_DURATION, // La sombra alcanza su estado final al mismo tiempo
                        new KeyValue(currentEffect.radiusProperty(), EXPANDED_CARD_SHADOW_EFFECT.getRadius(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.offsetXProperty(), EXPANDED_CARD_SHADOW_EFFECT.getOffsetX(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.offsetYProperty(), EXPANDED_CARD_SHADOW_EFFECT.getOffsetY(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.colorProperty(), EXPANDED_CARD_SHADOW_EFFECT.getColor(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.spreadProperty(), EXPANDED_CARD_SHADOW_EFFECT.getSpread(), PRIMARY_MOVEMENT_INTERPOLATOR)
                )
        );

        return new ParallelTransition(card, move, fadeIn, scaleAnim, shadowAnim);
    }

    // ---------------- COLLAPSE CARD ANIMATION ----------------
    /**
     * Crea una animación para colapsar una tarjeta de vuelta a su estado original.
     *
     * @param card El nodo de la tarjeta a animar.
     * @param overlayPane El panel overlay del que se removerá la tarjeta.
     * @param originalParent El panel original al que volverá la tarjeta.
     * @param placeholder El nodo placeholder a remover del originalParent (puede ser null).
     * @param onAllOperationsCompleted Callback a ejecutar cuando toda la animación y limpieza hayan finalizado.
     * @return Una ParallelTransition para la animación de colapso.
     */
    public static ParallelTransition createCollapseAnimation(Node card,
                                                             Pane overlayPane,
                                                             Pane originalParent,
                                                             Node placeholder,
                                                             Runnable onAllOperationsCompleted) {

        // 1. Movimiento de Retorno (Translate)
        // Vuelve a translateX/Y = 0,0 relativo a su layoutX/Y en el overlayPane
        TranslateTransition moveBack = new TranslateTransition(COLLAPSE_ANIM_DURATION, card);
        moveBack.setToX(0);
        moveBack.setToY(0);
        moveBack.setInterpolator(PRIMARY_MOVEMENT_INTERPOLATOR);

        // 2. Escalado de Retorno
        ScaleTransition scaleBack = new ScaleTransition(COLLAPSE_ANIM_DURATION, card);
        scaleBack.setToX(1.0);
        scaleBack.setToY(1.0);
        scaleBack.setInterpolator(PRIMARY_MOVEMENT_INTERPOLATOR);

        // 3. Desvanecimiento Parcial (Fade)
        // Reduce la opacidad durante la transición para un efecto más suave
        FadeTransition fadeOut = new FadeTransition(COLLAPSE_ANIM_DURATION.multiply(0.7), card);
        fadeOut.setFromValue(card.getOpacity()); // Desde la opacidad actual
        fadeOut.setToValue(0.6); // Baja la opacidad temporalmente (se restaura a 1.0 al final)
        fadeOut.setInterpolator(FADE_INTERPOLATOR);

        // 4. Animación de Sombra de Retorno
        DropShadow currentEffect = (DropShadow) card.getEffect();
        Timeline shadowCollapseAnim = null;
        if (currentEffect != null) {
            shadowCollapseAnim = new Timeline(
                    new KeyFrame(COLLAPSE_ANIM_DURATION,
                            new KeyValue(currentEffect.radiusProperty(), SUBTLE_SHADOW_EFFECT.getRadius(), PRIMARY_MOVEMENT_INTERPOLATOR),
                            new KeyValue(currentEffect.offsetXProperty(), SUBTLE_SHADOW_EFFECT.getOffsetX(), PRIMARY_MOVEMENT_INTERPOLATOR),
                            new KeyValue(currentEffect.offsetYProperty(), SUBTLE_SHADOW_EFFECT.getOffsetY(), PRIMARY_MOVEMENT_INTERPOLATOR),
                            new KeyValue(currentEffect.colorProperty(), SUBTLE_SHADOW_EFFECT.getColor(), PRIMARY_MOVEMENT_INTERPOLATOR),
                            new KeyValue(currentEffect.spreadProperty(), SUBTLE_SHADOW_EFFECT.getSpread(), PRIMARY_MOVEMENT_INTERPOLATOR)
                    )
            );
        }

        ParallelTransition pt;
        if (shadowCollapseAnim != null) {
            pt = new ParallelTransition(card, moveBack, scaleBack, fadeOut, shadowCollapseAnim);
        } else {
            pt = new ParallelTransition(card, moveBack, scaleBack, fadeOut);
        }

        // --- Acciones al finalizar la animación ---
        pt.setOnFinished(event -> {
            // Es crucial ejecutar las manipulaciones de la escena en el hilo de la UI de JavaFX
            Platform.runLater(() -> {
                // 1. Remover la tarjeta del overlay
                overlayPane.getChildren().remove(card);

                // 2. Resetear completamente el estado de la tarjeta
                card.setTranslateX(0);
                card.setTranslateY(0);
                card.setScaleX(1.0);
                card.setScaleY(1.0);
                card.setOpacity(1.0); // Opacidad completa
                card.setEffect(SUBTLE_SHADOW_EFFECT); // Aplicar la sombra sutil final

                // 3. Reinsertar la tarjeta en su contenedor original
                boolean reinsertedCorrectly = false;
                if (originalParent != null) {
                    if (placeholder != null && placeholder.getParent() == originalParent) {
                        int placeholderIndex = originalParent.getChildren().indexOf(placeholder);
                        originalParent.getChildren().remove(placeholder); // Remover el placeholder
                        if (placeholderIndex != -1) {
                            originalParent.getChildren().add(placeholderIndex, card);
                        } else {
                            originalParent.getChildren().add(card); // Fallback si el índice no es válido
                        }
                    } else {
                        originalParent.getChildren().add(card); // Añadir al final si no hay placeholder
                    }
                    reinsertedCorrectly = true;

                    // Asegurar que la tarjeta sea visible y gestionada por el layout
                    card.setVisible(true);
                    card.setManaged(true);
                    originalParent.requestLayout(); // Solicitar un nuevo ciclo de layout
                } else {
                    System.err.println("CardAnimationHelper: originalParent es null. La tarjeta no se puede reinsertar.");
                    // Podrías querer hacer la tarjeta invisible si no se puede reinsertar
                    // card.setVisible(false);
                }

                // 4. Ejecutar el callback si existe
                if (onAllOperationsCompleted != null) {
                    onAllOperationsCompleted.run();
                }
            });
        });

        return pt;
    }
}