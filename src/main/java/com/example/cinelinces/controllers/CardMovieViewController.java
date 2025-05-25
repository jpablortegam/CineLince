package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.BlurUtil;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import com.example.cinelinces.utils.Animations.TransitionFactory;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

// … tus imports habituales …

public class CardMovieViewController {
    @FXML private VBox cardRoot;
    // … otros FXML …

    private Pane parentContainer, overlayPane;
    private OverlayHelper overlayHelper;
    private Region placeholder;
    private double origX, origY;
    private boolean isExpanded=false, isAnimating=false;
    private static CardMovieViewController currentlyExpanded=null;

    // constantes de duración…
    private static final Duration HOVER_D = Duration.millis(200);
    // … etc …

    public void initContext(Pane parentContainer, Pane overlayPane) {
        this.parentContainer = parentContainer;
        this.overlayPane     = overlayPane;
        this.overlayHelper   = new OverlayHelper(overlayPane);

        // click en overlay cierra la tarjeta expandida
        this.overlayHelper.getOverlay()
                .setOnMouseClicked(e -> {
                    if (isExpanded && !isAnimating) collapseCard();
                });

        // sombra inicial
        cardRoot.setEffect(new DropShadow(8, 0, 2, Color.rgb(0,0,0,0.15)));
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded||isAnimating||(currentlyExpanded!=null&&currentlyExpanded!=this)) return;
        CardAnimationHelper.hoverIn(cardRoot, HOVER_D).play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded||isAnimating||(currentlyExpanded!=null&&currentlyExpanded!=this)) return;
        CardAnimationHelper.hoverOut(cardRoot, HOVER_D).play();
    }

    @FXML
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded!=null && currentlyExpanded!=this) return;

        isAnimating = true;
        if (!isExpanded) expandCard();
        else            collapseCard();
    }

    private void expandCard() {
        currentlyExpanded = this;

        // placeholder + mover al overlay
        Bounds b = cardRoot.localToScene(cardRoot.getBoundsInLocal());
        origX = b.getMinX(); origY = b.getMinY();
        placeholder = new Region();
        placeholder.setPrefSize(cardRoot.getWidth(), cardRoot.getHeight());
        int idx = parentContainer.getChildren().indexOf(cardRoot);
        parentContainer.getChildren().add(idx>=0?idx:0, placeholder);
        parentContainer.getChildren().remove(cardRoot);

        Point2D pt = overlayPane.sceneToLocal(origX, origY);
        cardRoot.setLayoutX(pt.getX());
        cardRoot.setLayoutY(pt.getY());
        overlayPane.getChildren().add(cardRoot);

        // mostrar overlay + blur SOLO en parentContainer
        overlayHelper.show(Duration.millis(400), 0.4, Interpolator.EASE_BOTH);
        BlurUtil.blurIn(parentContainer, 0, 12, Duration.millis(400), Interpolator.EASE_BOTH).play();

        // ejecutar expansión centrada
        ParallelTransition exp = CardAnimationHelper.expand(
                cardRoot, overlayPane, 3.0, 2.4
        );
        exp.setOnFinished(e -> {
            isAnimating = false;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        exp.play();
        isExpanded = true;
    }

    private void collapseCard() {
        // reactivar hover
        cardRoot.setOnMouseEntered(this::onCardHover);
        cardRoot.setOnMouseExited(this::onCardHoverExit);

        // ocultar overlay + blur out
        overlayHelper.hide(Duration.millis(350), Interpolator.EASE_BOTH, () -> {/*nada*/});
        BlurUtil.blurOut(parentContainer, 12, 0, Duration.millis(350), Interpolator.EASE_BOTH).play();

        // colapsar y reinsertar
        ParallelTransition col = CardAnimationHelper.collapse(
                cardRoot, overlayPane, placeholder, origX, origY,
                (DropShadow)cardRoot.getEffect()
        );
        col.setOnFinished(e -> {
            parentContainer.setEffect(null);
            placeholder = null;
            currentlyExpanded = null;
            isAnimating = false;
            isExpanded = false;
        });
        col.play();
    }

    public void setMovieData(Movie movie) {
    }

    // … resto de tu clase (setMovieData, resetCard, etc.) …
}
