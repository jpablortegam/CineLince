// MovieCardViewController.java
package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;

import java.util.List;

public class MovieCardViewController {

    @FXML private VBox cardRoot;
    @FXML private ImageView poster;
    @FXML private Label title;
    @FXML private Label subtitle;
    @FXML private VBox detailsPane;
    @FXML private Label synopsisLabel;
    @FXML private Label durationLabel;
    @FXML private Label genreLabel;
    @FXML private Label yearLabel;
    @FXML private Label castLabel;

    private boolean isExpanded = false;
    private boolean isAnimating = false;
    private Movie currentMovie;

    // Control global de expansión
    private static MovieCardViewController currentlyExpanded = null;


    // Contexto para overlay y efectos
    private Pane parentContainer;
    private Pane overlayPane;
    private Region placeholder;
    private int placeholderIndex;
    private GaussianBlur backgroundBlur = new GaussianBlur(0);

    // Propiedades de dimensiones
    private double originalWidth;
    private double originalHeight;
    private double scaleMultiplier = 2.8; // Factor de escalado más ancho

    // Coordenadas originales para animación precisa
    private double originalSceneX;
    private double originalSceneY;

    // Efectos minimalistas
    private DropShadow subtleShadow;
    private DropShadow expandedShadow;

    // Dimensiones del overlay para cálculos precisos
    private double overlayWidth;
    private double overlayHeight;

    public void initContext(Pane parentContainer, Pane overlayPane) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.overlayWidth = overlayPane.getWidth();
        this.overlayHeight = overlayPane.getHeight();

        // Sombras más sutiles y minimalistas
        subtleShadow = new DropShadow();
        subtleShadow.setRadius(8);
        subtleShadow.setOffsetX(0);
        subtleShadow.setOffsetY(2);
        subtleShadow.setColor(Color.rgb(0, 0, 0, 0.15));

        expandedShadow = new DropShadow();
        expandedShadow.setRadius(30);
        expandedShadow.setOffsetX(0);
        expandedShadow.setOffsetY(8);
        expandedShadow.setColor(Color.rgb(0, 0, 0, 0.25));

