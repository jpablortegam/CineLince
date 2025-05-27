package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.FuncionDetallada;
// import com.example.cinelinces.model.Movie; // Podrías mantenerlo si aún usas Movie para algo

import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
// Quitamos la importación de DropShadow aquí si CardAnimationHelper lo maneja todo
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color; // No es necesario si las sombras se definen en el Helper
import javafx.util.Duration;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale; // Para formateo de fecha localizado

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
    @FXML private Label yearLabel;
    @FXML private Label castLabel;

    private Pane parentContainer;
    private Pane overlayPane;
    // private StackPane rootStack; // No parece usarse, si es así se puede quitar
    private OverlayHelper overlayHelper;
    private DialogAnimationHelper dialogAnimationHelper;
    private Region placeholder;
    // private double currentXInOverlay, currentYInOverlay; // Ya no son necesarios
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;

    // private static final Duration HOVER_CARD_DURATION = Duration.millis(200); // Se usa la duración del Helper
    private static final Duration FADE_INFO_DURATION = Duration.millis(200);

    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        // this.rootStack = rootStack; // Asignar si se usa
        this.overlayHelper = new OverlayHelper(overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;

        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (isExpanded && !isAnimating && currentlyExpanded == this) {
                onCardClick(null); // Reutiliza el handler de click
            }
        });
        // Aplicar la sombra sutil inicial definida en el Helper
        cardRoot.setEffect(CardAnimationHelper.SUBTLE_SHADOW_EFFECT);
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        // Usa la nueva función de hover y su duración interna
        CardAnimationHelper.createHoverInAnimation(cardRoot).play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        // Usa la nueva función de hover y su duración interna
        CardAnimationHelper.createHoverOutAnimation(cardRoot).play();
    }

    @FXML
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        // Si hay otra tarjeta expandida, no hacer nada (o colapsarla primero si se desea esa lógica)
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) {
            // Opcional: llamar a currentlyExpanded.collapseCard() si se quiere un comportamiento de "acordeón"
            // currentlyExpanded.collapseCard(); // Descomentar para efecto acordeón
            return; // O quitar este return si quieres que se expanda de todas formas después de colapsar la otra.
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
        performFadeTransition(textContainer, detailsPane); // Transición interna de contenido

        // Animaciones de fondo y overlay
        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 0.4, CardAnimationHelper.EASE_OUT_INTERPOLATOR); // Usar interpolador del helper

        // --- Preparación de la tarjeta para la animación de expansión ---
        Bounds cardBoundsInParent = cardRoot.getBoundsInParent();
        Point2D cardPositionInScene = cardRoot.localToScene(0,0);


        placeholder = new Region();
        placeholder.setPrefSize(cardRoot.getWidth(), cardRoot.getHeight());
        placeholder.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        placeholder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);


        if (cardRoot.getParent() instanceof Pane currentParentPane) {
            int idx = currentParentPane.getChildren().indexOf(cardRoot);
            if (idx != -1) {
                currentParentPane.getChildren().set(idx, placeholder);
            } else {
                // Si por alguna razón no está, no podemos poner el placeholder en su lugar exacto.
                // Esto podría ser un problema. Se añade al final como fallback.
                currentParentPane.getChildren().add(placeholder);
            }
        }


        Point2D cardPositionInOverlay = overlayPane.sceneToLocal(cardPositionInScene);
        cardRoot.setLayoutX(cardPositionInOverlay.getX());
        cardRoot.setLayoutY(cardPositionInOverlay.getY());

        cardRoot.setTranslateX(0);
        cardRoot.setTranslateY(0);
        cardRoot.setScaleX(1.0);
        cardRoot.setScaleY(1.0);
        cardRoot.setOpacity(0.0);

        if (!overlayPane.getChildren().contains(cardRoot)) {
            overlayPane.getChildren().add(cardRoot);
        }
        overlayHelper.getOverlay().toFront();
        cardRoot.toFront();

        double cardOriginalWidth = cardRoot.getWidth();
        double cardOriginalHeight = cardRoot.getHeight();
        double targetScaleXFactor = 1.5;
        double targetScaleYFactor = 1.8;

        if (cardOriginalWidth <= 0) cardOriginalWidth = cardRoot.getPrefWidth(); // Fallback a prefWidth
        if (cardOriginalHeight <= 0) cardOriginalHeight = cardRoot.getPrefHeight(); // Fallback a prefHeight
        if (cardOriginalWidth <=0) cardOriginalWidth = 150; // Hardcoded fallback
        if (cardOriginalHeight <=0) cardOriginalHeight = 225; // Hardcoded fallback


        double expandedVisualWidth = cardOriginalWidth * targetScaleXFactor;
        double expandedVisualHeight = cardOriginalHeight * targetScaleYFactor;

        double finalCenteredLayoutX = (overlayPane.getWidth() - expandedVisualWidth) / 2.0;
        double finalCenteredLayoutY = (overlayPane.getHeight() - expandedVisualHeight) / 2.0;

        double targetTranslateX = finalCenteredLayoutX - cardRoot.getLayoutX();
        double targetTranslateY = finalCenteredLayoutY - cardRoot.getLayoutY();

        ParallelTransition expandAnimation = CardAnimationHelper.createExpandAnimation(
                cardRoot, targetTranslateX, targetTranslateY, targetScaleXFactor, targetScaleYFactor
        );

        expandAnimation.setOnFinished(event -> {
            isAnimating = false;
            isExpanded = true;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        expandAnimation.play();
    }

    private void collapseCard() {
        performFadeTransition(detailsPane, textContainer);

        cardRoot.setOnMouseEntered(this::onCardHover);
        cardRoot.setOnMouseExited(this::onCardHoverExit);

        overlayHelper.hide(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8), CardAnimationHelper.EASE_OUT_INTERPOLATOR, () -> {});
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8)).play();

        ParallelTransition collapseAnimation = CardAnimationHelper.createCollapseAnimation(
                cardRoot, overlayPane, parentContainer, placeholder,
                () -> {
                    this.placeholder = null;
                    CardMovieViewController.currentlyExpanded = null;
                    this.isAnimating = false;
                    this.isExpanded = false;
                }
        );
        collapseAnimation.play();
    }

    private void performFadeTransition(VBox nodeToFadeOut, VBox nodeToFadeIn) {
        FadeTransition fadeOutTransition = new FadeTransition(FADE_INFO_DURATION, nodeToFadeOut);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(e -> {
            nodeToFadeOut.setVisible(false);
            nodeToFadeOut.setManaged(false);

            nodeToFadeIn.setOpacity(0.0);
            nodeToFadeIn.setVisible(true);
            nodeToFadeIn.setManaged(true);
            FadeTransition fadeInTransition = new FadeTransition(FADE_INFO_DURATION, nodeToFadeIn);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            fadeInTransition.play();
        });
        fadeOutTransition.play();
    }

    public void setFuncionData(FuncionDetallada funcion) {
        title.setText(funcion.getTituloPelicula());
        // Considera usar Locale para el formato de fecha/hora si tu app es multi-idioma
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm 'hrs'", Locale.getDefault());
        subtitle.setText(funcion.getFechaHoraFuncion().format(timeFormatter) + " • Sala " + funcion.getNumeroSala() + " (" + funcion.getTipoSala() + ")");

        if (funcion.getFotografiaPelicula() != null && !funcion.getFotografiaPelicula().isEmpty()) {
            String imagePath = funcion.getFotografiaPelicula();
            // Ajusta esta lógica de ruta si es necesario
            if (!imagePath.startsWith("http") && !imagePath.startsWith("file:") && !imagePath.startsWith("jar:") && !imagePath.startsWith("/")) {
                imagePath = "/com/example/images/" + imagePath; // Asume que está en resources/com/example/images
            }

            try {
                Image img = null;
                if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                    img = new Image(imagePath, true); // Cargar en segundo plano para URLs/archivos externos
                } else { // Cargar desde classpath/resources
                    InputStream imageStream = getClass().getResourceAsStream(imagePath);
                    if (imageStream != null) {
                        img = new Image(imageStream);
                    }
                }

                if (img != null) {
                    poster.setImage(img);
                } else {
                    System.err.println("No se pudo crear Image para el póster: " + imagePath + " para " + funcion.getTituloPelicula());
                    loadPlaceholderPoster();
                }
            } catch (IllegalArgumentException iae) {
                System.err.println("Argumento ilegal al cargar imagen (ruta vacía o nula?): " + imagePath + " - " + iae.getMessage());
                loadPlaceholderPoster();
            } catch (Exception e) {
                System.err.println("Excepción al cargar imagen: " + imagePath + " - " + e.getMessage());
                loadPlaceholderPoster();
            }
        } else {
            System.err.println("Ruta de fotografía nula o vacía para: " + funcion.getTituloPelicula());
            loadPlaceholderPoster();
        }

        synopsisLabel.setText(funcion.getSinopsisPelicula() != null ? funcion.getSinopsisPelicula() : "Sinopsis no disponible.");
        durationLabel.setText("⏱️ Duración: " + funcion.getDuracionMinutos() + " min");
        genreLabel.setText("🎭 Género: " + (funcion.getNombreTipoPelicula() != null ? funcion.getNombreTipoPelicula() : "No especificado"));

        // Usar un Locale específico para el formato de fecha si es necesario
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
        yearLabel.setText("📅 Estreno Película: " + (funcion.getFechaEstrenoPelicula() != null ? funcion.getFechaEstrenoPelicula().format(dateFormatter) : "N/A"));
        castLabel.setText("⭐ Reparto no disponible en esta vista."); // O cargar desde funcion si tienes esos datos
    }

    private void loadPlaceholderPoster() {
        // Carga una imagen placeholder si la original falla o no existe
        // Asegúrate de que la ruta a tu placeholder sea correcta.
        // Por ejemplo, si está en "src/main/resources/com/example/images/placeholder_poster.png"
        String placeholderPath = "/com/example/images/placeholder_poster.png";
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(placeholderPath);
            if (placeholderStream != null) {
                poster.setImage(new Image(placeholderStream));
            } else {
                System.err.println("No se encontró la imagen placeholder en: " + placeholderPath);
                // Como último recurso, podrías dejar el ImageView vacío o poner un color de fondo
                // poster.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen placeholder: " + e.getMessage());
        }
    }
}