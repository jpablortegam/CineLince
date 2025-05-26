package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.Movie; // Podrías mantenerlo si aún usas Movie para algo

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class CardMovieViewController {
    @FXML private VBox cardRoot;
    @FXML private ImageView poster;
    @FXML private VBox detailsPane;
    @FXML private VBox textContainer;
    @FXML private Label title;
    @FXML private Label subtitle;

    // Labels para el panel de detalles
    @FXML private Label synopsisLabel;
    @FXML private Label durationLabel;
    @FXML private Label genreLabel;
    @FXML private Label yearLabel; // Podría ser Fecha de Estreno o Fecha de Función
    @FXML private Label castLabel; // El reparto es más complejo de obtener con la consulta actual

    private Pane parentContainer;
    private Pane overlayPane;
    private StackPane rootStack;
    private OverlayHelper overlayHelper;
    private DialogAnimationHelper dialogAnimationHelper;
    private Region placeholder;
    private double origX, origY;
    private double currentXInOverlay, currentYInOverlay;
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;

    private static final Duration HOVER_CARD_DURATION = Duration.millis(200);
    private static final Duration FADE_INFO_DURATION = Duration.millis(200);

    private FuncionDetallada currentFuncion; // Para guardar la función actual

    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.rootStack = rootStack;
        this.overlayHelper = new OverlayHelper(overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;

        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (isExpanded && !isAnimating && currentlyExpanded == this) {
                onCardClick(null);
            }
        });
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
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) {
            return;
        }
        isAnimating = true;
        if (!isExpanded) {
            expandCard();
        } else {
            collapseCard();
        }
    }

    private void expandCard() {
        currentlyExpanded = this;
        performFadeTransition(textContainer, detailsPane);

        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_DURATION.multiply(0.8), 0.4, CardAnimationHelper.SMOOTH);

        Bounds cardBoundsInScene = cardRoot.localToScene(cardRoot.getBoundsInLocal());
        origX = cardBoundsInScene.getMinX();
        origY = cardBoundsInScene.getMinY();

        placeholder = new Region();
        placeholder.setPrefSize(cardRoot.getWidth(), cardRoot.getHeight());
        placeholder.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        placeholder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        if (cardRoot.getParent() == overlayPane) {
            overlayPane.getChildren().remove(cardRoot);
        }
        if (cardRoot.getParent() == parentContainer) {
            int idx = parentContainer.getChildren().indexOf(cardRoot);
            parentContainer.getChildren().remove(cardRoot);
            if (idx >= 0) parentContainer.getChildren().add(idx, placeholder);
            else parentContainer.getChildren().add(placeholder);
        } else if (cardRoot.getParent() != null) {
            ((Pane)cardRoot.getParent()).getChildren().remove(cardRoot);
        }

        Point2D cardPositionInOverlay = overlayPane.sceneToLocal(origX, origY);
        cardRoot.setLayoutX(cardPositionInOverlay.getX());
        cardRoot.setLayoutY(cardPositionInOverlay.getY());
        currentXInOverlay = cardPositionInOverlay.getX();
        currentYInOverlay = cardPositionInOverlay.getY();

        cardRoot.setOpacity(0);
        if (!overlayPane.getChildren().contains(cardRoot)) {
            overlayPane.getChildren().add(cardRoot);
        }
        overlayHelper.getOverlay().toFront();
        cardRoot.toFront();

        double cardOriginalWidth = cardRoot.getWidth();
        double cardOriginalHeight = cardRoot.getHeight();
        double targetScaleXFactor = 1.5;
        double targetScaleYFactor = 1.8; // Ajustado para más altura en detalles

        if (cardOriginalWidth <= 0) cardOriginalWidth = 150;
        if (cardOriginalHeight <= 0) cardOriginalHeight = 225;

        double expandedVisualWidth = cardOriginalWidth * targetScaleXFactor;
        double expandedVisualHeight = cardOriginalHeight * targetScaleYFactor;
        double finalCenteredLayoutX = (overlayPane.getWidth() - expandedVisualWidth) / 2;
        double finalCenteredLayoutY = (overlayPane.getHeight() - expandedVisualHeight) / 2;
        double deltaX = finalCenteredLayoutX - cardRoot.getLayoutX();
        double deltaY = finalCenteredLayoutY - cardRoot.getLayoutY();

        ParallelTransition exp = CardAnimationHelper.expand(
                cardRoot, overlayPane, deltaX, deltaY, targetScaleXFactor, targetScaleYFactor
        );
        exp.setOnFinished(event -> {
            isAnimating = false;
            isExpanded = true;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        exp.play();
    }

    private void collapseCard() {
        performFadeTransition(detailsPane, textContainer);
        cardRoot.setOnMouseEntered(this::onCardHover);
        cardRoot.setOnMouseExited(this::onCardHoverExit);

        overlayHelper.hide(CardAnimationHelper.COLLAPSE_DURATION.multiply(0.8), CardAnimationHelper.SMOOTH, () -> {});
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_DURATION.multiply(0.8)).play();

        DropShadow subtleShadow = new DropShadow(CardAnimationHelper.SUBTLE_SHADOW_RADIUS, 0, CardAnimationHelper.SUBTLE_SHADOW_OFFSETY, CardAnimationHelper.SUBTLE_SHADOW_COLOR);

        ParallelTransition col = CardAnimationHelper.collapse(
                cardRoot, overlayPane, parentContainer, placeholder,
                currentXInOverlay, currentYInOverlay, subtleShadow,
                () -> {
                    this.placeholder = null;
                    CardMovieViewController.currentlyExpanded = null;
                    this.isAnimating = false;
                    this.isExpanded = false;
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

    // Método adaptado para FuncionDetallada
    public void setFuncionData(FuncionDetallada funcion) {
        this.currentFuncion = funcion; // Guardar la función actual

        title.setText(funcion.getTituloPelicula());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm 'hrs'");
        subtitle.setText(funcion.getFechaHoraFuncion().format(timeFormatter) + " • Sala " + funcion.getNumeroSala() + " (" + funcion.getTipoSala() + ")");

        // Cargar póster
        if (funcion.getFotografiaPelicula() != null && !funcion.getFotografiaPelicula().isEmpty()) {
            // Asumimos que la ruta es relativa a /resources o una URL completa
            // Ejemplo: si las imágenes están en src/main/resources/images/posters/
            String imagePath = funcion.getFotografiaPelicula();
            if (!imagePath.startsWith("http") && !imagePath.startsWith("/")) {
                imagePath = "/com/example/images/" + imagePath; // Ajusta esta ruta base según tu estructura
            }
            try {
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream != null) {
                    poster.setImage(new Image(imageStream));
                } else {
                    System.err.println("No se pudo cargar la imagen del póster: " + imagePath + " para " + funcion.getTituloPelicula());
                    // Considera una imagen placeholder
                    // poster.setImage(new Image(getClass().getResourceAsStream("/images/placeholder_poster.png")));
                }
            } catch (Exception e) {
                System.err.println("Excepción al cargar imagen: " + imagePath + " - " + e.getMessage());
            }
        } else {
            // poster.setImage(new Image(getClass().getResourceAsStream("/images/placeholder_poster.png")));
            System.err.println("Ruta de fotografía nula o vacía para: " + funcion.getTituloPelicula());
        }


        // Llenar el panel de detalles (visible al expandir)
        synopsisLabel.setText(funcion.getSinopsisPelicula() != null ? funcion.getSinopsisPelicula() : "Sinopsis no disponible.");
        durationLabel.setText("⏱️ Duración: " + funcion.getDuracionMinutos() + " min");
        genreLabel.setText("🎭 Género: " + (funcion.getNombreTipoPelicula() != null ? funcion.getNombreTipoPelicula() : "No especificado"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        yearLabel.setText("📅 Estreno Película: " + (funcion.getFechaEstrenoPelicula() != null ? funcion.getFechaEstrenoPelicula().format(dateFormatter) : "N/A"));

        // El reparto requiere una lógica más compleja (otra consulta o datos en FuncionDetallada)
        castLabel.setText("⭐ Reparto no disponible en esta vista.");
    }
}
