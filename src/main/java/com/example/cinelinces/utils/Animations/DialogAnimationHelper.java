// src/main/java/com/example/cinelinces/utils/Animations/DialogAnimationHelper.java
package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class DialogAnimationHelper {
    private final StackPane    rootStack;
    private final OverlayHelper overlayHelper;

    private Node   currentDialog;
    private Button currentTrigger;

    private static final Duration DURATION_SHOW = Duration.millis(600);
    private static final Duration DURATION_HIDE = Duration.millis(450);
    private static final Interpolator SMOOTH    = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    private static final Interpolator BOUNCE    = Interpolator.SPLINE(0.68, 0.1, 0.265, 0.99);
    private static final Interpolator BACK_EASE = Interpolator.SPLINE(0.68, 0.1, 0.32, 0.9);

    public DialogAnimationHelper(StackPane rootStack, Pane overlayPane) {
        this.rootStack     = rootStack;
        this.overlayHelper = new OverlayHelper(overlayPane);

        // Clic fuera cierra el diálogo
        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (currentDialog != null && currentDialog.isVisible()) {
                hideDialog(currentDialog, currentTrigger);
            }
        });
    }

    /** Construye y devuelve la animación de difuminado de fondo (blur in). */
    public ParallelTransition blurBackgroundIn(Duration duration, double toRadius) {
        ParallelTransition pt = new ParallelTransition();
        for (Node child : rootStack.getChildren()) {
            // excluimos el overlayPane
            if (child != overlayHelper.getOverlay().getParent()) {
                pt.getChildren().add(
                        BlurUtil.blurIn(child, 0, toRadius, duration, SMOOTH)
                );
            }
        }
        return pt;
    }

    /** Construye y devuelve la animación de des-difuminado de fondo (blur out). */
    public ParallelTransition blurBackgroundOut(Duration duration) {
        ParallelTransition pt = new ParallelTransition();
        for (Node child : rootStack.getChildren()) {
            if (child != overlayHelper.getOverlay().getParent()) {
                // asumimos que el efecto actual es GaussianBlur
                pt.getChildren().add(
                        BlurUtil.blurOut(child, ((GaussianBlur)child.getEffect()).getRadius(), 0, duration, SMOOTH)
                );
            }
        }
        // al acabar limpiamos efectos
        pt.setOnFinished(e -> {
            for (Node child : rootStack.getChildren()) {
                if (child != overlayHelper.getOverlay().getParent()) {
                    child.setEffect(null);
                }
            }
        });
        return pt;
    }

    /**
     * Muestra el diálogo con overlay y blur.
     */
    public void showDialog(Node dialog, Button trigger) {
        currentDialog  = dialog;
        currentTrigger = trigger;

        // 1) overlay + blur fondo
        overlayHelper.show(DURATION_SHOW.multiply(0.8), 0.4, SMOOTH);
        blurBackgroundIn(DURATION_SHOW.multiply(0.8), 8).play();

        // 2) Z-order
        overlayHelper.getOverlay().toFront();
        dialog.toFront();

        // 3) posición/escala inicial desde el trigger
        Bounds btnB     = trigger.localToScreen(trigger.getBoundsInLocal());
        Bounds overB    = overlayHelper.getOverlay()
                .localToScreen(overlayHelper.getOverlay().getBoundsInLocal());
        double initX    = btnB.getMinX() - overB.getMinX();
        double initY    = btnB.getMinY() - overB.getMinY();
        double startSX  = trigger.getWidth()  / dialog.prefWidth(-1);
        double startSY  = trigger.getHeight() / dialog.prefHeight(-1);

        dialog.setLayoutX(initX);
        dialog.setLayoutY(initY);
        dialog.setScaleX(startSX);
        dialog.setScaleY(startSY);
        dialog.setOpacity(0);
        dialog.setVisible(true);

        // 4) posición final centrada
        double finalX = (overlayHelper.getOverlay().getWidth()  - dialog.prefWidth(-1)) / 2 - initX;
        double finalY = (overlayHelper.getOverlay().getHeight() - dialog.prefHeight(-1)) / 2 - initY;

        // 5) animaciones de movimiento, pop y fade
        TranslateTransition move   = TransitionFactory.translate(dialog, DURATION_SHOW, finalX, finalY, BACK_EASE);
        ScaleTransition     pop    = TransitionFactory.scale   (dialog, DURATION_SHOW, 0.9, 0.9, 1, 1, Interpolator.EASE_OUT);
        FadeTransition      fadeIn = TransitionFactory.fade    (dialog, DURATION_SHOW.multiply(0.6), 0, 1, DURATION_SHOW.multiply(0.2), SMOOTH);

        // 6) rebote de escala
        Timeline scaleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dialog.scaleXProperty(), startSX),
                        new KeyValue(dialog.scaleYProperty(), startSY)
                ),
                new KeyFrame(DURATION_SHOW.multiply(0.7),
                        new KeyValue(dialog.scaleXProperty(), 1.08, SMOOTH),
                        new KeyValue(dialog.scaleYProperty(), 1.08, SMOOTH)
                ),
                new KeyFrame(DURATION_SHOW,
                        new KeyValue(dialog.scaleXProperty(), 1.0, BOUNCE),
                        new KeyValue(dialog.scaleYProperty(), 1.0, BOUNCE)
                )
        );

        // 7) ejecutar todas las animaciones
        new ParallelTransition(
                new ParallelTransition(move, pop, fadeIn),
                scaleAnim
        ).play();
    }

    /**
     * Oculta el diálogo y revierte overlay y blur.
     */
    public void hideDialog(Node dialog, Button trigger) {
        currentDialog  = dialog;
        currentTrigger = trigger;

        // 1) overlay hide + blur out
        overlayHelper.hide(DURATION_HIDE.multiply(0.8), SMOOTH, ()->{});
        blurBackgroundOut(DURATION_HIDE.multiply(0.8)).play();

        // 2) animación de regreso a trigger
        Bounds btnB  = trigger.localToScreen(trigger.getBoundsInLocal());
        Bounds overB = overlayHelper.getOverlay()
                .localToScreen(overlayHelper.getOverlay().getBoundsInLocal());
        double fx    = btnB.getMinX() - overB.getMinX();
        double fy    = btnB.getMinY() - overB.getMinY();
        double endSX = trigger.getWidth()  / dialog.prefWidth(-1);
        double endSY = trigger.getHeight() / dialog.prefHeight(-1);

        TranslateTransition moveBack = TransitionFactory.translate(
                dialog, DURATION_HIDE, fx - dialog.getLayoutX(), fy - dialog.getLayoutY(), BACK_EASE
        );
        ScaleTransition scaleBack = TransitionFactory.scale(
                dialog, DURATION_HIDE,
                dialog.getScaleX(), dialog.getScaleY(),
                endSX, endSY,
                SMOOTH
        );
        FadeTransition fadeOut = TransitionFactory.fade(
                dialog, DURATION_HIDE.multiply(0.7),
                dialog.getOpacity(), 0,
                DURATION_HIDE.multiply(0.2),
                SMOOTH
        );

        ParallelTransition pt = new ParallelTransition(moveBack, scaleBack, fadeOut);
        pt.setOnFinished(e -> {
            dialog.setVisible(false);
            dialog.setOpacity(1);
            dialog.setScaleX(1);
            dialog.setScaleY(1);
            dialog.setTranslateX(0);
            dialog.setTranslateY(0);
        });
        pt.play();
    }
}
