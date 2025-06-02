// Archivo: com/example/cinelinces/utils/Animations/CardAnimationHelper.java

package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CardAnimationHelper {

    private static final Duration HOVER_ANIM_DURATION = Duration.millis(120);
    private static final double HOVER_SCALE_FACTOR = 1.03;
    private static final double HOVER_TRANSLATE_Y_DELTA = -6;
    private static final double HOVER_SHADOW_RADIUS_VALUE = 15;
    private static final double HOVER_SHADOW_OFFSET_Y_VALUE = 5;
    private static final Color HOVER_SHADOW_EFFECT_COLOR = Color.rgb(0, 0, 0, 0.28);

    public static final Duration EXPAND_ANIM_DURATION = Duration.millis(450);
    public static final Duration COLLAPSE_ANIM_DURATION = Duration.millis(350);

    public static final DropShadow SUBTLE_SHADOW_EFFECT = new DropShadow(
            8, Color.rgb(0, 0, 0, 0.12)
    );

    static {
        SUBTLE_SHADOW_EFFECT.setOffsetY(2);
        SUBTLE_SHADOW_EFFECT.setOffsetX(0);
    }

    public static final DropShadow EXPANDED_CARD_SHADOW_EFFECT = new DropShadow(
            25, Color.rgb(0, 0, 0, 0.30)
    );

    static {
        EXPANDED_CARD_SHADOW_EFFECT.setOffsetY(8);
        EXPANDED_CARD_SHADOW_EFFECT.setOffsetX(0);
    }

    // --- Interpoladores ---
    public static final Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0.0, 0.0, 0.2, 1.0);
    public static final Interpolator EASE_IN_OUT_INTERPOLATOR = Interpolator.SPLINE(0.42, 0.0, 0.58, 1.0);

    // CORRECCIÓN: Ajustamos el último punto para que esté dentro del rango [0,1].
    // Este interpolador busca simular un rebote sutil al final.
    // Aunque no es un rebote "físico" que sobrepase 1 y regrese,
    // esta configuración es más robusta y compatible.
    private static final Interpolator BOUNCE_OUT_INTERPOLATOR = Interpolator.SPLINE(0.175, 0.885, 0.320, 1.0);

    private static final Interpolator PRIMARY_MOVEMENT_INTERPOLATOR = EASE_IN_OUT_INTERPOLATOR;
    private static final Interpolator SETTLE_INTERPOLATOR = BOUNCE_OUT_INTERPOLATOR; // Se sigue usando el interpolador corregido.
    private static final Interpolator FADE_INTERPOLATOR = EASE_OUT_INTERPOLATOR;

    public static Timeline createHoverInAnimation(Node cardNode) {
        DropShadow currentEffect = (DropShadow) cardNode.getEffect();
        if (currentEffect == null || currentEffect == SUBTLE_SHADOW_EFFECT) {
            currentEffect = new DropShadow(
                    SUBTLE_SHADOW_EFFECT.getRadius(),
                    SUBTLE_SHADOW_EFFECT.getOffsetY(),
                    SUBTLE_SHADOW_EFFECT.getOffsetX(),
                    SUBTLE_SHADOW_EFFECT.getColor()
            );
            currentEffect.setSpread(SUBTLE_SHADOW_EFFECT.getSpread());
            cardNode.setEffect(currentEffect);
        }

        return new Timeline(new KeyFrame(HOVER_ANIM_DURATION,
                new KeyValue(cardNode.scaleXProperty(), HOVER_SCALE_FACTOR, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.scaleYProperty(), HOVER_SCALE_FACTOR, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.translateYProperty(), HOVER_TRANSLATE_Y_DELTA, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.radiusProperty(), HOVER_SHADOW_RADIUS_VALUE, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.offsetYProperty(), HOVER_SHADOW_OFFSET_Y_VALUE, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.offsetXProperty(), currentEffect.getOffsetX(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.colorProperty(), HOVER_SHADOW_EFFECT_COLOR, EASE_OUT_INTERPOLATOR)
        ));
    }

    public static Timeline createHoverOutAnimation(Node cardNode) {
        DropShadow currentEffect = (DropShadow) cardNode.getEffect();
        if (currentEffect == null) {
            currentEffect = new DropShadow();
            cardNode.setEffect(currentEffect);
        }

        Timeline timeline = new Timeline(new KeyFrame(HOVER_ANIM_DURATION,
                new KeyValue(cardNode.scaleXProperty(), 1.0, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.scaleYProperty(), 1.0, EASE_OUT_INTERPOLATOR),
                new KeyValue(cardNode.translateYProperty(), 0.0, EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.radiusProperty(), SUBTLE_SHADOW_EFFECT.getRadius(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.offsetYProperty(), SUBTLE_SHADOW_EFFECT.getOffsetY(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.offsetXProperty(), SUBTLE_SHADOW_EFFECT.getOffsetX(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.colorProperty(), SUBTLE_SHADOW_EFFECT.getColor(), EASE_OUT_INTERPOLATOR),
                new KeyValue(currentEffect.spreadProperty(), SUBTLE_SHADOW_EFFECT.getSpread(), EASE_OUT_INTERPOLATOR)
        ));

        timeline.setOnFinished(event -> {
            if (cardNode.getEffect() != SUBTLE_SHADOW_EFFECT) {
                cardNode.setEffect(SUBTLE_SHADOW_EFFECT);
            }
        });
        return timeline;
    }

    public static ParallelTransition createExpandAnimation(Node card,
                                                           double targetTranslateX, double targetTranslateY,
                                                           double finalScaleX, double finalScaleY) {

        TranslateTransition move = new TranslateTransition(EXPAND_ANIM_DURATION, card);
        move.setToX(targetTranslateX);
        move.setToY(targetTranslateY);
        move.setInterpolator(PRIMARY_MOVEMENT_INTERPOLATOR);

        FadeTransition fadeIn = new FadeTransition(EXPAND_ANIM_DURATION.multiply(0.7), card);
        fadeIn.setDelay(EXPAND_ANIM_DURATION.multiply(0.1));
        fadeIn.setFromValue(card.getOpacity());
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(FADE_INTERPOLATOR);

        Timeline scaleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.scaleXProperty(), card.getScaleX()),
                        new KeyValue(card.scaleYProperty(), card.getScaleY())
                ),
                new KeyFrame(EXPAND_ANIM_DURATION.multiply(0.75),
                        new KeyValue(card.scaleXProperty(), finalScaleX * 1.05, EASE_IN_OUT_INTERPOLATOR),
                        new KeyValue(card.scaleYProperty(), finalScaleY * 1.05, EASE_IN_OUT_INTERPOLATOR)
                ),
                new KeyFrame(EXPAND_ANIM_DURATION,
                        new KeyValue(card.scaleXProperty(), finalScaleX, SETTLE_INTERPOLATOR),
                        new KeyValue(card.scaleYProperty(), finalScaleY, SETTLE_INTERPOLATOR)
                )
        );

        DropShadow currentEffect = (DropShadow) card.getEffect();
        if (currentEffect == null || currentEffect == SUBTLE_SHADOW_EFFECT) {
            currentEffect = new DropShadow(
                    SUBTLE_SHADOW_EFFECT.getRadius(),
                    SUBTLE_SHADOW_EFFECT.getOffsetY(),
                    SUBTLE_SHADOW_EFFECT.getOffsetX(),
                    SUBTLE_SHADOW_EFFECT.getColor()
            );
            currentEffect.setSpread(SUBTLE_SHADOW_EFFECT.getSpread());
            card.setEffect(currentEffect);
        }

        Timeline shadowAnim = new Timeline(
                new KeyFrame(EXPAND_ANIM_DURATION,
                        new KeyValue(currentEffect.radiusProperty(), EXPANDED_CARD_SHADOW_EFFECT.getRadius(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.offsetXProperty(), EXPANDED_CARD_SHADOW_EFFECT.getOffsetX(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.offsetYProperty(), EXPANDED_CARD_SHADOW_EFFECT.getOffsetY(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.colorProperty(), EXPANDED_CARD_SHADOW_EFFECT.getColor(), PRIMARY_MOVEMENT_INTERPOLATOR),
                        new KeyValue(currentEffect.spreadProperty(), EXPANDED_CARD_SHADOW_EFFECT.getSpread(), PRIMARY_MOVEMENT_INTERPOLATOR)
                )
        );

        return new ParallelTransition(card, move, fadeIn, scaleAnim, shadowAnim);
    }

    public static ParallelTransition createCollapseAnimation(Node card,
                                                             Pane overlayPane,
                                                             Pane originalParent,
                                                             Node placeholder,
                                                             Runnable onAllOperationsCompleted) {

        TranslateTransition moveBack = new TranslateTransition(COLLAPSE_ANIM_DURATION, card);
        moveBack.setToX(0);
        moveBack.setToY(0);
        moveBack.setInterpolator(PRIMARY_MOVEMENT_INTERPOLATOR);

        ScaleTransition scaleBack = new ScaleTransition(COLLAPSE_ANIM_DURATION, card);
        scaleBack.setToX(1.0);
        scaleBack.setToY(1.0);
        scaleBack.setInterpolator(SETTLE_INTERPOLATOR);

        FadeTransition fadeOut = new FadeTransition(COLLAPSE_ANIM_DURATION.multiply(0.7), card);
        fadeOut.setDelay(COLLAPSE_ANIM_DURATION.multiply(0.1));
        fadeOut.setFromValue(card.getOpacity());
        fadeOut.setToValue(0.0);
        fadeOut.setInterpolator(FADE_INTERPOLATOR);

        DropShadow currentEffect = (DropShadow) card.getEffect();
        Timeline shadowCollapseAnim = null;
        if (currentEffect != null) {
            if (currentEffect == EXPANDED_CARD_SHADOW_EFFECT) {
                currentEffect = new DropShadow(
                        EXPANDED_CARD_SHADOW_EFFECT.getRadius(),
                        EXPANDED_CARD_SHADOW_EFFECT.getOffsetY(),
                        EXPANDED_CARD_SHADOW_EFFECT.getOffsetX(),
                        EXPANDED_CARD_SHADOW_EFFECT.getColor()
                );
                currentEffect.setSpread(EXPANDED_CARD_SHADOW_EFFECT.getSpread());
                card.setEffect(currentEffect);
            }

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

        pt.setOnFinished(event -> {
            Platform.runLater(() -> {
                overlayPane.getChildren().remove(card);

                card.setTranslateX(0);
                card.setTranslateY(0);
                card.setScaleX(1.0);
                card.setScaleY(1.0);
                card.setOpacity(1.0);
                card.setEffect(SUBTLE_SHADOW_EFFECT);

                if (originalParent != null) {
                    if (placeholder != null && placeholder.getParent() == originalParent) {
                        int placeholderIndex = originalParent.getChildren().indexOf(placeholder);
                        originalParent.getChildren().remove(placeholder);
                        if (placeholderIndex != -1) {
                            originalParent.getChildren().add(placeholderIndex, card);
                        } else {
                            originalParent.getChildren().add(card);
                        }
                    } else {
                        originalParent.getChildren().add(card);
                    }
                    card.setVisible(true);
                    card.setManaged(true);
                    originalParent.requestLayout();
                } else {
                    System.err.println("CardAnimationHelper: originalParent es null. La tarjeta no se puede reinsertar.");
                }

                if (onAllOperationsCompleted != null) {
                    onAllOperationsCompleted.run();
                }
            });
        });

        return pt;
    }
}