// MovieCardViewController.java
package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import javafx.scene.input.MouseEvent;

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

    // Contexto para blur y overlay
    private Pane parentContainer;
    private Pane overlayPane;
    private Region placeholder;
    private int placeholderIndex;
    private GaussianBlur blur = new GaussianBlur(0);
    private double originalHeight;

    /**
     * Debe llamarse desde HomeViewController: cardCtrl.initContext(upcomingPane, overlayPane);
     */
    public void initContext(Pane parentContainer, Pane overlayPane) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
    }

    @FXML
    private void initialize() {
        if (detailsPane != null) {
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);
            detailsPane.setOpacity(0.0);
        }
        // Registrar altura original tras layout
        cardRoot.heightProperty().addListener((obs, oldH, newH) -> {
            if (originalHeight == 0) originalHeight = newH.doubleValue();
        });
    }

    @FXML
    void onCardClick(MouseEvent event) {
        if (isAnimating) return;
        isAnimating = true;

        if (!isExpanded) expandCard();
        else collapseCard();
    }

    @FXML
    void onCardHover(MouseEvent event) {
        if (!isExpanded && !isAnimating) {
            cardRoot.setScaleX(1.05);
            cardRoot.setScaleY(1.05);
        }
    }

    @FXML
    void onCardHoverExit(MouseEvent event) {
        if (!isExpanded && !isAnimating) {
            cardRoot.setScaleX(1.0);
            cardRoot.setScaleY(1.0);
        }
    }

    private void expandCard() {
        // Crear placeholder para mantener el flujo
        placeholder = new Region();
        placeholder.setPrefSize(cardRoot.getWidth(), originalHeight);
        placeholderIndex = parentContainer.getChildren().indexOf(cardRoot);
        parentContainer.getChildren().add(placeholderIndex, placeholder);
        parentContainer.getChildren().remove(cardRoot);

        // Añadir tarjeta al overlay para superponer
        overlayPane.getChildren().add(cardRoot);

        // Aplicar blur al contenedor de fondo
        parentContainer.setEffect(blur);

        // Timeline para animar el blur
        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO,     new KeyValue(blur.radiusProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(blur.radiusProperty(), 10, Interpolator.EASE_BOTH))
        );
        blurIn.play();

        // Mostrar detalles con fade y expandir altura
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
        detailsPane.setOpacity(0);

        // Esperar un poco antes de animar expansión
        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(e -> {
            double detailsHeight = detailsPane.getBoundsInParent().getHeight();
            double targetHeight = originalHeight + detailsHeight + 16; // ajuste padding

            Timeline expand = new Timeline(
                    new KeyFrame(Duration.ZERO,       new KeyValue(cardRoot.prefHeightProperty(), originalHeight)),
                    new KeyFrame(Duration.millis(300), new KeyValue(cardRoot.prefHeightProperty(), targetHeight, Interpolator.EASE_BOTH))
            );

            FadeTransition fadeDetails = new FadeTransition(Duration.millis(200), detailsPane);
            fadeDetails.setFromValue(0);
            fadeDetails.setToValue(1);

            ParallelTransition pt = new ParallelTransition(expand, fadeDetails);
            pt.setOnFinished(evt -> isAnimating = false);
            pt.play();
        });
        delay.play();

        isExpanded = true;
    }

    private void collapseCard() {
        // Animación de contracción y fade out de detalles
        Timeline collapse = new Timeline(
                new KeyFrame(Duration.ZERO,       new KeyValue(cardRoot.prefHeightProperty(), cardRoot.getHeight())),
                new KeyFrame(Duration.millis(300), new KeyValue(cardRoot.prefHeightProperty(), originalHeight, Interpolator.EASE_BOTH))
        );
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), detailsPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Timeline para quitar blur
        Timeline blurOut = new Timeline(
                new KeyFrame(Duration.ZERO,       new KeyValue(blur.radiusProperty(), 10)),
                new KeyFrame(Duration.millis(300), new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_BOTH))
        );

        ParallelTransition pt = new ParallelTransition(collapse, fadeOut, blurOut);
        pt.setOnFinished(evt -> {
            // Restaurar escena
            overlayPane.getChildren().remove(cardRoot);
            parentContainer.getChildren().remove(placeholder);
            parentContainer.getChildren().add(placeholderIndex, cardRoot);
            parentContainer.setEffect(null);

            detailsPane.setVisible(false);
            detailsPane.setManaged(false);

            isAnimating = false;
            isExpanded = false;
        });
        pt.play();
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
            durationLabel.setText(
                    "⏱️ Duración: " + movie.getDuration()
            );
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
        isAnimating = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }
}
