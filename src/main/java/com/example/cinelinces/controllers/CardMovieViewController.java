package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CardMovieViewController {
    @FXML private VBox cardRoot;
    @FXML private VBox detailsPane;
    @FXML private VBox textContainer;
    @FXML private Label title;
    @FXML private Label subtitle;

    private Pane parentContainer; // Este es el FlowPane o el contenedor original de la tarjeta
    private Pane overlayPane;
    private StackPane rootStack; // Necesitamos el rootStack para aplicar el blur
    private OverlayHelper overlayHelper;
    private DialogAnimationHelper dialogAnimationHelper; // Necesitamos esto para el blur
    private Region placeholder;
    private double origX, origY; // Posición original en coordenadas de la escena
    private double currentXInOverlay, currentYInOverlay; // Posición de la tarjeta en el overlay al momento de expandir
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;

    private static final Duration HOVER_D = Duration.millis(200);
    private static final Duration FADE_INFO_D = Duration.millis(200);

    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.rootStack = rootStack;
        this.overlayHelper = new OverlayHelper(overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper; // Inyectamos el dialogAnimationHelper

        this.overlayHelper.getOverlay()
                .setOnMouseClicked(e -> {
                    if (isExpanded && !isAnimating) collapseCard();
                });

        cardRoot.setEffect(new DropShadow(8, 0, 2, Color.rgb(0, 0, 0, 0.15)));
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        CardAnimationHelper.hoverIn(cardRoot, HOVER_D).play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        CardAnimationHelper.hoverOut(cardRoot, HOVER_D).play();
    }

    @FXML
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) return;

        isAnimating = true;
        if (!isExpanded) expandCard();
        else collapseCard();
    }

    private void expandCard() {
        currentlyExpanded = this;

        // 1) Ocultar el texto original y mostrar los detalles
        FadeTransition(textContainer, detailsPane);

        // 2) blur fondo
        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_DURATION.multiply(0.8), 0.4, CardAnimationHelper.SMOOTH);


        // 3) placeholder + mover al overlay
        Bounds b = cardRoot.localToScene(cardRoot.getBoundsInLocal());
        origX = b.getMinX();
        origY = b.getMinY();
        placeholder = new Region();
        placeholder.setPrefSize(cardRoot.getWidth(), cardRoot.getHeight());
        int idx = parentContainer.getChildren().indexOf(cardRoot);
        parentContainer.getChildren().add(Math.max(idx, 0), placeholder);
        parentContainer.getChildren().remove(cardRoot);

        Point2D pt = overlayPane.sceneToLocal(origX, origY);
        cardRoot.setLayoutX(pt.getX());
        cardRoot.setLayoutY(pt.getY());
        currentXInOverlay = pt.getX(); // Guardamos la posición inicial en el overlay
        currentYInOverlay = pt.getY(); // Guardamos la posición inicial en el overlay

        cardRoot.setOpacity(0); // Start with opacity 0 for the fade-in effect
        overlayPane.getChildren().add(cardRoot);

        // 4) Z-order: la tarjeta debe estar encima del overlay y cualquier otro diálogo
        overlayHelper.getOverlay().toFront();
        cardRoot.toFront();


        // 5) calcular la posición final centrada y escala
        double cardWidth = cardRoot.prefWidth(-1);
        double cardHeight = cardRoot.prefHeight(-1);

        double expandedWidth = cardWidth * 1.5;
        double expandedHeight = cardHeight * 2.5;

        double finalScaleX = expandedWidth / cardWidth;
        double finalScaleY = expandedHeight / cardHeight;

        double finalX = (overlayPane.getWidth() - expandedWidth) / 2;
        double finalY = (overlayPane.getHeight() - expandedHeight) / 2;

        double moveX = finalX - pt.getX();
        double moveY = finalY - pt.getY();

        // 6) ejecutar expansión
        ParallelTransition exp = CardAnimationHelper.expand(
                cardRoot, overlayPane, moveX, moveY, finalScaleX, finalScaleY
        );
        exp.setOnFinished(e -> {
            isAnimating = false;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
            cardRoot.setOpacity(1);
        });
        exp.play();
        isExpanded = true;
    }

    private void collapseCard() {
        // 1) Ocultar los detalles y mostrar el texto original
        FadeTransition(detailsPane, textContainer);

        cardRoot.setOnMouseEntered(this::onCardHover);
        cardRoot.setOnMouseExited(this::onCardHoverExit);

        // 2) blur out y ocultar overlay
        overlayHelper.hide(CardAnimationHelper.COLLAPSE_DURATION.multiply(0.8), CardAnimationHelper.SMOOTH, () -> {
        });
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_DURATION.multiply(0.8)).play();


        // 3) colapsar y reinsertar
        ParallelTransition col = CardAnimationHelper.collapse(
                cardRoot, overlayPane, placeholder, currentXInOverlay, currentYInOverlay, // Pasamos la posición inicial en el overlay
                (DropShadow) cardRoot.getEffect()
        );
        col.setOnFinished(e -> {
            placeholder = null;
            currentlyExpanded = null;
            isAnimating = false;
            isExpanded = false;
        });
        col.play();
    }

    private void FadeTransition(VBox nodeToFadeOut, VBox nodeToFadeIn) {
        FadeTransition fadeOut = new FadeTransition(FADE_INFO_D, nodeToFadeOut);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            nodeToFadeOut.setVisible(false);
            nodeToFadeOut.setManaged(false);

            nodeToFadeIn.setVisible(true);
            nodeToFadeIn.setManaged(true);
            FadeTransition fadeIn = new FadeTransition(FADE_INFO_D, nodeToFadeIn);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public void setMovieData(Movie movie) {
        title.setText(movie.getTitle());
        subtitle.setText("(" + movie.getYear() + ") • " + movie.getDuration() + " min");
        // Asegúrate de que los Labels para detalles también estén inicializados si no lo están ya
        // Por ejemplo:
        // synopsisLabel.setText(movie.getSynopsis());
        // durationLabel.setText("⏱️ Duración: " + movie.getDuration() + " min");
        // genreLabel.setText("🎭 Género: " + movie.getGenre());
        // yearLabel.setText("📅 Año: " + movie.getYear());
        // castLabel.setText("⭐ " + String.join(", ", movie.getCast()));
    }
}