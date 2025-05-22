package com.example.cinelinces.utils;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Maneja la animación de mostrar/ocultar diálogos con blur y overlay.
 */
public class DialogAnimationHelper {
    private final StackPane rootStack;
    private final Pane overlayPane;
    private final Rectangle backgroundOverlay = new Rectangle();
    private final GaussianBlur backgroundBlur = new GaussianBlur(0);
    private Node currentDialog;
    private Button currentTrigger;

    private static final Duration DURATION_SHOW = Duration.millis(600);
    private static final Duration DURATION_HIDE = Duration.millis(450);
    private static final Interpolator SMOOTH = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    private static final Interpolator BOUNCE = Interpolator.SPLINE(0.68, 0.1, 0.265, 0.99);
    private static final Interpolator BACK_EASE = Interpolator.SPLINE(0.68, 0.1, 0.32, 0.9);

    public DialogAnimationHelper(StackPane rootStack, Pane overlayPane) {
        this.rootStack = rootStack;
        this.overlayPane = overlayPane;
        setupOverlay();
    }

    private void setupOverlay() {
        backgroundOverlay.setFill(Color.BLACK);
        backgroundOverlay.setOpacity(0);
        backgroundOverlay.setVisible(false);
        backgroundOverlay.widthProperty().bind(overlayPane.widthProperty());
        backgroundOverlay.heightProperty().bind(overlayPane.heightProperty());
        backgroundOverlay.setOnMouseClicked(e -> {
            if (currentDialog != null && currentDialog.isVisible()) {
                hideDialog(currentDialog, currentTrigger);
            }
        });
        overlayPane.getChildren().add(0, backgroundOverlay);
    }

