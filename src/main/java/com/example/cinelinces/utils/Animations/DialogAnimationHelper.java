package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.application.Platform; // Importar Platform
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene; // No debería ser necesario con Platform.runLater
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class DialogAnimationHelper {
    private final StackPane rootStack;
    private final OverlayHelper overlayHelper;

    private Node currentDialog;
    private Button currentTrigger;

    private static final Duration DURATION_SHOW = Duration.millis(380);
    private static final Duration DURATION_HIDE = Duration.millis(280);

    private static final Interpolator SMOOTH = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    private static final Interpolator SETTLE_INTERPOLATOR = Interpolator.EASE_OUT;
    private static final Interpolator MOVEMENT_INTERPOLATOR = SMOOTH;

    public DialogAnimationHelper(StackPane rootStack, Pane overlayPane) {
        this.rootStack = rootStack;
        this.overlayHelper = new OverlayHelper(overlayPane);

        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (currentDialog != null && currentDialog.isVisible()) {
                hideDialog(currentDialog, currentTrigger);
            }
        });
    }

    public ParallelTransition blurBackgroundIn(Duration duration, double toRadius) {
        ParallelTransition pt = new ParallelTransition();
        Node overlayParent = overlayHelper.getOverlay().getParent();
        for (Node child : rootStack.getChildrenUnmodifiable()) {
            if (child != overlayParent && child != overlayHelper.getOverlay()) {
                if (child.getEffect() == null) {
                    child.setEffect(new GaussianBlur(0));
                }
                if (child.getEffect() instanceof GaussianBlur) {
                    pt.getChildren().add(
                            new Timeline(new KeyFrame(duration, new KeyValue(((GaussianBlur) child.getEffect()).radiusProperty(), toRadius, SMOOTH)))
                    );
                }
            }
        }
        return pt;
    }

    public ParallelTransition blurBackgroundOut(Duration duration) {
        ParallelTransition pt = new ParallelTransition();
        Node overlayParent = overlayHelper.getOverlay().getParent();
        for (Node child : rootStack.getChildrenUnmodifiable()) {
            if (child != overlayParent && child != overlayHelper.getOverlay() && child.getEffect() instanceof GaussianBlur) {
                pt.getChildren().add(
                        new Timeline(new KeyFrame(duration, new KeyValue(((GaussianBlur) child.getEffect()).radiusProperty(), 0, SMOOTH)))
                );
            }
        }
        pt.setOnFinished(e -> {
            for (Node child : rootStack.getChildrenUnmodifiable()) {
                if (child != overlayParent && child != overlayHelper.getOverlay() && child.getEffect() instanceof GaussianBlur) {
                    child.setEffect(null);
                }
            }
        });
        return pt;
    }


    public void showDialog(Node dialog, Button trigger) {
        currentDialog = dialog;
        currentTrigger = trigger;

        overlayHelper.show(DURATION_SHOW.multiply(0.8), 0.4, SMOOTH);
        blurBackgroundIn(DURATION_SHOW.multiply(0.8), 8).play();

        dialog.setOpacity(0);
        dialog.setVisible(true);

        Platform.runLater(() -> {
            overlayHelper.getOverlay().toFront();
            dialog.toFront();
            double dialogPrefWidth = dialog.prefWidth(-1);
            double dialogPrefHeight = dialog.prefHeight(-1);
            if (dialogPrefWidth <= 0) dialogPrefWidth = 200;
            if (dialogPrefHeight <= 0) dialogPrefHeight = 150;

            Bounds btnBoundsScreen = trigger.localToScreen(trigger.getBoundsInLocal());
            Bounds overlayViewBounds = overlayHelper.getOverlay().getBoundsInLocal();
            Point2D overlayScreenCoords = overlayHelper.getOverlay().localToScreen(0, 0);

            if (btnBoundsScreen == null || overlayScreenCoords == null) {
                dialog.setVisible(false);
                overlayHelper.hide(Duration.millis(50), SMOOTH, () -> {
                });
                blurBackgroundOut(Duration.millis(50)).play();
                return;
            }
            double initLayoutX = (btnBoundsScreen.getMinX() + btnBoundsScreen.getWidth() / 2) - overlayScreenCoords.getX() - (dialogPrefWidth / 2);
            double initLayoutY = (btnBoundsScreen.getMinY() + btnBoundsScreen.getHeight() / 2) - overlayScreenCoords.getY() - (dialogPrefHeight / 2);

            double startScaleX = trigger.getWidth() / dialogPrefWidth;
            double startScaleY = trigger.getHeight() / dialogPrefHeight;

            dialog.setLayoutX(initLayoutX);
            dialog.setLayoutY(initLayoutY);
            dialog.setScaleX(startScaleX);
            dialog.setScaleY(startScaleY);

            double finalCenteredLayoutX = (overlayHelper.getOverlay().getWidth() - dialogPrefWidth) / 2;
            double finalCenteredLayoutY = (overlayHelper.getOverlay().getHeight() - dialogPrefHeight) / 2;
            double deltaTranslateX = finalCenteredLayoutX - dialog.getLayoutX();
            double deltaTranslateY = finalCenteredLayoutY - dialog.getLayoutY();

            TranslateTransition move = TransitionFactory.translate(dialog, DURATION_SHOW, deltaTranslateX, deltaTranslateY, MOVEMENT_INTERPOLATOR);
            FadeTransition fadeIn = TransitionFactory.fade(dialog, DURATION_SHOW.multiply(0.7), 0, 1, DURATION_SHOW.multiply(0.15), SMOOTH);

            Timeline scaleAnim = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dialog.scaleXProperty(), startScaleX),
                            new KeyValue(dialog.scaleYProperty(), startScaleY)
                    ),
                    new KeyFrame(DURATION_SHOW.multiply(0.75),
                            new KeyValue(dialog.scaleXProperty(), 1.0 * 1.05, SMOOTH),
                            new KeyValue(dialog.scaleYProperty(), 1.0 * 1.05, SMOOTH)
                    ),
                    new KeyFrame(DURATION_SHOW,
                            new KeyValue(dialog.scaleXProperty(), 1.0, SETTLE_INTERPOLATOR),
                            new KeyValue(dialog.scaleYProperty(), 1.0, SETTLE_INTERPOLATOR)
                    )
            );

            new ParallelTransition(dialog, move, fadeIn, scaleAnim).play();
        });
    }


    public void hideDialog(Node dialog, Button trigger) {
        if (dialog == null && currentDialog != null) dialog = currentDialog;
        if (trigger == null && currentTrigger != null) trigger = currentTrigger;
        if (dialog == null || trigger == null) {
            System.err.println("hideDialog: dialog o trigger es null.");
            return;
        }

        overlayHelper.hide(DURATION_HIDE.multiply(0.8), SMOOTH, () -> {
        });
        blurBackgroundOut(DURATION_HIDE.multiply(0.8)).play();

        double dialogPrefWidth = dialog.prefWidth(-1);
        double dialogPrefHeight = dialog.prefHeight(-1);
        if (dialogPrefWidth <= 0) dialogPrefWidth = 200;
        if (dialogPrefHeight <= 0) dialogPrefHeight = 150;

        Bounds btnBoundsScreen = trigger.localToScreen(trigger.getBoundsInLocal());
        Point2D overlayScreenCoords = overlayHelper.getOverlay().localToScreen(0, 0);

        if (btnBoundsScreen == null || overlayScreenCoords == null) {
            dialog.setVisible(false);
            return;
        }

        double finalTargetLayoutX = (btnBoundsScreen.getMinX() + btnBoundsScreen.getWidth() / 2) - overlayScreenCoords.getX() - (dialogPrefWidth / 2);
        double finalTargetLayoutY = (btnBoundsScreen.getMinY() + btnBoundsScreen.getHeight() / 2) - overlayScreenCoords.getY() - (dialogPrefHeight / 2);
        double targetTranslateX = finalTargetLayoutX - dialog.getLayoutX();
        double targetTranslateY = finalTargetLayoutY - dialog.getLayoutY();
        double endScaleX = trigger.getWidth() / dialogPrefWidth;
        double endScaleY = trigger.getHeight() / dialogPrefHeight;

        TranslateTransition moveBack = TransitionFactory.translate(
                dialog, DURATION_HIDE,
                targetTranslateX,
                targetTranslateY,
                MOVEMENT_INTERPOLATOR
        );

        ScaleTransition scaleBack = TransitionFactory.scale(
                dialog, DURATION_HIDE,
                dialog.getScaleX(), dialog.getScaleY(),
                endScaleX, endScaleY,
                SMOOTH
        );

        FadeTransition fadeOut = TransitionFactory.fade(
                dialog, DURATION_HIDE.multiply(0.7),
                dialog.getOpacity(), 0,
                DURATION_HIDE.multiply(0.1),
                SMOOTH
        );

        final Node finalDialog = dialog;
        ParallelTransition pt = new ParallelTransition(finalDialog, moveBack, scaleBack, fadeOut);
        pt.setOnFinished(e -> {
            finalDialog.setVisible(false);
            finalDialog.setOpacity(1);
            finalDialog.setScaleX(1);
            finalDialog.setScaleY(1);
            finalDialog.setTranslateX(0);
            finalDialog.setTranslateY(0);
        });
        pt.play();
    }
}