        cardRoot.setEffect(subtleShadow);
    }

    // Método para establecer dimensiones precisas del overlay
    public void setOverlayDimensions(double width, double height) {
        this.overlayWidth = width;
        this.overlayHeight = height;
    }

    @FXML
    private void initialize() {
        if (detailsPane != null) {
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);
            detailsPane.setOpacity(0.0);
        }

        // Capturar dimensiones originales
        cardRoot.widthProperty().addListener((obs, oldW, newW) -> {
            if (originalWidth == 0) originalWidth = newW.doubleValue();
        });

        cardRoot.heightProperty().addListener((obs, oldH, newH) -> {
            if (originalHeight == 0) originalHeight = newH.doubleValue();
        });
    }

    @FXML
    void onCardClick(MouseEvent event) {
        if (isAnimating) return;

        // Verificar si hay otra card expandida
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) {
            return; // No permitir expandir si otra está expandida
        }

        isAnimating = true;

        if (!isExpanded) expandCard();
        else collapseCard();
    }

    // Variables para controlar hover
    private ScaleTransition currentHoverScale;
    private Timeline currentShadowTimeline;

    @FXML
    void onCardHover(MouseEvent event) {
        // Solo aplicar hover si no está expandida, no está animando Y no hay otra card expandida
        if (!isExpanded && !isAnimating && currentlyExpanded == null) {
            // Cancelar animaciones previas si existen
            if (currentHoverScale != null) {
                currentHoverScale.stop();
            }
            if (currentShadowTimeline != null) {
                currentShadowTimeline.stop();
            }

            // Hover sutil y elegante
            currentHoverScale = new ScaleTransition(Duration.millis(250), cardRoot);
            currentHoverScale.setToX(1.02);
            currentHoverScale.setToY(1.02);
            currentHoverScale.setInterpolator(Interpolator.EASE_OUT);
            currentHoverScale.play();

            // Ligero aumento de sombra
            currentShadowTimeline = new Timeline(
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(((DropShadow)cardRoot.getEffect()).radiusProperty(), 12, Interpolator.EASE_OUT))
            );
            currentShadowTimeline.play();
        }
    }

    @FXML
    void onCardHoverExit(MouseEvent event) {
        // Solo aplicar hover exit si no está expandida, no está animando Y no hay otra card expandida
        if (!isExpanded && !isAnimating && currentlyExpanded == null) {
            // Cancelar animaciones previas si existen
            if (currentHoverScale != null) {
                currentHoverScale.stop();
            }
            if (currentShadowTimeline != null) {
                currentShadowTimeline.stop();
            }

            // Restaurar estado normal
            currentHoverScale = new ScaleTransition(Duration.millis(250), cardRoot);
            currentHoverScale.setToX(1.0);
            currentHoverScale.setToY(1.0);
            currentHoverScale.setInterpolator(Interpolator.EASE_OUT);
            currentHoverScale.play();

            currentShadowTimeline = new Timeline(
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(((DropShadow)cardRoot.getEffect()).radiusProperty(), 8, Interpolator.EASE_OUT))
            );
            currentShadowTimeline.play();
        }
    }

    private void expandCard() {
        // Establecer como la card actualmente expandida
        currentlyExpanded = this;

        // Capturar coordenadas exactas ANTES de mover la card
        Bounds originalBounds = cardRoot.localToScene(cardRoot.getBoundsInLocal());
        originalSceneX = originalBounds.getMinX();
        originalSceneY = originalBounds.getMinY();

        // Crear placeholder
        placeholder = new Region();
        placeholder.setPrefSize(originalWidth, originalHeight);
        placeholderIndex = parentContainer.getChildren().indexOf(cardRoot);
        parentContainer.getChildren().add(placeholderIndex, placeholder);
        parentContainer.getChildren().remove(cardRoot);

        // Convertir coordenadas del overlay
        Point2D overlayBoundsOriginal = overlayPane.sceneToLocal(originalSceneX, originalSceneY);

        // Posicionar la card en las coordenadas exactas originales
        cardRoot.setLayoutX(overlayBoundsOriginal.getX());
        cardRoot.setLayoutY(overlayBoundsOriginal.getY());
        overlayPane.getChildren().add(cardRoot);

        // Preparar detalles
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
        detailsPane.setOpacity(0);

        // Calcular posición central usando las dimensiones correctas del overlay
        double targetWidth = overlayWidth > 0 ? overlayWidth : overlayPane.getWidth();
        double targetHeight = overlayHeight > 0 ? overlayHeight : overlayPane.getHeight();

        // Centrado perfecto considerando que la card se escalará desde su esquina superior izquierda
        double centerX = (targetWidth - originalWidth) / 2 - (originalWidth * (scaleMultiplier - 1)) / 2;
        double centerY = (targetHeight - originalHeight) / 2 - (originalHeight * (scaleMultiplier - 1)) / 2;

        // Blur del fondo más suave
        parentContainer.setEffect(backgroundBlur);
        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(backgroundBlur.radiusProperty(), 12, Interpolator.EASE_BOTH))
        );

        // Escalado y movimiento hacia el centro simultáneo con coordenadas precisas
        Timeline scaleAndMove = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(cardRoot.scaleXProperty(), 1.0),
                        new KeyValue(cardRoot.scaleYProperty(), 1.0),
                        new KeyValue(cardRoot.layoutXProperty(), overlayBoundsOriginal.getX()),
                        new KeyValue(cardRoot.layoutYProperty(), overlayBoundsOriginal.getY())),
                new KeyFrame(Duration.millis(450),
                        new KeyValue(cardRoot.scaleXProperty(), scaleMultiplier, Interpolator.SPLINE(0.2, 0, 0.2, 1)),
                        new KeyValue(cardRoot.scaleYProperty(), scaleMultiplier, Interpolator.SPLINE(0.2, 0, 0.2, 1)),
                        new KeyValue(cardRoot.layoutXProperty(), centerX, Interpolator.SPLINE(0.2, 0, 0.2, 1)),
                        new KeyValue(cardRoot.layoutYProperty(), centerY, Interpolator.SPLINE(0.2, 0, 0.2, 1)))
        );

        // Fade in de detalles con retraso
        FadeTransition detailsFade = new FadeTransition(Duration.millis(300), detailsPane);
        detailsFade.setFromValue(0);
        detailsFade.setToValue(1);
        detailsFade.setDelay(Duration.millis(200));
        detailsFade.setInterpolator(Interpolator.EASE_OUT);

        // Sombra expandida
        Timeline shadowExpand = new Timeline(
                new KeyFrame(Duration.millis(450),
                        new KeyValue(((DropShadow)cardRoot.getEffect()).radiusProperty(), 30, Interpolator.EASE_OUT),
                        new KeyValue(((DropShadow)cardRoot.getEffect()).offsetYProperty(), 8, Interpolator.EASE_OUT))
        );

        ParallelTransition expandAnimation = new ParallelTransition(
                blurIn, scaleAndMove, detailsFade, shadowExpand
        );

        expandAnimation.setOnFinished(e -> {
            cardRoot.setEffect(expandedShadow);
            isAnimating = false;
        });

        expandAnimation.play();
        isExpanded = true;
    }

    private void collapseCard() {
        // Calcular posición de retorno usando las coordenadas originales guardadas
        Point2D overlayBoundsReturn = overlayPane.sceneToLocal(originalSceneX, originalSceneY);

        // Animación de colapso
        Timeline scaleAndMove = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(cardRoot.scaleXProperty(), scaleMultiplier),
                        new KeyValue(cardRoot.scaleYProperty(), scaleMultiplier),
                        new KeyValue(cardRoot.layoutXProperty(), cardRoot.getLayoutX()),
                        new KeyValue(cardRoot.layoutYProperty(), cardRoot.getLayoutY())),
                new KeyFrame(Duration.millis(350),
                        new KeyValue(cardRoot.scaleXProperty(), 1.0, Interpolator.SPLINE(0.2, 0, 0.2, 1)),
                        new KeyValue(cardRoot.scaleYProperty(), 1.0, Interpolator.SPLINE(0.2, 0, 0.2, 1)),
                        new KeyValue(cardRoot.layoutXProperty(), overlayBoundsReturn.getX(), Interpolator.SPLINE(0.2, 0, 0.2, 1)),
                        new KeyValue(cardRoot.layoutYProperty(), overlayBoundsReturn.getY(), Interpolator.SPLINE(0.2, 0, 0.2, 1)))
        );

        // Fade out inmediato de detalles
        FadeTransition detailsFadeOut = new FadeTransition(Duration.millis(150), detailsPane);
        detailsFadeOut.setFromValue(1);
        detailsFadeOut.setToValue(0);
        detailsFadeOut.setInterpolator(Interpolator.EASE_IN);

        // Quitar blur
        Timeline blurOut = new Timeline(
                new KeyFrame(Duration.millis(350),
                        new KeyValue(backgroundBlur.radiusProperty(), 0, Interpolator.EASE_BOTH))
        );

        // Restaurar sombra sutil
        Timeline shadowRestore = new Timeline(
                new KeyFrame(Duration.millis(350),
                        new KeyValue(((DropShadow)cardRoot.getEffect()).radiusProperty(), 8, Interpolator.EASE_OUT),
                        new KeyValue(((DropShadow)cardRoot.getEffect()).offsetYProperty(), 2, Interpolator.EASE_OUT))
        );

        ParallelTransition collapseAnimation = new ParallelTransition(
                scaleAndMove, detailsFadeOut, blurOut, shadowRestore
        );

        collapseAnimation.setOnFinished(e -> {
            // Restaurar estado original
            overlayPane.getChildren().remove(cardRoot);
            parentContainer.getChildren().remove(placeholder);
            parentContainer.getChildren().add(placeholderIndex, cardRoot);
            parentContainer.setEffect(null);

            cardRoot.setEffect(subtleShadow);
            cardRoot.setLayoutX(0);
            cardRoot.setLayoutY(0);
            cardRoot.setScaleX(1.0);
            cardRoot.setScaleY(1.0);

            detailsPane.setVisible(false);
            detailsPane.setManaged(false);

            // Liberar el control de expansión
            currentlyExpanded = null;

            isAnimating = false;
            isExpanded = false;
        });

        collapseAnimation.play();
    }

    public void setMovieData(Movie movie) {
        this.currentMovie = movie;
        if (movie == null) return;

        if (title != null) {
            title.setText(movie.getTitle() != null ? movie.getTitle() : "Sin título");
        }

        if (subtitle != null) {
            StringBuilder sb = new StringBuilder();
            if (movie.getYear() != null && !movie.getYear().isEmpty()) {
                sb.append("(").append(movie.getYear()).append(") ");
            }
            sb.append(movie.getDuration()).append(" min");
            subtitle.setText(sb.length() > 0 ? sb.toString() : "Información no disponible");
        }

        if (poster != null && movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
            try {
                Image img = new Image(movie.getImageUrl(), true);
                poster.setImage(img);
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + movie.getImageUrl());
            }
        }

        setupMovieDetails(movie);
    }

    private void setupMovieDetails(Movie movie) {
        if (synopsisLabel != null) {
            String synopsis = movie.getSynopsis();
            synopsisLabel.setText(
                    (synopsis != null && !synopsis.trim().isEmpty())
                            ? synopsis
                            : "Sinopsis no disponible para esta película."
            );
        }

        if (durationLabel != null) {
            durationLabel.setText("⏱️ Duración: " + movie.getDuration() + " min");
        }

        if (genreLabel != null) {
            List<String> genres = movie.getGenre();
            if (genres != null && !genres.isEmpty()) {
                genreLabel.setText("🎭 Género: " + String.join(", ", genres));
            } else {
                genreLabel.setText("🎭 Género: No especificado");
            }
        }

        if (yearLabel != null) {
            String year = movie.getYear();
            yearLabel.setText(
                    "📅 Año: " + (year != null && !year.trim().isEmpty() ? year : "No especificado")
            );
        }

        if (castLabel != null) {
            List<String> cast = movie.getCast();
            if (cast != null && !cast.isEmpty()) {
                castLabel.setText("⭐ " + String.join(", ", cast));
            } else {
                castLabel.setText("⭐ Reparto no disponible");
            }
        }
    }

    public void resetCard() {
        if (isExpanded) collapseCard();

        // Limpiar animaciones hover si existen
        if (currentHoverScale != null) {
            currentHoverScale.stop();
            currentHoverScale = null;
        }
        if (currentShadowTimeline != null) {
            currentShadowTimeline.stop();
            currentShadowTimeline = null;
        }

        // Restaurar transformaciones
        cardRoot.setScaleX(1.0);
        cardRoot.setScaleY(1.0);
        cardRoot.setEffect(subtleShadow);

        isAnimating = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }

    // Configuración del factor de escalado
    public void setScaleMultiplier(double multiplier) {
        this.scaleMultiplier = multiplier;
    }

    public double getScaleMultiplier() {
        return scaleMultiplier;
    }

    // Método estático para cerrar cualquier card expandida
    public static void closeCurrentlyExpanded() {
        if (currentlyExpanded != null && currentlyExpanded.isExpanded()) {
            currentlyExpanded.collapseCard();
        }
    }

    // Verificar si hay alguna card expandida
    public static boolean hasExpandedCard() {
        return currentlyExpanded != null;
    }
}