    public void showDialog(Node dialog, Button trigger) {
        // asegurar overlay encima de todo
        overlayPane.toFront();
        dialog.toFront();
        currentDialog = dialog;
        currentTrigger = trigger;
        dialog.toFront();
        // blur al fondo
        backgroundBlur.setRadius(0);
        rootStack.getChildren().stream()
                .filter(n -> n != overlayPane)
                .forEach(n -> n.setEffect(backgroundBlur));
        // overlay fade in
        backgroundOverlay.setVisible(true);
        FadeTransition fadeInBg = new FadeTransition(DURATION_SHOW.multiply(0.8), backgroundOverlay);
        fadeInBg.setFromValue(0);
        fadeInBg.setToValue(0.4);
        fadeInBg.setInterpolator(SMOOTH);
        // blur timeline
        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(backgroundBlur.radiusProperty(), 0)),
                new KeyFrame(DURATION_SHOW.multiply(0.8), new KeyValue(backgroundBlur.radiusProperty(), 8, SMOOTH))
        );
        // posición inicial y escala según botón
        Bounds btnB = trigger.localToScreen(trigger.getBoundsInLocal());
        Bounds parentB = overlayPane.localToScreen(overlayPane.getBoundsInLocal());
        double initX = btnB.getMinX() - parentB.getMinX();
        double initY = btnB.getMinY() - parentB.getMinY();
        double sx = trigger.getWidth()  / dialog.prefWidth(-1);
        double sy = trigger.getHeight() / dialog.prefHeight(-1);
        dialog.setLayoutX(initX);
        dialog.setLayoutY(initY);
        dialog.setScaleX(sx);
        dialog.setScaleY(sy);
        dialog.setOpacity(0);
        dialog.setVisible(true);
        // posición final (centro)
        double finalX = (overlayPane.getWidth()  - dialog.prefWidth(-1)) / 2;
        double finalY = (overlayPane.getHeight() - dialog.prefHeight(-1)) / 2;
        // translate + scale
        TranslateTransition move = new TranslateTransition(DURATION_SHOW, dialog);
        move.setToX(finalX - initX);
        move.setToY(finalY - initY);
        move.setInterpolator(BACK_EASE);
        Timeline scaleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dialog.scaleXProperty(), sx),
                        new KeyValue(dialog.scaleYProperty(), sy)),
                new KeyFrame(DURATION_SHOW.multiply(0.7),
                        new KeyValue(dialog.scaleXProperty(), 1.08, SMOOTH),
                        new KeyValue(dialog.scaleYProperty(), 1.08, SMOOTH)),
                new KeyFrame(DURATION_SHOW,
                        new KeyValue(dialog.scaleXProperty(), 1.0, BOUNCE),
                        new KeyValue(dialog.scaleYProperty(), 1.0, BOUNCE))
        );
        // fade in dialog
        FadeTransition fadeInDlg = new FadeTransition(DURATION_SHOW.multiply(0.6), dialog);
        fadeInDlg.setFromValue(0);
        fadeInDlg.setToValue(1);
        fadeInDlg.setDelay(DURATION_SHOW.multiply(0.2));
        fadeInDlg.setInterpolator(SMOOTH);
        // pop
        ScaleTransition pop = new ScaleTransition(DURATION_SHOW, dialog);
        pop.setFromX(0.9);
        pop.setFromY(0.9);
        pop.setToX(1.0);
        pop.setToY(1.0);
        pop.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition main = new ParallelTransition(move, fadeInDlg, fadeInBg, pop);
        ParallelTransition all = new ParallelTransition(main, blurIn, scaleAnim);
        all.play();
    }

    public void hideDialog(Node dialog, Button trigger) {
        // blur out
        Timeline blurOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(backgroundBlur.radiusProperty(), 8)),
                new KeyFrame(DURATION_HIDE.multiply(0.8), new KeyValue(backgroundBlur.radiusProperty(), 0, SMOOTH))
        );
        FadeTransition fadeOutBg = new FadeTransition(DURATION_HIDE.multiply(0.8), backgroundOverlay);
        fadeOutBg.setToValue(0);
        fadeOutBg.setInterpolator(SMOOTH);
        fadeOutBg.setOnFinished(e -> {
            backgroundOverlay.setVisible(false);
            rootStack.getChildren().stream()
                    .filter(n -> n != overlayPane)
                    .forEach(n -> n.setEffect(null));
        });
        // regresar al botón
        Bounds btnB = trigger.localToScreen(trigger.getBoundsInLocal());
        Bounds parentB = overlayPane.localToScreen(overlayPane.getBoundsInLocal());
        double fx = btnB.getMinX() - parentB.getMinX();
        double fy = btnB.getMinY() - parentB.getMinY();
        double sx = trigger.getWidth()  / dialog.prefWidth(-1);
        double sy = trigger.getHeight() / dialog.prefHeight(-1);
        TranslateTransition move = new TranslateTransition(DURATION_HIDE, dialog);
        move.setToX(fx - dialog.getLayoutX());
        move.setToY(fy - dialog.getLayoutY());
        move.setInterpolator(BACK_EASE);
        ScaleTransition scale = new ScaleTransition(DURATION_HIDE, dialog);
        scale.setToX(sx);
        scale.setToY(sy);
        scale.setInterpolator(SMOOTH);
        FadeTransition fadeOutDlg = new FadeTransition(DURATION_HIDE.multiply(0.7), dialog);
        fadeOutDlg.setToValue(0);
        fadeOutDlg.setDelay(DURATION_HIDE.multiply(0.2));
        fadeOutDlg.setInterpolator(SMOOTH);
        ParallelTransition pt = new ParallelTransition(move, scale, fadeOutDlg, fadeOutBg);
        pt.setOnFinished(e -> {
            dialog.setVisible(false);
            dialog.setOpacity(1);
            dialog.setScaleX(1);
            dialog.setScaleY(1);
            dialog.setTranslateX(0);
            dialog.setTranslateY(0);
        });
        ParallelTransition all = new ParallelTransition(pt, blurOut);
        all.play();
    }
}