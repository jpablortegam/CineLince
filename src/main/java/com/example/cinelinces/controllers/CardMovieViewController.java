package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
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

    private Pane parentContainer;
    private Pane overlayPane;
    private StackPane rootStack;
    private OverlayHelper overlayHelper;
    private DialogAnimationHelper dialogAnimationHelper;
    private Region placeholder;
    private double origX, origY; // Posición original de la tarjeta en coordenadas de la escena
    private double currentXInOverlay, currentYInOverlay; // Posición layout de la tarjeta cuando está en el overlay
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;

    private static final Duration HOVER_CARD_DURATION = Duration.millis(200);
    private static final Duration FADE_INFO_DURATION = Duration.millis(200);

    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.rootStack = rootStack;
        this.overlayHelper = new OverlayHelper(overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;

        // Click en el overlay para colapsar la tarjeta expandida
        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (isExpanded && !isAnimating && currentlyExpanded == this) {
                // Llama a onCardClick (que a su vez llamará a collapseCard si está expandida)
                // El MouseEvent es null porque es una acción programática
                onCardClick(null);
            }
        });

        // Sombra inicial sutil para la tarjeta
        cardRoot.setEffect(new DropShadow(CardAnimationHelper.SUBTLE_SHADOW_RADIUS, 0, CardAnimationHelper.SUBTLE_SHADOW_OFFSETY, CardAnimationHelper.SUBTLE_SHADOW_COLOR));
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        CardAnimationHelper.hoverIn(cardRoot, HOVER_CARD_DURATION).play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        CardAnimationHelper.hoverOut(cardRoot, HOVER_CARD_DURATION).play();
    }

    @FXML
    void onCardClick(MouseEvent e) { // e puede ser null si es llamado programáticamente
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) {
            // Hay otra tarjeta expandida, no hacer nada con esta.
            return;
        }

        isAnimating = true; // Bloquear interacciones múltiples
        if (!isExpanded) {
            expandCard();
        } else {
            collapseCard();
        }
    }

    private void expandCard() {
        currentlyExpanded = this; // Marcar esta tarjeta como la expandida
        performFadeTransition(textContainer, detailsPane); // Cambiar contenido interno

        // Efectos de fondo
        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_DURATION.multiply(0.8), 0.4, CardAnimationHelper.SMOOTH);

        // Guardar posición original y crear placeholder
        Bounds cardBoundsInScene = cardRoot.localToScene(cardRoot.getBoundsInLocal());
        origX = cardBoundsInScene.getMinX();
        origY = cardBoundsInScene.getMinY();

        placeholder = new Region();
        placeholder.setPrefSize(cardRoot.getWidth(), cardRoot.getHeight());
        placeholder.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        placeholder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Mover tarjeta del parentContainer al overlayPane
        // Asegurarse de que la tarjeta no esté ya en el overlayPane (defensivo)
        if (cardRoot.getParent() == overlayPane) {
            overlayPane.getChildren().remove(cardRoot);
        }
        // Remover de parentContainer si es su padre actual
        if (cardRoot.getParent() == parentContainer) {
            int idx = parentContainer.getChildren().indexOf(cardRoot);
            parentContainer.getChildren().remove(cardRoot);
            parentContainer.getChildren().add(Math.max(idx, 0), placeholder);
        } else if (cardRoot.getParent() != null) { // Si tiene otro padre, remover
            ((Pane)cardRoot.getParent()).getChildren().remove(cardRoot);
        }


        Point2D cardPositionInOverlay = overlayPane.sceneToLocal(origX, origY);
        cardRoot.setLayoutX(cardPositionInOverlay.getX());
        cardRoot.setLayoutY(cardPositionInOverlay.getY());
        currentXInOverlay = cardPositionInOverlay.getX(); // Guardar para el colapso (aunque no se usa activamente)
        currentYInOverlay = cardPositionInOverlay.getY(); // Guardar para el colapso (aunque no se usa activamente)

        cardRoot.setOpacity(0); // Inicia invisible para el fadeIn
        if (!overlayPane.getChildren().contains(cardRoot)) { // Añadir al overlay solo si no está ya
            overlayPane.getChildren().add(cardRoot);
        }


        overlayHelper.getOverlay().toFront(); // Overlay semitransparente
        cardRoot.toFront(); // Tarjeta encima del overlay

        // Calcular transformación para centrar y escalar
        double cardOriginalWidth = cardRoot.getWidth();
        double cardOriginalHeight = cardRoot.getHeight();

        double targetScaleXFactor = 1.5;
        double targetScaleYFactor = 2.0;

        if (cardOriginalWidth <= 0) cardOriginalWidth = 150; // Fallback razonable
        if (cardOriginalHeight <= 0) cardOriginalHeight = 225; // Fallback razonable

        double expandedVisualWidth = cardOriginalWidth * targetScaleXFactor;
        double expandedVisualHeight = cardOriginalHeight * targetScaleYFactor;

        double finalCenteredLayoutX = (overlayPane.getWidth() - expandedVisualWidth) / 2;
        double finalCenteredLayoutY = (overlayPane.getHeight() - expandedVisualHeight) / 2;

        // deltaX/Y son para la TranslateTransition, desde su layoutX/Y actual en el overlay
        double deltaX = finalCenteredLayoutX - cardRoot.getLayoutX();
        double deltaY = finalCenteredLayoutY - cardRoot.getLayoutY();

        ParallelTransition exp = CardAnimationHelper.expand(
                cardRoot, overlayPane, deltaX, deltaY, targetScaleXFactor, targetScaleYFactor
        );
        exp.setOnFinished(event -> {
            isAnimating = false; // Animación de expansión terminada
            isExpanded = true;   // Estado actualizado
            cardRoot.setOnMouseEntered(null); // Deshabilitar hover mientras está expandida
            cardRoot.setOnMouseExited(null);
        });
        exp.play();
    }

    private void collapseCard() {
        performFadeTransition(detailsPane, textContainer); // Revertir contenido

        // Restaurar handlers de hover
        cardRoot.setOnMouseEntered(this::onCardHover);
        cardRoot.setOnMouseExited(this::onCardHoverExit);

        // Efectos de fondo
        overlayHelper.hide(CardAnimationHelper.COLLAPSE_DURATION.multiply(0.8), CardAnimationHelper.SMOOTH, () -> {});
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_DURATION.multiply(0.8)).play();

        // Sombra final para la tarjeta colapsada
        DropShadow subtleShadow = new DropShadow(CardAnimationHelper.SUBTLE_SHADOW_RADIUS, 0, CardAnimationHelper.SUBTLE_SHADOW_OFFSETY, CardAnimationHelper.SUBTLE_SHADOW_COLOR);

        ParallelTransition col = CardAnimationHelper.collapse(
                cardRoot,
                overlayPane,
                parentContainer,
                placeholder,
                currentXInOverlay, // No se usa activamente si la traslación es a (0,0)
                currentYInOverlay, // No se usa activamente si la traslación es a (0,0)
                subtleShadow,
                () -> { // Callback ejecutado DESPUÉS de todas las operaciones de UI en collapse
                    this.placeholder = null; // Limpiar referencia
                    CardMovieViewController.currentlyExpanded = null; // Ya no hay tarjeta expandida
                    this.isAnimating = false; // Animación de colapso completada
                    this.isExpanded = false;  // Estado actualizado
                }
        );
        col.play();
    }

    private void performFadeTransition(VBox nodeToFadeOut, VBox nodeToFadeIn) {
        FadeTransition fadeOutTransition = new FadeTransition(FADE_INFO_DURATION, nodeToFadeOut);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(e -> {
            nodeToFadeOut.setVisible(false);
            nodeToFadeOut.setManaged(false);

            nodeToFadeIn.setVisible(true);
            nodeToFadeIn.setManaged(true);
            FadeTransition fadeInTransition = new FadeTransition(FADE_INFO_DURATION, nodeToFadeIn);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            fadeInTransition.play();
        });
        fadeOutTransition.play();
    }

    public void setMovieData(Movie movie) {
        title.setText(movie.getTitle());
        subtitle.setText("(" + movie.getYear() + ") • " + movie.getDuration() + " min");
    }
}